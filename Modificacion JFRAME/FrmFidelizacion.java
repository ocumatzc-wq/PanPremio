package vista;

import panpuntos.model.Canje;
import panpuntos.model.Cliente;
import panpuntos.model.MovimientoPuntos;
import panpuntos.model.Recompensa;
import panpuntos.service.ClienteService;
import panpuntos.service.FidelizacionService;
import panpuntos.util.Comprobante;
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
 * Ventana de Fidelización (FrmFidelizacion) — Exclusiva para:
 * 1. Consultar saldo de puntos de un cliente.
 * 2. Visualizar catálogo de recompensas (Sándwich, Menú, Bebidas, etc.).
 * 3. Ejecutar canjes de recompensas validando puntos en tiempo real.
 * 4. Historial de movimientos de fidelización.
 * 
 * Persona 3 — Interfaz Gráfica (GUI con Swing).
 */
public class FrmFidelizacion extends javax.swing.JFrame {

    private static final String[] COLUMNAS_RECOMPENSAS = {
            "ID", "Código", "Recompensa / Premio", "Tipo", "Puntos Requeridos ⭐", "Estado"
    };

    private static final String[] COLUMNAS_MOVIMIENTOS = {
            "Fecha", "Tipo de Movimiento", "Puntos ⭐", "Saldo Resultante", "Referencia"
    };

    private final ClienteService clienteService = new ClienteService();
    private final FidelizacionService fidelizacionService = new FidelizacionService();

    private Cliente clienteActual;
    private Recompensa recompensaSeleccionada;
    private Canje ultimoCanje;

    // Componentes de Consulta de Cliente
    private JTextField txtBuscarCliente;
    private JButton btnConsultar;
    private JButton btnListaClientes;
    private JLabel lblNombreCliente;
    private JLabel lblDpiCliente;
    private JLabel lblSaldoPuntos;
    private JLabel lblEstadoCliente;

    // Catálogo de Recompensas
    private JTable tablaRecompensas;
    private DefaultTableModel modeloRecompensas;
    private JButton btnCanjear;
    private JButton btnVerComprobante;

    // Historial de Movimientos
    private JTable tablaMovimientos;
    private DefaultTableModel modeloMovimientos;

    private JButton btnVolver;

    public FrmFidelizacion() {
        initComponents();
        configurarVentana();
        recargarRecompensas();
    }

