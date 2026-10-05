package proyectooracle;

import modelo.ItemVenta;
import modelo.MenuCompleto;
import modelo.ProductoIndividual;
import java.util.ArrayList;
import java.util.List;

public class ProyectoOracle {

    public static void main(String[] args) {
        // 1. Crear Productos Individuales
        ProductoIndividual p1 = new ProductoIndividual(1, "Sándwich", "Sándwich Jamón y Queso", 25.00, 20);
        ProductoIndividual p2 = new ProductoIndividual(2, "Bebida", "Café Americano", 12.00, 30);
        ProductoIndividual p3 = new ProductoIndividual(3, "Postre", "Donado de Chocolate", 8.00, 15);

        // 2. Crear un Menú Completo (Composición)
        MenuCompleto menuEjecutivo = new MenuCompleto("Menú Desayuno Especial", 40.00);
        menuEjecutivo.agregarProducto(p1);
        menuEjecutivo.agregarProducto(p2);
        menuEjecutivo.agregarProducto(p3);

        // 3. Probar Polimorfismo usando la lista genérica de ItemVenta
        List<ItemVenta> carrito = new ArrayList<>();
        carrito.add(p1);            // Producto individual
        carrito.add(menuEjecutivo); // Menú compuesto

        System.out.println("=== DEMOSTRACIÓN DE POLIMORFISMO Y COMPOSICIÓN ===");
        double total = 0;
        for (ItemVenta item : carrito) {
            System.out.println(item.getDetalles());
            total += item.getPrecio();
        }

        System.out.println("Total a Pagar en Carrito: Q" + total);
    }
}