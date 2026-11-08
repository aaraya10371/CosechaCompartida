package cosecha.cliente.gui.componentes;

import cosecha.cliente.gui.Estilo;
import cosecha.modelo.CategoriaAlimento;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import javax.swing.JComponent;

// Cuadro decorativo para cada publicación según su categoría
public class ImagenAlimento extends JComponent {

    private final String alimento;
    private final CategoriaAlimento categoria;

    public ImagenAlimento(String alimento, CategoriaAlimento categoria, int ancho, int alto) {
        this.alimento = alimento;
        this.categoria = categoria;
        setPreferredSize(new Dimension(ancho, alto));
        setMinimumSize(new Dimension(80, alto));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        Color[] c = Estilo.coloresCategoria(categoria);
        g2.setPaint(new GradientPaint(0, 0, c[0], w, h, c[1]));
        g2.fillRoundRect(0, 0, w, h, 16, 16);

        g2.setColor(new Color(255, 255, 255, 38));
        int r = Math.min(w, h);
        g2.fill(new Ellipse2D.Double(w * 0.62, -r * 0.25, r * 0.75, r * 0.75));
        g2.fill(new Ellipse2D.Double(-r * 0.2, h * 0.55, r * 0.6, r * 0.6));

        String inicial = alimento == null || alimento.isEmpty() ? "?" : alimento.substring(0, 1).toUpperCase();
        g2.setFont(Estilo.fuente(Math.max(28, h * 0.42f), true));
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(new Color(255, 255, 255, 225));
        g2.drawString(inicial, (w - fm.stringWidth(inicial)) / 2, (h + fm.getAscent() - fm.getDescent()) / 2 - 6);

        if (categoria != null) {
            g2.setFont(Estilo.fuente(11, true));
            String texto = categoria.getEtiqueta().toUpperCase();
            FontMetrics fm2 = g2.getFontMetrics();
            g2.setColor(new Color(255, 255, 255, 200));
            g2.drawString(texto, (w - fm2.stringWidth(texto)) / 2, h - 14);
        }
        g2.dispose();
    }
}
