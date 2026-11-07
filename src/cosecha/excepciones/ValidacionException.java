package cosecha.excepciones;

/** Datos incompletos o con formato incorrecto (el usuario puede corregirlos). */
public class ValidacionException extends CosechaException {

    private static final long serialVersionUID = 1L;

    public ValidacionException(String mensaje) {
        super(mensaje);
    }

    @Override
    public String getTitulo() {
        return "Revisá los datos";
    }
}
