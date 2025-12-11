"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { Search, Edit, AlertCircle, Home, UserPlus } from "lucide-react"
import {Guest, useGuest} from "@/lib/guest-context"
import Link from "next/link"

export function BuscarHuespedForm() {
    const router = useRouter()
    const { setSelectedGuest } = useGuest()
    const [searchType, setSearchType] = useState<"documento" | "nombre">("documento")
    const [tipoDocumento, setTipoDocumento] = useState("")
    const [numeroDocumento, setNumeroDocumento] = useState("")
    const [apellido, setApellido] = useState("")
    const [nombres, setNombres] = useState("")
    const [searchResults, setSearchResults] = useState<Guest[]>([])
    const [error, setError] = useState("")
    const [isSearching, setIsSearching] = useState(false)
    const [documentError, setDocumentError] = useState("")
    const [hasSearched, setHasSearched] = useState(false)

    const validateDocument = (tipo: string, numero: string): boolean => {
        if (!numero) return true

        switch (tipo) {
            case "DNI":
            case "LE":
            case "LC":
                if (!/^\d{7,8}$/.test(numero)) {
                    setDocumentError("Debe contener 7 u 8 dígitos numéricos")
                    return false
                }
                break
            case "Pasaporte":
                if (!/^[A-Za-z0-9]+$/.test(numero)) {
                    setDocumentError("Puede contener letras y números")
                    return false
                }
                break
        }
        setDocumentError("")
        return true
    }

    const handleDocumentChange = (value: string) => {
        const upperValue = value.toUpperCase()
        setNumeroDocumento(upperValue)
        if (tipoDocumento) {
            validateDocument(tipoDocumento, upperValue)
        }
    }

    const handleSearch = async () => {
        console.log("[v0] Starting search with:", { searchType, tipoDocumento, numeroDocumento, apellido, nombres })
        setError("")
        setSearchResults([])
        setHasSearched(false)

        if (searchType === "documento") {
            if (!tipoDocumento || !numeroDocumento) {
                setError("Debe completar tipo y número de documento")
                return
            }
            if (!validateDocument(tipoDocumento, numeroDocumento)) {
                return
            }
        } else {
            if (!apellido && !nombres) {
                setError("Debe ingresar al menos apellido o nombre")
                return
            }
        }

        setIsSearching(true)
        console.log("[v0] Search validation passed, fetching mock data...")

        try {
            setTimeout(() => {
                console.log("[v0] Mock data loading...")
                const mockResults: Guest[] = [
                    {
                        id: "1",
                        tipoDocumento: tipoDocumento || "DNI",
                        numeroDocumento: numeroDocumento || "12345678",
                        apellido: apellido || "García",
                        nombres: nombres || "Juan Carlos",
                        cuit: "20-12345678-9",
                        posicionIVA: "Consumidor final",
                        fechaNacimiento: "1985-03-15",
                        direccion: "Av. Siempre Viva 742, Springfield, IL, 62701, USA",
                        telefono: "+54 9 11 1234-5678",
                        email: "juan.garcia@example.com",
                        ocupacion: "Ingeniero",
                        nacionalidad: "Argentina",
                    },
                    {
                        id: "2",
                        tipoDocumento: tipoDocumento || "DNI",
                        numeroDocumento: numeroDocumento || "23456789",
                        apellido: apellido || "García",
                        nombres: nombres || "María Laura",
                        cuit: "27-23456789-4",
                        posicionIVA: "Responsable inscripto",
                        fechaNacimiento: "1990-07-22",
                        direccion: "Calle Falsa 123, Ciudad, Provincia, CP 1234, País",
                        telefono: "+54 9 11 9876-5432",
                        email: "maria.garcia@example.com",
                        ocupacion: "Contadora",
                        nacionalidad: "Argentina",
                    },
                ]

                console.log("[v0] Mock results:", mockResults)
                setSearchResults(mockResults)
                setIsSearching(false)
                setHasSearched(true)
                console.log("[v0] Search completed successfully")
            }, 1000)
        } catch (err) {
            console.error("[v0] Error during search:", err)
            setError("Error al buscar huésped: " + (err instanceof Error ? err.message : "Error desconocido"))
            setIsSearching(false)
        }
    }

    const handleSelectGuest = (guest: Guest) => {
        console.log("[v0] Selecting guest:", guest)
        try {
            setSelectedGuest(guest)
            console.log("[v0] Guest selected, navigating to modificar-huesped")
            router.push("/modificar-huesped")
        } catch (err) {
            console.error("[v0] Error selecting guest:", err)
        }
    }

    const handleCreateNewGuest = () => {
        console.log("[v0] Creating new guest, clearing selection")
        try {
            setSelectedGuest(null)
            console.log("[v0] Selection cleared, navigating to alta-huesped")
            router.push("/alta-huesped")
        } catch (err) {
            console.error("[v0] Error creating new guest:", err)
        }
    }

    return (
        <div className="mx-auto max-w-4xl space-y-6">
            <div className="flex items-center justify-between">
                <div>
                    <h2 className="text-3xl font-bold text-foreground">Buscar Huésped</h2>
                    <p className="text-muted-foreground mt-1">Busque un huésped para ver o modificar sus datos</p>
                </div>
                <Button variant="outline" asChild>
                    <Link href="/">
                        <Home className="h-4 w-4 mr-2" />
                        Volver al inicio
                    </Link>
                </Button>
            </div>

            <Card>
                <CardHeader>
                    <CardTitle>Criterios de Búsqueda</CardTitle>
                    <CardDescription>Seleccione cómo desea buscar al huésped</CardDescription>
                </CardHeader>
                <CardContent className="space-y-6">
                    <div className="space-y-2">
                        <Label>Tipo de búsqueda</Label>
                        <Select
                            value={searchType}
                            onValueChange={(value: "documento" | "nombre") => {
                                setSearchType(value)
                                setError("")
                                setDocumentError("")
                                setSearchResults([])
                            }}
                        >
                            <SelectTrigger>
                                <SelectValue />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value="documento">Por Tipo y Número de Documento</SelectItem>
                                <SelectItem value="nombre">Por Apellido y/o Nombre</SelectItem>
                            </SelectContent>
                        </Select>
                    </div>

                    {searchType === "documento" ? (
                        <div className="grid gap-4 sm:grid-cols-2">
                            <div className="space-y-2">
                                <Label htmlFor="tipoDocumento">Tipo de Documento *</Label>
                                <Select
                                    value={tipoDocumento}
                                    onValueChange={(value) => {
                                        setTipoDocumento(value)
                                        if (numeroDocumento) {
                                            validateDocument(value, numeroDocumento)
                                        }
                                    }}
                                >
                                    <SelectTrigger id="tipoDocumento">
                                        <SelectValue placeholder="Seleccione tipo" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="DNI">DNI</SelectItem>
                                        <SelectItem value="LE">LE (Libreta de Enrolamiento)</SelectItem>
                                        <SelectItem value="LC">LC (Libreta Cívica)</SelectItem>
                                        <SelectItem value="Pasaporte">Pasaporte</SelectItem>
                                        <SelectItem value="OTROS">OTROS</SelectItem>
                                    </SelectContent>
                                </Select>
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="numeroDocumento">Número de Documento *</Label>
                                <Input
                                    id="numeroDocumento"
                                    value={numeroDocumento}
                                    onChange={(e) => handleDocumentChange(e.target.value)}
                                    placeholder={
                                        tipoDocumento === "DNI" || tipoDocumento === "LE" || tipoDocumento === "LC"
                                            ? "7-8 dígitos"
                                            : tipoDocumento === "Pasaporte"
                                                ? "Letras y números"
                                                : "Ingrese número"
                                    }
                                />
                                {documentError && <p className="text-sm text-destructive">{documentError}</p>}
                            </div>
                        </div>
                    ) : (
                        <div className="grid gap-4 sm:grid-cols-2">
                            <div className="space-y-2">
                                <Label htmlFor="apellido">Apellido</Label>
                                <Input
                                    id="apellido"
                                    value={apellido}
                                    onChange={(e) => setApellido(e.target.value.toUpperCase())}
                                    placeholder="GARCÍA"
                                />
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="nombres">Nombres</Label>
                                <Input
                                    id="nombres"
                                    value={nombres}
                                    onChange={(e) => setNombres(e.target.value.toUpperCase())}
                                    placeholder="JUAN CARLOS"
                                />
                            </div>
                        </div>
                    )}

                    {error && (
                        <Alert variant="destructive">
                            <AlertCircle className="h-4 w-4" />
                            <AlertDescription>{error}</AlertDescription>
                        </Alert>
                    )}

                    <Button onClick={handleSearch} disabled={isSearching} className="w-full">
                        <Search className="mr-2 h-4 w-4" />
                        {isSearching ? "Buscando..." : "Buscar"}
                    </Button>
                </CardContent>
            </Card>

            {searchResults.length > 0 && (
                <Card>
                    <CardHeader>
                        <CardTitle>Resultados de la Búsqueda</CardTitle>
                        <CardDescription>Se encontraron {searchResults.length} huésped(es)</CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-3">
                        {searchResults.map((guest) => (
                            <Card key={guest.id} className="overflow-hidden">
                                <CardContent className="p-4">
                                    <div className="flex items-start justify-between gap-4">
                                        <div className="flex-1 space-y-2">
                                            <div className="flex items-center gap-2">
                                                <h3 className="text-lg font-semibold">
                                                    {guest.apellido}, {guest.nombres}
                                                </h3>
                                            </div>
                                            <div className="grid gap-2 text-sm text-muted-foreground sm:grid-cols-2">
                                                <div>
                                                    <span className="font-medium">Documento:</span> {guest.tipoDocumento} {guest.numeroDocumento}
                                                </div>
                                                {guest.email && (
                                                    <div>
                                                        <span className="font-medium">Email:</span> {guest.email}
                                                    </div>
                                                )}
                                                {guest.telefono && (
                                                    <div>
                                                        <span className="font-medium">Teléfono:</span> {guest.telefono}
                                                    </div>
                                                )}
                                                {guest.nacionalidad && (
                                                    <div>
                                                        <span className="font-medium">Nacionalidad:</span> {guest.nacionalidad}
                                                    </div>
                                                )}
                                            </div>
                                        </div>
                                        <Button onClick={() => handleSelectGuest(guest)} size="sm">
                                            <Edit className="mr-2 h-4 w-4" />
                                            Modificar
                                        </Button>
                                    </div>
                                </CardContent>
                            </Card>
                        ))}
                    </CardContent>
                </Card>
            )}

            {hasSearched && searchResults.length === 0 && (
                <Card>
                    <CardHeader>
                        <CardTitle>No se encontraron resultados</CardTitle>
                        <CardDescription>No existe ningún huésped con los criterios especificados</CardDescription>
                    </CardHeader>
                    <CardContent>
                        <Alert className="mb-4">
                            <AlertCircle className="h-4 w-4" />
                            <AlertDescription>
                                El huésped que está buscando no se encuentra registrado en el sistema. ¿Desea dar de alta un nuevo
                                huésped?
                            </AlertDescription>
                        </Alert>
                        <Button onClick={handleCreateNewGuest} className="w-full">
                            <UserPlus className="mr-2 h-4 w-4" />
                            Dar de alta nuevo huésped
                        </Button>
                    </CardContent>
                </Card>
            )}
        </div>
    )
}
