"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Alert, AlertDescription } from "@/components/ui/alert"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog"
import { AlertCircle, CheckCircle2 } from "lucide-react"

// Datos de prueba - CUITs existentes en el sistema
const EXISTING_CUITS = ["20-12345678-9", "30-87654321-5", "27-11111111-3"]

export function AltaResponsablePagoForm() {
    const router = useRouter()
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

    const [errors, setErrors] = useState<string[]>([])
    const [showCuitWarning, setShowCuitWarning] = useState(false)
    const [showCancelDialog, setShowCancelDialog] = useState(false)
    const [showSuccess, setShowSuccess] = useState(false)
    const [loading, setLoading] = useState(false)

    const handleInputChange = (field: string, value: string) => {
        // Convertir a mayúsculas los campos de texto literal
        const upperValue = value.toUpperCase()
        setFormData((prev) => ({ ...prev, [field]: upperValue }))
        // Limpiar errores cuando el usuario empiece a escribir
        if (errors.length > 0) {
            setErrors([])
        }
    }

    const validateForm = () => {
        const missingFields: string[] = []

        if (!formData.razonSocial.trim()) missingFields.push("Razón Social")
        if (!formData.cuit.trim()) missingFields.push("CUIT")
        if (!formData.calle.trim()) missingFields.push("Calle")
        if (!formData.numero.trim()) missingFields.push("Número")
        if (!formData.codigoPostal.trim()) missingFields.push("Código Postal")
        if (!formData.localidad.trim()) missingFields.push("Localidad")
        if (!formData.provincia.trim()) missingFields.push("Provincia")
        if (!formData.pais.trim()) missingFields.push("País")
        if (!formData.telefono.trim()) missingFields.push("Teléfono")

        return missingFields
    }

    const handleSubmit = () => {
        // Validar campos obligatorios
        const missingFields = validateForm()

        if (missingFields.length > 0) {
            setErrors(missingFields)
            return
        }

        // Verificar si el CUIT ya existe
        if (EXISTING_CUITS.includes(formData.cuit)) {
            setShowCuitWarning(true)
            return
        }

        // Si todo está correcto, proceder a registrar
        confirmRegistration()
    }

    const confirmRegistration = () => {
        setLoading(true)
        setShowCuitWarning(false)

        // Simular llamada a backend
        setTimeout(() => {
            setLoading(false)
            setShowSuccess(true)

            // Ocultar mensaje de éxito después de 2 segundos y volver al inicio
            setTimeout(() => {
                router.push("/")
            }, 2000)
        }, 500)
    }

    const handleCancel = () => {
        // Si hay datos ingresados, mostrar confirmación
        const hasData = Object.values(formData).some((value) => value.trim() !== "")

        if (hasData) {
            setShowCancelDialog(true)
        } else {
            router.push("/")
        }
    }

    const confirmCancel = () => {
        setShowCancelDialog(false)
        router.push("/")
    }

    return (
        <>
            <Card className="border-emerald-200 dark:border-emerald-900">
                <CardHeader className="bg-emerald-50 dark:bg-emerald-950/20">
                    <CardTitle className="flex items-center gap-2 text-emerald-900 dark:text-emerald-100">
                        Datos del Responsable de Pago
                    </CardTitle>
                </CardHeader>
                <CardContent className="space-y-6 pt-6">
                    {/* Mostrar errores de validación */}
                    {errors.length > 0 && (
                        <Alert variant="destructive">
                            <AlertCircle className="h-4 w-4" />
                            <AlertDescription>
                                <div className="font-semibold">Los siguientes campos son obligatorios:</div>
                                <ul className="mt-2 list-inside list-disc space-y-1">
                                    {errors.map((field, index) => (
                                        <li key={index}>{field}</li>
                                    ))}
                                </ul>
                            </AlertDescription>
                        </Alert>
                    )}

                    {/* Razón Social y CUIT */}
                    <div className="grid gap-4 sm:grid-cols-2">
                        <div className="space-y-2">
                            <Label htmlFor="razonSocial" className="text-slate-700 dark:text-slate-300">
                                Razón Social <span className="text-red-500">*</span>
                            </Label>
                            <Input
                                id="razonSocial"
                                value={formData.razonSocial}
                                onChange={(e) => handleInputChange("razonSocial", e.target.value)}
                                placeholder="Ingrese la razón social"
                                className="uppercase"
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="cuit" className="text-slate-700 dark:text-slate-300">
                                CUIT <span className="text-red-500">*</span>
                            </Label>
                            <Input
                                id="cuit"
                                value={formData.cuit}
                                onChange={(e) => handleInputChange("cuit", e.target.value)}
                                placeholder="XX-XXXXXXXX-X"
                                maxLength={13}
                            />
                        </div>
                    </div>

                    {/* Dirección */}
                    <div className="space-y-4 rounded-lg border border-slate-200 p-4 dark:border-slate-700">
                        <h3 className="font-semibold text-slate-900 dark:text-slate-100">Dirección</h3>

                        <div className="grid gap-4 sm:grid-cols-3">
                            <div className="space-y-2 sm:col-span-2">
                                <Label htmlFor="calle" className="text-slate-700 dark:text-slate-300">
                                    Calle <span className="text-red-500">*</span>
                                </Label>
                                <Input
                                    id="calle"
                                    value={formData.calle}
                                    onChange={(e) => handleInputChange("calle", e.target.value)}
                                    placeholder="Nombre de la calle"
                                    className="uppercase"
                                />
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="numero" className="text-slate-700 dark:text-slate-300">
                                    Número <span className="text-red-500">*</span>
                                </Label>
                                <Input
                                    id="numero"
                                    value={formData.numero}
                                    onChange={(e) => handleInputChange("numero", e.target.value)}
                                    placeholder="1234"
                                />
                            </div>
                        </div>

                        <div className="grid gap-4 sm:grid-cols-2">
                            <div className="space-y-2">
                                <Label htmlFor="piso" className="text-slate-700 dark:text-slate-300">
                                    Piso
                                </Label>
                                <Input
                                    id="piso"
                                    value={formData.piso}
                                    onChange={(e) => handleInputChange("piso", e.target.value)}
                                    placeholder="Ej: 3"
                                />
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="departamento" className="text-slate-700 dark:text-slate-300">
                                    Departamento
                                </Label>
                                <Input
                                    id="departamento"
                                    value={formData.departamento}
                                    onChange={(e) => handleInputChange("departamento", e.target.value)}
                                    placeholder="Ej: A"
                                    className="uppercase"
                                />
                            </div>
                        </div>

                        <div className="grid gap-4 sm:grid-cols-3">
                            <div className="space-y-2">
                                <Label htmlFor="codigoPostal" className="text-slate-700 dark:text-slate-300">
                                    Código Postal <span className="text-red-500">*</span>
                                </Label>
                                <Input
                                    id="codigoPostal"
                                    value={formData.codigoPostal}
                                    onChange={(e) => handleInputChange("codigoPostal", e.target.value)}
                                    placeholder="XXXX"
                                />
                            </div>

                            <div className="space-y-2 sm:col-span-2">
                                <Label htmlFor="localidad" className="text-slate-700 dark:text-slate-300">
                                    Localidad <span className="text-red-500">*</span>
                                </Label>
                                <Input
                                    id="localidad"
                                    value={formData.localidad}
                                    onChange={(e) => handleInputChange("localidad", e.target.value)}
                                    placeholder="Ciudad"
                                    className="uppercase"
                                />
                            </div>
                        </div>

                        <div className="grid gap-4 sm:grid-cols-2">
                            <div className="space-y-2">
                                <Label htmlFor="provincia" className="text-slate-700 dark:text-slate-300">
                                    Provincia <span className="text-red-500">*</span>
                                </Label>
                                <Input
                                    id="provincia"
                                    value={formData.provincia}
                                    onChange={(e) => handleInputChange("provincia", e.target.value)}
                                    placeholder="Provincia"
                                    className="uppercase"
                                />
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="pais" className="text-slate-700 dark:text-slate-300">
                                    País <span className="text-red-500">*</span>
                                </Label>
                                <Input
                                    id="pais"
                                    value={formData.pais}
                                    onChange={(e) => handleInputChange("pais", e.target.value)}
                                    placeholder="País"
                                    className="uppercase"
                                />
                            </div>
                        </div>
                    </div>

                    {/* Teléfono */}
                    <div className="space-y-2">
                        <Label htmlFor="telefono" className="text-slate-700 dark:text-slate-300">
                            Teléfono <span className="text-red-500">*</span>
                        </Label>
                        <Input
                            id="telefono"
                            value={formData.telefono}
                            onChange={(e) => handleInputChange("telefono", e.target.value)}
                            placeholder="+54 11 1234-5678"
                        />
                    </div>

                    {/* Botones */}
                    <div className="flex flex-col gap-3 sm:flex-row sm:justify-end">
                        <Button
                            type="button"
                            variant="outline"
                            onClick={handleCancel}
                            className="w-full sm:w-auto bg-transparent"
                            disabled={loading}
                        >
                            CANCELAR
                        </Button>
                        <Button type="button" onClick={handleSubmit} className="w-full sm:w-auto" disabled={loading}>
                            {loading ? "PROCESANDO..." : "SIGUIENTE"}
                        </Button>
                    </div>
                </CardContent>
            </Card>

            {/* Dialog de advertencia CUIT duplicado */}
            <Dialog open={showCuitWarning} onOpenChange={setShowCuitWarning}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle className="flex items-center gap-2 text-amber-600">
                            <AlertCircle className="h-5 w-5" />
                            CUIT Existente
                        </DialogTitle>
                        <DialogDescription className="pt-4 text-base">
                            ¡CUIDADO! El CUIT {formData.cuit} ya existe en el sistema.
                        </DialogDescription>
                    </DialogHeader>
                    <DialogFooter className="flex gap-2 sm:gap-2">
                        <Button variant="outline" onClick={() => setShowCuitWarning(false)} className="flex-1">
                            No
                        </Button>
                        <Button onClick={confirmRegistration} className="flex-1">
                            Sí
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>

            {/* Dialog de confirmación de cancelación */}
            <Dialog open={showCancelDialog} onOpenChange={setShowCancelDialog}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>¿Desea cancelar el alta del responsable de pago?</DialogTitle>
                    </DialogHeader>
                    <DialogFooter className="flex gap-2 sm:gap-2">
                        <Button variant="outline" onClick={() => setShowCancelDialog(false)} className="flex-1">
                            No
                        </Button>
                        <Button onClick={confirmCancel} className="flex-1">
                            Sí
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>

            {/* Mensaje de éxito */}
            {showSuccess && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
                    <Card className="mx-4 w-full max-w-md border-emerald-500 bg-white shadow-2xl">
                        <CardContent className="flex flex-col items-center gap-4 pt-6">
                            <div className="flex h-16 w-16 items-center justify-center rounded-full bg-emerald-100">
                                <CheckCircle2 className="h-10 w-10 text-emerald-600" />
                            </div>
                            <div className="text-center">
                                <p className="text-lg font-semibold text-slate-900">
                                    La firma razón social ha sido satisfactoriamente cargada al sistema.
                                </p>
                            </div>
                        </CardContent>
                    </Card>
                </div>
            )}
        </>
    )
}
