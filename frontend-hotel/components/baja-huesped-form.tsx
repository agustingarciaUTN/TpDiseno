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
import { Search, UserMinus, AlertCircle, CheckCircle2 } from "lucide-react"

type Guest = {
    id: string
    tipoDocumento: string
    nroDocumento: string
    nombre: string
    apellido: string
    email: string
    telefono: string
}

export function BajaHuespedForm() {
    const [searchType, setSearchType] = useState<"documento" | "nombre">("documento")
    const [tipoDocumento, setTipoDocumento] = useState("")
    const [nroDocumento, setNroDocumento] = useState("")
    const [nombre, setNombre] = useState("")
    const [apellido, setApellido] = useState("")
    const [selectedGuest, setSelectedGuest] = useState<Guest | null>(null)
    const [showConfirmDialog, setShowConfirmDialog] = useState(false)
    const [alert, setAlert] = useState<{ type: "success" | "error" | "info"; message: string } | null>(null)
    const [isSearching, setIsSearching] = useState(false)
    const [documentError, setDocumentError] = useState("")

    const validateDocumentNumber = (tipo: string, numero: string): boolean => {
        setDocumentError("")

        if (!numero.trim()) {
            setDocumentError("El número de documento es requerido")
            return false
        }

        if (tipo === "LE" || tipo === "LC" || tipo === "DNI") {
            const numberPattern = /^\d{7,8}$/
            if (!numberPattern.test(numero)) {
                setDocumentError("Debe contener solo números (7-8 dígitos)")
                return false
            }
        }

        if (tipo === "Pasaporte") {
            const passportPattern = /^[A-Za-z0-9]+$/
            if (!passportPattern.test(numero)) {
                setDocumentError("Debe contener solo letras y números")
                return false
            }
        }

        return true
    }

    const handleSearch = async () => {
        if (searchType === "documento") {
            if (!tipoDocumento) {
                setAlert({ type: "error", message: "Debe seleccionar un tipo de documento" })
                return
            }
            if (!validateDocumentNumber(tipoDocumento, nroDocumento)) {
                return
            }
        } else {
            if (!nombre.trim() || !apellido.trim()) {
                setAlert({ type: "error", message: "Debe ingresar nombre y apellido" })
                return
            }
        }

        setIsSearching(true)
        setAlert(null)

        setTimeout(() => {
            const mockGuest: Guest = {
                id: "123",
                tipoDocumento: tipoDocumento || "DNI",
                nroDocumento: nroDocumento || "12345678",
                nombre: nombre || "Juan",
                apellido: apellido || "Pérez",
                email: "juan.perez@example.com",
                telefono: "+54 9 11 1234-5678",
            }
            setSelectedGuest(mockGuest)
            setIsSearching(false)
        }, 500)
    }

    const handleDelete = () => {
        setShowConfirmDialog(true)
    }

    const confirmDelete = async () => {
        const isOccupied = Math.random() > 0.7

        if (isOccupied) {
            setAlert({
                type: "error",
                message:
                    "No se puede eliminar el huésped. El huésped está alojado en el hotel en alguna oportunidad. PRESIONE CUALQUIER TECLA PARA CONTINUAR.",
            })
            setShowConfirmDialog(false)
        } else {
            setTimeout(() => {
                setAlert({
                    type: "success",
                    message: `Los datos del huésped ${selectedGuest?.nombre} ${selectedGuest?.apellido} y sus tipoDeDocumento y nroDeDocumento han sido eliminados del sistema exitosamente.`,
                })
                setShowConfirmDialog(false)
                setSelectedGuest(null)
                setTipoDocumento("")
                setNroDocumento("")
                setNombre("")
                setApellido("")
            }, 500)
        }
    }

    const cancelDelete = () => {
        setShowConfirmDialog(false)
        setAlert({
            type: "info",
            message: "Operación cancelada. No se realizaron cambios.",
        })
    }

    return (
        <div className="mx-auto max-w-3xl space-y-6">
            <Card>
                <CardHeader>
                    <CardTitle className="flex items-center gap-2">
                        <UserMinus className="h-6 w-6 text-rose-600" />
                        Dar de Baja Huésped
                    </CardTitle>
                    <CardDescription>Busque un huésped para eliminarlo permanentemente del sistema</CardDescription>
                </CardHeader>
                <CardContent className="space-y-6">
                    <div className="space-y-2">
                        <Label>Buscar por</Label>
                        <Select value={searchType} onValueChange={(value: "documento" | "nombre") => setSearchType(value)}>
                            <SelectTrigger>
                                <SelectValue />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value="documento">Documento</SelectItem>
                                <SelectItem value="nombre">Nombre y Apellido</SelectItem>
                            </SelectContent>
                        </Select>
                    </div>

                    {searchType === "documento" && (
                        <div className="grid gap-4 sm:grid-cols-2">
                            <div className="space-y-2">
                                <Label htmlFor="tipoDocumento">Tipo de Documento</Label>
                                <Select
                                    value={tipoDocumento}
                                    onValueChange={(value) => {
                                        setTipoDocumento(value)
                                        setDocumentError("")
                                    }}
                                >
                                    <SelectTrigger id="tipoDocumento">
                                        <SelectValue placeholder="Seleccione" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="DNI">DNI</SelectItem>
                                        <SelectItem value="LE">LE (Libreta de Enrolamiento)</SelectItem>
                                        <SelectItem value="LC">LC (Libreta Cívica)</SelectItem>
                                        <SelectItem value="Pasaporte">Pasaporte</SelectItem>
                                        <SelectItem value="OTROS">Otros</SelectItem>
                                    </SelectContent>
                                </Select>
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="nroDocumento">Número de Documento</Label>
                                <Input
                                    id="nroDocumento"
                                    value={nroDocumento}
                                    onChange={(e) => {
                                        setNroDocumento(e.target.value)
                                        setDocumentError("")
                                    }}
                                    placeholder={
                                        tipoDocumento === "LE" || tipoDocumento === "LC" || tipoDocumento === "DNI"
                                            ? "Ej: 12345678 (7-8 dígitos)"
                                            : tipoDocumento === "Pasaporte"
                                                ? "Ej: ABC123456"
                                                : "Ingrese el número"
                                    }
                                    className={documentError ? "border-red-500" : ""}
                                />
                                {documentError && (
                                    <p className="text-sm text-red-500 flex items-center gap-1">
                                        <AlertCircle className="h-3 w-3" />
                                        {documentError}
                                    </p>
                                )}
                            </div>
                        </div>
                    )}

                    {searchType === "nombre" && (
                        <div className="grid gap-4 sm:grid-cols-2">
                            <div className="space-y-2">
                                <Label htmlFor="nombre">Nombre</Label>
                                <Input id="nombre" value={nombre} onChange={(e) => setNombre(e.target.value)} placeholder="Ej: Juan" />
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="apellido">Apellido</Label>
                                <Input
                                    id="apellido"
                                    value={apellido}
                                    onChange={(e) => setApellido(e.target.value)}
                                    placeholder="Ej: Pérez"
                                />
                            </div>
                        </div>
                    )}

                    <Button onClick={handleSearch} disabled={isSearching} className="w-full">
                        <Search className="mr-2 h-4 w-4" />
                        {isSearching ? "Buscando..." : "Buscar Huésped"}
                    </Button>

                    {selectedGuest && (
                        <Card className="border-2 border-rose-200 dark:border-rose-900">
                            <CardHeader>
                                <CardTitle className="text-lg">Datos del Huésped</CardTitle>
                            </CardHeader>
                            <CardContent className="space-y-3">
                                <div className="grid gap-3 sm:grid-cols-2">
                                    <div>
                                        <p className="text-sm font-medium text-muted-foreground">Nombre completo</p>
                                        <p className="text-base font-semibold">
                                            {selectedGuest.nombre} {selectedGuest.apellido}
                                        </p>
                                    </div>
                                    <div>
                                        <p className="text-sm font-medium text-muted-foreground">Documento</p>
                                        <p className="text-base font-semibold">
                                            {selectedGuest.tipoDocumento} {selectedGuest.nroDocumento}
                                        </p>
                                    </div>
                                    <div>
                                        <p className="text-sm font-medium text-muted-foreground">Email</p>
                                        <p className="text-base">{selectedGuest.email}</p>
                                    </div>
                                    <div>
                                        <p className="text-sm font-medium text-muted-foreground">Teléfono</p>
                                        <p className="text-base">{selectedGuest.telefono}</p>
                                    </div>
                                </div>
                                <Button onClick={handleDelete} variant="destructive" className="w-full mt-4">
                                    <UserMinus className="mr-2 h-4 w-4" />
                                    ELIMINAR HUÉSPED
                                </Button>
                            </CardContent>
                        </Card>
                    )}

                    {alert && (
                        <Alert variant={alert.type === "error" ? "destructive" : "default"}>
                            {alert.type === "success" && <CheckCircle2 className="h-4 w-4" />}
                            {alert.type === "error" && <AlertCircle className="h-4 w-4" />}
                            {alert.type === "info" && <AlertCircle className="h-4 w-4" />}
                            <AlertDescription>{alert.message}</AlertDescription>
                        </Alert>
                    )}
                </CardContent>
            </Card>

            <Dialog open={showConfirmDialog} onOpenChange={setShowConfirmDialog}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>Confirmar eliminación</DialogTitle>
                        <DialogDescription>
                            Los datos del huésped{" "}
                            <strong>
                                {selectedGuest?.nombre} {selectedGuest?.apellido}
                            </strong>{" "}
                            y sus <strong>tipoDeDocumento</strong> y <strong>nroDeDocumento</strong> serán eliminados del sistema.
                        </DialogDescription>
                    </DialogHeader>
                    <div className="rounded-lg bg-rose-50 p-4 dark:bg-rose-950/20">
                        <p className="text-sm font-medium text-rose-800 dark:text-rose-300">⚠️ Esta acción no se puede deshacer</p>
                    </div>
                    <DialogFooter className="gap-2 sm:gap-0">
                        <Button variant="outline" onClick={cancelDelete}>
                            CANCELAR
                        </Button>
                        <Button variant="destructive" onClick={confirmDelete}>
                            <UserMinus className="mr-2 h-4 w-4" />
                            ELIMINAR
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </div>
    )
}
