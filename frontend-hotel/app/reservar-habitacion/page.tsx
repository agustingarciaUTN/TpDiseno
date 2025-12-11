"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Hotel, Home, Calendar, UserCheck, CheckCircle } from "lucide-react";

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
  apellido: string;
  nombres: string;
  telefono: string;
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

type Paso = "fechaDesde" | "fechaHasta" | "grilla" | "datosHuesped" | "confirmacion";

// Helper function to create date in local timezone
const createLocalDate = (dateString: string): Date => {
  const [year, month, day] = dateString.split("-").map(Number);
  return new Date(year, month - 1, day);
};

export default function ReservarHabitacion() {
  const router = useRouter();
  const [paso, setPaso] = useState<Paso>("fechaDesde");
  const [fechaDesde, setFechaDesde] = useState("");
  const [fechaHasta, setFechaHasta] = useState("");
  const [errorFecha, setErrorFecha] = useState("");
  
  // Estado de selección de habitaciones
  const [selecciones, setSelecciones] = useState<SeleccionHabitacion[]>([]);
  const [seleccionActual, setSeleccionActual] = useState<{
    habitacionId: string;
    diaInicio: number | null;
  } | null>(null);

  // Datos del huésped responsable
  const [datosHuesped, setDatosHuesped] = useState<DatosHuesped>({
    apellido: "",
    nombres: "",
    telefono: "",
  });
  const [erroresHuesped, setErroresHuesped] = useState<Partial<DatosHuesped>>({});

  // Validaciones de fechas
  const validarFechaDesde = (): boolean => {
    if (!fechaDesde) {
      setErrorFecha("Debe seleccionar una fecha");
      return false;
    }
    // VALIDACIÓN DEL BACKEND: La fecha de ingreso no puede ser anterior al día de hoy
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);
    const fechaSeleccionada = createLocalDate(fechaDesde);
    if (fechaSeleccionada < hoy) {
      setErrorFecha("La fecha de ingreso no puede ser anterior al día de hoy");
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
    // VALIDACIÓN DEL BACKEND: fechaHasta > fechaDesde
    if (desde >= hasta) {
      setErrorFecha("La fecha 'Hasta' debe ser posterior a la fecha 'Desde'");
      return false;
    }
    // VALIDACIÓN DEL BACKEND: @Future - debe ser futura respecto a hoy
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);
    if (hasta <= hoy) {
      setErrorFecha("La fecha de egreso debe ser futura");
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

  // Generar días del rango
  const generarDias = (): Date[] => {
    if (!fechaDesde || !fechaHasta) return [];
    const desde = createLocalDate(fechaDesde);
    const hasta = createLocalDate(fechaHasta);
    const dias: Date[] = [];
    const actual = new Date(desde);
    while (actual < hasta) {
      dias.push(new Date(actual));
      actual.setDate(actual.getDate() + 1);
    }
    return dias;
  };

  const diasRango = generarDias();

  // Verificar si una celda está disponible para selección
  const esCeldaDisponible = (habitacionId: string, diaIdx: number): boolean => {
    const habitacion = HABITACIONES_MOCK.find(h => h.id === habitacionId);
    if (!habitacion || habitacion.estado !== "DISPONIBLE") return false;

    // Verificar que no esté ya seleccionada
    return !selecciones.some(sel => 
      sel.habitacionId === habitacionId && 
      diaIdx >= sel.diaInicio && 
      diaIdx <= sel.diaFin
    );
  };

  // Manejar click en celda de la grilla
  const handleClickCelda = (habitacionId: string, diaIdx: number) => {
    if (!esCeldaDisponible(habitacionId, diaIdx)) return;

    if (!seleccionActual || seleccionActual.habitacionId !== habitacionId) {
      // Primer click: establecer punto de inicio
      setSeleccionActual({ habitacionId, diaInicio: diaIdx });
    } else {
      // Segundo click: establecer punto de fin y confirmar selección
      const diaInicio = seleccionActual.diaInicio!;
      const diaFin = diaIdx;
      
      // Validar que todos los días en el rango estén disponibles
      const rangoValido = Array.from(
        { length: Math.abs(diaFin - diaInicio) + 1 },
        (_, i) => Math.min(diaInicio, diaFin) + i
      ).every(dia => esCeldaDisponible(habitacionId, dia));

      if (rangoValido) {
        setSelecciones(prev => [...prev, {
          habitacionId,
          diaInicio: Math.min(diaInicio, diaFin),
          diaFin: Math.max(diaInicio, diaFin),
        }]);
        setSeleccionActual(null);
      } else {
        alert("Hay días no disponibles en el rango seleccionado");
        setSeleccionActual(null);
      }
    }
  };

  // Verificar si una celda está seleccionada
  const esCeldaSeleccionada = (habitacionId: string, diaIdx: number): boolean => {
    return selecciones.some(sel =>
      sel.habitacionId === habitacionId &&
      diaIdx >= sel.diaInicio &&
      diaIdx <= sel.diaFin
    );
  };

  // Verificar si es inicio de selección actual
  const esInicioSeleccionActual = (habitacionId: string, diaIdx: number): boolean => {
    return seleccionActual?.habitacionId === habitacionId && 
           seleccionActual.diaInicio === diaIdx;
  };

  // Remover una selección
  const handleRemoverSeleccion = (index: number) => {
    setSelecciones(prev => prev.filter((_, i) => i !== index));
  };

  // Continuar a datos del huésped
  const handleContinuarHuesped = () => {
    if (selecciones.length === 0) {
      alert("Debe seleccionar al menos una habitación con un rango de fechas");
      return;
    }
    setPaso("datosHuesped");
  };

  // Validar datos del huésped
  const validarDatosHuesped = (): boolean => {
    const errores: Partial<DatosHuesped> = {};
    
    // Regex patterns del backend DtoReserva
    const regexNombre = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/;
    const regexTelefono = /^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\s./0-9]*$/;

    // Apellido - BACKEND: @NotBlank, @Pattern(REGEX_NOMBRE)
    if (!datosHuesped.apellido.trim()) {
      errores.apellido = "El apellido es obligatorio";
    } else if (datosHuesped.apellido.length < 2 || datosHuesped.apellido.length > 50) {
      errores.apellido = "El apellido debe tener entre 2 y 50 caracteres";
    } else if (!regexNombre.test(datosHuesped.apellido)) {
      errores.apellido = "El apellido solo puede contener letras y espacios";
    }

    // Nombres - BACKEND: @NotBlank, @Pattern(REGEX_NOMBRE)
    if (!datosHuesped.nombres.trim()) {
      errores.nombres = "El nombre es obligatorio";
    } else if (datosHuesped.nombres.length < 2 || datosHuesped.nombres.length > 50) {
      errores.nombres = "El nombre debe tener entre 2 y 50 caracteres";
    } else if (!regexNombre.test(datosHuesped.nombres)) {
      errores.nombres = "El nombre solo puede contener letras y espacios";
    }

    // Teléfono - BACKEND: @NotBlank, @Pattern(REGEX_TELEFONO)
    if (!datosHuesped.telefono.trim()) {
      errores.telefono = "El teléfono es obligatorio";
    } else {
      // Normalizar teléfono: agregar +54 si no tiene código de país
      let telefonoNormalizado = datosHuesped.telefono.trim();
      if (!telefonoNormalizado.startsWith('+')) {
        telefonoNormalizado = '+54 ' + telefonoNormalizado;
        setDatosHuesped(prev => ({ ...prev, telefono: telefonoNormalizado }));
      }
      if (!regexTelefono.test(telefonoNormalizado)) {
        errores.telefono = "Formato de teléfono inválido";
      } else if (telefonoNormalizado.replace(/[^0-9]/g, '').length < 8) {
        errores.telefono = "El teléfono debe tener al menos 8 dígitos";
      }
    }

    setErroresHuesped(errores);
    return Object.keys(errores).length === 0;
  };

  const handleConfirmarHuesped = () => {
    if (validarDatosHuesped()) {
      setPaso("confirmacion");
    }
  };

  const handleVolverPaso = () => {
    if (paso === "fechaHasta") {
      setErrorFecha("");
      setPaso("fechaDesde");
    } else if (paso === "grilla") {
      setErrorFecha("");
      setPaso("fechaHasta");
    } else if (paso === "datosHuesped") {
      setPaso("grilla");
    } else if (paso === "confirmacion") {
      setPaso("datosHuesped");
    }
  };

  // Calcular precio total
  const calcularTotal = (): number => {
    return selecciones.reduce((total, sel) => {
      const habitacion = HABITACIONES_MOCK.find(h => h.id === sel.habitacionId);
      if (!habitacion) return total;
      const noches = sel.diaFin - sel.diaInicio + 1;
      return total + (habitacion.precioNoche * noches);
    }, 0);
  };

  const getEstadoColor = (estado: string) => {
    switch (estado) {
      case "DISPONIBLE":
        return "bg-green-600 dark:bg-green-500";
      case "RESERVADA":
        return "bg-orange-500 dark:bg-orange-400";
      case "OCUPADA":
        return "bg-slate-600 dark:bg-slate-500";
      default:
        return "bg-slate-600";
    }
  };

  const conteo = {
    disponibles: HABITACIONES_MOCK.filter(h => h.estado === "DISPONIBLE").length,
    reservadas: HABITACIONES_MOCK.filter(h => h.estado === "RESERVADA").length,
    ocupadas: HABITACIONES_MOCK.filter(h => h.estado === "OCUPADA").length,
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50 dark:from-slate-950 dark:via-slate-900 dark:to-slate-950">
      <main className="mx-auto max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
        <div className="mb-8">
          <div className="mb-6 flex items-center gap-4">
            <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-gradient-to-br from-blue-600 to-indigo-600 text-white shadow-lg">
              <Hotel className="h-6 w-6" />
            </div>
            <div>
              <p className="text-xs font-semibold uppercase tracking-wider text-blue-600 dark:text-blue-400">CU04</p>
              <h1 className="text-3xl font-bold text-slate-900 dark:text-slate-50">Reservar Habitación</h1>
            </div>
          </div>
          <p className="text-slate-600 dark:text-slate-400">
            Paso {paso === "fechaDesde" ? 1 : paso === "fechaHasta" ? 2 : paso === "grilla" ? 3 : paso === "datosHuesped" ? 4 : 5} de 5
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
          <Card className="p-6 max-w-md">
            <h2 className="text-xl font-semibold mb-4 text-slate-900 dark:text-slate-50">Seleccione fecha de fin</h2>
            <div className="space-y-4">
              <Card className="border-blue-200 bg-blue-50/50 p-4 dark:border-blue-900 dark:bg-blue-950/20">
                <p className="text-sm text-slate-600 dark:text-slate-400">Fecha desde:</p>
                <p className="text-lg font-semibold text-slate-900 dark:text-slate-50">{createLocalDate(fechaDesde).toLocaleDateString()}</p>
              </Card>
              <div>
                <Label htmlFor="fechaHasta">Fecha Hasta</Label>
                <Input
                  id="fechaHasta"
                  type="date"
                  value={fechaHasta}
                  onChange={(e) => {
                    setFechaHasta(e.target.value);
                    setErrorFecha("");
                  }}
                />
              </div>
              {errorFecha && (
                <Card className="border-red-200 bg-red-50 p-3 dark:border-red-900 dark:bg-red-950/20">
                  <p className="text-sm text-red-600 dark:text-red-400">{errorFecha}</p>
                </Card>
              )}
              <div className="flex gap-3 mt-6">
                <Button onClick={handleVolverPaso} variant="outline" className="flex-1">
                  ← Atrás
                </Button>
                <Button onClick={handleConfirmarHasta} className="flex-1 gap-2">
                  Continuar
                  <Calendar className="h-4 w-4" />
                </Button>
              </div>
            </div>
          </Card>
        )}

        {/* PASO 3: Grilla interactiva (CU05 integrado) */}
        {paso === "grilla" && (
          <div className="space-y-6">
            {/* Resumen de estados */}
            <div className="grid grid-cols-3 gap-4">
              <Card className="border-l-4 border-green-500 p-4">
                <p className="text-sm text-slate-600 dark:text-slate-400">Disponibles</p>
                <p className="text-3xl font-bold text-green-600 dark:text-green-400">{conteo.disponibles}</p>
              </Card>
              <Card className="border-l-4 border-blue-500 p-4">
                <p className="text-sm text-slate-600 dark:text-slate-400">Reservadas</p>
                <p className="text-3xl font-bold text-blue-600 dark:text-blue-400">{conteo.reservadas}</p>
              </Card>
              <Card className="border-l-4 border-red-500 p-4">
                <p className="text-sm text-slate-600 dark:text-slate-400">Ocupadas</p>
                <p className="text-3xl font-bold text-red-600 dark:text-red-400">{conteo.ocupadas}</p>
              </Card>
            </div>

            {/* Instrucciones */}
            <Card className="border-blue-200 bg-blue-50/50 p-4 dark:border-blue-900 dark:bg-blue-950/20">
              <p className="text-sm text-slate-700 dark:text-slate-300">
                <strong>Instrucciones:</strong> Click en una celda para iniciar selección, luego click en otra celda de la misma habitación para finalizar el rango.
                Puede seleccionar múltiples habitaciones con diferentes rangos.
              </p>
            </Card>

            {/* Grilla */}
            <Card className="p-6 overflow-x-auto">
              <h2 className="text-xl font-semibold mb-4 text-slate-900 dark:text-slate-50">
                Del {createLocalDate(fechaDesde).toLocaleDateString()} al {createLocalDate(fechaHasta).toLocaleDateString()}
              </h2>
              
              <div className="min-w-max">
                <table className="w-full text-sm border-collapse">
                  <thead>
                    <tr className="bg-slate-100 dark:bg-slate-800">
                      <th className="border px-4 py-2 text-left font-semibold text-slate-900 dark:text-slate-50 w-32">
                        Habitación
                      </th>
                      {diasRango.map((dia, idx) => (
                        <th
                          key={idx}
                          className="border px-3 py-2 text-center font-semibold text-slate-900 dark:text-slate-50 w-24 text-xs"
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
                      const habsComodidad = HABITACIONES_MOCK.filter(
                        (h) => h.comodidad === comodidad
                      );
                      return habsComodidad.length > 0
                        ? habsComodidad.map((hab, habIdx) => (
                            <tr
                              key={hab.id}
                              className={habIdx % 2 === 0 ? "bg-slate-50 dark:bg-slate-800/50" : "bg-white dark:bg-slate-900/30"}
                            >
                              <td className="border px-4 py-2 font-semibold">
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

                                return (
                                  <td
                                    key={`${hab.id}-${dayIdx}`}
                                    className="border px-2 py-2 text-center"
                                  >
                                    <div
                                      onClick={() => handleClickCelda(hab.id, dayIdx)}
                                      className={`rounded px-2 py-1 text-xs font-semibold text-white transition cursor-pointer ${
                                        seleccionada
                                          ? "bg-blue-600 hover:bg-blue-700"
                                          : inicioActual
                                          ? "bg-purple-500 animate-pulse"
                                          : disponible
                                          ? getEstadoColor(hab.estado) + " hover:brightness-110"
                                          : "bg-slate-400 dark:bg-slate-600 cursor-not-allowed opacity-50"
                                      }`}
                                      title={
                                        seleccionada
                                          ? "Seleccionada - Click para remover"
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
                <div className="mt-4 flex gap-4 text-xs text-slate-600 dark:text-slate-400">
                  <span>○ = Disponible</span>
                  <span>✓ = Seleccionada</span>
                  <span>R = Reservada</span>
                  <span>X = Ocupada</span>
                </div>
              </div>
            </Card>

            {/* Resumen de selecciones */}
            {selecciones.length > 0 && (
              <Card className="p-6">
                <h3 className="text-lg font-semibold mb-4 text-slate-900 dark:text-slate-50">Habitaciones seleccionadas</h3>
                <div className="space-y-3">
                  {selecciones.map((sel, idx) => {
                    const hab = HABITACIONES_MOCK.find(h => h.id === sel.habitacionId);
                    if (!hab) return null;
                    const noches = sel.diaFin - sel.diaInicio + 1;
                    const subtotal = hab.precioNoche * noches;
                    return (
                      <div key={idx} className="flex justify-between items-center bg-slate-50 dark:bg-slate-800 p-4 rounded-lg border border-slate-200 dark:border-slate-700">
                        <div>
                          <p className="font-semibold text-slate-900 dark:text-white">Habitación {hab.numero} ({hab.tipo})</p>
                          <p className="text-sm text-slate-600 dark:text-slate-400">
                            {diasRango[sel.diaInicio]?.toLocaleDateString()} - {diasRango[sel.diaFin]?.toLocaleDateString()} ({noches} noche{noches > 1 ? "s" : ""})
                          </p>
                        </div>
                        <div className="flex items-center gap-4">
                          <p className="text-blue-600 dark:text-blue-400 font-semibold">${subtotal}</p>
                          <Button
                            onClick={() => handleRemoverSeleccion(idx)}
                            variant="ghost"
                            size="sm"
                            className="text-red-600 hover:text-red-700 hover:bg-red-50 dark:text-red-400 dark:hover:text-red-300 dark:hover:bg-red-950/20"
                          >
                            ✕ Quitar
                          </Button>
                        </div>
                      </div>
                    );
                  })}
                </div>
                <div className="mt-4 pt-4 border-t border-slate-200 dark:border-slate-700 flex justify-between items-center">
                  <p className="text-lg font-semibold text-slate-900 dark:text-slate-50">Total:</p>
                  <p className="text-2xl font-bold text-blue-600 dark:text-blue-400">${calcularTotal()}</p>
                </div>
              </Card>
            )}

            <div className="flex gap-3">
              <Button onClick={handleVolverPaso} variant="outline" className="flex-1">
                ← Atrás
              </Button>
              <Button
                onClick={handleContinuarHuesped}
                disabled={selecciones.length === 0}
                className="flex-1 gap-2"
              >
                Continuar
                <UserCheck className="h-4 w-4" />
              </Button>
            </div>
          </div>
        )}

        {/* PASO 4: Datos del Huésped Responsable */}
        {paso === "datosHuesped" && (
          <Card className="p-6 max-w-2xl">
            <h2 className="text-xl font-semibold mb-4 text-slate-900 dark:text-slate-50">Datos del huésped responsable</h2>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <Label htmlFor="apellido">Apellido *</Label>
                <Input
                  id="apellido"
                  type="text"
                  value={datosHuesped.apellido}
                  onChange={(e) => {
                    const valor = e.target.value;
                    setDatosHuesped({ ...datosHuesped, apellido: valor });
                    
                    // Validar en tiempo real
                    if (!valor.trim()) {
                      setErroresHuesped({ ...erroresHuesped, apellido: "El apellido es obligatorio" });
                    } else if (valor.length < 2 || valor.length > 50) {
                      setErroresHuesped({ ...erroresHuesped, apellido: "El apellido debe tener entre 2 y 50 caracteres" });
                    } else if (!/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/.test(valor)) {
                      setErroresHuesped({ ...erroresHuesped, apellido: "El apellido solo puede contener letras y espacios" });
                    } else {
                      setErroresHuesped({ ...erroresHuesped, apellido: undefined });
                    }
                  }}
                  placeholder="Ej: González"
                  className={erroresHuesped.apellido ? "border-red-500" : ""}
                />
                {erroresHuesped.apellido && (
                  <p className="text-xs text-red-500 mt-1">{erroresHuesped.apellido}</p>
                )}
              </div>
              <div>
                <Label htmlFor="nombres">Nombres *</Label>
                <Input
                  id="nombres"
                  type="text"
                  value={datosHuesped.nombres}
                  onChange={(e) => {
                    const valor = e.target.value;
                    setDatosHuesped({ ...datosHuesped, nombres: valor });
                    
                    // Validar en tiempo real
                    if (!valor.trim()) {
                      setErroresHuesped({ ...erroresHuesped, nombres: "El nombre es obligatorio" });
                    } else if (valor.length < 2 || valor.length > 50) {
                      setErroresHuesped({ ...erroresHuesped, nombres: "El nombre debe tener entre 2 y 50 caracteres" });
                    } else if (!/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/.test(valor)) {
                      setErroresHuesped({ ...erroresHuesped, nombres: "El nombre solo puede contener letras y espacios" });
                    } else {
                      setErroresHuesped({ ...erroresHuesped, nombres: undefined });
                    }
                  }}
                  placeholder="Ej: Juan Carlos"
                  className={erroresHuesped.nombres ? "border-red-500" : ""}
                />
                {erroresHuesped.nombres && (
                  <p className="text-xs text-red-500 mt-1">{erroresHuesped.nombres}</p>
                )}
              </div>
              <div className="md:col-span-2">
                <Label htmlFor="telefono">Teléfono *</Label>
                <Input
                  id="telefono"
                  type="tel"
                  value={datosHuesped.telefono}
                  onChange={(e) => {
                    const valor = e.target.value;
                    setDatosHuesped({ ...datosHuesped, telefono: valor });
                    
                    // Validar en tiempo real
                    if (!valor.trim()) {
                      setErroresHuesped({ ...erroresHuesped, telefono: "El teléfono es obligatorio" });
                    } else {
                      // Normalizar teléfono: agregar +54 si no tiene código de país
                      let telefonoNormalizado = valor.trim();
                      if (!telefonoNormalizado.startsWith('+')) {
                        telefonoNormalizado = '+54 ' + telefonoNormalizado;
                      }
                      // Validar formato y longitud mínima
                      if (!/^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\s./0-9]*$/.test(telefonoNormalizado)) {
                        setErroresHuesped({ ...erroresHuesped, telefono: "Formato de teléfono inválido" });
                      } else if (telefonoNormalizado.replace(/[^0-9]/g, '').length < 8) {
                        setErroresHuesped({ ...erroresHuesped, telefono: "El teléfono debe tener al menos 8 dígitos" });
                      } else {
                        setErroresHuesped({ ...erroresHuesped, telefono: undefined });
                      }
                    }
                  }}
                  placeholder="+54 11 1234-5678 o 11 1234-5678"
                  className={erroresHuesped.telefono ? "border-red-500" : ""}
                />
                {erroresHuesped.telefono && (
                  <p className="text-xs text-red-500 mt-1">{erroresHuesped.telefono}</p>
                )}
                <p className="text-xs text-slate-500 mt-1">Mínimo 8 dígitos. Si no incluye +, se asume +54 (Argentina)</p>
              </div>
            </div>

            <div className="flex gap-3 mt-6">
              <Button onClick={handleVolverPaso} variant="outline" className="flex-1">
                ← Atrás
              </Button>
              <Button
                onClick={handleConfirmarHuesped}
                className="flex-1 gap-2"
              >
                Continuar
                <CheckCircle className="h-4 w-4" />
              </Button>
            </div>
          </Card>
        )}

        {/* PASO 5: Confirmación */}
        {paso === "confirmacion" && (
          <Card className="max-w-2xl p-6">
            <h2 className="text-xl font-semibold mb-6 text-slate-900 dark:text-slate-50">Confirmar reserva</h2>

            {/* Datos del huésped */}
            <div className="mb-6">
              <h3 className="text-lg font-semibold mb-3 text-slate-900 dark:text-slate-50">Huésped Responsable</h3>
              <Card className="border-blue-200 bg-blue-50/50 p-4 dark:border-blue-900 dark:bg-blue-950/20">
                <div className="space-y-2">
                  <p className="text-sm"><span className="font-medium text-slate-600 dark:text-slate-400">Nombre:</span> <span className="text-slate-900 dark:text-slate-50">{datosHuesped.nombres} {datosHuesped.apellido}</span></p>
                  <p className="text-sm"><span className="font-medium text-slate-600 dark:text-slate-400">Teléfono:</span> <span className="text-slate-900 dark:text-slate-50">{datosHuesped.telefono}</span></p>
                </div>
              </Card>
            </div>

            {/* Habitaciones reservadas */}
            <div className="mb-6">
              <h3 className="text-lg font-semibold mb-3 text-slate-900 dark:text-slate-50">Habitaciones y Fechas</h3>
              <div className="space-y-2">
                {selecciones.map((sel, idx) => {
                  const hab = HABITACIONES_MOCK.find(h => h.id === sel.habitacionId);
                  if (!hab) return null;
                  const noches = sel.diaFin - sel.diaInicio + 1;
                  const subtotal = hab.precioNoche * noches;
                  return (
                    <Card key={idx} className="p-4">
                      <p className="font-semibold text-slate-900 dark:text-slate-50">Habitación {hab.numero} - {hab.tipo}</p>
                      <p className="text-sm text-slate-600 dark:text-slate-400">
                        {diasRango[sel.diaInicio]?.toLocaleDateString()} - {diasRango[sel.diaFin]?.toLocaleDateString()}
                      </p>
                      <p className="font-semibold text-blue-600 dark:text-blue-400">{noches} noche{noches > 1 ? "s" : ""} × ${hab.precioNoche} = ${subtotal}</p>
                    </Card>
                  );
                })}
              </div>
            </div>

            {/* Total */}
            <Card className="border-green-200 bg-green-50/50 p-4 dark:border-green-900 dark:bg-green-950/20">
              <p className="text-sm text-slate-600 dark:text-slate-400">Importe total</p>
              <p className="text-2xl font-bold text-green-600 dark:text-green-400">${calcularTotal()}</p>
            </Card>

            <div className="flex gap-3 mt-6">
              <Button onClick={handleVolverPaso} variant="outline" className="flex-1">
                ← Atrás
              </Button>
              <Button
                onClick={() => {
                  alert(`La operación ha culminado con éxito. Reserva confirmada para ${datosHuesped.nombres} ${datosHuesped.apellido}. Total: $${calcularTotal()}`);
                  // Redirect to home after brief delay
                  setTimeout(() => {
                    router.push("/");
                  }, 500);
                }}
                className="flex-1 gap-2 bg-green-600 hover:bg-green-700"
              >
                <CheckCircle className="h-4 w-4" />
                Confirmar reserva
              </Button>
            </div>
          </Card>
        )}
      </main>
    </div>
  );
}
