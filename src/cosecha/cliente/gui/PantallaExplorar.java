package cosecha.cliente.gui;

import cosecha.cliente.gui.componentes.Boton;
import cosecha.cliente.gui.componentes.CampoTexto;
import cosecha.cliente.gui.componentes.Chip;
import cosecha.cliente.gui.componentes.ImagenAlimento;
import cosecha.cliente.gui.componentes.PanelRedondeado;
import cosecha.comun.Operacion;
import cosecha.comun.Solicitud;
import cosecha.modelo.CategoriaAlimento;
import cosecha.modelo.Publicacion;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PantallaExplorar extends PantallaBase {

    private final CampoTexto filtroAlimento = new CampoTexto("Mangos, limones, hierbas...");
    private final CampoTexto filtroZona = new CampoTexto("San Ramón, Alajuela");
    private final Map<Boton, CategoriaAlimento> chipsCategoria = new LinkedHashMap<>();
    private CategoriaAlimento categoria;
    private final JLabel conteo = Estilo.etiqueta("", 13, false, Estilo.TEXTO_SUAVE);
    private final JPanel resultados = Ui.columna();

    public PantallaExplorar(VentanaPrincipal ventana) {
        super(ventana);
        Ui.agregar(contenido, Ui.encabezado("Tu comunidad, tu cosecha", "Algo rico crece cerca de vos.",
                "Descubrí alimentos disponibles y encontrá tu próximo intercambio."));

        // Filtros
        JPanel filtros = new Ui.Bloque(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.SOUTH;
        c.insets = new Insets(0, 0, 0, 12);
        c.weightx = 0.55;
        filtros.add(Ui.campo("¿Qué alimento buscás?", filtroAlimento), c);
        c.weightx = 0.45;
        filtros.add(Ui.campo("Zona", filtroZona), c);
        c.weightx = 0;
        Boton buscar = new Boton("Buscar", Boton.Tipo.PRIMARIO);
        buscar.setPreferredSize(new Dimension(112, 36));
        buscar.addActionListener(e -> refrescar());
        filtros.add(buscar, c);
        c.insets = new Insets(0, 0, 0, 0);
        Boton limpiar = new Boton("Limpiar", Boton.Tipo.SECUNDARIO);
        limpiar.setPreferredSize(new Dimension(112, 36));
        limpiar.addActionListener(e -> limpiarFiltros());
        filtros.add(limpiar, c);
        filtroAlimento.addActionListener(e -> refrescar());
        filtroZona.addActionListener(e -> refrescar());
        Ui.agregar(contenido, filtros);
        Ui.espacio(contenido, 14);

        List<Boton> botones = new ArrayList<>();
        chipsCategoria.put(new Boton("Todos", Boton.Tipo.PRIMARIO), null);
        for (CategoriaAlimento cat : CategoriaAlimento.values()) {
            chipsCategoria.put(new Boton(cat.getPlural(), Boton.Tipo.SECUNDARIO), cat);
        }
        for (Map.Entry<Boton, CategoriaAlimento> e : chipsCategoria.entrySet()) {
            Boton b = e.getKey();
            b.setPreferredSize(new Dimension(100, 34));
            b.addActionListener(ev -> {
                categoria = e.getValue();
                pintarCategorias();
                refrescar();
            });
            botones.add(b);
        }
        Ui.agregar(contenido, Ui.filaPegada(10, botones.toArray(new Boton[0])));
        Ui.espacio(contenido, 24);

        JLabel recientes = Estilo.etiqueta("Recién compartidos", 19, true, Estilo.VERDE_OSCURO);
        Ui.agregar(contenido, Ui.filaPegada(16, recientes, conteo));
        Ui.espacio(contenido, 14);
        Ui.agregar(contenido, resultados);
        Ui.espacio(contenido, 18);
        Ui.agregar(contenido, Ui.cajaInfo("Cada lote se intercambia completo",
                "Encontrá una publicación que te interese y ofrecé algo de tu propia cosecha."));
    }

    private void pintarCategorias() {
        for (Map.Entry<Boton, CategoriaAlimento> e : chipsCategoria.entrySet()) {
            e.getKey().setTipo(e.getValue() == categoria ? Boton.Tipo.PRIMARIO : Boton.Tipo.SECUNDARIO);
        }
    }

    private void limpiarFiltros() {
        filtroAlimento.setText("");
        filtroZona.setText("");
        categoria = null;
        pintarCategorias();
        refrescar();
    }

    @Override
    public void refrescar() {
        Solicitud s = new Solicitud(Operacion.BUSCAR_PUBLICACIONES)
                .con("alimento", filtroAlimento.getText())
                .con("zona", filtroZona.getText())
                .con("categoria", categoria);
        ejecutar(s, r -> {
            @SuppressWarnings("unchecked")
            List<Publicacion> lista = (List<Publicacion>) r.getDatos();
            mostrarResultados(lista);
        });
    }

    private void mostrarResultados(List<Publicacion> lista) {
        resultados.removeAll();
        boolean hayFiltros = !filtroAlimento.getText().trim().isEmpty() || !filtroZona.getText().trim().isEmpty()
                || categoria != null;
        conteo.setText(lista.size() + (lista.size() == 1 ? " lote disponible" : " lotes disponibles")
                + (hayFiltros ? " con estos filtros" : ""));
        if (lista.isEmpty()) {
            PanelRedondeado vacio = Ui.tarjeta(new BorderLayout(0, 8), 28);
            vacio.add(Estilo.etiqueta("No encontramos publicaciones", 17, true, Estilo.VERDE_OSCURO),
                    BorderLayout.NORTH);
            vacio.add(Estilo.etiqueta(hayFiltros
                    ? "Probá con otro alimento, otra zona o limpiá los filtros."
                    : "Todavía no hay lotes disponibles. ¡Publicá el tuyo desde Mis publicaciones!", 13, false,
                    Estilo.TEXTO_SUAVE), BorderLayout.CENTER);
            Ui.agregar(resultados, vacio);
        } else {
            JPanel grilla = new Ui.Bloque(new GridLayout(0, 3, 20, 20));
            for (Publicacion p : lista) {
                grilla.add(crearTarjeta(p));
            }
            Ui.agregar(resultados, grilla);
        }
        actualizarVista(contenido);
    }

    private JPanel crearTarjeta(Publicacion p) {
        PanelRedondeado t = Ui.tarjeta(new BorderLayout(), 14);
        t.setBorde(Estilo.BORDE);
        JPanel col = Ui.columna();
        ImagenAlimento img = new ImagenAlimento(p.getAlimento(), p.getCategoria(), 260, 160);
        JPanel marco = new Ui.Bloque(new BorderLayout());
        marco.add(img);
        Ui.agregar(col, marco);
        Ui.espacio(col, 14);
        JLabel nombre = Estilo.etiqueta(p.getAlimento(), 19, true, Estilo.VERDE_OSCURO);
        Chip cantidad = new Chip(p.getUnidad().formatear(p.getCantidad()), Estilo.VERDE_SUAVE, Estilo.VERDE_OSCURO);
        Ui.agregar(col, Ui.filaPegada(10, nombre, cantidad));
        Ui.espacio(col, 10);
        Ui.agregar(col, Estilo.etiqueta(p.getZona(), 13, false, Estilo.TEXTO_SUAVE));
        Ui.espacio(col, 6);
        String busca = p.getPreferencias().isEmpty() ? "Abierto a propuestas" : "Busca: " + p.getPreferencias();
        Ui.agregar(col, Estilo.etiqueta(busca, 12, false, Estilo.TEXTO));
        Ui.espacio(col, 4);
        Ui.agregar(col, Estilo.etiqueta("Por " + p.getCultivador().getPrimerNombre() + " · hasta el "
                + Estilo.fecha(p.getFechaLimite()), 11, false, Estilo.TEXTO_SUAVE));
        Ui.espacio(col, 14);
        Boton ver = new Boton("Ver publicación  →", Boton.Tipo.PRIMARIO);
        ver.addActionListener(e -> ventana.verPublicacion(p.getId()));
        JPanel fila = new Ui.Bloque(new BorderLayout());
        fila.add(ver);
        Ui.agregar(col, fila);
        t.add(col, BorderLayout.NORTH);
        return t;
    }

    @Override
    public String getSeccionMenu() {
        return VentanaPrincipal.EXPLORAR;
    }
}
