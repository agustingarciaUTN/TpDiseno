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
import { AlertCircle, CheckCircle2, FileText, ArrowLeft, Loader2, MapPin, Phone } from "lucide-react"
import Link from "next/link"

// ... (Interfaces Ocupante, ItemFactura, DetalleFacturacion igual que antes) ...
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

    // --- PASO 1: BUSCAR HABITACIÓN ---
    const handleSearch = async () => {
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

    // --- PASO 2: SELECCIONAR PERSONA ---
    const handleSelectPerson = async (ocupante: Ocupante) => {
        setErrorMessage("")
        setSelectedOcupante(ocupante)

        await obtenerDetalleFacturacion({
            esTercero: false,
            tipoDoc: ocupante.tipoDocumento,
            nroDoc: ocupante.nroDocumento
        });
    }

    // --- LÓGICA CENTRAL: CALCULAR DETALLE ---
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
                    setErrorMessage("El CUIT no existe. Complete el formulario para dar el alta.");
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

            // Mapeo Items
            const itemsMapeados: ItemFactura[] = [
                { id: "estadia", descripcion: "Alojamiento Base", monto: data.montoEstadiaBase, selected: true }
            ];
            if (data.recargoHorario > 0) {
                itemsMapeados.push({ id: "recargo", descripcion: data.detalleRecargo, monto: data.recargoHorario, selected: true });
            }
            data.serviciosAdicionales.forEach((serv, idx) => {
                itemsMapeados.push({ id: `serv-${idx}`, descripcion: serv.descripcion, monto: serv.valor, selected: true });
            });

            setItems(itemsMapeados);
            setShowThirdPartyDialog(false);
            setStep("select-items");

        } catch (error: any) {
            setErrorMessage(error.message);
        } finally {
            setIsLoading(false);
        }
    }

    // --- LÓGICA: ALTA RÁPIDA DE EMPRESA (POST) ---
    const handleCrearEmpresa = async () => {
        setErrorMessage("");

        // 1. Validaciones Frontend Básicas
        if (!cuitTercero || !razonSocialTercero || !telefonoTercero ||
            !direccion.calle || !direccion.numero || !direccion.codPostal ||
            !direccion.localidad || !direccion.provincia || !direccion.pais) {
            setErrorMessage("Por favor complete todos los campos obligatorios (*)");
            return;
        }

        // Validación Regex CUIT
        const cuitRegex = /^\d{2}-?\d{8}-?\d{1}$/;
        if (!cuitRegex.test(cuitTercero)) {
            setErrorMessage("El formato del CUIT es inválido (Ej: 30-12345678-9)");
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
                    telefono: [parseInt(telefonoTercero)], // Enviamos como lista de Long
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

            // Éxito: Volvemos al modo de búsqueda simple con el CUIT precargado
            setIdResponsableSeleccionado(nuevoId);
            setNecesitaAltaEmpresa(false);
            setErrorMessage("");

            // Limpiamos campos del formulario (opcional)
            // setRazonSocialTercero(""); setDireccion({...});

        } catch (error: any) {
            setErrorMessage("Error: " + error.message);
            setIsLoading(false);
        }
    }

    // --- MANEJO UI ---
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
            setNecesitaAltaEmpresa(true); // Activa el modo alta
            return;
        }
        setErrorMessage("");
        obtenerDetalleFacturacion({ esTercero: true, idResponsableJuridico: undefined });
    }

    const handleDireccionChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setDireccion({ ...direccion, [e.target.name]: e.target.value });
    }

    const resetForm = () => {
        window.location.reload(); // Manera más fácil de limpiar todo
    }

    // --- RENDER ---
    return (
        <div className="space-y-6">
            <div className="flex items-center gap-4">
                <Button variant="ghost" size="icon" asChild><Link href="/"><ArrowLeft className="h-5 w-5" /></Link></Button>
                <div>
                    <h1 className="text-3xl font-bold text-slate-900">Registrar Factura</h1>
                    <p className="text-slate-600">Integración con Spring Boot</p>
                </div>
            </div>

            {errorMessage && (
                <Alert variant="destructive">
                    <AlertCircle className="h-4 w-4" />
                    <AlertDescription>{errorMessage}</AlertDescription>
                </Alert>
            )}

            {/* PASO 1 */}
            {step === "search" && (
                <Card>
                    <CardHeader><CardTitle>Buscar Habitación</CardTitle></CardHeader>
                    <CardContent className="space-y-4">
                        <div className="grid grid-cols-2 gap-4">
                            <div className="space-y-2">
                                <Label>Nro Habitación</Label>
                                <Input value={numeroHabitacion} onChange={(e) => setNumeroHabitacion(e.target.value)} placeholder="Ej: 101" />
                            </div>
                            <div className="space-y-2">
                                <Label>Hora Salida</Label>
                                <Input type="time" value={horaSalida} onChange={(e) => setHoraSalida(e.target.value)} />
                            </div>
                        </div>
                        <Button onClick={handleSearch} className="w-full" disabled={isLoading}>
                            {isLoading ? <Loader2 className="mr-2 h-4 w-4 animate-spin" /> : "Buscar Ocupantes"}
                        </Button>
                    </CardContent>
                </Card>
            )}

            {/* PASO 2 */}
            {step === "select-person" && (
                <Card>
                    <CardHeader><CardTitle>Seleccionar Responsable</CardTitle></CardHeader>
                    <CardContent className="space-y-4">
                        <div className="space-y-3">
                            {ocupantes.map((ocup, idx) => (
                                <Card key={idx} className="cursor-pointer hover:border-blue-500" onClick={() => handleSelectPerson(ocup)}>
                                    <CardContent className="flex items-center justify-between p-4">
                                        <div><p className="font-medium">{ocup.apellido}, {ocup.nombres}</p><p className="text-sm text-slate-500">{ocup.tipoDocumento}: {ocup.nroDocumento}</p></div>
                                        <Badge variant="outline">Huésped</Badge>
                                    </CardContent>
                                </Card>
                            ))}
                        </div>
                        <div className="flex gap-2">
                            <Button variant="outline" onClick={() => setStep("search")} className="flex-1">Volver</Button>
                            <Button variant="secondary" onClick={() => { setErrorMessage(""); setShowThirdPartyDialog(true); setNecesitaAltaEmpresa(false); }} className="flex-1">Facturar a Empresa</Button>
                        </div>
                    </CardContent>
                </Card>
            )}

            {/* PASO 3 */}
            {step === "select-items" && detalleCalculado && (
                <Card>
                    <CardHeader><CardTitle>Detalle Facturación</CardTitle><CardDescription>Resp: {detalleCalculado.nombreResponsable}</CardDescription></CardHeader>
                    <CardContent className="space-y-4">
                        <div className="space-y-2">
                            {items.map((item) => (
                                <div key={item.id} className="flex items-center justify-between border p-3 rounded">
                                    <div className="flex items-center gap-2"><Checkbox checked={item.selected} onCheckedChange={() => handleToggleItem(item.id)} /><span>{item.descripcion}</span></div>
                                    <span className="font-bold">${item.monto}</span>
                                </div>
                            ))}
                        </div>
                        <div className="bg-slate-100 p-4 rounded text-right">
                             <p>Tipo Factura: <strong>{detalleCalculado.tipoFactura}</strong></p>
                             <p className="text-2xl font-bold text-blue-600">Total: ${detalleCalculado.montoTotal}</p>
                        </div>
                        <div className="flex gap-2">
                             <Button onClick={() => setStep("select-person")} variant="outline">Volver</Button>
                             <Button onClick={handleGenerateInvoice} disabled={isLoading} className="flex-1">{isLoading ? "Generando..." : "Confirmar e Imprimir"}</Button>
                        </div>
                    </CardContent>
                </Card>
            )}

            {/* EXITO */}
            {step === "success" && (
                <Card className="bg-green-50 border-green-200">
                    <CardContent className="flex flex-col items-center p-8 text-center">
                        <CheckCircle2 className="h-16 w-16 text-green-600 mb-4" />
                        <h2 className="text-2xl font-bold text-green-800">Factura Creada Exitosamente</h2>
                        <Button onClick={resetForm} className="mt-6">Volver al Inicio</Button>
                    </CardContent>
                </Card>
            )}

            {/* MODAL TERCEROS */}
            <Dialog open={showThirdPartyDialog} onOpenChange={setShowThirdPartyDialog}>
                <DialogContent className={necesitaAltaEmpresa ? "max-w-3xl" : "max-w-md"}>
                    <DialogHeader>
                        <DialogTitle>{necesitaAltaEmpresa ? "Alta de Responsable de Pago (CU-12)" : "Seleccionar Tercero"}</DialogTitle>
                        <DialogDescription>{necesitaAltaEmpresa ? "Complete todos los campos obligatorios (*) para registrar la empresa." : "Ingrese el CUIT del responsable."}</DialogDescription>
                    </DialogHeader>

                    <div className="space-y-4 py-4">
                        {/* Buscador CUIT */}
                        <div className="grid grid-cols-1 gap-2">
                            <Label>CUIT {necesitaAltaEmpresa && "*"}</Label>
                            <Input value={cuitTercero} onChange={(e) => setCuitTercero(e.target.value)} placeholder="30-12345678-9" disabled={false} />
                        </div>

                        {/* FORMULARIO DE ALTA EXTENDIDO */}
                        {necesitaAltaEmpresa && (
                            <div className="space-y-4 animate-in fade-in slide-in-from-top-2 border-t pt-4 mt-2">
                                <div className="grid grid-cols-2 gap-4">
                                    <div className="col-span-1 space-y-2">
                                        <Label>Razón Social *</Label>
                                        <Input value={razonSocialTercero} onChange={(e) => setRazonSocialTercero(e.target.value)} placeholder="Ej: Empresa S.A." />
                                    </div>
                                    <div className="col-span-1 space-y-2">
                                        <Label>Teléfono *</Label>
                                        <div className="flex items-center gap-2">
                                            <Phone className="h-4 w-4 text-slate-500" />
                                            <Input value={telefonoTercero} onChange={(e) => setTelefonoTercero(e.target.value)} placeholder="Ej: 342555555" type="number"/>
                                        </div>
                                    </div>
                                </div>

                                <div className="flex items-center gap-2 text-slate-700 font-medium mt-2">
                                    <MapPin className="h-4 w-4" /> Dirección
                                </div>

                                <div className="grid grid-cols-6 gap-3 bg-slate-50 p-3 rounded-md">
                                    <div className="col-span-4 space-y-1">
                                        <Label className="text-xs">Calle *</Label>
                                        <Input name="calle" value={direccion.calle} onChange={handleDireccionChange} placeholder="Ej: San Martin" className="h-8" />
                                    </div>
                                    <div className="col-span-2 space-y-1">
                                        <Label className="text-xs">Número *</Label>
                                        <Input name="numero" value={direccion.numero} onChange={handleDireccionChange} placeholder="123" type="number" className="h-8" />
                                    </div>

                                    <div className="col-span-2 space-y-1">
                                        <Label className="text-xs">Piso</Label>
                                        <Input name="piso" value={direccion.piso} onChange={handleDireccionChange} placeholder="-" type="number" className="h-8" />
                                    </div>
                                    <div className="col-span-2 space-y-1">
                                        <Label className="text-xs">Depto</Label>
                                        <Input name="departamento" value={direccion.departamento} onChange={handleDireccionChange} placeholder="-" className="h-8" />
                                    </div>
                                    <div className="col-span-2 space-y-1">
                                        <Label className="text-xs">CP *</Label>
                                        <Input name="codPostal" value={direccion.codPostal} onChange={handleDireccionChange} placeholder="3000" type="number" className="h-8" />
                                    </div>

                                    <div className="col-span-3 space-y-1">
                                        <Label className="text-xs">Localidad *</Label>
                                        <Input name="localidad" value={direccion.localidad} onChange={handleDireccionChange} placeholder="Santa Fe" className="h-8" />
                                    </div>
                                    <div className="col-span-3 space-y-1">
                                        <Label className="text-xs">Provincia *</Label>
                                        <Input name="provincia" value={direccion.provincia} onChange={handleDireccionChange} placeholder="Santa Fe" className="h-8" />
                                    </div>
                                    <div className="col-span-6 space-y-1">
                                        <Label className="text-xs">País *</Label>
                                        <Input name="pais" value={direccion.pais} onChange={handleDireccionChange} className="h-8" disabled />
                                    </div>
                                </div>
                            </div>
                        )}
                        {errorMessage && <p className="text-sm text-red-500 font-medium text-center">{errorMessage}</p>}
                    </div>

                    <DialogFooter>
                        <Button variant="outline" onClick={() => setShowThirdPartyDialog(false)}>Cancelar</Button>
                        {necesitaAltaEmpresa ? (
                            <Button onClick={handleCrearEmpresa} disabled={isLoading}>{isLoading ? "Guardando..." : "Guardar Empresa"}</Button>
                        ) : (
                            <Button onClick={obtainingDetalleFacturacionWrapper} disabled={isLoading}>{isLoading ? "Buscando..." : "Buscar"}</Button>
                        )}
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </div>
    )
}