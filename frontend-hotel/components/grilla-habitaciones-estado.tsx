"use client";

import { Card } from "@/components/ui/card";
import { Loader2 } from "lucide-react";

interface HabitacionEstado {
  id: string;
  numero: string;
  tipo: string;
  capacidad: number;
  estadoHabitacion?: "HABILITADA" | "FUERA_DE_SERVICIO";
  estado?: "DISPONIBLE" | "RESERVADA" | "OCUPADA";
  estadosPorDia?: Record<string, "DISPONIBLE" | "RESERVADA" | "OCUPADA" | "MANTENIMIENTO">;
  precioNoche?: number;
}

interface GrillaHabitacionesEstadoProps {
  habitaciones: HabitacionEstado[];
  diasRango: Date[];
  loading: boolean;
  errorCarga: string;
  onHabitacionClick?: (habitacion: HabitacionEstado, dia: Date) => void;
  mostrarPrecio?: boolean;
}

// Helper para crear fecha local
const createLocalDate = (dateString: string): Date => {
  const [year, month, day] = dateString.split("-").map(Number);
  return new Date(year, month - 1, day);
};

export default function GrillaHabitacionesEstado({
  habitaciones,
  diasRango,
  loading,
  errorCarga,
  onHabitacionClick,
  mostrarPrecio = false,
}: GrillaHabitacionesEstadoProps) {
  
  // Obtener tipos únicos ordenados
  const getTiposUnicos = (): string[] => {
    const tipos = new Set(habitaciones.map((h) => h.tipo));
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

  // Obtener el estado de una celda específica
  const obtenerEstadoCelda = (habitacionId: string, diaIdx: number): string => {
    const habitacion = habitaciones.find((h) => h.id === habitacionId);
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

    // Fallback al estado general
    return habitacion.estado || "DISPONIBLE";
  };

  const mapearEstadoAColor = (estado: string) => {
    switch (estado) {
      case "DISPONIBLE":
        return "bg-green-100 text-green-800 border-green-300 hover:bg-green-200";
      case "RESERVADA":
        return "bg-blue-100 text-blue-800 border-blue-300 hover:bg-blue-200";
      case "OCUPADA":
        return "bg-yellow-100 text-yellow-800 border-yellow-300 hover:bg-yellow-200";
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
      MANTENIMIENTO: "Fuera de Servicio",
    };
    return textos[estado] || estado;
  };

  if (loading) {
    return (
      <Card className="p-12 flex flex-col items-center justify-center gap-4">
        <Loader2 className="w-12 h-12 animate-spin text-blue-600" />
        <p className="text-lg font-semibold text-slate-700 dark:text-slate-300">Procesando datos...</p>
      </Card>
    );
  }

  if (errorCarga) {
    return (
      <Card className="p-6 bg-red-50 border-red-200 dark:bg-red-900/20 dark:border-red-800">
        <p className="text-red-600 dark:text-red-400">{errorCarga}</p>
      </Card>
    );
  }

  if (habitaciones.length === 0) {
    return (
      <Card className="p-6">
        <p className="text-slate-600 dark:text-slate-400">No hay habitaciones disponibles para el rango seleccionado</p>
      </Card>
    );
  }

  return (
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
                    <div>{hab.numero}</div>
                    {mostrarPrecio && hab.precioNoche && (
                      <div className="text-xs font-normal text-slate-600 dark:text-slate-400">
                        ${hab.precioNoche}
                      </div>
                    )}
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
                    const esClickeable = onHabitacionClick && estado === "DISPONIBLE";
                    
                    return (
                      <td
                        key={`${hab.id}-${diaIdx}`}
                        className={`border border-gray-300 dark:border-gray-600 p-3 text-center text-xs font-medium min-w-20 ${mapearEstadoAColor(estado)} ${
                          esClickeable ? "cursor-pointer" : "cursor-default"
                        }`}
                        onClick={() => esClickeable && onHabitacionClick(hab, dia)}
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
  );
}
