"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { AlertCircle, DollarSign, CreditCard } from "lucide-react"
import { Alert, AlertDescription } from "@/components/ui/alert"
import Link from "next/link"

type Invoice = {
    id: number
    numeroFactura: string
    responsableNombre: string
    responsableApellido: string
    monto: number
    fecha: string
    estado: "PENDIENTE" | "PAGADA"
}

type PaymentMethod = "MONEDA" | "CHEQUES" | "TARJETA_CREDITO" | "TARJETA_DEBITO"

export default function RegistrarPagoPage() {
    const [roomNumber, setRoomNumber] = useState("")
    const [invoices, setInvoices] = useState<Invoice[]>([])
    const [selectedInvoice, setSelectedInvoice] = useState<Invoice | null>(null)
    const [paymentMethod, setPaymentMethod] = useState<PaymentMethod | "">("")
    const [paymentAmount, setPaymentAmount] = useState("")
    const [change, setChange] = useState(0)
    const [error, setError] = useState("")
    const [success, setSuccess] = useState("")
    const [isSearching, setIsSearching] = useState(false)
    const [showPaymentFields, setShowPaymentFields] = useState(false)
    const [roomNumberError, setRoomNumberError] = useState("")

    const handleRoomNumberChange = (value: string) => {
        const numericValue = value.replace(/\D/g, "")
        setRoomNumber(numericValue)
        setRoomNumberError("")

        if (numericValue && numericValue.length !== 3) {
            setRoomNumberError("El número de habitación debe tener exactamente 3 dígitos")
        }
    }

    const handleSearchRoom = () => {
        setError("")
        setSuccess("")
        setSelectedInvoice(null)
        setShowPaymentFields(false)

        if (!roomNumber.trim()) {
            setRoomNumberError("Debe ingresar un número de habitación")
            return
        }

        if (roomNumber.length !== 3) {
            setRoomNumberError("El número de habitación debe tener exactamente 3 dígitos")
            return
        }

        setIsSearching(true)

        setTimeout(() => {
            const foundInvoices = mockInvoices[roomNumber] || []

            if (foundInvoices.length === 0) {
                setError(`No se encontraron facturas pendientes para la habitación ${roomNumber}`)
                setInvoices([])
            } else {
                setInvoices(foundInvoices)
                setError("")
            }

            setIsSearching(false)
        }, 500)
    }

    const handleSelectInvoice = (invoice: Invoice) => {
        setSelectedInvoice(invoice)
        setChange(0)
        setPaymentAmount("")
        setPaymentMethod("")
        setShowPaymentFields(true)
        setError("")
        setSuccess("")
        resetPaymentFields()
    }

    const resetPaymentFields = () => {
        setCashAmount("")
        setCheckNumber("")
        setCheckBank("")
        setCheckPlaza("")
        setCheckDate("")
        setCardNumber("")
        setCardQuota("")
    }

    const handlePaymentMethodChange = (method: PaymentMethod) => {
        setPaymentMethod(method)
        resetPaymentFields()
        setError("")
    }

    const calculatePayment = () => {
        if (!selectedInvoice) return

        const amount = Number.parseFloat(paymentAmount)

        if (isNaN(amount) || amount <= 0) {
            setError("Debe ingresar un monto válido")
            return
        }

        if (paymentMethod === "MONEDA") {
            if (!cashAmount || Number.parseFloat(cashAmount) <= 0) {
                setError("Debe ingresar el importe en efectivo")
                return
            }
        } else if (paymentMethod === "CHEQUES") {
            if (!checkNumber || !checkBank || !checkPlaza || !checkDate) {
                setError("Debe completar todos los datos del cheque")
                return
            }
        } else if (paymentMethod === "TARJETA_CREDITO" || paymentMethod === "TARJETA_DEBITO") {
            if (!cardNumber || !cardQuota) {
                setError("Debe completar todos los datos de la tarjeta")
                return
            }
        }

        if (amount < selectedInvoice.monto) {
            const remaining = selectedInvoice.monto - amount
            setChange(0)
            setError(`El monto ingresado es menor a la deuda. Resta pagar: $${remaining.toFixed(2)}`)
            return
        }

        const changeAmount = amount - selectedInvoice.monto
        setChange(changeAmount)
        setError("")
    }

    const handleConfirmPayment = () => {
        if (!selectedInvoice) return

        const amount = Number.parseFloat(paymentAmount)

        if (amount >= selectedInvoice.monto) {
            setTimeout(() => {
                setSuccess("Factura salida. TOQUE UNA TECLA PARA CONTINUAR... y VUELTO")

                setInvoices((prevInvoices) =>
                    prevInvoices.map((inv) => (inv.id === selectedInvoice.id ? { ...inv, estado: "PAGADA" as const } : inv)),
                )

                setTimeout(() => {
                    setSelectedInvoice(null)
                    setShowPaymentFields(false)
                    setPaymentAmount("")
                    setChange(0)
                    setPaymentMethod("")
                    resetPaymentFields()
                }, 2000)
            }, 500)
        }
    }

    const mockInvoices: Record<string, Invoice[]> = {
        "101": [
            {
                id: 1,
                numeroFactura: "F-001",
                responsableNombre: "Juan",
                responsableApellido: "Pérez",
                monto: 5000,
                fecha: "2025-12-05",
                estado: "PENDIENTE",
            },
            {
                id: 2,
                numeroFactura: "F-002",
                responsableNombre: "María",
                responsableApellido: "González",
                monto: 3000,
                fecha: "2025-12-08",
                estado: "PENDIENTE",
            },
        ],
        "102": [
            {
                id: 3,
                numeroFactura: "F-003",
                responsableNombre: "Carlos",
                responsableApellido: "Rodríguez",
                monto: 8000,
                fecha: "2025-12-09",
                estado: "PENDIENTE",
            },
        ],
        "103": [],
    }

    const [cashAmount, setCashAmount] = useState("")
    const [checkNumber, setCheckNumber] = useState("")
    const [checkBank, setCheckBank] = useState("")
    const [checkPlaza, setCheckPlaza] = useState("")
    const [checkDate, setCheckDate] = useState("")
    const [cardNumber, setCardNumber] = useState("")
    const [cardQuota, setCardQuota] = useState("")

    return (
        <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50 dark:from-slate-950 dark:via-slate-900 dark:to-slate-950">
            <header className="border-b border-slate-200 bg-white/80 backdrop-blur-sm dark:border-slate-800 dark:bg-slate-900/80">
                <div className="container mx-auto px-4 py-6">
                    <div className="flex items-center justify-between">
                        <div>
                            <h1 className="text-3xl font-bold text-slate-900 dark:text-slate-50">Registrar Pago</h1>
                            <p className="mt-1 text-sm text-slate-600 dark:text-slate-400">
                                Gestión de pagos de facturas por habitación
                            </p>
                        </div>
                        <Button asChild variant="outline">
                            <Link href="/">Volver al Menú Principal</Link>
                        </Button>
                    </div>
                </div>
            </header>

            <main className="container mx-auto px-4 py-8">
                <div className="mx-auto max-w-4xl space-y-6">
                    <Card className="border-slate-200 shadow-lg dark:border-slate-800">
                        <CardHeader className="space-y-1 bg-gradient-to-r from-blue-50 to-indigo-50 dark:from-slate-900 dark:to-slate-800">
                            <CardTitle className="flex items-center gap-2 text-slate-900 dark:text-slate-50">
                                <DollarSign className="h-5 w-5" />
                                Buscar Facturas Pendientes
                            </CardTitle>
                            <CardDescription className="text-slate-600 dark:text-slate-400">
                                Ingrese el número de habitación para ver las facturas pendientes
                            </CardDescription>
                        </CardHeader>
                        <CardContent className="space-y-4 pt-6">
                            <div className="flex gap-4">
                                <div className="flex-1 space-y-2">
                                    <Label htmlFor="roomNumber">Número de Habitación *</Label>
                                    <Input
                                        id="roomNumber"
                                        value={roomNumber}
                                        onChange={(e) => handleRoomNumberChange(e.target.value)}
                                        placeholder="Ej: 101"
                                        maxLength={3}
                                        className={roomNumberError ? "border-red-500" : ""}
                                        onKeyDown={(e) => e.key === "Enter" && handleSearchRoom()}
                                    />
                                    {roomNumberError && <p className="text-sm text-red-600 dark:text-red-400">{roomNumberError}</p>}
                                </div>
                                <div className="flex items-end">
                                    <Button onClick={handleSearchRoom} disabled={isSearching || !!roomNumberError}>
                                        {isSearching ? "Buscando..." : "Buscar"}
                                    </Button>
                                </div>
                            </div>

                            {error && !selectedInvoice && (
                                <Alert variant="destructive">
                                    <AlertCircle className="h-4 w-4" />
                                    <AlertDescription>{error}</AlertDescription>
                                </Alert>
                            )}
                        </CardContent>
                    </Card>

                    {invoices.length > 0 && (
                        <Card className="border-slate-200 shadow-lg dark:border-slate-800">
                            <CardHeader className="space-y-1 bg-gradient-to-r from-emerald-50 to-teal-50 dark:from-slate-900 dark:to-slate-800">
                                <CardTitle className="text-slate-900 dark:text-slate-50">
                                    Facturas Pendientes - Habitación {roomNumber}
                                </CardTitle>
                                <CardDescription className="text-slate-600 dark:text-slate-400">
                                    Seleccione una factura para registrar el pago
                                </CardDescription>
                            </CardHeader>
                            <CardContent className="pt-6">
                                <div className="space-y-3">
                                    {invoices.map((invoice) => (
                                        <div
                                            key={invoice.id}
                                            onClick={() => handleSelectInvoice(invoice)}
                                            className={`cursor-pointer rounded-lg border p-4 transition-all hover:shadow-md ${
                                                selectedInvoice?.id === invoice.id
                                                    ? "border-blue-500 bg-blue-50 dark:border-blue-400 dark:bg-blue-950"
                                                    : "border-slate-200 hover:border-slate-300 dark:border-slate-700"
                                            }`}
                                        >
                                            <div className="flex items-center justify-between">
                                                <div className="space-y-1">
                                                    <p className="font-semibold text-slate-900 dark:text-slate-50">{invoice.numeroFactura}</p>
                                                    <p className="text-sm font-medium text-slate-700 dark:text-slate-300">
                                                        {invoice.responsableNombre} {invoice.responsableApellido}
                                                    </p>
                                                    <p className="text-xs text-slate-600 dark:text-slate-400">Fecha: {invoice.fecha}</p>
                                                </div>
                                                <div className="text-right">
                                                    <p className="text-3xl font-bold text-slate-900 dark:text-slate-50">
                                                        ${invoice.monto.toFixed(2)}
                                                    </p>
                                                    <span
                                                        className={`mt-1 inline-block text-xs px-2 py-1 rounded-full ${
                                                            invoice.estado === "PAGADA"
                                                                ? "bg-green-100 text-green-700 dark:bg-green-950 dark:text-green-400"
                                                                : "bg-amber-100 text-amber-700 dark:bg-amber-950 dark:text-amber-400"
                                                        }`}
                                                    >
                            {invoice.estado}
                          </span>
                                                </div>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            </CardContent>
                        </Card>
                    )}

                    {showPaymentFields && selectedInvoice && (
                        <Card className="border-slate-200 shadow-lg dark:border-slate-800">
                            <CardHeader className="space-y-1 bg-gradient-to-r from-violet-50 to-purple-50 dark:from-slate-900 dark:to-slate-800">
                                <CardTitle className="flex items-center gap-2 text-slate-900 dark:text-slate-50">
                                    <CreditCard className="h-5 w-5" />
                                    Registrar Pago - {selectedInvoice.numeroFactura}
                                </CardTitle>
                                <CardDescription className="text-slate-600 dark:text-slate-400">
                                    Responsable: {selectedInvoice.responsableNombre} {selectedInvoice.responsableApellido}
                                </CardDescription>
                                <div className="mt-2">
                                    <p className="text-sm text-slate-600 dark:text-slate-400">Total a Pagar:</p>
                                    <p className="text-4xl font-bold text-slate-900 dark:text-slate-50">
                                        ${selectedInvoice.monto.toFixed(2)}
                                    </p>
                                </div>
                            </CardHeader>
                            <CardContent className="space-y-6 pt-6">
                                <div className="rounded-lg bg-slate-100 p-3 dark:bg-slate-800">
                                    <Label className="text-sm font-medium text-slate-600 dark:text-slate-400">Vuelto</Label>
                                    <p className="text-xl font-semibold text-slate-900 dark:text-slate-50">${change.toFixed(2)}</p>
                                </div>

                                <div className="space-y-2">
                                    <Label htmlFor="paymentMethod">Método de Pago *</Label>
                                    <Select value={paymentMethod} onValueChange={handlePaymentMethodChange}>
                                        <SelectTrigger id="paymentMethod">
                                            <SelectValue placeholder="Seleccione un método" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectItem value="MONEDA">Moneda</SelectItem>
                                            <SelectItem value="CHEQUES">Cheques</SelectItem>
                                            <SelectItem value="TARJETA_CREDITO">Tarjeta de Crédito</SelectItem>
                                            <SelectItem value="TARJETA_DEBITO">Tarjeta de Débito</SelectItem>
                                        </SelectContent>
                                    </Select>
                                </div>

                                {paymentMethod === "MONEDA" && (
                                    <div className="space-y-4 rounded-lg border border-slate-200 bg-slate-50 p-4 dark:border-slate-700 dark:bg-slate-900">
                                        <h4 className="font-semibold">Datos de Pago en Efectivo</h4>
                                        <div className="space-y-2">
                                            <Label htmlFor="cashAmount">Importe *</Label>
                                            <Input
                                                id="cashAmount"
                                                type="number"
                                                value={cashAmount}
                                                onChange={(e) => setCashAmount(e.target.value)}
                                                placeholder="0.00"
                                            />
                                        </div>
                                    </div>
                                )}

                                {paymentMethod === "CHEQUES" && (
                                    <div className="space-y-4 rounded-lg border border-slate-200 bg-slate-50 p-4 dark:border-slate-700 dark:bg-slate-900">
                                        <h4 className="font-semibold">Datos del Cheque</h4>
                                        <div className="grid gap-4 md:grid-cols-2">
                                            <div className="space-y-2">
                                                <Label htmlFor="checkNumber">Nro. De cheque *</Label>
                                                <Input id="checkNumber" value={checkNumber} onChange={(e) => setCheckNumber(e.target.value)} />
                                            </div>
                                            <div className="space-y-2">
                                                <Label htmlFor="checkBank">Banco *</Label>
                                                <Input id="checkBank" value={checkBank} onChange={(e) => setCheckBank(e.target.value)} />
                                            </div>
                                            <div className="space-y-2">
                                                <Label htmlFor="checkPlaza">Plaza *</Label>
                                                <Input id="checkPlaza" value={checkPlaza} onChange={(e) => setCheckPlaza(e.target.value)} />
                                            </div>
                                            <div className="space-y-2">
                                                <Label htmlFor="checkDate">Fecha de cobro *</Label>
                                                <Input
                                                    id="checkDate"
                                                    type="date"
                                                    value={checkDate}
                                                    onChange={(e) => setCheckDate(e.target.value)}
                                                />
                                            </div>
                                        </div>
                                    </div>
                                )}

                                {(paymentMethod === "TARJETA_CREDITO" || paymentMethod === "TARJETA_DEBITO") && (
                                    <div className="space-y-4 rounded-lg border border-slate-200 bg-slate-50 p-4 dark:border-slate-700 dark:bg-slate-900">
                                        <h4 className="font-semibold">
                                            Datos de {paymentMethod === "TARJETA_CREDITO" ? "Tarjeta de Crédito" : "Tarjeta de Débito"}
                                        </h4>
                                        <div className="grid gap-4 md:grid-cols-2">
                                            <div className="space-y-2">
                                                <Label htmlFor="cardNumber">Número de Tarjeta *</Label>
                                                <Input
                                                    id="cardNumber"
                                                    value={cardNumber}
                                                    onChange={(e) => setCardNumber(e.target.value)}
                                                    placeholder="1234 5678 9012 3456"
                                                />
                                            </div>
                                            <div className="space-y-2">
                                                <Label htmlFor="cardQuota">Cotización *</Label>
                                                <Input
                                                    id="cardQuota"
                                                    type="number"
                                                    value={cardQuota}
                                                    onChange={(e) => setCardQuota(e.target.value)}
                                                    placeholder="1"
                                                />
                                            </div>
                                        </div>
                                    </div>
                                )}

                                {paymentMethod && (
                                    <div className="space-y-2">
                                        <Label htmlFor="paymentAmount">Monto a Pagar *</Label>
                                        <Input
                                            id="paymentAmount"
                                            type="number"
                                            value={paymentAmount}
                                            onChange={(e) => setPaymentAmount(e.target.value)}
                                            placeholder="0.00"
                                        />
                                    </div>
                                )}

                                {error && selectedInvoice && (
                                    <Alert variant="destructive">
                                        <AlertCircle className="h-4 w-4" />
                                        <AlertDescription>{error}</AlertDescription>
                                    </Alert>
                                )}

                                {success && (
                                    <Alert className="border-green-500 bg-green-50 text-green-900 dark:bg-green-950 dark:text-green-100">
                                        <AlertDescription className="font-semibold">{success}</AlertDescription>
                                    </Alert>
                                )}

                                <div className="flex gap-4">
                                    <Button onClick={calculatePayment} className="flex-1" disabled={!paymentMethod || !paymentAmount}>
                                        Calcular
                                    </Button>
                                    <Button
                                        onClick={handleConfirmPayment}
                                        className="flex-1"
                                        disabled={change === 0 || Number.parseFloat(paymentAmount) < selectedInvoice.monto}
                                        variant="default"
                                    >
                                        Confirmar Pago
                                    </Button>
                                    <Button
                                        onClick={() => {
                                            setShowPaymentFields(false)
                                            setSelectedInvoice(null)
                                            setPaymentAmount("")
                                            setPaymentMethod("")
                                            setChange(0)
                                            setError("")
                                            resetPaymentFields()
                                        }}
                                        variant="outline"
                                    >
                                        Cancelar
                                    </Button>
                                </div>
                            </CardContent>
                        </Card>
                    )}
                </div>
            </main>
        </div>
    )
}
