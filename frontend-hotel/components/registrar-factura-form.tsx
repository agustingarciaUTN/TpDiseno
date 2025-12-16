"use client"

import { useState } from "react"
import Link from "next/link"
import { useRouter } from "next/navigation"

// UI Components
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

// Icons
import {
    AlertCircle,
    CheckCircle2,
    FileText,
    ArrowLeft,
    Loader2,
    MapPin,
    Phone,
    Home,
    Search,
    User,
    Building2,
    Receipt
} from "lucide-react"

interface Ocupante {
    tipoDocumento: string;
    nroDocumento: string;
    nombres: string;
    apellido: string;
}

interface ItemFactura {
    id: string;
    descripcion: string;
    monto: number;
    selected: boolean;
}

interface DetalleFacturacion {
    nombreResponsable: string;
    cuitResponsable: string | null;
    montoEstadiaBase: number;
    recargoHorario: number;
    detalleRecargo: string;
    subtotal: number;
    montoIva: number;
    montoTotal: number;
    tipoFactura: "A" | "B";
    serviciosAdicionales: { descripcion: string; valor: number }[];
    idResponsable: number;
}

export function RegistrarFacturaForm() {
    const router = useRouter()

    // ESTADOS PRINCIPALES
    const [step, setStep] = useState<"search" | "select-person" | "select-items" | "invoice-type" | "success">("search")
    const [isLoading, setIsLoading] = useState(false)
    const [errorMessage, setErrorMessage] = useState("")

    // DATOS DE ENTRADA
    const [numeroHabitacion, setNumeroHabitacion] = useState("")
    const [horaSalida, setHoraSalida] = useState("")

    // DATOS RECIBIDOS
    const [idEstadia, setIdEstadia] = useState<number | null>(null)
    const [ocupantes, setOcupantes] = useState<Ocupante[]>([])

    // SELECCIÓN
    const [selectedOcupante, setSelectedOcupante] = useState<Ocupante | null>(null)
    const [items, setItems] = useState<ItemFactura[]>([])
    const [detalleCalculado, setDetalleCalculado] = useState<DetalleFacturacion | null>(null)
    const [idResponsableSeleccionado, setIdResponsableSeleccionado] = useState<number | null>(null);

    // ESTADOS PARA TERCEROS / ALTA
    const [showThirdPartyDialog, setShowThirdPartyDialog] = useState(false)
    const [necesitaAltaEmpresa, setNecesitaAltaEmpresa] = useState(false)

    // Formulario Alta Empresa
    const [cuitTercero, setCuitTercero] = useState("")
    const [razonSocialTercero, setRazonSocialTercero] = useState("")
    const [telefonoTercero, setTelefonoTercero] = useState("")
    const [direccion, setDireccion] = useState({
        calle: "",
        numero: "",
        piso: "",
        departamento: "",
        codPostal: "",
        localidad: "",
        provincia: "",
        pais: "Argentina"
    })

    // --- LÓGICA DE NEGOCIO (Idéntica a la original) ---

    const handleSearch = async (e?: React.FormEvent) => {
        if (e) e.preventDefault();
        setErrorMessage("")
        if (!numeroHabitacion.trim() || !horaSalida.trim()) {
            setErrorMessage("Complete todos los campos obligatorios")
            return
        }
        setIsLoading(true)
        try {
            const res = await fetch('http://localhost:8080/api/factura/buscar-ocupantes', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ numeroHabitacion, horaSalida })
            });
            if (!res.ok) {
                const errorData = await res.json().catch(() => ({}));
                throw new Error(errorData.message || "No se encontró ocupación activa.");
            }
            const data = await res.json();
            setIdEstadia(data.idEstadia);
            setOcupantes(data.ocupantes);
            setStep("select-person");
        } catch (error: any) {
            setErrorMessage(error.message);
        } finally {
            setIsLoading(false);
        }
    }

    const handleSelectPerson = async (ocupante: Ocupante) => {
        setErrorMessage("")
        setSelectedOcupante(ocupante)

        await obtenerDetalleFacturacion({
            esTercero: false,
            tipoDoc: ocupante.tipoDocumento,
            nroDoc: ocupante.nroDocumento
        });
    }

    const obtenerDetalleFacturacion = async (params: {
        esTercero: boolean,
        tipoDoc?: string,
        nroDoc?: string,
        idResponsableJuridico?: number
    }) => {
        setIsLoading(true)
        try {
            const query = new URLSearchParams({
                idEstadia: idEstadia!.toString(),
                horaSalida: horaSalida,
                esTercero: params.esTercero.toString(),
            });

            if (params.esTercero && cuitTercero) query.append("cuit", cuitTercero);
            if (params.idResponsableJuridico) query.append("idResponsableJuridico", params.idResponsableJuridico.toString());
            if (!params.esTercero && params.tipoDoc && params.nroDoc) {
                query.append("tipoDoc", params.tipoDoc);
                query.append("nroDoc", params.nroDoc);
            }

            const res = await fetch(`http://localhost:8080/api/factura/calcular-detalle?${query.toString()}`);

            if (res.status === 409) {
                const data = await res.json();
                if (data.accion === "REDIRECCIONAR_A_ALTA_RESPONSABLE") {
                    setErrorMessage("El CUIT no existe. Ingrese un CUIT válido o uno vacío para dar de alta un responsable de pago.");
                    setIsLoading(false);
                    return;
                }
            }

            if (!res.ok) {
                const msg = await res.text();
                throw new Error(msg || "Error al calcular detalle");
            }

            const data: DetalleFacturacion = await res.json();
            setDetalleCalculado(data);
            setIdResponsableSeleccionado(data.idResponsable);

            const itemsMapeados: ItemFactura[] = [
                {
                    id: "estadia",
                    descripcion: "Alojamiento Base",
                    monto: data.montoEstadiaBase || 0,
                    selected: true
                }
            ];

            // Si hay recargo
            if (data.recargoHorario > 0) {
                itemsMapeados.push({
                    id: "recargo",
                    descripcion: data.detalleRecargo || "Recargo",
                    monto: data.recargoHorario || 0,
                    selected: true
                });
            }

            // Servicios adicionales
            if (data.serviciosAdicionales) {
                data.serviciosAdicionales.forEach((serv, idx) => {
                    itemsMapeados.push({
                        id: `serv-${idx}`,
                        descripcion: serv.descripcion || "Servicio Adicional",
                        monto: serv.valor || 0,
                        selected: true
                    });
                });
            }

            setItems(itemsMapeados);
            setShowThirdPartyDialog(false);
            setStep("select-items");

        } catch (error: any) {
            setErrorMessage(error.message);
        } finally {
            setIsLoading(false);
        }
    }

    const handleCrearEmpresa = async () => {
        setErrorMessage("");

        if (!cuitTercero || !razonSocialTercero || !telefonoTercero ||
            !direccion.calle || !direccion.numero || !direccion.codPostal ||
            !direccion.localidad || !direccion.provincia || !direccion.pais) {
            setErrorMessage("Por favor complete todos los campos obligatorios (*)");
            return;
        }

        const cuitRegex = /^\d{2}-?\d{8}-?\d{1}$/;
        if (!cuitRegex.test(cuitTercero)) {
            setErrorMessage("El formato del CUIT es inválido (Ej: 30-12345678-9)");
            return;
        }

        if (telefonoTercero.length < 6) {
            setErrorMessage("El número de teléfono es corto. Ingrese un número válido.");
            return;
        }

        setIsLoading(true);

        try {
            const res = await fetch('http://localhost:8080/api/factura/responsable', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    tipoResponsable: "J",
                    cuit: cuitTercero,
                    razonSocial: razonSocialTercero,
                    telefono: [parseInt(telefonoTercero)],
                    dtoDireccion: {
                        calle: direccion.calle,
                        numero: parseInt(direccion.numero),
                        piso: direccion.piso ? parseInt(direccion.piso) : null,
                        departamento: direccion.departamento,
                        codPostal: parseInt(direccion.codPostal),
                        localidad: direccion.localidad,
                        provincia: direccion.provincia,
                        pais: direccion.pais
                    }
                })
            });

            if (!res.ok) {
                const errorText = await res.text();
                throw new Error(errorText || "Error al crear empresa");
            }

            const dataResp = await res.json();
            const nuevoId = dataResp.idResponsableGenerado;

            setIsLoading(false);
            alert(`La firma ${razonSocialTercero} ha sido cargada al sistema.`);
            setIdResponsableSeleccionado(nuevoId);
            setNecesitaAltaEmpresa(false);
            setErrorMessage("");

        } catch (error: any) {
            setErrorMessage("Error: " + error.message);
            setIsLoading(false);
        }
    }

    const handleToggleItem = (itemId: string) => {
        setItems(items.map((item) => (item.id === itemId ? { ...item, selected: !item.selected } : item)))
    }

    const calculateTotalLocal = () => items.filter(i => i.selected).reduce((acc, curr) => acc + curr.monto, 0);

    const getMontosFinales = () => {
        const totalItems = calculateTotalLocal();
        const esA = detalleCalculado?.tipoFactura === "A";
        const iva = esA ? totalItems * 0.21 : 0;
        return {
            subtotal: totalItems,
            iva: detalleCalculado?.montoIva || 0,
            total: detalleCalculado?.montoTotal || 0
        };
    }

    const handleGenerateInvoice = async () => {
        setIsLoading(true);
        try {
            const facturaFinal = {
                numeroFactura: "Generando...",
                fechaEmision: new Date().toISOString(),
                fechaVencimiento: new Date().toISOString(),
                importeTotal: getMontosFinales().total,
                importeNeto: getMontosFinales().subtotal,
                iva: getMontosFinales().iva,
                tipoFactura: detalleCalculado?.tipoFactura,
                idEstadia: { idEstadia: idEstadia },
                idResponsable: { idResponsable: idResponsableSeleccionado }
            };

            const res = await fetch('http://localhost:8080/api/factura/generar', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(facturaFinal)
            });

            if(!res.ok) throw new Error("Error al generar factura");
            setStep("success");
        } catch (error: any) {
            setErrorMessage(error.message);
        } finally {
            setIsLoading(false);
        }
    }

    const obtainingDetalleFacturacionWrapper = () => {
        if (!cuitTercero || !cuitTercero.trim()) {
            setErrorMessage("");
            setNecesitaAltaEmpresa(true);
            return;
        }
        setErrorMessage("");
        obtenerDetalleFacturacion({ esTercero: true, idResponsableJuridico: undefined });
    }

    const handleDireccionChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setDireccion({ ...direccion, [e.target.name]: e.target.value });
    }

    const resetForm = () => {
        window.location.reload();
    }

    // --- RENDERIZADO ---

    return (
        <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50 dark:from-slate-950 dark:via-slate-900 dark:to-slate-950">
            <div className="mx-auto max-w-5xl px-4 py-8 sm:px-6 lg:px-8">

                {/* --- HEADER --- */}
                <div className="mb-8 space-y-2">
                    <div className="flex items-center justify-between">
                        <div className="flex items-center gap-3">
                            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-emerald-600 text-white shadow-md">
                                <Receipt className="h-6 w-6" />
                            </div>
                            <div>
                                <p className="text-xs font-semibold uppercase tracking-wider text-emerald-600 dark:text-emerald-400">
                                    Caso de Uso 07
                                </p>
                                <h1 className="text-3xl font-bold text-slate-900 dark:text-slate-50">Registrar Factura</h1>
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
                        Cálculo de estadía, recargos y generación de comprobantes fiscales.
                    </p>
                </div>

                {/* --- ALERTA DE ERROR GLOBAL --- */}
                {errorMessage && (
                    <Alert variant="destructive" className="mb-6 shadow-md border-red-200 bg-red-50 text-red-800">
                        <AlertCircle className="h-4 w-4" />
                        <AlertDescription>{errorMessage}</AlertDescription>
                    </Alert>
                )}

                {/* --- PASO 1: BUSCAR HABITACIÓN --- */}
                {step === "search" && (
                    <Card className="shadow-lg border-slate-200 dark:border-slate-800">
                        <CardHeader className="border-b border-slate-100 bg-slate-50/50 pb-4">
                            <CardTitle className="text-lg font-medium text-slate-800">Datos de la Estadía</CardTitle>
                        </CardHeader>
                        <CardContent className="pt-6">
                            <form onSubmit={handleSearch} className="space-y-6">
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                    <div className="space-y-2">
                                        <Label htmlFor="habitacion" className="text-slate-600">Nro Habitación</Label>
                                        <div className="relative">
                                            <Home className="absolute left-3 top-3 h-4 w-4 text-slate-400" />
                                            <Input
                                                id="habitacion"
                                                value={numeroHabitacion}
                                                onChange={(e) => setNumeroHabitacion(e.target.value)}
                                                placeholder="Ej: 101"
                                                className="pl-10 bg-white"
                                            />
                                        </div>
                                    </div>
                                    <div className="space-y-2">
                                        <Label htmlFor="horaSalida" className="text-slate-600">Hora Salida Real</Label>
                                        <Input
                                            id="horaSalida"
                                            type="time"
                                            value={horaSalida}
                                            onChange={(e) => setHoraSalida(e.target.value)}
                                            className="bg-white"
                                        />
                                    </div>
                                </div>
                                <Button onClick={() => handleSearch()} type="submit" className="w-full bg-emerald-600 hover:bg-emerald-700 shadow-md" disabled={isLoading}>
                                    {isLoading ? <Loader2 className="mr-2 h-4 w-4 animate-spin" /> :
                                        <> <Search className="mr-2 h-4 w-4" /> Buscar Ocupantes </>}
                                </Button>
                            </form>
                        </CardContent>
                    </Card>
                )}

                {/* --- PASO 2: SELECCIONAR PERSONA --- */}
                {step === "select-person" && (
                    <Card className="shadow-lg animate-in fade-in slide-in-from-bottom-4">
                        <CardHeader className="border-b border-slate-100">
                            <CardTitle>Seleccionar Responsable de Pago</CardTitle>
                            <CardDescription>Elija uno de los huéspedes registrados o facture a una empresa.</CardDescription>
                        </CardHeader>
                        <CardContent className="pt-6 space-y-6">
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                {ocupantes.map((ocup, idx) => (
                                    <div
                                        key={idx}
                                        className="relative flex cursor-pointer flex-col gap-2 rounded-lg border border-slate-200 bg-white p-4 shadow-sm hover:border-emerald-500 hover:bg-emerald-50 transition-all"
                                        onClick={() => handleSelectPerson(ocup)}
                                    >
                                        <div className="flex items-center justify-between">
                                            <div className="flex items-center gap-3">
                                                <div className="flex h-10 w-10 items-center justify-center rounded-full bg-slate-100 text-slate-600">
                                                    <User className="h-5 w-5" />
                                                </div>
                                                <div>
                                                    <p className="font-semibold text-slate-900">{ocup.apellido}, {ocup.nombres}</p>
                                                    <p className="text-sm text-slate-500">{ocup.tipoDocumento} {ocup.nroDocumento}</p>
                                                </div>
                                            </div>
                                            <ArrowLeft className="h-4 w-4 text-emerald-600 rotate-180 opacity-0 group-hover:opacity-100" />
                                        </div>
                                    </div>
                                ))}
                            </div>

                            <div className="relative">
                                <div className="absolute inset-0 flex items-center"><span className="w-full border-t" /></div>
                                <div className="relative flex justify-center text-xs uppercase"><span className="bg-white px-2 text-muted-foreground">O bien</span></div>
                            </div>

                            <div className="flex gap-4">
                                <Button variant="outline" onClick={() => setStep("search")} className="flex-1">
                                    <ArrowLeft className="mr-2 h-4 w-4" /> Volver
                                </Button>
                                <Button
                                    variant="secondary"
                                    onClick={() => { setErrorMessage(""); setShowThirdPartyDialog(true); setNecesitaAltaEmpresa(false); }}
                                    className="flex-1 bg-slate-100 hover:bg-slate-200 text-slate-900 border border-slate-200"
                                >
                                    <Building2 className="mr-2 h-4 w-4" /> Facturar a Empresa / Tercero
                                </Button>
                            </div>
                        </CardContent>
                    </Card>
                )}

                {/* --- PASO 3: DETALLE FACTURACIÓN --- */}
                {step === "select-items" && detalleCalculado && (
                    <Card className="shadow-xl border-slate-200 animate-in fade-in zoom-in-95">
                        <CardHeader className="bg-slate-50 border-b border-slate-100">
                            <div className="flex justify-between items-start">
                                <div>
                                    <CardTitle className="text-xl">Detalle de Facturación</CardTitle>
                                    <CardDescription className="mt-1 flex items-center gap-2">
                                        <User className="h-3 w-3" /> Responsable: <span className="font-medium text-slate-900">{detalleCalculado.nombreResponsable}</span>
                                    </CardDescription>
                                </div>
                                <Badge variant={detalleCalculado.tipoFactura === "A" ? "default" : "secondary"} className="text-lg px-4 py-1">
                                    Factura {detalleCalculado.tipoFactura}
                                </Badge>
                            </div>
                        </CardHeader>
                        <CardContent className="pt-6 space-y-6">

                            {/* Lista de Items */}
                            <div className="space-y-3">
                                <h3 className="text-sm font-medium text-slate-500 uppercase tracking-wider">Conceptos a Facturar</h3>
                                {items.map((item) => (
                                    <div
                                        key={item.id}
                                        className={`flex items-center justify-between border p-4 rounded-lg transition-colors ${item.selected ? "bg-white border-emerald-200 shadow-sm" : "bg-slate-50 border-transparent opacity-60"}`}
                                    >
                                        <div className="flex items-center gap-3">
                                            <Checkbox checked={item.selected} onCheckedChange={() => handleToggleItem(item.id)} className="data-[state=checked]:bg-emerald-600 data-[state=checked]:border-emerald-600" />
                                            <span className="font-medium text-slate-700">{item.descripcion}</span>
                                        </div>
                                            <span className="font-mono font-bold text-slate-900">${(item.monto || 0).toLocaleString('es-AR')}
                                            </span>
                                    </div>
                                ))}
                            </div>

                            {/* Resumen de Totales */}
                            <div className="bg-slate-50 p-6 rounded-lg border border-slate-100 space-y-2">
                                <div className="flex justify-between text-sm text-slate-600">
                                    <span>Subtotal</span>
                                    <span>${detalleCalculado.montoTotal.toLocaleString('es-AR')}</span>
                                </div>
                                {detalleCalculado.tipoFactura === "A" && (
                                    <div className="flex justify-between text-sm text-slate-600">
                                        <span>IVA (21%)</span>
                                        <span>${detalleCalculado.montoIva.toLocaleString('es-AR')}</span>
                                    </div>
                                )}
                                <div className="border-t border-slate-200 my-2 pt-2 flex justify-between items-center">
                                    <span className="font-bold text-lg text-slate-800">Total a Pagar</span>
                                    <span className="font-bold text-2xl text-emerald-600">${detalleCalculado.montoTotal.toLocaleString('es-AR')}</span>
                                </div>
                            </div>

                            <div className="flex gap-4 pt-2">
                                <Button onClick={() => setStep("select-person")} variant="ghost" className="text-slate-500">
                                    Cancelar y Volver
                                </Button>
                                <Button onClick={handleGenerateInvoice} disabled={isLoading} className="flex-1 bg-emerald-600 hover:bg-emerald-700 text-white shadow-md">
                                    {isLoading ? <Loader2 className="mr-2 h-4 w-4 animate-spin" /> :
                                        <> <CheckCircle2 className="mr-2 h-4 w-4" /> Confirmar e Imprimir Factura </>}
                                </Button>
                            </div>
                        </CardContent>
                    </Card>
                )}

                {/* --- ÉXITO --- */}
                {step === "success" && (
                    <Card className="border-emerald-100 bg-emerald-50/50 shadow-lg text-center py-12 animate-in zoom-in-95 duration-500">
                        <CardContent className="flex flex-col items-center gap-4">
                            <div className="rounded-full bg-emerald-100 p-4 shadow-sm">
                                <CheckCircle2 className="h-12 w-12 text-emerald-600" />
                            </div>
                            <div className="space-y-2">
                                <h2 className="text-2xl font-bold text-emerald-900">¡Factura Generada!</h2>
                                <p className="text-emerald-700">El comprobante ha sido registrado y enviado a impresión.</p>
                            </div>
                            <Button onClick={resetForm} className="mt-8 bg-emerald-600 hover:bg-emerald-700 min-w-[200px]">
                                Realizar Nueva Factura
                            </Button>
                        </CardContent>
                    </Card>
                )}

                {/* --- MODAL TERCEROS --- */}
                <Dialog open={showThirdPartyDialog} onOpenChange={setShowThirdPartyDialog}>
                    <DialogContent className={necesitaAltaEmpresa ? "max-w-3xl" : "max-w-md"}>
                        <DialogHeader>
                            <DialogTitle className="flex items-center gap-2">
                                <Building2 className="h-5 w-5 text-emerald-600" />
                                {necesitaAltaEmpresa ? "Alta de Responsable (Empresa)" : "Facturar a Tercero"}
                            </DialogTitle>
                            <DialogDescription>
                                {necesitaAltaEmpresa ? "Complete los datos fiscales para registrar la nueva empresa en el sistema." : "Ingrese el CUIT para buscar o dar de alta."}
                            </DialogDescription>
                        </DialogHeader>

                        <div className="space-y-4 py-4">
                            {/* Buscador CUIT */}
                            <div className="grid grid-cols-1 gap-2">
                                <Label>CUIT {necesitaAltaEmpresa && "*"}</Label>
                                <div className="flex gap-2">
                                    <Input
                                        value={cuitTercero}
                                        onChange={(e) => setCuitTercero(e.target.value)}
                                        placeholder="30-12345678-9"
                                        className="font-mono"
                                    />
                                </div>
                            </div>

                            {/* FORMULARIO DE ALTA EXTENDIDO */}
                            {necesitaAltaEmpresa && (
                                <div className="space-y-4 animate-in fade-in slide-in-from-top-2 border-t pt-4 mt-2">
                                    <div className="grid grid-cols-2 gap-4">
                                        <div className="col-span-1 space-y-2">
                                            <Label>Razón Social *</Label>
                                            <Input value={razonSocialTercero} onChange={(e) => setRazonSocialTercero(e.target.value)} placeholder="Ej: Tech Solutions S.A." />
                                        </div>
                                        <div className="col-span-1 space-y-2">
                                            <Label>Teléfono *</Label>
                                            <div className="flex items-center gap-2">
                                                <Phone className="h-4 w-4 text-slate-400" />
                                                <Input value={telefonoTercero} onChange={(e) => setTelefonoTercero(e.target.value)} placeholder="Ej: 342555555" type="number"/>
                                            </div>
                                        </div>
                                    </div>

                                    <div className="flex items-center gap-2 text-emerald-700 font-medium mt-2 text-sm bg-emerald-50 p-2 rounded">
                                        <MapPin className="h-4 w-4" /> Domicilio Fiscal
                                    </div>

                                    <div className="grid grid-cols-6 gap-3 p-1">
                                        <div className="col-span-4 space-y-1">
                                            <Label className="text-xs">Calle *</Label>
                                            <Input name="calle" value={direccion.calle} onChange={handleDireccionChange} className="h-8" />
                                        </div>
                                        <div className="col-span-2 space-y-1">
                                            <Label className="text-xs">Número *</Label>
                                            <Input name="numero" value={direccion.numero} onChange={handleDireccionChange} type="number" className="h-8" />
                                        </div>

                                        <div className="col-span-2 space-y-1">
                                            <Label className="text-xs">Piso</Label>
                                            <Input name="piso" value={direccion.piso} onChange={handleDireccionChange} type="number" className="h-8" />
                                        </div>
                                        <div className="col-span-2 space-y-1">
                                            <Label className="text-xs">Depto</Label>
                                            <Input name="departamento" value={direccion.departamento} onChange={handleDireccionChange} className="h-8" />
                                        </div>
                                        <div className="col-span-2 space-y-1">
                                            <Label className="text-xs">CP *</Label>
                                            <Input name="codPostal" value={direccion.codPostal} onChange={handleDireccionChange} type="number" className="h-8" />
                                        </div>

                                        <div className="col-span-3 space-y-1">
                                            <Label className="text-xs">Localidad *</Label>
                                            <Input name="localidad" value={direccion.localidad} onChange={handleDireccionChange} className="h-8" />
                                        </div>
                                        <div className="col-span-3 space-y-1">
                                            <Label className="text-xs">Provincia *</Label>
                                            <Input name="provincia" value={direccion.provincia} onChange={handleDireccionChange} className="h-8" />
                                        </div>
                                        <div className="col-span-6 space-y-1">
                                            <Label className="text-xs">País *</Label>
                                            <Input name="pais" value={direccion.pais} onChange={handleDireccionChange} className="h-8" />
                                        </div>
                                    </div>
                                </div>
                            )}
                            {errorMessage && <p className="text-sm text-red-500 font-medium text-center bg-red-50 p-2 rounded">{errorMessage}</p>}
                        </div>

                        <DialogFooter>
                            <Button variant="outline" onClick={() => setShowThirdPartyDialog(false)}>Cancelar</Button>
                            {necesitaAltaEmpresa ? (
                                <Button onClick={handleCrearEmpresa} disabled={isLoading} className="bg-emerald-600 hover:bg-emerald-700 text-white">
                                    {isLoading ? "Guardando..." : "Guardar Empresa"}
                                </Button>
                            ) : (
                                <Button onClick={obtainingDetalleFacturacionWrapper} disabled={isLoading}>
                                    {isLoading ? "Buscando..." : "Buscar"}
                                </Button>
                            )}
                        </DialogFooter>
                    </DialogContent>
                </Dialog>
            </div>
        </div>
    )
}