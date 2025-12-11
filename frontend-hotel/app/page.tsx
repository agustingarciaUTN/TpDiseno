import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import {
    Hotel,
    Search,
    Edit,
    UserPlus,
    CalendarCheck,
    DoorOpen,
    XCircle,
    FileText,
    UserCog,
    UserCheck, UserMinus
} from "lucide-react"

export default function Home() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50 dark:from-slate-950 dark:via-slate-900 dark:to-slate-950">
      <main className="mx-auto max-w-6xl px-4 py-12 sm:px-6 lg:px-8">
        <div className="mb-12 text-center">
          <div className="mx-auto mb-6 flex h-16 w-16 items-center justify-center rounded-2xl bg-gradient-to-br from-blue-600 to-indigo-600 text-white shadow-lg">
            <Hotel className="h-8 w-8" />
          </div>
          <h1 className="mb-3 text-4xl font-bold text-slate-900 dark:text-slate-50">Sistema de Hotelería</h1>
          <p className="text-lg text-slate-600 dark:text-slate-400">Gestión completa de huéspedes, reservas y habitaciones</p>
        </div>

        <h2 className="mb-4 text-xl font-semibold text-slate-900 dark:text-slate-50">Gestión de Huéspedes</h2>
        <div className="grid gap-6 md:grid-cols-3 mb-8">
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
              <Link href="/alta-huesped">Crear nuevo huésped</Link>
            </Button>
          </Card>
            <Card className="group p-6 transition-all hover:shadow-xl">
                <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-rose-100 text-rose-600 transition-colors group-hover:bg-rose-600 group-hover:text-white dark:bg-rose-950 dark:text-rose-400">
                    <UserMinus className="h-6 w-6" />
                </div>
                <h3 className="mb-2 text-xl font-semibold text-slate-900 dark:text-slate-50">Dar de Baja Huésped</h3>
                <p className="mb-4 text-sm text-slate-600 dark:text-slate-400">
                    Eliminar un huésped del sistema permanentemente.
                </p>
                <Button asChild className="w-full">
                    <Link href="/baja-huesped">Eliminar huésped</Link>
                </Button>
            </Card>
        </div>

        <h2 className="mb-4 text-xl font-semibold text-slate-900 dark:text-slate-50">Gestión de Habitaciones</h2>
        <div className="grid gap-6 md:grid-cols-2 mb-8">
          <Card className="group p-6 transition-all hover:shadow-xl">
            <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-indigo-100 text-indigo-600 transition-colors group-hover:bg-indigo-600 group-hover:text-white dark:bg-indigo-950 dark:text-indigo-400">
              <CalendarCheck className="h-6 w-6" />
            </div>
            <h3 className="mb-2 text-xl font-semibold text-slate-900 dark:text-slate-50">Reservar Habitación</h3>
            <p className="mb-4 text-sm text-slate-600 dark:text-slate-400">
              Registrar una nueva reserva de habitación para un huésped responsable.
            </p>
            <Button asChild variant="outline" className="w-full bg-transparent">
              <Link href="/reservar-habitacion">Crear reserva</Link>
            </Button>
          </Card>

          <Card className="group p-6 transition-all hover:shadow-xl">
            <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-orange-100 text-orange-600 transition-colors group-hover:bg-orange-600 group-hover:text-white dark:bg-orange-950 dark:text-orange-400">
              <DoorOpen className="h-6 w-6" />
            </div>
            <h3 className="mb-2 text-xl font-semibold text-slate-900 dark:text-slate-50">Ocupar Habitación</h3>
            <p className="mb-4 text-sm text-slate-600 dark:text-slate-400">
              Realizar el check-in de huéspedes en una habitación disponible o reservada.
            </p>
            <Button asChild variant="outline" className="w-full bg-transparent">
              <Link href="/ocupar-habitacion">Check-In</Link>
            </Button>
          </Card>
        </div>
          <h2 className="mb-4 text-xl font-semibold text-slate-900 dark:text-slate-50">Gestión de Pago</h2>
          <div className="grid gap-6 md:grid-cols-2 mb-8">

              <Card className="group p-6 transition-all hover:shadow-xl">
                  <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-amber-100 text-amber-600 transition-colors group-hover:bg-amber-600 group-hover:text-white dark:bg-amber-950 dark:text-amber-400">
                      <FileText className="h-6 w-6" />
                  </div>
                  <h3 className="mb-2 text-xl font-semibold text-slate-900 dark:text-slate-50">Facturar</h3>
                  <p className="mb-4 text-sm text-slate-600 dark:text-slate-400">
                      Generar facturas para una habitación en el momento del check out.
                  </p>
                  <Button asChild className="w-full">
                      <Link href="/facturar">Generar factura</Link>
                  </Button>
              </Card>
              <Card className="group p-6 transition-all hover:shadow-xl">
                  <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-emerald-100 text-emerald-600 transition-colors group-hover:bg-emerald-600 group-hover:text-white dark:bg-emerald-950 dark:text-emerald-400">
                      <UserCheck className="h-6 w-6" />
                  </div>
                  <h3 className="mb-2 text-xl font-semibold text-slate-900 dark:text-slate-50">Alta Responsable de Pago</h3>
                  <p className="mb-4 text-sm text-slate-600 dark:text-slate-400">
                      Cargar datos personales de nuevos responsables de pago.
                  </p>
                  <Button asChild className="w-full">
                      <Link href="/alta-responsable-pago">Registrar responsable</Link>
                  </Button>
              </Card>

              <Card className="group p-6 transition-all hover:shadow-xl">
                  <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-purple-100 text-purple-600 transition-colors group-hover:bg-purple-600 group-hover:text-white dark:bg-purple-950 dark:text-purple-400">
                      <UserCog className="h-6 w-6" />
                  </div>
                  <h3 className="mb-2 text-xl font-semibold text-slate-900 dark:text-slate-50">
                      Modificar Responsable de Pago
                  </h3>
                  <p className="mb-4 text-sm text-slate-600 dark:text-slate-400">
                      Modificar datos personales de los responsables de pago.
                  </p>
                  <Button asChild className="w-full">
                      <Link href="/modificar-responsable-pago">Modificar responsable</Link>
                  </Button>
              </Card>
              <Card className="group p-6 transition-all hover:shadow-xl">
                  <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-purple-100 text-purple-600 transition-colors group-hover:bg-purple-600 group-hover:text-white dark:bg-purple-950 dark:text-purple-400">
                      <UserCog className="h-6 w-6" />
                  </div>
                  <h3 className="mb-2 text-xl font-semibold text-slate-900 dark:text-slate-50">
                      Modificar Responsable de Pago
                  </h3>
                  <p className="mb-4 text-sm text-slate-600 dark:text-slate-400">
                      Modificar datos personales de los responsables de pago.
                  </p>
                  <Button asChild className="w-full">
                      <Link href="/modificar-responsable-pago">Modificar responsable</Link>
                  </Button>
              </Card>
          </div>
              <h2 className="mb-4 text-xl font-semibold text-slate-900 dark:text-slate-50">Gestión de Pago</h2>
              <div className="grid gap-6 md:grid-cols-2 mb-8">
                  <Card className="group p-6 transition-all hover:shadow-xl">
                      <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-red-100 text-red-600 transition-colors group-hover:bg-red-600 group-hover:text-white dark:bg-red-950 dark:text-red-400">
                          <XCircle className="h-6 w-6" />
                      </div>
                      <h3 className="mb-2 text-xl font-semibold text-slate-900 dark:text-slate-50">Cancelar Reserva</h3>
                      <p className="mb-4 text-sm text-slate-600 dark:text-slate-400">
                          Cancelar reservas a nombre de un eventual huésped.
                      </p>
                      <Button asChild className="w-full">
                          <Link href="/cancelar-reserva">Cancelar reserva</Link>
                      </Button>
                  </Card>
              </div>
      </main>
    </div>
  )
}
