package vista;

import panpuntos.model.Cliente;
import panpuntos.model.ItemVenta;
import panpuntos.model.Menu;
import panpuntos.model.MenuCompleto;
import panpuntos.model.Pedido;
import panpuntos.model.Producto;
import panpuntos.model.ProductoIndividual;
import panpuntos.model.TipoPago;
import panpuntos.service.CatalogoService;
import panpuntos.service.ClienteService;
import panpuntos.service.ClienteSnapshot;
import panpuntos.service.PagoService;
import panpuntos.service.PedidoService;
import panpuntos.service.ResultadoCobro;
import panpuntos.util.Comprobante;
import panpuntos.util.ExcepcionNegocio;
import panpuntos.util.Formato;
import panpuntos.view.Componentes;
import panpuntos.view.Dialogos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * La Ventana Maestra (FrmPedido) — Terminal de Punto de Venta (POS).
 * Incluye:
 * 1. Buscador de clientes por ID, DPI o Nombre.
 * 2. Muestra automática de los puntos actuales del cliente.
 * 3. JTable interactivo para agregar/eliminar productos del carrito.
 * 4. Etiquetas de actualización en tiempo real para Subtotal, Total y Puntos a generar.
 * 
 * Persona 3 — Interfaz Gráfica (GUI con Swing).
 */
public class FrmPedido extends javax.swing.JFrame {

    private static final String[] COLUMNAS_CARRITO = {
            "#", "Código", "Producto / Ítem", "Tipo", "Cant.", "Precio Unit.", "Subtotal", "Puntos ⭐"
    };

    private final ClienteService clienteService = new ClienteService();
    private final CatalogoService catalogoService = new CatalogoService();
    private final PedidoService pedidoService = new PedidoService();
    private final PagoService pagoService = new PagoService();

    // Estado actual de la venta
    private Cliente clienteActual;
    private final List<ItemVenta> carrito = new ArrayList<>();
    private Pedido pedidoConfirmado;

    // 1. Buscador y Datos del Cliente
    private JTextField txtBuscarCliente;
    private JButton btnBuscarCliente;
    private JButton btnListaClientes;
    private JLabel lblNombreCliente;
    private JLabel lblDpiCliente;
    private JLabel lblPuntosActuales;
    private JLabel lblEstadoCliente;

    // 2. Selector de Productos
    private JComboBox<ElementoCatalogo> cmbProductos;
    private JSpinner spnCantidad;
    private JButton btnAgregar;

    // 3. JTable Interactivo del Carrito
    private JTable tablaCarrito;
    private DefaultTableModel modeloCarrito;
    private JButton btnEliminarItem;
    private JButton btnLimpiarCarrito;

    // 4. Etiquetas de Actualización en Tiempo Real
    private JLabel lblSubtotal;
    private JLabel lblTotal;
    private JLabel lblPuntosGenerar;

    // 5. Método de Pago y Cobro
    private JRadioButton rbEfectivo;
    private JRadioButton rbTarjeta;
    private JTextField txtEfectivo;
    private JLabel lblCambio;
    private JTextField txtReferenciaTarjeta;
    private JButton btnConfirmarPedido;
    private JButton btnCobrar;
    private JButton btnComprobante;
    private JButton btnVolver;

    public FrmPedido() {
        initComponents();
        configurarVentana();
        cargarCatalogo();
        actualizarTotalesEnTiempoReal();
    }

