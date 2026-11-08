package cosecha.servidor;

import cosecha.comun.Operacion;
import cosecha.comun.Respuesta;
import cosecha.comun.Sesion;
import cosecha.comun.Solicitud;
import cosecha.excepciones.CosechaException;
import cosecha.modelo.CategoriaAlimento;
import cosecha.modelo.UnidadMedida;
import cosecha.modelo.Usuario;
import cosecha.servidor.datos.AlmacenDatos;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

// Atiende a un cliente conectado en un hilo independiente
public class ManejadorCliente implements Runnable {

    private final Socket socket;
    private final AlmacenDatos almacen;
    private final ServicioUsuarios usuarios;
    private final ServicioIntercambios intercambios;

    public ManejadorCliente(Socket socket, AlmacenDatos almacen, ServicioUsuarios usuarios,
            ServicioIntercambios intercambios) {
        this.socket = socket;
        this.almacen = almacen;
        this.usuarios = usuarios;
        this.intercambios = intercambios;
    }

    @Override
    public void run() {
        System.out.println("Cliente conectado desde " + socket.getRemoteSocketAddress());
        try {
            ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
            salida.flush();
            ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());

            while (!socket.isClosed()) {
                Object recibido = entrada.readObject();
                if (!(recibido instanceof Solicitud)) {
                    salida.writeObject(Respuesta.error("Solicitud inválida."));
                    salida.flush();
                    continue;
                }

                almacen.bloquear();
                try {
                    Respuesta respuesta = atender((Solicitud) recibido);
                    salida.reset();
                    salida.writeObject(respuesta);
                    salida.flush();
                } finally {
                    almacen.desbloquear();
                }
            }
        } catch (EOFException | SocketException e) {
            // El cliente cerró la ventana o se desconectó
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error de comunicación con cliente: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                // Ignorar error al cerrar
            }
            System.out.println("Cliente desconectado: " + socket.getRemoteSocketAddress());
        }
    }

    public Respuesta atender(Solicitud s) {
        try {
            Operacion op = s.getOperacion();
            if (op == null) {
                return Respuesta.error("Operación desconocida.");
            }
            long id = 0;
            if (op.requiereSesion()) {
                id = usuarios.usuarioDeSesion(s.getToken()).getId();
            }
            return ejecutar(op, s, id);
        } catch (CosechaException e) {
            return Respuesta.error(e);
        } catch (RuntimeException e) {
            System.err.println("Error inesperado en el servidor: " + e.getMessage());
            return Respuesta.error("Ocurrió un error en el servidor.");
        }
    }

    private Respuesta ejecutar(Operacion op, Solicitud s, long id) throws CosechaException {
        switch (op) {
            case REGISTRAR: {
                Usuario u = usuarios.registrar(s.getTexto("nombre"), s.getTexto("correo"),
                        s.getTexto("clave"), s.getTexto("confirmacion"), s.getTexto("zona"));
                return Respuesta.ok(u, "¡Cuenta creada! Ya podés iniciar sesión.");
            }
            case INICIAR_SESION: {
                Sesion sesion = usuarios.iniciarSesion(s.getTexto("correo"), s.getTexto("clave"));
                return Respuesta.ok(sesion);
            }
            case CERRAR_SESION:
                usuarios.cerrarSesion(s.getToken());
                return Respuesta.ok(null, "Sesión cerrada.");
            case CONSULTAR_PERFIL:
                return Respuesta.ok(usuarios.consultarPerfil(s.getToken()));
            case ACTUALIZAR_PERFIL:
                return Respuesta.ok(usuarios.actualizarPerfil(s.getToken(), s.getTexto("nombre"), s.getTexto("zona")),
                        "Tus datos se actualizaron.");
            case CREAR_PUBLICACION:
                return Respuesta.ok(intercambios.crearPublicacion(id, s.getTexto("alimento"),
                        s.getEnum("categoria", CategoriaAlimento.class), s.getDecimal("cantidad"),
                        s.getEnum("unidad", UnidadMedida.class), s.getTexto("zona"), s.getTexto("preferencias"),
                        s.getTexto("descripcion"), s.getFecha("fechaLimite")), "Tu lote ya está publicado.");
            case BUSCAR_PUBLICACIONES:
                return Respuesta.ok(intercambios.buscarPublicaciones(s.getTexto("alimento"), s.getTexto("zona"),
                        s.getEnum("categoria", CategoriaAlimento.class)));
            case CONSULTAR_PUBLICACION:
                return Respuesta.ok(intercambios.consultarPublicacion(s.getLong("idPublicacion")));
            case LISTAR_MIS_PUBLICACIONES:
                return Respuesta.ok(intercambios.listarMisPublicaciones(id));
            case EDITAR_PUBLICACION:
                return Respuesta.ok(intercambios.editarPublicacion(id, s.getLong("idPublicacion"),
                        s.getTexto("alimento"), s.getEnum("categoria", CategoriaAlimento.class),
                        s.getDecimal("cantidad"), s.getEnum("unidad", UnidadMedida.class), s.getTexto("zona"),
                        s.getTexto("preferencias"), s.getTexto("descripcion"), s.getFecha("fechaLimite")),
                        "Cambios guardados.");
            case RETIRAR_PUBLICACION:
                return Respuesta.ok(intercambios.retirarPublicacion(id, s.getLong("idPublicacion")),
                        "El lote fue retirado. Se conserva en tu lista.");
            case ELIMINAR_PUBLICACION:
                intercambios.eliminarPublicacion(id, s.getLong("idPublicacion"));
                return Respuesta.ok(null, "La publicación fue eliminada.");
            case ENVIAR_PROPUESTA:
                return Respuesta.ok(intercambios.enviarPropuesta(id, s.getLong("idPublicacion"),
                        s.getTexto("alimento"), s.getDecimal("cantidad"), s.getEnum("unidad", UnidadMedida.class),
                        s.getTexto("mensaje")), "¡Propuesta enviada!");
            default:
                return Respuesta.error("Operación no soportada: " + op);
        }
    }
}
