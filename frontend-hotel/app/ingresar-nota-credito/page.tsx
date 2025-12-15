"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { Checkbox } from "@/components/ui/checkbox"
import { AlertCircle, CheckCircle2, User, ArrowLeft, FileCheck } from "lucide-react"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog"

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
    nombre: string
    apellido: string
    cuit?: string
    documentType?: DocumentType
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
    const [creditNoteGenerated, setCreditNoteGenerated] = useState<CreditNote | null>(null)

    // Validation functions
    const validateCuit = (value: string) => {
        if (value === "") return ""
        if (!/^\d*$/.test(value)) return "El CUIT solo puede contener números"
        if (value.length !== 11 && value.length > 0) return "El CUIT debe tener exactamente 11 dígitos"
        return ""
    }

    const validateDocumentNumber = (value: string, type: DocumentType) => {
        if (value === "") return ""

        if (type === "DNI" || type === "LE" || type === "LC") {
            if (!/^\d*$/.test(value)) return "Solo se permiten números"
            if (value.length < 7 || value.length > 8) return "Debe tener entre 7 y 8 dígitos"
        } else if (type === "Pasaporte") {
            if (!/^[a-zA-Z0-9]*$/.test(value)) return "Solo se permiten letras y números"
        }
        return ""
    }

    const handleCuitChange = (value: string) => {
        const upperValue = value.toUpperCase()
        if (upperValue.length <= 11) {
            setCuit(upperValue)
        }
    }

    const handleDocumentNumberChange = (value: string) => {
        const upperValue = value.toUpperCase()
        if (documentType === "Pasaporte" || documentType === "OTROS") {
            setDocumentNumber(upperValue)
        } else {
            if (/^\d*$/.test(upperValue) && upperValue.length <= 8) {
                setDocumentNumber(upperValue)
            }
        }
    }

    const handleSearch = () => {
        setError("")

        // Validation 2.A: No search filters selected
        if (searchMethod === "cuit" && !cuit) {
            setError("Debe ingresar el CUIT para realizar la búsqueda")
            return
        }

        if (searchMethod === "document" && !documentNumber) {
            setError("Debe ingresar el número de documento para realizar la búsqueda")
            return
        }

        // Validate CUIT format
        if (searchMethod === "cuit") {
            const cuitError = validateCuit(cuit)
            if (cuitError) {
                setError(cuitError)
                return
            }
            if (cuit.length !== 11) {
                setError("El CUIT debe tener exactamente 11 dígitos")
                return
            }
        }

        // Validate document number format
        if (searchMethod === "document") {
            const docError = validateDocumentNumber(documentNumber, documentType)
            if (docError) {
                setError(docError)
                return
            }
        }

        setIsSearching(true)

        setTimeout(() => {
            // Mock person database
            const mockPersons = [
                {
                    cuit: "20345678901",
                    nombre: "Juan",
                    apellido: "Pérez",
                    documentType: "DNI" as DocumentType,
                    documentNumber: "34567890",
                },
                {
                    cuit: "27123456789",
                    nombre: "María",
                    apellido: "González",
                    documentType: "DNI" as DocumentType,
                    documentNumber: "12345678",
                },
                {
                    cuit: "23987654321",
                    nombre: "Carlos",
                    apellido: "Rodríguez",
                    documentType: "DNI" as DocumentType,
                    documentNumber: "98765432",
                },
            ]

            // Find person by CUIT or document
            let foundPerson: Person | null = null

            if (searchMethod === "cuit") {
                const person = mockPersons.find((p) => p.cuit === cuit)
                if (person) {
                    foundPerson = person
                }
            } else {
                const person = mockPersons.find((p) => p.documentType === documentType && p.documentNumber === documentNumber)
                if (person) {
                    foundPerson = person
                }
            }

            // If no person found
            if (!foundPerson) {
                setError("No se encontró ninguna persona con los datos ingresados")
                setInvoices([])
                setPersonInfo(null)
                setShowResults(false)
                setIsSearching(false)
                return
            }

            // Set person info
            setPersonInfo(foundPerson)

            // Mock invoices for this specific person
            const mockInvoicesForPerson: Invoice[] = [
                {
                    id: "1",
                    nroFactura: "FAC-2024-001",
                    fechaConfeccion: "2024-11-15",
                    importeNeto: 15000,
                    iva: 3150,
                    importeTotal: 18150,
                    responsablePago: `${foundPerson.nombre} ${foundPerson.apellido}`,
                },
                {
                    id: "2",
                    nroFactura: "FAC-2024-002",
                    fechaConfeccion: "2024-11-20",
                    importeNeto: 8500,
                    iva: 1785,
                    importeTotal: 10285,
                    responsablePago: `${foundPerson.nombre} ${foundPerson.apellido}`,
                },
                {
                    id: "3",
                    nroFactura: "FAC-2024-003",
                    fechaConfeccion: "2024-12-01",
                    importeNeto: 12000,
                    iva: 2520,
                    importeTotal: 14520,
                    responsablePago: `${foundPerson.nombre} ${foundPerson.apellido}`,
                },
            ]

            // Flow 4.A: No pending invoices found
            const hasInvoices = Math.random() > 0.2 // 80% chance of finding invoices

            if (!hasInvoices) {
                setError(`No hay facturas pendientes de pago a nombre de ${foundPerson.nombre} ${foundPerson.apellido}`)
                setInvoices([])
                setShowResults(false)
            } else {
                setInvoices(mockInvoicesForPerson)
                setShowResults(true)
                setSelectedInvoices(new Set())
            }

            setIsSearching(false)
        }, 800)
    }

    const toggleInvoiceSelection = (invoiceId: string) => {
        const newSelected = new Set(selectedInvoices)
        if (newSelected.has(invoiceId)) {
            newSelected.delete(invoiceId)
        } else {
            newSelected.add(invoiceId)
        }
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
            setError("Debe seleccionar al menos una factura para generar la nota de crédito")
            return
        }

        setShowConfirmation(true)
    }

    const handleConfirmCreditNote = () => {
        // Generate credit note
        const totals = calculateTotalSelected()
        const newCreditNote: CreditNote = {
            nroNotaCredito: `NC-${new Date().getFullYear()}-${String(Math.floor(Math.random() * 1000)).padStart(3, "0")}`,
            responsablePago: invoices[0]?.responsablePago || "",
            importeNeto: totals.neto,
            iva: totals.iva,
            importeTotal: totals.total,
        }

        setCreditNoteGenerated(newCreditNote)
        setShowConfirmation(false)
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

    if (creditNoteGenerated) {
        return (
            <div className="min-h-screen bg-gradient-to-br from-green-50 via-emerald-50 to-teal-50">
                <header className="border-b border-border bg-card">
                    <div className="container mx-auto px-4 py-4 flex justify-between items-center">
                        <div>
                            <h1 className="text-2xl font-semibold text-foreground">Sistema de Gestión Hotelera</h1>
                            <p className="text-sm text-muted-foreground mt-1">Panel de Conserje</p>
                        </div>
                        <Button onClick={handleCancel} variant="outline" className="gap-2 bg-transparent">
                            <ArrowLeft className="h-4 w-4" />
                            Volver al Menú Principal
                        </Button>
                    </div>
                </header>

                <main className="container mx-auto px-4 py-8">
                    <Card className="max-w-2xl mx-auto border-green-200 shadow-lg">
                        <CardHeader className="text-center bg-gradient-to-r from-green-50 to-emerald-50">
                            <div className="mx-auto w-12 h-12 bg-green-100 rounded-full flex items-center justify-center mb-4">
                                <CheckCircle2 className="h-6 w-6 text-green-600" />
                            </div>
                            <CardTitle className="text-2xl text-green-700">Nota de Crédito Generada</CardTitle>
                            <CardDescription>La operación ha culminado con éxito</CardDescription>
                        </CardHeader>
                        <CardContent className="pt-6 space-y-4">
                            <div className="bg-green-50 border border-green-200 rounded-lg p-4 space-y-3">
                                <div className="flex justify-between items-center">
                                    <span className="text-sm font-medium text-muted-foreground">Nro. Nota de Crédito:</span>
                                    <span className="text-lg font-bold text-green-700">{creditNoteGenerated.nroNotaCredito}</span>
                                </div>

                                <div className="flex justify-between items-center">
                                    <span className="text-sm font-medium text-muted-foreground">Responsable de Pago:</span>
                                    <span className="text-base font-semibold">{creditNoteGenerated.responsablePago}</span>
                                </div>

                                <div className="border-t border-green-200 pt-3 space-y-2">
                                    <div className="flex justify-between items-center">
                                        <span className="text-sm text-muted-foreground">Importe Neto:</span>
                                        <span className="text-base font-medium">
                      ${creditNoteGenerated.importeNeto.toLocaleString("es-AR", { minimumFractionDigits: 2 })}
                    </span>
                                    </div>

                                    <div className="flex justify-between items-center">
                                        <span className="text-sm text-muted-foreground">IVA:</span>
                                        <span className="text-base font-medium">
                      ${creditNoteGenerated.iva.toLocaleString("es-AR", { minimumFractionDigits: 2 })}
                    </span>
                                    </div>

                                    <div className="flex justify-between items-center border-t border-green-300 pt-2">
                                        <span className="text-base font-semibold text-foreground">Importe Total:</span>
                                        <span className="text-xl font-bold text-green-700">
                      ${creditNoteGenerated.importeTotal.toLocaleString("es-AR", { minimumFractionDigits: 2 })}
                    </span>
                                    </div>
                                </div>
                            </div>

                            <div className="flex gap-3 pt-4">
                                <Button onClick={handleNewSearch} className="flex-1 bg-transparent" variant="outline">
                                    Nueva Búsqueda
                                </Button>
                                <Button onClick={() => router.push("/")} className="flex-1 bg-green-600 hover:bg-green-700">
                                    Volver al Menú Principal
                                </Button>
                            </div>
                        </CardContent>
                    </Card>
                </main>
            </div>
        )
    }

    return (
        <div className="min-h-screen bg-gradient-to-br from-purple-50 via-violet-50 to-indigo-50">
            <header className="border-b border-border bg-card">
                <div className="container mx-auto px-4 py-4 flex justify-between items-center">
                    <div>
                        <h1 className="text-2xl font-semibold text-foreground">Sistema de Gestión Hotelera</h1>
                        <p className="text-sm text-muted-foreground mt-1">Panel de Conserje</p>
                    </div>
                    <Button onClick={handleCancel} variant="outline" className="gap-2 bg-transparent">
                        <ArrowLeft className="h-4 w-4" />
                        Volver al Menú Principal
                    </Button>
                </div>
            </header>

            <main className="container mx-auto px-4 py-8">
                <Card className="max-w-4xl mx-auto border-purple-200 shadow-xl">
                    <CardHeader className="bg-gradient-to-r from-purple-500 to-violet-600 text-white">
                        <div className="flex items-center gap-3">
                            <div className="p-2 bg-white/20 rounded-lg backdrop-blur-sm">
                                <FileCheck className="h-6 w-6" />
                            </div>
                            <div>
                                <CardTitle className="text-2xl">Ingresar Nota de Crédito</CardTitle>
                                <CardDescription className="text-purple-100">
                                    Busque facturas pendientes por CUIT o documento para generar una nota de crédito
                                </CardDescription>
                            </div>
                        </div>
                    </CardHeader>

                    <CardContent className="p-6 space-y-6">
                        {error && (
                            <Alert variant="destructive">
                                <AlertCircle className="h-4 w-4" />
                                <AlertDescription>{error}</AlertDescription>
                            </Alert>
                        )}

                        {/* Search Method Selection */}
                        <div className="space-y-4">
                            <Label className="text-base font-semibold">Método de Búsqueda</Label>
                            <div className="flex gap-4">
                                <Button
                                    type="button"
                                    variant={searchMethod === "cuit" ? "default" : "outline"}
                                    onClick={() => {
                                        setSearchMethod("cuit")
                                        setError("")
                                    }}
                                    className="flex-1"
                                >
                                    Buscar por CUIT
                                </Button>
                                <Button
                                    type="button"
                                    variant={searchMethod === "document" ? "default" : "outline"}
                                    onClick={() => {
                                        setSearchMethod("document")
                                        setError("")
                                    }}
                                    className="flex-1"
                                >
                                    Buscar por Documento
                                </Button>
                            </div>
                        </div>

                        {/* Search Fields */}
                        {searchMethod === "cuit" ? (
                            <div className="space-y-2">
                                <Label htmlFor="cuit" className="text-base">
                                    CUIT *
                                </Label>
                                <Input
                                    id="cuit"
                                    value={cuit}
                                    onChange={(e) => handleCuitChange(e.target.value)}
                                    placeholder="Ingrese CUIT (11 dígitos)"
                                    maxLength={11}
                                    className="text-lg uppercase"
                                />
                                {cuit && validateCuit(cuit) && <p className="text-sm text-red-500">{validateCuit(cuit)}</p>}
                            </div>
                        ) : (
                            <div className="grid grid-cols-2 gap-4">
                                <div className="space-y-2">
                                    <Label htmlFor="documentType" className="text-base">
                                        Tipo de Documento *
                                    </Label>
                                    <Select
                                        value={documentType}
                                        onValueChange={(value) => {
                                            setDocumentType(value as DocumentType)
                                            setDocumentNumber("")
                                            setError("")
                                        }}
                                    >
                                        <SelectTrigger id="documentType" className="text-base">
                                            <SelectValue />
                                        </SelectTrigger>
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
                                    <Label htmlFor="documentNumber" className="text-base">
                                        Nro. Documento *
                                    </Label>
                                    <Input
                                        id="documentNumber"
                                        value={documentNumber}
                                        onChange={(e) => handleDocumentNumberChange(e.target.value)}
                                        placeholder={
                                            documentType === "DNI" || documentType === "LE" || documentType === "LC"
                                                ? "7-8 dígitos"
                                                : "Alfanumérico"
                                        }
                                        className="text-lg uppercase"
                                    />
                                    {documentNumber && validateDocumentNumber(documentNumber, documentType) && (
                                        <p className="text-sm text-red-500">{validateDocumentNumber(documentNumber, documentType)}</p>
                                    )}
                                </div>
                            </div>
                        )}

                        {showResults && personInfo && (
                            <div className="pt-4 border-t">
                                <Alert className="bg-blue-50 border-blue-200">
                                    <User className="h-5 w-5 text-blue-600" />
                                    <AlertDescription className="text-base font-semibold text-blue-900">
                                        Facturas a nombre de {personInfo.nombre} {personInfo.apellido}
                                    </AlertDescription>
                                </Alert>
                            </div>
                        )}

                        {/* Results */}
                        {showResults && invoices.length > 0 && (
                            <div className="space-y-4">
                                <div>
                                    <h3 className="text-lg font-semibold mb-2">Facturas Pendientes de Pago</h3>
                                    <p className="text-sm text-muted-foreground mb-4">
                                        Seleccione las facturas para cancelar con la nota de crédito
                                    </p>
                                </div>

                                <div className="space-y-2">
                                    {invoices.map((invoice) => (
                                        <Card
                                            key={invoice.id}
                                            className={`cursor-pointer transition-all ${
                                                selectedInvoices.has(invoice.id) ? "border-purple-500 bg-purple-50" : "hover:border-purple-300"
                                            }`}
                                            onClick={() => toggleInvoiceSelection(invoice.id)}
                                        >
                                            <CardContent className="p-4">
                                                <div className="flex items-start gap-3">
                                                    <Checkbox
                                                        checked={selectedInvoices.has(invoice.id)}
                                                        onCheckedChange={() => toggleInvoiceSelection(invoice.id)}
                                                        className="mt-1"
                                                    />
                                                    <div className="flex-1 grid grid-cols-2 md:grid-cols-4 gap-3">
                                                        <div>
                                                            <p className="text-xs text-muted-foreground">Nro. Factura</p>
                                                            <p className="font-semibold">{invoice.nroFactura}</p>
                                                        </div>
                                                        <div>
                                                            <p className="text-xs text-muted-foreground">Fecha</p>
                                                            <p className="font-medium">
                                                                {new Date(invoice.fechaConfeccion).toLocaleDateString("es-AR")}
                                                            </p>
                                                        </div>
                                                        <div>
                                                            <p className="text-xs text-muted-foreground">Importe Neto</p>
                                                            <p className="font-medium">
                                                                ${invoice.importeNeto.toLocaleString("es-AR", { minimumFractionDigits: 2 })}
                                                            </p>
                                                        </div>
                                                        <div>
                                                            <p className="text-xs text-muted-foreground">IVA</p>
                                                            <p className="font-medium">
                                                                ${invoice.iva.toLocaleString("es-AR", { minimumFractionDigits: 2 })}
                                                            </p>
                                                        </div>
                                                        <div className="col-span-2">
                                                            <p className="text-xs text-muted-foreground">Total</p>
                                                            <p className="text-lg font-bold text-purple-700">
                                                                ${invoice.importeTotal.toLocaleString("es-AR", { minimumFractionDigits: 2 })}
                                                            </p>
                                                        </div>
                                                    </div>
                                                </div>
                                            </CardContent>
                                        </Card>
                                    ))}
                                </div>

                                {selectedInvoices.size > 0 && (
                                    <Card className="bg-purple-50 border-purple-200">
                                        <CardContent className="p-4">
                                            <h4 className="font-semibold mb-3">Total Nota de Crédito</h4>
                                            <div className="space-y-2">
                                                <div className="flex justify-between">
                                                    <span className="text-sm text-muted-foreground">Importe Neto:</span>
                                                    <span className="font-medium">
                            ${totals.neto.toLocaleString("es-AR", { minimumFractionDigits: 2 })}
                          </span>
                                                </div>
                                                <div className="flex justify-between">
                                                    <span className="text-sm text-muted-foreground">IVA:</span>
                                                    <span className="font-medium">
                            ${totals.iva.toLocaleString("es-AR", { minimumFractionDigits: 2 })}
                          </span>
                                                </div>
                                                <div className="flex justify-between border-t border-purple-300 pt-2">
                                                    <span className="font-semibold">Importe Total:</span>
                                                    <span className="text-xl font-bold text-purple-700">
                            ${totals.total.toLocaleString("es-AR", { minimumFractionDigits: 2 })}
                          </span>
                                                </div>
                                            </div>
                                        </CardContent>
                                    </Card>
                                )}

                                <div className="flex gap-3 pt-2">
                                    <Button
                                        onClick={handleAccept}
                                        disabled={selectedInvoices.size === 0}
                                        className="flex-1 bg-green-600 hover:bg-green-700"
                                        size="lg"
                                    >
                                        Aceptar
                                    </Button>
                                    <Button onClick={handleCancel} variant="outline" size="lg">
                                        Cancelar
                                    </Button>
                                </div>
                            </div>
                        )}

                        <div className="flex gap-3 justify-end">
                            <Button onClick={handleCancel} variant="outline" size="lg">
                                Cancelar
                            </Button>
                            <Button
                                onClick={handleSearch}
                                disabled={isSearching}
                                className="bg-purple-600 hover:bg-purple-700"
                                size="lg"
                            >
                                {isSearching ? "Buscando..." : "Buscar"}
                            </Button>
                        </div>
                    </CardContent>
                </Card>
            </main>

            {/* Confirmation Dialog */}
            <Dialog open={showConfirmation} onOpenChange={setShowConfirmation}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>Confirmar Nota de Crédito</DialogTitle>
                        <DialogDescription>
                            ¿Está seguro que desea generar la nota de crédito por un total de $
                            {totals.total.toLocaleString("es-AR", { minimumFractionDigits: 2 })}?
                        </DialogDescription>
                    </DialogHeader>
                    <div className="py-4">
                        <p className="text-sm text-muted-foreground">
                            Se cancelarán {selectedInvoices.size} factura{selectedInvoices.size > 1 ? "s" : ""} con esta nota de
                            crédito.
                        </p>
                    </div>
                    <DialogFooter>
                        <Button variant="outline" onClick={() => setShowConfirmation(false)}>
                            Cancelar
                        </Button>
                        <Button onClick={handleConfirmCreditNote} className="bg-green-600 hover:bg-green-700">
                            Confirmar
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </div>
    )
}
