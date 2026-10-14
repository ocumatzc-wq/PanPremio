package dao;

import proyectooracle.Conexion;
import modelo.Pago;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
// Clase DAO para gestionar las operaciones de base de datos relacionadas con los pagos
public class PagoDAO {
    // Método para registrar un nuevo pago en la base de datos utilizando los datos de un objeto Pago
    public boolean registrarPago(Pago pago) { // Sentencia SQL para insertar un nuevo registro en la tabla Pagos
        String sql = "INSERT INTO Pagos (id_pedido, tipo, monto, recibido, cambio, referencia) VALUES (?, ?, ?, ?, ?, ?)";
        Connection cn = Conexion.getConexion(); // Obtiene la conexión activa a la base de datos Oracle
        if (cn == null) {
            System.err.println("No se pudo obtener conexión a Oracle.");
            return false; // Retorna falso si no se pudo establecer la conexión
        }
        // Bloque try-with-resources para asegurar el cierre automático de la conexión y el PreparedStatement
        try (cn; PreparedStatement ps = cn.prepareStatement(sql)) { // Asigna los valores del objeto Pago a cada parámetro de la consulta SQL
            ps.setInt(1, pago.getIdPedido());
            ps.setString(2, pago.getTipo());
            ps.setDouble(3, pago.getMonto());
            ps.setDouble(4, pago.getRecibido());
            ps.setDouble(5, pago.getCambio());
            ps.setString(6, pago.getReferencia());
            return ps.executeUpdate() > 0; // Ejecuta la consulta de inserción y retorna true si se insertó al menos una fila correctamente
        } catch (SQLException e) { // Captura cualquier error de SQL que ocurra durante el registro
            System.err.println("Error al registrar pago: " + e.getMessage());
            return false; // Retorna falso indicando que la operación falló
        }
    }
}
