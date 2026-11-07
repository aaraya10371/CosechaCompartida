package cosecha.modelo;

import cosecha.excepciones.ValidacionException;

/**
 * Vecino o vecina de la red. Su zona sirve como punto de partida para descubrir
 * cosechas cercanas y como zona sugerida al crear nuevas publicaciones.
 */
public class Usuario extends EntidadBase {

    private static final long serialVersionUID = 1L;

    private String nombreCompleto;
    private String correoElectronico;
    private String hashContrasena;
    private String salContrasena;
    private String zona;

    public Usuario(String nombreCompleto, String correoElectronico, String hashContrasena,
            String salContrasena, String zona) {
        this.nombreCompleto = nombreCompleto;
        this.correoElectronico = correoElectronico;
        this.hashContrasena = hashContrasena;
        this.salContrasena = salContrasena;
        this.zona = zona;
    }

    /**
     * Actualiza nombre y zona. No modifica publicaciones ni acuerdos existentes:
     * estos guardan su propia zona y sus propios datos.
     */
    public void actualizarPerfil(String nombre, String zona) throws ValidacionException {
        String n = nombre == null ? "" : nombre.trim();
        String z = zona == null ? "" : zona.trim();
        if (n.isEmpty() || z.isEmpty()) {
            throw new ValidacionException("El nombre completo y la zona son obligatorios.");
        }
        this.nombreCompleto = n;
        this.zona = z;
    }

    /**
     * Copia sin datos sensibles (hash y sal) para enviarla al cliente por el socket.
     */
    public Usuario copiaPublica() {
        Usuario copia = new Usuario(nombreCompleto, correoElectronico, null, null, zona);
        copia.setId(getId());
        copia.setFechaCreacion(getFechaCreacion());
        return copia;
    }

    public String getPrimerNombre() {
        String[] partes = nombreCompleto.trim().split("\\s+");
        return partes[0];
    }

    public String getIniciales() {
        String[] partes = nombreCompleto.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < partes.length && sb.length() < 2; i++) {
            if (!partes[i].isEmpty()) {
                sb.append(Character.toUpperCase(partes[i].charAt(0)));
            }
        }
        return sb.toString();
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public String getHashContrasena() {
        return hashContrasena;
    }

    public String getSalContrasena() {
        return salContrasena;
    }

    public String getZona() {
        return zona;
    }

    @Override
    public String descripcionCorta() {
        return nombreCompleto + " <" + correoElectronico + ">";
    }
}
