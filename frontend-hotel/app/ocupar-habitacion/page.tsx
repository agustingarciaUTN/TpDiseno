"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { DoorOpen, Home, Calendar, Users, CheckCircle, Loader2 } from "lucide-react";
import { obtenerHabitaciones, buscarHuespedes, crearEstadia } from "@/lib/api";
import { DtoHabitacion, DtoHuesped, DtoEstadia, EstadoHabitacion } from "@/lib/types";

interface HabitacionEstado {
  id: string;
  numero: string;
  tipo: string;
  comodidad: "Simple" | "Doble" | "Triple" | "Suite";
  capacidad: number;
  estado: "DISPONIBLE" | "RESERVADA" | "OCUPADA";
  estadosPorDia?: Record<string, "DISPONIBLE" | "RESERVADA" | "OCUPADA" | "MANTENIMIENTO">;
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

const COMODIDADES_ORDEN = ["Simple", "Doble", "Triple", "Suite"];

// Función para mapear tipos de habitación del backend
const mapearTipoAComodidad = (tipoJava: string): "Simple" | "Doble" | "Triple" | "Suite" => {
  const tipo = tipoJava.toUpperCase();
  if (tipo.includes("INDIVIDUAL")) return "Simple";
  if (tipo.includes("DOBLE")) return "Doble";
  if (tipo.includes("FAMILY") || tipo.includes("TRIPLE")) return "Triple";
  if (tipo.includes("SUITE")) return "Suite";
  return "Simple";
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
  const [loading, setLoading] = useState(false);
  const [errorCarga, setErrorCarga] = useState("");

  // Estados para la grilla interactiva (estilo CU4)
  const [seleccion, setSeleccion] = useState<SeleccionHabitacion | null>(null);
  const [seleccionActual, setSeleccionActual] = useState<{
    habitacionId: string;
    diaInicio: number | null;
  } | null>(null);

  // Cargar habitaciones al montar el componente
  useEffect(() => {
    const cargarHabitacionesConEstado = async () => {
      setLoading(true);
      try {
        // Obtener rango de fechas (hoy + 6 días para tener una semana)
        const hoy = new Date();
        const fechaDesde = hoy.toISOString().split('T')[0];
        const fechaFin = new Date(hoy);
        fechaFin.setDate(fechaFin.getDate() + 6);
        const fechaHasta = fechaFin.toISOString().split('T')[0];

        const response = await fetch(`http://localhost:8080/api/habitaciones/estado?fechaDesde=${fechaDesde}&fechaHasta=${fechaHasta}`);
        const data = await response.json();
        
        const habitacionesMapeadas = data.map((h: any) => ({
          id: h.numero.toString(),
          numero: h.numero.toString(),
          tipo: h.tipoHabitacion,
          comodidad: mapearTipoAComodidad(h.tipoHabitacion),
          capacidad: h.capacidad,
          // Usar el estado del día actual (primer día en estadosPorDia)
          estado: (h.estadosPorDia && h.estadosPorDia[fechaDesde]) || "DISPONIBLE" as const,
          estadosPorDia: h.estadosPorDia || {},
          precioNoche: h.costoPorNoche
        }));
        setHabitaciones(habitacionesMapeadas);
      } catch (error) {
        console.error("Error al cargar habitaciones:", error);
        setErrorCarga("No se pudieron cargar las habitaciones");
      } finally {
        setLoading(false);
      }
    };
    cargarHabitacionesConEstado();
  }, []);

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
    const desde = createLocalDate(fechaDesdeGrilla);
    const hasta = createLocalDate(fechaHastaGrilla);
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
    const habitacion = habitaciones.find((h: HabitacionEstado) => h.id === habitacionId);
    if (!habitacion) return false;

    // Verificar el estado específico de este día
    const estadoDia = obtenerEstadoCelda(habitacionId, diaIdx);
    
    // Permitir clicks en habitaciones disponibles y reservadas, solo bloquear ocupadas y en mantenimiento
    if (estadoDia === "OCUPADA" || estadoDia === "MANTENIMIENTO") return false;
    
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

    if (!seleccionActual || seleccionActual.habitacionId !== habitacionId) {
      // Primer click: establecer punto de inicio (y limpiar cualquier selección previa)
      setSeleccion(null);
      setSeleccionActual({ habitacionId, diaInicio: diaIdx });
    } else {
      // Segundo click: establecer punto de fin y confirmar selección
      const diaInicio = seleccionActual.diaInicio!;
      const diaFin = diaIdx;
      const dias = generarDias();
      const hab = habitaciones.find((h: HabitacionEstado) => h.id === habitacionId);

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

  // Obtener el estado de una celda específica por día
  const obtenerEstadoCelda = (habitacionId: string, diaIdx: number): "DISPONIBLE" | "RESERVADA" | "OCUPADA" | "MANTENIMIENTO" => {
    const habitacion = habitaciones.find(h => h.id === habitacionId);
    if (!habitacion || !habitacion.estadosPorDia) return "DISPONIBLE";

    const dia = new Date(fechaInicio);
    dia.setDate(dia.getDate() + diaIdx);
    const fechaDia = dia.toISOString().split('T')[0];

    return habitacion.estadosPorDia[fechaDia] || "DISPONIBLE";
  };

  const getEstadoColor = (estado: "DISPONIBLE" | "RESERVADA" | "OCUPADA" | "MANTENIMIENTO") => {
    switch (estado) {
      case "DISPONIBLE":
        return "bg-green-600 dark:bg-green-700";
      case "RESERVADA":
        return "bg-blue-600 dark:bg-blue-700";
      case "OCUPADA":
        return "bg-red-600 dark:bg-red-700";
      case "MANTENIMIENTO":
        return "bg-yellow-600 dark:bg-yellow-700";
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
      const criterios = {
        apellido: busquedaHuesped.apellido,
        nombres: busquedaHuesped.nombres,
        tipoDocumento: busquedaHuesped.tipoDocumento,
        nroDocumento: busquedaHuesped.nroDocumento
      };
      
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

  const handleConfirmarEstadia = async () => {
    try {
      setLoading(true);
      
      if (!habitacionSeleccionada || huespedes.length === 0) {
        alert("Faltan datos para confirmar el check-in");
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
          estadoHabitacion: EstadoHabitacion.DISPONIBLE,
          costoPorNoche: habitacionSeleccionada.precioNoche
        },
        // Solo enviar el primer huésped (responsable/a cargo) para la tabla estadia-huesped
        dtoHuespedes: [{
          idHuesped: 0,
          apellido: huespedes[0].apellido,
          nombres: huespedes[0].nombres,
          tipoDocumento: huespedes[0].tipoDocumento as any,
          nroDocumento: huespedes[0].nroDocumento,
        }]
      };

      // Llamar al backend
      await crearEstadia(estadia);
      
      alert("La operación ha culminado con éxito. Check-in realizado correctamente.");
      router.push("/");
    } catch (error: any) {
      console.error("Error al confirmar estadía:", error);
      alert("Error al confirmar el check-in: " + error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleVolverPaso = () => {
    if (paso === "grilla") setPaso("fechasGrilla");
    else if (paso === "huespedes") setPaso("grilla");
    else if (paso === "confirmacion") setPaso("huespedes");
  };

  const diasRango = generarDias();
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
                  Volver al Inicio
                </Link>
              </Button>
            </div>
          </Card>
        )}

        {paso === "grilla" && (
          <div className="space-y-6">
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
                      <th className="border border-slate-300 dark:border-slate-700 px-4 py-2 text-slate-900 dark:text-slate-50 font-semibold">Habitación</th>
                      {diasRango.map((dia, idx) => (
                        <th key={idx} className="border border-slate-300 dark:border-slate-700 px-2 py-2 text-center text-slate-900 dark:text-slate-50 font-semibold min-w-[60px]">
                          <div className="text-xs">{dia.toLocaleDateString("es-AR", { day: "2-digit", month: "2-digit" })}</div>
                        </th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {COMODIDADES_ORDEN.map((comodidad) => {
                      const habitacionesPorComodidad = habitaciones.filter((h: HabitacionEstado) => h.comodidad === comodidad);
                      return habitacionesPorComodidad.length > 0
                        ? habitacionesPorComodidad.map((hab, habIdx) => (
                            <tr
                              key={hab.id}
                              className={habIdx % 2 === 0 ? "bg-slate-50 dark:bg-slate-800/50" : "bg-white dark:bg-transparent"}
                            >
                              <td className="border border-slate-300 dark:border-slate-700 px-4 py-2 font-semibold text-slate-900 dark:text-slate-200">
                                {habIdx === 0 && (
                                  <div className="font-bold text-blue-600 dark:text-blue-400 mb-1">
                                    {comodidad}
                                  </div>
                                )}
                                <div className="text-slate-600 dark:text-slate-400 text-sm">
                                  Hab. {hab.numero}
                                </div>
                              </td>
                              {diasRango.map((dia, dayIdx) => {
                                const disponible = esCeldaDisponible(hab.id, dayIdx);
                                const seleccionada = esCeldaSeleccionada(hab.id, dayIdx);
                                const inicioActual = esInicioSeleccionActual(hab.id, dayIdx);
                                const estadoDia = obtenerEstadoCelda(hab.id, dayIdx);

                                return (
                                  <td
                                    key={`${hab.id}-${dayIdx}`}
                                    className="border border-slate-300 dark:border-slate-700 px-2 py-2 text-center"
                                  >
                                    <div
                                      onClick={() => handleClickCelda(hab.id, dayIdx)}
                                      className={`rounded px-2 py-1 text-xs font-semibold text-white transition cursor-pointer ${
                                        seleccionada
                                          ? "bg-blue-600 hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600"
                                          : inicioActual
                                          ? "bg-purple-500 animate-pulse dark:bg-purple-600"
                                          : disponible
                                          ? getEstadoColor(estadoDia) + " hover:brightness-110"
                                          : "bg-slate-600 dark:bg-slate-700 cursor-not-allowed opacity-50"
                                      }`}
                                      title={
                                        seleccionada
                                          ? "Seleccionada"
                                          : inicioActual
                                          ? "Click en otra celda para finalizar"
                                          : disponible
                                          ? "Click para seleccionar"
                                          : estadoDia
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
                <div className="mt-4 text-xs text-slate-600 dark:text-slate-400 flex gap-4">
                  <span>○ = Disponible</span>
                  <span>✓ = Seleccionada</span>
                  <span>► = Inicio de selección</span>
                  <span>R = Reservada</span>
                  <span>X = Ocupada</span>
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

            {/* Diálogo de confirmación: Habitación totalmente reservada */}
            {confirmacionTipo === "reservada" && seleccion && (
              <Card className="border-2 border-orange-500 bg-orange-50/50 p-6 dark:border-orange-600 dark:bg-orange-950/20">
                <div className="flex items-start gap-4">
                  <div className="text-orange-600 text-3xl dark:text-orange-400">⚠️</div>
                  <div className="flex-1">
                    <h3 className="text-xl font-bold text-orange-600 mb-2 dark:text-orange-400">Habitación Reservada</h3>
                    <p className="text-slate-700 mb-4 dark:text-slate-200">
                      La habitación seleccionada está completamente reservada en el rango de fechas elegido.
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
                        <p className="text-slate-900 dark:text-white font-semibold">{idx === 0 && "👤 "}{h.apellido}, {h.nombres}{idx === 0 && <span className="text-xs text-blue-600 dark:text-blue-400 ml-2">(Responsable)</span>}</p>
                        <p className="text-slate-600 dark:text-slate-400 text-sm">{h.tipoDocumento}: {h.nroDocumento}</p>
                      </div>
                      {idx > 0 && <Button onClick={() => handleEliminarHuesped(idx)} className="bg-red-600 hover:bg-red-700 dark:bg-red-700 dark:hover:bg-red-600" size="sm">Eliminar</Button>}
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
                    <option value="PASAPORTE">Pasaporte</option>
                    <option value="CI">Cédula de Identidad</option>
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
              <Button onClick={handleContinuarConfirmacion}>
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
                      <p className="text-slate-900 dark:text-white font-semibold">{idx === 0 && "👤 "}{h.apellido}, {h.nombres}{idx === 0 && <span className="text-xs text-blue-600 dark:text-blue-400 ml-2">(Responsable)</span>}</p>
                      <p className="text-slate-600 dark:text-slate-400 text-sm">{h.tipoDocumento}: {h.nroDocumento}</p>
                    </div>
                  ))}
                </div>
              </div>
              <Card className="border-orange-200 bg-orange-50/50 p-4 dark:border-orange-900 dark:bg-orange-950/20">
                <p className="text-orange-700 dark:text-orange-200 text-sm">⚠️ Por favor, verifique que todos los datos sean correctos antes de confirmar el check-in.</p>
              </Card>
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
            </div>
          </Card>
        )}
      </main>
    </div>
  );
}
