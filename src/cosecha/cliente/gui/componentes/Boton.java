package cosecha.cliente.gui.componentes;

import cosecha.cliente.gui.Estilo;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JButton;

public class Boton extends JButton {

    public enum Tipo {
        PRIMARIO(Estilo.VERDE, Estilo.BLANCO, null),
        SECUNDARIO(Estilo.BLANCO, Estilo.VERDE_OSCURO, Estilo.BORDE),
        SUAVE(Estilo.VERDE_SUAVE, Estilo.VERDE_OSCURO, null),
        PELIGRO(null, Estilo.ROJO, null),
        NAV(null, Estilo.VERDE_OSCURO, null),
        NAV_ACTIVO(Estilo.BLANCO, Estilo.VERDE_OSCURO, Estilo.BORDE),
        PERFIL(Estilo.VERDE_SUAVE, Estilo.VERDE_OSCURO, null);

        final Color fondo;
        final Color texto;
        final Color borde;

        Tipo(Color fondo, Color texto, Color borde) {
            this.fondo = fondo;
            this.texto = texto;
            this.borde = borde;
        }
    }

    private Tipo tipo;

    public Boton(String texto, Tipo tipo) {
        super(texto);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(Estilo.fuente(tipo == Tipo.PERFIL ? 11 : 13, tipo == Tipo.PRIMARIO));
        setMargin(new java.awt.Insets(8, 18, 8, 18));
        setTipo(tipo);
    }

    public final void setTipo(Tipo tipo) {
        this.tipo = tipo;
        setForeground(tipo.texto);
        repaint();
    }

    public Tipo getTipo() {
        return tipo;
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(Math.max(d.width, 90), Math.max(d.height, tipo == Tipo.PERFIL ? 28 : 36));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color fondo = tipo.fondo;
        if (fondo != null) {
            if (!isEnabled()) {
                fondo = tipo == Tipo.PRIMARIO ? new Color(0xB9CCBF) : Estilo.GRIS_SUAVE;
            } else if (getModel().isRollover()) {
                fondo = tipo == Tipo.PRIMARIO ? Estilo.VERDE_OSCURO : new Color(0xF0F3EC);
            }
            g2.setColor(fondo);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
        }
        if (tipo.borde != null) {
            g2.setColor(tipo.borde);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
        }
        g2.dispose();
        setForeground(isEnabled() ? tipo.texto : new Color(0x9AA39C));
        super.paintComponent(g);
    }
}
