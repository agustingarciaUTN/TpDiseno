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

// --- PAGO ---
export enum Moneda {
  ARS = "ARS",
  USD = "USD",
  EUR = "EUR"
}

export interface DtoPago {
  idPago: number
  moneda: Moneda
  montoTotal: number
  cotizacion: number
  fechaPago: string
  Factura: any // Simplificado por ahora
  idsMediosPago?: number[]
}
