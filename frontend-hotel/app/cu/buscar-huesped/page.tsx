"use client"

import Link from "next/link"
import { useState, type FormEvent, type ChangeEvent } from "react"
import { useRouter } from "next/navigation"
import { apiFetch } from "@/lib/api"
import { validateField, validateSearchForm } from "@/lib/validators"
import { TipoDocumento, TIPO_DOCUMENTO_LABELS, type BuscarHuespedForm, type DtoHuesped } from "@/lib/types"

const INITIAL_FORM: BuscarHuespedForm = {
  apellido: "",
  nombres: "",
  tipoDocumento: "",
  nroDocumento: "",
}

export default function BuscarHuesped() {
  const router = useRouter()
  const [form, setForm] = useState<BuscarHuespedForm>(INITIAL_FORM)
  const [errors, setErrors] = useState<Partial<Record<keyof BuscarHuespedForm, string>>>({})
  const [isSearching, setIsSearching] = useState(false)

  const [resultados, setResultados] = useState<DtoHuesped[] | null>(null)
  const [huespedSeleccionado, setHuespedSeleccionado] = useState<number | null>(null)

  const handleChange = (e: ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setForm((prev) => ({ ...prev, [name]: value }))
    const error = validateField(name, value)
    setErrors((prev) => ({ ...prev, [name]: error || "" }))
  }

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setResultados(null)
    setHuespedSeleccionado(null)

    if (!validateSearchForm(form.apellido, form.nombres, form.nroDocumento)) {
      alert("Debe ingresar al menos un criterio de búsqueda")
      return
    }

    const newErrors: Partial<Record<keyof BuscarHuespedForm, string>> = {}
    ;(Object.keys(form) as Array<keyof BuscarHuespedForm>).forEach((key) => {
      const error = validateField(key, String(form[key]))
      if (error) newErrors[key] = error
    })

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors)
      return
    }

    setIsSearching(true)
    try {
      const data = await apiFetch<DtoHuesped[]>("/huespedes/buscar", {
        method: "POST",
        body: {
          apellido: form.apellido || null,
          nombres: form.nombres || null,
          tipoDocumento: form.tipoDocumento || null,
          nroDocumento: form.nroDocumento || null,
        },
      })

      setResultados(data)
    } catch (error: any) {
      console.error("Error en búsqueda:", error)
      alert("Error al buscar: " + error.message)
    } finally {
      setIsSearching(false)
    }
  }

  const handleSiguiente = () => {
    if (resultados === null || resultados.length === 0 || huespedSeleccionado === null) {
      // 5.A - No hay resultados o no hay selección -> Dar de alta
      router.push("/cu/alta-huesped")
    } else {
      // 5 - Hay selección -> Modificar huésped
      router.push(`/cu/modificar-huesped?id=${huespedSeleccionado}`)
    }
  }

  return (
    <div className="min-h-screen bg-slate-950 text-slate-50">
      <main className="mx-auto flex max-w-4xl flex-col gap-6 px-6 py-12">
        <header className="space-y-3">
          <p className="text-xs uppercase tracking-[0.25em] text-amber-200/80">CU02</p>
          <h1 className="text-3xl font-semibold">Buscar huésped</h1>
          <p className="text-sm text-slate-200/80">Gestionar datos personales de los huéspedes.</p>
        </header>

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

        {resultados !== null && (
          <section className="animate-in fade-in slide-in-from-bottom-4 duration-500 space-y-4">
            <h2 className="text-xl font-semibold">Resultados ({resultados.length})</h2>

            {resultados.length === 0 ? (
              <div className="rounded-xl border border-white/10 bg-white/5 p-8 text-center">
                <p className="text-slate-300">No se encontraron huéspedes con esos criterios.</p>
                <p className="text-sm text-slate-400 mt-2">Pulse SIGUIENTE para dar de alta un nuevo huésped</p>
              </div>
            ) : (
              <div className="space-y-3">
                {resultados.map((h) => (
                  <div
                    key={h.idHuesped}
                    onClick={() => setHuespedSeleccionado(h.idHuesped)}
                    className={`
                                            rounded-xl border p-4 cursor-pointer transition-all
                                            ${
                                              huespedSeleccionado === h.idHuesped
                                                ? "border-amber-400 bg-amber-400/10"
                                                : "border-white/10 bg-slate-900/50 hover:border-amber-400/50 hover:bg-slate-900"
                                            }
                                        `}
                  >
                    <div className="flex items-center justify-between">
                      <div>
                        <p className="font-medium text-lg text-slate-100">
                          {h.apellido}, {h.nombres}
                        </p>
                        <p className="text-sm text-slate-400">
                          <span className="rounded bg-slate-800 px-2 py-1 text-xs font-bold text-amber-200">
                            {h.tipoDocumento}
                          </span>{" "}
                          {h.nroDocumento}
                        </p>
                        {h.email?.[0] && <p className="text-sm text-slate-400 mt-1">{h.email[0]}</p>}
                      </div>
                      {huespedSeleccionado === h.idHuesped && <div className="text-amber-400 text-xl">✓</div>}
                    </div>
                  </div>
                ))}
                {!huespedSeleccionado && (
                  <p className="text-xs text-amber-300/80 text-center">
                    Si no selecciona ningún huésped, se iniciará el proceso de alta
                  </p>
                )}
              </div>
            )}

            <div className="pt-4">
              <button
                onClick={handleSiguiente}
                className="w-full rounded-full bg-amber-400 px-6 py-3 text-lg font-bold text-slate-950 hover:bg-amber-300 transition-all"
              >
                SIGUIENTE
              </button>
            </div>
          </section>
        )}
      </main>
    </div>
  )
}
