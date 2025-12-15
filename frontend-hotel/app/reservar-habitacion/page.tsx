"use client"

import Link from "next/link"
import { useRouter } from "next/navigation"
import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Hotel, Home, Calendar, UserCheck, CheckCircle, Loader2 } from "lucide-react"
import GrillaHabitacionesEstado from "@/components/grilla-habitaciones-estado"

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
    diaInicio: number // índice del día en el rango
    diaFin: number
}

interface DatosHuesped {
    apellido: string
    nombres: string
    telefono: string
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

// Helper function to create date in local timezone
const createLocalDate = (dateString: string): Date => {
    const [year, month, day] = dateString.split("-").map(Number)
    return new Date(year, month - 1, day)
}

export default function ReservarHabitacion() {
    const router = useRouter()
    const [paso, setPaso] = useState<Paso>("fechaDesde")
    const [fechaDesde, setFechaDesde] = useState("")
    const [fechaHasta, setFechaHasta] = useState("")
    const [errorFecha, setErrorFecha] = useState("")
    const [habitaciones, setHabitaciones] = useState<HabitacionEstado[]>([])
    const [loading, setLoading] = useState(false)
    const [errorCarga, setErrorCarga] = useState("")

    // Estado de selección de habitaciones
    const [selecciones, setSelecciones] = useState<SeleccionHabitacion[]>([])
    const [seleccionActual, setSeleccionActual] = useState<{
        habitacionId: string
        diaInicio: number | null
    } | null>(null)

    // Datos del huésped responsable
    const [datosHuesped, setDatosHuesped] = useState<DatosHuesped>({
        apellido: "",
        nombres: "",
        telefono: "",
    })
    const [erroresHuesped, setErroresHuesped] = useState<Partial<DatosHuesped>>({})

    // Las habitaciones se cargarán después de seleccionar las fechas

    // Validaciones de fechas
    const validarFechaDesde = (): boolean => {
        if (!fechaDesde) {
            setErrorFecha("Debe seleccionar una fecha")
            return false
        }
        // VALIDACIÓN DEL BACKEND: La fecha de ingreso no puede ser anterior al día de hoy
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
        // VALIDACIÓN DEL BACKEND: fechaHasta > fechaDesde
        if (desde >= hasta) {
            setErrorFecha("La fecha 'Hasta' debe ser posterior a la fecha 'Desde'")
            return false
        }
        // VALIDACIÓN DEL BACKEND: @Future - debe ser futura respecto a hoy
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
            // Cambiar al paso de grilla primero para que se vea el mensaje de carga
            setPaso("grilla")
            // Cargar habitaciones con estado real para el rango de fechas
            await cargarHabitacionesConEstado()
        }
    }

