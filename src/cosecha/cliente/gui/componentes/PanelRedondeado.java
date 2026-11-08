package cosecha.cliente.gui.componentes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class PanelRedondeado extends JPanel {

    private Color fondo;
    private Color borde;
    private final int radio;

    public PanelRedondeado(LayoutManager layout, Color fondo, int radio) {
        super(layout);
        this.fondo = fondo;
        this.radio = radio;
        setOpaque(false);
    }

    public void setFondo(Color fondo) {
        this.fondo = fondo;
        repaint();
    }

    public void setBorde(Color borde) {
        this.borde = borde;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(fondo);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radio, radio);
        if (borde != null) {
            g2.setColor(borde);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radio, radio);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
