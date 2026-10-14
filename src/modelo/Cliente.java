package modelo;
// Clase Cliente que funciona como modelo para representar los datos de los clientes registrados en el sistema
public class Cliente {
    // Atributos privados de la clase que definen el perfil e información del cliente
    private int idCliente; // Identificador único del cliente (Clave primaria)
    private String codigo; // Código de identificación o tarjeta del cliente
    private String nombre; // Nombre del cliente
    private String apellido; // Apellido del cliente
    private String email; // Correo electrónico de contacto
    private String telefono; // Número de teléfono de contacto
    private int saldoPuntos; // Saldo actual acumulado de puntos de fidelidad
    private boolean activo = true; // Estado del cliente: por defecto inicia activo (true)
    // Constructor por defecto (vacío)
    public Cliente() {
    }
    // Constructor parametrizado completo (útil al consultar un cliente existente desde la base de datos)
    public Cliente(int idCliente, String nombre, String apellido, String email, String telefono, int saldoPuntos) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.saldoPuntos = saldoPuntos;
    }
    // Constructor parametrizado para registrar un cliente nuevo (inicializa saldo de puntos en 0)
    public Cliente(String nombre, String apellido, String email, String telefono) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.saldoPuntos = 0;
    }
    // Métodos Getters y Setters para cada uno de los atributos de la clase
    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public int getSaldoPuntos() {
        return saldoPuntos;
    }

    public void setSaldoPuntos(int saldoPuntos) {
        this.saldoPuntos = saldoPuntos;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
