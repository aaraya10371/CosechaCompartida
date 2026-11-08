package cosecha.cliente.gui.componentes;

import cosecha.cliente.gui.Estilo;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class CampoTexto extends JTextField {

    private final String ayuda;

    public CampoTexto(String ayuda) {
        this.ayuda = ayuda;
        decorar(this);
    }

    public static void decorar(JTextField campo) {
        campo.setFont(Estilo.fuente(13, false));
        campo.setForeground(Estilo.TEXTO);
        campo.setCaretColor(Estilo.VERDE_OSCURO);
        campo.setDisabledTextColor(Estilo.TEXTO_SUAVE);
        campo.setOpaque(false);
        campo.setBorder(Estilo.margen(8, 12, 8, 12));
        campo.setPreferredSize(new Dimension(160, 36));
    }

    public static JPasswordField campoClave() {
        JPasswordField campo = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                pintarFondo(this, g);
                super.paintComponent(g);
            }
        };
        decorar(campo);
        return campo;
    }

    static void pintarFondo(JComponent c, Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        boolean editable = !(c instanceof JTextField) || (((JTextField) c).isEditable() && c.isEnabled());
        g2.setColor(editable ? Estilo.CAMPO : Estilo.VERDE_SUAVE);
        g2.fillRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 10, 10);
        g2.setColor(c.hasFocus() ? new Color(0x9CC3A9) : Estilo.BORDE);
        g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 10, 10);
        g2.dispose();
    }

    @Override
    protected void paintComponent(Graphics g) {
        pintarFondo(this, g);
        super.paintComponent(g);
        if (getText().isEmpty() && ayuda != null && !hasFocus()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(new Color(0x9A9E97));
            g2.setFont(getFont());
            int y = (getHeight() + g2.getFontMetrics().getAscent() - g2.getFontMetrics().getDescent()) / 2;
            g2.drawString(ayuda, getInsets().left, y);
            g2.dispose();
        }
    }
}
