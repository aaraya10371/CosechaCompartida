package cosecha.servidor;

import cosecha.comun.Sesion;
import cosecha.excepciones.AccesoDenegadoException;
import cosecha.excepciones.CosechaException;
import cosecha.excepciones.ValidacionException;
import cosecha.modelo.Usuario;
import cosecha.servidor.datos.AlmacenDatos;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServicioUsuarios extends ServicioBase {

    private static final int LARGO_MINIMO_CLAVE = 6;

    // Guarda las sesiones activas (token -> idUsuario)
    private final Map<String, Long> sesiones = new ConcurrentHashMap<>();

    public ServicioUsuarios(AlmacenDatos almacen) {
        super(almacen);
    }

    public Usuario registrar(String nombre, String correo, String clave, String confirmacion, String zona)
            throws CosechaException {
        String n = nombre == null ? "" : nombre.trim();
        String z = zona == null ? "" : zona.trim();
        String c = Seguridad.normalizarCorreo(correo);
        if (n.isEmpty() || c.isEmpty() || z.isEmpty() || clave == null || clave.isEmpty()
                || confirmacion == null || confirmacion.isEmpty()) {
            throw new ValidacionException("Todos los campos son obligatorios.");
        }
        if (!Seguridad.correoValido(c)) {
            throw new ValidacionException("El correo electrónico no tiene un formato válido.");
        }
        if (clave.length() < LARGO_MINIMO_CLAVE) {
            throw new ValidacionException("La contraseña debe tener al menos " + LARGO_MINIMO_CLAVE + " caracteres.");
        }
        if (!clave.equals(confirmacion)) {
            throw new ValidacionException("Las contraseñas no coinciden.");
        }
        return enTransaccion(() -> {
            if (almacen.usuarios().buscarPorCorreo(c) != null) {
                throw new ValidacionException("Ya existe una cuenta con ese correo electrónico.");
            }
            String sal = Seguridad.generarSal();
            Usuario u = new Usuario(n, c, Seguridad.calcularHash(clave, sal), sal, z);
            almacen.usuarios().insertar(u);
            return u.copiaPublica();
        });
    }

    public Sesion iniciarSesion(String correo, String clave) throws CosechaException {
        String c = Seguridad.normalizarCorreo(correo);
        if (c.isEmpty() || clave == null || clave.isEmpty()) {
            throw new ValidacionException("Ingresá tu correo y tu contraseña.");
        }
        Usuario u = enLectura(() -> almacen.usuarios().buscarPorCorreo(c));
        if (u == null || !Seguridad.claveCorrecta(clave, u.getSalContrasena(), u.getHashContrasena())) {
            throw new AccesoDenegadoException("Correo o contraseña incorrectos.");
        }
        String token = Seguridad.generarToken();
        sesiones.put(token, u.getId());
        return new Sesion(token, u.copiaPublica());
    }

    public void cerrarSesion(String token) {
        if (token != null) {
            sesiones.remove(token);
        }
    }

    public Usuario usuarioDeSesion(String token) throws CosechaException {
        Long id = token == null ? null : sesiones.get(token);
        if (id == null) {
            throw new AccesoDenegadoException("Tu sesión no es válida. Volvé a ingresar.");
        }
        Usuario u = enLectura(() -> almacen.usuarios().buscarPorId(id));
        if (u == null) {
            sesiones.remove(token);
            throw new AccesoDenegadoException("La cuenta ya no existe.");
        }
        return u;
    }

    public Usuario consultarPerfil(String token) throws CosechaException {
        return usuarioDeSesion(token).copiaPublica();
    }

    public Usuario actualizarPerfil(String token, String nombre, String zona) throws CosechaException {
        long id = usuarioDeSesion(token).getId();
        return enTransaccion(() -> {
            Usuario u = almacen.usuarios().buscarPorId(id);
            if (u == null) {
                throw new AccesoDenegadoException("La cuenta ya no existe.");
            }
            u.actualizarPerfil(nombre, zona);
            almacen.usuarios().actualizarPerfil(u);
            return u.copiaPublica();
        });
    }
}
