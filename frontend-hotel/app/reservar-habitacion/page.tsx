"use client"

import Link from "next/link"
import { useRouter } from "next/navigation"
import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Hotel, Home, Calendar, UserCheck, CheckCircle, Loader2 } from "lucide-react"

interface HabitacionEstado {
  id: string
  numero: string
  tipo: string
  capacidad: number
  estadoHabitacion?: "HABILITADA" | "FUERA_DE_SERVICIO"
  estado: "DISPONIBLE" | "RESERVADA" | "OCUPADA"
  precioNoche: number
  estadosPorDia?: Record<string, "DISPONIBLE" | "RESERVADA" | "OCUPADA">
}

interface SeleccionHabitacion {
  habitacionId: string
  diaInicio: number
  diaFin: number
}

interface DatosHuesped {
  apellido: string
  nombres: string
  telefono: string
  nroDocumento: string
  tipoDocumento: string
}

const TIPOS_HABITACION_ORDEN = [
  "INDIVIDUAL_ESTANDAR",
  "DOBLE_ESTANDAR",
  "DOBLE_SUPERIOR",
  "SUPERIOR_FAMILY_PLAN",
  "SUITE_DOBLE",
]

const formatearTipo = (tipo: string): string => {
  return tipo.replace(/_/g, " ")
}

type Paso = "fechaDesde" | "fechaHasta" | "grilla" | "datosHuesped" | "confirmacion"

const createLocalDate = (dateString: string): Date => {
  const [year, month, day] = dateString.split("-").map(Number)
  return new Date(year, month - 1, day)
}

