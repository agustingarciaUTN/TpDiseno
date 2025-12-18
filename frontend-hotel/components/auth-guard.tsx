"use client"

import { useEffect, useState } from "react"
import { useRouter, usePathname } from "next/navigation"

export function AuthGuard({ children }: { children: React.ReactNode }) {
    const router = useRouter()
    const pathname = usePathname()
    const [authorized, setAuthorized] = useState(false)

    useEffect(() => {
        // 1. Verificar si el usuario está autenticado en localStorage
        const isAuthenticated = localStorage.getItem("isAuthenticated") === "true"

        // 2. Definir rutas públicas que no requieren login
        const publicPaths = ["/login"]
        const isPublicPath = publicPaths.includes(pathname)

        if (!isAuthenticated && !isPublicPath) {
            // Si NO está logueado y trata de entrar a una ruta privada -> Redirigir a Login
            setAuthorized(false)
            router.push("/login")
        } else {
            // Si está logueado O está en una ruta pública -> Permitir acceso
            setAuthorized(true)
        }
    }, [router, pathname])

    // 3. Mientras se verifica, no renderizar nada para evitar el "parpadeo" del contenido protegido
    // (Opcionalmente podrías retornar un componente <Loading /> aquí)
    if (!authorized && pathname !== "/login") {
        return null 
    }

    return <>{children}</>
}