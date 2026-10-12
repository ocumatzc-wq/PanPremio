package dao;

import proyectooracle.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CanjeDAO {

    public int realizarCanje(int idCliente, int idRecompensa, int puntosRequeridos,
                             int idProductoEntrega, int idMenuEntrega) {
        String sqlSaldo = "UPDATE Clientes SET saldo_puntos = saldo_puntos - ? WHERE id_cliente = ? AND saldo_puntos >= ?";
        String sqlCanje = "INSERT INTO Canjes (id_cliente, id_recompensa, puntos_usados) VALUES (?, ?, ?)";
        String sqlMovPts = "INSERT INTO Movimientos_Puntos (id_cliente, tipo, puntos, referencia) VALUES (?, 'CANJE', ?, ?)";
        String sqlComp = "SELECT id_producto, cantidad FROM Menu_Composicion WHERE id_menu = ?";
        String sqlStock = "UPDATE Productos SET existencia = existencia - ? WHERE id_producto = ? AND existencia >= ?";
        String sqlMovInv = "INSERT INTO Movimientos_Inventario (id_producto, tipo, cantidad, referencia) VALUES (?, 'SALIDA', ?, ?)";

        Connection cn = Conexion.getConexion();
        if (cn == null) {
            System.err.println("No se pudo obtener conexión a Oracle.");
            return -1;
        }

        int idCanje = -1;
        try {
            cn.setAutoCommit(false);

            try (PreparedStatement ps = cn.prepareStatement(sqlSaldo)) {
                ps.setInt(1, puntosRequeridos);
                ps.setInt(2, idCliente);
                ps.setInt(3, puntosRequeridos);
                if (ps.executeUpdate() == 0) {
                    throw new SQLException("El cliente no tiene puntos suficientes.");
                }
            }

            try (PreparedStatement ps = cn.prepareStatement(sqlCanje, new String[]{"id_canje"})) {
                ps.setInt(1, idCliente);
                ps.setInt(2, idRecompensa);
                ps.setInt(3, puntosRequeridos);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        idCanje = rs.getInt(1);
                    }
                }
            }

            try (PreparedStatement ps = cn.prepareStatement(sqlMovPts)) {
                ps.setInt(1, idCliente);
                ps.setInt(2, -puntosRequeridos);
                ps.setString(3, "canje " + idCanje);
                ps.executeUpdate();
            }

            try (PreparedStatement psStock = cn.prepareStatement(sqlStock);
                 PreparedStatement psMovInv = cn.prepareStatement(sqlMovInv);
                 PreparedStatement psComp = cn.prepareStatement(sqlComp)) {

                if (idMenuEntrega > 0) {
                    psComp.setInt(1, idMenuEntrega);
                    try (ResultSet rc = psComp.executeQuery()) {
                        while (rc.next()) {
                            int compProducto = rc.getInt("id_producto");
                            int compCantidad = rc.getInt("cantidad");
                            descontar(psStock, psMovInv, compProducto, compCantidad, idCanje);
                        }
                    }
                } else if (idProductoEntrega > 0) {
                    descontar(psStock, psMovInv, idProductoEntrega, 1, idCanje);
                }
            }

            cn.commit();
            return idCanje;
        } catch (SQLException e) {
            try {
                cn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error en rollback: " + ex.getMessage());
            }
            System.err.println("Error al realizar canje: " + e.getMessage());
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

    private void descontar(PreparedStatement psStock, PreparedStatement psMovInv,
                           int idProducto, int cantidad, int idCanje) throws SQLException {
        psStock.setInt(1, cantidad);
        psStock.setInt(2, idProducto);
        psStock.setInt(3, cantidad);
        if (psStock.executeUpdate() == 0) {
            throw new SQLException("Stock insuficiente para el producto " + idProducto);
        }
        psMovInv.setInt(1, idProducto);
        psMovInv.setInt(2, cantidad);
        psMovInv.setString(3, "canje " + idCanje);
        psMovInv.executeUpdate();
    }
}
