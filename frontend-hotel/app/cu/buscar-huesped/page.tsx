"use client";

import Link from "next/link";
import { useState, FormEvent, ChangeEvent } from "react";
import {
  TipoDocumento,
  TIPO_DOCUMENTO_LABELS,
  VALIDATION,
  type BuscarHuespedForm,
} from "@/lib/types";

const INITIAL_FORM: BuscarHuespedForm = {
  apellido: "",
  nombres: "",
  tipoDocumento: "",
  nroDocumento: "",
};

export default function BuscarHuesped() {
  const [form, setForm] = useState<BuscarHuespedForm>(INITIAL_FORM);
  const [errors, setErrors] = useState<Partial<Record<keyof BuscarHuespedForm, string>>>({});
  const [isSearching, setIsSearching] = useState(false);

  // Determina si el documento debe ser solo numérico
  const isNumericDocRequired =
    form.tipoDocumento === TipoDocumento.DNI ||
    form.tipoDocumento === TipoDocumento.LE ||
    form.tipoDocumento === TipoDocumento.LC;

  const validateField = (name: keyof BuscarHuespedForm, value: string): string => {
    switch (name) {
      case "apellido":
      case "nombres":
        // Opcional: si está vacío no validamos
        if (!value.trim()) return "";
        if (value.length !== 1)
          return "Debe ingresar solo la primera letra";
        if (!VALIDATION.REGEX_NOMBRE.test(value))
          return "Solo puede contener letras";
        return "";

      case "tipoDocumento":
        // Opcional
        return "";

      case "nroDocumento":
        // Solo validar si hay tipo de documento seleccionado
        if (!form.tipoDocumento) return "";
        // Opcional si hay tipo seleccionado
        if (!value.trim()) return "";
        if (value.length < 6 || value.length > 15)
          return "Debe tener entre 6 y 15 caracteres";
        if (isNumericDocRequired && !VALIDATION.REGEX_DOCUMENTO_NUMERICO.test(value))
          return "Solo se permiten números para este tipo de documento";
        if (!isNumericDocRequired && !VALIDATION.REGEX_DOCUMENTO.test(value))
          return "El documento no debe contener espacios ni símbolos";
        return "";

      default:
        return "";
    }
  };

  const handleChange = (
    e: ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    const fieldName = name as keyof BuscarHuespedForm;

    setForm((prev) => ({ ...prev, [fieldName]: value }));

    // Validar en tiempo real mientras el usuario escribe
    const error = validateField(fieldName, value);
    setErrors((prev) => ({ ...prev, [fieldName]: error }));
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();

    // Validar todos los campos (todos opcionales, pero si tienen valor deben ser válidos)
    const newErrors: Partial<Record<keyof BuscarHuespedForm, string>> = {};
    (Object.keys(form) as Array<keyof BuscarHuespedForm>).forEach((key) => {
      const error = validateField(key, String(form[key]));
      if (error) newErrors[key] = error;
    });

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    setIsSearching(true);
    try {
      // TODO: llamar al backend GET /huespedes/buscar con query params
      // const results = await apiFetch<DtoHuesped[]>(`/huespedes/buscar?apellido=${form.apellido}&nombres=${form.nombres}&tipoDocumento=${form.tipoDocumento}&nroDocumento=${form.nroDocumento}`);
      console.log("Buscar con:", form);
      await new Promise((resolve) => setTimeout(resolve, 800));
      alert("Búsqueda completada (placeholder). Implementar listado de resultados.");
    } catch (error) {
      console.error("Error en búsqueda:", error);
      alert("Error al buscar huésped");
    } finally {
      setIsSearching(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-50">
      <main className="mx-auto flex max-w-4xl flex-col gap-6 px-6 py-12">
        <header className="space-y-3">
          <p className="text-xs uppercase tracking-[0.25em] text-amber-200/80">CU02</p>
          <h1 className="text-3xl font-semibold">Buscar huésped</h1>
          <p className="text-sm text-slate-200/80">
            Gestionar datos personales de los huéspedes. Si no hay coincidencias, el flujo
            continúa con el alta de huésped.
          </p>
        </header>

        <form
          onSubmit={handleSubmit}
          className="rounded-2xl border border-white/10 bg-white/5 p-6 shadow-lg shadow-slate-900/40"
        >
          <h2 className="text-lg font-semibold">Datos de búsqueda</h2>
          <p className="mt-1 text-xs text-slate-200/70">
            Todos los campos son opcionales. Si no ingresás ningún dato, se listarán todos los huéspedes.
          </p>

          <div className="mt-6 grid gap-5 md:grid-cols-2">
            {/* Apellido */}
            <div className="space-y-2">
              <label htmlFor="apellido" className="block text-sm font-semibold text-slate-200">
                Apellido
              </label>
              <input
                type="text"
                id="apellido"
                name="apellido"
                value={form.apellido}
                onChange={handleChange}
                className={`w-full rounded-lg border ${
                  errors.apellido ? "border-red-400" : "border-white/10"
                } bg-slate-900 px-4 py-2 text-sm text-slate-50 placeholder-slate-400 focus:border-amber-300 focus:outline-none focus:ring-2 focus:ring-amber-300/30`}
                placeholder="Ej: G (si quieres buscar García)"
                maxLength={50}
              />
              {errors.apellido && (
                <p className="text-xs text-red-400">{errors.apellido}</p>
              )}
            </div>

            {/* Nombre */}
            <div className="space-y-2">
              <label htmlFor="nombres" className="block text-sm font-semibold text-slate-200">
                Nombre(s)
              </label>
              <input
                type="text"
                id="nombres"
                name="nombres"
                value={form.nombres}
                onChange={handleChange}
                className={`w-full rounded-lg border ${
                  errors.nombres ? "border-red-400" : "border-white/10"
                } bg-slate-900 px-4 py-2 text-sm text-slate-50 placeholder-slate-400 focus:border-amber-300 focus:outline-none focus:ring-2 focus:ring-amber-300/30`}
                placeholder="Ej: A (si quieres buscar Agustin)"
                maxLength={50}
              />
              {errors.nombres && (
                <p className="text-xs text-red-400">{errors.nombres}</p>
              )}
            </div>

            {/* Tipo de documento */}
            <div className="space-y-2">
              <label htmlFor="tipoDocumento" className="block text-sm font-semibold text-slate-200">
                Tipo de documento
              </label>
              <select
                id="tipoDocumento"
                name="tipoDocumento"
                value={form.tipoDocumento}
                onChange={handleChange}
                className={`w-full rounded-lg border ${
                  errors.tipoDocumento ? "border-red-400" : "border-white/10"
                } bg-slate-900 px-4 py-2 text-sm text-slate-50 focus:border-amber-300 focus:outline-none focus:ring-2 focus:ring-amber-300/30`}
              >
                <option value="">Seleccionar...</option>
                {Object.values(TipoDocumento).map((tipo) => (
                  <option key={tipo} value={tipo}>
                    {TIPO_DOCUMENTO_LABELS[tipo]}
                  </option>
                ))}
              </select>
              {errors.tipoDocumento && (
                <p className="text-xs text-red-400">{errors.tipoDocumento}</p>
              )}
            </div>

            {/* Número de documento */}
            <div className="space-y-2">
              <label htmlFor="nroDocumento" className="block text-sm font-semibold text-slate-200">
                Número de documento
              </label>
              <input
                type="text"
                id="nroDocumento"
                name="nroDocumento"
                value={form.nroDocumento}
                onChange={handleChange}
                disabled={!form.tipoDocumento}
                className={`w-full rounded-lg border ${
                  errors.nroDocumento ? "border-red-400" : "border-white/10"
                } bg-slate-900 px-4 py-2 text-sm text-slate-50 placeholder-slate-400 focus:border-amber-300 focus:outline-none focus:ring-2 focus:ring-amber-300/30 disabled:cursor-not-allowed disabled:opacity-50`}
                placeholder={
                  isNumericDocRequired ? "Solo números" : "Números y letras"
                }
                maxLength={15}
              />
              {errors.nroDocumento && (
                <p className="text-xs text-red-400">{errors.nroDocumento}</p>
              )}
              <p className="text-xs text-slate-200/60">
                {!form.tipoDocumento
                  ? "Seleccioná un tipo de documento primero"
                  : isNumericDocRequired
                  ? "Solo números para DNI, LE y LC"
                  : "Alfanumérico para Pasaporte y Otro"}
              </p>
            </div>
          </div>

          <div className="mt-6 flex flex-wrap gap-3 text-sm">
            <button
              type="submit"
              disabled={isSearching}
              className="rounded-full bg-amber-400 px-5 py-2 font-semibold text-slate-950 transition hover:bg-amber-300 disabled:opacity-50"
            >
              {isSearching ? "Buscando..." : "Buscar"}
            </button>
            <Link
              href="/"
              className="rounded-full border border-white/10 px-5 py-2 font-semibold text-slate-50 transition hover:border-amber-200/60 hover:bg-amber-100/10"
            >
              Cancelar
            </Link>
          </div>
        </form>

        <section className="rounded-2xl border border-white/10 bg-white/5 p-5 shadow-lg shadow-slate-900/40">
          <h2 className="text-lg font-semibold">Próximos pasos</h2>
          <ul className="mt-3 space-y-2 text-sm text-slate-200/80">
            <li className="flex gap-2">
              <span className="text-amber-200">•</span>
              <span>Al buscar, se listará el resultado con coincidencias</span>
            </li>
            <li className="flex gap-2">
              <span className="text-amber-200">•</span>
              <span>Si no hay resultados, se habilitará el botón de alta</span>
            </li>
            <li className="flex gap-2">
              <span className="text-amber-200">•</span>
              <span>Seleccionando un huésped se continúa al flujo correspondiente</span>
            </li>
          </ul>
        </section>
      </main>
    </div>
  );
}
