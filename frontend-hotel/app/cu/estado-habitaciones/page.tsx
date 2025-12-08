import Link from "next/link";

const steps = [
  "Ingresar fecha desde y hasta",
  "Validar fechas y mostrar grilla de estados",
  "Diferenciar por color y estado (Disponible, Reservada, Ocupada)",
];

export default function EstadoHabitaciones() {
  return (
    <div className="min-h-screen bg-slate-950 text-slate-50">
      <main className="mx-auto flex max-w-4xl flex-col gap-6 px-6 py-12">
        <header className="space-y-3">
          <p className="text-xs uppercase tracking-[0.25em] text-amber-200/80">CU05</p>
          <h1 className="text-3xl font-semibold">Mostrar estado de habitaciones</h1>
          <p className="text-sm text-slate-200/80">
            Consultar estado de habitaciones en un rango de fechas y mostrar la grilla
            ordenada por comodidades y fechas.
          </p>
        </header>

        <section className="rounded-2xl border border-white/10 bg-white/5 p-5 shadow-lg shadow-slate-900/40">
          <h2 className="text-lg font-semibold">Pasos clave</h2>
          <ul className="mt-3 space-y-2 text-sm text-slate-200/80">
            {steps.map((step) => (
              <li key={step} className="flex gap-2">
                <span className="text-amber-200">•</span>
                <span>{step}</span>
              </li>
            ))}
          </ul>
        </section>

        <div className="flex flex-wrap gap-3 text-sm">
          <Link
            href="/"
            className="rounded-full border border-white/10 px-4 py-2 font-semibold text-slate-50 transition hover:border-amber-200/60 hover:bg-amber-100/10"
          >
            Volver al menú
          </Link>
          <button className="rounded-full bg-amber-400 px-4 py-2 font-semibold text-slate-950 transition hover:bg-amber-300">
            Consultar estado (placeholder)
          </button>
        </div>
      </main>
    </div>
  );
}
