package dao;

import proyectooracle.Conexion;
import modelo.Recompensa;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona las consultas de recompensas almacenadas en la base de datos.
 */
public class RecompensaDAO {
    /**
     * Obtiene todas las recompensas activas ordenadas por los puntos requeridos.
     * @return Lista de recompensas activas; vacía si ocurre un error.
     */
    public List<Recompensa> listarRecompensasActivas() {
        List<Recompensa> lista = new ArrayList<>();
        String sql = "SELECT id_recompensa, nombre, puntos_requeridos, id_producto, id_menu, activo FROM Recompensas WHERE activo = 1 ORDER BY puntos_requeridos"; // Consulta las recompensas activas ordenadas por puntos.
        Connection cn = Conexion.getConexion(); // Obtiene la conexión con la base de datos Oracle.
        if (cn == null) {
            System.err.println("No se pudo obtener conexión a Oracle.");
            return lista;
        }
        try (cn; PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) { // Recorre los resultados y convierte cada fila en un objeto Recompensa.
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) { // Informa si ocurre un error durante la consulta.
            System.err.println("Error al consultar recompensas: " + e.getMessage());
        }
        return lista;
    }
    /**
     * Busca una recompensa mediante su identificador.
     * @param idRecompensa Identificador de la recompensa que se desea buscar.
     * @return La recompensa encontrada o null si no existe o ocurre un error.
     */
    public Recompensa obtenerRecompensaPorId(int idRecompensa) { // Consulta una recompensa específica usando un parámetro.
        String sql = "SELECT id_recompensa, nombre, puntos_requeridos, id_producto, id_menu, activo FROM Recompensas WHERE id_recompensa = ?";
        Connection cn = Conexion.getConexion();
        if (cn == null) { // Comprueba que se haya obtenido una conexión válida.
            System.err.println("No se pudo obtener conexión a Oracle.");
            return null;
        }
        try (cn; PreparedStatement ps = cn.prepareStatement(sql)) { // Prepara la consulta y asigna el identificador recibido.
            ps.setInt(1, idRecompensa);
            try (ResultSet rs = ps.executeQuery()) { // Ejecuta la consulta y revisa si encontró una fila.
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) { // Informa si ocurre un error durante la búsqueda.
            System.err.println("Error al consultar recompensa: " + e.getMessage());
        }
        return null;
    }
    /**
     * Convierte una fila del resultado SQL en un objeto Recompensa.
     * @param rs Resultado de la consulta SQL.
     * @return Objeto Recompensa con los datos obtenidos.
     * @throws SQLException Si ocurre un error al leer los datos.
     */
    private Recompensa mapear(ResultSet rs) throws SQLException { // Asigna al objeto los datos básicos de la recompensa.
        Recompensa r = new Recompensa();
        r.setIdRecompensa(rs.getInt("id_recompensa"));
        r.setNombre(rs.getString("nombre"));
        r.setPuntosRequeridos(rs.getInt("puntos_requeridos"));
        // Convierte los identificadores nulos en cero.
        int idProducto = rs.getInt("id_producto");
        r.setIdProducto(rs.wasNull() ? 0 : idProducto);
        int idMenu = rs.getInt("id_menu");
        r.setIdMenu(rs.wasNull() ? 0 : idMenu);
        // Convierte el valor numérico del campo activo en un booleano.
        r.setActivo(rs.getInt("activo") == 1);
        return r;
    }
}
