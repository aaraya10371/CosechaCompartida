package cosecha.pruebas;

import cosecha.cliente.ClienteConexion;
import cosecha.comun.Operacion;
import cosecha.comun.Respuesta;
import cosecha.comun.Sesion;
import cosecha.comun.Solicitud;
import cosecha.modelo.CategoriaAlimento;
import cosecha.modelo.EstadoPublicacion;
import cosecha.modelo.PropuestaIntercambio;
import cosecha.modelo.Publicacion;
import cosecha.modelo.UnidadMedida;
import cosecha.modelo.Usuario;
import cosecha.servidor.ServidorAplicacion;
import cosecha.servidor.datos.AlmacenDatos;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// Clase de prueba para verificar el flujo del Avance 2 entre cliente y servidor
public class PruebaIntegracion {

    private static int correctas = 0;
    private static int fallidas = 0;
    private static int puerto;

    public static void main(String[] args) throws Exception {
        File archivo = File.createTempFile("cosecha-prueba", ".dat");
        archivo.delete();

        ServidorAplicacion servidor = new ServidorAplicacion(new AlmacenDatos(archivo));
        puerto = servidor.iniciar(0);
        LocalDate hoy = LocalDate.now();

        try {
            ejecutarPruebas(servidor, archivo, hoy);
        } finally {
            servidor.detener();
            archivo.delete();
        }
    }

