import { RegistrarFacturaForm } from "@/components/registrar-factura-form" // Asegúrate que esta ruta coincida con donde guardaste tu componente

export default function FacturarPage() {
    return (
        <div className="min-h-screen bg-slate-50 dark:bg-slate-950">
            <main className="mx-auto max-w-5xl px-4 py-8">
                <RegistrarFacturaForm />
            </main>
        </div>
    )
}
