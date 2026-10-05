package modelo;

public class Recompensa {
    private int idRecompensa;
    private String nombre;
    private int puntosRequeridos;
    private int stockDisponibilidad;

    public Recompensa() {}

    public Recompensa(int idRecompensa, String nombre, int puntosRequeridos, int stockDisponibilidad) {
        this.idRecompensa = idRecompensa;
        this.nombre = nombre;
        this.puntosRequeridos = puntosRequeridos;
        this.stockDisponibilidad = stockDisponibilidad;
    }

    public int getIdRecompensa() { return idRecompensa; }
    public void setIdRecompensa(int idRecompensa) { this.idRecompensa = idRecompensa; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getPuntosRequeridos() { return puntosRequeridos; }
    public void setPuntosRequeridos(int puntosRequeridos) { this.puntosRequeridos = puntosRequeridos; }
    public int getStockDisponibilidad() { return stockDisponibilidad; }
    public void setStockDisponibilidad(int stockDisponibilidad) { this.stockDisponibilidad = stockDisponibilidad; }
}
