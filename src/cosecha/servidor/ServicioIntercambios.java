package cosecha.servidor;

import cosecha.excepciones.AccesoDenegadoException;
import cosecha.excepciones.CosechaException;
import cosecha.excepciones.NoEncontradoException;
import cosecha.excepciones.ReglaNegocioException;
import cosecha.modelo.CategoriaAlimento;
import cosecha.modelo.EstadoPublicacion;
import cosecha.modelo.PropuestaIntercambio;
import cosecha.modelo.Publicacion;
import cosecha.modelo.UnidadMedida;
import cosecha.modelo.Usuario;
import cosecha.servidor.datos.AlmacenDatos;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

public class ServicioIntercambios extends ServicioBase {

    public ServicioIntercambios(AlmacenDatos almacen) {
        super(almacen);
    }

    public Publicacion crearPublicacion(long idUsuario, String alimento, CategoriaAlimento categoria,
            BigDecimal cantidad, UnidadMedida unidad, String zona, String preferencias, String descripcion,
            LocalDate fechaLimite) throws CosechaException {
        Publicacion.validarDatos(alimento, categoria, cantidad, unidad, zona, descripcion, fechaLimite, hoy());
        return enTransaccion(() -> {
            Usuario cultivador = usuario(idUsuario);
            Publicacion p = new Publicacion(cultivador, alimento, categoria, cantidad, unidad, zona,
                    preferencias, descripcion, fechaLimite);
            almacen.publicaciones().insertar(p);
            return completarInfo(p);
        });
    }

    public ArrayList<Publicacion> buscarPublicaciones(String alimento, String zona, CategoriaAlimento categoria)
            throws CosechaException {
        return enTransaccion(() -> {
            actualizarVencimientos();
            return new ArrayList<>(almacen.publicaciones().buscarDisponibles(alimento, zona, categoria, hoy()));
        });
    }

    public Publicacion consultarPublicacion(long idPublicacion) throws CosechaException {
        return enTransaccion(() -> {
            actualizarVencimientos();
            return completarInfo(publicacion(idPublicacion));
        });
    }

    public ArrayList<Publicacion> listarMisPublicaciones(long idUsuario) throws CosechaException {
        return enTransaccion(() -> {
            actualizarVencimientos();
            ArrayList<Publicacion> lista = new ArrayList<>(almacen.publicaciones().listarPorPropietario(idUsuario));
            for (Publicacion p : lista) {
                completarInfo(p);
            }
            return lista;
        });
    }

    public Publicacion editarPublicacion(long idUsuario, long idPublicacion, String alimento,
            CategoriaAlimento categoria, BigDecimal cantidad, UnidadMedida unidad, String zona,
            String preferencias, String descripcion, LocalDate fechaLimite) throws CosechaException {
        return enTransaccion(() -> {
            actualizarVencimientos();
            Publicacion p = publicacionPropia(idUsuario, idPublicacion);
            if (!almacen.propuestas().listarPendientes(idPublicacion).isEmpty()) {
                throw new ReglaNegocioException("Este lote tiene propuestas pendientes: no se puede editar.");
            }
            p.editar(alimento, categoria, cantidad, unidad, zona, preferencias, descripcion, fechaLimite, hoy());
            almacen.publicaciones().actualizar(p);
            return completarInfo(p);
        });
    }

    public Publicacion retirarPublicacion(long idUsuario, long idPublicacion) throws CosechaException {
        return enTransaccion(() -> {
            actualizarVencimientos();
            Publicacion p = publicacionPropia(idUsuario, idPublicacion);
            p.retirar();
            almacen.propuestas().rechazarPendientes(idPublicacion);
            almacen.publicaciones().actualizar(p);
            return completarInfo(p);
        });
    }

    public void eliminarPublicacion(long idUsuario, long idPublicacion) throws CosechaException {
        enTransaccion(() -> {
            actualizarVencimientos();
            Publicacion p = publicacionPropia(idUsuario, idPublicacion);
            if (p.getEstado() != EstadoPublicacion.DISPONIBLE && p.getEstado() != EstadoPublicacion.RETIRADA) {
                throw new ReglaNegocioException("Solo se pueden eliminar lotes disponibles o retirados.");
            }
            if (almacen.propuestas().existeAlguna(idPublicacion)) {
                throw new ReglaNegocioException(
                        "Este lote tiene propuestas asociadas. Podés retirarlo, pero no eliminarlo.");
            }
            almacen.publicaciones().eliminar(idPublicacion);
            return null;
        });
    }

    public PropuestaIntercambio enviarPropuesta(long idUsuario, long idPublicacion, String alimento,
            BigDecimal cantidad, UnidadMedida unidad, String mensaje) throws CosechaException {
        PropuestaIntercambio.validarDatos(alimento, cantidad, unidad);
        return enTransaccion(() -> {
            actualizarVencimientos();
            Usuario proponente = usuario(idUsuario);
            Publicacion p = publicacion(idPublicacion);
            if (p.esDe(idUsuario)) {
                throw new ReglaNegocioException("No podés proponer un intercambio sobre tu propia publicación.");
            }
            if (!p.estaDisponible(hoy())) {
                throw new ReglaNegocioException("Este lote ya no está disponible.");
            }
            if (almacen.propuestas().existePendiente(idPublicacion, idUsuario)) {
                throw new ReglaNegocioException("Ya tenés una propuesta pendiente para este lote.");
            }
            PropuestaIntercambio propuesta = new PropuestaIntercambio(p, proponente, alimento, cantidad, unidad, mensaje);
            almacen.propuestas().insertar(propuesta);
            return propuesta;
        });
    }

    private void actualizarVencimientos() throws ReglaNegocioException {
        LocalDate hoy = hoy();
        for (Publicacion p : almacen.publicaciones().listarPorEstado(EstadoPublicacion.DISPONIBLE)) {
            if (p.getFechaLimite().isBefore(hoy)) {
                p.vencer();
                almacen.propuestas().rechazarPendientes(p.getId());
            }
        }
    }

    private Publicacion completarInfo(Publicacion p) {
        p.setPropuestasPendientes(almacen.propuestas().listarPendientes(p.getId()).size());
        p.setTieneAsociados(almacen.propuestas().existeAlguna(p.getId()));
        return p;
    }

    private Usuario usuario(long id) throws NoEncontradoException {
        Usuario u = almacen.usuarios().buscarPorId(id);
        if (u == null) {
            throw new NoEncontradoException("La cuenta ya no existe.");
        }
        return u;
    }

    private Publicacion publicacion(long id) throws NoEncontradoException {
        Publicacion p = almacen.publicaciones().buscarPorId(id);
        if (p == null) {
            throw new NoEncontradoException("La publicación ya no existe.");
        }
        return p;
    }

    private Publicacion publicacionPropia(long idUsuario, long idPublicacion) throws CosechaException {
        Publicacion p = almacen.publicaciones().buscarParaActualizar(idPublicacion);
        if (p == null) {
            throw new NoEncontradoException("La publicación ya no existe.");
        }
        if (!p.esDe(idUsuario)) {
            throw new AccesoDenegadoException("Esta publicación no es tuya.");
        }
        return p;
    }
}
