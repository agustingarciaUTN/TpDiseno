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
  id?: string;
  apellido: string;
  nombres: string;
  tipoDocumento: string;
  nroDocumento: string;
}

interface BusquedaHuesped {
  apellido: string;
  nombres: string;
  tipoDocumento: string;
  nroDocumento: string;
}

interface Errores {
  apellido?: string;
  nombres?: string;
  tipoDocumento?: string;
  nroDocumento?: string;
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

type Paso = "fechasGrilla" | "grilla" | "huespedes" | "confirmacion";
type TipoConfirmacion = "reservada" | "duenioReserva" | null;

export default function OcuparHabitacion() {
  const [paso, setPaso] = useState<Paso>("fechasGrilla");
  const [fechaDesdeGrilla, setFechaDesdeGrilla] = useState("");
  const [fechaHastaGrilla, setFechaHastaGrilla] = useState("");
  const [errorFechaGrilla, setErrorFechaGrilla] = useState("");
  const [habitacionSeleccionada, setHabitacionSeleccionada] = useState<HabitacionEstado | null>(null);
  const [fechaCheckIn, setFechaCheckIn] = useState("");
  const [fechaCheckOut, setFechaCheckOut] = useState("");
  const [confirmacionTipo, setConfirmacionTipo] = useState<TipoConfirmacion>(null);
  const [huespedes, setHuespedes] = useState<DatosHuesped[]>([]);
  const [busquedaHuesped, setBusquedaHuesped] = useState<BusquedaHuesped>({
    apellido: "",
    nombres: "",
    tipoDocumento: "",
    nroDocumento: "",
  });
  const [erroresBusqueda, setErroresBusqueda] = useState<Errores>({});
  const [resultadosBusqueda, setResultadosBusqueda] = useState<DatosHuesped[]>([]);
  const [buscando, setBuscando] = useState(false);
  const [mostrarResultados, setMostrarResultados] = useState(false);

  // Estados para la grilla interactiva (estilo CU4)
  const [seleccion, setSeleccion] = useState<SeleccionHabitacion | null>(null);
  const [seleccionActual, setSeleccionActual] = useState<{
    habitacionId: string;
    diaInicio: number | null;
  } | null>(null);

  const validarFechasGrilla = (): boolean => {
    // Establecer fecha desde como hoy automáticamente
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);
    const fechaHoyStr = hoy.toISOString().split("T")[0];
    setFechaDesdeGrilla(fechaHoyStr);

    if (!fechaHastaGrilla) {
      setErrorFechaGrilla("Debe seleccionar la fecha hasta");
      return false;
    }
    const hasta = new Date(fechaHastaGrilla);
    if (hoy >= hasta) {
      setErrorFechaGrilla("La fecha 'Hasta' debe ser posterior a hoy");
      return false;
    }
    setErrorFechaGrilla("");
    return true;
  };



  const validarCampoBusqueda = (campo: keyof BusquedaHuesped, valor: string): string => {
    const regexNombre = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/;
    const regexDocumento = /^[a-zA-Z0-9]+$/;

    switch (campo) {
      case "apellido":
      case "nombres":
        if (!valor.trim()) return ""; // Opcional
        if (valor.length !== 1) return "Debe ingresar solo la primera letra";
        if (!regexNombre.test(valor)) return "Solo puede contener letras";
        return "";

      case "tipoDocumento":
        return ""; // Opcional

      case "nroDocumento":
        if (!busquedaHuesped.tipoDocumento) return ""; // Solo validar si hay tipo
        if (!valor.trim()) return ""; // Opcional
        if (valor.length < 6 || valor.length > 15)
          return "Debe tener entre 6 y 15 caracteres";
        if (!regexDocumento.test(valor))
          return "El documento no debe contener espacios ni símbolos";
        return "";

      default:
        return "";
    }
  };

  const handleConfirmarFechasGrilla = () => {
    if (validarFechasGrilla()) {
      // Resetear selección al cambiar fechas de grilla
      setSeleccion(null);
      setSeleccionActual(null);
      setPaso("grilla");
    }
  };

  // Generar días del rango de grilla
  const generarDias = (): Date[] => {
    if (!fechaDesdeGrilla || !fechaHastaGrilla) return [];
    const desde = new Date(fechaDesdeGrilla);
    const hasta = new Date(fechaHastaGrilla);
    const dias: Date[] = [];
    const actual = new Date(desde);
    while (actual <= hasta) {
      dias.push(new Date(actual));
      actual.setDate(actual.getDate() + 1);
    }
    return dias;
  };

