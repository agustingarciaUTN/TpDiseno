"use client"

import Link from "next/link"
import { useRouter } from "next/navigation"
import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Hotel, Home, Calendar, UserCheck, CheckCircle, Loader2, CheckCircle2, AlertTriangle } from "lucide-react"

// --- INTERFACES ---

interface InfoCelda {
  estado: "DISPONIBLE" | "RESERVADA" | "OCUPADA" | "MANTENIMIENTO" | "FUERA_DE_SERVICIO"
  idReserva?: number
  idEstadia?: number
  fechaInicio?: string
  fechaFin?: string
}

interface HabitacionEstado {
  id: string
  numero: string
  tipo: string
  capacidad: number;
  estadoHabitacion?: "HABILITADA" | "FUERA_DE_SERVICIO"
  estado: "DISPONIBLE" | "RESERVADA" | "OCUPADA" | "MANTENIMIENTO" | "FUERA_DE_SERVICIO"
  precioNoche: number
  estadosPorDia?: Record<string, InfoCelda>
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
  const [showSuccessModal, setShowSuccessModal] = useState(false)
  const [showErrorModal, setShowErrorModal] = useState(false)
  const [mensajeError, setMensajeError] = useState("")

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

  // --- ESTADO Y VALIDACIONES DEL HUÉSPED ---
  const [datosHuesped, setDatosHuesped] = useState<DatosHuesped>({
    apellido: "",
    nombres: "",
    telefono: "",
    nroDocumento: "",
    tipoDocumento: "DNI",
  })
  const [erroresHuesped, setErroresHuesped] = useState<Partial<DatosHuesped>>({})

  const CONFIG_DOCUMENTOS: Record<string, { regex: RegExp; error: string; placeholder: string }> = {
    DNI: { regex: /^\d{7,8}$/, error: "El DNI debe tener 7 u 8 números", placeholder: "Ej: 12345678" },
    PASAPORTE: { regex: /^[A-Z0-9]{6,9}$/, error: "El pasaporte debe tener 6 a 9 caracteres alfanuméricos", placeholder: "Ej: A1234567" },
    LC: { regex: /^\d{6,8}$/, error: "La LC debe tener 6 a 8 números", placeholder: "Ej: 1234567" },
    LE: { regex: /^\d{6,8}$/, error: "La LE debe tener 6 a 8 números", placeholder: "Ej: 1234567" },
    OTRO: { regex: /^[a-zA-Z0-9]{5,20}$/, error: "Formato inválido (5-20 caracteres)", placeholder: "Nro. de Identificación" },
  }

  const validarCampoHuesped = (campo: keyof DatosHuesped, valor: string) => {
    let error = ""
    if (campo === "apellido" || campo === "nombres") {
      const regexNombre = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s-]+$/
      if (!valor.trim()) {
        error = campo === "apellido" ? "El apellido es obligatorio" : "El nombre es obligatorio"
      } else if (valor.length < 2 || valor.length > 50) {
        error = "Debe tener entre 2 y 50 caracteres"
      } else if (!regexNombre.test(valor)) {
        error = "Solo puede contener letras, espacios y guiones"
      }
    }
    
    // --- CORRECCIÓN EN VALIDACIÓN DE TELÉFONO ---
    if (campo === "telefono") {
      // Regex estándar: permite +, (), espacios, guiones y números
      const regexTelefono = /^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\s./0-9]*$/
      const digitos = valor.replace(/[^0-9]/g, "");
      if (!valor.trim()) {
        error = "El teléfono es obligatorio"
      } else if (!regexTelefono.test(valor)) {
        error = "Formato inválido (solo números, +, -, (), espacios)"
      } else if (digitos.length < 8) {
        error = "Debe tener al menos 8 dígitos"
      } else if (digitos.length > 15) {
        error = "Debe tener como máximo 15 dígitos"
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
      if (!valor.trim()) error = "El tipo de documento es obligatorio"
    }
    
