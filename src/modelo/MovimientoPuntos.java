package modelo;

import java.sql.Date;

public class MovimientoPuntos {
    private int idMovimiento;
    private int idCliente;
    private String tipo;
    private int puntos;
    private Date fechaMovimiento;
    private String referencia;

    public MovimientoPuntos() {
    }

    public MovimientoPuntos(int idCliente, String tipo, int puntos, String referencia) {
        this.idCliente = idCliente;
        this.tipo = tipo;
        this.puntos = puntos;
        this.referencia = referencia;
    }

    public int getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public Date getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(Date fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
}
