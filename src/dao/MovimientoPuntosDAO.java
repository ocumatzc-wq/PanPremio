package dao;

import proyectooracle.Conexion;
import modelo.MovimientoPuntos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class MovimientoPuntosDAO {
    public List<MovimientoPuntos> listarPorCliente(int idCliente) {
        List<MovimientoPuntos> lista = new ArrayList<>();
        String sql = "SELECT id_movimiento, id_cliente, tipo, puntos, fecha_movimiento, referencia "
                + "FROM Movimientos_Puntos WHERE id_cliente = ? ORDER BY fecha_movimiento";
        Connection cn = Conexion.getConexion();
        if (cn == null) {
            System.err.println("No se pudo obtener conexión a Oracle.");
            return lista;
        }
        try (cn; PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MovimientoPuntos m = new MovimientoPuntos();
                    m.setIdMovimiento(rs.getInt("id_movimiento"));
                    m.setIdCliente(rs.getInt("id_cliente"));
                    m.setTipo(rs.getString("tipo"));
                    m.setPuntos(rs.getInt("puntos"));
                    m.setFechaMovimiento(rs.getDate("fecha_movimiento"));
                    m.setReferencia(rs.getString("referencia"));
                    lista.add(m);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar movimientos de puntos: " + e.getMessage());
        }
        return lista;
    }
    public boolean registrar(int idCliente, String tipo, int puntos, String referencia) {
        String sql = "INSERT INTO Movimientos_Puntos (id_cliente, tipo, puntos, referencia) VALUES (?, ?, ?, ?)";
        Connection cn = Conexion.getConexion();
        if (cn == null) {
            System.err.println("No se pudo obtener conexión a Oracle.");
            return false;
        }
        try (cn; PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ps.setString(2, tipo);
            ps.setInt(3, puntos);
            ps.setString(4, referencia);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar movimiento de puntos: " + e.getMessage());
            return false;
        }
    }
}
