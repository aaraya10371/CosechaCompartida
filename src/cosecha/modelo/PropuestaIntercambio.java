package cosecha.modelo;

import cosecha.excepciones.ReglaNegocioException;
import cosecha.excepciones.ValidacionException;
import java.math.BigDecimal;

/**
 * Oferta de trueque que un vecino hace por el lote completo de una publicación.
 * El alimento ofrecido se describe directamente en la propuesta.
 */
public class PropuestaIntercambio extends EntidadBase {

    private static final long serialVersionUID = 1L;

    private Publicacion publicacion;
    private Usuario proponente;
    private String alimentoOfrecido;
    private BigDecimal cantidadOfrecida;
    private UnidadMedida unidad;
    private String mensaje;
    private EstadoPropuesta estado;

    public PropuestaIntercambio(Publicacion publicacion, Usuario proponente, String alimentoOfrecido,
            BigDecimal cantidadOfrecida, UnidadMedida unidad, String mensaje) {
        this.publicacion = publicacion;
        this.proponente = proponente;
        this.alimentoOfrecido = alimentoOfrecido.trim();
        this.cantidadOfrecida = cantidadOfrecida;
        this.unidad = unidad;
        this.mensaje = mensaje == null ? "" : mensaje.trim();
        this.estado = EstadoPropuesta.PENDIENTE;
    }

    public static void validarDatos(String alimento, BigDecimal cantidad, UnidadMedida unidad)
            throws ValidacionException {
        if (alimento == null || alimento.trim().isEmpty()) {
            throw new ValidacionException("Indicá qué alimento ofrecés.");
        }
        if (cantidad == null || cantidad.signum() <= 0) {
            throw new ValidacionException("La cantidad ofrecida debe ser mayor que cero.");
        }
        if (unidad == null) {
            throw new ValidacionException("Elegí la unidad de lo que ofrecés.");
        }
    }

    public void aceptar() throws ReglaNegocioException {
        exigirPendiente();
        estado = EstadoPropuesta.ACEPTADA;
    }

    public void rechazar() throws ReglaNegocioException {
        exigirPendiente();
        estado = EstadoPropuesta.RECHAZADA;
    }

    /**
     * El proponente retira su oferta pendiente, o bien se actualiza el estado
     * de la propuesta aceptada cuando el acuerdo se cancela.
     */
    public void cancelar() throws ReglaNegocioException {
        if (estado != EstadoPropuesta.PENDIENTE && estado != EstadoPropuesta.ACEPTADA) {
            throw new ReglaNegocioException("La propuesta ya fue " + estado.getEtiqueta().toLowerCase() + ".");
        }
        estado = EstadoPropuesta.CANCELADA;
    }

    private void exigirPendiente() throws ReglaNegocioException {
        if (estado != EstadoPropuesta.PENDIENTE) {
            throw new ReglaNegocioException("La propuesta ya no está pendiente (estado: "
                    + estado.getEtiqueta().toLowerCase() + ").");
        }
    }

    public boolean estaPendiente() {
        return estado == EstadoPropuesta.PENDIENTE;
    }

    /** Ej.: "2 kg de aguacates". */
    public String getResumenOferta() {
        return unidad.formatear(cantidadOfrecida) + " de " + alimentoOfrecido.toLowerCase();
    }

    public Publicacion getPublicacion() {
        return publicacion;
    }

    public Usuario getProponente() {
        return proponente;
    }

    public String getAlimentoOfrecido() {
        return alimentoOfrecido;
    }

    public BigDecimal getCantidadOfrecida() {
        return cantidadOfrecida;
    }

    public UnidadMedida getUnidad() {
        return unidad;
    }

    public String getMensaje() {
        return mensaje;
    }

    public EstadoPropuesta getEstado() {
        return estado;
    }

    @Override
    public String descripcionCorta() {
        return getResumenOferta() + " por " + publicacion.getResumenLote() + " [" + estado + "]";
    }
}
