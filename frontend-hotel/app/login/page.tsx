"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Hotel, User, Lock, AlertCircle } from "lucide-react"
import { autenticarUsuario } from "@/lib/api"

export default function LoginPage() {
    const router = useRouter()
    const [username, setUsername] = useState("")
    const [password, setPassword] = useState("")
    const [error, setError] = useState("")
    const [isLoading, setIsLoading] = useState(false)

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault()
        setError("")
        setIsLoading(true)

        try {
            // CU1: Autenticar Usuario
            const response = await autenticarUsuario({
                nombre: username,
                contrasenia: password
            })
            
            // Login exitoso
            localStorage.setItem("isAuthenticated", "true")
            localStorage.setItem("username", username)
            
            // Redirect to main menu
            router.push("/")
        } catch (err: any) {
            // Show error and clear fields (Flujo Alternativo 3.A)
            setError(err.message || "El usuario o la contraseña no son válidos")
            setUsername("")
            setPassword("")
            setIsLoading(false)
        }
    }

    return (
        <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50 flex items-center justify-center p-4">
            <Card className="w-full max-w-md p-8">
                {/* Header */}
                <div className="mb-8 text-center">
                    <div className="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-2xl bg-gradient-to-br from-blue-600 to-indigo-600 text-white shadow-lg">
                        <Hotel className="h-8 w-8" />
                    </div>
                    <h1 className="mb-2 text-3xl font-bold bg-gradient-to-r from-blue-600 via-indigo-600 to-purple-600 bg-clip-text text-transparent">
                        Sistema de Hotelería
                    </h1>
                    <p className="text-slate-600">Autenticación de Usuario</p>
                </div>

                {/* Login Form */}
                <form onSubmit={handleLogin} className="space-y-6">
                    <div className="space-y-2">
                        <Label htmlFor="username" className="flex items-center gap-2">
                            <User className="h-4 w-4 text-slate-600" />
                            Nombre de Usuario
                        </Label>
                        <Input
                            id="username"
                            type="text"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            placeholder="Ingrese su usuario"
                            required
                            autoFocus
                            className={error ? "border-red-500" : ""}
                        />
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="password" className="flex items-center gap-2">
                            <Lock className="h-4 w-4 text-slate-600" />
                            Contraseña
                        </Label>
                        <Input
                            id="password"
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="••••••••"
                            required
                            className={error ? "border-red-500" : ""}
                        />
                    </div>

                    {/* Error Message */}
                    {error && (
                        <Card className="border-red-200 bg-red-50 p-3 dark:border-red-900 dark:bg-red-950/20">
                            <div className="flex items-center gap-2 text-red-600 dark:text-red-400">
                                <AlertCircle className="h-4 w-4" />
                                <p className="text-sm font-medium">{error}</p>
                            </div>
                        </Card>
                    )}

                    <Button
                        type="submit"
                        disabled={isLoading}
                        className="w-full bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700"
                    >
                        {isLoading ? "Verificando..." : "Ingresar al Sistema"}
                    </Button>
                </form>

                {/* Help Text */}
                <div className="mt-6 text-center">
                    <p className="text-xs text-slate-500">
                        Panel de Conserje - Acceso Restringido
                    </p>
                </div>
            </Card>
        </div>
    )
}
