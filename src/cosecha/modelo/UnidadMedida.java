package cosecha.modelo;

import java.math.BigDecimal;

/** Unidades en las que se indican las cantidades (no se calcula ningún valor económico). */
public enum UnidadMedida {
    KG("kg", "kg"),
    UNIDAD("unidad", "unidades"),
    MANOJO("manojo", "manojos");

    private final String singular;
    private final String plural;

    UnidadMedida(String singular, String plural) {
        this.singular = singular;
        this.plural = plural;
    }

    /** Devuelve la unidad en singular o plural según la cantidad. */
    public String etiqueta(BigDecimal cantidad) {
        return cantidad != null && cantidad.compareTo(BigDecimal.ONE) == 0 ? singular : plural;
    }

    /** Ej.: "3 kg", "12 unidades", "1 manojo". */
    public String formatear(BigDecimal cantidad) {
        return cantidad.stripTrailingZeros().toPlainString() + " " + etiqueta(cantidad);
    }

    @Override
    public String toString() {
        return plural;
    }
}
