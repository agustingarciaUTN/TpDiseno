"use client";

import Link from "next/link";
import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { BarChart3, Home, Calendar, Loader2 } from "lucide-react";
import { obtenerHabitaciones } from "@/lib/api"; // Asegúrate de tener esta función en api.ts

interface HabitacionEstado {
  id: string;
  numero: string;
  tipo: string;
  comodidad: "Simple" | "Doble" | "Triple" | "Suite";
  capacidad: number;
  estado: "DISPONIBLE" | "RESERVADA" | "OCUPADA" | "MANTENIMIENTO";
  huesped?: string;
  checkin?: string;
  checkout?: string;
}

const COMODIDADES_ORDEN = ["Simple", "Doble", "Triple", "Suite"];

type Paso = "fechaDesde" | "fechaHasta" | "grilla";

export default function EstadoHabitaciones() {
  const [paso, setPaso] = useState<Paso>("fechaDesde");
  const [fechaDesde, setFechaDesde] = useState("");
  const [fechaHasta, setFechaHasta] = useState("");
  const [errorFecha, setErrorFecha] = useState("");

  // Estado para datos reales
  const [habitaciones, setHabitaciones] = useState<HabitacionEstado[]>([]);
  const [loading, setLoading] = useState(false);

  // Función para convertir tipos de Java a UI
  const mapearTipoAComodidad = (tipoJava: string) => {
    const tipo = tipoJava.toUpperCase();
    if (tipo.includes("INDIVIDUAL")) return "Simple";
    if (tipo.includes("DOBLE")) return "Doble";
    if (tipo.includes("FAMILY") || tipo.includes("TRIPLE")) return "Triple";
    if (tipo.includes("SUITE")) return "Suite";
    return "Simple";
  };

  // Cargar habitaciones al montar el componente
  useEffect(() => {
    const fetchHabitaciones = async () => {
      setLoading(true);
      try {
        // @ts-ignore - Ignoramos tipado estricto temporalmente
        const data = await obtenerHabitaciones();

        const mapeadas = data.map((h: any) => ({
          id: h.numero,
          numero: h.numero,
          tipo: h.tipoHabitacion,
          comodidad: mapearTipoAComodidad(h.tipoHabitacion),
          capacidad: h.capacidad,
          // Nota: Aquí usamos el estado físico de la habitación.
          // Para ver ocupación real por fechas, necesitaríamos un endpoint nuevo en el backend.
          estado: h.estadoHabitacion === "FUERA_DE_SERVICIO" ? "MANTENIMIENTO" : "DISPONIBLE"
        }));

        setHabitaciones(mapeadas);
      } catch (error) {
        console.error("Error al cargar habitaciones:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchHabitaciones();
  }, []);

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
    // Limitamos a 14 días para que entre en pantalla
    let count = 0;
    while (actual < hasta && count < 14) {
      dias.push(new Date(actual));
      actual.setDate(actual.getDate() + 1);
      count++;
    }
    return dias;
  };

  const diasRango = generarDias();

  const conteo = {
    disponibles: habitaciones.filter((h) => h.estado === "DISPONIBLE").length,
    reservadas: habitaciones.filter((h) => h.estado === "RESERVADA").length,
    ocupadas: habitaciones.filter((h) => h.estado === "OCUPADA").length,
  };

  const getEstadoColor = (estado: string) => {
    switch (estado) {
      case "DISPONIBLE": return "bg-green-500 hover:bg-green-600";
      case "RESERVADA": return "bg-blue-500 hover:bg-blue-600";
      case "OCUPADA": return "bg-red-500 hover:bg-red-600";
      case "MANTENIMIENTO": return "bg-gray-500 hover:bg-gray-600";
      default: return "bg-slate-600";
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50 dark:from-slate-950 dark:via-slate-900 dark:to-slate-950">
      <main className="mx-auto max-w-6xl px-4 py-12 sm:px-6 lg:px-8">
        <div className="mb-8">
          <div className="mb-6 flex items-center gap-4">
            <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-gradient-to-br from-blue-600 to-indigo-600 text-white shadow-lg">
              <BarChart3 className="h-6 w-6" />
            </div>
            <div>
              <p className="text-xs font-semibold uppercase tracking-wider text-blue-600 dark:text-blue-400">CU05</p>
              <h1 className="text-3xl font-bold text-slate-900 dark:text-slate-50">Estado de Habitaciones</h1>
            </div>
          </div>
          <p className="text-slate-600 dark:text-slate-400">
            Paso {paso === "fechaDesde" ? 1 : paso === "fechaHasta" ? 2 : 3} de 3
          </p>
        </div>

        {/* PASO 1: Seleccionar Fecha Desde */}
        {paso === "fechaDesde" && (
          <Card className="p-6 max-w-md">
            <h2 className="text-xl font-semibold mb-4 text-slate-900 dark:text-slate-50">Seleccione fecha de inicio</h2>
            <div className="space-y-4">
              <div>
                <Label htmlFor="fechaDesde">Fecha Desde</Label>
                <Input
                  id="fechaDesde"
                  type="date"
                  value={fechaDesde}
                  onChange={(e) => {
                    setFechaDesde(e.target.value);
                    setErrorFecha("");
                  }}
                  autoFocus
                />
              </div>
              {errorFecha && (
                <Card className="border-red-200 bg-red-50 p-3 dark:border-red-900 dark:bg-red-950/20">
                  <p className="text-sm text-red-600 dark:text-red-400">{errorFecha}</p>
                </Card>
              )}
              <div className="flex gap-3">
                <Button onClick={handleConfirmarDesde} className="flex-1 gap-2">
                  Continuar
                  <Calendar className="h-4 w-4" />
                </Button>
                <Button variant="outline" asChild>
                  <Link href="/">
                    <Home className="mr-2 h-4 w-4" />
                    Inicio
                  </Link>
                </Button>
              </div>
            </div>
          </Card>
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
                <label className="block text-sm font-medium mb-2 text-white">Fecha Hasta</label>
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
            {loading ? (
                <div className="flex justify-center p-12">
                    <Loader2 className="h-10 w-10 animate-spin text-