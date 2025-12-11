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
  huesped?: string;
  checkin?: string;
  checkout?: string;
}

const HABITACIONES_ESTADO_MOCK: HabitacionEstado[] = [
  {
    id: "1",
    numero: "101",
    tipo: "Doble",
    comodidad: "Doble",
    capacidad: 2,
    estado: "DISPONIBLE",
  },
  {
    id: "2",
    numero: "102",
    tipo: "Simple",
    comodidad: "Simple",
    capacidad: 1,
    estado: "OCUPADA",
    huesped: "García, Juan",
    checkin: "2025-12-08",
    checkout: "2025-12-10",
  },
  {
    id: "3",
    numero: "103",
    tipo: "Suite",
    comodidad: "Suite",
    capacidad: 4,
    estado: "RESERVADA",
    huesped: "López, María",
    checkin: "2025-12-12",
    checkout: "2025-12-15",
  },
  {
    id: "4",
    numero: "104",
    tipo: "Doble",
    comodidad: "Doble",
    capacidad: 2,
    estado: "DISPONIBLE",
  },
  {
    id: "5",
    numero: "201",
    tipo: "Triple",
    comodidad: "Triple",
    capacidad: 3,
    estado: "OCUPADA",
    huesped: "Martínez, Carlos",
    checkin: "2025-12-07",
    checkout: "2025-12-09",
  },
  {
    id: "6",
    numero: "202",
    tipo: "Doble",
    comodidad: "Doble",
    capacidad: 2,
    estado: "DISPONIBLE",
  },
];

const COMODIDADES_ORDEN = ["Simple", "Doble", "Triple", "Suite"];

type Paso = "fechaDesde" | "fechaHasta" | "grilla";

export default function EstadoHabitaciones() {
  const [paso, setPaso] = useState<Paso>("fechaDesde");
  const [fechaDesde, setFechaDesde] = useState("");
  const [fechaHasta, setFechaHasta] = useState("");
  const [errorFecha, setErrorFecha] = useState("");

  const validarFechaDesde = (): boolean => {
    if (!fechaDesde) {
      setErrorFecha("Debe seleccionar una fecha");
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

  const handleConfirmarHasta = () => {
    if (validarFechaHasta()) {
      setPaso("grilla");
    }
  };

  const handleVolver = () => {
    if (paso === "fechaHasta") {
      setErrorFecha("");
      setPaso("fechaDesde");
    } else if (paso === "grilla") {
      setErrorFecha("");
      setPaso("fechaHasta");
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

  const conteo = {
    disponibles: HABITACIONES_ESTADO_MOCK.filter(
      (h) => h.estado === "DISPONIBLE"
    ).length,
    reservadas: HABITACIONES_ESTADO_MOCK.filter(
      (h) => h.estado === "RESERVADA"
    ).length,
    ocupadas: HABITACIONES_ESTADO_MOCK.filter(
      (h) => h.estado === "OCUPADA"
    ).length,
  };

  const getEstadoColor = (estado: string) => {
    switch (estado) {
      case "DISPONIBLE":
        return "bg-green-500 hover:bg-green-600";
      case "RESERVADA":
        return "bg-blue-500 hover:bg-blue-600";
      case "OCUPADA":
        return "bg-red-500 hover:bg-red-600";
      default:
        return "bg-slate-600";
    }
  };

  return (
    <div className="min-h-screen bg-slate-950 text-white p-8">
      <div className="max-w-4xl mx-auto">
        <Link href="/" className="text-amber-400 hover:text-amber-300 mb-6 inline-block">
          ← Volver
        </Link>

        <h1 className="text-4xl font-bold mb-2 text-amber-400">CU05 - Estado de Habitaciones</h1>
        <p className="text-slate-300 mb-8">Paso {paso === "fechaDesde" ? 1 : paso === "fechaHasta" ? 2 : 3} de 3</p>

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
                  autoFocus
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
                  autoFocus
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
                  onClick={handleVolver}
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

        {/* PASO 3: Mostrar Grilla */}
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

            {/* Grilla por comodidades y fechas */}
            <div className="bg-slate-900 border border-slate-700 rounded-lg p-6 overflow-x-auto">
              <h2 className="text-xl font-semibold mb-4 text-amber-400">
                Estado de habitaciones del {new Date(fechaDesde).toLocaleDateString()} al {new Date(fechaHasta).toLocaleDateString()}
              </h2>
              
              <div className="min-w-max">
                <table className="w-full text-sm border-collapse">
                  <thead>
                    <tr className="bg-slate-800">
                      <th className="border border-slate-700 px-4 py-2 text-left font-semibold text-amber-400 w-32">
                        Comodidad / Habitación
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
                      const habsComodidad = HABITACIONES_ESTADO_MOCK.filter(
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
                              {diasRango.map((dia, dayIdx) => (
                                <td
                                  key={`${hab.id}-${dayIdx}`}
                                  className="border border-slate-700 px-2 py-2 text-center"
                                >
                                  <div
                                    className={`rounded px-2 py-1 text-xs font-semibold text-white ${getEstadoColor(
                                      hab.estado
                                    )}`}
                                    title={
                                      hab.huesped
                                        ? `${hab.huesped} (${hab.checkin} a ${hab.checkout})`
                                        : hab.estado
                                    }
                                  >
                                    {hab.estado === "DISPONIBLE"
                                      ? "✓"
                                      : hab.estado === "RESERVADA"
                                      ? "R"
                                      : "X"}
                                  </div>
                                </td>
                              ))}
                            </tr>
                          ))
                        : null;
                    })}
                  </tbody>
                </table>
                <div className="mt-4 text-xs text-slate-400 flex gap-4">
                  <span>✓ = Disponible</span>
                  <span>R = Reservada</span>
                  <span>X = Ocupada</span>
                </div>
              </div>

              <button
                onClick={handleVolver}
                className="mt-6 bg-slate-700 hover:bg-slate-600 text-white font-semibold py-2 px-4 rounded transition"
              >
                ← Modificar fechas
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
