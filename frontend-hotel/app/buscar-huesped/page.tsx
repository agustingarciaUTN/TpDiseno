"use client"
import { buscarHuespedes } from "@/lib/api"
import Link from "next/link"
import { useState, type FormEvent, type ChangeEvent } from "react"
import { useRouter } from "next/navigation"
import { useGuest } from "@/lib/guest-context"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Search, UserPlus, Edit, Home, CheckCircle } from "lucide-react"

type Guest = {
    id: string
    tipoDocumento: string
    numeroDocumento: string
    apellido: string
    nombres: string
    cuit: string
    posicionIVA: string
    fechaNacimiento: string
    direccion: string
    telefono: string
    email: string
    ocupacion: string
    nacionalidad: string
}

type BuscarHuespedForm = {
    apellido: string
    nombres: string
    tipoDocumento: string
    nroDocumento: string
}

const INITIAL_FORM: BuscarHuespedForm = {
    apellido: "",
    nombres: "",
    tipoDocumento: "",
    nroDocumento: "",
}

const TIPO_DOCUMENTO_LABELS: Record<string, string> = {
    DNI: "DNI",
    LE: "LE (Libreta de Enrolamiento)",
    LC: "LC (Libreta Cívica)",
    Pasaporte: "Pasaporte",
    OTROS: "OTROS",
}

