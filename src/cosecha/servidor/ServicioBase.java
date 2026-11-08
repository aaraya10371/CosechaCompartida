package cosecha.servidor;

import cosecha.excepciones.CosechaException;
import cosecha.servidor.datos.AlmacenDatos;
import java.time.LocalDate;

public abstract class ServicioBase {

    protected interface Tarea<R> {
        R ejecutar() throws CosechaException;
    }

    protected final AlmacenDatos almacen;

    protected ServicioBase(AlmacenDatos almacen) {
        this.almacen = almacen;
    }

    protected <R> R enTransaccion(Tarea<R> tarea) throws CosechaException {
        almacen.iniciarTransaccion();
        try {
            R resultado = tarea.ejecutar();
            almacen.confirmar();
            return resultado;
        } catch (CosechaException | RuntimeException e) {
            almacen.revertir();
            throw e;
        }
    }

    protected <R> R enLectura(Tarea<R> tarea) throws CosechaException {
        almacen.bloquear();
        try {
            return tarea.ejecutar();
        } finally {
            almacen.desbloquear();
        }
    }

    protected LocalDate hoy() {
        return LocalDate.now();
    }
}
