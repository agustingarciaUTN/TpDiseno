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

  const fullUrl = `${API_BASE_URL}${endpoint}`
  console.log(`[API] ${method} ${fullUrl}`)
  if (body) console.log("[API] Body:", body)

  try {
    const response = await fetch(fullUrl, config)
    console.log(`[API] Response status: ${response.status}`)

      if (!response.ok) {
          // Intentar leer el error del backend
          try {
              const contentType = response.headers.get("content-type");
              let errorMessage = "";

              if (contentType && contentType.includes("application/json")) {
                  const errorData = await response.json();

                  // Detectar si es un mapa de errores de validación
                  if (errorData && typeof errorData === 'object') {
                      // Caso 1: Tiene propiedad message estándar
                      if (errorData.message) {
                          errorMessage = errorData.message;
                      }
                      // Caso 2: Es un objeto tipo {"campo": "Error"}
                      else {
                          const valores = Object.values(errorData);
                          // Si hay valores y son strings, los unimos
                          if (valores.length > 0 && valores.every(v => typeof v === 'string')) {
                              errorMessage = valores.join('. ');
                          } else {
                              // Fallback: Si la estructura es rara, mostramos el JSON
                              errorMessage = JSON.stringify(errorData);
                          }
                      }
                  } else {
                      // Si el JSON es un string directo
                      errorMessage = String(errorData);
                  }

              } else {
                  // Si es texto plano, leerlo directamente
                  errorMessage = await response.text();
              }

              throw new Error(errorMessage || `Error HTTP ${response.status}`);
          } catch (parseError: any) {
              // Si lo que atrapamos ya es el Error que lanzamos arriba, lo relanzamos tal cual
              if (parseError instanceof Error && !parseError.message.startsWith("Unexpected token")) {
                  throw parseError;
              }
              // Si fue un error real de parseo o de red
              throw new Error(`Error HTTP ${response.status}`);
          }
      }

    // Si la respuesta es 204 No Content o vacía
    if (response.status === 204) return {} as T;

    // Verificar si hay contenido antes de parsear JSON
    const contentType = response.headers.get("content-type");
    console.log("[API] Content-Type:", contentType);
    
    if (!contentType || !contentType.includes("application/json")) {
      console.log("[API] No JSON content, returning null");
      return null as T;
    }
    
    const text = await response.text();
    console.log("[API] Response text length:", text.length);
    
    if (!text || text === "null") {
      console.log("[API] Empty or null response");
      return null as T;
    }
    
    // Limpiar el texto antes de parsear (remover caracteres inválidos)
    const cleanText = text.trim();
    
    try {
      const parsed = JSON.parse(cleanText);
      console.log("[API] Parsed response");
      return parsed;
    } catch (parseError: any) {
      console.error("[API] JSON Parse Error:", parseError.message);
      console.error("[API] Problematic text (first 500 chars):", cleanText.substring(0, 500));
      console.error("[API] Problematic text (last 500 chars):", cleanText.substring(cleanText.length - 500));
      throw new Error("Error al parsear respuesta del servidor");
    }

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