  // Funciones para la grilla interactiva (estilo CU4)
  const esCeldaDisponible = (habitacionId: string, diaIdx: number): boolean => {
    const habitacion = HABITACIONES_MOCK.find(h => h.id === habitacionId);
    // Permitir clicks en habitaciones disponibles y reservadas, solo bloquear ocupadas
    if (!habitacion || habitacion.estado === "OCUPADA") return false;
    // Si hay una selección existente y estamos en esa habitación, no permitir solapamiento
    if (seleccion && seleccion.habitacionId === habitacionId) {
      return !(diaIdx >= seleccion.diaInicio && diaIdx <= seleccion.diaFin);
    }
    return true;
  };

  const esCeldaSeleccionada = (habitacionId: string, diaIdx: number): boolean => {
    if (!seleccion) return false;
    return (
      seleccion.habitacionId === habitacionId &&
      diaIdx >= seleccion.diaInicio &&
      diaIdx <= seleccion.diaFin
    );
  };

  const esInicioSeleccionActual = (habitacionId: string, diaIdx: number): boolean => {
    return (
      seleccionActual?.habitacionId === habitacionId &&
      seleccionActual.diaInicio === diaIdx
    );
  };

  const handleClickCelda = (habitacionId: string, diaIdx: number) => {
    const hab = HABITACIONES_MOCK.find(h => h.id === habitacionId);
    
    // No permitir clicks en habitaciones ocupadas
    if (!hab || hab.estado === "OCUPADA") return;

    if (!seleccionActual || seleccionActual.habitacionId !== habitacionId) {
      // Primer click: establecer punto de inicio (y limpiar cualquier selección previa)
      setSeleccion(null);
      setSeleccionActual({ habitacionId, diaInicio: diaIdx });
    } else {
      // Segundo click: establecer punto de fin y confirmar selección
      const diaInicio = seleccionActual.diaInicio!;
      const diaFin = diaIdx;
      const dias = generarDias();
      const hab = HABITACIONES_MOCK.find(h => h.id === habitacionId);

      if (!hab) return;

      // Crear rango de índices seleccionados
      const rangoDias = Array.from(
        { length: Math.abs(diaFin - diaInicio) + 1 },
        (_, i) => Math.min(diaInicio, diaFin) + i
      );

      // Verificar estado de cada día en el rango
      let diasDisponibles = 0;
      let diasReservados = 0;
      let diasOcupados = 0;

      rangoDias.forEach(diaIdx => {
        // Aquí deberíamos consultar el backend para saber el estado real de cada día
        // Por ahora usamos el estado general de la habitación como aproximación
        if (hab.estado === "DISPONIBLE") diasDisponibles++;
        else if (hab.estado === "RESERVADA") diasReservados++;
        else if (hab.estado === "OCUPADA") diasOcupados++;
      });

      // CASO 1: Habitación parcialmente reservada (algunos días disponibles, otros reservados)
      if (diasDisponibles > 0 && diasReservados > 0) {
        alert("❌ No se puede ocupar esta habitación.\n\nLa habitación está parcialmente reservada en el rango seleccionado.\nPor favor, seleccione otro rango de fechas o una habitación diferente.");
        setSeleccionActual(null);
        setSeleccion(null);
        return;
      }

      // CASO 2: Habitación totalmente reservada
      if (diasReservados === rangoDias.length && diasDisponibles === 0) {
        const seleccionFinal = {
          habitacionId,
          diaInicio: Math.min(diaInicio, diaFin),
          diaFin: Math.max(diaInicio, diaFin),
        };
        setSeleccion(seleccionFinal);
        setSeleccionActual(null);

        // Auto-poblar fechas
        if (dias.length > 0) {
          const fechaInicio = dias[seleccionFinal.diaInicio];
          const fechaFin = dias[seleccionFinal.diaFin];
          setFechaCheckIn(fechaInicio.toISOString().split("T")[0]);
          setFechaCheckOut(fechaFin.toISOString().split("T")[0]);
        }

        setHabitacionSeleccionada(hab);
        setConfirmacionTipo("reservada");
        return;
      }

      // CASO 3: Habitación totalmente disponible (flujo normal)
      if (diasDisponibles === rangoDias.length) {
        const seleccionFinal = {
          habitacionId,
          diaInicio: Math.min(diaInicio, diaFin),
          diaFin: Math.max(diaInicio, diaFin),
        };
        setSeleccion(seleccionFinal);
        setSeleccionActual(null);

        // Auto-poblar fechas
        if (dias.length > 0) {
          const fechaInicio = dias[seleccionFinal.diaInicio];
          const fechaFin = dias[seleccionFinal.diaFin];
          setFechaCheckIn(fechaInicio.toISOString().split("T")[0]);
          setFechaCheckOut(fechaFin.toISOString().split("T")[0]);
        }

        setHabitacionSeleccionada(hab);
        setConfirmacionTipo(null);
        return;
      }

      // Otros casos (ocupada, etc.)
      alert("No se puede seleccionar este rango");
      setSeleccionActual(null);
    }
  };

