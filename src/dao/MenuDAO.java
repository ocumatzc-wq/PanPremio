package dao;

import proyectooracle.Conexion;
import modelo.MenuCompleto;
import modelo.ProductoIndividual;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
public class MenuDAO {
    public List<MenuCompleto> listarMenus() {
        return listarMenus(true);
    }
    public List<MenuCompleto> listarMenus(boolean soloActivos) {
        List<MenuCompleto> lista = new ArrayList<>();
        Map<Integer, MenuCompleto> mapa = new LinkedHashMap<>();
        String sql = "SELECT m.id_menu, m.codigo, m.nombre, m.precio, m.activo, "
                + "p.id_producto, p.categoria, p.nombre AS prod_nombre, p.precio AS prod_precio, p.existencia "
                + "FROM Menus m "
                + "JOIN Menu_Composicion mc ON mc.id_menu = m.id_menu "
                + "JOIN Productos p ON p.id_producto = mc.id_producto "
                + (soloActivos ? "WHERE m.activo = 1 " : "")
                + "ORDER BY m.id_menu";
        Connection cn = Conexion.getConexion();
        if (cn == null) {
            System.err.println("No se pudo obtener conexión a Oracle.");
            return lista;
        }
        try (cn; PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int idMenu = rs.getInt("id_menu");
                MenuCompleto menu = mapa.get(idMenu);
                if (menu == null) {
                    menu = new MenuCompleto(rs.getString("nombre"), rs.getDouble("precio"));
                    menu.setIdMenu(idMenu);
                    menu.setCodigo(rs.getString("codigo"));
                    menu.setActivo(rs.getInt("activo") == 1);
                    mapa.put(idMenu, menu);
                }
                ProductoIndividual componente = new ProductoIndividual(
                        rs.getInt("id_producto"),
                        rs.getString("categoria"),
                        rs.getString("prod_nombre"),
                        rs.getDouble("prod_precio"),
                        rs.getInt("existencia"));
                menu.agregarProducto(componente);
            }
            lista.addAll(mapa.values());
        } catch (SQLException e) {
            System.err.println("Error al consultar menús: " + e.getMessage());
        }
        return lista;
    }
    public int agregarMenu(MenuCompleto menu) {
        String sqlMenu = "INSERT INTO Menus (codigo, nombre, precio, activo) VALUES (?, ?, ?, ?)";
        String sqlComp = "INSERT INTO Menu_Composicion (id_menu, id_producto, cantidad) VALUES (?, ?, ?)";
        Connection cn = Conexion.getConexion();
        if (cn == null) {
            System.err.println("No se pudo obtener conexión a Oracle.");
            return -1;
        }
        int idMenuGenerado = -1;
        try {
            cn.setAutoCommit(false);
            try (PreparedStatement ps = cn.prepareStatement(sqlMenu, new String[]{"id_menu"})) {
                ps.setString(1, menu.getCodigo());
                ps.setString(2, menu.getNombre());
                ps.setDouble(3, menu.getPrecio());
                ps.setInt(4, menu.isActivo() ? 1 : 0);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        idMenuGenerado = rs.getInt(1);
                    }
                }
            }
            try (PreparedStatement ps = cn.prepareStatement(sqlComp)) {
                for (ProductoIndividual p : menu.getProductos()) {
                    ps.setInt(1, idMenuGenerado);
                    ps.setInt(2, p.getIdProducto());
                    ps.setInt(3, 1);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            cn.commit();
            return idMenuGenerado;
        } catch (SQLException e) {
            try {
                cn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error en rollback: " + ex.getMessage());
            }
            System.err.println("Error al guardar menú: " + e.getMessage());
            return -1;
        } finally {
            try {
                cn.setAutoCommit(true);
                cn.close();
            } catch (SQLException ex) {
                System.err.println("Error al cerrar la conexión: " + ex.getMessage());
            }
        }
    }
}
