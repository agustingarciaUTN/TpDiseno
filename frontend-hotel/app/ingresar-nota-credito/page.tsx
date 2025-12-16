"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import Link from "next/link"

// UI Components
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { Checkbox } from "@/components/ui/checkbox"
import { Badge } from "@/components/ui/badge"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog"

// Icons
import {
    AlertCircle,
    CheckCircle2,
    User,
    Home,
    FileMinus,
    Loader2,
    FileText, Search
} from "lucide-react"

// IMPORTAMOS LA API REAL
import { buscarResponsable, buscarFacturasPorResponsable, generarNotaCredito } from "@/lib/api"

type DocumentType = "DNI" | "LE" | "LC" | "Pasaporte" | "OTROS"

type Invoice = {
    id: string
    nroFactura: string
    fechaConfeccion: string
    importeNeto: number
    iva: number
    importeTotal: number
    responsablePago: string
}

type Person = {
    id: number // Agregamos ID real de base de datos
    nombre: string
    apellido: string // O Razón Social si es empresa
    cuit?: string
    documentType?: string
    documentNumber?: string
}

type CreditNote = {
    nroNotaCredito: string
    responsablePago: string
    importeNeto: number
    iva: number
    importeTotal: number
}

export default function IngresarNotaCreditoPage() {
    const router = useRouter()

    // --- ESTADOS ---
    const [searchMethod, setSearchMethod] = useState<"cuit" | "document">("cuit")
    const [cuit, setCuit] = useState("")
    const [documentType, setDocumentType] = useState<DocumentType>("DNI")
    const [documentNumber, setDocumentNumber] = useState("")

    const [invoices, setInvoices] = useState<Invoice[]>([])
    const [personInfo, setPersonInfo] = useState<Person | null>(null)
    const [selectedInvoices, setSelectedInvoices] = useState<Set<string>>(new Set())

    const [error, setError] = useState("")
    const [isSearching, setIsSearching] = useState(false)
    const [showResults, setShowResults] = useState(false)
    const [showConfirmation, setShowConfirmation] = useState(false)
    const [isProcessing, setIsProcessing] = useState(false) // Nuevo estado para cuando genera la NC
    const [creditNoteGenerated, setCreditNoteGenerated] = useState<CreditNote | null>(null)

    // --- VALIDACIONES ---
    const validateCuit = (value: string) => {
        if (!value) return ""
        if (!/^[\d-]+$/.test(value)) return "El CUIT contiene caracteres inválidos"
        const cleanValue = value.replace(/\D/g, "")
        if (cleanValue.length !== 11) return "El CUIT debe tener 11 números"
        return ""
    }

    const validateDocumentNumber = (value: string, type: DocumentType) => {
        if (!value) return ""
        const cleanValue = value.replace(/\./g, "").replace(/\s/g, "")
        if (type === "DNI" || type === "LE" || type === "LC") {
            if (!/^\d*$/.test(cleanValue)) return "Para este documento solo se permiten números"
            if (cleanValue.length < 7 || cleanValue.length > 8) return "Debe tener entre 7 y 8 números"
        }
        return ""
    }

    const handleCuitChange = (value: string) => {
        if (/^[\d-]*$/.test(value) && value.length <= 13) setCuit(value)
    }

    const handleDocumentNumberChange = (value: string) => {
        const upperValue = value.toUpperCase()
        if (upperValue.length <= 15) setDocumentNumber(upperValue)
    }

    // --- LÓGICA DE NEGOCIO REAL (CONECTADA AL BACKEND) ---
    const handleSearch = async () => {
        setError("")
        setInvoices([])
        setPersonInfo(null)
        setShowResults(false)

        // Validaciones pre-búsqueda
        if (searchMethod === "cuit") {
            if(!cuit) return setError("Ingrese el CUIT")
            if(validateCuit(cuit)) return setError(validateCuit(cuit))
        } else {
            if(!documentNumber) return setError("Ingrese el documento")
            if(validateDocumentNumber(documentNumber, documentType)) return setError(validateDocumentNumber(documentNumber, documentType))
        }

        setIsSearching(true)

        try {
            // 1. Buscamos al Responsable en la BD
            const cleanCuit = cuit.replace(/\D/g, "")
            const cleanDoc = documentNumber.replace(/\./g, "").replace(/\s/g, "")

            const responsable = await buscarResponsable(searchMethod, searchMethod === "cuit" ? cleanCuit : cleanDoc, documentType);

            if (!responsable) {
                throw new Error("No se encontró ningún responsable con esos datos.")
            }

            // Guardamos info del responsable (Mapeamos lo que viene del back)
            setPersonInfo({
                id: responsable.idResponsable,
                nombre: responsable.nombre || responsable.razonSocial, // Maneja Persona o Empresa
                apellido: responsable.apellido || "",
                cuit: responsable.cuit,
                documentType: responsable.tipoDocumento,
                documentNumber: responsable.nroDocumento
            })

            // 2. Buscamos sus facturas
            const facturasEncontradas = await buscarFacturasPorResponsable(responsable.idResponsable);

            if (!facturasEncontradas || facturasEncontradas.length === 0) {
                setError(`El responsable ${responsable.nombre || responsable.razonSocial} existe, pero no tiene facturas registradas.`)
                // Mostramos al responsable pero sin facturas
                setShowResults(true)
            } else {
                // Mapeamos las facturas del back al front
                const facturasMapeadas: Invoice[] = facturasEncontradas.map((f: any) => ({
                    id: f.idFactura || f.id, // Asegúrate que tu back mande el ID
                    nroFactura: f.numeroFactura,
                    fechaConfeccion: f.fechaEmision,
                    importeNeto: f.importeNeto,
                    iva: f.iva,
                    importeTotal: f.importeTotal,
                    responsablePago: `${responsable.nombre || responsable.razonSocial} ${responsable.apellido || ""}`.trim()
                }))

                setInvoices(facturasMapeadas)
                setShowResults(true)
                setSelectedInvoices(new Set())
            }

        } catch (err: any) {
            console.error(err)
            setError(err.message || "Error al conectar con el servidor")
        } finally {
            setIsSearching(false)
        }
    }

    const toggleInvoiceSelection = (invoiceId: string) => {
        const newSelected = new Set(selectedInvoices)
        if (newSelected.has(invoiceId)) newSelected.delete(invoiceId)
        else newSelected.add(invoiceId)
        setSelectedInvoices(newSelected)
    }

    const calculateTotalSelected = () => {
        const selected = invoices.filter((inv) => selectedInvoices.has(inv.id))
        return {
            neto: selected.reduce((sum, inv) => sum + inv.importeNeto, 0),
            iva: selected.reduce((sum, inv) => sum + inv.iva, 0),
            total: selected.reduce((sum, inv) => sum + inv.importeTotal, 0),
        }
    }

    const handleAccept = () => {
        if (selectedInvoices.size === 0) {
            setError("Debe seleccionar al menos una factura")
            return
        }
        setShowConfirmation(true)
    }

    const handleConfirmCreditNote = async () => {
        setIsProcessing(true)
        setShowConfirmation(false)

        try {
            // Preparamos los datos para el Back
            const dtoNotaCredito = {
                idResponsable: personInfo?.id,
                idsFacturasACancelar: Array.from(selectedInvoices),
                fecha: new Date().toISOString()
            }

            const resultado = await generarNotaCredito(dtoNotaCredito);

            // Mapeamos la respuesta para mostrar el éxito
            setCreditNoteGenerated({
                nroNotaCredito: resultado.nroNotaCredito,
                responsablePago: resultado.responsableNombre,
                importeNeto: resultado.importeNeto,
                iva: resultado.importeIva,
                importeTotal: resultado.importeTotal
            })

        } catch (err: any) {
            setError(err.message || "Error al generar la Nota de Crédito")
            setShowConfirmation(false) // Volvemos si falló
        } finally {
            setIsProcessing(false)
        }
    }

    const handleCancel = () => {
        router.push("/")
    }

    const handleNewSearch = () => {
        setCuit("")
        setDocumentNumber("")
        setInvoices([])
        setPersonInfo(null)
        setSelectedInvoices(new Set())
        setShowResults(false)
        setError("")
        setCreditNoteGenerated(null)
    }

    const totals = calculateTotalSelected()

    // --- RENDERIZADO (IGUAL QUE ANTES) ---
    return (
        <div className="min-h-screen bg-gradient-to-br from-slate-50 via-indigo-50 to-purple-50 dark:from-slate-950 dark:via-slate-900 dark:to-slate-950">
            <div className="mx-auto max-w-5xl px-4 py-8 sm:px-6 lg:px-8">

                {/* --- HEADER --- */}
                <div className="mb-8 space-y-2">
                    <div className="flex items-center justify-between">
                        <div className="flex items-center gap-3">
                            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-indigo-600 text-white shadow-md">
                                <FileMinus className="h-6 w-6" />
                            </div>
                            <div>
                                <p className="text-xs font-semibold uppercase tracking-wider text-indigo-600 dark:text-indigo-400">
                                    Caso de Uso 03
                                </p>
                                <h1 className="text-3xl font-bold text-slate-900 dark:text-slate-50">Ingresar Nota de Crédito</h1>
                            </div>
                        </div>
                        <Button
                            variant="outline"
                            className="bg-white/80 backdrop-blur-sm gap-2 hover:bg-slate-100"
                            asChild
                        >
                            <Link href="/">
                                <Home className="h-4 w-4" />
                                Volver al Menú
                            </Link>
                        </Button>
                    </div>
                    <p className="text-slate-600 dark:text-slate-400 ml-1">
                        Busque facturas y genere notas de crédito para correcciones o devoluciones.
                    </p>
                </div>

                {/* --- VISTA DE ÉXITO --- */}
                {creditNoteGenerated ? (
                    <Card className="border-indigo-200 shadow-xl bg-white/50 backdrop-blur animate-in zoom-in-95 duration-500">
                        <CardHeader className="text-center border-b border-indigo-100 bg-indigo-50/50 pb-8 pt-8">
                            <div className="mx-auto w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mb-4 shadow-sm">
                                <CheckCircle2 className="h-8 w-8 text-green-600" />
                            </div>
                            <CardTitle className="text-2xl text-indigo-900">Operación Exitosa</CardTitle>
                            <CardDescription>La Nota de Crédito ha sido generada correctamente</CardDescription>
                        </CardHeader>
                        <CardContent className="pt-8 pb-8 space-y-6 max-w-lg mx-auto">
                            <div className="bg-white border border-slate-200 rounded-xl p-6 shadow-sm space-y-4">
                                <div className="flex justify-between items-center pb-4 border-b border-slate-100">
                                    <span className="text-sm text-slate-500 uppercase tracking-wider">Nro. Comprobante</span>
                                    <span className="text-xl font-mono font-bold text-indigo-700">{creditNoteGenerated.nroNotaCredito}</span>
                                </div>
                                <div className="space-y-2">
                                    <div className="flex justify-between text-sm">
                                        <span className="text-slate-600">Responsable:</span>
                                        <span className="font-medium text-slate-900">{creditNoteGenerated.responsablePago}</span>
                                    </div>
                                    <div className="flex justify-between text-sm">
                                        <span className="text-slate-600">Importe Neto:</span>
                                        <span className="font-medium">${creditNoteGenerated.importeNeto.toLocaleString("es-AR", { minimumFractionDigits: 2 })}</span>
                                    </div>
                                    <div className="flex justify-between text-sm">
                                        <span className="text-slate-600">IVA:</span>
                                        <span className="font-medium">${creditNoteGenerated.iva.toLocaleString("es-AR", { minimumFractionDigits: 2 })}</span>
                                    </div>
                                </div>
                                <div className="flex justify-between items-center pt-4 border-t border-slate-100 bg-slate-50 -mx-6 -mb-6 p-6 rounded-b-xl">
                                    <span className="font-bold text-slate-700">Total Acreditado</span>
                                    <span className="text-2xl font-bold text-green-600">
                                        ${creditNoteGenerated.importeTotal.toLocaleString("es-AR", { minimumFractionDigits: 2 })}
                                    </span>
                                </div>
                            </div>
                            <div className="flex gap-3">
                                <Button onClick={handleNewSearch} className="flex-1" variant="outline">
                                    Nueva Operación
                                </Button>
                                <Button onClick={() => router.push("/")} className="flex-1 bg-indigo-600 hover:bg-indigo-700 text-white">
                                    Finalizar
                                </Button>
                            </div>
                        </CardContent>
                    </Card>
                ) : (
                    /* --- VISTA FORMULARIO --- */
                    <Card className="shadow-lg border-slate-200 dark:border-slate-800">
                        <CardHeader className="border-b border-slate-100 bg-slate-50/50">
                            <CardTitle className="text-lg font-medium text-slate-800">Criterios de Búsqueda</CardTitle>
                            <CardDescription>Seleccione el método para encontrar al responsable.</CardDescription>
                        </CardHeader>
                        <CardContent className="p-6 space-y-6">
                            <div className="bg-slate-100/50 p-1 rounded-lg inline-flex gap-1 w-full md:w-auto">
                                <Button
                                    type="button"
                                    variant={searchMethod === "cuit" ? "default" : "ghost"}
                                    onClick={() => { setSearchMethod("cuit"); setError(""); }}
                                    className={`flex-1 md:flex-none ${searchMethod === "cuit" ? "bg-white text-indigo-700 shadow-sm hover:bg-white" : "text-slate-500 hover:text-slate-700"}`}
                                >
                                    Buscar por CUIT
                                </Button>
                                <Button
                                    type="button"
                                    variant={searchMethod === "document" ? "default" : "ghost"}
                                    onClick={() => { setSearchMethod("document"); setError(""); }}
                                    className={`flex-1 md:flex-none ${searchMethod === "document" ? "bg-white text-indigo-700 shadow-sm hover:bg-white" : "text-slate-500 hover:text-slate-700"}`}
                                >
                                    Buscar por Documento
                                </Button>
                            </div>

                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6 items-start">
                                {searchMethod === "cuit" ? (
                                    <div className="space-y-2">
                                        <Label htmlFor="cuit">CUIT *</Label>
                                        <div className="relative">
                                            <FileText className="absolute left-3 top-2.5 h-4 w-4 text-slate-400" />
                                            <Input
                                                id="cuit"
                                                value={cuit}
                                                onChange={(e) => handleCuitChange(e.target.value)}
                                                placeholder="Ej: 20-12345678-9"
                                                maxLength={13}
                                                className="pl-10 bg-white"
                                            />
                                        </div>
                                    </div>
                                ) : (
                                    <>
                                        <div className="space-y-2">
                                            <Label htmlFor="documentType">Tipo de Documento *</Label>
                                            <Select value={documentType} onValueChange={(value) => { setDocumentType(value as DocumentType); setDocumentNumber(""); setError(""); }}>
                                                <SelectTrigger id="documentType" className="bg-white"><SelectValue /></SelectTrigger>
                                                <SelectContent>
                                                    <SelectItem value="DNI">DNI</SelectItem>
                                                    <SelectItem value="LE">LE</SelectItem>
                                                    <SelectItem value="LC">LC</SelectItem>
                                                    <SelectItem value="Pasaporte">Pasaporte</SelectItem>
                                                    <SelectItem value="OTROS">OTROS</SelectItem>
                                                </SelectContent>
                                            </Select>
                                        </div>
                                        <div className="space-y-2">
                                            <Label htmlFor="documentNumber">Nro. Documento *</Label>
                                            <Input
                                                id="documentNumber"
                                                value={documentNumber}
                                                onChange={(e) => handleDocumentNumberChange(e.target.value)}
                                                placeholder="Ingrese número"
                                                className="bg-white"
                                            />
                                        </div>
                                    </>
                                )}

                                <div className="flex items-end h-full">
                                    <Button onClick={handleSearch} disabled={isSearching} className="w-full bg-indigo-600 hover:bg-indigo-700 text-white shadow-sm mt-8 md:mt-0">
                                        {isSearching ? <Loader2 className="mr-2 h-4 w-4 animate-spin"/> : <Search className="mr-2 h-4 w-4"/>}
                                        {isSearching ? "Buscando..." : "Buscar Facturas"}
                                    </Button>
                                </div>
                            </div>
                            {error && (
                                <Alert variant="destructive" className="mt-4 animate-in slide-in-from-top-2">
                                    <AlertCircle className="h-4 w-4" />
                                    <AlertDescription>{error}</AlertDescription>
                                </Alert>
                            )}
                        </CardContent>

                        {showResults && personInfo && (
                            <div className="border-t border-slate-100 animate-in fade-in slide-in-from-bottom-4">
                                <div className="bg-blue-50/50 p-4 border-b border-blue-100 flex items-center gap-3">
                                    <div className="bg-blue-100 p-2 rounded-full text-blue-600"><User className="h-5 w-5" /></div>
                                    <div>
                                        <p className="text-sm font-bold text-blue-900">{personInfo.nombre} {personInfo.apellido}</p>
                                        <p className="text-xs text-blue-700">
                                            {personInfo.cuit ? `CUIT: ${personInfo.cuit}` : `${personInfo.documentType}: ${personInfo.documentNumber}`}
                                        </p>
                                    </div>
                                </div>

                                <CardContent className="p-6 space-y-6">
                                    {invoices.length > 0 ? (
                                        <div>
                                            <h3 className="text-sm font-semibold text-slate-500 uppercase tracking-wider mb-4">Facturas Disponibles</h3>
                                            <div className="space-y-3">
                                                {invoices.map((invoice) => (
                                                    <div
                                                        key={invoice.id}
                                                        onClick={() => toggleInvoiceSelection(invoice.id)}
                                                        className={`
                                                            relative flex cursor-pointer items-center justify-between rounded-lg border p-4 transition-all
                                                            ${selectedInvoices.has(invoice.id)
                                                            ? "border-indigo-600 bg-indigo-50 ring-1 ring-indigo-600 shadow-sm"
                                                            : "border-slate-200 hover:border-indigo-300 hover:bg-slate-50"}
                                                        `}
                                                    >
                                                        <div className="flex items-center gap-4">
                                                            <Checkbox checked={selectedInvoices.has(invoice.id)} onCheckedChange={() => toggleInvoiceSelection(invoice.id)} />
                                                            <div>
                                                                <p className="font-semibold text-slate-900">{invoice.nroFactura}</p>
                                                                <p className="text-xs text-slate-500">{new Date(invoice.fechaConfeccion).toLocaleDateString("es-AR")}</p>
                                                            </div>
                                                        </div>
                                                        <div className="text-right">
                                                            <p className="font-bold text-slate-900">${invoice.importeTotal.toLocaleString("es-AR", { minimumFractionDigits: 2 })}</p>
                                                            <div className="text-xs text-slate-500 flex gap-2 justify-end">
                                                                <span>Neto: ${invoice.importeNeto}</span>
                                                                <span>IVA: ${invoice.iva}</span>
                                                            </div>
                                                        </div>
                                                    </div>
                                                ))}
                                            </div>

                                            {/* Resumen Totales */}
                                            {selectedInvoices.size > 0 && (
                                                <div className="bg-indigo-900 text-white rounded-xl p-6 shadow-lg mt-6">
                                                    <div className="flex justify-between items-center mb-4">
                                                        <h4 className="font-medium text-indigo-100">Resumen Nota de Crédito</h4>
                                                        <Badge className="bg-indigo-700 text-white">{selectedInvoices.size} facturas</Badge>
                                                    </div>
                                                    <div className="flex justify-between items-center pt-2 border-t border-indigo-700">
                                                        <span className="text-lg font-bold">Total a Acreditar</span>
                                                        <span className="text-2xl font-bold text-green-400">
                                                            ${totals.total.toLocaleString("es-AR", { minimumFractionDigits: 2 })}
                                                        </span>
                                                    </div>
                                                </div>
                                            )}
                                        </div>
                                    ) : (
                                        <Alert className="bg-amber-50 border-amber-200 text-amber-900">
                                            <AlertCircle className="h-4 w-4" />
                                            <AlertDescription>
                                                Este responsable no tiene facturas registradas en el sistema.
                                            </AlertDescription>
                                        </Alert>
                                    )}

                                    <div className="flex gap-4 pt-2">
                                        <Button onClick={handleCancel} variant="ghost" className="text-slate-500">Cancelar Operación</Button>
                                        <Button
                                            onClick={handleAccept}
                                            disabled={selectedInvoices.size === 0}
                                            className="flex-1 bg-indigo-600 hover:bg-indigo-700 text-white shadow-md"
                                            size="lg"
                                        >
                                            Generar Nota de Crédito
                                        </Button>
                                    </div>
                                </CardContent>
                            </div>
                        )}
                    </Card>
                )}
            </div>

            {/* --- CONFIRMATION DIALOG --- */}
            <Dialog open={showConfirmation} onOpenChange={setShowConfirmation}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle className="flex items-center gap-2 text-indigo-700">
                            <AlertCircle className="h-5 w-5"/> Confirmar Emisión
                        </DialogTitle>
                        <DialogDescription>
                            Está a punto de generar una Nota de Crédito. Esta acción no se puede deshacer.
                        </DialogDescription>
                    </DialogHeader>
                    <div className="bg-slate-50 p-4 rounded-lg space-y-3 my-2">
                        <div className="flex justify-between text-sm">
                            <span className="text-slate-600">Facturas afectadas:</span>
                            <span className="font-medium">{selectedInvoices.size}</span>
                        </div>
                        <div className="flex justify-between items-center pt-2 border-t border-slate-200">
                            <span className="font-bold text-slate-800">Total a devolver:</span>
                            <span className="text-xl font-bold text-indigo-600">
                                ${totals.total.toLocaleString("es-AR", { minimumFractionDigits: 2 })}
                            </span>
                        </div>
                    </div>
                    <DialogFooter>
                        <Button variant="outline" onClick={() => setShowConfirmation(false)}>Volver</Button>
                        <Button onClick={handleConfirmCreditNote} disabled={isProcessing} className="bg-indigo-600 hover:bg-indigo-700 text-white">
                            {isProcessing ? "Procesando..." : "Confirmar"}
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </div>
    )
}