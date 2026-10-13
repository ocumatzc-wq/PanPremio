package vista;

import dao.ClienteDAO;
import modelo.Cliente;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public final class FrmCliente extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(FrmCliente.class.getName());

    private int idClienteActual = -1;
    private int saldoPuntosActual = 0;
    private boolean activoActual = true;

    public FrmCliente() {
        initComponents();
        setLocationRelativeTo(null);
        tblClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cargarClienteSeleccionado();
            }
        });
        cargarTablaClientes();
    }

    // Consultar los clientes en Oracle y mostrarlos en la tabla.
    public void cargarTablaClientes() {
        DefaultTableModel modeloTabla = new DefaultTableModel(
                new Object[]{
                    "ID", "Código", "Nombre", "Apellido",
                    "Email", "Teléfono", "Puntos", "Activo"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        try {
            ClienteDAO dao = new ClienteDAO();
            List<Cliente> lista = dao.obtenerClientes();

            for (Cliente c : lista) {
                Object[] fila = {
                    c.getIdCliente(),
                    c.getCodigo(),
                    c.getNombre(),
                    c.getApellido(),
                    c.getEmail(),
                    c.getTelefono(),
                    c.getSaldoPuntos(),
                    c.isActivo() ? "Sí" : "No"
                };
                modeloTabla.addRow(fila);
            }

            tblClientes.setModel(modeloTabla);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al cargar los clientes: " + e.getMessage(),
                    "Error de consulta",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Guardar un cliente en Oracle.
    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String email = txtEmail.getText().trim();
        String telefono = txtTelefono.getText().trim();

        if (nombre.isEmpty() || apellido.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Por favor, ingrese el nombre y apellido."
            );
            return;
        }

        Cliente nuevoCliente = new Cliente(nombre, apellido, email, telefono);
        if (!codigo.isEmpty()) {
            nuevoCliente.setCodigo(codigo);
        }
        ClienteDAO dao = new ClienteDAO();

        try {
            if (dao.agregarCliente(nuevoCliente)) {
                JOptionPane.showMessageDialog(
                        this,
                        "¡Cliente registrado con éxito en Oracle!"
                );
                limpiarCampos();
                cargarTablaClientes();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo registrar el cliente.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al guardar el cliente: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Buscar un cliente por su código.
    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el código del cliente a buscar.");
            return;
        }

        Cliente c = new ClienteDAO().obtenerClientePorCodigo(codigo);
        if (c == null) {
            JOptionPane.showMessageDialog(this, "No se encontró un cliente con ese código.");
            return;
        }
        mostrarCliente(c);
    }

    // Actualizar los datos del cliente cargado.
    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {
        if (idClienteActual == -1) {
            JOptionPane.showMessageDialog(this, "Busque o seleccione un cliente de la tabla.");
            return;
        }

        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();

        if (nombre.isEmpty() || apellido.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese el nombre y apellido.");
            return;
        }

        Cliente c = new Cliente();
        c.setIdCliente(idClienteActual);
        c.setCodigo(codigo.isEmpty() ? null : codigo);
        c.setNombre(nombre);
        c.setApellido(apellido);
        c.setEmail(txtEmail.getText().trim());
        c.setTelefono(txtTelefono.getText().trim());
        c.setSaldoPuntos(saldoPuntosActual);
        c.setActivo(activoActual);

        if (new ClienteDAO().actualizarCliente(c)) {
            JOptionPane.showMessageDialog(this, "Cliente actualizado correctamente.");
            cargarTablaClientes();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar el cliente.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Activar o desactivar el cliente cargado.
    private void btnActivarActionPerformed(java.awt.event.ActionEvent evt) {
        if (idClienteActual == -1) {
            JOptionPane.showMessageDialog(this, "Busque o seleccione un cliente de la tabla.");
            return;
        }

        boolean nuevoEstado = !activoActual;
        if (new ClienteDAO().cambiarEstadoCliente(idClienteActual, nuevoEstado)) {
            activoActual = nuevoEstado;
            JOptionPane.showMessageDialog(this,
                    "Cliente " + (nuevoEstado ? "activado" : "desactivado") + " correctamente.");
            cargarTablaClientes();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo cambiar el estado del cliente.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Limpiar los campos del formulario.
    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {
        limpiarCampos();
    }

    // Volver a consultar los clientes.
    private void btnRegresacarActionPerformed(java.awt.event.ActionEvent evt) {
        cargarTablaClientes();
    }

    private void cargarClienteSeleccionado() {
        int fila = tblClientes.getSelectedRow();
        if (fila == -1) {
            return;
        }
        int id = Integer.parseInt(tblClientes.getValueAt(fila, 0).toString());
        Cliente c = new ClienteDAO().obtenerClientePorId(id);
        if (c != null) {
            mostrarCliente(c);
        }
    }

    private void mostrarCliente(Cliente c) {
        idClienteActual = c.getIdCliente();
        saldoPuntosActual = c.getSaldoPuntos();
        activoActual = c.isActivo();
        txtCodigo.setText(c.getCodigo() == null ? "" : c.getCodigo());
        txtNombre.setText(c.getNombre());
        txtApellido.setText(c.getApellido());
        txtEmail.setText(c.getEmail());
        txtTelefono.setText(c.getTelefono());
    }

    private void limpiarCampos() {
        idClienteActual = -1;
        saldoPuntosActual = 0;
        activoActual = true;
        txtCodigo.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtEmail.setText("");
        txtTelefono.setText("");
        txtCodigo.requestFocusInWindow();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        txtNombre = new javax.swing.JTextField();
        txtApellido = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        txtTelefono = new javax.swing.JTextField();
        btnGuardar = new javax.swing.JButton();
        btnBuscar = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        btnActivar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        btnRegresacar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Código:");

        jLabel2.setText("Nombre:");

        jLabel3.setText("Apellido:");

        jLabel4.setText("Email:");

        jLabel5.setText("Teléfono:");

        txtCodigo.addActionListener(this::txtCodigoActionPerformed);

        txtNombre.addActionListener(this::txtNombreActionPerformed);

        txtApellido.addActionListener(this::txtApellidoActionPerformed);

        txtEmail.addActionListener(this::txtEmailActionPerformed);

        txtTelefono.addActionListener(this::txtTelefonoActionPerformed);

        btnGuardar.setText("Guardar Cliente");
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);

        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(this::btnBuscarActionPerformed);

        btnActualizar.setText("Actualizar");
        btnActualizar.addActionListener(this::btnActualizarActionPerformed);

        btnActivar.setText("Activar/Desactivar");
        btnActivar.addActionListener(this::btnActivarActionPerformed);

        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(this::btnLimpiarActionPerformed);

        btnRegresacar.setText("Refrescar Tabla");
        btnRegresacar.addActionListener(this::btnRegresacarActionPerformed);

        tblClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tblClientes);

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
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnGuardar)
                        .addGap(10, 10, 10)
                        .addComponent(btnBuscar)
                        .addGap(10, 10, 10)
                        .addComponent(btnActualizar)
                        .addGap(10, 10, 10)
                        .addComponent(btnActivar)
                        .addGap(10, 10, 10)
                        .addComponent(btnLimpiar)
                        .addGap(10, 10, 10)
                        .addComponent(btnRegresacar)))
                .addGap(30, 30, 30)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
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
                            .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnGuardar)
                            .addComponent(btnBuscar)
                            .addComponent(btnActualizar)
                            .addComponent(btnActivar)
                            .addComponent(btnLimpiar)
                            .addComponent(btnRegresacar)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoActionPerformed
        txtNombre.requestFocusInWindow();
    }//GEN-LAST:event_txtCodigoActionPerformed

    private void txtNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreActionPerformed
        txtApellido.requestFocusInWindow();
    }//GEN-LAST:event_txtNombreActionPerformed

    private void txtApellidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtApellidoActionPerformed
        txtEmail.requestFocusInWindow();
    }//GEN-LAST:event_txtApellidoActionPerformed

    private void txtEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailActionPerformed
        txtTelefono.requestFocusInWindow();
    }//GEN-LAST:event_txtEmailActionPerformed

    private void txtTelefonoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTelefonoActionPerformed
        btnGuardarActionPerformed(evt);
    }//GEN-LAST:event_txtTelefonoActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info
                    : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException
                | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> new FrmCliente().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActivar;
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnRegresacar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblClientes;
    private javax.swing.JTextField txtApellido;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