    private void configurarVentana() {
        setTitle("⭐ Club de Fidelización: Consulta de Saldo & Canje de Recompensas");
        setSize(1040, 680);
        setMinimumSize(new Dimension(960, 600));
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
        JLabel lblTitulo = Componentes.titulo("⭐ Club de Fidelización: Puntos y Premios Gourmet");
        JLabel lblSubtitulo = Componentes.etiqueta("  Consulte saldo acumulado y canjee sándwiches, menús y bebidas gratis");
        lblSubtitulo.setForeground(Componentes.TEXTO_MUTED);
        panelEncabezado.add(lblTitulo, BorderLayout.NORTH);
        panelEncabezado.add(lblSubtitulo, BorderLayout.SOUTH);
        panelRaiz.add(panelEncabezado, BorderLayout.NORTH);

        // 2. Panel Superior: Consulta de Saldo del Cliente
        JPanel panelCliente = new JPanel(new BorderLayout(8, 8));
        panelCliente.setBackground(Componentes.BLANCO_CARD);
        panelCliente.setBorder(Componentes.bordeSeccion("1. Consulta de Saldo del Cliente"));

        JPanel filaBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        filaBusqueda.setOpaque(false);

        txtBuscarCliente = new JTextField(14);
        txtBuscarCliente.setToolTipText("Ingrese DPI o ID del cliente...");
        txtBuscarCliente.addActionListener(e -> consultarCliente());

        btnConsultar = Componentes.boton("🔍 Consultar Saldo", Componentes.PAN_DORADO);
        btnConsultar.addActionListener(e -> consultarCliente());

        btnListaClientes = Componentes.botonSecundario("📋 Lista de Clientes");
        btnListaClientes.addActionListener(e -> mostrarSelectorClientes());

        filaBusqueda.add(Componentes.etiqueta("DPI o ID de Cliente:"));
        filaBusqueda.add(txtBuscarCliente);
        filaBusqueda.add(btnConsultar);
        filaBusqueda.add(btnListaClientes);
        panelCliente.add(filaBusqueda, BorderLayout.NORTH);

        // Datos del cliente consultado
        JPanel infoCliente = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 6));
        infoCliente.setOpaque(false);
        infoCliente.setBorder(new EmptyBorder(4, 8, 8, 8));

        lblNombreCliente = Componentes.valor("Cliente: No seleccionado");
        lblNombreCliente.setForeground(Componentes.PAN_TOSTADO);

        lblDpiCliente = Componentes.etiqueta("DPI: -");

        // BADGE DE PUNTOS DESTACADO
        lblSaldoPuntos = Componentes.badgePuntos("0 pts disponibles");

        lblEstadoCliente = Componentes.etiqueta("Estado: -");

        infoCliente.add(lblNombreCliente);
        infoCliente.add(lblDpiCliente);
        infoCliente.add(lblSaldoPuntos);
        infoCliente.add(lblEstadoCliente);
        panelCliente.add(infoCliente, BorderLayout.SOUTH);

        panelRaiz.add(panelCliente, BorderLayout.NORTH);

        // 3. Panel Central: Dividido entre Catálogo de Recompensas (Izq) e Historial (Der)
        JSplitPane splitTablas = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitTablas.setResizeWeight(0.55);
        splitTablas.setOpaque(false);
        splitTablas.setBorder(null);

        // A. Panel de Recompensas
        JPanel panelRecompensas = new JPanel(new BorderLayout(0, 8));
        panelRecompensas.setBackground(Componentes.BLANCO_CARD);
        panelRecompensas.setBorder(Componentes.bordeSeccion("2. Catálogo de Recompensas & Premios Gourmet"));

        modeloRecompensas = new DefaultTableModel(COLUMNAS_RECOMPENSAS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tablaRecompensas = Componentes.tabla(COLUMNAS_RECOMPENSAS);
        tablaRecompensas.setModel(modeloRecompensas);
        Componentes.configurarTabla(tablaRecompensas);
        tablaRecompensas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tablaRecompensas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarRecompensa();
            }
        });

        panelRecompensas.add(Componentes.panelDesplazable(tablaRecompensas), BorderLayout.CENTER);

        // Botones de Canje
        JPanel accionesCanje = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        accionesCanje.setOpaque(false);

        btnCanjear = Componentes.boton("🎁 Canjear Recompensa Seleccionada", Componentes.VERDE_FRESCO);
        btnCanjear.addActionListener(e -> ejecutarCanje());

        btnVerComprobante = Componentes.boton("🧾 Ver Comprobante", Componentes.PAN_TOSTADO);
        btnVerComprobante.setEnabled(false);
        btnVerComprobante.addActionListener(e -> verComprobante());

        accionesCanje.add(btnCanjear);
        accionesCanje.add(btnVerComprobante);
        panelRecompensas.add(accionesCanje, BorderLayout.SOUTH);

        splitTablas.setLeftComponent(panelRecompensas);

        // B. Panel de Movimientos / Historial
        JPanel panelMovimientos = new JPanel(new BorderLayout(0, 8));
        panelMovimientos.setBackground(Componentes.BLANCO_CARD);
        panelMovimientos.setBorder(Componentes.bordeSeccion("3. Historial de Puntos del Cliente"));

        modeloMovimientos = new DefaultTableModel(COLUMNAS_MOVIMIENTOS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tablaMovimientos = Componentes.tabla(COLUMNAS_MOVIMIENTOS);
        tablaMovimientos.setModel(modeloMovimientos);
        Componentes.configurarTabla(tablaMovimientos);

        panelMovimientos.add(Componentes.panelDesplazable(tablaMovimientos), BorderLayout.CENTER);

        splitTablas.setRightComponent(panelMovimientos);

        panelRaiz.add(splitTablas, BorderLayout.CENTER);

        // 4. Panel Inferior
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        panelInferior.setOpaque(false);

        btnVolver = Componentes.botonSecundario("🚪 Cerrar");
        btnVolver.addActionListener(e -> dispose());
        panelInferior.add(btnVolver);

        panelRaiz.add(panelInferior, BorderLayout.SOUTH);
        getContentPane().add(panelRaiz);
    }

    private void recargarRecompensas() {
        modeloRecompensas.setRowCount(0);
        try {
            List<Recompensa> lista = fidelizacionService.listarRecompensas(null);
            for (Recompensa r : lista) {
                modeloRecompensas.addRow(new Object[]{
                        r.getIdRecompensa(),
                        r.getCodigo(),
                        r.getNombre(),
                        r.getTipoItem().name(),
                        r.getPuntosNecesarios() + " pts",
                        r.getEstado().getEtiqueta()
                });
            }
        } catch (Exception e) {
            Dialogos.error(this, e);
        }
    }

    private void consultarCliente() {
        String texto = txtBuscarCliente.getText().trim();
        if (texto.isEmpty()) {
            Dialogos.aviso(this, "Ingrese el DPI o ID del cliente para consultar.");
            return;
        }

        try {
            Cliente encontrado = null;
            if (texto.matches("\\d+")) {
                try {
                    encontrado = clienteService.buscarPorId(Integer.parseInt(texto));
                } catch (Exception ignored) {
                }
                if (encontrado == null) {
                    encontrado = clienteService.buscarPorDpi(texto);
                }
            }

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
                "Seleccione el cliente:",
                "Consulta de Clientes",
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

    private void seleccionarCliente(Cliente c) {
        this.clienteActual = c;
        lblNombreCliente.setText("👤 Cliente: " + c.getNombre());
        lblDpiCliente.setText("DPI: " + c.getDpi() + " (ID #" + c.getIdCliente() + ")");
        lblEstadoCliente.setText("Estado: " + c.getEstado().getEtiqueta());

        // Actualización destacada del saldo de puntos
        lblSaldoPuntos.setText("⭐ " + c.getSaldoPuntos() + " pts disponibles");
        txtBuscarCliente.setText(c.getDpi());

        recargarHistorial();
    }

    private void recargarHistorial() {
        modeloMovimientos.setRowCount(0);
        if (clienteActual == null) return;

        try {
            List<MovimientoPuntos> movimientos = clienteService.historialDePuntos(clienteActual.getIdCliente());
            for (MovimientoPuntos m : movimientos) {
                modeloMovimientos.addRow(new Object[]{
                        Formato.fechaHora(m.getFechaMovimiento()),
                        m.getTipo(),
                        Formato.puntosConSigno(m.getPuntos()),
                        m.getSaldoResultante() + " pts",
                        m.getReferencia()
                });
            }
        } catch (Exception e) {
            Dialogos.error(this, e);
        }
    }

    private void seleccionarRecompensa() {
        int fila = tablaRecompensas.getSelectedRow();
        if (fila >= 0) {
            int id = ((Number) modeloRecompensas.getValueAt(fila, 0)).intValue();
            List<Recompensa> lista = fidelizacionService.listarRecompensas(null);
            for (Recompensa r : lista) {
                if (r.getIdRecompensa() == id) {
                    recompensaSeleccionada = r;
                    break;
                }
            }
        }
    }

    private void ejecutarCanje() {
        if (clienteActual == null) {
            Dialogos.aviso(this, "Debe consultar y seleccionar un cliente antes de realizar un canje.");
            txtBuscarCliente.requestFocus();
            return;
        }

        if (!clienteActual.puedeOperar()) {
            Dialogos.aviso(this, "El cliente " + clienteActual.getNombre() + " no está activo y no puede canjear premios.");
            return;
        }

        if (recompensaSeleccionada == null) {
            Dialogos.aviso(this, "Seleccione una recompensa de la tabla para realizar el canje.");
            return;
        }

        int saldo = clienteActual.getSaldoPuntos();
        int requeridos = recompensaSeleccionada.getPuntosNecesarios();

        // VALIDACIÓN EN TIEMPO REAL DE PUNTOS SUFICIENTES
        if (saldo < requeridos) {
            int faltantes = requeridos - saldo;
            Dialogos.aviso(this, "⚠️ PUNTOS INSUFICIENTES\n\n"
                    + "Cliente: " + clienteActual.getNombre() + "\n"
                    + "Saldo actual: " + saldo + " pts ⭐\n"
                    + "Recompensa: " + recompensaSeleccionada.getNombre() + " (" + requeridos + " pts requeridos)\n\n"
                    + "Le faltan " + faltantes + " pts para obtener este premio.");
            return;
        }

        String pregunta = "¿Confirma el canje de '" + recompensaSeleccionada.getNombre() + "'?\n\n"
                + "Se descontarán " + requeridos + " puntos de la cuenta de " + clienteActual.getNombre() + ".\n"
                + "Nuevo saldo será: " + (saldo - requeridos) + " pts ⭐";

        if (Dialogos.confirmar(this, pregunta)) {
            try {
                ultimoCanje = fidelizacionService.canjear(clienteActual.getIdCliente(), recompensaSeleccionada.getIdRecompensa());

                // Actualizar inmediatamente el cliente y su saldo en pantalla
                Cliente actualizado = clienteService.buscarPorId(clienteActual.getIdCliente());
                if (actualizado != null) {
                    seleccionarCliente(actualizado);
                }

                Dialogos.exito(this, "🎉 ¡Canje realizado con éxito!\n\n"
                        + "Premio: " + ultimoCanje.getNombreRecompensa() + "\n"
                        + "Puntos canjeados: -" + ultimoCanje.getPuntosUsados() + " pts\n"
                        + "Nuevo saldo: " + (actualizado != null ? actualizado.getSaldoPuntos() : "") + " pts ⭐\n"
                        + "Comprobante: CANJE-" + ultimoCanje.getIdCanje());

                btnVerComprobante.setEnabled(true);
                verComprobante();

            } catch (Exception e) {
                Dialogos.error(this, e);
            }
        }
    }

    private void verComprobante() {
        if (ultimoCanje == null) {
            Dialogos.aviso(this, "No hay ningún canje reciente para mostrar comprobante.");
            return;
        }
        int saldoRestante = clienteActual != null ? clienteActual.getSaldoPuntos() : 0;
        String ticket = Comprobante.deCanje(ultimoCanje.getNombreCliente(), ultimoCanje.getNombreRecompensa(), ultimoCanje.getPuntosUsados(), saldoRestante);
        Dialogos.mostrarTexto(this, "Comprobante de Canje #" + ultimoCanje.getIdCanje(), ticket);
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
        SwingUtilities.invokeLater(() -> new FrmFidelizacion().setVisible(true));
    }
}
