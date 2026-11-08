package cosecha.comun;

import cosecha.modelo.Usuario;
import java.io.Serializable;

/** Resultado de iniciar sesión: el token que identifica la sesión y el usuario. */
public class Sesion implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String token;
    private Usuario usuario;

    public Sesion(String token, Usuario usuario) {
        this.token = token;
        this.usuario = usuario;
    }

    public String getToken() {
        return token;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
