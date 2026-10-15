package dao;

import proyectooracle.Conexion;
import modelo.Recompensa;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RecompensaDAO {
    public List<Recompensa> listarRecompensasActivas() {
        List<Recompensa> lista = new ArrayList<>();
        String sql = "SELECT id_recompensa, nombre, puntos_requeridos, id_producto, id_menu, activo FROM Recompensas WHERE activo = 1 ORDER BY puntos_requeridos";
        Connection cn = Conexion.getConexion();
        if (cn == null) {
            System.err.println("No se pudo obtener conexión a Oracle.");
            return lista;
        }
        try (cn; PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar recompensas: " + e.getMessage());
        }
        return lista;
    }
    public Recompensa obtenerRecompensaPorId(int idRecompensa) {
        String sql = "SELECT id_recompensa, nombre, puntos_requeridos, id_producto, id_menu, activo FROM Recompensas WHERE id_recompensa = ?";
        Connection cn = Conexion.getConexion();
        if (cn == null) {
            System.err.println("No se pudo obtener conexión a Oracle.");
            return null;
        }
        try (cn; PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idRecompensa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar recompensa: " + e.getMessage());
        }
        return null;
    }
    private Recompensa mapear(ResultSet rs) throws SQLException {
        Recompensa r = new Recompensa();
        r.setIdRecompensa(rs.getInt("id_recompensa"));
        r.setNombre(rs.getString("nombre"));
        r.setPuntosRequeridos(rs.getInt("puntos_requeridos"));
        int idProducto = rs.getInt("id_producto");
        r.setIdProducto(rs.wasNull() ? 0 : idProducto);
        int idMenu = rs.getInt("id_menu");
        r.setIdMenu(rs.wasNull() ? 0 : idMenu);
        r.setActivo(rs.getInt("activo") == 1);
        return r;
    }
}
