"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Alert, AlertDescription } from "@/components/ui/alert"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog"
import { ArrowLeft, AlertCircle, CheckCircle2, Trash2 } from "lucide-react"
import Link from "next/link"
import { useRouter } from "next/navigation"

// Datos mock de responsables de pago existentes
const responsablesDePago = [
    {
        id: 1,
        razonSocial: "HOTEL PLAZA SA",
        cuit: "20-12345678-9",
        calle: "AV CORRIENTES",
        numero: "1500",
        piso: "3",
        departamento: "B",
        codigoPostal: "1043",
        localidad: "CABA",
        provincia: "BUENOS AIRES",
        pais: "ARGENTINA",
        telefono: "1145678901",
    },
    {
        id: 2,
        razonSocial: "EVENTOS MARTINEZ SRL",
        cuit: "27-98765432-1",
        calle: "SAN MARTIN",
        numero: "850",
        piso: "",
        departamento: "",
        codigoPostal: "2000",
        localidad: "ROSARIO",
        provincia: "SANTA FE",
        pais: "ARGENTINA",
        telefono: "3414567890",
    },
    {
        id: 3,
        razonSocial: "CATERING DELICIA SRL",
        cuit: "30-45678901-2",
        calle: "MAIPU",
        numero: "2500",
        piso: "10",
        departamento: "A",
        codigoPostal: "1636",
        localidad: "OLIVOS",
        provincia: "BUENOS AIRES",
        pais: "ARGENTINA",
        telefono: "1147891234",
    },
]

