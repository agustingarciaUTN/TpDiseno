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

// Validaciones
export const VALIDATION = {
  REGEX_NOMBRE: /^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ]+$/,
  REGEX_DOCUMENTO: /^[a-zA-Z0-9]+$/,
}

// Formulario de búsqueda
export interface BuscarHuespedForm {
  apellido: string
  nombres: string
  tipoDocumento: string
  nroDocumento: string
}

// DTO del huésped (según backend)
export interface DtoHuesped {
  id: number
  apellido: string
  nombres: string
  tipoDocumento: TipoDocumento
  nroDocumento: string
  domicilio?: string
  telefono?: string
  email?: string
}

// Enums iguales a Java
export type Moneda = 'PESOS_ARGENTINOS' | 'DOLARES' | 'EUROS' | 'REALES' | 'PESOS_URUGUAYOS';
export type TipoMedioPago = 'EFECTIVO' | 'TARJETA_DEBITO' | 'TARJETA_CREDITO' | 'CHEQUE';

// Clases Padre/Hijo para Medios de Pago
export interface MedioPago {
    tipoMedio: TipoMedioPago;
    monto: number;
    moneda: Moneda;
    fechaDePago: string;
}

export interface Efectivo extends MedioPago {
    tipoMedio: 'EFECTIVO';
}

export interface TarjetaDebito extends MedioPago {
    tipoMedio: 'TARJETA_DEBITO';
    banco: string;
    numeroDeTarjeta: string;
}

// DTO Principal de Pago
export interface DtoPago {
    idPago: number;
    montoTotal: number;
    idFactura: number;
    mediosPago: MedioPago[];
}


// Interface del Formulario de Búsqueda
export interface BuscarHuespedForm {
    apellido: string;
    nombres: string;
    tipoDocumento: TipoDocumento | ""; // Puede estar vacío en el form
    nroDocumento: string;
}

