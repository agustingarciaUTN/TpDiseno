import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Hotel, Search, Edit, UserPlus } from "lucide-react"

export default function Home() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50 dark:from-slate-950 dark:via-slate-900 dark:to-slate-950">
      <main className="mx-auto max-w-6xl px-4 py-12 sm:px-6 lg:px-8">
        <div className="mb-12 text-center">
          <div className="mx-auto mb-6 flex h-16 w-16 items-center justify-center rounded-2xl bg-gradient-to-br from-blue-600 to-indigo-600 text-white shadow-lg">
            <Hotel className="h-8 w-8" />
          </div>
          <h1 className="mb-3 text-4xl font-bold text-slate-900 dark:text-slate-50">Sistema de Hotelería</h1>
          <p className="text-lg text-slate-600 dark:text-slate-400">Gestión de huéspedes y datos personales</p>
        </div>

        <div className="grid gap-6 md:grid-cols-3">
          <Card className="group p-6 transition-all hover:shadow-xl">
            <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-blue-100 text-blue-600 transition-colors group-hover:bg-blue-600 group-hover:text-white dark:bg-blue-950 dark:text-blue-400">
              <Search className="h-6 w-6" />
            </div>
            <h3 className="mb-2 text-xl font-semibold text-slate-900 dark:text-slate-50">Buscar Huésped</h3>
            <p className="mb-4 text-sm text-slate-600 dark:text-slate-400">
              Buscar huéspedes existentes por apellido, nombre, tipo o número de documento.
            </p>
            <Button asChild className="w-full">
              <Link href="/buscar-huesped">Ir a búsqueda</Link>
            </Button>
          </Card>

          <Card className="group p-6 transition-all hover:shadow-xl">
            <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-green-100 text-green-600 transition-colors group-hover:bg-green-600 group-hover:text-white dark:bg-green-950 dark:text-green-400">
              <Edit className="h-6 w-6" />
            </div>
            <h3 className="mb-2 text-xl font-semibold text-slate-900 dark:text-slate-50">Modificar Huésped</h3>
            <p className="mb-4 text-sm text-slate-600 dark:text-slate-400">
              Actualizar los datos personales de un huésped existente.
            </p>
            <Button asChild variant="outline" className="w-full bg-transparent" disabled>
              <span>Seleccione un huésped primero</span>
            </Button>
          </Card>

          <Card className="group p-6 transition-all hover:shadow-xl">
            <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-violet-100 text-violet-600 transition-colors group-hover:bg-violet-600 group-hover:text-white dark:bg-violet-950 dark:text-violet-400">
              <UserPlus className="h-6 w-6" />
            </div>
            <h3 className="mb-2 text-xl font-semibold text-slate-900 dark:text-slate-50">Dar de Alta Huésped</h3>
            <p className="mb-4 text-sm text-slate-600 dark:text-slate-400">
              Registrar un nuevo huésped en el sistema hotelero.
            </p>
            <Button asChild variant="outline" className="w-full bg-transparent">
              <Link href="/dar-de-alta-huesped">Crear nuevo huésped</Link>
            </Button>
          </Card>
        </div>

        <Card className="mt-8 border-blue-200 bg-blue-50/50 p-6 dark:border-blue-900 dark:bg-blue-950/20">
          <h2 className="mb-3 font-semibold text-slate-900 dark:text-slate-50">Flujo de Trabajo</h2>
          <ol className="space-y-2 text-sm text-slate-700 dark:text-slate-300">
            <li className="flex gap-3">
              <span className="flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-blue-600 text-xs font-semibold text-white">
                1
              </span>
              <span>Buscar huésped por criterios (apellido, nombre, documento)</span>
            </li>
            <li className="flex gap-3">
              <span className="flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-blue-600 text-xs font-semibold text-white">
                2
              </span>
              <span>Si existe, seleccionarlo y modificar sus datos</span>
            </li>
            <li className="flex gap-3">
              <span className="flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-blue-600 text-xs font-semibold text-white">
                3
              </span>
              <span>Si no existe, dar de alta un nuevo huésped</span>
            </li>
          </ol>
        </Card>
      </main>
    </div>
  )
}
