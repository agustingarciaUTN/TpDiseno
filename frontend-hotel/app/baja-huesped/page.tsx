import { BajaHuespedForm } from "@/components/baja-huesped-form"

export default function BajaHuespedPage() {
    return (
        <div className="min-h-screen bg-background">
            <header className="border-b border-border bg-card">
                <div className="container mx-auto px-4 py-4">
                    <h1 className="text-2xl font-semibold text-foreground">Sistema de Gestión Hotelera</h1>
                    <p className="text-sm text-muted-foreground mt-1">Panel de Conserje</p>
                </div>
            </header>
            <main className="container mx-auto px-4 py-8">
                <BajaHuespedForm />
            </main>
        </div>
    )
}
