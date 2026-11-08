# Cosecha Compartida - Avance 2

**Curso:** Programación Cliente Servidor Concurrente  
**Profesor:** Mario Alberto Vargas Montes  
**Estudiante:** Andrea Araya Fonseca  
**III Cuatrimestre 2026**

## Descripción del proyecto

Cosecha Compartida es una aplicación cliente-servidor en Java pensada para que vecinos de una comunidad puedan intercambiar excedentes de frutas, verduras y hierbas sin dinero de por medio. Una persona publica un lote disponible y otros vecinos pueden explorar las ofertas cercanas y proponer un alimento a cambio.

En este segundo avance se construyó la arquitectura cliente-servidor con sockets TCP, manejo de múltiples clientes con hilos, serialización en archivo y la interfaz gráfica en Swing siguiendo los prototipos diseñados en Figma.

## Alcance de este Avance 2 (7 de 11 historias — 64%)

**Historias de usuario implementadas en este avance:**
- **HU01 - Registrar una cuenta:** creación de cuenta validando campos obligatorios, formato/unicidad de correo y coincidencia de contraseñas (`P01`).
- **HU02 - Ingresar y salir de la aplicación:** inicio de sesión con credenciales propias y cierre de sesión seguro en el servidor (`P01`, `P09`).
- **HU03 - Publicar alimentos:** registro de lotes con categoría, cantidad, unidad, zona, preferencias, descripción y fecha límite (`P03`).
- **HU04 - Buscar publicaciones:** exploración de lotes disponibles y vigentes con filtros combinables por alimento, zona y categoría (`P02`).
- **HU05 - Proponer un intercambio:** vista de detalle del lote y envío de propuesta validando que no sea publicación propia ni duplicada (`P04`).
- **HU10 - Administrar mis publicaciones:** consulta, edición, retiro y eliminación de lotes propios respetando si tienen propuestas pendientes (`P03`).
- **HU11 - Actualizar mi perfil:** edición de nombre y zona predeterminada conservando el correo de acceso (`P09`).

**Pendiente para la Entrega Final:**
- Resolución de propuestas recibidas/enviadas y registro del acuerdo de entrega (**HU06**, **HU07**).
- Seguimiento de intercambios, confirmación de entregas e historial personal (**HU08**, **HU09**).
- Migración del almacenamiento en archivo (`datos/cosecha.dat`) a base de datos relacional con JDBC, tal como se planteó en el diseño del Avance 1.

## Requisitos

- Java JDK 17 o superior.
- NetBeans o IntelliJ IDEA (sin librerías ni frameworks externos).

## Cómo ejecutar la aplicación

1. **Abrir el proyecto en el IDE:**
   - **En NetBeans:** ir a *File -> New Project -> Java with Ant -> Java Project with Existing Sources*, agregar las carpetas `src` (Sources) y `test` (Test Packages), y revisar en *Properties -> Sources* que la codificación esté en **UTF-8**.
   - **En IntelliJ IDEA:** abrir la carpeta del proyecto y en *File -> Project Structure* seleccionar el JDK (17 o superior) y marcar `src` como *Sources* y `test` como *Tests*.
2. Ejecutar primero el servidor en la clase `cosecha.servidor.ServidorAplicacion` (escucha por defecto en el puerto `5050`).
3. Con el servidor corriendo, ejecutar `cosecha.cliente.AplicacionCliente`. Se puede correr esta clase varias veces al mismo tiempo para abrir distintas ventanas y probar varios vecinos conectados.

### Usuarios de prueba precargados

La primera vez que se inicia el servidor (cuando aún no existe `datos/cosecha.dat`), se cargan automáticamente tres cuentas de prueba para facilitar la revisión. Todas usan la contraseña **`cosecha123`**:

- `andrea@example.com` (Andrea Araya) - tiene publicaciones de mangos y papayas, con propuestas pendientes en el lote de mangos.
- `luis@example.com` (Luis Mora) - tiene limones y chayotes publicados.
- `maria@example.com` (María Solís) - tiene culantro publicado.

Si se desea reiniciar los datos de prueba desde cero, basta con detener el servidor y borrar la carpeta `datos/`.

## Organización de paquetes

- `cosecha.modelo`: clases principales del dominio (`EntidadBase`, `Usuario`, `Publicacion`, `PropuestaIntercambio`) y sus enumeraciones.
- `cosecha.excepciones`: excepción base `CosechaException` y subclases para validaciones, reglas del negocio, permisos, elementos no encontrados y errores de conexión.
- `cosecha.comun`: clases serializables que viajan entre cliente y servidor (`Solicitud`, `Respuesta`, `Operacion`, `Sesion`).
- `cosecha.servidor`: `ServidorAplicacion`, `ManejadorCliente` (un hilo por cliente conectado), servicios con las reglas del negocio y carga de datos iniciales.
- `cosecha.servidor.datos`: interfaz `Repositorio`, repositorios por entidad y `AlmacenDatos` para guardar el estado en archivo y sincronizar operaciones concurrentes.
- `cosecha.cliente` y `cosecha.cliente.gui`: conexión por socket del cliente, `VentanaPrincipal` (`JFrame`) y las pantallas en Swing.
- `cosecha.pruebas`: clase `PruebaIntegracion` para comprobar las historias implementadas contra el servidor.
