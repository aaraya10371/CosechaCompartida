package cosecha.servidor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Pattern;

public class Seguridad {

    private static final SecureRandom ALEATORIO = new SecureRandom();
    private static final Pattern FORMATO_CORREO =
            Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)*\\.[A-Za-z]{2,}$");

    private Seguridad() {
    }

    public static String normalizarCorreo(String correo) {
        return correo == null ? "" : correo.trim().toLowerCase();
    }

    public static boolean correoValido(String correoNormalizado) {
        return FORMATO_CORREO.matcher(correoNormalizado).matches();
    }

    public static String generarSal() {
        byte[] sal = new byte[16];
        ALEATORIO.nextBytes(sal);
        return Base64.getEncoder().encodeToString(sal);
    }

    public static String calcularHash(String clave, String sal) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(sal.getBytes(StandardCharsets.UTF_8));
            byte[] hash = md.digest(clave.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al encriptar contraseña", e);
        }
    }

    public static boolean claveCorrecta(String clave, String sal, String hashGuardado) {
        String calculado = calcularHash(clave, sal);
        return calculado.equals(hashGuardado);
    }

    public static String generarToken() {
        return UUID.randomUUID().toString();
    }
}
