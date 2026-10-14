package modelo;
// Clase Recompensa que gestiona los premios o beneficios canjeables por puntos de fidelidad en el sistema
public class Recompensa { // Atributos de la clase que definen las características de una recompensa
    private int idRecompensa; // Identificador único de la recompensa (Clave primaria)
    private String nombre; // Nombre o descripción del premio/recompensa
    private int puntosRequeridos; // Cantidad de puntos de fidelidad necesarios para canjearla
    private int idProducto; // ID del producto asociado a la recompensa (si aplica)
    private int idMenu; // ID del menú asociado a la recompensa (si aplica)
    private boolean activo = true; // Estado de la recompensa: por defecto inicia activa (true)
    // Constructor por defecto (vacío) para instanciar la clase sin valores iniciales
    public Recompensa() {
    }
    // Constructor parametrizado para inicializar la recompensa con ID, nombre y puntos requeridos
    public Recompensa(int idRecompensa, String nombre, int puntosRequeridos) {
        this.idRecompensa = idRecompensa;
        this.nombre = nombre;
        this.puntosRequeridos = puntosRequeridos;
    }
    // Método Getter para obtener el ID de la recompensa
    public int getIdRecompensa() {
        return idRecompensa;
    }
    // Método Setter para asignar o modificar el ID de la recompensa
    public void setIdRecompensa(int idRecompensa) {
        this.idRecompensa = idRecompensa;
    }
    // Método Getter para obtener el nombre de la recompensa
    public String getNombre() {
        return nombre;
    }
    // Método Setter para asignar o modificar el nombre de la recompensa
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    // Método Getter para obtener los puntos necesarios para el canje
    public int getPuntosRequeridos() {
        return puntosRequeridos;
    }
    // Método Setter para asignar o modificar los puntos requeridos
    public void setPuntosRequeridos(int puntosRequeridos) {
        this.puntosRequeridos = puntosRequeridos;
    }
    // Método Getter para obtener el ID del producto asociado
    public int getIdProducto() {
        return idProducto;
    }
    // Método Setter para asignar o modificar el ID del producto asociado
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }
    // Método Getter para obtener el ID del menú asociado
    public int getIdMenu() {
        return idMenu;
    }
    // Método Setter para asignar o modificar el ID del menú asociado
    public void setIdMenu(int idMenu) {
        this.idMenu = idMenu;
    }
    // Método para consultar si la recompensa se encuentra activa en el sistema
    public boolean isActivo() {
        return activo;
    }
    // Método Setter para activar o desactivar la recompensa
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
