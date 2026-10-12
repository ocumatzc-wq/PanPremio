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

public class PedidoDAO {

    public int crearPedido(int idCliente, List<DetallePedido> detalles, double total, int puntos) {
        String sqlPedido = "INSERT INTO Pedidos (id_cliente, estado, total, puntos_generados) VALUES (?, 'PENDIENTE', ?, ?)";
        String sqlDetalle = "INSERT INTO Detalle_Pedidos (id_pedido, id_producto, id_menu, cantidad, precio_unitario, subtotal, puntos) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection cn = Conexion.getConexion();
        if (cn == null) {
            System.err.println("No se pudo obtener conexión a Oracle.");
            return -1;
        }
        int idPedidoGenerado = -1;
        try {
            cn.setAutoCommit(false);

            try (PreparedStatement ps = cn.prepareStatement(sqlPedido, new String[]{"id_pedido"})) {
                ps.setInt(1, idCliente);
                ps.setDouble(2, total);
                ps.setInt(3, puntos);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        idPedidoGenerado = rs.getInt(1);
                    }
                }
            }

            try (PreparedStatement ps = cn.prepareStatement(sqlDetalle)) {
                for (DetallePedido d : detalles) {
                    ps.setInt(1, idPedidoGenerado);
                    if (d.getIdMenu() > 0) {
                        ps.setNull(2, Types.INTEGER);
                        ps.setInt(3, d.getIdMenu());
                    } else {
                        ps.setInt(2, d.getIdProducto());
                        ps.setNull(3, Types.INTEGER);
                    }
                    ps.setInt(4, d.getCantidad());
                    ps.setDouble(5, d.getPrecioUnitario());
                    ps.setDouble(6, d.getSubtotal());
                    ps.setInt(7, d.getPuntos());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            cn.commit();
            return idPedidoGenerado;
        } catch (SQLException e) {
            try {
                cn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error en rollback: " + ex.getMessage());
            }
            System.err.println("Error al crear pedido: " + e.getMessage());
            return -1;
        } finally {
            try {
                cn.setAutoCommit(true);
                cn.close();
            } catch (SQLException ex) {
                System.err.println("Error al cerrar la conexión: " + ex.getMessage());
            }
        }
    }

    public boolean cobrarPedido(int idPedido, Pago pago) {
        String sqlPedido = "SELECT id_cliente, estado, puntos_generados FROM Pedidos WHERE id_pedido = ?";
        String sqlDetalle = "SELECT id_producto, id_menu, cantidad FROM Detalle_Pedidos WHERE id_pedido = ?";
        String sqlComp = "SELECT id_producto, cantidad FROM Menu_Composicion WHERE id_menu = ?";
        String sqlStock = "UPDATE Productos SET existencia = existencia - ? WHERE id_producto = ? AND existencia >= ?";
        String sqlMovInv = "INSERT INTO Movimientos_Inventario (id_producto, tipo, cantidad, referencia) VALUES (?, 'SALIDA', ?, ?)";
        String sqlPago = "INSERT INTO Pagos (id_pedido, tipo, monto, recibido, cambio, referencia) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlEstado = "UPDATE Pedidos SET estado = 'PAGADO' WHERE id_pedido = ?";
        String sqlPuntos = "UPDATE Clientes SET saldo_puntos = saldo_puntos + ? WHERE id_cliente = ?";
        String sqlMovPts = "INSERT INTO Movimientos_Puntos (id_cliente, tipo, puntos, referencia) VALUES (?, 'ACUMULACION', ?, ?)";

        Connection cn = Conexion.getConexion();
        if (cn == null) {
            System.err.println("No se pudo obtener conexión a Oracle.");
            return false;
        }

        try {
            cn.setAutoCommit(false);

            int idCliente;
            String estado;
            int puntosGenerados;
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

            if (!"PENDIENTE".equals(estado)) {
                throw new SQLException("Solo se pueden cobrar pedidos PENDIENTES. Estado actual: " + estado);
            }

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

                        if (idMenu > 0) {
                            psComp.setInt(1, idMenu);
                            try (ResultSet rc = psComp.executeQuery()) {
                                while (rc.next()) {
                                    int compProducto = rc.getInt("id_producto");
                                    int compCantidad = rc.getInt("cantidad");
                                    descontarStock(psStock, psMovInv, compProducto, cantidad * compCantidad, idPedido);
                                }
                            }
                        } else {
                            descontarStock(psStock, psMovInv, idProducto, cantidad, idPedido);
                        }
                    }
                }
            }

            try (PreparedStatement ps = cn.prepareStatement(sqlPago)) {
                ps.setInt(1, idPedido);
                ps.setString(2, pago.getTipo());
                ps.setDouble(3, pago.getMonto());
                ps.setDouble(4, pago.getRecibido());
                ps.setDouble(5, pago.getCambio());
                ps.setString(6, pago.getReferencia());
                ps.executeUpdate();
            }

            try (PreparedStatement ps = cn.prepareStatement(sqlEstado)) {
                ps.setInt(1, idPedido);
                ps.executeUpdate();
            }

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

            cn.commit();
            return true;
        } catch (SQLException e) {
            try {
                cn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error en rollback: " + ex.getMessage());
            }
            System.err.println("Error al cobrar pedido: " + e.getMessage());
            return false;
        } finally {
            try {
                cn.setAutoCommit(true);
                cn.close();
            } catch (SQLException ex) {
                System.err.println("Error al cerrar la conexión: " + ex.getMessage());
            }
        }
    }

    public boolean anularPedido(int idPedido) {
        String sql = "UPDATE Pedidos SET estado = 'ANULADO' WHERE id_pedido = ? AND estado = 'PENDIENTE'";
        Connection cn = Conexion.getConexion();
        if (cn == null) {
            System.err.println("No se pudo obtener conexión a Oracle.");
            return false;
        }
        try (cn; PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al anular pedido: " + e.getMessage());
            return false;
        }
    }

    private void descontarStock(PreparedStatement psStock, PreparedStatement psMovInv,
                                int idProducto, int cantidad, int idPedido) throws SQLException {
        psStock.setInt(1, cantidad);
        psStock.setInt(2, idProducto);
        psStock.setInt(3, cantidad);
        if (psStock.executeUpdate() == 0) {
            throw new SQLException("Stock insuficiente para el producto " + idProducto);
        }
        psMovInv.setInt(1, idProducto);
        psMovInv.setInt(2, cantidad);
        psMovInv.setString(3, "pedido " + idPedido);
        psMovInv.executeUpdate();
    }
}
