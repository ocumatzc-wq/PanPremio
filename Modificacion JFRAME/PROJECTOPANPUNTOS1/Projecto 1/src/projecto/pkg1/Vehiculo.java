/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projecto.pkg1;

/**
 *
 * @author amilc
 */
public class Vehiculo {
    String marca="";
    String modelo="";
    int año=0;
    double precio=0;
    String placa;
    String llanta;
    
    public void setPrecio(double precio) {

    if (precio > 0) {
        this.precio = precio;
    } else {
        System.out.println("Error: el precio debe ser mayor que cero");
    }
}

    Vehiculo(){}
    Vehiculo(String marca, String modelo, int año, double precio){
        this.marca=marca;
        this.modelo=modelo;
        this.año=año;
        this.precio=precio;
        this.llanta=llanta;
        
        



        
    }
    
    void mostrardatos(){
        System.out.println("marca: "+marca);
        System.out.println("modelo: "+modelo);
        System.out.println("año: "+año);
        System.out.println("precio: "+precio);
        System.out.println();
        System.out.println("Información de la llanta:");

        
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public int getAño() {
        return año;
    }

    public double getPrecio() {
        return precio;
    }

    public String getPlaca() {
        return placa;
    }

    public String getLlanta() {
        return llanta;
    }

}
