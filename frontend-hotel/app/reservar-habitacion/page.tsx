"use client";

import Link from "next/link";
import { useState } from "react";

interface HabitacionEstado {
  id: string;
  numero: string;
  tipo: string;
  comodidad: "Simple" | "Doble" | "Triple" | "Suite";
  capacidad: number;
  estado: "DISPONIBLE" | "RESERVADA" | "OCUPADA";
  precioNoche: number;
}

interface SeleccionHabitacion {
  habitacionId: string;
  diaInicio: number; // índice del día en el rango
  diaFin: number;
}

interface DatosHuesped {
  apellido: string;
  nombres: string;
  telefono: string;
}

const HABITACIONES_MOCK: HabitacionEstado[] = [
  { id: "1", numero: "101", tipo: "Doble", comodidad: "Doble", capacidad: 2, estado: "DISPONIBLE", precioNoche: 120 },
  { id: "2", numero: "102", tipo: "Simple", comodidad: "Simple", capacidad: 1, estado: "DISPONIBLE", precioNoche: 80 },
  { id: "3", numero: "103", tipo: "Suite", comodidad: "Suite", capacidad: 4, estado: "RESERVADA", precioNoche: 200 },
  { id: "4", numero: "104", tipo: "Doble", comodidad: "Doble", capacidad: 2, estado: "DISPONIBLE", precioNoche: 120 },
  { id: "5", numero: "201", tipo: "Triple", comodidad: "Triple", capacidad: 3, estado: "OCUPADA", precioNoche: 150 },
  { id: "6", numero: "202", tipo: "Doble", comodidad: "Doble", capacidad: 2, estado: "DISPONIBLE", precioNoche: 120 },
];

const COMODIDADES_ORDEN = ["Simple", "Doble", "Triple", "Suite"];

type Paso = "fechaDesde" | "fechaHasta" | "grilla" | "datosHuesped" | "confirmacion";

