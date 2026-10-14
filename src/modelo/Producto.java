package modelo;
/**
 * Clase que representa el modelo de datos para un Producto.
 * Funciona como un JavaBean / POJO para mapear los artículos o productos disponibles en el inventario del sistema.
 */
public class Producto {
    private int idProducto;       // Identificador único del producto (Clave primaria)
    private String codigo;        // Código de barras o clave alfanumérica única del producto
    private String categoria;     // Categoría a la que pertenece el producto (ej. Bebidas, Abarrotes)
    private String nombre;        // Nombre o descripción corta del producto
    private double precio;        // Precio de venta unitario del producto
    private int existencia;       // Cantidad disponible en stock o inventario
    private boolean activo = true; // Estado del producto: por defecto inicia como activo (true)
    /**
     * Constructor por defecto (vacío).
     * Permite instanciar la clase sin valores iniciales.
     */
    public Producto() {
    }
    /**
     * Constructor parametrizado con ID.
     * Útil cuando se recupera un producto existente de la base de datos (incluyendo su ID).
     */
    public Producto(int idProducto, String categoria, String nombre, double precio, int existencia) {
        this.idProducto = idProducto;
        this.categoria = categoria;
        this.nombre = nombre;
        this.precio = precio;
        this.existencia = existencia;
    }
    /**
     * Constructor parametrizado sin ID.
     * Útil al registrar un producto nuevo antes de que la base de datos le asigne un ID autoincrementable.
     */
    public Producto(String categoria, String nombre, double precio, int existencia) {
        this.categoria = categoria;
        this.nombre = nombre;
        this.precio = precio;
        this.existencia = existencia;
    }
    //Obtiene el ID del producto.
    public int getIdProducto() {
        return idProducto;
    }
    //Asigna o modifica el ID del producto.
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }
    //Obtiene el código de barras o clave del producto.
    public String getCodigo() {
        return codigo;
    }
    //Asigna o modifica el código de barras o clave del producto.
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    //Obtiene la categoría del producto.
    public String getCategoria() {
        return categoria;
    }
    //Asigna o modifica la categoría del producto.
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    //Obtiene el nombre del producto.
    public String getNombre() {
        return nombre;
    }
    //Asigna o modifica el nombre del producto.
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    //Obtiene el precio de venta del producto.
    public double getPrecio() {
        return precio;
    }
    //Asigna o modifica el precio de venta del producto.
    public void setPrecio(double precio) {
        this.precio = precio;
    }
    //Obtiene la cantidad existente en stock.
    public int getExistencia() {
        return existencia;
    }
    //Asigna o modifica la cantidad existente en stock.
    public void setExistencia(int existencia) {
        this.existencia = existencia;
    }
    //Consulta si el producto se encuentra activo en el sistema.
    public boolean isActivo() {
        return activo;
    }
    //Asigna o modifica el estado de actividad del producto.
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
