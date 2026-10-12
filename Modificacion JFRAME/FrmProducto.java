package vista;

import panpuntos.model.Categoria;
import panpuntos.model.EstadoRegistro;
import panpuntos.model.Producto;
import panpuntos.service.CatalogoService;
import panpuntos.util.ExcepcionNegocio;
import panpuntos.util.Formato;
import panpuntos.view.Componentes;
import panpuntos.view.Dialogos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Formulario de Mantenimiento / CRUD visual de Productos y Sándwiches.
 * Persona 3 — Interfaz Gráfica (GUI con Swing).
 */
public class FrmProducto extends javax.swing.JFrame {

    private static final String[] COLUMNAS = {"ID", "Código", "Nombre / Sándwich", "Categoría", "Precio (Q)", "Existencia", "Estado"};

    private final CatalogoService catalogoService = new CatalogoService();
    private int idProductoSeleccionado = 0;

    // Componentes del Formulario
    private JTextField txtId;
    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JComboBox<String> cmbCategoria;
    private JTextField txtPrecio;
    private JTextField txtExistencia;
    private JComboBox<String> cmbEstado;

    // Componentes de Búsqueda y Tabla
    private JTextField txtBuscar;
    private JComboBox<String> cmbFiltroCategoria;
    private JButton btnBuscar;
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;

    // Botones de Acción CRUD
    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnModificar;
    private JButton btnCambiarEstado;
    private JButton btnLimpiar;
    private JButton btnVolver;

    public FrmProducto() {
        initComponents();
        configurarVentana();
        recargarTabla();
    }

    private void configurarVentana() {
        setTitle("🥪 Catálogo y Mantenimiento de Productos — Pan, Puntos y Premios");
        setSize(980, 620);
        setMinimumSize(new Dimension(900, 560));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        JPanel panelRaiz = new JPanel(new BorderLayout(10, 10));
        panelRaiz.setBackground(Componentes.FONDO_RESTAURANTE);
        panelRaiz.setBorder(new EmptyBorder(12, 14, 12, 14));

        // 1. Encabezado
        JPanel panelEncabezado = new JPanel(new BorderLayout());
        panelEncabezado.setOpaque(false);
        JLabel lblTitulo = Componentes.titulo("🥪 Catálogo de Sándwiches, Bebidas y Acompañamientos");
        JLabel lblSubtitulo = Componentes.etiqueta("  Control de precios, stock de ingredientes y activación en el menú");
        lblSubtitulo.setForeground(Componentes.TEXTO_MUTED);
        panelEncabezado.add(lblTitulo, BorderLayout.NORTH);
        panelEncabezado.add(lblSubtitulo, BorderLayout.SOUTH);
        panelRaiz.add(panelEncabezado, BorderLayout.NORTH);

        // 2. Panel Izquierdo: Formulario de Datos
        JPanel panelIzquierdo = new JPanel(new BorderLayout(0, 10));
        panelIzquierdo.setOpaque(false);
        panelIzquierdo.setPreferredSize(new Dimension(370, 480));

        JPanel formCampos = new JPanel(new GridLayout(7, 2, 8, 8));
        formCampos.setBackground(Componentes.BLANCO_CARD);
        formCampos.setBorder(Componentes.bordeSeccion("Datos del Producto"));

        txtId = new JTextField();
        txtId.setEditable(false);
        txtId.setBackground(new Color(0xEEEEEE));

        txtCodigo = new JTextField();
        txtNombre = new JTextField();

        cmbCategoria = new JComboBox<>();
        for (Categoria c : Categoria.values()) {
            cmbCategoria.addItem(c.name());
        }

        txtPrecio = new JTextField();
        txtExistencia = new JTextField();

        cmbEstado = new JComboBox<>();
        for (EstadoRegistro er : EstadoRegistro.values()) {
            cmbEstado.addItem(er.name());
        }

        formCampos.add(Componentes.etiqueta("ID Interno:"));
        formCampos.add(txtId);
        formCampos.add(Componentes.etiqueta("Código (ej. SAN-01):"));
        formCampos.add(txtCodigo);
        formCampos.add(Componentes.etiqueta("Nombre / Sándwich:"));
        formCampos.add(txtNombre);
        formCampos.add(Componentes.etiqueta("Categoría:"));
        formCampos.add(cmbCategoria);
        formCampos.add(Componentes.etiqueta("Precio Unitario (Q):"));
        formCampos.add(txtPrecio);
        formCampos.add(Componentes.etiqueta("Existencia / Stock:"));
        formCampos.add(txtExistencia);
        formCampos.add(Componentes.etiqueta("Estado:"));
        formCampos.add(cmbEstado);

        panelIzquierdo.add(formCampos, BorderLayout.NORTH);

        // Botones de acción CRUD
        JPanel panelBotonesCrud = new JPanel(new GridLayout(3, 2, 8, 8));
        panelBotonesCrud.setOpaque(false);
        panelBotonesCrud.setBorder(new EmptyBorder(8, 0, 8, 0));

        btnNuevo = Componentes.botonSecundario("✨ Nuevo");
        btnGuardar = Componentes.boton("💾 Guardar", Componentes.VERDE_FRESCO);
        btnModificar = Componentes.boton("✏️ Modificar", Componentes.PAN_DORADO);
        btnCambiarEstado = Componentes.boton("🔒 Activar/Desact.", Componentes.ROJO_TOMATE);
        btnLimpiar = Componentes.botonSecundario("🔄 Limpiar");
        btnVolver = Componentes.botonSecundario("🚪 Cerrar");

        btnNuevo.addActionListener(this::btnNuevoActionPerformed);
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);
        btnModificar.addActionListener(this::btnModificarActionPerformed);
        btnCambiarEstado.addActionListener(this::btnCambiarEstadoActionPerformed);
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnVolver.addActionListener(e -> dispose());

