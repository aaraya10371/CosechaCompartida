package cosecha.cliente.gui;

import cosecha.cliente.gui.componentes.Boton;
import cosecha.cliente.gui.componentes.CampoTexto;
import cosecha.cliente.gui.componentes.PanelRedondeado;
import cosecha.comun.Operacion;
import cosecha.comun.Sesion;
import cosecha.comun.Solicitud;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class PantallaAcceso extends PantallaBase {

    private final CardLayout formularios = new CardLayout();
    private final JPanel panelFormularios = new JPanel(formularios);
    private final Boton pestanaIngreso = new Boton("Iniciar sesión", Boton.Tipo.NAV_ACTIVO);
    private final Boton pestanaRegistro = new Boton("Crear cuenta", Boton.Tipo.SECUNDARIO);

    // Ingreso
    private final CampoTexto correoIngreso = new CampoTexto("andrea@example.com");
    private final JPasswordField claveIngreso = CampoTexto.campoClave();

    // Registro
    private final CampoTexto nombreRegistro = new CampoTexto("Andrea Araya");
    private final CampoTexto correoRegistro = new CampoTexto("andrea@example.com");
    private final CampoTexto zonaRegistro = new CampoTexto("San Ramón, Alajuela");
    private final JPasswordField claveRegistro = CampoTexto.campoClave();
    private final JPasswordField confirmacionRegistro = CampoTexto.campoClave();

    public PantallaAcceso(VentanaPrincipal ventana) {
        super(ventana);
        contenido.setBorder(Estilo.margen(34, 68, 34, 68));

        JPanel dos = new Ui.Bloque(new GridLayout(1, 2, 44, 0));
        dos.add(new Portada());
        dos.add(crearTarjetaAcceso());
        Ui.agregar(contenido, dos);
    }

    private JPanel crearTarjetaAcceso() {
        PanelRedondeado tarjeta = Ui.tarjeta(new BorderLayout(), 26);
        JPanel col = Ui.columna();
        Ui.agregar(col, Estilo.etiqueta("¡Hola de nuevo!", 20, true, Estilo.VERDE_OSCURO));
        Ui.espacio(col, 12);
        Ui.agregar(col, Estilo.etiqueta("Tu comunidad tiene nuevos frutos listos para intercambiar.", 13, false,
                Estilo.TEXTO_SUAVE));
        Ui.espacio(col, 22);

        JPanel pestanas = new Ui.Bloque(new GridLayout(1, 2, 10, 0));
        pestanas.add(pestanaIngreso);
        pestanas.add(pestanaRegistro);
        pestanaIngreso.addActionListener(e -> mostrarFormulario(true));
        pestanaRegistro.addActionListener(e -> mostrarFormulario(false));
        Ui.agregar(col, pestanas);
        Ui.espacio(col, 18);

        panelFormularios.setOpaque(false);
        panelFormularios.add(crearFormularioIngreso(), "ingreso");
        panelFormularios.add(crearFormularioRegistro(), "registro");
        Ui.agregar(col, panelFormularios);
        tarjeta.add(col, BorderLayout.NORTH);
        return tarjeta;
    }

    private JPanel crearFormularioIngreso() {
        JPanel col = Ui.columna();
        Ui.agregar(col, Ui.campo("Correo electrónico", correoIngreso));
        Ui.espacio(col, 14);
        Ui.agregar(col, Ui.campo("Contraseña", claveIngreso));
        Ui.espacio(col, 20);
        Boton ingresar = new Boton("Ingresar  →", Boton.Tipo.PRIMARIO);
        ingresar.addActionListener(e -> ingresar());
        claveIngreso.addActionListener(e -> ingresar());
        JPanel fila = new Ui.Bloque(new BorderLayout());
        fila.add(ingresar);
        Ui.agregar(col, fila);
        Ui.espacio(col, 16);
        Ui.agregar(col, Estilo.etiqueta("Cultivar es cuidar. Compartir es transformar.", 11, false,
                Estilo.TEXTO_SUAVE));
        JPanel envoltura = new JPanel(new BorderLayout());
        envoltura.setOpaque(false);
        envoltura.add(col, BorderLayout.NORTH);
        return envoltura;
    }

    private JPanel crearFormularioRegistro() {
        JPanel col = Ui.columna();
        Ui.agregar(col, Ui.campo("Nombre completo", nombreRegistro));
        Ui.espacio(col, 10);
        Ui.agregar(col, Ui.dosColumnas(Ui.campo("Correo electrónico", correoRegistro),
                Ui.campo("Zona", zonaRegistro)));
        Ui.espacio(col, 10);
        Ui.agregar(col, Ui.dosColumnas(Ui.campo("Contraseña", claveRegistro),
                Ui.campo("Confirmar contraseña", confirmacionRegistro)));
        Ui.espacio(col, 18);
        Boton crear = new Boton("Crear cuenta  →", Boton.Tipo.PRIMARIO);
        crear.addActionListener(e -> registrar());
        JPanel fila = new Ui.Bloque(new BorderLayout());
        fila.add(crear);
        Ui.agregar(col, fila);
        Ui.espacio(col, 12);
        Ui.agregar(col, Estilo.etiqueta("Todos los campos son obligatorios.", 11, false, Estilo.TEXTO_SUAVE));
        JPanel envoltura = new JPanel(new BorderLayout());
        envoltura.setOpaque(false);
        envoltura.add(col, BorderLayout.NORTH);
        return envoltura;
    }

    private void mostrarFormulario(boolean ingreso) {
        formularios.show(panelFormularios, ingreso ? "ingreso" : "registro");
        pestanaIngreso.setTipo(ingreso ? Boton.Tipo.NAV_ACTIVO : Boton.Tipo.SECUNDARIO);
        pestanaRegistro.setTipo(ingreso ? Boton.Tipo.SECUNDARIO : Boton.Tipo.NAV_ACTIVO);
        pestanaIngreso.setFont(Estilo.fuente(13, ingreso));
        pestanaRegistro.setFont(Estilo.fuente(13, !ingreso));
    }

    private void ingresar() {
        Solicitud s = new Solicitud(Operacion.INICIAR_SESION)
                .con("correo", correoIngreso.getText())
                .con("clave", new String(claveIngreso.getPassword()));
        ejecutar(s, r -> {
            claveIngreso.setText("");
            ventana.iniciarSesion((Sesion) r.getDatos());
        });
    }

    private void registrar() {
        Solicitud s = new Solicitud(Operacion.REGISTRAR)
                .con("nombre", nombreRegistro.getText())
                .con("correo", correoRegistro.getText())
                .con("zona", zonaRegistro.getText())
                .con("clave", new String(claveRegistro.getPassword()))
                .con("confirmacion", new String(confirmacionRegistro.getPassword()));
        ejecutar(s, r -> {
            mostrarInfo(r.getMensaje());
            correoIngreso.setText(correoRegistro.getText().trim().toLowerCase());
            nombreRegistro.setText("");
            correoRegistro.setText("");
            zonaRegistro.setText("");
            claveRegistro.setText("");
            confirmacionRegistro.setText("");
            mostrarFormulario(true);
            claveIngreso.requestFocusInWindow();
        });
    }

    @Override
    public void refrescar() {
        claveIngreso.setText("");
        mostrarFormulario(true);
    }

    private static class Portada extends JPanel {

        Portada() {
            super(new BorderLayout());
            setOpaque(false);
            setPreferredSize(new Dimension(420, 450));
            setBorder(Estilo.margen(32, 30, 34, 30));
            JPanel arriba = Ui.columna();
            Ui.agregar(arriba, Estilo.etiqueta("EXCEDENTES CON PROPÓSITO", 11, false, new Color(0xE8EFE6)));
            add(arriba, BorderLayout.NORTH);

            JPanel abajo = Ui.columna();
            javax.swing.JLabel titulo = new javax.swing.JLabel(
                    "<html>Que nada se pierda,<br>que todo se comparta.</html>");
            titulo.setFont(Estilo.fuente(30, true));
            titulo.setForeground(Color.WHITE);
            Ui.agregar(abajo, titulo);
            Ui.espacio(abajo, 16);
            Ui.agregar(abajo, Estilo.parrafo("Conectá con vecinos, intercambiá los frutos de tu siembra y "
                    + "transformá los excedentes de tu huerta en variedad fresca para tu mesa.", 14,
                    new Color(0xF1F5EF), 340));
            Ui.espacio(abajo, 26);
            Ui.agregar(abajo, Estilo.etiqueta("Sin dinero de por medio · Vecinos reales · Alimentos vivos", 12,
                    false, new Color(0xE8EFE6)));
            add(abajo, BorderLayout.SOUTH);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            g2.setClip(new java.awt.geom.RoundRectangle2D.Double(0, 0, w, h, 26, 26));
            g2.setPaint(new GradientPaint(0, 0, new Color(0x2E5A35), w, h, new Color(0x13261A)));
            g2.fillRect(0, 0, w, h);
            g2.setColor(new Color(0xE3B53B));
            g2.fill(new Ellipse2D.Double(w * 0.55, h * 0.06, w * 0.33, h * 0.30));
            g2.setColor(new Color(0x7A9A3A));
            g2.fill(new Ellipse2D.Double(-w * 0.08, h * 0.02, w * 0.30, h * 0.22));
            g2.setColor(new Color(0xC2662B));
            g2.fill(new Ellipse2D.Double(w * 0.62, h * 0.72, w * 0.45, h * 0.16));
            g2.setColor(new Color(0x8E2F3B));
            g2.fill(new Ellipse2D.Double(w * 0.25, h * -0.05, w * 0.22, h * 0.17));
            g2.setColor(new Color(0, 0, 0, 120));
            g2.fillRect(0, 0, w, h);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
