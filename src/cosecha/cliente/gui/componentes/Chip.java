package cosecha.cliente.gui.componentes;

import cosecha.cliente.gui.Estilo;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;

public class Chip extends JLabel {

    private Color fondo;

    public Chip(String texto, Color fondo, Color colorTexto) {
        super(texto);
        this.fondo = fondo;
        setForeground(colorTexto);
        setFont(Estilo.fuente(11, false));
        setBorder(Estilo.margen(5, 9, 5, 9));
        setOpaque(false);
    }

    public static Chip deEstado(String estado) {
        String e = estado.toLowerCase();
        if (e.startsWith("pendiente")) {
            return new Chip(estado, Estilo.AMBAR, Estilo.AMBAR_TEXTO);
        }
        if (e.startsWith("rechazada") || e.startsWith("cancelad") || e.startsWith("retirad")
                || e.startsWith("vencida")) {
            return new Chip(estado, Estilo.GRIS_SUAVE, Estilo.TEXTO_SUAVE);
        }
        return new Chip(estado, Estilo.VERDE_SUAVE, Estilo.VERDE_OSCURO);
    }

    public void setFondo(Color fondo) {
        this.fondo = fondo;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(fondo);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
        g2.dispose();
        super.paintComponent(g);
    }
}
