"use client"

import Link from "next/link"
import { useRouter } from "next/navigation"
import { useState, type FormEvent, type ChangeEvent } from "react"
import { TipoDocumento, TIPO_DOCUMENTO_LABELS, VALIDATION, type BuscarHuespedForm, type DtoHuesped } from "@/lib/types"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Search, User, Users } from "lucide-react"

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
  const [resultados, setResultados] = useState<DtoHuesped[]>([])
  const [selectedHuespedId, setSelectedHuespedId] = useState<number | null>(null)

  const validateField = (name: keyof BuscarHuespedForm, value: string): string => {
    switch (name) {
      case "apellido":
      case "nombres":
        if (!value.trim()) return ""
        if (value.length !== 1) return "Debe ingresar solo la primera letra"
        if (!VALIDATION.REGEX_NOMBRE.test(value)) return "Solo puede contener letras"
        return ""

      case "tipoDocumento":
        return ""

      case "nroDocumento":
        if (!form.tipoDocumento) return ""
        if (!value.trim()) return ""
        if (value.length < 6 || value.length > 15) return "Debe tener entre 6 y 15 caracteres"
        if (!VALIDATION.REGEX_DOCUMENTO.test(value)) return "El documento no debe contener espacios ni símbolos"
        return ""

      default:
        return ""
    }
  }

  const handleChange = (e: ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    const fieldName = name as keyof BuscarHuespedForm

    setForm((prev) => ({ ...prev, [fieldName]: value }))

    const error = validateField(fieldName, value)
    setErrors((prev) => ({ ...prev, [fieldName]: error }))
  }

  const handleSelectChange = (value: string) => {
    setForm((prev) => ({ ...prev, tipoDocumento: value }))
    setErrors((prev) => ({ ...prev, tipoDocumento: "" }))
  }

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()

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
    setSearchPerformed(false)
    setSelectedHuespedId(null)

    try {
      // Construir query params
      const params = new URLSearchParams()
      if (form.apellido) params.append("apellido", form.apellido)
      if (form.nombres) params.append("nombres", form.nombres)
      if (form.tipoDocumento) params.append("tipoDocumento", form.tipoDocumento)
      if (form.nroDocumento) params.append("nroDocumento", form.nroDocumento)

      // Llamada al backend
      const response = await fetch(`/api/huespedes/buscar?${params.toString()}`)

      if (!response.ok) {
        throw new Error("Error en la búsqueda")
      }

      const data: DtoHuesped[] = await response.json()

      setResultados(data)
      setSearchPerformed(true)
    } catch (error) {
      console.error("Error en búsqueda:", error)
      alert("Error al buscar huésped. Verifica tu conexión.")
    } finally {
      setIsSearching(false)
    }
  }

  const handleSiguiente = () => {
    if (resultados.length === 0) {
      // No hay resultados -> Alta de huésped
      router.push("/dar-de-alta-huesped")
    } else if (selectedHuespedId === null) {
      // No se seleccionó ningún huésped -> Alta de huésped
      router.push("/dar-de-alta-huesped")
    } else {
      // Se seleccionó un huésped -> Modificar huésped
      router.push(`/modificar-huesped/${selectedHuespedId}`)
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50 dark:from-slate-950 dark:via-slate-900 dark:to-slate-950">
      <main className="mx-auto max-w-6xl px-4 py-8 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="mb-8 space-y-2">
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-blue-600 text-white">
              <Search className="h-5 w-5" />
            </div>
            <div>
              <p className="text-xs font-semibold uppercase tracking-wider text-blue-600 dark:text-blue-400">
                CU02 - Caso de Uso
              </p>
              <h1 className="text-3xl font-bold text-slate-900 dark:text-slate-50">Buscar Huésped</h1>
            </div>
          </div>
          <p className="text-slate-600 dark:text-slate-400">
            Gestionar datos personales de los huéspedes. Ingrese los criterios de búsqueda para encontrar un huésped
            existente.
          </p>
        </div>

        {/* Formulario de búsqueda */}
        <Card className="mb-6 p-6 shadow-lg">
          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <h2 className="mb-4 text-lg font-semibold text-slate-900 dark:text-slate-50">Criterios de Búsqueda</h2>
              <p className="mb-6 text-sm text-muted-foreground">
                Todos los campos son opcionales. Puede buscar por uno o varios criterios. Si no ingresa ningún dato, se
                listarán todos los huéspedes.
              </p>

              <div className="grid gap-5 md:grid-cols-2">
                {/* Apellido */}
                <div className="space-y-2">
                  <Label htmlFor="apellido">Apellido</Label>
                  <Input
                    type="text"
                    id="apellido"
                    name="apellido"
                    value={form.apellido}
                    onChange={handleChange}
                    placeholder="Ej: G (buscar por García)"
                    maxLength={50}
                    className={errors.apellido ? "border-destructive" : ""}
                  />
                  {errors.apellido && <p className="text-xs text-destructive">{errors.apellido}</p>}
                  <p className="text-xs text-muted-foreground">Ingrese la primera letra del apellido</p>
                </div>

                {/* Nombres */}
                <div className="space-y-2">
                  <Label htmlFor="nombres">Nombre(s)</Label>
                  <Input
                    type="text"
                    id="nombres"
                    name="nombres"
                    value={form.nombres}
                    onChange={handleChange}
                    placeholder="Ej: A (buscar por Agustín)"
                    maxLength={50}
                    className={errors.nombres ? "border-destructive" : ""}
                  />
                  {errors.nombres && <p className="text-xs text-destructive">{errors.nombres}</p>}
                  <p className="text-xs text-muted-foreground">Ingrese la primera letra del nombre</p>
                </div>

                {/* Tipo de Documento */}
                <div className="space-y-2">
                  <Label htmlFor="tipoDocumento">Tipo de Documento</Label>
                  <Select value={form.tipoDocumento} onValueChange={handleSelectChange}>
                    <SelectTrigger className={errors.tipoDocumento ? "border-destructive" : ""}>
                      <SelectValue placeholder="Seleccionar..." />
                    </SelectTrigger>
                    <SelectContent>
                      {Object.values(TipoDocumento).map((tipo) => (
                        <SelectItem key={tipo} value={tipo}>
                          {TIPO_DOCUMENTO_LABELS[tipo]}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  {errors.tipoDocumento && <p className="text-xs text-destructive">{errors.tipoDocumento}</p>}
                </div>

                {/* Número de Documento */}
                <div className="space-y-2">
                  <Label htmlFor="nroDocumento">Número de Documento</Label>
                  <Input
                    type="text"
                    id="nroDocumento"
                    name="nroDocumento"
                    value={form.nroDocumento}
                    onChange={handleChange}
                    disabled={!form.tipoDocumento}
                    placeholder="Alfanumérico sin espacios"
                    maxLength={15}
                    className={errors.nroDocumento ? "border-destructive" : ""}
                  />
                  {errors.nroDocumento && <p className="text-xs text-destructive">{errors.nroDocumento}</p>}
                  <p className="text-xs text-muted-foreground">
                    {!form.tipoDocumento
                      ? "Seleccione un tipo de documento primero"
                      : "Alfanumérico (letras y números) sin espacios"}
                  </p>
                </div>
              </div>
            </div>

            <div className="flex flex-wrap gap-3">
              <Button type="submit" disabled={isSearching} size="lg">
                <Search className="mr-2 h-4 w-4" />
                {isSearching ? "Buscando..." : "Buscar"}
              </Button>
              <Button type="button" variant="outline" size="lg" asChild>
                <Link href="/">Cancelar</Link>
              </Button>
            </div>
          </form>
        </Card>

        {/* Resultados de búsqueda */}
        {searchPerformed && (
          <Card className="mb-6 p-6 shadow-lg">
            <div className="mb-4 flex items-center justify-between">
              <div className="flex items-center gap-2">
                <Users className="h-5 w-5 text-blue-600 dark:text-blue-400" />
                <h2 className="text-lg font-semibold text-slate-900 dark:text-slate-50">Resultados de Búsqueda</h2>
              </div>
              <span className="text-sm text-muted-foreground">
                {resultados.length} {resultados.length === 1 ? "huésped encontrado" : "huéspedes encontrados"}
              </span>
            </div>

            {resultados.length === 0 ? (
              <div className="rounded-lg border border-dashed border-muted-foreground/25 bg-muted/20 p-8 text-center">
                <User className="mx-auto mb-3 h-12 w-12 text-muted-foreground/40" />
                <p className="mb-2 font-medium text-slate-900 dark:text-slate-50">No se encontraron huéspedes</p>
                <p className="text-sm text-muted-foreground">
                  No hay coincidencias con los criterios de búsqueda ingresados. Puede dar de alta un nuevo huésped.
                </p>
              </div>
            ) : (
              <div className="space-y-2">
                {resultados.map((huesped) => (
                  <button
                    key={huesped.id}
                    onClick={() => setSelectedHuespedId(huesped.id)}
                    className={`w-full rounded-lg border p-4 text-left transition-all hover:shadow-md ${
                      selectedHuespedId === huesped.id
                        ? "border-blue-600 bg-blue-50 shadow-md dark:border-blue-500 dark:bg-blue-950/30"
                        : "border-border bg-card hover:border-blue-300 dark:hover:border-blue-700"
                    }`}
                  >
                    <div className="flex items-start justify-between">
                      <div className="space-y-1">
                        <div className="flex items-center gap-2">
                          <User className="h-4 w-4 text-muted-foreground" />
                          <p className="font-semibold text-slate-900 dark:text-slate-50">
                            {huesped.apellido}, {huesped.nombres}
                          </p>
                        </div>
                        <div className="flex gap-4 text-sm text-muted-foreground">
                          <span>
                            {TIPO_DOCUMENTO_LABELS[huesped.tipoDocumento]}: {huesped.nroDocumento}
                          </span>
                          {huesped.telefono && <span>Tel: {huesped.telefono}</span>}
                        </div>
                        {huesped.email && <p className="text-sm text-muted-foreground">{huesped.email}</p>}
                      </div>
                      {selectedHuespedId === huesped.id && (
                        <div className="flex h-6 w-6 items-center justify-center rounded-full bg-blue-600 text-white">
                          <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                          </svg>
                        </div>
                      )}
                    </div>
                  </button>
                ))}
              </div>
            )}

            <div className="mt-6 flex gap-3">
              <Button onClick={handleSiguiente} size="lg">
                Siguiente
              </Button>
              {resultados.length === 0 && (
                <p className="flex items-center text-sm text-muted-foreground">Se continuará con el alta de huésped</p>
              )}
              {resultados.length > 0 && selectedHuespedId === null && (
                <p className="flex items-center text-sm text-amber-600 dark:text-amber-500">
                  Si no selecciona un huésped, se continuará con el alta
                </p>
              )}
            </div>
          </Card>
        )}

        {/* Información del flujo */}
        <Card className="border-blue-200 bg-blue-50/50 p-5 dark:border-blue-900 dark:bg-blue-950/20">
          <h3 className="mb-3 font-semibold text-slate-900 dark:text-slate-50">Flujo del Caso de Uso</h3>
          <ul className="space-y-2 text-sm text-slate-700 dark:text-slate-300">
            <li className="flex gap-2">
              <span className="text-blue-600 dark:text-blue-400">1.</span>
              <span>Ingrese criterios de búsqueda (todos opcionales)</span>
            </li>
            <li className="flex gap-2">
              <span className="text-blue-600 dark:text-blue-400">2.</span>
              <span>El sistema presentará la lista de huéspedes que coincidan</span>
            </li>
            <li className="flex gap-2">
              <span className="text-blue-600 dark:text-blue-400">3.</span>
              <span>Seleccione un huésped y presione SIGUIENTE para modificar sus datos</span>
            </li>
            <li className="flex gap-2">
              <span className="text-blue-600 dark:text-blue-400">4.</span>
              <span>Si no hay resultados o no selecciona ninguno, continuará con el alta de huésped</span>
            </li>
          </ul>
        </Card>
      </main>
    </div>
  )
}
