"use client";

import Link from "next/link";
import { useState } from "react";

interface HabitacionReserva {
  id: string;
  numero: string;
  tipo: string;
  capacidad: number;
  precioNoche: number;
}

interface HuespedBusqueda {
  id: string;
  apellido: string;
  nombres: string;
  documento: string;
}

const HABITACIONES_MOCK: HabitacionReserva[] = [
  { id: "1", numero: "101", tipo: "Doble", capacidad: 2, precioNoche: 120 },
  { id: "2", numero: "102", tipo: "Simple", capacidad: 1, precioNoche: 80 },
  { id: "3", numero: "103", tipo: "Suite", capacidad: 4, precioNoche: 200 },
  { id: "4", numero: "104", tipo: "Doble", capacidad: 2, precioNoche: 120 },
  { id: "5", numero: "201", tipo: "Triple", capacidad: 3, precioNoche: 150 },
];

const HUESPEDES_MOCK: HuespedBusqueda[] = [
  { id: "1", apellido: "García", nombres: "Juan", documento: "12345678" },
  { id: "2", apellido: "López", nombres: "María", documento: "87654321" },
  { id: "3", apellido: "Martínez", nombres: "Carlos", documento: "11223344" },
];

type Paso = "fechas" | "habitaciones" | "huesped" | "confirmacion";

