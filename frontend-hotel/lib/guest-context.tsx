"use client"

import { createContext, useContext, useState, type ReactNode } from "react"

export interface Guest {
    id: string
    tipoDocumento: string
    numeroDocumento: string
    apellido: string
    nombres: string
    cuit?: string
    posicionIVA?: string
    fechaNacimiento?: string
    direccion?: string
    telefono?: string
    email?: string
    ocupacion?: string
    nacionalidad?: string
}

interface GuestContextType {
    selectedGuest: Guest | null
    setSelectedGuest: (guest: Guest | null) => void
}

const GuestContext = createContext<GuestContextType | undefined>(undefined)

export function GuestProvider({ children }: { children: ReactNode }) {
    const [selectedGuest, setSelectedGuest] = useState<Guest | null>(null)

    return <GuestContext.Provider value={{ selectedGuest, setSelectedGuest }}>{children}</GuestContext.Provider>
}

export function useGuest() {
    const context = useContext(GuestContext)
    if (context === undefined) {
        throw new Error("useGuest must be used within a GuestProvider")
    }
    return context
}
