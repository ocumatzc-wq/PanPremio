package excepciones;
// Excepción personalizada para manejar casos donde un cliente no cuenta con los puntos de fidelidad necesarios
public class PuntosInsuficientesException extends Exception {
    // Constructor que recibe un mensaje descriptivo y se lo pasa a la clase padre (Exception)
    public PuntosInsuficientesException(String mensaje) {
        super(mensaje);
    }
}
