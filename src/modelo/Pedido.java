package modelo;

import java.sql.Date;
/**
 * Clase que representa el modelo de datos para un Pedido.
 * Funciona como un JavaBean / POJO para mapear los pedidos realizados en el sistema.
 */
public class Pedido {
    private int idPedido;         // Identificador único del pedido (Clave primaria)
    private int idCliente;        // Identificador del cliente que realizó el pedido
    private Date fechaPedido;     // Fecha en la que se efectuó o registró el pedido
    private String estado;        // Estado actual del pedido (ej. Pendiente, Completado, Cancelado)
    private double total;         // Monto total a pagar por el pedido
    private int puntosGenerados;  // Puntos de fidelidad o recompensa acumulados con este pedido
    /**
     * Constructor por defecto (vacío).
     * Permite instanciar la clase sin valores iniciales.
     */
    public Pedido() {
    }
    /**
     * Constructor parametrizado básico.
     * Útil al crear un pedido nuevo donde solo se requiere el cliente y su estado inicial.
     */
    public Pedido(int idCliente, String estado) {
        this.idCliente = idCliente;
        this.estado = estado;
    }
    /**
     * Constructor parametrizado completo.
     * Permite inicializar un pedido con sus datos principales (ideal para consultas a la base de datos).
     */
    public Pedido(int idPedido, int idCliente, Date fechaPedido, String estado) {
        this.idPedido = idPedido;
        this.idCliente = idCliente;
        this.fechaPedido = fechaPedido;
        this.estado = estado;
    }
    //Obtiene el ID del pedido.
    public int getIdPedido() {
        return idPedido;
    }
    //Asigna o modifica el ID del pedido.
    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }
    //Obtiene el ID del cliente asociado al pedido.
    public int getIdCliente() {
        return idCliente;
    }
    //Asigna o modifica el ID del cliente.
    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }
    //Obtiene la fecha del pedido.
    public Date getFechaPedido() {
        return fechaPedido;
    }
    //Asigna o modifica la fecha del pedido.
    public void setFechaPedido(Date fechaPedido) {
        this.fechaPedido = fechaPedido;
    }
    //Obtiene el estado actual del pedido.
    public String getEstado() {
        return estado;
    }
    //Asigna o modifica el estado actual del pedido.
    public void setEstado(String estado) {
        this.estado = estado;
    }
    //Obtiene el monto total del pedido.
    public double getTotal() {
        return total;
    }
    //Asigna o modifica el monto total del pedido.
    public void setTotal(double total) {
        this.total = total;
    }
    //Obtiene la cantidad de puntos generados por el pedido.
    public int getPuntosGenerados() {
        return puntosGenerados;
    }
    //Asigna o modifica los puntos generados.
    public void setPuntosGenerados(int puntosGenerados) {
        this.puntosGenerados = puntosGenerados;
    }
}
