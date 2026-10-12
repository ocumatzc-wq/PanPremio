/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package projecto.pkg1;

/**
 *
 * @author amilc
 */
public class Projecto1 {
    
//como atributos


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
      Vehiculo carro1;
       carro1= new Vehiculo();
       carro1.marca="Toyota";
       carro1.modelo="Corola";
       carro1.año=2025;
       carro1.precio=105000;
       
       carro1.mostrardatos();
       
      Vehiculo carro2;
       carro2= new Vehiculo();
       
       carro2.marca="Masda";
       carro2.modelo="Tilin";
       carro2.año=2002;
       carro2.precio=12500;
       carro2.mostrardatos();
       
      Vehiculo carro3=new Vehiculo();

      FrmVehiculo ventana = new FrmVehiculo(); 
ventana.setVisible(true); 




    }
    
}
