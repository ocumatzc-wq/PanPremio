package modelo;

import java.sql.Date; // Importa el tipo Date de JDBC, utilizado para manejar fechas SQL.
/**
 * Representa un movimiento de inventario asociado a un producto.
 * Permite almacenar información sobre el tipo de movimiento, la cantidad de unidades involucradas, la fecha y su referencia.
 */
public class MovimientoInventario {
    private int idMovimiento;
    private int idProducto;
    private String tipo;
    private int cantidad;
    private Date fecha;
    private String referencia;
    /**
     * Constructor vacío.
     * Permite crear un objeto MovimientoInventario sin asignar valores mediante parámetros.
     */
    public MovimientoInventario() {
    }
    /**
     * Constructor parametrizado.
     * Permite crear un movimiento proporcionando el producto, el tipo de movimiento, la cantidad y su referencia.
     * El identificador y la fecha no se reciben como parámetros; pueden asignarse posteriormente o ser gestionados por la BD.
     * @param idProducto identificador del producto afectado
     * @param tipo tipo de movimiento realizado
     * @param cantidad cantidad de unidades involucradas
     * @param referencia referencia relacionada con el movimiento
     */
    public MovimientoInventario(int idProducto, String tipo, int cantidad, String referencia) {
        this.idProducto = idProducto;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.referencia = referencia;
    }
    /**
     * Obtiene el identificador del movimiento.
     * @return identificador del movimiento
     */
    public int getIdMovimiento() {
        return idMovimiento;
    }
    /**
     * Asigna el identificador del movimiento.
     * @param idMovimiento identificador que se establecerá
     */
    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }
    /**
     * Obtiene el identificador del producto afectado.
     * @return identificador del producto
     */
    public int getIdProducto() {
        return idProducto;
    }
    /**
     * Asigna el identificador del producto.
     * @param idProducto identificador que se establecerá
     */
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }
    /**
     * Obtiene el tipo de movimiento.
     * @return tipo de movimiento
     */
    public String getTipo() {
        return tipo;
    }
    /**
     * Asigna el tipo de movimiento.
     * @param tipo tipo que se establecerá
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    /**
     * Obtiene la cantidad de unidades involucradas.
     * @return cantidad de unidades
     */
    public int getCantidad() {
        return cantidad;
    }
    /**
     * Asigna la cantidad de unidades involucradas.
     * @param cantidad cantidad que se establecerá
     */
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    /**
     * Obtiene la fecha del movimiento.
     * @return fecha del movimiento
     */
    public Date getFecha() {
        return fecha;
    }
    /**
     * Asigna la fecha del movimiento.
     * @param fecha fecha que se establecerá
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    /**
     * Obtiene la referencia asociada al movimiento.
     * @return referencia del movimiento
     */
    public String getReferencia() {
        return referencia;
    }
    /**
     * Asigna la referencia asociada al movimiento.
     * @param referencia referencia que se establecerá
     */

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
}
