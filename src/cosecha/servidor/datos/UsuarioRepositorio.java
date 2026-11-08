package cosecha.servidor.datos;

import cosecha.modelo.Usuario;

public class UsuarioRepositorio extends RepositorioMemoria<Usuario> {

    private static final long serialVersionUID = 1L;

    public Usuario buscarPorCorreo(String correoNormalizado) {
        for (Usuario u : registros.values()) {
            if (u.getCorreoElectronico().equals(correoNormalizado)) {
                return u;
            }
        }
        return null;
    }

    public void actualizarPerfil(Usuario usuario) {
        registros.put(usuario.getId(), usuario);
    }
}