export default function BuscarHuesped() {
    const router = useRouter()
    const { setSelectedGuest } = useGuest()
    const [form, setForm] = useState<BuscarHuespedForm>(INITIAL_FORM)
    const [errors, setErrors] = useState<Partial<Record<keyof BuscarHuespedForm, string>>>({})
    const [error, setError] = useState<string>("")
    const [isSearching, setIsSearching] = useState(false)
    const [searchPerformed, setSearchPerformed] = useState(false)

    const [resultados, setResultados] = useState<Guest[] | null>(null)
    const [huespedSeleccionado, setHuespedSeleccionado] = useState<string | null>(null)

    const validateField = (name: keyof BuscarHuespedForm, value: string): string => {
        switch (name) {
            case "apellido":
            case "nombres":
                if (!value.trim()) return ""
                if (value.length > 1) return "Solo ingrese una letra inicial"
                if (!/^[a-zA-ZáéíóúÁÉÍÓÚñÑ]$/.test(value)) return "Solo puede contener una letra"
                return ""

            case "nroDocumento":
                if (!form.tipoDocumento && value) return "Seleccione tipo primero"
                if (!value.trim()) return ""

                // Validation based on document type
                switch (form.tipoDocumento) {
                    case "DNI":
                    case "LE":
                    case "LC":
                        if (!/^\d{7,8}$/.test(value)) return "Debe contener 7 u 8 dígitos"
                        break
                    case "Pasaporte":
                        if (!/^[A-Za-z0-9]+$/.test(value)) return "Puede contener letras y números"
                        break
                }

                if (!/^[^\s]+$/.test(value)) return "El documento no debe contener espacios ni símbolos"
                return ""

            default:
                return ""
        }
    }

    const handleChange = (e: ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value } = e.target
        setForm((prev) => ({ ...prev, [name]: value }))
        const error = validateField(name as keyof BuscarHuespedForm, value)
        setErrors((prev) => ({ ...prev, [name]: error }))
    }

    const handleSubmit = async (e: FormEvent) => {
            e.preventDefault()
            setResultados(null)
            setHuespedSeleccionado(null)
            setSearchPerformed(false)
            setError("") // Limpiar errores previos

        const newErrors: Partial<Record<keyof BuscarHuespedForm, string>> = {}
        ;(Object.keys(form) as Array<keyof BuscarHuespedForm>).forEach((key) => {
            const error = validateField(key, String(form[key]))
            if (error) newErrors[key] = error
        })

        if (Object.keys(newErrors).length > 0) {
                    setErrors(newErrors)
                    return
                }

                setIsSearching(true)

                try {
                    // LLAMADA REAL AL BACKEND
                    // Mapeamos los nombres de campos del formulario a lo que espera el DTO de búsqueda
                    const criterios = {
                        apellido: form.apellido,
                        nombres: form.nombres,
                        tipoDocumento: form.tipoDocumento || null, // Enviar null si está vacío
                        nroDocumento: form.nroDocumento
                    }

                    // @ts-ignore - Ignoramos tipos estrictos temporalmente para facilitar la integración
                    const data = await buscarHuespedes(criterios)

                    // Adaptar respuesta del backend al formato que espera el componente (si es necesario)
                    // Tu DtoHuesped backend devuelve "nroDocumento", tu tipo Guest usa "numeroDocumento"
                    const invitadosMapeados = data.map((h: any) => ({
                        id: h.nroDocumento, // Usamos DNI como ID temporal o h.idHuesped si existe
                        tipoDocumento: h.tipoDocumento,
                        numeroDocumento: h.nroDocumento,
                        apellido: h.apellido,
                        nombres: h.nombres,
                        cuit: h.cuit,
                        posicionIVA: h.posicionIva,
                        fechaNacimiento: h.fechaNacimiento,
                        direccion: h.dtoDireccion ? `${h.dtoDireccion.calle} ${h.dtoDireccion.numero}` : "",
                        telefono: h.telefono ? h.telefono[0] : "",
                        email: h.email ? h.email[0] : "",
                        ocupacion: h.ocupacion ? h.ocupacion[0] : "",
                        nacionalidad: h.nacionalidad
                    }))

                    setResultados(invitadosMapeados)
                    setSearchPerformed(true)
                } catch (err: any) {
                    console.error(err)
                    setError("Error al conectar con el servidor: " + err.message)
                } finally {
                    setIsSearching(false)
                }
            }

    const handleDarDeAlta = () => {
        setSelectedGuest(null)
        router.push("/alta-huesped")
    }

    const handleSeleccionarHuesped = (id: string) => {
        if (huespedSeleccionado === id) {
            setHuespedSeleccionado(null)
        } else {
            setHuespedSeleccionado(id)
        }
    }

    const handleEditar = () => {
        if (huespedSeleccionado && resultados) {
            const guest = resultados.find((h) => h.id === huespedSeleccionado)
            if (guest) {
                setSelectedGuest(guest)
                router.push("/modificar-huesped")
            }
        }
    }

    const handleCancelarSeleccion = () => {
        setHuespedSeleccionado(null)
    }

    const handleSiguiente = () => {
        if (huespedSeleccionado && resultados) {
            // If a guest is selected, go to modify
            const guest = resultados.find((h) => h.id === huespedSeleccionado)
            if (guest) {
                setSelectedGuest(guest)
                router.push("/modificar-huesped")
            }
        } else {
            // If no guest is selected, go to register new guest
            setSelectedGuest(null)
            router.push("/alta-huesped")
        }
    }

    return (
        <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50 dark:from-slate-950 dark:via-slate-900 dark:to-slate-950">
            <main className="mx-auto max-w-6xl px-4 py-12 sm:px-6 lg:px-8">
                <div className="mb-8">
                    <div className="mb-6 flex items-center gap-4">
                        <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-gradient-to-br from-blue-600 to-indigo-600 text-white shadow-lg">
                            <Search className="h-6 w-6" />
                        </div>
                        <div>
                            <p className="text-xs font-semibold uppercase tracking-wider text-blue-600 dark:text-blue-400">CU02</p>
                            <h1 className="text-3xl font-bold text-slate-900 dark:text-slate-50">Buscar Huésped</h1>
                        </div>
                    </div>
                    <p className="text-slate-600 dark:text-slate-400">
                        Gestionar datos personales de los huéspedes del sistema hotelero.
                    </p>
                </div>

                <Card className="p-6">
                    <h2 className="mb-4 text-lg font-semibold text-slate-900 dark:text-slate-50">Criterios de Búsqueda</h2>
                    <form onSubmit={handleSubmit}>
                        <div className="grid gap-4 md:grid-cols-2">
                            <div className="space-y-2">
                                <Label htmlFor="apellido">Apellido</Label>
                                <Input
                                    id="apellido"
                                    name="apellido"
                                    value={form.apellido}
                                    onChange={handleChange}
                                    placeholder="Ej: G"
                                    maxLength={1}
                                />
                                {errors.apellido && <p className="text-xs text-red-500">{errors.apellido}</p>}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="nombres">Nombres</Label>
                                <Input
                                    id="nombres"
                                    name="nombres"
                                    value={form.nombres}
                                    onChange={handleChange}
                                    placeholder="Ej: A"
                                    maxLength={1}
                                />
                                {errors.nombres && <p className="text-xs text-red-500">{errors.nombres}</p>}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="tipoDocumento">Tipo de Documento</Label>
                                <select
                                    id="tipoDocumento"
                                    name="tipoDocumento"
                                    value={form.tipoDocumento}
                                    onChange={handleChange}
                                    className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                                >
                                    <option value="">Todos</option>
                                    {Object.entries(TIPO_DOCUMENTO_LABELS).map(([value, label]) => (
                                        <option key={value} value={value}>
                                            {label}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="nroDocumento">Nro Documento</Label>
                                <Input
                                    id="nroDocumento"
                                    name="nroDocumento"
                                    value={form.nroDocumento}
                                    onChange={handleChange}
                                    disabled={!form.tipoDocumento}
                                    placeholder={form.tipoDocumento ? "Ingrese el número" : "Seleccione tipo primero"}
                                />
                                {errors.nroDocumento && <p className="text-xs text-red-500">{errors.nroDocumento}</p>}
                            </div>
                        </div>

                        <div className="mt-6 flex gap-3">
                            <Button type="submit" disabled={isSearching} className="gap-2">
                                <Search className="h-4 w-4" />
                                {isSearching ? "Buscando..." : "Buscar"}
                            </Button>
                            <Button type="button" variant="outline" asChild>
                                <Link href="/">
                                    <Home className="mr-2 h-4 w-4" />
                                    Volver al Inicio
                                </Link>
                            </Button>
                        </div>
                    </form>
                </Card>

                {searchPerformed && resultados !== null && (
                    <Card className="mt-6 animate-in fade-in slide-in-from-bottom-4 duration-500">
                        <div className="border-b p-6">
                            <h2 className="text-xl font-semibold text-slate-900 dark:text-slate-50">
                                Resultados ({resultados.length})
                            </h2>
                        </div>

                        {resultados.length === 0 ? (
                            <div className="p-8 text-center space-y-4">
                                <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-amber-100 text-amber-600 dark:bg-amber-950 dark:text-amber-400">
                                    <Search className="h-8 w-8" />
                                </div>
                                <div>
                                    <p className="text-lg font-medium text-slate-900 dark:text-slate-50">⚠️ No se encontraron huéspedes</p>
                                    <p className="text-sm text-slate-600 dark:text-slate-400 mt-1">
                                        No hay coincidencias con los criterios ingresados
                                    </p>
                                </div>
                                <div className="border-t pt-6 mt-6">
                                    <p className="text-sm text-slate-600 dark:text-slate-400 mb-4">
                                        ¿Desea dar de alta un nuevo huésped?
                                    </p>
                                    <div className="flex gap-3 justify-center">
                                        <Button onClick={handleDarDeAlta} className="gap-2">
                                            <UserPlus className="h-4 w-4" />
                                            Dar de Alta
                                        </Button>
                                        <Button
                                            variant="outline"
                                            onClick={() => {
                                                setSearchPerformed(false)
                                                setResultados(null)
                                            }}
                                        >
                                            Nueva Búsqueda
                                        </Button>
                                    </div>
                                </div>
                            </div>
                        ) : (
                            <div className="p-6 space-y-4">
                                <div className="space-y-3">
                                    {resultados.map((h, index) => (
                                        <Card
                                            key={h.id}
                                            onClick={() => handleSeleccionarHuesped(h.id)}
                                            className={`
                        p-4 cursor-pointer transition-all
                        ${
                                                huespedSeleccionado === h.id
                                                    ? "border-blue-600 bg-blue-50 dark:bg-blue-950/20"
                                                    : "hover:border-blue-300 dark:hover:border-blue-700"
                                            }
                      `}
                                        >
                                            <div className="flex items-center justify-between">
                                                <div className="flex items-center gap-4">
                                                    <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-slate-100 dark:bg-slate-800">
                            <span className="text-sm font-semibold text-slate-600 dark:text-slate-400">
                              {index + 1}
                            </span>
                                                    </div>
                                                    <div>
                                                        <p className="font-semibold text-slate-900 dark:text-slate-50">
                                                            {h.apellido}, {h.nombres}
                                                        </p>
                                                        <p className="text-sm text-slate-600 dark:text-slate-400">
                              <span className="inline-flex items-center rounded-md bg-blue-100 px-2 py-1 text-xs font-medium text-blue-700 dark:bg-blue-950 dark:text-blue-300">
                                {h.tipoDocumento}
                              </span>{" "}
                                                            {h.numeroDocumento}
                                                        </p>
                                                        {h.email && <p className="text-sm text-slate-600 dark:text-slate-400 mt-1">📧 {h.email}</p>}
                                                    </div>
                                                </div>
                                                {huespedSeleccionado === h.id && (
                                                    <CheckCircle className="h-6 w-6 text-blue-600 dark:text-blue-400" />
                                                )}
                                            </div>
                                        </Card>
                                    ))}
                                </div>

                                <Card className="border-blue-200 bg-blue-50/50 p-4 dark:border-blue-900 dark:bg-blue-950/20">
                                    <p className="text-sm text-slate-700 dark:text-slate-300 text-center">
                                        {huespedSeleccionado
                                            ? "✓ Huésped seleccionado. Presione 'Siguiente' para modificar sus datos."
                                            : "ℹ️ No hay huésped seleccionado. Presione 'Siguiente' para dar de alta un nuevo huésped."}
                                    </p>
                                </Card>

                                <div className="flex gap-3 pt-2">
                                    <Button onClick={handleSiguiente} className="flex-1 gap-2">
                                        {huespedSeleccionado ? (
                                            <>
                                                <Edit className="h-4 w-4" />
                                                Siguiente (Modificar)
                                            </>
                                        ) : (
                                            <>
                                                <UserPlus className="h-4 w-4" />
                                                Siguiente (Dar de Alta)
                                            </>
                                        )}
                                    </Button>
                                    {huespedSeleccionado && (
                                        <Button variant="outline" onClick={handleCancelarSeleccion}>
                                            Cancelar Selección
                                        </Button>
                                    )}
                                    <Button
                                        variant="outline"
                                        onClick={() => {
                                            setSearchPerformed(false)
                                            setResultados(null)
                                            setHuespedSeleccionado(null)
                                        }}
                                    >
                                        Nueva Búsqueda
                                    </Button>
                                </div>
                            </div>
                        )}
                    </Card>
                )}
            </main>
        </div>
    )
}