    private static void ejecutarPruebas(ServidorAplicacion servidor, File archivo, LocalDate hoy) throws Exception {
        System.out.println("=== Iniciando pruebas del Avance 2 ===");

        // HU01 y HU02: Registro e inicio de sesión
        ClienteConexion anonimo = new ClienteConexion("localhost", puerto);
        anonimo.conectar();

        comprobar(registrar(anonimo, "Ana Pérez", "ana@correo.com", "San Ramón").esExitosa(),
                "HU01 - Registro de usuario Ana");
        comprobar(registrar(anonimo, "Beto Ruiz", "beto@correo.com", "San Ramón").esExitosa(),
                "HU01 - Registro de usuario Beto");
        comprobar(registrar(anonimo, "Carla Vega", "carla@correo.com", "Palmares").esExitosa(),
                "HU01 - Registro de usuario Carla");
        comprobar(!registrar(anonimo, "Otra Ana", " ANA@correo.com ", "San Ramón").esExitosa(),
                "HU01 - Rechazo de correo duplicado");
        comprobar(!anonimo.enviar(new Solicitud(Operacion.INICIAR_SESION)
                .con("correo", "ana@correo.com").con("clave", "incorrecta")).esExitosa(),
                "HU02 - Rechazo de contraseña incorrecta");

        ClienteConexion ana = iniciarSesion("ana@correo.com");
        ClienteConexion beto = iniciarSesion("beto@correo.com");
        ClienteConexion carla = iniciarSesion("carla@correo.com");

        // HU03 y HU04: Publicación y búsqueda
        Publicacion mangos = (Publicacion) crearPublicacion(ana, "Mangos", new BigDecimal("3"), hoy.plusDays(10))
                .getDatos();
        comprobar(mangos != null && mangos.getEstado() == EstadoPublicacion.DISPONIBLE,
                "HU03 - Crear publicación disponible");
        comprobar(buscar(beto, "mango", "ramon").size() == 1,
                "HU04 - Buscar publicación por alimento y zona");

        // HU10: Editar publicación sin propuestas pendientes
        Respuesta editada = ana.enviar(new Solicitud(Operacion.EDITAR_PUBLICACION)
                .con("idPublicacion", mangos.getId())
                .con("alimento", "Mangos Tommy")
                .con("categoria", CategoriaAlimento.FRUTA)
                .con("cantidad", new BigDecimal("4"))
                .con("unidad", UnidadMedida.KG)
                .con("zona", "San Ramón")
                .con("preferencias", "Aguacates")
                .con("descripcion", "Lote actualizado")
                .con("fechaLimite", hoy.plusDays(12)));
        comprobar(editada.esExitosa(), "HU10 - Editar publicación vigente sin propuestas");

        // HU05: Propuestas de intercambio
        comprobar(!proponer(ana, mangos.getId(), "Limones").esExitosa(),
                "HU05 - Impedir propuesta sobre publicación propia");
        PropuestaIntercambio propCarla = (PropuestaIntercambio) proponer(carla, mangos.getId(), "Culantro").getDatos();
        comprobar(propCarla != null, "HU05 - Enviar propuesta de otro vecino");

        // HU10: Reglas con propuestas pendientes, retiro y eliminación
        Respuesta intentoEdicion = ana.enviar(new Solicitud(Operacion.EDITAR_PUBLICACION)
                .con("idPublicacion", mangos.getId())
                .con("alimento", "Mangos")
                .con("categoria", CategoriaAlimento.FRUTA)
                .con("cantidad", new BigDecimal("4"))
                .con("unidad", UnidadMedida.KG)
                .con("zona", "San Ramón")
                .con("preferencias", "")
                .con("descripcion", "No debería dejar editar")
                .con("fechaLimite", hoy.plusDays(12)));
        comprobar(!intentoEdicion.esExitosa(), "HU10 - Bloquear edición cuando hay propuestas pendientes");

        Publicacion limones = (Publicacion) crearPublicacion(ana, "Limones", new BigDecimal("10"), hoy.plusDays(7))
                .getDatos();
        comprobar(ana.enviar(new Solicitud(Operacion.RETIRAR_PUBLICACION)
                .con("idPublicacion", limones.getId())).esExitosa(),
                "HU10 - Retirar publicación disponible");
        comprobar(ana.enviar(new Solicitud(Operacion.ELIMINAR_PUBLICACION)
                .con("idPublicacion", limones.getId())).esExitosa(),
                "HU10 - Eliminar publicación retirada sin propuestas");

        // HU11: Actualización de perfil
        Usuario perfil = (Usuario) ana.enviarOFallar(new Solicitud(Operacion.ACTUALIZAR_PERFIL)
                .con("nombre", "Ana María Pérez").con("zona", "Palmares")).getDatos();
        comprobar("Palmares".equals(perfil.getZona()), "HU11 - Actualizar datos del perfil");

        // Prueba de concurrencia: dos sesiones de Beto intentan proponer sobre el mismo lote a la vez
        ClienteConexion sesionBeto1 = iniciarSesion("beto@correo.com");
        ClienteConexion sesionBeto2 = iniciarSesion("beto@correo.com");
        Respuesta[] resultados = new Respuesta[2];

        Thread h1 = new Thread(() -> {
            try {
                resultados[0] = proponer(sesionBeto1, mangos.getId(), "Aguacates");
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        });
        Thread h2 = new Thread(() -> {
            try {
                resultados[1] = proponer(sesionBeto2, mangos.getId(), "Chayotes");
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        });

        h1.start();
        h2.start();
        h1.join();
        h2.join();

        boolean soloUnaEntro = (resultados[0].esExitosa() && !resultados[1].esExitosa())
                || (!resultados[0].esExitosa() && resultados[1].esExitosa());
        comprobar(soloUnaEntro, "Concurrencia - Solo se permite una propuesta pendiente por vecino en el mismo lote");

        // Cierre de sesión y persistencia al reiniciar el servidor
        comprobar(carla.enviar(new Solicitud(Operacion.CERRAR_SESION)).esExitosa(),
                "HU02 - Cerrar sesión invalida el acceso");

        sesionBeto1.cerrar();
        sesionBeto2.cerrar();
        ana.cerrar();
        beto.cerrar();
        carla.cerrar();
        anonimo.cerrar();
        servidor.detener();

        ServidorAplicacion servidorReiniciado = new ServidorAplicacion(new AlmacenDatos(archivo));
        try {
            puerto = servidorReiniciado.iniciar(0);
            ClienteConexion anaReinicio = iniciarSesion("ana@correo.com");
            @SuppressWarnings("unchecked")
            List<Publicacion> mias = (List<Publicacion>) anaReinicio.enviarOFallar(
                    new Solicitud(Operacion.LISTAR_MIS_PUBLICACIONES)).getDatos();
            comprobar(mias.size() == 1 && mias.get(0).getPropuestasPendientes() == 2,
                    "Persistencia - Los datos y propuestas se mantienen al reiniciar el servidor");
            anaReinicio.cerrar();
        } finally {
            servidorReiniciado.detener();
        }

        System.out.println("\nPruebas finalizadas: " + correctas + " correctas, " + fallidas + " fallidas.");
        if (fallidas > 0) {
            System.exit(1);
        }
    }

    private static void comprobar(boolean condicion, String nombre) {
        if (condicion) {
            correctas++;
            System.out.println("[OK] " + nombre);
        } else {
            fallidas++;
            System.out.println("[FALLO] " + nombre);
        }
    }

    private static ClienteConexion iniciarSesion(String correo) throws Exception {
        ClienteConexion c = new ClienteConexion("localhost", puerto);
        c.conectar();
        Respuesta r = c.enviarOFallar(new Solicitud(Operacion.INICIAR_SESION)
                .con("correo", correo)
                .con("clave", "secreto1"));
        c.setToken(((Sesion) r.getDatos()).getToken());
        return c;
    }

    private static Respuesta registrar(ClienteConexion c, String nombre, String correo, String zona) throws Exception {
        return c.enviar(new Solicitud(Operacion.REGISTRAR)
                .con("nombre", nombre)
                .con("correo", correo)
                .con("zona", zona)
                .con("clave", "secreto1")
                .con("confirmacion", "secreto1"));
    }

    private static Respuesta crearPublicacion(ClienteConexion c, String alimento, BigDecimal cantidad,
            LocalDate limite) throws Exception {
        return c.enviar(new Solicitud(Operacion.CREAR_PUBLICACION)
                .con("alimento", alimento)
                .con("categoria", CategoriaAlimento.FRUTA)
                .con("cantidad", cantidad)
                .con("unidad", UnidadMedida.KG)
                .con("zona", "San Ramón")
                .con("preferencias", "")
                .con("descripcion", "Lote de prueba")
                .con("fechaLimite", limite));
    }

    @SuppressWarnings("unchecked")
    private static List<Publicacion> buscar(ClienteConexion c, String alimento, String zona) throws Exception {
        return (List<Publicacion>) c.enviarOFallar(new Solicitud(Operacion.BUSCAR_PUBLICACIONES)
                .con("alimento", alimento)
                .con("zona", zona)
                .con("categoria", null)).getDatos();
    }

    private static Respuesta proponer(ClienteConexion c, long idPublicacion, String alimento) throws Exception {
        return c.enviar(new Solicitud(Operacion.ENVIAR_PROPUESTA)
                .con("idPublicacion", idPublicacion)
                .con("alimento", alimento)
                .con("cantidad", new BigDecimal("2"))
                .con("unidad", UnidadMedida.KG)
                .con("mensaje", "Hola, me interesa intercambiar"));
    }
}
