package dao;

import proyectooracle.Conexion;
import modelo.DetallePedido;
import modelo.Pedido;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PedidoDAO {

    public boolean registrarVenta(Pedido pedido, List<DetallePedido> detalles, double totalVenta) {
        String sqlPedido = "INSERT INTO Pedidos (id_cliente, estado) VALUES (?, ?)";
        String[] generatedKeys = {"id_pedido"};
        String sqlDetalle = "INSERT INTO Detalle_Pedidos (id_pedido, id_producto, cantidad) VALUES (?, ?, ?)";
        // El AND existencia >= ? hace que la fila NO se actualice (0 filas) si no hay stock.
        String sqlStock = "UPDATE Productos SET existencia = existencia - ? WHERE id_producto = ? AND existencia >= ?";
        String sqlPuntos = "UPDATE Clientes SET saldo_puntos = saldo_puntos + ? WHERE id_cliente = ?";
        String sqlMov = "INSERT INTO Movimientos_Puntos (id_cliente, tipo, puntos) VALUES (?, 'acreditación', ?)";

        Connection cn = Conexion.getConexion();
        if (cn == null) {
            System.err.println("No se pudo obtener conexión a Oracle.");
            return false;
        }

        try {
            cn.setAutoCommit(false); // Iniciar transacción explícita

            // 1. Insertar el Pedido y obtener su id generado
            int idPedidoGenerado = 0;
            try (PreparedStatement psPedido = cn.prepareStatement(sqlPedido, generatedKeys)) {
                psPedido.setInt(1, pedido.getIdCliente());
                psPedido.setString(2, pedido.getEstado());
                psPedido.executeUpdate();

                try (ResultSet rsKeys = psPedido.getGeneratedKeys()) {
                    if (rsKeys.next()) {
                        idPedidoGenerado = rsKeys.getInt(1);
                    }
                }
            }

            // 2. Insertar Detalle_Pedidos y descontar Stock validando existencias
            try (PreparedStatement psDetalle = cn.prepareStatement(sqlDetalle);
                 PreparedStatement psStock = cn.prepareStatement(sqlStock)) {

                for (DetallePedido dp : detalles) {
                    psDetalle.setInt(1, idPedidoGenerado);
                    psDetalle.setInt(2, dp.getIdProducto());
                    psDetalle.setInt(3, dp.getCantidad());
                    psDetalle.addBatch();

                    psStock.setInt(1, dp.getCantidad());
                    psStock.setInt(2, dp.getIdProducto());
                    psStock.setInt(3, dp.getCantidad());
                    psStock.addBatch();
                }

                psDetalle.executeBatch();

                int[] filasStock = psStock.executeBatch();
                for (int filas : filasStock) {
                    if (filas == 0) {
                        throw new SQLException("Stock insuficiente para uno de los productos.");
                    }
                }
            }

            // 3. Calcular puntos (1 punto por cada Q10 comprados)
            int puntosGanados = (int) (totalVenta / 10);
            if (puntosGanados > 0) {
                try (PreparedStatement psPuntos = cn.prepareStatement(sqlPuntos);
                     PreparedStatement psMov = cn.prepareStatement(sqlMov)) {

                    psPuntos.setInt(1, puntosGanados);
                    psPuntos.setInt(2, pedido.getIdCliente());
                    psPuntos.executeUpdate();

                    psMov.setInt(1, pedido.getIdCliente());
                    psMov.setInt(2, puntosGanados);
                    psMov.executeUpdate();
                }
            }

            cn.commit(); // Confirmar la transacción
            System.out.println("¡Venta procesada con éxito! Puntos otorgados: " + puntosGanados);
            return true;

        } catch (SQLException e) {
            try {
                cn.rollback(); // Revertir todo si falla
                System.err.println("Transacción revertida por error.");
            } catch (SQLException ex) {
                System.err.println("Error en rollback: " + ex.getMessage());
            }
            System.err.println("Error al procesar la venta: " + e.getMessage());
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
}
