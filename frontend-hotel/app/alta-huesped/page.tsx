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
    pais: "Argentina",
  })

  const [errores, setErrores] = useState<Errores>({})
  const [popup, setPopup] = useState<TipoPopup>(null)
  const [huespedCreado, setHuespedCreado] = useState<{ nombres: string; apellido: string } | null>(null)
  const [huespedExistente, setHuespedExistente] = useState<any>(null)

  const regexNombre = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/
  const regexDocumento = /^[a-zA-Z0-9]+$/
  const regexCuit = /^\d{2}-?\d{8}-?\d{1}$/
  const regexTelefono = /^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\s./0-9]*$/
  const regexEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  const regexCalle = /^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\s.,]+$/
  const regexTexto = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/
  const regexAlfanumerico = /^[a-zA-Z0-9]+$/

  const validarFormulario = (): boolean => {
    const nuevosErrores: Errores = {}

    if (!datos.apellido.trim()) {
      nuevosErrores.apellido = "El apellido es obligatorio"
    } else if (datos.apellido.length < 2 || datos.apellido.length > 50) {
      nuevosErrores.apellido = "El apellido debe tener entre 2 y 50 caracteres"
    } else if (!regexNombre.test(datos.apellido)) {
      nuevosErrores.apellido = "El apellido solo puede contener letras y espacios"
    }

    if (!datos.nombres.trim()) {
      nuevosErrores.nombres = "El nombre es obligatorio"
    } else if (datos.nombres.length < 2 || datos.nombres.length > 50) {
      nuevosErrores.nombres = "El nombre debe tener entre 2 y 50 caracteres"
    } else if (!regexNombre.test(datos.nombres)) {
      nuevosErrores.nombres = "El nombre solo puede contener letras y espacios"
    }

    if (!datos.tipoDocumento) {
      nuevosErrores.tipoDocumento = "El tipo de documento es obligatorio"
    }

    if (!datos.nroDocumento.trim()) {
      nuevosErrores.nroDocumento = "El número de documento es obligatorio"
    } else if (datos.nroDocumento.length < 6 || datos.nroDocumento.length > 15) {
      nuevosErrores.nroDocumento = "El documento debe tener entre 6 y 15 caracteres"
    } else if (!regexDocumento.test(datos.nroDocumento)) {
      nuevosErrores.nroDocumento = "El documento no debe contener espacios ni símbolos"
    }

    if (datos.posicionIva === "RESPONSABLE_INSCRIPTO") {
      if (!datos.cuit.trim()) {
        nuevosErrores.cuit = "El CUIT es obligatorio para Responsables Inscriptos"
      } else if (!regexCuit.test(datos.cuit)) {
        nuevosErrores.cuit = "El CUIT debe tener 11 dígitos (con o sin guiones)"
      }
    } else if (datos.cuit.trim() && !regexCuit.test(datos.cuit)) {
      nuevosErrores.cuit = "El CUIT debe tener 11 dígitos (con o sin guiones)"
    }

    if (!datos.posicionIva) {
      nuevosErrores.posicionIva = "La posición frente al IVA es obligatoria"
    }

    if (!datos.fechaNacimiento) {
      nuevosErrores.fechaNacimiento = "La fecha de nacimiento es obligatoria"
    } else {
      const fechaNac = new Date(datos.fechaNacimiento)
      const hoy = new Date()
      if (fechaNac >= hoy) {
        nuevosErrores.fechaNacimiento = "La fecha de nacimiento debe ser anterior a hoy"
      }
    }

    if (!datos.nacionalidad.trim()) {
      nuevosErrores.nacionalidad = "La nacionalidad es obligatoria"
    }

    if (!datos.email.trim()) {
      nuevosErrores.email = "El email es obligatorio"
    } else if (!regexEmail.test(datos.email)) {
      nuevosErrores.email = "Formato de email inválido"
    }

    if (!datos.telefono.trim()) {
      nuevosErrores.telefono = "El teléfono es obligatorio"
    } else {
      const telefonoNormalizado = datos.telefono.trim()
      if (!regexTelefono.test(telefonoNormalizado)) {
        nuevosErrores.telefono = "Formato de teléfono inválido"
      }
    }

    if (!datos.calle.trim()) {
      nuevosErrores.calle = "La calle es obligatoria"
    } else if (!regexCalle.test(datos.calle)) {
      nuevosErrores.calle = "La calle contiene caracteres inválidos"
    } else if (datos.calle.length > 100) {
      nuevosErrores.calle = "La calle no puede superar los 100 caracteres"
    }

    if (!datos.numero.trim()) {
      nuevosErrores.numero = "El número de calle es obligatorio"
    } else {
      const numVal = Number.parseInt(datos.numero)
      if (isNaN(numVal) || numVal < 1 || numVal > 99999) {
        nuevosErrores.numero = "El número debe ser entre 1 y 99999"
      }
    }

    if (datos.departamento.trim() && !regexAlfanumerico.test(datos.departamento)) {
      nuevosErrores.departamento = "El departamento solo acepta letras y números"
    } else if (datos.departamento.length > 5) {
      nuevosErrores.departamento = "El departamento es muy largo"
    }

    if (datos.piso.trim() && !regexAlfanumerico.test(datos.piso)) {
      nuevosErrores.piso = "El piso solo acepta letras y números"
    } else if (datos.piso.length > 5) {
      nuevosErrores.piso = "El piso es muy largo"
    }

    if (!datos.codPostal.trim()) {
      nuevosErrores.codPostal = "El código postal es obligatorio"
    } else {
      const codVal = Number.parseInt(datos.codPostal)
      if (isNaN(codVal) || codVal < 1000 || codVal > 9999) {
        nuevosErrores.codPostal = "El código postal debe ser entre 1000 y 9999"
      }
    }

    if (!datos.localidad.trim()) {
      nuevosErrores.localidad = "La localidad es obligatoria"
    } else if (!regexTexto.test(datos.localidad)) {
      nuevosErrores.localidad = "La localidad solo puede contener letras y espacios"
    }

    if (!datos.provincia.trim()) {
      nuevosErrores.provincia = "La provincia es obligatoria"
    } else if (!regexTexto.test(datos.provincia)) {
      nuevosErrores.provincia = "La provincia solo puede contener letras y espacios"
    }

    if (!datos.pais.trim()) {
      nuevosErrores.pais = "El país es obligatorio"
    } else if (!regexTexto.test(datos.pais)) {
      nuevosErrores.pais = "El país solo puede contener letras y espacios"
    }

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
    // 1. Validaciones del frontend
    const esValido = validarFormulario()
    console.log("Validación resultado:", esValido)
    console.log("Errores encontrados:", errores)
    
    if (!esValido) {
      console.log("Formulario inválido, mostrando errores")
      return
    }

    // 2. Verificar duplicados en el backend
    console.log("Verificando duplicados para:", datos.tipoDocumento, datos.nroDocumento)
    try {
      const existente = await verificarExistenciaHuesped(datos.tipoDocumento, datos.nroDocumento)
      console.log("Resultado verificación:", existente)
      if (existente) {
        // Guardar el huésped existente y mostrar popup de duplicado
        setHuespedExistente(existente)
        setPopup("duplicado_dni")
        return
      }
    } catch (error) {
      console.error("Error al verificar duplicado:", error)
      // Continuar con el guardado aunque falle la verificación
    }

    console.log("Llamando a guardarEnBackend")
    // 3. Si no hay duplicado, guardar
    guardarEnBackend(false)
  }

  const handleContinuarConDuplicado = () => {
    setPopup(null)
    // Si hay duplicado, modificar en lugar de crear
    guardarEnBackend(true)
  }

  const handleCorregirDatos = () => {
    setPopup(null)
    setHuespedExistente(null)
    // No hacer nada más, solo permitir al usuario corregir los datos
  }

  const guardarEnBackend = async (esModificacion: boolean = false) => {
      try {
        console.log("Iniciando guardado en backend...", esModificacion ? "(MODIFICACIÓN)" : "(CREACIÓN)")
        // 1. Convertir datos del formulario al formato DtoHuesped del backend
        // El backend espera números como números (no strings) y listas para email/teléfono
        const nuevoHuesped = {
          nombres: datos.nombres,
          apellido: datos.apellido,
          tipoDocumento: datos.tipoDocumento,
          nroDocumento: datos.nroDocumento,
          cuit: datos.cuit || null,
          posicionIva: datos.posicionIva,
          fechaNacimiento: datos.fechaNacimiento,
          nacionalidad: datos.nacionalidad,
          // El backend espera listas, así que metemos el string en un array
          email: [datos.email],
          // Limpiamos el teléfono de caracteres no numéricos para enviarlo como Long
          telefono: [parseInt(datos.telefono.replace(/\D/g, '')) || 0],
          ocupacion: ["Ocupacion"], // Campo opcional

          // Objeto anidado DtoDireccion (no "domicilio")
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

        console.log("Datos a enviar:", nuevoHuesped)
        
        // 2. Llamada real al Backend
        if (esModificacion && huespedExistente) {
          // Modificar el huésped existente
          console.log("Modificando huésped existente...")
          await modificarHuespedAPI(datos.tipoDocumento, datos.nroDocumento, nuevoHuesped)
          console.log("Huésped modificado exitosamente")
        } else {
          // Crear nuevo huésped
          console.log("Creando nuevo huésped...")
          await crearHuespedAPI(nuevoHuesped)
          console.log("Huésped creado exitosamente")
        }
        
        // 3. Si no hubo error, actualizamos estado de éxito
        setHuespedCreado({
          nombres: datos.nombres,
          apellido: datos.apellido,
        })
        setHuespedExistente(null) // Limpiar el estado del existente
        setPopup("exito")

      } catch (error: any) {
        console.error("Error al guardar huésped:", error)
        // Mostramos el error del backend en un alert o en la UI
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
      pais: "Argentina",
    })
    setErrores({})
    setHuespedCreado(null)
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
                    setErrores({ ...errores, apellido: undefined })
                  }}
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
                    setErrores({ ...errores, nombres: undefined })
                  }}
                  placeholder="Ej: Juan Carlos"
                  className={errores.nombres ? "border-destructive" : ""}
                />
                {errores.nombres && <p className="text-xs text-destructive">{errores.nombres}</p>}
              </div>

              <div className="space-y-2">
                <Label htmlFor="tipoDocumento">Tipo de Documento *</Label>
                <Select
                  value={datos.tipoDocumento}
                  onValueChange={(value) => {
                    setDatos({ ...datos, tipoDocumento: value, nroDocumento: "" })
                    setErrores({ ...errores, tipoDocumento: undefined, nroDocumento: undefined })
                  }}
                >
                  <SelectTrigger className={errores.tipoDocumento ? "border-destructive" : ""}>
                    <SelectValue placeholder="Seleccione..." />
                  </SelectTrigger>
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
                  type="text"
                  value={datos.nroDocumento}
                  onChange={(e) => {
                    setDatos({ ...datos, nroDocumento: e.target.value })
                    setErrores({ ...errores, nroDocumento: undefined })
                  }}
                  placeholder="Alfanumérico sin espacios"
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
                  max={new Date().toISOString().split("T")[0]}
                  onChange={(e) => {
                    setDatos({ ...datos, fechaNacimiento: e.target.value })
                    setErrores({ ...errores, fechaNacimiento: undefined })
                  }}
                  className={errores.fechaNacimiento ? "border-destructive" : ""}
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
                    setErrores({ ...errores, nacionalidad: undefined })
                  }}
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
                    setErrores({ ...errores, cuit: undefined })
                  }}
                  placeholder="XX-XXXXXXXX-X"
                  className={errores.cuit ? "border-destructive" : ""}
                />
                {errores.cuit && <p className="text-xs text-destructive">{errores.cuit}</p>}
              </div>

              <div className="space-y-2">
                <Label htmlFor="email">Email *</Label>
                <Input
                  id="email"
                  type="email"
                  value={datos.email}
                  onChange={(e) => {
                    setDatos({ ...datos, email: e.target.value })
                    setErrores({ ...errores, email: undefined })
                  }}
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
                    setErrores({ ...errores, telefono: undefined })
                  }}
                  placeholder="+54 11 1234-5678"
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
                    setErrores({ ...errores, calle: undefined })
                  }}
                  placeholder="Av. Corrientes"
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
                    setErrores({ ...errores, numero: undefined })
                  }}
                  placeholder="1234"
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
                    setErrores({ ...errores, piso: undefined })
                  }}
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
                    setErrores({ ...errores, departamento: undefined })
                  }}
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
                    setErrores({ ...errores, codPostal: undefined })
                  }}
                  placeholder="1000"
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
                    setErrores({ ...errores, localidad: undefined })
                  }}
                  placeholder="CABA"
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
                    setErrores({ ...errores, provincia: undefined })
                  }}
                  placeholder="Buenos Aires"
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
                    setErrores({ ...errores, pais: undefined })
                  }}
                  placeholder="Argentina"
                  className={errores.pais ? "border-destructive" : ""}
                />
                {errores.pais && <p className="text-xs text-destructive">{errores.pais}</p>}
              </div>
            </div>
          </div>

          <div className="flex flex-wrap gap-3">
            <Button onClick={handleAceptar} size="lg">
              <CheckCircle2 className="mr-2 h-4 w-4" />
              Aceptar
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
              <p className="mb-6 text-sm">
                El tipo y número de documento ya existen en el sistema
              </p>
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
