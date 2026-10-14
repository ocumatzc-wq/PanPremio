package dao;

import proyectooracle.Conexion;
import modelo.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
// Clase DAO para gestionar todas las operaciones de base de datos relacionadas con los productos
public class ProductoDAO {
    // Método para registrar o agregar un nuevo producto en la base de datos
    public boolean agregarProducto(Producto p) {
        String sql = "INSERT INTO Productos (codigo, categoria, nombre, precio, existencia, activo) VALUES (?, ?, ?, ?, ?, ?)";
        Connection cn = Conexion.getConexion(); // Obtiene la conexión activa a la base de datos
        if (cn == null) {
            System.err.println("No se pudo obtener conexión a Oracle.");
            return false; // Retorna falso si no hay conexión
        }
        // Bloque try-with-resources para asegurar el cierre automático de conexión y statement
        try (cn; PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getCategoria());
            ps.setString(3, p.getNombre());
            ps.setDouble(4, p.getPrecio());
            ps.setInt(5, p.getExistencia());
            ps.setInt(6, p.isActivo() ? 1 : 0); // Convierte el booleano a entero (1 o 0) para la base de datos
            return ps.executeUpdate() > 0; // Retorna true si se insertó al menos una fila correctamente
        } catch (SQLException e) {
            System.err.println("Error al guardar producto: " + e.getMessage());
            return false;
        }
    }
    // Método sobrecargado para obtener todos los productos (activos e inactivos)
    public List<Producto> obtenerProductos() {
        return obtenerProductos(false);
    }
    // Método para obtener únicamente los productos que se encuentran activos en el sistema
    public List<Producto> obtenerProductosActivos() {
        return obtenerProductos(true);
    }
    // Método privado auxiliar para consultar productos aplicando opcionalmente un filtro por estado activo
    private List<Producto> obtenerProductos(boolean soloActivos) {
        List<Producto> lista = new ArrayList<>(); // Lista para almacenar los productos encontrados
        // Construye la sentencia SQL agregando la condición WHERE si soloActivos es verdadero
        String sql = "SELECT id_producto, codigo, categoria, nombre, precio, existencia, activo FROM Productos"
                + (soloActivos ? " WHERE activo = 1" : "") + " ORDER BY id_producto";
        Connection cn = Conexion.getConexion();
        if (cn == null) {
            System.err.println("No se pudo obtener conexión a Oracle.");
            return lista;
        }
        try (cn; PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs)); // Mapea cada registro del ResultSet a un objeto Producto y lo añade a la lista
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar productos: " + e.getMessage());
        }
        return lista;
    }
    // Método para buscar y retornar un producto específico según su ID
    public Producto obtenerProductoPorId(int idProducto) {
        String sql = "SELECT id_producto, codigo, categoria, nombre, precio, existencia, activo FROM Productos WHERE id_producto = ?";
        Connection cn = Conexion.getConexion();
        if (cn == null) {
            System.err.println("No se pudo obtener conexión a Oracle.");
            return null;
        }
        try (cn; PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs); // Retorna el objeto Producto mapeado si se encuentra el registro
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar producto: " + e.getMessage());
        }
        return null; // Retorna null si no se encuentra o ocurre un error
    }
    // Método para actualizar los datos generales de un producto existente
    public boolean actualizarProducto(Producto p) {
        String sql = "UPDATE Productos SET codigo = ?, categoria = ?, nombre = ?, precio = ?, existencia = ?, activo = ? WHERE id_producto = ?";
        Connection cn = Conexion.getConexion();
        if (cn == null) {
            System.err.println("No se pudo obtener conexión a Oracle.");
            return false;
        }
        try (cn; PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getCategoria());
            ps.setString(3, p.getNombre());
            ps.setDouble(4, p.getPrecio());
            ps.setInt(5, p.getExistencia());
            ps.setInt(6, p.isActivo() ? 1 : 0);
            ps.setInt(7, p.getIdProducto());
            return ps.executeUpdate() > 0; // Retorna true si se actualizó correctamente
        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }
    // Método para cambiar de forma rápida el estado (activo/inactivo) de un producto
    public boolean cambiarEstadoProducto(int idProducto, boolean activo) {
        String sql = "UPDATE Productos SET activo = ? WHERE id_producto = ?";
        Connection cn = Conexion.getConexion();
        if (cn == null) {
            System.err.println("No se pudo obtener conexión a Oracle.");
            return false;
        }
        try (cn; PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, activo ? 1 : 0);
            ps.setInt(2, idProducto);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al cambiar estado del producto: " + e.getMessage());
            return false;
        }
    }
    // Método transaccional para abastecer inventario: aumenta el stock del producto y registra un movimiento de entrada
    public boolean abastecerProducto(int idProducto, int cantidad, String referencia) {
        if (cantidad <= 0) { // Valida que la cantidad a ingresar sea estrictamente mayor a cero
            System.err.println("La cantidad a abastecer debe ser mayor a 0.");
            return false;
        }
        String sqlStock = "UPDATE Productos SET existencia = existencia + ? WHERE id_producto = ?";
        String sqlMov = "INSERT INTO Movimientos_Inventario (id_producto, tipo, cantidad, referencia) VALUES (?, 'ENTRADA', ?, ?)";
        Connection cn = Conexion.getConexion();
        if (cn == null) {
            System.err.println("No se pudo obtener conexión a Oracle.");
            return false;
        }
        try {
            cn.setAutoCommit(false); // Inicia transacción manual
            // 1. Actualiza incrementando el stock del producto
            try (PreparedStatement ps = cn.prepareStatement(sqlStock)) {
                ps.setInt(1, cantidad);
                ps.setInt(2, idProducto);
                if (ps.executeUpdate() == 0) {
                    throw new SQLException("El producto no existe."); // Lanza excepción si el ID no es válido
                }
            }
            // 2. Registra el movimiento de inventario de tipo 'ENTRADA'
            try (PreparedStatement ps = cn.prepareStatement(sqlMov)) {
                ps.setInt(1, idProducto);
                ps.setInt(2, cantidad);
                ps.setString(3, referencia);
                ps.executeUpdate();
            }
            cn.commit(); // Confirma la transacción si todo salió bien
            return true;
        } catch (SQLException e) {
            try {
                cn.rollback(); // Revierte los cambios si ocurre algún error
            } catch (SQLException ex) {
                System.err.println("Error en rollback: " + ex.getMessage());
            }
            System.err.println("Error al abastecer producto: " + e.getMessage());
            return false;
        } finally {
            try {
                cn.setAutoCommit(true); // Restaura el autocommit
                cn.close(); // Cierra la conexión
            } catch (SQLException ex) {
                System.err.println("Error al cerrar la conexión: " + ex.getMessage());
            }
        }
    }
    // Método auxiliar privado para mapear los resultados de un ResultSet a una instancia de la clase Producto
    private Producto mapear(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setIdProducto(rs.getInt("id_producto"));
        p.setCodigo(rs.getString("codigo"));
        p.setCategoria(rs.getString("categoria"));
        p.setNombre(rs.getString("nombre"));
        p.setPrecio(rs.getDouble("precio"));
        p.setExistencia(rs.getInt("existencia"));
        p.setActivo(rs.getInt("activo") == 1);
        return p;
    }
}
