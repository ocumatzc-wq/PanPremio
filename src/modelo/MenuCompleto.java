package modelo;

import java.util.ArrayList;
import java.util.List;

public class MenuCompleto extends ItemVenta {
    private int idMenu;
    private String codigo;
    private List<ProductoIndividual> productos;
    private double descuento;
    private boolean activo = true;
    private static final int PUNTOS_MENU = 8;

    public MenuCompleto(String nombreMenu, double precioTotal) {
        super(nombreMenu, precioTotal);
        this.productos = new ArrayList<>();
        this.descuento = 0.0;
    }

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

    @Override
    public int calcularPuntos() {
        return PUNTOS_MENU;
    }

    public int getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(int idMenu) {
        this.idMenu = idMenu;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public List<ProductoIndividual> getProductos() { return productos; }

    public double getDescuento() { return descuento; }

    public void setDescuento(double descuento) { this.descuento = descuento; }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
