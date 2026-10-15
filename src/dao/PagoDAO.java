package dao;

import proyectooracle.Conexion;
import modelo.Pago;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
public class PagoDAO {
    public boolean registrarPago(Pago pago) {
        String sql = "INSERT INTO Pagos (id_pedido, tipo, monto, recibido, cambio, referencia) VALUES (?, ?, ?, ?, ?, ?)";
        Connection cn = Conexion.getConexion();
        if (cn == null) {
            System.err.println("No se pudo obtener conexión a Oracle.");
            return false;
        }
        try (cn; PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, pago.getIdPedido());
            ps.setString(2, pago.getTipo());
            ps.setDouble(3, pago.getMonto());
            ps.setDouble(4, pago.getRecibido());
            ps.setDouble(5, pago.getCambio());
            ps.setString(6, pago.getReferencia());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar pago: " + e.getMessage());
            return false;
        }
    }
}
