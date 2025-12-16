# CU16 - Registrar Pago

## Descripción
Este caso de uso permite registrar el pago de facturas pendientes al momento del check out de una habitación.

## Flujo Principal

### 1. Buscar Facturas Pendientes por Habitación

**Endpoint:** `POST /api/pagos/buscar-facturas-pendientes`

**Request:**
```json
{
  "numeroHabitacion": "101"
}
```

**Response exitosa (200 OK):**
```json
[
  {
    "numeroFactura": "F-001",
    "responsableNombre": "Juan",
    "responsableApellido": "Pérez",
    "monto": 5000.0,
    "fecha": "2025-12-05",
    "estado": "PENDIENTE",
    "numeroHabitacion": "101"
  }
]
```

**Errores posibles:**
- `400 Bad Request`: 
  - "Debe ingresar un número de habitación"
  - "El número de habitación debe tener exactamente 3 dígitos"
  - "Número de habitación incorrecto"
  - "No existen facturas pendientes de pago"

### 2. Registrar el Pago

**Endpoint:** `POST /api/pagos/registrar-pago`

**Request:**
```json
{
  "numeroFactura": "F-001",
  "fechaPago": "2025-12-15",
  "moneda": "PESOS_ARGENTINOS",
  "cotizacion": 1.0,
  "montoTotal": 5000.0,
  "mediosPago": [
    {
      "tipoMedio": "EFECTIVO",
      "monto": 5000.0,
      "moneda": "PESOS_ARGENTINOS",
      "fechaDePago": "2025-12-15"
    }
  ]
}
```

**Tipos de medios de pago:**

#### Efectivo (EFECTIVO)
```json
{
  "tipoMedio": "EFECTIVO",
  "monto": 5000.0,
  "moneda": "PESOS_ARGENTINOS",
  "fechaDePago": "2025-12-15"
}
```

#### Cheque (CHEQUE)
```json
{
  "tipoMedio": "CHEQUE",
  "numeroCheque": "12345678",
  "banco": "Banco Nación",
  "plaza": "Buenos Aires",
  "monto": 5000.0,
  "moneda": "PESOS_ARGENTINOS",
  "fechaCobro": "2025-12-20",
  "fechaDePago": "2025-12-15"
}
```

#### Tarjeta de Crédito (TARJETA_CREDITO)
```json
{
  "tipoMedio": "TARJETA_CREDITO",
  "numeroDeTarjeta": "4111111111111111",
  "banco": "Banco Santander",
  "redDePago": "VISA",
  "fechaVencimiento": "2027-12-31",
  "codigoSeguridad": 123,
  "monto": 5000.0,
  "moneda": "PESOS_ARGENTINOS",
  "fechaDePago": "2025-12-15",
  "cuotasCantidad": 3
}
```

#### Tarjeta de Débito (TARJETA_DEBITO)
```json
{
  "tipoMedio": "TARJETA_DEBITO",
  "numeroDeTarjeta": "5111111111111111",
  "banco": "Banco Galicia",
  "redDePago": "MASTERCARD",
  "fechaVencimiento": "2027-12-31",
  "codigoSeguridad": 456,
  "monto": 5000.0,
  "moneda": "PESOS_ARGENTINOS",
  "fechaDePago": "2025-12-15"
}
```

**Response exitosa (200 OK):**
```json
{
  "mensaje": "Factura saldada. TOQUE UNA TECLA PARA CONTINUAR…",
  "vuelto": 0.0,
  "numeroFactura": "F-001",
  "estadoFactura": "PAGADA",
  "estadoHabitacion": "La deuda de la habitación ha sido cancelada",
  "facturaSaldada": true
}
```

**Errores posibles:**
- `400 Bad Request`:
  - "La factura no existe"
  - "La factura ya está pagada"
  - "Debe proporcionar una cotización válida" (si la moneda no es Pesos Argentinos)
  - "El monto ingresado es menor a la deuda. Resta pagar: $X.XX"
  - "Debe completar todos los datos del [medio de pago]"

## Monedas Disponibles
- `PESOS_ARGENTINOS`
- `DOLARES`
- `REALES`
- `PESOS_URUGUAYOS`
- `EUROS`

## Redes de Pago (para tarjetas)
Enum: `RedDePago`
- Valores: (revisar el enum RedDePago.java para los valores exactos)

