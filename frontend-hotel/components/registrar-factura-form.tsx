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

// Icons
import {
    AlertCircle,
    CheckCircle2,
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

// --- CONSTANTES Y REGEX DE VALIDACIÓN ---
const regexCuit = /^\d{2}-?\d{8}-?\d{1}$/
const regexTelefono = /^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\s./0-9]*$/
const regexCalle = /^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\s.,°()-]+$/
const regexTexto = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/

const MSJ_OBLIGATORIO = "Este campo es obligatorio"
const MSJ_TEXTO = "Solo se permiten letras y espacios"
const MSJ_NUMERICO = "Solo se permiten números válidos"
const MSJ_FORMATO_TEL = "Formato inválido (ej: +54 342 1234567)"
const MSJ_LARGO_CORTO = "El texto ingresado es demasiado corto"

// --- INTERFACES ---
interface Ocupante { tipoDocumento: string; nroDocumento: string; nombres: string; apellido: string; }
interface ItemFactura { id: string; descripcion: string; monto: number; selected: boolean; }
interface DetalleFacturacion { nombreResponsable: string; cuitResponsable: string | null; montoEstadiaBase: number; recargoHorario: number; detalleRecargo: string; subtotal: number; montoIva: number; montoTotal: number; tipoFactura: "A" | "B"; serviciosAdicionales: { descripcion: string; valor: number }[]; idResponsable: number; }

