import { type NextRequest, NextResponse } from "next/server"
import { type DtoHuesped, TipoDocumento } from "@/lib/types"

// Datos de ejemplo para simular el backend
const HUESPEDES_MOCK: DtoHuesped[] = [
  {
    idHuesped: 1,
    apellido: "García",
    nombres: "Juan Carlos",
    tipoDocumento: TipoDocumento.DNI,
    nroDocumento: "12345678",
  },
  {
    idHuesped: 2,
    apellido: "González",
    nombres: "María Laura",
    tipoDocumento: TipoDocumento.DNI,
    nroDocumento: "23456789",
  },
  {
    idHuesped: 3,
    apellido: "Fernández",
    nombres: "Carlos Alberto",
    tipoDocumento: TipoDocumento.DNI,
    nroDocumento: "34567890",
  },
  {
    idHuesped: 4,
    apellido: "Rodríguez",
    nombres: "Ana María",
    tipoDocumento: TipoDocumento.LE,
    nroDocumento: "4567890",
  },
  {
    idHuesped: 5,
    apellido: "López",
    nombres: "José Luis",
    tipoDocumento: TipoDocumento.PASAPORTE,
    nroDocumento: "AB123456",
  },
  {
    idHuesped: 6,
    apellido: "Martínez",
    nombres: "Laura Beatriz",
    tipoDocumento: TipoDocumento.DNI,
    nroDocumento: "45678901",
    email: ["lmartinez@email.com"],
  },
  {
    idHuesped: 7,
    apellido: "Gómez",
    nombres: "Roberto Carlos",
    tipoDocumento: TipoDocumento.DNI,
    nroDocumento: "56789012",
  },
  {
    idHuesped: 8,
    apellido: "Pérez",
    nombres: "Silvia Beatriz",
    tipoDocumento: TipoDocumento.DNI,
    nroDocumento: "67890123",
    email: ["sperez@email.com"],
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

export async function POST(request: NextRequest) {
  try {
    const body = await request.json()
    const { apellido, nombres, tipoDocumento, nroDocumento } = body

    // Simulamos un delay del backend
    await new Promise((resolve) => setTimeout(resolve, 500))

    // Filtrar huéspedes según criterios
    let resultados = HUESPEDES_MOCK

    if (apellido) {
      resultados = resultados.filter((h) => h.apellido.toUpperCase().startsWith(apellido.toUpperCase()))
    }

    if (nombres) {
      resultados = resultados.filter((h) => h.nombres.toUpperCase().startsWith(nombres.toUpperCase()))
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
