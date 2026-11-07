package cosecha.excepciones;

/** Problema de comunicación entre el cliente y el servidor. */
public class ConexionException extends CosechaException {

    private static final long serialVersionUID = 1L;

    public ConexionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    @Override
    public String getTitulo() {
        return "Sin conexión con el servidor";
    }
}
