import { 
  DtoHuesped, 
  BuscarHuespedForm, 
  DtoUsuario,
  DtoHabitacion,
  DtoReserva,
  DtoEstadia,
  DtoPago
} from "./types"

// Asegúrate de que esta URL sea correcta. Si usas Docker o red local, ajusta la IP.
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api"

// Interfaz genérica para manejo de errores
interface ApiError {
  message: string;
  [key: string]: any;
}

// Interfaz personalizada para opciones de fetch
interface ApiFetchOptions extends Omit<RequestInit, 'body'> {
  body?: any;
}

export async function apiFetch<T>(endpoint: string, options: ApiFetchOptions = {}): Promise<T> {
  const { method = "GET", body, headers = {}, ...rest } = options

  const config: RequestInit = {
    method,
    headers: {
      "Content-Type": "application/json",
      ...headers,
    },
    ...rest,
  }

  if (body && method !== "GET") {
    config.body = JSON.stringify(body)
  }

  try {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, config)

    if (!response.ok) {
      // Intentar leer el error del backend (ej: validaciones de Spring Boot)
      const errorData = await response.json().catch(() => null)
      const errorMessage = errorData?.message || errorData ? JSON.stringify(errorData) : `Error HTTP ${response.status}`;
      throw new Error(errorMessage)
    }

    // Si la respuesta es 204 No Content o vacía
    if (response.status === 204) return {} as T;

    // Verificar si hay contenido antes de parsear JSON
    const text = await response.text();
    return text ? JSON.parse(text) : {} as T;

  } catch (error: any) {
    console.error(`API Error [${method} ${endpoint}]:`, error)
    throw error
  }
}

// --- CU1: AUTENTICAR USUARIO ---

export async function autenticarUsuario(credenciales: DtoUsuario): Promise<string> {
  return apiFetch<string>("/usuarios/login", {
    method: "POST",
    body: credenciales,
  })
}

// --- CU2: BUSCAR HUÉSPED ---

export async function buscarHuespedes(criterios: Partial<BuscarHuespedForm>): Promise<DtoHuesped[]> {
  return apiFetch<DtoHuesped[]>("/huespedes/buscar", {
    method: "POST",
    body: criterios,
  })
}

// --- CU9: ALTA HUÉSPED ---

export async function verificarExistenciaHuesped(
  tipo: string, 
  nroDocumento: string
): Promise<DtoHuesped | null> {
  return apiFetch<DtoHuesped | null>(`/huespedes/existe/${tipo}/${nroDocumento}`)
}

export async function crearHuesped(huesped: any): Promise<string> {
  return apiFetch<string>("/huespedes/crear", {
    method: "POST",
    body: huesped,
  })
}

export async function modificarHuesped(
  tipo: string, 
  nroDocumento: string, 
  huesped: any
): Promise<string> {
  return apiFetch<string>(`/huespedes/modificar/${tipo}/${nroDocumento}`, {
    method: "PUT",
    body: huesped,
  })
}

// --- CU5: MOSTRAR ESTADO HABITACIONES ---

export async function obtenerHabitaciones(): Promise<DtoHabitacion[]> {
  return apiFetch<DtoHabitacion[]>("/habitaciones")
}

export async function obtenerHabitacionPorNumero(numero: string): Promise<DtoHabitacion> {
  return apiFetch<DtoHabitacion>(`/habitaciones/${numero}`)
}

// --- CU4: RESERVAR HABITACIÓN ---

export async function verificarDisponibilidadHabitacion(
  idHabitacion: string,
  fechaDesde: string,
  fechaHasta: string
): Promise<boolean> {
  const params = new URLSearchParams({
    idHabitacion,
    fechaDesde,
    fechaHasta
  })
  return apiFetch<boolean>(`/reservas/disponibilidad?${params}`)
}

export async function crearReserva(reserva: DtoReserva | DtoReserva[]): Promise<string> {
  // El backend espera una LISTA de reservas
  const payload = Array.isArray(reserva) ? reserva : [reserva]
  return apiFetch<string>("/reservas/crear", {
    method: "POST",
    body: payload
  })
}

// --- CU15: OCUPAR HABITACIÓN (CHECK-IN) ---

export async function crearEstadia(estadia: DtoEstadia): Promise<string> {
  return apiFetch<string>("/estadias/crear", {
    method: "POST",
    body: estadia,
  })
}

// --- PAGOS ---

export async function registrarPago(pago: DtoPago): Promise<string> {
  return apiFetch<string>("/pagos/registrar", {
    method: "POST",
    body: pago,
  })
}