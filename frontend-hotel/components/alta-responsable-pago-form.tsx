"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"

// UI Components
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog"

// Icons
import {
    AlertCircle,
    CheckCircle2,
    Building2,
    MapPin,
    Phone,
    Home,
    Briefcase,
    Save,
    X,
    Loader2
} from "lucide-react"

// --- CONSTANTES Y REGEX ---
const regexNombre = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/
const regexCuit = /^\d{2}-?\d{8}-?\d{1}$/
const regexTelefono = /^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\s./0-9]*$/
const regexEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const regexCalle = /^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\s.,]+$/
const regexTexto = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/
const regexAlfanumerico = /^[a-zA-Z0-9]+$/

const MSJ_OBLIGATORIO = "Este campo es obligatorio"
const MSJ_TEXTO = "Solo se permiten letras y espacios"
const MSJ_ALFANUMERICO = "Solo se permiten letras y números"
const MSJ_NUMERICO = "Solo se permiten números válidos"
const MSJ_FORMATO_EMAIL = "Formato inválido (ej: usuario@dominio.com)"
const MSJ_FORMATO_TEL = "Formato inválido (ej: +54 342 1234567)"
const MSJ_LARGO_CORTO = "El texto ingresado es demasiado corto"
const MSJ_LARGO_EXCESIVO = "El texto supera el límite permitido"

// Datos de prueba
const EXISTING_CUITS = ["20123456789", "30876543215", "27111111113"]

