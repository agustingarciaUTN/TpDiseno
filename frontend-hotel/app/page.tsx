import Link from "next/link";

const useCases = [
  {
    code: "CU02",
    title: "Buscar huésped",
    objective: "Gestionar datos personales de los huéspedes (búsqueda y edición)",
    path: "/cu/buscar-huesped",
    actions: ["Buscar por apellido, nombre o documento", "Seleccionar un huésped y continuar"],
  },
  {
    code: "CU04",
    title: "Reservar habitación",
    objective: "Reservar habitaciones a nombre de un eventual huésped en un rango de fechas",
    path: "/cu/reservar-habitacion",
    actions: ["Validar disponibilidad", "Seleccionar habitaciones y confirmar"],
  },
  {
    code: "CU09",
    title: "Alta de huésped",
    objective: "Cargar datos personales de nuevos huéspedes",
    path: "/cu/alta-huesped",
    actions: ["Datos obligatorios", "Confirmar o corregir"] ,
  },
  {
    code: "CU15",
    title: "Ocupar habitación (check-in)",
    objective: "Asignar habitaciones a un huésped y acompañantes",
    path: "/cu/ocupar-habitacion",
    actions: ["Validar disponibilidad", "Registrar datos y confirmar ingreso"],
  },
];

const highlights = [
  {
    label: "Check-ins hoy",
    value: "12",
    delta: "+3 vs ayer",
  },
  {
    label: "Ocupación",
    value: "81%",
    delta: "12 hab. disponibles",
  },
  {
    label: "Reservas pendientes",
    value: "7",
    delta: "4 requieren confirmación",
  },
];

