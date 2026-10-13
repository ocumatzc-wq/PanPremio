package dao;

import proyectooracle.Conexion;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona las consultas SQL utilizadas para generar los reportes del sistema.
 */
public class ReporteDAO {
    /**
     * Consulta las ventas realizadas durante un período de fechas.
     * Incluye pedidos pagados o entregados y sus datos de pago.
     *
     * @param desde Fecha inicial del período.
     * @param hasta Fecha final del período.
     * @return Lista con los datos de las ventas consultadas.
     */
    public List<Object[]> ventasPorPeriodo(Date desde, Date hasta) { // Consulta la fecha, el identificador del pedido, el tipo de pago y el monto.
        String sql = "SELECT p.fecha_pedido, p.id_pedido, pg.tipo, pg.monto "
                + "FROM Pedidos p JOIN Pagos pg ON pg.id_pedido = p.id_pedido "
                + "WHERE p.estado IN ('PAGADO', 'ENTREGADO') "
                + "AND p.fecha_pedido >= ? AND p.fecha_pedido < ? + 1 "
                + "ORDER BY p.fecha_pedido";
        return consultar(sql, desde, hasta); // Ejecuta la consulta enviando las fechas como parámetros.
    }
    /**
     * Consulta los productos más vendidos, incluyendo los componentes
     * de los menús completos, y los ordena por unidades vendidas.
     *
     * @return Lista de productos con sus unidades vendidas.
     */
    public List<Object[]> productosMasVendidos() { // Suma las ventas individuales y las unidades vendidas como parte de menús.
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
        return consultar(sql); // Ejecuta la consulta y devuelve los resultados del reporte.
    }
    /**
     * Consulta los productos activos con cinco unidades o menos en inventario.
     *
     * @return Lista de productos con existencias bajas.
     */
    public List<Object[]> productosBajoInventario() { // Filtra los productos activos cuya existencia sea igual o inferior a cinco.
        String sql = "SELECT codigo, nombre, categoria, existencia FROM Productos "
                + "WHERE activo = 1 AND existencia <= 5 ORDER BY existencia ASC, nombre";
        return consultar(sql);
    }
    /**
     * Consulta el saldo de puntos de todos los clientes activos.
     * Ordena los resultados desde el mayor saldo hasta el menor.
     *
     * @return Lista de clientes y sus puntos acumulados.
     */
    public List<Object[]> puntosPorCliente() { // Une el nombre y los apellidos para mostrar el nombre completo del cliente.
        String sql = "SELECT codigo, nombre || ' ' || apellidos, saldo_puntos FROM Clientes "
                + "WHERE activo = 1 ORDER BY saldo_puntos DESC";
        return consultar(sql);
    }
    /**
     * Consulta el historial de canjes de recompensas.
     * Incluye el cliente, la recompensa, los puntos utilizados y la fecha.
     *
     * @return Lista de los canjes registrados, ordenados del más reciente al más antiguo.
     */
    public List<Object[]> historialCanjes() { // Relaciona los canjes con los clientes y las recompensas correspondientes.
        String sql = "SELECT c.id_canje, cl.nombre || ' ' || cl.apellidos, r.nombre, "
                + "c.puntos_usados, c.fecha_canje FROM Canjes c "
                + "JOIN Clientes cl ON cl.id_cliente = c.id_cliente "
                + "JOIN Recompensas r ON r.id_recompensa = c.id_recompensa "
                + "ORDER BY c.fecha_canje DESC";
        return consultar(sql);
    }
    /**
     * Consulta las ventas de menús completos, agrupadas por menú.
     * Calcula la cantidad vendida y el importe total generado.
     *
     * @return Lista de menús con sus cantidades e importes de venta.
     */
    public List<Object[]> ventasMenus() { // Agrupa las ventas por menú y suma las cantidades y los subtotales.
        String sql = "SELECT m.codigo, m.nombre, SUM(dp.cantidad), SUM(dp.subtotal) "
                + "FROM Detalle_Pedidos dp JOIN Menus m ON m.id_menu = dp.id_menu "
                + "JOIN Pedidos p ON p.id_pedido = dp.id_pedido "
                + "WHERE dp.id_menu IS NOT NULL AND p.estado <> 'ANULADO' "
                + "GROUP BY m.codigo, m.nombre ORDER BY SUM(dp.subtotal) DESC";
        return consultar(sql);
    }
    /**
     * Ejecuta una consulta SQL y convierte cada fila obtenida en un arreglo
     * de objetos. Permite reutilizar el mismo procedimiento en los reportes.
     *
     * @param sql Consulta SQL que se ejecutará.
     * @param parametros Valores que se asignarán a los parámetros de la consulta.
     * @return Lista de filas con los resultados obtenidos.
     */
    private List<Object[]> consultar(String sql, Object... parametros) { // Crea la lista donde se almacenarán las filas del resultado.
        List<Object[]> lista = new ArrayList<>();
        Connection cn = Conexion.getConexion(); // Obtiene la conexión con la base de datos Oracle.
        if (cn == null) { // Si no hay conexión, informa del problema y devuelve una lista vacía.
            System.err.println("No se pudo obtener conexión a Oracle.");
            return lista;
        }
        try (cn; PreparedStatement ps = cn.prepareStatement(sql)) { // Prepara la consulta y garantiza el cierre de los recursos utilizados.
            for (int i = 0; i < parametros.length; i++) { // Asigna los valores recibidos a los parámetros de la consulta SQL.
                ps.setObject(i + 1, parametros[i]);
            }
            try (ResultSet rs = ps.executeQuery()) { // Ejecuta la consulta y obtiene las filas devueltas por Oracle.
                int columnas = rs.getMetaData().getColumnCount(); // Obtiene la cantidad de columnas que contiene el resultado.
                while (rs.next()) { // Recorre cada fila del resultado.
                    Object[] fila = new Object[columnas]; // Crea un arreglo para almacenar los valores de la fila actual.
                    for (int i = 0; i < columnas; i++) { // Copia cada columna al arreglo, conservando su tipo de dato.
                        fila[i] = rs.getObject(i + 1);
                    }
                    lista.add(fila); // Agrega la fila completa a la lista de resultados.
                }
            }
        } catch (SQLException e) { // Informa si ocurre un error durante la consulta SQL.
            System.err.println("Error al ejecutar reporte: " + e.getMessage());
        }
        return lista; // Devuelve las filas obtenidas; la lista estará vacía si no hubo resultados.
    }
}
