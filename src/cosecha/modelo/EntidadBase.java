package cosecha.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

// Clase base para las entidades principales del modelo
public abstract class EntidadBase implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;
    private LocalDateTime fechaCreacion;

    protected EntidadBase() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    protected void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public abstract String descripcionCorta();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return id != 0 && id == ((EntidadBase) o).id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "#" + id + " (" + descripcionCorta() + ")";
    }
}
