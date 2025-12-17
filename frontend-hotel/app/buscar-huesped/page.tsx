"use client"

import { useState, type FormEvent, type ChangeEvent, useEffect } from "react"
import { useRouter } from "next/navigation"
import { useGuest } from "@/lib/guest-context"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Search, UserPlus, Edit, Users, Home, CheckCircle } from "lucide-react"
import { buscarHuespedes } from "@/lib/api"

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
    PASAPORTE: "Pasaporte",
    OTRO: "Otro",
}

const CONFIG_DOCUMENTOS: Record<string, { regex: RegExp; error: string; placeholder: string }> = {
    DNI: {
        regex: /^\d{7,8}$/,
        error: "El DNI debe tener 7 u 8 números",
        placeholder: "Ej: 12345678"
    },
    PASAPORTE: {
        regex: /^[A-Z0-9]{6,9}$/,
        error: "El pasaporte debe tener 6 a 9 caracteres alfanuméricos",
        placeholder: "Ej: A1234567"
    },
    LC: {
        regex: /^\d{6,8}$/,
        error: "La LC debe tener 6 a 8 números",
        placeholder: "Ej: 1234567"
    },
    LE: {
        regex: /^\d{6,8}$/,
        error: "La LE debe tener 6 a 8 números",
        placeholder: "Ej: 1234567"
    },
    OTRO: {
        regex: /^[a-zA-Z0-9]{5,20}$/,
        error: "Formato inválido (5-20 caracteres)",
        placeholder: "Nro. de Identificación"
    }
}