        panelBotonesCrud.add(btnNuevo);
        panelBotonesCrud.add(btnGuardar);
        panelBotonesCrud.add(btnModificar);
        panelBotonesCrud.add(btnCambiarEstado);
        panelBotonesCrud.add(btnLimpiar);
        panelBotonesCrud.add(btnVolver);

        panelIzquierdo.add(panelBotonesCrud, BorderLayout.CENTER);
        panelRaiz.add(panelIzquierdo, BorderLayout.WEST);

        // 3. Panel Derecho: Buscador y Tabla de Productos
        JPanel panelDerecho = new JPanel(new BorderLayout(0, 8));
        panelDerecho.setOpaque(false);

        JPanel panelBuscar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelBuscar.setOpaque(false);
        panelBuscar.setBorder(new EmptyBorder(0, 0, 6, 0));

        txtBuscar = new JTextField(12);
        txtBuscar.setToolTipText("Buscar por Código o Nombre...");
        txtBuscar.addActionListener(e -> recargarTabla());

        cmbFiltroCategoria = new JComboBox<>();
        cmbFiltroCategoria.addItem("TODAS");
        for (Categoria c : Categoria.values()) {
            cmbFiltroCategoria.addItem(c.name());
        }
        cmbFiltroCategoria.addActionListener(e -> recargarTabla());

        btnBuscar = Componentes.boton("🔍 Filtrar", Componentes.PAN_DORADO);
        btnBuscar.addActionListener(e -> recargarTabla());

        panelBuscar.add(Componentes.etiqueta("Buscar:"));
        panelBuscar.add(txtBuscar);
        panelBuscar.add(Componentes.etiqueta("Categoría:"));
        panelBuscar.add(cmbFiltroCategoria);
        panelBuscar.add(btnBuscar);

        panelDerecho.add(panelBuscar, BorderLayout.NORTH);