  const handleRemoverSeleccion = () => {
    setSeleccion(null);
    setSeleccionActual(null);
    setHabitacionSeleccionada(null);
    setFechaCheckIn("");
    setFechaCheckOut("");
  };

  const handleContinuarDesdeGrilla = () => {
    if (!seleccion) {
      alert("Debe seleccionar una habitación y un rango de fechas");
      return;
    }
    // Si hay confirmación pendiente, no avanzar
    if (confirmacionTipo) return;
    
    setPaso("huespedes");
  };

  const handleOcuparIgualmente = () => {
    setConfirmacionTipo("duenioReserva");
  };

  const handleVolverASeleccion = () => {
    setSeleccion(null);
    setSeleccionActual(null);
    setHabitacionSeleccionada(null);
    setConfirmacionTipo(null);
    setFechaCheckIn("");
    setFechaCheckOut("");
  };

  const handleEsDuenioReserva = (esDuenio: boolean) => {
    if (esDuenio) {
      // Continuar con el flujo normal
      setConfirmacionTipo(null);
      setPaso("huespedes");
    } else {
      // Volver a la selección de habitación
      alert("No puede ocupar una reserva que no le pertenece.");
      handleVolverASeleccion();
    }
  };

  const getEstadoColor = (estado: HabitacionEstado["estado"]) => {
    switch (estado) {
      case "DISPONIBLE":
        return "bg-green-600";
      case "RESERVADA":
        return "bg-yellow-600";
      case "OCUPADA":
        return "bg-red-600";
      default:
        return "bg-slate-600";
    }
  };



  const handleChangeBusqueda = (campo: keyof BusquedaHuesped, valor: string) => {
    setBusquedaHuesped({ ...busquedaHuesped, [campo]: valor });
    const error = validarCampoBusqueda(campo, valor);
    setErroresBusqueda({ ...erroresBusqueda, [campo]: error });
  };

  const handleBuscarHuespedes = async () => {
    // Validar todos los campos
    const errores: Errores = {};
    (Object.keys(busquedaHuesped) as Array<keyof BusquedaHuesped>).forEach((key) => {
      const error = validarCampoBusqueda(key, busquedaHuesped[key]);
      if (error) errores[key] = error;
    });

    if (Object.keys(errores).length > 0) {
      setErroresBusqueda(errores);
      return;
    }

    setBuscando(true);
    try {
      // TODO: Llamar al backend GET /huespedes/buscar
      // const response = await fetch(`/api/huespedes/buscar?apellido=${busquedaHuesped.apellido}&...`);
      // const data = await response.json();
      // setResultadosBusqueda(data);
      
      // Simulación temporal
      await new Promise((resolve) => setTimeout(resolve, 800));
      console.log("Buscar con:", busquedaHuesped);
      setResultadosBusqueda([]); // Por ahora vacío hasta que esté el backend
      setMostrarResultados(true);
    } catch (error) {
      console.error("Error en búsqueda:", error);
      alert("Error al buscar huésped");
    } finally {
      setBuscando(false);
    }
  };

  const handleSeleccionarHuesped = (huesped: DatosHuesped) => {
    // Verificar duplicados
    const duplicado = huespedes.some(
      h => h.tipoDocumento === huesped.tipoDocumento && h.nroDocumento === huesped.nroDocumento
    );
    if (duplicado) {
      alert("Este huésped ya fue agregado a la estadía");
      return;
    }
    setHuespedes([...huespedes, huesped]);
    setMostrarResultados(false);
    setBusquedaHuesped({ apellido: "", nombres: "", tipoDocumento: "", nroDocumento: "" });
    setErroresBusqueda({});
  };

