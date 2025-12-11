"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { Search, Calendar, User, Bed, X, AlertCircle } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { Badge } from "@/components/ui/badge"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog"

type Reserva = {
    id: number
    apellido: string
    nombres: string
    numeroHabitacion: string
    tipoHabitacion: string
    fechaInicial: string
    fechaFinal: string
}

export function CancelarReservaForm() {
    const router = useRouter()
    const [apellido, setApellido] = useState("")
    const [nombres, setNombres] = useState("")
    const [reservas, setReservas] = useState<Reserva[]>([])
    const [searching, setSearching] = useState(false)
    const [showNoResults, setShowNoResults] = useState(false)
    const [selectedReserva, setSelectedReserva] = useState<Reserva | null>(null)
    const [showConfirmDialog, setShowConfirmDialog] = useState(false)
    const [showSuccessMessage, setShowSuccessMessage] = useState(false)

    // Datos mock para demostración
    const mockReservas: Reserva[] = [
        {
            id: 1,
            apellido: "García",
            nombres: "Juan Carlos",
            numeroHabitacion: "205",
            tipoHabitacion: "Suite",
            fechaInicial: "2025-12-15",
            fechaFinal: "2025-12-20",
        },
        {
            id: 2,
            apellido: "García",
            nombres: "María Elena",
            numeroHabitacion: "308",
            tipoHabitacion: "Doble",
            fechaInicial: "2025-12-18",
            fechaFinal: "2025-12-22",
        },
        {
            id: 3,
            apellido: "Rodríguez",
            nombres: "Pedro José",
            numeroHabitacion: "101",
            tipoHabitacion: "Simple",
            fechaInicial: "2025-12-12",
            fechaFinal: "2025-12-16",
        },
        {
            id: 4,
            apellido: "Martínez",
            nombres: "Ana Laura",
            numeroHabitacion: "410",
            tipoHabitacion: "Suite Presidencial",
            fechaInicial: "2025-12-20",
            fechaFinal: "2025-12-28",
        },
        {
            id: 5,
            apellido: "López",
            nombres: "Carlos Eduardo",
            numeroHabitacion: "203",
            tipoHabitacion: "Doble",
            fechaInicial: "2025-12-10",
            fechaFinal: "2025-12-14",
        },
        {
            id: 6,
            apellido: "Fernández",
            nombres: "Sofía Isabel",
            numeroHabitacion: "305",
            tipoHabitacion: "Triple",
            fechaInicial: "2025-12-16",
            fechaFinal: "2025-12-19",
        },
    ]

    const handleSearch = () => {
        console.log("[v0] Buscando reservas con:", { apellido, nombres })

        // Validación: al menos apellido debe estar presente
        if (!apellido.trim()) {
            setShowNoResults(true)
            setReservas([])
            return
        }

        setSearching(true)
        setShowNoResults(false)

        // Simular búsqueda
        setTimeout(() => {
            const results = mockReservas.filter((r) => {
                const apellidoMatch = r.apellido.toLowerCase().includes(apellido.toLowerCase())
                const nombresMatch = !nombres || r.nombres.toLowerCase().includes(nombres.toLowerCase())
                return apellidoMatch && nombresMatch
            })

            console.log("[v0] Resultados encontrados:", results.length)

            if (results.length === 0) {
                setShowNoResults(true)
                setReservas([])
            } else {
                setReservas(results)
                setShowNoResults(false)
            }

            setSearching(false)
        }, 500)
    }

    const handleCancelarClick = (reserva: Reserva) => {
        setSelectedReserva(reserva)
        setShowConfirmDialog(true)
    }

    const handleConfirmCancel = () => {
        console.log("[v0] Cancelando reserva:", selectedReserva?.id)

        // Simular cancelación
        setReservas(reservas.filter((r) => r.id !== selectedReserva?.id))
        setShowConfirmDialog(false)
        setShowSuccessMessage(true)

        // Redirigir al menú principal después de mostrar el mensaje
        setTimeout(() => {
            router.push("/")
        }, 2000)
    }

    return (
        <div className="max-w-6xl mx-auto space-y-6">
            <Card>
                <CardHeader>
                    <CardTitle className="flex items-center gap-2">
                        <X className="h-6 w-6" />
                        Cancelar Reserva
                    </CardTitle>
                    <CardDescription>Busque y cancele reservas ingresando al menos el apellido del huésped</CardDescription>
                </CardHeader>

                <CardContent className="space-y-6">
                    {/* Formulario de búsqueda */}
                    <div className="grid md:grid-cols-2 gap-4">
                        <div className="space-y-2">
                            <Label htmlFor="apellido" className="flex items-center gap-2">
                                <User className="h-4 w-4" />
                                Apellido *
                            </Label>
                            <Input
                                id="apellido"
                                placeholder="Ingrese el apellido"
                                value={apellido}
                                onChange={(e) => setApellido(e.target.value)}
                                onKeyDown={(e) => e.key === "Enter" && handleSearch()}
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="nombres" className="flex items-center gap-2">
                                <User className="h-4 w-4" />
                                Nombres
                            </Label>
                            <Input
                                id="nombres"
                                placeholder="Ingrese los nombres (opcional)"
                                value={nombres}
                                onChange={(e) => setNombres(e.target.value)}
                                onKeyDown={(e) => e.key === "Enter" && handleSearch()}
                            />
                        </div>
                    </div>

                    <Button onClick={handleSearch} disabled={searching} className="w-full md:w-auto">
                        <Search className="h-4 w-4 mr-2" />
                        {searching ? "Buscando..." : "Buscar Reservas"}
                    </Button>

                    {/* Mensaje sin resultados */}
                    {showNoResults && (
                        <Alert>
                            <AlertCircle className="h-4 w-4" />
                            <AlertDescription>No existen reservas para los criterios de búsqueda</AlertDescription>
                        </Alert>
                    )}

                    {/* Mensaje de éxito */}
                    {showSuccessMessage && (
                        <Alert className="bg-green-50 dark:bg-green-950 border-green-200 dark:border-green-800">
                            <AlertCircle className="h-4 w-4 text-green-600 dark:text-green-400" />
                            <AlertDescription className="text-green-800 dark:text-green-200">
                                Reserva cancelada/s PRESIONE UNA TECLA PARA CONTINUAR... La/s habitación/es quedan disponibles.
                            </AlertDescription>
                        </Alert>
                    )}

                    {/* Grilla de resultados */}
                    {reservas.length > 0 && (
                        <div className="space-y-4">
                            <h3 className="text-lg font-semibold">Reservas encontradas ({reservas.length})</h3>

                            <div className="grid gap-4">
                                {reservas.map((reserva) => (
                                    <Card key={reserva.id} className="overflow-hidden">
                                        <CardContent className="p-6">
                                            <div className="grid md:grid-cols-5 gap-4 items-center">
                                                <div className="md:col-span-2">
                                                    <div className="flex items-center gap-2 mb-1">
                                                        <User className="h-4 w-4 text-muted-foreground" />
                                                        <span className="font-semibold">
                              {reserva.apellido}, {reserva.nombres}
                            </span>
                                                    </div>
                                                </div>

                                                <div>
                                                    <div className="flex items-center gap-2 text-sm">
                                                        <Bed className="h-4 w-4 text-muted-foreground" />
                                                        <div>
                                                            <p className="font-medium">Habitación {reserva.numeroHabitacion}</p>
                                                            <Badge variant="secondary" className="mt-1">
                                                                {reserva.tipoHabitacion}
                                                            </Badge>
                                                        </div>
                                                    </div>
                                                </div>

                                                <div>
                                                    <div className="flex items-center gap-2 text-sm">
                                                        <Calendar className="h-4 w-4 text-muted-foreground" />
                                                        <div>
                                                            <p className="text-muted-foreground">Desde</p>
                                                            <p className="font-medium">
                                                                {new Date(reserva.fechaInicial).toLocaleDateString("es-ES")}
                                                            </p>
                                                            <p className="text-muted-foreground mt-1">Hasta</p>
                                                            <p className="font-medium">{new Date(reserva.fechaFinal).toLocaleDateString("es-ES")}</p>
                                                        </div>
                                                    </div>
                                                </div>

                                                <div className="flex justify-end">
                                                    <Button variant="destructive" onClick={() => handleCancelarClick(reserva)}>
                                                        <X className="h-4 w-4 mr-2" />
                                                        Cancelar
                                                    </Button>
                                                </div>
                                            </div>
                                        </CardContent>
                                    </Card>
                                ))}
                            </div>
                        </div>
                    )}
                </CardContent>
            </Card>

            {/* Dialog de confirmación */}
            <Dialog open={showConfirmDialog} onOpenChange={setShowConfirmDialog}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>Confirmar Cancelación</DialogTitle>
                        <DialogDescription>¿Está seguro que desea cancelar la siguiente reserva?</DialogDescription>
                    </DialogHeader>

                    {selectedReserva && (
                        <div className="space-y-3 py-4">
                            <div className="flex justify-between">
                                <span className="text-muted-foreground">Huésped:</span>
                                <span className="font-medium">
                  {selectedReserva.apellido}, {selectedReserva.nombres}
                </span>
                            </div>
                            <div className="flex justify-between">
                                <span className="text-muted-foreground">Habitación:</span>
                                <span className="font-medium">
                  {selectedReserva.numeroHabitacion} - {selectedReserva.tipoHabitacion}
                </span>
                            </div>
                            <div className="flex justify-between">
                                <span className="text-muted-foreground">Fechas:</span>
                                <span className="font-medium">
                  {new Date(selectedReserva.fechaInicial).toLocaleDateString("es-ES")} -{" "}
                                    {new Date(selectedReserva.fechaFinal).toLocaleDateString("es-ES")}
                </span>
                            </div>
                        </div>
                    )}

                    <DialogFooter className="gap-2">
                        <Button variant="outline" onClick={() => setShowConfirmDialog(false)}>
                            Cancelar
                        </Button>
                        <Button variant="destructive" onClick={handleConfirmCancel}>
                            Aceptar
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </div>
    )
}
