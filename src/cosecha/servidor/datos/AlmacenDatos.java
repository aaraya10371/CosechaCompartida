package cosecha.servidor.datos;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.locks.ReentrantLock;

// Maneja el almacenamiento en archivo (.dat) y el bloqueo entre hilos
public class AlmacenDatos {

    private static class Estado implements Serializable {
        private static final long serialVersionUID = 1L;
        UsuarioRepositorio usuarios = new UsuarioRepositorio();
        PublicacionRepositorio publicaciones = new PublicacionRepositorio();
        PropuestaRepositorio propuestas = new PropuestaRepositorio();
    }

    private final File archivo;
    private final ReentrantLock candado = new ReentrantLock();
    private Estado estado;
    private byte[] respaldo;

    public AlmacenDatos(File archivo) {
        this.archivo = archivo;
        this.estado = cargar(archivo);
    }

    private static Estado cargar(File archivo) {
        if (!archivo.exists()) {
            return new Estado();
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(archivo))) {
            return (Estado) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("No se pudo leer el archivo de datos: " + e.getMessage());
            return new Estado();
        }
    }

    public boolean estaVacio() {
        return estado.usuarios.contar() == 0;
    }

    public void iniciarTransaccion() {
        candado.lock();
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            try (ObjectOutputStream out = new ObjectOutputStream(buffer)) {
                out.writeObject(estado);
            }
            respaldo = buffer.toByteArray();
        } catch (IOException e) {
            candado.unlock();
            throw new RuntimeException("Error al respaldar estado", e);
        }
    }

    public void confirmar() {
        try {
            guardar();
            respaldo = null;
        } finally {
            candado.unlock();
        }
    }

    public void revertir() {
        try {
            if (respaldo != null) {
                try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(respaldo))) {
                    estado = (Estado) in.readObject();
                }
                respaldo = null;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al revertir cambios: " + e.getMessage());
        } finally {
            candado.unlock();
        }
    }

    public void bloquear() {
        candado.lock();
    }

    public void desbloquear() {
        candado.unlock();
    }

    private void guardar() {
        File carpeta = archivo.getAbsoluteFile().getParentFile();
        if (carpeta != null && !carpeta.exists()) {
            carpeta.mkdirs();
        }
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(archivo))) {
            out.writeObject(estado);
        } catch (IOException e) {
            System.err.println("Error al guardar los datos: " + e.getMessage());
        }
    }

    public UsuarioRepositorio usuarios() {
        return estado.usuarios;
    }

    public PublicacionRepositorio publicaciones() {
        return estado.publicaciones;
    }

    public PropuestaRepositorio propuestas() {
        return estado.propuestas;
    }
}
