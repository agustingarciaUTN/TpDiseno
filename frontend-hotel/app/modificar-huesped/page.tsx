"use client"

import Link from "next/link"
import { useSearchParams } from "next/navigation"
import { Card } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Edit, ArrowLeft } from "lucide-react"

export default function ModificarHuesped() {
  const searchParams = useSearchParams()
  const idHuesped = searchParams.get("id")

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50 dark:from-slate-950 dark:via-slate-900 dark:to-slate-950">
      <main className="mx-auto max-w-4xl px-4 py-12 sm:px-6 lg:px-8">
        <div className="mb-8 space-y-2">
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-green-600 text-white">
              <Edit className="h-5 w-5" />
            </div>
            <div>
              <p className="text-xs font-semibold uppercase tracking-wider text-green-600 dark:text-green-400">
                Caso de Uso
              </p>
              <h1 className="text-3xl font-bold text-slate-900 dark:text-slate-50">Modificar Huésped</h1>
            </div>
          </div>
          <p className="text-slate-600 dark:text-slate-400">Actualizar los datos personales de un huésped existente</p>
        </div>

        <Card className="p-12 text-center shadow-lg">
          <div className="mx-auto mb-6 flex h-24 w-24 items-center justify-center rounded-full bg-green-100 dark:bg-green-950">
            <Edit className="h-12 w-12 text-green-600 dark:text-green-400" />
          </div>
          <h2 className="mb-3 text-2xl font-bold text-slate-900 dark:text-slate-50">Funcionalidad en Desarrollo</h2>
          {idHuesped ? (
            <p className="mb-8 text-slate-600 dark:text-slate-400">
              Esta página permitirá modificar los datos del huésped con ID: <strong>{idHuesped}</strong>
            </p>
          ) : (
            <p className="mb-8 text-slate-600 dark:text-slate-400">
              Esta página permitirá modificar los datos de un huésped previamente seleccionado desde la búsqueda.
            </p>
          )}
          <div className="space-y-3">
            <p className="text-sm text-muted-foreground">
              Para llegar aquí, primero debe buscar y seleccionar un huésped.
            </p>
            <Button asChild size="lg">
              <Link href="/cu/buscar-huesped">
                <ArrowLeft className="mr-2 h-4 w-4" />
                Ir a Búsqueda
              </Link>
            </Button>
          </div>
        </Card>

        <Card className="mt-6 border-blue-200 bg-blue-50/50 p-6 dark:border-blue-900 dark:bg-blue-950/20">
          <h3 className="mb-3 font-semibold text-slate-900 dark:text-slate-50">Flujo Esperado</h3>
          <ol className="space-y-2 text-sm text-slate-700 dark:text-slate-300">
            <li className="flex gap-3">
              <span className="flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-blue-600 text-xs font-semibold text-white">
                1
              </span>
              <span>Buscar huésped por criterios</span>
            </li>
            <li className="flex gap-3">
              <span className="flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-blue-600 text-xs font-semibold text-white">
                2
              </span>
              <span>Seleccionar un huésped de los resultados</span>
            </li>
            <li className="flex gap-3">
              <span className="flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-blue-600 text-xs font-semibold text-white">
                3
              </span>
              <span>Presionar "Siguiente" para acceder a esta página</span>
            </li>
            <li className="flex gap-3">
              <span className="flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-blue-600 text-xs font-semibold text-white">
                4
              </span>
              <span>Modificar los datos del huésped seleccionado</span>
            </li>
          </ol>
        </Card>
      </main>
    </div>
  )
}
