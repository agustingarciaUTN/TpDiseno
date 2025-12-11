import { VALIDATION } from "./types"

/**
 * Valida un campo según su tipo
 * @param field Nombre del campo a validar
 * @param value Valor del campo
 * @returns Mensaje de error o undefined si es válido
 */
export function validateField(field: string, value: string): string | undefined {
  if (!value || value.trim() === "") {
    return undefined
  }

  switch (field) {
    case "apellido":
    case "nombres":
      if (!VALIDATION.REGEX_NOMBRE.test(value)) {
        return "Solo puede contener letras"
      }
      break

    case "nroDocumento":
      if (!VALIDATION.REGEX_DOCUMENTO.test(value)) {
        return "El documento no debe contener espacios ni símbolos"
      }
      break

    case "email":
      if (!VALIDATION.REGEX_EMAIL.test(value)) {
        return "Email inválido"
      }
      break

    case "telefono":
      if (!VALIDATION.REGEX_TELEFONO.test(value)) {
        return "Teléfono inválido"
      }
      break

    case "cuit":
      if (!VALIDATION.REGEX_CUIT.test(value)) {
        return "CUIT inválido (formato: XX-XXXXXXXX-X)"
      }
      break

    case "calle":
    case "localidad":
    case "provincia":
    case "pais":
      if (!VALIDATION.REGEX_DIRECCION.test(value)) {
        return "Formato de dirección inválido"
      }
      break

    default:
      return undefined
  }

  return undefined
}

/**
 * Valida que al menos un campo de búsqueda esté completo
 */
export function validateSearchForm(apellido: string, nombres: string, nroDocumento: string): boolean {
  return apellido.trim() !== "" || nombres.trim() !== "" || nroDocumento.trim() !== ""
}
