package modelo;

public class ProductoIndividual extends ItemVenta {
    private int idProducto;
    private String categoria;
    private int existencia;

    public ProductoIndividual(int idProducto, String categoria, String nombre, double precio, int existencia) {
        super(nombre, precio);
        this.idProducto = idProducto;
        this.categoria = categoria;
        this.existencia = existencia;
    }

    public ProductoIndividual(String categoria, String nombre, double precio, int existencia) {
        super(nombre, precio);
        this.categoria = categoria;
        this.existencia = existencia;
    }

    @Override
    public double getPrecio() {
        return this.precio;
    }

    @Override
    public String getNombre() {
        return this.nombre;
    }

    @Override
    public String getDetalles() {
        return "Producto: " + nombre + " | Categoría: " + categoria + " | Precio: Q" + precio;
    }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public int getExistencia() { return existencia; }
    public void setExistencia(int existencia) { this.existencia = existencia; }
}
