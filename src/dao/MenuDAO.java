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
// Clase DAO para gestionar las operaciones de base de datos relacionadas con los menús
public class MenuDAO {
    // Método que lista los menús activos por defecto llamando al método sobrecargado
    public List<MenuCompleto> listarMenus() {
        return listarMenus(true);
    }
    // Método principal para listar menús, permitiendo filtrar si solo se desean los activos o todos
    public List<MenuCompleto> listarMenus(boolean soloActivos) {
        List<MenuCompleto> lista = new ArrayList<>();
        Map<Integer, MenuCompleto> mapa = new LinkedHashMap<>(); // Consulta SQL para obtener los datos del menú y hacer un JOIN con sus productos componentes
        String sql = "SELECT m.id_menu, m.codigo, m.nombre, m.precio, m.activo, "
                + "p.id_producto, p.categoria, p.nombre AS prod_nombre, p.precio AS prod_precio, p.existencia "
                + "FROM Menus m "
                + "JOIN Menu_Composicion mc ON mc.id_menu = m.id_menu "
                + "JOIN Productos p ON p.id_producto = mc.id_producto "
                + (soloActivos ? "WHERE m.activo = 1 " : "") // Aplica filtro si soloActivos es verdadero
                + "ORDER BY m.id_menu";
        Connection cn = Conexion.getConexion(); // Obtiene la conexión a la base de datos
        if (cn == null) {
            System.err.println("No se pudo obtener conexión a Oracle.");
            return lista; // Retorna la lista vacía si falla la conexión
        }
        // Bloque try-with-resources para asegurar el cierre automático de conexión, statement y resultset
        try (cn; PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) { 
            while (rs.next()) {
                int idMenu = rs.getInt("id_menu");
                MenuCompleto menu = mapa.get(idMenu); // Busca si el menú ya fue agregado al mapa
                if (menu == null) { // Si el menú no existe en el mapa, se crea e inicializa
                    menu = new MenuCompleto(rs.getString("nombre"), rs.getDouble("precio"));
                    menu.setIdMenu(idMenu);
                    menu.setCodigo(rs.getString("codigo"));
                    menu.setActivo(rs.getInt("activo") == 1);
                    mapa.put(idMenu, menu);
                }
                // Se guarda en el mapa para futuras filas del mismo menú
                ProductoIndividual componente = new ProductoIndividual(
                        rs.getInt("id_producto"),
                        rs.getString("categoria"),
                        rs.getString("prod_nombre"),
                        rs.getDouble("prod_precio"),
                        rs.getInt("existencia"));
                menu.agregarProducto(componente); // Se añade el producto a la lista de componentes del menú
            }
            lista.addAll(mapa.values()); // Se transfieren todos los menús únicos del mapa a la lista final
        } catch (SQLException e) {
            System.err.println("Error al consultar menús: " + e.getMessage());
        }
        return lista; // Retorna la lista completa de menús
    }
    // Método para registrar un nuevo menú y su composición en la base de datos
    public int agregarMenu(MenuCompleto menu) {
        String sqlMenu = "INSERT INTO Menus (codigo, nombre, precio, activo) VALUES (?, ?, ?, ?)";
        String sqlComp = "INSERT INTO Menu_Composicion (id_menu, id_producto, cantidad) VALUES (?, ?, ?)";
        Connection cn = Conexion.getConexion(); // Obtiene la conexión
        if (cn == null) {
            System.err.println("No se pudo obtener conexión a Oracle.");
            return -1; // Retorna -1 si no hay conexión
        }
        int idMenuGenerado = -1; // Variable para almacenar el ID autogenerado del menú
        try {
            cn.setAutoCommit(false); // Desactiva el autocommit para manejar transacciones manualmente
            // Inserta el menú principal solicitando que devuelva la llave generada (id_menu)
            try (PreparedStatement ps = cn.prepareStatement(sqlMenu, new String[]{"id_menu"})) {
                ps.setString(1, menu.getCodigo());
                ps.setString(2, menu.getNombre());
                ps.setDouble(3, menu.getPrecio());
                ps.setInt(4, menu.isActivo() ? 1 : 0);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) { // Recupera el ID generado por la base de datos para el nuevo menú
                    if (rs.next()) {
                        idMenuGenerado = rs.getInt(1);
                    }
                }
            }
            // Inserta en lote (Batch) los productos que componen este menú
            try (PreparedStatement ps = cn.prepareStatement(sqlComp)) {
                for (ProductoIndividual p : menu.getProductos()) {
                    ps.setInt(1, idMenuGenerado);
                    ps.setInt(2, p.getIdProducto());
                    ps.setInt(3, 1); // Cantidad predeterminada de 1 por producto en el menú
                    ps.addBatch();
                }
                ps.executeBatch(); // Ejecuta todas las inserciones juntas
            }
            cn.commit(); // Confirma la transacción si todo salió bien
            return idMenuGenerado; // Retorna el ID del menú creado
        } catch (SQLException e) {
            try {
                cn.rollback(); // Revierte los cambios si ocurre algún error durante el proceso
            } catch (SQLException ex) {
                System.err.println("Error en rollback: " + ex.getMessage());
            }
            System.err.println("Error al guardar menú: " + e.getMessage());
            return -1; // Retorna -1 indicando fallo
        } finally {
            try {
                cn.setAutoCommit(true); // Restaura el autocommit por defecto
                cn.close(); // Cierra la conexión a la base de datos
            } catch (SQLException ex) {
                System.err.println("Error al cerrar la conexión: " + ex.getMessage());
            }
        }
    }
}
