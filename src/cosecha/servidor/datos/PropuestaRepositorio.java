package cosecha.servidor.datos;

import cosecha.excepciones.ReglaNegocioException;
import cosecha.modelo.PropuestaIntercambio;
import java.util.ArrayList;
import java.util.List;

public class PropuestaRepositorio extends RepositorioMemoria<PropuestaIntercambio> {

    private static final long serialVersionUID = 1L;

    public boolean existePendiente(long idPublicacion, long idProponente) {
        for (PropuestaIntercambio p : registros.values()) {
            if (p.estaPendiente()
                    && p.getPublicacion().getId() == idPublicacion
                    && p.getProponente().getId() == idProponente) {
                return true;
            }
        }
        return false;
    }

    public List<PropuestaIntercambio> listarPendientes(long idPublicacion) {
        List<PropuestaIntercambio> resultado = new ArrayList<>();
        for (PropuestaIntercambio p : registros.values()) {
            if (p.estaPendiente() && p.getPublicacion().getId() == idPublicacion) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public boolean existeAlguna(long idPublicacion) {
        for (PropuestaIntercambio p : registros.values()) {
            if (p.getPublicacion().getId() == idPublicacion) {
                return true;
            }
        }
        return false;
    }

    public int rechazarPendientes(long idPublicacion) throws ReglaNegocioException {
        int total = 0;
        for (PropuestaIntercambio p : listarPendientes(idPublicacion)) {
            p.rechazar();
            total++;
        }
        return total;
    }
}