  const handleEliminarHuesped = (index: number) => {
    if (index === 0) {
      alert("No se puede eliminar al huésped responsable");
      return;
    }
    setHuespedes(huespedes.filter((_, i) => i !== index));
  };

  const handleContinuarConfirmacion = () => {
    if (huespedes.length === 0) {
      alert("Debe agregar al menos un huésped (responsable)");
      return;
    }
    setPaso("confirmacion");
  };

  const handleConfirmarEstadia = () => {
    alert("Estadía creada exitosamente (simulado)");
  };

  const handleVolverPaso = () => {
    if (paso === "grilla") setPaso("fechasGrilla");
    else if (paso === "huespedes") setPaso("grilla");
    else if (paso === "confirmacion") setPaso("huespedes");
  };

  const diasRango = generarDias();
  const conteo = {
    disponibles: HABITACIONES_MOCK.filter(h => h.estado === "DISPONIBLE").length,
    reservadas: HABITACIONES_MOCK.filter(h => h.estado === "RESERVADA").length,
    ocupadas: HABITACIONES_MOCK.filter(h => h.estado === "OCUPADA").length,
  };

  return (
    <div className="min-h-screen bg-slate-950 text-white">
      <div className="max-w-6xl mx-auto p-8">
        <div className="mb-8">
          <Link href="/" className="text-amber-400 hover:text-amber-300 text-sm flex items-center gap-2">
            ← Volver al menú
          </Link>
          <h1 className="text-4xl font-bold text-amber-400 mt-4">CU15 - Ocupar Habitación (Check-In)</h1>
        </div>

        <div className="bg-slate-900 border border-slate-700 rounded-lg p-6 mb-8">
          <div className="flex items-center justify-between text-sm">
            <div className={`flex-1 text-center ${paso === "fechasGrilla" ? "text-amber-400 font-bold" : "text-slate-500"}`}>
              1. Fechas Grilla
            </div>
            <div className="w-8 text-slate-600">→</div>
            <div className={`flex-1 text-center ${paso === "grilla" ? "text-amber-400 font-bold" : "text-slate-500"}`}>
              2. Habitación
            </div>
            <div className="w-8 text-slate-600">→</div>
            <div className={`flex-1 text-center ${paso === "huespedes" ? "text-amber-400 font-bold" : "text-slate-500"}`}>
              3. Huéspedes
            </div>
            <div className="w-8 text-slate-600">→</div>
            <div className={`flex-1 text-center ${paso === "confirmacion" ? "text-amber-400 font-bold" : "text-slate-500"}`}>
              4. Confirmar
            </div>
          </div>
        </div>

        {paso === "fechasGrilla" && (
          <div className="bg-slate-900 border border-slate-700 rounded-lg p-8">
            <h2 className="text-2xl font-semibold mb-6 text-amber-400">Paso 1: Rango de Fechas para la Grilla</h2>
            <p className="text-slate-400 text-sm mb-6">
              El check-in se realizará HOY. Seleccione hasta qué fecha desea visualizar la disponibilidad.
            </p>
            
            <div className="bg-amber-900/20 border border-amber-700 rounded-lg p-4 mb-6">
              <p className="text-amber-200 text-sm font-semibold">
                📅 Fecha de Check-In (Inicio): <span className="text-white">{new Date().toLocaleDateString('es-AR', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}</span>
              </p>
            </div>

            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium mb-2">Fecha Hasta *</label>
                <input
                  type="date"
                  value={fechaHastaGrilla}
                  onChange={(e) => { setFechaHastaGrilla(e.target.value); setErrorFechaGrilla(""); }}
                  min={new Date(Date.now() + 86400000).toISOString().split("T")[0]}
                  className="w-full px-4 py-2 bg-slate-800 border border-slate-600 rounded text-white focus:border-amber-400 focus:outline-none"
                />
              </div>
            </div>
            {errorFechaGrilla && <div className="text-red-400 text-sm bg-red-900/20 p-3 rounded border border-red-700 mt-4">{errorFechaGrilla}</div>}
            <button onClick={handleConfirmarFechasGrilla} className="mt-6 bg-amber-400 hover:bg-amber-500 text-slate-950 font-semibold py-2 px-6 rounded transition">
              Continuar →
            </button>
          </div>
        )}

        {paso === "grilla" && (
          <div className="space-y-6">
            <div className="grid grid-cols-3 gap-4 mb-6">
              <div className="bg-slate-900 border border-slate-700 rounded-lg p-4">
                <p className="text-slate-400 text-sm">Disponibles</p>
                <p className="text-2xl font-bold text-green-400">{conteo.disponibles}</p>
              </div>
              <div className="bg-slate-900 border border-slate-700 rounded-lg p-4">
                <p className="text-slate-400 text-sm">Reservadas</p>
                <p className="text-2xl font-bold text-blue-400">{conteo.reservadas}</p>
              </div>
              <div className="bg-slate-900 border border-slate-700 rounded-lg p-4">
                <p className="text-slate-400 text-sm">Ocupadas</p>
                <p className="text-2xl font-bold text-red-400">{conteo.ocupadas}</p>
              </div>
            </div>

            <div className="bg-slate-900 border border-slate-700 rounded-lg p-6">
              <h2 className="text-xl font-semibold mb-4 text-amber-400">
                Grilla de disponibilidad: {new Date(fechaDesdeGrilla).toLocaleDateString()} al {new Date(fechaHastaGrilla).toLocaleDateString()}
              </h2>
              <p className="text-slate-400 text-sm mb-4">
                Haga click en una celda disponible para iniciar la selección, luego haga click en otra celda de la misma habitación para completar el rango.
              </p>

              <div className="mb-4 flex gap-6 text-xs text-slate-400">
                <span>📊 Disponibles: {conteo.disponibles}</span>
                <span>📅 Reservadas: {conteo.reservadas}</span>
                <span>🚫 Ocupadas: {conteo.ocupadas}</span>
              </div>

              <div className="overflow-x-auto">
                <table className="w-full border-collapse text-sm">
                  <thead>
                    <tr className="bg-slate-800">
                      <th className="border border-slate-700 px-4 py-2 text-amber-400 font-semibold">Habitación</th>
                      {diasRango.map((dia, idx) => (
                        <th key={idx} className="border border-slate-700 px-2 py-2 text-center text-amber-400 font-semibold min-w-[60px]">
                          <div className="text-xs">{dia.toLocaleDateString("es-AR", { day: "2-digit", month: "2-digit" })}</div>
                        </th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {COMODIDADES_ORDEN.map((comodidad) => {
                      const habitacionesPorComodidad = HABITACIONES_MOCK.filter(h => h.comodidad === comodidad);
                      return habitacionesPorComodidad.length > 0
                        ? habitacionesPorComodidad.map((hab, habIdx) => (
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
                                          ? "Seleccionada"
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
                  <span>► = Inicio de selección</span>
                  <span>R = Reservada</span>
                  <span>X = Ocupada</span>
                </div>
              </div>
            </div>

            {/* Resumen de selección */}
            {seleccion && (
              <div className="bg-slate-900 border border-slate-700 rounded-lg p-6">
                <h3 className="text-lg font-semibold mb-4 text-amber-400">Habitación y fechas seleccionadas</h3>
                <div className="bg-slate-800 p-4 rounded">
                  {(() => {
                    const hab = HABITACIONES_MOCK.find(h => h.id === seleccion.habitacionId);
                    if (!hab) return null;
                    const noches = seleccion.diaFin - seleccion.diaInicio + 1;
                    const subtotal = hab.precioNoche * noches;
                    return (
                      <div className="flex justify-between items-center">
                        <div>
                          <p className="font-semibold text-white">Habitación {hab.numero} ({hab.tipo})</p>
                          <p className="text-sm text-slate-400">
                            {diasRango[seleccion.diaInicio]?.toLocaleDateString()} - {diasRango[seleccion.diaFin]?.toLocaleDateString()} ({noches} noche{noches > 1 ? "s" : ""})
                          </p>
                        </div>
                        <div className="flex items-center gap-4">
                          <p className="text-amber-400 font-semibold">${subtotal}</p>
                          <button
                            onClick={handleRemoverSeleccion}
                            className="text-red-400 hover:text-red-300 text-sm"
                          >
                            ✕ Quitar
                          </button>
                        </div>
                      </div>
                    );
                  })()}
                </div>
              </div>
            )}

            {/* Diálogo de confirmación: Habitación totalmente reservada */}
            {confirmacionTipo === "reservada" && seleccion && (
              <div className="bg-yellow-900/30 border-2 border-yellow-500 rounded-lg p-6">
                <div className="flex items-start gap-4">
                  <div className="text-yellow-400 text-3xl">⚠️</div>
                  <div className="flex-1">
                    <h3 className="text-xl font-bold text-yellow-400 mb-2">Habitación Reservada</h3>
                    <p className="text-slate-200 mb-4">
                      La habitación seleccionada está completamente reservada en el rango de fechas elegido.
                    </p>
                    <p className="text-slate-300 text-sm mb-6">
                      ¿Desea ocuparla igualmente?
                    </p>
                    <div className="flex gap-3">
                      <button
                        onClick={handleOcuparIgualmente}
                        className="bg-yellow-600 hover:bg-yellow-500 text-white font-semibold py-2 px-6 rounded transition"
                      >
                        Ocupar Igualmente
                      </button>
                      <button
                        onClick={handleVolverASeleccion}
                        className="bg-slate-700 hover:bg-slate-600 text-white font-semibold py-2 px-6 rounded transition"
                      >
                        Volver
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            )}

            {/* Diálogo de confirmación: ¿Es dueño de la reserva? */}
            {confirmacionTipo === "duenioReserva" && seleccion && (
              <div className="bg-blue-900/30 border-2 border-blue-500 rounded-lg p-6">
                <div className="flex items-start gap-4">
                  <div className="text-blue-400 text-3xl">❓</div>
                  <div className="flex-1">
                    <h3 className="text-xl font-bold text-blue-400 mb-2">Confirmación de Reserva</h3>
                    <p className="text-slate-200 mb-4">
                      ¿Es usted el dueño de esta reserva?
                    </p>
                    <div className="flex gap-3">
                      <button
                        onClick={() => handleEsDuenioReserva(true)}
                        className="bg-green-600 hover:bg-green-500 text-white font-semibold py-2 px-6 rounded transition"
                      >
                        Sí
                      </button>
                      <button
                        onClick={() => handleEsDuenioReserva(false)}
                        className="bg-red-600 hover:bg-red-500 text-white font-semibold py-2 px-6 rounded transition"
                      >
                        No
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            )}

            <div className="flex justify-between">
              <button onClick={handleVolverPaso} className="bg-slate-700 hover:bg-slate-600 text-white font-semibold py-2 px-6 rounded transition">
                ← Atrás
              </button>
              <button 
                onClick={handleContinuarDesdeGrilla} 
                disabled={!seleccion || confirmacionTipo !== null}
                className={`font-semibold py-2 px-6 rounded transition ${
                  seleccion && confirmacionTipo === null
                    ? "bg-amber-500 hover:bg-amber-600 text-white"
                    : "bg-slate-700 text-slate-500 cursor-not-allowed"
                }`}
              >
                Continuar →
              </button>
            </div>
          </div>
        )}

        {paso === "huespedes" && (
          <div className="space-y-6">
            <div className="bg-slate-900 border border-slate-700 rounded-lg p-6">
              <h3 className="text-lg font-semibold mb-4 text-amber-400">Resumen de la Estadía</h3>
              <div className="grid grid-cols-3 gap-4 text-sm">
                <div><p className="text-slate-400">Habitación</p><p className="text-white font-semibold">{habitacionSeleccionada?.numero}</p></div>
                <div><p className="text-slate-400">Check-In</p><p className="text-white font-semibold">{fechaCheckIn ? new Date(fechaCheckIn).toLocaleDateString() : "-"}</p></div>
                <div><p className="text-slate-400">Check-Out</p><p className="text-white font-semibold">{fechaCheckOut ? new Date(fechaCheckOut).toLocaleDateString() : "-"}</p></div>
              </div>
            </div>

            {huespedes.length > 0 && (
              <div className="bg-slate-900 border border-slate-700 rounded-lg p-6">
                <h3 className="text-lg font-semibold mb-4 text-amber-400">Huéspedes Agregados ({huespedes.length})</h3>
                <div className="space-y-2">
                  {huespedes.map((h, idx) => (
                    <div key={idx} className="flex items-center justify-between bg-slate-800 p-4 rounded border border-slate-700">
                      <div>
                        <p className="text-white font-semibold">{idx === 0 && "👤 "}{h.apellido}, {h.nombres}{idx === 0 && <span className="text-xs text-amber-300 ml-2">(Responsable)</span>}</p>
                        <p className="text-slate-400 text-sm">{h.tipoDocumento}: {h.nroDocumento}</p>
                      </div>
                      {idx > 0 && <button onClick={() => handleEliminarHuesped(idx)} className="bg-red-700 hover:bg-red-600 text-white px-3 py-1 rounded text-sm transition">Eliminar</button>}
                    </div>
                  ))}
                </div>
              </div>
            )}

            <div className="bg-slate-900 border border-slate-700 rounded-lg p-6">
              <h3 className="text-lg font-semibold mb-4 text-amber-400">
                {huespedes.length === 0 ? "Buscar Responsable (Obligatorio)" : "Buscar Acompañante (Opcional)"}
              </h3>
              <p className="text-slate-400 text-sm mb-4">
                Todos los campos son opcionales. Si no ingresa ningún dato, se listarán todos los huéspedes.
              </p>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
                <div>
                  <label className="block text-sm font-medium mb-2">Apellido</label>
                  <input
                    type="text"
                    value={busquedaHuesped.apellido}
                    onChange={(e) => handleChangeBusqueda("apellido", e.target.value)}
                    placeholder="Ej: G (primera letra)"
                    maxLength={1}
                    className={`w-full px-4 py-2 bg-slate-800 border rounded text-white focus:outline-none ${
                      erroresBusqueda.apellido ? "border-red-500" : "border-slate-600 focus:border-amber-400"
                    }`}
                  />
                  {erroresBusqueda.apellido && (
                    <p className="text-red-400 text-xs mt-1">{erroresBusqueda.apellido}</p>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium mb-2">Nombre(s)</label>
                  <input
                    type="text"
                    value={busquedaHuesped.nombres}
                    onChange={(e) => handleChangeBusqueda("nombres", e.target.value)}
                    placeholder="Ej: A (primera letra)"
                    maxLength={1}
                    className={`w-full px-4 py-2 bg-slate-800 border rounded text-white focus:outline-none ${
                      erroresBusqueda.nombres ? "border-red-500" : "border-slate-600 focus:border-amber-400"
                    }`}
                  />
                  {erroresBusqueda.nombres && (
                    <p className="text-red-400 text-xs mt-1">{erroresBusqueda.nombres}</p>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium mb-2">Tipo de Documento</label>
                  <select
                    value={busquedaHuesped.tipoDocumento}
                    onChange={(e) => handleChangeBusqueda("tipoDocumento", e.target.value)}
                    className="w-full px-4 py-2 bg-slate-800 border border-slate-600 rounded text-white focus:border-amber-400 focus:outline-none"
                  >
                    <option value="">Seleccionar...</option>
                    <option value="DNI">DNI</option>
                    <option value="PASAPORTE">Pasaporte</option>
                    <option value="CI">Cédula de Identidad</option>
                  </select>
                  {erroresBusqueda.tipoDocumento && (
                    <p className="text-red-400 text-xs mt-1">{erroresBusqueda.tipoDocumento}</p>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium mb-2">Número de Documento</label>
                  <input
                    type="text"
                    value={busquedaHuesped.nroDocumento}
                    onChange={(e) => handleChangeBusqueda("nroDocumento", e.target.value)}
                    placeholder="Alfanumérico, 6-15 caracteres"
                    maxLength={15}
                    className={`w-full px-4 py-2 bg-slate-800 border rounded text-white focus:outline-none ${
                      erroresBusqueda.nroDocumento ? "border-red-500" : "border-slate-600 focus:border-amber-400"
                    }`}
                  />
                  {erroresBusqueda.nroDocumento && (
                    <p className="text-red-400 text-xs mt-1">{erroresBusqueda.nroDocumento}</p>
                  )}
                </div>
              </div>

              <button
                onClick={handleBuscarHuespedes}
                disabled={buscando}
                className={`w-full font-semibold py-3 px-6 rounded transition ${
                  buscando
                    ? "bg-slate-700 text-slate-500 cursor-not-allowed"
                    : "bg-amber-500 hover:bg-amber-600 text-white"
                }`}
              >
                {buscando ? "Buscando..." : "🔍 Buscar Huésped"}
              </button>
            </div>

            {/* Resultados de búsqueda (placeholder hasta que esté el backend) */}
            {mostrarResultados && (
              <div className="bg-slate-900 border border-slate-700 rounded-lg p-6">
                <h3 className="text-lg font-semibold mb-4 text-amber-400">Resultados de la Búsqueda</h3>
                {resultadosBusqueda.length === 0 ? (
                  <div className="text-center py-8">
                    <p className="text-slate-400 mb-4">No se encontraron huéspedes con los criterios especificados.</p>
                    <p className="text-amber-300 text-sm">
                      (Esta funcionalidad se completará cuando el backend esté implementado)
                    </p>
                  </div>
                ) : (
                  <div className="space-y-2">
                    {resultadosBusqueda.map((huesped, idx) => (
                      <div
                        key={idx}
                        className="flex items-center justify-between bg-slate-800 p-4 rounded border border-slate-700 hover:border-amber-500 transition"
                      >
                        <div>
                          <p className="text-white font-semibold">{huesped.apellido}, {huesped.nombres}</p>
                          <p className="text-slate-400 text-sm">{huesped.tipoDocumento}: {huesped.nroDocumento}</p>
                        </div>
                        <button
                          onClick={() => handleSeleccionarHuesped(huesped)}
                          className="bg-green-600 hover:bg-green-500 text-white px-4 py-2 rounded text-sm transition"
                        >
                          Seleccionar
                        </button>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            )}

            <div className="flex gap-4">
              <button onClick={handleVolverPaso} className="bg-slate-700 hover:bg-slate-600 text-white font-semibold py-2 px-6 rounded transition">
                ← Atrás
              </button>
              <button onClick={handleContinuarConfirmacion} className="bg-amber-400 hover:bg-amber-500 text-slate-950 font-semibold py-2 px-6 rounded transition">
                Continuar →
              </button>
            </div>
          </div>
        )}

        {paso === "confirmacion" && (
          <div className="bg-slate-900 border border-slate-700 rounded-lg p-8">
            <h2 className="text-2xl font-semibold mb-6 text-amber-400">Paso 5: Confirmación de Check-In</h2>
            <div className="space-y-6">
              <div className="bg-slate-800 rounded-lg p-6">
                <h3 className="text-lg font-semibold mb-4 text-white">Detalles de la Estadía</h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div><p className="text-slate-400 text-sm">Habitación</p><p className="text-white text-xl font-bold">{habitacionSeleccionada?.numero} - {habitacionSeleccionada?.tipo}</p></div>
                  <div><p className="text-slate-400 text-sm">Capacidad</p><p className="text-white text-xl font-bold">{habitacionSeleccionada?.capacidad} personas</p></div>
                  <div><p className="text-slate-400 text-sm">Check-In</p><p className="text-white font-semibold">{fechaCheckIn ? new Date(fechaCheckIn).toLocaleDateString("es-ES", { weekday: "long", year: "numeric", month: "long", day: "numeric" }) : "-"}</p></div>
                  <div><p className="text-slate-400 text-sm">Check-Out</p><p className="text-white font-semibold">{fechaCheckOut ? new Date(fechaCheckOut).toLocaleDateString("es-ES", { weekday: "long", year: "numeric", month: "long", day: "numeric" }) : "-"}</p></div>
                </div>
              </div>
              <div className="bg-slate-800 rounded-lg p-6">
                <h3 className="text-lg font-semibold mb-4 text-white">Huéspedes ({huespedes.length})</h3>
                <div className="space-y-3">
                  {huespedes.map((h, idx) => (
                    <div key={idx} className="bg-slate-900 p-4 rounded border border-slate-700">
                      <p className="text-white font-semibold">{idx === 0 && "👤 "}{h.apellido}, {h.nombres}{idx === 0 && <span className="text-xs text-amber-300 ml-2">(Responsable)</span>}</p>
                      <p className="text-slate-400 text-sm">{h.tipoDocumento}: {h.nroDocumento}</p>
                    </div>
                  ))}
                </div>
              </div>
              <div className="bg-amber-900/20 border border-amber-700 rounded-lg p-4">
                <p className="text-amber-200 text-sm">⚠️ Por favor, verifique que todos los datos sean correctos antes de confirmar el check-in.</p>
              </div>
              <div className="flex gap-4">
                <button onClick={handleVolverPaso} className="bg-slate-700 hover:bg-slate-600 text-white font-semibold py-2 px-6 rounded transition">
                  ← Modificar Datos
                </button>
                <button onClick={handleConfirmarEstadia} className="bg-green-600 hover:bg-green-500 text-white font-semibold py-2 px-6 rounded transition">
                  ✓ Confirmar Check-In
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
