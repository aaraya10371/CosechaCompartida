package cosecha.modelo;

/** Clasificación de los alimentos que se pueden intercambiar. */
public enum CategoriaAlimento {
    FRUTA("Fruta", "Frutas"),
    VERDURA("Verdura", "Verduras"),
    HIERBA("Hierba", "Hierbas");

    private final String etiqueta;
    private final String plural;

    CategoriaAlimento(String etiqueta, String plural) {
        this.etiqueta = etiqueta;
        this.plural = plural;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getPlural() {
        return plural;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
