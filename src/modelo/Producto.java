package modelo;

public class Producto {
    private int idProducto;
    private String categoria;
    private String nombre;
    private double precio;
    private int existencia;

    public Producto() {
    }

    public Producto(int idProducto, String categoria, String nombre, double precio, int existencia) {
        this.idProducto = idProducto;
        this.categoria = categoria;
        this.nombre = nombre;
        this.precio = precio;
        this.existencia = existencia;
    }

    public Producto(String categoria, String nombre, double precio, int existencia) {
        this.categoria = categoria;
        this.nombre = nombre;
        this.precio = precio;
        this.existencia = existencia;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getExistencia() {
        return existencia;
    }

    public void setExistencia(int existencia) {
        this.existencia = existencia;
    }
}
