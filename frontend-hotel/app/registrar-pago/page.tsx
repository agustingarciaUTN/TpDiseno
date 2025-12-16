"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { AlertCircle, DollarSign, CreditCard, X } from "lucide-react"
import { Alert, AlertDescription } from "@/components/ui/alert"
import Link from "next/link"
import { buscarFacturasPendientes, registrarPago } from "@/lib/api"
import { DtoFactura, Moneda, TipoMedioPago, DtoMedioPago, DtoPago, EstadoFactura } from "@/lib/types"

type PaymentMethod = "EFECTIVO" | "CHEQUE" | "TARJETA_CREDITO" | "TARJETA_DEBITO"

export default function RegistrarPagoPage() {
    const [roomNumber, setRoomNumber] = useState("")
    const [invoices, setInvoices] = useState<DtoFactura[]>([])
    const [selectedInvoice, setSelectedInvoice] = useState<DtoFactura | null>(null)
    const [paymentMethod, setPaymentMethod] = useState<PaymentMethod | "">("")
    const [paymentAmount, setPaymentAmount] = useState("")
    const [moneda, setMoneda] = useState<Moneda>(Moneda.PESOS_ARGENTINOS)
    const [cotizacion, setCotizacion] = useState("1")
    const [change, setChange] = useState(0)
    const [error, setError] = useState("")
    const [success, setSuccess] = useState("")
    const [isSearching, setIsSearching] = useState(false)
    const [showPaymentFields, setShowPaymentFields] = useState(false)
    const [roomNumberError, setRoomNumberError] = useState("")
    const [mediosPagoAcumulados, setMediosPagoAcumulados] = useState<DtoMedioPago[]>([])
    const [montoAcumulado, setMontoAcumulado] = useState(0)

    const handleRoomNumberChange = (value: string) => {
        const numericValue = value.replace(/\D/g, "")
        setRoomNumber(numericValue)
        setRoomNumberError("")

        if (numericValue && numericValue.length !== 3) {
            setRoomNumberError("El número de habitación debe tener exactamente 3 dígitos")
        }
    }

    const handleSearchRoom = async () => {
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

        try {
            const facturas = await buscarFacturasPendientes(roomNumber)
            
            if (facturas.length === 0) {
                setError(`No se encontraron facturas pendientes para la habitación ${roomNumber}`)
                setInvoices([])
            } else {
                setInvoices(facturas)
                setError("")
            }
        } catch (err: any) {
            setError(err.message || `Error al buscar facturas para la habitación ${roomNumber}`)
            setInvoices([])
        } finally {
            setIsSearching(false)
        }
    }

    const handleSelectInvoice = (invoice: DtoFactura) => {
        setSelectedInvoice(invoice)
        setChange(0)
        setPaymentAmount(invoice.importeTotal.toString())
        setPaymentMethod("")
        setMoneda(Moneda.PESOS_ARGENTINOS)
        setCotizacion("1")
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
        setCardNetwork("")
        setCardCvv("")
        setCardExpiry("")
        setCardBank("")
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

        if (paymentMethod === "EFECTIVO") {
            if (!cashAmount || Number.parseFloat(cashAmount) <= 0) {
                setError("Debe ingresar el importe en efectivo")
                return
            }
        } else if (paymentMethod === "CHEQUE") {
            if (!checkNumber || !checkBank || !checkPlaza || !checkDate) {
                setError("Debe completar todos los datos del cheque")
                return
            }
        } else if (paymentMethod === "TARJETA_CREDITO") {
            if (!cardNumber || !cardNetwork || !cardBank || !cardCvv || !cardExpiry || !cardQuota) {
                setError("Debe completar todos los datos de la tarjeta")
                return
            }
        } else if (paymentMethod === "TARJETA_DEBITO") {
            if (!cardNumber || !cardNetwork || !cardBank || !cardCvv || !cardExpiry) {
                setError("Debe completar todos los datos de la tarjeta")
                return
            }
        }

        // Permitir pagos parciales - solo validar que el monto sea > 0
        if (amount > selectedInvoice.importeTotal) {
            const changeAmount = amount - selectedInvoice.importeTotal
            setChange(changeAmount)
            setError("")
        } else {
            setChange(0)
            setError("")
        }
    }

    const handleAddPaymentMethod = () => {
        if (!selectedInvoice) return

        const amount = Number.parseFloat(paymentAmount)
        const cotizacionNum = Number.parseFloat(cotizacion)

        if (isNaN(amount) || amount <= 0) {
            setError("Debe ingresar un monto válido mayor a 0")
            return
        }

        if (!paymentMethod) {
            setError("Debe seleccionar un método de pago")
            return
        }

        if (paymentMethod === "EFECTIVO") {
            if (!cashAmount || Number.parseFloat(cashAmount) <= 0) {
                setError("Debe ingresar el importe en efectivo")
                return
            }
        } else if (paymentMethod === "CHEQUE") {
            if (!checkNumber || !checkBank || !checkPlaza || !checkDate) {
                setError("Debe completar todos los datos del cheque")
                return
            }
        } else if (paymentMethod === "TARJETA_CREDITO") {
            if (!cardNumber || !cardNetwork || !cardBank || !cardCvv || !cardExpiry || !cardQuota) {
                setError("Debe completar todos los datos de la tarjeta")
                return
            }
        } else if (paymentMethod === "TARJETA_DEBITO") {
            if (!cardNumber || !cardNetwork || !cardBank || !cardCvv || !cardExpiry) {
                setError("Debe completar todos los datos de la tarjeta")
                return
            }
        }

        // Crear el medio de pago (acumular en memoria)
        const fechaActual = new Date().toISOString().split('T')[0]
        const nuevoMedio: DtoMedioPago = {
            tipoMedio: TipoMedioPago[paymentMethod as keyof typeof TipoMedioPago],
            monto: amount,
            moneda: moneda,
            fechaDePago: fechaActual
        } as DtoMedioPago

        // Agregar campos específicos según el tipo
        if (paymentMethod === "EFECTIVO") {
            Object.assign(nuevoMedio, {})
        } else if (paymentMethod === "CHEQUE") {
            Object.assign(nuevoMedio, {
                numeroCheque: checkNumber,
                banco: checkBank,
                plaza: checkPlaza,
                fechaCobro: checkDate
            })
        } else if (paymentMethod === "TARJETA_CREDITO") {
            Object.assign(nuevoMedio, {
                numeroDeTarjeta: cardNumber,
                redDePago: cardNetwork,
                cuotasCantidad: Number.parseInt(cardQuota || "1"),
                codigoSeguridad: Number.parseInt(cardCvv),
                fechaVencimiento: cardExpiry,
                banco: cardBank
            })
        } else if (paymentMethod === "TARJETA_DEBITO") {
            Object.assign(nuevoMedio, {
                numeroDeTarjeta: cardNumber,
                redDePago: cardNetwork,
                codigoSeguridad: Number.parseInt(cardCvv),
                fechaVencimiento: cardExpiry,
                banco: cardBank
            })
        }

        // Calcular el equivalente en pesos argentinos (para acumular)
        const montoEnPesos = moneda === Moneda.PESOS_ARGENTINOS 
            ? amount 
            : amount * cotizacionNum

        // Agregar a la lista
        setMediosPagoAcumulados([...mediosPagoAcumulados, nuevoMedio])
        const nuevoMontoAcumuladoEnPesos = montoAcumulado + montoEnPesos
        setMontoAcumulado(nuevoMontoAcumuladoEnPesos)

        // Calcular cuánto falta
        const faltaPagar = Math.max(0, selectedInvoice.importeTotal - nuevoMontoAcumuladoEnPesos)

        // Limpiar formulario para agregar otro medio
        setPaymentMethod("")
        setPaymentAmount("")
        setMoneda(Moneda.PESOS_ARGENTINOS)
        setCotizacion("1")
        resetPaymentFields()
        setError("")
        
        if (faltaPagar > 0) {
            setSuccess(`Medio agregado. Total acumulado: $${nuevoMontoAcumuladoEnPesos.toFixed(2)} | Falta: $${faltaPagar.toFixed(2)}`)
        } else {
            setSuccess(`Medio agregado. Total acumulado: $${nuevoMontoAcumuladoEnPesos.toFixed(2)} | Listo para finalizar`)
        }

        setTimeout(() => {
            setSuccess("")
        }, 3000)
    }

    const handleRemoverMedioPago = (indice: number) => {
        const medioARemover = mediosPagoAcumulados[indice]
        const montoEnPesos = medioARemover.moneda === Moneda.PESOS_ARGENTINOS 
            ? medioARemover.monto 
            : medioARemover.monto * Number.parseFloat(cotizacion)
        
        // Remover de la lista
        const nuevosMedios = mediosPagoAcumulados.filter((_, idx) => idx !== indice)
        setMediosPagoAcumulados(nuevosMedios)
        
        // Actualizar monto acumulado
        const nuevoMontoAcumulado = montoAcumulado - montoEnPesos
        setMontoAcumulado(nuevoMontoAcumulado)
        
        setError("")
        setSuccess(`Medio de pago removido. Total acumulado: $${nuevoMontoAcumulado.toFixed(2)}`)
        setTimeout(() => {
            setSuccess("")
        }, 2000)
    }

    const handleFinalizarPago = async () => {
        if (!selectedInvoice || mediosPagoAcumulados.length === 0) {
            setError("No hay medios de pago agregados")
            return
        }

        const cotizacionNum = Number.parseFloat(cotizacion)

        // Validar que el total sea suficiente
        if (montoAcumulado < selectedInvoice.importeTotal) {
            setError(`El monto total ($${montoAcumulado.toFixed(2)}) es menor al de la factura ($${selectedInvoice.importeTotal.toFixed(2)})`)
            return
        }

        try {
            const dtoPago: DtoPago = {
                numeroFactura: selectedInvoice.numeroFactura,
                fechaPago: new Date().toISOString().split('T')[0],
                moneda: moneda,
                cotizacion: cotizacionNum,
                montoTotal: montoAcumulado,
                mediosPago: mediosPagoAcumulados
            }

            const resultado = await registrarPago(dtoPago)
            
            // Mostrar resultado
            setSuccess(`${resultado.mensaje}${resultado.vuelto > 0 ? ` Vuelto: $${resultado.vuelto.toFixed(2)}` : ""}`)
            setChange(resultado.vuelto)

            // Actualizar la factura en la lista (marcar como PAGADA sin cambiar monto)
            setInvoices((prevInvoices) =>
                prevInvoices.map((inv) => 
                    inv.numeroFactura === selectedInvoice.numeroFactura 
                        ? { ...inv, estadoFactura: EstadoFactura.PAGADA } 
                        : inv
                ),
            )

            // Limpiar después de 3 segundos
            setTimeout(() => {
                setSelectedInvoice(null)
                setShowPaymentFields(false)
                setMediosPagoAcumulados([])
                setMontoAcumulado(0)
                setPaymentAmount("")
                setChange(0)
                setPaymentMethod("")
                setMoneda(Moneda.PESOS_ARGENTINOS)
                setCotizacion("1")
                resetPaymentFields()
                setSuccess("")
            }, 3000)

        } catch (err: any) {
            setError(err.message || "Error al registrar el pago")
        }
    }

    const handleCancelarPago = () => {
        setSelectedInvoice(null)
        setShowPaymentFields(false)
        setMediosPagoAcumulados([])
        setMontoAcumulado(0)
        setPaymentAmount("")
        setChange(0)
        setPaymentMethod("")
        setMoneda(Moneda.PESOS_ARGENTINOS)
        setCotizacion("1")
        resetPaymentFields()
        setError("")
        setSuccess("")
    }

    // Estados para campos de pago
    const [cashAmount, setCashAmount] = useState("")
    const [checkNumber, setCheckNumber] = useState("")
    const [checkBank, setCheckBank] = useState("")
    const [checkPlaza, setCheckPlaza] = useState("")
    const [checkDate, setCheckDate] = useState("")
    const [cardNumber, setCardNumber] = useState("")
    const [cardQuota, setCardQuota] = useState("")
    const [cardNetwork, setCardNetwork] = useState("")
    const [cardCvv, setCardCvv] = useState("")
    const [cardExpiry, setCardExpiry] = useState("")
    const [cardBank, setCardBank] = useState("")

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
                                            key={invoice.numeroFactura}
                                            onClick={() => handleSelectInvoice(invoice)}
                                            className={`cursor-pointer rounded-lg border p-4 transition-all hover:shadow-md ${
                                                selectedInvoice?.numeroFactura === invoice.numeroFactura
                                                    ? "border-blue-500 bg-blue-50 dark:border-blue-400 dark:bg-blue-950"
                                                    : "border-slate-200 hover:border-slate-300 dark:border-slate-700"
                                            }`}
                                        >
                                            <div className="flex items-center justify-between">
                                                <div className="space-y-1">
                                                    <p className="font-semibold text-slate-900 dark:text-slate-50">{invoice.numeroFactura}</p>
                                                    <p className="text-sm font-medium text-slate-700 dark:text-slate-300">
                                                        {invoice.nombreResponsable || 'N/A'} {invoice.apellidoResponsable || ''}
                                                    </p>
                                                    <p className="text-xs text-slate-600 dark:text-slate-400">Fecha: {new Date(invoice.fechaEmision).toLocaleDateString()}</p>
                                                </div>
                                                <div className="text-right">
                                                    <p className="text-3xl font-bold text-slate-900 dark:text-slate-50">
                                                        ${invoice.importeTotal.toFixed(2)}
                                                    </p>
                                                    <span
                                                        className={`mt-1 inline-block text-xs px-2 py-1 rounded-full ${
                                                            invoice.estadoFactura === "PAGADA"
                                                                ? "bg-green-100 text-green-700 dark:bg-green-950 dark:text-green-400"
                                                                : "bg-amber-100 text-amber-700 dark:bg-amber-950 dark:text-amber-400"
                                                        }`}
                                                    >
                            {invoice.estadoFactura}
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
                                    Responsable: {selectedInvoice.nombreResponsable || 'N/A'} {selectedInvoice.apellidoResponsable || ''}
                                </CardDescription>
                                <div className="mt-2">
                                    <p className="text-sm text-slate-600 dark:text-slate-400">Total a Pagar:</p>
                                    <p className="text-4xl font-bold text-slate-900 dark:text-slate-50">
                                        ${selectedInvoice.importeTotal.toFixed(2)}
                                    </p>
                                </div>
                            </CardHeader>
                            <CardContent className="space-y-6 pt-6">
                                <div className="rounded-lg bg-slate-100 p-3 dark:bg-slate-800">
                                    <Label className="text-sm font-medium text-slate-600 dark:text-slate-400">Vuelto</Label>
                                    <p className="text-xl font-semibold text-slate-900 dark:text-slate-50">${change.toFixed(2)}</p>
                                </div>

                                <div className="grid gap-4 md:grid-cols-3">
                                    <div className="space-y-2">
                                        <Label htmlFor="moneda">Moneda *</Label>
                                        <Select value={moneda} onValueChange={(value) => setMoneda(value as Moneda)}>
                                            <SelectTrigger id="moneda">
                                                <SelectValue placeholder="Seleccione moneda" />
                                            </SelectTrigger>
                                            <SelectContent>
                                                <SelectItem value={Moneda.PESOS_ARGENTINOS}>Pesos Argentinos</SelectItem>
                                                <SelectItem value={Moneda.DOLARES}>Dólares</SelectItem>
                                                <SelectItem value={Moneda.EUROS}>Euros</SelectItem>
                                                <SelectItem value={Moneda.REALES}>Reales</SelectItem>
                                                <SelectItem value={Moneda.PESOS_URUGUAYOS}>Pesos Uruguayos</SelectItem>
                                            </SelectContent>
                                        </Select>
                                    </div>
                                    <div className="space-y-2">
                                        <Label htmlFor="cotizacion">Cotización *</Label>
                                        <Input
                                            id="cotizacion"
                                            type="number"
                                            step="0.01"
                                            value={cotizacion}
                                            onChange={(e) => setCotizacion(e.target.value)}
                                            placeholder="1.0"
                                        />
                                    </div>
                                    <div className="space-y-2">
                                        <Label htmlFor="paymentMethod">Método de Pago *</Label>
                                        <Select value={paymentMethod} onValueChange={handlePaymentMethodChange}>
                                            <SelectTrigger id="paymentMethod">
                                                <SelectValue placeholder="Seleccione un método" />
                                            </SelectTrigger>
                                            <SelectContent>
                                                <SelectItem value="EFECTIVO">Efectivo</SelectItem>
                                                <SelectItem value="CHEQUE">Cheque</SelectItem>
                                                <SelectItem value="TARJETA_CREDITO">Tarjeta de Crédito</SelectItem>
                                                <SelectItem value="TARJETA_DEBITO">Tarjeta de Débito</SelectItem>
                                            </SelectContent>
                                        </Select>
                                    </div>
                                </div>

                                {paymentMethod === "EFECTIVO" && (
                                    <div className="space-y-4 rounded-lg border border-slate-200 bg-slate-50 p-4 dark:border-slate-700 dark:bg-slate-900">
                                        <h4 className="font-semibold">Datos de Pago en Efectivo</h4>
                                        <div className="space-y-2">
                                            <Label htmlFor="cashAmount">Importe Entregado *</Label>
                                            <Input
                                                id="cashAmount"
                                                type="number"
                                                step="0.01"
                                                value={cashAmount}
                                                onChange={(e) => setCashAmount(e.target.value)}
                                                placeholder="0.00"
                                            />
                                        </div>
                                    </div>
                                )}

                                {paymentMethod === "CHEQUE" && (
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
                                                    placeholder="1234567890123456"
                                                    maxLength={16}
                                                />
                                            </div>
                                            <div className="space-y-2">
                                                <Label htmlFor="cardNetwork">Red de Pago *</Label>
                                                <Select value={cardNetwork} onValueChange={setCardNetwork}>
                                                    <SelectTrigger id="cardNetwork">
                                                        <SelectValue placeholder="Seleccione red" />
                                                    </SelectTrigger>
                                                    <SelectContent>
                                                        <SelectItem value="VISA">Visa</SelectItem>
                                                        <SelectItem value="MASTERCARD">Mastercard</SelectItem>
                                                    </SelectContent>
                                                </Select>
                                            </div>
                                            <div className="space-y-2">
                                                <Label htmlFor="cardBank">Banco *</Label>
                                                <Input
                                                    id="cardBank"
                                                    value={cardBank}
                                                    onChange={(e) => setCardBank(e.target.value)}
                                                    placeholder="Nombre del banco"
                                                />
                                            </div>
                                            <div className="space-y-2">
                                                <Label htmlFor="cardCvv">Código de Seguridad *</Label>
                                                <Input
                                                    id="cardCvv"
                                                    type="text"
                                                    maxLength={4}
                                                    value={cardCvv}
                                                    onChange={(e) => setCardCvv(e.target.value)}
                                                    placeholder="123"
                                                />
                                            </div>
                                            <div className="space-y-2">
                                                <Label htmlFor="cardExpiry">Fecha de Vencimiento *</Label>
                                                <Input
                                                    id="cardExpiry"
                                                    type="date"
                                                    value={cardExpiry}
                                                    onChange={(e) => setCardExpiry(e.target.value)}
                                                />
                                            </div>
                                            {paymentMethod === "TARJETA_CREDITO" && (
                                                <div className="space-y-2">
                                                    <Label htmlFor="cardQuota">Cuotas *</Label>
                                                    <Input
                                                        id="cardQuota"
                                                        type="number"
                                                        min="1"
                                                        value={cardQuota}
                                                        onChange={(e) => setCardQuota(e.target.value)}
                                                        placeholder="1"
                                                    />
                                                </div>
                                            )}
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
                                    <Button
                                        onClick={handleAddPaymentMethod}
                                        className="flex-1"
                                        disabled={!paymentMethod || !paymentAmount}
                                        variant="default"
                                    >
                                        Agregar Medio de Pago
                                    </Button>
                                    <Button
                                        onClick={handleCancelarPago}
                                        variant="outline"
                                    >
                                        Cancelar
                                    </Button>
                                </div>

                                {/* Mostrar medios acumulados */}
                                {mediosPagoAcumulados.length > 0 && (
                                    <div className="rounded-lg border border-blue-200 bg-blue-50 p-4 dark:border-blue-800 dark:bg-blue-950">
                                        <h4 className="mb-3 font-semibold text-blue-900 dark:text-blue-100">
                                            Medios de Pago Agregados ({mediosPagoAcumulados.length})
                                        </h4>
                                        <div className="space-y-2">
                                            {mediosPagoAcumulados.map((medio, idx) => (
                                                <div key={idx} className="flex items-center justify-between rounded bg-white p-2 dark:bg-slate-800">
                                                    <div className="flex flex-col flex-1">
                                                        <span className="text-sm font-medium">
                                                            {medio.tipoMedio === TipoMedioPago.EFECTIVO && "Efectivo"}
                                                            {medio.tipoMedio === TipoMedioPago.CHEQUE && `Cheque #${(medio as any).numeroCheque}`}
                                                            {medio.tipoMedio === TipoMedioPago.TARJETA_CREDITO && `Tarj. Crédito: ${(medio as any).numeroDeTarjeta?.slice(-4)}`}
                                                            {medio.tipoMedio === TipoMedioPago.TARJETA_DEBITO && `Tarj. Débito: ${(medio as any).numeroDeTarjeta?.slice(-4)}`}
                                                        </span>
                                                        <span className="text-xs text-slate-500">
                                                            {medio.moneda === Moneda.PESOS_ARGENTINOS ? "Pesos" : 
                                                             medio.moneda === Moneda.DOLARES ? "Dólares" :
                                                             medio.moneda === Moneda.EUROS ? "Euros" :
                                                             medio.moneda === Moneda.REALES ? "Reales" : "Pesos Uruguayos"}
                                                        </span>
                                                    </div>
                                                    <div className="flex items-center gap-2">
                                                        <span className="font-semibold">${medio.monto.toFixed(2)}</span>
                                                        <Button
                                                            onClick={() => handleRemoverMedioPago(idx)}
                                                            variant="ghost"
                                                            size="sm"
                                                            className="h-6 w-6 p-0 hover:bg-red-100 hover:text-red-600 dark:hover:bg-red-900 dark:hover:text-red-300"
                                                        >
                                                            <X className="h-4 w-4" />
                                                        </Button>
                                                    </div>
                                                </div>
                                            ))}
                                        </div>
                                        <div className="mt-3 border-t border-blue-200 pt-2 dark:border-blue-800">
                                            <div className="flex justify-between font-semibold">
                                                <span>Total Acumulado (en pesos):</span>
                                                <span className="text-lg">${montoAcumulado.toFixed(2)}</span>
                                            </div>
                                            <div className="flex justify-between text-sm text-blue-700 dark:text-blue-300">
                                                <span>Monto de Factura:</span>
                                                <span>${selectedInvoice?.importeTotal.toFixed(2)}</span>
                                            </div>
                                            {montoAcumulado < (selectedInvoice?.importeTotal || 0) ? (
                                                <div className="mt-2 flex justify-between rounded bg-yellow-100 px-2 py-1 text-sm font-semibold text-yellow-800 dark:bg-yellow-900 dark:text-yellow-100">
                                                    <span>Falta Pagar:</span>
                                                    <span>${(selectedInvoice!.importeTotal - montoAcumulado).toFixed(2)}</span>
                                                </div>
                                            ) : (
                                                <>
                                                    <div className="mt-2 flex justify-between rounded bg-green-100 px-2 py-1 text-sm font-semibold text-green-800 dark:bg-green-900 dark:text-green-100">
                                                        <span>✓ Listo para finalizar</span>
                                                        {montoAcumulado > selectedInvoice!.importeTotal && (
                                                            <span>Vuelto: ${(montoAcumulado - selectedInvoice!.importeTotal).toFixed(2)}</span>
                                                        )}
                                                    </div>
                                                    <div className="mt-3 flex gap-2">
                                                        <Button
                                                            onClick={handleFinalizarPago}
                                                            className="flex-1"
                                                            variant="default"
                                                        >
                                                            Finalizar Pago
                                                        </Button>
                                                    </div>
                                                </>
                                            )}
                                        </div>
                                    </div>
                                )}
                            </CardContent>
                        </Card>
                    )}
                </div>
            </main>
        </div>
    )
}
