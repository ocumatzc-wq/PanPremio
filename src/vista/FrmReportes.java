package vista;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public final class FrmReportes extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmReportes.class.getName());
    private final dao.ReporteDAO reporteDAO = new dao.ReporteDAO();

    public FrmReportes() {
        initComponents();
        cmbReporte.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{
            "Ventas por período", "Productos más vendidos", "Productos con bajo inventario",
            "Puntos por cliente", "Historial de canjes", "Ventas de menús"}));
        String hoy = java.time.LocalDate.now().toString();
        txtDesde.setText(hoy);
        txtHasta.setText(hoy);
        setLocationRelativeTo(null);
        cmbReporteActionPerformed(null);
    }

    private DefaultTableModel modelo(String... columnas) {
        return new DefaultTableModel(new Object[][]{}, columnas) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    private void agregar(DefaultTableModel modelo, List<Object[]> filas) {
        for (Object[] fila : filas) {
            modelo.addRow(fila);
        }
    }

    private void cmbReporteActionPerformed(java.awt.event.ActionEvent evt) {
        boolean rango = cmbReporte.getSelectedIndex() == 0;
        jLabel2.setVisible(rango);
        txtDesde.setVisible(rango);
        jLabel3.setVisible(rango);
        txtHasta.setVisible(rango);
        lblResumen.setVisible(rango);
        btnGenerarActionPerformed(evt);
    }

    private void btnGenerarActionPerformed(java.awt.event.ActionEvent evt) {
        int indice = cmbReporte.getSelectedIndex();
        DefaultTableModel modelo;
        switch (indice) {
            case 0:
                modelo = modelo("Fecha", "Pedido", "Tipo de pago", "Monto");
                java.sql.Date desde;
                java.sql.Date hasta;
                try {
                    desde = java.sql.Date.valueOf(txtDesde.getText().trim());
                    hasta = java.sql.Date.valueOf(txtHasta.getText().trim());
                } catch (IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(this,
                            "Use el formato de fecha AAAA-MM-DD en Desde y Hasta.");
                    return;
                }
                double efectivo = 0;
                double tarjeta = 0;
                for (Object[] fila : reporteDAO.ventasPorPeriodo(desde, hasta)) {
                    modelo.addRow(fila);
                    double monto = ((Number) fila[3]).doubleValue();
                    if ("EFECTIVO".equals(fila[2])) {
                        efectivo += monto;
                    } else {
                        tarjeta += monto;
                    }
                }
                lblResumen.setText(String.format(
                        "Total vendido: %.2f   |   Efectivo: %.2f   |   Tarjeta: %.2f",
                        efectivo + tarjeta, efectivo, tarjeta));
                break;
            case 1:
                modelo = modelo("Código", "Producto", "Categoría", "Unidades vendidas");
                agregar(modelo, reporteDAO.productosMasVendidos());
                break;
            case 2:
                modelo = modelo("Código", "Producto", "Categoría", "Existencia");
                agregar(modelo, reporteDAO.productosBajoInventario());
                break;
            case 3:
                modelo = modelo("Código", "Cliente", "Puntos");
                agregar(modelo, reporteDAO.puntosPorCliente());
                break;
            case 4:
                modelo = modelo("N° canje", "Cliente", "Recompensa", "Puntos usados", "Fecha");
                agregar(modelo, reporteDAO.historialCanjes());
                break;
            default:
                modelo = modelo("Código", "Menú", "Cantidad", "Importe");
                agregar(modelo, reporteDAO.ventasMenus());
                break;
        }
        tblReporte.setModel(modelo);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        cmbReporte = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        txtDesde = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtHasta = new javax.swing.JTextField();
        btnGenerar = new javax.swing.JButton();
        lblResumen = new javax.swing.JLabel();
        jScrollPaneReporte = new javax.swing.JScrollPane();
        tblReporte = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Reporte");

        cmbReporte.addActionListener(this::cmbReporteActionPerformed);

        jLabel2.setText("Desde");

        jLabel3.setText("Hasta");

        btnGenerar.setText("Generar");
        btnGenerar.addActionListener(this::btnGenerarActionPerformed);

        tblReporte.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Columna 1", "Columna 2", "Columna 3", "Columna 4"
            }
        ));
        jScrollPaneReporte.setViewportView(tblReporte);

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
                        .addComponent(cmbReporte, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(jLabel2)
                        .addGap(10, 10, 10)
                        .addComponent(txtDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(jLabel3)
                        .addGap(10, 10, 10)
                        .addComponent(txtHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(btnGenerar))
                    .addComponent(lblResumen)
                    .addComponent(jScrollPaneReporte, javax.swing.GroupLayout.PREFERRED_SIZE, 720, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cmbReporte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGenerar))
                .addGap(10, 10, 10)
                .addComponent(lblResumen)
                .addGap(10, 10, 10)
                .addComponent(jScrollPaneReporte, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        java.awt.EventQueue.invokeLater(() -> new FrmReportes().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGenerar;
    private javax.swing.JComboBox<String> cmbReporte;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPaneReporte;
    private javax.swing.JLabel lblResumen;
    private javax.swing.JTable tblReporte;
    private javax.swing.JTextField txtDesde;
    private javax.swing.JTextField txtHasta;
    // End of variables declaration//GEN-END:variables
}
