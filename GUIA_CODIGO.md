# Guía completa del código — Proyecto "Pan, Puntos y Premios"

> Documento de estudio para explicar el proyecto ante el profesor.
> Explica **qué es cada parte, qué hace, cómo se conecta y qué pasaría si se elimina**.

---

## Índice

1. [Visión general](#1-visión-general)
2. [Tecnologías usadas](#2-tecnologías-usadas)
3. [Arquitectura por capas](#3-arquitectura-por-capas)
4. [Arranque de la aplicación](#4-arranque-de-la-aplicación)
5. [Configuración y conexión a Oracle](#5-configuración-y-conexión-a-oracle)
6. [Base de datos (tablas y relaciones)](#6-base-de-datos)
7. [Capa modelo](#7-capa-modelo-package-modelo)
8. [Capa excepciones](#8-capa-excepciones-package-excepciones)
9. [Capa DAO](#9-capa-dao-package-dao)
10. [Capa vista](#10-capa-vista-package-vista)
11. [Extras: API y demo](#11-extras-package-proyectooracle)
12. [Flujos completos paso a paso](#12-flujos-completos-paso-a-paso)
13. [Conceptos de POO y JDBC aplicados](#13-conceptos-de-poo-y-jdbc-aplicados)
14. [Preguntas típicas del profesor](#14-preguntas-típicas-del-profesor-respuestas)

---

## 1. Visión general

**"Pan, Puntos y Premios"** es una aplicación de **escritorio (Java Swing)** para administrar una panadería. Permite:

- Registrar y administrar **clientes**.
- Administrar **productos** y **menús** (un menú se compone de productos).
- Controlar el **inventario** (entradas por abastecimiento y salidas por ventas/canjes).
- Registrar **pedidos**, cobrarlos (efectivo/tarjeta) y **acumular puntos** de fidelidad.
- **Canjear** puntos por recompensas (producto o menú gratis).
- Ver **reportes** (ventas por período, más vendidos, bajo inventario, puntos por cliente, historial de canjes, ventas de menús).

Los datos viven en una base de datos **Oracle**. Todo se maneja con **JDBC + PreparedStatement** y **transacciones**.

---

## 2. Tecnologías usadas

| Elemento | Uso |
|---|---|
| **Java 26** | Lenguaje (el proyecto compila con `javac.source/target=26`). |
| **Java Swing** | Interfaz gráfica de escritorio (`JFrame`, `JTable`, `JOptionPane`, `GroupLayout`). |
| **JDBC** | Acceso a la base de datos Oracle (`java.sql.*`). |
| **Oracle** | Motor de base de datos. |
| **ojdbc17.jar** | Driver JDBC de Oracle (en `lib/`). |
| **NetBeans / Ant** | IDE y sistema de construcción (`build.xml`, `nbproject/`). |

---

## 3. Arquitectura por capas

El proyecto sigue una arquitectura en capas tipo **"MVC"** (modelo–vista–controlador aproximado):

```
vista/        → Interfaz gráfica (lo que ve el usuario). Captura eventos (botones).
    ↓ usa
dao/          → Acceso a datos. Ejecuta SQL contra Oracle.
    ↓ usa
modelo/       → Clases de datos (POJOs / JavaBeans y jerarquía de venta).
    ↓
proyectooracle/Conexion → Abre la conexión JDBC a Oracle.
excepciones/  → Excepciones personalizadas.
```

**Regla de dependencias:** la vista llama a los DAO; los DAO construyen/llenan objetos del modelo; nadie en `modelo` conoce a `vista` ni a `dao`. Esto se llama **separación de responsabilidades**.

---

## 4. Arranque de la aplicación

Clase que se ejecuta al arrancar: **`vista.FrmPrincipal`** (definida en `nbproject/project.properties` como `main.class=vista.FrmPrincipal`).

- `main(String[] args)`:
  1. Busca el *Look and Feel* **Nimbus** y lo aplica (aspecto visual moderno). Si falla, usa el aspecto por defecto (por eso el `try/catch`).
  2. `EventQueue.invokeLater(() -> new FrmPrincipal().setVisible(true))` → crea y muestra la ventana **en el hilo de la interfaz (EDT)**, que es la forma correcta en Swing.
- Constructor de `FrmPrincipal`:
  - `initComponents()` → código generado por NetBeans que crea los botones.
  - Pone el título en negrita y centra la ventana.

**¿Qué hace cada botón?**
- Cada botón llama a `abrir(new FrmXxx())`. El método `abrir` configura la ventana hija con `DISPOSE_ON_CLOSE` (se cierra solo esa ventana al cerrarla), la centra respecto a la principal y la muestra. Así cada módulo es una **ventana independiente**.
- `Salir` → `System.exit(0)` termina todo el programa.

**Si elimino `FrmPrincipal`:** no habría punto de entrada (la app no arranca). Es la raíz de la navegación.

---

## 5. Configuración y conexión a Oracle

### `proyectooracle/Conexion.java`
- Tiene 3 constantes: `URL`, `USUARIO`, `CLAVE`.
- `getConexion()`:
  1. `Class.forName("oracle.jdbc.OracleDriver")` → carga el driver JDBC.
  2. `DriverManager.getConnection(URL, USUARIO, CLAVE)` → abre la conexión.
  3. Devuelve el `Connection`, o **`null` si falla** (por eso todos los DAO empiezan verificando `if (cn == null)`).
- `main` sirve solo para **probar** que la conexión funciona.

> **Punto de seguridad a mencionar:** la URL y la contraseña están "quemadas" en el código. Lo ideal es leerlas de variables de entorno o un archivo de configuración.

### Archivos de configuración
- **`build.xml`**: script Ant de NetBeans. Solo define el proyecto e importa `nbproject/build-impl.xml`.
- **`nbproject/project.properties`**: datos clave → `main.class=vista.FrmPrincipal`, `javac.source/target=26`, y el *classpath* con `lib/ojdbc17.jar`.
- **`manifest.mf`**: manifiesto del JAR; el Main-Class lo agrega el build.
- **`.form`**: XML que describe la ventana para el **editor visual** de NetBeans. Los bloques "Generated Code" (`//GEN-*`) en los `.java` son lo que el editor regenera.

---

## 6. Base de datos

Script: **`database/PanPuntosPremios.sql`** (esquema `USUARIO_OSCAR`). 11 tablas:

| Tabla | Qué guarda | Detalles clave |
|---|---|---|
| **Clientes** | Clientes y su saldo de puntos | `id_cliente IDENTITY`, `codigo UNIQUE`, `saldo_puntos >= 0`, `activo 0/1`. |
| **Productos** | Productos vendibles | `categoria IN ('Sandwich','Bebida','Acompanamiento')`, precio>=0, existencia>=0. |
| **Menus** | Menús completos | precio, activo. |
| **Menu_Composicion** | Qué productos forman cada menú | PK compuesta `(id_menu,id_producto)`, FK a Menus y Productos. **Composición (N a N)**. |
| **Movimientos_Inventario** | Historial de stock | `tipo IN ('ENTRADA','SALIDA','AJUSTE')`, FK a Productos. |
| **Pedidos** | Cabecera de cada pedido | `estado IN ('PENDIENTE','PAGADO','ENTREGADO','ANULADO')`, `total`, `puntos_generados`. |
| **Detalle_Pedidos** | Líneas del pedido | FK a Pedidos; `id_producto` **XOR** `id_menu` (CHECK), cantidad, precio_unitario, subtotal, puntos. |
| **Pagos** | Pago de un pedido | `tipo IN ('EFECTIVO','TARJETA')`, monto, recibido, cambio, referencia (obligatoria si es TARJETA). |
| **Movimientos_Puntos** | Historial de puntos | `tipo IN ('ACUMULACION','CANJE','AJUSTE')`, puntos <> 0. |
| **Recompensas** | Catálogo de premios | `puntos_requeridos`, `id_producto` **XOR** `id_menu`, activo. |
| **Canjes** | Recompensas entregadas | FK a Clientes y Recompensas, `puntos_usados`. |

Incluye **datos de prueba** (3 clientes, 6 productos, 2 menús con su composición y 4 recompensas).

**Conceptos a explicar:** clave primaria (PK), clave foránea (FK), restricción `CHECK`, `UNIQUE`, `IDENTITY` (autoincremental), el **XOR** (un detalle/recompensa es producto **o** menú, nunca ambos).

---

## 7. Capa modelo (paquete `modelo`)

### 7.1 Jerarquía de venta (herencia + polimorfismo)

**`ItemVenta` (abstracta)**
- Atributos `protected String nombre; protected double precio;`.
- Constructor que los inicializa.
- Métodos **abstractos**: `getPrecio()`, `getNombre()`, `getDetalles()`, `calcularPuntos()`.
- Es el **contrato** común de "algo que se puede vender". `abstract` porque no tiene sentido un "artículo genérico".
- **Si la elimino:** `ProductoIndividual` y `MenuCompleto` no compilan; se pierde la base del polimorfismo.

**`ProductoIndividual extends ItemVenta`**
- Añade `idProducto`, `categoria`, `existencia`.
- Constante `PUNTOS_SANDWICH = 2`.
- `calcularPuntos()` → devuelve **2 si la categoría es "Sandwich"** (comparación `equalsIgnoreCase`), **0 en cualquier otra**.
- `getDetalles()` → texto con nombre, categoría y precio.
- Dos constructores: uno con `idProducto` (registro existente) y otro sin él (registro nuevo).

**`MenuCompleto extends ItemVenta`**
- Añade `idMenu`, `codigo`, `List<ProductoIndividual> productos`, `descuento`, `activo`, `PUNTOS_MENU = 8`.
- `agregarProducto(...)` añade a la lista (**composición**: un menú *tiene* productos).
- `getPrecio()` → `precio - descuento` (**sobrescritura**: el menú ajusta el precio base).
- `getDetalles()` → usa `StringBuilder` para armar el texto con la lista de componentes.
- `calcularPuntos()` → **8 fijos** por menú.

> **Polimorfismo:** en `ProyectoOracle` (demo) y en `FrmPedido` se tratan productos y menús del mismo modo a través de tipos comunes. El "mismo mensaje" (`calcularPuntos`, `getPrecio`) produce resultados distintos según el objeto real.

### 7.2 POJOs / JavaBeans (clases de datos con getters y setters)

| Clase | Atributos principales |
|---|---|
| **Cliente** | idCliente, codigo, nombre, apellido, email, telefono, saldoPuntos, activo. Constructores: vacío, completo, y de alta (saldo=0). |
| **Producto** | idProducto, codigo, categoria, nombre, precio, existencia, activo. |
| **Pedido** | idPedido, idCliente, fechaPedido (`java.sql.Date`), estado, total, puntosGenerados. |
| **DetallePedido** | idDetalle, idPedido, idProducto, idMenu, cantidad, precioUnitario, subtotal, puntos. El constructor calcula `subtotal = cantidad * precioUnitario`. |
| **Pago** | idPago, idPedido, tipo, monto, recibido, cambio, referencia, fechaPago. |
| **Canje** | idCanje, idCliente, idRecompensa, puntosUtilizados, fechaCanje. |
| **Recompensa** | idRecompensa, nombre, puntosRequeridos, idProducto, idMenu, activo. |
| **MovimientoInventario** | idMovimiento, idProducto, tipo, cantidad, fecha, referencia. |
| **MovimientoPuntos** | idMovimiento, idCliente, tipo, puntos, fechaMovimiento, referencia. |

**Detalle importante:** el modelo `Cliente` usa `apellido` (singular), pero la columna en BD es `apellidos`. El DAO hace el mapeo.

**Encapsulamiento:** todos los atributos son `private`; se accede con getters/setters. Eso protege los datos.

---

## 8. Capa excepciones (paquete `excepciones`)

- **`PuntosInsuficientesException`** y **`StockInsuficienteException`** → extienden `Exception`, reciben un mensaje y lo pasan a `super`. Son **excepciones comprobadas personalizadas**.
- **Uso actual:** están creadas pero **no se lanzan** en el flujo actual (los DAO usan `SQLException` con mensajes de texto). Su propósito era representar errores de negocio de forma explícita. Útil para mencionar como *mejora pendiente*: usarlas en `CanjeDAO`/`PedidoDAO` en lugar de `SQLException` genérica.

---

## 9. Capa DAO (paquete `dao`)

Cada DAO encapsula el SQL de una tabla/entidad. Patrón repetido:

1. Construir el SQL con `?` (parámetros).
2. `Conexion.getConexion()`; si es `null`, avisar y salir.
3. **try-with-resources**: `try (cn; PreparedStatement ps = ...)` → cierra automáticamente conexión y statement (sintaxis Java 9+).
4. Asignar parámetros con `ps.setXxx(...)`.
5. Ejecutar (`executeUpdate` para insert/update/delete; `executeQuery` para select).
6. Para SELECT, mapear cada fila con un método privado `mapear(ResultSet)`.
7. Errores: `catch (SQLException e)` → mensaje por consola y devolver `false`/lista vacía/`null`.

### 9.1 `ClienteDAO`
- `agregarCliente(Cliente)` → INSERT con código, nombre, apellidos, email, teléfono, saldo, activo.
- `obtenerClientes()` → SELECT ordenado por id.
- `obtenerClientePorId(int)` / `obtenerClientePorCodigo(String)` → búsquedas.
- `actualizarCliente(Cliente)` → UPDATE por id.
- `cambiarEstadoCliente(int, boolean)` → UPDATE solo del campo `activo`.
- `eliminarCliente(int)` → DELETE.
- `mapear(ResultSet)` → convierte fila en objeto `Cliente` (activo `1→true`).

### 9.2 `ProductoDAO`
- `agregarProducto`, `obtenerProductoPorId`, `actualizarProducto`, `cambiarEstadoProducto`.
- `obtenerProductos()` / `obtenerProductosActivos()` → ambos llaman al **método privado sobrecargado** `obtenerProductos(boolean soloActivos)` que arma el SQL condicionalmente. (Ejemplo de **sobrecarga** y reutilización).
- **`abastecerProducto(idProducto, cantidad, referencia)`** → **transacción**: 
  1. `UPDATE Productos SET existencia = existencia + ?`  
  2. `INSERT INTO Movimientos_Inventario ... 'ENTRADA'`  
  Si algo falla → `rollback`. `commit` si todo bien.

### 9.3 `MenuDAO`
- `listarMenus()` / `listarMenus(boolean soloActivos)`:
  - Un **JOIN** trae el menú y todos sus productos componentes.
  - Usa un `LinkedHashMap<Integer, MenuCompleto>` para **no duplicar** el menú cuando aparecen varias filas (una por componente): la primera fila crea el menú, las siguientes solo agregan productos.
  - El mapa preserva el orden de inserción.
  - Nota: al ser `JOIN` interno, **solo aparecen menús que tienen composición**.
- `agregarMenu(MenuCompleto)` → **transacción**:
  1. `INSERT INTO Menus ...` pidiendo la clave generada (`getGeneratedKeys`).
  2. `INSERT INTO Menu_Composicion` en **batch** (`addBatch`/`executeBatch`) por cada producto.
  3. `commit`/`rollback`.

### 9.4 `PedidoDAO` (el más importante)
- **`crearPedido(idCliente, detalles, total, puntos)`** → transacción:
  1. `INSERT INTO Pedidos (id_cliente, estado, total, puntos_generados) VALUES (?, 'PENDIENTE', ?, ?)` y recoge el `id_pedido` generado.
  2. `INSERT INTO Detalle_Pedidos` en batch: si el detalle es menú se guarda `id_menu` y `id_producto=NULL`; si es producto, al revés.
  3. `commit`/`rollback` y devuelve el id del pedido.
- **`cobrarPedido(idPedido, Pago)`** → la transacción más completa:
  1. Lee el pedido (cliente, estado, puntos).
  2. Valida que el estado sea `PENDIENTE` (si no, error).
  3. Recorre los detalles: si es **menú**, consulta `Menu_Composicion` y descuenta cada componente × cantidad; si es **producto**, descuenta directo.
     - `descontarStock(...)` hace `UPDATE Productos SET existencia = existencia - ? WHERE id_producto = ? AND existencia >= ?`. Si afecta **0 filas**, lanza "Stock insuficiente" (así **nunca** deja stock negativo). Luego registra el movimiento `SALIDA`.
  4. `INSERT INTO Pagos`.
  5. `UPDATE Pedidos SET estado='PAGADO'`.
  6. Si generó puntos: `UPDATE Clientes SET saldo_puntos = saldo_puntos + ?` y `INSERT INTO Movimientos_Puntos ... 'ACUMULACION'`.
  7. `commit`. Si algo falló en medio → `rollback` (el pedido queda PENDIENTE y sin cobrar).
- **`anularPedido(idPedido)`** → solo cambia a `ANULADO` si estaba `PENDIENTE`.
- **`descontarStock(...)`** → método auxiliar privado reutilizado.

> **Atomicidad:** todo el cobro es "todo o nada". Ese es el corazón de las transacciones.

### 9.5 `PagoDAO`
- `registrarPago(Pago)` → INSERT en `Pagos` (usado de forma independiente; `cobrarPedido` inserta el pago dentro de su propia transacción).

### 9.6 `CanjeDAO`
- **`realizarCanje(idCliente, idRecompensa, puntosRequeridos, idProductoEntrega, idMenuEntrega)`** → transacción:
  1. `UPDATE Clientes SET saldo_puntos = saldo_puntos - ? WHERE id_cliente = ? AND saldo_puntos >= ?`. Si afecta **0 filas** → "no tiene puntos suficientes" (evita saldo negativo).
  2. `INSERT INTO Canjes` y obtiene su id generado.
  3. `INSERT INTO Movimientos_Puntos ... 'CANJE'` con puntos **negativos** (−puntos).
  4. Si la recompensa es menú: descuenta los componentes; si es producto: descuenta 1 unidad.
     - `descontar(...)` hace el `UPDATE` condicional de stock (≥ cantidad) y registra `SALIDA`.
  5. `commit`/`rollback`.

### 9.7 `RecompensaDAO`
- `listarRecompensasActivas()` → SELECT de recompensas `activo = 1` ordenadas por puntos.
- `obtenerRecompensaPorId(int)`.
- `mapear(...)` → usa `rs.wasNull()` para convertir `id_producto`/`id_menu` nulos en `0` (así el resto del código sabe que "no aplica").

### 9.8 `MovimientoInventarioDAO`
- `listar()` → todos los movimientos de inventario ordenados por fecha.

### 9.9 `MovimientoPuntosDAO`
- `listarPorCliente(int)` → historial de puntos de un cliente.
- `registrar(idCliente, tipo, puntos, referencia)` → INSERT de un movimiento.

### 9.10 `ReporteDAO`
Métodos que devuelven `List<Object[]>` (cada fila es un arreglo de columnas):
- `ventasPorPeriodo(desde, hasta)` → Pedidos PAGADO/ENTREGADO + Pago, filtrando por fecha (`fecha_pedido >= ? AND fecha_pedido < ? + 1`; en Oracle sumar 1 a una fecha = día siguiente).
- `productosMasVendidos()` → `UNION ALL` de (productos vendidos directo) + (productos vendidos como parte de menús), agrupado por producto.
- `productosBajoInventario()` → activos con existencia ≤ 5.
- `puntosPorCliente()` → clientes activos, nombre concatenado, ordenado por puntos.
- `historialCanjes()` → Canjes + Cliente + Recompensa.
- `ventasMenus()` → menús agrupados con cantidad e importe.
- `consultar(sql, parámetros...)` → método genérico privado (**varargs**) que ejecuta cualquier consulta y arma las filas usando `ResultSetMetaData` para conocer el número de columnas.

---

## 10. Capa vista (paquete `vista`)

Cada formulario es un `JFrame`. Contiene:
- **Código generado** por NetBeans (`initComponents`) dentro de los marcadores `//GEN-BEGIN`/`//GEN-END`.
- **Lógica propia** en los métodos `btnXxxActionPerformed` (manejadores de eventos).
- Tablas construidas con `DefaultTableModel` con `isCellEditable = false` (no se editan a mano).

### 10.1 `FrmPrincipal`
Menú de botones que abren cada módulo (ver sección 4).

### 10.2 `FrmCliente`
- Estado interno: `idClienteActual`, `saldoPuntosActual`, `activoActual` (recuerdan el cliente cargado).
- `cargarTablaClientes()` → `ClienteDAO.obtenerClientes()` y llena la tabla.
- **Guardar**: valida nombre/apellido, crea `new Cliente(...)` y llama `agregarCliente`.
- **Buscar**: por código (`obtenerClientePorCodigo`) y rellena el formulario.
- **Actualizar**: arma un `Cliente` con `idClienteActual` y llama `actualizarCliente`.
- **Activar/Desactivar**: alterna `activoActual` con `cambiarEstadoCliente`.
- Clic en una fila de la tabla → `cargarClienteSeleccionado()` → `obtenerClientePorId`.
- Teclas Enter recorren los campos (`requestFocusInWindow`).

### 10.3 `FrmProducto`
- Combo de categorías (`Sandwich`, `Bebida`, `Acompanamiento`) — coincide con el CHECK de la BD.
- **Guardar**: convierte precio/existencia con `Double.parseDouble` / `Integer.parseInt` (con `catch NumberFormatException`).
- **Abastecer**: toma el producto seleccionado y pide cantidad/referencia por diálogos → `abastecerProducto`.
- `cargarTablaProductos()` recarga la lista.

### 10.4 `FrmPedido` (el más complejo)
- Clase interna **`Opcion`**: representa un ítem del combo (producto o menú) con `id, nombre, precio, stock, puntos` y un `toString` legible.
- `cargarItems()`: si el tipo es "Producto" carga productos activos; si es "Menú" carga menús y calcula el **stock mínimo** entre sus componentes.
- **Añadir al carrito**: valida cantidad > 0 y `stock >= cantidad`, calcula subtotal y puntos, agrega fila a la tabla, y actualiza el total.
- **Quitar**: resta el subtotal de la fila y la elimina.
- **Completar Venta**:
  1. Valida que haya id de cliente y que exista (`obtenerClientePorId`).
  2. `construirDetalles()` convierte la tabla en `List<DetallePedido>`.
  3. Suma puntos totales.
  4. Pregunta método de pago (`EFECTIVO`/`TARJETA`):
     - Efectivo → pide el monto recibido, valida que alcance y calcula el **cambio**.
     - Tarjeta → pide **referencia** (obligatoria en BD).
  5. `crearPedido(...)` y luego `cobrarPedido(idPedido, pago)`.
  6. Muestra resultado (venta completada o pedido que quedó PENDIENTE).

### 10.5 `FrmFidelizacion`
- `idClienteActual` y `recompensasActuales` (lista paralela a la tabla; el índice de la fila mapea a la lista).
- **Buscar Cliente** → muestra "Cliente: ... | Puntos: ..." y carga movimientos.
- **Canjear** → valida que haya cliente y recompensa, **prevalida puntos**, pide confirmación, llama `CanjeDAO.realizarCanje` y recarga.
- `cargarMovimientos()` → historial en la tabla.

### 10.6 `FrmReportes`
- Combo con 6 reportes. Al cambiar de reporte, muestra/oculta los campos de fecha (solo el reporte 0 "Ventas por período" usa rango).
- `btnGenerarActionPerformed` → `switch` por índice: construye el modelo de tabla adecuado y consulta al `ReporteDAO`.
- Para ventas: valida fechas `AAAA-MM-DD` con `java.sql.Date.valueOf`, suma efectivo/tarjeta y muestra un resumen.

---

## 11. Extras (paquete `proyectooracle`)

- **`ProyectoOracle`**: clase `main` de **demostración de polimorfismo y composición**. Crea 3 `ProductoIndividual`, arma un `MenuCompleto`, los mete en una `List<ItemVenta>` y recorre calculando precio y puntos. **No es el punto de entrada real** de la app (ese es `FrmPrincipal`).
- **`ApiPuntos`**: pequeña **API HTTP** de solo lectura con `com.sun.net.httpserver` en el puerto 8080:  
  `GET /puntos?codigo=CLI001` → devuelve un JSON con el nombre y los puntos del cliente. Arma el JSON por concatenación de cadenas y usa `Conexion.getConexion()`. (Serviría, por ejemplo, para que otra app consulte puntos.)

---

## 12. Flujos completos paso a paso

### Flujo A — Registrar un cliente
`FrmCliente` (Guardar) → `ClienteDAO.agregarCliente` → `INSERT INTO Clientes` → recarga la tabla.

### Flujo B — Hacer un pedido y cobrarlo (incluye puntos e inventario)
1. `FrmPedido` arma el carrito (productos y/o menús).
2. "Completar Venta" → `PedidoDAO.crearPedido` → INSERT en `Pedidos` (PENDIENTE) + `Detalle_Pedidos`.
3. `PedidoDAO.cobrarPedido` (una sola transacción):
   - Descuenta stock (expandiendo menús a componentes) → `Movimientos_Inventario` (SALIDA).
   - Inserta el `Pago`.
   - Pedido → `PAGADO`.
   - Suma puntos al cliente → `Movimientos_Puntos` (ACUMULACION).
4. Si algo falla → `rollback`: el pedido queda PENDIENTE y no se cobra.

### Flujo C — Canjear puntos
`FrmFidelizacion` → `CanjeDAO.realizarCanje` (transacción) → descuenta puntos (con validación), registra `Canjes` y `Movimientos_Puntos` (CANJE, negativo), descuenta inventario (`SALIDA`).

### Flujo D — Abastecer inventario
`FrmProducto` (Abastecer) → `ProductoDAO.abastecerProducto` (transacción) → sube existencia + `Movimientos_Inventario` (ENTRADA).

### Flujo E — Reportes
`FrmReportes` → `ReporteDAO` → SELECT(s) → tablas en pantalla.

---

## 13. Conceptos de POO y JDBC aplicados

**Programación orientada a objetos**
- **Abstracción:** `ItemVenta` define el "qué" sin el "cómo".
- **Herencia:** `ProductoIndividual` y `MenuCompleto` heredan de `ItemVenta`.
- **Polimorfismo:** el mismo método (`calcularPuntos`, `getPrecio`) se comporta distinto según el objeto.
- **Encapsulamiento:** atributos `private` + getters/setters.
- **Composición:** `MenuCompleto` *tiene* una `List<ProductoIndividual>`.
- **Sobrecarga:** `obtenerProductos()` vs `obtenerProductos(boolean)`; constructores múltiples.
- **Sobrescritura (`@Override`):** métodos de `ItemVenta` redefinidos en las subclases.

**JDBC**
- **Driver**: se carga con `Class.forName`.
- **Connection / PreparedStatement / ResultSet**: el trío básico del acceso a datos.
- **PreparedStatement** (con `?`): evita **inyección SQL** y simplifica el binding de parámetros.
- **Transacciones**: `setAutoCommit(false)` + `commit()` / `rollback()` para atomicidad; al final se restaura `setAutoCommit(true)` y se cierra la conexión.
- **Claves generadas**: `getGeneratedKeys()` para recuperar el `IDENTITY` recién insertado.
- **try-with-resources**: cierra recursos automáticamente (incluso el `Connection` como recurso, sintaxis `try (cn; ...)`).

**Java Swing**
- **EDT**: `EventQueue.invokeLater` para crear la UI en el hilo correcto.
- **Layouts**: `GroupLayout` (generado por NetBeans) para posicionar componentes.
- **Modelos de tabla**: `DefaultTableModel`.
- **Diálogos**: `JOptionPane`.
- **Look and Feel**: Nimbus.

---

## 14. Preguntas típicas del profesor (respuestas)

**¿Cuál es el punto de entrada de la aplicación?**
`vista.FrmPrincipal.main`. Está declarado en `project.properties` como `main.class`.

**¿Qué pasa si elimino `ItemVenta`?**
`ProductoIndividual` y `MenuCompleto` no compilan (pierden su superclase). Se elimina el polimorfismo.

**¿Y si elimino `calcularPuntos()`?**
No se podrían calcular/acumular puntos de fidelidad en los pedidos.

**¿Por qué usas `PreparedStatement` y no `Statement`?**
Porque parametriza los valores (`?`), evitando inyección SQL y errores de formato.

**¿Por qué `setAutoCommit(false)`?**
Para agrupar varias operaciones en una **transacción**: o se aplican todas, o ninguna (`commit`/`rollback`). Ejemplo: al cobrar, no tendría sentido descontar stock sin registrar el pago.

**¿Qué hace `try (cn; PreparedStatement ps = ...)`?**
Es *try-with-resources*: cierra automáticamente la conexión y el statement al terminar, aunque ocurra una excepción.

**¿Cómo evitas dejar stock o puntos negativos?**
Con `UPDATE ... WHERE existencia >= ?` (o `saldo_puntos >= ?`): si no afecta ninguna fila, se lanza error y se hace `rollback`.

**¿Cómo maneja un menú el descuento de inventario?**
`Menu_Composicion` guarda qué productos forman el menú; al vender/canjear un menú se descuenta cada componente × la cantidad vendida.

**¿Por qué el modelo `Cliente` dice `apellido` y la tabla `apellidos`?**
Por una diferencia de nombres; el `ClienteDAO.mapear` traduce `apellidos` (BD) → `apellido` (objeto).

**¿Qué son `PuntosInsuficientesException` y `StockInsuficienteException`?**
Excepciones de negocio personalizadas. Están definidas como buena práctica, aunque actualmente el flujo usa `SQLException` con mensajes (mejora pendiente).

**¿Para qué sirve `ApiPuntos`?**
Es una API HTTP opcional de solo lectura (`/puntos?codigo=...`) para consultar el saldo de un cliente desde otro sistema.

**¿Por qué `MenuCompleto` usa un `LinkedHashMap` en `MenuDAO`?**
Porque el JOIN devuelve una fila por componente; el mapa agrupa todas las filas de un mismo menú en un solo objeto, sin duplicarlo, y conserva el orden.

**Si el profesor borra una línea…** mira qué método/clase se está usando: casi todo el flujo pasa por **`Conexion` → `DAO` → SQL → `modelo` → `vista`**. Identifica en qué capa estás y qué método llama a qué, y podrás explicar el efecto de quitarlo.

---

## Cómo ejecutar el proyecto

- En **NetBeans**: abrir el proyecto y *Run* (F6). Ya está configurado (`FrmPrincipal` es el main).
- Desde línea de comandos (requiere JDK y el driver):
  ```
  javac -encoding UTF-8 -cp "lib/ojdbc17.jar" -d build/classes $(todos los .java de src)
  java  -cp "build/classes;lib/ojdbc17.jar" vista.FrmPrincipal
  ```
- Antes hay que **crear el esquema** ejecutando `database/PanPuntosPremios.sql` en Oracle y ajustar la URL/usuario/clave en `Conexion.java`.
