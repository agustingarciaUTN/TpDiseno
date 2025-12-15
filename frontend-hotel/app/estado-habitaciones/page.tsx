"use client";

import Link from "next/link";
import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { BarChart3, Home, Calendar, Loader2 } from "lucide-react";

interface HabitacionEstado {
  id: string;
  numero: string;
  tipo: string;
  capacidad: number;
  estadoHabitacion: "HABILITADA" | "FUERA_DE_SERVICIO";
  estadoReserva: "DISPONIBLE" | "RESERVADA" | "OCUPADA";
  estadosPorDia?: Record<string, "DISPONIBLE" | "RESERVADA" | "OCUPADA" | "MANTENIMIENTO">;
  precioNoche?: number;
}

type Paso = "fechaDesde" | "fechaHasta" | "grilla";

// Helper function to create date in local timezone
const createLocalDate = (dateString: string): Date => {
  const [year, month, day] = dateString.split("-").map(Number);
  return new Date(year, month - 1, day);
};

export default function EstadoHabitaciones() {
  const [paso, setPaso] = useState<Paso>("fechaDesde");
  const [fechaDesde, setFechaDesde] = useState("");
  const [fechaHasta, setFechaHasta] = useState("");
  const [errorFecha, setErrorFecha] = useState("");

  // Estado para datos reales
  const [habitaciones, setHabitaciones] = useState<HabitacionEstado[]>([]);
  const [loading, setLoading] = useState(false);
  const [errorCarga, setErrorCarga] = useState("");

  // Obtener tipos de habitación únicos del backend (como aparecen en el enum)
  const getTiposUnicos = (): string[] => {
    const tipos = new Set(habitaciones.map((h) => h.tipo));
    // Ordenar según el orden del enum en el backend
    const orden = [
      "INDIVIDUAL_ESTANDAR",
      "DOBLE_ESTANDAR",
      "DOBLE_SUPERIOR",
      "SUPERIOR_FAMILY_PLAN",
      "SUITE_DOBLE",
    ];
    return Array.from(tipos).sort((a, b) => orden.indexOf(a) - orden.indexOf(b));
  };

  const formatearTipo = (tipo: string): string => {
    return tipo.replace(/_/g, " ");
  };

  // Validaciones de fechas
  const validarFechaDesde = (): boolean => {
    if (!fechaDesde) {
      setErrorFecha("Debe seleccionar una fecha");
      return false;
    }
    // La fecha de inicio no puede ser anterior al día de hoy
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);
    const fechaSeleccionada = createLocalDate(fechaDesde);
    if (fechaSeleccionada < hoy) {
      setErrorFecha("La fecha de inicio no puede ser anterior al día de hoy");
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
    const desde = createLocalDate(fechaDesde);
    const hasta = createLocalDate(fechaHasta);
    // fechaHasta > fechaDesde
    if (desde >= hasta) {
      setErrorFecha("La fecha 'Hasta' debe ser posterior a la fecha 'Desde'");
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

  const handleConfirmarHasta = async () => {
    if (validarFechaHasta()) {
      // Cambiar al paso de grilla primero para que se vea el mensaje de carga
      setPaso("grilla");
      // Cargar habitaciones con estado real para el rango de fechas
      await cargarHabitacionesConEstado();
    }
  };

  // Cargar habitaciones con estado real basado en fechas
  const cargarHabitacionesConEstado = async () => {
    if (!fechaDesde || !fechaHasta) return;

    setLoading(true);
    setErrorCarga("");
    try {
      const url = `http://localhost:8080/api/habitaciones/estados?fechaDesde=${fechaDesde}&fechaHasta=${fechaHasta}`;
      const response = await fetch(url);
      if (!response.ok) throw new Error("Error al cargar habitaciones");

      const data = await response.json();
      console.log("[CU5] Habitaciones con estado:", data);

      const habitacionesMapeadas: HabitacionEstado[] = data.map((h: any) => {
        // Obtener el estado de reserva del primer día disponible o del campo estadoReserva
        const estadoReservaBase = h.estadoReserva || 
          (h.estadosPorDia && Object.values(h.estadosPorDia)[0]) || 
          "DISPONIBLE";
        
        return {
          id: h.numero.toString(),
          numero: h.numero.toString(),
          tipo: h.tipoHabitacion,
          capacidad: h.capacidad,
          estadoHabitacion: h.estadoHabitacion || "HABILITADA",
          estadoReserva: estadoReservaBase as "DISPONIBLE" | "RESERVADA" | "OCUPADA",
          estadosPorDia: h.estadosPorDia || {},
          precioNoche: h.costoPorNoche,
        };
      });

      setHabitaciones(habitacionesMapeadas);
    } catch (error) {
      const msg = error instanceof Error ? error.message : "Error desconocido";
      setErrorCarga(msg);
      console.error("[CU5] Error al cargar habitaciones:", error);
    } finally {
      setLoading(false);
    }
  };

  const generarDias = () => {
    if (!fechaDesde || !fechaHasta) return [];
    const desde = createLocalDate(fechaDesde);
    const hasta = createLocalDate(fechaHasta);
    const dias: Date[] = [];
    const actual = new Date(desde);
    while (actual <= hasta) {
      dias.push(new Date(actual));
      actual.setDate(actual.getDate() + 1);
    }
    return dias;
  };

  const diasRango = generarDias();

  // Obtener el estado de una celda específica
  const obtenerEstadoCelda = (
    habitacionId: string,
    diaIdx: number
  ): string => {
    const habitacion = habitaciones.find((h: HabitacionEstado) => h.id === habitacionId);
    if (!habitacion) return "FUERA_DE_SERVICIO";

    // Si la habitación no está habilitada, mostrar su estado
    if (habitacion.estadoHabitacion === "FUERA_DE_SERVICIO") {
      return "FUERA_DE_SERVICIO";
    }

    // Si está habilitada, mostrar si está disponible, reservada u ocupada
    if (habitacion.estadosPorDia && diasRango[diaIdx]) {
      const fechaDia = diasRango[diaIdx].toISOString().split("T")[0];
      const estadoDia = habitacion.estadosPorDia[fechaDia];
      // El backend puede devolver MANTENIMIENTO, que equivale a FUERA_DE_SERVICIO
      if (estadoDia === "MANTENIMIENTO") return "FUERA_DE_SERVICIO";
      return (estadoDia as "DISPONIBLE" | "RESERVADA" | "OCUPADA") || "DISPONIBLE";
    }

    // Fallback al estado general de reserva
    return habitacion.estadoReserva;
  };

  const mapearEstadoAColor = (estado: string) => {
    switch (estado) {
      case "DISPONIBLE":
        return "bg-green-100 text-green-800 border-green-300";
      case "RESERVADA":
        return "bg-blue-100 text-blue-800 border-blue-300";
      case "OCUPADA":
        return "bg-yellow-100 text-yellow-800 border-yellow-300";
      case "FUERA_DE_SERVICIO":
        return "bg-red-100 text-red-800 border-red-300";
      default:
        return "bg-gray-100 text-gray-800 border-gray-300";
    }
  };

  const mapearEstadoATexto = (estado: string): string => {
    const textos: Record<string, string> = {
      DISPONIBLE: "Disponible",
      RESERVADA: "Reservada",
      OCUPADA: "Ocupada",
      FUERA_DE_SERVICIO: "Fuera de Servicio",
      MANTENIMIENTO: "Fuera de Servicio", // Backend envía MANTENIMIENTO para FUERA_DE_SERVICIO
    };
    return textos[estado] || estado;
  };

  const handleVolverPaso = () => {
    if (paso === "grilla") setPaso("fechaHasta");
    else if (paso === "fechaHasta") setPaso("fechaDesde");
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 to-slate-100 p-6 dark:from-slate-900 dark:to-slate-800">
      <div className="max-w-4xl mx-auto">
        {/* Encabezado */}
        <div className="mb-8">
          <Link href="/" className="inline-flex items-center gap-2 text-slate-600 hover:text-slate-900 mb-6 dark:text-slate-400 dark:hover:text-slate-100">
            <Home className="w-4 h-4" />
            <span>Volver al inicio</span>
          </Link>

          <div className="flex items-center gap-3 mb-2">
            <BarChart3 className="w-8 h-8 text-blue-600 dark:text-blue-400" />
            <h1 className="text-3xl font-bold text-slate-900 dark:text-slate-50">Estado de Habitaciones</h1>
          </div>
          <p className="text-slate-600 dark:text-slate-400">Visualiza la disponibilidad de habitaciones por fecha</p>
        </div>

        {/* Paso 1: Fecha Desde */}
        {paso === "fechaDesde" && (
          <Card className="p-8">
            <h2 className="text-2xl font-bold mb-6 text-slate-900 dark:text-slate-50">1. Selecciona la Fecha de Inicio</h2>
            <div className="space-y-4">
              <div>
                <Label htmlFor="fechaDesde" className="text-base font-semibold text-slate-700 dark:text-slate-300">
                  Fecha de Inicio
                </Label>
                <Input
                  id="fechaDesde"
                  type="date"
                  value={fechaDesde}
                  onChange={(e) => setFechaDesde(e.target.value)}
                  className="mt-2 text-base"
                />
                {errorFecha && <p className="text-red-600 text-sm mt-2 dark:text-red-400">{errorFecha}</p>}
              </div>

              <div className="flex gap-4 pt-4">
                <Button onClick={handleConfirmarDesde} className="flex-1 bg-blue-600 hover:bg-blue-700">
                  <Calendar className="w-4 h-4 mr-2" />
                  Confirmar Fecha
                </Button>
              </div>
            </div>
          </Card>
        )}

        {/* Paso 2: Fecha Hasta */}
        {paso === "fechaHasta" && (
          <Card className="p-8">
            <h2 className="text-2xl font-bold mb-6 text-slate-900 dark:text-slate-50">2. Selecciona la Fecha de Fin</h2>
            <div className="space-y-4">
              <div>
                <Label className="text-slate-700 dark:text-slate-300">
                  Desde: <span className="font-semibold">{createLocalDate(fechaDesde).toLocaleDateString()}</span>
                </Label>
              </div>

              <div>
                <Label htmlFor="fechaHasta" className="text-base font-semibold text-slate-700 dark:text-slate-300">
                  Hasta
                </Label>
                <Input
                  id="fechaHasta"
                  type="date"
                  value={fechaHasta}
                  onChange={(e) => setFechaHasta(e.target.value)}
                  className="mt-2 text-base"
                />
                {errorFecha && <p className="text-red-600 text-sm mt-2 dark:text-red-400">{errorFecha}</p>}
              </div>

              <div className="flex gap-4 pt-4">
                <Button onClick={handleVolverPaso} variant="outline">
                  ← Atrás
                </Button>
                <Button onClick={handleConfirmarHasta} className="flex-1 bg-blue-600 hover:bg-blue-700">
                  <Calendar className="w-4 h-4 mr-2" />
                  Ver Disponibilidad
                </Button>
              </div>
            </div>
          </Card>
        )}

        {/* Paso 3: Grilla */}
        {paso === "grilla" && (
          <div className="space-y-6">
            <Card className="p-6">
              <h2 className="text-2xl font-bold mb-4 text-slate-900 dark:text-slate-50">3. Estado de Habitaciones</h2>
              <div className="text-sm text-slate-600 dark:text-slate-400">
                <p>
                  Desde <span className="font-semibold">{createLocalDate(fechaDesde).toLocaleDateString()}</span> hasta{" "}
                  <span className="font-semibold">{createLocalDate(fechaHasta).toLocaleDateString()}</span>
                </p>
              </div>
            </Card>

            {loading && (
              <Card className="p-12 flex flex-col items-center justify-center gap-4">
                <Loader2 className="w-12 h-12 animate-spin text-blue-600" />
                <p className="text-lg font-semibold text-slate-700 dark:text-slate-300">Procesando datos...</p>
              </Card>
            )}

            {errorCarga && <Card className="p-6 bg-red-50 border-red-200 dark:bg-red-900/20 dark:border-red-800"><p className="text-red-600 dark:text-red-400">{errorCarga}</p></Card>}

            {!loading && !errorCarga && (
              <>
                {/* Grilla */}
                <Card className="p-6 overflow-x-auto">
                  <table className="w-full border-collapse border border-gray-300 dark:border-gray-600">
                    <thead>
                      <tr className="bg-gray-100 dark:bg-gray-800">
                        <th className="border border-gray-300 dark:border-gray-600 p-3 text-left font-semibold min-w-24 text-slate-900 dark:text-slate-100">
                          Fecha
                        </th>
                        {/* Agrupar habitaciones por tipo de habitación */}
                        {getTiposUnicos().map((tipo) => {
                          const habsTipo = habitaciones.filter((h) => h.tipo === tipo);
                          if (habsTipo.length === 0) return null;

                          return (
                            <th
                              key={tipo}
                              colSpan={habsTipo.length}
                              className="border border-gray-300 dark:border-gray-600 p-3 text-center font-semibold bg-blue-50 dark:bg-blue-900/30 text-blue-900 dark:text-blue-100"
                            >
                              {formatearTipo(tipo)}
                            </th>
                          );
                        })}
                      </tr>
                      <tr className="bg-gray-50 dark:bg-gray-800">
                        <th className="border border-gray-300 dark:border-gray-600 p-3"></th>
                        {/* Sub-encabezados con números de habitación */}
                        {getTiposUnicos().map((tipo) => {
                          const habsTipo = habitaciones.filter((h) => h.tipo === tipo);
                          if (habsTipo.length === 0) return null;

                          return habsTipo.map((hab) => (
                            <th
                              key={hab.id}
                              className="border border-gray-300 dark:border-gray-600 p-3 text-center font-semibold min-w-20 text-sm text-slate-900 dark:text-slate-100"
                            >
                              {hab.numero}
                            </th>
                          ));
                        })}
                      </tr>
                    </thead>
                    <tbody>
                      {diasRango.map((dia, diaIdx) => (
                        <tr key={diaIdx} className="hover:bg-gray-50 dark:hover:bg-gray-800">
                          <td className="border border-gray-300 dark:border-gray-600 p-3 font-semibold text-slate-900 dark:text-slate-100 min-w-24 bg-gray-50 dark:bg-gray-800">
                            {dia.toLocaleDateString("es-AR", { weekday: "short", day: "numeric", month: "short" })}
                          </td>
                          {/* Mostrar celdas de estado para cada habitación en este día */}
                          {getTiposUnicos().map((tipo) => {
                            const habsTipo = habitaciones.filter((h) => h.tipo === tipo);
                            if (habsTipo.length === 0) return null;

                            return habsTipo.map((hab) => {
                              const estado = obtenerEstadoCelda(hab.id, diaIdx);
                              return (
                                <td
                                  key={`${hab.id}-${diaIdx}`}
                                  className={`border border-gray-300 dark:border-gray-600 p-3 text-center text-xs font-medium cursor-default min-w-20 ${mapearEstadoAColor(estado)}`}
                                >
                                  <span className="inline-block px-2 py-1 rounded">{mapearEstadoATexto(estado)}</span>
                                </td>
                              );
                            });
                          })}
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </Card>

                {/* Leyenda */}
                <Card className="p-6 bg-slate-50 dark:bg-slate-800">
                  <h3 className="font-semibold mb-4 text-slate-900 dark:text-slate-100">Leyenda</h3>
                  <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                    <div className="flex items-center gap-2">
                      <div className="w-6 h-6 bg-green-100 border border-green-300 rounded"></div>
                      <span className="text-sm text-slate-700 dark:text-slate-300">Disponible</span>
                    </div>
                    <div className="flex items-center gap-2">
                      <div className="w-6 h-6 bg-blue-100 border border-blue-300 rounded"></div>
                      <span className="text-sm text-slate-700 dark:text-slate-300">Reservada</span>
                    </div>
                    <div className="flex items-center gap-2">
                      <div className="w-6 h-6 bg-yellow-100 border border-yellow-300 rounded"></div>
                      <span className="text-sm text-slate-700 dark:text-slate-300">Ocupada</span>
                    </div>
                    <div className="flex items-center gap-2">
                      <div className="w-6 h-6 bg-red-100 border border-red-300 rounded"></div>
                      <span className="text-sm text-slate-700 dark:text-slate-300">Fuera de Servicio</span>
                    </div>
                  </div>
                </Card>
              </>
            )}

            {/* Botones de navegación */}
            <div className="flex justify-between">
              <Button onClick={handleVolverPaso} variant="outline">
                ← Atrás
              </Button>
              <Link href="/">
                <Button variant="outline">Cerrar</Button>
              </Link>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}