"use client"

import Link from "next/link"
import { use } from "react"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Edit, ArrowLeft } from "lucide-react"

export default function ModificarHuesped({
  params,
}: {
  params: Promise<{ id: string }>
}) {
  const { id } = use(params)

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-green-50 to-emerald-50 dark:from-slate-950 dark:via-slate-900 dark:to-slate-950">
      <main className="mx-auto max-w-4xl px-4 py-8 sm:px-6 lg:px-8">
        <div className="mb-8 space-y-2">
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-green-600 text-white">
              <Edit className="h-5 w-5" />
            </div>
            <div>
              <p className="text-xs font-semibold uppercase tracking-wider text-green-600 dark:text-green-400">
                CU10 - Caso de Uso
              </p>
              <h1 className="text-3xl font-bold text-slate-900 dark:text-slate-50">Modificar Huésped</h1>
            </div>
          </div>
          <p className="text-slate-600 dark:text-slate-400">
            Actualizar los datos personales del huésped seleccionado.
          </p>
        </div>

        <Card className="p-8 shadow-lg">
          <div className="text-center">
            <div className="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-green-100 dark:bg-green-950">
              <Edit className="h-8 w-8 text-green-600 dark:text-green-400" />
            </div>
            <h2 className="mb-2 text-2xl font-bold text-slate-900 dark:text-slate-50">
              Placeholder - Modificar Huésped
            </h2>
            <p className="mb-1 text-muted-foreground">
              Esta es la página para modificar el huésped con ID: <strong>{id}</strong>
            </p>
            <p className="mb-6 text-sm text-muted-foreground">
              Aquí se implementará el formulario de edición de datos del huésped.
            </p>

            <div className="space-y-3">
              <div className="rounded-lg border border-dashed border-green-300 bg-green-50 p-4 dark:border-green-800 dark:bg-green-950/20">
                <p className="text-sm font-medium text-green-800 dark:text-green-300">
                  📋 Funcionalidad a implementar:
                </p>
                <ul className="mt-2 space-y-1 text-left text-sm text-green-700 dark:text-green-400">
                  <li>• Cargar datos actuales del huésped</li>
                  <li>• Formulario de edición de datos personales</li>
                  <li>• Validación de campos</li>
                  <li>• Guardar cambios en la base de datos</li>
                </ul>
              </div>

              <Button asChild size="lg" className="w-full sm:w-auto">
                <Link href="/buscar-huesped">
                  <ArrowLeft className="mr-2 h-4 w-4" />
                  Volver a la búsqueda
                </Link>
              </Button>
            </div>
          </div>
        </Card>
      </main>
    </div>
  )
}
