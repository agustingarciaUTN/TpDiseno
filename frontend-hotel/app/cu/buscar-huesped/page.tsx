"use client";

import Link from "next/link";
import { useState, FormEvent, ChangeEvent } from "react";
import { apiFetch } from "@/lib/api"; // <--- IMPORTANTE
import {
    TipoDocumento,
    TIPO_DOCUMENTO_LABELS,
    VALIDATION,
    type BuscarHuespedForm,
    type DtoHuesped, // <--- IMPORTANTE
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

    // Nuevo estado para los resultados
    const [resultados, setResultados] = useState<DtoHuesped[] | null>(null);

    const validateField = (name: keyof BuscarHuespedForm, value: string): string => {
        switch (name) {
            case "apellido":
            case "nombres":
                if (!value.trim()) return "";
                // En búsqueda permitimos más de 1 letra, pero validamos caracteres
                if (!VALIDATION.REGEX_NOMBRE.test(value))
                    return "Solo puede contener letras";
                return "";

            case "nroDocumento":
                if (!form.tipoDocumento && value) return "Seleccione tipo primero";
                if (!value.trim()) return "";
                if (!VALIDATION.REGEX_DOCUMENTO.test(value))
                    return "El documento no debe contener espacios ni símbolos";
                return "";

            default:
                return "";
        }
    };

    const handleChange = (e: ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value } = e.target;
        setForm((prev) => ({ ...prev, [name]: value }));
        const error = validateField(name as keyof BuscarHuespedForm, value);
        setErrors((prev) => ({ ...prev, [name]: error }));
    };

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        setResultados(null); // Limpiar resultados anteriores

        // Validación previa al envío
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
            // --- CONEXIÓN CON BACKEND ---
            // El backend espera un POST con el objeto JSON en el cuerpo
            const data = await apiFetch<DtoHuesped[]>("/huespedes/buscar", {
                method: "POST",
                body: {
                    // Enviamos null si el string está vacío para que el backend lo ignore correctamente
                    apellido: form.apellido || null,
                    nombres: form.nombres || null,
                    tipoDocumento: form.tipoDocumento || null,
                    nroDocumento: form.nroDocumento || null,
                },
            });

            setResultados(data); // Guardamos la respuesta

        } catch (error: any) {
            console.error("Error en búsqueda:", error);
            alert("Error al buscar: " + error.message);
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
                        Gestionar datos personales de los huéspedes.
                    </p>
                </header>

                {/* --- FORMULARIO --- */}
                <form
                    onSubmit={handleSubmit}
                    className="rounded-2xl border border-white/10 bg-white/5 p-6 shadow-lg shadow-slate-900/40"
                >
                    <div className="mt-2 grid gap-5 md:grid-cols-2">
                        <div className="space-y-2">
                            <label className="block text-sm font-semibold text-slate-200">Apellido</label>
                            <input
                                type="text"
                                name="apellido"
                                value={form.apellido}
                                onChange={handleChange}
                                className="w-full rounded-lg border border-white/10 bg-slate-900 px-4 py-2 text-sm text-slate-50 focus:border-amber-300 focus:outline-none focus:ring-2 focus:ring-amber-300/30"
                                placeholder="Ej: Garcia"
                            />
                            {errors.apellido && <p className="text-xs text-red-400">{errors.apellido}</p>}
                        </div>

                        <div className="space-y-2">
                            <label className="block text-sm font-semibold text-slate-200">Nombres</label>
                            <input
                                type="text"
                                name="nombres"
                                value={form.nombres}
                                onChange={handleChange}
                                className="w-full rounded-lg border border-white/10 bg-slate-900 px-4 py-2 text-sm text-slate-50 focus:border-amber-300 focus:outline-none focus:ring-2 focus:ring-amber-300/30"
                                placeholder="Ej: Agustin"
                            />
                            {errors.nombres && <p className="text-xs text-red-400">{errors.nombres}</p>}
                        </div>

                        <div className="space-y-2">
                            <label className="block text-sm font-semibold text-slate-200">Tipo Doc</label>
                            <select
                                name="tipoDocumento"
                                value={form.tipoDocumento}
                                onChange={handleChange}
                                className="w-full rounded-lg border border-white/10 bg-slate-900 px-4 py-2 text-sm text-slate-50 focus:border-amber-300 focus:outline-none focus:ring-2 focus:ring-amber-300/30"
                            >
                                <option value="">Todos</option>
                                {Object.values(TipoDocumento).map((tipo) => (
                                    <option key={tipo} value={tipo}>
                                        {TIPO_DOCUMENTO_LABELS[tipo]}
                                    </option>
                                ))}
                            </select>
                        </div>

                        <div className="space-y-2">
                            <label className="block text-sm font-semibold text-slate-200">Nro Documento</label>
                            <input
                                type="text"
                                name="nroDocumento"
                                value={form.nroDocumento}
                                onChange={handleChange}
                                disabled={!form.tipoDocumento}
                                className="w-full rounded-lg border border-white/10 bg-slate-900 px-4 py-2 text-sm text-slate-50 focus:border-amber-300 focus:outline-none focus:ring-2 focus:ring-amber-300/30 disabled:opacity-50"
                            />
                            {errors.nroDocumento && <p className="text-xs text-red-400">{errors.nroDocumento}</p>}
                        </div>
                    </div>

                    <div className="mt-6 flex gap-3">
                        <button
                            type="submit"
                            disabled={isSearching}
                            className="rounded-full bg-amber-400 px-6 py-2 font-semibold text-slate-950 hover:bg-amber-300 disabled:opacity-50"
                        >
                            {isSearching ? "Buscando..." : "Buscar"}
                        </button>
                        <Link
                            href="/"
                            className="rounded-full border border-white/10 px-6 py-2 font-semibold text-slate-50 hover:bg-white/5"
                        >
                            Volver
                        </Link>
                    </div>
                </form>

                {/* --- RESULTADOS --- */}
                {resultados !== null && (
                    <section className="animate-in fade-in slide-in-from-bottom-4 duration-500">
                        <h2 className="mb-4 text-xl font-semibold">Resultados ({resultados.length})</h2>

                        {resultados.length === 0 ? (
                            <div className="rounded-xl border border-white/10 bg-white/5 p-8 text-center">
                                <p className="text-slate-300">No se encontraron huéspedes con esos criterios.</p>
                                <Link href="/cu/alta-huesped" className="mt-4 inline-block rounded-full bg-blue-600 px-5 py-2 text-sm font-medium text-white hover:bg-blue-500">
                                    + Dar de alta nuevo huésped
                                </Link>
                            </div>
                        ) : (
                            <div className="overflow-hidden rounded-xl border border-white/10 bg-slate-900/50">
                                <table className="w-full text-left text-sm text-slate-300">
                                    <thead className="bg-white/5 text-xs uppercase text-slate-400">
                                    <tr>
                                        <th className="px-6 py-3">Apellido y Nombres</th>
                                        <th className="px-6 py-3">Documento</th>
                                        <th className="px-6 py-3">Contacto</th>
                                        <th className="px-6 py-3 text-right">Acciones</th>
                                    </tr>
                                    </thead>
                                    <tbody className="divide-y divide-white/5">
                                    {resultados.map((h, i) => (
                                        <tr key={i} className="hover:bg-white/5">
                                            <td className="px-6 py-4 font-medium text-slate-100">
                                                {h.apellido}, {h.nombres}
                                            </td>
                                            <td className="px-6 py-4">
                          <span className="rounded bg-slate-800 px-2 py-1 text-xs font-bold text-amber-200">
                            {h.tipoDocumento}
                          </span>{" "}
                                                {h.nroDocumento}
                                            </td>
                                            <td className="px-6 py-4">
                                                {h.email?.[0] || "-"}
                                            </td>
                                            <td className="px-6 py-4 text-right">
                                                <button className="text-amber-300 hover:text-amber-200 hover:underline">
                                                    Seleccionar
                                                </button>
                                            </td>
                                        </tr>
                                    ))}
                                    </tbody>
                                </table>
                            </div>
                        )}
                    </section>
                )}
            </main>
        </div>
    );
}