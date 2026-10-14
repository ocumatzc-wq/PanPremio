package dao;

import proyectooracle.Conexion;
import modelo.MovimientoInventario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
// Clase DAO para gestionar las consultas y operaciones de base de datos de los movimientos de inventario
public class MovimientoInventarioDAO {
    // Método que recupera y lista todos los movimientos de inventario registrados en la base de datos
    public List<MovimientoInventario> listar() {
        List<MovimientoInventario> lista = new ArrayList<>();
        // Consulta SQL para seleccionar todos los campos de la tabla ordenados por fecha
        String sql = "SELECT id_movimiento, id_producto, tipo, cantidad, fecha, referencia "
                + "FROM Movimientos_Inventario ORDER BY fecha";
        Connection cn = Conexion.getConexion(); // Obtiene la conexión activa a la base de datos
        if (cn == null) {
            System.err.println("No se pudo obtener conexión a Oracle.");
            return lista; // Retorna la lista vacía si la conexión falla
        }
        // Bloque try-with-resources para asegurar el cierre automático de la conexión, statement y resultset
        try (cn; PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) { // Recorre cada registro devuelto por la consulta SQL
            while (rs.next()) {
                MovimientoInventario m = new MovimientoInventario(); // Crea una nueva instancia para cada movimiento
                // Asigna los valores obtenidos de la base de datos a los atributos del objeto
                m.setIdMovimiento(rs.getInt("id_movimiento"));
                m.setIdProducto(rs.getInt("id_producto"));
                m.setTipo(rs.getString("tipo"));
                m.setCantidad(rs.getInt("cantidad"));
                m.setFecha(rs.getDate("fecha"));
                m.setReferencia(rs.getString("referencia"));
                lista.add(m); // Añade el objeto poblado a la lista final
            }
        } catch (SQLException e) { // Captura y muestra en consola cualquier error relacionado con SQL
            System.err.println("Error al consultar movimientos de inventario: " + e.getMessage());
        }
        return lista; // Retorna la lista con todos los movimientos obtenidos
    }
}