    setErroresHuesped((prev) => ({ ...prev, [campo]: error }))
  }

  // Helper para restringir entrada en teléfono (solo caracteres válidos mientras escribe)
  const handleChangeTelefono = (e: React.ChangeEvent<HTMLInputElement>) => {
    const val = e.target.value
    // Permitir solo caracteres válidos para teléfono
    if (/^[+0-9\-\s().]*$/.test(val)) {
      setDatosHuesped((prev) => ({ ...prev, telefono: val }))
    }
  }

  const validarDatosHuesped = (): boolean => {
    const errores: Partial<DatosHuesped> = {}
    
    // Validaciones manuales rápidas al confirmar
    if (!datosHuesped.apellido.trim()) errores.apellido = "Requerido"
    if (!datosHuesped.nombres.trim()) errores.nombres = "Requerido"
    if (!datosHuesped.nroDocumento.trim()) errores.nroDocumento = "Requerido"
    if (!datosHuesped.telefono.trim()) errores.telefono = "Requerido"
    else if (datosHuesped.telefono.replace(/[^0-9]/g, "").length < 8) errores.telefono = "Mínimo 8 dígitos"

    setErroresHuesped(errores)
    return Object.keys(errores).length === 0
  }

  // --- VALIDACIONES DE FECHAS ---
  const validarFechaDesde = (): boolean => {
    if (!fechaDesde) { setErrorFecha("Debe seleccionar una fecha"); return false; }
    const hoy = new Date(); hoy.setHours(0, 0, 0, 0);
    if (createLocalDate(fechaDesde) < hoy) { setErrorFecha("No puede ser anterior a hoy"); return false; }
    setErrorFecha(""); return true;
  }

  const validarFechaHasta = (): boolean => {
    if (!fechaHasta) { setErrorFecha("Debe seleccionar una fecha"); return false; }
    if (createLocalDate(fechaDesde) >= createLocalDate(fechaHasta)) { setErrorFecha("La fecha hasta debe ser posterior"); return false; }
    setErrorFecha(""); return true;
  }

  const handleConfirmarDesde = () => { if (validarFechaDesde()) setPaso("fechaHasta"); }
  const handleConfirmarHasta = async () => { if (validarFechaHasta()) { setPaso("grilla"); await cargarHabitacionesConEstado(); } }

  // --- CARGA DE DATOS ---
  const cargarHabitacionesConEstado = async () => {
    if (!fechaDesde || !fechaHasta) return
    setLoading(true); setErrorCarga("")

    try {
      const response = await fetch(`http://localhost:8080/api/habitaciones/estados?fechaDesde=${fechaDesde}&fechaHasta=${fechaHasta}`)
      if (!response.ok) throw new Error("Error al cargar habitaciones")
      const data = await response.json()

      const habitacionesMapeadas: HabitacionEstado[] = data.map((h: any) => {
        const estadosPorDia = h.estadosPorDia || {}
        const todosLosEstados = Object.values(estadosPorDia).map((e: any) => e.estado)
        const esFueraDeServicio = todosLosEstados.length > 0 && todosLosEstados.every((e: string) => e === "MANTENIMIENTO")
        const infoPrimerDia = estadosPorDia[fechaDesde] as InfoCelda | undefined
        const estadoBase = infoPrimerDia?.estado || "DISPONIBLE"

        return {
          id: h.numero.toString(),
          numero: h.numero.toString(),
          tipo: h.tipoHabitacion,
          capacidad: h.capacidad,
          estadoHabitacion: esFueraDeServicio ? "FUERA_DE_SERVICIO" : "HABILITADA",
          estado: estadoBase,
          precioNoche: h.costoPorNoche,
          estadosPorDia: estadosPorDia,
        }
      })
      setHabitaciones(habitacionesMapeadas)
    } catch (error) {
      console.error(error); setErrorCarga("Error al cargar habitaciones")
    } finally {
      setLoading(false)
    }
  }

  const generarDias = (desde?: string, hasta?: string): Date[] => {
    if (!desde || !hasta) return []
    const d = createLocalDate(desde); const h = createLocalDate(hasta)
    const dias: Date[] = []; const actual = new Date(d)
    while (actual < h) { dias.push(new Date(actual)); actual.setDate(actual.getDate() + 1) }
    return dias
  }
  const diasRango = generarDias(fechaDesde, fechaHasta)

  // --- LÓGICA DE SELECCIÓN ---
  const obtenerDatosCelda = (habitacionId: string, diaIdx: number): InfoCelda => {
    const habitacion = habitaciones.find(h => h.id === habitacionId)
    if (!habitacion) return { estado: "OCUPADA" }
    if (habitacion.estadoHabitacion === "FUERA_DE_SERVICIO") return { estado: "FUERA_DE_SERVICIO" }

    const diaDate = diasRango[diaIdx]
    if (habitacion.estadosPorDia && diaDate) {
      const fechaDia = diaDate.toISOString().split("T")[0]
      if (habitacion.estadosPorDia[fechaDia]) return habitacion.estadosPorDia[fechaDia]
    }
    return { estado: habitacion.estado || "DISPONIBLE" }
  }

  const esCeldaDisponible = (habitacionId: string, diaIdx: number): boolean => {
    const info = obtenerDatosCelda(habitacionId, diaIdx)
    if (info.estado !== "DISPONIBLE") return false
    return !selecciones.some(
      (sel) => sel.habitacionId === habitacionId && diaIdx >= sel.diaInicio && diaIdx <= sel.diaFin
    )
  }

    const [dialogError, setDialogError] = useState<{
        titulo: string;
        mensaje: string;
        tipo: "error" | "warning" | "info";
    } | null>(null);

  const handleClickCelda = (habitacionId: string, diaIdx: number) => {
    if (!esCeldaDisponible(habitacionId, diaIdx)) return

    if (!seleccionActual || seleccionActual.habitacionId !== habitacionId) {
      setSeleccionActual({ habitacionId, diaInicio: diaIdx })
    } else {
      const inicio = seleccionActual.diaInicio!
      const fin = diaIdx
      const min = Math.min(inicio, fin)
      const max = Math.max(inicio, fin)

      const rango = Array.from({ length: max - min + 1 }, (_, i) => min + i)
      const todoDisponible = rango.every(dia => esCeldaDisponible(habitacionId, dia))

      if (todoDisponible) {
        setSelecciones(prev => [...prev, { habitacionId, diaInicio: min, diaFin: max }])
        setSeleccionActual(null)
      } else {
          setDialogError({
              titulo: "Rango no disponible",
              mensaje: "El rango seleccionado contiene días no disponibles.",
              tipo: "warning"
          });

          setSeleccionActual(null)
      }
    }
  }

  const esCeldaSeleccionada = (habitacionId: string, diaIdx: number) => {
    return selecciones.some(s => s.habitacionId === habitacionId && diaIdx >= s.diaInicio && diaIdx <= s.diaFin)
  }

  const esInicioSeleccionActual = (habitacionId: string, diaIdx: number) => {
    return seleccionActual?.habitacionId === habitacionId && seleccionActual.diaInicio === diaIdx
  }

  const handleRemoverSeleccion = (index: number) => {
    setSelecciones(prev => prev.filter((_, i) => i !== index))
  }

  // --- NAVEGACIÓN ---

  const handleConfirmarHuesped = () => { if (validarDatosHuesped()) setPaso("confirmacion") }

  const handleVolverPaso = () => {
    if (paso === "fechaHasta") { setErrorFecha(""); setPaso("fechaDesde") }
    else if (paso === "grilla") { setErrorFecha(""); setPaso("fechaHasta") }
    else if (paso === "datosHuesped") setPaso("grilla")
    else if (paso === "confirmacion") setPaso("datosHuesped")
  }

  // --- FINALIZAR ---
  const handleFinalizarReserva = async () => {
    setLoading(true)
    try {
      const payload = selecciones.map(sel => {
        const hab = habitaciones.find(h => h.id === sel.habitacionId)
        const dInicio = diasRango[sel.diaInicio]
        const dFin = new Date(diasRango[sel.diaFin])
        dFin.setDate(dFin.getDate() + 1)

        return {
            idReserva: 0,
            estadoReserva: "ACTIVA",
            fechaDesde: dInicio.toISOString().split("T")[0],
            fechaHasta: dFin.toISOString().split("T")[0],
            nombreHuespedResponsable: datosHuesped.nombres,
            apellidoHuespedResponsable: datosHuesped.apellido,
            telefonoHuespedResponsable: datosHuesped.telefono,
            nroDocumentoResponsable: datosHuesped.nroDocumento,
            tipoDocumentoResponsable: datosHuesped.tipoDocumento,
            idHabitacion: hab?.numero || sel.habitacionId
        }
      })

      const res = await fetch("http://localhost:8080/api/reservas/crear", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload)
      })
      if (!res.ok) throw new Error(await res.text())
      setShowSuccessModal(true)
    } catch (e: any) {
        setMensajeError(e.message)
        setShowErrorModal(true)
    } finally {
        setLoading(false)
    }
  }

  const getEstadoColor = (estado: string) => {
    switch (estado) {
      case "DISPONIBLE": return "bg-green-600 dark:bg-green-700"
      case "RESERVADA": return "bg-orange-600 dark:bg-orange-700"
      case "OCUPADA": return "bg-red-600 dark:bg-red-700"
      case "FUERA_DE_SERVICIO": return "bg-slate-500 dark:bg-slate-600"
      case "MANTENIMIENTO": return "bg-slate-500 dark:bg-slate-600"
      default: return "bg-slate-600 dark:bg-slate-700"
    }
  }

  const calcularTotal = () => {
      return selecciones.reduce((acc, sel) => {
          const hab = habitaciones.find(h => h.id === sel.habitacionId)
          if (!hab) return acc
          return acc + (hab.precioNoche * (sel.diaFin - sel.diaInicio + 1))
      }, 0)
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50 dark:from-slate-950 dark:via-slate-900 dark:to-slate-950 relative">
      <main className="mx-auto max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
        
        {/* HEADER */}
        <div className="mb-8 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
            <div className="flex items-center gap-4">
                <div className="flex h-14 w-14 items-center justify-center rounded-2xl bg-blue-600 text-white shadow-lg">
                    <Hotel className="h-8 w-8" />
                </div>
                <div>
                    <p className="text-xs font-bold uppercase tracking-widest text-blue-600 dark:text-blue-400">CASO DE USO 04</p>
                    <h1 className="text-3xl font-extrabold text-slate-900 dark:text-slate-50 tracking-tight">Reservar Habitación</h1>
                </div>
            </div>
            <Button variant="outline" asChild>
                <Link href="/"> <Home className="mr-2 h-4 w-4" /> Volver al Menú Principal </Link>
            </Button>
        </div>

        {/* STEPPER */}
        <Card className="mb-8 border-none shadow-sm bg-white/60 backdrop-blur-md overflow-hidden p-1">
            <div className="flex flex-wrap items-center">
                {[
                    { id: "fechaDesde", label: "Inicio", icon: Calendar },
                    { id: "fechaHasta", label: "Fin", icon: Calendar },
                    { id: "grilla", label: "Disponibilidad", icon: Hotel },
                    { id: "datosHuesped", label: "Titular", icon: UserCheck },
                    { id: "confirmacion", label: "Finalizar", icon: CheckCircle },
                ].map((s, idx) => {
                    const active = paso === s.id;
                    return (
                        <div key={s.id} className="flex flex-1 items-center">
                            <div className={`flex flex-1 items-center justify-center gap-2 py-3 px-4 rounded-xl transition-all duration-300 ${active ? "bg-white shadow-sm text-blue-600 font-bold" : "text-slate-400"}`}>
                                <s.icon className="h-4 w-4" />
                                <span className="hidden sm:inline text-sm uppercase tracking-tighter">{idx + 1}. {s.label}</span>
                            </div>
                            {idx < 4 && <div className="h-4 w-[1px] bg-slate-200 mx-1 hidden sm:block" />}
                        </div>
                    );
                })}
            </div>
        </Card>

        {/* PASOS FECHAS */}
        {(paso === "fechaDesde" || paso === "fechaHasta") && (
            <div className="flex justify-center items-center min-h-[50vh]">
                <Card className="p-8 max-w-md w-full bg-white/80 backdrop-blur-sm shadow-xl">
                    <h2 className="text-2xl font-bold mb-6 text-center text-slate-900">
                        {paso === "fechaDesde" ? "Fecha de Inicio" : "Fecha de Fin"}
                    </h2>
                    <div className="space-y-6">
                        {paso === "fechaDesde" ? (
                            <Input className="h-12" type="date" value={fechaDesde} onChange={e => {setFechaDesde(e.target.value); setErrorFecha("")}} />
                        ) : (
                            <Input className="h-12" type="date" value={fechaHasta} onChange={e => {setFechaHasta(e.target.value); setErrorFecha("")}} />
                        )}
                        {errorFecha && <p className="text-red-600 text-sm font-medium">{errorFecha}</p>}
                        <div className="flex gap-3 pt-4">
                            {paso === "fechaHasta" && <Button variant="outline" className="flex-1 h-12" onClick={() => setPaso("fechaDesde")}>Atrás</Button>}
                            <Button className="flex-1 h-12 bg-blue-600 hover:bg-blue-700" onClick={paso === "fechaDesde" ? handleConfirmarDesde : handleConfirmarHasta}>Continuar</Button>
                        </div>
                    </div>
                </Card>
            </div>
        )}

        {/* PASO GRILLA */}
        {paso === "grilla" && (
          <div className="space-y-6">
            {loading ? <div className="text-center p-12"><Loader2 className="animate-spin w-16 h-16 mx-auto text-blue-600"/></div> : (
              <>
              <Card className="p-8 border-0 shadow-xl bg-white/80 dark:bg-slate-900/80 backdrop-blur-sm">
                <h2 className="text-2xl font-bold mb-4 text-slate-900 dark:text-slate-50">Grilla de Disponibilidad</h2>
                <div className="overflow-x-auto rounded-lg border border-slate-200">
                  <table className="w-full border-collapse text-sm">
                    <thead>
                      <tr className="bg-slate-100 dark:bg-slate-800">
                        <th className="border px-4 py-3 text-slate-900 dark:text-white">Fecha</th>
                        {TIPOS_HABITACION_ORDEN.map(t => {
                          const count = habitaciones.filter(h => h.tipo === t).length
                          return count > 0 ? <th key={t} colSpan={count} className="border px-2 py-3 bg-blue-50 dark:bg-blue-900/30 text-slate-900 dark:text-white">{formatearTipo(t)}</th> : null
                        })}
                      </tr>
                      <tr className="bg-slate-50 dark:bg-slate-800">
                        <th className="border px-4 py-3"></th>
                        {TIPOS_HABITACION_ORDEN.map(t => 
                          habitaciones.filter(h => h.tipo === t)
                          .sort((a,b) => parseInt(a.numero)-parseInt(b.numero))
                          .map(h => <th key={h.id} className="border px-2 text-xs text-slate-900 dark:text-white">Hab. {h.numero}</th>)
                        )}
                      </tr>
                    </thead>
                    <tbody>
                      {diasRango.map((dia, diaIdx) => (
                        <tr key={diaIdx} className={diaIdx % 2 === 0 ? "bg-slate-50 dark:bg-slate-800/50" : "bg-white dark:bg-transparent"}>
                          <td className="border px-4 py-3 font-bold text-slate-700 dark:text-slate-200">
                            {dia.toLocaleDateString("es-AR", {weekday: 'short', day: '2-digit', month: '2-digit'})}
                          </td>
                          {TIPOS_HABITACION_ORDEN.map(t => 
                            habitaciones.filter(h => h.tipo === t)
                            .sort((a,b) => parseInt(a.numero)-parseInt(b.numero))
                            .map(h => {
                              const info = obtenerDatosCelda(h.id, diaIdx)
                              const infoPrev = diaIdx > 0 ? obtenerDatosCelda(h.id, diaIdx - 1) : null
                              const seleccionado = esCeldaSeleccionada(h.id, diaIdx)
                              const esInicio = esInicioSeleccionActual(h.id, diaIdx)
                              const baseColor = getEstadoColor(info.estado)

                              let bordeClase = "border border-slate-200 dark:border-slate-700"
                              if (info.estado === "RESERVADA" && infoPrev?.estado === "RESERVADA" && info.idReserva !== infoPrev.idReserva) {
                                bordeClase = "border-t-4 border-t-white border-x border-b border-slate-200"
                              }

                              return (
                                <td key={`${h.id}-${diaIdx}`} className={`p-0 text-center relative ${bordeClase}`}>
                                  <div 
                                    onClick={() => handleClickCelda(h.id, diaIdx)}
                                    className={`
                                      h-10 flex items-center justify-center text-xs font-bold text-white transition-all cursor-pointer
                                      ${seleccionado ? "bg-blue-600 ring-2 ring-blue-600 z-10 relative" : 
                                        esInicio ? "bg-blue-400 animate-pulse" :
                                        info.estado === "DISPONIBLE" ? baseColor : `${baseColor} opacity-75 cursor-not-allowed`
                                      }
                                    `}
                                  >
                                    {seleccionado ? "✓" : esInicio ? "●" : 
                                     info.estado === "RESERVADA" ? "R" :
                                     info.estado === "OCUPADA" ? "X" :
                                     info.estado === "DISPONIBLE" ? "○" : "FS"}
                                  </div>
                                </td>
                              )
                            })
                          )}
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
                {/* Listado de reservas seleccionadas con opción de quitar */}
                {selecciones.length > 0 && (
                  <>
                  <div className="mt-8">
                    <h3 className="text-lg font-bold mb-3 text-blue-700">Reservas seleccionadas</h3>
                    <ul className="space-y-3">
                    {selecciones.map((sel, idx) => {
                      const habitacion = habitaciones.find(h => h.id === sel.habitacionId)
                      const diaInicio = diasRango[sel.diaInicio]
                      const diaFin = new Date(diasRango[sel.diaFin])
                      diaFin.setDate(diaFin.getDate() + 1)
                      return (
                      <li key={idx} className="flex items-center gap-4 bg-blue-50 dark:bg-blue-900/30 rounded-lg px-4 py-3 border border-blue-100 dark:border-blue-800">
                        <span className="font-bold text-blue-800 dark:text-blue-200">Hab. {habitacion?.numero}</span>
                        <span className="text-slate-700 dark:text-slate-200">{formatearTipo(habitacion?.tipo || "")}</span>
                        <span className="text-slate-600 dark:text-slate-300">{diaInicio?.toLocaleDateString()} - {diaFin?.toLocaleDateString()}</span>
                        <Button variant="outline" size="sm" className="ml-auto" onClick={() => handleRemoverSeleccion(idx)}>
                        Quitar
                        </Button>
                      </li>
                      )
                    })}
                    </ul>
                  </div>
                  <div className="mt-6 flex justify-end">
                    <Button onClick={() => setPaso("datosHuesped")} className="h-12 bg-blue-600 hover:bg-blue-700 font-bold px-8">
                      Continuar con Datos del Huésped
                    </Button>
                  </div>
                  </>
                )}
              </Card>
              </>
            )}
          </div>
        )}

        {/* PASO 4: DATOS HUÉSPED (CORREGIDO) */}
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
                  {/* CAMBIO: onChange restringido y validación normalizada */}
                  <Input
                    id="telefono"
                    value={datosHuesped.telefono}
                    placeholder="Ej: +541112345678"
                    onChange={handleChangeTelefono}
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

        {/* PASO 5: CONFIRMACIÓN */}
        {paso === "confirmacion" && (
            <Card className="border-0 shadow-xl bg-white/80 dark:bg-slate-900/80 backdrop-blur-sm p-8 max-w-3xl mx-auto">
                <h2 className="text-2xl font-bold mb-8 text-slate-900 dark:text-slate-50">Confirmación de reserva</h2>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
                    <div>
                        <div className="text-sm font-semibold text-slate-500 dark:text-slate-400 mb-2">Fecha desde:</div>
                        <div className="rounded-xl bg-gradient-to-br from-blue-50 to-indigo-50 border border-blue-100 px-5 py-4 text-lg font-bold">
                            {fechaDesde && createLocalDate(fechaDesde).toLocaleDateString()}
                        </div>
                    </div>
                    <div>
                        <div className="text-sm font-semibold text-slate-500 dark:text-slate-400 mb-2">Fecha hasta:</div>
                        <div className="rounded-xl bg-gradient-to-br from-blue-50 to-indigo-50 border border-blue-100 px-5 py-4 text-lg font-bold">
                            {fechaHasta && createLocalDate(fechaHasta).toLocaleDateString()}
                        </div>
                    </div>
                    <div>
                        <div className="text-sm font-semibold text-slate-500 mb-2">Huésped responsable:</div>
                        <div className="rounded-xl bg-gradient-to-br from-blue-50 to-indigo-50 border border-blue-100 px-5 py-4 text-lg font-bold">
                            {datosHuesped.nombres} {datosHuesped.apellido}
                        </div>
                    </div>
                    <div>
                        <div className="text-sm font-semibold text-slate-500 mb-2">Teléfono:</div>
                        <div className="rounded-xl bg-gradient-to-br from-blue-50 to-indigo-50 border border-blue-100 px-5 py-4 text-lg font-bold">
                            {datosHuesped.telefono}
                        </div>
                    </div>
                </div>

                <div className="mb-8">
                    <div className="text-sm font-semibold text-slate-500 mb-3">Habitaciones reservadas:</div>
                    <div className="space-y-3">
                        {selecciones.map((sel, idx) => {
                            const habitacion = habitaciones.find((h) => h.id === sel.habitacionId)
                            const diaInicioDate = diasRango[sel.diaInicio]
                            const diaFinReal = new Date(diasRango[sel.diaFin])
                            diaFinReal.setDate(diaFinReal.getDate() + 1)

                            return (
                                <div key={idx} className="rounded-xl border border-slate-200 bg-gradient-to-br from-slate-50 to-blue-50 px-6 py-4 flex flex-col md:flex-row md:items-center md:gap-6 shadow-sm">
                                    <span className="font-bold text-lg text-blue-700">Habitación {habitacion?.numero}</span>
                                    <span className="text-slate-700 font-semibold">{formatearTipo(habitacion?.tipo || "")}</span>
                                    <span className="text-slate-600 ml-auto font-bold text-xl">
                                        ${habitacion ? habitacion.precioNoche * (sel.diaFin - sel.diaInicio + 1) : 0}
                                    </span>
                                </div>
                            )
                        })}
                    </div>
                </div>

                <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-6 border-t-2 border-slate-200 pt-6">
                    <div className="text-2xl font-bold text-slate-900">
                        Total: <span className="bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent">${calcularTotal()}</span>
                    </div>
                    <div className="flex gap-4">
                        <Button onClick={handleVolverPaso} variant="outline" disabled={loading} className="h-12 bg-transparent">← Atrás</Button>
                        <Button onClick={handleFinalizarReserva} disabled={loading} className="h-12 gap-2 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700">
                            {loading ? <Loader2 className="w-5 h-5 animate-spin" /> : <CheckCircle className="w-5 h-5" />} Finalizar reserva
                        </Button>
                    </div>
                </div>
            </Card>
        )}

        {/* MODALES */}
        {showSuccessModal && (
            <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm animate-in fade-in duration-300">
                <Card className="mx-4 w-full max-w-md border-none shadow-2xl bg-white p-8 text-center animate-in zoom-in-95 duration-300">
                    <div className="mx-auto mb-4 flex h-20 w-20 items-center justify-center rounded-full bg-emerald-100 text-emerald-600">
                        <CheckCircle2 className="h-12 w-12" />
                    </div>
                    <h3 className="text-2xl font-black text-slate-800 mb-2">¡Reserva Exitosa!</h3>
                    <p className="text-slate-500 mb-8 font-medium">Las habitaciones han sido reservadas correctamente.</p>
                    <Button onClick={() => router.push("/")} className="w-full h-12 bg-emerald-500 hover:bg-emerald-600 text-white font-bold rounded-xl">Finalizar y Volver</Button>
                </Card>
            </div>
        )}

        {showErrorModal && (
            <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm">
                <Card className="mx-4 w-full max-w-md border-none shadow-2xl bg-white p-8 text-center">
                    <div className="mx-auto mb-4 flex h-20 w-20 items-center justify-center rounded-full bg-rose-100 text-rose-600">
                        <AlertTriangle className="h-12 w-12" />
                    </div>
                    <h3 className="text-2xl font-black text-slate-800 mb-2">Error en la Reserva</h3>
                    <p className="text-slate-500 mb-6 font-medium text-sm">{mensajeError}</p>
                    <Button onClick={() => setShowErrorModal(false)} className="w-full h-12 bg-rose-500 text-white font-bold">Cerrar</Button>
                </Card>
            </div>
        )}
          {dialogError && (
              <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm" onClick={() => setDialogError(null)}>
                  <Card className={`w-full max-w-md p-6 border-2 ${
                      dialogError.tipo === "error" ? "border-red-500" :
                          dialogError.tipo === "warning" ? "border-orange-500" :
                              "border-blue-500"
                  }`} onClick={(e) => e.stopPropagation()}>
                      <h3 className={`text-xl font-bold mb-4 ${
                          dialogError.tipo === "error" ? "text-red-600" :
                              dialogError.tipo === "warning" ? "text-orange-600" :
                                  "text-blue-600"
                      }`}>
                          {dialogError.titulo}
                      </h3>
                      <p className="text-slate-700 mb-6 whitespace-pre-line">{dialogError.mensaje}</p>
                      <Button
                          onClick={() => setDialogError(null)}
                          className={`w-full ${
                              dialogError.tipo === "error" ? "bg-red-600 hover:bg-red-700" :
                                  dialogError.tipo === "warning" ? "bg-orange-600 hover:bg-orange-700" :
                                      "bg-blue-600 hover:bg-blue-700"
                          }`}
                      >
                          Entendido
                      </Button>
                  </Card>
              </div>
          )}

      </main>
    </div>
  )
}