    private void configurarVentana() {
        setTitle("🛒 Terminal de Punto de Venta (POS) — Pan, Puntos y Premios");
        setSize(1100, 720);
        setMinimumSize(new Dimension(1020, 660));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        JPanel panelRaiz = new JPanel(new BorderLayout(10, 10));
        panelRaiz.setBackground(Componentes.FONDO_RESTAURANTE);
        panelRaiz.setBorder(new EmptyBorder(12, 14, 12, 14));

        // ==========================================
        // 1. ENCABEZADO SUPERIOR
        // ==========================================
        JPanel panelEncabezado = new JPanel(new BorderLayout());
        panelEncabezado.setOpaque(false);
        JLabel lblTitulo = Componentes.titulo("🛒 Punto de Venta POS & Emisión de Pedidos");
        JLabel lblSubtitulo = Componentes.etiqueta("  Registro de ventas, acumulación automática de puntos y facturación");
        lblSubtitulo.setForeground(Componentes.TEXTO_MUTED);
        panelEncabezado.add(lblTitulo, BorderLayout.NORTH);
        panelEncabezado.add(lblSubtitulo, BorderLayout.SOUTH);
        panelRaiz.add(panelEncabezado, BorderLayout.NORTH);

        // ==========================================
        // 2. PANEL SUPERIOR: CLIENTE + SELECCIÓN DE PRODUCTOS
        // ==========================================
        JPanel panelSuperior = new JPanel(new GridLayout(1, 2, 12, 0));
        panelSuperior.setOpaque(false);

        // A. Tarjeta de Cliente
        JPanel panelCliente = new JPanel(new BorderLayout(8, 8));
        panelCliente.setBackground(Componentes.BLANCO_CARD);
        panelCliente.setBorder(Componentes.bordeSeccion("1. Buscador de Clientes & Puntos"));

        JPanel busquedaCliente = new JPanel(new BorderLayout(6, 0));
        busquedaCliente.setOpaque(false);
        txtBuscarCliente = new JTextField();
        txtBuscarCliente.setToolTipText("Ingrese ID, DPI o Nombre del cliente...");
        txtBuscarCliente.addActionListener(e -> buscarCliente());

        btnBuscarCliente = Componentes.boton("🔍 Buscar", Componentes.PAN_DORADO);
        btnBuscarCliente.addActionListener(e -> buscarCliente());

        btnListaClientes = Componentes.botonSecundario("📋 Lista");
        btnListaClientes.addActionListener(e -> mostrarSelectorClientes());

        JPanel botonesBusqueda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        botonesBusqueda.setOpaque(false);
        botonesBusqueda.add(btnBuscarCliente);
        botonesBusqueda.add(btnListaClientes);

        busquedaCliente.add(Componentes.etiqueta("Cliente (ID/Nombre/DPI):"), BorderLayout.WEST);
        busquedaCliente.add(txtBuscarCliente, BorderLayout.CENTER);
        busquedaCliente.add(botonesBusqueda, BorderLayout.EAST);
        panelCliente.add(busquedaCliente, BorderLayout.NORTH);

        // Información del cliente seleccionado
        JPanel infoCliente = new JPanel(new GridLayout(2, 2, 10, 6));
        infoCliente.setOpaque(false);
        infoCliente.setBorder(new EmptyBorder(6, 4, 6, 4));

        lblNombreCliente = Componentes.valor("Ningún cliente seleccionado");
        lblNombreCliente.setForeground(Componentes.PAN_TOSTADO);

        lblDpiCliente = Componentes.etiqueta("DPI: -");

        // BADGE AUTOMÁTICO DE PUNTOS ACTUALES
        lblPuntosActuales = Componentes.badgePuntos("0 pts actuales");

        lblEstadoCliente = Componentes.etiqueta("Estado: Pendiente");

        infoCliente.add(lblNombreCliente);
        infoCliente.add(lblPuntosActuales);
        infoCliente.add(lblDpiCliente);
        infoCliente.add(lblEstadoCliente);
        panelCliente.add(infoCliente, BorderLayout.CENTER);

        panelSuperior.add(panelCliente);

        // B. Tarjeta de Selección de Productos y Combos
        JPanel panelAgregarProd = new JPanel(new BorderLayout(8, 8));
        panelAgregarProd.setBackground(Componentes.BLANCO_CARD);
        panelAgregarProd.setBorder(Componentes.bordeSeccion("2. Selección de Productos del Menú"));

        cmbProductos = new JComboBox<>();
        spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        spnCantidad.setPreferredSize(new Dimension(65, 28));

        btnAgregar = Componentes.boton("➕ Agregar al Carrito", Componentes.VERDE_FRESCO);
        btnAgregar.addActionListener(e -> agregarProductoAlCarrito());

        JPanel filaSeleccion = new JPanel(new BorderLayout(8, 0));
        filaSeleccion.setOpaque(false);
        filaSeleccion.add(Componentes.etiqueta("Producto / Sándwich:"), BorderLayout.WEST);
        filaSeleccion.add(cmbProductos, BorderLayout.CENTER);
        panelAgregarProd.add(filaSeleccion, BorderLayout.NORTH);

        JPanel filaCantidadBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        filaCantidadBoton.setOpaque(false);
        filaCantidadBoton.add(Componentes.etiqueta("Cantidad:"));
        filaCantidadBoton.add(spnCantidad);
        filaCantidadBoton.add(btnAgregar);
        panelAgregarProd.add(filaCantidadBoton, BorderLayout.SOUTH);

        panelSuperior.add(panelAgregarProd);

        // ==========================================
        // 3. PANEL CENTRAL: JTABLE DEL CARRITO DE COMPRAS
        // ==========================================
        JPanel panelCentral = new JPanel(new BorderLayout(0, 8));
        panelCentral.setOpaque(false);

        modeloCarrito = new DefaultTableModel(COLUMNAS_CARRITO, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tablaCarrito = Componentes.tabla(COLUMNAS_CARRITO);
        tablaCarrito.setModel(modeloCarrito);
        Componentes.configurarTabla(tablaCarrito);
        tablaCarrito.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollCarrito = Componentes.panelDesplazable(tablaCarrito);
        scrollCarrito.setBorder(Componentes.bordeSeccion("3. Carrito de Compras (Productos en la Orden)"));
        panelCentral.add(scrollCarrito, BorderLayout.CENTER);

        // Botones de acción del carrito
        JPanel barraCarrito = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        barraCarrito.setOpaque(false);

        btnEliminarItem = Componentes.botonPeligro("🗑️ Quitar Seleccionado");
        btnEliminarItem.addActionListener(e -> eliminarItemDelCarrito());

        btnLimpiarCarrito = Componentes.botonSecundario("🔄 Vaciar Carrito");
        btnLimpiarCarrito.addActionListener(e -> vaciarCarrito());

        barraCarrito.add(btnEliminarItem);
        barraCarrito.add(btnLimpiarCarrito);
        panelCentral.add(barraCarrito, BorderLayout.SOUTH);

        // Unimos Superior + Central
        JPanel panelContenido = new JPanel(new BorderLayout(0, 10));
        panelContenido.setOpaque(false);
        panelContenido.add(panelSuperior, BorderLayout.NORTH);
        panelContenido.add(panelCentral, BorderLayout.CENTER);
        panelRaiz.add(panelContenido, BorderLayout.CENTER);

        // ==========================================
        // 4. PANEL INFERIOR: TOTALES EN TIEMPO REAL & COBRO
        // ==========================================
        JPanel panelInferior = new JPanel(new BorderLayout(14, 0));
        panelInferior.setBackground(Componentes.BLANCO_CARD);
        panelInferior.setBorder(Componentes.bordeSeccion("4. Resumen en Tiempo Real & Finalización de Venta"));

        // A. Resumen en Tiempo Real (Izquierda / Centro)
        JPanel panelTotales = new JPanel(new GridLayout(3, 2, 10, 6));
        panelTotales.setOpaque(false);
        panelTotales.setBorder(new EmptyBorder(8, 14, 8, 14));

        lblSubtotal = Componentes.valor("Q0.00");
        lblTotal = Componentes.totalDestacado("Q0.00");
        lblPuntosGenerar = Componentes.badgePuntos("+0 pts a generar");

        panelTotales.add(Componentes.etiqueta("Subtotal de la orden:"));
        panelTotales.add(lblSubtotal);
        panelTotales.add(Componentes.etiqueta("⭐ Puntos a generar:"));
        panelTotales.add(lblPuntosGenerar);
        panelTotales.add(Componentes.etiqueta("TOTAL A PAGAR:"));
        panelTotales.add(lblTotal);
        panelInferior.add(panelTotales, BorderLayout.WEST);

        // B. Datos de Pago (Centro)
        JPanel panelPago = new JPanel(new GridLayout(3, 2, 8, 6));
        panelPago.setOpaque(false);
        panelPago.setBorder(new EmptyBorder(8, 10, 8, 10));

        rbEfectivo = new JRadioButton("💵 Efectivo", true);
        rbTarjeta = new JRadioButton("💳 Tarjeta");
        ButtonGroup bgPago = new ButtonGroup();
        bgPago.add(rbEfectivo);
        bgPago.add(rbTarjeta);

        JPanel panelRadios = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        panelRadios.setOpaque(false);
        panelRadios.add(rbEfectivo);
        panelRadios.add(rbTarjeta);

        txtEfectivo = new JTextField("0.00", 8);
        txtEfectivo.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { calcularCambio(); }
            public void removeUpdate(DocumentEvent e) { calcularCambio(); }
            public void changedUpdate(DocumentEvent e) { calcularCambio(); }
        });