## Validaciones Implementadas

### A nivel de DTO:
1. **DtoBuscarFacturasPorHabitacion:**
   - `numeroHabitacion`: Obligatorio, debe tener exactamente 3 dígitos

2. **DtoRegistrarPago:**
   - `numeroFactura`: Obligatorio, no vacío
   - `fechaPago`: Obligatoria
   - `moneda`: Obligatoria
   - `cotizacion`: Mayor o igual a cero
   - `mediosPago`: Obligatorio, al menos un medio de pago
   - `montoTotal`: Obligatorio, debe ser positivo

### A nivel de Servicio:
1. Verificar que la habitación existe
2. Verificar que hay una estadía activa en la habitación
3. Verificar que existen facturas pendientes
4. Verificar que la factura existe y está pendiente
5. Validar que el monto pagado cubre la deuda (aplicando cotización si corresponde)
6. Todos los medios de pago deben tener sus datos completos

## Flujo de Datos

### Backend
1. **DTOs creados:**
   - `DtoBuscarFacturasPorHabitacion`: Request para buscar facturas
   - `DtoFacturaPendiente`: Response con datos de facturas pendientes
   - `DtoRegistrarPago`: Request para registrar el pago
   - `DtoResponsePago`: Response con resultado del pago y vuelto

2. **Servicios:**
   - `PagoService.buscarFacturasPendientesPorHabitacion()`: Busca facturas pendientes
   - `PagoService.registrarPago()`: Registra el pago y actualiza estado de factura
   - `PagoService.obtenerPagosPorFactura()`: Obtiene historial de pagos

3. **Controlador:**
   - `PagoController.buscarFacturasPendientes()`: Endpoint de búsqueda
   - `PagoController.registrarPagoNuevo()`: Endpoint de registro
   - `PagoController.obtenerPagosPorFactura()`: Endpoint de consulta

## Base de Datos

### Tablas involucradas:
- `factura`: Información de las facturas
- `pago`: Registro de los pagos realizados
- `medio_pago`: Tabla de vinculación de medios de pago
- `efectivo`: Datos de pagos en efectivo
- `cheque`: Datos de pagos con cheque
- `tarjeta`: Datos base de tarjetas
- `tarjeta_credito`: Datos específicos de tarjetas de crédito (cuotas)
- `tarjeta_debito`: Datos específicos de tarjetas de débito
- `estadia`: Para obtener la habitación asociada a la factura
- `habitacion`: Para validar la habitación

### Relaciones:
- `Pago` N:1 `Factura`
- `MedioPago` N:1 `Pago`
- `MedioPago` 1:1 `Efectivo` (opcional)
- `MedioPago` 1:1 `Cheque` (opcional)
- `MedioPago` 1:1 `Tarjeta` (opcional)
- `Tarjeta` es abstracta, implementada por `TarjetaCredito` y `TarjetaDebito`

## Observaciones
1. **Manejo de múltiples medios de pago:** El sistema permite registrar un pago utilizando múltiples medios (por ejemplo, parte en efectivo y parte con tarjeta).

2. **Cotización de monedas:** Si se paga en una moneda diferente a Pesos Argentinos, se debe proporcionar la cotización del día para convertir el monto.

3. **Vuelto:** El sistema calcula automáticamente el vuelto si el monto pagado es mayor al monto adeudado.

4. **Actualización de estado:** Al registrar un pago que cubre totalmente la deuda, la factura pasa de estado `PENDIENTE` a `PAGADA`.

5. **Verificación de deuda total:** El sistema verifica si todas las facturas de la estadía están pagadas para informar que la deuda de la habitación fue cancelada.

6. **Conversión de mayúsculas:** Según el CU original, todos los datos literales deben ingresarse en mayúsculas independientemente del estado de <bloq mayús>. Esto se debe manejar en el frontend.

## Testing
Para probar el CU16, puedes usar los siguientes pasos:

1. Crear una habitación y una estadía activa
2. Crear una factura pendiente asociada a esa estadía
3. Buscar facturas pendientes por número de habitación
4. Registrar un pago para la factura seleccionada
5. Verificar que el estado de la factura cambió a PAGADA
6. Verificar que se calculó correctamente el vuelto
