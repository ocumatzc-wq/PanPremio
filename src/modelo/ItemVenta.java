package modelo;

public abstract class ItemVenta {
    protected String nombre;
    protected double precio;

    public ItemVenta(String nombre, double precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    // Métodos abstractos que implementarán las subclases (Polimorfismo)
    public abstract double getPrecio();
    public abstract String getNombre();
    public abstract String getDetalles();
}