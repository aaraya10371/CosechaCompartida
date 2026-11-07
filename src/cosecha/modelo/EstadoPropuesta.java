package cosecha.modelo;

public enum EstadoPropuesta {
    PENDIENTE("Pendiente"),
    ACEPTADA("Aceptada"),
    RECHAZADA("Rechazada"),
    CANCELADA("Cancelada");

    private final String etiqueta;

    EstadoPropuesta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
