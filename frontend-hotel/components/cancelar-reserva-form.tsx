"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"

import { Checkbox } from "@/components/ui/checkbox"
import { Alert, AlertDescription } from "@/components/ui/alert"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog"
import {
    Search,
    XCircle,
    ArrowLeft,
    AlertCircle,
    CheckCircle2,
    Home,
    Trash2
} from "lucide-react"
import { buscarReservasPorHuesped, cancelarReservas } from "@/lib/api"
import { esReservaPasada, estaLinkeadaAEstadia } from "@/lib/reserva-utils"

// Tipo de dato para la grilla
interface ReservaRow {
    idReserva: number
    apellido: string
    nombre: string
    nroHabitacion: string
    tipoHabitacion: string
    fechaInicio: string
    fechaFin: string
}

export function CancelarReservaForm() {
    const router = useRouter()

    // Estados de Búsqueda
    const [apellido, setApellido] = useState("")
    const [nombre, setNombre] = useState("")
    const [hasSearched, setHasSearched] = useState(false)
    const [resultados, setResultados] = useState<ReservaRow[]>([])

    // Estados de Selección
    const [seleccionados, setSeleccionados] = useState<number[]>([])

    // Estados de UI
    const [error, setError] = useState("")
    const [showConfirmDialog, setShowConfirmDialog] = useState(false)
    const [showSuccessDialog, setShowSuccessDialog] = useState(false)
    const [isProcessing, setIsProcessing] = useState(false)
    const [fieldErrors, setFieldErrors] = useState<{apellido?: string, nombre?: string}>({})
    const REGEX_SOLO_LETRAS = /^[a-zA-ZÀ-ÿ\u00f1\u00d1\s]*$/;
    const [showExitDialog, setShowExitDialog] = useState(false)

    const validarCampo = (campo: "apellido" | "nombre", valor: string) => {
        if (!valor) {
            setFieldErrors(prev => ({ ...prev, [campo]: undefined }))
            return
        }
        if (!REGEX_SOLO_LETRAS.test(valor) || valor.length > 1) {
            setFieldErrors(prev => ({
                ...prev,
                [campo]: "Solo se permite una letra."
            }))
        } else {
            setFieldErrors(prev => ({ ...prev, [campo]: undefined }))
        }
    }

    // --- MANEJADORES ---

    const handleSearch = async (e: React.FormEvent) => {
        e.preventDefault()
        setError("")
        setHasSearched(false)
        setSeleccionados([])

        if (fieldErrors.apellido || fieldErrors.nombre) {
            return
        }

        // Permitir búsqueda solo si hay una sola letra en alguno de los campos
        if ((apellido && apellido.length !== 1) && (nombre && nombre.length !== 1)) {
            setError("Debe ingresar solo una letra en apellido o nombre.")
            return
        }
        if (!apellido && !nombre) {
            setError("Debe ingresar una letra en apellido o nombre.")
            return
        }

        try {
            const data = await buscarReservasPorHuesped(apellido, nombre)

            // Mapeamos lo que viene del back a la estructura de la tabla (CON EL FIX QUE HICIMOS ANTES)
            const filas: ReservaRow[] = data.map((d: any) => ({
                idReserva: d.idReserva || d.id,
                apellido: d.apellidoHuespedResponsable || d.apellidoHuesped || apellido,
                nombre: d.nombreHuespedResponsable || d.nombreHuesped || nombre,
                nroHabitacion: d.idHabitacion || d.nroHabitacion,
                tipoHabitacion: d.tipoHabitacion || "Estándar",
                fechaInicio: d.fechaDesde || d.fechaInicio,
                fechaFin: d.fechaHasta || d.fechaFin
            }))

            setResultados(filas)
            setHasSearched(true)

            if (filas.length === 0) {
                setError("No existen reservas para los criterios de búsqueda.")
            }

        } catch (err) {
            setError("Error de conexión al buscar reservas.")
        }
    }

    const toggleSeleccion = (id: number) => {
        setSeleccionados(prev =>
            prev.includes(id)
                ? prev.filter(x => x !== id)
                : [...prev, id]
        )
    }

    const handleCancelarClick = () => {
        if (seleccionados.length === 0) return
        setShowConfirmDialog(true)
    }

    const confirmCancellation = async () => {
        setIsProcessing(true)
        try {
            await cancelarReservas(seleccionados)
            setShowConfirmDialog(false)
            setShowSuccessDialog(true)
        } catch (err) {
            alert("Hubo un problema al cancelar las reservas.")
            setIsProcessing(false)
            setShowConfirmDialog(false)
        }
    }

    const handleFinalizar = () => {
        router.push("/")
    }

    const handleVolver = () => {
        setShowExitDialog(true)

    }

    const confirmExit = () => {
        router.push("/")
    }

    return (
        <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50 dark:from-slate-950 dark:via-slate-900 dark:to-slate-950">
            <div className="mx-auto max-w-6xl px-4 py-8 sm:px-6 lg:px-8">

                {/* --- HEADER ESTILO CU09/CU10 --- */}
                <div className="mb-8 space-y-2">
                    <div className="flex items-center justify-between">
                        <div className="flex items-center gap-3">
                            {/* Icono en caja roja para indicar Cancelación */}
                            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-red-600 text-white shadow-md">
                                <XCircle className="h-6 w-6" />
                            </div>
                            <div>
                                <p className="text-xs font-semibold uppercase tracking-wider text-red-600 dark:text-red-400">
                                    Caso de Uso 06
                                </p>
                                <h1 className="text-3xl font-bold text-slate-900 dark:text-slate-50">Cancelar Reserva</h1>
                            </div>
                        </div>
                        <Button variant="outline" className="bg-white/80 backdrop-blur-sm" onClick={handleVolver}>
                            <Home className="mr-2 h-4 w-4" />
                            Volver al Menú Principal
                        </Button>
                    </div>
                    <p className="text-slate-600 dark:text-slate-400 ml-1">
                        Busque reservas activas por apellido y seleccione las que desea dar de baja.
                    </p>
                </div>

                {/* --- CARD DE BÚSQUEDA --- */}
                <Card className="mb-6 border-slate-200 shadow-lg dark:border-slate-800 dark:bg-slate-900/50">
                    <CardHeader className="pb-4 border-b border-slate-100 dark:border-slate-800">
                        <CardTitle className="text-lg font-medium text-slate-800 dark:text-slate-200">
                            Criterios de Búsqueda
                        </CardTitle>
                    </CardHeader>
                    <CardContent className="pt-6">
                        <form onSubmit={handleSearch} className="flex flex-col gap-6 md:flex-row md:items-start">
                            {/* CAMPO APELLIDO */}
                            <div className="flex-1 space-y-2">
                                <Label htmlFor="apellido" className="text-slate-600">Apellido del Responsable *</Label>
                                <Input
                                    id="apellido"
                                    placeholder="Ej: P"
                                    value={apellido}
                                    maxLength={1}
                                    onChange={(e) => {
                                        const val = e.target.value.toUpperCase().replace(/[^A-ZÀ-ŸÑ\s]/g, "").slice(0, 1)
                                        setApellido(val)
                                        if (fieldErrors.apellido) setFieldErrors(prev => ({...prev, apellido: undefined}))
                                    }}
                                    onBlur={(e) => validarCampo("apellido", e.target.value)}
                                    className={`bg-white ${fieldErrors.apellido ? "border-red-500 ring-red-200" : "focus:ring-blue-500"}`}
                                />
                                {fieldErrors.apellido && (
                                    <p className="text-xs text-red-500 font-medium animate-pulse">
                                        {fieldErrors.apellido}
                                    </p>
                                )}
                            </div>

                            {/* CAMPO NOMBRE */}
                            <div className="flex-1 space-y-2">
                                <Label htmlFor="nombre" className="text-slate-600">Nombre (Opcional)</Label>
                                <Input
                                    id="nombre"
                                    placeholder="Ej: J"
                                    value={nombre}
                                    maxLength={1}
                                    onChange={(e) => {
                                        const val = e.target.value.toUpperCase().replace(/[^A-ZÀ-ŸÑ\s]/g, "").slice(0, 1)
                                        setNombre(val)
                                        if (fieldErrors.nombre) setFieldErrors(prev => ({...prev, nombre: undefined}))
                                    }}
                                    onBlur={(e) => validarCampo("nombre", e.target.value)}
                                    className={`bg-white ${fieldErrors.nombre ? "border-red-500 ring-red-200" : "focus:ring-blue-500"}`}
                                />
                                {fieldErrors.nombre && (
                                    <p className="text-xs text-red-500 font-medium animate-pulse">
                                        {fieldErrors.nombre}
                                    </p>
                                )}
                            </div>

                            <Button type="submit" className="mt-8 w-full md:w-auto bg-blue-600 hover:bg-blue-700 text-white shadow-md transition-all hover:shadow-lg">
                                <Search className="mr-2 h-4 w-4" />
                                Buscar Reservas
                            </Button>
                        </form>

                        {error && (
                            <Alert variant="destructive" className="mt-6 border-red-200 bg-red-50 text-red-800 dark:border-red-900 dark:bg-red-950/20 dark:text-red-300">
                                <AlertCircle className="h-4 w-4" />
                                <AlertDescription>{error}</AlertDescription>
                            </Alert>
                        )}
                    </CardContent>
                </Card>

                {/* --- CARD DE RESULTADOS --- */}
                {hasSearched && resultados.length > 0 && (
                    <Card className="border-slate-200 shadow-lg dark:border-slate-800 dark:bg-slate-900/50">
                        <CardHeader className="flex flex-row items-center justify-between border-b border-slate-100 bg-slate-50/50 pb-4 dark:border-slate-800 dark:bg-slate-800/20">
                            <div className="space-y-1">
                                <CardTitle className="text-lg font-medium text-slate-800">Resultados Encontrados</CardTitle>
                                <p className="text-sm text-slate-500">Seleccione las reservas a cancelar</p>
                            </div>
                            <div className="rounded-full bg-blue-100 px-3 py-1 text-sm font-medium text-blue-700 dark:bg-blue-900/30 dark:text-blue-300">
                                {seleccionados.length} seleccionada{seleccionados.length !== 1 && 's'}
                            </div>
                        </CardHeader>
                        <CardContent className="p-0">
                            <div className="relative w-full overflow-auto">
                                <div className="overflow-x-auto">
                                    <table className="min-w-full divide-y divide-slate-200 dark:divide-slate-800">
                                        <thead className="bg-slate-50 dark:bg-slate-900">
                                            <tr>
                                                <th className="w-[50px] text-center">Sel.</th>
                                                <th className="font-semibold text-slate-700">Apellido</th>
                                                <th className="font-semibold text-slate-700">Nombres</th>
                                                <th className="font-semibold text-slate-700">Habitación</th>
                                                <th className="font-semibold text-slate-700">Tipo</th>
                                                <th className="font-semibold text-slate-700">Desde</th>
                                                <th className="font-semibold text-slate-700">Hasta</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {resultados.map((res) => {
                                                const pasada = esReservaPasada(res);
                                                const linkeada = estaLinkeadaAEstadia(res);
                                                const deshabilitada = pasada || linkeada;
                                                return (
                                                    <tr
                                                        key={res.idReserva}
                                                        className={`transition-colors ${seleccionados.includes(res.idReserva) ? "bg-red-50 hover:bg-red-100 dark:bg-red-900/10 dark:hover:bg-red-900/20" : "hover:bg-slate-50 dark:hover:bg-slate-800/50"}`}
                                                    >
                                                        <td className="text-center">
                                                            <Checkbox
                                                                checked={seleccionados.includes(res.idReserva)}
                                                                onCheckedChange={() => !deshabilitada && toggleSeleccion(res.idReserva)}
                                                                className="data-[state=checked]:bg-red-600 data-[state=checked]:border-red-600"
                                                                disabled={deshabilitada}
                                                            />
                                                        </td>
                                                        <td className="font-medium text-slate-900">{res.apellido}</td>
                                                        <td>{res.nombre}</td>
                                                        <td>
                                                            <span className="font-mono font-bold text-slate-600">{res.nroHabitacion}</span>
                                                        </td>
                                                        <td>{res.tipoHabitacion}</td>
                                                        <td className="text-slate-500">{new Date(res.fechaInicio).toLocaleDateString()}</td>
                                                        <td className="text-slate-500">{new Date(res.fechaFin).toLocaleDateString()}</td>
                                                        {deshabilitada && (
                                                            <td className="text-xs text-red-500 font-semibold">
                                                                {pasada ? "Reserva pasada" : "Vinculada a estadía"}
                                                            </td>
                                                        )}
                                                    </tr>
                                                )
                                            })}
                                        </tbody>
                                    </table>
                                </div>
                            </div>

                            <div className="flex justify-end gap-3 p-6 bg-slate-50/50 dark:bg-slate-900/20 border-t border-slate-100">
                                <Button variant="ghost" onClick={handleVolver} className="text-slate-500 hover:text-slate-700">
                                    Cancelar Operación
                                </Button>
                                <Button
                                    className={`transition-all ${seleccionados.length > 0 ? "bg-red-600 hover:bg-red-700 shadow-md hover:shadow-lg" : "bg-slate-200 text-slate-400"}`}
                                    onClick={handleCancelarClick}
                                    disabled={seleccionados.length === 0}
                                >
                                    <Trash2 className="mr-2 h-4 w-4" />
                                    Cancelar Reservas
                                </Button>
                            </div>
                        </CardContent>
                    </Card>
                )}

                {/* --- DIALOGO DE CONFIRMACIÓN --- */}
                <Dialog open={showConfirmDialog} onOpenChange={setShowConfirmDialog}>
                    <DialogContent className="sm:max-w-[425px]">
                        <DialogHeader>
                            <DialogTitle className="flex items-center gap-2 text-red-600">
                                <AlertCircle className="h-5 w-5" />
                                Confirmar Cancelación
                            </DialogTitle>
                            <DialogDescription>
                                ¿Está seguro que desea cancelar las <strong>{seleccionados.length}</strong> reservas seleccionadas?
                            </DialogDescription>
                        </DialogHeader>

                        <div className="rounded-lg bg-red-50 p-4 border border-red-100 dark:bg-red-950/20 dark:border-red-900">
                            <p className="text-sm text-red-800 dark:text-red-300">
                                ⚠️ Esta acción liberará las habitaciones inmediatamente y cambiará el estado a <strong>CANCELADA</strong>.
                            </p>
                        </div>

                        <DialogFooter className="gap-2 sm:gap-0">
                            <Button variant="outline" onClick={() => setShowConfirmDialog(false)}>Volver</Button>
                            <Button
                                className="bg-red-600 hover:bg-red-700 text-white"
                                onClick={confirmCancellation}
                                disabled={isProcessing}
                            >
                                {isProcessing ? "Procesando..." : "Sí, Cancelar Todo"}
                            </Button>
                        </DialogFooter>
                    </DialogContent>
                </Dialog>

                {/* --- DIALOGO DE ÉXITO --- */}
                <Dialog open={showSuccessDialog} onOpenChange={setShowSuccessDialog}>
                    <DialogContent className="sm:max-w-[425px]">
                        <div className="flex flex-col items-center justify-center py-6 text-center">
                            <div className="mb-4 rounded-full bg-green-100 p-3 text-green-600 dark:bg-green-900/30 dark:text-green-400">
                                <CheckCircle2 className="h-8 w-8" />
                            </div>
                            <DialogTitle className="text-xl font-bold text-slate-900">¡Operación Exitosa!</DialogTitle>
                            <DialogDescription className="mt-2 text-center text-slate-600">
                                Las reservas han sido canceladas correctamente.
                            </DialogDescription>
                        </div>
                        <DialogFooter>
                            <Button className="w-full bg-slate-900 hover:bg-slate-800 text-white" onClick={handleFinalizar}>
                                Aceptar y Salir
                            </Button>
                        </DialogFooter>
                    </DialogContent>
                </Dialog>

                {/* --- DIALOGO DE CONFIRMACIÓN DE SALIDA --- */}
                <Dialog open={showExitDialog} onOpenChange={setShowExitDialog}>
                    <DialogContent className="sm:max-w-[400px]">
                        <DialogHeader>
                            <DialogTitle>¿Desea salir?</DialogTitle>
                            <DialogDescription>
                                Perderá los datos ingresados y los resultados de la búsqueda actual.
                            </DialogDescription>
                        </DialogHeader>
                        <DialogFooter className="gap-2 sm:gap-0">
                            <Button variant="outline" onClick={() => setShowExitDialog(false)}>
                                Quedarse
                            </Button>
                            <Button variant="default" onClick={confirmExit}>
                                Salir al Menú
                            </Button>
                        </DialogFooter>
                    </DialogContent>
                </Dialog>

            </div>
        </div>
    )
}