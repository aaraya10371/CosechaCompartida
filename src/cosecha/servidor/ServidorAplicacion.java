package cosecha.servidor;

import cosecha.servidor.datos.AlmacenDatos;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ServidorAplicacion {

    public static final int PUERTO_PREDETERMINADO = 5050;

    private final AlmacenDatos almacen;
    private final ServicioUsuarios servicioUsuarios;
    private final ServicioIntercambios servicioIntercambios;
    private ServerSocket serverSocket;
    private boolean activo;

    public ServidorAplicacion(AlmacenDatos almacen) {
        this.almacen = almacen;
        this.servicioUsuarios = new ServicioUsuarios(almacen);
        this.servicioIntercambios = new ServicioIntercambios(almacen);
    }

    public int iniciar(int puerto) throws IOException {
        serverSocket = new ServerSocket(puerto);
        activo = true;
        Thread hiloServidor = new Thread(this::aceptarConexiones);
        hiloServidor.start();
        return serverSocket.getLocalPort();
    }

    private void aceptarConexiones() {
        int numeroCliente = 1;
        while (activo) {
            try {
                Socket cliente = serverSocket.accept();
                ManejadorCliente manejador = new ManejadorCliente(
                        cliente, almacen, servicioUsuarios, servicioIntercambios);
                Thread hiloCliente = new Thread(manejador, "Cliente-" + numeroCliente++);
                hiloCliente.start();
            } catch (SocketException e) {
                if (activo) {
                    System.err.println("Error en el socket del servidor: " + e.getMessage());
                }
            } catch (IOException e) {
                System.err.println("Error al aceptar conexión: " + e.getMessage());
            }
        }
    }

    public void detener() {
        activo = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error al cerrar el servidor: " + e.getMessage());
        }
    }

    public ServicioUsuarios getServicioUsuarios() {
        return servicioUsuarios;
    }

    public ServicioIntercambios getServicioIntercambios() {
        return servicioIntercambios;
    }

    public static void main(String[] args) {
        try {
            int puerto = args.length > 0 ? Integer.parseInt(args[0]) : PUERTO_PREDETERMINADO;
            File archivo = new File(args.length > 1 ? args[1] : "datos/cosecha.dat");
            AlmacenDatos almacen = new AlmacenDatos(archivo);
            ServidorAplicacion servidor = new ServidorAplicacion(almacen);

            if (almacen.estaVacio()) {
                DatosIniciales.cargar(servidor);
                System.out.println("Datos iniciales de prueba cargados.");
            }

            int puertoReal = servidor.iniciar(puerto);
            System.out.println("Servidor de Cosecha Compartida iniciado en el puerto " + puertoReal);
        } catch (Exception e) {
            System.err.println("No se pudo iniciar el servidor: " + e.getMessage());
        }
    }
}
