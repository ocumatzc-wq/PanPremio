package dao;

import proyectooracle.Conexion;
import modelo.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public boolean agregarProducto(Producto p) {
        String sql = "INSERT INTO Productos (codigo, categoria, nombre, precio, existencia, activo) VALUES (?, ?, ?, ?, ?, ?)";
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
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al guardar producto: " + e.getMessage());
            return false;
        }
    }

    public List<Producto> obtenerProductos() {
        return obtenerProductos(false);
    }

    public List<Producto> obtenerProductosActivos() {
        return obtenerProductos(true);
    }

    private List<Producto> obtenerProductos(boolean soloActivos) {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT id_producto, codigo, categoria, nombre, precio, existencia, activo FROM Productos"
                + (soloActivos ? " WHERE activo = 1" : "") + " ORDER BY id_producto";
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
            System.err.println("Error al consultar productos: " + e.getMessage());
        }
        return lista;
    }

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
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar producto: " + e.getMessage());
        }
        return null;
    }

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
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

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

    public boolean abastecerProducto(int idProducto, int cantidad, String referencia) {
        if (cantidad <= 0) {
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
            cn.setAutoCommit(false);
            try (PreparedStatement ps = cn.prepareStatement(sqlStock)) {
                ps.setInt(1, cantidad);
                ps.setInt(2, idProducto);
                if (ps.executeUpdate() == 0) {
                    throw new SQLException("El producto no existe.");
                }
            }
            try (PreparedStatement ps = cn.prepareStatement(sqlMov)) {
                ps.setInt(1, idProducto);
                ps.setInt(2, cantidad);
                ps.setString(3, referencia);
                ps.executeUpdate();
            }
            cn.commit();
            return true;
        } catch (SQLException e) {
            try {
                cn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error en rollback: " + ex.getMessage());
            }
            System.err.println("Error al abastecer producto: " + e.getMessage());
            return false;
        } finally {
            try {
                cn.setAutoCommit(true);
                cn.close();
            } catch (SQLException ex) {
                System.err.println("Error al cerrar la conexión: " + ex.getMessage());
            }
        }
    }

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
