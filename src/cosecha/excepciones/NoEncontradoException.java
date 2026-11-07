package cosecha.excepciones;

/** El registro solicitado no existe (por ejemplo, fue eliminado por su dueño). */
public class NoEncontradoException extends CosechaException {

    private static final long serialVersionUID = 1L;

    public NoEncontradoException(String mensaje) {
        super(mensaje);
    }

    @Override
    public String getTitulo() {
        return "No encontrado";
    }
}