    // Cargar habitaciones con estado real basado en fechas
    const cargarHabitacionesConEstado = async () => {
        if (!fechaDesde || !fechaHasta) return

        setLoading(true)
        setErrorCarga("")
        
        // Delay mínimo para asegurar que el mensaje de carga se vea
        const [response] = await Promise.all([
            fetch(`http://localhost:8080/api/habitaciones/estados?fechaDesde=${fechaDesde}&fechaHasta=${fechaHasta}`),
            new Promise(resolve => setTimeout(resolve, 300))
        ])
        
        try {
            if (!response.ok) throw new Error("Error al cargar habitaciones")

            const data = await response.json()
            console.log("[v0] Habitaciones con estado para reserva:", data)

            const habitacionesMapeadas = data.map((h: any) => {
                const estadosPorDia = h.estadosPorDia || {}
                
                // Detectar si la habitación está fuera de servicio
                // (el backend envía "MANTENIMIENTO" en estadosPorDia cuando está fuera de servicio)
                const todosLosEstados = Object.values(estadosPorDia)
                const esFueraDeServicio = todosLosEstados.length > 0 && 
                    todosLosEstados.every((estado: any) => estado === "MANTENIMIENTO")
                
                // El estado base viene de estadosPorDia para el primer día
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
            setErrorCarga("Error al cargar habitaciones: Failed to fetch")
        } finally {
            setLoading(false)
        }
    }

    // Generar días del rango
    const generarDias = (desde?: string, hasta?: string): Date[] => {
        const fechaDesdeUse = desde || fechaDesde;
        const fechaHastaUse = hasta || fechaHasta;
        if (!fechaDesdeUse || !fechaHastaUse) return [];
        const desdeDate = createLocalDate(fechaDesdeUse);
        const hastaDate = createLocalDate(fechaHastaUse);
        const dias: Date[] = [];
        const actual = new Date(desdeDate);
        while (actual < hastaDate) {
            dias.push(new Date(actual));
            actual.setDate(actual.getDate() + 1);
        }
        return dias;
    }

    const diasRango = generarDias(fechaDesde, fechaHasta);

    // Verificar si una celda está disponible para selección
    const esCeldaDisponible = (habitacionId: string, diaIdx: number): boolean => {
        const habitacion = habitaciones.find((h: HabitacionEstado) => h.id === habitacionId)
        if (!habitacion) return false

        // Si la habitación está fuera de servicio, no está disponible
        if (habitacion.estadoHabitacion === "FUERA_DE_SERVICIO") return false

        // Si tenemos estadosPorDia, usar el estado específico del día
        if (habitacion.estadosPorDia && diasRango[diaIdx]) {
            const fechaDia = diasRango[diaIdx].toISOString().split("T")[0]
            const estadoDia = habitacion.estadosPorDia[fechaDia]
            if (estadoDia && estadoDia !== "DISPONIBLE") return false
        } else {
            // Fallback al estado general
            if (habitacion.estado !== "DISPONIBLE") return false
        }

        // Verificar que no esté ya seleccionada
        return !selecciones.some(
            (sel) => sel.habitacionId === habitacionId && diaIdx >= sel.diaInicio && diaIdx <= sel.diaFin,
        )
    }

    // Obtener el estado de una celda específica
    const obtenerEstadoCelda = (habitacionId: string, diaIdx: number): "DISPONIBLE" | "RESERVADA" | "OCUPADA" | "FUERA_DE_SERVICIO" => {
        const habitacion = habitaciones.find((h: HabitacionEstado) => h.id === habitacionId)
        if (!habitacion) return "OCUPADA"

        // Si la habitación está fuera de servicio, retornar ese estado
        if (habitacion.estadoHabitacion === "FUERA_DE_SERVICIO") return "FUERA_DE_SERVICIO"

        // Si tenemos estadosPorDia, usar el estado específico del día
        if (habitacion.estadosPorDia && diasRango[diaIdx]) {
            const fechaDia = diasRango[diaIdx].toISOString().split("T")[0]
            const estadoDia = habitacion.estadosPorDia[fechaDia]
            return (estadoDia as "DISPONIBLE" | "RESERVADA" | "OCUPADA") || "DISPONIBLE"
        }

        // Fallback al estado general
        return habitacion.estado
    }

    // Manejar click en celda de la grilla
    const handleClickCelda = (habitacionId: string, diaIdx: number) => {
        if (!esCeldaDisponible(habitacionId, diaIdx)) return

        if (!seleccionActual || seleccionActual.habitacionId !== habitacionId) {
            // Primer click: establecer punto de inicio
            setSeleccionActual({ habitacionId, diaInicio: diaIdx })
        } else {
            // Segundo click: establecer punto de fin y confirmar selección
            const diaInicio = seleccionActual.diaInicio!
            const diaFin = diaIdx

            // Validar que todos los días en el rango estén disponibles
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

    // Verificar si una celda está seleccionada
    const esCeldaSeleccionada = (habitacionId: string, diaIdx: number): boolean => {
        return selecciones.some(
            (sel) => sel.habitacionId === habitacionId && diaIdx >= sel.diaInicio && diaIdx <= sel.diaFin,
        )
    }

    // Verificar si es inicio de selección actual
    const esInicioSeleccionActual = (habitacionId: string, diaIdx: number): boolean => {
        return seleccionActual?.habitacionId === habitacionId && seleccionActual.diaInicio === diaIdx
    }

    // Remover una selección
    const handleRemoverSeleccion = (index: number) => {
        setSelecciones((prev) => prev.filter((_, i) => i !== index))
    }

    // Continuar a datos del huésped
    const handleContinuarHuesped = () => {
        if (selecciones.length === 0) {
            alert("Debe seleccionar al menos una habitación con un rango de fechas")
            return
        }
        setPaso("datosHuesped")
    }

    // Validar datos del huésped
    const validarDatosHuesped = (): boolean => {
        const errores: Partial<DatosHuesped> = {}

        // Regex patterns del backend DtoReserva
        const regexNombre = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/
        const regexTelefono = /^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\s./0-9]*$/

        // Apellido - BACKEND: @NotBlank, @Pattern(REGEX_NOMBRE)
        if (!datosHuesped.apellido.trim()) {
            errores.apellido = "El apellido es obligatorio"
        } else if (datosHuesped.apellido.length < 2 || datosHuesped.apellido.length > 50) {
            errores.apellido = "El apellido debe tener entre 2 y 50 caracteres"
        } else if (!regexNombre.test(datosHuesped.apellido)) {
            errores.apellido = "El apellido solo puede contener letras y espacios"
        }

        // Nombres - BACKEND: @NotBlank, @Pattern(REGEX_NOMBRE)
        if (!datosHuesped.nombres.trim()) {
            errores.nombres = "El nombre es obligatorio"
        } else if (datosHuesped.nombres.length < 2 || datosHuesped.nombres.length > 50) {
            errores.nombres = "El nombre debe tener entre 2 y 50 caracteres"
        } else if (!regexNombre.test(datosHuesped.nombres)) {
            errores.nombres = "El nombre solo puede contener letras y espacios"
        }

        // Teléfono - BACKEND: @NotBlank, @Pattern(REGEX_TELEFONO)
        if (!datosHuesped.telefono.trim()) {
            errores.telefono = "El teléfono es obligatorio"
        } else {
            // Normalizar teléfono: agregar +54 si no tiene código de país
            let telefonoNormalizado = datosHuesped.telefono.trim()
            if (!telefonoNormalizado.startsWith("+")) {
                telefonoNormalizado = "+54 " + telefonoNormalizado
                setDatosHuesped((prev) => ({ ...prev, telefono: telefonoNormalizado }))
            }
            if (!regexTelefono.test(telefonoNormalizado)) {
                errores.telefono = "Formato de teléfono inválido"
            } else if (telefonoNormalizado.replace(/[^0-9]/g, "").length < 8) {
                errores.telefono = "El teléfono debe tener al menos 8 dígitos"
            }
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
            // Crear array de reservas para enviar al backend
            const reservas = selecciones.map(sel => {
                const habitacion = habitaciones.find(h => h.id === sel.habitacionId)
                const diaInicioDate = diasRango[sel.diaInicio]
                const diaFinDate = diasRango[sel.diaFin]

                return {
                    idReserva: 0,
                    estadoReserva: "ACTIVA" as const,
                    fechaDesde: diaInicioDate.toISOString().split('T')[0],
                    fechaHasta: diaFinDate.toISOString().split('T')[0],
                    nombreHuespedResponsable: datosHuesped.nombres,
                    apellidoHuespedResponsable: datosHuesped.apellido,
                    telefonoHuespedResponsable: datosHuesped.telefono,
                    idHabitacion: habitacion?.numero || sel.habitacionId
                }
            })

            // Llamar al backend
            const response = await fetch('http://localhost:8080/api/reservas/crear', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(reservas)
            })

            if (!response.ok) {
                throw new Error('Error al crear las reservas')
            }

            alert('✅ Reservas creadas con éxito')
            router.push("/")
        } catch (error) {
            console.error('Error:', error)
            alert('Error al crear las reservas. Por favor intente nuevamente.')
        } finally {
            setLoading(false)
        }
    }

    // Calcular precio total
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
        <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50 dark:from-slate-950 dark:via-slate-900 dark:to-slate-950">
            <main className="mx-auto max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
                <div className="mb-8">
                    <div className="mb-6 flex items-center gap-4">
                        <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-gradient-to-br from-blue-600 to-indigo-600 text-white shadow-lg">
                            <Hotel className="h-6 w-6" />
                        </div>
                        <div>
                            <p className="text-xs font-semibold uppercase tracking-wider text-blue-600 dark:text-blue-400">CU04</p>
                            <h1 className="text-3xl font-bold text-slate-900 dark:text-slate-50">Reservar Habitación</h1>
                        </div>
                    </div>
                    <p className="text-slate-600 dark:text-slate-400">
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
                    </p>
                </div>

                {/* PASO 1: Seleccionar Fecha Desde */}
                {paso === "fechaDesde" && (
                    <Card className="p-6 max-w-md">
                        <h2 className="text-xl font-semibold mb-4 text-slate-900 dark:text-slate-50">Seleccione fecha de inicio</h2>
                        <div className="space-y-4">
                            <div>
                                <Label htmlFor="fechaDesde">Fecha Desde</Label>
                                <Input
                                    id="fechaDesde"
                                    type="date"
                                    value={fechaDesde}
                                    onChange={(e) => {
                                        setFechaDesde(e.target.value)
                                        setErrorFecha("")
                                    }}
                                />
                            </div>
                            {errorFecha && (
                                <Card className="border-red-200 bg-red-50 p-3 dark:border-red-900 dark:bg-red-950/20">
                                    <p className="text-sm text-red-600 dark:text-red-400">{errorFecha}</p>
                                </Card>
                            )}
                            <div className="flex gap-3">
                                <Button onClick={handleConfirmarDesde} className="flex-1 gap-2">
                                    Continuar
                                    <Calendar className="h-4 w-4" />
                                </Button>
                                <Button variant="outline" asChild>
                                    <Link href="/">
                                        <Home className="mr-2 h-4 w-4" />
                                        Inicio
                                    </Link>
                                </Button>
                            </div>
                        </div>
                    </Card>
                )}

                {/* PASO 2: Seleccionar Fecha Hasta */}
                {paso === "fechaHasta" && (
                    <Card className="p-6 max-w-md">
                        <h2 className="text-xl font-semibold mb-4 text-slate-900 dark:text-slate-50">Seleccione fecha de fin</h2>
                        <div className="space-y-4">
                            <Card className="border-blue-200 bg-blue-50/50 p-4 dark:border-blue-900 dark:bg-blue-950/20">
                                <p className="text-sm text-slate-600 dark:text-slate-400">Fecha desde:</p>
                                <p className="text-lg font-semibold text-slate-900 dark:text-slate-50">
                                    {createLocalDate(fechaDesde).toLocaleDateString()}
                                </p>
                            </Card>
                            <div>
                                <Label htmlFor="fechaHasta">Fecha Hasta</Label>
                                <Input
                                    id="fechaHasta"
                                    type="date"
                                    value={fechaHasta}
                                    onChange={(e) => {
                                        setFechaHasta(e.target.value)
                                        setErrorFecha("")
                                    }}
                                />
                            </div>
                            {errorFecha && (
                                <Card className="border-red-200 bg-red-50 p-3 dark:border-red-900 dark:bg-red-950/20">
                                    <p className="text-sm text-red-600 dark:text-red-400">{errorFecha}</p>
                                </Card>
                            )}
                            <div className="flex gap-3 mt-6">
                                <Button onClick={handleVolverPaso} variant="outline" className="flex-1 bg-transparent">
                                    ← Atrás
                                </Button>
                                <Button onClick={handleConfirmarHasta} className="flex-1 gap-2">
                                    Continuar
                                    <Calendar className="h-4 w-4" />
                                </Button>
                            </div>
                        </div>
                    </Card>
                )}

                {/* PASO 3: Grilla interactiva (CU05 integrado) */}
                {paso === "grilla" && (
                    <div className="space-y-6">
                        {loading ? (
                            <Card className="p-12 flex flex-col items-center justify-center gap-4">
                                <Loader2 className="w-12 h-12 animate-spin text-blue-600" />
                                <p className="text-lg font-semibold text-slate-700 dark:text-slate-300">Procesando datos...</p>
                            </Card>
                        ) : errorCarga ? (
                            <Card className="p-6 text-center border-red-200 bg-red-50 dark:border-red-900 dark:bg-red-950/20">
                                <p className="text-red-600 dark:text-red-400 mb-4">{errorCarga}</p>
                                <Button onClick={handleVolver} variant="outline">
                                    ← Volver a fechas
                                </Button>
                            </Card>
                        ) : (
                            <>
                                {/* Resumen de estados */}
                                <div className="grid grid-cols-3 gap-4">
                                    <Card className="border-l-4 border-green-500 p-4">
                                        <p className="text-sm text-slate-600 dark:text-slate-400">Disponibles</p>
                                        <p className="text-3xl font-bold text-green-600 dark:text-green-400">{conteo.disponibles}</p>
                                    </Card>
                                    <Card className="border-l-4 border-orange-500 p-4">
                                        <p className="text-sm text-slate-600 dark:text-slate-400">Reservadas</p>
                                        <p className="text-3xl font-bold text-orange-600 dark:text-orange-400">{conteo.reservadas}</p>
                                    </Card>
                                    <Card className="border-l-4 border-red-500 p-4">
                                        <p className="text-sm text-slate-600 dark:text-slate-400">Ocupadas</p>
                                        <p className="text-3xl font-bold text-red-600 dark:text-red-400">{conteo.ocupadas}</p>
                                    </Card>
                                </div>

                                {/* Instrucciones */}
                                <Card className="border-blue-200 bg-blue-50/50 p-4 dark:border-blue-900 dark:bg-blue-950/20">
                                    <p className="text-sm text-slate-700 dark:text-slate-300">
                                        <strong>Instrucciones:</strong> Click en una celda para iniciar selección, luego click en otra celda
                                        de la misma habitación para finalizar el rango. Puede seleccionar múltiples habitaciones con
                                        diferentes rangos.
                                    </p>
                                </Card>

                                {/* Grilla de disponibilidad */}
                                <Card className="p-6">
                                    <h2 className="text-xl font-semibold mb-4 text-slate-900 dark:text-slate-50">
                                        Grilla de disponibilidad: {createLocalDate(fechaDesde).toLocaleDateString()} al {createLocalDate(fechaHasta).toLocaleDateString()}
                                    </h2>
                                    <p className="text-slate-600 text-sm mb-4 dark:text-slate-400">
                                        Haga click en una celda para iniciar la selección, luego haga click en otra celda de la misma habitación para completar el rango.
                                    </p>

                                    <div className="overflow-x-auto">
                                        <table className="w-full border-collapse text-sm">
                                            <thead>
                                                <tr className="bg-slate-100 dark:bg-slate-800">
                                                    <th className="border border-slate-300 dark:border-slate-700 px-4 py-2 text-slate-900 dark:text-slate-50 font-semibold">Fecha</th>
                                                    {/* Agrupar por tipo de habitación */}
                                                    {TIPOS_HABITACION_ORDEN.map((tipo) => {
                                                        const habsTipo = habitaciones.filter((h: HabitacionEstado) => h.tipo === tipo)
                                                        if (habsTipo.length === 0) return null
                                                        return (
                                                            <th key={tipo} colSpan={habsTipo.length} className="border border-slate-300 dark:border-slate-700 px-2 py-2 text-center text-slate-900 dark:text-slate-50 font-bold bg-blue-50 dark:bg-blue-900/30">
                                                                {formatearTipo(tipo)}
                                                            </th>
                                                        )
                                                    })}
                                                </tr>
                                                <tr className="bg-slate-50 dark:bg-slate-800/50">
                                                    <th className="border border-slate-300 dark:border-slate-700 px-4 py-2"></th>
                                                    {/* Sub-encabezados con números de habitación */}
                                                    {TIPOS_HABITACION_ORDEN.map((tipo) => {
                                                        const habsTipo = habitaciones.filter((h: HabitacionEstado) => h.tipo === tipo).sort((a, b) => parseInt(a.numero) - parseInt(b.numero))
                                                        return habsTipo.map((hab) => (
                                                            <th key={hab.id} className="border border-slate-300 dark:border-slate-700 px-2 py-2 text-center text-slate-900 dark:text-slate-50 font-semibold min-w-[80px] text-xs">
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
                                                        className={diaIdx % 2 === 0 ? "bg-slate-50 dark:bg-slate-800/50" : "bg-white dark:bg-transparent"}
                                                    >
                                                        <td className="border border-slate-300 dark:border-slate-700 px-4 py-2 font-semibold text-slate-900 dark:text-slate-200">
                                                            <div className="text-sm">{dia.toLocaleDateString("es-AR", { weekday: "short", day: "2-digit", month: "2-digit" })}</div>
                                                        </td>
                                                        {/* Mostrar celdas por tipo de habitación */}
                                                        {TIPOS_HABITACION_ORDEN.map((tipo) => {
                                                            const habsTipo = habitaciones.filter((h: HabitacionEstado) => h.tipo === tipo).sort((a, b) => parseInt(a.numero) - parseInt(b.numero))
                                                            return habsTipo.map((hab) => {
                                                                const disponible = esCeldaDisponible(hab.id, diaIdx)
                                                                const seleccionada = esCeldaSeleccionada(hab.id, diaIdx)
                                                                const inicioActual = esInicioSeleccionActual(hab.id, diaIdx)
                                                                const estadoDia = obtenerEstadoCelda(hab.id, diaIdx)
                                                                const baseColor = getEstadoColor(estadoDia)

                                                                return (
                                                                    <td
                                                                        key={`${hab.id}-${diaIdx}`}
                                                                        className="border border-slate-300 dark:border-slate-700 px-2 py-2 text-center"
                                                                    >
                                                                        <div
                                                                            onClick={() => handleClickCelda(hab.id, diaIdx)}
                                                                            className={`rounded px-2 py-1 text-xs font-semibold text-white transition ${
                                                                                seleccionada
                                                                                    ? "bg-blue-600 hover:bg-blue-700 cursor-pointer"
                                                                                    : inicioActual
                                                                                    ? "bg-blue-400 hover:bg-blue-500 cursor-pointer animate-pulse"
                                                                                    : estadoDia === "RESERVADA" || estadoDia === "OCUPADA" || estadoDia === "FUERA_DE_SERVICIO"
                                                                                    ? `${baseColor} cursor-not-allowed opacity-75`
                                                                                    : `${baseColor} hover:brightness-110 cursor-pointer`
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
                                        <div className="mt-4 text-xs text-slate-600 dark:text-slate-400 flex gap-4 flex-wrap">
                                            <span>✓ = Seleccionada</span>
                                            <span>● = Inicio de selección (click otro día para completar)</span>
                                            <span>○ = Disponible</span>
                                            <span>R = Reservada (no se puede pisar)</span>
                                            <span>X = Ocupada</span>
                                            <span>FS = Fuera de servicio</span>
                                        </div>
                                    </div>
                                </Card>

                                {/* Resumen de selecciones */}
                                {selecciones.length > 0 && (
                                    <Card className="p-6">
                                        <h3 className="text-lg font-semibold mb-4 text-slate-900 dark:text-slate-50">
                                            Habitaciones seleccionadas
                                        </h3>
                                        <div className="space-y-3">
                                            {selecciones.map((sel, idx) => {
                                                const hab = habitaciones.find((h: HabitacionEstado) => h.id === sel.habitacionId)
                                                if (!hab) return null
                                                const noches = sel.diaFin - sel.diaInicio + 1
                                                const subtotal = hab.precioNoche * noches
                                                return (
                                                    <div
                                                        key={idx}
                                                        className="flex justify-between items-center bg-slate-50 dark:bg-slate-800 p-4 rounded-lg border border-slate-200 dark:border-slate-700"
                                                    >
                                                        <div>
                                                            <p className="font-semibold text-slate-900 dark:text-white">
                                                                Habitación {hab.numero} ({hab.tipo})
                                                            </p>
                                                            <p className="text-sm text-slate-600 dark:text-slate-400">
                                                                {diasRango[sel.diaInicio]?.toLocaleDateString()} -{" "}
                                                                {diasRango[sel.diaFin]?.toLocaleDateString()} ({noches} noche{noches > 1 ? "s" : ""})
                                                            </p>
                                                        </div>
                                                        <div className="flex items-center gap-4">
                                                            <p className="text-blue-600 dark:text-blue-400 font-semibold">${subtotal}</p>
                                                            <Button
                                                                onClick={() => handleRemoverSeleccion(idx)}
                                                                variant="ghost"
                                                                size="sm"
                                                                className="text-red-600 hover:text-red-700 hover:bg-red-50 dark:text-red-400 dark:hover:text-red-300 dark:hover:bg-red-950/20"
                                                            >
                                                                ✕ Quitar
                                                            </Button>
                                                        </div>
                                                    </div>
                                                )
                                            })}
                                        </div>
                                        <div className="mt-4 pt-4 border-t border-slate-200 dark:border-slate-700 flex justify-between items-center">
                                            <p className="text-lg font-semibold text-slate-900 dark:text-slate-50">Total:</p>
                                            <p className="text-2xl font-bold text-blue-600 dark:text-blue-400">${calcularTotal()}</p>
                                        </div>
                                    </Card>
                                )}

                                {/* Botones de acción */}
                                <div className="flex gap-3">
                                    <Button onClick={handleVolver} variant="outline" className="flex-1 bg-transparent">
                                        ← Atrás
                                    </Button>
                                    <Button onClick={handleContinuarHuesped} className="flex-1 gap-2">
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
                    <Card className="p-6 max-w-md">
                        <h2 className="text-xl font-semibold mb-4 text-slate-900 dark:text-slate-50">
                            Ingrese datos del huésped responsable
                        </h2>
                        <div className="space-y-4">
                            <div>
                                <Label htmlFor="apellido">Apellido</Label>
                                <Input
                                    id="apellido"
                                    value={datosHuesped.apellido}
                                    onChange={(e) => setDatosHuesped((prev) => ({ ...prev, apellido: e.target.value }))}
                                />
                                {erroresHuesped.apellido && (
                                    <p className="text-sm text-red-600 dark:text-red-400">{erroresHuesped.apellido}</p>
                                )}
                            </div>
                            <div>
                                <Label htmlFor="nombres">Nombres</Label>
                                <Input
                                    id="nombres"
                                    value={datosHuesped.nombres}
                                    onChange={(e) => setDatosHuesped((prev) => ({ ...prev, nombres: e.target.value }))}
                                />
                                {erroresHuesped.nombres && (
                                    <p className="text-sm text-red-600 dark:text-red-400">{erroresHuesped.nombres}</p>
                                )}
                            </div>
                            <div>
                                <Label htmlFor="telefono">Teléfono</Label>
                                <Input
                                    id="telefono"
                                    value={datosHuesped.telefono}
                                    onChange={(e) => setDatosHuesped((prev) => ({ ...prev, telefono: e.target.value }))}
                                />
                                {erroresHuesped.telefono && (
                                    <p className="text-sm text-red-600 dark:text-red-400">{erroresHuesped.telefono}</p>
                                )}
                            </div>
                            <div className="flex gap-3 mt-6">
                                <Button onClick={handleVolverPaso} variant="outline" className="flex-1 bg-transparent">
                                    ← Atrás
                                </Button>
                                <Button onClick={handleConfirmarHuesped} className="flex-1 gap-2">
                                    Continuar
                                    <UserCheck className="h-4 w-4" />
                                </Button>
                            </div>
                        </div>
                    </Card>
                )}

                {/* PASO 5: Confirmación */}
                {paso === "confirmacion" && (
                    <Card className="p-6 max-w-md">
                        <h2 className="text-xl font-semibold mb-4 text-slate-900 dark:text-slate-50">Confirmación de reserva</h2>
                        <div className="space-y-4">
                            <Card className="border-blue-200 bg-blue-50/50 p-4 dark:border-blue-900 dark:bg-blue-950/20">
                                <p className="text-sm text-slate-600 dark:text-slate-400">Fecha desde:</p>
                                <p className="text-lg font-semibold text-slate-900 dark:text-slate-50">
                                    {createLocalDate(fechaDesde).toLocaleDateString()}
                                </p>
                            </Card>
                            <Card className="border-blue-200 bg-blue-50/50 p-4 dark:border-blue-900 dark:bg-blue-950/20">
                                <p className="text-sm text-slate-600 dark:text-slate-400">Fecha hasta:</p>
                                <p className="text-lg font-semibold text-slate-900 dark:text-slate-50">
                                    {createLocalDate(fechaHasta).toLocaleDateString()}
                                </p>
                            </Card>
                            <Card className="border-blue-200 bg-blue-50/50 p-4 dark:border-blue-900 dark:bg-blue-950/20">
                                <p className="text-sm text-slate-600 dark:text-slate-400">Huésped responsable:</p>
                                <p className="text-lg font-semibold text-slate-900 dark:text-slate-50">{`${datosHuesped.nombres} ${datosHuesped.apellido}`}</p>
                            </Card>
                            <Card className="border-blue-200 bg-blue-50/50 p-4 dark:border-blue-900 dark:bg-blue-950/20">
                                <p className="text-sm text-slate-600 dark:text-slate-400">Teléfono:</p>
                                <p className="text-lg font-semibold text-slate-900 dark:text-slate-50">{datosHuesped.telefono}</p>
                            </Card>
                            <Card className="p-6">
                                <h3 className="text-lg font-semibold mb-4 text-slate-900 dark:text-slate-50">
                                    Habitaciones seleccionadas
                                </h3>
                                <div className="space-y-3">
                                    {selecciones.map((sel, idx) => {
                                        const hab = habitaciones.find((h: HabitacionEstado) => h.id === sel.habitacionId)
                                        if (!hab) return null
                                        const noches = sel.diaFin - sel.diaInicio + 1
                                        const subtotal = hab.precioNoche * noches
                                        return (
                                            <div
                                                key={idx}
                                                className="flex justify-between items-center bg-slate-50 dark:bg-slate-800 p-4 rounded-lg border border-slate-200 dark:border-slate-700"
                                            >
                                                <div>
                                                    <p className="font-semibold text-slate-900 dark:text-white">
                                                        Habitación {hab.numero} ({hab.tipo})
                                                    </p>
                                                    <p className="text-sm text-slate-600 dark:text-slate-400">
                                                        {diasRango[sel.diaInicio]?.toLocaleDateString()} -{" "}
                                                        {diasRango[sel.diaFin]?.toLocaleDateString()} ({noches} noche{noches > 1 ? "s" : ""})
                                                    </p>
                                                </div>
                                                <div className="flex items-center gap-4">
                                                    <p className="text-blue-600 dark:text-blue-400 font-semibold">${subtotal}</p>
                                                </div>
                                            </div>
                                        )
                                    })}
                                </div>
                                <div className="mt-4 pt-4 border-t border-slate-200 dark:border-slate-700 flex justify-between items-center">
                                    <p className="text-lg font-semibold text-slate-900 dark:text-slate-50">Total:</p>
                                    <p className="text-2xl font-bold text-blue-600 dark:text-blue-400">${calcularTotal()}</p>
                                </div>
                            </Card>
                            <div className="flex gap-3 mt-6">
                                <Button onClick={handleVolverPaso} variant="outline" className="flex-1 bg-transparent" disabled={loading}>
                                    ← Atrás
                                </Button>
                                <Button onClick={handleFinalizarReserva} className="flex-1 gap-2" disabled={loading}>
                                    {loading ? (
                                        <>
                                            <Loader2 className="h-4 w-4 animate-spin" />
                                            Procesando...
                                        </>
                                    ) : (
                                        <>
                                            Finalizar
                                            <CheckCircle className="h-4 w-4" />
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