export default function ReservarHabitacion() {
  const [paso, setPaso] = useState<Paso>("fechas");
  const [fechaDesde, setFechaDesde] = useState("");
  const [fechaHasta, setFechaHasta] = useState("");
  const [errorFecha, setErrorFecha] = useState("");
  const [seleccionadas, setSeleccionadas] = useState<string[]>([]);
  const [huespedSeleccionado, setHuespedSeleccionado] = useState<string>("");
  const [busquedaHuesped, setBusquedaHuesped] = useState("");

  const validarFechas = (): boolean => {
    if (!fechaDesde || !fechaHasta) {
      setErrorFecha("Ambas fechas son obligatorias");
      return false;
    }
    const desde = new Date(fechaDesde);
    const hasta = new Date(fechaHasta);
    if (desde >= hasta) {
      setErrorFecha("La fecha 'Desde' debe ser anterior a 'Hasta'");
      return false;
    }
    setErrorFecha("");
    return true;
  };

  const handleConfirmarFechas = () => {
    if (validarFechas()) {
      setPaso("habitaciones");
    }
  };

  const handleToggleHabitacion = (id: string) => {
    setSeleccionadas((prev) =>
      prev.includes(id) ? prev.filter((h) => h !== id) : [...prev, id]
    );
  };

  const handleContinuarHuesped = () => {
    if (seleccionadas.length === 0) {
      alert("Debe seleccionar al menos una habitación");
      return;
    }
    setPaso("huesped");
  };

  const handleSeleccionarHuesped = (id: string) => {
    setHuespedSeleccionado(id);
    setPaso("confirmacion");
  };

  const habSeleccionadas = HABITACIONES_MOCK.filter((h) => seleccionadas.includes(h.id));
  const totalPrecio = habSeleccionadas.reduce((acc, h) => acc + h.precioNoche, 0);
  const huespedFinal = HUESPEDES_MOCK.find((h) => h.id === huespedSeleccionado);

  const huespedsFiltrados = HUESPEDES_MOCK.filter(
    (h) =>
      busquedaHuesped === "" ||
      h.apellido.toLowerCase().includes(busquedaHuesped.toLowerCase()) ||
      h.nombres.toLowerCase().includes(busquedaHuesped.toLowerCase()) ||
      h.documento.includes(busquedaHuesped)
  );

  return (
    <div className="min-h-screen bg-slate-950 text-white p-8">
      <div className="max-w-6xl mx-auto">
        <Link href="/" className="text-amber-400 hover:text-amber-300 mb-6 inline-block">
          ← Volver
        </Link>

        <h1 className="text-4xl font-bold mb-2 text-amber-400">CU04 - Reservar Habitación</h1>
        <p className="text-slate-300 mb-8">Paso {paso === "fechas" ? 1 : paso === "habitaciones" ? 2 : paso === "huesped" ? 3 : 4} de 4</p>

        {/* PASO 1: Ingresar fechas */}
        {paso === "fechas" && (
          <div className="bg-slate-900 border border-slate-700 rounded-lg p-8 max-w-md">
            <h2 className="text-2xl font-semibold mb-6 text-amber-400">Ingrese las fechas de reserva</h2>
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
              <button
                onClick={handleConfirmarFechas}
                className="w-full mt-6 bg-amber-400 hover:bg-amber-500 text-slate-950 font-semibold py-2 px-4 rounded transition"
              >
                Continuar →
              </button>
            </div>
          </div>
        )}

        {/* PASO 2: Seleccionar habitaciones */}
        {paso === "habitaciones" && (
          <div className="space-y-6">
            <div className="bg-slate-900 border border-slate-700 rounded-lg p-8">
              <h2 className="text-2xl font-semibold mb-6 text-amber-400">Seleccione habitaciones disponibles</h2>
              <p className="text-slate-400 mb-4">
                Periodo: {new Date(fechaDesde).toLocaleDateString()} a {new Date(fechaHasta).toLocaleDateString()}
              </p>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
                {HABITACIONES_MOCK.map((hab) => (
                  <div
                    key={hab.id}
                    className={`border-2 p-4 rounded-lg cursor-pointer transition ${
                      seleccionadas.includes(hab.id)
                        ? "border-amber-400 bg-amber-400/10"
                        : "border-slate-600 bg-slate-800 hover:border-slate-500"
                    }`}
                    onClick={() => handleToggleHabitacion(hab.id)}
                  >
                    <input
                      type="checkbox"
                      checked={seleccionadas.includes(hab.id)}
                      onChange={() => handleToggleHabitacion(hab.id)}
                      className="mr-3 w-5 h-5 accent-amber-400"
                    />
                    <div className="inline-block">
                      <p className="font-semibold">Habitación {hab.numero}</p>
                      <p className="text-slate-400 text-sm">{hab.tipo} - Capacidad: {hab.capacidad} personas</p>
                      <p className="text-amber-400 font-semibold">${hab.precioNoche}/noche</p>
                    </div>
                  </div>
                ))}
              </div>

              {seleccionadas.length > 0 && (
                <div className="bg-slate-800 border border-slate-600 rounded-lg p-4 mb-6">
                  <p className="text-sm text-slate-400">Total: {seleccionadas.length} habitación(es) × $0 (cálculo de noches pendiente)</p>
                  <p className="text-lg font-semibold text-amber-400">Total estimado: ${totalPrecio * 1} (por noche)</p>
                </div>
              )}

              <div className="flex gap-4">
                <button
                  onClick={() => setPaso("fechas")}
                  className="flex-1 bg-slate-700 hover:bg-slate-600 text-white font-semibold py-2 px-4 rounded transition"
                >
                  ← Atrás
                </button>
                <button
                  onClick={handleContinuarHuesped}
                  disabled={seleccionadas.length === 0}
                  className="flex-1 bg-amber-400 hover:bg-amber-500 disabled:bg-slate-600 disabled:cursor-not-allowed text-slate-950 font-semibold py-2 px-4 rounded transition"
                >
                  Continuar →
                </button>
              </div>
            </div>
          </div>
        )}

        {/* PASO 3: Seleccionar huésped */}
        {paso === "huesped" && (
          <div className="space-y-6">
            <div className="bg-slate-900 border border-slate-700 rounded-lg p-8">
              <h2 className="text-2xl font-semibold mb-6 text-amber-400">Seleccione el huésped</h2>

              <div className="mb-6">
                <label className="block text-sm font-medium mb-2">Buscar por apellido, nombre o documento</label>
                <input
                  type="text"
                  placeholder="Ej: García, Juan, 12345678"
                  value={busquedaHuesped}
                  onChange={(e) => setBusquedaHuesped(e.target.value)}
                  className="w-full px-4 py-2 bg-slate-800 border border-slate-600 rounded text-white focus:border-amber-400 focus:outline-none"
                />
              </div>

              <div className="space-y-2 max-h-96 overflow-y-auto">
                {huespedsFiltrados.length > 0 ? (
                  huespedsFiltrados.map((huesped) => (
                    <button
                      key={huesped.id}
                      onClick={() => handleSeleccionarHuesped(huesped.id)}
                      className={`w-full text-left p-4 rounded-lg border-2 transition ${
                        huespedSeleccionado === huesped.id
                          ? "border-amber-400 bg-amber-400/10"
                          : "border-slate-600 bg-slate-800 hover:border-slate-500"
                      }`}
                    >
                      <p className="font-semibold">{huesped.apellido}, {huesped.nombres}</p>
                      <p className="text-slate-400 text-sm">Doc: {huesped.documento}</p>
                    </button>
                  ))
                ) : (
                  <p className="text-slate-400 text-center py-8">No se encontraron huéspedes</p>
                )}
              </div>

              <div className="flex gap-4 mt-8">
                <button
                  onClick={() => {
                    setPaso("habitaciones");
                    setBusquedaHuesped("");
                  }}
                  className="flex-1 bg-slate-700 hover:bg-slate-600 text-white font-semibold py-2 px-4 rounded transition"
                >
                  ← Atrás
                </button>
                <button
                  onClick={() => setPaso("confirmacion")}
                  disabled={!huespedSeleccionado}
                  className="flex-1 bg-amber-400 hover:bg-amber-500 disabled:bg-slate-600 disabled:cursor-not-allowed text-slate-950 font-semibold py-2 px-4 rounded transition"
                >
                  Continuar →
                </button>
              </div>
            </div>
          </div>
        )}

        {/* PASO 4: Confirmación */}
        {paso === "confirmacion" && (
          <div className="space-y-6">
            <div className="bg-slate-900 border border-slate-700 rounded-lg p-8">
              <h2 className="text-2xl font-semibold mb-8 text-amber-400">Confirmar reserva</h2>

              <div className="space-y-4 mb-8">
                <div className="bg-slate-800 rounded-lg p-4">
                  <p className="text-slate-400 text-sm">Huésped</p>
                  <p className="text-xl font-semibold">{huespedFinal?.apellido}, {huespedFinal?.nombres}</p>
                </div>

                <div className="bg-slate-800 rounded-lg p-4">
                  <p className="text-slate-400 text-sm">Período de reserva</p>
                  <p className="text-xl font-semibold">{new Date(fechaDesde).toLocaleDateString()} a {new Date(fechaHasta).toLocaleDateString()}</p>
                </div>

                <div className="bg-slate-800 rounded-lg p-4">
                  <p className="text-slate-400 text-sm mb-2">Habitaciones seleccionadas</p>
                  {habSeleccionadas.map((hab) => (
                    <p key={hab.id} className="font-semibold">
                      {hab.numero} - {hab.tipo} (${hab.precioNoche}/noche)
                    </p>
                  ))}
                </div>

                <div className="bg-amber-400/10 border-2 border-amber-400 rounded-lg p-4">
                  <p className="text-slate-400 text-sm">Importe total estimado</p>
                  <p className="text-2xl font-bold text-amber-400">${totalPrecio}</p>
                </div>
              </div>

              <div className="flex gap-4">
                <button
                  onClick={() => {
                    setPaso("huesped");
                    setBusquedaHuesped("");
                  }}
                  className="flex-1 bg-slate-700 hover:bg-slate-600 text-white font-semibold py-2 px-4 rounded transition"
                >
                  ← Atrás
                </button>
                <button
                  onClick={() => {
                    alert(`Reserva confirmada para ${huespedFinal?.apellido}, ${huespedFinal?.nombres}. Total: $${totalPrecio}`);
                    setPaso("fechas");
                    setFechaDesde("");
                    setFechaHasta("");
                    setSeleccionadas([]);
                    setHuespedSeleccionado("");
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
