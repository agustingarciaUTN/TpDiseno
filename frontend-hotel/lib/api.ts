import { DtoHuesped } from "./types"

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api"

interface ApiFetchOptions {
  method?: "GET" | "POST" | "PUT" | "DELETE"
  body?: any
  headers?: Record<string, string>
}

/**
 * Wrapper para hacer peticiones fetch al backend Spring Boot
 * Maneja automáticamente JSON, errores y headers
 */
export async function apiFetch<T>(endpoint: string, options: ApiFetchOptions = {}): Promise<T> {
  const { method = "GET", body, headers = {} } = options

  const config: RequestInit = {
    method,
    headers: {
      "Content-Type": "application/json",
      ...headers,
    },
  }

  if (body && method !== "GET") {
    config.body = JSON.stringify(body)
  }

  const url = `${API_BASE_URL}${endpoint}`

  try {
    const response = await fetch(url, config)

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({ message: "Error en la solicitud" }))
      throw new Error(errorData.message || `Error ${response.status}`)
    }

    return await response.json()
  } catch (error: any) {
    console.error(`API Error [${method} ${endpoint}]:`, error)
    throw error
  }
}

// ============================================
// FUNCIONES ESPECÍFICAS PARA HUÉSPEDES
// ============================================

/**
 * Busca huéspedes según criterios
 */
export async function buscarHuespedes(criterios: Partial<DtoHuesped>): Promise<DtoHuesped[]> {
  return apiFetch<DtoHuesped[]>("/huespedes/buscar", {
    method: "POST",
    body: criterios,
  })
}

/**
 * Verifica si existe un huésped con ese tipo y número de documento
 */
export async function verificarExistenciaHuesped(
  tipo: string,
  nro: string
): Promise<DtoHuesped | null> {
  return apiFetch<DtoHuesped | null>(`/huespedes/existe/${tipo}/${nro}`)
}

/**
 * Crea un nuevo huésped
 */
export async function crearHuesped(huesped: DtoHuesped): Promise<DtoHuesped> {
  return apiFetch<DtoHuesped>("/huespedes", {
    method: "POST",
    body: huesped,
  })
}

/**
 * Obtiene un huésped por ID
 */
export async function obtenerHuespedPorId(id: number): Promise<DtoHuesped> {
  return apiFetch<DtoHuesped>(`/huespedes/${id}`)
}

// ============================================
// FUNCIONES PARA OTROS RECURSOS
// ============================================

// Aquí puedes agregar funciones similares para:
// - Habitaciones (/habitaciones)
// - Reservas (/reservas)
// - Estadías (/estadias)
// - Pagos (/pagos)
// - Usuarios (/usuarios)
