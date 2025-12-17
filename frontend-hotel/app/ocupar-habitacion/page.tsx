"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { DoorOpen, Home, Calendar, Users, CheckCircle, Loader2 } from "lucide-react";
import { obtenerHabitaciones, buscarHuespedes, crearEstadia, buscarReservas } from "@/lib/api";
import { DtoHabitacion, DtoHuesped, DtoEstadia, EstadoHabitacion } from "@/lib/types";

// --- INTERFACES ---
interface HabitacionEstado {
  id: string;
  numero: string;
  tipo: string;
  capacidad: number;
  estadoHabitacion?: "HABILITADA" | "FUERA_DE_SERVICIO";
  estado: "DISPONIBLE" | "RESERVADA" | "OCUPADA";
  estadosPorDia?: Record<string, "DISPONIBLE" | "RESERVADA" | "OCUPADA">;
  precioNoche: number;
}

interface SeleccionHabitacion {
  habitacionId: string;
  diaInicio: number;
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

// --- CONSTANTES ---
const TIPOS_HABITACION_ORDEN = [
  "INDIVIDUAL_ESTANDAR",
  "DOBLE_ESTANDAR",
  "DOBLE_SUPERIOR",
  "SUPERIOR_FAMILY_PLAN",
  "SUITE_DOBLE",
];

const formatearTipo = (tipo: string): string => {
  return tipo.replace(/_/g, " ");
};

type Paso = "fechasGrilla" | "grilla" | "huespedes" | "confirmacion";
type TipoConfirmacion = "reservada" | "duenioReserva" | null;

const createLocalDate = (dateString: string): Date => {
  const [year, month, day] = dateString.split("-").map(Number);
  return new Date(year, month - 1, day);
};

export default function OcuparHabitacion() {
  const router = useRouter();

  // --- ESTADOS ---
  const [paso, setPaso] = useState<Paso>("fechasGrilla");
  const [fechaDesdeGrilla, setFechaDesdeGrilla] = useState("");
  const [fechaHastaGrilla, setFechaHastaGrilla] = useState("");
  const [errorFechaGrilla, setErrorFechaGrilla] = useState("");

  const [habitaciones, setHabitaciones] = useState<HabitacionEstado[]>([]);
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
  const [responsableIdx, setResponsableIdx] = useState<number | null>(null);
  const [loading, setLoading] = useState(false);
  const [errorCarga, setErrorCarga] = useState("");

  // Estados Grilla
  const [seleccion, setSeleccion] = useState<SeleccionHabitacion | null>(null);
  const [seleccionActual, setSeleccionActual] = useState<{
    habitacionId: string;
    diaInicio: number | null;
  } | null>(null);

  const [seleccionTieneReserva, setSeleccionTieneReserva] = useState(false);
  const [ocupandoReserva, setOcupandoReserva] = useState(false);
  const [esDuenoReserva, setEsDuenoReserva] = useState<boolean>(false);
  const [postCheckin, setPostCheckin] = useState(false);
  const [estadiaPendiente, setEstadiaPendiente] = useState<DtoEstadia | null>(null);
  const [errorCreacion, setErrorCreacion] = useState<string | null>(null);

  // --- VALIDACIONES ---
  const validarFechasGrilla = (): boolean => {
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);
    const fechaHoyStr = hoy.toISOString().split("T")[0];
    setFechaDesdeGrilla(fechaHoyStr);

    if (!fechaHastaGrilla) {
      setErrorFechaGrilla("Debe seleccionar la fecha hasta");
      return false;
    }
    const hasta = createLocalDate(fechaHastaGrilla);
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
        if (!valor.trim()) return "";
        // CORRECCIÓN: Permitir longitud 1 para búsqueda por inicial
        if (valor.length > 50) return "Demasiado largo";
        if (!regexNombre.test(valor)) return "Solo puede contener letras";
        return "";
      case "tipoDocumento":
        return "";
      case "nroDocumento":
        if (!busquedaHuesped.tipoDocumento) return "";
        if (!valor.trim()) return "";
        if (valor.length < 6 || valor.length > 15) return "Debe tener entre 6 y 15 caracteres";
        if (!regexDocumento.test(valor)) return "El documento no debe contener espacios ni símbolos";
        return "";
      default:
        return "";
    }
  };

  // --- CARGA DE GRILLA ---
  const handleConfirmarFechasGrilla = async () => {
    if (validarFechasGrilla()) {
      setSeleccion(null);
      setSeleccionActual(null);
      setLoading(true);

      const hoy = new Date();
      hoy.setHours(0, 0, 0, 0);
      const fechaDesde = hoy.toISOString().split("T")[0];
      const fechaHasta = fechaHastaGrilla;

      setPaso("grilla");
      await new Promise(resolve => setTimeout(resolve, 100));

      const success = await recargarHabitacionesConNuevasFechas(fechaDesde, fechaHasta);
      if (!success) setPaso("fechasGrilla");
      setLoading(false);
    }
  };

  const recargarHabitacionesConNuevasFechas = async (fechaDesde: string, fechaHasta: string): Promise<boolean> => {
    if (!fechaDesde || !fechaHasta) {
      setErrorCarga("Fechas no válidas");
      return false;
    }
    try {
      const response = await fetch(`http://localhost:8080/api/habitaciones/estados?fechaDesde=${fechaDesde}&fechaHasta=${fechaHasta}`);
      if (!response.ok) {
        const errorText = await response.text();
        setErrorCarga(`Error ${response.status}: ${errorText}`);
        return false;
      }
      const data = await response.json();
      if (!Array.isArray(data) || data.length === 0) {
        setErrorCarga("No se encontraron habitaciones");
        return false;
      }
        const habitacionesMapeadas = data.map((h: any) => {
            const estadosPorDia = h.estadosPorDia || {};
            const todosLosEstados = Object.values(estadosPorDia);
            const esFueraDeServicio = todosLosEstados.length > 0 && todosLosEstados.every((estado: any) => estado === "MANTENIMIENTO");
            const estadoReservaBase = (estadosPorDia[fechaDesde]) || "DISPONIBLE";
            return {
                id: h.numero.toString(),
                numero: h.numero.toString(),
                tipo: h.tipoHabitacion,
                capacidad: h.capacidad,
                estadoHabitacion: (esFueraDeServicio ? "FUERA_DE_SERVICIO" : "HABILITADA") as "HABILITADA" | "FUERA_DE_SERVICIO",
                estado: estadoReservaBase as "DISPONIBLE" | "RESERVADA" | "OCUPADA",
                estadosPorDia: estadosPorDia,
                precioNoche: h.costoPorNoche
            };
        });

        setHabitaciones(habitacionesMapeadas);
      setFechaDesdeGrilla(fechaDesde);
      setFechaHastaGrilla(fechaHasta);
      setErrorCarga("");
      return true;
    } catch (error) {
      setErrorCarga("Error de conexión al cargar habitaciones");
      return false;
    }
  };

  const generarDias = (desde?: string, hasta?: string): Date[] => {
    const fechaDesde = desde || fechaDesdeGrilla;
    const fechaHasta = hasta || fechaHastaGrilla;
    if (!fechaDesde || !fechaHasta) return [];
    const desdeDate = createLocalDate(fechaDesde);
    const hastaDate = createLocalDate(fechaHasta);
    const dias: Date[] = [];
    const actual = new Date(desdeDate);
    while (actual <= hastaDate) {
      dias.push(new Date(actual));
      actual.setDate(actual.getDate() + 1);
    }
    return dias;
  };

  // --- LÓGICA DE SELECCIÓN ---
  const esCeldaDisponible = (habitacionId: string, diaIdx: number): boolean => {
    const habitacion = habitaciones.find((h: HabitacionEstado) => h.id === habitacionId);
    if (!habitacion) return false;
    const estadoDia = obtenerEstadoCelda(habitacionId, diaIdx, fechaDesdeGrilla);
    if (estadoDia === "OCUPADA" || estadoDia === "FUERA_DE_SERVICIO") return false;
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

  const handleClickCelda = (habitacionId: string, diaIdx: number) => {
    const hab = habitaciones.find((h: HabitacionEstado) => h.id === habitacionId);
    if (!hab || hab.estado === "OCUPADA") return;

    if (diaIdx === 0) {
      alert("El check-in es HOY. Seleccione la fecha de check-out (desde mañana en adelante).");
      return;
    }

    const diaInicio = 0;
    const diaFin = diaIdx;
    const dias = generarDias(fechaDesdeGrilla, fechaHastaGrilla);
    const rangoDias = Array.from({ length: Math.abs(diaFin - diaInicio) + 1 }, (_, i) => Math.min(diaInicio, diaFin) + i);

    let hayOcupadaOMant = false;
    let hayReservada = false;
    rangoDias.forEach(idx => {
      const estado = obtenerEstadoCelda(habitacionId, idx, fechaDesdeGrilla);
      if (estado === "OCUPADA" || estado === "FUERA_DE_SERVICIO") hayOcupadaOMant = true;
      if (estado === "RESERVADA") hayReservada = true;
    });

    if (hayOcupadaOMant) {
      alert("No se puede seleccionar ese rango. Hay días no disponibles.");
      return;
    }

    setOcupandoReserva(false);
    const seleccionFinal = { habitacionId, diaInicio: 0, diaFin: diaIdx };
    setSeleccion(seleccionFinal);
    setSeleccionActual(null);
    setSeleccionTieneReserva(hayReservada);

    if (dias.length > 0) {
      const fechaInicio = dias[seleccionFinal.diaInicio];
      const fechaFin = dias[seleccionFinal.diaFin];
      setFechaCheckIn(fechaInicio.toISOString().split("T")[0]);
      setFechaCheckOut(fechaFin.toISOString().split("T")[0]);
    }

    setHabitacionSeleccionada(hab);
    setConfirmacionTipo(hayReservada ? "reservada" : null);
  };

  const handleRemoverSeleccion = () => {
    setSeleccion(null);
    setSeleccionActual(null);
    setSeleccionTieneReserva(false);
    setOcupandoReserva(false);
    setHabitacionSeleccionada(null);
    setFechaCheckIn("");
    setFechaCheckOut("");
    setConfirmacionTipo(null);
  };

  const handleContinuarDesdeGrilla = () => {
    if (!seleccion) {
      alert("Debe seleccionar una habitación y un rango de fechas");
      return;
    }
    if (confirmacionTipo) return;
    setPaso("huespedes");
  };

  const handleOcuparIgualmente = () => setConfirmacionTipo("duenioReserva");

  const handleVolverASeleccion = () => {
    setSeleccion(null);
    setSeleccionActual(null);
    setSeleccionTieneReserva(false);
    setOcupandoReserva(false);
    setEsDuenoReserva(false);
    setHabitacionSeleccionada(null);
    setConfirmacionTipo(null);
    setFechaCheckIn("");
    setFechaCheckOut("");
  };

  const handleEsDuenioReserva = (esDuenio: boolean) => {
    if (esDuenio) {
      setOcupandoReserva(true);
      setEsDuenoReserva(true);
      setConfirmacionTipo(null);
      setPaso("huespedes");
    } else {
      setOcupandoReserva(false);
      setEsDuenoReserva(false);
      setConfirmacionTipo(null);
      handleVolverASeleccion();
    }
  };

  const obtenerEstadoCelda = (habitacionId: string, diaIdx: number, fechaDesde: string): "DISPONIBLE" | "RESERVADA" | "OCUPADA" | "FUERA_DE_SERVICIO" => {
    const habitacion = habitaciones.find(h => h.id === habitacionId);
    if (!habitacion) return "OCUPADA";
    if (habitacion.estadoHabitacion === "FUERA_DE_SERVICIO") return "FUERA_DE_SERVICIO";
    if (habitacion.estadosPorDia && Object.keys(habitacion.estadosPorDia).length > 0) {
      const dia = createLocalDate(fechaDesde);
      dia.setDate(dia.getDate() + diaIdx);
      const fechaDia = dia.toISOString().split('T')[0];
      const estadoDia = habitacion.estadosPorDia[fechaDia];
      return estadoDia || "DISPONIBLE";
    }
    return habitacion.estado || "DISPONIBLE";
  };

  const getEstadoColor = (estado: "DISPONIBLE" | "RESERVADA" | "OCUPADA" | "FUERA_DE_SERVICIO") => {
    switch (estado) {
      case "DISPONIBLE": return "bg-green-600 dark:bg-green-700";
      case "RESERVADA": return "bg-orange-600 dark:bg-orange-700";
      case "OCUPADA": return "bg-red-600 dark:bg-red-700";
      case "FUERA_DE_SERVICIO": return "bg-slate-500 dark:bg-slate-600";
      default: return "bg-slate-600 dark:bg-slate-700";
    }
  };

  // --- LÓGICA HUÉSPEDES ---
  const handleChangeBusqueda = (campo: keyof BusquedaHuesped, valor: string) => {
    // Forzar mayúsculas para la inicial
    const val = (campo === "apellido" || campo === "nombres") ? valor.toUpperCase() : valor;
    setBusquedaHuesped({ ...busquedaHuesped, [campo]: val });
    const error = validarCampoBusqueda(campo, val);
    setErroresBusqueda({ ...erroresBusqueda, [campo]: error });
  };

  const handleBuscarHuespedes = async () => {
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
      const criterios: any = {
        apellido: busquedaHuesped.apellido,
        nombres: busquedaHuesped.nombres,
        nroDocumento: busquedaHuesped.nroDocumento
      };
      if (busquedaHuesped.tipoDocumento) criterios.tipoDocumento = busquedaHuesped.tipoDocumento;

      const data = await buscarHuespedes(criterios);

      let resultados = data.map((h: DtoHuesped) => ({
        id: h.nroDocumento,
        apellido: h.apellido,
        nombres: h.nombres,
        tipoDocumento: String(h.tipoDocumento),
        nroDocumento: h.nroDocumento
      }));
      if (busquedaHuesped.nroDocumento) {
        resultados = resultados.filter(h => h.nroDocumento.includes(busquedaHuesped.nroDocumento));
      }
      setResultadosBusqueda(resultados);
      setMostrarResultados(true);
    } catch (error) {
      console.error("Error en búsqueda:", error);
      alert("Error al buscar huésped");
    } finally {
      setBuscando(false);
    }
  };

  // --- SELECCIÓN Y GESTIÓN DE HUÉSPEDES (CORREGIDO) ---
  const handleSeleccionarHuesped = (huesped: DatosHuesped) => {
    // 1. Validar duplicados
    const yaExiste = huespedes.some(
      h => h.tipoDocumento === huesped.tipoDocumento && h.nroDocumento === huesped.nroDocumento
    );
    if (yaExiste) {
      alert("Este huésped ya ha sido agregado a la lista.");
      return;
    }

    // 2. Validar capacidad
    if (habitacionSeleccionada && huespedes.length >= habitacionSeleccionada.capacidad) {
      alert(`La capacidad máxima de esta habitación es de ${habitacionSeleccionada.capacidad} personas.`);
      return;
    }

    // 3. Agregar al final (evita loop de roles)
    const nuevaLista = [...huespedes, huesped];
    setHuespedes(nuevaLista);

    // 4. Si es el primero, es responsable por defecto
    if (nuevaLista.length === 1) {
      setResponsableIdx(0);
    }

    setMostrarResultados(false);
    setBusquedaHuesped({ apellido: "", nombres: "", tipoDocumento: "", nroDocumento: "" });
    setErroresBusqueda({});
  };

  const handleEliminarHuesped = (index: number) => {
    const nuevaLista = huespedes.filter((_, i) => i !== index);
    setHuespedes(nuevaLista);
    setResponsableIdx(prev => {
      if (prev === null) return null;
      if (prev === index) return null;
      if (prev > index) return prev - 1;
      return prev;
    });
  };

  const handleSetResponsable = (index: number) => {
    const elegido = huespedes[index];
    const resto = huespedes.filter((_, i) => i !== index);
    setHuespedes([elegido, ...resto]);
    setResponsableIdx(0);
  };

  const handleContinuarConfirmacion = () => {
    if (huespedes.length === 0 || responsableIdx === null) {
      alert("Debe seleccionar un huésped responsable");
      return;
    }
    setPaso("confirmacion");
  };

  // --- CONFIRMACIÓN ---
  const handleConfirmarEstadia = async () => {
    try {
      setLoading(true);
      if (!habitacionSeleccionada || huespedes.length === 0) {
        alert("Faltan datos para preparar el check-in");
        return;
      }

      const dias = seleccion ? seleccion.diaFin - seleccion.diaInicio + 1 : 1;
      const valorEstadia = habitacionSeleccionada.precioNoche * dias;

      const estadia: DtoEstadia = {
        fechaCheckIn: fechaCheckIn,
        fechaCheckOut: fechaCheckOut || undefined,
        valorEstadia: valorEstadia,
        dtoHabitacion: {
          numero: habitacionSeleccionada.numero,
          tipoHabitacion: habitacionSeleccionada.tipo,
          capacidad: habitacionSeleccionada.capacidad,
          estadoHabitacion: EstadoHabitacion.HABILITADA,
          costoPorNoche: habitacionSeleccionada.precioNoche
        },
        dtoHuespedes: huespedes.map(h => ({
          idHuesped: 0,
          apellido: h.apellido,
          nombres: h.nombres,
          tipoDocumento: h.tipoDocumento as any,
          nroDocumento: h.nroDocumento,
        }))
      };

      if (ocupandoReserva && esDuenoReserva && fechaCheckIn && fechaCheckOut) {
        try {
          const reservas = await buscarReservas(fechaCheckIn, fechaCheckOut, habitacionSeleccionada.numero)
          if (reservas && reservas.length > 0) {
            const r = reservas[0]
            estadia.dtoReserva = {
              idReserva: r.idReserva,
              estadoReserva: r.estadoReserva,
              fechaDesde: r.fechaDesde,
              fechaHasta: r.fechaHasta,
              nombreHuespedResponsable: r.nombreHuespedResponsable,
              apellidoHuespedResponsable: r.apellidoHuespedResponsable,
              telefonoHuespedResponsable: r.telefonoHuespedResponsable,
              idHabitacion: r.idHabitacion,
            } as any
          }
        } catch (e) {
          console.warn("[CU15] No se pudo obtener idReserva para vincular:", e)
        }
      }

      setEstadiaPendiente(estadia);
      setPostCheckin(true);
    } catch (error: any) {
      console.error("Error al confirmar estadía:", error);
      alert("Error al preparar el check-in: " + error.message);
    } finally {
      setLoading(false);
    }
  };

  const ejecutarCreacionEstadia = async () => {
    if (!estadiaPendiente) return;
    setErrorCreacion(null);
    setLoading(true);
    try {
      await crearEstadia(estadiaPendiente);
      alert("La operación ha culminado con éxito. Check-in realizado correctamente.");
    } catch (error: any) {
      const mensaje = error.message || "Error desconocido al crear la estadía";
      setErrorCreacion(mensaje);
      console.error("[CU15] Error al crear estadía:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleVolverPaso = () => {
    if (paso === "grilla") setPaso("fechasGrilla");
    else if (paso === "huespedes") setPaso("grilla");
    else if (paso === "confirmacion") setPaso("huespedes");
  };

  const diasRango = generarDias(fechaDesdeGrilla, fechaHastaGrilla);
  const conteo = {
    disponibles: habitaciones.filter((h: HabitacionEstado) => h.estado === "DISPONIBLE").length,
    reservadas: habitaciones.filter((h: HabitacionEstado) => h.estado === "RESERVADA").length,
    ocupadas: habitaciones.filter((h: HabitacionEstado) => h.estado === "OCUPADA").length,
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50 dark:from-slate-950 dark:via-slate-900 dark:to-slate-950">
      <main className="mx-auto max-w-6xl px-4 py-12 sm:px-6 lg:px-8">
        <div className="mb-8">
          <div className="mb-6 flex items-center gap-4">
            <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-gradient-to-br from-blue-600 to-indigo-600 text-white shadow-lg">
              <DoorOpen className="h-6 w-6" />
            </div>
            <div>
              <p className="text-xs font-semibold uppercase tracking-wider text-blue-600 dark:text-blue-400">CU15</p>
              <h1 className="text-3xl font-bold text-slate-900 dark:text-slate-50">Ocupar Habitación (Check-In)</h1>
            </div>
          </div>
          <p className="text-slate-600 dark:text-slate-400">Registrar el check-in de huéspedes en una habitación.</p>
        </div>

        <Card className="mb-6 border-blue-200 bg-blue-50/50 p-4 dark:border-blue-900 dark:bg-blue-950/20">
          <div className="flex items-center justify-between text-sm">
            <div className={`flex-1 text-center ${paso === "fechasGrilla" ? "font-bold text-blue-600" : "text-slate-500"}`}>1. Fechas</div>
            <div className="text-slate-400">→</div>
            <div className={`flex-1 text-center ${paso === "grilla" ? "font-bold text-blue-600" : "text-slate-500"}`}>2. Habitación</div>
            <div className="text-slate-400">→</div>
            <div className={`flex-1 text-center ${paso === "huespedes" ? "font-bold text-blue-600" : "text-slate-500"}`}>3. Huéspedes</div>
            <div className="text-slate-400">→</div>
            <div className={`flex-1 text-center ${paso === "confirmacion" ? "font-bold text-blue-600" : "text-slate-500"}`}>4. Confirmar</div>
          </div>
        </Card>

        {paso === "fechasGrilla" && (
          <Card className="p-6">
            <h2 className="text-xl font-semibold mb-4 text-slate-900 dark:text-slate-50">Paso 1: Rango de Fechas</h2>
            <Card className="mb-6 border-blue-200 bg-blue-50/50 p-4">
              <p className="text-sm font-semibold text-slate-900">
                <Calendar className="inline h-4 w-4 mr-2" />
                Fecha de Check-In (Inicio): <span className="text-blue-600">{new Date().toLocaleDateString('es-AR')}</span>
              </p>
            </Card>
            <div className="space-y-4">
              <div>
                <Label htmlFor="fechaHasta">Fecha Hasta *</Label>
                <Input
                  id="fechaHasta"
                  type="date"
                  value={fechaHastaGrilla}
                  onChange={(e) => { setFechaHastaGrilla(e.target.value); setErrorFechaGrilla(""); }}
                  min={new Date(Date.now() + 86400000).toISOString().split("T")[0]}
                />
              </div>
            </div>
            {errorFechaGrilla && <Card className="mt-4 border-red-200 bg-red-50 p-3"><p className="text-sm text-red-600">{errorFechaGrilla}</p></Card>}
            <div className="mt-6 flex gap-3">
              <Button onClick={handleConfirmarFechasGrilla} className="gap-2">Continuar <Calendar className="h-4 w-4" /></Button>
              <Button variant="outline" asChild><Link href="/">Volver</Link></Button>
            </div>
          </Card>
        )}

        {paso === "grilla" && (
          <div className="space-y-6">
            {errorCarga && <Card className="p-4 border-red-200 bg-red-50"><p className="text-red-600">{errorCarga}</p></Card>}
            {loading || diasRango.length === 0 || habitaciones.length === 0 ? (
              <Card className="p-12 flex flex-col items-center justify-center gap-4">
                <Loader2 className="w-12 h-12 animate-spin text-blue-600" />
                <p className="text-lg font-semibold text-slate-700">Procesando datos...</p>
              </Card>
            ) : (
              <>
            <div className="grid grid-cols-3 gap-4 mb-6">
              <div className="bg-white border p-4 rounded"><p className="text-sm">Disponibles</p><p className="text-2xl font-bold text-green-600">{conteo.disponibles}</p></div>
              <div className="bg-white border p-4 rounded"><p className="text-sm">Reservadas</p><p className="text-2xl font-bold text-orange-600">{conteo.reservadas}</p></div>
              <div className="bg-white border p-4 rounded"><p className="text-sm">Ocupadas</p><p className="text-2xl font-bold text-slate-600">{conteo.ocupadas}</p></div>
            </div>

            <Card className="p-6">
              <h2 className="text-xl font-semibold mb-4 text-slate-900">Grilla de disponibilidad</h2>
              <div className="overflow-x-auto">
                <table className="w-full border-collapse text-sm">
                  <thead>
                    <tr className="bg-slate-100 dark:bg-slate-800">
                      <th className="border px-4 py-2">Fecha</th>
                      {TIPOS_HABITACION_ORDEN.map((tipo) => {
                        const habsTipo = habitaciones.filter((h) => h.tipo === tipo);
                        if (habsTipo.length === 0) return null;
                        return <th key={tipo} colSpan={habsTipo.length} className="border px-2 py-2 text-center bg-blue-50">{formatearTipo(tipo)}</th>;
                      })}
                    </tr>
                    <tr className="bg-slate-50">
                      <th className="border px-4 py-2"></th>
                      {TIPOS_HABITACION_ORDEN.map((tipo) => {
                        const habsTipo = habitaciones.filter((h) => h.tipo === tipo).sort((a, b) => parseInt(a.numero) - parseInt(b.numero));
                        return habsTipo.map((hab) => <th key={hab.id} className="border px-2 py-2 text-center min-w-[80px] text-xs">Hab. {hab.numero}</th>);
                      })}
                    </tr>
                  </thead>
                  <tbody>
                    {diasRango.map((dia, diaIdx) => (
                      <tr key={diaIdx} className={diaIdx % 2 === 0 ? "bg-slate-50" : "bg-white"}>
                        <td className="border px-4 py-2 font-semibold">
                          <div className="text-sm">{dia.toLocaleDateString("es-AR", { weekday: "short", day: "2-digit", month: "2-digit" })}</div>
                          {diaIdx === 0 && <div className="text-xs text-blue-600 font-bold">HOY</div>}
                        </td>
                        {TIPOS_HABITACION_ORDEN.map((tipo) => {
                          const habsTipo = habitaciones.filter((h) => h.tipo === tipo).sort((a, b) => parseInt(a.numero) - parseInt(b.numero));
                          return habsTipo.map((hab) => {
                            const disponible = esCeldaDisponible(hab.id, diaIdx);
                            const seleccionada = esCeldaSeleccionada(hab.id, diaIdx);
                            const estadoDia = obtenerEstadoCelda(hab.id, diaIdx, fechaDesdeGrilla);
                            const baseColor = getEstadoColor(estadoDia);
                            return (
                              <td key={`${hab.id}-${diaIdx}`} className="border px-2 py-2 text-center">
                                <div onClick={() => handleClickCelda(hab.id, diaIdx)} className={`rounded px-2 py-1 text-xs font-semibold text-white transition ${seleccionada || (diaIdx === 0 && seleccion?.habitacionId === hab.id) ? "bg-blue-600 cursor-pointer" : estadoDia === "OCUPADA" ? `${baseColor} cursor-not-allowed` : `${baseColor} cursor-pointer`}`}>
                                  {seleccionada || (diaIdx === 0 && seleccion?.habitacionId === hab.id) ? "✓" : estadoDia === "RESERVADA" ? "R" : estadoDia === "OCUPADA" ? "X" : estadoDia === "FUERA_DE_SERVICIO" ? "FS" : "○"}
                                </div>
                              </td>
                            );
                          });
                        })}
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              {/* --- REFERENCIAS (LEYENDA) RESTAURADA --- */}
              <div className="mt-4 text-xs text-slate-600 dark:text-slate-400 flex gap-4">
                <span>✓ = Seleccionada</span>
                <span>○ = Disponible</span>
                <span>R = Reservada</span>
                <span>X = Ocupada</span>
                <span>FS = Fuera de servicio</span>
              </div>
            </Card>

            {seleccion && (
              <Card className="p-6">
                <h3 className="text-lg font-semibold mb-4">Selección Actual</h3>
                <div className="bg-slate-50 p-4 rounded flex justify-between items-center">
                  <div>
                    <p className="font-semibold">Habitación {habitacionSeleccionada?.numero}</p>
                    <p className="text-sm text-slate-600">{diasRango[seleccion.diaInicio]?.toLocaleDateString()} - {diasRango[seleccion.diaFin]?.toLocaleDateString()}</p>
                  </div>
                  <button onClick={handleRemoverSeleccion} className="text-red-600 text-sm">✕ Quitar</button>
                </div>
              </Card>
            )}

            {confirmacionTipo === "reservada" && (
              <Card className="border-2 border-orange-500 bg-orange-50 p-6">
                <h3 className="text-xl font-bold text-orange-600 mb-2">Habitación Reservada</h3>
                <p className="text-slate-700 mb-4">Esta habitación tiene una reserva en las fechas seleccionadas. ¿Desea ocuparla igualmente?</p>
                <div className="flex gap-3">
                  <Button onClick={handleOcuparIgualmente} className="bg-orange-600 hover:bg-orange-700">Ocupar Igualmente</Button>
                  <Button onClick={handleVolverASeleccion} variant="outline">Volver</Button>
                </div>
              </Card>
            )}

            {confirmacionTipo === "duenioReserva" && (
              <Card className="border-2 border-blue-500 bg-blue-50 p-6">
                <h3 className="text-xl font-bold text-blue-600 mb-2">Confirmación</h3>
                <p className="text-slate-700 mb-4">¿Es usted el dueño de esta reserva?</p>
                <div className="flex gap-3">
                  <Button onClick={() => handleEsDuenioReserva(true)} className="bg-green-600 hover:bg-green-700">Sí</Button>
                  <Button onClick={() => handleEsDuenioReserva(false)} className="bg-red-600 hover:bg-red-700">No</Button>
                </div>
              </Card>
            )}

            <div className="flex justify-between">
              <Button onClick={handleVolverPaso} variant="outline">← Atrás</Button>
              <Button onClick={handleContinuarDesdeGrilla} disabled={!seleccion || confirmacionTipo !== null}>Continuar →</Button>
            </div>
              </>
            )}
          </div>
        )}

        {paso === "huespedes" && (
          <div className="space-y-6">
            <Card className="p-6">
              <h3 className="text-lg font-semibold mb-4">Resumen</h3>
              <div className="grid grid-cols-3 gap-4 text-sm">
                <div><p className="text-slate-600">Habitación</p><p className="font-semibold">{habitacionSeleccionada?.numero}</p></div>
                <div><p className="text-slate-600">Capacidad</p><p className="font-semibold">{habitacionSeleccionada?.capacidad} personas</p></div>
                <div><p className="text-slate-600">Check-In</p><p className="font-semibold">{fechaCheckIn}</p></div>
              </div>
            </Card>

            {huespedes.length > 0 && (
              <Card className="p-6">
                <h3 className="text-lg font-semibold mb-4">Huéspedes Agregados ({huespedes.length})</h3>
                <div className="space-y-2">
                  {huespedes.map((h, idx) => (
                    <div key={idx} className="flex items-center justify-between bg-slate-50 p-4 rounded border">
                      <div>
                        <p className="font-semibold">{responsableIdx === idx && "👤 "}{h.apellido}, {h.nombres}</p>
                        <p className="text-slate-600 text-sm">{h.tipoDocumento}: {h.nroDocumento}</p>
                      </div>
                      <div className="flex gap-2">
                        {responsableIdx !== idx && <Button onClick={() => handleSetResponsable(idx)} size="sm" className="bg-blue-600">Responsable</Button>}
                        <Button onClick={() => handleEliminarHuesped(idx)} size="sm" className="bg-red-600">Eliminar</Button>
                      </div>
                    </div>
                  ))}
                </div>
              </Card>
            )}

            <Card className="p-6">
              <h3 className="text-lg font-semibold mb-4">Buscar Huésped</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
                <div>
                  <Label>Apellido</Label>
                  <Input
                    value={busquedaHuesped.apellido}
                    onChange={(e) => handleChangeBusqueda("apellido", e.target.value)}
                    placeholder="Ej: G (primera letra)"
                    maxLength={1} // RESTAURADO
                  />
                  {erroresBusqueda.apellido && <p className="text-red-600 text-xs">{erroresBusqueda.apellido}</p>}
                </div>
                <div>
                  <Label>Nombres</Label>
                  <Input
                    value={busquedaHuesped.nombres}
                    onChange={(e) => handleChangeBusqueda("nombres", e.target.value)}
                    placeholder="Ej: A (primera letra)"
                    maxLength={1} // RESTAURADO
                  />
                </div>
                <div>
                  <Label>Tipo Documento</Label>
                  <select className="flex h-9 w-full rounded-md border border-slate-200 bg-transparent px-3 py-1 text-sm" value={busquedaHuesped.tipoDocumento} onChange={(e) => handleChangeBusqueda("tipoDocumento", e.target.value)}>
                    <option value="">Seleccionar...</option>
                    <option value="DNI">DNI</option>
                    <option value="PASAPORTE">Pasaporte</option>
                  </select>
                </div>
                <div>
                  <Label>Nro Documento</Label>
                  <Input value={busquedaHuesped.nroDocumento} onChange={(e) => handleChangeBusqueda("nroDocumento", e.target.value)} />
                </div>
              </div>
              <Button onClick={handleBuscarHuespedes} disabled={buscando} className="w-full">{buscando ? "Buscando..." : "🔍 Buscar"}</Button>
            </Card>

            {mostrarResultados && (
              <Card className="p-6">
                <h3 className="text-lg font-semibold mb-4">Resultados</h3>
                {resultadosBusqueda.length === 0 ? <p>No se encontraron resultados.</p> : (
                  <div className="space-y-2">
                    {resultadosBusqueda.map((h, idx) => (
                      <div key={idx} className="flex items-center justify-between bg-slate-50 p-4 rounded border">
                        <div>
                          <p className="font-semibold">{h.apellido}, {h.nombres}</p>
                          <p className="text-slate-600 text-sm">{h.tipoDocumento}: {h.nroDocumento}</p>
                        </div>
                        <Button onClick={() => handleSeleccionarHuesped(h)} size="sm" className="bg-green-600">Seleccionar</Button>
                      </div>
                    ))}
                  </div>
                )}
              </Card>
            )}

            <div className="flex gap-4">
              <Button onClick={handleVolverPaso} variant="outline">← Atrás</Button>
              <Button onClick={handleContinuarConfirmacion} disabled={responsableIdx === null}>Continuar →</Button>
            </div>
          </div>
        )}

        {paso === "confirmacion" && (
          <Card className="p-8">
            <h2 className="text-2xl font-semibold mb-6">Confirmación</h2>
            {!postCheckin ? (
              <>
                <div className="bg-slate-50 p-6 rounded-lg mb-6">
                  <h3 className="font-bold mb-2">Resumen Final</h3>
                  <p>Habitación: {habitacionSeleccionada?.numero}</p>
                  <p>Huéspedes: {huespedes.length}</p>
                  <p>Responsable: {huespedes[responsableIdx!]?.apellido}, {huespedes[responsableIdx!]?.nombres}</p>
                </div>
                <div className="flex gap-4">
                  <Button onClick={handleVolverPaso} variant="outline">Modificar</Button>
                  <Button onClick={handleConfirmarEstadia} disabled={loading} className="bg-green-600 hover:bg-green-700">
                    {loading ? <Loader2 className="animate-spin mr-2" /> : "✓"} Confirmar Check-In
                  </Button>
                </div>
              </>
            ) : (
              <Card className="p-6 bg-green-50 border-green-200">
                <p className="text-green-700 font-bold mb-4">Check-In realizado con éxito.</p>
                <div className="flex gap-3">
                  {/* BOTÓN SEGUIR CARGANDO CON LIMPIEZA DE ESTADO */}
                  <Button onClick={() => {
                    setHuespedes([]);
                    setResponsableIdx(null);
                    setBusquedaHuesped({ apellido: "", nombres: "", tipoDocumento: "", nroDocumento: "" });
                    setResultadosBusqueda([]);
                    setPostCheckin(false);
                    setPaso("huespedes");
                  }} className="bg-blue-600 hover:bg-blue-700">Seguir cargando</Button>

                  <Button onClick={async () => { await ejecutarCreacionEstadia(); if (!errorCreacion) { setPostCheckin(false); handleVolverASeleccion(); setPaso("fechasGrilla"); } }} className="bg-orange-600">Cargar otra habitación</Button>
                  <Button onClick={async () => { await ejecutarCreacionEstadia(); if (!errorCreacion) { router.push("/"); } }} variant="outline">Salir</Button>
                </div>
              </Card>
            )}
          </Card>
        )}
      </main>
    </div>
  );
}