        lblCambio = Componentes.valor("Q0.00");
        txtReferenciaTarjeta = new JTextField("", 8);
        txtReferenciaTarjeta.setEnabled(false);

        rbTarjeta.addActionListener(e -> {
            txtEfectivo.setEnabled(false);
            txtReferenciaTarjeta.setEnabled(true);
            lblCambio.setText("Q0.00");
        });
        rbEfectivo.addActionListener(e -> {
            txtEfectivo.setEnabled(true);
            txtReferenciaTarjeta.setEnabled(false);
            calcularCambio();
        });

        panelPago.add(Componentes.etiqueta("Método de Pago:"));
        panelPago.add(panelRadios);
        panelPago.add(Componentes.etiqueta("Efectivo recibido:"));
        panelPago.add(txtEfectivo);
        panelPago.add(Componentes.etiqueta("Cambio / Vuelto:"));
        panelPago.add(lblCambio);
        panelInferior.add(panelPago, BorderLayout.CENTER);

        // C. Botones de Acción (Derecha)
        JPanel panelBotonesAccion = new JPanel(new GridLayout(4, 1, 6, 6));
        panelBotonesAccion.setOpaque(false);
        panelBotonesAccion.setBorder(new EmptyBorder(8, 8, 8, 14));

        btnConfirmarPedido = Componentes.boton("🛒 Confirmar Pedido", Componentes.PAN_DORADO);
        btnConfirmarPedido.addActionListener(e -> confirmarPedido());