        // Configuración de la JTable
        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaProductos = Componentes.tabla(COLUMNAS);
        tablaProductos.setModel(modeloTabla);
        Componentes.configurarTabla(tablaProductos);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tablaProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarFila();
            }
        });

        JScrollPane scroll = Componentes.panelDesplazable(tablaProductos);
        scroll.setBorder(Componentes.bordeSeccion("Listado de Productos en Catálogo"));
        panelDerecho.add(scroll, BorderLayout.CENTER);

        panelRaiz.add(panelDerecho, BorderLayout.CENTER);
        getContentPane().add(panelRaiz);
    }

    private void recargarTabla() {
        modeloTabla.setRowCount(0);
        String filtro = txtBuscar.getText().trim();
        String cat = (String) cmbFiltroCategoria.getSelectedItem();
        String catFiltro = "TODAS".equals(cat) ? null : cat;

        try {
            List<Producto> productos = catalogoService.listarProductos(catFiltro, filtro);
            for (Producto p : productos) {
                modeloTabla.addRow(new Object[]{
                        p.getIdProducto(),
                        p.getCodigo(),
                        p.getNombre(),
                        p.getCategoria().getEtiqueta(),
                        Formato.moneda(p.getPrecioUnitario()),
                        p.getExistencia(),
                        p.getEstado().getEtiqueta()
                });
            }
        } catch (Exception e) {
            Dialogos.error(this, e);
        }
    }

    private void seleccionarFila() {
        int fila = tablaProductos.getSelectedRow();
        if (fila >= 0) {
            idProductoSeleccionado = ((Number) modeloTabla.getValueAt(fila, 0)).intValue();
            txtId.setText(String.valueOf(idProductoSeleccionado));
            txtCodigo.setText(String.valueOf(modeloTabla.getValueAt(fila, 1)));
            txtNombre.setText(String.valueOf(modeloTabla.getValueAt(fila, 2)));

            String catTexto = String.valueOf(modeloTabla.getValueAt(fila, 3));
            for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
                if (cmbCategoria.getItemAt(i).equalsIgnoreCase(catTexto) ||
                        catTexto.toUpperCase().contains(cmbCategoria.getItemAt(i).toUpperCase())) {
                    cmbCategoria.setSelectedIndex(i);
                    break;
                }
            }

            String precioStr = String.valueOf(modeloTabla.getValueAt(fila, 4)).replace("Q", "").trim();
            txtPrecio.setText(precioStr);
            txtExistencia.setText(String.valueOf(modeloTabla.getValueAt(fila, 5)));

            String estadoStr = String.valueOf(modeloTabla.getValueAt(fila, 6));
            for (int i = 0; i < cmbEstado.getItemCount(); i++) {
                if (cmbEstado.getItemAt(i).equalsIgnoreCase(estadoStr) ||
                        estadoStr.toUpperCase().contains(cmbEstado.getItemAt(i).toUpperCase())) {
                    cmbEstado.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void btnNuevoActionPerformed(ActionEvent evt) {
        limpiarFormulario();
        txtCodigo.requestFocus();
    }

    private void btnGuardarActionPerformed(ActionEvent evt) {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String categoriaStr = (String) cmbCategoria.getSelectedItem();
        String precioStr = txtPrecio.getText().trim();
        String stockStr = txtExistencia.getText().trim();

        if (codigo.isEmpty() || nombre.isEmpty() || precioStr.isEmpty()) {
            Dialogos.aviso(this, "El Código, Nombre y Precio son obligatorios.");
            return;
        }

        double precio;
        int existencia = 0;
        try {
            precio = Double.parseDouble(precioStr);
            if (!stockStr.isEmpty()) {
                existencia = Integer.parseInt(stockStr);
            }
        } catch (NumberFormatException e) {
            Dialogos.aviso(this, "El precio y la existencia deben ser valores numéricos válidos.");
            return;
        }

        try {
            Categoria categoria = Categoria.desdeTexto(categoriaStr);
            Producto nuevo = catalogoService.registrarProducto(codigo, nombre, categoria, precio, existencia);
            Dialogos.exito(this, "Producto '" + nuevo.getNombre() + "' registrado exitosamente.");
            limpiarFormulario();
            recargarTabla();
        } catch (Exception e) {
            Dialogos.error(this, e);
        }
    }

    private void btnModificarActionPerformed(ActionEvent evt) {
        if (idProductoSeleccionado <= 0) {
            Dialogos.aviso(this, "Por favor seleccione un producto de la tabla para modificar.");
            return;
        }

        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String categoriaStr = (String) cmbCategoria.getSelectedItem();
        String precioStr = txtPrecio.getText().trim();
        String stockStr = txtExistencia.getText().trim();
        String estadoStr = (String) cmbEstado.getSelectedItem();

        double precio;
        int existencia;
        try {
            precio = Double.parseDouble(precioStr);
            existencia = Integer.parseInt(stockStr);
        } catch (NumberFormatException e) {
            Dialogos.aviso(this, "El precio y la existencia deben ser valores numéricos válidos.");
            return;
        }

        try {
            Categoria categoria = Categoria.desdeTexto(categoriaStr);
            EstadoRegistro estado = parseEstado(estadoStr);
            Producto modificado = new Producto(idProductoSeleccionado, codigo, nombre, categoria, precio, existencia, estado);
            catalogoService.actualizarProducto(modificado);
            Dialogos.exito(this, "Producto actualizado correctamente.");
            recargarTabla();
        } catch (Exception e) {
            Dialogos.error(this, e);
        }
    }

    private void btnCambiarEstadoActionPerformed(ActionEvent evt) {
        if (idProductoSeleccionado <= 0) {
            Dialogos.aviso(this, "Seleccione un producto de la tabla para cambiar su estado.");
            return;
        }

        EstadoRegistro actual = parseEstado((String) cmbEstado.getSelectedItem());
        EstadoRegistro nuevo = actual == EstadoRegistro.ACTIVO ? EstadoRegistro.INACTIVO : EstadoRegistro.ACTIVO;

        if (Dialogos.confirmar(this, "¿Desea cambiar el estado del producto a " + nuevo.getEtiqueta() + "?")) {
            try {
                catalogoService.cambiarEstadoProducto(idProductoSeleccionado, nuevo);
                Dialogos.exito(this, "Estado del producto cambiado a " + nuevo.getEtiqueta() + ".");
                recargarTabla();
            } catch (Exception e) {
                Dialogos.error(this, e);
            }
        }
    }

    private EstadoRegistro parseEstado(String str) {
        if (str == null) return EstadoRegistro.ACTIVO;
        return str.trim().toUpperCase().contains("ACTIVO") ? EstadoRegistro.ACTIVO : EstadoRegistro.INACTIVO;
    }

    private void limpiarFormulario() {
        idProductoSeleccionado = 0;
        txtId.setText("");
        txtCodigo.setText("");
        txtNombre.setText("");
        cmbCategoria.setSelectedIndex(0);
        txtPrecio.setText("");
        txtExistencia.setText("");
        cmbEstado.setSelectedIndex(0);
        tablaProductos.clearSelection();
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new FrmProducto().setVisible(true));
    }
}
