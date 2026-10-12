package modelo;

public class Recompensa {
    private int idRecompensa;
    private String nombre;
    private int puntosRequeridos;
    private int idProducto;
    private int idMenu;
    private boolean activo = true;

    public Recompensa() {
    }

    public Recompensa(int idRecompensa, String nombre, int puntosRequeridos) {
        this.idRecompensa = idRecompensa;
        this.nombre = nombre;
        this.puntosRequeridos = puntosRequeridos;
    }

    public int getIdRecompensa() {
        return idRecompensa;
    }

    public void setIdRecompensa(int idRecompensa) {
        this.idRecompensa = idRecompensa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPuntosRequeridos() {
        return puntosRequeridos;
    }

    public void setPuntosRequeridos(int puntosRequeridos) {
        this.puntosRequeridos = puntosRequeridos;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(int idMenu) {
        this.idMenu = idMenu;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
