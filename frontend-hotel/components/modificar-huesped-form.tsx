"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { Edit, AlertCircle, CheckCircle2, UserMinus, ArrowRight } from "lucide-react"
import { useRouter } from "next/navigation"

type Guest = {
    id: string
    apellido: string
    nombre: string
    tipoDocumento: string
    nroDocumento: string
    cuit: string
    posicionIVA: string
    fechaNacimiento: string
    direccionCalle: string
    direccionNumero: string
    direccionDepartamento: string
    direccionPiso: string
    direccionCodigoPostal: string
    direccionLocalidad: string
    direccionProvincia: string
    direccionPais: string
    telefono: string
    email: string
    ocupacion: string
    nacionalidad: string
}

export function ModificarHuespedForm() {
    const router = useRouter()
    const [guestData, setGuestData] = useState<Guest>({
        id: "123",
        apellido: "PÉREZ",
        nombre: "JUAN CARLOS",
        tipoDocumento: "DNI",
        nroDocumento: "12345678",
        cuit: "20-12345678-9",
        posicionIVA: "Consumidor Final",
        fechaNacimiento: "1985-03-15",
        direccionCalle: "AV. CORRIENTES",
        direccionNumero: "1234",
        direccionDepartamento: "A",
        direccionPiso: "5",
        direccionCodigoPostal: "1043",
        direccionLocalidad: "CAPITAL FEDERAL",
        direccionProvincia: "BUENOS AIRES",
        direccionPais: "ARGENTINA",
        telefono: "+54 9 11 1234-5678",
        email: "juan.perez@example.com",
        ocupacion: "INGENIERO",
        nacionalidad: "ARGENTINA",
    })

    const [originalData, setOriginalData] = useState<Guest>(guestData)
    const [showConfirmDialog, setShowConfirmDialog] = useState(false)
    const [showCancelDialog, setShowCancelDialog] = useState(false)
    const [showDeleteDialog, setShowDeleteDialog] = useState(false)
    const [alert, setAlert] = useState<{ type: "success" | "error" | "info"; message: string } | null>(null)
    const [errors, setErrors] = useState<Record<string, string>>({})
    const [isSaving, setIsSaving] = useState(false)

    const handleUppercaseInput = (field: keyof Guest, value: string) => {
        setGuestData((prev) => ({
            ...prev,
            [field]: value.toUpperCase(),
        }))
        // Clear error when user starts typing
        if (errors[field]) {
            setErrors((prev) => {
                const newErrors = { ...prev }
                delete newErrors[field]
                return newErrors
            })
        }
    }

    const handleInputChange = (field: keyof Guest, value: string) => {
        setGuestData((prev) => ({
            ...prev,
            [field]: value,
        }))
        // Clear error when user starts typing
        if (errors[field]) {
            setErrors((prev) => {
                const newErrors = { ...prev }
                delete newErrors[field]
                return newErrors
            })
        }
    }

    const validateDocumentNumber = (tipo: string, numero: string): boolean => {
        if (!numero.trim()) {
            setErrors((prev) => ({ ...prev, nroDocumento: "El número de documento es requerido" }))
            return false
        }

        if (tipo === "LE" || tipo === "LC" || tipo === "DNI") {
            const numberPattern = /^\d{7,8}$/
            if (!numberPattern.test(numero)) {
                setErrors((prev) => ({ ...prev, nroDocumento: "Debe contener solo números (7-8 dígitos)" }))
                return false
            }
        }

        if (tipo === "Pasaporte") {
            const passportPattern = /^[A-Za-z0-9]+$/
            if (!passportPattern.test(numero)) {
                setErrors((prev) => ({ ...prev, nroDocumento: "Debe contener solo letras y números" }))
                return false
            }
        }

        return true
    }

    const validateForm = (): boolean => {
        const newErrors: Record<string, string> = {}

        // Validate required fields
        if (!guestData.apellido.trim()) newErrors.apellido = "El apellido es requerido"
        if (!guestData.nombre.trim()) newErrors.nombre = "El nombre es requerido"
        if (!guestData.tipoDocumento) newErrors.tipoDocumento = "El tipo de documento es requerido"
        if (!guestData.nroDocumento.trim()) newErrors.nroDocumento = "El número de documento es requerido"
        if (!guestData.posicionIVA) newErrors.posicionIVA = "La posición frente al IVA es requerida"
        if (!guestData.fechaNacimiento) newErrors.fechaNacimiento = "La fecha de nacimiento es requerida"
        if (!guestData.direccionCalle.trim()) newErrors.direccionCalle = "La calle es requerida"
        if (!guestData.direccionNumero.trim()) newErrors.direccionNumero = "El número es requerido"
        if (!guestData.direccionCodigoPostal.trim()) newErrors.direccionCodigoPostal = "El código postal es requerido"
        if (!guestData.direccionLocalidad.trim()) newErrors.direccionLocalidad = "La localidad es requerida"
        if (!guestData.direccionProvincia.trim()) newErrors.direccionProvincia = "La provincia es requerida"
        if (!guestData.direccionPais.trim()) newErrors.direccionPais = "El país es requerido"
        if (!guestData.telefono.trim()) newErrors.telefono = "El teléfono es requerido"
        if (!guestData.ocupacion.trim()) newErrors.ocupacion = "La ocupación es requerida"
        if (!guestData.nacionalidad.trim()) newErrors.nacionalidad = "La nacionalidad es requerida"

        // Validate document number format
        if (guestData.tipoDocumento && guestData.nroDocumento) {
            if (!validateDocumentNumber(guestData.tipoDocumento, guestData.nroDocumento)) {
                // Error already set in validateDocumentNumber
            }
        }

        setErrors(newErrors)

        if (Object.keys(newErrors).length > 0) {
            setAlert({
                type: "error",
                message:
                    "¿Desea cancelar la modificación del huésped? Hay omisiones en las que Ud. ha incurrido, sin tapar campos ni botón.",
            })
            return false
        }

        return true
    }

    const checkDocumentExists = async (): Promise<boolean> => {
        // Simulate API call to check if document combination exists for a different guest
        return new Promise((resolve) => {
            setTimeout(() => {
                // Simulate: if document changed, check if it exists
                const documentChanged =
                    guestData.tipoDocumento !== originalData.tipoDocumento || guestData.nroDocumento !== originalData.nroDocumento

                if (documentChanged) {
                    // Simulate 30% chance that document exists
                    const exists = Math.random() > 0.7
                    resolve(exists)
                } else {
                    resolve(false)
                }
            }, 300)
        })
    }

    const handleNext = async () => {
        setAlert(null)

        // Validate all fields (Flujo Alternativo 2.A)
        if (!validateForm()) {
            return
        }

        // Check if document already exists (Flujo Alternativo 2.B)
        const documentExists = await checkDocumentExists()
        if (documentExists) {
            setAlert({
                type: "error",
                message: "¡CUIDADO! El tipo y número de documento ya existen en el sistema.",
            })
            return
        }

        // Show confirmation dialog (Flujo Principal, paso 2)
        setShowConfirmDialog(true)
    }

    const handleCancel = () => {
        setShowCancelDialog(true)
    }

    const handleDelete = () => {
        setShowDeleteDialog(true)
    }

    const confirmSave = async () => {
        setIsSaving(true)
        setShowConfirmDialog(false)

        // Simulate API call to update guest
        setTimeout(() => {
            setAlert({
                type: "success",
                message: "La operación ha culminado con éxito",
            })
            setOriginalData(guestData)
            setIsSaving(false)
        }, 800)
    }

    const confirmCancel = () => {
        setGuestData(originalData)
        setShowCancelDialog(false)
        setErrors({})
        setAlert({
            type: "info",
            message: "Los cambios han sido descartados. Los datos permanecen sin modificar.",
        })
    }

    const confirmDelete = () => {
        setShowDeleteDialog(false)
        // Navigate to delete guest page
        router.push("/baja-huesped")
    }

    return (
        <div className="mx-auto max-w-5xl space-y-6">
            <Card>
                <CardHeader>
                    <CardTitle className="flex items-center gap-2">
                        <Edit className="h-6 w-6 text-green-600" />
                        Modificar Huésped
                    </CardTitle>
                    <CardDescription>
                        Actualice los datos personales del huésped. Los campos en mayúsculas son obligatorios.
                    </CardDescription>
                </CardHeader>
                <CardContent className="space-y-6">
                    {/* Personal Information */}
                    <div className="space-y-4">
                        <h3 className="text-lg font-semibold text-foreground">Datos Personales</h3>
                        <div className="grid gap-4 sm:grid-cols-2">
                            <div className="space-y-2">
                                <Label htmlFor="apellido">
                                    Apellido <span className="text-red-500">*</span>
                                </Label>
                                <Input
                                    id="apellido"
                                    value={guestData.apellido}
                                    onChange={(e) => handleUppercaseInput("apellido", e.target.value)}
                                    placeholder="Ej: PÉREZ"
                                    className={errors.apellido ? "border-red-500" : ""}
                                />
                                {errors.apellido && (
                                    <p className="text-sm text-red-500 flex items-center gap-1">
                                        <AlertCircle className="h-3 w-3" />
                                        {errors.apellido}
                                    </p>
                                )}
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="nombre">
                                    Nombre <span className="text-red-500">*</span>
                                </Label>
                                <Input
                                    id="nombre"
                                    value={guestData.nombre}
                                    onChange={(e) => handleUppercaseInput("nombre", e.target.value)}
                                    placeholder="Ej: JUAN CARLOS"
                                    className={errors.nombre ? "border-red-500" : ""}
                                />
                                {errors.nombre && (
                                    <p className="text-sm text-red-500 flex items-center gap-1">
                                        <AlertCircle className="h-3 w-3" />
                                        {errors.nombre}
                                    </p>
                                )}
                            </div>
                        </div>
                    </div>

                    {/* Document Information */}
                    <div className="space-y-4">
                        <h3 className="text-lg font-semibold text-foreground">Documentación</h3>
                        <div className="grid gap-4 sm:grid-cols-2">
                            <div className="space-y-2">
                                <Label htmlFor="tipoDocumento">
                                    Tipo de Documento <span className="text-red-500">*</span>
                                </Label>
                                <Select
                                    value={guestData.tipoDocumento}
                                    onValueChange={(value) => handleInputChange("tipoDocumento", value)}
                                >
                                    <SelectTrigger id="tipoDocumento" className={errors.tipoDocumento ? "border-red-500" : ""}>
                                        <SelectValue />
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="DNI">DNI</SelectItem>
                                        <SelectItem value="LE">LE (Libreta de Enrolamiento)</SelectItem>
                                        <SelectItem value="LC">LC (Libreta Cívica)</SelectItem>
                                        <SelectItem value="Pasaporte">Pasaporte</SelectItem>
                                        <SelectItem value="Otro">Otro</SelectItem>
                                    </SelectContent>
                                </Select>
                                {errors.tipoDocumento && (
                                    <p className="text-sm text-red-500 flex items-center gap-1">
                                        <AlertCircle className="h-3 w-3" />
                                        {errors.tipoDocumento}
                                    </p>
                                )}
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="nroDocumento">
                                    Número de Documento <span className="text-red-500">*</span>
                                </Label>
                                <Input
                                    id="nroDocumento"
                                    value={guestData.nroDocumento}
                                    onChange={(e) => handleInputChange("nroDocumento", e.target.value)}
                                    placeholder={
                                        guestData.tipoDocumento === "LE" ||
                                        guestData.tipoDocumento === "LC" ||
                                        guestData.tipoDocumento === "DNI"
                                            ? "Ej: 12345678 (7-8 dígitos)"
                                            : guestData.tipoDocumento === "Pasaporte"
                                                ? "Ej: ABC123456"
                                                : "Ingrese el número"
                                    }
                                    className={errors.nroDocumento ? "border-red-500" : ""}
                                />
                                {errors.nroDocumento && (
                                    <p className="text-sm text-red-500 flex items-center gap-1">
                                        <AlertCircle className="h-3 w-3" />
                                        {errors.nroDocumento}
                                    </p>
                                )}
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="cuit">CUIT (opcional)</Label>
                                <Input
                                    id="cuit"
                                    value={guestData.cuit}
                                    onChange={(e) => handleInputChange("cuit", e.target.value)}
                                    placeholder="Ej: 20-12345678-9"
                                />
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="posicionIVA">
                                    Posición frente al IVA <span className="text-red-500">*</span>
                                </Label>
                                <Select
                                    value={guestData.posicionIVA}
                                    onValueChange={(value) => handleInputChange("posicionIVA", value)}
                                >
                                    <SelectTrigger id="posicionIVA" className={errors.posicionIVA ? "border-red-500" : ""}>
                                        <SelectValue />
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="Consumidor Final">Consumidor Final</SelectItem>
                                        <SelectItem value="Responsable Inscripto">Responsable Inscripto</SelectItem>
                                        <SelectItem value="Exento">Exento</SelectItem>
                                        <SelectItem value="Monotributo">Monotributo</SelectItem>
                                    </SelectContent>
                                </Select>
                                {errors.posicionIVA && (
                                    <p className="text-sm text-red-500 flex items-center gap-1">
                                        <AlertCircle className="h-3 w-3" />
                                        {errors.posicionIVA}
                                    </p>
                                )}
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="fechaNacimiento">
                                    Fecha de Nacimiento <span className="text-red-500">*</span>
                                </Label>
                                <Input
                                    id="fechaNacimiento"
                                    type="date"
                                    value={guestData.fechaNacimiento}
                                    onChange={(e) => handleInputChange("fechaNacimiento", e.target.value)}
                                    className={errors.fechaNacimiento ? "border-red-500" : ""}
                                />
                                {errors.fechaNacimiento && (
                                    <p className="text-sm text-red-500 flex items-center gap-1">
                                        <AlertCircle className="h-3 w-3" />
                                        {errors.fechaNacimiento}
                                    </p>
                                )}
                            </div>
                        </div>
                    </div>

                    {/* Address Information */}
                    <div className="space-y-4">
                        <h3 className="text-lg font-semibold text-foreground">Dirección</h3>
                        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
                            <div className="space-y-2 sm:col-span-2">
                                <Label htmlFor="direccionCalle">
                                    Calle <span className="text-red-500">*</span>
                                </Label>
                                <Input
                                    id="direccionCalle"
                                    value={guestData.direccionCalle}
                                    onChange={(e) => handleUppercaseInput("direccionCalle", e.target.value)}
                                    placeholder="Ej: AV. CORRIENTES"
                                    className={errors.direccionCalle ? "border-red-500" : ""}
                                />
                                {errors.direccionCalle && (
                                    <p className="text-sm text-red-500 flex items-center gap-1">
                                        <AlertCircle className="h-3 w-3" />
                                        {errors.direccionCalle}
                                    </p>
                                )}
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="direccionNumero">
                                    Número <span className="text-red-500">*</span>
                                </Label>
                                <Input
                                    id="direccionNumero"
                                    value={guestData.direccionNumero}
                                    onChange={(e) => handleInputChange("direccionNumero", e.target.value)}
                                    placeholder="Ej: 1234"
                                    className={errors.direccionNumero ? "border-red-500" : ""}
                                />
                                {errors.direccionNumero && (
                                    <p className="text-sm text-red-500 flex items-center gap-1">
                                        <AlertCircle className="h-3 w-3" />
                                        {errors.direccionNumero}
                                    </p>
                                )}
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="direccionPiso">Piso (opcional)</Label>
                                <Input
                                    id="direccionPiso"
                                    value={guestData.direccionPiso}
                                    onChange={(e) => handleInputChange("direccionPiso", e.target.value)}
                                    placeholder="Ej: 5"
                                />
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="direccionDepartamento">Departamento (opcional)</Label>
                                <Input
                                    id="direccionDepartamento"
                                    value={guestData.direccionDepartamento}
                                    onChange={(e) => handleUppercaseInput("direccionDepartamento", e.target.value)}
                                    placeholder="Ej: A"
                                />
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="direccionCodigoPostal">
                                    Código Postal <span className="text-red-500">*</span>
                                </Label>
                                <Input
                                    id="direccionCodigoPostal"
                                    value={guestData.direccionCodigoPostal}
                                    onChange={(e) => handleInputChange("direccionCodigoPostal", e.target.value)}
                                    placeholder="Ej: 1043"
                                    className={errors.direccionCodigoPostal ? "border-red-500" : ""}
                                />
                                {errors.direccionCodigoPostal && (
                                    <p className="text-sm text-red-500 flex items-center gap-1">
                                        <AlertCircle className="h-3 w-3" />
                                        {errors.direccionCodigoPostal}
                                    </p>
                                )}
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="direccionLocalidad">
                                    Localidad <span className="text-red-500">*</span>
                                </Label>
                                <Input
                                    id="direccionLocalidad"
                                    value={guestData.direccionLocalidad}
                                    onChange={(e) => handleUppercaseInput("direccionLocalidad", e.target.value)}
                                    placeholder="Ej: CAPITAL FEDERAL"
                                    className={errors.direccionLocalidad ? "border-red-500" : ""}
                                />
                                {errors.direccionLocalidad && (
                                    <p className="text-sm text-red-500 flex items-center gap-1">
                                        <AlertCircle className="h-3 w-3" />
                                        {errors.direccionLocalidad}
                                    </p>
                                )}
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="direccionProvincia">
                                    Provincia <span className="text-red-500">*</span>
                                </Label>
                                <Input
                                    id="direccionProvincia"
                                    value={guestData.direccionProvincia}
                                    onChange={(e) => handleUppercaseInput("direccionProvincia", e.target.value)}
                                    placeholder="Ej: BUENOS AIRES"
                                    className={errors.direccionProvincia ? "border-red-500" : ""}
                                />
                                {errors.direccionProvincia && (
                                    <p className="text-sm text-red-500 flex items-center gap-1">
                                        <AlertCircle className="h-3 w-3" />
                                        {errors.direccionProvincia}
                                    </p>
                                )}
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="direccionPais">
                                    País <span className="text-red-500">*</span>
                                </Label>
                                <Input
                                    id="direccionPais"
                                    value={guestData.direccionPais}
                                    onChange={(e) => handleUppercaseInput("direccionPais", e.target.value)}
                                    placeholder="Ej: ARGENTINA"
                                    className={errors.direccionPais ? "border-red-500" : ""}
                                />
                                {errors.direccionPais && (
                                    <p className="text-sm text-red-500 flex items-center gap-1">
                                        <AlertCircle className="h-3 w-3" />
                                        {errors.direccionPais}
                                    </p>
                                )}
                            </div>
                        </div>
                    </div>

                    {/* Contact & Additional Information */}
                    <div className="space-y-4">
                        <h3 className="text-lg font-semibold text-foreground">Contacto y Otros Datos</h3>
                        <div className="grid gap-4 sm:grid-cols-2">
                            <div className="space-y-2">
                                <Label htmlFor="telefono">
                                    Teléfono <span className="text-red-500">*</span>
                                </Label>
                                <Input
                                    id="telefono"
                                    value={guestData.telefono}
                                    onChange={(e) => handleInputChange("telefono", e.target.value)}
                                    placeholder="Ej: +54 9 11 1234-5678"
                                    className={errors.telefono ? "border-red-500" : ""}
                                />
                                {errors.telefono && (
                                    <p className="text-sm text-red-500 flex items-center gap-1">
                                        <AlertCircle className="h-3 w-3" />
                                        {errors.telefono}
                                    </p>
                                )}
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="email">Email (opcional)</Label>
                                <Input
                                    id="email"
                                    type="email"
                                    value={guestData.email}
                                    onChange={(e) => handleInputChange("email", e.target.value)}
                                    placeholder="Ej: juan.perez@example.com"
                                />
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="ocupacion">
                                    Ocupación <span className="text-red-500">*</span>
                                </Label>
                                <Input
                                    id="ocupacion"
                                    value={guestData.ocupacion}
                                    onChange={(e) => handleUppercaseInput("ocupacion", e.target.value)}
                                    placeholder="Ej: INGENIERO"
                                    className={errors.ocupacion ? "border-red-500" : ""}
                                />
                                {errors.ocupacion && (
                                    <p className="text-sm text-red-500 flex items-center gap-1">
                                        <AlertCircle className="h-3 w-3" />
                                        {errors.ocupacion}
                                    </p>
                                )}
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="nacionalidad">
                                    Nacionalidad <span className="text-red-500">*</span>
                                </Label>
                                <Input
                                    id="nacionalidad"
                                    value={guestData.nacionalidad}
                                    onChange={(e) => handleUppercaseInput("nacionalidad", e.target.value)}
                                    placeholder="Ej: ARGENTINA"
                                    className={errors.nacionalidad ? "border-red-500" : ""}
                                />
                                {errors.nacionalidad && (
                                    <p className="text-sm text-red-500 flex items-center gap-1">
                                        <AlertCircle className="h-3 w-3" />
                                        {errors.nacionalidad}
                                    </p>
                                )}
                            </div>
                        </div>
                    </div>

                    {/* Action Buttons */}
                    <div className="flex flex-col-reverse gap-3 sm:flex-row sm:justify-between pt-4 border-t">
                        <div className="flex flex-col-reverse gap-3 sm:flex-row">
                            <Button onClick={handleCancel} variant="outline" className="w-full sm:w-auto bg-transparent">
                                CANCELAR
                            </Button>
                            <Button onClick={handleDelete} variant="destructive" className="w-full sm:w-auto">
                                <UserMinus className="mr-2 h-4 w-4" />
                                BORRAR
                            </Button>
                        </div>
                        <Button onClick={handleNext} disabled={isSaving} className="w-full sm:w-auto">
                            {isSaving ? (
                                "Guardando..."
                            ) : (
                                <>
                                    SIGUIENTE
                                    <ArrowRight className="ml-2 h-4 w-4" />
                                </>
                            )}
                        </Button>
                    </div>

                    {alert && (
                        <Alert variant={alert.type === "error" ? "destructive" : "default"} className="mt-4">
                            {alert.type === "success" && <CheckCircle2 className="h-4 w-4" />}
                            {alert.type === "error" && <AlertCircle className="h-4 w-4" />}
                            {alert.type === "info" && <AlertCircle className="h-4 w-4" />}
                            <AlertDescription>{alert.message}</AlertDescription>
                        </Alert>
                    )}
                </CardContent>
            </Card>

            {/* Confirm Save Dialog */}
            <Dialog open={showConfirmDialog} onOpenChange={setShowConfirmDialog}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>Confirmar modificación</DialogTitle>
                        <DialogDescription>¿Está seguro de que desea guardar los cambios realizados al huésped?</DialogDescription>
                    </DialogHeader>
                    <div className="rounded-lg bg-blue-50 p-4 dark:bg-blue-950/20">
                        <p className="text-sm font-medium text-blue-800 dark:text-blue-300">
                            Los datos del huésped{" "}
                            <strong>
                                {guestData.nombre} {guestData.apellido}
                            </strong>{" "}
                            serán actualizados en el sistema.
                        </p>
                    </div>
                    <DialogFooter className="gap-2 sm:gap-0">
                        <Button variant="outline" onClick={() => setShowConfirmDialog(false)}>
                            Cancelar
                        </Button>
                        <Button onClick={confirmSave}>
                            <CheckCircle2 className="mr-2 h-4 w-4" />
                            Confirmar
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>

            {/* Cancel Dialog */}
            <Dialog open={showCancelDialog} onOpenChange={setShowCancelDialog}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>¿Desea cancelar la modificación del huésped?</DialogTitle>
                        <DialogDescription>Si cancela, todos los cambios realizados se perderán.</DialogDescription>
                    </DialogHeader>
                    <DialogFooter className="gap-2 sm:gap-0">
                        <Button variant="outline" onClick={() => setShowCancelDialog(false)}>
                            No, continuar editando
                        </Button>
                        <Button variant="destructive" onClick={confirmCancel}>
                            Sí, cancelar cambios
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>

            {/* Delete Dialog (Flujo de Extensión - CU13 "Dar baja de huésped") */}
            <Dialog open={showDeleteDialog} onOpenChange={setShowDeleteDialog}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>Dar de baja huésped</DialogTitle>
                        <DialogDescription>
                            Será redirigido al caso de uso "Dar de baja huésped" para eliminar permanentemente este huésped del
                            sistema.
                        </DialogDescription>
                    </DialogHeader>
                    <div className="rounded-lg bg-rose-50 p-4 dark:bg-rose-950/20">
                        <p className="text-sm font-medium text-rose-800 dark:text-rose-300">
                            ⚠️ Esta acción lo llevará a otra pantalla
                        </p>
                    </div>
                    <DialogFooter className="gap-2 sm:gap-0">
                        <Button variant="outline" onClick={() => setShowDeleteDialog(false)}>
                            Cancelar
                        </Button>
                        <Button variant="destructive" onClick={confirmDelete}>
                            <UserMinus className="mr-2 h-4 w-4" />
                            Ir a Dar de Baja
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </div>
    )
}
