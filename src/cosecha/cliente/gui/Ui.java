package cosecha.cliente.gui;

import cosecha.cliente.gui.componentes.PanelRedondeado;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;

// Métodos auxiliares para acomodar paneles y tarjetas en las pantallas
public class Ui {

    private Ui() {
    }

    public static class Bloque extends JPanel {

        public Bloque(LayoutManager layout) {
            super(layout);
            setOpaque(false);
            setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        @Override
        public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
        }
    }

    public static JPanel columna() {
        JPanel p = new Bloque(null);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        return p;
    }

    public static <T extends JComponent> T agregar(JPanel columna, T componente) {
        componente.setAlignmentX(Component.LEFT_ALIGNMENT);
        columna.add(componente);
        return componente;
    }

    public static void espacio(JPanel columna, int alto) {
        Component c = Box.createVerticalStrut(alto);
        ((JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
        columna.add(c);
    }

    public static JPanel fila(int separacion, JComponent... componentes) {
        JPanel p = new Bloque(new FlowLayout(FlowLayout.LEFT, separacion, 0));
        for (JComponent c : componentes) {
            p.add(c);
        }
        return p;
    }

    public static JPanel filaPegada(int separacion, JComponent... componentes) {
        JPanel p = new Bloque(new FlowLayout(FlowLayout.LEFT, separacion, 0));
        p.setBorder(BorderFactory.createEmptyBorder(0, -separacion, 0, 0));
        for (JComponent c : componentes) {
            p.add(c);
        }
        return p;
    }

    public static JPanel campo(String etiqueta, JComponent campo) {
        JPanel p = new Bloque(new BorderLayout(0, 6));
        p.add(Estilo.etiqueta(etiqueta, 12, true, Estilo.TEXTO), BorderLayout.NORTH);
        p.add(campo, BorderLayout.CENTER);
        return p;
    }

    public static JPanel dosColumnas(JComponent a, JComponent b) {
        JPanel p = new Bloque(new GridLayout(1, 2, 18, 0));
        p.add(a);
        p.add(b);
        return p;
    }

    public static JPanel encabezado(String antetitulo, String titulo, String subtitulo) {
        JPanel col = columna();
        agregar(col, Estilo.etiqueta(antetitulo.toUpperCase(), 11, false, Estilo.VERDE));
        espacio(col, 14);
        agregar(col, Estilo.etiqueta(titulo, 30, true, Estilo.VERDE_OSCURO));
        espacio(col, 12);
        agregar(col, Estilo.etiqueta(subtitulo, 13, false, Estilo.TEXTO_SUAVE));
        espacio(col, 22);
        return col;
    }

    public static PanelRedondeado cajaInfo(String titulo, String texto) {
        PanelRedondeado caja = new PanelRedondeado(new BorderLayout(0, 6), Estilo.VERDE_SUAVE, 18) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        caja.setBorder(Estilo.margen(14, 16, 14, 16));
        caja.add(Estilo.etiqueta(titulo, 12, true, Estilo.VERDE_OSCURO), BorderLayout.NORTH);
        caja.add(Estilo.parrafo(texto, 13, Estilo.TEXTO_SUAVE, 300), BorderLayout.CENTER);
        return caja;
    }

    public static <T> JComboBox<T> estilizar(JComboBox<T> combo) {
        combo.setFont(Estilo.fuente(13, false));
        combo.setForeground(Estilo.TEXTO);
        combo.setBackground(Estilo.CAMPO);
        combo.setPreferredSize(new Dimension(160, 36));
        combo.setBorder(new LineBorder(Estilo.BORDE, 1, true));
        combo.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton b = new JButton("▾");
                b.setFont(Estilo.fuente(13, false));
                b.setForeground(Estilo.TEXTO_SUAVE);
                b.setBorder(Estilo.margen(0, 8, 0, 10));
                b.setContentAreaFilled(false);
                b.setFocusPainted(false);
                return b;
            }

            @Override
            public void paintCurrentValueBackground(java.awt.Graphics g, Rectangle r, boolean foco) {
                g.setColor(Estilo.CAMPO);
                g.fillRect(r.x, r.y, r.width, r.height);
            }
        });
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, false);
                setBorder(Estilo.margen(6, 10, 6, 10));
                if (index >= 0) {
                    setBackground(selected ? Estilo.VERDE_SUAVE : Estilo.BLANCO);
                } else {
                    setOpaque(false);
                }
                setForeground(combo.isEnabled() ? Estilo.TEXTO : Estilo.TEXTO_SUAVE);
                return this;
            }
        });
        return combo;
    }

    public static PanelRedondeado tarjeta(LayoutManager layout, int margen) {
        return tarjeta(layout, margen, Estilo.BLANCO);
    }

    public static PanelRedondeado tarjeta(LayoutManager layout, int margen, Color fondo) {
        PanelRedondeado t = new PanelRedondeado(layout, fondo, 24) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        t.setBorder(Estilo.margen(margen, margen, margen, margen));
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        return t;
    }
}
