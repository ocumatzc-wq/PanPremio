package modelo;

import java.util.ArrayList;
import java.util.List;

public class MenuCompleto extends ItemVenta {
    private List<ProductoIndividual> productos;
    private double descuento;

    public MenuCompleto(String nombreMenu, double precioTotal) {
        super(nombreMenu, precioTotal);
        this.productos = new ArrayList<>();
        this.descuento = 0.0;
    }

    // Método de Composición: Un menú se compone de varios productos individuales
    public void agregarProducto(ProductoIndividual producto) {
        this.productos.add(producto);
    }

    @Override
    public double getPrecio() {
        return this.precio - descuento;
    }

    @Override
    public String getNombre() {
        return this.nombre;
    }

    @Override
    public String getDetalles() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Menú Combo: ").append(nombre).append(" (Precio: Q").append(getPrecio()).append(") ---\n");
        sb.append("   Contenido del menú:\n");
        for (ProductoIndividual p : productos) {
            sb.append("   - ").append(p.getNombre()).append(" [").append(p.getCategoria()).append("]\n");
        }
        return sb.toString();
    }

    public List<ProductoIndividual> getProductos() { return productos; }
    public double getDescuento() { return descuento; }
    public void setDescuento(double descuento) { this.descuento = descuento; }
}