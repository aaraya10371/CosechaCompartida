package cosecha.modelo;

import cosecha.excepciones.ReglaNegocioException;
import cosecha.excepciones.ValidacionException;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Lote completo de un alimento que un vecino ofrece. No se divide: quien lo
 * recibe se lleva toda la cantidad publicada.
 */
public class Publicacion extends EntidadBase {

    private static final long serialVersionUID = 1L;

    private Usuario cultivador;
    private String alimento;
    private String descripcion;
    private CategoriaAlimento categoria;
    private BigDecimal cantidad;
    private UnidadMedida unidad;
    private String zona;
    private String preferencias;
    private LocalDate fechaLimite;
    private EstadoPublicacion estado;

    /* Datos calculados por el servidor para que la interfaz habilite o no las acciones. */
    private int propuestasPendientes;
    private boolean tieneAsociados;

    public Publicacion(Usuario cultivador, String alimento, CategoriaAlimento categoria,
            BigDecimal cantidad, UnidadMedida unidad, String zona, String preferencias,
            String descripcion, LocalDate fechaLimite) {
        this.cultivador = cultivador;
        this.alimento = alimento.trim();
        this.categoria = categoria;
        this.cantidad = cantidad;
        this.unidad = unidad;
        this.zona = zona.trim();
        this.preferencias = preferencias == null ? "" : preferencias.trim();
        this.descripcion = descripcion.trim();
        this.fechaLimite = fechaLimite;
        this.estado = EstadoPublicacion.DISPONIBLE;
    }

    public static void validarDatos(String alimento, CategoriaAlimento categoria, BigDecimal cantidad,
            UnidadMedida unidad, String zona, String descripcion, LocalDate fechaLimite, LocalDate hoy)
            throws ValidacionException {
        if (alimento == null || alimento.trim().isEmpty()) {
            throw new ValidacionException("Indicá el nombre del alimento.");
        }
        if (categoria == null || unidad == null) {
            throw new ValidacionException("Elegí la categoría y la unidad.");
        }
        if (cantidad == null || cantidad.signum() <= 0) {
            throw new ValidacionException("La cantidad debe ser un número mayor que cero.");
        }
        if (zona == null || zona.trim().isEmpty()) {
            throw new ValidacionException("Indicá la zona de la publicación.");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new ValidacionException("Agregá una descripción del lote.");
        }
        if (fechaLimite == null) {
            throw new ValidacionException("Indicá hasta cuándo está disponible.");
        }
        if (fechaLimite.isBefore(hoy)) {
            throw new ValidacionException("La fecha límite no puede ser anterior a hoy.");
        }
    }

    /** Disponible y con fecha límite vigente. */
    public boolean estaDisponible(LocalDate hoy) {
        return estado == EstadoPublicacion.DISPONIBLE && !fechaLimite.isBefore(hoy);
    }

    /**
     * Modifica los datos del lote. El servicio verifica antes que no haya propuestas pendientes.
     */
    public void editar(String alimento, CategoriaAlimento categoria, BigDecimal cantidad,
            UnidadMedida unidad, String zona, String preferencias, String descripcion,
            LocalDate fechaLimite, LocalDate hoy) throws ValidacionException, ReglaNegocioException {
        if (!estaDisponible(hoy)) {
            throw new ReglaNegocioException("Solo se pueden editar publicaciones disponibles y vigentes.");
        }
        validarDatos(alimento, categoria, cantidad, unidad, zona, descripcion, fechaLimite, hoy);
        this.alimento = alimento.trim();
        this.categoria = categoria;
        this.cantidad = cantidad;
        this.unidad = unidad;
        this.zona = zona.trim();
        this.preferencias = preferencias == null ? "" : preferencias.trim();
        this.descripcion = descripcion.trim();
        this.fechaLimite = fechaLimite;
    }

    public void reservar(LocalDate hoy) throws ReglaNegocioException {
        if (!estaDisponible(hoy)) {
            throw new ReglaNegocioException("Este lote ya no está disponible.");
        }
        estado = EstadoPublicacion.RESERVADA;
    }

    public void completar() throws ReglaNegocioException {
        if (estado != EstadoPublicacion.RESERVADA) {
            throw new ReglaNegocioException("Solo un lote reservado puede marcarse como intercambiado.");
        }
        estado = EstadoPublicacion.INTERCAMBIADA;
    }

    public void retirar() throws ReglaNegocioException {
        if (estado != EstadoPublicacion.DISPONIBLE) {
            throw new ReglaNegocioException("Solo se puede retirar un lote disponible.");
        }
        estado = EstadoPublicacion.RETIRADA;
    }

    public void vencer() {
        if (estado == EstadoPublicacion.DISPONIBLE) {
            estado = EstadoPublicacion.VENCIDA;
        }
    }

    /** Tras cancelar el acuerdo: vuelve a DISPONIBLE si sigue vigente, o pasa a VENCIDA. */
    public void liberarReserva(LocalDate hoy) throws ReglaNegocioException {
        if (estado != EstadoPublicacion.RESERVADA) {
            throw new ReglaNegocioException("El lote no está reservado.");
        }
        estado = fechaLimite.isBefore(hoy) ? EstadoPublicacion.VENCIDA : EstadoPublicacion.DISPONIBLE;
    }

    public boolean esDe(long idUsuario) {
        return cultivador.getId() == idUsuario;
    }

    /** Ej.: "3 kg de mangos". */
    public String getResumenLote() {
        return unidad.formatear(cantidad) + " de " + alimento.toLowerCase();
    }

    public Usuario getCultivador() {
        return cultivador;
    }

    public String getAlimento() {
        return alimento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public CategoriaAlimento getCategoria() {
        return categoria;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public UnidadMedida getUnidad() {
        return unidad;
    }

    public String getZona() {
        return zona;
    }

    public String getPreferencias() {
        return preferencias;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public EstadoPublicacion getEstado() {
        return estado;
    }

    public int getPropuestasPendientes() {
        return propuestasPendientes;
    }

    public void setPropuestasPendientes(int propuestasPendientes) {
        this.propuestasPendientes = propuestasPendientes;
    }

    public boolean isTieneAsociados() {
        return tieneAsociados;
    }

    public void setTieneAsociados(boolean tieneAsociados) {
        this.tieneAsociados = tieneAsociados;
    }

    @Override
    public String descripcionCorta() {
        return getResumenLote() + " [" + estado + "]";
    }
}
