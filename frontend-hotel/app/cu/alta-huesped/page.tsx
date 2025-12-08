import Link from "next/link";

const required = [
  "Nombres y apellido",
  "Tipo y número de documento",
  "CUIT (opcional según condición IVA)",
  "Condición IVA, domicilio, país, provincia, postal",
];

export default function AltaHuesped() {
  return (
    <div className="min-h-screen bg-slate-950 text-slate-50">
      <main className="mx-auto flex max-w-4xl flex-col gap-6 px-6 py-12">
        <header className="space-y-3">
          <p className="text-xs uppercase tracking-[0.25em] text-amber-200/80">CU09</p>
          <h1 className="text-3xl font-semibold">Alta de huésped</h1>
          <p className="text-sm text-slate-200/80">
            Cargar datos personales de huéspedes nuevos. Incluye validaciones de campos
            obligatorios y manejo de correcciones.
          </p>
        </header>

        <section className="rounded-2xl border border-white/10 bg-white/5 p-5 shadow-lg shadow-slate-900/40">
          <h2 className="text-lg font-semibold">Datos obligatorios</h2>
          <ul className="mt-3 space-y-2 text-sm text-slate-200/80">
            {required.map((item) => (
              <li key={item} className="flex gap-2">
                <span className="text-amber-200">•</span>
                <span>{item}</span>
              </li>
            ))}
          </ul>
          <p className="mt-3 text-xs text-slate-200/70">
            Atajos: Shift+Tab retrocede, Enter confirma, datos literales en mayúsculas.
          </p>
        </section>

        <div className="flex flex-wrap gap-3 text-sm">
          <Link
            href="/"
            className="rounded-full border border-white/10 px-4 py-2 font-semibold text-slate-50 transition hover:border-amber-200/60 hover:bg-amber-100/10"
          >
            Volver al menú
          </Link>
          <button className="rounded-full bg-amber-400 px-4 py-2 font-semibold text-slate-950 transition hover:bg-amber-300">
            Cargar datos (placeholder)
          </button>
        </div>
      </main>
    </div>
  );
}
