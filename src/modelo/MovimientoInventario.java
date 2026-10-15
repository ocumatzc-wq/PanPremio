package modelo;

import java.sql.Date;
public class MovimientoInventario {
    private int idMovimiento;
    private int idProducto;
    private String tipo;
    private int cantidad;
    private Date fecha;
    private String referencia;
    public MovimientoInventario() {
    }
    public MovimientoInventario(int idProducto, String tipo, int cantidad, String referencia) {
        this.idProducto = idProducto;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.referencia = referencia;
    }
    public int getIdMovimiento() {
        return idMovimiento;
    }
    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }
    public int getIdProducto() {
        return idProducto;
    }
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public int getCantidad() {
        return cantidad;
    }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    public Date getFecha() {
        return fecha;
    }
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
}
