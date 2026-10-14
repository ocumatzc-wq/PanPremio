package excepciones;
// Excepción personalizada para manejar casos donde no hay suficiente existencia en el inventario de un producto
public class StockInsuficienteException extends Exception {
    // Constructor que recibe un mensaje descriptivo y se lo pasa a la clase padre (Exception)
    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }
}