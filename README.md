# Sistema de Gestión Hotelera - Informe Técnico

**Materia:** Taller de Diseño
**Tecnología:** Spring Boot 3 + Java 17

---

## 1. Resumen de lo que se hizo

Migramos el sistema viejo a una arquitectura moderna con **Spring Boot**, cumpliendo completamente con lo pedido por la cátedra:

* **Framework:** ✅ Usamos **Spring Boot 3.2.5** gestionado con Maven.
* **Tests:** ✅ Cubrimos más del 80% de la lógica de negocio con **JUnit 5** y **Mockito**.
* **Patrones:** ✅ Aplicamos **Builder**, **Singleton**, **Repository**, **DTO** y **Dependency Injection** en todo el código.
* **SOLID:** ✅ Respetamos las capas y la responsabilidad única de cada clase.
* **Documentación API:** ✅ Los controladores se documentan solos con **OpenAPI (Swagger UI)**.
* **Base de Datos:** ✅ Usamos **Spring Data JPA** (Hibernate) y PostgreSQL.
* **Fechas:** ✅ Reemplazamos `Date` por `java.time` (`LocalDate`, `LocalTime`) en la lógica importante.

---

## 2. Arquitectura del Proyecto

Organizamos el código en capas para que esté ordenado y desacoplado:

* **Controladores (`/Controllers`):** Solo atienden los pedidos HTTP (GET, POST, etc.) y llaman a los servicios. Manejan errores globales con `GlobalExceptionHandler`.
* **Servicios (`/Services`):** Acá está toda la "magia" del negocio (cálculos, validaciones). Usan `@Transactional` para cuidar los datos.
* **Repositorios (`/Repositories`):** Interfaces que extienden de `JpaRepository`. No usamos mas SQL a mano para cosas básicas.
* **Dominio y DTOs:**
* **Dominio:** Las entidades reales de la base de datos (`@Entity`).
* **DTOs:** Objetos simples para mandar datos al Frontend sin exponer la base de datos.



---

## 3. Requisitos Técnicos al Detalle

### 3.1. Patrones de Diseño (Más de 4)

1. **Singleton:** Spring maneja una sola instancia de los `@Service` y `@Controller` para ahorrar memoria.
2. **Builder:** Lo usamos en DTOs y Entidades para crear objetos complejos de forma clara, sin constructores gigantes.
3. **Repository:** Desacoplamos la lógica de negocio del acceso a datos.
4. **DTO (Data Transfer Object):** Usamos paquetes enteros de DTOs para mover datos entre capas de forma segura.
5. **Inyección de Dependencias:** Usamos `@Autowired` en los constructores, lo que facilita mucho el testing.

### 3.2. Manejo de Fechas (Java Time API)

Dejamos de usar `java.util.Date` donde importaba:

* **FacturaService:** Usa `LocalTime` para ver si corresponde recargo por *Late Check-out* y `Period` para calcular edades.
* **HabitacionService:** Usa `LocalDate` para armar la grilla de estados día por día.

---

## 4. Testing y Calidad

Tenemos tests unitarios sólidos usando **JUnit 5** y **Mockito**.

### 4.1. Qué probamos

* **`ReservaServiceTest`:** Que valide fechas y disponibilidad correctamente.
* **`EstadiaServiceTest`:** El Check-in, ocupación real y acompañantes.
* **`FacturaServiceTest`:** Cálculos de montos, IVA y recargos horarios.
* **`PagoServiceTest`:** Registro de pagos y validaciones de deuda.
* **`UsuarioServiceTest`:** Login y hashing de contraseñas.

### 4.2. Cómo probamos

Usamos la técnica **Arrange-Act-Assert**: preparamos datos falsos (Mocks), ejecutamos el método y verificamos que el resultado sea el esperado.

---

Aquí tienes la sección 5 lista para copiar y pegar en tu `README.md`, ajustada sin emojis y con las descripciones concisas solicitadas.

---

## 5. Funcionalidad Implementada (Casos de Uso)

### Gestión de Habitaciones

* **Consultar Estado (Grilla):** Obtiene el estado de las habitaciones en un rango de fechas, indicando si están disponibles, reservadas, ocupadas o en mantenimiento.
* **Listar Habitaciones:** Recupera el listado completo de habitaciones del hotel, ordenadas por tipo y número para su visualización.

### Gestión de Huéspedes

* **Alta de Huésped:** Registra un nuevo huésped en el sistema, validando previamente que no exista un duplicado por tipo y número de documento.
* **Búsqueda de Huéspedes:** Permite buscar huéspedes aplicando filtros por apellido, nombre, tipo o número de documento.
* **Modificación de Huésped:** Actualiza los datos personales y de contacto de un huésped existente identificado por su documento.
* **Baja de Huésped:** Elimina un huésped del sistema, aplicando validaciones de negocio para impedir el borrado si posee historial de alojamientos o facturas.

