import { RegistrarFacturaForm } from "@/components/registrar-factura-form"

export default function FacturarPage() {
    return (
        <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50 dark:from-slate-950 dark:via-slate-900 dark:to-slate-950">
            <div className="mx-auto max-w-4xl px-4 py-12">
                <RegistrarFacturaForm />
            </div>
        </div>
    )
}
