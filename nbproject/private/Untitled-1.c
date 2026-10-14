#include <stdio.h> 
#define MAX 100 
/* ========================================== 
VARIABLES GLOBALES 
========================================== */ 
int codigo[MAX]; 
char nombre[MAX][30]; 
float precio[MAX]; 
int cantidad[MAX]; 
int totalProductos = 0; 
/* ========================================== 
PROTOTIPOS DE LOS MODULOS 
========================================== */ 
void MisDatos(); 
int buscarProducto(int cod); 
float calcularTotalVenta(int unidades, float precioUnitario); 
float calcularValorInventario(); 
void registrarProducto(); 
void mostrarInventario(); 
void registrarVenta(); 
void reabastecer(); 
void generarReporte(); 
void aplicarVenta(int *existencias, int unidades); 
/* ========================================== 
PROCEDIMIENTO: MIS DATOS 
========================================== */ 
void MisDatos() 
{ 
} 
printf("\n====================================\n"); 
printf("Nombre: Carlos Andres Trujillo Velasquez\n"); 
printf("Carnet: 5990-24-25509\n"); 
printf("Sistema de Gestion de Inventario\n"); 
printf("====================================\n"); 
/* ========================================== 
FUNCION: BUSCAR PRODUCTO POR CODIGO 
Devuelve la posicion o -1 si no existe 
========================================== */ 
int buscarProducto(int cod) 
{ 
    int i; 
 
    for (i = 0; i < totalProductos; i++) 
    { 
        if (codigo[i] == cod) 
        { 
            return i; 
        } 
    } 
 
    return -1; 
} 
 
/* ========================================== 
   FUNCION: CALCULAR TOTAL DE UNA VENTA 
   ========================================== */ 
 
float calcularTotalVenta(int unidades, float precioUnitario) 
{ 
    float total; 
 
    total = unidades * precioUnitario; 
 
    return total; 
} 
/* ========================================== 
FUNCION: CALCULAR VALOR DEL INVENTARIO 
========================================== */ 
float calcularValorInventario() 
{ 
} 
int i; 
float total = 0; 
for (i = 0; i < totalProductos; i++) 
{ 
} 
total = total + cantidad[i] * precio[i]; 
return total; 
/* ========================================== 
PROCEDIMIENTO: REGISTRAR PRODUCTO 
========================================== */ 
void registrarProducto() 
{ 
int cod; 
char nom[30]; 
    float prec; 
    int cant; 
 
    MisDatos(); 
 
    if (totalProductos >= MAX) 
    { 
        printf("El inventario esta lleno.\n"); 
        return; 
    } 
 
    printf("Codigo del producto: "); 
    if (scanf("%d", &cod) != 1) 
    { 
        printf("Codigo no valido.\n"); 
        return; 
    } 
 
    if (buscarProducto(cod) != -1) 
    { 
        printf("El codigo ya existe.\n"); 
        return; 
    } 
 
    printf("Nombre sin espacios: "); 
    scanf("%29s", nom); 
 
    printf("Precio del producto: Q"); 
    if (scanf("%f", &prec) != 1) 
    { 
        printf("Precio no valido.\n"); 
        return; 
    } 
 
    printf("Cantidad inicial: "); 
    if (scanf("%d", &cant) != 1) 
    { 
        printf("Cantidad no valida.\n"); 
        return; 
    } 
 
    if (prec < 0 || cant < 0) 
    { 
        printf("El precio y la cantidad no pueden ser negativos.\n"); 
        return; 
    } 
 
    codigo[totalProductos] = cod; 
 
    snprintf(nombre[totalProductos], 
             sizeof(nombre[totalProductos]), "%s", nom); 
 
precio[totalProductos] = prec; 
cantidad[totalProductos] = cant; 
totalProductos++; 
printf("\nProducto registrado correctamente.\n"); 
} 
/* ========================================== 
PROCEDIMIENTO: MOSTRAR INVENTARIO 
========================================== */ 
void mostrarInventario() 
{ 
int i; 
MisDatos(); 
if (totalProductos == 0) 
{ 
} 
printf("No hay productos registrados.\n"); 
return; 
printf("\n========== INVENTARIO ==========\n"); 
for (i = 0; i < totalProductos; i++) 
{ 
} 
} 
printf("\nCodigo: %d\n", codigo[i]); 
printf("Nombre: %s\n", nombre[i]); 
printf("Precio: Q%.2f\n", precio[i]); 
printf("Existencias: %d\n", cantidad[i]); 
/* ========================================== 
PROCEDIMIENTO: MODIFICAR EXISTENCIAS 
PARAMETRO POR REFERENCIA 
========================================== */ 
void aplicarVenta(int *existencias, int unidades) 
{ 
} 
*existencias = *existencias - unidades; 
/* ========================================== 
PROCEDIMIENTO: REGISTRAR VENTA 
========================================== */ 
void registrarVenta() 
{ 
int cod; 
    int indice; 
    int unidades; 
    float total; 
 
    MisDatos(); 
 
    printf("Codigo del producto: "); 
    if (scanf("%d", &cod) != 1) 
    { 
        printf("Codigo no valido.\n"); 
        return; 
    } 
 
    indice = buscarProducto(cod); 
 
    if (indice == -1) 
    { 
        printf("Producto no encontrado.\n"); 
        return; 
    } 
 
    printf("\nProducto: %s\n", nombre[indice]); 
    printf("Precio: Q%.2f\n", precio[indice]); 
    printf("Existencias: %d\n", cantidad[indice]); 
 
    printf("Unidades a vender: "); 
    if (scanf("%d", &unidades) != 1) 
    { 
        printf("Cantidad no valida.\n"); 
        return; 
    } 
 
    if (unidades <= 0) 
    { 
        printf("Debe vender al menos una unidad.\n"); 
        return; 
    } 
 
    if (unidades > cantidad[indice]) 
    { 
        printf("No hay suficientes existencias.\n"); 
        return; 
    } 
 
    total = calcularTotalVenta(unidades, precio[indice]); 
 
    aplicarVenta(&cantidad[indice], unidades); 
 
    printf("\n========== FACTURA ==========\n"); 
    printf("Producto: %s\n", nombre[indice]); 
    printf("Unidades vendidas: %d\n", unidades); 
    printf("Precio unitario: Q%.2f\n", precio[indice]); 
printf("Total a pagar: Q%.2f\n", total); 
printf("Existencias restantes: %d\n", 
cantidad[indice]); 
} 
/* ========================================== 
PROCEDIMIENTO: REABASTECER PRODUCTO 
========================================== */ 
void reabastecer() 
{ 
int cod; 
int indice; 
int unidades; 
MisDatos(); 
printf("Codigo del producto: "); 
if (scanf("%d", &cod) != 1) 
{ 
} 
printf("Codigo no valido.\n"); 
return; 
indice = buscarProducto(cod); 
    if (indice == -1) 
    { 
        printf("Producto no encontrado.\n"); 
        return; 
    } 
 
    printf("Producto: %s\n", nombre[indice]); 
    printf("Existencias actuales: %d\n", cantidad[indice]); 
 
    printf("Unidades que ingresan: "); 
    if (scanf("%d", &unidades) != 1) 
    { 
        printf("Cantidad no valida.\n"); 
        return; 
    } 
 
    if (unidades <= 0) 
    { 
        printf("Debe ingresar una cantidad positiva.\n"); 
        return; 
    } 
 
    cantidad[indice] = cantidad[indice] + unidades; 
 
    printf("Reabastecimiento realizado correctamente.\n"); 
    printf("Existencias nuevas: %d\n", cantidad[indice]); 
} 
 
