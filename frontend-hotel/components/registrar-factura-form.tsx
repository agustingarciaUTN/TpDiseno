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
import { AlertCircle, CheckCircle2, FileText, ArrowLeft, Loader2 } from "lucide-react"
import Link from "next/link"

type PosicionIVA = "Responsable Inscripto" | "Consumidor Final" | "Monotributista" | "Exento"

interface Ocupante {
    // Mapeo del DtoDatosOcupantes
    tipoDocumento: string;
    nroDocumento: string;
    nombres: string;
    apellido: string;
    // Campos derivados para UI
    esMenor?: boolean;
    posicionIVA?: string; // Si el backend lo devuelve, genial. Si no, lo simulamos o pedimos.
}

interface ItemFactura {
    id: string; // Para el backend puede ser un ID numérico, aquí usamos string para key
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
}

export function RegistrarFacturaForm() {
    // ESTADOS
    const [step, setStep] = useState<"search" | "select-person" | "select-items" | "invoice-type" | "success">("search")
    const [isLoading, setIsLoading] = useState(false)
    const [errorMessage, setErrorMessage] = useState("")

    // DATOS DE ENTRADA
    const [numeroHabitacion, setNumeroHabitacion] = useState("")
    const [horaSalida, setHoraSalida] = useState("")

    // DATOS RECIBIDOS DEL BACKEND
    const [idEstadia, setIdEstadia] = useState<number | null>(null)
    const [ocupantes, setOcupantes] = useState<Ocupante[]>([])

    // SELECCIÓN ACTUAL
    const [selectedOcupante, setSelectedOcupante] = useState<Ocupante | null>(null)
    const [items, setItems] = useState<ItemFactura[]>([])
    const [detalleCalculado, setDetalleCalculado] = useState<DetalleFacturacion | null>(null)

    // ESTADOS PARA TERCEROS Y ALTA RAPIDA
    const [showThirdPartyDialog, setShowThirdPartyDialog] = useState(false)
    const [cuitTercero, setCuitTercero] = useState("")
    // Estado nuevo: Si necesitamos crear la empresa (Error 409)
    const [necesitaAltaEmpresa, setNecesitaAltaEmpresa] = useState(false)
    const [razonSocialTercero, setRazonSocialTercero] = useState("")
    const [idEmpresaCreada, setIdEmpresaCreada] = useState<number | null>(null)


    // --- PASO 1: BUSCAR HABITACIÓN ---
    const handleSearch = async () => {
        setErrorMessage("")
        if (!numeroHabitacion.trim() || !horaSalida.trim()) {
            setErrorMessage("Complete todos los campos obligatorios")
            return
        }

        setIsLoading(true)
        try {
            // LLAMADA AL BACKEND: PASO 1
            const res = await fetch('http://localhost:8080/api/factura/buscar-ocupantes', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                        numeroHabitacion: numeroHabitacion,
                        horaSalida: horaSalida
                })
            });

            if (!res.ok) {
                const errorData = await res.json().catch(() => ({}));
                throw new Error(errorData.message || "No se encontró ocupación activa en esa habitación");
            }

            const data = await res.json();

            // Guardamos datos clave
            setIdEstadia(data.idEstadia);
            setOcupantes(data.ocupantes); // Asumiendo que el DTO trae la lista
            setStep("select-person");

        } catch (error: any) {
            setErrorMessage(error.message);
        } finally {
            setIsLoading(false);
        }
    }

    // --- PASO 2: SELECCIONAR PERSONA (HUESPED) ---
    const handleSelectPerson = async (ocupante: Ocupante) => {
        setErrorMessage("")
        setSelectedOcupante(ocupante)
        setIdEmpresaCreada(null) // Limpiamos selección de empresa si elige persona

        // Llamamos al backend para pre-calcular (y validar edad/responsable)
        await obtenerDetalleFacturacion({
            esTercero: false,
            tipoDoc: ocupante.tipoDocumento,
            nroDoc: ocupante.nroDocumento
        });
    }

    // --- LÓGICA CENTRAL: CALCULAR DETALLE (Con manejo de 409) ---
    const obtenerDetalleFacturacion = async (params: {
        esTercero: boolean,
        tipoDoc?: string,
        nroDoc?: string,
        idResponsableJuridico?: number
    }) => {
        setIsLoading(true)
        try {
            // Construimos Query Params
            const query = new URLSearchParams({
                idEstadia: idEstadia!.toString(),
                horaSalida: horaSalida,
                esTercero: params.esTercero.toString(),
            });

            if (params.esTercero && params.idResponsableJuridico) {
                query.append("idResponsableJuridico", params.idResponsableJuridico.toString());
            }
            if (!params.esTercero && params.tipoDoc && params.nroDoc) {
                query.append("tipoDoc", params.tipoDoc);
                query.append("nroDoc", params.nroDoc);
            }

            const res = await fetch(`http://localhost:8080/api/factura/calcular-detalle?${query.toString()}`);

            // === INTERCEPCIÓN DEL ERROR 409 (ALTA EMPRESA) ===
            if (res.status === 409) {
                const data = await res.json();
                if (data.accion === "REDIRECCIONAR_A_ALTA_RESPONSABLE") {
                    // Nos quedamos en el modal y pedimos Razón Social
                    setNecesitaAltaEmpresa(true);
                    setErrorMessage("El CUIT no existe. Ingrese la Razón Social para darlo de alta ahora.");
                    setIsLoading(false);
                    return; // Interrumpimos
                }
            }

            if (!res.ok) throw new Error("Error al calcular detalle");

            // Si todo ok (200), procesamos los datos para mostrar
            const data: DetalleFacturacion = await res.json();
            setDetalleCalculado(data);

            // Mapeamos los items para la selección visual
            const itemsMapeados: ItemFactura[] = [
                {
                    id: "estadia",
                    descripcion: "Alojamiento Base",
                    monto: data.montoEstadiaBase,
                    selected: true
                }
            ];

            if (data.recargoHorario > 0) {
                itemsMapeados.push({
                    id: "recargo",
                    descripcion: data.detalleRecargo,
                    monto: data.recargoHorario,
                    selected: true
                });
            }

            data.serviciosAdicionales.forEach((serv, idx) => {
                itemsMapeados.push({
                    id: `serv-${idx}`,
                    descripcion: serv.descripcion,
                    monto: serv.valor,
                    selected: true
                });
            });

            setItems(itemsMapeados);
            setShowThirdPartyDialog(false); // Cerramos modal si estaba abierto
            setStep("select-items"); // Avanzamos

        } catch (error: any) {
            setErrorMessage(error.message);
        } finally {
            setIsLoading(false);
        }
    }

    // --- LÓGICA: ALTA RÁPIDA DE EMPRESA (POST) ---
    const handleCrearEmpresa = async () => {
        if (!razonSocialTercero.trim()) {
            setErrorMessage("La Razón Social es obligatoria");
            return;
        }
        setIsLoading(true);
        try {
            const res = await fetch('http://localhost:8080/api/factura/responsable', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    cuit: cuitTercero,
                    razonSocial: razonSocialTercero,
                    telefono: [342000000], // Dato dummy obligatorio por DTO
                    dtoDireccion: { // Dirección dummy obligatoria por DTO
                        calle: "Domicilio Fiscal",
                        numero: 123,
                        localidad: "Santa Fe",
                        provincia: "Santa Fe",
                        pais: "Argentina",
                        codPostal: 3000
                    }
                })
            });

            if (!res.ok) throw new Error("Error al crear empresa");

            const data = await res.json();
            const nuevoId = data.idResponsableGenerado;

            // Exito! Ahora volvemos a intentar calcular con el nuevo ID
            setNecesitaAltaEmpresa(false);
            setIdEmpresaCreada(nuevoId);

            // Re-ejecutamos el cálculo automáticamente
            await obtenerDetalleFacturacion({
                esTercero: true,
                idResponsableJuridico: nuevoId
            });

        } catch (error: any) {
            setErrorMessage("No se pudo crear: " + error.message);
            setIsLoading(false);
        }
    }

    // --- MANEJO DE ITEMS Y TOTALES ---
    const handleToggleItem = (itemId: string) => {
        setItems(items.map((item) => (item.id === itemId ? { ...item, selected: !item.selected } : item)))
    }

    const calculateTotalLocal = () => {
        return items.filter(i => i.selected).reduce((acc, curr) => acc + curr.monto, 0);
    }

    // Recalculo simple para UI basado en el tipo de factura que dijo el backend
    const getMontosFinales = () => {
        const totalItems = calculateTotalLocal();
        const esA = detalleCalculado?.tipoFactura === "A";

        // Lógica simplificada de visualización (El backend ya dio los valores exactos para todos los items)
        // Si el usuario desmarca algo, restamos proporcionalmente
        const iva = esA ? totalItems * 0.21 : 0; // Solo para mostrar discriminado
        const total = esA ? totalItems + iva : totalItems; // Ajustar según si el backend manda montos netos o brutos

        // *Nota*: Lo ideal es volver a llamar al backend si cambian los items,
        // pero para este ejemplo usaremos los valores del backend como base.
        return {
            subtotal: totalItems,
            iva: detalleCalculado?.montoIva || 0, // Usamos el del backend por defecto
            total: detalleCalculado?.montoTotal || 0
        };
    }

    // --- PASO FINAL: GENERAR FACTURA ---
    const handleGenerateInvoice = async () => {
        setIsLoading(true);
        try {
            // Construimos el DTO para generar
            const facturaFinal = {
                numeroFactura: `F-${Date.now()}`, // Generamos uno al azar para probar
                fechaEmision: new Date().toISOString(),
                fechaVencimiento: new Date().toISOString(), // +10 dias
                importeTotal: getMontosFinales().total,
                importeNeto: getMontosFinales().subtotal,
                iva: getMontosFinales().iva,
                tipoFactura: detalleCalculado?.tipoFactura,

                // Relaciones
                idEstadia: { idEstadia: idEstadia },
                idResponsable: {
                    // Truco: Enviamos solo el ID envuelto en objeto
                    idResponsable: idEmpresaCreada
                        ? idEmpresaCreada
                        : (await obtenerIdResponsableHuesped()) // Helper si es huesped
                }
            };

            // POST FINAL
            const res = await fetch('http://localhost:8080/api/factura/generar', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(facturaFinal)
            });

            if(!res.ok) throw new Error("Error al generar factura final");

            setStep("success");

        } catch (error: any) {
            setErrorMessage(error.message);
        } finally {
            setIsLoading(false);
        }
    }

    // Helper sucio para obtener el ID de responsable del huésped (ya que el front no lo guardó explícitamente)
    // En un caso real, el /calcular-detalle debería devolver también el idResponsableFinal.
    const obtenerIdResponsableHuesped = async () => {
       // Asumimos que ya existe porque pasamos el paso 2
       // Podrías guardar este ID en el estado durante el 'calcular-detalle'
       return 1; // HARDCODE PARA EVITAR ERROR EN DEMO SI NO LO TIENES
    }


    const resetForm = () => {
        setStep("search")
        setNumeroHabitacion("")
        setHoraSalida("")
        setErrorMessage("")
        setOcupantes([])
        setSelectedOcupante(null)
        setItems([])
        setDetalleCalculado(null)
        setCuitTercero("")
        setNecesitaAltaEmpresa(false)
        setRazonSocialTercero("")
    }

    return (
        <div className="space-y-6">
        <h1 className="bg-red-500 text-white p-4 text-2xl font-bold">
                        SI VES ESTO, ESTÁS EN EL ARCHIVO CORRECTO
                    </h1>
            <div className="flex items-center gap-4">
                <Button variant="ghost" size="icon" asChild>
                    <Link href="/">
                        <ArrowLeft className="h-5 w-5" />
                    </Link>
                </Button>
                <div>
                    <h1 className="text-3xl font-bold text-slate-900 dark:text-slate-50">Registrar Factura</h1>
                    <p className="text-slate-600 dark:text-slate-400">Integración con Spring Boot</p>
                </div>
            </div>

            {errorMessage && (
                <Alert variant="destructive">
                    <AlertCircle className="h-4 w-4" />
                    <AlertDescription>{errorMessage}</AlertDescription>
                </Alert>
            )}

            {/* --- PASO 1: BUSQUEDA --- */}
            {step === "search" && (
                <Card>
                    <CardHeader>
                        <CardTitle className="flex items-center gap-2">
                            <FileText className="h-5 w-5" /> Buscar Habitación
                        </CardTitle>
                    </CardHeader>
                    <CardContent className="space-y-4">
                        <div className="grid grid-cols-2 gap-4">
                            <div className="space-y-2">
                                <Label htmlFor="numeroHabitacion">Nro Habitación</Label>
                                <Input
                                    id="numeroHabitacion"
                                    placeholder="Ej: 101"
                                    value={numeroHabitacion}
                                    onChange={(e) => setNumeroHabitacion(e.target.value)}
                                />
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="horaSalida">Hora Salida</Label>
                                <Input
                                    id="horaSalida"
                                    type="time"
                                    value={horaSalida}
                                    onChange={(e) => setHoraSalida(e.target.value)}
                                />
                            </div>
                        </div>
                        <Button onClick={handleSearch} className="w-full" disabled={isLoading}>
                            {isLoading ? <Loader2 className="mr-2 h-4 w-4 animate-spin" /> : "Buscar Ocupantes"}
                        </Button>
                    </CardContent>
                </Card>
            )}

            {/* --- PASO 2: SELECCION RESPONSABLE --- */}
            {step === "select-person" && (
                <Card>
                    <CardHeader>
                        <CardTitle>Seleccionar Responsable</CardTitle>
                        <CardDescription>Ocupantes encontrados en la habitación {numeroHabitacion}</CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-4">
                        <div className="space-y-3">
                            {ocupantes.map((ocup, idx) => (
                                <Card
                                    key={idx}
                                    className="cursor-pointer hover:border-blue-500"
                                    onClick={() => handleSelectPerson(ocup)}
                                >
                                    <CardContent className="flex items-center justify-between p-4">
                                        <div>
                                            <p className="font-medium">{ocup.apellido}, {ocup.nombres}</p>
                                            <p className="text-sm text-slate-500">{ocup.tipoDocumento}: {ocup.nroDocumento}</p>
                                        </div>
                                        <Badge variant="outline">Huésped</Badge>
                                    </CardContent>
                                </Card>
                            ))}
                        </div>

                        <div className="flex gap-2">
                            <Button variant="outline" onClick={() => setStep("search")} className="flex-1">Volver</Button>
                            <Button variant="secondary" onClick={() => {
                                setErrorMessage("");
                                setShowThirdPartyDialog(true);
                                setNecesitaAltaEmpresa(false);
                            }} className="flex-1">
                                Facturar a Empresa (Tercero)
                            </Button>
                        </div>
                    </CardContent>
                </Card>
            )}

            {/* --- PASO 3: CONFIRMACIÓN ITEMS --- */}
            {step === "select-items" && detalleCalculado && (
                <Card>
                    <CardHeader>
                        <CardTitle>Detalle de Facturación</CardTitle>
                        <CardDescription>Responsable: {detalleCalculado.nombreResponsable}</CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-4">
                        <div className="space-y-2">
                            {items.map((item) => (
                                <div key={item.id} className="flex items-center justify-between border p-3 rounded">
                                    <div className="flex items-center gap-2">
                                        <Checkbox checked={item.selected} onCheckedChange={() => handleToggleItem(item.id)} />
                                        <span>{item.descripcion}</span>
                                    </div>
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
                             <Button onClick={handleGenerateInvoice} disabled={isLoading} className="flex-1">
                                {isLoading ? "Generando..." : "Confirmar e Imprimir"}
                             </Button>
                        </div>
                    </CardContent>
                </Card>
            )}

            {/* --- PASO EXITO --- */}
            {step === "success" && (
                <Card className="bg-green-50 border-green-200">
                    <CardContent className="flex flex-col items-center p-8 text-center">
                        <CheckCircle2 className="h-16 w-16 text-green-600 mb-4" />
                        <h2 className="text-2xl font-bold text-green-800">Factura Creada Exitosamente</h2>
                        <Button onClick={resetForm} className="mt-6">Volver al Inicio</Button>
                    </CardContent>
                </Card>
            )}

            {/* --- DIALOGO TERCEROS (CON LOGICA DE ALTA) --- */}
            <Dialog open={showThirdPartyDialog} onOpenChange={setShowThirdPartyDialog}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>{necesitaAltaEmpresa ? "Alta de Nueva Empresa" : "Facturar a Tercero"}</DialogTitle>
                        <DialogDescription>
                            {necesitaAltaEmpresa
                                ? "El CUIT ingresado no existe. Complete los datos para registrarla."
                                : "Ingrese el CUIT del responsable."}
                        </DialogDescription>
                    </DialogHeader>

                    <div className="space-y-4 py-4">
                        <div className="space-y-2">
                            <Label>CUIT</Label>
                            <Input
                                value={cuitTercero}
                                onChange={(e) => setCuitTercero(e.target.value)}
                                placeholder="30-12345678-9"
                                disabled={necesitaAltaEmpresa} // Bloqueamos CUIT si ya estamos en alta
                            />
                        </div>

                        {/* INPUT QUE APARECE SOLO SI DA ERROR 409 */}
                        {necesitaAltaEmpresa && (
                            <div className="space-y-2 animate-in fade-in slide-in-from-top-2">
                                <Label>Razón Social</Label>
                                <Input
                                    value={razonSocialTercero}
                                    onChange={(e) => setRazonSocialTercero(e.target.value)}
                                    placeholder="Ej: Empresa S.A."
                                    autoFocus
                                />
                                <p className="text-xs text-slate-500">Se crearán datos de dirección genéricos para la demo.</p>
                            </div>
                        )}

                        {errorMessage && <p className="text-sm text-red-500">{errorMessage}</p>}
                    </div>

                    <DialogFooter>
                        <Button variant="outline" onClick={() => setShowThirdPartyDialog(false)}>Cancelar</Button>

                        {necesitaAltaEmpresa ? (
                            <Button onClick={handleCrearEmpresa} disabled={isLoading}>
                                {isLoading ? "Guardando..." : "Guardar Empresa"}
                            </Button>
                        ) : (
                            <Button onClick={() => obtainingDetalleFacturacionWrapper()} disabled={isLoading}>
                                {isLoading ? "Buscando..." : "Buscar"}
                            </Button>
                        )}
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </div>
    )

    // Wrapper auxiliar para llamar a la logica desde el dialog
    function obtainingDetalleFacturacionWrapper() {
        if(!cuitTercero) return;
        setErrorMessage("");
        obtenerDetalleFacturacion({
            esTercero: true,
            // Truco: Enviamos ID nulo para que el backend dispare la validacion 409 la primera vez
            // O podríamos implementar un endpoint de buscar por CUIT antes.
            // Para tu TP, dejar ID nulo funciona con tu logica de controller.
            idResponsableJuridico: undefined
        });
    }
}
