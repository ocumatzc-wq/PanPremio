package modelo;
/**
 * Representa un detalle individual de un pedido.
 * Contiene información sobre el producto o menú solicitado, la cantidad, el precio unitario, el subtotal y los puntos asociados.
 */
public class DetallePedido {
    private int idDetalle; // Identificador único del detalle del pedido.
    private int idPedido; // Identificador del pedido al que pertenece este detalle.
    private int idProducto; // Identificador del producto incluido en el detalle, si corresponde.
    private int idMenu; // Identificador del menú incluido en el detalle, si corresponde.
    private int cantidad; // Cantidad de unidades solicitadas del producto o menú.
    private double precioUnitario; // Precio correspondiente a una unidad del artículo.
    private double subtotal; // Importe total del detalle: cantidad multiplicada por precio unitario.
    private int puntos; // Cantidad de puntos asociada a este detalle del pedido.
    /**
     * Constructor vacío.
     * Permite crear un objeto DetallePedido sin asignar valores inicialmente. Los atributos numéricos comienzan en cero.
     */
    public DetallePedido() {
    }
    /**
     * Constructor que inicializa un detalle con producto, cantidad y precio unitario.
     * También calcula el subtotal automáticamente.
     * @param idProducto Identificador del producto.
     * @param cantidad Cantidad de unidades solicitadas.
     * @param precioUnitario Precio de cada unidad.
     */
    public DetallePedido(int idProducto, int cantidad, double precioUnitario) {
        this.idProducto = idProducto; // Guarda el identificador del producto seleccionado.
        this.cantidad = cantidad; // Guarda la cantidad de unidades solicitadas.
        this.precioUnitario = precioUnitario; // Guarda el precio de cada unidad.
        this.subtotal = cantidad * precioUnitario; // Calcula el importe total del detalle.
    }
    /**
     * Obtiene el identificador del detalle.
     * @return Identificador del detalle del pedido.
     */
    public int getIdDetalle() {
        return idDetalle;
    }
    /**
     * Asigna el identificador del detalle.
     * @param idDetalle Identificador que se desea establecer.
     */
    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }
    /**
     * Obtiene el identificador del pedido al que pertenece el detalle.
     * @return Identificador del pedido.
     */
    public int getIdPedido() {
        return idPedido;
    }
    /**
     * Asigna el identificador del pedido.
     * @param idPedido Identificador del pedido.
     */
    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }
    /**
     * Obtiene el identificador del producto asociado al detalle.
     * @return Identificador del producto.
     */
    public int getIdProducto() {
        return idProducto;
    }
    /**
     * Asigna el identificador del producto.
     * @param idProducto Identificador del producto.
     */
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }
    /**
     * Obtiene el identificador del menú asociado al detalle.
     * @return Identificador del menú.
     */
    public int getIdMenu() {
        return idMenu;
    }
    /**
     * Asigna el identificador del menú.
     * @param idMenu Identificador del menú.
     */
    public void setIdMenu(int idMenu) {
        this.idMenu = idMenu;
    }
    /**
     * Obtiene la cantidad de unidades solicitadas.
     * @return Cantidad del producto o menú.
     */
    public int getCantidad() {
        return cantidad;
    }
    /**
     * Asigna la cantidad de unidades solicitadas.
     * @param cantidad Cantidad que se desea establecer.
     */
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    /**
     * Obtiene el precio de una unidad del artículo.
     * @return Precio unitario.
     */
    public double getPrecioUnitario() {
        return precioUnitario;
    }
    /**
     * Asigna el precio unitario del artículo.
     * @param precioUnitario Precio que se desea establecer.
     */
    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
    /**
     * Obtiene el subtotal del detalle.
     * @return Subtotal calculado para este detalle.
     */
    public double getSubtotal() {
        return subtotal;
    }
    /**
     * Asigna el subtotal del detalle.
     * @param subtotal Importe que se desea establecer.
     */
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
    /**
     * Obtiene los puntos asociados al detalle.
     * @return Cantidad de puntos.
     */
    public int getPuntos() {
        return puntos;
    }
    /**
     * Asigna los puntos asociados al detalle.
     * @param puntos Cantidad de puntos que se desea establecer.
     */
    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }
}
