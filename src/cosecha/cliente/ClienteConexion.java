package cosecha.cliente;

import cosecha.comun.Respuesta;
import cosecha.comun.Solicitud;
import cosecha.excepciones.ConexionException;
import cosecha.excepciones.CosechaException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

// Maneja la conexión por socket del cliente hacia el servidor
public class ClienteConexion {

    private final String host;
    private final int puerto;
    private Socket socket;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    private String token;

    public ClienteConexion(String host, int puerto) {
        this.host = host;
        this.puerto = puerto;
    }

    public synchronized void conectar() throws ConexionException {
        if (socket != null && !socket.isClosed()) {
            return;
        }
        try {
            socket = new Socket(host, puerto);
            salida = new ObjectOutputStream(socket.getOutputStream());
            salida.flush();
            entrada = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            socket = null;
            throw new ConexionException("No se pudo conectar con el servidor en " + host + ":" + puerto
                    + ". Revisá que el servidor esté corriendo.", e);
        }
    }

    public synchronized Respuesta enviar(Solicitud solicitud) throws ConexionException {
        conectar();
        solicitud.setToken(token);
        try {
            salida.reset();
            salida.writeObject(solicitud);
            salida.flush();
            return (Respuesta) entrada.readObject();
        } catch (IOException | ClassNotFoundException e) {
            cerrar();
            throw new ConexionException("Se perdió la conexión con el servidor. Intentá de nuevo.", e);
        }
    }

    public Respuesta enviarOFallar(Solicitud solicitud) throws CosechaException {
        Respuesta r = enviar(solicitud);
        if (!r.esExitosa()) {
            throw r.comoExcepcion();
        }
        return r;
    }

    public synchronized void cerrar() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            // Ignorar al cerrar
        }
        socket = null;
    }

    public synchronized void setToken(String token) {
        this.token = token;
    }
}
