package modelo;

public class ProductoIndividual extends ItemVenta { // Clase ProductoIndividual que hereda de ItemVenta para representar un producto unitario en el sistema
    private int idProducto; // Identificador único del producto
    private String categoria; // Categoría a la que pertenece el producto
    private int existencia; // Cantidad disponible en stock
    // Constante que define los puntos generados si el producto es un sándwich
    private static final int PUNTOS_SANDWICH = 2;
    // Constructor que inicializa el producto incluyendo su ID
    public ProductoIndividual(int idProducto, String categoria, String nombre, double precio, int existencia) {
        super(nombre, precio); // Llama al constructor de la clase padre (ItemVenta)
        this.idProducto = idProducto;
        this.categoria = categoria;
        this.existencia = existencia;
    }
    // Constructor que inicializa el producto sin ID (para nuevos registros)
    public ProductoIndividual(String categoria, String nombre, double precio, int existencia) {
        super(nombre, precio);
        this.categoria = categoria;
        this.existencia = existencia;
    }
    // Sobrescribe el método para obtener el precio del producto
    @Override
    public double getPrecio() {
        return this.precio;
    }
    // Sobrescribe el método para obtener el nombre del producto
    @Override
    public String getNombre() {
        return this.nombre;
    }
    // Sobrescribe el método para retornar una cadena con los detalles del producto
    @Override
    public String getDetalles() {
        return "Producto: " + nombre + " | Categoría: " + categoria + " | Precio: Q" + precio;
    }
    // Sobrescribe el método para calcular puntos de fidelidad según la categoría
    @Override
    public int calcularPuntos() { // Retorna PUNTOS_SANDWICH si la categoría es "Sandwich" (ignorando mayúsculas/minúsculas), de lo contrario 0
        return "Sandwich".equalsIgnoreCase(categoria) ? PUNTOS_SANDWICH : 0; 
    }
    // Métodos Getters y Setters para acceder y modificar los atributos
    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public int getExistencia() { return existencia; }
    public void setExistencia(int existencia) { this.existencia = existencia; }
}