export function AltaResponsablePagoForm() {
    const router = useRouter()

    // --- ESTADOS ---
    const [formData, setFormData] = useState({
        razonSocial: "",
        cuit: "",
        calle: "",
        numero: "",
        departamento: "",
        piso: "",
        codigoPostal: "",
        localidad: "",
        provincia: "",
        pais: "",
        telefono: "",
    })

    const [errors, setErrors] = useState<{ [key: string]: string }>({})

    const [showCuitWarning, setShowCuitWarning] = useState(false)
    const [showCancelDialog, setShowCancelDialog] = useState(false)
    const [showSuccess, setShowSuccess] = useState(false)
    const [loading, setLoading] = useState(false)

    // --- LÓGICA DE VALIDACIÓN (ON BLUR) ---
    const validarCampo = (nombre: string, valor: string) => {
        let error = ""

        switch (nombre) {
            case "razonSocial":
                if (!valor.trim()) error = MSJ_OBLIGATORIO
                else if (valor.length < 2) error = MSJ_LARGO_CORTO
                else if (!regexCalle.test(valor)) error = "Caracteres inválidos"
                break

            case "cuit":
                if (!valor.trim()) error = MSJ_OBLIGATORIO
                else if (!regexCuit.test(valor)) error = "Formato inválido (Ej: 20-12345678-9)"
                else {
                    const soloNumeros = valor.replace(/\D/g, "")
                    if (soloNumeros.length !== 11) error = "El CUIT debe tener 11 números"
                }
                break

            case "telefono":
                if (!valor.trim()) error = MSJ_OBLIGATORIO
                else if (!regexTelefono.test(valor.trim())) error = MSJ_FORMATO_TEL
                else if (valor.trim().length > 15) error = "El número ingresado es demasiado largo (max. 15 caracteres)"
                else if (valor.trim().length < 9) error = "El número ingresado es demasiado  corto (min. 9 caracteres)"
                break

            case "calle":
                if (!valor.trim()) error = MSJ_OBLIGATORIO
                else if (!regexCalle.test(valor)) error = "Caracteres inválidos"
                break

            case "numero":
                if (!valor.trim()) error = MSJ_OBLIGATORIO
                else if (!/^\d+$/.test(valor)) error = MSJ_NUMERICO
                break

            case "codigoPostal":
                if (!valor.trim()) error = MSJ_OBLIGATORIO
                else if (!/^\d+$/.test(valor)) error = MSJ_NUMERICO
                break

            case "localidad":
                if (!valor.trim()) error = MSJ_OBLIGATORIO
                else if (!regexCalle.test(valor)) error = "Caracteres inválidos"
                break

            case "provincia":
                if (!valor.trim()) error = MSJ_OBLIGATORIO
                else if (!regexTexto.test(valor)) error = MSJ_TEXTO
                break

            case "pais":
                if (!valor.trim()) error = MSJ_OBLIGATORIO
                else if (!regexTexto.test(valor)) error = MSJ_TEXTO
                break

            case "departamento":
                if (valor.trim() && !regexCalle.test(valor)) error = "Caracteres inválidos"
                break

            case "piso":
                if (valor.trim() && !regexCalle.test(valor)) error = "Caracteres inválidos"
                break
        }

        setErrors((prev) => {
            const newErrors = { ...prev }
            if (error) newErrors[nombre] = error
            else delete newErrors[nombre]
            return newErrors
        })

        return error
    }

    // --- MANEJADORES ---

    const handleBlur = (e: React.FocusEvent<HTMLInputElement>) => {
        const { id, value } = e.target
        validarCampo(id, value)
    }

    const handleInputChange = (field: string, value: string) => {
        if (field === "cuit" && value.length > 13) return

        setFormData((prev) => ({ ...prev, [field]: value }))

        if (errors[field]) {
            setErrors((prev) => {
                const newErrors = { ...prev }
                delete newErrors[field]
                return newErrors
            })
        }
    }

    const validateAll = () => {
        let isValid = true
        Object.keys(formData).forEach((key) => {
            const error = validarCampo(key, formData[key as keyof typeof formData])
            if (error) isValid = false
        })
        return isValid
    }

    const handleSubmit = () => {
        const isFormValid = validateAll()

        if (!isFormValid) {
            window.scrollTo({ top: 0, behavior: 'smooth' })
            return
        }

        const cleanCuitInput = formData.cuit.replace(/\D/g, "")
        if (EXISTING_CUITS.includes(cleanCuitInput)) {
            setShowCuitWarning(true)
            return
        }

        confirmRegistration()
    }

    const confirmRegistration = () => {
        setLoading(true)
        setShowCuitWarning(false)
        setTimeout(() => {
            setLoading(false)
            setShowSuccess(true)
        }, 1500)
    }

    const handleCancel = () => {
        const hasData = Object.values(formData).some((value) => value.trim() !== "")
        if (hasData) setShowCancelDialog(true)
        else router.push("/")
    }

    const confirmCancel = () => {
        setShowCancelDialog(false)
        router.push("/")
    }

    // --- RENDERIZADO ---

    return (
        <div className="min-h-screen bg-gradient-to-br from-slate-50 via-orange-50 to-amber-50 dark:from-slate-950 dark:via-slate-900 dark:to-slate-950">
            <div className="mx-auto max-w-4xl px-4 py-8 sm:px-6 lg:px-8">

                {/* --- HEADER --- */}
                <div className="mb-8 space-y-2">
                    <div className="flex items-center justify-between">
                        <div className="flex items-center gap-3">
                            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-orange-600 text-white shadow-md">
                                <Briefcase className="h-6 w-6" />
                            </div>
                            <div>
                                <p className="text-xs font-semibold uppercase tracking-wider text-orange-600 dark:text-orange-400">
                                    Caso de Uso 12
                                </p>
                                <h1 className="text-3xl font-bold text-slate-900 dark:text-slate-50">Alta Responsable de Pago</h1>
                            </div>
                        </div>
                        <Button
                            variant="outline"
                            className="bg-white/80 backdrop-blur-sm gap-2 hover:bg-slate-100"
                            onClick={handleCancel}
                        >
                            <Home className="h-4 w-4" />
                            Volver al Menú Principal
                        </Button>
                    </div>
                    <p className="text-slate-600 dark:text-slate-400 ml-1">
                        Registre una nueva Persona Jurídica (Empresa) para facturación.
                    </p>
                </div>

                {/* --- FORMULARIO --- */}
                <Card className="border-orange-100 shadow-xl bg-white/80 backdrop-blur-sm">
                    <CardHeader className="bg-orange-50/50 border-b border-orange-100 pb-4">
                        <CardTitle className="text-lg font-medium text-slate-800 flex items-center gap-2">
                            <Building2 className="h-5 w-5 text-orange-600"/> Datos de la Empresa
                        </CardTitle>
                        <CardDescription>Complete la información fiscal y de contacto.</CardDescription>
                    </CardHeader>

                    <CardContent className="space-y-8 pt-8 px-8">

                        <div className="flex justify-end">
                            <p className="text-sm text-slate-500 dark:text-slate-400">
                                (*) Campos obligatorios
                            </p>
                        </div>

                        {/* SECCIÓN 1: DATOS PRINCIPALES */}
                        <div className="grid gap-6 sm:grid-cols-2">
                            <div className="space-y-2">
                                <Label htmlFor="razonSocial" className={errors.razonSocial ? "text-red-500" : "text-slate-600"}>
                                    Razón Social <span className="text-black dark:text-white">*</span>
                                </Label>
                                <div className="relative">
                                    <Building2 className="absolute left-3 top-2.5 h-4 w-4 text-slate-400" />
                                    <Input
                                        id="razonSocial"
                                        value={formData.razonSocial}
                                        onChange={(e) => handleInputChange("razonSocial", e.target.value)}
                                        onBlur={handleBlur}
                                        placeholder="Ej: Empresa S.A."
                                        className={`pl-10 bg-white focus:ring-orange-500 ${errors.razonSocial ? "border-red-500 focus-visible:ring-red-500" : "border-slate-200"}`}
                                    />
                                </div>
                                {errors.razonSocial && <p className="text-xs text-red-500 mt-1">{errors.razonSocial}</p>}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="cuit" className={errors.cuit ? "text-red-500" : "text-slate-600"}>
                                    CUIT <span className="text-black dark:text-white">*</span>
                                </Label>
                                <Input
                                    id="cuit"
                                    value={formData.cuit}
                                    onChange={(e) => handleInputChange("cuit", e.target.value)}
                                    onBlur={handleBlur}
                                    placeholder="Ej: 30-12345678-9"
                                    maxLength={13}
                                    className={`bg-white focus:ring-orange-500 font-mono ${errors.cuit ? "border-red-500 focus-visible:ring-red-500" : "border-slate-200"}`}
                                />
                                {errors.cuit && <p className="text-xs text-red-500 mt-1">{errors.cuit}</p>}
                            </div>
                        </div>

                        <div className="border-t border-slate-100 my-4"></div>

                        {/* SECCIÓN 2: DIRECCIÓN */}
                        <div className="space-y-6">
                            <div className="flex items-center gap-2 text-slate-800 font-semibold">
                                <div className="p-1.5 bg-orange-100 rounded-md text-orange-600">
                                    <MapPin className="h-4 w-4" />
                                </div>
                                Domicilio Fiscal
                            </div>

                            <div className="grid gap-5 sm:grid-cols-6 bg-slate-50 p-6 rounded-xl border border-slate-100">
                                <div className="space-y-2 sm:col-span-4">
                                    <Label htmlFor="calle" className={errors.calle ? "text-red-500" : ""}>
                                        Calle <span className="text-black dark:text-white">*</span>
                                    </Label>
                                    <Input
                                        id="calle"
                                        value={formData.calle}
                                        onChange={(e) => handleInputChange("calle", e.target.value)}
                                        onBlur={handleBlur}
                                        placeholder="Ej: Av. San Martín"
                                        className={`bg-white ${errors.calle ? "border-red-500 focus-visible:ring-red-500" : ""}`}
                                    />
                                    {errors.calle && <p className="text-xs text-red-500 mt-1">{errors.calle}</p>}
                                </div>

                                <div className="space-y-2 sm:col-span-2">
                                    <Label htmlFor="numero" className={errors.numero ? "text-red-500" : ""}>
                                        Número <span className="text-black dark:text-white">*</span>
                                    </Label>
                                    <Input
                                        id="numero"
                                        value={formData.numero}
                                        onChange={(e) => handleInputChange("numero", e.target.value)}
                                        onBlur={handleBlur}
                                        placeholder="Ej: 1234"
                                        className={`bg-white ${errors.numero ? "border-red-500 focus-visible:ring-red-500" : ""}`}
                                    />
                                    {errors.numero && <p className="text-xs text-red-500 mt-1">{errors.numero}</p>}
                                </div>

                                <div className="space-y-2 sm:col-span-2">
                                    <Label htmlFor="piso" className={errors.piso ? "text-red-500" : ""}>Piso</Label>
                                    <Input
                                        id="piso"
                                        value={formData.piso}
                                        onChange={(e) => handleInputChange("piso", e.target.value)}
                                        onBlur={handleBlur}
                                        placeholder="Ej: 5"
                                        className={`bg-white ${errors.piso ? "border-red-500 focus-visible:ring-red-500" : ""}`}
                                    />
                                    {errors.piso && <p className="text-xs text-red-500 mt-1">{errors.piso}</p>}
                                </div>

                                <div className="space-y-2 sm:col-span-2">
                                    <Label htmlFor="departamento" className={errors.departamento ? "text-red-500" : ""}>Depto</Label>
                                    <Input
                                        id="departamento"
                                        value={formData.departamento}
                                        onChange={(e) => handleInputChange("departamento", e.target.value)}
                                        onBlur={handleBlur}
                                        placeholder="Ej: A"
                                        className={`bg-white ${errors.departamento ? "border-red-500 focus-visible:ring-red-500" : ""}`}
                                    />
                                    {errors.departamento && <p className="text-xs text-red-500 mt-1">{errors.departamento}</p>}
                                </div>

                                <div className="space-y-2 sm:col-span-2">
                                    <Label htmlFor="codigoPostal" className={errors.codigoPostal ? "text-red-500" : ""}>
                                        CP <span className="text-black dark:text-white">*</span>
                                    </Label>
                                    <Input
                                        id="codigoPostal"
                                        value={formData.codigoPostal}
                                        onChange={(e) => handleInputChange("codigoPostal", e.target.value)}
                                        onBlur={handleBlur}
                                        placeholder="Ej: 3000"
                                        className={`bg-white ${errors.codigoPostal ? "border-red-500 focus-visible:ring-red-500" : ""}`}
                                    />
                                    {errors.codigoPostal && <p className="text-xs text-red-500 mt-1">{errors.codigoPostal}</p>}
                                </div>

                                <div className="space-y-2 sm:col-span-2">
                                    <Label htmlFor="localidad" className={errors.localidad ? "text-red-500" : ""}>
                                        Localidad <span className="text-black dark:text-white">*</span>
                                    </Label>
                                    <Input
                                        id="localidad"
                                        value={formData.localidad}
                                        onChange={(e) => handleInputChange("localidad", e.target.value)}
                                        onBlur={handleBlur}
                                        placeholder="Ej: Santa Fe"
                                        className={`bg-white ${errors.localidad ? "border-red-500 focus-visible:ring-red-500" : ""}`}
                                    />
                                    {errors.localidad && <p className="text-xs text-red-500 mt-1">{errors.localidad}</p>}
                                </div>

                                <div className="space-y-2 sm:col-span-2">
                                    <Label htmlFor="provincia" className={errors.provincia ? "text-red-500" : ""}>
                                        Provincia <span className="text-black dark:text-white">*</span>
                                    </Label>
                                    <Input
                                        id="provincia"
                                        value={formData.provincia}
                                        onChange={(e) => handleInputChange("provincia", e.target.value)}
                                        onBlur={handleBlur}
                                        placeholder="Ej: Santa Fe"
                                        className={`bg-white ${errors.provincia ? "border-red-500 focus-visible:ring-red-500" : ""}`}
                                    />
                                    {errors.provincia && <p className="text-xs text-red-500 mt-1">{errors.provincia}</p>}
                                </div>

                                <div className="space-y-2 sm:col-span-2">
                                    <Label htmlFor="pais" className={errors.pais ? "text-red-500" : ""}>
                                        País <span className="text-black dark:text-white">*</span>
                                    </Label>
                                    <Input
                                        id="pais"
                                        value={formData.pais}
                                        onChange={(e) => handleInputChange("pais", e.target.value)}
                                        onBlur={handleBlur}
                                        placeholder="Ej: Argentina"
                                        className={`bg-white ${errors.pais ? "border-red-500 focus-visible:ring-red-500" : ""}`}
                                    />
                                    {errors.pais && <p className="text-xs text-red-500 mt-1">{errors.pais}</p>}
                                </div>
                            </div>
                        </div>

                        {/* SECCIÓN 3: CONTACTO */}
                        <div className="space-y-2">
                            <Label htmlFor="telefono" className={errors.telefono ? "text-red-500" : "text-slate-600"}>
                                Teléfono de Contacto <span className="text-black dark:text-white">*</span>
                            </Label>
                            <div className="relative">
                                <Phone className="absolute left-3 top-2.5 h-4 w-4 text-slate-400" />
                                <Input
                                    id="telefono"
                                    value={formData.telefono}
                                    onChange={(e) => handleInputChange("telefono", e.target.value)}
                                    onBlur={handleBlur}
                                    placeholder="Ej: +54 342 1234567"
                                    className={`pl-10 bg-white ${errors.telefono ? "border-red-500 focus-visible:ring-red-500" : ""}`}
                                />
                            </div>
                            {errors.telefono && <p className="text-xs text-red-500 mt-1">{errors.telefono}</p>}
                        </div>

                        {/* BOTONES */}
                        <div className="pt-6 flex flex-col gap-3 sm:flex-row sm:justify-end border-t border-slate-100 mt-4">
                            <Button
                                type="button"
                                variant="ghost"
                                onClick={handleCancel}
                                className="w-full sm:w-auto text-slate-500 hover:text-red-600 hover:bg-red-50"
                                disabled={loading}
                            >
                                <X className="mr-2 h-4 w-4" /> Cancelar Operación
                            </Button>
                            <Button
                                type="button"
                                onClick={handleSubmit}
                                className="w-full sm:w-auto bg-orange-600 hover:bg-orange-700 text-white shadow-md min-w-[150px]"
                                disabled={loading}
                            >
                                {loading ? <Loader2 className="mr-2 h-4 w-4 animate-spin"/> : <Save className="mr-2 h-4 w-4" />}
                                {loading ? "Guardando..." : "Registrar Empresa"}
                            </Button>
                        </div>
                    </CardContent>
                </Card>
            </div>

            {/* --- DIALOGOS --- */}
            <Dialog open={showCuitWarning} onOpenChange={setShowCuitWarning}>
                <DialogContent className="border-amber-200">
                    <DialogHeader>
                        <DialogTitle className="flex items-center gap-2 text-amber-700">
                            <AlertCircle className="h-6 w-6" />
                            Atención: CUIT Existente
                        </DialogTitle>
                        <DialogDescription className="pt-4 text-base text-slate-700">
                            ¡CUIDADO! El CUIT <strong>{formData.cuit}</strong> ya existe en el sistema.
                            <br/><br/>
                            ¿Está seguro de que desea registrarlo nuevamente? Esto podría duplicar información.
                        </DialogDescription>
                    </DialogHeader>
                    <DialogFooter className="flex gap-2 sm:gap-2">
                        <Button variant="outline" onClick={() => setShowCuitWarning(false)} className="flex-1 border-slate-300">
                            No, corregir
                        </Button>
                        <Button onClick={confirmRegistration} className="flex-1 bg-amber-600 hover:bg-amber-700 text-white">
                            Sí, registrar igual
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>

            <Dialog open={showCancelDialog} onOpenChange={setShowCancelDialog}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>¿Desea salir sin guardar?</DialogTitle>
                        <DialogDescription>
                            Perderá todos los datos ingresados.
                        </DialogDescription>
                    </DialogHeader>
                    <DialogFooter className="flex gap-2 sm:gap-2">
                        <Button variant="outline" onClick={() => setShowCancelDialog(false)} className="flex-1">
                            Seguir editando
                        </Button>
                        <Button onClick={confirmCancel} variant="destructive" className="flex-1">
                            Salir
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>

            {showSuccess && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm animate-in fade-in duration-300">
                    <Card className="mx-4 w-full max-w-md border-green-200 bg-white shadow-2xl animate-in zoom-in-95 duration-300">
                        <CardContent className="flex flex-col items-center gap-6 pt-10 pb-10 text-center">
                            <div className="flex h-20 w-20 items-center justify-center rounded-full bg-green-100 shadow-inner">
                                <CheckCircle2 className="h-10 w-10 text-green-600" />
                            </div>
                            <div className="space-y-2 px-4">
                                <h3 className="text-xl font-bold text-slate-900">¡Registro Exitoso!</h3>
                                <p className="text-slate-600">
                                    La firma <span className="font-semibold text-green-700">{formData.razonSocial}</span> ha sido cargada correctamente.
                                </p>
                            </div>
                            <Button onClick={() => router.push("/")} className="mt-2 bg-green-600 hover:bg-green-700 text-white min-w-[120px]">
                                Aceptar
                            </Button>
                        </CardContent>
                    </Card>
                </div>
            )}
        </div>
    )
}