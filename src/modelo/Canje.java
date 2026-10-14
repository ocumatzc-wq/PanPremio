package modelo;

import java.sql.Date;
// Clase Canje que representa el registro de intercambio de puntos de fidelidad por recompensas
public class Canje {
    // Atributos de la clase que almacenan la información del canje
    private int idCanje; // Identificador único del canje (Clave primaria)
    private int idCliente; // Identificador del cliente que realiza el canje
    private int idRecompensa; // Identificador de la recompensa que se está canjeando
    private int puntosUtilizados; // Cantidad de puntos gastados en el canje
    private Date fechaCanje; // Fecha en la que se efectuó el canje
    // Constructor por defecto (vacío) para instanciar la clase sin valores iniciales
    public Canje() {}
    // Constructor parametrizado para inicializar un nuevo canje con los datos esenciales
    public Canje(int idCliente, int idRecompensa, int puntosUtilizados) {
        this.idCliente = idCliente;
        this.idRecompensa = idRecompensa;
        this.puntosUtilizados = puntosUtilizados;
    }
    // Métodos Getters y Setters para acceder y modificar los atributos de la clase
    public int getIdCanje() { return idCanje; }
    public void setIdCanje(int idCanje) { this.idCanje = idCanje; }
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public int getIdRecompensa() { return idRecompensa; }
    public void setIdRecompensa(int idRecompensa) { this.idRecompensa = idRecompensa; }
    public int getPuntosUtilizados() { return puntosUtilizados; }
    public void setPuntosUtilizados(int puntosUtilizados) { this.puntosUtilizados = puntosUtilizados; }
    public Date getFechaCanje() { return fechaCanje; }
    public void setFechaCanje(Date fechaCanje) { this.fechaCanje = fechaCanje; }
}