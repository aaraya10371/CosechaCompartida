package cosecha.cliente.gui;

import cosecha.cliente.gui.componentes.Boton;
import cosecha.cliente.gui.componentes.CampoTexto;
import cosecha.cliente.gui.componentes.Chip;
import cosecha.cliente.gui.componentes.ImagenAlimento;
import cosecha.cliente.gui.componentes.PanelRedondeado;
import cosecha.comun.Operacion;
import cosecha.comun.Solicitud;
import cosecha.excepciones.ValidacionException;
import cosecha.modelo.Publicacion;
import cosecha.modelo.UnidadMedida;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PantallaDetalle extends PantallaBase {

    private long idPublicacion;
    private Publicacion publicacion;

    private final JPanel izquierda = Ui.columna();
    private final CampoTexto alimento = new CampoTexto("Culantro");
    private final CampoTexto cantidad = new CampoTexto("2");
    private final JComboBox<UnidadMedida> unidad = new JComboBox<>(UnidadMedida.values());
    private final CampoTexto mensaje = new CampoTexto("Tengo culantro fresco para intercambiar.");
    private final JLabel textoLote = Estilo.etiqueta("", 13, false, Estilo.TEXTO_SUAVE);
    private final JLabel etiquetaMensaje = Estilo.etiqueta("Mensaje (opcional)", 12, true, Estilo.TEXTO);
    private final JPanel avisoContenedor = new Ui.Bloque(new BorderLayout());
    private final Boton enviar = new Boton("Enviar propuesta  →", Boton.Tipo.PRIMARIO);

    public PantallaDetalle(VentanaPrincipal ventana) {
        super(ventana);
        Boton volver = new Boton("←  Volver a explorar", Boton.Tipo.SECUNDARIO);
        volver.setPreferredSize(new Dimension(170, 36));
        volver.addActionListener(e -> ventana.mostrar(VentanaPrincipal.EXPLORAR));
        Ui.agregar(contenido, Ui.filaPegada(0, volver));
        Ui.espacio(contenido, 20);

        JPanel dos = new Ui.Bloque(new GridLayout(1, 2, 24, 0));
        PanelRedondeado tarjetaIzq = Ui.tarjeta(new BorderLayout(), 22);
        tarjetaIzq.add(izquierda, BorderLayout.NORTH);
        dos.add(tarjetaIzq);
        dos.add(crearFormulario());
        JPanel envoltura = new Ui.Bloque(new BorderLayout());
        envoltura.add(dos, BorderLayout.NORTH);
        Ui.agregar(contenido, envoltura);
    }

    private JPanel crearFormulario() {
        PanelRedondeado t = Ui.tarjeta(new BorderLayout(), 24);
        JPanel col = Ui.columna();
        Ui.agregar(col, Estilo.etiqueta("HAGAMOS UN INTERCAMBIO", 11, false, Estilo.VERDE));
        Ui.espacio(col, 12);
        Ui.agregar(col, Estilo.etiqueta("¿Qué tenés para ofrecer?", 24, true, Estilo.VERDE_OSCURO));
        Ui.espacio(col, 10);
        Ui.agregar(col, textoLote);
        Ui.espacio(col, 16);
        Ui.agregar(col, Ui.campo("Alimento que ofrecés", alimento));
        Ui.espacio(col, 12);
        Ui.estilizar(unidad);
        Ui.agregar(col, Ui.dosColumnas(Ui.campo("Cantidad", cantidad), Ui.campo("Unidad", unidad)));
        Ui.espacio(col, 12);
        JPanel campoMensaje = new Ui.Bloque(new BorderLayout(0, 6));
        campoMensaje.add(etiquetaMensaje, BorderLayout.NORTH);
        campoMensaje.add(mensaje, BorderLayout.CENTER);
        Ui.agregar(col, campoMensaje);
        Ui.espacio(col, 16);
        Ui.agregar(col, avisoContenedor);
        Ui.espacio(col, 16);
        enviar.addActionListener(e -> enviarPropuesta());
        JPanel fila = new Ui.Bloque(new BorderLayout());
        fila.add(enviar);
        Ui.agregar(col, fila);
        t.add(col, BorderLayout.NORTH);
        return t;
    }

    public void setPublicacion(long idPublicacion) {
        if (this.idPublicacion != idPublicacion) {
            alimento.setText("");
            cantidad.setText("");
            mensaje.setText("");
            unidad.setSelectedIndex(0);
        }
        this.idPublicacion = idPublicacion;
    }

    @Override
    public void refrescar() {
        if (idPublicacion == 0) {
            return;
        }
        ejecutar(new Solicitud(Operacion.CONSULTAR_PUBLICACION).con("idPublicacion", idPublicacion), r -> {
            publicacion = (Publicacion) r.getDatos();
            mostrarPublicacion();
        }, e -> {
            mostrarError(e);
            ventana.mostrar(VentanaPrincipal.EXPLORAR);
        });
    }

    private void mostrarPublicacion() {
        Publicacion p = publicacion;
        izquierda.removeAll();
        JPanel marco = new Ui.Bloque(new BorderLayout());
        marco.add(new ImagenAlimento(p.getAlimento(), p.getCategoria(), 380, 260));
        Ui.agregar(izquierda, marco);
        Ui.espacio(izquierda, 16);
        Ui.agregar(izquierda, Ui.filaPegada(0, Chip.deEstado(p.getEstado().getEtiqueta())));
        Ui.espacio(izquierda, 16);
        Ui.agregar(izquierda, Estilo.etiqueta(p.getAlimento(), 26, true, Estilo.VERDE_OSCURO));
        Ui.espacio(izquierda, 12);
        Ui.agregar(izquierda, Estilo.etiqueta(p.getUnidad().formatear(p.getCantidad()) + " · Lote completo", 18,
                true, Estilo.VERDE_OSCURO));
        Ui.espacio(izquierda, 14);
        Ui.agregar(izquierda, Estilo.etiqueta("Compartido por " + p.getCultivador().getNombreCompleto(), 13, false,
                Estilo.TEXTO_SUAVE));
        Ui.espacio(izquierda, 4);
        Ui.agregar(izquierda, Estilo.etiqueta(p.getZona() + " · Hasta el " + Estilo.fecha(p.getFechaLimite()), 13,
                false, Estilo.TEXTO_SUAVE));
        Ui.espacio(izquierda, 14);
        Ui.agregar(izquierda, Estilo.parrafo(p.getDescripcion(), 13, Estilo.TEXTO, 360));
        if (!p.getPreferencias().isEmpty()) {
            Ui.espacio(izquierda, 8);
            Ui.agregar(izquierda, Estilo.parrafo("Le interesa recibir: " + p.getPreferencias(), 13, Estilo.TEXTO,
                    360));
        }

        String nombre = p.getCultivador().getPrimerNombre();
        textoLote.setText("Tu propuesta será por " + (p.getCantidad().compareTo(BigDecimal.ONE) == 0 ? "el " : "los ")
                + p.getResumenLote() + ".");
        etiquetaMensaje.setText("Mensaje para " + nombre + " (opcional)");

        long yo = ventana.getUsuario().getId();
        String motivo = null;
        if (p.esDe(yo)) {
            motivo = "Esta es tu publicación: no podés hacerle propuestas.";
        } else if (!p.estaDisponible(LocalDate.now())) {
            motivo = "Este lote está " + p.getEstado().getEtiqueta().toLowerCase()
                    + " y no recibe propuestas.";
        }
        boolean habilitado = motivo == null;
        for (javax.swing.JComponent c : new javax.swing.JComponent[]{alimento, cantidad, unidad, mensaje, enviar}) {
            c.setEnabled(habilitado);
        }
        avisoContenedor.removeAll();
        avisoContenedor.add(habilitado
                ? Ui.cajaInfo("Un acuerdo entre ustedes", nombre
                        + " revisará tu propuesta. Los datos de entrega se definen al aceptarla.")
                : Ui.cajaInfo("Formulario no disponible", motivo));
        actualizarVista(contenido);
    }

    private void enviarPropuesta() {
        BigDecimal valor;
        try {
            valor = leerCantidad(cantidad.getText());
        } catch (ValidacionException e) {
            mostrarError(e);
            return;
        }
        Solicitud s = new Solicitud(Operacion.ENVIAR_PROPUESTA)
                .con("idPublicacion", idPublicacion)
                .con("alimento", alimento.getText())
                .con("cantidad", valor)
                .con("unidad", (UnidadMedida) unidad.getSelectedItem())
                .con("mensaje", mensaje.getText());
        ejecutar(s, r -> {
            mostrarInfo(r.getMensaje());
            alimento.setText("");
            cantidad.setText("");
            mensaje.setText("");
            ventana.mostrar(VentanaPrincipal.EXPLORAR);
        });
    }

    static BigDecimal leerCantidad(String texto) throws ValidacionException {
        try {
            BigDecimal v = new BigDecimal(texto.trim().replace(',', '.'));
            if (v.signum() <= 0) {
                throw new ValidacionException("La cantidad debe ser mayor que cero.");
            }
            return v;
        } catch (NumberFormatException e) {
            throw new ValidacionException("La cantidad debe ser un número (por ejemplo 2 o 1.5).");
        }
    }

    @Override
    public String getSeccionMenu() {
        return VentanaPrincipal.EXPLORAR;
    }
}
