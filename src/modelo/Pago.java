package modelo;

import java.sql.Date;

public class Pago {
    private int idPago;
    private int idPedido;
    private double monto;
    private String metodoPago; // EFECTIVO, TARJETA, PUNTOS
    private Date fechaPago;

    public Pago() {}

    public Pago(int idPedido, double monto, String metodoPago) {
        this.idPedido = idPedido;
        this.monto = monto;
        this.metodoPago = metodoPago;
    }

    public int getIdPago() { return idPago; }
    public void setIdPago(int idPago) { this.idPago = idPago; }
    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }
    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public Date getFechaPago() { return fechaPago; }
    public void setFechaPago(Date fechaPago) { this.fechaPago = fechaPago; }
}