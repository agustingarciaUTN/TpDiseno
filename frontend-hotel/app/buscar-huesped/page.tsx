"use client"

import Link from "next/link"
import { useState, type FormEvent, type ChangeEvent } from "react"
import { useRouter } from "next/navigation"
import { apiFetch } from "@/lib/api"
import { TipoDocumento, TIPO_DOCUMENTO_LABELS, VALIDATION, type BuscarHuespedForm, type DtoHuesped } from "@/lib/types"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Search, UserPlus, Edit, Home, CheckCircle } from "lucide-react"

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
  const [searchPerformed, setSearchPerformed] = useState(false)

  const [resultados, setResultados] = useState<DtoHuesped[] | null>(null)
  const [huespedSeleccionado, setHuespedSeleccionado] = useState<number | null>(null)

  const validateField = (name: keyof BuscarHuespedForm, value: string): string => {
    switch (name) {
      case "apellido":
      case "nombres":
        if (!value.trim()) return ""
        if (!VALIDATION.REGEX_NOMBRE.test(value)) return "Solo puede contener letras"
        return ""

      case "nroDocumento":
        if (!form.tipoDocumento && value) return "Seleccione tipo primero"
        if (!value.trim()) return ""
        if (!VALIDATION.REGEX_DOCUMENTO.test(value)) return "El documento no debe contener espacios ni símbolos"
        return ""

      default:
        return ""
    }
  }

  const handleChange = (e: ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setForm((prev) => ({ ...prev, [name]: value }))
    const error = validateField(name as keyof BuscarHuespedForm, value)
    setErrors((prev) => ({ ...prev, [name]: error }))
  }

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setResultados(null)
    setHuespedSeleccionado(null)
    setSearchPerformed(false)

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
      setSearchPerformed(true)
    } catch (error: any) {
      console.error("Error en búsqueda:", error)
      alert("Error al buscar: " + error.message)
    } finally {
      setIsSearching(false)
    }
  }

  const handleDarDeAlta = () => {
    router.push("/dar-de-alta-huesped")
  }

  const handleSeleccionarHuesped = (id: number) => {
    if (huespedSeleccionado === id) {
      setHuespedSeleccionado(null)
    } else {
      setHuespedSeleccionado(id)
    }
  }

  const handleEditar = () => {
    if (huespedSeleccionado) {
      // CU10 - Modificar huésped (en progreso)
      alert("🚧 Funcionalidad de Edición (CU10) en progreso...")
    }
  }

  const handleCancelarSeleccion = () => {
    setHuespedSeleccionado(null)
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50 dark:from-slate-950 dark:via-slate-900 dark:to-slate-950">
      <main className="mx-auto max-w-6xl px-4 py-12 sm:px-6 lg:px-8">
        <div className="mb-8">
          <div className="mb-6 flex items-center gap-4">
            <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-gradient-to-br from-blue-600 to-indigo-600 text-white shadow-lg">
              <Search className="h-6 w-6" />
            </div>
            <div>
              <p className="text-xs font-semibold uppercase tracking-wider text-blue-600 dark:text-blue-400">CU02</p>
              <h1 className="text-3xl font-bold text-slate-900 dark:text-slate-50">Buscar Huésped</h1>
            </div>
          </div>
          <p className="text-slate-600 dark:text-slate-400">
            Gestionar datos personales de los huéspedes del sistema hotelero.
          </p>
        </div>

        <Card className="p-6">
          <h2 className="mb-4 text-lg font-semibold text-slate-900 dark:text-slate-50">Criterios de Búsqueda</h2>
          <form onSubmit={handleSubmit}>
            <div className="grid gap-4 md:grid-cols-2">
              <div className="space-y-2">
                <Label htmlFor="apellido">Apellido</Label>
                <Input
                  id="apellido"
                  name="apellido"
                  value={form.apellido}
                  onChange={handleChange}
                  placeholder="Ej: Garcia"
                />
                {errors.apellido && <p className="text-xs text-red-500">{errors.apellido}</p>}
              </div>

              <div className="space-y-2">
                <Label htmlFor="nombres">Nombres</Label>
                <Input
                  id="nombres"
                  name="nombres"
                  value={form.nombres}
                  onChange={handleChange}
                  placeholder="Ej: Agustin"
                />
                {errors.nombres && <p className="text-xs text-red-500">{errors.nombres}</p>}
              </div>

              <div className="space-y-2">
                <Label htmlFor="tipoDocumento">Tipo de Documento</Label>
                <select
                  id="tipoDocumento"
                  name="tipoDocumento"
                  value={form.tipoDocumento}
                  onChange={handleChange}
                  className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
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
                <Label htmlFor="nroDocumento">Nro Documento</Label>
                <Input
                  id="nroDocumento"
                  name="nroDocumento"
                  value={form.nroDocumento}
                  onChange={handleChange}
                  disabled={!form.tipoDocumento}
                  placeholder={form.tipoDocumento ? "Ingrese el número" : "Seleccione tipo primero"}
                />
                {errors.nroDocumento && <p className="text-xs text-red-500">{errors.nroDocumento}</p>}
              </div>
            </div>

            <div className="mt-6 flex gap-3">
              <Button type="submit" disabled={isSearching} className="gap-2">
                <Search className="h-4 w-4" />
                {isSearching ? "Buscando..." : "Buscar"}
              </Button>
              <Button type="button" variant="outline" asChild>
                <Link href="/">
                  <Home className="mr-2 h-4 w-4" />
                  Volver al Inicio
                </Link>
              </Button>
            </div>
          </form>
        </Card>

        {searchPerformed && resultados !== null && (
          <Card className="mt-6 animate-in fade-in slide-in-from-bottom-4 duration-500">
            <div className="border-b p-6">
              <h2 className="text-xl font-semibold text-slate-900 dark:text-slate-50">
                Resultados ({resultados.length})
              </h2>
            </div>

            {resultados.length === 0 ? (
              <div className="p-8 text-center space-y-4">
                <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-amber-100 text-amber-600 dark:bg-amber-950 dark:text-amber-400">
                  <Search className="h-8 w-8" />
                </div>
                <div>
                  <p className="text-lg font-medium text-slate-900 dark:text-slate-50">
                    ⚠️ No se encontraron huéspedes
                  </p>
                  <p className="text-sm text-slate-600 dark:text-slate-400 mt-1">
                    No hay coincidencias con los criterios ingresados
                  </p>
                </div>
                <div className="border-t pt-6 mt-6">
                  <p className="text-sm text-slate-600 dark:text-slate-400 mb-4">
                    ¿Desea dar de alta un nuevo huésped?
                  </p>
                  <div className="flex gap-3 justify-center">
                    <Button onClick={handleDarDeAlta} className="gap-2">
                      <UserPlus className="h-4 w-4" />
                      Dar de Alta
                    </Button>
                    <Button
                      variant="outline"
                      onClick={() => {
                        setSearchPerformed(false)
                        setResultados(null)
                      }}
                    >
                      Nueva Búsqueda
                    </Button>
                  </div>
                </div>
              </div>
            ) : (
              <div className="p-6 space-y-4">
                <div className="space-y-3">
                  {resultados.map((h, index) => (
                    <Card
                      key={h.idHuesped}
                      onClick={() => handleSeleccionarHuesped(h.idHuesped)}
                      className={`
                        p-4 cursor-pointer transition-all
                        ${
                          huespedSeleccionado === h.idHuesped
                            ? "border-blue-600 bg-blue-50 dark:bg-blue-950/20"
                            : "hover:border-blue-300 dark:hover:border-blue-700"
                        }
                      `}
                    >
                      <div className="flex items-center justify-between">
                        <div className="flex items-center gap-4">
                          <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-slate-100 dark:bg-slate-800">
                            <span className="text-sm font-semibold text-slate-600 dark:text-slate-400">
                              {index + 1}
                            </span>
                          </div>
                          <div>
                            <p className="font-semibold text-slate-900 dark:text-slate-50">
                              {h.apellido}, {h.nombres}
                            </p>
                            <p className="text-sm text-slate-600 dark:text-slate-400">
                              <span className="inline-flex items-center rounded-md bg-blue-100 px-2 py-1 text-xs font-medium text-blue-700 dark:bg-blue-950 dark:text-blue-300">
                                {h.tipoDocumento}
                              </span>{" "}
                              {h.nroDocumento}
                            </p>
                            {h.email?.[0] && (
                              <p className="text-sm text-slate-600 dark:text-slate-400 mt-1">
                                📧 {h.email[0]}
                              </p>
                            )}
                          </div>
                        </div>
                        {huespedSeleccionado === h.idHuesped && (
                          <CheckCircle className="h-6 w-6 text-blue-600 dark:text-blue-400" />
                        )}
                      </div>
                    </Card>
                  ))}
                </div>

                <Card className="border-blue-200 bg-blue-50/50 p-4 dark:border-blue-900 dark:bg-blue-950/20">
                  <p className="text-sm text-slate-700 dark:text-slate-300 text-center">
                    {huespedSeleccionado
                      ? "✓ Huésped seleccionado. Puede editarlo o cancelar la selección."
                      : "Seleccione un huésped de la lista para editar sus datos."}
                  </p>
                </Card>

                <div className="flex gap-3 pt-2">
                  {huespedSeleccionado ? (
                    <>
                      <Button onClick={handleEditar} className="flex-1 gap-2">
                        <Edit className="h-4 w-4" />
                        Editar Huésped
                      </Button>
                      <Button variant="outline" onClick={handleCancelarSeleccion}>
                        Cancelar
                      </Button>
                    </>
                  ) : (
                    <Button
                      variant="outline"
                      className="flex-1"
                      onClick={() => {
                        setSearchPerformed(false)
                        setResultados(null)
                      }}
                    >
                      Nueva Búsqueda
                    </Button>
                  )}
                </div>
              </div>
            )}
          </Card>
        )}
      </main>
    </div>
  )
}
