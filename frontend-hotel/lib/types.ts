// Enums mapeados desde el backend
export enum TipoDocumento {
  DNI = "DNI",
  LE = "LE",
  LC = "LC",
  PASAPORTE = "PASAPORTE",
  OTRO = "OTRO",
}

export enum PosIva {
  RESPONSABLE_INSCRIPTO = "RESPONSABLE_INSCRIPTO",
  MONOTRIBUTISTA = "MONOTRIBUTISTA",
  EXENTO = "EXENTO",
  CONSUMIDOR_FINAL = "CONSUMIDOR_FINAL",
}

// Validaciones regex del backend DtoHuesped y DtoReserva
export const VALIDATION = {
  REGEX_NOMBRE: /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/,
  REGEX_DOCUMENTO: /^[a-zA-Z0-9]+$/, // Alfanumérico para TODOS los tipos de documento
  REGEX_CUIT: /^\d{2}-?\d{8}-?\d{1}$/,
  REGEX_TELEFONO: /^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\s\./0-9]*$/,
} as const;

// Labels para UI
export const TIPO_DOCUMENTO_LABELS: Record<TipoDocumento, string> = {
  [TipoDocumento.DNI]: "DNI",
  [TipoDocumento.LE]: "Libreta de Enrolamiento",
  [TipoDocumento.LC]: "Libreta Cívica",
  [TipoDocumento.PASAPORTE]: "Pasaporte",
  [TipoDocumento.OTRO]: "Otro",
};

// DTO parcial para búsqueda (CU02)
export interface BuscarHuespedForm {
  apellido: string;
  nombres: string;
  tipoDocumento: TipoDocumento | "";
  nroDocumento: string;
}

// DTO completo (espejo del backend)
export interface DtoHuesped {
  nombres: string;
  apellido: string;
  tipoDocumento: TipoDocumento;
  nroDocumento: string;
  cuit?: string;
  posicionIva: PosIva;
  fechaNacimiento: string; // ISO 8601 format
  nacionalidad: string;
  email: string[];
  ocupacion?: string[];
  telefono: number[];
  dtoDireccion: DtoDireccion;
}

export interface DtoDireccion {
  calle: string;
  numero: string;
  piso?: string;
  departamento?: string;
  codigoPostal: string;
  localidad: string;
  provincia: string;
  pais: string;
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

