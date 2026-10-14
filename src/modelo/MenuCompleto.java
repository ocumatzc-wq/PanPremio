package modelo;

import java.util.ArrayList; // Importa ArrayList para crear una lista dinámica de productos.
import java.util.List; // Importa List para declarar la colección mediante una interfaz.
/**
 * Representa un menú completo compuesto por varios productos.
 * Hereda de ItemVenta, por lo que implementa los métodos necesarios para obtener el nombre, el precio, los detalles y los puntos del menú.
 */
public class MenuCompleto extends ItemVenta {
    private int idMenu; // Identificador único del menú.
    private String codigo; // Código que identifica al menú dentro del sistema.
    private List<ProductoIndividual> productos; // Lista de productos individuales que componen el menú.
    private double descuento; // Importe que se descontará del precio base del menú.
    private boolean activo = true; // Indica si el menú está disponible o activo en el sistema. Su valor inicial es true.
    private static final int PUNTOS_MENU = 8; // Constante que establece los puntos otorgados por cada menú. Al ser static, pertenece a la clase. Al ser final, su valor no puede reasignarse.
    /**
     * Constructor de MenuCompleto.
     * Inicializa el nombre y el precio base mediante el constructor de la clase padre ItemVenta.
     * También crea una lista vacía de productos y establece el descuento inicial en cero.
     * @param nombreMenu Nombre del menú.
     * @param precioTotal Precio base del menú antes del descuento.
     */
    public MenuCompleto(String nombreMenu, double precioTotal) {
        super(nombreMenu, precioTotal); // Llama al constructor de ItemVenta para inicializar los atributos heredados nombre y precio.
        this.productos = new ArrayList<>(); // Crea la lista donde se almacenarán los productos del menú.
        this.descuento = 0.0; // Inicializa el descuento sin aplicar ninguna rebaja.
    }
    /**
     * Agrega un producto individual a la lista del menú.
     * @param producto Producto que formará parte del menú.
     */
    public void agregarProducto(ProductoIndividual producto) {
        this.productos.add(producto); // Añade el producto recibido a la lista de productos del menú.
    }
    /**
     * Obtiene el precio final del menú.
     * El precio final se calcula restando el descuento al precio base heredado de ItemVenta.
     * @return Precio del menú después de aplicar el descuento.
     */
    @Override
    public double getPrecio() {
        return this.precio - descuento; // Devuelve el precio base menos el importe del descuento.
    }
    /**
     * Obtiene el nombre del menú.
     * @return Nombre del menú.
     */
    @Override
    public String getNombre() { // Devuelve el nombre heredado de ItemVenta.
        return this.nombre;
    }
    /**
     * Genera una descripción del menú y de los productos que contiene.
     * Incluye el nombre, el precio final y la categoría de cada producto que forma parte del menú.
     * @return Cadena de texto con los detalles del menú.
     */
    @Override
    public String getDetalles() {
        StringBuilder sb = new StringBuilder(); // StringBuilder permite construir el texto agregando fragmentos sin crear tantas cadenas intermedias.
        sb.append("--- Menú Combo: ").append(nombre).append(" (Precio: Q").append(getPrecio()).append(") ---\n"); // Agrega el encabezado con el nombre y el precio final del menú.
        sb.append("   Contenido del menú:\n"); // Agrega el encabezado de la lista de productos.
        for (ProductoIndividual p : productos) { // Recorre todos los productos almacenados en la lista.
            sb.append("   - ").append(p.getNombre()).append(" [").append(p.getCategoria()).append("]\n"); // Agrega el nombre y la categoría del producto actual.
        }
        return sb.toString(); // Devuelve el texto completo con la información del menú.
    }
    /**
     * Calcula los puntos otorgados por la compra del menú.
     * Actualmente, todos los menús generan la misma cantidad de puntos.
     * @return Cantidad fija de puntos definida por PUNTOS_MENU.
     */
    @Override
    public int calcularPuntos() { // Devuelve los 8 puntos establecidos en la constante.
        return PUNTOS_MENU;
    }
    /**
     * Obtiene el identificador del menú.
     * @return Identificador del menú.
     */
    public int getIdMenu() {
        return idMenu;
    }
    /**
     * Asigna el identificador del menú.
     * @param idMenu Identificador que se desea establecer.
     */
    public void setIdMenu(int idMenu) {
        this.idMenu = idMenu;
    }
    /**
     * Obtiene el código del menú.
     * @return Código del menú.
     */
    public String getCodigo() {
        return codigo;
    }
    /**
     * Asigna el código del menú.
     * @param codigo Código que se desea establecer.
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    /**
     * Obtiene la lista de productos que componen el menú.
     * @return Lista de productos individuales del menú.
     */
    public List<ProductoIndividual> getProductos() { return productos; }
    /**
     * Obtiene el importe del descuento aplicado al menú.
     * @return Importe del descuento.
     */
    public double getDescuento() { return descuento; }
    /**
     * Establece el importe del descuento del menú.
     * @param descuento Importe que se desea descontar del precio base.
     */
    public void setDescuento(double descuento) { this.descuento = descuento; }
    /**
     * Comprueba si el menú está activo.
     * @return true si el menú está activo; false si está inactivo.
     */
    public boolean isActivo() {
        return activo;
    }
    /**
     * Cambia el estado del menú.
     * @param activo true para activar el menú; false para desactivarlo.
     */
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