export default function Home() {
  return (
    <div className="relative min-h-screen bg-slate-950 text-slate-50">
      <div className="pointer-events-none absolute inset-0 overflow-hidden">
        <div className="absolute -left-24 top-[-6rem] h-64 w-64 rounded-full bg-amber-400/20 blur-3xl" />
        <div className="absolute right-[-10rem] top-10 h-72 w-72 rounded-full bg-teal-400/10 blur-3xl" />
        <div className="absolute bottom-[-8rem] left-10 h-80 w-80 rounded-full bg-sky-400/10 blur-3xl" />
      </div>

      <main className="relative mx-auto flex max-w-6xl flex-col gap-12 px-6 py-14 md:px-10 md:py-16">
        <header className="flex flex-col gap-6 md:flex-row md:items-end md:justify-between">
          <div className="space-y-3">
            <p className="text-xs uppercase tracking-[0.25em] text-amber-200/80">Hotel Ops</p>
            <p className="text-3xl font-semibold leading-tight md:text-4xl">
              Menú de casos de uso
            </p>
            <p className="max-w-2xl text-sm text-slate-200/80 md:text-base">
              Elegí el caso de uso que querés ejecutar. Cada tarjeta te lleva al flujo
              correspondiente para reservas, estado de habitaciones, alta/búsqueda de
              huéspedes y check-in.
            </p>
            <div className="flex flex-wrap gap-3 text-sm">
              <Link
                href="#menu"
                className="rounded-full bg-amber-400 px-4 py-2 font-semibold text-slate-950 transition hover:bg-amber-300"
              >
                Ir al menú
              </Link>
              <Link
                href="/cu/estado-habitaciones"
                className="rounded-full border border-white/10 px-4 py-2 font-semibold text-slate-50 transition hover:border-white/30 hover:bg-white/5"
              >
                Ver estado de habitaciones
              </Link>
            </div>
          </div>

          <div className="rounded-2xl border border-white/10 bg-white/5 px-4 py-3 shadow-lg shadow-amber-500/10">
            <p className="text-xs uppercase tracking-[0.25em] text-amber-200/70">
              Turno
            </p>
            <p className="text-sm font-semibold">Recepción · Hoy</p>
            <p className="text-xs text-slate-200/70">Checklist: caja inicial, tarjetas, llaves</p>
          </div>
        </header>

        <section className="grid gap-4 md:grid-cols-3">
          {highlights.map((item) => (
            <div
              key={item.label}
              className="rounded-2xl border border-white/10 bg-white/5 p-5 shadow-lg shadow-slate-900/40"
            >
              <p className="text-sm text-slate-200/70">{item.label}</p>
              <div className="mt-2 flex items-baseline gap-2">
                <span className="text-3xl font-semibold">{item.value}</span>
                <span className="text-xs text-emerald-200/80">{item.delta}</span>
              </div>
            </div>
          ))}
        </section>

        <section id="menu" className="rounded-3xl border border-white/10 bg-white/5 p-6 shadow-xl shadow-slate-900/40">
          <div className="flex flex-wrap items-center justify-between gap-3">
            <div>
              <p className="text-xs uppercase tracking-[0.2em] text-amber-200/70">Operaciones</p>
              <h2 className="text-xl font-semibold">Menú de opciones</h2>
              <p className="text-sm text-slate-200/70">
                Seleccioná un caso de uso para continuar. Los flujos están basados en los
                requerimientos funcionales (CU02, CU04, CU05, CU09, CU15).
              </p>
            </div>
            <span className="rounded-full border border-amber-200/40 px-3 py-1 text-xs font-semibold text-amber-100">
              Conserje
            </span>
          </div>
          <div className="mt-6 grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {useCases.map((item) => (
              <div
                key={item.code}
                className="group flex h-full flex-col justify-between rounded-2xl border border-white/10 bg-white/5 p-4 transition hover:-translate-y-1 hover:border-amber-300/40 hover:bg-amber-100/5"
              >
                <div className="flex items-center justify-between gap-2">
                  <span className="rounded-full bg-amber-400/20 px-3 py-1 text-xs font-semibold text-amber-100">
                    {item.code}
                  </span>
                  <span className="text-xs text-slate-200/70">Actor: Conserje</span>
                </div>
                <div className="mt-3 space-y-2">
                  <p className="text-sm font-semibold text-slate-50">{item.title}</p>
                  <p className="text-xs text-slate-200/80">{item.objective}</p>
                  <ul className="space-y-1 text-xs text-slate-200/70">
                    {item.actions.map((action) => (
                      <li key={action} className="flex items-start gap-2">
                        <span aria-hidden className="text-amber-200">•</span>
                        <span>{action}</span>
                      </li>
                    ))}
                  </ul>
                </div>
                <Link
                  href={item.path}
                  className="mt-4 inline-flex items-center justify-center gap-2 rounded-full border border-white/10 px-4 py-2 text-xs font-semibold text-slate-50 transition hover:border-amber-200/60 hover:bg-amber-100/10"
                >
                  Abrir {item.code}
                  <span aria-hidden>→</span>
                </Link>
              </div>
            ))}
          </div>
        </section>

        <section className="grid gap-5 lg:grid-cols-[1.4fr_1fr]">
          <div className="rounded-3xl border border-white/10 bg-white/5 p-6 shadow-xl shadow-slate-900/40">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-xs uppercase tracking-[0.2em] text-amber-200/70">Contexto</p>
                <h2 className="text-xl font-semibold">Estado rápido</h2>
              </div>
              <span className="text-xs text-slate-200/70">Última actualización: hoy</span>
            </div>
            <div className="mt-6 grid gap-4 md:grid-cols-3">
              {highlights.map((item) => (
                <div
                  key={item.label}
                  className="rounded-2xl border border-white/10 bg-white/5 p-4 shadow-lg shadow-slate-900/40"
                >
                  <p className="text-sm text-slate-200/70">{item.label}</p>
                  <div className="mt-2 flex items-baseline gap-2">
                    <span className="text-3xl font-semibold">{item.value}</span>
                    <span className="text-xs text-emerald-200/80">{item.delta}</span>
                  </div>
                </div>
              ))}
            </div>
          </div>

          <div className="rounded-3xl border border-white/10 bg-white/5 p-6 shadow-xl shadow-slate-900/40">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-xs uppercase tracking-[0.2em] text-amber-200/70">Ayuda rápida</p>
                <h2 className="text-xl font-semibold">Tips de operación</h2>
              </div>
              <span className="text-xs text-slate-200/70">Atajos útiles</span>
            </div>
            <ul className="mt-4 space-y-2 text-xs text-slate-200/80">
              <li>• Shift + Tab retrocede un campo en los formularios.</li>
              <li>• Enter confirma acciones o equivale a click en botones.</li>
              <li>• La búsqueda acepta apellido, nombres o documento.</li>
              <li>• Mantené datos literales en mayúsculas según reglas del CU.</li>
            </ul>
          </div>
        </section>
      </main>
    </div>
  );
}
