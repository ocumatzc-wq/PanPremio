package modelo;

import java.sql.Date; // Importa la clase Date de JDBC para manejar fechas SQL.
/**
 * Representa un movimiento relacionado con los puntos de un cliente.
 * Permite almacenar información sobre el tipo de operación, la cantidad de puntos, la fecha y la referencia asociada.
 */
public class MovimientoPuntos {
    private int idMovimiento; // Identificador único del movimiento de puntos.
    private int idCliente; // Identificador del cliente al que pertenece el movimiento.
    private String tipo; // Tipo de operación realizada, por ejemplo: ACUMULACION, CANJE o AJUSTE.
    private int puntos; // Cantidad de puntos involucrados en la operación.
    private Date fechaMovimiento; // Fecha en que se realizó o registró el movimiento.
    private String referencia; // Referencia que permite identificar el origen de la operación, como una compra, un pedido o un canje.
    /**
     * Constructor vacío.
     * Permite crear un objeto MovimientoPuntos sin proporcionar valores iniciales mediante parámetros.
     */
    public MovimientoPuntos() {
    }
    /**
     * Constructor parametrizado.
     *
     * Permite crear un movimiento de puntos indicando el cliente, el tipo de operación, la cantidad de puntos y su referencia.
     * El identificador y la fecha no se reciben como parámetros; pueden asignarse posteriormente o ser gestionados por la BD.
     * @param idCliente identificador del cliente
     * @param tipo tipo de operación realizada
     * @param puntos cantidad de puntos involucrados
     * @param referencia referencia asociada al movimiento
     */
    public MovimientoPuntos(int idCliente, String tipo, int puntos, String referencia) {
        this.idCliente = idCliente;
        this.tipo = tipo;
        this.puntos = puntos;
        this.referencia = referencia;
    }
    /**
     * Obtiene el identificador del movimiento.
     * @return identificador del movimiento
     */
    public int getIdMovimiento() {
        return idMovimiento;
    }
    /**
     * Asigna el identificador del movimiento.
     * @param idMovimiento identificador que se establecerá
     */
    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }
    /**
     * Obtiene el identificador del cliente.
     * @return identificador del cliente
     */
    public int getIdCliente() {
        return idCliente;
    }
    /**
     * Asigna el identificador del cliente.
     * @param idCliente identificador que se establecerá
     */
    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }
    /**
     * Obtiene el tipo de movimiento.
     * @return tipo de operación
     */
    public String getTipo() {
        return tipo;
    }
    /**
     * Asigna el tipo de movimiento.
     * @param tipo tipo de operación que se establecerá
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    /**
     * Obtiene la cantidad de puntos involucrados.
     * @return cantidad de puntos
     */
    public int getPuntos() {
        return puntos;
    }
    /**
     * Asigna la cantidad de puntos del movimiento.
     * @param puntos cantidad de puntos que se establecerá
     */
    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }
    /**
     * Obtiene la fecha del movimiento.
     * @return fecha del movimiento
     */
    public Date getFechaMovimiento() {
        return fechaMovimiento;
    }
    /**
     * Asigna la fecha del movimiento.
     * @param fechaMovimiento fecha que se establecerá
     */
    public void setFechaMovimiento(Date fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }
    /**
     * Obtiene la referencia asociada al movimiento.
     * @return referencia de la operación
     */
    public String getReferencia() {
        return referencia;
    }
    /**
     * Asigna la referencia asociada al movimiento.
     * @param referencia referencia que se establecerá
     */
    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
}
