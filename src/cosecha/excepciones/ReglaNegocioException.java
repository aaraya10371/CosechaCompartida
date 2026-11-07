package cosecha.excepciones;

/** La acción viola una regla del intercambio (estado incorrecto, fecha fuera de rango, etc.). */
public class ReglaNegocioException extends CosechaException {

    private static final long serialVersionUID = 1L;

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }

    @Override
    public String getTitulo() {
        return "Acción no permitida";
    }
}
