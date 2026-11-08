package cosecha.cliente.gui;

import cosecha.comun.Respuesta;
import cosecha.comun.Solicitud;
import cosecha.excepciones.AccesoDenegadoException;
import cosecha.excepciones.CosechaException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.SwingWorker;

// Clase base para las pantallas de la aplicación
public abstract class PantallaBase extends JPanel {

    protected final VentanaPrincipal ventana;
    protected final JPanel contenido;

    protected PantallaBase(VentanaPrincipal ventana) {
        super(new BorderLayout());
        this.ventana = ventana;
        setBackground(Estilo.FONDO);

        contenido = Ui.columna();
        contenido.setBorder(Estilo.margen(30, 68, 40, 68));

        VistaDesplazable vista = new VistaDesplazable();
        vista.add(contenido, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(vista);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Estilo.FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        add(scroll, BorderLayout.CENTER);
    }

    public abstract void refrescar();

    public String getSeccionMenu() {
        return null;
    }

    protected void ejecutar(Solicitud solicitud, Consumer<Respuesta> alTerminar) {
        ejecutar(solicitud, alTerminar, this::mostrarError);
    }

    // Envía la solicitud en segundo plano para no trabar la ventana
    protected void ejecutar(Solicitud solicitud, Consumer<Respuesta> alTerminar,
            Consumer<CosechaException> alFallar) {
        ventana.setOcupado(true);
        new SwingWorker<Respuesta, Void>() {
            @Override
            protected Respuesta doInBackground() throws Exception {
                return ventana.getConexion().enviarOFallar(solicitud);
            }

            @Override
            protected void done() {
                ventana.setOcupado(false);
                try {
                    alTerminar.accept(get());
                } catch (ExecutionException e) {
                    Throwable causa = e.getCause();
                    if (causa instanceof AccesoDenegadoException && ventana.getSesion() != null
                            && causa.getMessage().contains("sesión")) {
                        ventana.sesionExpirada(causa.getMessage());
                    } else if (causa instanceof CosechaException) {
                        alFallar.accept((CosechaException) causa);
                    } else {
                        alFallar.accept(new CosechaException("Error inesperado: " + causa));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }.execute();
    }

    protected void mostrarError(CosechaException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), e.getTitulo(), JOptionPane.WARNING_MESSAGE);
    }

    protected void mostrarInfo(String mensaje) {
        if (mensaje != null && !mensaje.isEmpty()) {
            JOptionPane.showMessageDialog(this, mensaje, "Cosecha Compartida", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    protected boolean confirmar(String titulo, String mensaje) {
        return JOptionPane.showConfirmDialog(this, mensaje, titulo, JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    protected static void actualizarVista(JComponent c) {
        c.revalidate();
        c.repaint();
    }

    private static class VistaDesplazable extends JPanel implements Scrollable {

        VistaDesplazable() {
            super(new BorderLayout());
            setBackground(Estilo.FONDO);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle r, int o, int d) {
            return 18;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle r, int o, int d) {
            return r.height - 40;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return getParent() != null && getParent().getHeight() > getPreferredSize().height;
        }
    }
}
