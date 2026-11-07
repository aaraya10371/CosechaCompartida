package cosecha.excepciones;

/** Credenciales incorrectas, sesión inválida o el usuario no participa en el recurso. */
public class AccesoDenegadoException extends CosechaException {

    private static final long serialVersionUID = 1L;

    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }

    @Override
    public String getTitulo() {
        return "Acceso denegado";
    }
}
