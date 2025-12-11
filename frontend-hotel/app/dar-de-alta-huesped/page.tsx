"use client"

import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { UserPlus, ArrowLeft } from "lucide-react"

export default function DarDeAltaHuesped() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-purple-50 to-violet-50 dark:from-slate-950 dark:via-slate-900 dark:to-slate-950">
      <main className="mx-auto max-w-4xl px-4 py-8 sm:px-6 lg:px-8">
        <div className="mb-8 space-y-2">
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-violet-600 text-white">
              <UserPlus className="h-5 w-5" />
            </div>
            <div>
              <p className="text-xs font-semibold uppercase tracking-wider text-violet-600 dark:text-violet-400">
                CU11 - Caso de Uso
              </p>
              <h1 className="text-3xl font-bold text-slate-900 dark:text-slate-50">Dar de Alta Huésped</h1>
            </div>
          </div>
          <p className="text-slate-600 dark:text-slate-400">Registrar un nuevo huésped en el sistema.</p>
        </div>

        <Card className="p-8 shadow-lg">
          <div className="text-center">
            <div className="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-violet-100 dark:bg-violet-950">
              <UserPlus className="h-8 w-8 text-violet-600 dark:text-violet-400" />
            </div>
            <h2 className="mb-2 text-2xl font-bold text-slate-900 dark:text-slate-50">
              Placeholder - Dar de Alta Huésped
            </h2>
            <p className="mb-1 text-muted-foreground">
              Esta es la página para registrar un nuevo huésped en el sistema.
            </p>
            <p className="mb-6 text-sm text-muted-foreground">
              Aquí se implementará el formulario de registro de huésped.
            </p>

            <div className="space-y-3">
              <div className="rounded-lg border border-dashed border-violet-300 bg-violet-50 p-4 dark:border-violet-800 dark:bg-violet-950/20">
                <p className="text-sm font-medium text-violet-800 dark:text-violet-300">
                  📋 Funcionalidad a implementar:
                </p>
                <ul className="mt-2 space-y-1 text-left text-sm text-violet-700 dark:text-violet-400">
                  <li>• Formulario de datos personales del huésped</li>
                  <li>• Validación de documento único</li>
                  <li>• Campos requeridos y opcionales</li>
                  <li>• Guardar nuevo huésped en la base de datos</li>
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
