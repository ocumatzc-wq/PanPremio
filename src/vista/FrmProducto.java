/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;

/**
 *
 * @author Usuario
 */
public class FrmProducto extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmProducto.class.getName());

    /**
     * Creates new form FrmProducto
     */
    public FrmProducto() {
        initComponents();
        cmbCategoria.setModel(new javax.swing.DefaultComboBoxModel<>(
                new String[]{"Sandwich", "Bebida", "Acompanamiento"}));
        setLocationRelativeTo(null); // Centra la ventana en la pantalla
        cargarTablaProductos();      // Carga los productos desde Oracle
    }

    public void cargarTablaProductos() {
        javax.swing.table.DefaultTableModel modeloTabla = new javax.swing.table.DefaultTableModel(
            new Object[]{"ID", "Código", "Categoría", "Nombre", "Precio", "Stock", "Activo"}, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false; // Evita que se editen las celdas directamente
            }
        };

        try {
            dao.ProductoDAO dao = new dao.ProductoDAO();
            java.util.List<modelo.Producto> lista = dao.obtenerProductos();

            for (modelo.Producto p : lista) {
                Object[] fila = {
                    p.getIdProducto(),
                    p.getCodigo(),
                    p.getCategoria(),
                    p.getNombre(),
                    p.getPrecio(),
                    p.getExistencia(),
                    p.isActivo() ? "Sí" : "No"
                };
                modeloTabla.addRow(fila);
            }

            tblProductos.setModel(modeloTabla);

        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error al cargar productos: " + e.getMessage());
        }
    }

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {
        String codigo = txtCodigo.getText().trim();
        String categoria = (String) cmbCategoria.getSelectedItem();
        String nombre = txtNombre.getText().trim();
        String precioStr = txtPrecio.getText().trim();
        String existenciaStr = txtExistencia.getText().trim();

        if (nombre.isEmpty() || precioStr.isEmpty() || existenciaStr.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Por favor complete todos los campos.");
            return;
        }

        try {
            double precio = Double.parseDouble(precioStr);
            int existencia = Integer.parseInt(existenciaStr);

            modelo.Producto nuevoProd = new modelo.Producto(categoria, nombre, precio, existencia);
            if (!codigo.isEmpty()) {
                nuevoProd.setCodigo(codigo);
            }
            dao.ProductoDAO dao = new dao.ProductoDAO();

            if (dao.agregarProducto(nuevoProd)) {
                javax.swing.JOptionPane.showMessageDialog(this, "¡Producto guardado con éxito!");
                limpiarCampos();
                cargarTablaProductos();
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "No se pudo guardar el producto.");
            }
        } catch (NumberFormatException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "El precio y la existencia deben ser números válidos.");
        }
    }

    private void btnAbastecerActionPerformed(java.awt.event.ActionEvent evt) {
        int fila = tblProductos.getSelectedRow();
        if (fila == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla.");
            return;
        }

        int idProducto = Integer.parseInt(tblProductos.getValueAt(fila, 0).toString());
        String cantStr = javax.swing.JOptionPane.showInputDialog(this, "Cantidad a abastecer:");
        if (cantStr == null || cantStr.trim().isEmpty()) {
            return;
        }

        try {
            int cantidad = Integer.parseInt(cantStr.trim());
            String referencia = javax.swing.JOptionPane.showInputDialog(this, "Referencia (opcional):");
            dao.ProductoDAO dao = new dao.ProductoDAO();

            if (dao.abastecerProducto(idProducto, cantidad, referencia)) {
                javax.swing.JOptionPane.showMessageDialog(this, "Inventario abastecido correctamente.");
                cargarTablaProductos();
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "No se pudo abastecer. Verifique la cantidad.");
            }
        } catch (NumberFormatException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Ingrese una cantidad válida.");
        }
    }

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {
        limpiarCampos();
    }

    private void btnRefrescarActionPerformed(java.awt.event.ActionEvent evt) {
        cargarTablaProductos();
    }

    private void limpiarCampos() {
        txtCodigo.setText("");
        cmbCategoria.setSelectedIndex(0);
        txtNombre.setText("");
        txtPrecio.setText("");
        txtExistencia.setText("");
        txtCodigo.requestFocusInWindow();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        cmbCategoria = new javax.swing.JComboBox();
        txtNombre = new javax.swing.JTextField();
        txtPrecio = new javax.swing.JTextField();
        txtExistencia = new javax.swing.JTextField();
        btnGuardar = new javax.swing.JButton();
        btnAbastecer = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        btnRefrescar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblProductos = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Código:");

        jLabel2.setText("Categoria:");

        jLabel3.setText("Nombre:");

        jLabel4.setText("Precio:");

        jLabel5.setText("Existencia:");

        txtCodigo.addActionListener(this::txtCodigoActionPerformed);

        txtNombre.addActionListener(this::txtNombreActionPerformed);

        txtPrecio.addActionListener(this::txtPrecioActionPerformed);

        txtExistencia.addActionListener(this::txtExistenciaActionPerformed);

        btnGuardar.setText("Guardar Producto");
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);

        btnAbastecer.setText("Abastecer");
        btnAbastecer.addActionListener(this::btnAbastecerActionPerformed);

        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(this::btnLimpiarActionPerformed);

        btnRefrescar.setText("Refrescar Tabla");
        btnRefrescar.addActionListener(this::btnRefrescarActionPerformed);

        tblProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tblProductos);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtExistencia, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnGuardar)
                        .addGap(10, 10, 10)
                        .addComponent(btnAbastecer))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnLimpiar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnRefrescar)))
                .addGap(50, 50, 50)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 623, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(cmbCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txtExistencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnGuardar)
                            .addComponent(btnAbastecer))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnLimpiar)
                            .addComponent(btnRefrescar)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoActionPerformed
        cmbCategoria.requestFocusInWindow();
    }//GEN-LAST:event_txtCodigoActionPerformed

    private void txtNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreActionPerformed
        txtPrecio.requestFocusInWindow();
    }//GEN-LAST:event_txtNombreActionPerformed

    private void txtPrecioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecioActionPerformed
        txtExistencia.requestFocusInWindow();
    }//GEN-LAST:event_txtPrecioActionPerformed

    private void txtExistenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtExistenciaActionPerformed
        btnGuardarActionPerformed(evt);
    }//GEN-LAST:event_txtExistenciaActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
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
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new FrmProducto().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbastecer;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnRefrescar;
    private javax.swing.JComboBox cmbCategoria;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblProductos;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtExistencia;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPrecio;
    // End of variables declaration//GEN-END:variables
}
