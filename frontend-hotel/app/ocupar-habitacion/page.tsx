"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { DoorOpen, Calendar, Loader2, Home } from "lucide-react"; // Se agregó Home
import { buscarHuespedes, crearEstadia, buscarReservas } from "@/lib/api";
import { DtoHuesped, DtoEstadia, EstadoHabitacion } from "@/lib/types";

// --- INTERFACES ---

interface InfoCelda {
  estado: "DISPONIBLE" | "RESERVADA" | "OCUPADA" | "MANTENIMIENTO" | "FUERA_DE_SERVICIO";
  idReserva?: number;
  idEstadia?: number;
  fechaInicio?: string;
  fechaFin?: string;
}

interface HabitacionEstado {
  id: string;
  numero: string;
  tipo: string;
  capacidad: number;
  estadoHabitacion?: "HABILITADA" | "FUERA_DE_SERVICIO";
  estado: "DISPONIBLE" | "RESERVADA" | "OCUPADA" | "MANTENIMIENTO" | "FUERA_DE_SERVICIO";
  estadosPorDia?: Record<string, InfoCelda>; 
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

  const [dialogError, setDialogError] = useState<{
        titulo: string;
        mensaje: string;
        tipo: "error" | "warning" | "info";
   } | null>(null);


  const [erroresBusqueda, setErroresBusqueda] = useState<Errores>({});
  const [resultadosBusqueda, setResultadosBusqueda] = useState<DatosHuesped[]>([]);
  const [buscando, setBuscando] = useState(false);
  const [mostrarResultados, setMostrarResultados] = useState(false);
  const [responsableIdx, setResponsableIdx] = useState<number | null>(null);
  const [loading, setLoading] = useState(false);
  const [errorCarga, setErrorCarga] = useState("");

  // Estados Grilla
  const [seleccion, setSeleccion] = useState<SeleccionHabitacion | null>(null);
  const [seleccionActual, setSeleccionActual] = useState<null>(null);

  const [seleccionTieneReserva, setSeleccionTieneReserva] = useState(false);
  const [ocupandoReserva, setOcupandoReserva] = useState(false);
  const [esDuenoReserva, setEsDuenoReserva] = useState<boolean>(false);

  // --- FUNCIÓN DE LIMPIEZA TOTAL ---
  const resetearTodo = () => {
    // 1. Limpiar Selección
    setSeleccion(null);
    setSeleccionActual(null);
    setHabitacionSeleccionada(null);
    setFechaCheckIn("");
    setFechaCheckOut("");
    setConfirmacionTipo(null);
    setSeleccionTieneReserva(false);
    
    // 2. Limpiar Lógica
    setOcupandoReserva(false);
    setEsDuenoReserva(false);
    
    // 3. Limpiar Huéspedes
    setHuespedes([]);
    setResponsableIdx(null);
    
    // 4. Limpiar Buscador
    setBusquedaHuesped({ apellido: "", nombres: "", tipoDocumento: "", nroDocumento: "" });
    setResultadosBusqueda([]);
    setMostrarResultados(false);
    
    // 5. Reiniciar Flujo
    setPaso("fechasGrilla"); 
  };

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
      