### Gestión de Reservas

* **Registrar Reserva:** Crea nuevas reservas para un rango de fechas, verificando la disponibilidad tanto en reservas existentes como en estadías activas.
* **Buscar Reservas:** Permite consultar reservas filtrando por rango de fechas, habitación específica o apellido del huésped.
* **Cancelar Reserva:** Anula una o varias reservas existentes mediante su identificación, liberando la disponibilidad de las habitaciones.
* **Verificar Disponibilidad:** Consulta si una habitación específica está libre en un rango de fechas determinado, cruzando datos de reservas y estadías.

### Gestión de Estadías

* **Registrar Estadía (Check-In):** Formaliza el ingreso de los huéspedes a la habitación, validando fechas y registrando a todos los ocupantes.

### Facturación y Check-Out

* **Calcular Detalle:** Realiza un cálculo previo de la facturación, aplicando recargos por salida tardía y determinando los impuestos según el responsable de pago.
* **Generar Factura (Check-Out):** Emite el comprobante fiscal final, cierra la estadía actualizando su fecha de egreso y libera la habitación.
* **Alta Responsable Jurídico:** Permite registrar una persona jurídica (empresa) como responsable de pago durante el proceso de facturación.

### Cobranza

* **Buscar Facturas Pendientes:** Localiza las facturas impagas asociadas a una habitación específica para proceder al cobro.
* **Registrar Pago:** Asienta el pago de una factura, soportando múltiples medios de pago y validando los montos ingresados.
* **Historial de Pagos:** Consulta el detalle de todos los pagos realizados sobre una factura particular.

### Seguridad y Usuarios

* **Autenticación (Login):** Valida las credenciales de acceso (usuario y contraseña) contra la base de datos para permitir el ingreso al sistema.
* **Crear Usuario:** Permite el registro de nuevos usuarios operadores en el sistema.
---

## 6. Documentación

* **Javadoc:** Comentamos las clases y métodos (públicos y privados) explicando qué hacen y por qué.
* **Swagger:** La documentación de la API se genera sola. Al levantar la app, entrás a `http://localhost:8080/swagger-ui.html` y ves todos los endpoints listos para probar.

---

## 7. Endpoints Implementados (API REST)

A continuación listamos los endpoints principales expuestos por la API, demostrando que superamos el requisito mínimo de operaciones por tipo de verbo HTTP:

### Gestión de Huéspedes
| Verbo | Endpoint | Descripción |
|:---:|:---|:---|
| **GET** | `/huespedes` | Buscar huéspedes con filtros combinados (CU02). |
| **POST** | `/huespedes` | Registrar un nuevo huésped (CU09). |
| **PUT** | `/huespedes/{id}` | Modificar datos de un huésped existente (CU10). |
| **DELETE** | `/huespedes/{id}` | Dar de baja un huésped (CU11). |

### Gestión de Habitaciones y Reservas
| Verbo | Endpoint | Descripción |
|:---:|:---|:---|
| **GET** | `/habitaciones/estado` | Grilla de disponibilidad por fechas (CU05). |
| **GET** | `/habitaciones` | Listado general de habitaciones. |
| **POST** | `/reservas` | Crear una nueva reserva validando fechas (CU04). |
| **DELETE** | `/reservas/{id}` | Cancelar una reserva activa (CU06). |

### Facturación y Pagos
| Verbo | Endpoint | Descripción |
|:---:|:---|:---|
| **POST** | `/estadias` | Registrar Ocupación / Check-in (CU15). |
| **POST** | `/facturas/checkout` | Generar factura y cerrar estadía (CU07). |
| **POST** | `/pagos` | Registrar el pago de una factura (CU16). |
| **POST** | `/responsables` | Alta de Responsable Jurídico (CU12). |

### Seguridad
| Verbo | Endpoint | Descripción |
|:---:|:---|:---|
| **POST** | `/auth/login` | Autenticación de usuarios y generación de sesión (CU01). |

---
## 8. Cómo levantar el proyecto

1. **Clonar el repo.**
2. **Configurar BD:** Poner tus credenciales de PostgreSQL en `src/main/resources/application.properties`.
3. **Ejecutar:** `./mvnw spring-boot:run`
4. **Testear:** `./mvnw test`

---

> **Conclusión:** El proyecto cumple completamente con los estándares de ingeniería de software moderna que pidieron, priorizando un código limpio, mantenible y bien testeado.
