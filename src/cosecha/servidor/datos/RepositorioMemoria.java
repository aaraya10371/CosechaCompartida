package cosecha.servidor.datos;

import cosecha.modelo.EntidadBase;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Implementación en memoria de un repositorio usando un mapa
public abstract class RepositorioMemoria<T extends EntidadBase> implements Repositorio<T>, Serializable {

    private static final long serialVersionUID = 1L;

    protected final Map<Long, T> registros = new LinkedHashMap<>();
    private long siguienteId = 1;

    @Override
    public long insertar(T entidad) {
        long id = siguienteId++;
        entidad.setId(id);
        registros.put(id, entidad);
        return id;
    }

    @Override
    public T buscarPorId(long id) {
        return registros.get(id);
    }

    @Override
    public List<T> listar() {
        return new ArrayList<>(registros.values());
    }

    @Override
    public void eliminar(long id) {
        registros.remove(id);
    }

    protected void ordenarRecientes(List<T> lista) {
        lista.sort((a, b) -> {
            int cmp = b.getFechaCreacion().compareTo(a.getFechaCreacion());
            if (cmp != 0) {
                return cmp;
            }
            return Long.compare(b.getId(), a.getId());
        });
    }

    public int contar() {
        return registros.size();
    }
}
