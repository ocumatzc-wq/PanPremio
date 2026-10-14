package proyectooracle;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ApiPuntos {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/puntos", ApiPuntos::handlePuntos);
        server.start();
        System.out.println("API en http://0.0.0.0:8080/puntos?codigo=CLI001");
    }

    private static void handlePuntos(HttpExchange ex) throws java.io.IOException {
        String query = ex.getRequestURI().getQuery();
        String codigo = null;
        if (query != null) {
            for (String par : query.split("&")) {
                String[] kv = par.split("=");
                if (kv.length == 2 && kv[0].equals("codigo")) {
                    codigo = URLDecoder.decode(kv[1], "UTF-8");
                }
            }
        }

        String json;
        int status;
        if (codigo == null || codigo.isEmpty()) {
            json = "{\"error\":\"Falta el parametro codigo\"}";
            status = 400;
        } else {
            json = consultar(codigo);
            status = json == null ? 404 : 200;
            if (json == null) {
                json = "{\"error\":\"Cliente no encontrado\"}";
            }
        }

        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String consultar(String codigo) {
        String sql = "SELECT nombre, apellidos, saldo_puntos FROM Clientes WHERE codigo = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return "{\"codigo\":\"" + codigo + "\",\"cliente\":\""
                            + rs.getString("nombre") + " " + rs.getString("apellidos")
                            + "\",\"puntos\":" + rs.getInt("saldo_puntos") + "}";
                }
            }
        } catch (Exception e) {
            System.err.println("Error API: " + e.getMessage());
        }
        return null;
    }
}
