import Link from "next/link";

const steps = [
  "Ingresar fechas de ocupación y validar disponibilidad",
  "Seleccionar habitaciones y asignar a huésped + acompañantes",
  "Registrar datos (nombre, doc, relación, observaciones)",
  "Confirmar ocupación o cancelar y volver al menú",
];

export default function OcuparHabitacion() {
  return (
    <div className="min-h-screen bg-slate-950 text-slate-50">
      <main className="mx-auto flex max-w-4xl flex-col gap-6 px-6 py-12">
        <header className="space-y-3">
          <p className="text-xs uppercase tracking-[0.25em] text-amber-200/80">CU15</p>
          <h1 className="text-3xl font-semibold">Ocupar habitación (check-in)</h1>
          <p className="text-sm text-slate-200/80">
            Asignar habitaciones a un huésped y acompañantes. Incluye grilla de
            disponibilidad, selección de habitación y confirmación del ingreso.
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
            Registrar ocupación (placeholder)
          </button>
        </div>
      </main>
    </div>
  );
}
