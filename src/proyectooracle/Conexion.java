package proyectooracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    // Apunta al contenedor principal :xe con el usuario correcto
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String USUARIO = "USUARIO_OSCAR";
    private static final String CLAVE = "654321$";

    public static Connection getConexion() {
        Connection cn = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            cn = DriverManager.getConnection(URL, USUARIO, CLAVE);
            System.out.println("¡Conexión a Oracle realizada con éxito!");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Driver no encontrado. " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error al conectar a Oracle: " + e.getMessage());
        }
        return cn;
    }

    public static void main(String[] args) {
        Connection cn = getConexion();
        if (cn != null) {
            try {
                cn.close();
                System.out.println("Conexión cerrada correctamente.");
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }
}

