package cosecha.comun;

import cosecha.excepciones.AccesoDenegadoException;
import cosecha.excepciones.CosechaException;
import cosecha.excepciones.NoEncontradoException;
import cosecha.excepciones.ReglaNegocioException;
import cosecha.excepciones.ValidacionException;
import java.io.Serializable;

// Respuesta que devuelve el servidor después de procesar una solicitud
public class Respuesta implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum TipoError { NINGUNO, VALIDACION, REGLA, ACCESO, NO_ENCONTRADO, GENERAL }

    private final boolean exitosa;
    private final String mensaje;
    private final Serializable datos;
    private final TipoError tipoError;

    private Respuesta(boolean exitosa, String mensaje, Serializable datos, TipoError tipoError) {
        this.exitosa = exitosa;
        this.mensaje = mensaje;
        this.datos = datos;
        this.tipoError = tipoError;
    }

    public static Respuesta ok(Serializable datos) {
        return new Respuesta(true, "", datos, TipoError.NINGUNO);
    }

    public static Respuesta ok(Serializable datos, String mensaje) {
        return new Respuesta(true, mensaje, datos, TipoError.NINGUNO);
    }

    public static Respuesta error(CosechaException e) {
        TipoError tipo = TipoError.GENERAL;
        if (e instanceof ValidacionException) {
            tipo = TipoError.VALIDACION;
        } else if (e instanceof ReglaNegocioException) {
            tipo = TipoError.REGLA;
        } else if (e instanceof AccesoDenegadoException) {
            tipo = TipoError.ACCESO;
        } else if (e instanceof NoEncontradoException) {
            tipo = TipoError.NO_ENCONTRADO;
        }
        return new Respuesta(false, e.getMessage(), null, tipo);
    }

    public static Respuesta error(String mensaje) {
        return new Respuesta(false, mensaje, null, TipoError.GENERAL);
    }

    public CosechaException comoExcepcion() {
        switch (tipoError) {
            case VALIDACION:
                return new ValidacionException(mensaje);
            case REGLA:
                return new ReglaNegocioException(mensaje);
            case ACCESO:
                return new AccesoDenegadoException(mensaje);
            case NO_ENCONTRADO:
                return new NoEncontradoException(mensaje);
            default:
                return new CosechaException(mensaje);
        }
    }

    public boolean esExitosa() {
        return exitosa;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Serializable getDatos() {
        return datos;
    }

    public TipoError getTipoError() {
        return tipoError;
    }
}