/* ========================================== 
   PROCEDIMIENTO: GENERAR REPORTE 
   ========================================== */ 
 
void generarReporte() 
{ 
    int i; 
    int pocos = 0; 
    float valor; 
 
    MisDatos(); 
 
    valor = calcularValorInventario(); 
 
    for (i = 0; i < totalProductos; i++) 
    { 
        if (cantidad[i] <= 5) 
        { 
            pocos++; 
        } 
    } 
 
    printf("\n======= REPORTE DE INVENTARIO =======\n"); 
    printf("Productos registrados: %d\n", totalProductos); 
    printf("Valor del inventario: Q%.2f\n", valor); 
    printf("Productos con pocas existencias: %d\n", pocos); 
 
    printf("\n--- PRODUCTOS CON 5 UNIDADES O MENOS ---\n"); 
 
    for (i = 0; i < totalProductos; i++) 
    { 
        if (cantidad[i] <= 5) 
        { 
            printf("Codigo: %d | ", codigo[i]); 
            printf("Nombre: %s | ", nombre[i]); 
            printf("Existencias: %d\n", cantidad[i]); 
        } 
    } 
} 
 
/* ========================================== 
   PROGRAMA PRINCIPAL 
   ========================================== */ 
 
int main() 
{ 
    int opcion; 
 
    do 
    { 
        MisDatos(); 
 
        printf("\n========== MENU PRINCIPAL ==========\n"); 
        printf("1. Registrar producto\n"); 
        printf("2. Mostrar inventario\n"); 
        printf("3. Buscar producto\n"); 
        printf("4. Registrar venta\n"); 
        printf("5. Reabastecer inventario\n"); 
        printf("6. Generar reporte\n"); 
        printf("7. Salir\n"); 
        printf("Seleccione una opcion: "); 
 
        if (scanf("%d", &opcion) != 1) 
        { 
            printf("Debe ingresar un numero.\n"); 
            return 1; 
        } 
 
        switch (opcion) 
        { 
            case 1: 
                registrarProducto(); 
                break; 
 
            case 2: 
                mostrarInventario(); 
                break; 
 
            case 3: 
            { 
                int cod; 
                int indice; 
 
                MisDatos(); 
 
                printf("Codigo del producto a buscar: "); 
 
                if (scanf("%d", &cod) != 1) 
                { 
                    printf("Codigo no valido.\n"); 
                    break; 
                } 
 
                indice = buscarProducto(cod); 
 
                if (indice == -1) 
                { 
                    printf("Producto no encontrado.\n"); 
                } 
                else 
                { 
                    printf("\nProducto encontrado:\n"); 
                    printf("Codigo: %d\n", codigo[indice]); 
                    printf("Nombre: %s\n", nombre[indice]); 
                    printf("Precio: Q%.2f\n", precio[indice]); 
                    printf("Existencias: %d\n", cantidad[indice]); 
                } 
 
                break; 
            } 
 
            case 4: 
                registrarVenta(); 
                break; 
 
            case 5: 
                reabastecer(); 
                break; 
 
            case 6: 
                generarReporte(); 
                break; 
 
            case 7: 
                MisDatos(); 
                printf("Saliendo del sistema.\n"); 
                break; 
 
default: 
MisDatos(); 
printf("Opcion incorrecta.\n"); 
} 
} while (opcion != 7); 
return 0; 
}