export function RegistrarFacturaForm() {
    const router = useRouter()

    // ESTADOS PRINCIPALES
    const [step, setStep] = useState<"search" | "select-person" | "select-items" | "create-company" | "success">("search")

    const [isLoading, setIsLoading] = useState(false)
    const [errorMessage, setErrorMessage] = useState("")

    // DATOS DE ENTRADA
    const [numeroHabitacion, setNumeroHabitacion] = useState("")
    const [horaSalida, setHoraSalida] = useState("")
    const [idEstadia, setIdEstadia] = useState<number | null>(null)
    const [ocupantes, setOcupantes] = useState<Ocupante[]>([])

    // SELECCIÓN Y DETALLE
    const [selectedOcupante, setSelectedOcupante] = useState<Ocupante | null>(null)
    const [items, setItems] = useState<ItemFactura[]>([])
    const [detalleCalculado, setDetalleCalculado] = useState<DetalleFacturacion | null>(null)
    const [idResponsableSeleccionado, setIdResponsableSeleccionado] = useState<number | null>(null);

    // FORMULARIO ALTA EMPRESA
    const [cuitTercero, setCuitTercero] = useState("")
    const [razonSocialTercero, setRazonSocialTercero] = useState("")
    const [telefonoTercero, setTelefonoTercero] = useState("")
    const [direccion, setDireccion] = useState({
        calle: "", numero: "", piso: "", departamento: "",
        codPostal: "", localidad: "", provincia: "", pais: ""
    })
    const [popupErrors, setPopupErrors] = useState<{ [key: string]: string }>({})

    // --- VALIDACIONES ---
    const validarCampoPopup = (nombre: string, valor: string) => {
            let error = ""
            switch (nombre) {
                case "razonSocialTercero":
                    if (!valor.trim()) error = MSJ_OBLIGATORIO
                    else if (valor.length < 3) error = "El nombre es muy corto"
                    else if (valor.length > 50) error = "El nombre es muy largo"
                    else if (!regexCalle.test(valor)) error = "Caracteres inválidos"
                    break;
                case "cuitTercero":
                    if (!valor.trim()) error = MSJ_OBLIGATORIO
                    else if (!regexCuit.test(valor)) error = "Formato inválido (Ej: 30-12345678-9)"
                    // No validamos longitud numérica pura aca porque la regex exige formato xx-xxxxxxxx-x
                    break;
                case "telefonoTercero":
                    if (!valor.trim()) error = MSJ_OBLIGATORIO
                    else if (valor.length < 7) error = "El número es muy corto" // Validación de longitud mínima
                    else if (!regexTelefono.test(valor)) error = MSJ_FORMATO_TEL
                    break;
                case "calle":
                case "localidad":
                case "departamento":
                    if (nombre !== "departamento" && !valor.trim()) error = MSJ_OBLIGATORIO
                    else if (valor.trim() && !regexCalle.test(valor)) error = "Caracteres inválidos"
                    // Validación de largo para depto
                    else if (nombre === "departamento" && valor.trim().length > 10) error = "Máx 10 caracteres"
                    break;
                case "provincia":
                case "pais":
                    if (!valor.trim()) error = MSJ_OBLIGATORIO
                    else if (!regexTexto.test(valor) && nombre !== 'calle') error = MSJ_TEXTO // Calle permite números
                    break;
                case "numero":
                case "codPostal":
                    if (!valor.trim()) error = MSJ_OBLIGATORIO
                    else if (!/^\d+$/.test(valor)) error = MSJ_NUMERICO
                    break;
                case "piso": // Opcional real
                    // Solo validamos si escribió algo.
                    if (valor.trim() && !/^\d+$/.test(valor)) error = MSJ_NUMERICO
                    break;
            }
            setPopupErrors((prev) => {
                const newErrors = { ...prev }
                if (error) newErrors[nombre] = error
                else delete newErrors[nombre]
                return newErrors
            })
            return error
        }

    const validateAllPopup = () => {
        let isValid = true
        if(validarCampoPopup("cuitTercero", cuitTercero)) isValid = false;
        if(validarCampoPopup("razonSocialTercero", razonSocialTercero)) isValid = false;
        if(validarCampoPopup("telefonoTercero", telefonoTercero)) isValid = false;
        if(validarCampoPopup("calle", direccion.calle)) isValid = false;
        if(validarCampoPopup("numero", direccion.numero)) isValid = false;
        if(validarCampoPopup("codPostal", direccion.codPostal)) isValid = false;
        if(validarCampoPopup("localidad", direccion.localidad)) isValid = false;
        if(validarCampoPopup("provincia", direccion.provincia)) isValid = false;
        if(validarCampoPopup("pais", direccion.pais)) isValid = false;
        if(validarCampoPopup("piso", direccion.piso)) isValid = false;
        if(validarCampoPopup("departamento", direccion.departamento)) isValid = false;
        return isValid
    }

    // --- LÓGICA DE NEGOCIO ---

    const handleSearch = async (e?: React.FormEvent) => {
        if (e) e.preventDefault();
        setErrorMessage("")

        if (!numeroHabitacion.trim()) {
            setErrorMessage("Por favor, ingrese el número de habitación.");
            return
        }

        if (!horaSalida.trim()) {
            setErrorMessage("Por favor, ingrese la hora de salida.");
            return
        }

        setIsLoading(true)
        try {
            const res = await fetch('http://localhost:8080/api/factura/buscar-ocupantes', {
                method: 'POST', headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ numeroHabitacion, horaSalida })
            });
            if (!res.ok) throw new Error("No se encontró ocupación.");
            const data = await res.json();
            setIdEstadia(data.idEstadia); setOcupantes(data.ocupantes); setStep("select-person");
        } catch (error: any) { setErrorMessage(error.message); } finally { setIsLoading(false); }
    }

    const handleSelectPerson = async (ocupante: Ocupante) => {
        setErrorMessage(""); setSelectedOcupante(ocupante);
        await obtenerDetalleFacturacion({ esTercero: false, tipoDoc: ocupante.tipoDocumento, nroDoc: ocupante.nroDocumento });
    }

    const obtenerDetalleFacturacion = async (params: any) => {
        setIsLoading(true)
        try {
            const query = new URLSearchParams({ idEstadia: idEstadia!.toString(), horaSalida: horaSalida, esTercero: params.esTercero.toString() });
            if (params.idResponsableJuridico) query.append("idResponsableJuridico", params.idResponsableJuridico.toString());
            if (!params.esTercero && params.tipoDoc) { query.append("tipoDoc", params.tipoDoc); query.append("nroDoc", params.nroDoc); }

            const res = await fetch(`http://localhost:8080/api/factura/calcular-detalle?${query.toString()}`);
            if (!res.ok) {
                const errorText = await res.text();
                let mensajeError = "Error al calcular detalle";

                try {
                    const errorJson = JSON.parse(errorText);
                    mensajeError = errorJson.message || errorJson.error || errorText;
                } catch {
                    if (errorText.length > 0) mensajeError = errorText;
                }

                throw new Error(mensajeError);
            }

            const data: DetalleFacturacion = await res.json();
            setDetalleCalculado(data);
            setIdResponsableSeleccionado(data.idResponsable);

            // 1. Alojamiento
            const itemsMapeados: ItemFactura[] = [
                {
                    id: "estadia",
                    descripcion: "Alojamiento Base",
                    monto: Number(data.montoEstadiaBase) || 0,
                    selected: true
                }
            ];

            // 2. Recargo
            const recargo = Number(data.recargoHorario);
            if (recargo > 0) {
                itemsMapeados.push({
                    id: "recargo",
                    descripcion: data.detalleRecargo || "Recargo (Late Check-out)",
                    monto: recargo,
                    selected: true
                });
            }

            // 3. Servicios Adicionales
            if (Array.isArray(data.serviciosAdicionales)) {
                data.serviciosAdicionales.forEach((serv: any, idx) => {
                    const valor = Number(serv?.valor) || 0;
                    let textoDescripcion = "Servicio Adicional";
                    if (serv) {
                        const posibleTexto = serv.descripcion || serv.description || serv.detalle || serv.nombre;
                        if (posibleTexto && typeof posibleTexto === 'string' && posibleTexto.trim().length > 0) {
                            textoDescripcion = posibleTexto.trim();
                        }
                    }
                    itemsMapeados.push({
                        id: `serv-${idx}`,
                        descripcion: textoDescripcion,
                        monto: valor,
                        selected: true
                    });
                });
            }

            setItems(itemsMapeados);
            setStep("select-items");
        } catch (error: any) {
            setErrorMessage(error.message);
        } finally {
            setIsLoading(false);
        }
    }

    const handleCrearEmpresa = async () => {
        setErrorMessage("");
        if (!validateAllPopup()) {
            return;
        }
        setIsLoading(true);

        const pisoFinal = direccion.piso.trim() === "" ? null : parseInt(direccion.piso);
        const deptoFinal = direccion.departamento.trim() === "" ? null : direccion.departamento;

        try {
            const res = await fetch('http://localhost:8080/api/factura/responsable', {
                method: 'POST', headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    tipoResponsable: "J", cuit: cuitTercero, razonSocial: razonSocialTercero, telefono: [parseInt(telefonoTercero)],
                    dtoDireccion: { ...direccion, numero: parseInt(direccion.numero), codPostal: parseInt(direccion.codPostal), piso: pisoFinal, departamento: deptoFinal }
                })
            });
            if (!res.ok) throw new Error("Error al crear empresa");
            const dataResp = await res.json();

            setCuitTercero("");          // Limpia el CUIT
            setRazonSocialTercero("");   // Limpia Razón Social
            setTelefonoTercero("");      // Limpia Teléfono
            setDireccion({               // Limpia Dirección
                calle: "", numero: "", piso: "", departamento: "",
                codPostal: "", localidad: "", provincia: "", pais: "Argentina"
            });

            // Éxito: Calculamos detalle con el nuevo ID
            obtenerDetalleFacturacion({ esTercero: true, idResponsableJuridico: dataResp.idResponsableGenerado });

        } catch (error: any) { setErrorMessage("Error: " + error.message); setIsLoading(false); }
    }

    // Funciones de manejo de inputs
    const handleDireccionChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target; setDireccion({ ...direccion, [name]: value });
        if(popupErrors[name]) { const newErrs = {...popupErrors}; delete newErrs[name]; setPopupErrors(newErrs); }
    }
    const handleDireccionBlur = (e: React.FocusEvent<HTMLInputElement>) => {
        validarCampoPopup(e.target.name, e.target.value);
    }

    const handleTopLevelChange = (setter: any, field: string) => (e: React.ChangeEvent<HTMLInputElement>) => {
        setter(e.target.value);
        if(popupErrors[field]) { const newErrs = {...popupErrors}; delete newErrs[field]; setPopupErrors(newErrs); }
    }

    // --- LÓGICA DE BÚSQUEDA CUIT vs ALTA (MODIFICADA) ---
    const handleBuscarCuitOAlta = async () => {
        // 1. Caso VACÍO: Ir a Alta de Empresa
        if (!cuitTercero.trim()) {
            setErrorMessage("");
            setPopupErrors({});
            setStep("create-company"); // Vamos directo al formulario de alta
            return;
        }

        // 2. Caso FORMATO INVÁLIDO: Mostrar error y quedarse aquí
        if (!regexCuit.test(cuitTercero)) {
            setErrorMessage("Formato de CUIT inválido. Debe ser xx-xxxxxxxx-x");
            return;
        }

        setIsLoading(true);
        setErrorMessage(""); // Limpiamos errores previos

        try {
            const query = new URLSearchParams({ idEstadia: idEstadia!.toString(), horaSalida: horaSalida, esTercero: "true", cuit: cuitTercero });
            const res = await fetch(`http://localhost:8080/api/factura/calcular-detalle?${query.toString()}`);

            // 3. Caso NO ENCONTRADO: Mostrar error y quedarse aquí (usuario decide)
            if (res.status === 409) {
                setErrorMessage("El CUIT no pertenece a ningún Responsable de Pago registrado en en sistema.");
                setIsLoading(false);
                return;
            }

            if(res.ok) {
                const data = await res.json();
                setDetalleCalculado(data);
                setIdResponsableSeleccionado(data.idResponsable);

                const itemsMapeados: ItemFactura[] = [{ id: "estadia", descripcion: "Alojamiento Base", monto: data.montoEstadiaBase || 0, selected: true }];
                if (data.recargoHorario > 0) itemsMapeados.push({ id: "recargo", descripcion: data.detalleRecargo || "Recargo", monto: data.recargoHorario || 0, selected: true });
                if (data.serviciosAdicionales) data.serviciosAdicionales.forEach((serv: any, idx: number) => itemsMapeados.push({ id: `serv-${idx}`, descripcion: serv.descripcion, monto: serv.valor, selected: true }));

                setItems(itemsMapeados);
                setStep("select-items");
            }
        } catch(e: any) {
            console.error(e);
            setErrorMessage(e.message || "Error de conexión.");
        } finally {
            setIsLoading(false);
        }
    }

    // --- FUNCIONES FACTURACIÓN ---
    const handleToggleItem = (itemId: string) => {
        setItems(items.map((item) => (item.id === itemId ? { ...item, selected: !item.selected } : item)))
    }

    const calculateTotalLocal = () => {
        return items
            .filter(i => i.selected)
            .reduce((acc, curr) => {
                const montoSeguro = Number.isFinite(curr.monto) ? curr.monto : 0;
                return acc + montoSeguro;
            }, 0);
    }

    const handleGenerateInvoice = async () => {
        setIsLoading(true);
        try {
            const subtotal = calculateTotalLocal();
            const esA = detalleCalculado?.tipoFactura === "A";
            const iva = esA ? subtotal * 0.21 : 0;
            const total = subtotal + iva;

            const facturaFinal = {
                numeroFactura: "Generando...",
                fechaEmision: new Date().toISOString(),
                fechaVencimiento: new Date().toISOString(),
                importeTotal: total,
                importeNeto: subtotal,
                iva: iva,
                tipoFactura: detalleCalculado?.tipoFactura,
                idEstadia: { idEstadia: idEstadia },
                idResponsable: { idResponsable: idResponsableSeleccionado }
            };

            const res = await fetch('http://localhost:8080/api/factura/generar', {
                method: 'POST', headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(facturaFinal)
            });

            if(!res.ok) throw new Error("Error al generar factura");
            setStep("success");
        } catch (error: any) { setErrorMessage(error.message); } finally { setIsLoading(false); }
    }

    const resetForm = () => { window.location.reload(); }

    // --- RENDERIZADO ---

    return (
        <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50 dark:from-slate-950 dark:via-slate-900 dark:to-slate-950">
            <div className="mx-auto max-w-5xl px-4 py-8 sm:px-6 lg:px-8">

                {/* HEADER */}
                <div className="mb-8 space-y-2">
                    <div className="flex items-center justify-between">
                        <div className="flex items-center gap-3">
                            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-emerald-600 text-white shadow-md">
                                <Receipt className="h-6 w-6" />
                            </div>
                            <div>
                                <p className="text-xs font-semibold uppercase tracking-wider text-emerald-600 dark:text-emerald-400">Caso de Uso 07</p>
                                <h1 className="text-3xl font-bold text-slate-900 dark:text-slate-50">Registrar Factura</h1>
                            </div>
                        </div>
                        <Button variant="outline" className="bg-white/80 backdrop-blur-sm gap-2" asChild>
                            <Link href="/"><Home className="h-4 w-4" /> Volver al Menú Principal</Link>
                        </Button>
                    </div>
                </div>

                {errorMessage && (
                    <Alert variant="destructive" className="mb-6 shadow-md border-red-200 bg-red-50 text-red-800">
                        <AlertCircle className="h-4 w-4" />
                        <AlertDescription>{errorMessage}</AlertDescription>
                    </Alert>
                )}

                {/* PASO 1 y 2 (BUSQUEDA y SELECCION) */}
                {step === "search" && (
                    <Card className="shadow-lg">
                        <CardHeader className="border-b bg-slate-50/50 pb-4"><CardTitle>Datos de la Estadía</CardTitle></CardHeader>
                        <CardContent className="pt-6">
                            <form onSubmit={handleSearch} className="space-y-6">
                                <div className="grid md:grid-cols-2 gap-6">
                                    <div className="space-y-2"><Label>Nro Habitación</Label><Input
                                    value={numeroHabitacion}
                                    onChange={(e) => {
                                        const val = e.target.value;
                                        // Solo actualizamos si son números y longitud <= 3
                                        if (/^\d*$/.test(val) && val.length <= 3) {
                                             setNumeroHabitacion(val);
                                             setErrorMessage(""); // Limpiamos el error al escribir
                                        }
                                    }}
                                    placeholder="Ej: 101"
                                    className="bg-white"
                                    maxLength={3} // Refuerzo HTML estándar
                                    />
                                    </div>
                                    <div className="space-y-2"><Label>Hora Salida</Label><Input type="time" value={horaSalida} onChange={e=>setHoraSalida(e.target.value)} className="bg-white"/></div>
                                </div>
                                <Button onClick={()=>handleSearch()} type="submit" className="w-full bg-emerald-600 hover:bg-emerald-700" disabled={isLoading}>{isLoading ? "Buscando..." : "Buscar Ocupantes"}</Button>
                            </form>
                        </CardContent>
                    </Card>
                )}

                {step === "select-person" && (
                    <Card className="shadow-lg animate-in fade-in">
                        <CardHeader className="border-b"><CardTitle>Seleccionar Responsable</CardTitle></CardHeader>
                        <CardContent className="pt-6 space-y-6">
                            <div className="grid md:grid-cols-2 gap-4">
                                {ocupantes.map((ocup, idx) => (
                                    <div key={idx} className="flex cursor-pointer flex-col gap-2 rounded-lg border bg-white p-4 shadow-sm hover:border-emerald-500" onClick={() => handleSelectPerson(ocup)}>
                                        <div className="flex items-center gap-3">
                                            <div className="bg-slate-100 p-2 rounded-full"><User className="h-5 w-5"/></div>
                                            <div><p className="font-semibold">{ocup.apellido}, {ocup.nombres}</p><p className="text-sm text-slate-500">{ocup.tipoDocumento} {ocup.nroDocumento}</p></div>
                                        </div>
                                    </div>
                                ))}
                            </div>
                            <div className="relative"><div className="absolute inset-0 flex items-center"><span className="w-full border-t"/></div><div className="relative flex justify-center text-xs uppercase"><span className="bg-white px-2 text-muted-foreground">O bien</span></div></div>

                            {/* CAJA DE BÚSQUEDA RÁPIDA DE EMPRESA */}
                            <div className="bg-slate-50 p-4 rounded-lg border border-slate-200">
                                <Label className="mb-2 block">Facturar a Empresa (CUIT)</Label>
                                <div className="flex gap-2">
                                    {/* CORRECCIÓN: Limpiar error al escribir y placeholder actualizado */}
                                    <Input
                                        value={cuitTercero}
                                        onChange={(e) => {
                                            handleTopLevelChange(setCuitTercero, "cuitTercero")(e);
                                            setErrorMessage(""); // Limpia la alerta global al editar
                                        }}
                                        placeholder="Ej: XX-XXXXXXXX-X (Dejar vacío para Dar de Alta Responsable de Pago)"
                                        className={`bg-white ${errorMessage ? "border-red-500" : ""}`}
                                    />
                                    <Button onClick={handleBuscarCuitOAlta} disabled={isLoading}>Buscar / Alta</Button>

                                    {/* CORRECCIÓN: Botón + eliminado */}
                                </div>
                            </div>

                            <Button variant="outline" onClick={() => setStep("search")} className="w-full">Volver</Button>
                        </CardContent>
                    </Card>
                )}

                {/* --- NUEVO "PASO": PANTALLA DE ALTA --- */}
                {step === "create-company" && (
                    <Card className="shadow-xl border-emerald-100 animate-in slide-in-from-right-4">
                        <CardHeader className="bg-emerald-50/50 border-b border-emerald-100">
                            <div className="flex items-center gap-2 text-emerald-800">
                                <Building2 className="h-6 w-6" />
                                <div>
                                    <CardTitle>Alta de Nueva Empresa</CardTitle>
                                    <CardDescription>Complete los datos fiscales para continuar con la facturación.</CardDescription>
                                </div>
                            </div>
                        </CardHeader>
                        <CardContent className="pt-6 space-y-6">

                            <div className="flex justify-end"><p className="text-sm text-slate-500">(*) Campos obligatorios</p></div>

                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                <div className="space-y-2">
                                    <Label className={popupErrors.cuitTercero ? "text-red-500" : ""}>CUIT <span className="text-black">*</span></Label>
                                    <Input value={cuitTercero} onChange={handleTopLevelChange(setCuitTercero, "cuitTercero")} onBlur={(e) => validarCampoPopup("cuitTercero", e.target.value)} placeholder="XX-XXXXXXXX-X" className={popupErrors.cuitTercero ? "border-red-500" : ""}/>
                                    {popupErrors.cuitTercero && <p className="text-xs text-red-500">{popupErrors.cuitTercero}</p>}
                                </div>
                                <div className="space-y-2">
                                    <Label className={popupErrors.razonSocialTercero ? "text-red-500" : ""}>Razón Social <span className="text-black">*</span></Label>
                                    <Input value={razonSocialTercero} onChange={handleTopLevelChange(setRazonSocialTercero, "razonSocialTercero")} onBlur={(e) => validarCampoPopup("razonSocialTercero", e.target.value)} placeholder="Ej: Tech Solutions S.A." className={popupErrors.razonSocialTercero ? "border-red-500" : ""}/>
                                    {popupErrors.razonSocialTercero && <p className="text-xs text-red-500">{popupErrors.razonSocialTercero}</p>}
                                </div>
                                <div className="space-y-2">
                                    <Label className={popupErrors.telefonoTercero ? "text-red-500" : ""}>Teléfono <span className="text-black">*</span></Label>
                                    <Input value={telefonoTercero} onChange={handleTopLevelChange(setTelefonoTercero, "telefonoTercero")} onBlur={(e) => validarCampoPopup("telefonoTercero", e.target.value)} placeholder="Ej: +543424654987" className={popupErrors.telefonoTercero ? "border-red-500" : ""}/>
                                    {popupErrors.telefonoTercero && <p className="text-xs text-red-500">{popupErrors.telefonoTercero}</p>}
                                </div>
                            </div>

                            <div className="border-t pt-4">
                                <h3 className="flex items-center gap-2 font-medium text-emerald-800 mb-4"><MapPin className="h-4 w-4"/> Domicilio Fiscal</h3>
                                <div className="grid grid-cols-6 gap-4">
                                    <div className="col-span-4 space-y-2">
                                        <Label className={popupErrors.calle ? "text-red-500" : ""}>Calle <span className="text-black">*</span></Label>
                                        <Input name="calle" value={direccion.calle} onChange={handleDireccionChange} onBlur={handleDireccionBlur} placeholder="Ej: Av. San Martín" className={popupErrors.calle ? "border-red-500" : ""}/>
                                        {popupErrors.calle && <p className="text-xs text-red-500">{popupErrors.calle}</p>}
                                    </div>
                                    <div className="col-span-2 space-y-2">
                                        <Label className={popupErrors.numero ? "text-red-500" : ""}>Número <span className="text-black">*</span></Label>
                                        <Input name="numero" value={direccion.numero} onChange={handleDireccionChange} onBlur={handleDireccionBlur} placeholder="Ej: 1234" className={popupErrors.numero ? "border-red-500" : ""}/>
                                        {popupErrors.numero && <p className="text-xs text-red-500">{popupErrors.numero}</p>}
                                    </div>
                                    <div className="col-span-2 space-y-2">
                                        <Label className={popupErrors.piso ? "text-red-500" : ""}>Piso</Label>
                                        <Input name="piso" value={direccion.piso} onChange={handleDireccionChange} onBlur={handleDireccionBlur} placeholder="Ej: 5" className={popupErrors.piso ? "border-red-500" : ""}/>
                                        {popupErrors.piso && <p className="text-xs text-red-500">{popupErrors.piso}</p>}
                                    </div>
                                    <div className="col-span-2 space-y-2">
                                        <Label className={popupErrors.departamento ? "text-red-500" : ""}>Depto</Label>
                                        <Input name="departamento" value={direccion.departamento} onChange={handleDireccionChange} onBlur={handleDireccionBlur} placeholder="Ej: A" className={popupErrors.departamento ? "border-red-500" : ""}/>
                                        {popupErrors.departamento && <p className="text-xs text-red-500">{popupErrors.departamento}</p>}
                                    </div>
                                    <div className="col-span-2 space-y-2">
                                        <Label className={popupErrors.codPostal ? "text-red-500" : ""}>CP <span className="text-black">*</span></Label>
                                        <Input name="codPostal" value={direccion.codPostal} onChange={handleDireccionChange} onBlur={handleDireccionBlur} placeholder="Ej: 3000" className={popupErrors.codPostal ? "border-red-500" : ""}/>
                                        {popupErrors.codPostal && <p className="text-xs text-red-500">{popupErrors.codPostal}</p>}
                                    </div>
                                    <div className="col-span-3 space-y-2">
                                        <Label className={popupErrors.localidad ? "text-red-500" : ""}>Localidad <span className="text-black">*</span></Label>
                                        <Input name="localidad" value={direccion.localidad} onChange={handleDireccionChange} onBlur={handleDireccionBlur} placeholder="Ej: Santa Fe" className={popupErrors.localidad ? "border-red-500" : ""}/>
                                        {popupErrors.localidad && <p className="text-xs text-red-500">{popupErrors.localidad}</p>}
                                    </div>
                                    <div className="col-span-3 space-y-2">
                                        <Label className={popupErrors.provincia ? "text-red-500" : ""}>Provincia <span className="text-black">*</span></Label>
                                        <Input name="provincia" value={direccion.provincia} onChange={handleDireccionChange} onBlur={handleDireccionBlur} placeholder="Ej: Santa Fe" className={popupErrors.provincia ? "border-red-500" : ""}/>
                                        {popupErrors.provincia && <p className="text-xs text-red-500">{popupErrors.provincia}</p>}
                                    </div>
                                    <div className="col-span-6 space-y-2">
                                        <Label className={popupErrors.pais ? "text-red-500" : ""}>País <span className="text-black">*</span></Label>
                                        <Input name="pais" value={direccion.pais} onChange={handleDireccionChange} onBlur={handleDireccionBlur} placeholder="Ej: Argentina" className={popupErrors.pais ? "border-red-500" : ""}/>
                                        {popupErrors.pais && <p className="text-xs text-red-500">{popupErrors.pais}</p>}
                                    </div>
                                </div>
                            </div>


                            <div className="flex gap-4 pt-4">
                                <Button variant="ghost" onClick={() => {
                                    // 1. Limpiamos todos los campos del formulario
                                    setCuitTercero("");
                                    setRazonSocialTercero("");
                                    setTelefonoTercero("");
                                    setDireccion({
                                        calle: "", numero: "", piso: "", departamento: "",
                                        codPostal: "", localidad: "", provincia: "", pais: "Argentina"
                                    });
                                    // 2. Limpiamos errores
                                    setPopupErrors({});
                                    setErrorMessage("");
                                    // 3. Volvemos a la pantalla de selección
                                    setStep("select-person");
                                }} className="text-slate-500">Cancelar Alta</Button>
                                <Button onClick={handleCrearEmpresa} disabled={isLoading} className="flex-1 bg-emerald-600 hover:bg-emerald-700 text-white">
                                    {isLoading ? <Loader2 className="mr-2 h-4 w-4 animate-spin"/> : <><CheckCircle2 className="mr-2 h-4 w-4"/> Guardar Empresa y Continuar</>}
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
                                {items.length === 0 ? (
                                    <p className="text-sm text-slate-500 italic">No hay ítems para facturar.</p>
                                ) : (
                                    items.map((item) => (
                                        <div
                                            key={item.id}
                                            className={`flex items-center justify-between border p-4 rounded-lg transition-colors ${item.selected ? "bg-white border-emerald-200 shadow-sm" : "bg-slate-50 border-transparent opacity-60"}`}
                                        >
                                            <div className="flex items-center gap-3">
                                                <Checkbox
                                                    checked={item.selected}
                                                    onCheckedChange={() => handleToggleItem(item.id)}
                                                    className="data-[state=checked]:bg-emerald-600 data-[state=checked]:border-emerald-600"
                                                />
                                                <span className="font-medium text-slate-700">{item.descripcion}</span>
                                            </div>
                                            <span className="font-mono font-bold text-slate-900">
                                                ${(item.monto || 0).toLocaleString('es-AR')}
                                            </span>
                                        </div>
                                    ))
                                )}
                            </div>

                            {/* Resumen de Totales */}
                            <div className="bg-slate-50 p-6 rounded-lg border border-slate-100 space-y-2">
                                <div className="flex justify-between text-sm text-slate-600">
                                    <span>Subtotal</span>
                                    <span>${calculateTotalLocal().toLocaleString('es-AR')}</span>
                                </div>
                                {detalleCalculado.tipoFactura === "A" && (
                                    <div className="flex justify-between text-sm text-slate-600">
                                        <span>IVA (21%)</span>
                                        <span>${(calculateTotalLocal() * 0.21).toLocaleString('es-AR')}</span>
                                    </div>
                                )}
                                <div className="border-t border-slate-200 my-2 pt-2 flex justify-between items-center">
                                    <span className="font-bold text-lg text-slate-800">Total a Pagar</span>
                                    <span className="font-bold text-2xl text-emerald-600">
                                        ${(detalleCalculado.tipoFactura === "A" ? calculateTotalLocal() * 1.21 : calculateTotalLocal()).toLocaleString('es-AR')}
                                    </span>
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
            </div>
        </div>
    )
}