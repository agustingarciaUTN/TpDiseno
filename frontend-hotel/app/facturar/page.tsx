import { AltaResponsablePagoForm } from "@/components/alta-responsable-pago-form"

export default function AltaResponsablePagoPage() {
    return (
        <div className="min-h-screen bg-gradient-to-br from-slate-50 via-emerald-50 to-teal-50 dark:from-slate-950 dark:via-slate-900 dark:to-slate-950">
            <main className="mx-auto max-w-4xl px-4 py-12 sm:px-6 lg:px-8">
                <div className="mb-8 text-center">
                    <h1 className="mb-3 text-4xl font-bold text-slate-900 dark:text-slate-50">Dar Alta de Responsable de Pago</h1>
                    <p className="text-lg text-slate-600 dark:text-slate-400">Caso de Uso 12 - Versión 1.1</p>
                </div>
                <AltaResponsablePagoForm />
            </main>
        </div>
    )
}
