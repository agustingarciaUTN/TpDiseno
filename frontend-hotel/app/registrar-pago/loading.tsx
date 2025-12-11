export default function Loading() {
    return (
        <div className="flex min-h-screen items-center justify-center">
            <div className="text-center">
                <div className="h-32 w-32 animate-spin rounded-full border-b-2 border-t-2 border-blue-600"></div>
                <p className="mt-4 text-slate-600 dark:text-slate-400">Cargando...</p>
            </div>
        </div>
    )
}