export default function BuscarHuesped() {
    const router = useRouter()
    const { setSelectedGuest } = useGuest()

    // Estados
    const [form, setForm] = useState<BuscarHuespedForm>(INITIAL_FORM)
    const [errors, setErrors] = useState<Partial<Record<keyof BuscarHuespedForm, string>>>({})
    const [error, setError] = useState<string>("") // Error general de API
    const [isSearching, setIsSearching] = useState(false)
    const [searchPerformed, setSearchPerformed] = useState(false)
    const [resultados, setResultados] = useState<Guest[] | null>(null)
    const [huespedSeleccionado, setHuespedSeleccionado] = useState<string | null>(null)

    // Limpieza al entrar
    useEffect(() => {
        console.log("Entrando al buscador...")
        setSelectedGuest(null) // Aseguramos que no haya nada seleccionado al entrar
    }, [setSelectedGuest])

    // --- VALIDACIONES ---
    const validateField = (name: keyof BuscarHuespedForm, value: string): string => {
        switch (name) {
            case "apellido":
            case "nombres":
                if (!value.trim()) return ""
                if (value.length > 1) return "Solo ingrese una letra inicial"
                if (!/^[a-zA-ZáéíóúÁÉÍÓÚñÑ]$/.test(value)) return "Solo puede contener una letra"
                return ""

            case "nroDocumento":
                if (!value.trim()) return ""
                if (!form.tipoDocumento) return "Seleccione tipo primero"

                const config = CONFIG_DOCUMENTOS[form.tipoDocumento]
                if (config && !config.regex.test(value)) {
                    return config.error
                }
                return ""

            default:
                return ""
        }
    }

    const handleChange = (e: ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value } = e.target
        // Convertir a mayúsculas automáticamente si son iniciales para consistencia
        const finalValue = (name === "apellido" || name === "nombres") ? value.toUpperCase() : value
        setForm((prev) => ({ ...prev, [name]: finalValue }))

        // Si se cambia el tipo de documento, limpiamos error del número para revalidar luego
        if (name === "tipoDocumento") {
            setErrors((prev) => ({ ...prev, nroDocumento: "" }))
        }
    }

    const handleBlur = (e: ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value } = e.target
        const error = validateField(name as keyof BuscarHuespedForm, value)
        setErrors((prev) => ({ ...prev, [name]: error }))
    }

    // --- MANEJO DE API ---
    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault()
        setResultados(null)
        setHuespedSeleccionado(null)
        setSearchPerformed(false)
        setError("")

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
            const criterios = {
                apellido: form.apellido,
                nombres: form.nombres,
                tipoDocumento: form.tipoDocumento || null,
                nroDocumento: form.nroDocumento
            }

            // @ts-ignore
            const data = await buscarHuespedes(criterios)

            const mapPosicionIVA = (posIva: string) => {
                const mapping: Record<string, string> = {
                    'CONSUMIDOR_FINAL': 'Consumidor Final',
                    'RESPONSABLE_INSCRIPTO': 'Responsable Inscripto',
                    'EXENTO': 'Exento',
                    'MONOTRIBUTISTA': 'Monotributo'
                }
                return mapping[posIva] || posIva
            }

            const invitadosMapeados = data.map((h: any) => ({
                id: h.nroDocumento,
                tipoDocumento: h.tipoDocumento,
                numeroDocumento: h.nroDocumento,
                apellido: h.apellido,
                nombres: h.nombres,
                cuit: h.cuit,
                posicionIVA: h.posicionIva ? mapPosicionIVA(h.posicionIva) : '',
                fechaNacimiento: h.fechaNacimiento,
                direccion: h.dtoDireccion ? `${h.dtoDireccion.calle} ${h.dtoDireccion.numero}` : "",
                direccionCalle: h.dtoDireccion?.calle || "",
                direccionNumero: h.dtoDireccion?.numero?.toString() || "",
                direccionDepartamento: h.dtoDireccion?.departamento || "",
                direccionPiso: h.dtoDireccion?.piso || "",
                direccionCodigoPostal: h.dtoDireccion?.codPostal?.toString() || "",
                direccionLocalidad: h.dtoDireccion?.localidad || "",
                direccionProvincia: h.dtoDireccion?.provincia || "",
                direccionPais: h.dtoDireccion?.pais || "",
                telefono: h.telefono && h.telefono.length > 0 ? h.telefono[0].toString() : "",
                email: h.email && h.email.length > 0 ? h.email[0] : "",
                ocupacion: h.ocupacion && h.ocupacion.length > 0 ? h.ocupacion[0] : "",
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

    // --- ACCIONES DE NAVEGACIÓN ---

    const handleDarDeAlta = () => {
        setSelectedGuest(null)
        router.push("/alta-huesped")
    }

    const handleSeleccionarHuesped = (id: string) => {
        setHuespedSeleccionado(prev => prev === id ? null : id)
    }

    const handleCancelarSeleccion = () => {
        setHuespedSeleccionado(null)
    }

    const handleSiguiente = () => {
        if (huespedSeleccionado && resultados) {
            // Caso: Modificar
            const guest = resultados.find((h) => h.id === huespedSeleccionado)
            if (guest) {
                setSelectedGuest(guest)
                router.push("/modificar-huesped")
            }
        } else {
            // Caso: Alta Nueva
            setSelectedGuest(null)
            router.push("/alta-huesped")
        }
    }

    const handleVolverMenu = () => {
        setSelectedGuest(null)
        router.push("/")
    }

    return (
        <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50 dark:from-slate-950 dark:via-slate-900 dark:to-slate-950">
            <main className="mx-auto max-w-6xl px-4 py-8 sm:px-6 lg:px-8">

                {/* --- HEADER CORREGIDO (Estilo CU09/CU06) --- */}
                <div className="mb-8 space-y-2">
                    <div className="flex items-center justify-between">
                        <div className="flex items-center gap-3">
                            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-blue-600 text-white shadow-md">
                                <Users className="h-6 w-6" />
                            </div>
                            <div>
                                <p className="text-xs font-semibold uppercase tracking-wider text-blue-600 dark:text-blue-400">
                                    Caso de Uso 02
                                </p>
                                <h1 className="text-3xl font-bold text-slate-900 dark:text-slate-50">Gestionar Huéspedes</h1>
                            </div>
                        </div>
                        <Button
                            variant="outline"
                            className="bg-white/80 backdrop-blur-sm gap-2 hover:bg-slate-100"
                            onClick={handleVolverMenu}
                        >
                            <Home className="h-4 w-4" />
                            Volver al Menú Principal
                        </Button>
                    </div>
                    <p className="text-slate-600 dark:text-slate-400 ml-1">
                        Busque, modifique o elimine huéspedes registrados en el sistema.
                    </p>
                </div>

                {/* --- CARD DE BÚSQUEDA --- */}
                <Card className="shadow-lg border-slate-200 dark:border-slate-800">
                    <CardHeader className="pb-4 border-b border-slate-100 dark:border-slate-800">
                        <CardTitle className="text-lg font-medium text-slate-800 dark:text-slate-200">
                            Criterios de Búsqueda
                        </CardTitle>
                    </CardHeader>

                    <CardContent className="pt-6">
                        <form onSubmit={handleSubmit}>
                            <div className="grid gap-6 md:grid-cols-2">
                                <div className="space-y-2">
                                    <Label htmlFor="apellido">Apellido (Inicial)</Label>
                                    <Input
                                        id="apellido"
                                        name="apellido"
                                        value={form.apellido}
                                        onChange={handleChange}
                                        onBlur={handleBlur}
                                        placeholder="Ej: G"
                                        maxLength={1}
                                        className="bg-white"
                                    />
                                    {errors.apellido && <p className="text-xs text-red-500">{errors.apellido}</p>}
                                </div>

                                <div className="space-y-2">
                                    <Label htmlFor="nombres">Nombres (Inicial)</Label>
                                    <Input
                                        id="nombres"
                                        name="nombres"
                                        value={form.nombres}
                                        onChange={handleChange}
                                        onBlur={handleBlur}
                                        placeholder="Ej: A"
                                        maxLength={1}
                                        className="bg-white"
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
                                        className="flex h-10 w-full rounded-md border border-input bg-white px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-blue-500 focus-visible:ring-offset-2"
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
                                        onBlur={handleBlur}
                                        disabled={!form.tipoDocumento}
                                        placeholder={form.tipoDocumento ? CONFIG_DOCUMENTOS[form.tipoDocumento]?.placeholder : "Seleccione tipo primero"}
                                        className="bg-white"
                                    />
                                    {errors.nroDocumento && <p className="text-xs text-red-500">{errors.nroDocumento}</p>}
                                </div>
                            </div>

                            <div className="mt-8">
                                <Button type="submit" disabled={isSearching} className="w-full md:w-auto gap-2 bg-blue-600 hover:bg-blue-700">
                                    <Search className="h-4 w-4" />
                                    {isSearching ? "Buscando..." : "Buscar Huésped"}
                                </Button>
                            </div>
                        </form>
                    </CardContent>
                </Card>

                {/* --- RESULTADOS --- */}
                {searchPerformed && resultados !== null && (
                    <Card className="mt-6 shadow-lg border-slate-200 dark:border-slate-800 animate-in fade-in slide-in-from-bottom-4 duration-500">
                        <div className="border-b p-6 bg-slate-50/50">
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
                                                p-4 cursor-pointer transition-all border
                                                ${
                                                huespedSeleccionado === h.id
                                                    ? "border-blue-600 bg-blue-50 dark:bg-blue-950/20 ring-1 ring-blue-600"
                                                    : "border-slate-200 hover:border-blue-300 dark:hover:border-blue-700"
                                            }
                                            `}
                                        >
                                            <div className="flex items-center justify-between">
                                                <div className="flex items-center gap-4">
                                                    <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-slate-100 dark:bg-slate-800 text-slate-500 font-bold">
                                                        {index + 1}
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
                                                        {h.email && <p className="text-xs text-slate-500 mt-1">📧 {h.email}</p>}
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
                                    <p className="text-sm text-slate-700 dark:text-slate-300 text-center font-medium">
                                        {huespedSeleccionado
                                            ? "✓ Huésped seleccionado. Presione 'Siguiente' para modificar sus datos."
                                            : "ℹ️ No hay huésped seleccionado. Presione 'Siguiente' para dar de alta uno nuevo."}
                                    </p>
                                </Card>

                                <div className="flex flex-col sm:flex-row gap-3 pt-2">
                                    <Button onClick={handleSiguiente} className="flex-1 gap-2 bg-blue-600 hover:bg-blue-700">
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
                                        variant="ghost"
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