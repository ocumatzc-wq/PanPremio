package vista;

import panpuntos.view.Componentes;
import panpuntos.view.Dialogos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Menú Principal (Dashboard) del Sistema — Persona 3 (GUI Swing).
 * Conecta todas las pantallas del sistema:
 * - FrmCliente (Gestión de Clientes)
 * - FrmProducto (Gestión de Productos y Sándwiches)
 * - FrmPedido (Punto de Venta POS & Carrito)
 * - FrmFidelizacion (Club de Fidelización & Canjes)
 */
public class FrmPrincipal extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmPrincipal.class.getName());

    // Botones del Dashboard
    private javax.swing.JButton btnClientes;
    private javax.swing.JButton btnProductos;
    private javax.swing.JButton btnVentas;
    private javax.swing.JButton btnFidelizacion;
    private javax.swing.JButton btnSalir;

    // Barra de Menús
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menuSistema;
    private javax.swing.JMenu menuMantenimientos;
    private javax.swing.JMenu menuOperaciones;
    private javax.swing.JMenu menuAyuda;

    public FrmPrincipal() {
        initComponents();
        configurarVentana();
    }

    private void configurarVentana() {
        setTitle("🥖 Pan, Puntos y Premios — Dashboard Principal (Front-end Swing)");
        setSize(920, 620);
        setMinimumSize(new Dimension(840, 560));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        // ==========================================
        // 1. BARRA DE MENÚS SUPERIOR
        // ==========================================
        menuBar = new javax.swing.JMenuBar();

        menuSistema = new javax.swing.JMenu("Sistema");
        JMenuItem itemInicio = new JMenuItem("🏠 Panel Principal");
        JMenuItem itemSalir = new JMenuItem("🚪 Salir del Sistema");
        itemSalir.addActionListener(e -> salirDelSistema());
        menuSistema.add(itemInicio);
        menuSistema.addSeparator();
        menuSistema.add(itemSalir);

        menuMantenimientos = new javax.swing.JMenu("Mantenimientos");
        JMenuItem itemClientes = new JMenuItem("👤 Gestión de Clientes");
        JMenuItem itemProductos = new JMenuItem("🥪 Gestión de Productos");
        itemClientes.addActionListener(this::btnClientesActionPerformed);
        itemProductos.addActionListener(this::btnProductosActionPerformed);
        menuMantenimientos.add(itemClientes);
        menuMantenimientos.add(itemProductos);

        menuOperaciones = new javax.swing.JMenu("Operaciones");
        JMenuItem itemPedido = new JMenuItem("🛒 Punto de Venta (FrmPedido)");
        JMenuItem itemFidelizacion = new JMenuItem("⭐ Club de Fidelización");
        itemPedido.addActionListener(this::btnVentasActionPerformed);
        itemFidelizacion.addActionListener(this::btnFidelizacionActionPerformed);
        menuOperaciones.add(itemPedido);
        menuOperaciones.add(itemFidelizacion);

        menuAyuda = new javax.swing.JMenu("Ayuda");
        JMenuItem itemAcercaDe = new JMenuItem("ℹ️ Acerca de Pan, Puntos y Premios");
        itemAcercaDe.addActionListener(e -> Dialogos.info(this,
                "🥖 Pan, Puntos y Premios — Restaurante & Deli Gourmet\n\n"
                        + "Persona 3 — Interfaz Gráfica (GUI con Swing en NetBeans):\n"
                        + "• FrmPrincipal: Dashboard de navegación interactivo.\n"
                        + "• FrmCliente: CRUD de clientes y membresías.\n"
                        + "• FrmProducto: CRUD de catálogo y existencias.\n"
                        + "• FrmPedido: Punto de venta POS con carrito y puntos en tiempo real.\n"
                        + "• FrmFidelizacion: Consulta de saldo y canje de premios."));
        menuAyuda.add(itemAcercaDe);

        menuBar.add(menuSistema);
        menuBar.add(menuMantenimientos);
        menuBar.add(menuOperaciones);
        menuBar.add(menuAyuda);
        setJMenuBar(menuBar);

        // ==========================================
        // 2. CONTENIDO PRINCIPAL (DASHBOARD)
        // ==========================================
        JPanel panelRaiz = new JPanel(new BorderLayout(16, 16));
        panelRaiz.setBackground(Componentes.FONDO_RESTAURANTE);
        panelRaiz.setBorder(new EmptyBorder(24, 28, 24, 28));

        // Banner Superior Gourmet
        JPanel bannerSuperior = new JPanel(new BorderLayout(0, 6));
        bannerSuperior.setOpaque(false);

        JLabel lblLogo = new JLabel("🥖 PAN, PUNTOS Y PREMIOS 🥪", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblLogo.setForeground(Componentes.PAN_TOSTADO);

        JLabel lblSubtitulo = new JLabel("Sistema Integral de Ventas POS, Facturación & Fidelización de Clientes", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSubtitulo.setForeground(Componentes.TEXTO_MUTED);

        JLabel lblTagline = new JLabel("⭐ Sándwiches Artesanales • Recompensas Exclusivas • Acumulación Automática de Puntos", SwingConstants.CENTER);
        lblTagline.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTagline.setForeground(Componentes.AMBAR_CHEDDAR);

        bannerSuperior.add(lblLogo, BorderLayout.NORTH);
        bannerSuperior.add(lblSubtitulo, BorderLayout.CENTER);
        bannerSuperior.add(lblTagline, BorderLayout.SOUTH);
        panelRaiz.add(bannerSuperior, BorderLayout.NORTH);

        // Grid Central de Módulos (Tarjetas / Botones)
        JPanel gridBotones = new JPanel(new GridLayout(2, 2, 20, 20));
        gridBotones.setOpaque(false);
        gridBotones.setBorder(new EmptyBorder(16, 20, 16, 20));

        btnClientes = crearBotonDashboard(
                "<html><center><font size='5'><b>👤 Gestión de Clientes</b></font><br>"
                        + "<font size='3' color='#F3E8DC'>Registro, edición, estados y puntos</font></center></html>",
                Componentes.PAN_TOSTADO
        );
        btnClientes.addActionListener(this::btnClientesActionPerformed);

        btnProductos = crearBotonDashboard(
                "<html><center><font size='5'><b>🥪 Gestión de Productos</b></font><br>"
                        + "<font size='3' color='#F3E8DC'>Catálogo de sándwiches, precios y stock</font></center></html>",
                Componentes.PAN_DORADO
        );
        btnProductos.addActionListener(this::btnProductosActionPerformed);

        btnVentas = crearBotonDashboard(
                "<html><center><font size='5'><b>🛒 Nueva Venta / Pedido (POS)</b></font><br>"
                        + "<font size='3' color='#E8F5E9'>Terminal de pedidos y puntos en tiempo real</font></center></html>",
                Componentes.VERDE_FRESCO
        );
        btnVentas.addActionListener(this::btnVentasActionPerformed);

        btnFidelizacion = crearBotonDashboard(
                "<html><center><font size='5'><b>⭐ Club de Fidelización</b></font><br>"
                        + "<font size='3' color='#FFF8E1'>Consulta de saldo y canje de premios</font></center></html>",
                Componentes.AMBAR_CHEDDAR
        );
        btnFidelizacion.addActionListener(this::btnFidelizacionActionPerformed);

        gridBotones.add(btnClientes);
        gridBotones.add(btnProductos);
        gridBotones.add(btnVentas);
        gridBotones.add(btnFidelizacion);
        panelRaiz.add(gridBotones, BorderLayout.CENTER);

        // Barra Inferior de Estado
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setOpaque(false);

        JLabel lblEstado = new JLabel("⚡ Sistema Listo • Persona 3 — Interfaz Gráfica (Swing / NetBeans)");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstado.setForeground(Componentes.TEXTO_MUTED);

        btnSalir = Componentes.botonPeligro("🚪 Salir");
        btnSalir.addActionListener(e -> salirDelSistema());

        panelInferior.add(lblEstado, BorderLayout.WEST);
        panelInferior.add(btnSalir, BorderLayout.EAST);
        panelRaiz.add(panelInferior, BorderLayout.SOUTH);

        getContentPane().add(panelRaiz);
    }

    private JButton crearBotonDashboard(String textoHtml, Color colorBase) {
        JButton btn = Componentes.boton(textoHtml, colorBase);
        btn.setPreferredSize(new Dimension(340, 110));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void btnClientesActionPerformed(java.awt.event.ActionEvent evt) {
        FrmCliente frm = new FrmCliente();
        frm.setVisible(true);
    }

    private void btnProductosActionPerformed(java.awt.event.ActionEvent evt) {
        FrmProducto frm = new FrmProducto();
        frm.setVisible(true);
    }

    private void btnVentasActionPerformed(java.awt.event.ActionEvent evt) {
        FrmPedido frm = new FrmPedido();
        frm.setVisible(true);
    }

    private void btnFidelizacionActionPerformed(java.awt.event.ActionEvent evt) {
        FrmFidelizacion frm = new FrmFidelizacion();
        frm.setVisible(true);
    }

    private void salirDelSistema() {
        if (Dialogos.confirmar(this, "¿Está seguro que desea salir del sistema de Pan, Puntos y Premios?")) {
            dispose();
            System.exit(0);
        }
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> new FrmPrincipal().setVisible(true));
    }
}
