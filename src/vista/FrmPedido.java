package vista;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public final class FrmPedido extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmPedido.class.getName());
    private DefaultTableModel modeloTablaCarrito;
    private double totalPagar = 0.0;

    public FrmPedido() {
        initComponents();
        cmbTipo.setModel(new javax.swing.DefaultComboBoxModel<>(
                new String[]{"Producto", "Menú"}));
        setLocationRelativeTo(null); // Centrar ventana
        inicializarTablaCarrito();
        cargarItems();
    }

    // Opción disponible para agregar al carrito (producto o menú)
    private static class Opcion {
        final boolean esMenu;
        final int id;
        final String nombre;
        final double precio;
        final int stock;
        final int puntos;

        Opcion(boolean esMenu, int id, String nombre, double precio, int stock, int puntos) {
            this.esMenu = esMenu;
            this.id = id;
            this.nombre = nombre;
            this.precio = precio;
            this.stock = stock;
            this.puntos = puntos;
        }

        @Override
        public String toString() {
            return (esMenu ? "Menú: " : "Prod: ") + id + " - " + nombre
                    + " (Q" + String.format("%.2f", precio) + ")";
        }
    }

    // Configurar las columnas de la tabla del carrito
    private void inicializarTablaCarrito() {
        modeloTablaCarrito = new DefaultTableModel(
            new Object[]{"Tipo", "ID", "Nombre", "Cantidad", "Precio", "Subtotal", "Puntos"}, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false; // Evitar edición manual directa en celdas
            }
        };
        tblDetallePedido.setModel(modeloTablaCarrito);
    }

    // Cargar los ítems disponibles según el tipo seleccionado (Producto / Menú)
    private void cargarItems() {
        cmbItem.removeAllItems();
        if ("Producto".equals(cmbTipo.getSelectedItem())) {
            for (modelo.Producto p : new dao.ProductoDAO().obtenerProductosActivos()) {
                int puntos = new modelo.ProductoIndividual(
                        p.getCategoria(), p.getNombre(), p.getPrecio(), p.getExistencia()).calcularPuntos();
                cmbItem.addItem(new Opcion(false, p.getIdProducto(), p.getNombre(),
                        p.getPrecio(), p.getExistencia(), puntos));
            }
        } else {
            for (modelo.MenuCompleto m : new dao.MenuDAO().listarMenus()) {
                int stockMin = Integer.MAX_VALUE;
                for (modelo.ProductoIndividual componente : m.getProductos()) {
                    stockMin = Math.min(stockMin, componente.getExistencia());
                }
                if (m.getProductos().isEmpty()) {
                    stockMin = 0;
                }
                cmbItem.addItem(new Opcion(true, m.getIdMenu(), m.getNombre(),
                        m.getPrecio(), stockMin, m.calcularPuntos()));
            }
        }
    }

    // Botón para agregar productos/menús al carrito temporal
    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {
        Opcion opcion = (Opcion) cmbItem.getSelectedItem();
        if (opcion == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto o menú.");
            return;
        }

        String cantidadStr = txtCantidad.getText().trim();
        if (cantidadStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese la cantidad.");
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un número entero.");
            return;
        }

        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0.");
            return;
        }

        if (opcion.stock < cantidad) {
            JOptionPane.showMessageDialog(this, "Stock insuficiente. Disponible: " + opcion.stock,
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double subtotal = opcion.precio * cantidad;
        int puntosFila = opcion.puntos * cantidad;

        Object[] fila = {
            opcion.esMenu ? "Menú" : "Producto",
            opcion.id,
            opcion.nombre,
            cantidad,
            opcion.precio,
            subtotal,
            puntosFila
        };
        modeloTablaCarrito.addRow(fila);

        totalPagar += subtotal;
        actualizarTotalEtiqueta();

        txtCantidad.setText("");
        txtCantidad.requestFocusInWindow();
    }

    private void cmbTipoActionPerformed(java.awt.event.ActionEvent evt) {
        cargarItems();
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
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtIdCliente = new javax.swing.JTextField();
        cmbTipo = new javax.swing.JComboBox<>();
        cmbItem = new javax.swing.JComboBox<>();
        txtCantidad = new javax.swing.JTextField();
        lblTotal = new javax.swing.JLabel();
        btnAgregar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnGuardarPedido = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDetallePedido = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("ID Cliente");

        jLabel4.setText("Tipo");

        jLabel2.setText("Ítem");

        jLabel3.setText("Cantidad");

        cmbTipo.addActionListener(this::cmbTipoActionPerformed);

        lblTotal.setText("Total a Pagar: Q 0.00");

        btnAgregar.setText("Añadir al Carrito");
        btnAgregar.addActionListener(this::btnAgregarActionPerformed);

        btnEliminar.setText("Quitar Producto");
        btnEliminar.addActionListener(this::btnEliminarActionPerformed);

        btnGuardarPedido.setText("Completar Venta");
        btnGuardarPedido.addActionListener(this::btnGuardarPedidoActionPerformed);

        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(this::btnLimpiarActionPerformed);

        tblDetallePedido.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tblDetallePedido);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel4)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtIdCliente, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                            .addComponent(cmbTipo, 0, 150, Short.MAX_VALUE)
                            .addComponent(cmbItem, 0, 250, Short.MAX_VALUE)))
                    .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotal)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnAgregar)
                        .addGap(10, 10, 10)
                        .addComponent(btnEliminar)
                        .addGap(10, 10, 10)
                        .addComponent(btnGuardarPedido)
                        .addGap(10, 10, 10)
                        .addComponent(btnLimpiar)))
                .addGap(30, 30, 30)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtIdCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cmbTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cmbItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addComponent(lblTotal)
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAgregar)
                    .addComponent(btnEliminar)
                    .addComponent(btnGuardarPedido)
                    .addComponent(btnLimpiar))
                .addContainerGap(30, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        int filaSeleccionada = tblDetallePedido.getSelectedRow();
        if (filaSeleccionada >= 0) {
            // Restar del total general el subtotal de la fila eliminada
            double subtotalFila = (double) modeloTablaCarrito.getValueAt(filaSeleccionada, 5);
            totalPagar -= subtotalFila;
            actualizarTotalEtiqueta();

            modeloTablaCarrito.removeRow(filaSeleccionada);
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione de la tabla el ítem que desea quitar del carrito.");
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnGuardarPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarPedidoActionPerformed
        String idClienteStr = txtIdCliente.getText().trim();

        if (idClienteStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el ID del cliente.");
            return;
        }

        if (modeloTablaCarrito.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "El carrito de compras está vacío.");
            return;
        }

        int idCliente;
        try {
            idCliente = Integer.parseInt(idClienteStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El ID del cliente debe ser un número entero.");
            return;
        }

        dao.ClienteDAO clienteDao = new dao.ClienteDAO();
        modelo.Cliente cliente = clienteDao.obtenerClientePorId(idCliente);
        if (cliente == null) {
            JOptionPane.showMessageDialog(this, "El cliente con ID " + idCliente + " no existe.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<modelo.DetallePedido> detalles = construirDetalles();

        int puntosTotales = 0;
        for (modelo.DetallePedido d : detalles) {
            puntosTotales += d.getPuntos();
        }

        Object[] opcionesPago = {"EFECTIVO", "TARJETA"};
        String tipoPago = (String) JOptionPane.showInputDialog(this, "Método de pago:", "Cobro",
                JOptionPane.QUESTION_MESSAGE, null, opcionesPago, opcionesPago[0]);
        if (tipoPago == null) {
            return;
        }

        modelo.Pago pago = new modelo.Pago(0, tipoPago, totalPagar);
        if ("EFECTIVO".equals(tipoPago)) {
            String recibidoStr = JOptionPane.showInputDialog(this,
                    "Efectivo recibido (Total: Q " + String.format("%.2f", totalPagar) + "):");
            if (recibidoStr == null) {
                return;
            }
            double recibido;
            try {
                recibido = Double.parseDouble(recibidoStr.trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "El monto recibido debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (recibido < totalPagar) {
                JOptionPane.showMessageDialog(this, "El efectivo recibido es insuficiente.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            pago.setRecibido(recibido);
            pago.setCambio(recibido - totalPagar);
        } else {
            String referencia = JOptionPane.showInputDialog(this, "Referencia / autorización de la tarjeta:");
            if (referencia == null) {
                return;
            }
            pago.setRecibido(totalPagar);
            pago.setCambio(0.0);
            pago.setReferencia(referencia.trim());
        }

        dao.PedidoDAO pedidoDao = new dao.PedidoDAO();
        int idPedido = pedidoDao.crearPedido(idCliente, detalles, totalPagar, puntosTotales);
        if (idPedido <= 0) {
            JOptionPane.showMessageDialog(this, "No se pudo registrar el pedido en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        pago.setIdPedido(idPedido);
        if (pedidoDao.cobrarPedido(idPedido, pago)) {
            JOptionPane.showMessageDialog(this, "Venta completada.\nPedido #" + idPedido
                    + "\nTotal: Q " + String.format("%.2f", totalPagar)
                    + "\nPuntos acreditados: " + puntosTotales);
            limpiarTodo();
        } else {
            JOptionPane.showMessageDialog(this, "El pedido #" + idPedido
                    + " quedó PENDIENTE: no se pudo cobrar (verifique stock o pago).", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnGuardarPedidoActionPerformed

    private List<modelo.DetallePedido> construirDetalles() {
        List<modelo.DetallePedido> detalles = new java.util.ArrayList<>();
        for (int i = 0; i < modeloTablaCarrito.getRowCount(); i++) {
            String tipo = (String) modeloTablaCarrito.getValueAt(i, 0);
            int id = (int) modeloTablaCarrito.getValueAt(i, 1);
            int cantidad = (int) modeloTablaCarrito.getValueAt(i, 3);
            double precio = (double) modeloTablaCarrito.getValueAt(i, 4);
            int puntos = (int) modeloTablaCarrito.getValueAt(i, 6);

            modelo.DetallePedido d = new modelo.DetallePedido();
            if ("Menú".equals(tipo)) {
                d.setIdMenu(id);
            } else {
                d.setIdProducto(id);
            }
            d.setCantidad(cantidad);
            d.setPrecioUnitario(precio);
            d.setSubtotal(cantidad * precio);
            d.setPuntos(puntos);
            detalles.add(d);
        }
        return detalles;
    }

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        limpiarTodo();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void limpiarTodo() {
        txtIdCliente.setText("");
        txtCantidad.setText("");
        cmbTipo.setSelectedIndex(0);
        modeloTablaCarrito.setRowCount(0);
        totalPagar = 0.0;
        actualizarTotalEtiqueta();
        txtIdCliente.requestFocusInWindow();
    }

    private void actualizarTotalEtiqueta() {
        lblTotal.setText("Total a Pagar: Q " + String.format("%.2f", totalPagar));
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

        java.awt.EventQueue.invokeLater(() -> new FrmPedido().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardarPedido;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JComboBox<String> cmbTipo;
    private javax.swing.JComboBox<Object> cmbItem;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JTable tblDetallePedido;
    private javax.swing.JTextField txtCantidad;
    private javax.swing.JTextField txtIdCliente;
    // End of variables declaration//GEN-END:variables
}
