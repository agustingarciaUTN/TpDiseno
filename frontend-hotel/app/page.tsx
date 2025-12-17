"use client"

import Link from "next/link"
import { useRouter } from "next/navigation"
import { useEffect, useState } from "react"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import {
    Hotel,
    Search,
    Edit,
    UserPlus,
    XCircle,
    FileText,
    UserCheck,
    UserMinus,
    DollarSign,
    FileCheck,
    Users,
    Receipt,
    CreditCard,
    CalendarCheck,
    DoorOpen,
    LogOut,
} from "lucide-react"
import { useGuest } from "@/lib/guest-context"

export default function Home() {
    const router = useRouter()
    const { selectedGuest } = useGuest()
    const [isAuthenticated, setIsAuthenticated] = useState(false)
    const [username, setUsername] = useState("")

    useEffect(() => {
        // Check authentication on mount
        const authStatus = localStorage.getItem("isAuthenticated")
        const storedUsername = localStorage.getItem("username")
        
        if (authStatus === "true" && storedUsername) {
            setIsAuthenticated(true)
            setUsername(storedUsername)
        } else {
            router.push("/login")
        }
    }, [router])

    const handleLogout = () => {
        localStorage.removeItem("isAuthenticated")
        localStorage.removeItem("username")
        router.push("/login")
    }

    if (!isAuthenticated) {
        return null // or loading spinner
    }

    return (
        <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50">
            <main className="mx-auto max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
                {/* Header */}
                <div className="mb-16">
                    <div className="flex justify-end mb-4">
                        <Card className="px-4 py-2 flex items-center gap-3">
                            <span className="text-sm text-slate-600">
                                Usuario: <span className="font-semibold text-slate-900">{username}</span>
                            </span>
                            <Button
                                onClick={handleLogout}
                                variant="ghost"
                                size="sm"
                                className="gap-2 text-red-600 hover:text-red-700 hover:bg-red-50"
                            >
                                <LogOut className="h-4 w-4" />
                                Cerrar Sesión
                            </Button>
                        </Card>
                    </div>
                    <div className="text-center">
                        <div className="mx-auto mb-6 flex h-20 w-20 items-center justify-center rounded-3xl bg-gradient-to-br from-blue-600 via-indigo-600 to-purple-600 text-white shadow-xl">
                            <Hotel className="h-10 w-10" />
                        </div>
                        <h1 className="mb-4 bg-gradient-to-r from-blue-600 via-indigo-600 to-purple-600 bg-clip-text text-5xl font-bold text-transparent">
                            Sistema de Hotelería
                        </h1>
                        <p className="text-xl text-slate-600">Gestión integral de huéspedes, pagos y reservas</p>
                    </div>
                </div>

                <div className="space-y-12">
                    {/* Gestión de Huéspedes */}
                    <section>
                        <div className="mb-6 flex items-center gap-3">
                            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-blue-100 text-blue-600">
                                <Users className="h-5 w-5" />
                            </div>
                            <h2 className="text-2xl font-bold text-slate-900">Gestión de Huéspedes</h2>
                        </div>
                        <div className="grid gap-6 grid-cols-1 sm:grid-cols-2 justify-center place-items-center">
                            {/* Buscar Huésped */}
                            <Card className="group relative overflow-hidden bg-white p-6 transition-all hover:shadow-xl hover:-translate-y-1 w-full max-w-md min-h-[240px] flex flex-col justify-between">
                                <div className="absolute right-0 top-0 h-32 w-32 translate-x-8 -translate-y-8 rounded-full bg-blue-100/50" />
                                <div className="relative">
                                    <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-blue-100 text-blue-600 transition-colors group-hover:bg-blue-600 group-hover:text-white">
                                        <Search className="h-6 w-6" />
                                    </div>
                                    <h3 className="mb-2 text-xl font-semibold text-slate-900">Buscar Huésped</h3>
                                    <p className="mb-4 text-sm leading-relaxed text-slate-600">
                                        Buscar huéspedes existentes por apellido, nombre, tipo o número de documento.
                                    </p>
                                    <Button asChild className="w-full bg-blue-600 text-white hover:bg-blue-700">
                                        <Link href="/buscar-huesped">Ir a búsqueda</Link>
                                    </Button>
                                </div>
                            </Card>

                            {/* Modificar Huésped
                            <Card
                                className={`group relative overflow-hidden bg-white p-6 transition-all hover:shadow-xl hover:-translate-y-1 ${
                                    !selectedGuest ? "opacity-40 pointer-events-none" : ""
                                }`}
                            >
                                <div className="absolute right-0 top-0 h-32 w-32 translate-x-8 -translate-y-8 rounded-full bg-green-100/50" />
                                <div className="relative">
                                    <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-green-100 text-green-600 transition-colors group-hover:bg-green-600 group-hover:text-white">
                                        <Edit className="h-6 w-6" />
                                    </div>
                                    <h3 className="mb-2 text-xl font-semibold text-slate-900">Modificar Huésped</h3>
                                    <p className="mb-4 text-sm leading-relaxed text-slate-600">
                                        Actualizar los datos personales de un huésped existente.
                                    </p>
                                    {selectedGuest ? (
                                        <Button asChild className="w-full bg-green-600 text-white hover:bg-green-700">
                                            <Link href="/modificar-huesped">Modificar datos</Link>
                                        </Button>
                                    ) : (
                                        <Button className="w-full bg-slate-200 text-slate-700 cursor-not-allowed" disabled>
                                            Seleccione un huésped primero
                                        </Button>
                                    )}
                                </div>
                            </Card>*/}

                            {/* Dar de Alta Huésped */}
                            <Card
                                className={`group relative overflow-hidden bg-white p-6 transition-all hover:shadow-xl hover:-translate-y-1 w-full max-w-md min-h-[240px] flex flex-col justify-between ${
                                    selectedGuest ? "opacity-40 pointer-events-none" : ""
                                }`}
                            >
                                <div className="absolute right-0 top-0 h-32 w-32 translate-x-8 -translate-y-8 rounded-full bg-violet-100/50" />
                                <div className="relative">
                                    <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-violet-100 text-violet-600 transition-colors group-hover:bg-violet-600 group-hover:text-white">
                                        <UserPlus className="h-6 w-6" />
                                    </div>
                                    <h3 className="mb-2 text-xl font-semibold text-slate-900">Dar de Alta Huésped</h3>
                                    <p className="mb-4 text-sm leading-relaxed text-slate-600">
                                        Registrar un nuevo huésped en el sistema hotelero.
                                    </p>
                                    {!selectedGuest ? (
                                        <Button asChild className="w-full bg-violet-600 text-white hover:bg-violet-700">
                                            <Link href="/alta-huesped">Crear nuevo huésped</Link>
                                        </Button>
                                    ) : (
                                        <Button className="w-full bg-slate-200 text-slate-700 cursor-not-allowed" disabled>
                                            Ya hay un huésped seleccionado
                                        </Button>
                                    )}
                                </div>
                            </Card>

                            {/* Dar de Baja Huésped
                            <Card className="group relative overflow-hidden bg-white p-6 transition-all hover:shadow-xl hover:-translate-y-1">
                                <div className="absolute right-0 top-0 h-32 w-32 translate-x-8 -translate-y-8 rounded-full bg-rose-100/50" />
                                <div className="relative">
                                    <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-rose-100 text-rose-600 transition-colors group-hover:bg-rose-600 group-hover:text-white">
                                        <UserMinus className="h-6 w-6" />
                                    </div>
                                    <h3 className="mb-2 text-xl font-semibold text-slate-900">Dar de Baja Huésped</h3>
                                    <p className="mb-4 text-sm leading-relaxed text-slate-600">
                                        Eliminar un huésped del sistema permanentemente.
                                    </p>
                                    <Button asChild className="w-full bg-rose-600 text-white hover:bg-rose-700">
                                        <Link href="/baja-huesped">Eliminar huésped</Link>
                                    </Button>
                                </div>
                            </Card>*/}
                        </div>
                    </section>

                    {/* Gestión de Pagos y Facturación */}
                    <section>
                        <div className="mb-6 flex items-center gap-3">
                            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-emerald-100 text-emerald-600">
                                <Receipt className="h-5 w-5" />
                            </div>
                            <h2 className="text-2xl font-bold text-slate-900">Gestión de Pagos y Facturación</h2>
                        </div>
                        <div className="grid gap-6 grid-cols-1 sm:grid-cols-2 justify-center place-items-center">
                            {/* Facturar */}
                            <Card className="group relative overflow-hidden bg-white p-6 transition-all hover:shadow-xl hover:-translate-y-1 w-full max-w-md min-h-[240px] flex flex-col justify-between">
                                <div className="absolute right-0 top-0 h-32 w-32 translate-x-8 -translate-y-8 rounded-full bg-amber-100/50" />
                                <div className="relative">
                                    <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-amber-100 text-amber-600 transition-colors group-hover:bg-amber-600 group-hover:text-white">
                                        <FileText className="h-6 w-6" />
                                    </div>
                                    <h3 className="mb-2 text-xl font-semibold text-slate-900">Facturar</h3>
                                    <p className="mb-4 text-sm leading-relaxed text-slate-600">
                                        Generar facturas para una habitación en el momento del check out.
                                    </p>
                                    <Button asChild className="w-full bg-amber-600 text-white hover:bg-amber-700">
                                        <Link href="/facturar">Generar factura</Link>
                                    </Button>
                                </div>
                            </Card>

                            {/* Registrar Pago */}
                            <Card className="group relative overflow-hidden bg-white p-6 transition-all hover:shadow-xl hover:-translate-y-1 w-full max-w-md min-h-[240px] flex flex-col justify-between">
                                <div className="absolute right-0 top-0 h-32 w-32 translate-x-8 -translate-y-8 rounded-full bg-green-100/50" />
                                <div className="relative">
                                    <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-green-100 text-green-600 transition-colors group-hover:bg-green-600 group-hover:text-white">
                                        <DollarSign className="h-6 w-6" />
                                    </div>
                                    <h3 className="mb-2 text-xl font-semibold text-slate-900">Registrar Pago</h3>
                                    <p className="mb-4 text-sm leading-relaxed text-slate-600">
                                        Ingresar el pago de una factura en el momento del check out.
                                    </p>
                                    <Button asChild className="w-full bg-green-600 text-white hover:bg-green-700">
                                        <Link href="/registrar-pago">Registrar pago</Link>
                                    </Button>
                                </div>
                            </Card>

                        </div>
                    </section>


                    {/* Gestión de Habitaciones */}
                    <section>
                        <div className="mb-6 flex items-center gap-3">
                            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-orange-100 text-orange-600">
                                <Hotel className="h-5 w-5" />
                            </div>
                            <h2 className="text-2xl font-bold text-slate-900">Gestión de Habitaciones</h2>
                        </div>
                        <div className="grid gap-6 grid-cols-1 sm:grid-cols-2 justify-center place-items-center">
                            {/* Ocupar Habitación */}
                            <Card className="group relative overflow-hidden bg-white p-6 transition-all hover:shadow-xl hover:-translate-y-1 w-full max-w-md min-h-[240px] flex flex-col justify-between">
                                <div className="absolute right-0 top-0 h-32 w-32 translate-x-8 -translate-y-8 rounded-full bg-orange-100/50" />
                                <div className="relative">
                                    <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-orange-100 text-orange-600 transition-colors group-hover:bg-orange-600 group-hover:text-white">
                                        <DoorOpen className="h-6 w-6" />
                                    </div>
                                    <h3 className="mb-2 text-xl font-semibold text-slate-900">Ocupar Habitación</h3>
                                    <p className="mb-4 text-sm leading-relaxed text-slate-600">
                                        Realizar el check-in de huéspedes en una habitación disponible o reservada.
                                    </p>
                                    <Button asChild className="w-full bg-orange-600 text-white hover:bg-orange-700">
                                        <Link href="/ocupar-habitacion">Check-In</Link>
                                    </Button>
                                </div>
                            </Card>
                        </div>
                    </section>

                    {/* Gestión de Reservas */}
                    <section>
                        <div className="mb-6 flex items-center gap-3">
                            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-indigo-100 text-indigo-600">
                                <CalendarCheck className="h-5 w-5" />
                            </div>
                            <h2 className="text-2xl font-bold text-slate-900">Gestión de Reservas</h2>
                        </div>
                        <div className="grid gap-6 grid-cols-1 sm:grid-cols-2 justify-center place-items-center">
                            {/* Reservar Habitación */}
                            <Card className="group relative overflow-hidden bg-white p-6 transition-all hover:shadow-xl hover:-translate-y-1 w-full max-w-md min-h-[240px] flex flex-col justify-between">
                                <div className="absolute right-0 top-0 h-32 w-32 translate-x-8 -translate-y-8 rounded-full bg-indigo-100/50" />
                                <div className="relative">
                                    <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-indigo-100 text-indigo-600 transition-colors group-hover:bg-indigo-600 group-hover:text-white">
                                        <CalendarCheck className="h-6 w-6" />
                                    </div>
                                    <h3 className="mb-2 text-xl font-semibold text-slate-900">Reservar Habitación</h3>
                                    <p className="mb-4 text-sm leading-relaxed text-slate-600">
                                        Registrar una nueva reserva de habitación para un huésped responsable.
                                    </p>
                                    <Button asChild className="w-full bg-indigo-600 text-white hover:bg-indigo-700">
                                        <Link href="/reservar-habitacion">Crear reserva</Link>
                                    </Button>
                                </div>
                            </Card>

                            {/* Cancelar Reserva */}
                            <Card className="group relative overflow-hidden bg-white p-6 transition-all hover:shadow-xl hover:-translate-y-1 w-full max-w-md min-h-[240px] flex flex-col justify-between">
                                <div className="absolute right-0 top-0 h-32 w-32 translate-x-8 -translate-y-8 rounded-full bg-red-100/50" />
                                <div className="relative">
                                    <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-red-100 text-red-600 transition-colors group-hover:bg-red-600 group-hover:text-white">
                                        <XCircle className="h-6 w-6" />
                                    </div>
                                    <h3 className="mb-2 text-xl font-semibold text-slate-900">Cancelar Reserva</h3>
                                    <p className="mb-4 text-sm leading-relaxed text-slate-600">
                                        Cancelar reservas a nombre de un eventual huésped.
                                    </p>
                                    <Button asChild className="w-full bg-red-600 text-white hover:bg-red-700">
                                        <Link href="/cancelar-reserva">Cancelar reserva</Link>
                                    </Button>
                                </div>
                            </Card>
                        </div>
                    </section>
                </div>
            </main>
        </div>
    )
}
