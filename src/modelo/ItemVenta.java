package modelo;
/**
 * Clase abstracta que representa un elemento que puede venderse dentro del sistema.
 * Sirve como clase base para otros tipos de artículos, como productos individuales o menús.
 * Define atributos y comportamientos comunes que las subclases deberán implementar de acuerdo con sus características.
 */
public abstract class ItemVenta { // Constructor que recibe los datos del producto.
    protected String nombre; // Nombre del artículo que se ofrece a la venta. protected permite acceder a este atributo desde las subclases.
    protected double precio; // Precio del artículo. Las subclases pueden acceder directamente a este atributo.
/**
     * Constructor de la clase abstracta.
     * Inicializa el nombre y el precio del artículo.
     * Este constructor puede ser invocado por las subclases mediante super(nombre, precio).
     * @param nombre Nombre del artículo.
     * @param precio Precio del artículo.
     */
    public ItemVenta(String nombre, double precio) {
        this.nombre = nombre; // Asigna el nombre recibido al atributo de la instancia.
        this.precio = precio; // Asigna el precio recibido al atributo de la instancia.
    }

    // Métodos abstractos que implementarán las subclases (Polimorfismo)
    public abstract double getPrecio();
    public abstract String getNombre();
    public abstract String getDetalles();
    public abstract int calcularPuntos();
}