      const habitacionesMapeadas: HabitacionEstado[] = data.map((h: any) => {
          const estadosPorDia = h.estadosPorDia || {};
          const todosLosEstados = Object.values(estadosPorDia).map((e: any) => e.estado);
          const esFueraDeServicio = todosLosEstados.length > 0 && todosLosEstados.every((estado: string) => estado === "MANTENIMIENTO");
          const infoHoy = estadosPorDia[fechaDesde] as InfoCelda | undefined;
          const estadoReservaBase = infoHoy?.estado || "DISPONIBLE";

          return {
              id: h.numero.toString(),
              numero: h.numero.toString(),
              tipo: h.tipoHabitacion,
              capacidad: h.capacidad,
              estadoHabitacion: (esFueraDeServicio ? "FUERA_DE_SERVICIO" : "HABILITADA"),
              estado: estadoReservaBase,
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
  
  const obtenerDatosCelda = (habitacionId: string, diaIdx: number): InfoCelda => {
    const habitacion = habitaciones.find(h => h.id === habitacionId);
    if (!habitacion) return { estado: "OCUPADA" };

    const dia = createLocalDate(fechaDesdeGrilla);
    dia.setDate(dia.getDate() + diaIdx);
    const fechaDia = dia.toISOString().split('T')[0];

    if (habitacion.estadosPorDia && habitacion.estadosPorDia[fechaDia]) {
      return habitacion.estadosPorDia[fechaDia];
    }
    return { estado: habitacion.estado };
  };

  const esCeldaSeleccionada = (habitacionId: string, diaIdx: number): boolean => {
    if (!seleccion) return false;
    return (
      seleccion.habitacionId === habitacionId &&
      diaIdx >= seleccion.diaInicio &&
      diaIdx <= seleccion.diaFin
    );
  };


// Línea 178


// Línea 192


// Línea 213


// Línea 223


// Línea 250


// Línea 398


// Línea 402


// Línea 428


// Línea 440


// Línea 485


// Línea 496


// Línea 384




  const handleClickCelda = (habitacionId: string, diaIdx: number) => {
    const hab = habitaciones.find((h) => h.id === habitacionId);
    if (!hab) return;
    
    if (diaIdx === 0) {
        setDialogError({
            titulo: "Selección no permitida",
            mensaje: "El check-in es HOY. Por favor seleccione la fecha de check-out (salida).",
            tipo: "warning"
        });
      return;
    }

    const diaInicio = 0;
    const diaFin = diaIdx;
    
    const rangoCompleto = Array.from({ length: diaFin + 1 }, (_, i) => i);
    for (const idx of rangoCompleto) {
      const info = obtenerDatosCelda(habitacionId, idx);
      if (info.estado === "OCUPADA" || info.estado === "MANTENIMIENTO" || info.estado === "FUERA_DE_SERVICIO") {
          setDialogError({
              titulo: "Rango no disponible",
              mensaje: "No se puede seleccionar ese rango. Hay días no disponibles.",
              tipo: "error"
          });
        return;
      }
    }

    const rangoPernocte = Array.from({ length: diaFin }, (_, i) => i); 
    let reservaEncontrada: InfoCelda | null = null;
    let multiplesReservas = false;

    for (const idx of rangoPernocte) {
      const info = obtenerDatosCelda(habitacionId, idx);
      if (info.estado === "RESERVADA") {
        if (!reservaEncontrada) {
          reservaEncontrada = info;
        } else if (reservaEncontrada.idReserva !== info.idReserva) {
          multiplesReservas = true;
        }
      }
    }

    if (multiplesReservas) {
        setDialogError({
            titulo: "Múltiples reservas",
            mensaje: "No puede seleccionar un rango que abarque múltiples reservas distintas.",
            tipo: "error"
        });
      return;
    }

    const fechaInicioSel = fechaDesdeGrilla; 
    const finDate = createLocalDate(fechaDesdeGrilla);
    finDate.setDate(finDate.getDate() + diaFin);
    const fechaFinSel = finDate.toISOString().split("T")[0];

    if (reservaEncontrada && reservaEncontrada.idReserva && reservaEncontrada.fechaInicio && reservaEncontrada.fechaFin) {
        const resFinStr = reservaEncontrada.fechaFin.split(" ")[0];
        if (fechaFinSel < resFinStr) {
            setDialogError({
                titulo: "Reserva incompleta",
                mensaje: `No puede ocupar parcialmente una reserva\\nEsta reserva termina el ${resFinStr}.\nDebe extender su selección hasta esa fecha o posterior.`,
                tipo: "warning"
            });
             return;
        }
    }

    setOcupandoReserva(false);
    const seleccionFinal = { habitacionId, diaInicio, diaFin };
    setSeleccion(seleccionFinal);
    setSeleccionActual(null);
    setSeleccionTieneReserva(!!reservaEncontrada);
    setFechaCheckIn(fechaInicioSel);
    setFechaCheckOut(fechaFinSel);
    setHabitacionSeleccionada(hab);
    setConfirmacionTipo(reservaEncontrada ? "reservada" : null);
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
        setDialogError({
            titulo: "Selección requerida",
            mensaje: "Debe seleccionar una habitación y un rango de fechas",
            tipo: "warning"
        });
      return;
    }
    if (confirmacionTipo) return;
    setPaso("huespedes");
  };

  const handleOcuparIgualmente = () => setConfirmacionTipo("duenioReserva");

  const handleVolverASeleccion = () => {
    handleRemoverSeleccion();
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
      setPaso("huespedes");
    }
  };

  const getEstadoColor = (estado: string) => {
    switch (estado) {
      case "DISPONIBLE": return "bg-green-600 dark:bg-green-700";
      case "RESERVADA": return "bg-orange-600 dark:bg-orange-700";
      case "OCUPADA": return "bg-red-600 dark:bg-red-700";
      case "FUERA_DE_SERVICIO": return "bg-slate-500 dark:bg-slate-600";
      case "MANTENIMIENTO": return "bg-slate-500 dark:bg-slate-600";
      default: return "bg-slate-600 dark:bg-slate-700";
    }
  };

  // --- LÓGICA HUÉSPEDES ---
  const handleChangeBusqueda = (campo: keyof BusquedaHuesped, valor: string) => {
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
        resultados = resultados.filter((h: any) => h.nroDocumento.includes(busquedaHuesped.nroDocumento));
      }
      setResultadosBusqueda(resultados);
      setMostrarResultados(true);
    } catch (error) {
      console.error("Error en búsqueda:", error);
        setDialogError({
            titulo: "Error de búsqueda",
            mensaje: "Error al buscar huésped",
            tipo: "error"
        });
    } finally {
      setBuscando(false);
    }
  };

  const handleSeleccionarHuesped = (huesped: DatosHuesped) => {
    const yaExiste = huespedes.some(
      h => h.tipoDocumento === huesped.tipoDocumento && h.nroDocumento === huesped.nroDocumento
    );
    if (yaExiste) {
        setDialogError({
            titulo: "Huésped duplicado",
            mensaje: "Este huésped ya ha sido agregado a la lista.",
            tipo: "info"
        });
      return;
    }
    if (habitacionSeleccionada && huespedes.length >= habitacionSeleccionada.capacidad) {
        setDialogError({
            titulo: "Capacidad máxima",
            mensaje: `La capacidad máxima de esta habitación es de ${habitacionSeleccionada.capacidad} personas.`,
            tipo: "warning"
        });return;
    }
    const nuevaLista = [...huespedes, huesped];
    setHuespedes(nuevaLista);
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
        setDialogError({
            titulo: "Responsable requerido",
            mensaje: "Debe seleccionar un huésped responsable",
            tipo: "warning"
        });
      return;
    }
    setPaso("confirmacion");
  };

  // --- CONFIRMACIÓN Y GUARDADO UNIFICADO ---
  const handleConfirmarYGuardar = async (accionPosterior: "reset" | "salir") => {
    // 1. Validaciones previas
    if (!habitacionSeleccionada || huespedes.length === 0) {
        setDialogError({
            titulo: "Datos incompletos",
            mensaje: "Faltan datos para realizar el check-in",
            tipo: "error"
        });
      return;
    }

    setLoading(true);

    try {
      // 2. Construir el DTO
      const dias = seleccion ? seleccion.diaFin - seleccion.diaInicio : 1;

      const estadiaPayload: DtoEstadia = {
        fechaCheckIn: fechaCheckIn,
        fechaCheckOut: fechaCheckOut || undefined,
        valorEstadia: habitacionSeleccionada.precioNoche * dias, 
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

      // 3. Vincular Reserva
      if (ocupandoReserva && esDuenoReserva && fechaCheckIn && fechaCheckOut) {
        try {
          const reservas = await buscarReservas(fechaCheckIn, fechaCheckOut, habitacionSeleccionada.numero)
          if (reservas && reservas.length > 0) {
             const r = reservas.find((res: any) => res.estadoReserva === "ACTIVA");
             if (r) {
                estadiaPayload.dtoReserva = {
                  idReserva: r.idReserva,
                  estadoReserva: r.estadoReserva,
                  fechaDesde: r.fechaDesde,
                  fechaHasta: r.fechaHasta,
                  nombreHuespedResponsable: r.nombreHuespedResponsable,
                  apellidoHuespedResponsable: r.apellidoHuespedResponsable,
                  telefonoHuespedResponsable: r.telefonoHuespedResponsable,
                  idHabitacion: r.idHabitacion,
                } as any;
             }
          }
        } catch (e) {
          console.warn("No se pudo vincular reserva:", e)
        }
      }

      // 4. LLAMADA A LA API
      await crearEstadia(estadiaPayload);
      
      // 5. ÉXITO y ACCIÓN POSTERIOR
        setDialogError({
            titulo: "Check-in exitoso",
            mensaje: "Check-in realizado correctamente.",
            tipo: "info"
        });
      
      if (accionPosterior === "reset") {
          resetearTodo();
      } else {
          router.push("/");
      }

    } catch (error: any) {
      console.error("Error al guardar estadía:", error);
        setDialogError({
            titulo: "Error al guardar",
            mensaje: "Error al realizar el check-in: " + (error.message || "Error desconocido"),
            tipo: "error"
        });
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


  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50 dark:from-slate-950 dark:via-slate-900 dark:to-slate-950">
      <main className="mx-auto max-w-6xl px-4 py-12 sm:px-6 lg:px-8">
        <div className="mb-8">
          {/* HEADER MODIFICADO: Botón a la derecha */}
          <div className="mb-6 flex items-center justify-between">
            <div className="flex items-center gap-4">
              <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-gradient-to-br from-blue-600 to-indigo-600 text-white shadow-lg">
                <DoorOpen className="h-6 w-6" />
              </div>
              <div>
                <p className="text-xs font-semibold uppercase tracking-wider text-blue-600 dark:text-blue-400">Caso de Uso 15</p>
                <h1 className="text-3xl font-bold text-slate-900 dark:text-slate-50">Ocupar Habitación (Check-In)</h1>
              </div>
            </div>
            <Button asChild variant="outline">
                <Link href="/">
                    <Home className="mr-2 h-4 w-4" />
                    Volver al Menú Principal
                </Link>
            </Button>
          </div>
        </div>

        {/* --- PASOS VISUALES --- */}
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
                            const infoCelda = obtenerDatosCelda(hab.id, diaIdx);
                            const infoPrev = diaIdx > 0 ? obtenerDatosCelda(hab.id, diaIdx - 1) : null;
                            const seleccionada = esCeldaSeleccionada(hab.id, diaIdx);
                            const baseColor = getEstadoColor(infoCelda.estado);
                            
                            let bordeClase = "border border-slate-200"; 
                            if (infoCelda.estado === "RESERVADA" && infoPrev?.estado === "RESERVADA" && infoCelda.idReserva !== infoPrev.idReserva) {
                                bordeClase = "border-t-4 border-t-white border-x border-b border-slate-200";
                            }
                            
                            return (
                              <td key={`${hab.id}-${diaIdx}`} className={`p-0 text-center relative ${bordeClase}`}>
                                <div 
                                    onClick={() => handleClickCelda(hab.id, diaIdx)} 
                                    className={`
                                        h-10 flex items-center justify-center text-xs font-semibold text-white transition cursor-pointer
                                        ${seleccionada || (diaIdx === 0 && seleccion?.habitacionId === hab.id) 
                                            ? "bg-blue-600 z-10 relative ring-2 ring-blue-600 ring-offset-1" 
                                            : infoCelda.estado === "OCUPADA" ? `${baseColor} cursor-not-allowed opacity-80` 
                                            : baseColor
                                        }
                                    `}
                                >
                                  {seleccionada || (diaIdx === 0 && seleccion?.habitacionId === hab.id) ? "✓" 
                                    : infoCelda.estado === "RESERVADA" ? "R" 
                                    : infoCelda.estado === "OCUPADA" ? "X" 
                                    : (infoCelda.estado === "FUERA_DE_SERVICIO" || infoCelda.estado === "MANTENIMIENTO") ? "FS" 
                                    : "○"}
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
                <p className="text-slate-700 mb-4">Esta habitación tiene una reserva para las fechas seleccionadas.</p>
                <div className="flex gap-3">
                  <Button onClick={handleOcuparIgualmente} className="bg-orange-600 hover:bg-orange-700">Ocupar Igualmente</Button>
                  <Button onClick={handleVolverASeleccion} variant="outline">Cancelar</Button>
                </div>
              </Card>
            )}

            {confirmacionTipo === "duenioReserva" && (
              <Card className="border-2 border-blue-500 bg-blue-50 p-6">
                <h3 className="text-xl font-bold text-blue-600 mb-2">Titularidad de Reserva</h3>
                <p className="text-slate-700 mb-4">¿Es el cliente actual el titular de la reserva existente?</p>
                <div className="flex gap-3">
                  <Button onClick={() => handleEsDuenioReserva(true)} className="bg-green-600 hover:bg-green-700">Sí, vincular reserva</Button>
                  <Button onClick={() => handleEsDuenioReserva(false)} className="bg-blue-600 hover:bg-blue-700">No, es otro cliente</Button>
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
                    maxLength={1}
                  />
                  {erroresBusqueda.apellido && <p className="text-red-600 text-xs">{erroresBusqueda.apellido}</p>}
                </div>
                <div>
                  <Label>Nombres</Label>
                  <Input
                    value={busquedaHuesped.nombres}
                    onChange={(e) => handleChangeBusqueda("nombres", e.target.value)}
                    placeholder="Ej: A (primera letra)"
                    maxLength={1}
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
            <div className="bg-slate-50 p-6 rounded-lg mb-6">
              <h3 className="font-bold mb-2">Resumen Final</h3>
              <p>Habitación: {habitacionSeleccionada?.numero}</p>
              <p>Huéspedes: {huespedes.length}</p>
              <p>Responsable: {huespedes[responsableIdx!]?.apellido}, {huespedes[responsableIdx!]?.nombres}</p>
              {ocupandoReserva && <p className="text-blue-600 font-semibold mt-2">✓ Vinculado a Reserva Existente</p>}
            </div>

            {/* Listado de huéspedes */}
            <div className="bg-slate-50 p-6 rounded-lg mb-6">
              <h3 className="font-semibold mb-2">Listado de Huéspedes</h3>
              <ul className="space-y-2">
                {huespedes.map((h, idx) => (
                  <li key={idx} className="flex items-center gap-2">
                    <span className="font-semibold">
                      {responsableIdx === idx && <span title="Responsable" className="text-blue-600">👤</span>}
                      {h.apellido}, {h.nombres}
                    </span>
                    <span className="text-slate-600 text-sm">({h.tipoDocumento}: {h.nroDocumento})</span>
                  </li>
                ))}
              </ul>
            </div>

            <div className="flex flex-col gap-3">
              {/* BOTÓN 1: Modificar (Vuelve atrás) */}
              <Button 
                onClick={handleVolverPaso} 
                variant="outline" 
                className="w-full border-blue-600 text-blue-600 hover:bg-blue-50"
              >
                  Volver a la selección de Huéspedes
              </Button>
              
              <div className="flex gap-4">
                  {/* BOTÓN 2: Guardar y Resetear */}
                  <Button 
                    onClick={() => handleConfirmarYGuardar("reset")} 
                    disabled={loading} 
                    className="flex-1 bg-blue-600 hover:bg-blue-700"
                  >
                    {loading ? <Loader2 className="animate-spin mr-2" /> : null}
                      Confirmar y cargar otra habitación
                  </Button>

                  {/* BOTÓN 3: Guardar y Salir */}
                  <Button 
                    onClick={() => handleConfirmarYGuardar("salir")} 
                    disabled={loading} 
                    className="flex-1 bg-green-600 hover:bg-green-700"
                  >
                    {loading ? <Loader2 className="animate-spin mr-2" /> : null} 
                    Confirmar y Salir al Menú Principal
                  </Button>
              </div>
            </div>
          </Card>
        )}
          {dialogError && (
              <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50" onClick={() => setDialogError(null)}>
                  <Card className={`w-full max-w-md p-6 border-2 ${
                      dialogError.tipo === "error" ? "border-red-500" :
                          dialogError.tipo === "warning" ? "border-orange-500" :
                              "border-blue-500"
                  }`} onClick={(e) => e.stopPropagation()}>
                      <h3 className={`text-xl font-bold mb-4 ${
                          dialogError.tipo === "error" ? "text-red-600" :
                              dialogError.tipo === "warning" ? "text-orange-600" :
                                  "text-blue-600"
                      }`}>
                          {dialogError.titulo}
                      </h3>
                      <p className="text-slate-700 mb-6 whitespace-pre-line">{dialogError.mensaje}</p>
                      <Button
                          onClick={() => setDialogError(null)}
                          className={`w-full ${
                              dialogError.tipo === "error" ? "bg-red-600 hover:bg-red-700" :
                                  dialogError.tipo === "warning" ? "bg-orange-600 hover:bg-orange-700" :
                                      "bg-blue-600 hover:bg-blue-700"
                          }`}
                      >
                          Entendido
                      </Button>
                  </Card>
              </div>
          )}

      </main>
    </div>
  );
}