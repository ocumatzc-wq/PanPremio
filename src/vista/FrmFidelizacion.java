package vista;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public final class FrmFidelizacion extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmFidelizacion.class.getName());
    private int idClienteActual = -1;
    private List<modelo.Recompensa> recompensasActuales;

    public FrmFidelizacion() {
        initComponents();
        setLocationRelativeTo(null);
        cargarRecompensas();
    }

    private void cargarRecompensas() {
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"Recompensa", "Puntos requeridos"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        recompensasActuales = new dao.RecompensaDAO().listarRecompensasActivas();
        for (modelo.Recompensa r : recompensasActuales) {
            modelo.addRow(new Object[]{r.getNombre(), r.getPuntosRequeridos()});
        }
        tblRecompensas.setModel(modelo);
    }

    private void btnBuscarClienteActionPerformed(java.awt.event.ActionEvent evt) {
        String idStr = txtIdCliente.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el ID del cliente.");
            return;
        }

        int idCliente;
        try {
            idCliente = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El ID del cliente debe ser un número entero.");
            return;
        }

        modelo.Cliente cliente = new dao.ClienteDAO().obtenerClientePorId(idCliente);
        if (cliente == null) {
            JOptionPane.showMessageDialog(this, "No existe un cliente con ese ID.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        idClienteActual = cliente.getIdCliente();
        lblCliente.setText("Cliente: " + cliente.getNombre() + " " + cliente.getApellido()
                + " | Puntos: " + cliente.getSaldoPuntos());
        cargarMovimientos(idCliente);
    }

    private void btnCanjearActionPerformed(java.awt.event.ActionEvent evt) {
        if (idClienteActual == -1) {
            JOptionPane.showMessageDialog(this, "Primero busque un cliente.");
            return;
        }

        int fila = tblRecompensas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una recompensa de la tabla.");
            return;
        }

        modelo.Recompensa recompensa = recompensasActuales.get(fila);
        modelo.Cliente cliente = new dao.ClienteDAO().obtenerClientePorId(idClienteActual);

        if (cliente.getSaldoPuntos() < recompensa.getPuntosRequeridos()) {
            JOptionPane.showMessageDialog(this,
                    "Puntos insuficientes.\nDisponibles: " + cliente.getSaldoPuntos()
                    + "\nRequeridos: " + recompensa.getPuntosRequeridos(),
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmar = JOptionPane.showConfirmDialog(this,
                "¿Canjear \"" + recompensa.getNombre() + "\" por "
                + recompensa.getPuntosRequeridos() + " puntos?",
                "Confirmar canje", JOptionPane.YES_NO_OPTION);
        if (confirmar != JOptionPane.YES_OPTION) {
            return;
        }

        int idCanje = new dao.CanjeDAO().realizarCanje(
                idClienteActual,
                recompensa.getIdRecompensa(),
                recompensa.getPuntosRequeridos(),
                recompensa.getIdProducto(),
                recompensa.getIdMenu());

        if (idCanje > 0) {
            JOptionPane.showMessageDialog(this, "Canje realizado. N° " + idCanje
                    + "\nSe descontaron " + recompensa.getPuntosRequeridos() + " puntos.");
            btnBuscarClienteActionPerformed(evt);
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo realizar el canje (verifique puntos o stock).",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarMovimientos(int idCliente) {
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"Fecha", "Tipo", "Puntos", "Referencia"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        for (modelo.MovimientoPuntos m : new dao.MovimientoPuntosDAO().listarPorCliente(idCliente)) {
            modelo.addRow(new Object[]{m.getFechaMovimiento(), m.getTipo(), m.getPuntos(), m.getReferencia()});
        }
        tblMovimientos.setModel(modelo);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtIdCliente = new javax.swing.JTextField();
        btnBuscarCliente = new javax.swing.JButton();
        lblCliente = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPaneRecompensas = new javax.swing.JScrollPane();
        tblRecompensas = new javax.swing.JTable();
        btnCanjear = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jScrollPaneMov = new javax.swing.JScrollPane();
        tblMovimientos = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("ID Cliente");

        btnBuscarCliente.setText("Buscar Cliente");
        btnBuscarCliente.addActionListener(this::btnBuscarClienteActionPerformed);

        lblCliente.setText("Cliente: - | Puntos: 0");

        jLabel2.setText("Recompensas disponibles");

        tblRecompensas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2"
            }
        ));
        jScrollPaneRecompensas.setViewportView(tblRecompensas);

        btnCanjear.setText("Canjear");
        btnCanjear.addActionListener(this::btnCanjearActionPerformed);

        jLabel3.setText("Historial de puntos");

        tblMovimientos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPaneMov.setViewportView(tblMovimientos);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(10, 10, 10)
                        .addComponent(txtIdCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnBuscarCliente))
                    .addComponent(lblCliente)
                    .addComponent(jLabel2)
                    .addComponent(jScrollPaneRecompensas, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCanjear))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jScrollPaneMov, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtIdCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarCliente))
                .addGap(10, 10, 10)
                .addComponent(lblCliente)
                .addGap(15, 15, 15)
                .addComponent(jLabel2)
                .addGap(5, 5, 5)
                .addComponent(jScrollPaneRecompensas, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(btnCanjear)
                .addContainerGap(20, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel3)
                .addGap(5, 5, 5)
                .addComponent(jScrollPaneMov, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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

        java.awt.EventQueue.invokeLater(() -> new FrmFidelizacion().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscarCliente;
    private javax.swing.JButton btnCanjear;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPaneMov;
    private javax.swing.JScrollPane jScrollPaneRecompensas;
    private javax.swing.JLabel lblCliente;
    private javax.swing.JTable tblMovimientos;
    private javax.swing.JTable tblRecompensas;
    private javax.swing.JTextField txtIdCliente;
    // End of variables declaration//GEN-END:variables
}