export default function ModificarResponsablePagoForm() {
    const router = useRouter()
    const [step, setStep] = useState<"select" | "edit">("select")
    const [selectedId, setSelectedId] = useState<number | null>(null)
    const [formData, setFormData] = useState({
        razonSocial: "",
        cuit: "",
        calle: "",
        numero: "",
        piso: "",
        departamento: "",
        codigoPostal: "",
        localidad: "",
        provincia: "",
        pais: "",
        telefono: "",
    })
    const [originalData, setOriginalData] = useState(formData)
    const [error, setError] = useState("")
    const [showCancelDialog, setShowCancelDialog] = useState(false)
    const [showCuitWarning, setShowCuitWarning] = useState(false)
    const [showSuccess, setShowSuccess] = useState(false)

    const handleSelectResponsable = (id: number) => {
        const responsable = responsablesDePago.find((r) => r.id === id)
        if (responsable) {
            const data = {
                razonSocial: responsable.razonSocial,
                cuit: responsable.cuit,
                calle: responsable.calle,
                numero: responsable.numero,
                piso: responsable.piso,
                departamento: responsable.departamento,
                codigoPostal: responsable.codigoPostal,
                localidad: responsable.localidad,
                provincia: responsable.provincia,
                pais: responsable.pais,
                telefono: responsable.telefono,
            }
            setFormData(data)
            setOriginalData(data)
            setSelectedId(id)
            setStep("edit")
            setError("")
        }
    }

    const handleInputChange = (field: string, value: string) => {
        // Convertir a mayúsculas
        const upperValue = value.toUpperCase()
        setFormData((prev) => ({ ...prev, [field]: upperValue }))
        setError("")
    }

    const hasChanges = () => {
        return JSON.stringify(formData) !== JSON.stringify(originalData)
    }

    const validateForm = () => {
        const emptyFields: string[] = []

        if (!formData.razonSocial.trim()) emptyFields.push("Razón social")
        if (!formData.cuit.trim()) emptyFields.push("CUIT")
        if (!formData.calle.trim()) emptyFields.push("Calle")
        if (!formData.numero.trim()) emptyFields.push("Número")
        if (!formData.codigoPostal.trim()) emptyFields.push("Código postal")
        if (!formData.localidad.trim()) emptyFields.push("Localidad")
        if (!formData.provincia.trim()) emptyFields.push("Provincia")
        if (!formData.pais.trim()) emptyFields.push("País")
        if (!formData.telefono.trim()) emptyFields.push("Teléfono")

        return emptyFields
    }

    const handleSiguiente = () => {
        // Validar campos vacíos
        const emptyFields = validateForm()
        if (emptyFields.length > 0) {
            setError(`Los siguientes campos son obligatorios: ${emptyFields.join(", ")}.`)
            return
        }

        // Verificar si el CUIT ya existe en otro responsable
        const cuitExists = responsablesDePago.some((r) => r.id !== selectedId && r.cuit === formData.cuit)
        if (cuitExists) {
            setShowCuitWarning(true)
            return
        }

        // Simular actualización exitosa
        setShowSuccess(true)
        setTimeout(() => {
            router.push("/")
        }, 2000)
    }

    const handleCancelClick = () => {
        if (hasChanges()) {
            setShowCancelDialog(true)
        } else {
            router.push("/")
        }
    }

    const handleConfirmCancel = () => {
        setShowCancelDialog(false)
        router.push("/")
    }

    const handleContinueAfterWarning = () => {
        setShowCuitWarning(false)
        // Aquí continuaría con el flujo normal, pero como es warning, solo cerramos
    }

    const handleBorrar = () => {
        // Aquí se ejecutaría el CU14 "Dar baja de Responsable de pago"
        alert("Se ejecutará el CU14 'Dar baja de Responsable de pago'")
    }

    if (showSuccess) {
        return (
            <div className="mx-auto max-w-2xl px-4 py-12">
                <Card className="border-green-200 bg-green-50 dark:border-green-900 dark:bg-green-950/20">
                    <CardContent className="pt-6">
                        <div className="flex flex-col items-center gap-4 text-center">
                            <div className="flex h-16 w-16 items-center justify-center rounded-full bg-green-100 dark:bg-green-900">
                                <CheckCircle2 className="h-8 w-8 text-green-600 dark:text-green-400" />
                            </div>
                            <h2 className="text-xl font-semibold text-green-900 dark:text-green-100">
                                LA OPERACIÓN HA CULMINADO CON ÉXITO
                            </h2>
                        </div>
                    </CardContent>
                </Card>
            </div>
        )
    }

    if (step === "select") {
        return (
            <div className="mx-auto max-w-4xl px-4 py-8">
                <div className="mb-6">
                    <Button variant="ghost" asChild>
                        <Link href="/">
                            <ArrowLeft className="mr-2 h-4 w-4" />
                            Volver al Menú Principal
                        </Link>
                    </Button>
                </div>

                <Card>
                    <CardHeader>
                        <CardTitle>Modificar Responsable de Pago</CardTitle>
                        <CardDescription>Seleccione el responsable de pago que desea modificar</CardDescription>
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-3">
                            {responsablesDePago.map((responsable) => (
                                <Card
                                    key={responsable.id}
                                    className="cursor-pointer transition-all hover:shadow-md"
                                    onClick={() => handleSelectResponsable(responsable.id)}
                                >
                                    <CardContent className="p-4">
                                        <div className="flex items-start justify-between">
                                            <div>
                                                <h3 className="font-semibold text-lg">{responsable.razonSocial}</h3>
                                                <p className="text-sm text-muted-foreground">CUIT: {responsable.cuit}</p>
                                                <p className="text-sm text-muted-foreground">
                                                    {responsable.calle} {responsable.numero}
                                                    {responsable.piso && `, Piso ${responsable.piso}`}
                                                    {responsable.departamento && ` ${responsable.departamento}`}
                                                </p>
                                                <p className="text-sm text-muted-foreground">
                                                    {responsable.localidad}, {responsable.provincia}
                                                </p>
                                            </div>
                                        </div>
                                    </CardContent>
                                </Card>
                            ))}
                        </div>
                    </CardContent>
                </Card>
            </div>
        )
    }

    return (
        <div className="mx-auto max-w-4xl px-4 py-8">
            <div className="mb-6">
                <Button variant="ghost" onClick={() => setStep("select")}>
                    <ArrowLeft className="mr-2 h-4 w-4" />
                    Volver a selección
                </Button>
            </div>

            <Card>
                <CardHeader>
                    <CardTitle>Modificar Responsable de Pago</CardTitle>
                    <CardDescription>Actualice los datos del responsable de pago</CardDescription>
                </CardHeader>
                <CardContent className="space-y-6">
                    {error && (
                        <Alert variant="destructive">
                            <AlertCircle className="h-4 w-4" />
                            <AlertDescription>{error}</AlertDescription>
                        </Alert>
                    )}

                    <div className="grid gap-4 md:grid-cols-2">
                        <div className="md:col-span-2">
                            <Label htmlFor="razonSocial">RAZÓN SOCIAL *</Label>
                            <Input
                                id="razonSocial"
                                value={formData.razonSocial}
                                onChange={(e) => handleInputChange("razonSocial", e.target.value)}
                                placeholder="INGRESE RAZÓN SOCIAL"
                            />
                        </div>

                        <div className="md:col-span-2">
                            <Label htmlFor="cuit">CUIT *</Label>
                            <Input
                                id="cuit"
                                value={formData.cuit}
                                onChange={(e) => handleInputChange("cuit", e.target.value)}
                                placeholder="XX-XXXXXXXX-X"
                            />
                        </div>

                        <div className="md:col-span-2">
                            <Label htmlFor="calle">CALLE *</Label>
                            <Input
                                id="calle"
                                value={formData.calle}
                                onChange={(e) => handleInputChange("calle", e.target.value)}
                                placeholder="INGRESE CALLE"
                            />
                        </div>

                        <div>
                            <Label htmlFor="numero">NÚMERO *</Label>
                            <Input
                                id="numero"
                                value={formData.numero}
                                onChange={(e) => handleInputChange("numero", e.target.value)}
                                placeholder="NÚMERO"
                            />
                        </div>

                        <div>
                            <Label htmlFor="piso">PISO</Label>
                            <Input
                                id="piso"
                                value={formData.piso}
                                onChange={(e) => handleInputChange("piso", e.target.value)}
                                placeholder="PISO"
                            />
                        </div>

                        <div>
                            <Label htmlFor="departamento">DEPARTAMENTO</Label>
                            <Input
                                id="departamento"
                                value={formData.departamento}
                                onChange={(e) => handleInputChange("departamento", e.target.value)}
                                placeholder="DEPTO"
                            />
                        </div>

                        <div>
                            <Label htmlFor="codigoPostal">CÓDIGO POSTAL *</Label>
                            <Input
                                id="codigoPostal"
                                value={formData.codigoPostal}
                                onChange={(e) => handleInputChange("codigoPostal", e.target.value)}
                                placeholder="CP"
                            />
                        </div>

                        <div>
                            <Label htmlFor="localidad">LOCALIDAD *</Label>
                            <Input
                                id="localidad"
                                value={formData.localidad}
                                onChange={(e) => handleInputChange("localidad", e.target.value)}
                                placeholder="LOCALIDAD"
                            />
                        </div>

                        <div>
                            <Label htmlFor="provincia">PROVINCIA *</Label>
                            <Input
                                id="provincia"
                                value={formData.provincia}
                                onChange={(e) => handleInputChange("provincia", e.target.value)}
                                placeholder="PROVINCIA"
                            />
                        </div>

                        <div>
                            <Label htmlFor="pais">PAÍS *</Label>
                            <Input
                                id="pais"
                                value={formData.pais}
                                onChange={(e) => handleInputChange("pais", e.target.value)}
                                placeholder="PAÍS"
                            />
                        </div>

                        <div>
                            <Label htmlFor="telefono">TELÉFONO *</Label>
                            <Input
                                id="telefono"
                                value={formData.telefono}
                                onChange={(e) => handleInputChange("telefono", e.target.value)}
                                placeholder="TELÉFONO"
                            />
                        </div>
                    </div>

                    <div className="flex gap-3">
                        <Button onClick={handleSiguiente} className="flex-1">
                            SIGUIENTE
                        </Button>
                        <Button onClick={handleCancelClick} variant="outline" className="flex-1 bg-transparent">
                            CANCELAR
                        </Button>
                        <Button onClick={handleBorrar} variant="destructive" className="flex-1">
                            <Trash2 className="mr-2 h-4 w-4" />
                            BORRAR
                        </Button>
                    </div>
                </CardContent>
            </Card>

            {/* Diálogo de cancelación */}
            <Dialog open={showCancelDialog} onOpenChange={setShowCancelDialog}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>¿Desea cancelar la modificación del responsable de pago?</DialogTitle>
                    </DialogHeader>
                    <DialogFooter>
                        <Button variant="outline" onClick={() => setShowCancelDialog(false)}>
                            NO
                        </Button>
                        <Button onClick={handleConfirmCancel}>SÍ</Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>

            {/* Advertencia de CUIT existente */}
            <Dialog open={showCuitWarning} onOpenChange={setShowCuitWarning}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle className="text-amber-600">¡CUIDADO!</DialogTitle>
                        <DialogDescription>EL CUIT YA EXISTE EN EL SISTEMA</DialogDescription>
                    </DialogHeader>
                    <DialogFooter>
                        <Button onClick={handleContinueAfterWarning}>ACEPTAR</Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </div>
    )
}
