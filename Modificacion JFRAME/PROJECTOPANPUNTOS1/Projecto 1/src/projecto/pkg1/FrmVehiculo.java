package projecto.pkg1;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

/**
 
 * @author amilc
 */
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
public class FrmVehiculo extends JFrame {
    JLabel lblTitulo = new JLabel("REGISTRO DE VEHÍCULO");
    JLabel lblMarca = new JLabel("Marca:");
    JTextField txtMarca = new JTextField(); 
    JLabel lblModelo = new JLabel("Modelo:");
JTextField txtModelo = new JTextField();
JLabel lblAnio = new JLabel("Año:");
JTextField txtAnio = new JTextField();
JLabel lblPrecio = new JLabel("Precio:"); 
 JTextField txtPrecio = new JTextField();
JButton btnGuardar = new JButton("Guardar");


//como atributos
JTable tablaVehiculos;
 DefaultTableModel modeloTabla;
 JScrollPane scrollTabla; 

    public FrmVehiculo() {
            
    setTitle("Registro de Vehículo");
    setSize(500, 400);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setLayout(null);
     lblTitulo.setBounds(150, 20, 200, 30); 
add(lblTitulo); 
    lblMarca.setBounds(50, 70, 100, 30);
add(lblMarca);
txtMarca.setBounds(150, 70, 200, 30); 
add(txtMarca); 
lblModelo.setBounds(50, 110, 100, 30);
add(lblModelo);
lblAnio.setBounds(50, 150, 100, 30);
add(lblAnio);

txtAnio.setBounds(150, 150, 200, 30);
add(txtAnio);

txtModelo.setBounds(150, 110, 200, 30);
add(txtModelo);
lblPrecio.setBounds(50, 190, 100, 30); 
add(lblPrecio);
 txtPrecio.setBounds(150, 190, 200, 30); 
add(txtPrecio); 
btnGuardar.setBounds(150, 250, 120, 35);
add(btnGuardar);
     
    btnGuardar.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //System.out.println("Presiono el boton guardar");
            String marca=txtMarca.getText();
            String modelo=txtModelo.getText();
            double precio=Double.parseDouble(txtPrecio.getText());
            int anio = Integer.parseInt(txtAnio.getText());
            System.out.println("Marca: "+ marca);
            System.out.println("Modelo: "+ modelo);
            System.out.println("año: "+ anio);
            System.out.println("Precio: "+ precio);
            JOptionPane.showMessageDialog(FrmVehiculo.this, "Vehículo guardado correctamente");
            modeloTabla.addColumn("Marca");
            modeloTabla.addColumn("Modelo");
            modeloTabla.addColumn("Año");
            tablaVehiculos = new JTable(modeloTabla);
            scrollTabla = new JScrollPane(tablaVehiculos);
            scrollTabla.setBounds(30, 250, 450, 180);
            add(scrollTabla);
            modeloTabla.addRow(new Object[]{
                vehiculo.getMarca(),
                vehiculo.getModelo(), vehiculo.getAnio()
            });        
            Vehiculo vehiculo =
                    new Vehiculo(
                            marca,
                            modelo,
                            anio,
                            precio
                            
                            
                    );
            vehiculo.mostrardatos();
        }
    });
    
}

    private static class vehiculo {

        private static Object getAnio() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private static Object getModelo() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private static Object getMarca() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        public vehiculo() {
        }
    }
    }

