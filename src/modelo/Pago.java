package modelo;

import java.sql.Date;

public class Pago {
    private int idPago;
    private int idPedido;
    private String tipo;
    private double monto;
    private double recibido;
    private double cambio;
    private String referencia;
    private Date fechaPago;

    public Pago() {
    }

    public Pago(int idPedido, String tipo, double monto) {
        this.idPedido = idPedido;
        this.tipo = tipo;
        this.monto = monto;
    }

    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public double getRecibido() {
        return recibido;
    }

    public void setRecibido(double recibido) {
        this.recibido = recibido;
    }

    public double getCambio() {
        return cambio;
    }

    public void setCambio(double cambio) {
        this.cambio = cambio;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Date getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }
}
