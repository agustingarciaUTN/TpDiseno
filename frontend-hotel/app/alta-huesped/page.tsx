"use client"
import { crearHuesped as crearHuespedAPI, modificarHuesped as modificarHuespedAPI, verificarExistenciaHuesped } from "@/lib/api"
import { useState } from "react"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { UserPlus, AlertCircle, CheckCircle2, Home } from "lucide-react"

interface DatosHuesped {
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
}

interface DatosFormulario extends DatosHuesped {
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

type TipoPopup = "confirmacion_cancelar" | "duplicado_dni" | "exito" | null

export default function AltaHuesped() {
    const [datos, setDatos] = useState<DatosFormulario>({
        nombres: "",
        apellido: "",
        tipoDocumento: "",
        nroDocumento: "",
        cuit: "",
        posicionIva: "CONSUMIDOR_FINAL",
        fechaNacimiento: "",
        nacionalidad: "",
        email: "",
        telefono: "",
        calle: "",
        numero: "",
        departamento: "",
        piso: "",
        codPostal: "",
        localidad: "",
        provincia: "",
        pais: "",
    })

    const [errores, setErrores] = useState<Errores>({})
    const [popup, setPopup] = useState<TipoPopup>(null)
    const [huespedCreado, setHuespedCreado] = useState<{ nombres: string; apellido: string } | null>(null)
    const [huespedExistente, setHuespedExistente] = useState<any>(null)

    const regexNombre = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/
    const regexCuit = /^\d{2}-?\d{8}-?\d{1}$/
    const regexTelefono = /^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\s./0-9]*$/
    const regexEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    const regexCalle = /^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\s.,]+$/
    const regexTexto = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/
    const regexAlfanumerico = /^[a-zA-Z0-9]+$/

    // --- CONSTANTES DE MENSAJES (ESTANDARIZACIÓN) ---
    const MSJ_OBLIGATORIO = "Este campo es obligatorio"
    const MSJ_TEXTO = "Solo se permiten letras y espacios"
    const MSJ_ALFANUMERICO = "Solo se permiten letras y números"
    const MSJ_NUMERICO = "Solo se permiten números válidos"
    const MSJ_FORMATO_EMAIL = "Formato inválido (ej: usuario@dominio.com)"
    const MSJ_FORMATO_TEL = "Formato inválido (ej: +54 342 1234567)"
    const MSJ_LARGO_CORTO = "El texto ingresado es demasiado corto"
    const MSJ_LARGO_EXCESIVO = "El texto supera el límite permitido"

    // --- Validación individual por campo (ESTANDARIZADA) ---
    // --- Validación individual por campo (CORREGIDA) ---
    const validarCampo = (nombre: keyof DatosFormulario, valor: any) => {
        let error: string | undefined = undefined

        switch (nombre) {
            case "apellido":
                if (!valor.trim()) error = MSJ_OBLIGATORIO
                else if (valor.length < 2) error = MSJ_LARGO_CORTO
                else if (valor.length > 50) error = MSJ_LARGO_EXCESIVO
                else if (!regexNombre.test(valor)) error = MSJ_TEXTO
                break

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
                if (!valor.trim()) {
                    error = MSJ_OBLIGATORIO
                } else if (!datos.tipoDocumento) {
                    error = "Seleccione primero el tipo de documento"
                } else {
                    const config = CONFIG_DOCUMENTOS[datos.tipoDocumento]
                    if (config && !config.regex.test(valor)) {
                        error = config.error
                    }
                }
                break

            case "cuit":
                const esObligatorio = datos.posicionIva === "RESPONSABLE_INSCRIPTO"
                const cuitLimpio = limpiarCuit(valor)

                if (esObligatorio && !valor.trim()) {
                    error = "Requerido para Resp. Inscripto"
                } else if (valor.trim()) {
                    if (!regexCuit.test(valor)) {
                        error = "Formato inválido (Ej: 20-12345678-9)"
                    } else {
                        const prefijo = cuitLimpio.substring(0, 2)

                        if (!PREFIJOS_CUIT.includes(prefijo)) {
                            error = "Prefijo inválido (se espera 20, 23, 27...)"
                        }
                        else if (["DNI", "LC", "LE"].includes(datos.tipoDocumento) && datos.nroDocumento) {
                            // LOGICA CORREGIDA: Extrae el centro y compara como NUMERO
                            const dniEnCuitString = cuitLimpio.substring(2, cuitLimpio.length - 1)
                            const dniEnCuitNum = parseInt(dniEnCuitString, 10)
                            const dniIngresadoNum = parseInt(datos.nroDocumento, 10)

                            if (dniEnCuitNum !== dniIngresadoNum) {
                                error = "El CUIT no coincide con el Documento ingresado"
                            }
                        }
                    }
                }
                break

            case "fechaNacimiento":
                if (!valor) {
                    error = MSJ_OBLIGATORIO
                } else {
                    const fechaNac = new Date(valor)
                    const hoy = new Date()
                    const fechaMinima = new Date("1900-01-01")
                    if (fechaNac >= hoy) error = "La fecha debe ser anterior a hoy"
                    else if (fechaNac < fechaMinima) error = "Fecha inválida (muy antigua)"
                }
                break

            case "nacionalidad":
                if (!valor.trim()) error = MSJ_OBLIGATORIO
                // AGREGADO: Validación de solo letras
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
                if (!valor.trim()) error = MSJ_OBLIGATORIO
                else if (!regexCalle.test(valor)) error = "Contiene caracteres inválidos"
                else if (valor.length > 100) error = MSJ_LARGO_EXCESIVO
                break

            case "numero":
                if (!valor.trim()) error = MSJ_OBLIGATORIO
                else {
                    const numVal = Number.parseInt(valor)
                    // Validamos que sea número y el rango lógico (hasta 99999)
                    if (isNaN(numVal) || numVal < 1 || numVal > 99999) error = "Número inválido (máx. 5 dígitos)"
                }
                break

            case "departamento":
                // MODIFICADO: Ahora permite espacios/puntos (regexCalle) y hasta 20 chars
                if (valor.trim() && !regexCalle.test(valor)) error = "Caracteres inválidos"
                else if (valor.length > 20) error = MSJ_LARGO_EXCESIVO
                break

            case "piso":
                // MODIFICADO: Ahora permite espacios/puntos (regexCalle) y hasta 20 chars
                if (valor.trim() && !regexCalle.test(valor)) error = "Caracteres inválidos"
                else if (valor.length > 20) error = MSJ_LARGO_EXCESIVO
                break

            case "codPostal":
                if (!valor.trim()) error = MSJ_OBLIGATORIO

                else if (!/^\d+$/.test(valor)) error = MSJ_NUMERICO
                else if (valor.length > 8) error = "Código postal demasiado largo"
                break

            case "localidad":
                if (!valor.trim()) error = MSJ_OBLIGATORIO
                // MODIFICADO: Ahora usa regexCalle para permitir números (ej: 9 de Julio)
                else if (!regexCalle.test(valor)) error = "Caracteres inválidos"
                break

            case "provincia":
                if (!valor.trim()) error = MSJ_OBLIGATORIO
                else if (!regexTexto.test(valor)) error = MSJ_TEXTO
                break

            case "pais":
                if (!valor.trim()) error = MSJ_OBLIGATORIO
                else if (!regexTexto.test(valor)) error = MSJ_TEXTO
                break
        }

        setErrores((prev) => ({
            ...prev,
            [nombre]: error,
        }))
    }

    // --- Manejador del evento onBlur ---
    const handleBlur = (e: React.FocusEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { id, value } = e.target
        validarCampo(id as keyof DatosFormulario, value)
    }

    const validarFormulario = (): boolean => {
        const nuevosErrores: Errores = {}

        // Apellido
        if (!datos.apellido.trim()) nuevosErrores.apellido = MSJ_OBLIGATORIO
        else if (datos.apellido.length < 2) nuevosErrores.apellido = MSJ_LARGO_CORTO
        else if (datos.apellido.length > 50) nuevosErrores.apellido = MSJ_LARGO_EXCESIVO
        else if (!regexNombre.test(datos.apellido)) nuevosErrores.apellido = MSJ_TEXTO

        // Nombres
        if (!datos.nombres.trim()) nuevosErrores.nombres = MSJ_OBLIGATORIO
        else if (datos.nombres.length < 2) nuevosErrores.nombres = MSJ_LARGO_CORTO
        else if (datos.nombres.length > 50) nuevosErrores.nombres = MSJ_LARGO_EXCESIVO
        else if (!regexNombre.test(datos.nombres)) nuevosErrores.nombres = MSJ_TEXTO

        // Documentos
        if (!datos.tipoDocumento) nuevosErrores.tipoDocumento = MSJ_OBLIGATORIO
        if (!datos.nroDocumento.trim()) {
            nuevosErrores.nroDocumento = MSJ_OBLIGATORIO
        } else if (datos.tipoDocumento) {
            const config = CONFIG_DOCUMENTOS[datos.tipoDocumento]
            if (config && !config.regex.test(datos.nroDocumento)) {
                nuevosErrores.nroDocumento = config.error
            }
        }

        // CUIT
        const esCuitObligatorio = datos.posicionIva === "RESPONSABLE_INSCRIPTO"
        const cuitValor = datos.cuit.trim()

        if (esCuitObligatorio && !cuitValor) {
            nuevosErrores.cuit = "Requerido para Resp. Inscripto"
        } else if (cuitValor) {
            const cuitLimpio = limpiarCuit(cuitValor)

            if (!regexCuit.test(cuitValor)) {
                nuevosErrores.cuit = "Formato inválido (Ej: 20-12345678-9)"
            } else {
                const prefijo = cuitLimpio.substring(0, 2)

                if (!PREFIJOS_CUIT.includes(prefijo)) {
                    nuevosErrores.cuit = "Prefijo inválido (se espera 20, 23, 27...)"
                }
                else if (["DNI", "LC", "LE"].includes(datos.tipoDocumento) && datos.nroDocumento) {
                    // LOGICA CORREGIDA: Extrae el centro y compara como NUMERO
                    const dniEnCuitString = cuitLimpio.substring(2, cuitLimpio.length - 1)
                    const dniEnCuitNum = parseInt(dniEnCuitString, 10)
                    const dniIngresadoNum = parseInt(datos.nroDocumento, 10)

                    if (dniEnCuitNum !== dniIngresadoNum) {
                        nuevosErrores.cuit = "El CUIT no coincide con el Documento ingresado"
                    }
                }
            }
        }

        // Otros datos personales
        if (!datos.posicionIva) nuevosErrores.posicionIva = MSJ_OBLIGATORIO
        if (!datos.fechaNacimiento) nuevosErrores.fechaNacimiento = MSJ_OBLIGATORIO
        else {
            const fechaNac = new Date(datos.fechaNacimiento)
            const hoy = new Date()
            const fechaMinima = new Date("1900-01-01")
            if (fechaNac >= hoy) nuevosErrores.fechaNacimiento = "La fecha debe ser anterior a hoy"
            else if (fechaNac < fechaMinima) nuevosErrores.fechaNacimiento = "Fecha inválida"
        }

        if (!datos.nacionalidad.trim()) nuevosErrores.nacionalidad = MSJ_OBLIGATORIO
        else if (!regexTexto.test(datos.nacionalidad)) nuevosErrores.nacionalidad = MSJ_TEXTO

        if (datos.email.trim() && !regexEmail.test(datos.email)) nuevosErrores.email = MSJ_FORMATO_EMAIL

        if (!datos.telefono.trim()) nuevosErrores.telefono = MSJ_OBLIGATORIO
        else if (!regexTelefono.test(datos.telefono.trim())) nuevosErrores.telefono = MSJ_FORMATO_TEL

        // Dirección
        if (!datos.calle.trim()) nuevosErrores.calle = MSJ_OBLIGATORIO
        else if (!regexCalle.test(datos.calle)) nuevosErrores.calle = "Contiene caracteres inválidos"
        else if (datos.calle.length > 100) nuevosErrores.calle = MSJ_LARGO_EXCESIVO

        if (!datos.numero.trim()) nuevosErrores.numero = MSJ_OBLIGATORIO
        else {
            const numVal = Number.parseInt(datos.numero)
            if (isNaN(numVal) || numVal < 1 || numVal > 99999) nuevosErrores.numero = "Ingrese un número entre 1 y 99999"
        }

        // Departamento y Piso (Más flexibles)
        if (datos.departamento.trim() && !regexCalle.test(datos.departamento)) nuevosErrores.departamento = "Caracteres inválidos"
        else if (datos.departamento.length > 20) nuevosErrores.departamento = MSJ_LARGO_EXCESIVO

        if (datos.piso.trim() && !regexCalle.test(datos.piso)) nuevosErrores.piso = "Caracteres inválidos"
        else if (datos.piso.length > 20) nuevosErrores.piso = MSJ_LARGO_EXCESIVO

        // Código Postal (Más flexible)
        if (!datos.codPostal.trim()) nuevosErrores.codPostal = MSJ_OBLIGATORIO
        else if (datos.codPostal.length > 10) nuevosErrores.codPostal = "Código postal demasiado largo"

        // Localidad (Permite números)
        if (!datos.localidad.trim()) nuevosErrores.localidad = MSJ_OBLIGATORIO
        else if (!regexCalle.test(datos.localidad)) nuevosErrores.localidad = "Caracteres inválidos"

        if (!datos.provincia.trim()) nuevosErrores.provincia = MSJ_OBLIGATORIO
        else if (!regexTexto.test(datos.provincia)) nuevosErrores.provincia = MSJ_TEXTO

        if (!datos.pais.trim()) nuevosErrores.pais = MSJ_OBLIGATORIO
        else if (!regexTexto.test(datos.pais)) nuevosErrores.pais = MSJ_TEXTO

        setErrores(nuevosErrores)
        return Object.keys(nuevosErrores).length === 0
    }

    const handleCancelar = () => {
        setPopup("confirmacion_cancelar")
    }

    const handleConfirmarCancelacion = () => {
        setPopup(null)
        window.location.href = "/"
    }

    const handleRechazarCancelacion = () => {
        setPopup(null)
    }

    const handleAceptar = async () => {
        const esValido = validarFormulario()
        if (!esValido) return

        try {
            const existente = await verificarExistenciaHuesped(datos.tipoDocumento, datos.nroDocumento)
            if (existente) {
                setHuespedExistente(existente)
                setPopup("duplicado_dni")
                return
            }
        } catch (error) {
            console.error("Error al verificar duplicado:", error)
        }

        guardarEnBackend(false)
    }

    const handleContinuarConDuplicado = () => {
        setPopup(null)
        guardarEnBackend(true)
    }

    const handleCorregirDatos = () => {
        setPopup(null)
        setHuespedExistente(null)

        // Esperamos 100ms a que se cierre el popup para mover el foco
        setTimeout(() => {
            const inputDocumento = document.getElementById("nroDocumento") as HTMLInputElement
            if (inputDocumento) {
                inputDocumento.focus()
                inputDocumento.select()
            }
        }, 100)
    }

    const guardarEnBackend = async (esModificacion: boolean = false) => {
        try {
            const nuevoHuesped = {
                nombres: datos.nombres,
                apellido: datos.apellido,
                tipoDocumento: datos.tipoDocumento,
                nroDocumento: datos.nroDocumento,
                cuit: datos.cuit || null,
                posicionIva: datos.posicionIva,
                fechaNacimiento: datos.fechaNacimiento,
                nacionalidad: datos.nacionalidad,
                email: datos.email.trim() ? [datos.email] : [],
                telefono: [parseInt(datos.telefono.replace(/\D/g, '')) || 0],
                ocupacion: ["Ocupacion"],
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

            if (esModificacion && huespedExistente) {
                await modificarHuespedAPI(datos.tipoDocumento, datos.nroDocumento, nuevoHuesped)
            } else {
                await crearHuespedAPI(nuevoHuesped)
            }

            setHuespedCreado({
                nombres: datos.nombres,
                apellido: datos.apellido,
            })
            setHuespedExistente(null)
            setPopup("exito")

        } catch (error: any) {
            console.error("Error al guardar huésped:", error)
            alert("Error al guardar en el sistema: " + (error.message || "Error desconocido"))
        }
    }

    const handleAceptarExito = () => {
        setPopup(null)
        resetearFormulario()
    }

    const handleVolver = () => {
        setPopup(null)
        window.location.href = "/"
    }

    const resetearFormulario = () => {
        setDatos({
            nombres: "",
            apellido: "",
            tipoDocumento: "",
            nroDocumento: "",
            cuit: "",
            posicionIva: "CONSUMIDOR_FINAL",
            fechaNacimiento: "",
            nacionalidad: "",
            email: "",
            telefono: "",
            calle: "",
            numero: "",
            departamento: "",
            piso: "",
            codPostal: "",
            localidad: "",
            provincia: "",
            pais: "",
        })
        setErrores({})
        setHuespedCreado(null)
    }

    const PREFIJOS_CUIT = ["20", "23", "24", "27", "30", "33", "34"]

    // Helper para limpiar el CUIT y dejar solo números
    const limpiarCuit = (cuit: string) => cuit.replace(/\D/g, "")

    // Configuración de validaciones y placeholders por tipo de documento
    const CONFIG_DOCUMENTOS: Record<string, { regex: RegExp; error: string; placeholder: string }> = {
        DNI: {
            regex: /^\d{7,8}$/,
            error: "El DNI debe tener 7 u 8 números",
            placeholder: "Ej: 12345678"
        },
        PASAPORTE: {
            regex: /^[A-Z0-9]{6,9}$/,
            error: "El pasaporte debe tener 6 a 9 caracteres alfanuméricos",
            placeholder: "Ej: A1234567"
        },
        LC: {
            regex: /^\d{6,8}$/,
            error: "La LC debe tener 6 a 8 números",
            placeholder: "Ej: 1234567"
        },
        LE: {
            regex: /^\d{6,8}$/,
            error: "La LE debe tener 6 a 8 números",
            placeholder: "Ej: 1234567"
        },
        OTRO: {
            regex: /^[a-zA-Z0-9]{5,20}$/,
            error: "Formato inválido (5-20 caracteres)",
            placeholder: "Nro. de Identificación"
        }
    }

    return (
        <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50 dark:from-slate-950 dark:via-slate-900 dark:to-slate-950">
            <div className="mx-auto max-w-6xl px-4 py-8 sm:px-6 lg:px-8">
                <div className="mb-8 space-y-2">
                    <div className="flex items-center justify-between">
                        <div className="flex items-center gap-3">
                            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-violet-600 text-white">
                                <UserPlus className="h-5 w-5" />
                            </div>
                            <div>
                                <p className="text-xs font-semibold uppercase tracking-wider text-violet-600 dark:text-violet-400">
                                    Caso de Uso
                                </p>
                                <h1 className="text-3xl font-bold text-slate-900 dark:text-slate-50">Alta de Huésped</h1>
                            </div>
                        </div>
                        <Button asChild variant="outline">
                            <Link href="/">
                                <Home className="mr-2 h-4 w-4" />
                                Volver al Inicio
                            </Link>
                        </Button>
                    </div>
                    <p className="text-slate-600 dark:text-slate-400">Registrar un nuevo huésped en el sistema</p>
                </div>

                <Card className="p-6 shadow-lg">

                    <div className="mb-4 flex justify-end">
                        <p className="text-sm text-slate-500 dark:text-slate-400">
                            (*) Campos obligatorios
                        </p>
                    </div>

                        {/* ... resto del formulario ... */}
                    <div className="mb-8">
                        <h2 className="mb-6 text-2xl font-semibold text-slate-900 dark:text-slate-50">Datos Personales</h2>
                        <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
                            <div className="space-y-2">
                                <Label htmlFor="apellido">Apellido *</Label>
                                <Input
                                    id="apellido"
                                    type="text"
                                    value={datos.apellido}
                                    onChange={(e) => {
                                        setDatos({ ...datos, apellido: e.target.value })
                                    }}
                                    onBlur={handleBlur}
                                    placeholder="Ej: González"
                                    className={errores.apellido ? "border-destructive" : ""}
                                />
                                {errores.apellido && <p className="text-xs text-destructive">{errores.apellido}</p>}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="nombres">Nombres *</Label>
                                <Input
                                    id="nombres"
                                    type="text"
                                    value={datos.nombres}
                                    onChange={(e) => {
                                        setDatos({ ...datos, nombres: e.target.value })
                                    }}
                                    onBlur={handleBlur}
                                    placeholder="Ej: Juan Carlos"
                                    className={errores.nombres ? "border-destructive" : ""}
                                />
                                {errores.nombres && <p className="text-xs text-destructive">{errores.nombres}</p>}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="tipoDocumento">Tipo de Documento *</Label>
                                <Select
                                    // TRUCO: Si el estado está vacío, le decimos al Select que el valor es "SIN_SELECCION"
                                    value={datos.tipoDocumento || "SIN_SELECCION"}
                                    onValueChange={(value) => {
                                        // TRUCO: Si elige "SIN_SELECCION", guardamos "" en el estado real
                                        const valorReal = value === "SIN_SELECCION" ? "" : value;

                                        setDatos({ ...datos, tipoDocumento: valorReal, nroDocumento: "" })
                                        setErrores({ ...errores, tipoDocumento: undefined, nroDocumento: undefined })

                                        // Si ya había escrito un número, lo revalidamos con el nuevo tipo
                                        if (datos.nroDocumento) {
                                        }
                                    }}
                                >
                                    <SelectTrigger className={errores.tipoDocumento ? "border-destructive" : ""}>
                                        <SelectValue placeholder="Seleccione..." />
                                    </SelectTrigger>
                                    <SelectContent>
                                        {/* CAMBIO: Usamos un valor explícito en lugar de "" */}
                                        <SelectItem value="SIN_SELECCION">Seleccione...</SelectItem>
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
                                    type="text"
                                    value={datos.nroDocumento}
                                    onChange={(e) => {
                                        setDatos({ ...datos, nroDocumento: e.target.value })
                                    }}
                                    // En el Input de nroDocumento
                                    onBlur={(e) => {
                                        handleBlur(e)
                                        // Si ya hay un CUIT escrito, lo revalidamos para ver si sigue coincidiendo con el nuevo DNI
                                        if (datos.cuit) {
                                            // Pequeño timeout para dar tiempo a que el estado 'datos' se actualice con el nuevo DNI
                                            setTimeout(() => validarCampo("cuit", datos.cuit), 0)
                                        }
                                    }}
                                    // Lógica dinámica para el placeholder
                                    placeholder={
                                        datos.tipoDocumento && CONFIG_DOCUMENTOS[datos.tipoDocumento]
                                            ? CONFIG_DOCUMENTOS[datos.tipoDocumento].placeholder
                                            : "Seleccione tipo de documento primero"
                                    }

                                    disabled={!datos.tipoDocumento}
                                    className={errores.nroDocumento ? "border-destructive" : ""}
                                />
                                {errores.nroDocumento && <p className="text-xs text-destructive">{errores.nroDocumento}</p>}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="fechaNacimiento">Fecha de Nacimiento *</Label>
                                <Input
                                    id="fechaNacimiento"
                                    type="date"
                                    value={datos.fechaNacimiento}
                                    // Bloquea navegación futura y pone en gris días futuros
                                    max={new Date().toISOString().split("T")[0]}
                                    // Bloquea navegación anterior a 1900
                                    min="1900-01-01"
                                    onChange={(e) => {
                                        setDatos({ ...datos, fechaNacimiento: e.target.value })
                                    }}
                                    onBlur={handleBlur}

                                    className={`${errores.fechaNacimiento ? "border-destructive" : ""} [color-scheme:light]`}
                                />
                                {errores.fechaNacimiento && <p className="text-xs text-destructive">{errores.fechaNacimiento}</p>}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="nacionalidad">Nacionalidad *</Label>
                                <Input
                                    id="nacionalidad"
                                    type="text"
                                    value={datos.nacionalidad}
                                    onChange={(e) => {
                                        setDatos({ ...datos, nacionalidad: e.target.value })
                                    }}
                                    onBlur={handleBlur}
                                    placeholder="Ej: Argentina"
                                    className={errores.nacionalidad ? "border-destructive" : ""}
                                />
                                {errores.nacionalidad && <p className="text-xs text-destructive">{errores.nacionalidad}</p>}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="posicionIva">Posición frente al IVA *</Label>
                                <Select
                                    value={datos.posicionIva}
                                    onValueChange={(value) => {
                                        setDatos({ ...datos, posicionIva: value })
                                        setErrores({ ...errores, posicionIva: undefined })
                                    }}
                                >
                                    <SelectTrigger>
                                        <SelectValue />
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="CONSUMIDOR_FINAL">Consumidor Final</SelectItem>
                                        <SelectItem value="RESPONSABLE_INSCRIPTO">Responsable Inscripto</SelectItem>
                                        <SelectItem value="MONOTRIBUTISTA">Monotributista</SelectItem> {/* <--- AGREGADO */}
                                        <SelectItem value="EXENTO">Exento</SelectItem>
                                    </SelectContent>
                                </Select>
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="cuit">CUIT {datos.posicionIva === "RESPONSABLE_INSCRIPTO" && "*"}</Label>
                                <Input
                                    id="cuit"
                                    type="text"
                                    value={datos.cuit}
                                    onChange={(e) => {
                                        setDatos({ ...datos, cuit: e.target.value })
                                    }}
                                    onBlur={handleBlur}
                                    placeholder="XX-XXXXXXXX-X"
                                    className={errores.cuit ? "border-destructive" : ""}
                                />
                                {errores.cuit && <p className="text-xs text-destructive">{errores.cuit}</p>}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="email">Email</Label>
                                <Input
                                    id="email"
                                    type="email"
                                    value={datos.email}
                                    onChange={(e) => {
                                        setDatos({ ...datos, email: e.target.value })
                                    }}
                                    onBlur={handleBlur}
                                    placeholder="ejemplo@email.com"
                                    className={errores.email ? "border-destructive" : ""}
                                />
                                {errores.email && <p className="text-xs text-destructive">{errores.email}</p>}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="telefono">Teléfono *</Label>
                                <Input
                                    id="telefono"
                                    type="tel"
                                    value={datos.telefono}
                                    onChange={(e) => {
                                        setDatos({ ...datos, telefono: e.target.value })
                                    }}
                                    onBlur={handleBlur}
                                    placeholder="Ej: +54 11 1234-5678"
                                    className={errores.telefono ? "border-destructive" : ""}
                                />
                                {errores.telefono && <p className="text-xs text-destructive">{errores.telefono}</p>}
                            </div>
                        </div>
                    </div>

                    <div className="mb-8">
                        <h2 className="mb-6 text-2xl font-semibold text-slate-900 dark:text-slate-50">Dirección</h2>
                        <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
                            <div className="space-y-2">
                                <Label htmlFor="calle">Calle *</Label>
                                <Input
                                    id="calle"
                                    type="text"
                                    value={datos.calle}
                                    onChange={(e) => {
                                        setDatos({ ...datos, calle: e.target.value })
                                    }}
                                    onBlur={handleBlur}
                                    placeholder="Ej: Av. Corrientes"
                                    className={errores.calle ? "border-destructive" : ""}
                                />
                                {errores.calle && <p className="text-xs text-destructive">{errores.calle}</p>}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="numero">Número *</Label>
                                <Input
                                    id="numero"
                                    type="text"
                                    value={datos.numero}
                                    onChange={(e) => {
                                        setDatos({ ...datos, numero: e.target.value })
                                    }}
                                    onBlur={handleBlur}
                                    placeholder="Ej: 1234"
                                    className={errores.numero ? "border-destructive" : ""}
                                />
                                {errores.numero && <p className="text-xs text-destructive">{errores.numero}</p>}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="piso">Piso</Label>
                                <Input
                                    id="piso"
                                    type="text"
                                    value={datos.piso}
                                    onChange={(e) => {
                                        setDatos({ ...datos, piso: e.target.value })
                                    }}
                                    onBlur={handleBlur}
                                    placeholder="Ej: 5"
                                    className={errores.piso ? "border-destructive" : ""}
                                />
                                {errores.piso && <p className="text-xs text-destructive">{errores.piso}</p>}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="departamento">Departamento</Label>
                                <Input
                                    id="departamento"
                                    type="text"
                                    value={datos.departamento}
                                    onChange={(e) => {
                                        setDatos({ ...datos, departamento: e.target.value })
                                    }}
                                    onBlur={handleBlur}
                                    placeholder="Ej: A"
                                    className={errores.departamento ? "border-destructive" : ""}
                                />
                                {errores.departamento && <p className="text-xs text-destructive">{errores.departamento}</p>}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="codPostal">Código Postal *</Label>
                                <Input
                                    id="codPostal"
                                    type="text"
                                    value={datos.codPostal}
                                    onChange={(e) => {
                                        setDatos({ ...datos, codPostal: e.target.value })
                                    }}
                                    onBlur={handleBlur}
                                    placeholder="Ej: 1000"
                                    className={errores.codPostal ? "border-destructive" : ""}
                                />
                                {errores.codPostal && <p className="text-xs text-destructive">{errores.codPostal}</p>}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="localidad">Localidad *</Label>
                                <Input
                                    id="localidad"
                                    type="text"
                                    value={datos.localidad}
                                    onChange={(e) => {
                                        setDatos({ ...datos, localidad: e.target.value })
                                    }}
                                    onBlur={handleBlur}
                                    placeholder="Ej: CABA"
                                    className={errores.localidad ? "border-destructive" : ""}
                                />
                                {errores.localidad && <p className="text-xs text-destructive">{errores.localidad}</p>}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="provincia">Provincia *</Label>
                                <Input
                                    id="provincia"
                                    type="text"
                                    value={datos.provincia}
                                    onChange={(e) => {
                                        setDatos({ ...datos, provincia: e.target.value })
                                    }}
                                    onBlur={handleBlur}
                                    placeholder="Ej: Buenos Aires"
                                    className={errores.provincia ? "border-destructive" : ""}
                                />
                                {errores.provincia && <p className="text-xs text-destructive">{errores.provincia}</p>}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="pais">País *</Label>
                                <Input
                                    id="pais"
                                    type="text"
                                    value={datos.pais}
                                    onChange={(e) => {
                                        setDatos({ ...datos, pais: e.target.value })
                                    }}
                                    onBlur={handleBlur}
                                    placeholder="Ej: Argentina"
                                    className={errores.pais ? "border-destructive" : ""}
                                />
                                {errores.pais && <p className="text-xs text-destructive">{errores.pais}</p>}
                            </div>
                        </div>
                    </div>

                    <div className="flex flex-wrap gap-3">
                        <Button onClick={handleAceptar} size="lg">
                            <CheckCircle2 className="mr-2 h-4 w-4" />
                            Siguiente
                        </Button>
                        <Button onClick={handleCancelar} variant="outline" size="lg">
                            Cancelar
                        </Button>
                    </div>
                </Card>

                {popup === "confirmacion_cancelar" && (
                    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
                        <Card className="w-full max-w-md p-6">
                            <div className="mb-4 flex items-center gap-3">
                                <AlertCircle className="h-6 w-6 text-amber-600" />
                                <h3 className="text-lg font-semibold">Confirmar Cancelación</h3>
                            </div>
                            <p className="mb-6 text-sm text-muted-foreground">
                                ¿Está seguro que desea cancelar? Se perderán todos los datos ingresados.
                            </p>
                            <div className="flex gap-3">
                                <Button onClick={handleConfirmarCancelacion} variant="destructive">
                                    Sí, Cancelar
                                </Button>
                                <Button onClick={handleRechazarCancelacion} variant="outline">
                                    No, Volver
                                </Button>
                            </div>
                        </Card>
                    </div>
                )}

                {popup === "duplicado_dni" && (
                    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
                        <Card className="w-full max-w-md p-6">
                            <div className="mb-4 flex items-center gap-3">
                                <AlertCircle className="h-6 w-6 text-amber-600" />
                                <h3 className="text-lg font-semibold">¡CUIDADO!</h3>
                            </div>


                            <div className="mb-6">
                                <p className="text-sm text-slate-600 dark:text-slate-400">
                                    El tipo y número de documento ya existen en el sistema.
                                </p>
                                <div className="mt-4 rounded-md bg-amber-50 p-3 dark:bg-amber-950/30">
                                    <p className="text-xs font-medium text-amber-800 dark:text-amber-300 uppercase tracking-wider">
                                        Huésped encontrado:
                                    </p>
                                    <p className="text-lg font-bold text-slate-900 dark:text-slate-50">
                                        {huespedExistente?.apellido}, {huespedExistente?.nombres}
                                    </p>
                                </div>
                            </div>


                            <div className="flex gap-3">
                                <Button onClick={handleContinuarConDuplicado}>
                                    ACEPTAR IGUALMENTE
                                </Button>
                                <Button onClick={handleCorregirDatos} variant="outline">
                                    CORREGIR
                                </Button>
                            </div>
                        </Card>
                    </div>
                )}

                {popup === "exito" && (
                    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
                        <Card className="w-full max-w-md p-6">
                            <div className="mb-4 flex items-center gap-3">
                                <CheckCircle2 className="h-6 w-6 text-green-600" />
                                <h3 className="text-lg font-semibold">Huésped Creado</h3>
                            </div>
                            <p className="mb-6 text-sm text-muted-foreground">
                                El huésped{" "}
                                <strong>
                                    {huespedCreado?.apellido}, {huespedCreado?.nombres}
                                </strong>{" "}
                                ha sido creado exitosamente.
                            </p>
                            <div className="flex gap-3">
                                <Button onClick={handleAceptarExito}>Crear Otro</Button>
                                <Button onClick={handleVolver} variant="outline">
                                    Volver al Menú
                                </Button>
                            </div>
                        </Card>
                    </div>
                )}
            </div>
        </div>
    )
}