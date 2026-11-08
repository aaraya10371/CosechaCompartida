package cosecha.cliente.gui.componentes;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

// JTextArea sin fondo ni bordes para mostrar textos de varias líneas
public class Parrafo extends JTextArea {

    private final int anchoSugerido;
    private int ultimoAncho;

    public Parrafo(String texto, Font fuente, Color color, int anchoSugerido) {
        super(texto);
        this.anchoSugerido = anchoSugerido;
        setFont(fuente);
        setForeground(color);
        setLineWrap(true);
        setWrapStyleWord(true);
        setEditable(false);
        setFocusable(false);
        setOpaque(false);
        setBorder(null);
        setHighlighter(null);
        setAlignmentX(Component.LEFT_ALIGNMENT);
        super.setBounds(0, 0, anchoSugerido, Short.MAX_VALUE);
    }

    @Override
    public Dimension getPreferredSize() {
        int w = getWidth() > 0 ? getWidth() : anchoSugerido;
        int hActual = getHeight();
        if (getWidth() <= 0 || hActual <= 0) {
            super.setBounds(getX(), getY(), w, Short.MAX_VALUE);
        }
        Dimension d = super.getPreferredSize();
        return new Dimension(anchoSugerido, d.height);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(40, getPreferredSize().height);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
    }

    @Override
    public void setBounds(int x, int y, int ancho, int alto) {
        super.setBounds(x, y, ancho, alto);
        if (ancho > 0 && ancho != ultimoAncho) {
            ultimoAncho = ancho;
            SwingUtilities.invokeLater(this::revalidate);
        }
    }
}
