package dao;

import proyectooracle.Conexion;
import modelo.MovimientoInventario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MovimientoInventarioDAO {

    public List<MovimientoInventario> listar() {
        List<MovimientoInventario> lista = new ArrayList<>();
        String sql = "SELECT id_movimiento, id_producto, tipo, cantidad, fecha, referencia "
                + "FROM Movimientos_Inventario ORDER BY fecha";
        Connection cn = Conexion.getConexion();
        if (cn == null) {
            System.err.println("No se pudo obtener conexión a Oracle.");
            return lista;
        }
        try (cn; PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MovimientoInventario m = new MovimientoInventario();
                m.setIdMovimiento(rs.getInt("id_movimiento"));
                m.setIdProducto(rs.getInt("id_producto"));
                m.setTipo(rs.getString("tipo"));
                m.setCantidad(rs.getInt("cantidad"));
                m.setFecha(rs.getDate("fecha"));
                m.setReferencia(rs.getString("referencia"));
                lista.add(m);
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar movimientos de inventario: " + e.getMessage());
        }
        return lista;
    }
}
