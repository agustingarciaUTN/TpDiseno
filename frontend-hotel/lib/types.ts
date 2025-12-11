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
