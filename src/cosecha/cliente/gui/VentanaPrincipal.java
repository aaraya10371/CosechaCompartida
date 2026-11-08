package cosecha.cliente.gui;

import cosecha.cliente.ClienteConexion;
import cosecha.cliente.gui.componentes.Boton;
import cosecha.comun.Operacion;
import cosecha.comun.Sesion;
import cosecha.comun.Solicitud;
import cosecha.excepciones.ConexionException;
import cosecha.modelo.Usuario;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

public class VentanaPrincipal extends JFrame {

    public static final String ACCESO = "acceso";
    public static final String EXPLORAR = "explorar";
    public static final String DETALLE = "detalle";
    public static final String MIS_PUBLICACIONES = "mis";
    public static final String PERFIL = "perfil";

    private final ClienteConexion conexion;
    private Sesion sesion;

    private final CardLayout tarjetas = new CardLayout();
    private final JPanel cuerpo = new JPanel(tarjetas);
    private final Map<String, PantallaBase> pantallas = new LinkedHashMap<>();
    private final Map<String, Boton> botonesMenu = new LinkedHashMap<>();
    private final JPanel menu = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
    private final JLabel lema = Estilo.etiqueta("Del suelo local a la mesa vecina.", 13, false, Estilo.TEXTO_SUAVE);
    private Boton botonPerfil;
    private int tareasEnCurso;

    private final PantallaDetalle detalle;

    public VentanaPrincipal(ClienteConexion conexion) {
        super("Cosecha Compartida");
        this.conexion = conexion;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Rectangle pantalla = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        setMinimumSize(new Dimension(Math.min(1000, pantalla.width), Math.min(640, pantalla.height)));
        setSize(Math.min(1180, pantalla.width), Math.min(840, pantalla.height));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Estilo.FONDO);
        setLayout(new BorderLayout());
        add(crearEncabezado(), BorderLayout.NORTH);
        add(cuerpo, BorderLayout.CENTER);

        registrar(ACCESO, new PantallaAcceso(this));
        registrar(EXPLORAR, new PantallaExplorar(this));
        detalle = registrar(DETALLE, new PantallaDetalle(this));
        registrar(MIS_PUBLICACIONES, new PantallaMisPublicaciones(this));
        registrar(PERFIL, new PantallaPerfil(this));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                cerrarConexionAlSalir();
            }
        });
        mostrar(ACCESO);
    }

    private <T extends PantallaBase> T registrar(String clave, T pantalla) {
        pantallas.put(clave, pantalla);
        cuerpo.add(pantalla, clave);
        return pantalla;
    }

    private JPanel crearEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(Estilo.BLANCO);
        encabezado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Estilo.BORDE), Estilo.margen(14, 44, 14, 44)));

        JLabel logo = new JLabel("<html>cosecha<br>compartida</html>");
        logo.setFont(Estilo.fuente(17, true));
        logo.setForeground(Estilo.VERDE);
        logo.setPreferredSize(new Dimension(190, 46));
        logo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (sesion != null) {
                    mostrar(EXPLORAR);
                }
            }
        });
        encabezado.add(logo, BorderLayout.WEST);

        menu.setOpaque(false);
        menu.add(lema);
        String[][] opciones = {{EXPLORAR, "Explorar"}, {MIS_PUBLICACIONES, "Mis publicaciones"}};
        for (String[] op : opciones) {
            Boton b = new Boton(op[1], Boton.Tipo.NAV);
            b.setPreferredSize(new Dimension(op[1].length() > 12 ? 150 : 118, 36));
            b.addActionListener(e -> mostrar(op[0]));
            botonesMenu.put(op[0], b);
            menu.add(b);
        }
        botonPerfil = new Boton("Mi perfil", Boton.Tipo.PERFIL);
        botonPerfil.addActionListener(e -> mostrar(PERFIL));
        botonesMenu.put(PERFIL, botonPerfil);
        menu.add(botonPerfil);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.setBorder(Estilo.margen(5, 20, 0, 0));
        centro.add(menu, BorderLayout.WEST);
        encabezado.add(centro, BorderLayout.CENTER);
        return encabezado;
    }

    public void mostrar(String clave) {
        boolean conSesion = sesion != null;
        if (!conSesion) {
            clave = ACCESO;
        }
        lema.setVisible(!conSesion);
        PantallaBase pantalla = pantallas.get(clave);
        String seccion = pantalla.getSeccionMenu();
        for (Map.Entry<String, Boton> e : botonesMenu.entrySet()) {
            Boton b = e.getValue();
            b.setVisible(conSesion);
            if (b != botonPerfil) {
                b.setTipo(e.getKey().equals(seccion) ? Boton.Tipo.NAV_ACTIVO : Boton.Tipo.NAV);
            }
        }
        tarjetas.show(cuerpo, clave);
        pantalla.refrescar();
    }

    public void verPublicacion(long idPublicacion) {
        detalle.setPublicacion(idPublicacion);
        mostrar(DETALLE);
    }

    public void iniciarSesion(Sesion nueva) {
        this.sesion = nueva;
        conexion.setToken(nueva.getToken());
        actualizarUsuario(nueva.getUsuario());
        mostrar(EXPLORAR);
    }

    public void actualizarUsuario(Usuario usuario) {
        sesion.setUsuario(usuario);
        botonPerfil.setText("Mi perfil · " + usuario.getPrimerNombre());
        menu.revalidate();
    }

    public void cerrarSesion() {
        Solicitud s = new Solicitud(Operacion.CERRAR_SESION);
        setOcupado(true);
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    conexion.enviar(s);
                } catch (ConexionException e) {
                    // Ignorar si ya no hay conexión
                }
                return null;
            }

            @Override
            protected void done() {
                setOcupado(false);
                sesion = null;
                conexion.setToken(null);
                mostrar(ACCESO);
            }
        }.execute();
    }

    public void sesionExpirada(String mensaje) {
        sesion = null;
        conexion.setToken(null);
        JOptionPane.showMessageDialog(this, mensaje, "Sesión finalizada", JOptionPane.INFORMATION_MESSAGE);
        mostrar(ACCESO);
    }

    private void cerrarConexionAlSalir() {
        if (sesion != null) {
            try {
                conexion.enviar(new Solicitud(Operacion.CERRAR_SESION));
            } catch (ConexionException e) {
                // Ignorar error al salir
            }
        }
        conexion.cerrar();
    }

    public void setOcupado(boolean ocupado) {
        tareasEnCurso = Math.max(0, tareasEnCurso + (ocupado ? 1 : -1));
        setCursor(Cursor.getPredefinedCursor(tareasEnCurso > 0 ? Cursor.WAIT_CURSOR : Cursor.DEFAULT_CURSOR));
    }

    public ClienteConexion getConexion() {
        return conexion;
    }

    public Sesion getSesion() {
        return sesion;
    }

    public Usuario getUsuario() {
        return sesion == null ? null : sesion.getUsuario();
    }
}
