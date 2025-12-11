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
