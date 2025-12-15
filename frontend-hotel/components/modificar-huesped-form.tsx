"use client"

import { useState, useEffect, useRef } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog"
import { Alert, AlertDescription } from "@/components/ui/alert"
import {
    Edit, // Icono para Modificar
    AlertCircle,
    CheckCircle2,
    UserMinus, // Icono para Borrar
    ArrowRight,
    Home
} from "lucide-react"

// Importamos la lógica de API y Contexto
import { useGuest } from "@/lib/guest-context"
import { modificarHuesped, verificarExistenciaHuesped, darDeBajaHuesped } from "@/lib/api"

// --- TIPOS ---
interface DatosFormulario {
    nombres: string
    apellido: string
    tipoDocumento: string
    nroDocumento: string
    cuit: string
    posicionIva: string
    fechaNacimiento: string
    nacionalidad: string
    email: string
    telefono: string
    ocupacion: string // Agregado porque CU10 lo usa
    calle: string
    numero: string
    departamento: string
    piso: string
    codPostal: string
    localidad: string
    provincia: string
    pais: string
}

interface Errores {
    [key: string]: string | undefined
}

export function ModificarHuespedForm() {
    const router = useRouter()
    const { selectedGuest, setSelectedGuest } = useGuest()
    const isExiting = useRef(false)

    // --- ESTADOS ---
    const [datos, setDatos] = useState<DatosFormulario>({
        nombres: "",
        apellido: "",
        tipoDocumento: "",
        nroDocumento: "",
        cuit: "",
        posicionIva: "",
        fechaNacimiento: "",
        nacionalidad: "",
        email: "",
        telefono: "",
        ocupacion: "",
        calle: "",
        numero: "",
        departamento: "",
        piso: "",
        codPostal: "",
        localidad: "",
        provincia: "",
        pais: "",
    })

    // Guardamos la data original para detectar cambios de identidad (PK)
    const [originalData, setOriginalData] = useState<DatosFormulario | null>(null)

    const [errores, setErrores] = useState<Errores>({})
    const [isSaving, setIsSaving] = useState(false)
    const [alert, setAlert] = useState<{ type: "success" | "error"; message: string } | null>(null)

    // Estados de Dialogs (Lógica CU10)
    const [showConfirmDialog, setShowConfirmDialog] = useState(false)
    const [showCancelDialog, setShowCancelDialog] = useState(false)
    const [showDeleteDialog, setShowDeleteDialog] = useState(false)
    const [showFusionDialog, setShowFusionDialog] = useState(false)

    // --- CARGA DE DATOS (DEL CONTEXTO) ---
    useEffect(() => {
        if (isExiting.current) return

        if (!selectedGuest) {
            router.push('/buscar-huesped')
            return
        }

        // 1. IVA
        const normalizarIVA = (valor: string | undefined) => {
            if (!valor) return "CONSUMIDOR_FINAL";
            const valoresValidos = ["CONSUMIDOR_FINAL", "RESPONSABLE_INSCRIPTO", "MONOTRIBUTISTA", "EXENTO"];
            if (valoresValidos.includes(valor)) return valor;

            const mapa: Record<string, string> = {
                "Consumidor Final": "CONSUMIDOR_FINAL",
                "Responsable Inscripto": "RESPONSABLE_INSCRIPTO",
                "Monotributo": "MONOTRIBUTISTA",
                "Monotributista": "MONOTRIBUTISTA",
                "Exento": "EXENTO"
            };
            return mapa[valor] || "CONSUMIDOR_FINAL";
        }

        // Esta función detecta si el dato es una lista (ej: ["juan@mail.com"]) o un texto (ej: "juan@mail.com")
        const extraerValor = (valor: any) => {
            if (!valor) return "";
            if (Array.isArray(valor)) {
                return valor.length > 0 ? String(valor[0]) : "";
            }
            return String(valor);
        }

        // Mapeo del objeto
        const mappedGuest: DatosFormulario = {
            apellido: selectedGuest.apellido || '',
            nombres: selectedGuest.nombres || '',
            tipoDocumento: selectedGuest.tipoDocumento || '',
            nroDocumento: selectedGuest.numeroDocumento || '',
            cuit: selectedGuest.cuit || '',
            posicionIva: normalizarIVA(selectedGuest.posicionIVA),
            fechaNacimiento: selectedGuest.fechaNacimiento || '',
            nacionalidad: selectedGuest.nacionalidad || '',
            email: extraerValor(selectedGuest.email),
            telefono: extraerValor(selectedGuest.telefono),
            ocupacion: extraerValor(selectedGuest.ocupacion),
            calle: selectedGuest.direccionCalle || '',
            numero: selectedGuest.direccionNumero?.toString() || '',
            departamento: selectedGuest.direccionDepartamento || '',
            piso: selectedGuest.direccionPiso || '',
            codPostal: selectedGuest.direccionCodigoPostal?.toString() || '',
            localidad: selectedGuest.direccionLocalidad || '',
            provincia: selectedGuest.direccionProvincia || '',
            pais: selectedGuest.direccionPais || '',
        }

        setDatos(mappedGuest)
        setOriginalData(mappedGuest)
    }, [selectedGuest, router])


    // --- REGEX Y VALIDACIONES (Idéntico a CU09) ---
    const regexNombre = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/
    const regexCuit = /^\d{2}-?\d{8}-?\d{1}$/
    const regexTelefono = /^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\s./0-9]*$/
    const regexEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    const regexCalle = /^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\s.,]+$/
    const regexTexto = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/

    const MSJ_OBLIGATORIO = "Este campo es obligatorio"
    const MSJ_TEXTO = "Solo se permiten letras y espacios"
    const MSJ_FORMATO_EMAIL = "Formato inválido (ej: usuario@dominio.com)"
    const MSJ_FORMATO_TEL = "Formato inválido (ej: +54 342 1234567)"
    const MSJ_LARGO_CORTO = "El texto ingresado es demasiado corto"
    const MSJ_LARGO_EXCESIVO = "El texto supera el límite permitido"
    const MSJ_NUMERICO = "Solo se permiten números válidos"

    const CONFIG_DOCUMENTOS: Record<string, { regex: RegExp; error: string; placeholder: string }> = {
        DNI: { regex: /^\d{7,8}$/, error: "El DNI debe tener 7 u 8 números", placeholder: "Ej: 12345678" },
        PASAPORTE: { regex: /^[A-Z0-9]{6,9}$/, error: "El pasaporte debe tener 6 a 9 caracteres alfanuméricos", placeholder: "Ej: A1234567" },
        LC: { regex: /^\d{6,8}$/, error: "La LC debe tener 6 a 8 números", placeholder: "Ej: 1234567" },
        LE: { regex: /^\d{6,8}$/, error: "La LE debe tener 6 a 8 números", placeholder: "Ej: 1234567" },
        OTRO: { regex: /^[a-zA-Z0-9]{5,20}$/, error: "Formato inválido (5-20 caracteres)", placeholder: "Nro. de Identificación" }
    }
    const PREFIJOS_CUIT = ["20", "23", "24", "27", "30", "33", "34"]
    const limpiarCuit = (cuit: string) => cuit.replace(/\D/g, "")

    // Validación individual (onBlur)
    const validarCampo = (nombre: keyof DatosFormulario, valor: any) => {
        let error: string | undefined = undefined

        switch (nombre) {
            case "apellido":
            case "nombres":
                if (!valor.trim()) error = MSJ_OBLIGATORIO
                else if (valor.length < 2) error = MSJ_LARGO_CORTO
                else if (valor.length > 50) error = MSJ_LARGO_EXCESIVO
                else if (!regexNombre.test(valor)) error = MSJ_TEXTO
                break
            case "tipoDocumento":
                if (!valor) error = MSJ_OBLIGATORIO
                break
            case "nroDocumento":
                if (!valor.trim()) error = MSJ_OBLIGATORIO
                else if (datos.tipoDocumento) {
                    const config = CONFIG_DOCUMENTOS[datos.tipoDocumento]
                    if (config && !config.regex.test(valor)) error = config.error
                }
                break
            case "cuit":
                const esObligatorio = datos.posicionIva === "RESPONSABLE_INSCRIPTO"
                const cuitLimpio = limpiarCuit(valor)
                if (esObligatorio && !valor.trim()) error = "Requerido para Resp. Inscripto"
                else if (valor.trim()) {
                    if (!regexCuit.test(valor)) error = "Formato inválido"
                    else {
                        const prefijo = cuitLimpio.substring(0, 2)
                        if (!PREFIJOS_CUIT.includes(prefijo)) error = "Prefijo inválido"
                        else if (["DNI", "LC", "LE"].includes(datos.tipoDocumento) && datos.nroDocumento) {
                            const dniEnCuit = parseInt(cuitLimpio.substring(2, cuitLimpio.length - 1), 10)
                            const dniIngresado = parseInt(datos.nroDocumento, 10)
                            if (dniEnCuit !== dniIngresado) error = "El CUIT no coincide con el Documento"
                        }
                    }
                }
                break
            case "fechaNacimiento":
                if (!valor) error = MSJ_OBLIGATORIO
                else {
                    const fecha = new Date(valor)
                    if (fecha >= new Date()) error = "Debe ser anterior a hoy"
                    else if (fecha < new Date("1900-01-01")) error = "Fecha inválida"
                }
                break
            case "piso":
            case "departamento":
                if (valor.trim()) {
                    if (!regexCalle.test(valor)) error = "Caracteres inválidos"
                    else if (valor.length > 10) error = MSJ_LARGO_EXCESIVO
                }
                break
            case "nacionalidad":
            case "provincia":
            case "pais":
                if (!valor.trim()) error = MSJ_OBLIGATORIO
                else if (!regexTexto.test(valor)) error = MSJ_TEXTO
                break
            case "email":
                if (valor.trim() && !regexEmail.test(valor)) error = MSJ_FORMATO_EMAIL
                break
            case "telefono":
                if (!valor.trim()) error = MSJ_OBLIGATORIO
                else if (!regexTelefono.test(valor.trim())) error = MSJ_FORMATO_TEL
                break
            case "calle":
            case "localidad":
                if (!valor.trim()) error = MSJ_OBLIGATORIO
                else if (!regexCalle.test(valor)) error = "Caracteres inválidos"
                break
            case "numero":
                if (!valor.trim()) error = MSJ_OBLIGATORIO
                else if (isNaN(Number(valor)) || Number(valor) < 1 || Number(valor) > 99999) error = "Número inválido"
                break
            case "codPostal":
                if (!valor.trim()) error = MSJ_OBLIGATORIO
                // Validamos que sean solo números porque el Backend espera un Integer
                else if (!/^\d+$/.test(valor)) error = MSJ_NUMERICO
                //Validamos longitud máxima de 8 caracteres
                else if (valor.length > 8) error = "El código postal no debe superar los 8 dígitos"
                break
            case "ocupacion":
                if (!valor.trim()) error = MSJ_OBLIGATORIO
                else if (!regexTexto.test(valor)) error = MSJ_TEXTO
                break
        }
        setErrores((prev) => ({ ...prev, [nombre]: error }))
        return !error
    }

    const handleBlur = (e: React.FocusEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { id, value } = e.target
        validarCampo(id as keyof DatosFormulario, value)
    }

    // Función para limpiar y salir correctamente
    const handleVolverMenu = () => {
        // 1. Levantamos la bandera para que el useEffect no intente redirigirnos a "buscar-huesped"
        // cuando detecte que selectedGuest es null.
        isExiting.current = true;

        // 2. Limpiamos el huésped del contexto global
        setSelectedGuest(null);

        // 3. Ahora sí, nos vamos al menú
        router.push("/");
    }

    const validarFormularioCompleto = (): boolean => {
        const campos = Object.keys(datos) as (keyof DatosFormulario)[]
        let esValido = true
        campos.forEach(campo => {
            if (!validarCampo(campo, datos[campo])) esValido = false
        })
        if (!esValido) {
            setAlert({ type: "error", message: "Hay errores en el formulario. Por favor revíselos." })
        }
        return esValido
    }

    // --- LÓGICA DE NEGOCIO (EL CEREBRO DE CU10) ---

    const handleNext = async () => {
        setAlert(null)
        if (!validarFormularioCompleto()) return

        // Detectar si cambió la clave primaria (Tipo o Número)
        const cambioIdentidad =
            datos.tipoDocumento !== originalData?.tipoDocumento ||
            datos.nroDocumento !== originalData?.nroDocumento;

        if (cambioIdentidad) {
            try {
                const existe = await verificarExistenciaHuesped(datos.tipoDocumento, datos.nroDocumento);
                if (existe) {
                    setShowFusionDialog(true);
                    return;
                }
            } catch (error) {
                console.error("Error verificando existencia:", error);
            }
        }

        // Si no hay conflicto, confirmar guardado normal
        setShowConfirmDialog(true)
    }

    const handleFusionConfirm = () => {
        setShowFusionDialog(false)
        setShowConfirmDialog(true)
    }

    const confirmSave = async () => {
        setIsSaving(true)
        setShowConfirmDialog(false)

        try {
            // Mapeo inverso de Strings "Bonitos" a Enums de Backend (si hace falta)
            // Asumimos que el backend acepta "Responsable Inscripto" y lo convierte,
            // O podemos enviarlo en mayúsculas SnakeCase.
            const mapPosicionIVAToBackend = (posIva: string) => {
                // Si ya viene en formato ENUM (ej: CONSUMIDOR_FINAL), devolverlo tal cual.
                // Si viene "Consumidor Final", mapearlo.
                const mapping: Record<string, string> = {
                    'Consumidor Final': 'CONSUMIDOR_FINAL',
                    'Responsable Inscripto': 'RESPONSABLE_INSCRIPTO',
                    'Exento': 'EXENTO',
                    'Monotributo': 'MONOTRIBUTISTA',
                    'Monotributista': 'MONOTRIBUTISTA'
                }
                return mapping[posIva] || posIva
            }

            const dtoHuesped = {
                nombres: datos.nombres,
                apellido: datos.apellido,
                tipoDocumento: datos.tipoDocumento,
                nroDocumento: datos.nroDocumento,
                cuit: datos.cuit,
                posicionIva: mapPosicionIVAToBackend(datos.posicionIva),
                fechaNacimiento: datos.fechaNacimiento,
                nacionalidad: datos.nacionalidad,
                email: [datos.email],
                ocupacion: [datos.ocupacion],
                telefono: [parseInt(datos.telefono.replace(/\D/g, '')) || 0],
                dtoDireccion: {
                    calle: datos.calle,
                    numero: parseInt(datos.numero) || 0,
                    departamento: datos.departamento || null,
                    piso: datos.piso || null,
                    codPostal: parseInt(datos.codPostal) || 0,
                    localidad: datos.localidad,
                    provincia: datos.provincia,
                    pais: datos.pais
                }
            }

            await modificarHuesped(
                originalData?.tipoDocumento || datos.tipoDocumento,
                originalData?.nroDocumento || datos.nroDocumento,
                dtoHuesped
            )

            setAlert({ type: "success", message: "La operación ha culminado con éxito" })
            setOriginalData(datos)
            setIsSaving(false)

            setTimeout(() => {
                if (isExiting) isExiting.current = true;
                setSelectedGuest(null);
                router.push("/");
            }, 2000)

        } catch (error: any) {
            setIsSaving(false)
            setAlert({ type: "error", message: `Error al modificar el huésped: ${error.message || 'Error desconocido'}` })
        }
    }

    const confirmDelete = async () => {
        const target = originalData || datos;
        if (!target) return;

        try {
            const mensajeExito = await darDeBajaHuesped(target.tipoDocumento, target.nroDocumento);
            setAlert({ type: "success", message: mensajeExito || `El huésped ha sido eliminado correctamente.` });
            setShowDeleteDialog(false);

            setTimeout(() => {
                if (isExiting) isExiting.current = true;
                setSelectedGuest(null);
                router.push("/");
            }, 2000);

        } catch (error: any) {
            setAlert({ type: "error", message: `⛔ ${error.message}` });
            setShowDeleteDialog(false);
        }
    }

    if (!originalData) return <div className="p-8 text-center">Cargando datos del huésped...</div>

    return (
        <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50 dark:from-slate-950 dark:via-slate-900 dark:to-slate-950">
            <div className="mx-auto max-w-6xl px-4 py-8 sm:px-6 lg:px-8">

                {/* --- HEADER (Idéntico a CU09 pero con Modificar) --- */}
                <div className="mb-8 space-y-2">
                    <div className="flex items-center justify-between">
                        <div className="flex items-center gap-3">
                            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-violet-600 text-white">
                                <Edit className="h-5 w-5" /> {/* Icono Edit */}
                            </div>
                            <div>
                                <p className="text-xs font-semibold uppercase tracking-wider text-violet-600 dark:text-violet-400">
                                    Caso de Uso
                                </p>
                                <h1 className="text-3xl font-bold text-slate-900 dark:text-slate-50">Modificar Huésped</h1>
                            </div>
                        </div>
                        <Button variant="outline" className="bg-white dark:bg-slate-800 gap-2" onClick={handleVolverMenu}>
                            <Home className="mr-2 h-4 w-4" />
                            Volver al Menú Principal
                        </Button>
                    </div>
                    <p className="text-slate-600 dark:text-slate-400">Actualice los datos personales del huésped.</p>
                </div>

                <Card className="p-6 shadow-lg">
                    <div className="mb-4 flex justify-end">
                        <p className="text-sm text-slate-500 dark:text-slate-400">(*) Campos obligatorios</p>
                    </div>

                    {/* --- FORMULARIO (Idéntico Estilo CU09) --- */}
                    <div className="mb-8">
                        <h2 className="mb-6 text-2xl font-semibold text-slate-900 dark:text-slate-50">Datos Personales</h2>
                        <div className="grid grid-cols-1 gap-6 md:grid-cols-2">

                            <div className="space-y-2">
                                <Label htmlFor="apellido">Apellido *</Label>
                                <Input id="apellido" value={datos.apellido} onChange={(e) => setDatos({ ...datos, apellido: e.target.value })} onBlur={handleBlur} placeholder="Ej: González" className={errores.apellido ? "border-destructive" : ""} />
                                {errores.apellido && <p className="text-xs text-destructive">{errores.apellido}</p>}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="nombres">Nombres *</Label>
                                <Input id="nombres" value={datos.nombres} onChange={(e) => setDatos({ ...datos, nombres: e.target.value })} onBlur={handleBlur} placeholder="Ej: Juan Carlos" className={errores.nombres ? "border-destructive" : ""} />
                                {errores.nombres && <p className="text-xs text-destructive">{errores.nombres}</p>}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="tipoDocumento">Tipo de Documento *</Label>
                                <Select value={datos.tipoDocumento} onValueChange={(val) => { setDatos({ ...datos, tipoDocumento: val, nroDocumento: "" }); setErrores({ ...errores, tipoDocumento: undefined }) }}>
                                    <SelectTrigger className={errores.tipoDocumento ? "border-destructive" : ""}><SelectValue placeholder="Seleccione..." /></SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="DNI">DNI</SelectItem>
                                        <SelectItem value="PASAPORTE">Pasaporte</SelectItem>
                                        <SelectItem value="LC">LC</SelectItem>
                                        <SelectItem value="LE">LE</SelectItem>
                                        <SelectItem value="OTRO">Otro</SelectItem>
                                    </SelectContent>
                                </Select>
                                {errores.tipoDocumento && <p className="text-xs text-destructive">{errores.tipoDocumento}</p>}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="nroDocumento">Nro. Documento *</Label>
                                <Input
                                    id="nroDocumento"
                                    value={datos.nroDocumento}
                                    onChange={(e) => setDatos({ ...datos, nroDocumento: e.target.value })}
                                    onBlur={(e) => { handleBlur(e); if (datos.cuit) setTimeout(() => validarCampo("cuit", datos.cuit), 0) }}
                                    placeholder={datos.tipoDocumento ? CONFIG_DOCUMENTOS[datos.tipoDocumento]?.placeholder : "Seleccione tipo primero"}
                                    disabled={!datos.tipoDocumento}
                                    className={errores.nroDocumento ? "border-destructive" : ""}
                                />
                                {errores.nroDocumento && <p className="text-xs text-destructive">{errores.nroDocumento}</p>}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="fechaNacimiento">Fecha de Nacimiento *</Label>
                                <Input id="fechaNacimiento" type="date" value={datos.fechaNacimiento} max={new Date().toISOString().split("T")[0]} min="1900-01-01" onChange={(e) => setDatos({ ...datos, fechaNacimiento: e.target.value })} onBlur={handleBlur} className={`${errores.fechaNacimiento ? "border-destructive" : ""} [color-scheme:light]`} />
                                {errores.fechaNacimiento && <p className="text-xs text-destructive">{errores.fechaNacimiento}</p>}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="nacionalidad">Nacionalidad *</Label>
                                <Input id="nacionalidad" value={datos.nacionalidad} onChange={(e) => setDatos({ ...datos, nacionalidad: e.target.value })} onBlur={handleBlur} placeholder="Ej: Argentina" className={errores.nacionalidad ? "border-destructive" : ""} />
                                {errores.nacionalidad && <p className="text-xs text-destructive">{errores.nacionalidad}</p>}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="posicionIva">Posición frente al IVA *</Label>
                                <Select value={datos.posicionIva} onValueChange={(val) => setDatos({ ...datos, posicionIva: val })}>
                                    <SelectTrigger><SelectValue /></SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="CONSUMIDOR_FINAL">Consumidor Final</SelectItem>
                                        <SelectItem value="RESPONSABLE_INSCRIPTO">Responsable Inscripto</SelectItem>
                                        <SelectItem value="MONOTRIBUTISTA">Monotributista</SelectItem>
                                        <SelectItem value="EXENTO">Exento</SelectItem>
                                    </SelectContent>
                                </Select>
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="cuit">CUIT {datos.posicionIva === "RESPONSABLE_INSCRIPTO" && "*"}</Label>
                                <Input id="cuit" value={datos.cuit} onChange={(e) => setDatos({ ...datos, cuit: e.target.value })} onBlur={handleBlur} placeholder="XX-XXXXXXXX-X" className={errores.cuit ? "border-destructive" : ""} />
                                {errores.cuit && <p className="text-xs text-destructive">{errores.cuit}</p>}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="email">Email</Label>
                                <Input id="email" type="email" value={datos.email} onChange={(e) => setDatos({ ...datos, email: e.target.value })} onBlur={handleBlur} placeholder="ejemplo@email.com" className={errores.email ? "border-destructive" : ""} />
                                {errores.email && <p className="text-xs text-destructive">{errores.email}</p>}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="telefono">Teléfono *</Label>
                                <Input id="telefono" type="tel" value={datos.telefono} onChange={(e) => setDatos({ ...datos, telefono: e.target.value })} onBlur={handleBlur} placeholder="Ej: +54 11 1234-5678" className={errores.telefono ? "border-destructive" : ""} />
                                {errores.telefono && <p className="text-xs text-destructive">{errores.telefono}</p>}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="ocupacion">Ocupación *</Label>
                                <Input id="ocupacion" value={datos.ocupacion} onChange={(e) => setDatos({ ...datos, ocupacion: e.target.value })} onBlur={handleBlur} placeholder="Ej: Empleado" className={errores.ocupacion ? "border-destructive" : ""} />
                                {errores.ocupacion && <p className="text-xs text-destructive">{errores.ocupacion}</p>}
                            </div>
                        </div>
                    </div>

                    <div className="mb-8">
                        <h2 className="mb-6 text-2xl font-semibold text-slate-900 dark:text-slate-50">Dirección</h2>
                        <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
                            <div className="space-y-2">
                                <Label htmlFor="calle">Calle *</Label>
                                <Input id="calle" value={datos.calle} onChange={(e) => setDatos({ ...datos, calle: e.target.value })} onBlur={handleBlur} placeholder="Ej: Av. Corrientes" className={errores.calle ? "border-destructive" : ""} />
                                {errores.calle && <p className="text-xs text-destructive">{errores.calle}</p>}
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="numero">Número *</Label>
                                <Input id="numero" value={datos.numero} onChange={(e) => setDatos({ ...datos, numero: e.target.value })} onBlur={handleBlur} placeholder="Ej: 1234" className={errores.numero ? "border-destructive" : ""} />
                                {errores.numero && <p className="text-xs text-destructive">{errores.numero}</p>}
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="piso">Piso</Label>
                                <Input id="piso" value={datos.piso} onChange={(e) => setDatos({ ...datos, piso: e.target.value })} onBlur={handleBlur} placeholder="Ej: 5" className={errores.piso ? "border-destructive" : ""} />
                                {errores.piso && <p className="text-xs text-destructive">{errores.piso}</p>}
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="departamento">Departamento</Label>
                                <Input id="departamento" value={datos.departamento} onChange={(e) => setDatos({ ...datos, departamento: e.target.value })} onBlur={handleBlur} placeholder="Ej: A" className={errores.departamento ? "border-destructive" : ""} />
                                {errores.departamento && <p className="text-xs text-destructive">{errores.departamento}</p>}
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="codPostal">Código Postal *</Label>
                                <Input id="codPostal" value={datos.codPostal} onChange={(e) => setDatos({ ...datos, codPostal: e.target.value })} onBlur={handleBlur} placeholder="Ej: 1000" className={errores.codPostal ? "border-destructive" : ""} />
                                {errores.codPostal && <p className="text-xs text-destructive">{errores.codPostal}</p>}
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="localidad">Localidad *</Label>
                                <Input id="localidad" value={datos.localidad} onChange={(e) => setDatos({ ...datos, localidad: e.target.value })} onBlur={handleBlur} placeholder="Ej: CABA" className={errores.localidad ? "border-destructive" : ""} />
                                {errores.localidad && <p className="text-xs text-destructive">{errores.localidad}</p>}
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="provincia">Provincia *</Label>
                                <Input id="provincia" value={datos.provincia} onChange={(e) => setDatos({ ...datos, provincia: e.target.value })} onBlur={handleBlur} placeholder="Ej: Buenos Aires" className={errores.provincia ? "border-destructive" : ""} />
                                {errores.provincia && <p className="text-xs text-destructive">{errores.provincia}</p>}
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="pais">País *</Label>
                                <Input id="pais" value={datos.pais} onChange={(e) => setDatos({ ...datos, pais: e.target.value })} onBlur={handleBlur} placeholder="Ej: Argentina" className={errores.pais ? "border-destructive" : ""} />
                                {errores.pais && <p className="text-xs text-destructive">{errores.pais}</p>}
                            </div>
                        </div>
                    </div>

                    {/* --- BOTONES FOOTER (Diferencia clave con CU09: Botón Borrar) --- */}
                    <div className="flex flex-col-reverse gap-3 sm:flex-row sm:justify-between pt-4 border-t">
                        <div className="flex flex-col-reverse gap-3 sm:flex-row">
                            <Button onClick={() => setShowCancelDialog(true)} variant="outline" className="w-full sm:w-auto">
                                Cancelar
                            </Button>
                            <Button onClick={() => setShowDeleteDialog(true)} variant="destructive" className="w-full sm:w-auto">
                                <UserMinus className="mr-2 h-4 w-4" />
                                Borrar
                            </Button>
                        </div>
                        <Button onClick={handleNext} disabled={isSaving} className="w-full sm:w-auto">
                            {isSaving ? "Guardando..." : <><span className="mr-2">Guardar Cambios</span> <ArrowRight className="h-4 w-4" /></>}
                        </Button>
                    </div>

                    {alert && (
                        <Alert variant={alert.type === "error" ? "destructive" : "default"} className="mt-4">
                            {alert.type === "success" && <CheckCircle2 className="h-4 w-4" />}
                            {alert.type === "error" && <AlertCircle className="h-4 w-4" />}
                            <AlertDescription>{alert.message}</AlertDescription>
                        </Alert>
                    )}
                </Card>

                {/* --- DIALOGOS --- */}

                {/* Confirmar Guardar */}
                <Dialog open={showConfirmDialog} onOpenChange={setShowConfirmDialog}>
                    <DialogContent>
                        <DialogHeader>
                            <DialogTitle>Confirmar modificación</DialogTitle>
                            <DialogDescription>¿Está seguro de que desea guardar los cambios realizados al huésped?</DialogDescription>
                        </DialogHeader>
                        <div className="rounded-lg bg-blue-50 p-4 dark:bg-blue-950/20">
                            <p className="text-sm font-medium text-blue-800 dark:text-blue-300">Los datos de <strong>{datos.nombres} {datos.apellido}</strong> serán actualizados.</p>
                        </div>
                        <DialogFooter>
                            <Button variant="outline" onClick={() => setShowConfirmDialog(false)}>Cancelar</Button>
                            <Button onClick={confirmSave}><CheckCircle2 className="mr-2 h-4 w-4" />Confirmar</Button>
                        </DialogFooter>
                    </DialogContent>
                </Dialog>

                {/* Cancelar */}
                <Dialog open={showCancelDialog} onOpenChange={setShowCancelDialog}>
                    <DialogContent>
                        <DialogHeader>
                            <DialogTitle>¿Desea cancelar la edición?</DialogTitle>
                            <DialogDescription>Si cancela, todos los cambios se perderán y volverá al menú.</DialogDescription>
                        </DialogHeader>
                        <DialogFooter>
                            <Button variant="outline" onClick={() => setShowCancelDialog(false)}>No, continuar</Button>
                            <Button variant="destructive" onClick={() => router.push("/")}>Sí, cancelar</Button>
                        </DialogFooter>
                    </DialogContent>
                </Dialog>

                {/* Borrar */}
                <Dialog open={showDeleteDialog} onOpenChange={setShowDeleteDialog}>
                    <DialogContent>
                        <DialogHeader>
                            <DialogTitle>Dar de baja huésped</DialogTitle>
                            <DialogDescription>¿Está seguro que desea eliminar a este huésped permanentemente?</DialogDescription>
                        </DialogHeader>
                        <div className="rounded-lg bg-rose-50 p-4 dark:bg-rose-950/20">
                            <p className="text-sm font-medium text-rose-800 dark:text-rose-300">⚠️ Esta acción no se puede deshacer.</p>
                        </div>
                        <DialogFooter>
                            <Button variant="outline" onClick={() => setShowDeleteDialog(false)}>Cancelar</Button>
                            <Button variant="destructive" onClick={confirmDelete}><UserMinus className="mr-2 h-4 w-4" />Confirmar Eliminación</Button>
                        </DialogFooter>
                    </DialogContent>
                </Dialog>

                {/* Fusión */}
                <Dialog open={showFusionDialog} onOpenChange={setShowFusionDialog}>
                    <DialogContent className="sm:max-w-[500px]">
                        <DialogHeader>
                            <DialogTitle className="flex items-center gap-2 text-amber-600">
                                <AlertCircle className="h-6 w-6" />
                                ATENCIÓN: Huésped Existente
                            </DialogTitle>
                            <DialogDescription className="pt-2 text-base">
                                El huésped con <strong>{datos.tipoDocumento} {datos.nroDocumento}</strong> YA EXISTE.
                            </DialogDescription>
                        </DialogHeader>

                        <div className="rounded-md bg-amber-50 p-4 text-sm text-amber-900 dark:bg-amber-950/30 dark:text-amber-200">
                            <p className="mb-2 font-bold">Si continúa, se realizará una FUSIÓN:</p>
                            <ul className="list-disc pl-5 space-y-1">
                                <li>Se actualizarán los datos del huésped existente con los de este formulario.</li>
                                <li>El historial (Reservas, Estadías, Facturas) del huésped actual se moverá al nuevo.</li>
                                <li><strong>El registro actual será eliminado.</strong></li>
                            </ul>
                        </div>

                        <div className="py-2">
                            <p className="text-sm font-medium text-center">¿Desea proceder con la FUSIÓN?</p>
                        </div>

                        <DialogFooter className="gap-2 sm:gap-0">
                            <Button variant="outline" onClick={() => setShowFusionDialog(false)}>Cancelar</Button>
                            <Button onClick={handleFusionConfirm} className="bg-amber-600 hover:bg-amber-700 text-white">Aceptar Fusión</Button>
                        </DialogFooter>
                    </DialogContent>
                </Dialog>

            </div>
        </div>
    )
}