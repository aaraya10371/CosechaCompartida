package cosecha.excepciones;

// Excepción base del proyecto; las demás heredan de esta clase
public class CosechaException extends Exception {

    private static final long serialVersionUID = 1L;

    public CosechaException(String mensaje) {
        super(mensaje);
    }

    public CosechaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    public String getTitulo() {
        return "No se pudo completar la acción";
    }
}
