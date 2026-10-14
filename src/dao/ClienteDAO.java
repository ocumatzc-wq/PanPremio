package dao;

import proyectooracle.Conexion;
import modelo.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList; // Importaciones para almacenar y devolver colecciones de clientes.
import java.util.List;

public class ClienteDAO {
    /**
     * Registra un nuevo cliente en la base de datos.
     * @param cliente Objeto que contiene la información del cliente.
     * @return true si se insertó el registro; false si ocurrió un error
     *         o no se insertó ninguna fila.
     */
    public boolean agregarCliente(Cliente cliente) {
        // Consulta SQL para insertar los datos de un cliente.
        // Los signos ? representan parámetros que se asignarán posteriormente.
        String sql = "INSERT INTO Clientes (codigo, nombre, apellidos, email, telefono, saldo_puntos, activo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection cn = Conexion.getConexion(); // Obtiene una conexión con la base de datos.
        if (cn == null) { // Verifica que la conexión se haya obtenido correctamente.
            System.err.println("No se pudo obtener conexión a Oracle.");
            return false;
        }
        try (cn; PreparedStatement ps = cn.prepareStatement(sql)) { // Prepara la consulta SQL y administra el cierre de los recursos.
            ps.setString(1, cliente.getCodigo()); // Asigna el código del cliente al primer parámetro.
            ps.setString(2, cliente.getNombre()); // Asigna el nombre del cliente al segundo parámetro.
            ps.setString(3, cliente.getApellido()); // Asigna los apellidos del cliente al tercer parámetro.
            ps.setString(4, cliente.getEmail()); // Asigna el correo electrónico al cuarto parámetro.
            ps.setString(5, cliente.getTelefono()); /* Asigna el número de teléfono al quinto parámetro.*/
            ps.setInt(6, cliente.getSaldoPuntos()); // Asigna el saldo inicial de puntos al sexto parámetro.
            ps.setInt(7, cliente.isActivo() ? 1 : 0); // Convierte el estado booleano a un valor numérico: true se almacena como 1 y false como 0.
            return ps.executeUpdate() > 0; // Ejecuta la inserción y devuelve true si se insertó alguna fila.
        } catch (SQLException e) {
            System.err.println("Error al insertar cliente: " + e.getMessage()); // Informa el error si la inserción falla.
            return false;
        }
    }
    /**
     * Obtiene todos los clientes registrados en la base de datos.
     *
     * @return Lista de clientes ordenados por su identificador.
     *         Si ocurre un error, devuelve la lista vacía.
     */
    public List<Cliente> obtenerClientes() { // Crea una lista vacía para almacenar los clientes consultados.
        List<Cliente> lista = new ArrayList<>(); // Consulta los datos de todos los clientes y los ordena por ID.
        String sql = "SELECT id_cliente, codigo, nombre, apellidos, email, telefono, saldo_puntos, activo FROM Clientes ORDER BY id_cliente";
        Connection cn = Conexion.getConexion();
        if (cn == null) { // Si no existe conexión, devuelve la lista vacía.
            System.err.println("No se pudo obtener conexión a Oracle.");
            return lista;
        }
        // Prepara y ejecuta la consulta.
        // El ResultSet contiene las filas devueltas por la base de datos.
        try (cn; PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) { // Recorre los resultados mientras existan filas disponibles.
                lista.add(mapear(rs)); // Convierte la fila actual en un objeto Cliente y lo agrega a la lista.
            }
        } catch (SQLException e) { // Informa el error ocurrido durante la consulta.
            System.err.println("Error al consultar clientes: " + e.getMessage());
        }
        return lista; // Devuelve la lista con los clientes encontrados.
    }
    /**
     * Busca un cliente mediante su identificador único.
     *
     * @param idCliente Identificador del cliente que se desea consultar.
     * @return Objeto Cliente si se encuentra; null si no existe
     *         o si ocurre un error.
     */
    public Cliente obtenerClientePorId(int idCliente) {
        // Consulta SQL que busca un cliente por su identificador.
        String sql = "SELECT id_cliente, codigo, nombre, apellidos, email, telefono, saldo_puntos, activo FROM Clientes WHERE id_cliente = ?";
        Connection cn = Conexion.getConexion(); // Obtiene la conexión con la base de datos.
        if (cn == null) { // Comprueba que la conexión esté disponible.
            System.err.println("No se pudo obtener conexión a Oracle.");
            return null;
        }
        try (cn; PreparedStatement ps = cn.prepareStatement(sql)) { // Prepara la consulta con el parámetro del identificador.
            ps.setInt(1, idCliente); // Asigna el identificador al primer parámetro de la consulta.
            try (ResultSet rs = ps.executeQuery()) { // Ejecuta la consulta y obtiene sus resultados.
                if (rs.next()) { // Si se encuentra una fila, la convierte en un Cliente.
                    return mapear(rs);
                }
            }
        } catch (SQLException e) { // Informa cualquier error ocurrido durante la consulta.
            System.err.println("Error al consultar cliente: " + e.getMessage());
        }
        return null; // Devuelve null si no se encontró el cliente o falló la consulta.
    }
    /**
     * Busca un cliente mediante su código.
     *
     * @param codigo Código asignado al cliente.
     * @return Objeto Cliente si se encuentra; null si no existe
     *         o si ocurre un error.
     */
    public Cliente obtenerClientePorCodigo(String codigo) { // Consulta SQL para buscar un cliente por su código.
        String sql = "SELECT id_cliente, codigo, nombre, apellidos, email, telefono, saldo_puntos, activo FROM Clientes WHERE codigo = ?";
        Connection cn = Conexion.getConexion(); // Obtiene la conexión con Oracle.
        if (cn == null) { // Si no hay conexión, no se puede realizar la búsqueda.
            System.err.println("No se pudo obtener conexión a Oracle.");
            return null;
        }
        try (cn; PreparedStatement ps = cn.prepareStatement(sql)) { // Prepara la consulta para recibir el código como parámetro.
            ps.setString(1, codigo); // Asigna el código recibido al primer parámetro.
            try (ResultSet rs = ps.executeQuery()) { // Ejecuta la consulta.
                if (rs.next()) { // Si existe un cliente con ese código, convierte la fila en un objeto Cliente.
                    return mapear(rs);
                }
            }
        } catch (SQLException e) { // Informa el error si la consulta no se ejecuta correctamente.
            System.err.println("Error al consultar cliente por código: " + e.getMessage());
        }
        return null; // Devuelve null si no encuentra un resultado o sucede un error.
    }
    /**
     * Actualiza los datos de un cliente existente.
     *
     * @param cliente Objeto con los datos actualizados del cliente.
     * @return true si se modificó alguna fila; false en caso contrario.
     */
    public boolean actualizarCliente(Cliente cliente) { // Consulta SQL para actualizar los datos del cliente. La condición WHERE identifica qué registro debe modificarse.
        String sql = "UPDATE Clientes SET codigo = ?, nombre = ?, apellidos = ?, email = ?, telefono = ?, saldo_puntos = ?, activo = ? WHERE id_cliente = ?";
        Connection cn = Conexion.getConexion(); // Obtiene la conexión con Oracle.
        if (cn == null) { // Comprueba que la conexión esté disponible.
            System.err.println("No se pudo obtener conexión a Oracle.");
            return false;
        }
        try (cn; PreparedStatement ps = cn.prepareStatement(sql)) { // Prepara la consulta de actualización.
            // Asigna los nuevos valores de los campos del cliente.
            ps.setString(1, cliente.getCodigo());
            ps.setString(2, cliente.getNombre());
            ps.setString(3, cliente.getApellido());
            ps.setString(4, cliente.getEmail());
            ps.setString(5, cliente.getTelefono());
            ps.setInt(6, cliente.getSaldoPuntos());
            ps.setInt(7, cliente.isActivo() ? 1 : 0); // Convierte el estado booleano a 1 o 0.
            ps.setInt(8, cliente.getIdCliente()); // Asigna el identificador del cliente que se actualizará.
            return ps.executeUpdate() > 0; // Ejecuta la actualización y devuelve true si se modificó al menos una fila.
        } catch (SQLException e) { // Informa el error ocurrido durante la actualización.
            System.err.println("Error al actualizar cliente: " + e.getMessage());
            return false;
        }
    }
    /**
     * Cambia el estado de un cliente sin modificar sus otros datos.
     *
     * @param idCliente Identificador del cliente.
     * @param activo true para activar al cliente; false para desactivarlo.
     * @return true si se modificó alguna fila; false en caso contrario.
     */
    public boolean cambiarEstadoCliente(int idCliente, boolean activo) { // Consulta para actualizar únicamente el estado del cliente.
        String sql = "UPDATE Clientes SET activo = ? WHERE id_cliente = ?";
        Connection cn = Conexion.getConexion();
        if (cn == null) { // Verifica que se haya obtenido la conexión.
            System.err.println("No se pudo obtener conexión a Oracle.");
            return false;
        }
        try (cn; PreparedStatement ps = cn.prepareStatement(sql)) { // Prepara la consulta de actualización.
            ps.setInt(1, activo ? 1 : 0); // Convierte el estado booleano a un valor numérico.
            ps.setInt(2, idCliente); // Identifica al cliente cuyo estado se modificará.
            return ps.executeUpdate() > 0; // Ejecuta la actualización y comprueba si se modificó una fila.
        } catch (SQLException e) { // Informa el error si no se pudo cambiar el estado.
            System.err.println("Error al cambiar estado del cliente: " + e.getMessage());
            return false;
        }
    }
    /**
     * Elimina físicamente un cliente de la tabla Clientes.
     *
     * @param idCliente Identificador del cliente que se desea eliminar.
     * @return true si se eliminó alguna fila; false en caso contrario.
     */
    public boolean eliminarCliente(int idCliente) { // Consulta SQL para eliminar el registro del cliente.
        String sql = "DELETE FROM Clientes WHERE id_cliente = ?"; // Consulta SQL para eliminar el registro del cliente.
        Connection cn = Conexion.getConexion(); // Obtiene la conexión con la base de datos.
        if (cn == null) { // Si no hay conexión, no se puede realizar la eliminación.
            System.err.println("No se pudo obtener conexión a Oracle.");
            return false;
        }
        try (cn; PreparedStatement ps = cn.prepareStatement(sql)) { // Prepara la consulta de eliminación.
            ps.setInt(1, idCliente); // Asigna el identificador del cliente que se eliminará.
            return ps.executeUpdate() > 0; // Ejecuta la eliminación y devuelve true si se eliminó una fila.
        } catch (SQLException e) { // Informa el error ocurrido durante la eliminación. Por ejemplo, podría existir una restricción de clave foránea.
            System.err.println("Error al eliminar cliente: " + e.getMessage());
            return false;
        }
    }
    /**
     * Convierte una fila obtenida de la base de datos en un objeto Cliente.
     *
     * Este método centraliza la asignación de los campos para evitar
     * repetir el mismo código en los métodos de consulta.
     *
     * @param rs ResultSet que contiene la fila consultada.
     * @return Objeto Cliente con los datos de la fila.
     * @throws SQLException Si ocurre un error al leer los datos.
     */
    private Cliente mapear(ResultSet rs) throws SQLException { // Crea un objeto Cliente vacío.
        Cliente c = new Cliente(); 
        c.setIdCliente(rs.getInt("id_cliente")); // Obtiene el identificador desde la columna id_cliente.
        c.setCodigo(rs.getString("codigo")); // Asigna el código del cliente.
        c.setNombre(rs.getString("nombre")); // Asigna el nombre del cliente.
        c.setApellido(rs.getString("apellidos")); // Asigna los apellidos al atributo apellido del modelo.
        c.setEmail(rs.getString("email")); // Asigna el correo electrónico.
        c.setTelefono(rs.getString("telefono")); // Asigna el número de teléfono.
        c.setSaldoPuntos(rs.getInt("saldo_puntos")); // Obtiene el saldo actual de puntos del cliente.
        c.setActivo(rs.getInt("activo") == 1); // Convierte el valor numérico de la columna activo en booleano: 1 equivale a true; cualquier otro valor equivale a false.
        return c; // Devuelve el objeto Cliente con todos sus campos asignados.
    }
}
