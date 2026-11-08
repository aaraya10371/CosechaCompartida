package cosecha.cliente.gui;

import cosecha.cliente.gui.componentes.Boton;
import cosecha.cliente.gui.componentes.CampoTexto;
import cosecha.cliente.gui.componentes.Chip;
import cosecha.cliente.gui.componentes.PanelRedondeado;
import cosecha.comun.Operacion;
import cosecha.comun.Solicitud;
import cosecha.excepciones.CosechaException;
import cosecha.modelo.CategoriaAlimento;
import cosecha.modelo.EstadoPublicacion;
import cosecha.modelo.Publicacion;
import cosecha.modelo.UnidadMedida;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PantallaMisPublicaciones extends PantallaBase {

    private List<Publicacion> publicaciones = new ArrayList<>();
    private Publicacion seleccionada;

    private final JPanel lista = Ui.columna();
    private final JLabel tituloFormulario = Estilo.etiqueta("Nueva publicación", 20, true, Estilo.VERDE_OSCURO);
    private final CampoTexto alimento = new CampoTexto("Mangos");
    private final JComboBox<CategoriaAlimento> categoria = new JComboBox<>(CategoriaAlimento.values());
    private final CampoTexto cantidad = new CampoTexto("3");
    private final JComboBox<UnidadMedida> unidad = new JComboBox<>(UnidadMedida.values());
    private final CampoTexto fechaLimite = new CampoTexto("18/10/2026");
    private final CampoTexto zona = new CampoTexto("San Ramón, Alajuela");
    private final CampoTexto preferencias = new CampoTexto("Aguacates o limones (opcional)");
    private final CampoTexto descripcion = new CampoTexto("Mangos de mi finca. Se intercambian los 3 kg juntos.");
    private final JLabel nota = Estilo.etiqueta(" ", 11, false, Estilo.TEXTO_SUAVE);

    private final Boton retirar = new Boton("Retirar lote", Boton.Tipo.PELIGRO);
    private final Boton eliminar = new Boton("Eliminar", Boton.Tipo.SUAVE);
    private final Boton cancelar = new Boton("Cancelar", Boton.Tipo.SECUNDARIO);
    private final Boton guardar = new Boton("Publicar lote", Boton.Tipo.PRIMARIO);

    public PantallaMisPublicaciones(VentanaPrincipal ventana) {
        super(ventana);
        Ui.agregar(contenido, Ui.encabezado("Lo que tenés para compartir", "Mi cosecha",
                "Administrá tus alimentos y mantené actualizada su disponibilidad."));

        JPanel cuerpo = new Ui.Bloque(new BorderLayout(24, 0));
        JPanel izquierda = Ui.columna();
        izquierda.setPreferredSize(null);
        Boton nueva = new Boton("+ Nueva publicación", Boton.Tipo.PRIMARIO);
        nueva.addActionListener(e -> seleccionar(null));
        JPanel filaNueva = new Ui.Bloque(new BorderLayout());
        filaNueva.add(nueva);
        Ui.agregar(izquierda, filaNueva);
        Ui.espacio(izquierda, 14);
        Ui.agregar(izquierda, lista);
        JPanel envolturaIzq = new JPanel(new BorderLayout());
        envolturaIzq.setOpaque(false);
        envolturaIzq.setPreferredSize(new Dimension(250, 10));
        envolturaIzq.add(izquierda, BorderLayout.NORTH);
        cuerpo.add(envolturaIzq, BorderLayout.WEST);

        JPanel envolturaDer = new JPanel(new BorderLayout());
        envolturaDer.setOpaque(false);
        envolturaDer.add(crearFormulario(), BorderLayout.NORTH);
        cuerpo.add(envolturaDer, BorderLayout.CENTER);
        Ui.agregar(contenido, cuerpo);
    }

    private JPanel crearFormulario() {
        PanelRedondeado t = Ui.tarjeta(new BorderLayout(), 24);
        JPanel col = Ui.columna();
        Ui.agregar(col, tituloFormulario);
        Ui.espacio(col, 16);
        Ui.estilizar(categoria);
        Ui.estilizar(unidad);
        Ui.agregar(col, Ui.dosColumnas(Ui.campo("Alimento", alimento), Ui.campo("Categoría", categoria)));
        Ui.espacio(col, 12);
        Ui.agregar(col, Ui.dosColumnas(Ui.campo("Cantidad", cantidad), Ui.campo("Unidad", unidad)));
        Ui.espacio(col, 12);
        Ui.agregar(col, Ui.dosColumnas(Ui.campo("Disponible hasta (dd/mm/aaaa)", fechaLimite),
                Ui.campo("Zona", zona)));
        Ui.espacio(col, 12);
        Ui.agregar(col, Ui.campo("Prefiero recibir", preferencias));
        Ui.espacio(col, 12);
        Ui.agregar(col, Ui.campo("Descripción", descripcion));
        Ui.espacio(col, 12);
        Ui.agregar(col, nota);
        Ui.espacio(col, 14);

        retirar.addActionListener(e -> retirar());
        eliminar.addActionListener(e -> eliminar());
        cancelar.addActionListener(e -> seleccionar(seleccionada));
        guardar.addActionListener(e -> guardar());
        for (Boton b : new Boton[]{retirar, eliminar, cancelar, guardar}) {
            b.setPreferredSize(new Dimension(b == guardar ? 150 : 112, 36));
        }
        Ui.agregar(col, Ui.filaPegada(10, retirar, eliminar, cancelar, guardar));
        t.add(col, BorderLayout.NORTH);
        return t;
    }

    @Override
    public void refrescar() {
        ejecutar(new Solicitud(Operacion.LISTAR_MIS_PUBLICACIONES), r -> {
            @SuppressWarnings("unchecked")
            List<Publicacion> datos = (List<Publicacion>) r.getDatos();
            publicaciones = datos;
            Publicacion anterior = seleccionada;
            seleccionada = null;
            if (anterior != null) {
                for (Publicacion p : publicaciones) {
                    if (p.getId() == anterior.getId()) {
                        seleccionada = p;
                    }
                }
            }
            seleccionar(seleccionada);
        });
    }

    private void pintarLista() {
        lista.removeAll();
        if (publicaciones.isEmpty()) {
            Ui.agregar(lista, Estilo.parrafo("Todavía no publicaste nada. Usá \"Nueva publicación\" para "
                    + "compartir tu primer lote.", 13, Estilo.TEXTO_SUAVE, 220));
        }
        for (Publicacion p : publicaciones) {
            boolean activa = seleccionada != null && seleccionada.getId() == p.getId();
            PanelRedondeado t = Ui.tarjeta(new BorderLayout(), 16, activa ? Estilo.VERDE_SUAVE : Estilo.BLANCO);
            JPanel col = Ui.columna();
            Ui.agregar(col, Estilo.etiqueta(p.getAlimento() + " · " + p.getUnidad().formatear(p.getCantidad()), 16,
                    true, Estilo.VERDE_OSCURO));
            Ui.espacio(col, 8);
            Ui.agregar(col, Ui.filaPegada(0, Chip.deEstado(p.getEstado().getEtiqueta())));
            Ui.espacio(col, 8);
            Ui.agregar(col, Estilo.etiqueta(subtitulo(p), 12, false, Estilo.TEXTO_SUAVE));
            t.add(col, BorderLayout.NORTH);
            t.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            t.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    seleccionar(p);
                }
            });
            Ui.agregar(lista, t);
            Ui.espacio(lista, 12);
        }
    }

    private static String subtitulo(Publicacion p) {
        if (p.getPropuestasPendientes() > 0) {
            int n = p.getPropuestasPendientes();
            return n + (n == 1 ? " propuesta pendiente" : " propuestas pendientes");
        }
        if (p.getEstado() == EstadoPublicacion.DISPONIBLE) {
            return "Hasta el " + Estilo.fecha(p.getFechaLimite());
        }
        if (p.getEstado() == EstadoPublicacion.RESERVADA) {
            return "Lote reservado";
        }
        return p.isTieneAsociados() ? "Con historial de propuestas" : "Sin propuestas registradas";
    }

    private void seleccionar(Publicacion p) {
        seleccionada = p;
        pintarLista();
        if (p == null) {
            tituloFormulario.setText("Nueva publicación");
            alimento.setText("");
            categoria.setSelectedIndex(0);
            cantidad.setText("");
            unidad.setSelectedIndex(0);
            fechaLimite.setText(Estilo.fechaCorta(LocalDate.now().plusDays(14)));
            zona.setText(ventana.getUsuario() == null ? "" : ventana.getUsuario().getZona());
            preferencias.setText("");
            descripcion.setText("");
            nota.setText("La zona se toma de tu perfil; podés cambiarla para este lote.");
            habilitarCampos(true);
            guardar.setText("Publicar lote");
            guardar.setEnabled(true);
            retirar.setVisible(false);
            eliminar.setVisible(false);
        } else {
            tituloFormulario.setText("Editar tu publicación");
            alimento.setText(p.getAlimento());
            categoria.setSelectedItem(p.getCategoria());
            cantidad.setText(p.getCantidad().stripTrailingZeros().toPlainString());
            unidad.setSelectedItem(p.getUnidad());
            fechaLimite.setText(Estilo.fechaCorta(p.getFechaLimite()));
            zona.setText(p.getZona());
            preferencias.setText(p.getPreferencias());
            descripcion.setText(p.getDescripcion());

            boolean disponible = p.estaDisponible(LocalDate.now());
            boolean puedeEditar = disponible && p.getPropuestasPendientes() == 0;
            boolean puedeRetirar = p.getEstado() == EstadoPublicacion.DISPONIBLE;
            boolean puedeEliminar = (p.getEstado() == EstadoPublicacion.DISPONIBLE
                    || p.getEstado() == EstadoPublicacion.RETIRADA) && !p.isTieneAsociados();
            habilitarCampos(puedeEditar);
            guardar.setText("Guardar cambios");
            guardar.setEnabled(puedeEditar);
            retirar.setVisible(true);
            retirar.setEnabled(puedeRetirar);
            eliminar.setVisible(true);
            eliminar.setEnabled(puedeEliminar);

            if (p.getPropuestasPendientes() > 0) {
                nota.setText("Este lote tiene propuestas pendientes: editar y eliminar están deshabilitados.");
            } else if (!disponible) {
                nota.setText("Este lote está " + p.getEstado().getEtiqueta().toLowerCase()
                        + ": solo se puede consultar" + (puedeEliminar ? " o eliminar." : "."));
            } else if (!puedeEliminar) {
                nota.setText("Tiene propuestas asociadas: podés retirarlo, pero no eliminarlo.");
            } else {
                nota.setText("Retirar conserva el registro; eliminar lo borra definitivamente.");
            }
        }
        actualizarVista(contenido);
    }

    private void habilitarCampos(boolean habilitar) {
        for (JComponent c : new JComponent[]{alimento, categoria, cantidad, unidad, fechaLimite, zona,
            preferencias, descripcion}) {
            c.setEnabled(habilitar);
        }
    }

    private void guardar() {
        BigDecimal valor;
        LocalDate limite;
        try {
            valor = PantallaDetalle.leerCantidad(cantidad.getText());
            limite = Estilo.leerFecha(fechaLimite.getText());
        } catch (CosechaException e) {
            mostrarError(e);
            return;
        }
        boolean nueva = seleccionada == null;
        Solicitud s = new Solicitud(nueva ? Operacion.CREAR_PUBLICACION : Operacion.EDITAR_PUBLICACION)
                .con("alimento", alimento.getText())
                .con("categoria", (CategoriaAlimento) categoria.getSelectedItem())
                .con("cantidad", valor)
                .con("unidad", (UnidadMedida) unidad.getSelectedItem())
                .con("fechaLimite", limite)
                .con("zona", zona.getText())
                .con("preferencias", preferencias.getText())
                .con("descripcion", descripcion.getText());
        if (!nueva) {
            s.con("idPublicacion", seleccionada.getId());
        }
        ejecutar(s, r -> {
            seleccionada = (Publicacion) r.getDatos();
            mostrarInfo(r.getMensaje());
            refrescar();
        });
    }

    private void retirar() {
        if (seleccionada == null || !confirmar("Retirar lote", "¿Retirar \"" + seleccionada.getAlimento()
                + "\"? Dejará de aparecer en Explorar y se rechazarán sus propuestas pendientes.")) {
            return;
        }
        ejecutar(new Solicitud(Operacion.RETIRAR_PUBLICACION).con("idPublicacion", seleccionada.getId()), r -> {
            mostrarInfo(r.getMensaje());
            refrescar();
        });
    }

    private void eliminar() {
        if (seleccionada == null || !confirmar("Eliminar publicación", "¿Eliminar definitivamente \""
                + seleccionada.getAlimento() + "\"? Esta acción no se puede deshacer.")) {
            return;
        }
        ejecutar(new Solicitud(Operacion.ELIMINAR_PUBLICACION).con("idPublicacion", seleccionada.getId()), r -> {
            mostrarInfo(r.getMensaje());
            seleccionada = null;
            refrescar();
        });
    }

    @Override
    public String getSeccionMenu() {
        return VentanaPrincipal.MIS_PUBLICACIONES;
    }
}
