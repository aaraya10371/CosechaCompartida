package cosecha.servidor.datos;

import cosecha.modelo.CategoriaAlimento;
import cosecha.modelo.EstadoPublicacion;
import cosecha.modelo.Publicacion;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PublicacionRepositorio extends RepositorioMemoria<Publicacion> {

    private static final long serialVersionUID = 1L;

    public Publicacion buscarParaActualizar(long id) {
        return buscarPorId(id);
    }

    public List<Publicacion> buscarDisponibles(String alimento, String zona, CategoriaAlimento categoria,
            LocalDate hoy) {
        String a = normalizar(alimento);
        String z = normalizar(zona);
        List<Publicacion> resultado = new ArrayList<>();
        for (Publicacion p : registros.values()) {
            if (!p.estaDisponible(hoy)) {
                continue;
            }
            if (!a.isEmpty() && !normalizar(p.getAlimento()).contains(a)) {
                continue;
            }
            if (!z.isEmpty() && !normalizar(p.getZona()).contains(z)) {
                continue;
            }
            if (categoria != null && p.getCategoria() != categoria) {
                continue;
            }
            resultado.add(p);
        }
        ordenarRecientes(resultado);
        return resultado;
    }

    public List<Publicacion> listarPorPropietario(long idUsuario) {
        List<Publicacion> resultado = new ArrayList<>();
        for (Publicacion p : registros.values()) {
            if (p.esDe(idUsuario)) {
                resultado.add(p);
            }
        }
        ordenarRecientes(resultado);
        return resultado;
    }

    public List<Publicacion> listarPorEstado(EstadoPublicacion estado) {
        List<Publicacion> resultado = new ArrayList<>();
        for (Publicacion p : registros.values()) {
            if (p.getEstado() == estado) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public void actualizar(Publicacion publicacion) {
        registros.put(publicacion.getId(), publicacion);
    }

    // Pasa a minúsculas y quita tildes para que la búsqueda sea más flexible
    private static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.trim().toLowerCase()
                .replace('á', 'a')
                .replace('é', 'e')
                .replace('í', 'i')
                .replace('ó', 'o')
                .replace('ú', 'u')
                .replace('ü', 'u');
    }
}
