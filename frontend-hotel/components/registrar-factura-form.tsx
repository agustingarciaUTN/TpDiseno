"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { Badge } from "@/components/ui/badge"
import { Checkbox } from "@/components/ui/checkbox"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog"
import { AlertCircle, CheckCircle2, FileText, ArrowLeft } from "lucide-react"
import Link from "next/link"

type PosicionIVA = "Responsable Inscripto" | "Consumidor Final" | "Monotributista" | "Exento"

// Mock data para testing
const mockOccupants = [
    {
        id: 1,
        nombre: "Juan Carlos García",
        dni: "12345678",
        edad: 45,
        esResponsable: true,
        posicionIVA: "Responsable Inscripto" as PosicionIVA, // Genera Factura A
    },
    {
        id: 2,
        nombre: "María Elena García",
        dni: "87654321",
        edad: 42,
        esResponsable: false,
        posicionIVA: "Consumidor Final" as PosicionIVA, // Genera Factura B
    },
    {
        id: 3,
        nombre: "Pedro García",
        dni: "11223344",
        edad: 15,
        esResponsable: false,
        posicionIVA: "Consumidor Final" as PosicionIVA,
    },
]

const mockRoomData = {
    "101": { ocupantes: mockOccupants, valorEstadia: 15000, consumos: 2500 },
    "205": { ocupantes: [mockOccupants[0]], valorEstadia: 25000, consumos: 5000 },
    "308": { ocupantes: [mockOccupants[1]], valorEstadia: 18000, consumos: 1200 },
}

interface ItemFactura {
    id: string
    descripcion: string
    monto: number
    selected: boolean
}

