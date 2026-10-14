package dao;

import proyectooracle.Conexion;
import modelo.DetallePedido;
import modelo.Pago;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
// Clase DAO para gestionar todas las operaciones de base de datos relacionadas con los pedidos
public class PedidoDAO {
    // Método para crear un nuevo pedido junto con sus detalles en la base de datos (maneja transacciones)
    public int crearPedido(int idCliente, List<DetallePedido> detalles, double total, int puntos) {
        // Sentencia SQL para insertar la cabecera del pedido con estado inicial PENDIENTE
        String sqlPedido = "INSERT INTO Pedidos (id_cliente, estado, total, puntos_generados) VALUES (?, 'PENDIENTE', ?, ?)";
        // Sentencia SQL para insertar cada línea de detalle del pedido
        String sqlDetalle = "INSERT INTO Detalle_Pedidos (id_pedido, id_producto, id_menu, cantidad, precio_unitario, subtotal, puntos) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection cn = Conexion.getConexion(); // Obtiene la conexión activa a la base de datos
        if (cn == null) {
            System.err.println("No se pudo obtener conexión a Oracle.");
            return -1; // Retorna -1 si no hay conexión
        }
        int idPedidoGenerado = -1; // Variable para almacenar el ID autogenerado del pedido
        try {
            cn.setAutoCommit(false); // Desactiva el autocommit para iniciar una transacción manual
            // Inserta el pedido principal solicitando que devuelva la llave generada (id_pedido)
            try (PreparedStatement ps = cn.prepareStatement(sqlPedido, new String[]{"id_pedido"})) {
                ps.setInt(1, idCliente);
                ps.setDouble(2, total);
                ps.setInt(3, puntos);
                ps.executeUpdate();
                // Recupera el ID asignado por la base de datos al nuevo pedido
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        idPedidoGenerado = rs.getInt(1);
                    }
                }
            }
            // Inserta en lote (Batch) cada uno de los detalles o productos que componen el pedido
            try (PreparedStatement ps = cn.prepareStatement(sqlDetalle)) {
                for (DetallePedido d : detalles) {
                    ps.setInt(1, idPedidoGenerado); // Asocia el detalle al ID del pedido recién creado
                    if (d.getIdMenu() > 0) { // Valida si el detalle corresponde a un menú o a un producto individual
                        ps.setNull(2, Types.INTEGER); // Si es menú, el producto es nulo
                        ps.setInt(3, d.getIdMenu()); // Asigna el ID del menú
                    } else {
                        ps.setInt(2, d.getIdProducto()); // Asigna el ID del producto individual
                        ps.setNull(3, Types.INTEGER); // Si es producto, el menú es nulo
                    }
                    ps.setInt(4, d.getCantidad());
                    ps.setDouble(5, d.getPrecioUnitario());
                    ps.setDouble(6, d.getSubtotal());
                    ps.setInt(7, d.getPuntos());
                    ps.addBatch(); // Agrega la instrucción al lote
                }
                ps.executeBatch(); // Ejecuta todas las inserciones de detalle juntas
            }

