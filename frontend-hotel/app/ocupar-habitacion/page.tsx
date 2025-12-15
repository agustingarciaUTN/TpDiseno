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

// Orden de tipos de habitación según el enum del backend
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

// Helper function to create date in local timezone
const createLocalDate = (dateString: string): Date => {
  const [year, month, day] = dateString.split("-").map(Number);
  return new Date(year, month - 1, day);
};

export default function OcuparHabitacion() {
  const router = useRouter();
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

  // Estados para la grilla interactiva (estilo CU4)
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
        if (!valor.trim()) return ""; // Opcional
        if (valor.length < 1 || valor.length > 50)
          return "Debe tener entre 1 y 50 caracteres";
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

  const handleConfirmarFechasGrilla = async () => {
    if (validarFechasGrilla()) {
      // Resetear selección al cambiar fechas de grilla
      setSeleccion(null);
      setSeleccionActual(null);

      // Mostrar loader
      setLoading(true);
      
      // Calcular fechas ahora para pasarlas directamente
      const hoy = new Date();
      hoy.setHours(0, 0, 0, 0);
      const fechaDesde = hoy.toISOString().split("T")[0];
      const fechaHasta = fechaHastaGrilla;
      
      // Cambiar a grilla primero para mostrar el loader
      setPaso("grilla");
      
      // Esperar un tick para que se renderice el loader
      await new Promise(resolve => setTimeout(resolve, 100));
      
      // LUEGO cargar las habitaciones
      const success = await recargarHabitacionesConNuevasFechas(fechaDesde, fechaHasta);
      
      // Si falló, volver al paso anterior
      if (!success) {
        setPaso("fechasGrilla");
      }
      
      setLoading(false);
    }
  };

  const recargarHabitacionesConNuevasFechas = async (fechaDesde: string, fechaHasta: string): Promise<boolean> => {
    if (!fechaDesde || !fechaHasta) {
      console.error("[CU15] No se pueden recargar: fechas no definidas", { fechaDesde, fechaHasta });
      setErrorCarga("Fechas no válidas");
      return false;
    }
    
    try {
      console.log(`[CU15] Solicitando datos: fechaDesde=${fechaDesde}, fechaHasta=${fechaHasta}`);
      const response = await fetch(`http://localhost:8080/api/habitaciones/estados?fechaDesde=${fechaDesde}&fechaHasta=${fechaHasta}`);
      
      if (!response.ok) {
        const errorText = await response.text();
        console.error(`[CU15] Error del backend (${response.status}):`, errorText);
        setErrorCarga(`Error ${response.status}: ${errorText}`);
        return false;
      }
      
      const data = await response.json();
      console.log("[CU15] Datos recargados del backend:", data);
      console.log("[CU15] Cantidad de habitaciones:", data.length);
      
      if (!Array.isArray(data) || data.length === 0) {
        console.error("[CU15] No hay habitaciones en la respuesta");
        setErrorCarga("No se encontraron habitaciones");
        return false;
      }
      
      const habitacionesMapeadas = data.map((h: any) => {
        const estadosPorDia = h.estadosPorDia || {};
        
        // Detectar si la habitación está fuera de servicio
        // (el backend envía "MANTENIMIENTO" en estadosPorDia cuando está fuera de servicio)
        const todosLosEstados = Object.values(estadosPorDia);
        const esFueraDeServicio = todosLosEstados.length > 0 && 
          todosLosEstados.every((estado: any) => estado === "MANTENIMIENTO");
        
        const estadoReservaBase = (estadosPorDia[fechaDesde]) || "DISPONIBLE";
        
        const mapeada = {
          id: h.numero.toString(),
          numero: h.numero.toString(),
          tipo: h.tipoHabitacion,
          capacidad: h.capacidad,
          estadoHabitacion: esFueraDeServicio ? "FUERA_DE_SERVICIO" : "HABILITADA",
          estado: estadoReservaBase as "DISPONIBLE" | "RESERVADA" | "OCUPADA",
          estadosPorDia: estadosPorDia,
          precioNoche: h.costoPorNoche
        };
        return mapeada;
      });
      
      console.log("[CU15] Habitaciones mapeadas:", habitacionesMapeadas.length);
      setHabitaciones(habitacionesMapeadas);
      setFechaDesdeGrilla(fechaDesde);
      setFechaHastaGrilla(fechaHasta);
      setErrorCarga("");
      return true;
    } catch (error) {
      console.error("[CU15] Error al recargar habitaciones:", error);
      setErrorCarga("Error de conexión al cargar habitaciones");
      return false;
    }
  };

  // Generar días del rango de grilla
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

  // Funciones para la grilla interactiva (estilo CU4)
  const esCeldaDisponible = (habitacionId: string, diaIdx: number): boolean => {
    const habitacion = habitaciones.find((h: HabitacionEstado) => h.id === habitacionId);
    if (!habitacion) return false;

    // Verificar el estado específico de este día
    const estadoDia = obtenerEstadoCelda(habitacionId, diaIdx, fechaDesdeGrilla);
    
    // Permitir click también en reservadas (para ofrecer ocupar igual)
    if (estadoDia === "OCUPADA" || estadoDia === "FUERA_DE_SERVICIO") return false;
    
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
    const hab = habitaciones.find((h: HabitacionEstado) => h.id === habitacionId);
    
    // No permitir clicks en habitaciones ocupadas
    if (!hab || hab.estado === "OCUPADA") return;

    // No permitir seleccionar HOY (diaIdx 0)
    if (diaIdx === 0) {
      alert("El check-in es HOY. Seleccione la fecha de check-out (desde mañana en adelante).");
      return;
    }

    // fechaDesde siempre es HOY (diaIdx 0), diaIdx es el check-out
    const diaInicio = 0; // Siempre HOY
    const diaFin = diaIdx;
    const dias = generarDias(fechaDesdeGrilla, fechaHastaGrilla);

    // Crear rango de índices seleccionados
    const rangoDias = Array.from(
      { length: Math.abs(diaFin - diaInicio) + 1 },
      (_, i) => Math.min(diaInicio, diaFin) + i
    );

    // Revisar estados del rango
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

    // Crear selección final desde HOY hasta el día seleccionado
    setOcupandoReserva(false);

    const seleccionFinal = {
      habitacionId,
      diaInicio: 0, // Siempre HOY
      diaFin: diaIdx,
    };
    setSeleccion(seleccionFinal);
    setSeleccionActual(null);
    setSeleccionTieneReserva(hayReservada);

    // Auto-poblar fechas
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
      // Continuar con el flujo normal; marcar que se ocupa una reserva
      setOcupandoReserva(true);
      setEsDuenoReserva(true);
      setConfirmacionTipo(null);
      setPaso("huespedes");
    } else {
      // Volver a la grilla si no es dueño
      setOcupandoReserva(false);
      setEsDuenoReserva(false);
      setConfirmacionTipo(null);
      handleVolverASeleccion();
    }
  };

  // Obtener el estado de una celda específica por día
  const obtenerEstadoCelda = (habitacionId: string, diaIdx: number, fechaDesde: string): "DISPONIBLE" | "RESERVADA" | "OCUPADA" | "FUERA_DE_SERVICIO" => {
    const habitacion = habitaciones.find(h => h.id === habitacionId);
    if (!habitacion) {
      return "OCUPADA";
    }

    // Si la habitación no está habilitada, mostrar FUERA_DE_SERVICIO
    if (habitacion.estadoHabitacion === "FUERA_DE_SERVICIO") {
      return "FUERA_DE_SERVICIO";
    }

    // Si está habilitada, buscar el estado del día específico
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
      case "DISPONIBLE":
        return "bg-green-600 dark:bg-green-700";
      case "RESERVADA":
        return "bg-orange-600 dark:bg-orange-700";
      case "OCUPADA":
        return "bg-red-600 dark:bg-red-700";
      case "FUERA_DE_SERVICIO":
        return "bg-slate-500 dark:bg-slate-600";
      default:
        return "bg-slate-600 dark:bg-slate-700";
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
      // Llamada real al backend
      const criterios: any = {
        apellido: busquedaHuesped.apellido,
        nombres: busquedaHuesped.nombres,
        nroDocumento: busquedaHuesped.nroDocumento
      };
      
      // Solo incluir tipoDocumento si tiene un valor
      if (busquedaHuesped.tipoDocumento) {
        criterios.tipoDocumento = busquedaHuesped.tipoDocumento;
      }
      
      const data = await buscarHuespedes(criterios);
      
      // Mapear respuesta del backend al formato del componente
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

  const handleSeleccionarHuesped = (huesped: DatosHuesped) => {
    // Si ya existe, lo movemos al frente como responsable
    const sinDuplicado = huespedes.filter(
      h => !(h.tipoDocumento === huesped.tipoDocumento && h.nroDocumento === huesped.nroDocumento)
    );
    // Nuevo responsable primero, el resto queda como acompañante
    setHuespedes([huesped, ...sinDuplicado]);
    setResponsableIdx(0);
    setMostrarResultados(false);
    setBusquedaHuesped({ apellido: "", nombres: "", tipoDocumento: "", nroDocumento: "" });
    setErroresBusqueda({});
  };

  const handleEliminarHuesped = (index: number) => {
    const nuevaLista = huespedes.filter((_, i) => i !== index);
    setHuespedes(nuevaLista);
    setResponsableIdx(prev => {
      if (prev === null) return null;
      if (prev === index) return null; // responsable eliminado, hay que elegir otro
      if (prev > index) return prev - 1; // ajustar índice si se eliminó alguien antes
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

  const handleConfirmarEstadia = async () => {
    try {
      setLoading(true);
      
      if (!habitacionSeleccionada || huespedes.length === 0) {
        alert("Faltan datos para preparar el check-in");
        return;
      }

      // Calcular valor de la estadía
      const dias = seleccion ? seleccion.diaFin - seleccion.diaInicio + 1 : 1;
      const valorEstadia = habitacionSeleccionada.precioNoche * dias;

      // Crear el objeto DtoEstadia - Solo enviar el primer huésped (responsable)
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
        // Enviar TODOS los huéspedes - el backend marca al primero como responsable=SI
        dtoHuespedes: huespedes.map(h => ({
          idHuesped: 0,
          apellido: h.apellido,
          nombres: h.nombres,
          tipoDocumento: h.tipoDocumento as any,
          nroDocumento: h.nroDocumento,
        }))
      };

      // Si estamos ocupando una reserva, buscar y vincular la reserva
      if (ocupandoReserva && esDuenoReserva && fechaCheckIn && fechaCheckOut) {
        try {
          const reservas = await buscarReservas(fechaCheckIn, fechaCheckOut, habitacionSeleccionada.numero)
          if (reservas && reservas.length > 0) {
            // Tomamos la primera reserva coincidente
            const r = reservas[0]
            // Vincular sólo por idReserva
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

      // No crear aún: guardar como pendiente y mostrar opciones
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
          <p className="text-slate-600 dark:text-slate-400">
            Registrar el check-in de huéspedes en una habitación del hotel.
          </p>
        </div>

        <Card className="mb-6 border-blue-200 bg-blue-50/50 p-4 dark:border-blue-900 dark:bg-blue-950/20">
          <div className="flex items-center justify-between text-sm">
            <div className={`flex-1 text-center ${paso === "fechasGrilla" ? "font-bold text-blue-600 dark:text-blue-400" : "text-slate-500 dark:text-slate-600"}`}>
              1. Fechas Grilla
            </div>
            <div className="text-slate-400">→</div>
            <div className={`flex-1 text-center ${paso === "grilla" ? "font-bold text-blue-600 dark:text-blue-400" : "text-slate-500 dark:text-slate-600"}`}>
              2. Habitación
            </div>
            <div className="text-slate-400">→</div>
            <div className={`flex-1 text-center ${paso === "huespedes" ? "font-bold text-blue-600 dark:text-blue-400" : "text-slate-500 dark:text-slate-600"}`}>
              3. Huéspedes
            </div>
            <div className="text-slate-400">→</div>
            <div className={`flex-1 text-center ${paso === "confirmacion" ? "font-bold text-blue-600 dark:text-blue-400" : "text-slate-500 dark:text-slate-600"}`}>
              4. Confirmar
            </div>
          </div>
        </Card>

        {paso === "fechasGrilla" && (
          <Card className="p-6">
            <h2 className="text-xl font-semibold mb-4 text-slate-900 dark:text-slate-50">Paso 1: Rango de Fechas para la Grilla</h2>
            <p className="text-sm text-slate-600 dark:text-slate-400 mb-6">
              El check-in se realizará HOY. Seleccione hasta qué fecha desea visualizar la disponibilidad.
            </p>
            
            <Card className="mb-6 border-blue-200 bg-blue-50/50 p-4 dark:border-blue-900 dark:bg-blue-950/20">
              <p className="text-sm font-semibold text-slate-900 dark:text-slate-50">
                <Calendar className="inline h-4 w-4 mr-2" />
                Fecha de Check-In (Inicio): <span className="text-blue-600 dark:text-blue-400">{new Date().toLocaleDateString('es-AR', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}</span>
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
            {errorFechaGrilla && (
              <Card className="mt-4 border-red-200 bg-red-50 p-3 dark:border-red-900 dark:bg-red-950/20">
                <p className="text-sm text-red-600 dark:text-red-400">{errorFechaGrilla}</p>
              </Card>
            )}
            <div className="mt-6 flex gap-3">
              <Button onClick={handleConfirmarFechasGrilla} className="gap-2">
                Continuar
                <Calendar className="h-4 w-4" />
              </Button>
              <Button variant="outline" asChild>
                <Link href="/">
                  <Home className="mr-2 h-4 w-4" />
                    Volver al Menú Principal
                </Link>
              </Button>
            </div>
          </Card>
        )}

        {paso === "grilla" && (
          <div className="space-y-6">
            {errorCarga && (
              <Card className="p-4 border-red-200 bg-red-50 dark:border-red-900 dark:bg-red-950/20">
                <p className="text-red-600 dark:text-red-400">{errorCarga}</p>
              </Card>
            )}
            {loading || diasRango.length === 0 || habitaciones.length === 0 ? (
              <Card className="p-12 flex flex-col items-center justify-center gap-4">
                <Loader2 className="w-12 h-12 animate-spin text-blue-600" />
                <p className="text-lg font-semibold text-slate-700 dark:text-slate-300">Procesando datos...</p>
                {habitaciones.length === 0 && !loading && (
                  <p className="text-sm text-slate-500">Esperando habitaciones...</p>
                )}
              </Card>
            ) : (
              <>
            <div className="grid grid-cols-3 gap-4 mb-6">
              <div className="bg-white border border-slate-200 rounded-lg p-4 dark:bg-slate-900 dark:border-slate-700">
                <p className="text-slate-600 text-sm dark:text-slate-400">Disponibles</p>
                <p className="text-2xl font-bold text-green-600 dark:text-green-400">{conteo.disponibles}</p>
              </div>
              <div className="bg-white border border-slate-200 rounded-lg p-4 dark:bg-slate-900 dark:border-slate-700">
                <p className="text-slate-600 text-sm dark:text-slate-400">Reservadas</p>
                <p className="text-2xl font-bold text-orange-600 dark:text-orange-400">{conteo.reservadas}</p>
              </div>
              <div className="bg-white border border-slate-200 rounded-lg p-4 dark:bg-slate-900 dark:border-slate-700">
                <p className="text-slate-600 text-sm dark:text-slate-400">Ocupadas</p>
                <p className="text-2xl font-bold text-slate-600 dark:text-slate-400">{conteo.ocupadas}</p>
              </div>
            </div>

            <Card className="p-6">
              <h2 className="text-xl font-semibold mb-4 text-slate-900 dark:text-slate-50">
                Grilla de disponibilidad: {createLocalDate(fechaDesdeGrilla).toLocaleDateString()} al {createLocalDate(fechaHastaGrilla).toLocaleDateString()}
              </h2>

              <p className="text-slate-600 text-sm mb-4 dark:text-slate-400">
                Haga click en una celda disponible para iniciar la selección, luego haga click en otra celda de la misma habitación para completar el rango.
              </p>

              <div className="mb-4 flex gap-6 text-xs text-slate-600 dark:text-slate-400">
                <span>📊 Disponibles: {conteo.disponibles}</span>
                <span>📅 Reservadas: {conteo.reservadas}</span>
                <span>🚫 Ocupadas: {conteo.ocupadas}</span>
              </div>

              <div className="overflow-x-auto">
                <table className="w-full border-collapse text-sm">
                  <thead>
                    <tr className="bg-slate-100 dark:bg-slate-800">
                      <th className="border border-slate-300 dark:border-slate-700 px-4 py-2 text-slate-900 dark:text-slate-50 font-semibold">Fecha</th>
                      {/* Agrupar por tipo de habitación */}
                      {TIPOS_HABITACION_ORDEN.map((tipo) => {
                        const habsTipo = habitaciones.filter((h: HabitacionEstado) => h.tipo === tipo);
                        if (habsTipo.length === 0) return null;
                        return (
                          <th key={tipo} colSpan={habsTipo.length} className="border border-slate-300 dark:border-slate-700 px-2 py-2 text-center text-slate-900 dark:text-slate-50 font-bold bg-blue-50 dark:bg-blue-900/30">
                            {formatearTipo(tipo)}
                          </th>
                        );
                      })}
                    </tr>
                    <tr className="bg-slate-50 dark:bg-slate-800/50">
                      <th className="border border-slate-300 dark:border-slate-700 px-4 py-2"></th>
                      {/* Sub-encabezados con números de habitación */}
                      {TIPOS_HABITACION_ORDEN.map((tipo) => {
                        const habsTipo = habitaciones.filter((h: HabitacionEstado) => h.tipo === tipo).sort((a, b) => parseInt(a.numero) - parseInt(b.numero));
                        return habsTipo.map((hab) => (
                          <th key={hab.id} className="border border-slate-300 dark:border-slate-700 px-2 py-2 text-center text-slate-900 dark:text-slate-50 font-semibold min-w-[80px] text-xs">
                            Hab. {hab.numero}
                          </th>
                        ));
                      })}
                    </tr>
                  </thead>
                  <tbody>
                    {diasRango.map((dia, diaIdx) => (
                      <tr
                        key={diaIdx}
                        className={diaIdx % 2 === 0 ? "bg-slate-50 dark:bg-slate-800/50" : "bg-white dark:bg-transparent"}
                      >
                        <td className="border border-slate-300 dark:border-slate-700 px-4 py-2 font-semibold text-slate-900 dark:text-slate-200">
                          <div className="text-sm">{dia.toLocaleDateString("es-AR", { weekday: "short", day: "2-digit", month: "2-digit" })}</div>
                          {diaIdx === 0 && <div className="text-xs text-blue-600 dark:text-blue-400 font-bold">HOY (Check-in)</div>}
                        </td>
                        {/* Mostrar celdas por tipo de habitación */}
                        {TIPOS_HABITACION_ORDEN.map((tipo) => {
                          const habsTipo = habitaciones.filter((h: HabitacionEstado) => h.tipo === tipo).sort((a, b) => parseInt(a.numero) - parseInt(b.numero));
                          return habsTipo.map((hab) => {
                            const disponible = esCeldaDisponible(hab.id, diaIdx);
                            const seleccionada = esCeldaSeleccionada(hab.id, diaIdx);
                            const inicioActual = esInicioSeleccionActual(hab.id, diaIdx);
                            const estadoDia = obtenerEstadoCelda(hab.id, diaIdx, fechaDesdeGrilla);
                              const baseColor = getEstadoColor(estadoDia);

                            return (
                              <td
                                key={`${hab.id}-${diaIdx}`}
                                className="border border-slate-300 dark:border-slate-700 px-2 py-2 text-center"
                              >
                                <div
                                  onClick={() => handleClickCelda(hab.id, diaIdx)}
                                  className={`rounded px-2 py-1 text-xs font-semibold text-white transition ${
                                    seleccionada || (diaIdx === 0 && seleccion?.habitacionId === hab.id)
                                      ? "bg-blue-600 hover:bg-blue-700 cursor-pointer"
                                      : estadoDia === "RESERVADA" || estadoDia === "OCUPADA" || estadoDia === "FUERA_DE_SERVICIO"
                                      ? `${baseColor} cursor-not-allowed opacity-90`
                                      : `${baseColor} ${diaIdx === 0 ? "opacity-80" : "hover:brightness-110"} cursor-pointer`
                                  }`}
                                  title={
                                    seleccionada || (diaIdx === 0 && seleccion?.habitacionId === hab.id)
                                      ? "Seleccionada"
                                      : diaIdx === 0
                                      ? "Check-in HOY"
                                      : disponible
                                      ? "Click para seleccionar Check-out"
                                      : estadoDia === "RESERVADA"
                                      ? "Reservada"
                                      : estadoDia === "OCUPADA"
                                      ? "Ocupada"
                                      : "Fuera de servicio"
                                  }
                                >
                                  {seleccionada || (diaIdx === 0 && seleccion?.habitacionId === hab.id)
                                    ? "✓"
                                    : estadoDia === "RESERVADA"
                                    ? "R"
                                    : estadoDia === "OCUPADA"
                                    ? "X"
                                    : estadoDia === "FUERA_DE_SERVICIO"
                                    ? "FS"
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
                <div className="mt-4 text-xs text-slate-600 dark:text-slate-400 flex gap-4">
                  <span>✓ = Seleccionada</span>
                  <span>○ = Disponible</span>
                  <span>R = Reservada</span>
                  <span>X = Ocupada</span>
                  <span>FS = Fuera de servicio</span>
                </div>
              </div>
            </Card>

            {/* Resumen de selección */}
            {seleccion && (
              <Card className="p-6">
                <h3 className="text-lg font-semibold mb-4 text-slate-900 dark:text-slate-50">Habitación y fechas seleccionadas</h3>
                <div className="bg-slate-50 p-4 rounded dark:bg-slate-800">
                  {(() => {
                    const hab = habitaciones.find((h: HabitacionEstado) => h.id === seleccion.habitacionId);
                    if (!hab) return null;
                    const noches = seleccion.diaFin - seleccion.diaInicio + 1;
                    const subtotal = hab.precioNoche * noches;
                    return (
                      <div className="flex justify-between items-center">
                        <div>
                          <p className="font-semibold text-slate-900 dark:text-white">Habitación {hab.numero} ({hab.tipo})</p>
                          <p className="text-sm text-slate-600 dark:text-slate-400">
                            {diasRango[seleccion.diaInicio]?.toLocaleDateString()} - {diasRango[seleccion.diaFin]?.toLocaleDateString()} ({noches} noche{noches > 1 ? "s" : ""})
                          </p>
                        </div>
                        <div className="flex items-center gap-4">
                          <p className="text-blue-600 font-semibold dark:text-blue-400">${subtotal}</p>
                          <button
                            onClick={handleRemoverSeleccion}
                            className="text-red-600 hover:text-red-700 dark:text-red-400 dark:hover:text-red-300 text-sm"
                          >
                            ✕ Quitar
                          </button>
                        </div>
                      </div>
                    );
                  })()}
                </div>
              </Card>
            )}

            {/* Diálogo de confirmación: Habitación reservada en el rango */}
            {confirmacionTipo === "reservada" && seleccion && (
              <Card className="border-2 border-orange-500 bg-orange-50/50 p-6 dark:border-orange-600 dark:bg-orange-950/20">
                <div className="flex items-start gap-4">
                  <div className="text-orange-600 text-3xl dark:text-orange-400">⚠️</div>
                  <div className="flex-1">
                    <h3 className="text-xl font-bold text-orange-600 mb-2 dark:text-orange-400">Habitación con reserva</h3>
                    <p className="text-slate-700 mb-2 dark:text-slate-200">
                      El rango seleccionado incluye días reservados.
                    </p>
                    <p className="text-slate-700 mb-2 text-sm dark:text-slate-200">
                      Rango: {diasRango[seleccion.diaInicio]?.toLocaleDateString()} — {diasRango[seleccion.diaFin]?.toLocaleDateString()}
                    </p>
                    <p className="text-slate-600 mb-4 text-sm dark:text-slate-300">
                      Titular: dato no disponible (se mostrará desde el backend si está en la reserva).
                    </p>
                    <p className="text-slate-600 text-sm mb-6 dark:text-slate-300">
                      ¿Desea ocuparla igualmente?
                    </p>
                    <div className="flex gap-3">
                      <Button
                        onClick={handleOcuparIgualmente}
                        className="bg-orange-600 hover:bg-orange-700 dark:bg-orange-500 dark:hover:bg-orange-600"
                      >
                        Ocupar Igualmente
                      </Button>
                      <Button
                        onClick={handleVolverASeleccion}
                        variant="outline"
                      >
                        Volver
                      </Button>
                    </div>
                  </div>
                </div>
              </Card>
            )}

            {/* Diálogo de confirmación: ¿Es dueño de la reserva? */}
            {confirmacionTipo === "duenioReserva" && seleccion && (
              <Card className="border-2 border-blue-500 bg-blue-50/50 p-6 dark:border-blue-600 dark:bg-blue-950/20">
                <div className="flex items-start gap-4">
                  <div className="text-blue-600 text-3xl dark:text-blue-400">❓</div>
                  <div className="flex-1">
                    <h3 className="text-xl font-bold text-blue-600 mb-2 dark:text-blue-400">Confirmación de Reserva</h3>
                    <p className="text-slate-700 mb-4 dark:text-slate-200">
                      ¿Es usted el dueño de esta reserva?
                    </p>
                    <div className="flex gap-3">
                      <Button
                        onClick={() => handleEsDuenioReserva(true)}
                        className="bg-green-600 hover:bg-green-700 dark:bg-green-500 dark:hover:bg-green-600"
                      >
                        Sí
                      </Button>
                      <Button
                        onClick={() => handleEsDuenioReserva(false)}
                        className="bg-red-600 hover:bg-red-700 dark:bg-red-500 dark:hover:bg-red-600"
                      >
                        No
                      </Button>
                    </div>
                  </div>
                </div>
              </Card>
            )}

            <div className="flex justify-between">
              <Button onClick={handleVolverPaso} variant="outline">
                ← Atrás
              </Button>
              <Button 
                onClick={handleContinuarDesdeGrilla} 
                disabled={!seleccion || confirmacionTipo !== null}
              >
                Continuar →
              </Button>
            </div>
              </>
            )}
          </div>
        )}

        {paso === "huespedes" && (
          <div className="space-y-6">
            <Card className="p-6">
              <h3 className="text-lg font-semibold mb-4 text-slate-900 dark:text-slate-50">Resumen de la Estadía</h3>
              <div className="grid grid-cols-3 gap-4 text-sm">
                <div><p className="text-slate-600 dark:text-slate-400">Habitación</p><p className="text-slate-900 dark:text-white font-semibold">{habitacionSeleccionada?.numero}</p></div>
                <div><p className="text-slate-600 dark:text-slate-400">Check-In</p><p className="text-slate-900 dark:text-white font-semibold">{fechaCheckIn ? createLocalDate(fechaCheckIn).toLocaleDateString() : "-"}</p></div>
                <div><p className="text-slate-600 dark:text-slate-400">Check-Out</p><p className="text-slate-900 dark:text-white font-semibold">{fechaCheckOut ? createLocalDate(fechaCheckOut).toLocaleDateString() : "-"}</p></div>
              </div>
            </Card>

            {huespedes.length > 0 && (
              <Card className="p-6">
                <h3 className="text-lg font-semibold mb-4 text-slate-900 dark:text-slate-50">Huéspedes Agregados ({huespedes.length})</h3>
                <div className="space-y-2">
                  {huespedes.map((h, idx) => (
                    <div key={idx} className="flex items-center justify-between bg-slate-50 dark:bg-slate-800 p-4 rounded border border-slate-200 dark:border-slate-700">
                      <div>
                        <p className="text-slate-900 dark:text-white font-semibold">{responsableIdx === idx && "👤 "}{h.apellido}, {h.nombres}{responsableIdx === idx && <span className="text-xs text-blue-600 dark:text-blue-400 ml-2">(Responsable)</span>}</p>
                        <p className="text-slate-600 dark:text-slate-400 text-sm">{h.tipoDocumento}: {h.nroDocumento}</p>
                      </div>
                      <div className="flex gap-2">
                        {responsableIdx !== idx && (
                          <Button onClick={() => handleSetResponsable(idx)} className="bg-blue-600 hover:bg-blue-700 dark:bg-blue-600 dark:hover:bg-blue-500" size="sm">
                            Hacer responsable
                          </Button>
                        )}
                        <Button onClick={() => handleEliminarHuesped(idx)} className="bg-red-600 hover:bg-red-700 dark:bg-red-700 dark:hover:bg-red-600" size="sm">
                          Eliminar
                        </Button>
                      </div>
                    </div>
                  ))}
                </div>
              </Card>
            )}

            <Card className="p-6">
              <h3 className="text-lg font-semibold mb-4 text-slate-900 dark:text-slate-50">
                {huespedes.length === 0 ? "Buscar Responsable (Obligatorio)" : "Buscar Acompañante (Opcional)"}
              </h3>
              <p className="text-slate-600 text-sm mb-4 dark:text-slate-400">
                Todos los campos son opcionales. Si no ingresa ningún dato, se listarán todos los huéspedes.
              </p>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
                <div>
                  <Label htmlFor="apellido">Apellido</Label>
                  <Input
                    id="apellido"
                    type="text"
                    value={busquedaHuesped.apellido}
                    onChange={(e) => handleChangeBusqueda("apellido", e.target.value)}
                    placeholder="Ej: G (primera letra)"
                    maxLength={1}
                  />
                  {erroresBusqueda.apellido && (
                    <p className="text-red-600 text-xs mt-1 dark:text-red-400">{erroresBusqueda.apellido}</p>
                  )}
                </div>

                <div>
                  <Label htmlFor="nombres">Nombre(s)</Label>
                  <Input
                    id="nombres"
                    type="text"
                    value={busquedaHuesped.nombres}
                    onChange={(e) => handleChangeBusqueda("nombres", e.target.value)}
                    placeholder="Ej: A (primera letra)"
                    maxLength={1}
                  />
                  {erroresBusqueda.nombres && (
                    <p className="text-red-600 text-xs mt-1 dark:text-red-400">{erroresBusqueda.nombres}</p>
                  )}
                </div>

                <div>
                  <Label htmlFor="tipoDocumento">Tipo de Documento</Label>
                  <select
                    id="tipoDocumento"
                    value={busquedaHuesped.tipoDocumento}
                    onChange={(e) => handleChangeBusqueda("tipoDocumento", e.target.value)}
                    className="flex h-9 w-full rounded-md border border-slate-200 bg-transparent px-3 py-1 text-sm shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-slate-950 dark:border-slate-800 dark:focus-visible:ring-slate-300"
                  >
                    <option value="">Seleccionar...</option>
                    <option value="DNI">DNI</option>
                    <option value="LE">LE</option>
                    <option value="LC">LC</option>
                    <option value="PASAPORTE">Pasaporte</option>
                    <option value="OTRO">Otro</option>
                  </select>
                  {erroresBusqueda.tipoDocumento && (
                    <p className="text-red-600 text-xs mt-1 dark:text-red-400">{erroresBusqueda.tipoDocumento}</p>
                  )}
                </div>

                <div>
                  <Label htmlFor="nroDocumento">Número de Documento</Label>
                  <Input
                    id="nroDocumento"
                    type="text"
                    value={busquedaHuesped.nroDocumento}
                    onChange={(e) => handleChangeBusqueda("nroDocumento", e.target.value)}
                    placeholder="Alfanumérico, 6-15 caracteres"
                    maxLength={15}
                  />
                  {erroresBusqueda.nroDocumento && (
                    <p className="text-red-600 text-xs mt-1 dark:text-red-400">{erroresBusqueda.nroDocumento}</p>
                  )}
                </div>
              </div>

              <Button
                onClick={handleBuscarHuespedes}
                disabled={buscando}
                className="w-full"
              >
                {buscando ? "Buscando..." : "🔍 Buscar Huésped"}
              </Button>
            </Card>

            {/* Resultados de búsqueda */}
            {mostrarResultados && (
              <Card className="p-6">
                <h3 className="text-lg font-semibold mb-4 text-slate-900 dark:text-slate-50">Resultados de la Búsqueda</h3>
                {resultadosBusqueda.length === 0 ? (
                  <div className="text-center py-8">
                    <p className="text-slate-600 mb-4 dark:text-slate-400">No se encontraron huéspedes con los criterios especificados.</p>
                  </div>
                ) : (
                  <div className="space-y-2">
                    {resultadosBusqueda.map((huesped, idx) => (
                      <div
                        key={idx}
                        className="flex items-center justify-between bg-slate-50 dark:bg-slate-800 p-4 rounded border border-slate-200 dark:border-slate-700 hover:border-blue-500 dark:hover:border-blue-500 transition"
                      >
                        <div>
                          <p className="text-slate-900 dark:text-white font-semibold">{huesped.apellido}, {huesped.nombres}</p>
                          <p className="text-slate-600 dark:text-slate-400 text-sm">{huesped.tipoDocumento}: {huesped.nroDocumento}</p>
                        </div>
                        <Button
                          onClick={() => handleSeleccionarHuesped(huesped)}
                          className="bg-green-600 hover:bg-green-700 dark:bg-green-500 dark:hover:bg-green-600"
                          size="sm"
                        >
                          Seleccionar
                        </Button>
                      </div>
                    ))}
                  </div>
                )}
              </Card>
            )}

            <div className="flex gap-4">
              <Button onClick={handleVolverPaso} variant="outline">
                ← Atrás
              </Button>
              <Button onClick={handleContinuarConfirmacion} disabled={responsableIdx === null}>
                Continuar →
              </Button>
            </div>
          </div>
        )}

        {paso === "confirmacion" && (
          <Card className="p-8">
            <h2 className="text-2xl font-semibold mb-6 text-slate-900 dark:text-slate-50">Paso 5: Confirmación de Check-In</h2>
            <div className="space-y-6">
              <div className="bg-slate-50 dark:bg-slate-800 rounded-lg p-6">
                <h3 className="text-lg font-semibold mb-4 text-slate-900 dark:text-white">Detalles de la Estadía</h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div><p className="text-slate-600 dark:text-slate-400 text-sm">Habitación</p><p className="text-slate-900 dark:text-white text-xl font-bold">{habitacionSeleccionada?.numero} - {habitacionSeleccionada?.tipo}</p></div>
                  <div><p className="text-slate-600 dark:text-slate-400 text-sm">Capacidad</p><p className="text-slate-900 dark:text-white text-xl font-bold">{habitacionSeleccionada?.capacidad} personas</p></div>
                  <div><p className="text-slate-600 dark:text-slate-400 text-sm">Check-In</p><p className="text-slate-900 dark:text-white font-semibold">{fechaCheckIn ? createLocalDate(fechaCheckIn).toLocaleDateString("es-ES", { weekday: "long", year: "numeric", month: "long", day: "numeric" }) : "-"}</p></div>
                  <div><p className="text-slate-600 dark:text-slate-400 text-sm">Check-Out</p><p className="text-slate-900 dark:text-white font-semibold">{fechaCheckOut ? createLocalDate(fechaCheckOut).toLocaleDateString("es-ES", { weekday: "long", year: "numeric", month: "long", day: "numeric" }) : "-"}</p></div>
                </div>
              </div>
              <div className="bg-slate-50 dark:bg-slate-800 rounded-lg p-6">
                  <h3 className="text-lg font-semibold mb-4 text-slate-900 dark:text-white">Huéspedes ({huespedes.length})</h3>
                <div className="space-y-3">
                  {huespedes.map((h, idx) => (
                    <div key={idx} className="bg-white dark:bg-slate-900 p-4 rounded border border-slate-200 dark:border-slate-700">
                      <p className="text-slate-900 dark:text-white font-semibold">{responsableIdx === idx && "👤 "}{h.apellido}, {h.nombres}{responsableIdx === idx && <span className="text-xs text-blue-600 dark:text-blue-400 ml-2">(Responsable)</span>}</p>
                      <p className="text-slate-600 dark:text-slate-400 text-sm">{h.tipoDocumento}: {h.nroDocumento}</p>
                    </div>
                  ))}
                </div>
              </div>
              <Card className="border-orange-200 bg-orange-50/50 p-4 dark:border-orange-900 dark:bg-orange-950/20">
                <p className="text-orange-700 dark:text-orange-200 text-sm">⚠️ Por favor, verifique que todos los datos sean correctos antes de confirmar el check-in.</p>
              </Card>
              {!postCheckin ? (
                <div className="flex gap-4">
                  <Button onClick={handleVolverPaso} variant="outline">
                    ← Modificar Datos
                  </Button>
                  <Button 
                    onClick={handleConfirmarEstadia} 
                    disabled={loading}
                    className="bg-green-600 hover:bg-green-700 dark:bg-green-500 dark:hover:bg-green-600"
                  >
                    {loading ? <Loader2 className="h-4 w-4 animate-spin mr-2" /> : "✓ "}
                    {loading ? "Confirmando..." : "Confirmar Check-In"}
                  </Button>
                </div>
              ) : (
                <Card className="p-6 border-green-200 bg-green-50/50 dark:border-green-900 dark:bg-green-950/20">
                  {errorCreacion && (
                    <Card className="mb-4 border-red-200 bg-red-50/50 p-4 dark:border-red-900 dark:bg-red-950/20">
                      <p className="text-red-700 dark:text-red-300 text-sm">{errorCreacion}</p>
                    </Card>
                  )}
                  <p className="mb-4 text-green-700 dark:text-green-300">Check-In realizado. ¿Qué desea hacer?</p>
                  <div className="flex gap-3">
                    <Button onClick={() => { setPostCheckin(false); setPaso("huespedes"); }} className="bg-blue-600 hover:bg-blue-700">Seguir cargando</Button>
                    <Button onClick={async () => { await ejecutarCreacionEstadia(); if (!errorCreacion) { setPostCheckin(false); handleVolverASeleccion(); setPaso("fechasGrilla"); } }} className="bg-orange-600 hover:bg-orange-700">Cargar otra habitación</Button>
                    <Button onClick={async () => { await ejecutarCreacionEstadia(); if (!errorCreacion) { router.push("/"); } }} variant="outline">Salir</Button>
                  </div>
                </Card>
              )}
            </div>
          </Card>
        )}
      </main>
    </div>
  );
}