export function RegistrarFacturaForm() {
    const [step, setStep] = useState<"search" | "select-person" | "select-items" | "invoice-type" | "success">("search")
    const [numeroHabitacion, setNumeroHabitacion] = useState("")
    const [horaSalida, setHoraSalida] = useState("")
    const [errorMessage, setErrorMessage] = useState("")
    const [ocupantes, setOcupantes] = useState<typeof mockOccupants>([])
    const [selectedPerson, setSelectedPerson] = useState<(typeof mockOccupants)[0] | null>(null)
    const [items, setItems] = useState<ItemFactura[]>([])
    const [tipoFactura, setTipoFactura] = useState<"A" | "B" | null>(null)
    const [showThirdPartyDialog, setShowThirdPartyDialog] = useState(false)
    const [cuitTercero, setCuitTercero] = useState("")

    const handleSearch = () => {
        setErrorMessage("")

        // Validación de campos
        if (!numeroHabitacion.trim()) {
            setErrorMessage("El número de habitación es obligatorio")
            return
        }

        if (!horaSalida.trim()) {
            setErrorMessage("La hora de salida es obligatoria")
            return
        }

        // Validar formato de habitación (debe ser número)
        if (!/^\d+$/.test(numeroHabitacion)) {
            setErrorMessage("El número de habitación debe ser un valor numérico")
            return
        }

        // Buscar habitación en mock data
        const roomData = mockRoomData[numeroHabitacion as keyof typeof mockRoomData]

        if (!roomData) {
            setErrorMessage("La habitación no existe o no está ocupada")
            return
        }

        setOcupantes(roomData.ocupantes)
        setStep("select-person")
    }

    const determinarTipoFactura = (posicionIVA: PosicionIVA): "A" | "B" => {
        // Factura A: Responsables Inscriptos
        // Factura B: Consumidor Final, Monotributistas, Exentos
        return posicionIVA === "Responsable Inscripto" ? "A" : "B"
    }

    const handleSelectPerson = (person: (typeof mockOccupants)[0]) => {
        // Verificar si es menor de edad
        if (person.edad < 18) {
            setErrorMessage("La persona seleccionada es menor de edad. Por favor elija otra")
            return
        }

        setErrorMessage("")
        setSelectedPerson(person)

        const tipoFacturaCalculado = determinarTipoFactura(person.posicionIVA)
        setTipoFactura(tipoFacturaCalculado)

        // Cargar items a facturar
        const roomData = mockRoomData[numeroHabitacion as keyof typeof mockRoomData]
        const itemsFactura: ItemFactura[] = [
            {
                id: "estadia",
                descripcion: "Valor de la estadía",
                monto: roomData.valorEstadia,
                selected: true,
            },
            {
                id: "consumos",
                descripcion: "Consumos de la habitación",
                monto: roomData.consumos,
                selected: true,
            },
        ]

        setItems(itemsFactura)
        setStep("select-items")
    }

    const handleToggleItem = (itemId: string) => {
        setItems(items.map((item) => (item.id === itemId ? { ...item, selected: !item.selected } : item)))
    }

    const handleAcceptItems = () => {
        const selectedItems = items.filter((item) => item.selected)

        if (selectedItems.length === 0) {
            setErrorMessage("Debe seleccionar al menos un ítem para facturar")
            return
        }

        setErrorMessage("")
        setStep("invoice-type")
    }

    const handleGenerateInvoice = () => {
        setErrorMessage("")
        setStep("success")
    }

    const calculateTotal = () => {
        return items.filter((item) => item.selected).reduce((sum, item) => sum + item.monto, 0)
    }

    const calculateSubtotal = () => {
        const total = calculateTotal()
        return tipoFactura === "A" ? total : total / 1.21
    }

    const calculateIVA = () => {
        const total = calculateTotal()
        return tipoFactura === "A" ? total * 0.21 : total - total / 1.21
    }

    const calculateFinalTotal = () => {
        const total = calculateTotal()
        return tipoFactura === "A" ? total * 1.21 : total
    }

    const resetForm = () => {
        setStep("search")
        setNumeroHabitacion("")
        setHoraSalida("")
        setErrorMessage("")
        setOcupantes([])
        setSelectedPerson(null)
        setItems([])
        setTipoFactura(null)
        setCuitTercero("")
    }

    return (
        <div className="space-y-6">
            <div className="flex items-center gap-4">
                <Button variant="ghost" size="icon" asChild>
                    <Link href="/">
                        <ArrowLeft className="h-5 w-5" />
                    </Link>
                </Button>
                <div>
                    <h1 className="text-3xl font-bold text-slate-900 dark:text-slate-50">Registrar Factura</h1>
                    <p className="text-slate-600 dark:text-slate-400">Caso de Uso 07 - Generar factura al momento del checkout</p>
                </div>
            </div>

            {errorMessage && (
                <Alert variant="destructive">
                    <AlertCircle className="h-4 w-4" />
                    <AlertDescription>{errorMessage}</AlertDescription>
                </Alert>
            )}

            {step === "search" && (
                <Card>
                    <CardHeader>
                        <CardTitle className="flex items-center gap-2">
                            <FileText className="h-5 w-5" />
                            Buscar Habitación
                        </CardTitle>
                        <CardDescription>
                            Ingrese el número de habitación y la hora de salida para generar la factura
                        </CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-4">
                        <div className="space-y-2">
                            <Label htmlFor="numeroHabitacion">Número de Habitación *</Label>
                            <Input
                                id="numeroHabitacion"
                                placeholder="Ej: 101"
                                value={numeroHabitacion}
                                onChange={(e) => setNumeroHabitacion(e.target.value)}
                                onKeyDown={(e) => {
                                    if (e.key === "Enter") {
                                        const horaSalidaInput = document.getElementById("horaSalida")
                                        horaSalidaInput?.focus()
                                    }
                                }}
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="horaSalida">Hora de Salida *</Label>
                            <Input
                                id="horaSalida"
                                type="time"
                                value={horaSalida}
                                onChange={(e) => setHoraSalida(e.target.value)}
                                onKeyDown={(e) => {
                                    if (e.key === "Enter") {
                                        handleSearch()
                                    }
                                }}
                            />
                        </div>

                        <div className="flex gap-2 pt-2">
                            <Button onClick={handleSearch} className="flex-1">
                                Buscar
                            </Button>
                            <Button variant="outline" onClick={() => setErrorMessage("")}>
                                Cancelar
                            </Button>
                        </div>

                        <div className="rounded-lg bg-blue-50 p-4 dark:bg-blue-950/20">
                            <p className="text-sm font-medium text-blue-900 dark:text-blue-100">Habitaciones de prueba:</p>
                            <ul className="mt-2 space-y-1 text-sm text-blue-700 dark:text-blue-300">
                                <li>• 101 - 3 ocupantes (incluye menor de edad)</li>
                                <li>• 205 - 1 ocupante</li>
                                <li>• 308 - 1 ocupante</li>
                            </ul>
                        </div>
                    </CardContent>
                </Card>
            )}

            {step === "select-person" && (
                <Card>
                    <CardHeader>
                        <CardTitle>Seleccionar Responsable de Pago</CardTitle>
                        <CardDescription>Habitación {numeroHabitacion} - Ocupantes registrados</CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-4">
                        <div className="space-y-3">
                            {ocupantes.map((ocupante) => (
                                <Card
                                    key={ocupante.id}
                                    className={`cursor-pointer transition-all hover:border-blue-500 ${
                                        ocupante.edad < 18 ? "opacity-60" : ""
                                    }`}
                                    onClick={() => handleSelectPerson(ocupante)}
                                >
                                    <CardContent className="flex items-center justify-between p-4">
                                        <div>
                                            <p className="font-medium text-slate-900 dark:text-slate-50">{ocupante.nombre}</p>
                                            <p className="text-sm text-slate-600 dark:text-slate-400">
                                                DNI: {ocupante.dni} - Edad: {ocupante.edad} años
                                            </p>
                                            <p className="text-xs text-slate-500 dark:text-slate-500">Posición IVA: {ocupante.posicionIVA}</p>
                                        </div>
                                        <div className="flex gap-2">
                                            {ocupante.esResponsable && <Badge variant="default">Responsable</Badge>}
                                            {ocupante.edad < 18 && <Badge variant="secondary">Menor</Badge>}
                                        </div>
                                    </CardContent>
                                </Card>
                            ))}
                        </div>

                        <Button variant="outline" onClick={() => setStep("search")} className="w-full">
                            Volver
                        </Button>

                        <Button variant="secondary" onClick={() => setShowThirdPartyDialog(true)} className="w-full">
                            Facturar a Tercero
                        </Button>
                    </CardContent>
                </Card>
            )}

            {step === "select-items" && selectedPerson && (
                <Card>
                    <CardHeader>
                        <CardTitle>Seleccionar Ítems a Facturar</CardTitle>
                        <CardDescription>
                            Responsable: {selectedPerson.nombre} - Habitación {numeroHabitacion}
                        </CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-4">
                        <div className="space-y-3">
                            {items.map((item) => (
                                <Card key={item.id} className="border-slate-200 dark:border-slate-800">
                                    <CardContent className="flex items-center justify-between p-4">
                                        <div className="flex items-center gap-3">
                                            <Checkbox
                                                id={item.id}
                                                checked={item.selected}
                                                onCheckedChange={() => handleToggleItem(item.id)}
                                            />
                                            <div>
                                                <Label htmlFor={item.id} className="cursor-pointer font-medium">
                                                    {item.descripcion}
                                                </Label>
                                            </div>
                                        </div>
                                        <p className="text-lg font-semibold text-slate-900 dark:text-slate-50">
                                            ${item.monto.toLocaleString("es-AR")}
                                        </p>
                                    </CardContent>
                                </Card>
                            ))}
                        </div>

                        <div className="rounded-lg bg-slate-100 p-4 dark:bg-slate-800">
                            <div className="flex justify-between text-lg font-semibold">
                                <span>Total a Facturar:</span>
                                <span className="text-blue-600 dark:text-blue-400">${calculateTotal().toLocaleString("es-AR")}</span>
                            </div>
                        </div>

                        <div className="flex gap-2">
                            <Button onClick={handleAcceptItems} className="flex-1">
                                Aceptar
                            </Button>
                            <Button variant="outline" onClick={() => setStep("select-person")}>
                                Volver
                            </Button>
                        </div>
                    </CardContent>
                </Card>
            )}

            {step === "invoice-type" && (
                <Card>
                    <CardHeader>
                        <CardTitle>Confirmar Factura</CardTitle>
                        <CardDescription>
                            Responsable: {selectedPerson?.nombre} - Habitación {numeroHabitacion}
                        </CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-4">
                        <div className="rounded-lg border border-slate-200 bg-slate-50 p-4 dark:border-slate-700 dark:bg-slate-900">
                            <h3 className="mb-3 font-semibold text-slate-900 dark:text-slate-50">Datos del Responsable de Pago</h3>
                            <p className="text-slate-700 dark:text-slate-300">{selectedPerson?.nombre}</p>
                            <p className="text-sm text-slate-600 dark:text-slate-400">
                                Posición frente al IVA: {selectedPerson?.posicionIVA}
                            </p>
                        </div>

                        <div className="space-y-3">
                            <h3 className="font-semibold text-slate-900 dark:text-slate-50">Ítems Pendientes de Facturar:</h3>

                            {items
                                .filter((item) => item.selected)
                                .map((item) => (
                                    <div
                                        key={item.id}
                                        className="flex justify-between rounded-lg border border-slate-200 p-3 dark:border-slate-700"
                                    >
                                        <span className="text-slate-700 dark:text-slate-300">{item.descripcion}</span>
                                        <span className="font-medium text-slate-900 dark:text-slate-50">
                      ${item.monto.toLocaleString("es-AR")}
                    </span>
                                    </div>
                                ))}

                            <div className="rounded-lg border border-slate-300 bg-slate-100 p-4 dark:border-slate-600 dark:bg-slate-800">
                                <div className="mb-3 flex justify-between text-base">
                                    <span className="text-slate-700 dark:text-slate-300">Subtotal:</span>
                                    <span className="font-medium text-slate-900 dark:text-slate-50">
                    ${calculateSubtotal().toLocaleString("es-AR")}
                  </span>
                                </div>

                                {tipoFactura === "A" && (
                                    <div className="mb-3 flex justify-between text-base">
                                        <span className="text-slate-700 dark:text-slate-300">IVA (21%):</span>
                                        <span className="font-medium text-slate-900 dark:text-slate-50">
                      ${calculateIVA().toLocaleString("es-AR")}
                    </span>
                                    </div>
                                )}

                                <div className="border-t border-slate-400 pt-3 dark:border-slate-600">
                                    <div className="flex justify-between text-xl font-bold">
                                        <span className="text-slate-900 dark:text-slate-50">Total a Pagar:</span>
                                        <span className="text-blue-600 dark:text-blue-400">
                      ${calculateFinalTotal().toLocaleString("es-AR")}
                    </span>
                                    </div>
                                    {tipoFactura === "A" && (
                                        <p className="mt-1 text-xs text-slate-600 dark:text-slate-400">IVA discriminado</p>
                                    )}
                                    {tipoFactura === "B" && (
                                        <p className="mt-1 text-xs text-slate-600 dark:text-slate-400">IVA incluido</p>
                                    )}
                                </div>
                            </div>
                        </div>

                        <div className="rounded-lg border border-blue-200 bg-blue-50 p-4 dark:border-blue-800 dark:bg-blue-950/20">
                            <h3 className="mb-2 font-semibold text-blue-900 dark:text-blue-100">Tipo de Factura a Generar:</h3>

                            <div className="rounded-lg border-2 border-blue-600 bg-blue-100 p-4 dark:border-blue-500 dark:bg-blue-900/40">
                                <div className="text-center">
                                    <h4 className="text-2xl font-bold text-slate-900 dark:text-slate-50">Factura {tipoFactura}</h4>
                                    <p className="mt-1 text-sm text-slate-600 dark:text-slate-400">
                                        {tipoFactura === "A"
                                            ? "Responsable Inscripto - IVA discriminado"
                                            : "Consumidor Final - IVA incluido"}
                                    </p>
                                    <p className="mt-2 text-xs italic text-slate-500 dark:text-slate-400">
                                        Tipo determinado según la posición frente al IVA del responsable
                                    </p>
                                </div>
                            </div>
                        </div>

                        <div className="flex gap-2 pt-2">
                            <Button onClick={handleGenerateInvoice} className="flex-1">
                                Aceptar
                            </Button>
                            <Button variant="outline" onClick={() => setStep("select-items")}>
                                Cancelar
                            </Button>
                        </div>
                    </CardContent>
                </Card>
            )}

            {step === "success" && (
                <Card className="border-green-200 bg-green-50/50 dark:border-green-900 dark:bg-green-950/20">
                    <CardContent className="flex flex-col items-center gap-4 p-8 text-center">
                        <div className="rounded-full bg-green-100 p-4 dark:bg-green-900">
                            <CheckCircle2 className="h-12 w-12 text-green-600 dark:text-green-400" />
                        </div>
                        <div>
                            <h2 className="mb-2 text-2xl font-bold text-slate-900 dark:text-slate-50">
                                Factura Generada Exitosamente
                            </h2>
                            <p className="text-slate-600 dark:text-slate-400">
                                La factura tipo {tipoFactura} ha sido generada y queda pendiente de pago
                            </p>
                        </div>

                        <div className="w-full max-w-md space-y-2 rounded-lg bg-white p-4 text-left dark:bg-slate-900">
                            <div className="flex justify-between text-sm">
                                <span className="text-slate-600 dark:text-slate-400">Habitación:</span>
                                <span className="font-medium">{numeroHabitacion}</span>
                            </div>
                            <div className="flex justify-between text-sm">
                                <span className="text-slate-600 dark:text-slate-400">Responsable:</span>
                                <span className="font-medium">{selectedPerson?.nombre}</span>
                            </div>
                            <div className="flex justify-between text-sm">
                                <span className="text-slate-600 dark:text-slate-400">Tipo de Factura:</span>
                                <span className="font-medium">Factura {tipoFactura}</span>
                            </div>
                            <div className="flex justify-between border-t pt-2 text-base font-bold">
                                <span>Total:</span>
                                <span className="text-green-600 dark:text-green-400">
                  ${calculateFinalTotal().toLocaleString("es-AR")}
                </span>
                            </div>
                        </div>

                        <div className="flex gap-2">
                            <Button onClick={resetForm} className="flex-1">
                                Generar Nueva Factura
                            </Button>
                            <Button variant="outline" asChild>
                                <Link href="/">Volver al Inicio</Link>
                            </Button>
                        </div>
                    </CardContent>
                </Card>
            )}

            {/* Dialog para facturar a tercero */}
            <Dialog open={showThirdPartyDialog} onOpenChange={setShowThirdPartyDialog}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>Facturar a Tercero</DialogTitle>
                        <DialogDescription>Ingrese el CUIT del tercero responsable del pago</DialogDescription>
                    </DialogHeader>
                    <div className="space-y-4">
                        <div className="space-y-2">
                            <Label htmlFor="cuitTercero">CUIT</Label>
                            <Input
                                id="cuitTercero"
                                placeholder="XX-XXXXXXXX-X"
                                value={cuitTercero}
                                onChange={(e) => setCuitTercero(e.target.value)}
                            />
                        </div>
                    </div>
                    <DialogFooter>
                        <Button
                            variant="outline"
                            onClick={() => {
                                setShowThirdPartyDialog(false)
                                setCuitTercero("")
                            }}
                        >
                            Cancelar
                        </Button>
                        <Button
                            onClick={() => {
                                if (!cuitTercero.trim()) {
                                    setErrorMessage("Debe ingresar un CUIT válido")
                                    return
                                }
                                setShowThirdPartyDialog(false)
                                // Aquí se ejecutaría el CU03 "Dar Alta de Responsable de Pago"
                                alert("Se ejecutaría el CU03 para dar de alta al tercero")
                            }}
                        >
                            Aceptar
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </div>
    )
}