export default function ReservarHabitacion() {
  const [paso, setPaso] = useState<Paso>("fechaDesde");
  const [fechaDesde, setFechaDesde] = useState("");
  const [fechaHasta, setFechaHasta] = useState("");
  const [errorFecha, setErrorFecha] = useState("");
  
  // Estado de selección de habitaciones
  const [selecciones, setSelecciones] = useState<SeleccionHabitacion[]>([]);
  const [seleccionActual, setSeleccionActual] = useState<{
    habitacionId: string;
    diaInicio: number | null;
  } | null>(null);

  // Datos del huésped responsable
  const [datosHuesped, setDatosHuesped] = useState<DatosHuesped>({
    apellido: "",
    nombres: "",
    telefono: "",
  });
  const [erroresHuesped, setErroresHuesped] = useState<Partial<DatosHuesped>>({});

  // Validaciones de fechas
  const validarFechaDesde = (): boolean => {
    if (!fechaDesde) {
      setErrorFecha("Debe seleccionar una fecha");
      return false;
    }
    // VALIDACIÓN DEL BACKEND: La fecha de ingreso no puede ser anterior al día de hoy
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);
    const fechaSeleccionada = new Date(fechaDesde);
    if (fechaSeleccionada < hoy) {
      setErrorFecha("La fecha de ingreso no puede ser anterior al día de hoy");
      return false;
    }
    setErrorFecha("");
    return true;
  };

  const validarFechaHasta = (): boolean => {
    if (!fechaHasta) {
      setErrorFecha("Debe seleccionar una fecha");
      return false;
    }
    const desde = new Date(fechaDesde);
    const hasta = new Date(fechaHasta);
    // VALIDACIÓN DEL BACKEND: fechaHasta > fechaDesde
    if (desde >= hasta) {
      setErrorFecha("La fecha 'Hasta' debe ser posterior a la fecha 'Desde'");
      return false;
    }
    // VALIDACIÓN DEL BACKEND: @Future - debe ser futura respecto a hoy
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);
    if (hasta <= hoy) {
      setErrorFecha("La fecha de egreso debe ser futura");
      return false;
    }
    setErrorFecha("");
    return true;
  };

  const handleConfirmarDesde = () => {
    if (validarFechaDesde()) {
      setPaso("fechaHasta");
    }
  };

  const handleConfirmarHasta = () => {
    if (validarFechaHasta()) {
      setPaso("grilla");
    }
  };

  // Generar días del rango
  const generarDias = (): Date[] => {
    if (!fechaDesde || !fechaHasta) return [];
    const desde = new Date(fechaDesde);
    const hasta = new Date(fechaHasta);
    const dias: Date[] = [];
    const actual = new Date(desde);
    while (actual < hasta) {
      dias.push(new Date(actual));
      actual.setDate(actual.getDate() + 1);
    }
    return dias;
  };

  const diasRango = generarDias();

  // Verificar si una celda está disponible para selección
  const esCeldaDisponible = (habitacionId: string, diaIdx: number): boolean => {
    const habitacion = HABITACIONES_MOCK.find(h => h.id === habitacionId);
    if (!habitacion || habitacion.estado !== "DISPONIBLE") return false;

    // Verificar que no esté ya seleccionada
    return !selecciones.some(sel => 
      sel.habitacionId === habitacionId && 
      diaIdx >= sel.diaInicio && 
      diaIdx <= sel.diaFin
    );
  };

  // Manejar click en celda de la grilla
  const handleClickCelda = (habitacionId: string, diaIdx: number) => {
    if (!esCeldaDisponible(habitacionId, diaIdx)) return;

    if (!seleccionActual || seleccionActual.habitacionId !== habitacionId) {
      // Primer click: establecer punto de inicio
      setSeleccionActual({ habitacionId, diaInicio: diaIdx });
    } else {
      // Segundo click: establecer punto de fin y confirmar selección
      const diaInicio = seleccionActual.diaInicio!;
      const diaFin = diaIdx;
      
      // Validar que todos los días en el rango estén disponibles
      const rangoValido = Array.from(
        { length: Math.abs(diaFin - diaInicio) + 1 },
        (_, i) => Math.min(diaInicio, diaFin) + i
      ).every(dia => esCeldaDisponible(habitacionId, dia));

      if (rangoValido) {
        setSelecciones(prev => [...prev, {
          habitacionId,
          diaInicio: Math.min(diaInicio, diaFin),
          diaFin: Math.max(diaInicio, diaFin),
        }]);
        setSeleccionActual(null);
      } else {
        alert("Hay días no disponibles en el rango seleccionado");
        setSeleccionActual(null);
      }
    }
  };

  // Verificar si una celda está seleccionada
  const esCeldaSeleccionada = (habitacionId: string, diaIdx: number): boolean => {
    return selecciones.some(sel =>
      sel.habitacionId === habitacionId &&
      diaIdx >= sel.diaInicio &&
      diaIdx <= sel.diaFin
    );
  };

  // Verificar si es inicio de selección actual
  const esInicioSeleccionActual = (habitacionId: string, diaIdx: number): boolean => {
    return seleccionActual?.habitacionId === habitacionId && 
           seleccionActual.diaInicio === diaIdx;
  };

  // Remover una selección
  const handleRemoverSeleccion = (index: number) => {
    setSelecciones(prev => prev.filter((_, i) => i !== index));
  };

  // Continuar a datos del huésped
  const handleContinuarHuesped = () => {
    if (selecciones.length === 0) {
      alert("Debe seleccionar al menos una habitación con un rango de fechas");
      return;
    }
    setPaso("datosHuesped");
  };

  // Validar datos del huésped
  const validarDatosHuesped = (): boolean => {
    const errores: Partial<DatosHuesped> = {};
    
    // Regex patterns del backend DtoReserva
    const regexNombre = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/;
    const regexTelefono = /^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\s./0-9]*$/;

    // Apellido - BACKEND: @NotBlank, @Pattern(REGEX_NOMBRE)
    if (!datosHuesped.apellido.trim()) {
      errores.apellido = "El apellido es obligatorio";
    } else if (datosHuesped.apellido.length < 2 || datosHuesped.apellido.length > 50) {
      errores.apellido = "El apellido debe tener entre 2 y 50 caracteres";
    } else if (!regexNombre.test(datosHuesped.apellido)) {
      errores.apellido = "El apellido solo puede contener letras y espacios";
    }

    // Nombres - BACKEND: @NotBlank, @Pattern(REGEX_NOMBRE)
    if (!datosHuesped.nombres.trim()) {
      errores.nombres = "El nombre es obligatorio";
    } else if (datosHuesped.nombres.length < 2 || datosHuesped.nombres.length > 50) {
      errores.nombres = "El nombre debe tener entre 2 y 50 caracteres";
    } else if (!regexNombre.test(datosHuesped.nombres)) {
      errores.nombres = "El nombre solo puede contener letras y espacios";
    }

    // Teléfono - BACKEND: @NotBlank, @Pattern(REGEX_TELEFONO)
    if (!datosHuesped.telefono.trim()) {
      errores.telefono = "El teléfono es obligatorio";
    } else {
      // Normalizar teléfono: agregar +54 si no tiene código de país
      let telefonoNormalizado = datosHuesped.telefono.trim();
      if (!telefonoNormalizado.startsWith('+')) {
        telefonoNormalizado = '+54 ' + telefonoNormalizado;
        setDatosHuesped(prev => ({ ...prev, telefono: telefonoNormalizado }));
      }
      if (!regexTelefono.test(telefonoNormalizado)) {
        errores.telefono = "Formato de teléfono inválido";
      } else if (telefonoNormalizado.replace(/[^0-9]/g, '').length < 8) {
        errores.telefono = "El teléfono debe tener al menos 8 dígitos";
      }
    }

    setErroresHuesped(errores);
    return Object.keys(errores).length === 0;
  };

  const handleConfirmarHuesped = () => {
    if (validarDatosHuesped()) {
      setPaso("confirmacion");
    }
  };

  const handleVolverPaso = () => {
    if (paso === "fechaHasta") {
      setErrorFecha("");
      setPaso("fechaDesde");
    } else if (paso === "grilla") {
      setErrorFecha("");
      setPaso("fechaHasta");
    } else if (paso === "datosHuesped") {
      setPaso("grilla");
    } else if (paso === "confirmacion") {
      setPaso("datosHuesped");
    }
  };

  // Calcular precio total
  const calcularTotal = (): number => {
    return selecciones.reduce((total, sel) => {
      const habitacion = HABITACIONES_MOCK.find(h => h.id === sel.habitacionId);
      if (!habitacion) return total;
      const noches = sel.diaFin - sel.diaInicio + 1;
      return total + (habitacion.precioNoche * noches);
    }, 0);
  };

  const getEstadoColor = (estado: string) => {
    switch (estado) {
      case "DISPONIBLE":
        return "bg-green-500";
      case "RESERVADA":
        return "bg-blue-500";
      case "OCUPADA":
        return "bg-red-500";
      default:
        return "bg-slate-600";
    }
  };

  const conteo = {
    disponibles: HABITACIONES_MOCK.filter(h => h.estado === "DISPONIBLE").length,
    reservadas: HABITACIONES_MOCK.filter(h => h.estado === "RESERVADA").length,
    ocupadas: HABITACIONES_MOCK.filter(h => h.estado === "OCUPADA").length,
  };

  return (
    <div className="min-h-screen bg-slate-950 text-white p-8">
      <div className="max-w-7xl mx-auto">
        <Link href="/" className="text-amber-400 hover:text-amber-300 mb-6 inline-block">
          ← Volver
        </Link>

        <h1 className="text-4xl font-bold mb-2 text-amber-400">CU04 - Reservar Habitación</h1>
        <p className="text-slate-300 mb-8">
          Paso {paso === "fechaDesde" ? 1 : paso === "fechaHasta" ? 2 : paso === "grilla" ? 3 : paso === "datosHuesped" ? 4 : 5} de 5
        </p>

        {/* PASO 1: Seleccionar Fecha Desde */}
        {paso === "fechaDesde" && (
          <div className="bg-slate-900 border border-slate-700 rounded-lg p-8 max-w-md">
            <h2 className="text-2xl font-semibold mb-6 text-amber-400">Seleccione fecha de inicio</h2>
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium mb-2">Fecha Desde</label>
                <input
                  type="date"
                  value={fechaDesde}
                  onChange={(e) => {
                    setFechaDesde(e.target.value);
                    setErrorFecha("");
                  }}
                  className="w-full px-4 py-2 bg-slate-800 border border-slate-600 rounded text-white focus:border-amber-400 focus:outline-none"
                />
              </div>
              {errorFecha && (
                <div className="text-red-400 text-sm bg-red-900/20 p-3 rounded border border-red-700">
                  {errorFecha}
                </div>
              )}
              <button
                onClick={handleConfirmarDesde}
                className="w-full mt-6 bg-amber-400 hover:bg-amber-500 text-slate-950 font-semibold py-2 px-4 rounded transition"
              >
                Continuar →
              </button>
            </div>
          </div>
        )}

        {/* PASO 2: Seleccionar Fecha Hasta */}
        {paso === "fechaHasta" && (
          <div className="bg-slate-900 border border-slate-700 rounded-lg p-8 max-w-md">
            <h2 className="text-2xl font-semibold mb-6 text-amber-400">Seleccione fecha de fin</h2>
            <div className="space-y-4">
              <div className="bg-slate-800 rounded-lg p-4 text-sm">
                <p className="text-slate-400">Fecha desde:</p>
                <p className="text-lg font-semibold">{new Date(fechaDesde).toLocaleDateString()}</p>
              </div>
              <div>
                <label className="block text-sm font-medium mb-2">Fecha Hasta</label>
                <input
                  type="date"
                  value={fechaHasta}
                  onChange={(e) => {
                    setFechaHasta(e.target.value);
                    setErrorFecha("");
                  }}
                  className="w-full px-4 py-2 bg-slate-800 border border-slate-600 rounded text-white focus:border-amber-400 focus:outline-none"
                />
              </div>
              {errorFecha && (
                <div className="text-red-400 text-sm bg-red-900/20 p-3 rounded border border-red-700">
                  {errorFecha}
                </div>
              )}
              <div className="flex gap-4 mt-6">
                <button
                  onClick={handleVolverPaso}
                  className="flex-1 bg-slate-700 hover:bg-slate-600 text-white font-semibold py-2 px-4 rounded transition"
                >
                  ← Atrás
                </button>
                <button
                  onClick={handleConfirmarHasta}
                  className="flex-1 bg-amber-400 hover:bg-amber-500 text-slate-950 font-semibold py-2 px-4 rounded transition"
                >
                  Continuar →
                </button>
              </div>
            </div>
          </div>
        )}

        {/* PASO 3: Grilla interactiva (CU05 integrado) */}
        {paso === "grilla" && (
          <div className="space-y-6">
            {/* Resumen de estados */}
            <div className="grid grid-cols-3 gap-4">
              <div className="bg-slate-900 border-l-4 border-green-500 rounded-lg p-4">
                <p className="text-slate-400 text-sm">Disponibles</p>
                <p className="text-3xl font-bold text-green-400">{conteo.disponibles}</p>
              </div>
              <div className="bg-slate-900 border-l-4 border-blue-500 rounded-lg p-4">
                <p className="text-slate-400 text-sm">Reservadas</p>
                <p className="text-3xl font-bold text-blue-400">{conteo.reservadas}</p>
              </div>
              <div className="bg-slate-900 border-l-4 border-red-500 rounded-lg p-4">
                <p className="text-slate-400 text-sm">Ocupadas</p>
                <p className="text-3xl font-bold text-red-400">{conteo.ocupadas}</p>
              </div>
            </div>

            {/* Instrucciones */}
            <div className="bg-blue-900/20 border border-blue-700 rounded-lg p-4">
              <p className="text-blue-200 text-sm">
                <strong>Instrucciones:</strong> Click en una celda para iniciar selección, luego click en otra celda de la misma habitación para finalizar el rango.
                Puede seleccionar múltiples habitaciones con diferentes rangos.
              </p>
            </div>

            {/* Grilla */}
            <div className="bg-slate-900 border border-slate-700 rounded-lg p-6 overflow-x-auto">
              <h2 className="text-xl font-semibold mb-4 text-amber-400">
                Del {new Date(fechaDesde).toLocaleDateString()} al {new Date(fechaHasta).toLocaleDateString()}
              </h2>
              
              <div className="min-w-max">
                <table className="w-full text-sm border-collapse">
                  <thead>
                    <tr className="bg-slate-800">
                      <th className="border border-slate-700 px-4 py-2 text-left font-semibold text-amber-400 w-32">
                        Habitación
                      </th>
                      {diasRango.map((dia, idx) => (
                        <th
                          key={idx}
                          className="border border-slate-700 px-3 py-2 text-center font-semibold text-amber-400 w-24 text-xs"
                        >
                          {dia.toLocaleDateString("es-ES", {
                            month: "short",
                            day: "numeric",
                          })}
                        </th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {COMODIDADES_ORDEN.map((comodidad) => {
                      const habsComodidad = HABITACIONES_MOCK.filter(
                        (h) => h.comodidad === comodidad
                      );
                      return habsComodidad.length > 0
                        ? habsComodidad.map((hab, habIdx) => (
                            <tr
                              key={hab.id}
                              className={habIdx % 2 === 0 ? "bg-slate-800/50" : ""}
                            >
                              <td className="border border-slate-700 px-4 py-2 font-semibold text-slate-200">
                                {habIdx === 0 && (
                                  <div className="font-bold text-amber-400 mb-1">
                                    {comodidad}
                                  </div>
                                )}
                                <div className="text-slate-400 text-sm">
                                  Hab. {hab.numero}
                                </div>
                              </td>
                              {diasRango.map((dia, dayIdx) => {
                                const disponible = esCeldaDisponible(hab.id, dayIdx);
                                const seleccionada = esCeldaSeleccionada(hab.id, dayIdx);
                                const inicioActual = esInicioSeleccionActual(hab.id, dayIdx);

                                return (
                                  <td
                                    key={`${hab.id}-${dayIdx}`}
                                    className="border border-slate-700 px-2 py-2 text-center"
                                  >
                                    <div
                                      onClick={() => handleClickCelda(hab.id, dayIdx)}
                                      className={`rounded px-2 py-1 text-xs font-semibold text-white transition cursor-pointer ${
                                        seleccionada
                                          ? "bg-amber-500 hover:bg-amber-600"
                                          : inicioActual
                                          ? "bg-purple-500 animate-pulse"
                                          : disponible
                                          ? getEstadoColor(hab.estado) + " hover:brightness-110"
                                          : "bg-slate-600 cursor-not-allowed opacity-50"
                                      }`}
                                      title={
                                        seleccionada
                                          ? "Seleccionada - Click para remover"
                                          : inicioActual
                                          ? "Click en otra celda para finalizar"
                                          : disponible
                                          ? "Click para seleccionar"
                                          : hab.estado
                                      }
                                    >
                                      {seleccionada
                                        ? "✓"
                                        : inicioActual
                                        ? "►"
                                        : hab.estado === "DISPONIBLE"
                                        ? "○"
                                        : hab.estado === "RESERVADA"
                                        ? "R"
                                        : "X"}
                                    </div>
                                  </td>
                                );
                              })}
                            </tr>
                          ))
                        : null;
                    })}
                  </tbody>
                </table>
                <div className="mt-4 text-xs text-slate-400 flex gap-4">
                  <span>○ = Disponible</span>
                  <span>✓ = Seleccionada</span>
                  <span>R = Reservada</span>
                  <span>X = Ocupada</span>
                </div>
              </div>
            </div>

            {/* Resumen de selecciones */}
            {selecciones.length > 0 && (
              <div className="bg-slate-900 border border-slate-700 rounded-lg p-6">
                <h3 className="text-lg font-semibold mb-4 text-amber-400">Habitaciones seleccionadas</h3>
                <div className="space-y-2">
                  {selecciones.map((sel, idx) => {
                    const hab = HABITACIONES_MOCK.find(h => h.id === sel.habitacionId);
                    if (!hab) return null;
                    const noches = sel.diaFin - sel.diaInicio + 1;
                    const subtotal = hab.precioNoche * noches;
                    return (
                      <div key={idx} className="flex justify-between items-center bg-slate-800 p-3 rounded">
                        <div>
                          <p className="font-semibold">Habitación {hab.numero} ({hab.tipo})</p>
                          <p className="text-sm text-slate-400">
                            {diasRango[sel.diaInicio]?.toLocaleDateString()} - {diasRango[sel.diaFin]?.toLocaleDateString()} ({noches} noche{noches > 1 ? "s" : ""})
                          </p>
                        </div>
                        <div className="flex items-center gap-4">
                          <p className="text-amber-400 font-semibold">${subtotal}</p>
                          <button
                            onClick={() => handleRemoverSeleccion(idx)}
                            className="text-red-400 hover:text-red-300 text-sm"
                          >
                            ✕ Quitar
                          </button>
                        </div>
                      </div>
                    );
                  })}
                </div>
                <div className="mt-4 pt-4 border-t border-slate-700 flex justify-between items-center">
                  <p className="text-lg font-semibold">Total:</p>
                  <p className="text-2xl font-bold text-amber-400">${calcularTotal()}</p>
                </div>
              </div>
            )}

            <div className="flex gap-4">
              <button
                onClick={handleVolverPaso}
                className="flex-1 bg-slate-700 hover:bg-slate-600 text-white font-semibold py-2 px-4 rounded transition"
              >
                ← Atrás
              </button>
              <button
                onClick={handleContinuarHuesped}
                disabled={selecciones.length === 0}
                className="flex-1 bg-amber-400 hover:bg-amber-500 disabled:bg-slate-600 disabled:cursor-not-allowed text-slate-950 font-semibold py-2 px-4 rounded transition"
              >
                Continuar →
              </button>
            </div>
          </div>
        )}

        {/* PASO 4: Datos del Huésped Responsable */}
        {paso === "datosHuesped" && (
          <div className="bg-slate-900 border border-slate-700 rounded-lg p-8 max-w-2xl">
            <h2 className="text-2xl font-semibold mb-6 text-amber-400">Datos del huésped responsable</h2>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium mb-2">Apellido *</label>
                <input
                  type="text"
                  value={datosHuesped.apellido}
                  onChange={(e) => {
                    const valor = e.target.value;
                    setDatosHuesped({ ...datosHuesped, apellido: valor });
                    
                    // Validar en tiempo real
                    if (!valor.trim()) {
                      setErroresHuesped({ ...erroresHuesped, apellido: "El apellido es obligatorio" });
                    } else if (valor.length < 2 || valor.length > 50) {
                      setErroresHuesped({ ...erroresHuesped, apellido: "El apellido debe tener entre 2 y 50 caracteres" });
                    } else if (!/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/.test(valor)) {
                      setErroresHuesped({ ...erroresHuesped, apellido: "El apellido solo puede contener letras y espacios" });
                    } else {
                      setErroresHuesped({ ...erroresHuesped, apellido: undefined });
                    }
                  }}
                  placeholder="Ej: González"
                  className={`w-full px-4 py-2 bg-slate-800 border rounded text-white focus:border-amber-400 focus:outline-none ${
                    erroresHuesped.apellido ? "border-red-500" : "border-slate-600"
                  }`}
                />
                {erroresHuesped.apellido && (
                  <p className="text-red-400 text-xs mt-1">{erroresHuesped.apellido}</p>
                )}
              </div>
              <div>
                <label className="block text-sm font-medium mb-2">Nombres *</label>
                <input
                  type="text"
                  value={datosHuesped.nombres}
                  onChange={(e) => {
                    const valor = e.target.value;
                    setDatosHuesped({ ...datosHuesped, nombres: valor });
                    
                    // Validar en tiempo real
                    if (!valor.trim()) {
                      setErroresHuesped({ ...erroresHuesped, nombres: "El nombre es obligatorio" });
                    } else if (valor.length < 2 || valor.length > 50) {
                      setErroresHuesped({ ...erroresHuesped, nombres: "El nombre debe tener entre 2 y 50 caracteres" });
                    } else if (!/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/.test(valor)) {
                      setErroresHuesped({ ...erroresHuesped, nombres: "El nombre solo puede contener letras y espacios" });
                    } else {
                      setErroresHuesped({ ...erroresHuesped, nombres: undefined });
                    }
                  }}
                  placeholder="Ej: Juan Carlos"
                  className={`w-full px-4 py-2 bg-slate-800 border rounded text-white focus:border-amber-400 focus:outline-none ${
                    erroresHuesped.nombres ? "border-red-500" : "border-slate-600"
                  }`}
                />
                {erroresHuesped.nombres && (
                  <p className="text-red-400 text-xs mt-1">{erroresHuesped.nombres}</p>
                )}
              </div>
              <div className="md:col-span-2">
                <label className="block text-sm font-medium mb-2">Teléfono *</label>
                <input
                  type="tel"
                  value={datosHuesped.telefono}
                  onChange={(e) => {
                    const valor = e.target.value;
                    setDatosHuesped({ ...datosHuesped, telefono: valor });
                    
                    // Validar en tiempo real
                    if (!valor.trim()) {
                      setErroresHuesped({ ...erroresHuesped, telefono: "El teléfono es obligatorio" });
                    } else {
                      // Normalizar teléfono: agregar +54 si no tiene código de país
                      let telefonoNormalizado = valor.trim();
                      if (!telefonoNormalizado.startsWith('+')) {
                        telefonoNormalizado = '+54 ' + telefonoNormalizado;
                      }
                      // Validar formato y longitud mínima
                      if (!/^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\s./0-9]*$/.test(telefonoNormalizado)) {
                        setErroresHuesped({ ...erroresHuesped, telefono: "Formato de teléfono inválido" });
                      } else if (telefonoNormalizado.replace(/[^0-9]/g, '').length < 8) {
                        setErroresHuesped({ ...erroresHuesped, telefono: "El teléfono debe tener al menos 8 dígitos" });
                      } else {
                        setErroresHuesped({ ...erroresHuesped, telefono: undefined });
                      }
                    }
                  }}
                  placeholder="+54 11 1234-5678 o 11 1234-5678"
                  className={`w-full px-4 py-2 bg-slate-800 border rounded text-white focus:border-amber-400 focus:outline-none ${
                    erroresHuesped.telefono ? "border-red-500" : "border-slate-600"
                  }`}
                />
                {erroresHuesped.telefono && (
                  <p className="text-red-400 text-xs mt-1">{erroresHuesped.telefono}</p>
                )}
                <p className="text-xs text-slate-400 mt-1">Mínimo 8 dígitos. Si no incluye +, se asume +54 (Argentina)</p>
              </div>
            </div>

            <div className="flex gap-4 mt-6">
              <button
                onClick={handleVolverPaso}
                className="flex-1 bg-slate-700 hover:bg-slate-600 text-white font-semibold py-2 px-4 rounded transition"
              >
                ← Atrás
              </button>
              <button
                onClick={handleConfirmarHuesped}
                className="flex-1 bg-amber-400 hover:bg-amber-500 text-slate-950 font-semibold py-2 px-4 rounded transition"
              >
                Continuar →
              </button>
            </div>
          </div>
        )}

        {/* PASO 5: Confirmación */}
        {paso === "confirmacion" && (
          <div className="space-y-6 max-w-2xl">
            <div className="bg-slate-900 border border-slate-700 rounded-lg p-8">
              <h2 className="text-2xl font-semibold mb-8 text-amber-400">Confirmar reserva</h2>

              {/* Datos del huésped */}
              <div className="mb-6">
                <h3 className="text-lg font-semibold mb-3 text-amber-400">Huésped Responsable</h3>
                <div className="bg-slate-800 rounded-lg p-4 space-y-2">
                  <p><span className="text-slate-400">Nombre:</span> {datosHuesped.nombres} {datosHuesped.apellido}</p>
                  <p><span className="text-slate-400">Teléfono:</span> {datosHuesped.telefono}</p>
                </div>
              </div>

              {/* Habitaciones reservadas */}
              <div className="mb-6">
                <h3 className="text-lg font-semibold mb-3 text-amber-400">Habitaciones y Fechas</h3>
                <div className="space-y-2">
                  {selecciones.map((sel, idx) => {
                    const hab = HABITACIONES_MOCK.find(h => h.id === sel.habitacionId);
                    if (!hab) return null;
                    const noches = sel.diaFin - sel.diaInicio + 1;
                    const subtotal = hab.precioNoche * noches;
                    return (
                      <div key={idx} className="bg-slate-800 rounded-lg p-4">
                        <p className="font-semibold">Habitación {hab.numero} - {hab.tipo}</p>
                        <p className="text-sm text-slate-400">
                          {diasRango[sel.diaInicio]?.toLocaleDateString()} - {diasRango[sel.diaFin]?.toLocaleDateString()}
                        </p>
                        <p className="text-amber-400 font-semibold">{noches} noche{noches > 1 ? "s" : ""} × ${hab.precioNoche} = ${subtotal}</p>
                      </div>
                    );
                  })}
                </div>
              </div>

              {/* Total */}
              <div className="bg-amber-400/10 border-2 border-amber-400 rounded-lg p-4">
                <p className="text-slate-400 text-sm">Importe total</p>
                <p className="text-2xl font-bold text-amber-400">${calcularTotal()}</p>
              </div>

              <div className="flex gap-4 mt-8">
                <button
                  onClick={handleVolverPaso}
                  className="flex-1 bg-slate-700 hover:bg-slate-600 text-white font-semibold py-2 px-4 rounded transition"
                >
                  ← Atrás
                </button>
                <button
                  onClick={() => {
                    alert(`Reserva confirmada para ${datosHuesped.nombres} ${datosHuesped.apellido}. Total: $${calcularTotal()}`);
                    // Reset form
                    setPaso("fechaDesde");
                    setFechaDesde("");
                    setFechaHasta("");
                    setSelecciones([]);
                    setDatosHuesped({
                      apellido: "",
                      nombres: "",
                      telefono: "",
                    });
                  }}
                  className="flex-1 bg-green-600 hover:bg-green-700 text-white font-semibold py-2 px-4 rounded transition"
                >
                  ✓ Confirmar reserva
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
