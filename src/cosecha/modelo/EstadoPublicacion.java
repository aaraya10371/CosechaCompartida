package cosecha.modelo;

public enum EstadoPublicacion {
    DISPONIBLE("Disponible"),
    RESERVADA("Reservada"),
    INTERCAMBIADA("Intercambiada"),
    RETIRADA("Retirada"),
    VENCIDA("Vencida");

    private final String etiqueta;

    EstadoPublicacion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
