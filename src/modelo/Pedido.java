package modelo;

import java.sql.Date;

public class Pedido {
    private int idPedido;
    private int idCliente;
    private Date fechaPedido;
    private String estado;

    public Pedido() {
    }

    public Pedido(int idCliente, String estado) {
        this.idCliente = idCliente;
        this.estado = estado;
    }

    public Pedido(int idPedido, int idCliente, Date fechaPedido, String estado) {
        this.idPedido = idPedido;
        this.idCliente = idCliente;
        this.fechaPedido = fechaPedido;
        this.estado = estado;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public Date getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(Date fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
