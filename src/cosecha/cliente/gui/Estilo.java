package cosecha.cliente.gui;

import cosecha.cliente.gui.componentes.Parrafo;
import cosecha.excepciones.ValidacionException;
import cosecha.modelo.CategoriaAlimento;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.Border;

public class Estilo {

    public static final Color FONDO = new Color(0xF6F5EF);
    public static final Color BLANCO = Color.WHITE;
    public static final Color VERDE = new Color(0x23684A);
    public static final Color VERDE_OSCURO = new Color(0x163B2B);
    public static final Color VERDE_SUAVE = new Color(0xE9F0E6);
    public static final Color VERDE_CLARO_TEXTO = new Color(0xD9EF9A);
    public static final Color CAMPO = new Color(0xF5F4EE);
    public static final Color BORDE = new Color(0xE2E2DA);
    public static final Color TEXTO = new Color(0x1F2A24);
    public static final Color TEXTO_SUAVE = new Color(0x6B7069);
    public static final Color ROJO = new Color(0xA8322D);
    public static final Color AMBAR = new Color(0xF6E6C8);
    public static final Color AMBAR_TEXTO = new Color(0x6E4B12);
    public static final Color GRIS_SUAVE = new Color(0xEFEEE8);

    private static final String FAMILIA = elegirFamilia();

    private static final String[] MESES = {"ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep",
        "oct", "nov", "dic"};

    private Estilo() {
    }

    private static String elegirFamilia() {
        try {
            List<String> disponibles = Arrays.asList(
                    GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
            for (String f : new String[]{"Manrope", "Inter", "Helvetica Neue", "Segoe UI", "Arial"}) {
                if (disponibles.contains(f)) {
                    return f;
                }
            }
        } catch (Exception e) {
            return Font.SANS_SERIF;
        }
        return Font.SANS_SERIF;
    }

    public static Font fuente(float tamano, boolean negrita) {
        return new Font(FAMILIA, negrita ? Font.BOLD : Font.PLAIN, Math.round(tamano));
    }

    public static JLabel etiqueta(String texto, float tamano, boolean negrita, Color color) {
        JLabel l = new JLabel(texto);
        l.setFont(fuente(tamano, negrita));
        l.setForeground(color);
        return l;
    }

    public static Parrafo parrafo(String texto, float tamano, Color color, int ancho) {
        return new Parrafo(texto == null ? "" : texto, fuente(tamano, false), color, ancho);
    }

    public static Border margen(int arriba, int izq, int abajo, int der) {
        return BorderFactory.createEmptyBorder(arriba, izq, abajo, der);
    }

    public static String fecha(LocalDate f) {
        return f == null ? "" : f.getDayOfMonth() + " " + MESES[f.getMonthValue() - 1] + " " + f.getYear();
    }

    public static String fechaCorta(LocalDate f) {
        return f == null ? "" : f.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public static LocalDate leerFecha(String texto) throws ValidacionException {
        try {
            String[] partes = texto.trim().split("/");
            if (partes.length != 3) {
                throw new ValidacionException("La fecha debe tener el formato dd/mm/aaaa, por ejemplo 18/10/2026.");
            }
            int dia = Integer.parseInt(partes[0].trim());
            int mes = Integer.parseInt(partes[1].trim());
            int anio = Integer.parseInt(partes[2].trim());
            if (anio < 2000 || anio > 2100) {
                throw new ValidacionException("Ingresá el año completo de 4 dígitos, por ejemplo 2026.");
            }
            return LocalDate.of(anio, mes, dia);
        } catch (NumberFormatException | DateTimeException e) {
            throw new ValidacionException("La fecha no es válida. Usá el formato dd/mm/aaaa, por ejemplo 18/10/2026.");
        }
    }

    public static Color[] coloresCategoria(CategoriaAlimento c) {
        if (c == null) {
            return new Color[]{VERDE, VERDE_OSCURO};
        }
        switch (c) {
            case FRUTA:
                return new Color[]{new Color(0xE9A23B), new Color(0x8C4A17)};
            case VERDURA:
                return new Color[]{new Color(0x7FB04A), new Color(0x2F5E2A)};
            default:
                return new Color[]{new Color(0x5FA36A), new Color(0x1F4D31)};
        }
    }
}
