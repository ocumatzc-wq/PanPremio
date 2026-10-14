package modelo;

import java.sql.Date;
/**
 * Clase que representa el modelo de datos para un Pago.
 * Funciona como un JavaBean / POJO para mapear los pagos del sistema.
 */
public class Pago {
    private int idPago;         // Identificador único del pago (Clave primaria)
    private int idPedido;       // Identificador del pedido asociado a este pago
    private String tipo;        // Forma de pago (ej. efectivo, tarjeta, transferencia)
    private double monto;       // Monto total a pagar
    private double recibido;    // Dinero entregado por el cliente (en efectivo)
    private double cambio;      // Vuelto o cambio devuelto al cliente
    private String referencia;  // Número de referencia, voucher o transacción bancaria
    private Date fechaPago;     // Fecha exacta en la que se realizó el pago
    /**
     * Constructor por defecto (vacío).
     * Permite instanciar la clase sin valores iniciales.
     */
    public Pago() {
    }
    /**
     * Constructor parametrizado.
     * Permite inicializar rápidamente los datos más esenciales del pago.
     */
    public Pago(int idPedido, String tipo, double monto) {
        this.idPedido = idPedido;
        this.tipo = tipo;
        this.monto = monto;
    }
    /** Obtiene el ID del pago. */
    public int getIdPago() {
        return idPago;
    }
    /** Asigna o modifica el ID del pago. */
    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }
    /** Obtiene el ID del pedido asociado. */
    public int getIdPedido() {
        return idPedido;
    }
    /** Asigna o modifica el ID del pedido asociado. */
    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }
    /** Obtiene el tipo o método de pago. */
    public String getTipo() {
        return tipo;
    }
    /** Asigna o modifica el tipo o método de pago. */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    /** Obtiene el monto total del pago. */
    public double getMonto() {
        return monto;
    }
    /** Asigna o modifica el monto total del pago. */
    public void setMonto(double monto) {
        this.monto = monto;
    }
    /** Obtiene el dinero recibido por parte del cliente. */
    public double getRecibido() {
        return recibido;
    }
    /** Asigna o modifica el dinero recibido. */
    public void setRecibido(double recibido) {
        this.recibido = recibido;
    }
    /** Obtiene el cambio devuelto al cliente. */
    public double getCambio() {
        return cambio;
    }
    /** Asigna o modifica el cambio devuelto. */
    public void setCambio(double cambio) {
        this.cambio = cambio;
    }
    /** Obtiene la referencia o comprobante de pago. */
    public String getReferencia() {
        return referencia;
    }
    /** Asigna o modifica la referencia o comprobante de pago. */
    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
    /** Obtiene la fecha en la que se efectuó el pago. */
    public Date getFechaPago() {
        return fechaPago;
    }
    /** Asigna o modifica la fecha del pago. */
    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }
}
