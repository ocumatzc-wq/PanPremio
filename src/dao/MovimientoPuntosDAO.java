package dao;

import proyectooracle.Conexion;
import modelo.MovimientoPuntos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
// Clase DAO para gestionar las consultas y registros de los movimientos de puntos de fidelidad de los clientes
public class MovimientoPuntosDAO {
    // Método para listar todos los movimientos de puntos asociados a un cliente específico, ordenados por fecha
    public List<MovimientoPuntos> listarPorCliente(int idCliente) {
        List<MovimientoPuntos> lista = new ArrayList<>(); // Lista para almacenar los movimientos encontrados
        // Consulta SQL para obtener los datos de la tabla filtrando por el ID del cliente
        String sql = "SELECT id_movimiento, id_cliente, tipo, puntos, fecha_movimiento, referencia "
                + "FROM Movimientos_Puntos WHERE id_cliente = ? ORDER BY fecha_movimiento";
        Connection cn = Conexion.getConexion(); // Obtiene la conexión activa a la base de datos
        if (cn == null) {
            System.err.println("No se pudo obtener conexión a Oracle.");
            return lista; // Retorna la lista vacía si no hay conexión
        }
        // Bloque try-with-resources para asegurar el cierre automático de conexión y recursos
        try (cn; PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) { // Recorre cada registro obtenido de la base de datos
                while (rs.next()) {
                    MovimientoPuntos m = new MovimientoPuntos();
                    // Asigna los valores de la base de datos a los atributos del objeto
                    m.setIdMovimiento(rs.getInt("id_movimiento"));
                    m.setIdCliente(rs.getInt("id_cliente"));
                    m.setTipo(rs.getString("tipo"));
                    m.setPuntos(rs.getInt("puntos"));
                    m.setFechaMovimiento(rs.getDate("fecha_movimiento"));
                    m.setReferencia(rs.getString("referencia"));
                    lista.add(m); // Agrega el objeto poblado a la lista final
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar movimientos de puntos: " + e.getMessage());
        }
        return lista; // Retorna la lista con los movimientos del cliente
    }
    // Método para registrar un nuevo movimiento de puntos (acumulación o redención) en la base de datos
    public boolean registrar(int idCliente, String tipo, int puntos, String referencia) { // Consulta SQL para insertar un nuevo registro de movimiento de puntos
        String sql = "INSERT INTO Movimientos_Puntos (id_cliente, tipo, puntos, referencia) VALUES (?, ?, ?, ?)";
        Connection cn = Conexion.getConexion(); // Obtiene la conexión activa a la base de datos
        if (cn == null) {
            System.err.println("No se pudo obtener conexión a Oracle.");
            return false; // Retorna falso si falla la conexión
        }
        // Bloque try-with-resources para asegurar el cierre automático de conexión y statement
        try (cn; PreparedStatement ps = cn.prepareStatement(sql)) { // Asigna los valores correspondientes a cada parámetro de la consulta de inserción
            ps.setInt(1, idCliente);
            ps.setString(2, tipo);
            ps.setInt(3, puntos);
            ps.setString(4, referencia);
            return ps.executeUpdate() > 0; // Ejecuta la actualización y retorna true si se insertó al menos una fila correctamente
        } catch (SQLException e) {
            System.err.println("Error al registrar movimiento de puntos: " + e.getMessage());
            return false; // Retorna falso en caso de ocurrir un error SQL
        }
    }
}
