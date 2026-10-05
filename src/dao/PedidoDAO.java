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
        Connection cn = null;
        try {
            cn = Conexion.getConexion();
            cn.setAutoCommit(false); // Iniciar transacción explícita

            // 1. Insertar el Pedido
            String sqlPedido = "INSERT INTO Pedidos (id_cliente, estado) VALUES (?, ?)";
            String[] generatedKeys = {"id_pedido"};
            PreparedStatement psPedido = cn.prepareStatement(sqlPedido, generatedKeys);
            psPedido.setInt(1, pedido.getIdCliente());
            psPedido.setString(2, pedido.getEstado());
            psPedido.executeUpdate();

            // Obtener el id_pedido generado
            ResultSet rsKeys = psPedido.getGeneratedKeys();
            int idPedidoGenerado = 0;
            if (rsKeys.next()) {
                idPedidoGenerado = rsKeys.getInt(1);
            }

            // 2. Insertar Detalle_Pedidos y descontar Stock
            String sqlDetalle = "INSERT INTO Detalle_Pedidos (id_pedido, id_producto, cantidad) VALUES (?, ?, ?)";
            String sqlStock = "UPDATE Productos SET existencia = existencia - ? WHERE id_producto = ?";
            PreparedStatement psDetalle = cn.prepareStatement(sqlDetalle);
            PreparedStatement psStock = cn.prepareStatement(sqlStock);

            for (DetallePedido dp : detalles) {
                // Insertar detalle
                psDetalle.setInt(1, idPedidoGenerado);
                psDetalle.setInt(2, dp.getIdProducto());
                psDetalle.setInt(3, dp.getCantidad());
                psDetalle.addBatch();

                // Actualizar stock
                psStock.setInt(1, dp.getCantidad());
                psStock.setInt(2, dp.getIdProducto());
                psStock.addBatch();
            }

            psDetalle.executeBatch();
            psStock.executeBatch();

            // 3. Calcular puntos (1 punto por cada Q10 comprados)
            int puntosGanados = (int) (totalVenta / 10);
            if (puntosGanados > 0) {
                // Actualizar saldo_puntos en Clientes
                String sqlPuntos = "UPDATE Clientes SET saldo_puntos = saldo_puntos + ? WHERE id_cliente = ?";
                PreparedStatement psPuntos = cn.prepareStatement(sqlPuntos);
                psPuntos.setInt(1, puntosGanados);
                psPuntos.setInt(2, pedido.getIdCliente());
                psPuntos.executeUpdate();

                // Registrar en Movimientos_Puntos
                String sqlMov = "INSERT INTO Movimientos_Puntos (id_cliente, tipo, puntos) VALUES (?, 'acreditación', ?)";
                PreparedStatement psMov = cn.prepareStatement(sqlMov);
                psMov.setInt(1, pedido.getIdCliente());
                psMov.setInt(2, puntosGanados);
                psMov.executeUpdate();
            }

            cn.commit(); // Confirmar la transacción
            System.out.println("¡Venta procesada con éxito! Puntos otorgados: " + puntosGanados);
            return true;

        } catch (SQLException e) {
            if (cn != null) {
                try {
                    cn.rollback(); // Revertir todo si falla
                    System.err.println("Transacción revertida por error.");
                } catch (SQLException ex) {
                    System.err.println("Error en rollback: " + ex.getMessage());
                }
            }
            System.err.println("Error al procesar la venta: " + e.getMessage());
            return false;
        }
    }
}
