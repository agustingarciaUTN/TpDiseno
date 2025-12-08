/*

package Facultad.TrabajoPracticoDesarrollo.PantallaDeTrabajo;

import Facultad.TrabajoPracticoDesarrollo.DTOs.*;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Habitacion;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Huesped;
import Facultad.TrabajoPracticoDesarrollo.Excepciones.CancelacionException;
import Facultad.TrabajoPracticoDesarrollo.Services.*; // Importamos los nuevos Services
import Facultad.TrabajoPracticoDesarrollo.Utils.Colores;
import Facultad.TrabajoPracticoDesarrollo.Utils.Mapear.MapearHabitacion;
import Facultad.TrabajoPracticoDesarrollo.Utils.Mapear.MapearHuesped;
import Facultad.TrabajoPracticoDesarrollo.Utils.PantallaHelper;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class Pantalla implements CommandLineRunner {

    // REEMPLAZO DE GESTORES POR SERVICES
    private final HuespedService huespedService;
    private final UsuarioService usuarioService;
    private final HabitacionService habitacionService;
    private final ReservaService reservaService;
    private final EstadiaService estadiaService;

    private final Scanner scanner;
    private boolean usuarioAutenticado;
    private String nombreUsuarioActual;

    @Autowired // Inyección de Dependencias
    public Pantalla(HuespedService huespedService, // <--- CORREGIDO: "Pantalla" (Mayúscula)
                    UsuarioService usuarioService,
                    HabitacionService habitacionService,
                    ReservaService reservaService,
                    EstadiaService estadiaService) {

        this.huespedService = huespedService;
        this.usuarioService = usuarioService;
        this.habitacionService = habitacionService;
        this.reservaService = reservaService;
        this.estadiaService = estadiaService;

        this.scanner = new Scanner(System.in);
        this.usuarioAutenticado = false;
        this.nombreUsuarioActual = "";
    }

    @Override
    public void run(String... args) throws Exception {
        iniciarSistema();
    }

    public void iniciarSistema() throws Exception {
        System.out.println(Colores.CYAN + "╔════════════════════════════════════════════════════╗");
        System.out.println("║         🏨 SISTEMA DE GESTION HOTELERA             ║");
        System.out.println("╚════════════════════════════════════════════════════╝" + Colores.RESET);
        System.out.println("");

        if (autenticarUsuario()) {
            mostrarMenuPrincipal();
        } else {
            System.out.println(Colores.ROJO + "❌ No se pudo acceder al sistema." + Colores.RESET);
        }

        System.out.println("\n" + Colores.CYAN + "========================================");
        System.out.println("        👋 FIN DEL SISTEMA");
        System.out.println("========================================" + Colores.RESET);
    }

    private boolean autenticarUsuario() {
        System.out.println(Colores.NEGRILLA + "🔐 AUTENTICACION DE USUARIO" + Colores.RESET);
        System.out.println(Colores.CYAN + "   -------------------------" + Colores.RESET + "\n");

        boolean autenticacionExitosa = false;

        while (!autenticacionExitosa) {
            System.out.println("Por favor, ingrese sus credenciales:");

            System.out.print(Colores.VERDE + "   👤 Usuario: " + Colores.RESET);
            String nombre = scanner.nextLine().trim();

            System.out.print(Colores.VERDE + "   🔑 Contraseña: " + Colores.RESET);
            String contrasenia = scanner.nextLine();

            // USAMOS EL SERVICE
            boolean credencialesValidas = usuarioService.autenticarUsuario(nombre, contrasenia);

            if (credencialesValidas) {
                this.usuarioAutenticado = true;
                this.nombreUsuarioActual = nombre;
                System.out.println("\n" + Colores.VERDE + "✅ ¡Autenticación exitosa! Bienvenido, " + nombre + Colores.RESET + "\n");
                autenticacionExitosa = true;
            } else {
                System.out.println("\n" + Colores.ROJO + "╔═════════════════════════════════════════════╗");
                System.out.println("║ ❌ ERROR: Usuario o contraseña inválidos    ║");
                System.out.println("╚═════════════════════════════════════════════╝" + Colores.RESET + "\n");

                int opcion = -1;
                boolean opcionValida = false;

                while (!opcionValida) {
                    System.out.println("\n¿Qué desea hacer?");
                    System.out.println(Colores.AMARILLO + " [1]" + Colores.RESET + " 🔄 Volver a ingresar credenciales");
                    System.out.println(Colores.AMARILLO + " [2]" + Colores.RESET + " 🚪 Cerrar el sistema");
                    System.out.print(">> Ingrese una opción: ");

                    try {
                        String entrada = scanner.nextLine().trim();
                        if (entrada.isEmpty()) continue;
                        opcion = Integer.parseInt(entrada);

                        if (opcion == 1 || opcion == 2) {
                            opcionValida = true;
                        } else {
                            System.out.println(Colores.ROJO + "⚠️  Opción inválida." + Colores.RESET);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println(Colores.ROJO + "⚠️  Error: Debe ingresar un número." + Colores.RESET);
                    }
                }

                if (opcion == 2) {
                    System.out.println(Colores.AZUL + "\nCerrando el sistema..." + Colores.RESET);
                    return false;
                } else {
                    System.out.println(Colores.AZUL + "\n-- Intente nuevamente --\n" + Colores.RESET);
                }
            }
        }
        return true;
    }

    private void mostrarMenuPrincipal() throws Exception {
        boolean salir = false;

        while (!salir && usuarioAutenticado) {
            System.out.println("\n" + Colores.CYAN + "╔════════════════════════════════════════════════════╗");
            System.out.println("║                MENU PRINCIPAL                      ║");
            System.out.println("╚════════════════════════════════════════════════════╝" + Colores.RESET);
            System.out.println(Colores.VERDE + "   👤 Usuario activo: " + Colores.NEGRILLA + nombreUsuarioActual + Colores.RESET);
            System.out.println(Colores.CYAN + "   ──────────────────────────────────────────────────" + Colores.RESET);

            System.out.println(Colores.AMARILLO + "   [1]" + Colores.RESET + " 🔍 Buscar huésped (CU2)");
            System.out.println(Colores.AMARILLO + "   [2]" + Colores.RESET + " 🛏️  Reservar Habitación (CU4)");
            System.out.println(Colores.AMARILLO + "   [3]" + Colores.RESET + " 📝 Dar de alta huésped (CU9)");
            System.out.println(Colores.AMARILLO + "   [4]" + Colores.RESET + " 🗑️  Ocupar una Habitacion (CU15)");
            System.out.println(Colores.AMARILLO + "   [5]" + Colores.RESET + " 🚪 Cerrar sesión");

            System.out.println(Colores.CYAN + "======================================================" + Colores.RESET);
            System.out.print(">> Ingrese una opción: ");

            int opcion = -1;
            try {
                String entrada = scanner.nextLine().trim();
                if (entrada.isEmpty()) throw new NumberFormatException();
                opcion = Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println(Colores.ROJO + "\n❌ Opción inválida.\n" + Colores.RESET);
                continue;
            }

            System.out.println();

            switch (opcion) {
                case 1: buscarHuesped(); break;
                case 2: reservarHabitacion(); break;
                case 3: darDeAltaHuesped(); break;
                case 4: ocuparHabitacion(); break;
                case 5:
                    System.out.print(Colores.AMARILLO + "⚠️  ¿Cerrar sesión? (SI/NO): " + Colores.RESET);
                    if (scanner.nextLine().trim().equalsIgnoreCase("SI")) {
                        salir = true;
                        usuarioAutenticado = false;
                    }
                    break;
                default:
                    System.out.println(Colores.ROJO + "❌ Opción inválida.\n" + Colores.RESET);
            }
        }
    }

    // =================================== CU9 ===========================================
    public void darDeAltaHuesped() {
        System.out.println("\n" + Colores.CYAN + "╔════════════════════════════════════════════════════╗");
        System.out.println("║           📝 DAR DE ALTA HUÉSPED (CU9)             ║");
        System.out.println("╚════════════════════════════════════════════════════╝" + Colores.RESET);
        System.out.println(Colores.AMARILLO + " ℹ️  Nota: Escriba 'CANCELAR' para salir." + Colores.RESET + "\n");

        boolean continuarCargando = true;

        while (continuarCargando) {
            DtoHuesped datosIngresados = null;
            try {
                datosIngresados = mostrarYPedirDatosFormulario();
            } catch (CancelacionException e) {
                System.out.println(Colores.ROJO + "❌ Carga cancelada." + Colores.RESET);
                return;
            }

            boolean decisionPendiente = true;
            while (decisionPendiente) {
                System.out.println(Colores.CYAN + "\n────────── Fin del Formulario ──────────" + Colores.RESET);
                System.out.println(Colores.VERDE + "   [1]" + Colores.RESET + " 💾 GUARDAR");
                System.out.println(Colores.ROJO  + "   [2]" + Colores.RESET + " ❌ CANCELAR");
                System.out.print(">> Opción: ");

                int opcionBoton = leerOpcionNumerica();

                if (opcionBoton == 1) {
                    // VALIDACIÓN DE NEGOCIO (SERVICE)
                    List<String> errores = huespedService.validarDatosHuesped(datosIngresados);

                    if (!errores.isEmpty()) {
                        System.out.println(Colores.ROJO + "❌ ERROR DE VALIDACIÓN:" + Colores.RESET);
                        errores.forEach(err -> System.out.println(Colores.ROJO + "  • " + err + Colores.RESET));
                        decisionPendiente = false;
                        continue;
                    }

                    try {
                        // CHEQUEO DUPLICADOS (SERVICE)
                        Huesped duplicado = huespedService.chequearDuplicado(datosIngresados);

                        if (duplicado != null) {
                            System.out.println(Colores.AMARILLO + "\n⚠️  ADVERTENCIA: Ya existe un huésped con ese documento." + Colores.RESET);
                            System.out.println("Huésped: " + duplicado.getApellido() + " " + duplicado.getNombres());
                            System.out.println("   [1] Sobreescribir datos");
                            System.out.println("   [2] Corregir documento");
                            System.out.print(">> Opción: ");

                            int opDup = leerOpcionNumerica();
                            if (opDup == 2) {
                                // Lógica simple para corregir solo documento
                                try {
                                    TipoDocumento nuevoTipo = pedirTipoDocumento();
                                    String nuevoDoc = pedirDocumento(nuevoTipo, false);
                                    datosIngresados.setTipoDocumento(nuevoTipo);
                                    datosIngresados.setNroDocumento(nuevoDoc);
                                    continue; // Revalidar
                                } catch (CancelacionException ex) {
                                    System.out.println("Cancelado.");
                                    break;
                                }
                            }
                        }

                        // GUARDAR (SERVICE)
                        huespedService.upsertHuesped(datosIngresados);
                        System.out.println("\n" + Colores.VERDE + "✅ ¡Huésped guardado exitosamente!" + Colores.RESET);

                        System.out.print(Colores.CYAN + "\n🔄 ¿Cargar otro? (SI/NO): " + Colores.RESET);
                        if (!scanner.nextLine().trim().equalsIgnoreCase("SI")) {
                            continuarCargando = false;
                        }
                        decisionPendiente = false;

                    } catch (Exception e) {
                        System.out.println(Colores.ROJO + "❌ Error al guardar: " + e.getMessage() + Colores.RESET);
                        decisionPendiente = false;
                    }

                } else if (opcionBoton == 2) {
                    System.out.println("Cancelado.");
                    continuarCargando = false;
                    decisionPendiente = false;
                }
            }
        }
    }

    // =================================== CU2 ===========================================
    public void buscarHuesped() {
        System.out.println("\n" + Colores.CYAN + "╔════════════════════════════════════════════════════╗");
        System.out.println("║           🔎 BÚSQUEDA DE HUÉSPED (CU2)             ║");
        System.out.println("╚════════════════════════════════════════════════════╝" + Colores.RESET);

        DtoHuesped criterios = solicitarCriteriosDeBusqueda();
        System.out.println(Colores.AZUL + "\n🔄 Buscando..." + Colores.RESET);

        // USAMOS EL SERVICE
        List<Huesped> encontrados = huespedService.buscarHuespedes(criterios);

        if (encontrados.isEmpty()) {
            System.out.println(Colores.AMARILLO + "\n⚠️  No se encontraron huéspedes." + Colores.RESET);
            System.out.print("¿Dar de alta nuevo? (SI/NO): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("SI")) {
                this.darDeAltaHuesped();
            }
        } else {
            mostrarListaDatosEspecificos(new ArrayList<>(encontrados));
            seleccionarHuespedDeLista(new ArrayList<>(encontrados));
        }
        pausa();
    }


    private void seleccionarHuespedDeLista(ArrayList<Huesped> lista) {
        System.out.println("\nIngrese ID para editar o 0 para cancelar.");
        System.out.print(">> Selección: ");
        int sel = leerOpcionNumerica();

        if (sel > 0 && sel <= lista.size()) {
            // Huesped h = lista.get(sel - 1);
            System.out.println(Colores.CYAN + "🚧 Funcionalidad de Edición (CU10) en progreso..." + Colores.RESET);
        }
    }

    // =================================== CU4 ===========================================
    public void reservarHabitacion() throws Exception {
        System.out.println("\n" + Colores.CYAN + "╔════════════════════════════════════════════════════╗");
        System.out.println("║           🛏️  RESERVAR HABITACIÓN (CU4)            ║");
        System.out.println("╚════════════════════════════════════════════════════╝" + Colores.RESET);

        // Llamada interna a mostrar estado (reutilización)
        Map<Habitacion, Map<Date, String>> grilla = mostrarEstadoHabitaciones();
        if (grilla == null) return;

        List<DtoReserva> listaReservas = new ArrayList<>();
        boolean seguir = true;

        while (seguir) {
            System.out.println(Colores.AMARILLO + "\n--- Nueva Selección ---" + Colores.RESET);

            // Lógica simplificada de selección (puedes copiar tu lógica de validación de inputs aquí)
            System.out.print("Ingrese Nro Habitación: ");
            String nro = scanner.nextLine().trim();

            Habitacion hab = habitacionService.obtenerPorNumero(nro);
            if(hab == null) {
                System.out.println("Habitación no existe.");
                continue;
            }

            // Pedir fechas (simplificado, usar tus métodos pedirFechaEntre...)
            Date inicio = pedirFecha();
            Date fin = pedirFecha();

            // Validaciones de Negocio (SERVICE)
            if (!habitacionService.validarRangoFechas(inicio, fin)) continue;

            if (reservaService.estaReservadaEnFecha(nro, inicio, fin) ||
                    estadiaService.estaOcupadaEnFecha(nro, inicio, fin)) {
                System.out.println(Colores.ROJO + "❌ Habitación no disponible." + Colores.RESET);
                continue;
            }

            System.out.print("Apellido Responsable: ");
            String ape = scanner.nextLine();
            System.out.print("Nombre Responsable: ");
            String nom = scanner.nextLine();
            System.out.print("Teléfono: ");
            String tel = scanner.nextLine();

            listaReservas.add(new DtoReserva.Builder()
                    .idHabitacion(nro)
                    .fechaDesde(inicio)
                    .fechaHasta(fin)
                    .apellidoResponsable(ape)
                    .nombreResponsable(nom)
                    .telefonoResponsable(tel)
                    .build());

            System.out.print("¿Agregar otra? (SI/NO): ");
            if(!scanner.nextLine().equalsIgnoreCase("SI")) seguir = false;
        }

        if(!listaReservas.isEmpty()) {
            try {
                // USAMOS EL SERVICE
                reservaService.crearReservas(listaReservas);
                System.out.println(Colores.VERDE + "✅ Reservas creadas con éxito." + Colores.RESET);
            } catch (Exception e) {
                System.out.println(Colores.ROJO + "Error: " + e.getMessage() + Colores.RESET);
            }
        }
    }

    // =================================== CU5 ===========================================
    public Map<Habitacion, Map<Date, String>> mostrarEstadoHabitaciones() {
        System.out.println("\n" + Colores.CYAN + "--- Estado de Habitaciones ---" + Colores.RESET);

        // Fechas hardcodeadas para ejemplo rápido, usar tus métodos pedirFecha
        LocalDate hoy = LocalDate.now();
        Date inicio = Date.from(hoy.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date fin = Date.from(hoy.plusDays(15).atStartOfDay(ZoneId.systemDefault()).toInstant());

        // USAMOS SERVICES PARA OBTENER DATOS
        List<Habitacion> habitaciones = habitacionService.obtenerTodas();
        List<DtoReserva> reservas = reservaService.buscarReservasEnFecha(inicio, fin);
        List<DtoEstadia> estadias = estadiaService.buscarEstadiasEnFecha(inicio, fin);

        // Generar Grilla (Lógica de presentación, se mantiene igual que tu código original)
        // ... (Tu lógica de generarGrillaEstados e imprimirGrilla va aquí) ...

        System.out.println("(Grilla generada con " + habitaciones.size() + " habitaciones)");
        return new HashMap<>(); // Retorno dummy para que compile, usar tu mapa real
    }

    // =================================== CU15 ==========================================
    public void ocuparHabitacion() throws Exception {
        System.out.println("\n" + Colores.CYAN + "╔════════════════════════════════════════════════════╗");
        System.out.println("║           🔑 OCUPAR HABITACIÓN (Check-In)          ║");
        System.out.println("╚════════════════════════════════════════════════════╝" + Colores.RESET);

        // 1. Pedir Habitación y Fechas
        System.out.print("Nro Habitación: ");
        String nro = scanner.nextLine();
        Date in = pedirFecha();
        Date out = pedirFecha();

        // 2. Validar Disponibilidad (Service)
        if (estadiaService.estaOcupadaEnFecha(nro, in, out)) {
            System.out.println(Colores.ROJO + "Habitación ocupada." + Colores.RESET);
            return;
        }

        // 3. Cargar Huéspedes
        ArrayList<DtoHuesped> huespedes = seleccionarGrupoHuespedes(); // Tu método existente
        if (huespedes.isEmpty()) return;

        // 4. Crear DTO
        DtoHabitacion habDto = MapearHabitacion.mapearEntidadADto(habitacionService.obtenerPorNumero(nro));
        DtoEstadia dtoEstadia = new DtoEstadia.Builder()
                .dtoHabitacion(habDto)
                .fechaCheckIn(in)
                .fechaCheckOut(out)
                .valorEstadia(1000.0) // Obtener de la habitación
                .dtoHuespedes(huespedes)
                .build();

        try {
            // USAMOS EL SERVICE
            estadiaService.crearEstadia(dtoEstadia);
            System.out.println(Colores.VERDE + "✅ Check-In realizado." + Colores.RESET);
        } catch (Exception e) {
            System.out.println(Colores.ROJO + "Error: " + e.getMessage() + Colores.RESET);
        }
    }

    // --- MÉTODOS AUXILIARES (Tus métodos privados originales van aquí abajo) ---
    // (Copiar pegar: pedirStringTexto, pedirFecha, mostrarListaDatosEspecificos, etc.)
    // ...

    private Date pedirFecha() {
        // Placeholder simple
        return new Date();
    }
    private int leerOpcionNumerica() {
        try {
            // Leemos toda la línea. Esto captura el "Enter" vacío.
            String input = scanner.nextLine().trim();

            // Si dio Enter sin escribir nada, devolvemos -1 (inválido)
            if (input.isEmpty()) {
                return -1;
            }

            // Intentamos convertir a entero
            return Integer.parseInt(input);

        } catch (NumberFormatException e) {
            return -1; // Si escribió letras o símbolos, devolvemos -1 (inválido)
        }
    }


    private DtoHuesped mostrarYPedirDatosFormulario() throws CancelacionException {
        // Tu lógica original de formulario
        return new DtoHuesped();
    }

    private DtoHuesped solicitarCriteriosDeBusqueda() {
        // Tu lógica original
        return new DtoHuesped();
    }

    private void mostrarListaDatosEspecificos(ArrayList<Huesped> lista) {
        // Tu lógica original de tabla
        lista.forEach(h -> System.out.println(h.getApellido() + " " + h.getNombres()));
    }



    private TipoDocumento pedirTipoDocumento() throws CancelacionException {
        // Tu lógica
        return TipoDocumento.DNI;
    }

    private String pedirDocumento(TipoDocumento t, boolean opc) throws CancelacionException {
        // Tu lógica
        return "123";
    }

    private ArrayList<DtoHuesped> seleccionarGrupoHuespedes() {
        ArrayList<DtoHuesped> lista = new ArrayList<>();
        boolean seguir = true;

        while (seguir) {
            int opcionSeleccionada = 1; // Por defecto "Cargar"

            // --- MENÚ CONDICIONAL (Responsable vs Acompañantes) ---
            if (lista.isEmpty()) {
                // CASO 1: PRIMER HUÉSPED (Responsable) - No preguntamos, vamos directo al grano
                System.out.println("\n" + Colores.AMARILLO + "╔════════════════════════════════════════════════════╗");
                System.out.println("║ 👤 DATOS DEL RESPONSABLE (Titular)                 ║");
                System.out.println("╚════════════════════════════════════════════════════╝" + Colores.RESET);
                System.out.println(Colores.AZUL + "ℹ️  Ingrese los datos para buscar o dar de alta:" + Colores.RESET);

            } else {
                // CASO 2: ACOMPAÑANTES - Menú de decisión
                System.out.println("\n" + Colores.CYAN + "┌──────────────────────────────────────────────────┐");
                System.out.printf("│ 👥 SELECCIÓN DE ACOMPAÑANTE #%-2d                  │%n", (lista.size() + 1));
                System.out.println("└──────────────────────────────────────────────────┘" + Colores.RESET);
                System.out.println("   (Actual: " + lista.size() + " huéspedes cargados en esta habitación)");

                System.out.println(Colores.VERDE + "   [1]" + Colores.RESET + " ➕ Agregar otro acompañante");
                System.out.println(Colores.ROJO  + "   [2]" + Colores.RESET + " ✅ Finalizar carga y continuar");
                System.out.print("   >> Opción: ");

                opcionSeleccionada = leerOpcionNumerica();
            }

            // --- PROCESAR OPCIÓN ---
            if (opcionSeleccionada == 2) {
                break; // Terminar carga
            } else if (opcionSeleccionada != 1) {
                System.out.println(Colores.ROJO + "     ❌ Opción inválida." + Colores.RESET);
                continue;
            }

            // --- BLOQUE DE BÚSQUEDA Y SELECCIÓN ---
            DtoHuesped seleccionado = null;

            // 1. Pedir Criterios (Reutilizamos el método bonito del CU2)
            DtoHuesped criterios = solicitarCriteriosDeBusqueda();

            System.out.println(Colores.AZUL + "🔄 Buscando..." + Colores.RESET);
            ArrayList<Huesped> res = (ArrayList<Huesped>) huespedService.buscarHuespedes(criterios);

            if (res.isEmpty()) {
                System.out.println(Colores.AMARILLO + "\n⚠️  No encontrado." + Colores.RESET);
                System.out.print("¿Desea darlo de alta ahora? (SI/NO): ");
                if (scanner.nextLine().trim().equalsIgnoreCase("SI")) {
                    // Llamada al Alta
                    this.darDeAltaHuesped();
                    System.out.println(Colores.AZUL + "\nℹ️  Por favor, busque nuevamente al huésped recién creado para confirmarlo:" + Colores.RESET);
                    // Al hacer 'continue', el bucle vuelve a empezar y le pide los criterios de nuevo. Es un flujo natural.
                    continue;
                }
            } else {
                // Resultados encontrados
                if (res.size() == 1) {
                    // Coincidencia única
                    Huesped h = res.get(0);
                    System.out.println("\nSe encontró a: " + Colores.NEGRILLA + h.getApellido() + " " + h.getNombres() + Colores.RESET);
                    System.out.println("DNI: " + h.getNroDocumento());
                    System.out.print(Colores.VERDE + "¿Es correcto? (SI/NO): " + Colores.RESET);

                    if(scanner.nextLine().trim().equalsIgnoreCase("SI")){
                        seleccionado = MapearHuesped.mapearEntidadADto(h);
                    }
                } else {
                    // Múltiples coincidencias -> Tabla
                    mostrarListaDatosEspecificos(res);
                    System.out.print("\nIngrese ID a seleccionar (0 para cancelar): ");
                    int id = leerOpcionNumerica();
                    if (id > 0 && id <= res.size()) {
                        seleccionado = MapearHuesped.mapearEntidadADto(res.get(id - 1));
                    }
                }
            }

            // 3. Agregar a la lista temporal
            if (seleccionado != null) {
                // Verificar duplicado en la misma habitación
                DtoHuesped finalSeleccionado = seleccionado;
                boolean yaEsta = lista.stream().anyMatch(h -> h.getNroDocumento().equals(finalSeleccionado.getNroDocumento()));

                if (yaEsta) {
                    System.out.println(Colores.ROJO + "     ❌ Error: ¡Este huésped ya está en la lista!" + Colores.RESET);
                } else {
                    lista.add(seleccionado);
                    System.out.println(Colores.VERDE + "     ✅ Agregado: " + seleccionado.getApellido() + " " + seleccionado.getNombres() + Colores.RESET);
                }
            }
        }
        return lista;
    }

    private void pintarHabitacionOcupada(Map<Habitacion, Map<Date, String>> grilla,
                                         Date inicioOcupacion, Date finOcupacion,
                                         List<DtoEstadia> estadiasConfirmadas,
                                         Habitacion seleccionActual) {

        // VOLVEMOS AL FORMATO LARGO
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        if (grilla == null || grilla.isEmpty()) return;

        // 1. Obtener límites
        Date inicioGrilla = grilla.values().iterator().next().keySet().stream().min(Date::compareTo).orElse(new Date());
        Date finGrilla = grilla.values().iterator().next().keySet().stream().max(Date::compareTo).orElse(new Date());

        List<Habitacion> habitacionesOrdenadas = new ArrayList<>(grilla.keySet());
        habitacionesOrdenadas.sort(Comparator.comparing(Habitacion::getTipoHabitacion).thenComparing(Habitacion::getNumero));

        System.out.println(Colores.CYAN + "\n   === 🗓️  GRILLA ACTUALIZADA (PRE-VISUALIZACIÓN) ===" + Colores.RESET);

        // 2. ENCABEZADOS
        // Línea Superior (Ajustada para fecha larga)
        System.out.print("   ┌──────────────"); // Más ancho para dd/MM/yyyy
        for (int k = 0; k < habitacionesOrdenadas.size(); k++) System.out.print("┬───────────");
        System.out.println("┐");

        // Títulos de Columnas
        System.out.print("   │    FECHA     ");
        for (Habitacion hab : habitacionesOrdenadas) {
            String textoHab = "Hab " + hab.getNumero();

            // Resaltamos columna activa
            if (seleccionActual != null && hab.getNumero().equals(seleccionActual.getNumero())) {
                System.out.print("│" + Colores.VERDE + String.format(" %-9s ", textoHab) + Colores.RESET);
            } else {
                System.out.print("│ " + String.format("%-9s", textoHab) + " ");
            }
        }
        System.out.println("│");

        // Línea Divisoria
        System.out.print("   ├──────────────");
        for (int k = 0; k < habitacionesOrdenadas.size(); k++) System.out.print("┼───────────");
        System.out.println("┤");

        // 3. CUERPO DE LA GRILLA
        LocalDate inicioLocal = inicioGrilla.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate finLocal = finGrilla.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        LocalDate actual = inicioLocal;
        while (!actual.isAfter(finLocal)) {
            // Columna Fecha (Ancho 12 para que entre dd/MM/yyyy)
            System.out.printf("   │ %-12s ", actual.format(dtf));
            Date fechaFila = Date.from(actual.atStartOfDay(ZoneId.systemDefault()).toInstant());

            for (Habitacion hab : habitacionesOrdenadas) {
                String visual = "   ?   ";
                String color = Colores.RESET;
                boolean esSeleccion = false;

                // A. Selección Actual
                if (seleccionActual != null && hab.getNumero().equals(seleccionActual.getNumero())) {
                    if (inicioOcupacion != null && finOcupacion != null) {
                        if (!fechaFila.before(inicioOcupacion) && fechaFila.before(finOcupacion)) {
                            esSeleccion = true;
                        }
                    }
                }

                // B. Confirmadas previamente
                if (!esSeleccion && estadiasConfirmadas != null) {
                    for (DtoEstadia dto : estadiasConfirmadas) {
                        if (dto.getDtoHabitacion().getNumero().equals(hab.getNumero())) {
                            if (!fechaFila.before(dto.getFechaCheckIn()) && fechaFila.before(dto.getFechaCheckOut())) {
                                esSeleccion = true;
                                break;
                            }
                        }
                    }
                }

                if (esSeleccion) {
                    visual = "   * ";
                    color = Colores.VERDE;
                } else {
                    // C. Estado Base
                    Map<Date, String> mapa = grilla.get(hab);
                    String estado = (mapa != null) ? mapa.get(fechaFila) : "LIBRE";
                    if (estado == null) estado = "LIBRE";

                    // Mantenemos tus letras originales
                    switch (estado) {
                        case "OCUPADA" -> {
                            visual = "   X   ";
                            color = Colores.ROJO;
                        }
                        case "RESERVADA" -> {
                            visual = "   R   ";
                            color = Colores.AMARILLO;
                        }
                        case "FUERA DE SERVICIO" -> {
                            visual = "   -   ";
                            color = Colores.CYAN;
                        }
                        case "LIBRE" -> {
                            visual = "   L   ";
                            color = Colores.RESET;
                        }
                    }
                }
                System.out.print("│" + color + String.format("%-11s", visual) + Colores.RESET);
            }
            System.out.println("│");
            actual = actual.plusDays(1);
        }

        // Línea Inferior
        System.out.print("   └──────────────");
        for (int k = 0; k < habitacionesOrdenadas.size(); k++) System.out.print("┴───────────");
        System.out.println("┘");

        System.out.println("\n   REFERENCIAS:  [L]ibre | " + Colores.AMARILLO + "[R]eservada" + Colores.RESET + " | "
                + Colores.ROJO + "[X]Ocupada" + Colores.RESET + " | " + Colores.VERDE + "[*] Selección Actual" + Colores.RESET
                + " | " + Colores.CYAN + "[-]Fuera de servicio" + Colores.RESET);
    }

    // Metodo que imprime la fila superior con los TIPOS agrupados
    public void imprimirEncabezadoTipos(List<Habitacion> habitacionesOrdenadas) {
        // Padding inicial para alinearse con la columna "FECHA" de la grilla (13 espacios)
        String padding = "             ";

        // 1. LÍNEA SUPERIOR (Dibujamos el techo de las cajas)
        System.out.print(padding);
        int i = 0;
        while (i < habitacionesOrdenadas.size()) {
            Habitacion actual = habitacionesOrdenadas.get(i);
            int contador = 0;

            // Contamos ancho del grupo
            for (int j = i; j < habitacionesOrdenadas.size(); j++) {
                if (habitacionesOrdenadas.get(j).getTipoHabitacion() == actual.getTipoHabitacion()) contador++;
                else break;
            }

            // Dibujamos techo: ┌───────────┐ (ajustado al ancho)
            // Restamos 1 al ancho total porque el borde final de uno es el inicio del otro si queremos pegarlos,
            // pero para cajas separadas usaremos estilo limpio.
            // Ancho celda = 12. Ancho grupo = 12 * n.
            // Usamos borde simple cian.
            System.out.print(Colores.CYAN + "┌" + "─".repeat((contador * 12) - 1) + "┐" + Colores.RESET);

            i += contador;
        }
        System.out.println();

        // 2. LÍNEA DE TEXTO (Nombres de Tipos)
        System.out.print(padding);
        i = 0;
        while (i < habitacionesOrdenadas.size()) {
            Habitacion actual = habitacionesOrdenadas.get(i);
            String tipoActual = actual.getTipoHabitacion().getDescripcion();

            int contador = 0;
            for (int j = i; j < habitacionesOrdenadas.size(); j++) {
                if (habitacionesOrdenadas.get(j).getTipoHabitacion() == actual.getTipoHabitacion()) contador++;
                else break;
            }

            int anchoGrupo = contador * 12; // 12 caracteres por habitación

            // Imprimimos texto centrado entre bordes verticales │
            // Usamos -2 en el ancho para descontar los bordes "│" y "│" que simulan la caja
            System.out.print(Colores.CYAN + "│" + PantallaHelper.centrarTexto(tipoActual, anchoGrupo - 2) + "│" + Colores.RESET);

            i += contador;
        }
        System.out.println();

        // 3. LÍNEA INFERIOR (Cierre de las cajas)
        System.out.print(padding);
        i = 0;
        while (i < habitacionesOrdenadas.size()) {
            Habitacion actual = habitacionesOrdenadas.get(i);
            int contador = 0;
            for (int j = i; j < habitacionesOrdenadas.size(); j++) {
                if (habitacionesOrdenadas.get(j).getTipoHabitacion() == actual.getTipoHabitacion()) contador++;
                else break;
            }

            // Dibujamos piso: └───────────┘
            System.out.print(Colores.CYAN + "└" + "─".repeat((contador * 12) - 1) + "┘" + Colores.RESET);

            i += contador;
        }
        System.out.println();
    }

    private void pausa() {
        System.out.print("\n" + Colores.AMARILLO + "⏹️  Presione ENTER para continuar..." + Colores.RESET);
        scanner.nextLine();
        System.out.println();
    }


}*/