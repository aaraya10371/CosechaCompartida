package cosecha.servidor.datos;

import cosecha.modelo.EntidadBase;
import java.util.List;

public interface Repositorio<T extends EntidadBase> {

    long insertar(T entidad);

    T buscarPorId(long id);

    List<T> listar();

    void eliminar(long id);
}