export default function ReservarHabitacion() {
  const BotonMenu = () => (
    <div className="absolute top-6 right-8 z-30">
      <Button asChild variant="outline" className="h-10 px-4 font-semibold shadow-md bg-white/80 dark:bg-slate-900/80">
        <Link href="/">
          <Home className="mr-2 h-5 w-5" />
          Menú principal
        </Link>
      </Button>
    </div>
  )

  const router = useRouter()
  const [paso, setPaso] = useState<Paso>("fechaDesde")
  const [fechaDesde, setFechaDesde] = useState("")
  const [fechaHasta, setFechaHasta] = useState("")
  const [errorFecha, setErrorFecha] = useState("")
  const [habitaciones, setHabitaciones] = useState<HabitacionEstado[]>([])
  const [loading, setLoading] = useState(false)
  const [errorCarga, setErrorCarga] = useState("")

  const [selecciones, setSelecciones] = useState<SeleccionHabitacion[]>([])
  const [seleccionActual, setSeleccionActual] = useState<{
    habitacionId: string
    diaInicio: number | null
  } | null>(null)

  const [datosHuesped, setDatosHuesped] = useState<DatosHuesped>({
    apellido: "",
    nombres: "",
    telefono: "",
    nroDocumento: "",
    tipoDocumento: "DNI",
  })
  const [erroresHuesped, setErroresHuesped] = useState<Partial<DatosHuesped>>({})

  const CONFIG_DOCUMENTOS: Record<string, { regex: RegExp; error: string; placeholder: string }> = {
    DNI: {
      regex: /^\d{7,8}$/,
      error: "El DNI debe tener 7 u 8 números",
      placeholder: "Ej: 12345678",
    },
    PASAPORTE: {
      regex: /^[A-Z0-9]{6,9}$/,
      error: "El pasaporte debe tener 6 a 9 caracteres alfanuméricos",
      placeholder: "Ej: A1234567",
    },
    LC: {
      regex: /^\d{6,8}$/,
      error: "La LC debe tener 6 a 8 números",
      placeholder: "Ej: 1234567",
    },
    LE: {
      regex: /^\d{6,8}$/,
      error: "La LE debe tener 6 a 8 números",
      placeholder: "Ej: 1234567",
    },
    OTRO: {
      regex: /^[a-zA-Z0-9]{5,20}$/,
      error: "Formato inválido (5-20 caracteres)",
      placeholder: "Nro. de Identificación",
    },
  }

  const validarCampoHuesped = (campo: keyof DatosHuesped, valor: string) => {
    let error = ""
    if (campo === "apellido" || campo === "nombres") {
      const regexNombre = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s-]+$/
      if (!valor.trim()) {
        error = campo === "apellido" ? "El apellido es obligatorio" : "El nombre es obligatorio"
      } else if (valor.length < 2 || valor.length > 50) {
        error =
          campo === "apellido"
            ? "El apellido debe tener entre 2 y 50 caracteres"
            : "El nombre debe tener entre 2 y 50 caracteres"
      } else if (!regexNombre.test(valor)) {
        error =
          campo === "apellido"
            ? "El apellido solo puede contener letras, espacios y guiones"
            : "El nombre solo puede contener letras, espacios y guiones"
      }
    }
    if (campo === "telefono") {
      const regexTelefono = /^\+?[0-9-]+$/
      let telefonoNormalizado = valor.trim()
      if (!telefonoNormalizado.startsWith("+")) {
        telefonoNormalizado = "+54" + telefonoNormalizado.replace(/[^0-9-]/g, "")
      }
      if (!valor.trim()) {
        error = "El teléfono es obligatorio"
      } else if (!regexTelefono.test(telefonoNormalizado)) {
        error = "El teléfono solo puede contener números, + y guion"
      } else if (telefonoNormalizado.replace(/[^0-9]/g, "").length < 8) {
        error = "El teléfono debe tener al menos 8 dígitos"
      } else if (!/^\+\d{7,15}$/.test(telefonoNormalizado.replace(/-/g, ""))) {
        error = "El teléfono debe tener formato internacional válido (ej: +541112345678)"
      }
    }
    if (campo === "nroDocumento") {
      const tipo = datosHuesped.tipoDocumento || "DNI"
      const config = CONFIG_DOCUMENTOS[tipo] || CONFIG_DOCUMENTOS["DNI"]
      if (!valor.trim()) {
        error = "El número de documento es obligatorio"
      } else if (!config.regex.test(valor.trim())) {
        error = config.error
      }
    }
    if (campo === "tipoDocumento") {
      if (!valor.trim()) {
        error = "El tipo de documento es obligatorio"
      }
    }
    setErroresHuesped((prev) => ({ ...prev, [campo]: error }))
  }

  const validarFechaDesde = (): boolean => {
    if (!fechaDesde) {
      setErrorFecha("Debe seleccionar una fecha")
      return false
    }
    const hoy = new Date()
    hoy.setHours(0, 0, 0, 0)
    const fechaSeleccionada = createLocalDate(fechaDesde)
    if (fechaSeleccionada < hoy) {
      setErrorFecha("La fecha de ingreso no puede ser anterior al día de hoy")
      return false
    }
    setErrorFecha("")
    return true
  }

  const validarFechaHasta = (): boolean => {
    if (!fechaHasta) {
      setErrorFecha("Debe seleccionar una fecha")
      return false
    }
    const desde = createLocalDate(fechaDesde)
    const hasta = createLocalDate(fechaHasta)
    if (desde >= hasta) {
      setErrorFecha("La fecha 'Hasta' debe ser posterior a la fecha 'Desde'")
      return false
    }
    const hoy = new Date()
    hoy.setHours(0, 0, 0, 0)
    if (hasta <= hoy) {
      setErrorFecha("La fecha de egreso debe ser futura")
      return false
    }
    setErrorFecha("")
    return true
  }

  const handleConfirmarDesde = () => {
    if (validarFechaDesde()) {
      setPaso("fechaHasta")
    }
  }

  const handleConfirmarHasta = async () => {
    if (validarFechaHasta()) {
      setPaso("grilla")
      await cargarHabitacionesConEstado()
    }
  }

  const cargarHabitacionesConEstado = async () => {
    if (!fechaDesde || !fechaHasta) return

    setLoading(true)
    setErrorCarga("")

    const [response] = await Promise.all([
      fetch(`http://localhost:8080/api/habitaciones/estados?fechaDesde=${fechaDesde}&fechaHasta=${fechaHasta}`),
      new Promise((resolve) => setTimeout(resolve, 300)),
    ])

    try {
      if (!response.ok) throw new Error("Error al cargar habitaciones")

      const data = await response.json()
      console.log("[v0] Habitaciones con estado para reserva:", data)

      const habitacionesMapeadas = data.map((h: any) => {
        const estadosPorDia = h.estadosPorDia || {}

        const todosLosEstados = Object.values(estadosPorDia)
        const esFueraDeServicio =
          todosLosEstados.length > 0 && todosLosEstados.every((estado: any) => estado === "MANTENIMIENTO")

        const primerDia = Object.keys(estadosPorDia)[0]
        let estado: "DISPONIBLE" | "RESERVADA" | "OCUPADA" = "DISPONIBLE"

        if (primerDia && estadosPorDia[primerDia]) {
          estado = estadosPorDia[primerDia] as "DISPONIBLE" | "RESERVADA" | "OCUPADA"
        }

        return {
          id: h.numero,
          numero: h.numero,
          tipo: h.tipoHabitacion,
          capacidad: h.capacidad,
          estadoHabitacion: esFueraDeServicio ? "FUERA_DE_SERVICIO" : "HABILITADA",
          estado: estado,
          precioNoche: h.costoPorNoche,
          estadosPorDia: estadosPorDia,
        }
      })

      console.log("[v0] Habitaciones mapeadas:", habitacionesMapeadas)
      setHabitaciones(habitacionesMapeadas)
    } catch (error) {
      console.error("[v0] Error al cargar habitaciones:", error)
      setErrorCarga("Error al cargar habitaciones")
    } finally {
      setLoading(false)
    }
  }

  const generarDias = (desde?: string, hasta?: string): Date[] => {
    const fechaDesdeUse = desde || fechaDesde
    const fechaHastaUse = hasta || fechaHasta
    if (!fechaDesdeUse || !fechaHastaUse) return []
    const desdeDate = createLocalDate(fechaDesdeUse)
    const hastaDate = createLocalDate(fechaHastaUse)
    const dias: Date[] = []
    const actual = new Date(desdeDate)
    while (actual < hastaDate) {
      dias.push(new Date(actual))
      actual.setDate(actual.getDate() + 1)
    }
    return dias
  }

  const diasRango = generarDias(fechaDesde, fechaHasta)

  const esCeldaDisponible = (habitacionId: string, diaIdx: number): boolean => {
    const habitacion = habitaciones.find((h: HabitacionEstado) => h.id === habitacionId)
    if (!habitacion) return false

    if (habitacion.estadoHabitacion === "FUERA_DE_SERVICIO") return false

    if (habitacion.estadosPorDia && diasRango[diaIdx]) {
      const fechaDia = diasRango[diaIdx].toISOString().split("T")[0]
      const estadoDia = habitacion.estadosPorDia[fechaDia]
      if (estadoDia && estadoDia !== "DISPONIBLE") return false
    } else {
      if (habitacion.estado !== "DISPONIBLE") return false
    }

    return !selecciones.some(
      (sel) => sel.habitacionId === habitacionId && diaIdx >= sel.diaInicio && diaIdx <= sel.diaFin,
    )
  }

  const obtenerEstadoCelda = (
    habitacionId: string,
    diaIdx: number,
  ): "DISPONIBLE" | "RESERVADA" | "OCUPADA" | "FUERA_DE_SERVICIO" => {
    const habitacion = habitaciones.find((h: HabitacionEstado) => h.id === habitacionId)
    if (!habitacion) return "OCUPADA"

    if (habitacion.estadoHabitacion === "FUERA_DE_SERVICIO") return "FUERA_DE_SERVICIO"

    if (habitacion.estadosPorDia && diasRango[diaIdx]) {
      const fechaDia = diasRango[diaIdx].toISOString().split("T")[0]
      const estadoDia = habitacion.estadosPorDia[fechaDia]
      return (estadoDia as "DISPONIBLE" | "RESERVADA" | "OCUPADA") || "DISPONIBLE"
    }

    return habitacion.estado
  }

  const handleClickCelda = (habitacionId: string, diaIdx: number) => {
    if (!esCeldaDisponible(habitacionId, diaIdx)) return

    if (!seleccionActual || seleccionActual.habitacionId !== habitacionId) {
      setSeleccionActual({ habitacionId, diaInicio: diaIdx })
    } else {
      const diaInicio = seleccionActual.diaInicio!
      const diaFin = diaIdx

      const rangoValido = Array.from(
        { length: Math.abs(diaFin - diaInicio) + 1 },
        (_, i) => Math.min(diaInicio, diaFin) + i,
      ).every((dia) => esCeldaDisponible(habitacionId, dia))

      if (rangoValido) {
        setSelecciones((prev) => [
          ...prev,
          {
            habitacionId,
            diaInicio: Math.min(diaInicio, diaFin),
            diaFin: Math.max(diaInicio, diaFin),
          },
        ])
        setSeleccionActual(null)
      } else {
        alert("No se puede seleccionar ese rango. Hay días con reservas, ocupadas o en mantenimiento.")
        setSeleccionActual(null)
      }
    }
  }

  const esCeldaSeleccionada = (habitacionId: string, diaIdx: number): boolean => {
    return selecciones.some(
      (sel) => sel.habitacionId === habitacionId && diaIdx >= sel.diaInicio && diaIdx <= sel.diaFin,
    )
  }

  const esInicioSeleccionActual = (habitacionId: string, diaIdx: number): boolean => {
    return seleccionActual?.habitacionId === habitacionId && seleccionActual.diaInicio === diaIdx
  }

  const handleRemoverSeleccion = (index: number) => {
    setSelecciones((prev) => prev.filter((_, i) => i !== index))
  }

  const handleContinuarHuesped = () => {
    if (selecciones.length === 0) {
      alert("Debe seleccionar al menos una habitación con un rango de fechas")
      return
    }
    setPaso("datosHuesped")
  }

  const validarDatosHuesped = (): boolean => {
    const errores: Partial<DatosHuesped> = {}

    const regexNombre = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s-]+$/
    const regexTelefono = /^\+?[0-9-]+$/

    if (!datosHuesped.apellido.trim()) {
      errores.apellido = "El apellido es obligatorio"
    } else if (datosHuesped.apellido.length < 2 || datosHuesped.apellido.length > 50) {
      errores.apellido = "El apellido debe tener entre 2 y 50 caracteres"
    } else if (!regexNombre.test(datosHuesped.apellido)) {
      errores.apellido = "El apellido solo puede contener letras, espacios y guiones"
    }

    if (!datosHuesped.nombres.trim()) {
      errores.nombres = "El nombre es obligatorio"
    } else if (datosHuesped.nombres.length < 2 || datosHuesped.nombres.length > 50) {
      errores.nombres = "El nombre debe tener entre 2 y 50 caracteres"
    } else if (!regexNombre.test(datosHuesped.nombres)) {
      errores.nombres = "El nombre solo puede contener letras, espacios y guiones"
    }

    if (!datosHuesped.telefono.trim()) {
      errores.telefono = "El teléfono es obligatorio"
    } else {
      let telefonoNormalizado = datosHuesped.telefono.trim()
      if (!telefonoNormalizado.startsWith("+")) {
        telefonoNormalizado = "+54" + telefonoNormalizado.replace(/[^0-9-]/g, "")
        setDatosHuesped((prev) => ({ ...prev, telefono: telefonoNormalizado }))
      }
      if (!regexTelefono.test(telefonoNormalizado)) {
        errores.telefono = "El teléfono solo puede contener números, + y guion"
      } else if (telefonoNormalizado.replace(/[^0-9]/g, "").length < 8) {
        errores.telefono = "El teléfono debe tener al menos 8 dígitos"
      } else if (!/^\+\d{7,15}$/.test(telefonoNormalizado.replace(/-/g, ""))) {
        errores.telefono = "El teléfono debe tener formato internacional válido (ej: +541112345678)"
      }
    }

    const tipo = datosHuesped.tipoDocumento || "DNI"
    const config = CONFIG_DOCUMENTOS[tipo] || CONFIG_DOCUMENTOS["DNI"]
    if (!datosHuesped.nroDocumento.trim()) {
      errores.nroDocumento = "El número de documento es obligatorio"
    } else if (!config.regex.test(datosHuesped.nroDocumento.trim())) {
      errores.nroDocumento = config.error
    }

    if (!datosHuesped.tipoDocumento.trim()) {
      errores.tipoDocumento = "El tipo de documento es obligatorio"
    }

    setErroresHuesped(errores)
    return Object.keys(errores).length === 0
  }

  const handleConfirmarHuesped = () => {
    if (validarDatosHuesped()) {
      setPaso("confirmacion")
    }
  }

  const handleVolverPaso = () => {
    if (paso === "fechaHasta") {
      setErrorFecha("")
      setPaso("fechaDesde")
    } else if (paso === "grilla") {
      setErrorFecha("")
      setPaso("fechaHasta")
    } else if (paso === "datosHuesped") {
      setPaso("grilla")
    } else if (paso === "confirmacion") {
      setPaso("datosHuesped")
    }
  }

  const handleVolver = handleVolverPaso

  const handleFinalizarReserva = async () => {
    setLoading(true)
    try {
      const reservas = selecciones.map((sel) => {
        const habitacion = habitaciones.find((h) => h.id === sel.habitacionId)
        const diaInicioDate = diasRango[sel.diaInicio]
        const diaFinDate = diasRango[sel.diaFin]
        return {
          idReserva: 0,
          estadoReserva: "ACTIVA" as const,
          fechaDesde: diaInicioDate.toISOString().split("T")[0],
          fechaHasta: diaFinDate.toISOString().split("T")[0],
          nombreHuespedResponsable: datosHuesped.nombres,
          apellidoHuespedResponsable: datosHuesped.apellido,
          telefonoHuespedResponsable: datosHuesped.telefono,
          nroDocumentoResponsable: datosHuesped.nroDocumento,
          tipoDocumentoResponsable: datosHuesped.tipoDocumento,
          idHabitacion: habitacion?.numero || sel.habitacionId,
        }
      })
      const response = await fetch("http://localhost:8080/api/reservas/crear", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(reservas),
      })
      if (!response.ok) {
        throw new Error("Error al crear las reservas")
      }
      alert("✅ Reservas creadas con éxito")
      router.push("/")
    } catch (error) {
      console.error("Error:", error)
      alert("Error al crear las reservas. Por favor intente nuevamente.")
    } finally {
      setLoading(false)
    }
  }

  const calcularTotal = (): number => {
    return selecciones.reduce((total, sel) => {
      const habitacion = habitaciones.find((h: HabitacionEstado) => h.id === sel.habitacionId)
      if (!habitacion) return total
      const noches = sel.diaFin - sel.diaInicio + 1
      return total + habitacion.precioNoche * noches
    }, 0)
  }

  const getEstadoColor = (estado: string) => {
    switch (estado) {
      case "DISPONIBLE":
        return "bg-green-600 dark:bg-green-700"
      case "RESERVADA":
        return "bg-orange-600 dark:bg-orange-700"
      case "OCUPADA":
        return "bg-red-600 dark:bg-red-700"
      case "FUERA_DE_SERVICIO":
        return "bg-slate-500 dark:bg-slate-600"
      default:
        return "bg-slate-600 dark:bg-slate-700"
    }
  }

  const conteo = {
    disponibles: habitaciones.filter((h: HabitacionEstado) => h.estado === "DISPONIBLE").length,
    reservadas: habitaciones.filter((h: HabitacionEstado) => h.estado === "RESERVADA").length,
    ocupadas: habitaciones.filter((h: HabitacionEstado) => h.estado === "OCUPADA").length,
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50 dark:from-slate-950 dark:via-slate-900 dark:to-slate-950 relative">
      <BotonMenu />
      <main className="mx-auto max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
        <div className="mb-8">
          <div className="mb-6 flex items-center gap-4">
            <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-gradient-to-br from-blue-600 to-indigo-600 text-white shadow-lg">
              <Hotel className="h-6 w-6" />
            </div>
            <div>
              <p className="text-xs font-semibold uppercase tracking-wider text-blue-600 dark:text-blue-400">
                Caso de Uso 4
              </p>
              <h1 className="text-3xl font-bold text-slate-900 dark:text-slate-50">Reservar Habitación</h1>
            </div>
          </div>
          <p className="text-slate-600 dark:text-slate-400">Reservar una habitación a nombre de un futuro Huésped</p>
          <div className="flex items-center gap-2">
            <div
              className="flex-1 h-2 bg-blue-600 rounded-full"
              style={{
                width: `${(paso === "fechaDesde" ? 1 : paso === "fechaHasta" ? 2 : paso === "grilla" ? 3 : paso === "datosHuesped" ? 4 : 5) * 20}%`,
              }}
            />
            <span className="text-sm font-medium text-slate-600 dark:text-slate-400">
              Paso{" "}
              {paso === "fechaDesde"
                ? 1
                : paso === "fechaHasta"
                  ? 2
                  : paso === "grilla"
                    ? 3
                    : paso === "datosHuesped"
                      ? 4
                      : 5}{" "}
              de 5
            </span>
          </div>
        </div>

        {/* PASO 1: Seleccionar Fecha Desde */}
        {paso === "fechaDesde" && (
          <div className="flex justify-center items-center min-h-[60vh]">
            <Card className="border-0 shadow-xl bg-white/80 dark:bg-slate-900/80 backdrop-blur-sm p-8 max-w-md w-full flex flex-col items-center">
              <h2 className="text-2xl font-bold mb-6 text-slate-900 dark:text-slate-50 text-center">
                Seleccione fecha de inicio
              </h2>
              <div className="space-y-6 w-full">
                <div>
                  <Label htmlFor="fechaDesde" className="text-base font-semibold mb-2 block">
                    Fecha Desde
                  </Label>
                  <Input
                    id="fechaDesde"
                    type="date"
                    value={fechaDesde}
                    onChange={(e) => {
                      setFechaDesde(e.target.value)
                      setErrorFecha("")
                    }}
                    className="h-12 text-base"
                  />
                </div>
                {errorFecha && (
                  <Card className="border-red-200 bg-red-50 p-4 dark:border-red-900 dark:bg-red-950/20">
                    <p className="text-sm text-red-600 dark:text-red-400 font-medium">{errorFecha}</p>
                  </Card>
                )}
                <div className="flex gap-3 pt-4">
                  <Button
                    onClick={handleConfirmarDesde}
                    className="flex-1 h-12 gap-2 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700"
                  >
                    Continuar
                    <Calendar className="h-4 w-4" />
                  </Button>
                  <Button variant="outline" asChild className="h-12 bg-transparent">
                    <Link href="/">
                      <Home className="mr-2 h-4 w-4" />
                      Inicio
                    </Link>
                  </Button>
                </div>
              </div>
            </Card>
          </div>
        )}

        {/* PASO 2: Seleccionar Fecha Hasta */}
        {paso === "fechaHasta" && (
          <div className="flex justify-center items-center min-h-[60vh]">
            <Card className="border-0 shadow-xl bg-white/80 dark:bg-slate-900/80 backdrop-blur-sm p-8 max-w-md w-full flex flex-col items-center">
              <h2 className="text-2xl font-bold mb-6 text-slate-900 dark:text-slate-50 text-center">
                Seleccione fecha de fin
              </h2>
              <div className="space-y-6 w-full">
                <Card className="border-blue-200 bg-gradient-to-br from-blue-50 to-indigo-50 p-4 dark:border-blue-900 dark:from-blue-950/30 dark:to-indigo-950/30">
                  <p className="text-sm text-slate-600 dark:text-slate-400 mb-1">Fecha desde:</p>
                  <p className="text-lg font-bold text-slate-900 dark:text-slate-50">
                    {createLocalDate(fechaDesde).toLocaleDateString()}
                  </p>
                </Card>
                <div>
                  <Label htmlFor="fechaHasta" className="text-base font-semibold mb-2 block">
                    Fecha Hasta
                  </Label>
                  <Input
                    id="fechaHasta"
                    type="date"
                    value={fechaHasta}
                    onChange={(e) => {
                      setFechaHasta(e.target.value)
                      setErrorFecha("")
                    }}
                    className="h-12 text-base"
                  />
                </div>
                {errorFecha && (
                  <Card className="border-red-200 bg-red-50 p-4 dark:border-red-900 dark:bg-red-950/20">
                    <p className="text-sm text-red-600 dark:text-red-400 font-medium">{errorFecha}</p>
                  </Card>
                )}
                <div className="flex gap-3 pt-4">
                  <Button onClick={handleVolverPaso} variant="outline" className="flex-1 h-12 bg-transparent">
                    ← Atrás
                  </Button>
                  <Button
                    onClick={handleConfirmarHasta}
                    className="flex-1 h-12 gap-2 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700"
                  >
                    Continuar
                    <Calendar className="h-4 w-4" />
                  </Button>
                </div>
              </div>
            </Card>
          </div>
        )}

        {/* PASO 3: Grilla interactiva */}
        {paso === "grilla" && (
          <div className="space-y-6">
            {loading ? (
              <Card className="border-0 shadow-xl bg-white/80 dark:bg-slate-900/80 backdrop-blur-sm p-16 flex flex-col items-center justify-center gap-6">
                <Loader2 className="w-16 h-16 animate-spin text-blue-600" />
                <p className="text-xl font-bold text-slate-900 dark:text-slate-300">Procesando datos...</p>
              </Card>
            ) : errorCarga ? (
              <Card className="border-0 shadow-xl bg-white/80 dark:bg-slate-900/80 backdrop-blur-sm p-8 text-center">
                <p className="text-red-600 dark:text-red-400 mb-6 font-semibold text-lg">{errorCarga}</p>
                <Button onClick={handleVolver} variant="outline" className="h-12 bg-transparent">
                  ← Volver a fechas
                </Button>
              </Card>
            ) : (
              <>
                <Card className="border-blue-200 bg-gradient-to-br from-blue-50 to-indigo-50 p-5 dark:border-blue-900 dark:from-blue-950/30 dark:to-indigo-950/30">
                  <p className="text-sm text-slate-700 dark:text-slate-300 leading-relaxed">
                    <strong className="text-blue-700 dark:text-blue-400">Instrucciones:</strong> Click en una celda para
                    iniciar selección, luego click en otra celda de la misma habitación para finalizar el rango. Puede
                    seleccionar múltiples habitaciones con diferentes rangos.
                  </p>
                </Card>

                <Card className="border-0 shadow-xl bg-white/80 dark:bg-slate-900/80 backdrop-blur-sm p-8">
                  <h2 className="text-2xl font-bold mb-4 text-slate-900 dark:text-slate-50">
                    Grilla de disponibilidad
                  </h2>
                  <p className="text-slate-600 dark:text-slate-400 mb-2 font-medium">
                    {createLocalDate(fechaDesde).toLocaleDateString()} al{" "}
                    {createLocalDate(fechaHasta).toLocaleDateString()}
                  </p>
                  <p className="text-slate-600 text-sm mb-6 dark:text-slate-400">
                    Haga click en una celda para iniciar la selección, luego haga click en otra celda de la misma
                    habitación para completar el rango.
                  </p>

                  <div className="overflow-x-auto rounded-lg border border-slate-200 dark:border-slate-700">
                    <table className="w-full border-collapse text-sm">
                      <thead>
                        <tr className="bg-gradient-to-r from-slate-100 to-slate-50 dark:from-slate-800 dark:to-slate-800/50">
                          <th className="border border-slate-300 dark:border-slate-700 px-4 py-3 text-slate-900 dark:text-slate-50 font-bold">
                            Fecha
                          </th>
                          {TIPOS_HABITACION_ORDEN.map((tipo) => {
                            const habsTipo = habitaciones.filter((h: HabitacionEstado) => h.tipo === tipo)
                            if (habsTipo.length === 0) return null
                            return (
                              <th
                                key={tipo}
                                colSpan={habsTipo.length}
                                className="border border-slate-300 dark:border-slate-700 px-2 py-3 text-center text-slate-900 dark:text-slate-50 font-bold bg-gradient-to-r from-blue-100 to-indigo-100 dark:from-blue-900/40 dark:to-indigo-900/40"
                              >
                                {formatearTipo(tipo)}
                              </th>
                            )
                          })}
                        </tr>
                        <tr className="bg-slate-50 dark:bg-slate-800/50">
                          <th className="border border-slate-300 dark:border-slate-700 px-4 py-3"></th>
                          {TIPOS_HABITACION_ORDEN.map((tipo) => {
                            const habsTipo = habitaciones
                              .filter((h: HabitacionEstado) => h.tipo === tipo)
                              .sort((a, b) => Number.parseInt(a.numero) - Number.parseInt(b.numero))
                            return habsTipo.map((hab) => (
                              <th
                                key={hab.id}
                                className="border border-slate-300 dark:border-slate-700 px-2 py-3 text-center text-slate-900 dark:text-slate-50 font-bold min-w-[80px] text-xs"
                              >
                                Hab. {hab.numero}
                              </th>
                            ))
                          })}
                        </tr>
                      </thead>
                      <tbody>
                        {diasRango.map((dia, diaIdx) => (
                          <tr
                            key={diaIdx}
                            className={
                              diaIdx % 2 === 0 ? "bg-slate-50 dark:bg-slate-800/50" : "bg-white dark:bg-transparent"
                            }
                          >
                            <td className="border border-slate-300 dark:border-slate-700 px-4 py-3 font-bold text-slate-900 dark:text-slate-200">
                              <div className="text-sm">
                                {dia.toLocaleDateString("es-AR", {
                                  weekday: "short",
                                  day: "2-digit",
                                  month: "2-digit",
                                })}
                              </div>
                            </td>
                            {TIPOS_HABITACION_ORDEN.map((tipo) => {
                              const habsTipo = habitaciones
                                .filter((h: HabitacionEstado) => h.tipo === tipo)
                                .sort((a, b) => Number.parseInt(a.numero) - Number.parseInt(b.numero))
                              return habsTipo.map((hab) => {
                                const disponible = esCeldaDisponible(hab.id, diaIdx)
                                const seleccionada = esCeldaSeleccionada(hab.id, diaIdx)
                                const inicioActual = esInicioSeleccionActual(hab.id, diaIdx)
                                const estadoDia = obtenerEstadoCelda(hab.id, diaIdx)
                                const baseColor = getEstadoColor(estadoDia)

                                return (
                                  <td
                                    key={`${hab.id}-${diaIdx}`}
                                    className="border border-slate-300 dark:border-slate-700 px-2 py-3 text-center"
                                  >
                                    <div
                                      onClick={() => handleClickCelda(hab.id, diaIdx)}
                                      className={`rounded-lg px-3 py-2 text-xs font-bold text-white transition-all ${
                                        seleccionada
                                          ? "bg-gradient-to-br from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 cursor-pointer shadow-md"
                                          : inicioActual
                                            ? "bg-gradient-to-br from-blue-400 to-indigo-400 hover:from-blue-500 hover:to-indigo-500 cursor-pointer animate-pulse shadow-md"
                                            : estadoDia === "RESERVADA" ||
                                                estadoDia === "OCUPADA" ||
                                                estadoDia === "FUERA_DE_SERVICIO"
                                              ? `${baseColor} cursor-not-allowed opacity-75`
                                              : `${baseColor} hover:brightness-110 cursor-pointer shadow-sm`
                                      }`}
                                      title={
                                        seleccionada
                                          ? "Seleccionada"
                                          : inicioActual
                                            ? "Inicio de selección - Click otro día para completar"
                                            : disponible
                                              ? "Click para seleccionar inicio"
                                              : estadoDia === "RESERVADA"
                                                ? "Reservada - No se puede pisar"
                                                : estadoDia === "OCUPADA"
                                                  ? "Ocupada"
                                                  : estadoDia === "FUERA_DE_SERVICIO"
                                                    ? "Fuera de servicio"
                                                    : "No disponible"
                                      }
                                    >
                                      {seleccionada
                                        ? "✓"
                                        : inicioActual
                                          ? "●"
                                          : estadoDia === "RESERVADA"
                                            ? "R"
                                            : estadoDia === "OCUPADA"
                                              ? "X"
                                              : estadoDia === "FUERA_DE_SERVICIO"
                                                ? "FS"
                                                : "○"}
                                    </div>
                                  </td>
                                )
                              })
                            })}
                          </tr>
                        ))}
                      </tbody>
                    </table>
                    <div className="mt-4 p-4 text-xs text-slate-600 dark:text-slate-400 flex gap-6 flex-wrap border-t border-slate-200 dark:border-slate-700">
                      <span className="font-semibold">✓ = Seleccionada</span>
                      <span className="font-semibold">● = Inicio de selección</span>
                      <span className="font-semibold">○ = Disponible</span>
                      <span className="font-semibold">R = Reservada</span>
                      <span className="font-semibold">X = Ocupada</span>
                      <span className="font-semibold">FS = Fuera de servicio</span>
                    </div>
                  </div>
                </Card>

                {selecciones.length > 0 && (
                  <Card className="border-0 shadow-xl bg-white/80 dark:bg-slate-900/80 backdrop-blur-sm p-8">
                    <h3 className="text-xl font-bold mb-6 text-slate-900 dark:text-slate-50">
                      Habitaciones seleccionadas
                    </h3>
                    <div className="space-y-4">
                      {selecciones.map((sel, idx) => {
                        const hab = habitaciones.find((h: HabitacionEstado) => h.id === sel.habitacionId)
                        if (!hab) return null
                        const noches = sel.diaFin - sel.diaInicio + 1
                        const subtotal = hab.precioNoche * noches
                        return (
                          <div
                            key={idx}
                            className="flex justify-between items-center bg-gradient-to-br from-slate-50 to-blue-50 dark:from-slate-800 dark:to-blue-900/20 p-5 rounded-xl border border-slate-200 dark:border-slate-700 shadow-sm hover:shadow-md transition-shadow"
                          >
                            <div>
                              <p className="font-bold text-slate-900 dark:text-white text-lg">
                                Habitación {hab.numero}
                              </p>
                              <p className="text-sm text-slate-600 dark:text-slate-400 font-medium">
                                {formatearTipo(hab.tipo)}
                              </p>
                              <p className="text-sm text-slate-500 dark:text-slate-500 mt-1">
                                {diasRango[sel.diaInicio]?.toLocaleDateString()} -{" "}
                                {diasRango[sel.diaFin]?.toLocaleDateString()}
                                <span className="font-semibold">
                                  {" "}
                                  ({noches} noche{noches > 1 ? "s" : ""})
                                </span>
                              </p>
                            </div>
                            <div className="flex items-center gap-6">
                              <p className="text-xl font-bold text-blue-600 dark:text-blue-400">${subtotal}</p>
                              <Button
                                onClick={() => handleRemoverSeleccion(idx)}
                                variant="ghost"
                                size="sm"
                                className="text-red-600 hover:text-red-700 hover:bg-red-50 dark:text-red-400 dark:hover:text-red-300 dark:hover:bg-red-950/20 font-bold"
                              >
                                ✕ Quitar
                              </Button>
                            </div>
                          </div>
                        )
                      })}
                    </div>
                    <div className="mt-6 pt-6 border-t-2 border-slate-200 dark:border-slate-700 flex justify-between items-center">
                      <p className="text-2xl font-bold text-slate-900 dark:text-slate-50">Total:</p>
                      <p className="text-3xl font-bold bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent">
                        ${calcularTotal()}
                      </p>
                    </div>
                  </Card>
                )}

                <div className="flex gap-4">
                  <Button onClick={handleVolver} variant="outline" className="flex-1 h-12 bg-transparent">
                    ← Atrás
                  </Button>
                  <Button
                    onClick={handleContinuarHuesped}
                    className="flex-1 h-12 gap-2 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700"
                  >
                    Continuar
                    <UserCheck className="h-4 w-4" />
                  </Button>
                </div>
              </>
            )}
          </div>
        )}

        {/* PASO 4: Datos del huésped */}
        {paso === "datosHuesped" && (
          <div className="flex justify-center items-center min-h-[60vh]">
            <Card className="border-0 shadow-xl bg-white/80 dark:bg-slate-900/80 backdrop-blur-sm p-8 max-w-md w-full flex flex-col items-center">
              <h2 className="text-2xl font-bold mb-6 text-slate-900 dark:text-slate-50">
                Datos del huésped responsable
              </h2>
              <div className="mb-4 flex items-center gap-2 w-full">
                <span className="text-red-600 dark:text-red-400 text-lg">*</span>
                <span className="text-sm text-slate-600 dark:text-slate-400">Campos obligatorios</span>
              </div>
              <div className="space-y-6 w-full">
                <div>
                  <Label htmlFor="apellido" className="text-base font-semibold mb-2 block">
                    Apellido <span className="text-red-600">*</span>
                  </Label>
                  <Input
                    id="apellido"
                    value={datosHuesped.apellido}
                    placeholder="Ej: Pérez"
                    onChange={(e) => {
                      setDatosHuesped((prev) => ({ ...prev, apellido: e.target.value }))
                    }}
                    onBlur={(e) => validarCampoHuesped("apellido", e.target.value)}
                    className="h-12 text-base"
                  />
                  {erroresHuesped.apellido && (
                    <p className="text-sm text-red-600 dark:text-red-400 mt-2 font-medium">{erroresHuesped.apellido}</p>
                  )}
                </div>
                <div>
                  <Label htmlFor="nombres" className="text-base font-semibold mb-2 block">
                    Nombres <span className="text-red-600">*</span>
                  </Label>
                  <Input
                    id="nombres"
                    value={datosHuesped.nombres}
                    placeholder="Ej: Juan Carlos"
                    onChange={(e) => {
                      setDatosHuesped((prev) => ({ ...prev, nombres: e.target.value }))
                    }}
                    onBlur={(e) => validarCampoHuesped("nombres", e.target.value)}
                    className="h-12 text-base"
                  />
                  {erroresHuesped.nombres && (
                    <p className="text-sm text-red-600 dark:text-red-400 mt-2 font-medium">{erroresHuesped.nombres}</p>
                  )}
                </div>
                <div>
                  <Label htmlFor="telefono" className="text-base font-semibold mb-2 block">
                    Teléfono <span className="text-red-600">*</span>
                  </Label>
                  <Input
                    id="telefono"
                    value={datosHuesped.telefono}
                    placeholder="Ej: +541112345678"
                    onChange={(e) => {
                      setDatosHuesped((prev) => ({ ...prev, telefono: e.target.value }))
                    }}
                    onBlur={(e) => validarCampoHuesped("telefono", e.target.value)}
                    className="h-12 text-base"
                  />
                  {erroresHuesped.telefono && (
                    <p className="text-sm text-red-600 dark:text-red-400 mt-2 font-medium">{erroresHuesped.telefono}</p>
                  )}
                </div>
                <div>
                  <Label htmlFor="tipoDocumento" className="text-base font-semibold mb-2 block">
                    Tipo de Documento <span className="text-red-600">*</span>
                  </Label>
                  <select
                    id="tipoDocumento"
                    className="w-full h-12 border rounded-lg px-4 text-base bg-white dark:bg-slate-800 border-slate-300 dark:border-slate-700"
                    value={datosHuesped.tipoDocumento}
                    onChange={(e) => {
                      setDatosHuesped((prev) => ({ ...prev, tipoDocumento: e.target.value }))
                    }}
                    onBlur={(e) => validarCampoHuesped("tipoDocumento", e.target.value)}
                  >
                    <option value="DNI">DNI</option>
                    <option value="LE">LE</option>
                    <option value="LC">LC</option>
                    <option value="PASAPORTE">Pasaporte</option>
                    <option value="OTRO">Otro</option>
                  </select>
                  {erroresHuesped.tipoDocumento && (
                    <p className="text-sm text-red-600 dark:text-red-400 mt-2 font-medium">
                      {erroresHuesped.tipoDocumento}
                    </p>
                  )}
                </div>
                <div>
                  <Label htmlFor="nroDocumento" className="text-base font-semibold mb-2 block">
                    Número de Documento <span className="text-red-600">*</span>
                  </Label>
                  <Input
                    id="nroDocumento"
                    value={datosHuesped.nroDocumento}
                    placeholder={CONFIG_DOCUMENTOS[datosHuesped.tipoDocumento]?.placeholder || "Nro. de Identificación"}
                    onChange={(e) => {
                      setDatosHuesped((prev) => ({ ...prev, nroDocumento: e.target.value }))
                    }}
                    onBlur={(e) => validarCampoHuesped("nroDocumento", e.target.value)}
                    className="h-12 text-base"
                  />
                  {erroresHuesped.nroDocumento && (
                    <p className="text-sm text-red-600 dark:text-red-400 mt-2 font-medium">
                      {erroresHuesped.nroDocumento}
                    </p>
                  )}
                </div>
                <div className="flex gap-4 pt-4">
                  <Button onClick={handleVolverPaso} variant="outline" className="flex-1 h-12 bg-transparent">
                    ← Atrás
                  </Button>
                  <Button
                    onClick={handleConfirmarHuesped}
                    className="flex-1 h-12 gap-2 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700"
                  >
                    Continuar
                    <UserCheck className="h-4 w-4" />
                  </Button>
                </div>
              </div>
            </Card>
          </div>
        )}

        {/* PASO 5: Confirmación */}
        {paso === "confirmacion" && (
          <Card className="border-0 shadow-xl bg-white/80 dark:bg-slate-900/80 backdrop-blur-sm p-8 max-w-3xl mx-auto">
            <h2 className="text-2xl font-bold mb-8 text-slate-900 dark:text-slate-50">Confirmación de reserva</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
              <div>
                <div className="text-sm font-semibold text-slate-500 dark:text-slate-400 mb-2">Fecha desde:</div>
                <div className="rounded-xl bg-gradient-to-br from-blue-50 to-indigo-50 border border-blue-100 dark:from-blue-950/30 dark:to-indigo-950/30 dark:border-blue-900 px-5 py-4 text-lg font-bold text-slate-900 dark:text-slate-50">
                  {fechaDesde && createLocalDate(fechaDesde).toLocaleDateString()}
                </div>
              </div>
              <div>
                <div className="text-sm font-semibold text-slate-500 dark:text-slate-400 mb-2">Fecha hasta:</div>
                <div className="rounded-xl bg-gradient-to-br from-blue-50 to-indigo-50 border border-blue-100 dark:from-blue-950/30 dark:to-indigo-950/30 dark:border-blue-900 px-5 py-4 text-lg font-bold text-slate-900 dark:text-slate-50">
                  {fechaHasta && createLocalDate(fechaHasta).toLocaleDateString()}
                </div>
              </div>
              <div>
                <div className="text-sm font-semibold text-slate-500 dark:text-slate-400 mb-2">
                  Huésped responsable:
                </div>
                <div className="rounded-xl bg-gradient-to-br from-blue-50 to-indigo-50 border border-blue-100 dark:from-blue-950/30 dark:to-indigo-950/30 dark:border-blue-900 px-5 py-4 text-lg font-bold text-slate-900 dark:text-slate-50">
                  {datosHuesped.nombres} {datosHuesped.apellido}
                </div>
              </div>
              <div>
                <div className="text-sm font-semibold text-slate-500 dark:text-slate-400 mb-2">Teléfono:</div>
                <div className="rounded-xl bg-gradient-to-br from-blue-50 to-indigo-50 border border-blue-100 dark:from-blue-950/30 dark:to-indigo-950/30 dark:border-blue-900 px-5 py-4 text-lg font-bold text-slate-900 dark:text-slate-50">
                  {datosHuesped.telefono}
                </div>
              </div>
            </div>

            <div className="mb-8">
              <div className="text-sm font-semibold text-slate-500 dark:text-slate-400 mb-3">
                Habitaciones reservadas:
              </div>
              <div className="space-y-3">
                {selecciones.map((sel, idx) => {
                  const habitacion = habitaciones.find((h) => h.id === sel.habitacionId)
                  const diaInicioDate = diasRango[sel.diaInicio]
                  const diaFinDate = diasRango[sel.diaFin]
                  const opcionesFecha = { weekday: "short", day: "2-digit", month: "2-digit", year: "numeric" } as const
                  return (
                    <div
                      key={idx}
                      className="rounded-xl border border-slate-200 dark:border-slate-700 bg-gradient-to-br from-slate-50 to-blue-50 dark:from-slate-800 dark:to-blue-900/20 px-6 py-4 flex flex-col md:flex-row md:items-center md:gap-6 shadow-sm"
                    >
                      <span className="font-bold text-lg text-blue-700 dark:text-blue-300">
                        Habitación {habitacion?.numero}
                      </span>
                      <span className="text-slate-700 dark:text-slate-300 font-semibold">
                        {formatearTipo(habitacion?.tipo || "")}
                      </span>
                      <span className="text-slate-600 dark:text-slate-400">{habitacion?.capacidad} personas</span>
                      <span className="text-slate-600 dark:text-slate-400 ml-auto">
                        <div className="font-semibold">
                          Ingreso: {diaInicioDate.toLocaleDateString("es-AR", opcionesFecha)}, 12:00hs.
                        </div>
                        <div className="font-semibold">
                          Egreso: {diaFinDate.toLocaleDateString("es-AR", opcionesFecha)}, 10:00hs
                        </div>
                      </span>
                      <span className="text-slate-900 dark:text-slate-50 font-bold text-xl ml-6">
                        ${habitacion ? habitacion.precioNoche * (sel.diaFin - sel.diaInicio + 1) : 0}
                      </span>
                    </div>
                  )
                })}
              </div>
            </div>

            <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-6 border-t-2 border-slate-200 dark:border-slate-700 pt-6">
              <div className="text-2xl font-bold text-slate-900 dark:text-slate-50">
                Total:{" "}
                <span className="bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent">
                  ${calcularTotal()}
                </span>
              </div>
              <div className="flex gap-4">
                <Button onClick={handleVolverPaso} variant="outline" disabled={loading} className="h-12 bg-transparent">
                  ← Atrás
                </Button>
                <Button
                  onClick={handleFinalizarReserva}
                  disabled={loading}
                  className="h-12 gap-2 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700"
                >
                  {loading ? (
                    <>
                      <Loader2 className="w-5 h-5 animate-spin" />
                      Procesando...
                    </>
                  ) : (
                    <>
                      <CheckCircle className="w-5 h-5" />
                      Finalizar reserva
                    </>
                  )}
                </Button>
              </div>
            </div>
          </Card>
        )}
      </main>
    </div>
  )
}
