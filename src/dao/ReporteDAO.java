package dao;

import proyectooracle.Conexion;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAO {
    public List<Object[]> ventasPorPeriodo(Date desde, Date hasta) {
        String sql = "SELECT p.fecha_pedido, p.id_pedido, pg.tipo, pg.monto "
                + "FROM Pedidos p JOIN Pagos pg ON pg.id_pedido = p.id_pedido "
                + "WHERE p.estado IN ('PAGADO', 'ENTREGADO') "
                + "AND p.fecha_pedido >= ? AND p.fecha_pedido < ? + 1 "
                + "ORDER BY p.fecha_pedido";
        return consultar(sql, desde, hasta);
    }
    public List<Object[]> productosMasVendidos() {
        String sql = "SELECT pr.codigo, pr.nombre, pr.categoria, SUM(v.cantidad) AS unidades "
                + "FROM ("
                + "SELECT dp.id_producto, dp.cantidad FROM Detalle_Pedidos dp "
                + "JOIN Pedidos p ON p.id_pedido = dp.id_pedido "
                + "WHERE dp.id_producto IS NOT NULL AND p.estado <> 'ANULADO' "
                + "UNION ALL "
                + "SELECT mc.id_producto, dp.cantidad * mc.cantidad FROM Detalle_Pedidos dp "
                + "JOIN Pedidos p ON p.id_pedido = dp.id_pedido "
                + "JOIN Menu_Composicion mc ON mc.id_menu = dp.id_menu "
                + "WHERE dp.id_menu IS NOT NULL AND p.estado <> 'ANULADO') v "
                + "JOIN Productos pr ON pr.id_producto = v.id_producto "
                + "GROUP BY pr.codigo, pr.nombre, pr.categoria "
                + "ORDER BY unidades DESC, pr.nombre";
        return consultar(sql);
    }
    public List<Object[]> productosBajoInventario() {
        String sql = "SELECT codigo, nombre, categoria, existencia FROM Productos "
                + "WHERE activo = 1 AND existencia <= 5 ORDER BY existencia ASC, nombre";
        return consultar(sql);
    }
    public List<Object[]> puntosPorCliente() {
        String sql = "SELECT codigo, nombre || ' ' || apellidos, saldo_puntos FROM Clientes "
                + "WHERE activo = 1 ORDER BY saldo_puntos DESC";
        return consultar(sql);
    }
    public List<Object[]> historialCanjes() {
        String sql = "SELECT c.id_canje, cl.nombre || ' ' || cl.apellidos, r.nombre, "
                + "c.puntos_usados, c.fecha_canje FROM Canjes c "
                + "JOIN Clientes cl ON cl.id_cliente = c.id_cliente "
                + "JOIN Recompensas r ON r.id_recompensa = c.id_recompensa "
                + "ORDER BY c.fecha_canje DESC";
        return consultar(sql);
    }
    public List<Object[]> ventasMenus() {
        String sql = "SELECT m.codigo, m.nombre, SUM(dp.cantidad), SUM(dp.subtotal) "
                + "FROM Detalle_Pedidos dp JOIN Menus m ON m.id_menu = dp.id_menu "
                + "JOIN Pedidos p ON p.id_pedido = dp.id_pedido "
                + "WHERE dp.id_menu IS NOT NULL AND p.estado <> 'ANULADO' "
                + "GROUP BY m.codigo, m.nombre ORDER BY SUM(dp.subtotal) DESC";
        return consultar(sql);
    }
    private List<Object[]> consultar(String sql, Object... parametros) {
        List<Object[]> lista = new ArrayList<>();
        Connection cn = Conexion.getConexion();
        if (cn == null) {
            System.err.println("No se pudo obtener conexión a Oracle.");
            return lista;
        }
        try (cn; PreparedStatement ps = cn.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                int columnas = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    Object[] fila = new Object[columnas];
                    for (int i = 0; i < columnas; i++) {
                        fila[i] = rs.getObject(i + 1);
                    }
                    lista.add(fila);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al ejecutar reporte: " + e.getMessage());
        }
        return lista;
    }
}
