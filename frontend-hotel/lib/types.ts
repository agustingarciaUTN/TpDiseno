export enum TipoDocumento {
  DNI = "DNI",
  LE = "LE",
  LC = "LC",
  PASAPORTE = "PASAPORTE",
  OTRO = "OTRO",
}

export const TIPO_DOCUMENTO_LABELS: Record<TipoDocumento, string> = {
  [TipoDocumento.DNI]: "DNI",
  [TipoDocumento.LE]: "LE (Libreta de Enrolamiento)",
  [TipoDocumento.LC]: "LC (Libreta Cívica)",
  [TipoDocumento.PASAPORTE]: "Pasaporte",
  [TipoDocumento.OTRO]: "Otro",
}

export enum PosicionIva {
  CONSUMIDOR_FINAL = "CONSUMIDOR_FINAL",
  MONOTRIBUTISTA = "MONOTRIBUTISTA",
  RESPONSABLE_INSCRIPTO = "RESPONSABLE_INSCRIPTO",
  EXENTO = "EXENTO",
}

// Validaciones
export const VALIDATION = {
  REGEX_NOMBRE: /^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\s]+$/,
  REGEX_DOCUMENTO: /^[a-zA-Z0-9]+$/,
  REGEX_EMAIL: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
  REGEX_TELEFONO: /^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\s./0-9]*$/,
  REGEX_CUIT: /^\d{2}-?\d{8}-?\d{1}$/,
  REGEX_DIRECCION: /^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\s.,]+$/,
}

// Formulario de búsqueda
export interface BuscarHuespedForm {
  apellido: string
  nombres: string
  tipoDocumento: string
  nroDocumento: string
}

export interface DtoHuesped {
  idHuesped: number
  apellido: string
  nombres: string
  tipoDocumento: TipoDocumento
  nroDocumento: string
  fechaNacimiento?: string
  nacionalidad?: string
  email?: string[]
  telefono?: string[]
  cuit?: string
  posicionIva?: PosicionIva
  domicilio?: DtoDomicilio
}

export interface DtoDomicilio {
  calle: string
  numero: number
  piso?: string
  departamento?: string
  codPostal: number
  localidad: string
  provincia: string
  pais: string
}

export interface HuespedFormData {
  apellido: string
  nombres: string
  tipoDocumento: string
  nroDocumento: string
  fechaNacimiento: string
  nacionalidad: string
  email: string
  telefono: string
  cuit?: string
  posicionIva: string
  calle: string
  numero: string
  piso?: string
  departamento?: string
  codPostal: string
  localidad: string
  provincia: string
  pais: string
}

// --- USUARIO (CU1) ---
export interface DtoUsuario {
  nombre: string
  contrasenia: string
}

// --- HABITACIÓN (CU5) ---
export enum EstadoHabitacion {
  HABILITADA = "HABILITADA",
  FUERA_DE_SERVICIO = "FUERA_DE_SERVICIO"
}

export interface DtoHabitacion {
  numero: string
  tipoHabitacion: string
  capacidad: number
  estadoHabitacion: EstadoHabitacion
  costoPorNoche: number
}

// --- RESERVA (CU4) ---
export enum EstadoReserva {
  ACTIVA = "ACTIVA",
  CANCELADA = "CANCELADA",
  COMPLETADA = "COMPLETADA"
}

export interface DtoReserva {
  idReserva: number
  estadoReserva: EstadoReserva
  fechaReserva?: string
  fechaDesde: string
  fechaHasta: string
  nombreHuespedResponsable: string
  apellidoHuespedResponsable: string
  telefonoHuespedResponsable: string
  idHabitacion: string
}

// --- ESTADÍA (CU15) ---
export interface DtoEstadia {
  idEstadia?: number
  fechaCheckIn: string
  fechaCheckOut?: string
  valorEstadia: number
  dtoReserva?: DtoReserva
  dtoHuespedes?: DtoHuesped[]
  dtoHabitacion: DtoHabitacion
}

// --- PAGO (CU16) ---
export enum Moneda {
  PESOS_ARGENTINOS = "PESOS_ARGENTINOS",
  DOLARES = "DOLARES",
  REALES = "REALES",
  PESOS_URUGUAYOS = "PESOS_URUGUAYOS",
  EUROS = "EUROS"
}

export enum EstadoFactura {
  PENDIENTE = "PENDIENTE",
  PAGADA = "PAGADA"
}

export enum TipoMedioPago {
  EFECTIVO = "EFECTIVO",
  CHEQUE = "CHEQUE",
  TARJETA_CREDITO = "TARJETA_CREDITO",
  TARJETA_DEBITO = "TARJETA_DEBITO"
}

export interface DtoFactura {
  numeroFactura: string
  fechaEmision: string
  importeTotal: number
  importeNeto?: number
  iva?: number
  estadoFactura: EstadoFactura
  tipoFactura?: string
  fechaVencimiento?: string
  idEstadia?: any
  idResponsable?: any
  nombreResponsable?: string
  apellidoResponsable?: string
  dtoNotaDeCredito?: any
}

export interface DtoEfectivo {
  idEfectivo?: number
  importe: number
}

export interface DtoCheque {
  numeroCheque: string
  banco: string
  plaza: string
  fechaCobro: string
  monto: number
}

export interface DtoTarjetaCredito {
  numeroTarjeta: string
  redDePago: string
  cuotas: number
  codigoSeguridad: number
  fechaVencimiento: string
  monto: number
}

export interface DtoTarjetaDebito {
  numeroTarjeta: string
  redDePago: string
  monto: number
}

export interface DtoMedioPago {
  idMedioPago?: number
  tipoMedio: TipoMedioPago
  monto: number
  moneda: Moneda
  fechaDePago: string
  efectivo?: DtoEfectivo
  cheque?: DtoCheque
  tarjetaCredito?: DtoTarjetaCredito
  tarjetaDebito?: DtoTarjetaDebito
}

export interface DtoPago {
  idPago?: number
  numeroFactura: string
  fechaPago: string
  moneda: Moneda
  cotizacion: number
  montoTotal: number
  mediosPago: DtoMedioPago[]
}

export interface DtoResultadoRegistroPago {
  mensaje: string
  vuelto: number
  saldoPendiente: number
  numeroFactura: string
  estadoFactura: string
  estadoHabitacion?: string
  facturaSaldada: boolean
}
