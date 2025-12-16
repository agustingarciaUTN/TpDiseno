"use client"

import { useState } from "react"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { Badge } from "@/components/ui/badge"
import {
    AlertCircle,
    DollarSign,
    CreditCard,
    X,
    Home,
    Search,
    Wallet,
    CheckCircle2,
    Banknote,
    Calendar,
    PlusCircle,
    Trash2
} from "lucide-react"

// Importaciones de tu lógica de negocio (asegúrate que las rutas sean correctas)
import { buscarFacturasPendientes, registrarPago } from "@/lib/api"
import { DtoFactura, Moneda, TipoMedioPago, DtoMedioPago, DtoPago, EstadoFactura } from "@/lib/types"

type PaymentMethod = "EFECTIVO" | "CHEQUE" | "TARJETA_CREDITO" | "TARJETA_DEBITO"

export default function RegistrarPagoPage() {
    // --- ESTADOS (Lógica original) ---
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

    // Estados para campos de pago detallados
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

    // --- MANEJADORES (Lógica original) ---

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
        setMediosPagoAcumulados([]) // Resetear medios al cambiar factura
        setMontoAcumulado(0)
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

        // Validaciones específicas
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

        // Crear el medio de pago
        const fechaActual = new Date().toISOString().split('T')[0]
        const nuevoMedio: DtoMedioPago = {
            tipoMedio: TipoMedioPago[paymentMethod as keyof typeof TipoMedioPago],
            monto: amount,
            moneda: moneda,
            fechaDePago: fechaActual
        } as DtoMedioPago

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

        const montoEnPesos = moneda === Moneda.PESOS_ARGENTINOS
            ? amount
            : amount * cotizacionNum

        setMediosPagoAcumulados([...mediosPagoAcumulados, nuevoMedio])
        const nuevoMontoAcumuladoEnPesos = montoAcumulado + montoEnPesos
        setMontoAcumulado(nuevoMontoAcumuladoEnPesos)

        const faltaPagar = Math.max(0, selectedInvoice.importeTotal - nuevoMontoAcumuladoEnPesos)

        setPaymentMethod("")
        setPaymentAmount(faltaPagar > 0 ? faltaPagar.toFixed(2) : "") // Sugerir el restante
        setMoneda(Moneda.PESOS_ARGENTINOS)
        setCotizacion("1")
        resetPaymentFields()
        setError("")

        setSuccess("Medio de pago agregado correctamente")
        setTimeout(() => setSuccess(""), 3000)
    }

    const handleRemoverMedioPago = (indice: number) => {
        const medioARemover = mediosPagoAcumulados[indice]
        const montoEnPesos = medioARemover.moneda === Moneda.PESOS_ARGENTINOS
            ? medioARemover.monto
            : medioARemover.monto * Number.parseFloat(cotizacion)

        const nuevosMedios = mediosPagoAcumulados.filter((_, idx) => idx !== indice)
        setMediosPagoAcumulados(nuevosMedios)

        const nuevoMontoAcumulado = Math.max(0, montoAcumulado - montoEnPesos)
        setMontoAcumulado(nuevoMontoAcumulado)

        setError("")
        setSuccess("Medio removido")
        setTimeout(() => setSuccess(""), 2000)
    }

    const handleFinalizarPago = async () => {
        if (!selectedInvoice || mediosPagoAcumulados.length === 0) return

        const cotizacionNum = Number.parseFloat(cotizacion)

        if (montoAcumulado < selectedInvoice.importeTotal) {
            setError(`Monto insuficiente. Faltan $${(selectedInvoice.importeTotal - montoAcumulado).toFixed(2)}`)
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

            setSuccess(resultado.mensaje)
            setChange(resultado.vuelto)

            setInvoices((prev) => prev.map((inv) =>
                inv.numeroFactura === selectedInvoice.numeroFactura
                    ? { ...inv, estadoFactura: EstadoFactura.PAGADA }
                    : inv
            ))

            // Retraso para ver el éxito antes de limpiar
            setTimeout(() => {
                handleCancelarPago() // Limpia todo
            }, 4000)

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

    return (
        <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50 dark:from-slate-950 dark:via-slate-900 dark:to-slate-950">
            <div className="mx-auto max-w-5xl px-4 py-8 sm:px-6 lg:px-8">

                {/* --- HEADER --- */}
                <div className="mb-8 space-y-2">
                    <div className="flex items-center justify-between">
                        <div className="flex items-center gap-3">
                            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-teal-600 text-white shadow-md">
                                <DollarSign className="h-6 w-6" />
                            </div>
                            <div>
                                <p className="text-xs font-semibold uppercase tracking-wider text-teal-600 dark:text-teal-400">
                                    Caso de Uso 08
                                </p>
                                <h1 className="text-3xl font-bold text-slate-900 dark:text-slate-50">Registrar Pago</h1>
                            </div>
                        </div>
                        <Button
                            variant="outline"
                            className="bg-white/80 backdrop-blur-sm gap-2 hover:bg-slate-100"
                            asChild
                        >
                            <Link href="/">
                                <Home className="h-4 w-4" />
                                Volver al Menú Principal
                            </Link>
                        </Button>
                    </div>
                    <p className="text-slate-600 dark:text-slate-400 ml-1">
                        Gestión de cobros y registro de múltiples medios de pago.
                    </p>
                </div>

                {/* --- CARD DE BÚSQUEDA --- */}
                <Card className="shadow-lg border-slate-200 dark:border-slate-800 mb-6">
                    <CardHeader className="border-b border-slate-100 bg-slate-50/50 pb-4">
                        <CardTitle className="text-lg font-medium text-slate-800">Buscar Facturas Pendientes</CardTitle>
                        <CardDescription>Ingrese el número de habitación para consultar deudas.</CardDescription>
                    </CardHeader>
                    <CardContent className="pt-6">
                        <div className="flex gap-4 items-start">
                            <div className="flex-1 space-y-2 max-w-xs">
                                <Label htmlFor="roomNumber" className="text-slate-600">Nro Habitación</Label>
                                <div className="relative">
                                    <Search className="absolute left-3 top-2.5 h-4 w-4 text-slate-400" />
                                    <Input
                                        id="roomNumber"
                                        value={roomNumber}
                                        onChange={(e) => handleRoomNumberChange(e.target.value)}
                                        placeholder="Ej: 101"
                                        maxLength={3}
                                        className={`pl-10 bg-white ${roomNumberError ? "border-red-500 ring-red-200" : ""}`}
                                        onKeyDown={(e) => e.key === "Enter" && handleSearchRoom()}
                                    />
                                </div>
                                {roomNumberError && <p className="text-xs text-red-500 font-medium">{roomNumberError}</p>}
                            </div>
                            <Button
                                onClick={handleSearchRoom}
                                disabled={isSearching || !!roomNumberError}
                                className="mt-8 bg-teal-600 hover:bg-teal-700 text-white shadow-sm"
                            >
                                {isSearching ? "Buscando..." : "Buscar Facturas"}
                            </Button>
                        </div>

                        {error && !selectedInvoice && (
                            <Alert variant="destructive" className="mt-6">
                                <AlertCircle className="h-4 w-4" />
                                <AlertDescription>{error}</AlertDescription>
                            </Alert>
                        )}
                    </CardContent>
                </Card>

                {/* --- RESULTADOS (FACTURAS) --- */}
                {invoices.length > 0 && !selectedInvoice && (
                    <div className="space-y-4 animate-in fade-in slide-in-from-bottom-4">
                        <h3 className="text-lg font-semibold text-slate-800 ml-1">Facturas Pendientes</h3>
                        <div className="grid grid-cols-1 gap-4">
                            {invoices.map((invoice) => (
                                <Card
                                    key={invoice.numeroFactura}
                                    onClick={() => handleSelectInvoice(invoice)}
                                    className={`
                                        cursor-pointer transition-all hover:shadow-md border-l-4
                                        ${invoice.estadoFactura === "PAGADA"
                                        ? "border-l-green-500 border-slate-200 opacity-60"
                                        : "border-l-amber-500 border-slate-200 hover:border-teal-400"}
                                    `}
                                >
                                    <CardContent className="p-4 flex items-center justify-between">
                                        <div className="flex items-center gap-4">
                                            <div className="h-10 w-10 rounded-full bg-slate-100 flex items-center justify-center text-slate-500">
                                                <Wallet className="h-5 w-5" />
                                            </div>
                                            <div>
                                                <p className="font-bold text-slate-900">{invoice.numeroFactura}</p>
                                                <p className="text-sm text-slate-500">
                                                    Resp: {invoice.nombreResponsable} {invoice.apellidoResponsable}
                                                </p>
                                            </div>
                                        </div>
                                        <div className="text-right">
                                            <p className="text-2xl font-bold text-slate-900">${invoice.importeTotal.toLocaleString('es-AR')}</p>
                                            <Badge variant={invoice.estadoFactura === "PAGADA" ? "default" : "secondary"} className={invoice.estadoFactura === "PAGADA" ? "bg-green-600" : "bg-amber-100 text-amber-800 hover:bg-amber-200"}>
                                                {invoice.estadoFactura}
                                            </Badge>
                                        </div>
                                    </CardContent>
                                </Card>
                            ))}
                        </div>
                    </div>
                )}

                {/* --- DETALLE DE PAGO --- */}
                {showPaymentFields && selectedInvoice && (
                    <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 animate-in zoom-in-95 duration-300">

                        {/* COLUMNA IZQUIERDA: FORMULARIO */}
                        <div className="lg:col-span-2 space-y-6">
                            <Card className="shadow-lg border-slate-200">
                                <CardHeader className="bg-slate-50 border-b border-slate-100">
                                    <CardTitle className="flex items-center gap-2">
                                        <CreditCard className="h-5 w-5 text-teal-600" />
                                        Nuevo Medio de Pago
                                    </CardTitle>
                                </CardHeader>
                                <CardContent className="p-6 space-y-6">

                                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                        <div className="space-y-2">
                                            <Label>Método de Pago *</Label>
                                            <Select value={paymentMethod} onValueChange={(val) => handlePaymentMethodChange(val as PaymentMethod)}>
                                                <SelectTrigger className="bg-white"><SelectValue placeholder="Seleccione..." /></SelectTrigger>
                                                <SelectContent>
                                                    <SelectItem value="EFECTIVO">Efectivo</SelectItem>
                                                    <SelectItem value="CHEQUE">Cheque</SelectItem>
                                                    <SelectItem value="TARJETA_CREDITO">Tarjeta Crédito</SelectItem>
                                                    <SelectItem value="TARJETA_DEBITO">Tarjeta Débito</SelectItem>
                                                </SelectContent>
                                            </Select>
                                        </div>
                                        <div className="space-y-2">
                                            <Label>Moneda *</Label>
                                            <Select value={moneda} onValueChange={(val) => setMoneda(val as Moneda)}>
                                                <SelectTrigger className="bg-white"><SelectValue /></SelectTrigger>
                                                <SelectContent>
                                                    <SelectItem value={Moneda.PESOS_ARGENTINOS}>Pesos (ARS)</SelectItem>
                                                    <SelectItem value={Moneda.DOLARES}>Dólares (USD)</SelectItem>
                                                    <SelectItem value={Moneda.EUROS}>Euros (EUR)</SelectItem>
                                                    <SelectItem value={Moneda.REALES}>Reales (BRL)</SelectItem>
                                                </SelectContent>
                                            </Select>
                                        </div>
                                    </div>

                                    {/* Campos Dinámicos */}
                                    <div className="bg-slate-50 p-4 rounded-lg border border-slate-200 space-y-4">
                                        {paymentMethod === "" && <p className="text-sm text-slate-500 text-center italic">Seleccione un método para ver los campos requeridos.</p>}

                                        {paymentMethod === "EFECTIVO" && (
                                            <div className="space-y-2 animate-in fade-in">
                                                <Label>Importe Entregado (Caja)</Label>
                                                <div className="relative">
                                                    <Banknote className="absolute left-3 top-2.5 h-4 w-4 text-slate-400" />
                                                    <Input type="number" value={cashAmount} onChange={(e) => setCashAmount(e.target.value)} className="pl-10 bg-white" placeholder="0.00" />
                                                </div>
                                            </div>
                                        )}

                                        {paymentMethod === "CHEQUE" && (
                                            <div className="grid grid-cols-2 gap-4 animate-in fade-in">
                                                <div className="space-y-2"><Label>Nro. Cheque</Label><Input value={checkNumber} onChange={(e) => setCheckNumber(e.target.value)} className="bg-white" /></div>
                                                <div className="space-y-2"><Label>Banco</Label><Input value={checkBank} onChange={(e) => setCheckBank(e.target.value)} className="bg-white" /></div>
                                                <div className="space-y-2"><Label>Plaza</Label><Input value={checkPlaza} onChange={(e) => setCheckPlaza(e.target.value)} className="bg-white" /></div>
                                                <div className="space-y-2"><Label>Fecha Cobro</Label><Input type="date" value={checkDate} onChange={(e) => setCheckDate(e.target.value)} className="bg-white" /></div>
                                            </div>
                                        )}

                                        {(paymentMethod === "TARJETA_CREDITO" || paymentMethod === "TARJETA_DEBITO") && (
                                            <div className="grid grid-cols-2 gap-4 animate-in fade-in">
                                                <div className="col-span-2 space-y-2"><Label>Número Tarjeta</Label><Input value={cardNumber} onChange={(e) => setCardNumber(e.target.value)} placeholder="0000 0000 0000 0000" maxLength={16} className="bg-white" /></div>
                                                <div className="space-y-2">
                                                    <Label>Red</Label>
                                                    <Select value={cardNetwork} onValueChange={setCardNetwork}>
                                                        <SelectTrigger className="bg-white"><SelectValue placeholder="Red..." /></SelectTrigger>
                                                        <SelectContent><SelectItem value="VISA">Visa</SelectItem><SelectItem value="MASTERCARD">Mastercard</SelectItem></SelectContent>
                                                    </Select>
                                                </div>
                                                <div className="space-y-2"><Label>Banco</Label><Input value={cardBank} onChange={(e) => setCardBank(e.target.value)} className="bg-white" /></div>
                                                <div className="space-y-2"><Label>Vencimiento</Label><Input type="date" value={cardExpiry} onChange={(e) => setCardExpiry(e.target.value)} className="bg-white" /></div>
                                                <div className="space-y-2"><Label>CVV</Label><Input value={cardCvv} onChange={(e) => setCardCvv(e.target.value)} maxLength={4} className="bg-white" /></div>
                                                {paymentMethod === "TARJETA_CREDITO" && (
                                                    <div className="col-span-2 space-y-2"><Label>Cuotas</Label><Input type="number" value={cardQuota} onChange={(e) => setCardQuota(e.target.value)} placeholder="1" className="bg-white" /></div>
                                                )}
                                            </div>
                                        )}
                                    </div>

                                    {/* Monto y Cotización */}
                                    <div className="grid grid-cols-2 gap-4">
                                        <div className="space-y-2">
                                            <Label>Cotización</Label>
                                            <Input type="number" value={cotizacion} onChange={(e) => setCotizacion(e.target.value)} disabled={moneda === Moneda.PESOS_ARGENTINOS} className="bg-white" />
                                        </div>
                                        <div className="space-y-2">
                                            <Label className="text-teal-700 font-bold">Monto a Imputar ({moneda})</Label>
                                            <Input type="number" value={paymentAmount} onChange={(e) => setPaymentAmount(e.target.value)} className="bg-white font-bold border-teal-200 focus:ring-teal-500" />
                                        </div>
                                    </div>

                                    {error && <p className="text-sm text-red-500 font-medium">{error}</p>}
                                    {success && <p className="text-sm text-green-600 font-medium flex items-center gap-2"><CheckCircle2 className="h-4 w-4"/> {success}</p>}

                                    <Button onClick={handleAddPaymentMethod} className="w-full bg-teal-600 hover:bg-teal-700 text-white shadow-md" disabled={!paymentMethod || !paymentAmount}>
                                        <PlusCircle className="mr-2 h-4 w-4" /> Agregar Medio de Pago
                                    </Button>
                                </CardContent>
                            </Card>
                        </div>

                        {/* COLUMNA DERECHA: RESUMEN */}
                        <div className="lg:col-span-1 space-y-6">
                            <Card className="shadow-lg border-slate-200 bg-slate-50/50 h-full">
                                <CardHeader className="border-b border-slate-100 pb-4">
                                    <CardTitle className="text-lg">Resumen de Pago</CardTitle>
                                    <p className="text-sm text-slate-500">Factura: {selectedInvoice.numeroFactura}</p>
                                </CardHeader>
                                <CardContent className="pt-6 flex flex-col justify-between h-[calc(100%-80px)]">
                                    <div>
                                        <div className="mb-6 text-center">
                                            <p className="text-sm text-slate-500 uppercase tracking-wider">Total Factura</p>
                                            <p className="text-3xl font-bold text-slate-900">${selectedInvoice.importeTotal.toLocaleString('es-AR')}</p>
                                        </div>

                                        <div className="space-y-3">
                                            <p className="text-sm font-semibold text-slate-700 border-b pb-2">Pagos Acumulados</p>
                                            {mediosPagoAcumulados.length === 0 && <p className="text-sm text-slate-400 italic">No se han ingresado pagos.</p>}

                                            {mediosPagoAcumulados.map((medio, idx) => (
                                                <div key={idx} className="flex justify-between items-center text-sm bg-white p-2 rounded border shadow-sm">
                                                    <div>
                                                        <span className="font-bold block text-slate-700">{medio.tipoMedio.replace('_', ' ')}</span>
                                                        <span className="text-xs text-slate-500">
                                                            {medio.monto} {medio.moneda !== "PESOS_ARGENTINOS" && `(${medio.moneda})`}
                                                        </span>
                                                    </div>
                                                    <div className="flex items-center gap-2">
                                                        <span className="font-medium text-teal-700">
                                                            ${(medio.moneda === Moneda.PESOS_ARGENTINOS ? medio.monto : medio.monto * parseFloat(cotizacion)).toFixed(2)}
                                                        </span>
                                                        <Button variant="ghost" size="icon" className="h-6 w-6 text-red-400 hover:text-red-600" onClick={() => handleRemoverMedioPago(idx)}>
                                                            <Trash2 className="h-3 w-3" />
                                                        </Button>
                                                    </div>
                                                </div>
                                            ))}
                                        </div>
                                    </div>

                                    <div className="mt-8 border-t border-slate-200 pt-4 space-y-4">
                                        <div className="flex justify-between text-sm">
                                            <span>Acumulado:</span>
                                            <span className="font-bold">${montoAcumulado.toFixed(2)}</span>
                                        </div>

                                        {montoAcumulado < selectedInvoice.importeTotal ? (
                                            <div className="flex justify-between text-red-600 font-bold bg-red-50 p-2 rounded">
                                                <span>Falta:</span>
                                                <span>${(selectedInvoice.importeTotal - montoAcumulado).toFixed(2)}</span>
                                            </div>
                                        ) : (
                                            <div className="space-y-2">
                                                <div className="flex justify-between text-green-700 font-bold bg-green-50 p-2 rounded">
                                                    <span>¡Cubierto!</span>
                                                    <span>Vuelto: ${(montoAcumulado - selectedInvoice.importeTotal).toFixed(2)}</span>
                                                </div>
                                                <Button onClick={handleFinalizarPago} className="w-full bg-green-600 hover:bg-green-700 text-white shadow-lg animate-pulse">
                                                    Finalizar y Cerrar Factura
                                                </Button>
                                            </div>
                                        )}

                                        <Button variant="outline" onClick={handleCancelarPago} className="w-full">Cancelar Operación</Button>
                                    </div>
                                </CardContent>
                            </Card>
                        </div>
                    </div>
                )}
            </div>
        </div>
    )
}