        btnCobrar = Componentes.boton("💰 Cobrar y Facturar", Componentes.VERDE_FRESCO);
        btnCobrar.addActionListener(e -> cobrarPedido());

        btnComprobante = Componentes.boton("🧾 Ver Comprobante", Componentes.PAN_TOSTADO);
        btnComprobante.setEnabled(false);
        btnComprobante.addActionListener(e -> verComprobante());

        btnVolver = Componentes.botonSecundario("🚪 Cerrar");
        btnVolver.addActionListener(e -> dispose());

        panelBotonesAccion.add(btnConfirmarPedido);
        panelBotonesAccion.add(btnCobrar);
        panelBotonesAccion.add(btnComprobante);
        panelBotonesAccion.add(btnVolver);
        panelInferior.add(panelBotonesAccion, BorderLayout.EAST);

        panelRaiz.add(panelInferior, BorderLayout.SOUTH);
        getContentPane().add(panelRaiz);
    }

    // ==========================================
    // LÓGICA DE NEGOCIO Y ACTUALIZACIONES EN TIEMPO REAL
    // ==========================================

    private void cargarCatalogo() {
        cmbProductos.removeAllItems();
        try {
            // Cargar productos individuales
            List<Producto> productos = catalogoService.listarProductos(null, null);
            for (Producto p : productos) {
                if (p.getEstado().estaActivo()) {
                    cmbProductos.addItem(new ElementoCatalogo(
                            p.getIdProducto(),
                            p.getCodigo(),
                            p.getNombre(),
                            "PRODUCTO",
                            p.getPrecioUnitario(),
                            p.getCategoria().getPuntosPorUnidad(),
                            p.getExistencia(),
                            p
                    ));
                }
            }

            // Cargar menús / combos completos
            List<Menu> menus = catalogoService.listarMenus(null);
            for (Menu m : menus) {
                if (m.getEstado().estaActivo()) {
                    cmbProductos.addItem(new ElementoCatalogo(
                            m.getIdMenu(),
                            m.getCodigo(),
                            "COMBO: " + m.getNombre(),
                            "MENU",
                            m.getPrecioMenu(),
                            Menu.PUNTOS_POR_MENU,
                            99,
                            m
                    ));
                }
            }
        } catch (Exception e) {
            Dialogos.error(this, e);
        }
    }

    private void buscarCliente() {
        String texto = txtBuscarCliente.getText().trim();
        if (texto.isEmpty()) {
            Dialogos.aviso(this, "Ingrese el ID, DPI o Nombre del cliente para buscar.");
            return;
        }

        try {
            Cliente encontrado = null;

            // Intentar por ID si es número
            if (texto.matches("\\d+")) {
                try {
                    int id = Integer.parseInt(texto);
                    encontrado = clienteService.buscarPorId(id);
                } catch (Exception ignored) {
                }
                if (encontrado == null) {
                    encontrado = clienteService.buscarPorDpi(texto);
                }
            }

            // Si no encontró por ID/DPI, buscar por nombre o texto
            if (encontrado == null) {
                List<Cliente> lista = clienteService.listar(texto);
                if (lista.size() == 1) {
                    encontrado = lista.get(0);
                } else if (lista.size() > 1) {
                    encontrado = seleccionarDeLista(lista);
                }
            }

            if (encontrado != null) {
                seleccionarCliente(encontrado);
            } else {
                Dialogos.aviso(this, "No se encontró ningún cliente con: " + texto);
            }
        } catch (Exception e) {
            Dialogos.error(this, e);
        }
    }

    private void mostrarSelectorClientes() {
        try {
            List<Cliente> lista = clienteService.listar(null);
            Cliente sel = seleccionarDeLista(lista);
            if (sel != null) {
                seleccionarCliente(sel);
            }
        } catch (Exception e) {
            Dialogos.error(this, e);
        }
    }

    private Cliente seleccionarDeLista(List<Cliente> lista) {
        if (lista.isEmpty()) {
            Dialogos.aviso(this, "No hay clientes registrados en el sistema.");
            return null;
        }
        String[] opciones = new String[lista.size()];
        for (int i = 0; i < lista.size(); i++) {
            Cliente c = lista.get(i);
            opciones[i] = c.getIdCliente() + " | " + c.getDpi() + " | " + c.getNombre() + " (" + c.getSaldoPuntos() + " pts)";
        }
        String seleccion = (String) JOptionPane.showInputDialog(
                this,
                "Seleccione el cliente correspondiente:",
                "Búsqueda de Clientes",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );
        if (seleccion != null) {
            int indice = java.util.Arrays.asList(opciones).indexOf(seleccion);
            if (indice >= 0) {
                return lista.get(indice);
            }
        }
        return null;
    }

    private void seleccionarCliente(Cliente cliente) {
        this.clienteActual = cliente;
        lblNombreCliente.setText("👤 " + cliente.getNombre());
        lblDpiCliente.setText("DPI: " + cliente.getDpi() + " (ID #" + cliente.getIdCliente() + ")");
        lblEstadoCliente.setText("Estado: " + cliente.getEstado().getEtiqueta());

        // MUESTRA AUTOMÁTICA DE PUNTOS EN TIEMPO REAL
        lblPuntosActuales.setText("⭐ " + cliente.getSaldoPuntos() + " pts actuales");
        txtBuscarCliente.setText(cliente.getDpi());
    }

    private void agregarProductoAlCarrito() {
        ElementoCatalogo elemento = (ElementoCatalogo) cmbProductos.getSelectedItem();
        if (elemento == null) {
            Dialogos.aviso(this, "Seleccione un producto del catálogo.");
            return;
        }

        int cantidad = (Integer) spnCantidad.getValue();
        if (cantidad <= 0) {
            Dialogos.aviso(this, "La cantidad debe ser mayor a cero.");
            return;
        }

        // Crear ItemVenta correspondiente (Producto o Menu)
        ItemVenta nuevoItem;
        if ("PRODUCTO".equals(elemento.tipo)) {
            nuevoItem = new ProductoIndividual((Producto) elemento.origen, cantidad);
        } else {
            nuevoItem = new MenuCompleto((Menu) elemento.origen, cantidad);
        }

        // Acumular si ya existía en el carrito
        boolean acumulado = false;
        for (ItemVenta item : carrito) {
            if (item.getTipoItem().equals(nuevoItem.getTipoItem()) && item.getIdItem() == nuevoItem.getIdItem()) {
                item.setCantidad(item.getCantidad() + cantidad);
                acumulado = true;
                break;
            }
        }
        if (!acumulado) {
            carrito.add(nuevoItem);
        }

        refrescarTablaCarrito();
        actualizarTotalesEnTiempoReal();
        spnCantidad.setValue(1);
    }

    private void eliminarItemDelCarrito() {
        int fila = tablaCarrito.getSelectedRow();
        if (fila < 0) {
            Dialogos.aviso(this, "Seleccione un producto de la tabla del carrito para eliminarlo.");
            return;
        }
        carrito.remove(fila);
        refrescarTablaCarrito();
        actualizarTotalesEnTiempoReal();
    }

    private void vaciarCarrito() {
        if (!carrito.isEmpty() && Dialogos.confirmar(this, "¿Desea vaciar todos los productos del carrito?")) {
            carrito.clear();
            refrescarTablaCarrito();
            actualizarTotalesEnTiempoReal();
        }
    }

    private void refrescarTablaCarrito() {
        modeloCarrito.setRowCount(0);
        int num = 1;
        for (ItemVenta item : carrito) {
            modeloCarrito.addRow(new Object[]{
                    num++,
                    item.getCodigo(),
                    item.getDescripcion(),
                    item.getTipoItem(),
                    item.getCantidad(),
                    Formato.moneda(item.getPrecioUnitario()),
                    Formato.moneda(item.getSubtotal()),
                    "+" + item.calcularPuntos() + " pts"
            });
        }
    }

    /**
     * ACTUALIZACIÓN EN TIEMPO REAL:
     * Calcula y refresca inmediatamente Subtotal, Total y Puntos a generar.
     */
    private void actualizarTotalesEnTiempoReal() {
        double subtotal = 0.0;
        int puntosAGenerar = 0;

        for (ItemVenta item : carrito) {
            subtotal += item.getSubtotal();
            puntosAGenerar += item.calcularPuntos();
        }

        lblSubtotal.setText(Formato.moneda(subtotal));
        lblTotal.setText(Formato.moneda(subtotal));
        lblPuntosGenerar.setText("⭐ +" + puntosAGenerar + " pts a generar");

        calcularCambio();
    }

    private void calcularCambio() {
        double total = carrito.stream().mapToDouble(ItemVenta::getSubtotal).sum();
        if (rbEfectivo.isSelected()) {
            try {
                String efText = txtEfectivo.getText().trim();
                double recibido = efText.isEmpty() ? 0.0 : Double.parseDouble(efText);
                double cambio = recibido - total;
                if (cambio >= 0) {
                    lblCambio.setText(Formato.moneda(cambio));
                    lblCambio.setForeground(Componentes.VERDE_FRESCO);
                } else {
                    lblCambio.setText("Faltan " + Formato.moneda(-cambio));
                    lblCambio.setForeground(Componentes.ROJO_TOMATE);
                }
            } catch (NumberFormatException e) {
                lblCambio.setText("Valor inválido");
            }
        }
    }

    private void confirmarPedido() {
        if (clienteActual == null) {
            Dialogos.aviso(this, "Debe seleccionar un cliente antes de confirmar el pedido.");
            txtBuscarCliente.requestFocus();
            return;
        }
        if (carrito.isEmpty()) {
            Dialogos.aviso(this, "El carrito está vacío. Agregue productos antes de confirmar.");
            return;
        }

        try {
            ClienteSnapshot snapshot = ClienteSnapshot.de(clienteActual);
            pedidoConfirmado = pedidoService.crearPedido(snapshot, carrito);
            Dialogos.exito(this, "Pedido #" + pedidoConfirmado.getIdPedido() + " confirmado exitosamente.\nTotal a cobrar: " + Formato.moneda(pedidoConfirmado.getTotal()));
            btnComprobante.setEnabled(true);
        } catch (Exception e) {
            Dialogos.error(this, e);
        }
    }

    private void cobrarPedido() {
        if (pedidoConfirmado == null) {
            // Confirmar automáticamente si aún no se había confirmado
            confirmarPedido();
            if (pedidoConfirmado == null) return;
        }

        TipoPago tipoPago = rbEfectivo.isSelected() ? TipoPago.EFECTIVO : TipoPago.TARJETA;
        Double efectivo = null;
        String ref = null;

        if (tipoPago == TipoPago.EFECTIVO) {
            try {
                efectivo = Double.parseDouble(txtEfectivo.getText().trim());
                if (efectivo < pedidoConfirmado.getTotal()) {
                    Dialogos.aviso(this, "El efectivo recibido es menor al total del pedido.");
                    return;
                }
            } catch (NumberFormatException e) {
                Dialogos.aviso(this, "Ingrese un monto en efectivo válido.");
                return;
            }
        } else {
            ref = txtReferenciaTarjeta.getText().trim();
            if (ref.isEmpty()) {
                ref = "POS-" + System.currentTimeMillis() % 100000;
            }
        }

        try {
            ResultadoCobro resultado = pagoService.cobrar(
                    pedidoConfirmado.getIdPedido(),
                    tipoPago,
                    efectivo,
                    ref,
                    true
            );

            // Refrescar datos del cliente y sus nuevos puntos
            Cliente actualizado = clienteService.buscarPorId(clienteActual.getIdCliente());
            if (actualizado != null) {
                seleccionarCliente(actualizado);
            }

            String mensajeCobro = "🎉 ¡Venta y Cobro Realizados con Éxito!\n\n"
                    + "Pedido: #" + pedidoConfirmado.getIdPedido() + "\n"
                    + "Cliente: " + clienteActual.getNombre() + "\n"
                    + "Total Pagado: " + Formato.moneda(pedidoConfirmado.getTotal()) + "\n"
                    + "Puntos ganados hoy: +" + pedidoConfirmado.getPuntosGenerados() + " pts ⭐\n"
                    + "Nuevo Saldo del Cliente: " + (actualizado != null ? actualizado.getSaldoPuntos() : "") + " pts";

            Dialogos.exito(this, mensajeCobro);
            btnComprobante.setEnabled(true);

            // Ofrecer ver el comprobante
            verComprobante();

            // Reiniciar venta
            carrito.clear();
            pedidoConfirmado = null;
            refrescarTablaCarrito();
            actualizarTotalesEnTiempoReal();
            txtEfectivo.setText("0.00");

        } catch (Exception e) {
            Dialogos.error(this, e);
        }
    }

    private void verComprobante() {
        if (pedidoConfirmado == null) {
            Dialogos.aviso(this, "No hay pedido confirmado para emitir comprobante.");
            return;
        }
        try {
            Pedido pedidoCompleto = pedidoService.buscarPedido(pedidoConfirmado.getIdPedido());
            String comprobante = Comprobante.deVenta(pedidoCompleto);
            Dialogos.mostrarTexto(this, "Comprobante de Venta #" + pedidoConfirmado.getIdPedido(), comprobante);
        } catch (Exception e) {
            Dialogos.error(this, e);
        }
    }

    // Clase auxiliar para los items del catálogo
    private static class ElementoCatalogo {
        final int id;
        final String codigo;
        final String nombre;
        final String tipo;
        final double precio;
        final int puntos;
        final int stock;
        final Object origen;

        ElementoCatalogo(int id, String codigo, String nombre, String tipo, double precio, int puntos, int stock, Object origen) {
            this.id = id;
            this.codigo = codigo;
            this.nombre = nombre;
            this.tipo = tipo;
            this.precio = precio;
            this.puntos = puntos;
            this.stock = stock;
            this.origen = origen;
        }

        @Override
        public String toString() {
            return "[" + codigo + "] " + nombre + " — " + Formato.moneda(precio) + " (⭐ +" + puntos + " pts)";
        }
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
        SwingUtilities.invokeLater(() -> new FrmPedido().setVisible(true));
    }
}