            cn.commit(); // Confirma la transacción completa si todo fue exitoso
            return idPedidoGenerado; // Retorna el ID del pedido creado
        } catch (SQLException e) {
            try {
                cn.rollback(); // Revierte todos los cambios si ocurre algún error
            } catch (SQLException ex) {
                System.err.println("Error en rollback: " + ex.getMessage());
            }
            System.err.println("Error al crear pedido: " + e.getMessage());
            return -1;
        } finally {
            try {
                cn.setAutoCommit(true); // Restaura el autocommit por defecto
                cn.close(); // Cierra la conexión a la base de datos
            } catch (SQLException ex) {
                System.err.println("Error al cerrar la conexión: " + ex.getMessage());
            }
        }
    }
    // Método complejo para cobrar un pedido: descuenta stock, registra movimiento de inventario, registra el pago, cambia estado y acumula puntos al cliente
    public boolean cobrarPedido(int idPedido, Pago pago) {
        // Consultas e instrucciones SQL necesarias para el proceso de cobro
        String sqlPedido = "SELECT id_cliente, estado, puntos_generados FROM Pedidos WHERE id_pedido = ?";
        String sqlDetalle = "SELECT id_producto, id_menu, cantidad FROM Detalle_Pedidos WHERE id_pedido = ?";
        String sqlComp = "SELECT id_producto, cantidad FROM Menu_Composicion WHERE id_menu = ?";
        String sqlStock = "UPDATE Productos SET existencia = existencia - ? WHERE id_producto = ? AND existencia >= ?";
        String sqlMovInv = "INSERT INTO Movimientos_Inventario (id_producto, tipo, cantidad, referencia) VALUES (?, 'SALIDA', ?, ?)";
        String sqlPago = "INSERT INTO Pagos (id_pedido, tipo, monto, recibido, cambio, referencia) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlEstado = "UPDATE Pedidos SET estado = 'PAGADO' WHERE id_pedido = ?";
        String sqlPuntos = "UPDATE Clientes SET saldo_puntos = saldo_puntos + ? WHERE id_cliente = ?";
        String sqlMovPts = "INSERT INTO Movimientos_Puntos (id_cliente, tipo, puntos, referencia) VALUES (?, 'ACUMULACION', ?, ?)";

        Connection cn = Conexion.getConexion(); // Obtiene la conexión a la base de datos
        if (cn == null) {
            System.err.println("No se pudo obtener conexión a Oracle.");
            return false;
        }

        try {
            cn.setAutoCommit(false); // Inicia la transacción manual

            int idCliente;
            String estado;
            int puntosGenerados;
            // 1. Obtiene la información general del pedido para validarlo
            try (PreparedStatement ps = cn.prepareStatement(sqlPedido)) {
                ps.setInt(1, idPedido);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("El pedido no existe.");
                    }
                    idCliente = rs.getInt("id_cliente");
                    estado = rs.getString("estado");
                    puntosGenerados = rs.getInt("puntos_generados");
                }
            }
            // 2. Valida que el pedido se encuentre en estado PENDIENTE para poder cobrarlo
            if (!"PENDIENTE".equals(estado)) {
                throw new SQLException("Solo se pueden cobrar pedidos PENDIENTES. Estado actual: " + estado);
            }
            // 3. Procesa los detalles del pedido para descontar inventario (tanto productos individuales como menús compuestos)
            try (PreparedStatement psDetalle = cn.prepareStatement(sqlDetalle);
                 PreparedStatement psComp = cn.prepareStatement(sqlComp);
                 PreparedStatement psStock = cn.prepareStatement(sqlStock);
                 PreparedStatement psMovInv = cn.prepareStatement(sqlMovInv)) {

                psDetalle.setInt(1, idPedido);
                try (ResultSet rs = psDetalle.executeQuery()) {
                    while (rs.next()) {
                        int idProducto = rs.getInt("id_producto");
                        int idMenu = rs.getInt("id_menu");
                        int cantidad = rs.getInt("cantidad");
                        // Si el detalle es un menú, consulta su composición para descontar cada producto que lo forma
                        if (idMenu > 0) {
                            psComp.setInt(1, idMenu);
                            try (ResultSet rc = psComp.executeQuery()) {
                                while (rc.next()) {
                                    int compProducto = rc.getInt("id_producto");
                                    int compCantidad = rc.getInt("cantidad");
                                    // Descuenta stock y registra la salida del componente multiplicando por la cantidad del menú
                                    descontarStock(psStock, psMovInv, compProducto, cantidad * compCantidad, idPedido);
                                }
                            }
                        } else { // Si es un producto individual, descuenta su stock directamente
                            descontarStock(psStock, psMovInv, idProducto, cantidad, idPedido);
                        }
                    }
                }
            }
            // 4. Registra el pago realizado asociado al pedido
            try (PreparedStatement ps = cn.prepareStatement(sqlPago)) {
                ps.setInt(1, idPedido);
                ps.setString(2, pago.getTipo());
                ps.setDouble(3, pago.getMonto());
                ps.setDouble(4, pago.getRecibido());
                ps.setDouble(5, pago.getCambio());
                ps.setString(6, pago.getReferencia());
                ps.executeUpdate();
            }
            // 5. Actualiza el estado del pedido a 'PAGADO'
            try (PreparedStatement ps = cn.prepareStatement(sqlEstado)) {
                ps.setInt(1, idPedido);
                ps.executeUpdate();
            }
            // 6. Si el pedido generó puntos de fidelidad, actualiza el saldo del cliente y registra el movimiento de puntos
            if (puntosGenerados > 0) {
                try (PreparedStatement ps = cn.prepareStatement(sqlPuntos)) {
                    ps.setInt(1, puntosGenerados);
                    ps.setInt(2, idCliente);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = cn.prepareStatement(sqlMovPts)) {
                    ps.setInt(1, idCliente);
                    ps.setInt(2, puntosGenerados);
                    ps.setString(3, "pedido " + idPedido);
                    ps.executeUpdate();
                }
            }

            cn.commit(); // Confirma todas las operaciones de la transacción
            return true; // Retorna true indicando que el cobro fue exitoso
        } catch (SQLException e) {
            try {
                cn.rollback(); // Revierte todos los cambios si ocurre algún error en el proceso
            } catch (SQLException ex) {
                System.err.println("Error en rollback: " + ex.getMessage());
            }
            System.err.println("Error al cobrar pedido: " + e.getMessage());
            return false;
        } finally {
            try {
                cn.setAutoCommit(true); // Restaura el autocommit
                cn.close(); // Cierra la conexión
            } catch (SQLException ex) {
                System.err.println("Error al cerrar la conexión: " + ex.getMessage());
            }
        }
    }
    // Método para anular un pedido siempre y cuando su estado actual sea PENDIENTE
    public boolean anularPedido(int idPedido) {
        String sql = "UPDATE Pedidos SET estado = 'ANULADO' WHERE id_pedido = ? AND estado = 'PENDIENTE'";
        Connection cn = Conexion.getConexion();
        if (cn == null) {
            System.err.println("No se pudo obtener conexión a Oracle.");
            return false;
        }
        try (cn; PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            return ps.executeUpdate() > 0; // Retorna true si se actualizó el estado correctamente
        } catch (SQLException e) {
            System.err.println("Error al anular pedido: " + e.getMessage());
            return false;
        }
    }
    // Método auxiliar privado para descontar stock de un producto y registrar su respectivo movimiento de inventario (salida)
    private void descontarStock(PreparedStatement psStock, PreparedStatement psMovInv,
                                int idProducto, int cantidad, int idPedido) throws SQLException {
        psStock.setInt(1, cantidad);
        psStock.setInt(2, idProducto);
        psStock.setInt(3, cantidad); // Valida que la existencia actual sea mayor o igual a la cantidad a descontar
        // Si no se actualizó ninguna fila, significa que no hay suficiente stock
        if (psStock.executeUpdate() == 0) {
            throw new SQLException("Stock insuficiente para el producto " + idProducto);
        }
        // Registra la salida en la tabla de movimientos de inventario
        psMovInv.setInt(1, idProducto);
        psMovInv.setInt(2, cantidad);
        psMovInv.setString(3, "pedido " + idPedido);
        psMovInv.executeUpdate();
    }
}
