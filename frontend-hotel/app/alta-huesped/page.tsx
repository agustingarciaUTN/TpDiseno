"use client";

import Link from "next/link";
import { useState } from "react";

interface DatosHuesped {
  nombres: string;
  apellido: string;
  tipoDocumento: string;
  nroDocumento: string;
  cuit: string;
  posicionIva: string;
  fechaNacimiento: string;
  nacionalidad: string;
  email: string;
  telefono: string;
}

interface DatosFormulario extends DatosHuesped {
  calle: string;
  numero: string;
  departamento: string;
  piso: string;
  codPostal: string;
  localidad: string;
  provincia: string;
  pais: string;
}

interface Errores {
  [key: string]: string | undefined;
}

type TipoPopup = "confirmacion_cancelar" | "duplicado_dni" | "exito" | null;

export default function AltaHuesped() {
  const [datos, setDatos] = useState<DatosFormulario>({
    // Datos personales
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
    // Dirección
    calle: "",
    numero: "",
    departamento: "",
    piso: "",
    codPostal: "",
    localidad: "",
    provincia: "",
    pais: "Argentina",
  });

  const [errores, setErrores] = useState<Errores>({});
  const [popup, setPopup] = useState<TipoPopup>(null);
  const [huespedCreado, setHuespedCreado] = useState<{ nombres: string; apellido: string } | null>(null);

  // Regex patterns del backend
  const regexNombre = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/;
  const regexDocumento = /^[a-zA-Z0-9]+$/;
  const regexCuit = /^\d{2}-?\d{8}-?\d{1}$/;
  const regexTelefono = /^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\s./0-9]*$/;
  const regexEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  const regexCalle = /^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\s.,]+$/;
  const regexTexto = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/;
  const regexAlfanumerico = /^[a-zA-Z0-9]+$/;

  const validarFormulario = (): boolean => {
    const nuevosErrores: Errores = {};

    // --- VALIDACIONES DE HUÉSPED ---

    // Apellido
    if (!datos.apellido.trim()) {
      nuevosErrores.apellido = "El apellido es obligatorio";
    } else if (datos.apellido.length < 2 || datos.apellido.length > 50) {
      nuevosErrores.apellido = "El apellido debe tener entre 2 y 50 caracteres";
    } else if (!regexNombre.test(datos.apellido)) {
      nuevosErrores.apellido = "El apellido solo puede contener letras y espacios";
    }

    // Nombres
    if (!datos.nombres.trim()) {
      nuevosErrores.nombres = "El nombre es obligatorio";
    } else if (datos.nombres.length < 2 || datos.nombres.length > 50) {
      nuevosErrores.nombres = "El nombre debe tener entre 2 y 50 caracteres";
    } else if (!regexNombre.test(datos.nombres)) {
      nuevosErrores.nombres = "El nombre solo puede contener letras y espacios";
    }

    // Tipo Documento
    if (!datos.tipoDocumento) {
      nuevosErrores.tipoDocumento = "El tipo de documento es obligatorio";
    }

    // Número de Documento - REGEX DEL BACKEND: ^[a-zA-Z0-9]+$
    if (!datos.nroDocumento.trim()) {
      nuevosErrores.nroDocumento = "El número de documento es obligatorio";
    } else if (datos.nroDocumento.length < 6 || datos.nroDocumento.length > 15) {
      nuevosErrores.nroDocumento = "El documento debe tener entre 6 y 15 caracteres";
    } else if (!regexDocumento.test(datos.nroDocumento)) {
      nuevosErrores.nroDocumento = "El documento no debe contener espacios ni símbolos";
    }

    // CUIT - VALIDACIÓN DE NEGOCIO: Obligatorio para RESPONSABLE_INSCRIPTO
    if (datos.posicionIva === "RESPONSABLE_INSCRIPTO") {
      if (!datos.cuit.trim()) {
        nuevosErrores.cuit = "El CUIT es obligatorio para Responsables Inscriptos";
      } else if (!regexCuit.test(datos.cuit)) {
        nuevosErrores.cuit = "El CUIT debe tener 11 dígitos (con o sin guiones)";
      }
    } else if (datos.cuit.trim() && !regexCuit.test(datos.cuit)) {
      nuevosErrores.cuit = "El CUIT debe tener 11 dígitos (con o sin guiones)";
    }

    // Posición IVA
    if (!datos.posicionIva) {
      nuevosErrores.posicionIva = "La posición frente al IVA es obligatoria";
    }

    // Fecha de Nacimiento
    if (!datos.fechaNacimiento) {
      nuevosErrores.fechaNacimiento = "La fecha de nacimiento es obligatoria";
    } else {
      const fechaNac = new Date(datos.fechaNacimiento);
      const hoy = new Date();
      if (fechaNac >= hoy) {
        nuevosErrores.fechaNacimiento = "La fecha de nacimiento debe ser anterior a hoy";
      }
    }

    // Nacionalidad
    if (!datos.nacionalidad.trim()) {
      nuevosErrores.nacionalidad = "La nacionalidad es obligatoria";
    }

    // Email
    if (!datos.email.trim()) {
      nuevosErrores.email = "El email es obligatorio";
    } else if (!regexEmail.test(datos.email)) {
      nuevosErrores.email = "Formato de email inválido";
    }

    // Teléfono
    if (!datos.telefono.trim()) {
      nuevosErrores.telefono = "El teléfono es obligatorio";
    } else {
      let telefonoNormalizado = datos.telefono.trim();
      if (!telefonoNormalizado.startsWith("+")) {
        telefonoNormalizado = "+54 " + telefonoNormalizado;
        setDatos((prev) => ({ ...prev, telefono: telefonoNormalizado }));
      }
      if (!regexTelefono.test(telefonoNormalizado)) {
        nuevosErrores.telefono = "Formato de teléfono inválido";
      }
    }

    // --- VALIDACIONES DE DIRECCIÓN ---

    // Calle
    if (!datos.calle.trim()) {
      nuevosErrores.calle = "La calle es obligatoria";
    } else if (!regexCalle.test(datos.calle)) {
      nuevosErrores.calle = "La calle contiene caracteres inválidos";
    } else if (datos.calle.length > 100) {
      nuevosErrores.calle = "La calle no puede superar los 100 caracteres";
    }

    // Número de calle
    if (!datos.numero.trim()) {
      nuevosErrores.numero = "El número de calle es obligatorio";
    } else {
      const numVal = parseInt(datos.numero);
      if (isNaN(numVal) || numVal < 1 || numVal > 99999) {
        nuevosErrores.numero = "El número debe ser entre 1 y 99999";
      }
    }

    // Departamento (opcional)
    if (datos.departamento.trim() && !regexAlfanumerico.test(datos.departamento)) {
      nuevosErrores.departamento = "El departamento solo acepta letras y números";
    } else if (datos.departamento.length > 5) {
      nuevosErrores.departamento = "El departamento es muy largo";
    }

    // Piso (opcional)
    if (datos.piso.trim() && !regexAlfanumerico.test(datos.piso)) {
      nuevosErrores.piso = "El piso solo acepta letras y números";
    } else if (datos.piso.length > 5) {
      nuevosErrores.piso = "El piso es muy largo";
    }

    // Código Postal
    if (!datos.codPostal.trim()) {
      nuevosErrores.codPostal = "El código postal es obligatorio";
    } else {
      const codVal = parseInt(datos.codPostal);
      if (isNaN(codVal) || codVal < 1000 || codVal > 9999) {
        nuevosErrores.codPostal = "El código postal debe ser entre 1000 y 9999";
      }
    }

    // Localidad
    if (!datos.localidad.trim()) {
      nuevosErrores.localidad = "La localidad es obligatoria";
    } else if (!regexTexto.test(datos.localidad)) {
      nuevosErrores.localidad = "La localidad solo puede contener letras y espacios";
    }

    // Provincia
    if (!datos.provincia.trim()) {
      nuevosErrores.provincia = "La provincia es obligatoria";
    } else if (!regexTexto.test(datos.provincia)) {
      nuevosErrores.provincia = "La provincia solo puede contener letras y espacios";
    }

    // País
    if (!datos.pais.trim()) {
      nuevosErrores.pais = "El país es obligatorio";
    } else if (!regexTexto.test(datos.pais)) {
      nuevosErrores.pais = "El país solo puede contener letras y espacios";
    }

    setErrores(nuevosErrores);
    return Object.keys(nuevosErrores).length === 0;
  };

  const handleCancelar = () => {
    setPopup("confirmacion_cancelar");
  };

  const handleConfirmarCancelacion = () => {
    setPopup(null);
    // Redirigir al menú
    window.location.href = "/";
  };

  const handleRechazarCancelacion = () => {
    setPopup(null);
  };

  const handleAceptar = () => {
    if (!validarFormulario()) {
      return;
    }

    // Simular verificación de duplicado DNI
    // En producción, esto sería una llamada al backend
    const dniExistente = false; // Cambiar a true para simular duplicado

    if (dniExistente) {
      setPopup("duplicado_dni");
    } else {
      // Proceder a crear el huésped
      crearHuesped();
    }
  };

  const handleContinuarConDuplicado = () => {
    setPopup(null);
    crearHuesped();
  };

  const handleCorregirDatos = () => {
    setPopup(null);
    // Focus en tipo y número de documento
    setTimeout(() => {
      const tipoDocInput = document.querySelector(
        'select[value="' + datos.tipoDocumento + '"]'
      ) as HTMLSelectElement;
      tipoDocInput?.focus();
    }, 0);
  };

  const crearHuesped = () => {
    // Simular creación en backend
    setHuespedCreado({
      nombres: datos.nombres,
      apellido: datos.apellido,
    });
    setPopup("exito");
  };

  const handleAceptarExito = () => {
    setPopup(null);
    resetearFormulario();
  };

  const handleVolver = () => {
    setPopup(null);
    window.location.href = "/";
  };

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
    });
    setErrores({});
    setHuespedCreado(null);
  };

  return (
    <div className="min-h-screen bg-slate-950 text-white">
      <div className="max-w-6xl mx-auto p-8">
        {/* Header */}
        <div className="mb-8">
          <Link
            href="/"
            className="text-amber-400 hover:text-amber-300 text-sm flex items-center gap-2"
          >
            ← Volver al menú
          </Link>
          <h1 className="text-4xl font-bold text-amber-400 mt-4">Alta de Huésped</h1>
        </div>

        {/* Formulario */}
        <div className="bg-slate-900 border border-slate-700 rounded-lg p-8">
          {/* Datos Personales */}
          <div className="mb-8">
            <h2 className="text-2xl font-semibold mb-6 text-amber-400">Datos Personales</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-medium mb-2">Apellido *</label>
                <input
                  type="text"
                  value={datos.apellido}
                  onChange={(e) => {
                    const valor = e.target.value;
                    setDatos({ ...datos, apellido: valor });
                    
                    // Validar en tiempo real
                    if (!valor.trim()) {
                      setErrores({ ...errores, apellido: "El apellido es obligatorio" });
                    } else if (valor.length < 2 || valor.length > 50) {
                      setErrores({ ...errores, apellido: "El apellido debe tener entre 2 y 50 caracteres" });
                    } else if (!/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/.test(valor)) {
                      setErrores({ ...errores, apellido: "El apellido solo puede contener letras y espacios" });
                    } else {
                      setErrores({ ...errores, apellido: undefined });
                    }
                  }}
                  placeholder="Ej: González"
                  className={`w-full px-4 py-2 bg-slate-800 border rounded text-white focus:border-amber-400 focus:outline-none ${
                    errores.apellido ? "border-red-500" : "border-slate-600"
                  }`}
                />
                {errores.apellido && (
                  <p className="text-red-400 text-xs mt-1">{errores.apellido}</p>
                )}
              </div>
              <div>
                <label className="block text-sm font-medium mb-2">Nombres *</label>
                <input
                  type="text"
                  value={datos.nombres}
                  onChange={(e) => {
                    const valor = e.target.value;
                    setDatos({ ...datos, nombres: valor });
                    
                    // Validar en tiempo real
                    if (!valor.trim()) {
                      setErrores({ ...errores, nombres: "El nombre es obligatorio" });
                    } else if (valor.length < 2 || valor.length > 50) {
                      setErrores({ ...errores, nombres: "El nombre debe tener entre 2 y 50 caracteres" });
                    } else if (!/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/.test(valor)) {
                      setErrores({ ...errores, nombres: "El nombre solo puede contener letras y espacios" });
                    } else {
                      setErrores({ ...errores, nombres: undefined });
                    }
                  }}
                  placeholder="Ej: Juan Carlos"
                  className={`w-full px-4 py-2 bg-slate-800 border rounded text-white focus:border-amber-400 focus:outline-none ${
                    errores.nombres ? "border-red-500" : "border-slate-600"
                  }`}
                />
                {errores.nombres && (
                  <p className="text-red-400 text-xs mt-1">{errores.nombres}</p>
                )}
              </div>
              <div>
                <label className="block text-sm font-medium mb-2">Tipo de Documento *</label>
                <select
                  value={datos.tipoDocumento}
                  onChange={(e) => {
                    setDatos({ ...datos, tipoDocumento: e.target.value, nroDocumento: "" });
                    setErrores({ ...errores, tipoDocumento: undefined, nroDocumento: undefined });
                  }}
                  className={`w-full px-4 py-2 bg-slate-800 border rounded text-white focus:border-amber-400 focus:outline-none ${
                    errores.tipoDocumento ? "border-red-500" : "border-slate-600"
                  }`}
                >
                  <option value="">Seleccione...</option>
                  <option value="DNI">DNI</option>
                  <option value="PASAPORTE">Pasaporte</option>
                  <option value="LC">LC</option>
                  <option value="LE">LE</option>
                  <option value="OTRO">Otro</option>
                </select>
                {errores.tipoDocumento && (
                  <p className="text-red-400 text-xs mt-1">{errores.tipoDocumento}</p>
                )}
              </div>
              <div>
                <label className="block text-sm font-medium mb-2">Nro. Documento *</label>
                <input
                  type="text"
                  value={datos.nroDocumento}
                  onChange={(e) => {
                    const valor = e.target.value;
                    setDatos({ ...datos, nroDocumento: valor });
                    
                    // Validar en tiempo real - REGEX DEL BACKEND: ^[a-zA-Z0-9]+$
                    if (!valor.trim()) {
                      setErrores({ ...errores, nroDocumento: "El número de documento es obligatorio" });
                    } else if (valor.length < 6 || valor.length > 15) {
                      setErrores({ ...errores, nroDocumento: "El documento debe tener entre 6 y 15 caracteres" });
                    } else if (!/^[a-zA-Z0-9]+$/.test(valor)) {
                      setErrores({ ...errores, nroDocumento: "El documento no debe contener espacios ni símbolos" });
                    } else {
                      setErrores({ ...errores, nroDocumento: undefined });
                    }
                  }}
                  placeholder="Alfanumérico sin espacios"
                  className={`w-full px-4 py-2 bg-slate-800 border rounded text-white focus:border-amber-400 focus:outline-none ${
                    errores.nroDocumento ? "border-red-500" : "border-slate-600"
                  }`}
                />
                {errores.nroDocumento && (
                  <p className="text-red-400 text-xs mt-1">{errores.nroDocumento}</p>
                )}
              </div>
              <div>
                <label className="block text-sm font-medium mb-2">Fecha de Nacimiento *</label>
                <input
                  type="date"
                  value={datos.fechaNacimiento}
                  max={new Date().toISOString().split('T')[0]}
                  onChange={(e) => {
                    const valor = e.target.value;
                    setDatos({ ...datos, fechaNacimiento: valor });
                    
                    // Validar en tiempo real
                    if (!valor) {
                      setErrores({ ...errores, fechaNacimiento: "La fecha de nacimiento es obligatoria" });
                    } else {
                      const fechaNac = new Date(valor);
                      const hoy = new Date();
                      if (fechaNac >= hoy) {
                        setErrores({ ...errores, fechaNacimiento: "La fecha de nacimiento debe ser anterior a hoy" });
                      } else {
                        setErrores({ ...errores, fechaNacimiento: undefined });
                      }
                    }
                  }}
                  className={`w-full px-4 py-2 bg-slate-800 border rounded text-white focus:border-amber-400 focus:outline-none ${
                    errores.fechaNacimiento ? "border-red-500" : "border-slate-600"
                  }`}
                />
                {errores.fechaNacimiento && (
                  <p className="text-red-400 text-xs mt-1">{errores.fechaNacimiento}</p>
                )}
              </div>
              <div>
                <label className="block text-sm font-medium mb-2">Nacionalidad *</label>
                <input
                  type="text"
                  value={datos.nacionalidad}
                  onChange={(e) => {
                    const valor = e.target.value;
                    setDatos({ ...datos, nacionalidad: valor });
                    
                    // Validar en tiempo real
                    if (!valor.trim()) {
                      setErrores({ ...errores, nacionalidad: "La nacionalidad es obligatoria" });
                    } else if (!/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/.test(valor)) {
                      setErrores({ ...errores, nacionalidad: "La nacionalidad solo puede contener letras y espacios" });
                    } else {
                      setErrores({ ...errores, nacionalidad: undefined });
                    }
                  }}
                  placeholder="Ej: Argentina"
                  className={`w-full px-4 py-2 bg-slate-800 border rounded text-white focus:border-amber-400 focus:outline-none ${
                    errores.nacionalidad ? "border-red-500" : "border-slate-600"
                  }`}
                />
                {errores.nacionalidad && (
                  <p className="text-red-400 text-xs mt-1">{errores.nacionalidad}</p>
                )}
              </div>
              <div>
                <label className="block text-sm font-medium mb-2">Posición frente al IVA *</label>
                <select
                  value={datos.posicionIva}
                  onChange={(e) => {
                    const valor = e.target.value;
                    setDatos({ ...datos, posicionIva: valor });
                    
                    // Validación de negocio: RESPONSABLE_INSCRIPTO requiere CUIT
                    if (valor === "RESPONSABLE_INSCRIPTO" && !datos.cuit.trim()) {
                      setErrores({ ...errores, posicionIva: undefined, cuit: "El CUIT es obligatorio para Responsables Inscriptos" });
                    } else {
                      // Limpiar error de CUIT si cambia a otro tipo que no sea RESPONSABLE_INSCRIPTO
                      setErrores({ ...errores, posicionIva: undefined, cuit: undefined });
                    }
                  }}
                  className={`w-full px-4 py-2 bg-slate-800 border rounded text-white focus:border-amber-400 focus:outline-none ${
                    errores.posicionIva ? "border-red-500" : "border-slate-600"
                  }`}
                >
                  <option value="CONSUMIDOR_FINAL">Consumidor Final</option>
                  <option value="MONOTRIBUTISTA">Monotributista</option>
                  <option value="RESPONSABLE_INSCRIPTO">Responsable Inscripto</option>
                  <option value="EXENTO">Exento</option>
                </select>
                {errores.posicionIva && (
                  <p className="text-red-400 text-xs mt-1">{errores.posicionIva}</p>
                )}
              </div>
              <div>
                <label className="block text-sm font-medium mb-2">
                  CUIT {datos.posicionIva === "RESPONSABLE_INSCRIPTO" && "*"}
                </label>
                <input
                  type="text"
                  value={datos.cuit}
                  onChange={(e) => {
                    const valor = e.target.value;
                    setDatos({ ...datos, cuit: valor });
                    
                    // Validar en tiempo real - REGEX DEL BACKEND: ^\d{2}-?\d{8}-?\d{1}$
                    if (datos.posicionIva === "RESPONSABLE_INSCRIPTO" && !valor.trim()) {
                      setErrores({ ...errores, cuit: "El CUIT es obligatorio para Responsables Inscriptos" });
                    } else if (valor.trim() && !/^\d{2}-?\d{8}-?\d{1}$/.test(valor)) {
                      setErrores({ ...errores, cuit: "El CUIT debe tener 11 dígitos (con o sin guiones)" });
                    } else {
                      setErrores({ ...errores, cuit: undefined });
                    }
                  }}
                  placeholder="Ej: 20-12345678-9"
                  className={`w-full px-4 py-2 bg-slate-800 border rounded text-white focus:border-amber-400 focus:outline-none ${
                    errores.cuit ? "border-red-500" : "border-slate-600"
                  }`}
                />
                {errores.cuit && (
                  <p className="text-red-400 text-xs mt-1">{errores.cuit}</p>
                )}
                <p className="text-xs text-slate-400 mt-1">
                  {datos.posicionIva === "RESPONSABLE_INSCRIPTO" ? "Obligatorio - 11 dígitos con o sin guiones" : "Opcional - 11 dígitos con o sin guiones"}
                </p>
              </div>
            </div>
          </div>

          {/* Datos de Contacto */}
          <div className="mb-8">
            <h2 className="text-2xl font-semibold mb-6 text-amber-400">Datos de Contacto</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-medium mb-2">Email *</label>
                <input
                  type="email"
                  value={datos.email}
                  onChange={(e) => {
                    const valor = e.target.value;
                    setDatos({ ...datos, email: valor });
                    
                    // Validar en tiempo real
                    if (!valor.trim()) {
                      setErrores({ ...errores, email: "El email es obligatorio" });
                    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(valor)) {
                      setErrores({ ...errores, email: "Formato de email inválido" });
                    } else {
                      setErrores({ ...errores, email: undefined });
                    }
                  }}
                  placeholder="Ej: ejemplo@email.com"
                  className={`w-full px-4 py-2 bg-slate-800 border rounded text-white focus:border-amber-400 focus:outline-none ${
                    errores.email ? "border-red-500" : "border-slate-600"
                  }`}
                />
                {errores.email && (
                  <p className="text-red-400 text-xs mt-1">{errores.email}</p>
                )}
              </div>
              <div>
                <label className="block text-sm font-medium mb-2">Teléfono *</label>
                <input
                  type="tel"
                  value={datos.telefono}
                  onChange={(e) => {
                    const valor = e.target.value;
                    setDatos({ ...datos, telefono: valor });
                    
                    // Validar en tiempo real
                    if (!valor.trim()) {
                      setErrores({ ...errores, telefono: "El teléfono es obligatorio" });
                    } else {
                      let telefonoNormalizado = valor.trim();
                      if (!telefonoNormalizado.startsWith("+")) {
                        telefonoNormalizado = "+54 " + telefonoNormalizado;
                      }
                      if (!/^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\s./0-9]*$/.test(telefonoNormalizado)) {
                        setErrores({ ...errores, telefono: "Formato de teléfono inválido" });
                      } else {
                        setErrores({ ...errores, telefono: undefined });
                      }
                    }
                  }}
                  placeholder="+54 11 1234-5678 o 11 1234-5678"
                  className={`w-full px-4 py-2 bg-slate-800 border rounded text-white focus:border-amber-400 focus:outline-none ${
                    errores.telefono ? "border-red-500" : "border-slate-600"
                  }`}
                />
                {errores.telefono && (
                  <p className="text-red-400 text-xs mt-1">{errores.telefono}</p>
                )}
                <p className="text-xs text-slate-400 mt-1">Si no incluye +, se asume +54 (Argentina)</p>
              </div>
            </div>
          </div>

          {/* Dirección */}
          <div className="mb-8">
            <h2 className="text-2xl font-semibold mb-6 text-amber-400">Dirección</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-medium mb-2">Calle *</label>
                <input
                  type="text"
                  value={datos.calle}
                  onChange={(e) => {
                    const valor = e.target.value;
                    setDatos({ ...datos, calle: valor });
                    
                    // Validar en tiempo real
                    if (!valor.trim()) {
                      setErrores({ ...errores, calle: "La calle es obligatoria" });
                    } else if (!/^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\s.,]+$/.test(valor)) {
                      setErrores({ ...errores, calle: "La calle contiene caracteres inválidos" });
                    } else if (valor.length > 100) {
                      setErrores({ ...errores, calle: "La calle no puede superar los 100 caracteres" });
                    } else {
                      setErrores({ ...errores, calle: undefined });
                    }
                  }}
                  placeholder="Ej: Av. Corrientes"
                  className={`w-full px-4 py-2 bg-slate-800 border rounded text-white focus:border-amber-400 focus:outline-none ${
                    errores.calle ? "border-red-500" : "border-slate-600"
                  }`}
                />
                {errores.calle && (
                  <p className="text-red-400 text-xs mt-1">{errores.calle}</p>
                )}
              </div>
              <div>
                <label className="block text-sm font-medium mb-2">Número *</label>
                <input
                  type="text"
                  value={datos.numero}
                  onChange={(e) => {
                    const valor = e.target.value;
                    setDatos({ ...datos, numero: valor });
                    
                    // Validar en tiempo real
                    if (!valor.trim()) {
                      setErrores({ ...errores, numero: "El número de calle es obligatorio" });
                    } else if (!/^[0-9]+$/.test(valor)) {
                      setErrores({ ...errores, numero: "El número solo puede contener dígitos" });
                    } else {
                      const numVal = parseInt(valor);
                      if (numVal < 1 || numVal > 99999) {
                        setErrores({ ...errores, numero: "El número debe ser entre 1 y 99999" });
                      } else {
                        setErrores({ ...errores, numero: undefined });
                      }
                    }
                  }}
                  placeholder="Ej: 1234"
                  className={`w-full px-4 py-2 bg-slate-800 border rounded text-white focus:border-amber-400 focus:outline-none ${
                    errores.numero ? "border-red-500" : "border-slate-600"
                  }`}
                />
                {errores.numero && (
                  <p className="text-red-400 text-xs mt-1">{errores.numero}</p>
                )}
              </div>
              <div>
                <label className="block text-sm font-medium mb-2">Piso</label>
                <input
                  type="text"
                  value={datos.piso}
                  onChange={(e) => {
                    const valor = e.target.value;
                    setDatos({ ...datos, piso: valor });
                    
                    // Validar en tiempo real - REGEX DEL BACKEND: ^[a-zA-Z0-9]+$
                    if (valor.trim() && !/^[a-zA-Z0-9]+$/.test(valor)) {
                      setErrores({ ...errores, piso: "El piso solo acepta letras y números" });
                    } else if (valor.length > 5) {
                      setErrores({ ...errores, piso: "El piso es muy largo" });
                    } else {
                      setErrores({ ...errores, piso: undefined });
                    }
                  }}
                  placeholder="Ej: 3, PB"
                  className={`w-full px-4 py-2 bg-slate-800 border rounded text-white focus:border-amber-400 focus:outline-none ${
                    errores.piso ? "border-red-500" : "border-slate-600"
                  }`}
                />
                {errores.piso && (
                  <p className="text-red-400 text-xs mt-1">{errores.piso}</p>
                )}
              </div>
              <div>
                <label className="block text-sm font-medium mb-2">Departamento</label>
                <input
                  type="text"
                  value={datos.departamento}
                  onChange={(e) => {
                    const valor = e.target.value;
                    setDatos({ ...datos, departamento: valor });
                    
                    // Validar en tiempo real - REGEX DEL BACKEND: ^[a-zA-Z0-9]+$
                    if (valor.trim() && !/^[a-zA-Z0-9]+$/.test(valor)) {
                      setErrores({ ...errores, departamento: "El departamento solo acepta letras y números (Ej: A, 2, PB)" });
                    } else if (valor.length > 5) {
                      setErrores({ ...errores, departamento: "El departamento es muy largo" });
                    } else {
                      setErrores({ ...errores, departamento: undefined });
                    }
                  }}
                  placeholder="Ej: A, 12"
                  className={`w-full px-4 py-2 bg-slate-800 border rounded text-white focus:border-amber-400 focus:outline-none ${
                    errores.departamento ? "border-red-500" : "border-slate-600"
                  }`}
                />
                {errores.departamento && (
                  <p className="text-red-400 text-xs mt-1">{errores.departamento}</p>
                )}
              </div>
              <div>
                <label className="block text-sm font-medium mb-2">Código Postal *</label>
                <input
                  type="text"
                  value={datos.codPostal}
                  onChange={(e) => {
                    const valor = e.target.value;
                    setDatos({ ...datos, codPostal: valor });
                    
                    // Validar en tiempo real
                    if (!valor.trim()) {
                      setErrores({ ...errores, codPostal: "El código postal es obligatorio" });
                    } else {
                      const codVal = parseInt(valor);
                      if (isNaN(codVal) || codVal < 1000 || codVal > 9999) {
                        setErrores({ ...errores, codPostal: "El código postal debe ser entre 1000 y 9999" });
                      } else {
                        setErrores({ ...errores, codPostal: undefined });
                      }
                    }
                  }}
                  placeholder="Ej: 1428"
                  className={`w-full px-4 py-2 bg-slate-800 border rounded text-white focus:border-amber-400 focus:outline-none ${
                    errores.codPostal ? "border-red-500" : "border-slate-600"
                  }`}
                />
                {errores.codPostal && (
                  <p className="text-red-400 text-xs mt-1">{errores.codPostal}</p>
                )}
              </div>
              <div>
                <label className="block text-sm font-medium mb-2">Localidad *</label>
                <input
                  type="text"
                  value={datos.localidad}
                  onChange={(e) => {
                    const valor = e.target.value;
                    setDatos({ ...datos, localidad: valor });
                    
                    // Validar en tiempo real
                    if (!valor.trim()) {
                      setErrores({ ...errores, localidad: "La localidad es obligatoria" });
                    } else if (!/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/.test(valor)) {
                      setErrores({ ...errores, localidad: "La localidad solo puede contener letras y espacios" });
                    } else {
                      setErrores({ ...errores, localidad: undefined });
                    }
                  }}
                  placeholder="Ej: Capital Federal"
                  className={`w-full px-4 py-2 bg-slate-800 border rounded text-white focus:border-amber-400 focus:outline-none ${
                    errores.localidad ? "border-red-500" : "border-slate-600"
                  }`}
                />
                {errores.localidad && (
                  <p className="text-red-400 text-xs mt-1">{errores.localidad}</p>
                )}
              </div>
              <div>
                <label className="block text-sm font-medium mb-2">Provincia *</label>
                <input
                  type="text"
                  value={datos.provincia}
                  onChange={(e) => {
                    const valor = e.target.value;
                    setDatos({ ...datos, provincia: valor });
                    
                    // Validar en tiempo real
                    if (!valor.trim()) {
                      setErrores({ ...errores, provincia: "La provincia es obligatoria" });
                    } else if (!/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/.test(valor)) {
                      setErrores({ ...errores, provincia: "La provincia solo puede contener letras y espacios" });
                    } else {
                      setErrores({ ...errores, provincia: undefined });
                    }
                  }}
                  placeholder="Ej: Buenos Aires"
                  className={`w-full px-4 py-2 bg-slate-800 border rounded text-white focus:border-amber-400 focus:outline-none ${
                    errores.provincia ? "border-red-500" : "border-slate-600"
                  }`}
                />
                {errores.provincia && (
                  <p className="text-red-400 text-xs mt-1">{errores.provincia}</p>
                )}
              </div>
              <div>
                <label className="block text-sm font-medium mb-2">País *</label>
                <input
                  type="text"
                  value={datos.pais}
                  onChange={(e) => {
                    const valor = e.target.value;
                    setDatos({ ...datos, pais: valor });
                    
                    // Validar en tiempo real
                    if (!valor.trim()) {
                      setErrores({ ...errores, pais: "El país es obligatorio" });
                    } else if (!/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/.test(valor)) {
                      setErrores({ ...errores, pais: "El país solo puede contener letras y espacios" });
                    } else {
                      setErrores({ ...errores, pais: undefined });
                    }
                  }}
                  placeholder="Ej: Argentina"
                  className={`w-full px-4 py-2 bg-slate-800 border rounded text-white focus:border-amber-400 focus:outline-none ${
                    errores.pais ? "border-red-500" : "border-slate-600"
                  }`}
                />
                {errores.pais && (
                  <p className="text-red-400 text-xs mt-1">{errores.pais}</p>
                )}
              </div>
            </div>
          </div>

          {/* Botones */}
          <div className="flex gap-4">
            <button
              onClick={handleCancelar}
              className="flex-1 bg-slate-700 hover:bg-slate-600 text-white font-semibold py-3 px-4 rounded transition"
            >
              Cancelar
            </button>
            <button
              onClick={handleAceptar}
              className="flex-1 bg-amber-400 hover:bg-amber-500 text-slate-950 font-semibold py-3 px-4 rounded transition"
            >
              Aceptar
            </button>
          </div>
        </div>
      </div>

      {/* POPUPS */}

      {/* Popup: Confirmación de Cancelación */}
      {popup === "confirmacion_cancelar" && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-slate-900 border border-slate-700 rounded-lg p-8 max-w-md">
            <h3 className="text-xl font-semibold mb-4 text-amber-400">Confirmar cancelación</h3>
            <p className="text-slate-300 mb-6">
              ¿Está seguro de que desea cancelar? Se perderán todos los datos ingresados.
            </p>
            <div className="flex gap-4">
              <button
                onClick={handleRechazarCancelacion}
                className="flex-1 bg-slate-700 hover:bg-slate-600 text-white font-semibold py-2 px-4 rounded transition"
              >
                No
              </button>
              <button
                onClick={handleConfirmarCancelacion}
                className="flex-1 bg-red-600 hover:bg-red-700 text-white font-semibold py-2 px-4 rounded transition"
              >
                Sí
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Popup: Duplicado DNI */}
      {popup === "duplicado_dni" && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-slate-900 border border-amber-500 rounded-lg p-8 max-w-md">
            <h3 className="text-xl font-semibold mb-4 text-amber-400">⚠️ ¡CUIDADO!</h3>
            <p className="text-slate-300 mb-6">
              El tipo y número de documento ya existen en el sistema.
            </p>
            <div className="flex gap-4">
              <button
                onClick={handleCorregirDatos}
                className="flex-1 bg-slate-700 hover:bg-slate-600 text-white font-semibold py-2 px-4 rounded transition"
              >
                Corregir
              </button>
              <button
                onClick={handleContinuarConDuplicado}
                className="flex-1 bg-amber-400 hover:bg-amber-500 text-slate-950 font-semibold py-2 px-4 rounded transition"
              >
                Aceptar igualmente
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Popup: Éxito */}
      {popup === "exito" && huespedCreado && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-slate-900 border border-green-500 rounded-lg p-8 max-w-md">
            <h3 className="text-xl font-semibold mb-4 text-green-400">✓ Éxito</h3>
            <p className="text-slate-300 mb-6">
              El usuario <span className="font-semibold">{huespedCreado.nombres} {huespedCreado.apellido}</span> ha sido satisfactoriamente cargado en el sistema.
            </p>
            <p className="text-slate-300 mb-6">¿Desea cargar otro?</p>
            <div className="flex gap-4">
              <button
                onClick={handleVolver}
                className="flex-1 bg-slate-700 hover:bg-slate-600 text-white font-semibold py-2 px-4 rounded transition"
              >
                No
              </button>
              <button
                onClick={handleAceptarExito}
                className="flex-1 bg-green-600 hover:bg-green-700 text-white font-semibold py-2 px-4 rounded transition"
              >
                Sí
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
