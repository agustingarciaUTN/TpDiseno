/*"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
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
import { buscarHuespedes, darDeBajaHuesped } from "@/lib/api"

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
    const router = useRouter()
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
            setDocumentError("Requerido")
            return false
        }
        if (["LE", "LC", "DNI"].includes(tipo)) {
            if (!/^\d{7,8}$/.test(numero)) {
                setDocumentError("Debe contener solo números (7-8 dígitos)")
                return false
            }
        }
        return true
    }

    // --- BÚSQUEDA REAL (Conectada al Backend) ---
    const handleSearch = async () => {
        setAlert(null)
        setSelectedGuest(null)

        // Validaciones previas
        if (searchType === "documento") {
            if (!tipoDocumento) {
                setAlert({ type: "error", message: "Debe seleccionar un tipo de documento" })
                return
            }
            if (!validateDocumentNumber(tipoDocumento, nroDocumento)) return
        } else {
            if (!nombre.trim() && !apellido.trim()) {
                setAlert({ type: "error", message: "Ingrese al menos un nombre o apellido" })
                return
            }
        }

        setIsSearching(true)

        try {
            // Construimos el criterio de búsqueda
            const criterios = searchType === "documento"
                ? { tipoDocumento, nroDocumento }
                : { nombres: nombre, apellido };

            const resultados = await buscarHuespedes(criterios);

            if (resultados && resultados.length > 0) {
                // Tomamos el primero que coincida (para simplificar la UI de baja)
                const encontrado = resultados[0];

                console.log("Objeto encontrado completo:", encontrado);
                console.log("Tiene idHuesped?", encontrado.idHuesped);

                // Mapeamos DtoHuesped -> Guest

                setSelectedGuest({

                    id: encontrado.nroDocumento.toString(),

                    tipoDocumento: encontrado.tipoDocumento,


                    nroDocumento: encontrado.nroDocumento,

                    nombre: encontrado.nombres,
                    apellido: encontrado.apellido,
                    email: encontrado.email && encontrado.email.length > 0 ? encontrado.email[0] : "-",
                    telefono: encontrado.telefono && encontrado.telefono.length > 0 ? encontrado.telefono[0] : "-",
                });
            } else {
                setAlert({ type: "info", message: "No se encontró ningún huésped con esos datos." });
            }
        } catch (error: any) {
            setAlert({ type: "error", message: "Error al buscar: " + error.message });
        } finally {
            setIsSearching(false)
        }
    }

    const handleDelete = () => {
        setShowConfirmDialog(true)
    }

    // --- BORRADO REAL (Conectado al Backend) ---
    const confirmDelete = async () => {
        if (!selectedGuest) return;

        try {
            // Llamada al endpoint DELETE real
            const mensajeExito = await darDeBajaHuesped(
                selectedGuest.tipoDocumento,
                selectedGuest.nroDocumento
            );

            // Mensaje de éxito del PDF (paso 3)
            setAlert({
                type: "success",
                message: mensajeExito || `Los datos del huésped ${selectedGuest.nombre} ${selectedGuest.apellido} han sido eliminados del sistema.`
            });

            setShowConfirmDialog(false);
            setSelectedGuest(null);

            // Limpiar inputs
            setTipoDocumento("");
            setNroDocumento("");
            setNombre("");
            setApellido("");

            // Redirección
            setTimeout(() => {
                router.push("/")
            }, 3000)

        } catch (error: any) {
            // Aquí capturamos los errores de negocio (Estadías, Facturas)
            // El mensaje vendrá directo del backend (ej: "El huésped no puede ser eliminado...")
            setAlert({
                type: "error",
                message: error.message
            })
            setShowConfirmDialog(false)
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
                                        <SelectItem value="LE">LE</SelectItem>
                                        <SelectItem value="LC">LC</SelectItem>
                                        <SelectItem value="Pasaporte">Pasaporte</SelectItem>
                                        <SelectItem value="Otro">Otro</SelectItem>
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
                                    placeholder="Ej: 12345678"
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
                            serán eliminados del sistema.
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
}*/