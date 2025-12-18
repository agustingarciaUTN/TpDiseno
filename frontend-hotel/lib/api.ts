// --- CU16: VERIFICAR EXISTENCIA CHEQUE Y TARJETA ---
// Ajusta los endpoints según tu backend
export async function verificarChequeExiste(numeroCheque: string): Promise<any | null> {
  return apiFetch<any | null>(`/pagos/cheque-existe/${numeroCheque}`)
}

export async function verificarTarjetaExiste(numeroTarjeta: string, tipo: string): Promise<any | null> {
  return apiFetch<any | null>(`/pagos/tarjeta-existe/${numeroTarjeta}?tipo=${tipo}`)
}
import { 
  DtoHuesped, 
  BuscarHuespedForm, 
  DtoUsuario,
  DtoHabitacion,
  DtoReserva,
  DtoEstadia,
  DtoPago,
  DtoFactura,
  DtoResultadoRegistroPago
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
      try {
        const contentType = response.headers.get("content-type");
        let errorMessage = "";

        if (contentType && contentType.includes("application/json")) {
          const errorData = await response.json();
          if (errorData && typeof errorData === 'object') {
            if (errorData.message) {
              errorMessage = errorData.message;
            } else {
              const valores = Object.values(errorData);
              if (valores.length > 0 && valores.every(v => typeof v === 'string')) {
                errorMessage = valores.join('. ');
              } else {
                errorMessage = JSON.stringify(errorData);
              }
            }
          } else {
            errorMessage = String(errorData);
          }
        } else {
          errorMessage = await response.text();
        }

        // Si el error es "No existen facturas pendientes de pago", devolver null o [] según el tipo esperado
        if (errorMessage && errorMessage.toLowerCase().includes("no existen facturas pendientes de pago")) {
          // @ts-ignore
          return ([] as unknown) as T;
        }

        // Si es cheque o tarjeta no existe (404), no mostrar error en consola, solo lanzar para manejo de frontend
        if (
          response.status === 404 && (
            endpoint.includes("cheque-existe") ||
            endpoint.includes("tarjeta-existe") ||
            errorMessage.toLowerCase().includes("no existe el cheque") ||
            errorMessage.toLowerCase().includes("no existe la tarjeta")
          )
        ) {
          throw new Error(errorMessage || `Error HTTP 404`);
        }

        throw new Error(errorMessage || `Error HTTP ${response.status}`);
      } catch (parseError: any) {
        if (parseError instanceof Error && !parseError.message.startsWith("Unexpected token")) {
          throw parseError;
        }
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
    // Si es cheque o tarjeta no existe (404), no mostrar error en consola
    if (
      (endpoint.includes("cheque-existe") && error?.message?.toLowerCase().includes("no existe el cheque")) ||
      (endpoint.includes("tarjeta-existe") && error?.message?.toLowerCase().includes("no existe la tarjeta"))
    ) {
      // No loguear como error
      throw error;
    }
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

// --- RESERVAS: Buscar por rango (opcionalmente por habitación) ---
export async function buscarReservas(
  fechaDesde: string,
  fechaHasta: string,
  idHabitacion?: string
): Promise<DtoReserva[]> {
  const params = new URLSearchParams({ fechaDesde, fechaHasta })
  if (idHabitacion) params.append("idHabitacion", idHabitacion)
  return apiFetch<DtoReserva[]>(`/reservas/buscar?${params.toString()}`)
}

// --- CU16: REGISTRAR PAGO ---

export async function buscarFacturasPendientes(numeroHabitacion: string): Promise<DtoFactura[]> {
  return apiFetch<DtoFactura[]>(`/pagos/buscar-facturas-pendientes?numeroHabitacion=${numeroHabitacion}`)
}

export async function registrarPago(pago: DtoPago): Promise<DtoResultadoRegistroPago> {
  return apiFetch<DtoResultadoRegistroPago>("/pagos/registrar", {
    method: "POST",
    body: pago,
  })
}


// CU11: Dar de Baja Huésped
export async function darDeBajaHuesped(tipo: string, nro: string): Promise<string> {
    return apiFetch<string>(`/huespedes/borrar/${tipo}/${nro}`, {
        method: "DELETE",
    })
}

// Buscar reservas por apellido/nombre
export async function buscarReservasPorHuesped(apellido: string, nombre: string): Promise<any[]> {
    const params = new URLSearchParams({ apellido });
    if (nombre) params.append("nombre", nombre);

    return apiFetch<any[]>(`/reservas/buscar-huesped?${params.toString()}`, {
        method: "GET",
    });
}

// Cancelar lista de reservas
export async function cancelarReservas(ids: number[]): Promise<string> {
    return apiFetch<string>("/reservas/cancelar", {
        method: "POST",
        body: ids,
    });
}