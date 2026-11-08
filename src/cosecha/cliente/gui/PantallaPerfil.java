package cosecha.cliente.gui;

import cosecha.cliente.gui.componentes.Boton;
import cosecha.cliente.gui.componentes.CampoTexto;
import cosecha.cliente.gui.componentes.PanelRedondeado;
import cosecha.comun.Operacion;
import cosecha.comun.Solicitud;
import cosecha.modelo.Usuario;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PantallaPerfil extends PantallaBase {

    private Usuario guardado;
    private final Iniciales iniciales = new Iniciales();
    private final JLabel nombreTarjeta = Estilo.etiqueta("", 19, true, Estilo.VERDE_OSCURO);
    private final JLabel correoTarjeta = Estilo.etiqueta("", 13, false, Estilo.TEXTO_SUAVE);
    private final JLabel zonaTarjeta = Estilo.etiqueta("", 12, true, Estilo.VERDE);
    private final CampoTexto nombre = new CampoTexto("Nombre y apellidos");
    private final CampoTexto correo = new CampoTexto("");
    private final CampoTexto zona = new CampoTexto("Barrio o localidad");

    public PantallaPerfil(VentanaPrincipal ventana) {
        super(ventana);
        Ui.agregar(contenido, Ui.encabezado("Tu espacio en la comunidad", "Mi perfil",
                "Mantené tus datos al día para compartir tu cosecha con tus vecinos."));

        JPanel cuerpo = new Ui.Bloque(new BorderLayout(24, 0));
        PanelRedondeado izq = Ui.tarjeta(new BorderLayout(), 22, Estilo.VERDE_SUAVE);
        JPanel c1 = Ui.columna();
        Ui.agregar(c1, iniciales);
        Ui.espacio(c1, 18);
        Ui.agregar(c1, nombreTarjeta);
        Ui.espacio(c1, 12);
        Ui.agregar(c1, correoTarjeta);
        Ui.espacio(c1, 12);
        Ui.agregar(c1, zonaTarjeta);
        Ui.espacio(c1, 16);
        Ui.agregar(c1, Estilo.parrafo("Tu zona ayuda a encontrar alimentos y vecinos cerca de vos.", 13,
                Estilo.TEXTO_SUAVE, 180));
        izq.add(c1, BorderLayout.NORTH);
        JPanel envIzq = new JPanel(new BorderLayout());
        envIzq.setOpaque(false);
        envIzq.setPreferredSize(new Dimension(230, 10));
        envIzq.add(izq, BorderLayout.NORTH);
        cuerpo.add(envIzq, BorderLayout.WEST);

        PanelRedondeado der = Ui.tarjeta(new BorderLayout(), 24);
        JPanel c2 = Ui.columna();
        Ui.agregar(c2, Estilo.etiqueta("Datos de mi cuenta", 19, true, Estilo.VERDE_OSCURO));
        Ui.espacio(c2, 8);
        Ui.agregar(c2, Estilo.etiqueta("Los campos con * son obligatorios.", 11, false, Estilo.TEXTO_SUAVE));
        Ui.espacio(c2, 16);
        correo.setEditable(false);
        correo.setForeground(Estilo.TEXTO_SUAVE);
        Ui.agregar(c2, Ui.dosColumnas(Ui.campo("Nombre completo *", nombre),
                Ui.campo("Correo electrónico · solo lectura", correo)));
        Ui.espacio(c2, 14);
        Ui.agregar(c2, Ui.campo("Barrio o localidad *", zona));
        Ui.espacio(c2, 10);
        Ui.agregar(c2, Estilo.etiqueta("Tu correo se conserva para iniciar sesión.", 11, false, Estilo.TEXTO_SUAVE));
        Ui.espacio(c2, 16);
        Ui.agregar(c2, Ui.cajaInfo("Tu zona en las publicaciones", "La nueva zona se usará al crear "
                + "publicaciones. Tus lotes y acuerdos existentes conservarán sus datos."));
        Ui.espacio(c2, 18);
        Boton cerrar = new Boton("Cerrar sesión", Boton.Tipo.PELIGRO);
        cerrar.addActionListener(e -> {
            if (confirmar("Cerrar sesión", "¿Querés cerrar tu sesión?")) {
                ventana.cerrarSesion();
            }
        });
        Boton cancelar = new Boton("Cancelar", Boton.Tipo.SECUNDARIO);
        cancelar.addActionListener(e -> cargarFormulario(guardado));
        Boton guardar = new Boton("Guardar cambios", Boton.Tipo.PRIMARIO);
        guardar.addActionListener(e -> guardar());
        cerrar.setPreferredSize(new Dimension(130, 36));
        cancelar.setPreferredSize(new Dimension(112, 36));
        guardar.setPreferredSize(new Dimension(160, 36));
        Ui.agregar(c2, Ui.filaPegada(12, cerrar, cancelar, guardar));
        der.add(c2, BorderLayout.NORTH);
        JPanel envDer = new JPanel(new BorderLayout());
        envDer.setOpaque(false);
        envDer.add(der, BorderLayout.NORTH);
        cuerpo.add(envDer, BorderLayout.CENTER);
        Ui.agregar(contenido, cuerpo);
    }

    @Override
    public void refrescar() {
        ejecutar(new Solicitud(Operacion.CONSULTAR_PERFIL), r -> {
            guardado = (Usuario) r.getDatos();
            mostrarTarjeta(guardado);
            cargarFormulario(guardado);
        });
    }

    private void mostrarTarjeta(Usuario u) {
        iniciales.texto = u.getIniciales();
        iniciales.repaint();
        nombreTarjeta.setText(u.getNombreCompleto());
        correoTarjeta.setText(u.getCorreoElectronico());
        zonaTarjeta.setText(u.getZona());
    }

    private void cargarFormulario(Usuario u) {
        if (u == null) {
            return;
        }
        nombre.setText(u.getNombreCompleto());
        correo.setText(u.getCorreoElectronico());
        zona.setText(u.getZona());
    }

    private void guardar() {
        Solicitud s = new Solicitud(Operacion.ACTUALIZAR_PERFIL)
                .con("nombre", nombre.getText())
                .con("zona", zona.getText());
        ejecutar(s, r -> {
            guardado = (Usuario) r.getDatos();
            mostrarTarjeta(guardado);
            cargarFormulario(guardado);
            ventana.actualizarUsuario(guardado);
            mostrarInfo(r.getMensaje());
        });
    }

    @Override
    public String getSeccionMenu() {
        return VentanaPrincipal.PERFIL;
    }

    private static class Iniciales extends JComponent {
        private String texto = "";

        Iniciales() {
            setPreferredSize(new Dimension(58, 58));
            setMaximumSize(new Dimension(58, 58));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(Estilo.BLANCO);
            g2.fillOval(0, 0, 57, 57);
            g2.setColor(Estilo.VERDE_OSCURO);
            g2.setFont(Estilo.fuente(18, true));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(texto, (58 - fm.stringWidth(texto)) / 2, (58 + fm.getAscent() - fm.getDescent()) / 2);
            g2.dispose();
        }
    }
}
