package dao;

import proyectooracle.Conexion; // Importa la clase encargada de obtener la conexión con Oracle.
import java.sql.Connection; // Importaciones para trabajar con la base de datos mediante JDBC.
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CanjeDAO {
    /**
     * Realiza el canje de una recompensa utilizando los puntos de un cliente.
     *
     * El proceso incluye:
     * 1. Descontar los puntos del cliente.
     * 2. Registrar el canje realizado.
     * 3. Registrar el movimiento de puntos.
     * 4. Descontar del inventario los productos entregados.
     * 5. Registrar los movimientos de inventario.
     *
     * Todas las operaciones se ejecutan dentro de una transacción para
     * intentar garantizar que los cambios se realicen de manera conjunta.
     *
     * @param idCliente Identificador del cliente que realiza el canje.
     * @param idRecompensa Identificador de la recompensa seleccionada.
     * @param puntosRequeridos Cantidad de puntos necesarios para el canje.
     * @param idProductoEntrega Identificador del producto que se entregará,
     *                          si la recompensa corresponde a un producto.
     * @param idMenuEntrega Identificador del menú que se entregará,
     *                      si la recompensa corresponde a un menú.
     * @return El identificador del canje realizado o -1 si ocurre un error.
     */
    public int realizarCanje(int idCliente, int idRecompensa, int puntosRequeridos,
                             int idProductoEntrega, int idMenuEntrega) {
        // Consulta para descontar los puntos del cliente.
        // Solo se actualiza el saldo si tiene suficientes puntos.
        String sqlSaldo = "UPDATE Clientes SET saldo_puntos = saldo_puntos - ? WHERE id_cliente = ? AND saldo_puntos >= ?";
        String sqlCanje = "INSERT INTO Canjes (id_cliente, id_recompensa, puntos_usados) VALUES (?, ?, ?)"; // Consulta para registrar el canje en la base de datos.
        String sqlMovPts = "INSERT INTO Movimientos_Puntos (id_cliente, tipo, puntos, referencia) VALUES (?, 'CANJE', ?, ?)"; // Consulta para registrar el movimiento negativo de puntos.
        String sqlComp = "SELECT id_producto, cantidad FROM Menu_Composicion WHERE id_menu = ?"; // Consulta para obtener los productos que componen un menú.
        // Consulta para descontar existencias de un producto.
        // La actualización solo ocurre si hay suficiente inventario.
        String sqlStock = "UPDATE Productos SET existencia = existencia - ? WHERE id_producto = ? AND existencia >= ?"; 
        String sqlMovInv = "INSERT INTO Movimientos_Inventario (id_producto, tipo, cantidad, referencia) VALUES (?, 'SALIDA', ?, ?)"; // Consulta para registrar la salida de un producto del inventari
        
        Connection cn = Conexion.getConexion(); // Obtiene la conexión con la base de datos Oracle.
        if (cn == null) { // Si no se pudo establecer la conexión, no se puede realizar el canje.
            System.err.println("No se pudo obtener conexión a Oracle.");
            return -1;
        }
        // Almacena el identificador generado para el nuevo canje.
        // Se inicializa en -1 para indicar que todavía no se ha obtenido.
        int idCanje = -1;
        try {
            // Desactiva la confirmación automática.
            // Los cambios se confirmarán todos juntos mediante commit().
            cn.setAutoCommit(false);
            
            try (PreparedStatement ps = cn.prepareStatement(sqlSaldo)) { // PASO 1: Descontar los puntos del cliente.
                ps.setInt(1, puntosRequeridos); // Primer parámetro: puntos que se descontarán.
                ps.setInt(2, idCliente); // Segundo parámetro: identificador del cliente.
                ps.setInt(3, puntosRequeridos); // Tercer parámetro: saldo mínimo necesario.
                // Ejecuta la actualización y devuelve las filas modificadas.
                // Si devuelve 0, el cliente no existe o no tiene suficientes
                // puntos para realizar el canje.
                if (ps.executeUpdate() == 0) { 
                    throw new SQLException("El cliente no tiene puntos suficientes.");
                }
            }
            // PASO 2: Registrar el canje y solicitar su identificador generado.
            try (PreparedStatement ps = cn.prepareStatement(sqlCanje, new String[]{"id_canje"})) { // Asigna los datos que se guardarán en el registro del canje.
                ps.setInt(1, idCliente);
                ps.setInt(2, idRecompensa);
                ps.setInt(3, puntosRequeridos);
                ps.executeUpdate(); // Inserta el registro en la tabla Canjes.
                try (ResultSet rs = ps.getGeneratedKeys()) { // Recupera el identificador generado por la base de datos.
                    if (rs.next()) {
                        idCanje = rs.getInt(1);
                    }
                }
            }
            // PASO 3: Registrar el movimiento de puntos.
            try (PreparedStatement ps = cn.prepareStatement(sqlMovPts)) { 
                ps.setInt(1, idCliente); // Identifica al cliente al que pertenece el movimiento.
                ps.setInt(2, -puntosRequeridos); // Los puntos se registran como una cantidad negativa porque representan puntos utilizados en el canje.
                ps.setString(3, "canje " + idCanje); // Guarda una referencia al canje que originó el movimiento.
                ps.executeUpdate(); // Inserta el movimiento en el historial de puntos.
            }
            // Prepara las consultas que se utilizarán para gestionar
            // el inventario y consultar los componentes de un menú.
            try (PreparedStatement psStock = cn.prepareStatement(sqlStock);
                 PreparedStatement psMovInv = cn.prepareStatement(sqlMovInv);
                 PreparedStatement psComp = cn.prepareStatement(sqlComp)) {
                
                if (idMenuEntrega > 0) { // Si se entregará un menú, se consultan sus componentes.
                    psComp.setInt(1, idMenuEntrega); // Indica qué menú se debe consultar.
                    try (ResultSet rc = psComp.executeQuery()) { // Ejecuta la consulta y recorre los productos del menú.
                        while (rc.next()) {
                            int compProducto = rc.getInt("id_producto"); // Obtiene el identificador del producto componente.
                            int compCantidad = rc.getInt("cantidad"); // Obtiene la cantidad requerida de ese producto.
                            descontar(psStock, psMovInv, compProducto, compCantidad, idCanje); // Descuenta del inventario la cantidad necesaria y registra el movimiento de salida.
                        }
                    }
                // Si no se entregará un menú, pero sí un producto individual, se descuenta una unidad de ese producto.
                } else if (idProductoEntrega > 0) {
                    descontar(psStock, psMovInv, idProductoEntrega, 1, idCanje);
                }
            }
            // PASO 4: Confirma todos los cambios realizados.
            // Los descuentos y registros quedan confirmados en la transacción.
            cn.commit();
            return idCanje; // Devuelve el identificador del canje realizado.
        } catch (SQLException e) {
            // Si ocurre un error SQL, intenta revertir los cambios
            // realizados durante la transacción.
            try {
                cn.rollback();
            } catch (SQLException ex) { // Informa si también falla la reversión de los cambios.
                System.err.println("Error en rollback: " + ex.getMessage());
            }
            System.err.println("Error al realizar canje: " + e.getMessage()); // Informa el error que impidió completar el canje.
            return -1; // Indica que el canje no se completó correctamente.
        } finally {
            // Intenta restaurar la confirmación automática y cerrar la conexión, independientemente del resultado del proceso.
            try {
                cn.setAutoCommit(true);
                cn.close();
            } catch (SQLException ex) {
                System.err.println("Error al cerrar la conexión: " + ex.getMessage());
            }
        }
    }
    /**
     * Descuenta una cantidad de un producto y registra la salida
     * correspondiente en el historial de movimientos de inventario.
     *
     * Este método se reutiliza para productos individuales y para los componentes de los menús que se entregan en un canje.
     *
     * @param psStock Sentencia preparada para actualizar las existencias.
     * @param psMovInv Sentencia preparada para registrar la salida.
     * @param idProducto Identificador del producto que se descontará.
     * @param cantidad Cantidad que se retirará del inventario.
     * @param idCanje Identificador del canje que origina la salida.
     * @throws SQLException Si falla la actualización o el registro
     *                      del movimiento de inventario.
     */
    private void descontar(PreparedStatement psStock, PreparedStatement psMovInv,
                           int idProducto, int cantidad, int idCanje) throws SQLException {
        psStock.setInt(1, cantidad); // Define la cantidad que se descontará del inventario.
        psStock.setInt(2, idProducto); // Define el producto al que se aplicará el descuento.
        psStock.setInt(3, cantidad); // Comprueba que la existencia sea igual o superior a la cantidad.
        // Ejecuta la actualización de existencias.
        // Si no se modifica ninguna fila, el producto no existe o no tiene suficiente inventario disponible.
        if (psStock.executeUpdate() == 0) {
            throw new SQLException("Stock insuficiente para el producto " + idProducto);
        }
        psMovInv.setInt(1, idProducto); // Define el producto al que corresponde el movimiento de salida.
        psMovInv.setInt(2, cantidad); // Registra la cantidad que se retiró del inventario.
        psMovInv.setString(3, "canje " + idCanje); // Relaciona el movimiento de inventario con el canje realizado.
        psMovInv.executeUpdate(); // Inserta el movimiento en el historial de inventario.
    }
}
