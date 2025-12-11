import { type NextRequest, NextResponse } from "next/server"
import { type DtoHuesped, TipoDocumento } from "@/lib/types"

// Datos de ejemplo para simular el backend
const HUESPEDES_MOCK: DtoHuesped[] = [
  {
    id: 1,
    apellido: "García",
    nombres: "Juan Carlos",
    tipoDocumento: TipoDocumento.DNI,
    nroDocumento: "12345678",
    domicilio: "Av. Corrientes 1234, CABA",
    telefono: "+54 11 4567-8900",
    email: "jgarcia@email.com",
  },
  {
    id: 2,
    apellido: "González",
    nombres: "María Laura",
    tipoDocumento: TipoDocumento.DNI,
    nroDocumento: "23456789",
    domicilio: "Calle Falsa 123, Buenos Aires",
    telefono: "+54 11 5678-9012",
    email: "mgonzalez@email.com",
  },
  {
    id: 3,
    apellido: "Fernández",
    nombres: "Carlos Alberto",
    tipoDocumento: TipoDocumento.DNI,
    nroDocumento: "34567890",
    domicilio: "San Martín 456, Rosario",
    telefono: "+54 341 234-5678",
    email: "cfernandez@email.com",
  },
  {
    id: 4,
    apellido: "Rodríguez",
    nombres: "Ana María",
    tipoDocumento: TipoDocumento.LE,
    nroDocumento: "4567890",
    domicilio: "Belgrano 789, Córdoba",
    telefono: "+54 351 345-6789",
  },
  {
    id: 5,
    apellido: "López",
    nombres: "José Luis",
    tipoDocumento: TipoDocumento.PASAPORTE,
    nroDocumento: "AB123456",
    domicilio: "Mitre 321, Mendoza",
    telefono: "+54 261 456-7890",
    email: "jlopez@email.com",
  },
  {
    id: 6,
    apellido: "Martínez",
    nombres: "Laura Beatriz",
    tipoDocumento: TipoDocumento.DNI,
    nroDocumento: "45678901",
    domicilio: "Rivadavia 654, La Plata",
    telefono: "+54 221 567-8901",
    email: "lmartinez@email.com",
  },
  {
    id: 7,
    apellido: "Gómez",
    nombres: "Roberto Carlos",
    tipoDocumento: TipoDocumento.DNI,
    nroDocumento: "56789012",
    domicilio: "Sarmiento 987, Tucumán",
    telefono: "+54 381 678-9012",
  },
  {
    id: 8,
    apellido: "Pérez",
    nombres: "Silvia Beatriz",
    tipoDocumento: TipoDocumento.DNI,
    nroDocumento: "67890123",
    domicilio: "9 de Julio 147, Salta",
    telefono: "+54 387 789-0123",
    email: "sperez@email.com",
  },
]

export async function GET(request: NextRequest) {
  try {
    const searchParams = request.nextUrl.searchParams
    const apellido = searchParams.get("apellido")?.toUpperCase()
    const nombres = searchParams.get("nombres")?.toUpperCase()
    const tipoDocumento = searchParams.get("tipoDocumento")
    const nroDocumento = searchParams.get("nroDocumento")

    // Simulamos un delay del backend
    await new Promise((resolve) => setTimeout(resolve, 500))

    // Filtrar huéspedes según criterios
    let resultados = HUESPEDES_MOCK

    if (apellido) {
      resultados = resultados.filter((h) => h.apellido.toUpperCase().startsWith(apellido))
    }

    if (nombres) {
      resultados = resultados.filter((h) => h.nombres.toUpperCase().startsWith(nombres))
    }

    if (tipoDocumento) {
      resultados = resultados.filter((h) => h.tipoDocumento === tipoDocumento)
    }

    if (nroDocumento) {
      resultados = resultados.filter((h) => h.nroDocumento.toLowerCase().includes(nroDocumento.toLowerCase()))
    }

    return NextResponse.json(resultados)
  } catch (error) {
    console.error("Error en búsqueda de huéspedes:", error)
    return NextResponse.json({ error: "Error al buscar huéspedes" }, { status: 500 })
  }
}
