package modelo;

import java.sql.Date;

public class Canje {
    private int idCanje;
    private int idCliente;
    private int idRecompensa;
    private int puntosUtilizados;
    private Date fechaCanje;

    public Canje() {}

    public Canje(int idCliente, int idRecompensa, int puntosUtilizados) {
        this.idCliente = idCliente;
        this.idRecompensa = idRecompensa;
        this.puntosUtilizados = puntosUtilizados;
    }

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