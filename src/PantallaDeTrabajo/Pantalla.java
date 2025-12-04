package PantallaDeTrabajo;
import Dominio.Habitacion;
import Dominio.Huesped;
import Estadia.DtoEstadia;
import Estadia.GestorEstadia;
import Habitacion.GestorHabitacion;
import Huesped.*;
import Reserva.DtoReserva;
import Reserva.GestorReserva;
import Utils.Colores;
import Utils.Mapear.MapearHuesped;
import enums.PosIva;
import enums.TipoDocumento;
import Usuario.*;
import Habitacion.DtoHabitacion;
import Utils.PantallaHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import Excepciones.PersistenciaException;

public class Pantalla {

    private final GestorHuesped gestorHuesped;
    private final Scanner scanner;//para la entrada por teclado
    private final GestorUsuario gestorUsuario;
    private final GestorHabitacion gestorHabitacion;
    private final GestorEstadia gestorEstadia;
    private final GestorReserva gestorReserva;
    private boolean usuarioAutenticado;
    private String nombreUsuarioActual;

    // Excepción interna para manejar la cancelación en cualquier momento
    private static class CancelacionException extends Exception {}


    //constructor
    public Pantalla() {
        this.gestorHabitacion = GestorHabitacion.getInstance();
        this.gestorEstadia = GestorEstadia.getInstance();
        this.gestorReserva = GestorReserva.getInstance();
        this.gestorHuesped = GestorHuesped.getInstance();
        this.gestorUsuario = GestorUsuario.getInstance();

        //inicializamos el scanner
        this.scanner = new Scanner(System.in);
        this.usuarioAutenticado = false;
        this.nombreUsuarioActual = "";
    }

    //METODO PRINCIPAL PARA INICIAR EL SISTEMA
    public void iniciarSistema() throws Exception {
        System.out.println(Colores.CYAN + "╔════════════════════════════════════════════════════╗");
        System.out.println("║         🏨 SISTEMA DE GESTION HOTELERA             ║");
        System.out.println("╚════════════════════════════════════════════════════╝" + Colores.RESET);
        System.out.println("");

        //Primero autenticar
        if (autenticarUsuario()) {
            //Si la autenticacion es exitosa, mostrar menu principal
            mostrarMenuPrincipal();
        } else {
            System.out.println(Colores.ROJO + "❌ No se pudo acceder al sistema." + Colores.RESET);
        }

        System.out.println("\n" + Colores.CYAN + "========================================");
        System.out.println("        👋 FIN DEL SISTEMA");
        System.out.println("========================================" + Colores.RESET);
    }

    //METODO PARA CU AUTENTICAR USUARIO
    private boolean autenticarUsuario() {
        System.out.println(Colores.NEGRILLA + "🔐 AUTENTICACION DE USUARIO" + Colores.RESET);
        System.out.println(Colores.CYAN + "   -------------------------" + Colores.RESET + "\n");

        boolean autenticacionExitosa = false;

        while (!autenticacionExitosa) {
            //Paso 2: El sistema presenta la pantalla para autenticar al usuario
            System.out.println("Por favor, ingrese sus credenciales:");

            //Paso 3: El actor ingresa su nombre (en forma visible) y su contraseña (oculta)
            System.out.print(Colores.VERDE + "   👤 Usuario: " + Colores.RESET);
            String nombre = scanner.nextLine().trim();

            System.out.print(Colores.VERDE + "   🔑 Contraseña: " + Colores.RESET);
            String contrasenia = scanner.nextLine(); //en consola no se puede ocultar realmente

            //Validar con el gestor
            boolean credencialesValidas = gestorUsuario.autenticarUsuario(nombre, contrasenia);

            if (credencialesValidas) {
                //Autenticacion exitosa
                this.usuarioAutenticado = true;
                this.nombreUsuarioActual = nombre;
                System.out.println("\n" + Colores.VERDE + "✅ ¡Autenticación exitosa! Bienvenido, " + nombre + Colores.RESET + "\n");
                autenticacionExitosa = true;
            } else {
                //Paso 3.A: El usuario o la contraseña son inválidos
                //Paso 3.A.1: El sistema muestra el mensaje de error
                System.out.println("\n" + Colores.ROJO + "╔═════════════════════════════════════════════╗");
                System.out.println("║ ❌ ERROR: Usuario o contraseña inválidos    ║");
                System.out.println("╚═════════════════════════════════════════════╝" + Colores.RESET + "\n");

                //Paso 3.A.2: El actor cierra la pantalla de error
                System.out.print("Presione " + Colores.NEGRILLA + "ENTER" + Colores.RESET + " para continuar...");
                System.out.print("\033[H\033" +
                        "[2J");
                System.out.flush();
                scanner.nextLine();

                //Paso 3.A.3: El sistema blanquea los campos (se hace automáticamente al repetir el ciclo)

                //Preguntar qué desea hacer
                System.out.println("\n¿Qué desea hacer?");
                System.out.println(Colores.AMARILLO + " [1]" + Colores.RESET + " 🔄 Volver a ingresar credenciales");
                System.out.println(Colores.AMARILLO + " [2]" + Colores.RESET + " 🚪 Cerrar el sistema");
                System.out.print(">> Ingrese una opción: ");

                int opcion;
                try {
                    opcion = scanner.nextInt();
                    scanner.nextLine(); //consumir salto de linea
                } catch (Exception e) {
                    scanner.nextLine(); //limpiar buffer
                    System.out.println(Colores.ROJO + "\n⚠️ Opción inválida. Intente nuevamente.\n" + Colores.RESET);
                    continue;
                }

                if (opcion == 2) {
                    System.out.println("\nCerrando el sistema...");
                    return false; //Sale sin autenticar
                } else if (opcion == 1) {
                    System.out.println(Colores.AZUL + "\n-- Intente nuevamente --\n" + Colores.RESET);
                    //Paso 3.A.4: El CU continua en el paso 2 (se repite el while)
                } else {
                    System.out.println(Colores.ROJO + "\n⚠️ Opción inválida. Intente nuevamente.\n" + Colores.RESET);
                }
            }
        }

        return true;
    }

    //METODO PARA MOSTRAR MENU PRINCIPAL
    private void mostrarMenuPrincipal() throws Exception {
        //Paso 4: El sistema presenta la pantalla principal
        boolean salir = false;

        while (!salir && usuarioAutenticado) {
            System.out.println("\n" + Colores.CYAN + "╔════════════════════════════════════════════════════╗");
            System.out.println("║                MENU PRINCIPAL                      ║");
            System.out.println("╚════════════════════════════════════════════════════╝" + Colores.RESET);

            // Datos del usuario con ícono
            System.out.println(Colores.VERDE + "   👤 Usuario activo: " + Colores.NEGRILLA + nombreUsuarioActual + Colores.RESET);
            System.out.println(Colores.CYAN + "   ──────────────────────────────────────────────────" + Colores.RESET);

            // Opciones con colores y emojis
            System.out.println(Colores.AMARILLO + "   [1]" + Colores.RESET + " 🔍 Buscar huésped (CU2)");
            System.out.println(Colores.AMARILLO + "   [2]" + Colores.RESET + " 🛏️  Reservar Habitación (CU4)");
            System.out.println(Colores.AMARILLO + "   [3]" + Colores.RESET + " 📝 Dar de alta huésped (CU9)");
            System.out.println(Colores.AMARILLO + "   [4]" + Colores.RESET + " 🗑️  Dar de baja huésped (CU11)");
            System.out.println(Colores.AMARILLO + "   [5]" + Colores.RESET + " 🚪 Cerrar sesión");

            System.out.println(Colores.CYAN + "======================================================" + Colores.RESET);
            System.out.print(">> Ingrese una opción: ");

            int opcion = -1;
            try {
                // CORRECCIÓN: Leemos toda la línea como String
                String entrada = scanner.nextLine().trim();

                // Si dió Enter vacío, lanzamos error manualmente para que caiga en el catch
                if (entrada.isEmpty()) {
                    throw new NumberFormatException();
                }

                // Intentamos convertir a entero
                opcion = Integer.parseInt(entrada);

            } catch (NumberFormatException e) {
                // Captura tanto texto no numérico como el Enter vacío
                System.out.println(Colores.ROJO + "\n❌ Opción inválida. Debe ingresar un número.\n" + Colores.RESET);
                continue; // Vuelve a mostrar el menú
            }

            System.out.println();

            switch (opcion) {
                case 1:
                    buscarHuesped();
                    break;
                case 2:
                    reservarHabitacion();
                    break;
                case 3:
                    darDeAltaHuesped();
                    break;
                case 4:
                    //iniciarBajaHuesped();
                    break;
                case 5:
                    System.out.print(Colores.AMARILLO + "⚠️  ¿Está seguro que desea cerrar sesión? (SI/NO): " + Colores.RESET);
                    String confirmar = scanner.nextLine().trim();
                    if (confirmar.equalsIgnoreCase("SI")) {
                        System.out.println(Colores.AZUL + "\n👋 Cerrando sesión...\n" + Colores.RESET);
                        salir = true;
                        usuarioAutenticado = false;
                    }
                    break;
                default:
                    System.out.println(Colores.ROJO + "❌ Opción inválida. Intente nuevamente.\n" + Colores.RESET);
            }
        }
        //Paso 5: El CU termina
    }

    // CU9
    public void darDeAltaHuesped() {
        //Mensaje de principio de ejecucion del CU9 con Estética de Título
        System.out.println("\n" + Colores.CYAN + "╔════════════════════════════════════════════════════╗");
        System.out.println("║           📝 DAR DE ALTA HUÉSPED (CU9)             ║");
        System.out.println("╚════════════════════════════════════════════════════╝" + Colores.RESET);
        System.out.println(Colores.AMARILLO + " ℹ️  Nota: Escriba 'CANCELAR' en cualquier campo para salir." + Colores.RESET + "\n");

        boolean continuarCargando = true; //bandera que representa la condicion del loop principal

        // [BUCLE 1]: Controla el ciclo completo de carga.
        // Se repite cada vez que el usuario termina de cargar un huésped y responde "SI" a "¿Desea cargar otro?".
        while (continuarCargando) {

            DtoHuesped datosIngresados = null;

            // 1. INTENTO DE CARGA DE DATOS (creamos una excepción para manejar la opcion de CANCELAR en cualquier momento del formulario)
            //Envolvemos la carga en un try-catch para capturar la cancelación
            try {
                //metodo Pantalla -> Conserje para mostrar formulario y pedir datos
                datosIngresados = mostrarYPedirDatosFormulario();
            } catch (CancelacionException e) {
                // Si el usuario escribió "CANCELAR" durante el formulario:
                System.out.print(Colores.ROJO + "\n🛑 ¿Está seguro que desea cancelar la carga actual? (SI/NO): " + Colores.RESET);
                String confir = scanner.nextLine();
                if (confir.equalsIgnoreCase("SI")) {
                    System.out.println(Colores.ROJO + "❌ Carga cancelada. Volviendo al menú principal..." + Colores.RESET);
                    return; // Sale del metodo completamente
                } else {
                    System.out.println(Colores.AZUL + "🔄 Reiniciando formulario..." + Colores.RESET);
                    continue; // Vuelve al inicio del while (Lamentablemente reinicia el form, es complejo reanudar en consola)
                }
            }

            // 2. MENU DE DECISIÓN (Siguiente / Cancelar)
            // Agregamos este bucle 'decisionPendiente' para no perder datos al cancelar
            boolean decisionPendiente = true;

            // [BUCLE 2]: Menú de Acciones Post-Formulario.
            // Mantiene al usuario en la pantalla de decisión ("Siguiente" o "Cancelar") hasta que elija una opción válida.
            // Evita que el programa se cierre si el usuario se equivoca al elegir una opción.
            while (decisionPendiente) {
                System.out.println(Colores.CYAN + "\n────────── Fin del Formulario ──────────" + Colores.RESET);
                System.out.println("Acciones disponibles:");
                System.out.println(Colores.VERDE + "   [1]" + Colores.RESET + " 💾 GUARDAR / SIGUIENTE");
                System.out.println(Colores.ROJO  + "   [2]" + Colores.RESET + " ❌ CANCELAR OPERACIÓN");
                System.out.print(">> Ingrese una opción: ");

                int opcionBoton = -1;
                try {//validacion mas robusta
                    String entrada = scanner.nextLine();
                    opcionBoton = Integer.parseInt(entrada);
                } catch (NumberFormatException e) {
                    System.out.println(Colores.ROJO + "⚠️  Error: Debe ingresar un número." + Colores.RESET);
                    continue;
                }

                if (opcionBoton == 1) { // presiono SIGUIENTE
                    System.out.println(Colores.AZUL + "⏳ Procesando datos..." + Colores.RESET);

                    //aca hay que llamar al gestor para que valide los datos
                    List<String> errores;
                    //Metodo que retorna una lista de todos los errores en la validacion de negocio
                    errores = gestorHuesped.validarDatosHuesped(datosIngresados);

                    //Actuamos en consecuencia, dependiendo si hubo errores o no
                    if (!errores.isEmpty()) {
                        System.out.println(Colores.ROJO + "\n╔══════════════════════════════════════════╗");
                        System.out.println("║ ❌ ERROR DE VALIDACIÓN DE DATOS          ║");
                        System.out.println("╚══════════════════════════════════════════╝" + Colores.RESET);
                        for (String error : errores) {
                            System.out.println(Colores.ROJO + "  • " + error + Colores.RESET);
                        }
                        System.out.println("\nPor favor, ingrese los datos nuevamente.");
                        decisionPendiente = false;//Salimos del bucle de decisión para recargar datos
                        continue; //fuerza al inicio del while principal
                    }

                    //Si no hubo errores de validacion de negocio, seguimos
                    try {
                        boolean verificacionPendiente = true;

                        // [BUCLE 3]: Verificación y Corrección de Duplicados.
                        // Este bucle permite que, si el usuario elige "CORREGIR", se pidan de nuevo SOLO los datos conflictivos
                        // y se vuelva a verificar la duplicidad sin perder el resto de la información cargada.
                        while (verificacionPendiente) {

                            //Debemos fijarnos en la DB si existe un Huesped con el mismo TipoDoc y NroDoc que el ingresado
                            DtoHuesped duplicado = gestorHuesped.chequearDuplicado(datosIngresados);
                            //Si chequearDuplicado retorna NULL, no hay duplicado

                            if (duplicado != null) {//si encuentra duplicado
                                // Caja amarilla de advertencia
                                System.out.println("\n" + Colores.AMARILLO + "╔══════════════════════════════════════════════════════════════╗");
                                System.out.println("║ ⚠️  ADVERTENCIA DE DUPLICADO                                 ║");
                                System.out.println("╠══════════════════════════════════════════════════════════════╣");
                                System.out.println("║ El tipo y número de documento ya existen en el sistema.      ║");
                                System.out.println("║ Huésped existente: " + String.format("%-41s", duplicado.getNombres() + " " + duplicado.getApellido()) + " ║");
                                System.out.println("╚══════════════════════════════════════════════════════════════╝" + Colores.RESET);

                                //Parámetros para bucle interno de decisión
                                int opcionDuplicado = -1;
                                boolean opcionValida2 = false;

                                // [BUCLE 4]: Menú de Resolución de Duplicados.
                                // Valida que el usuario elija 1 o 2 correctamente.
                                while (!opcionValida2) {
                                    System.out.println("Opciones:");
                                    System.out.println(Colores.AMARILLO + "   [1]" + Colores.RESET + " ACEPTAR IGUALMENTE (Sobreescribir/Actualizar)");
                                    System.out.println(Colores.AMARILLO + "   [2]" + Colores.RESET + " CORREGIR DATOS (Solo documento)");
                                    System.out.print(">> Ingrese una opción: ");

                                    try {
                                        String entrada = scanner.nextLine();
                                        opcionDuplicado = Integer.parseInt(entrada);

                                        if (opcionDuplicado == 1 || opcionDuplicado == 2) {
                                            opcionValida2 = true; // Salimos del bucle de validación
                                        } else {
                                            System.out.println(Colores.ROJO + "⚠️ Opción inválida." + Colores.RESET);
                                        }
                                    } catch (NumberFormatException e) {
                                        System.out.println(Colores.ROJO + "⚠️ Debe ingresar un número." + Colores.RESET);
                                    }
                                }

                                if (opcionDuplicado == 2) { // Eligió CORREGIR
                                    System.out.println(Colores.AZUL + "\n📝 Ingrese los nuevos datos de identificación:" + Colores.RESET);

                                    // Pedimos solo los campos conflictivos
                                    try {
                                        TipoDocumento nuevoTipo = pedirTipoDocumento();
                                        String nuevoDoc = pedirDocumento(nuevoTipo);

                                        // Actualizamos el DTO existente (Mantenemos nombre, dir, etc)
                                        datosIngresados.setTipoDocumento(nuevoTipo);
                                        datosIngresados.setNroDocumento(nuevoDoc);

                                        System.out.println(Colores.AZUL + "🔄 Re-verificando duplicados..." + Colores.RESET);
                                        continue; // Vuelve al inicio del Bucle 3 para verificar de nuevo
                                    } catch (CancelacionException e) {
                                        System.out.println(Colores.ROJO + "Corrección cancelada. Volviendo al menú anterior..." + Colores.RESET);
                                        // Si cancela la corrección, volvemos a mostrar la advertencia
                                        continue;
                                    }
                                }
                                // Si elige 1 (ACEPTAR IGUALMENTE), salimos del bucle 3 y guardamos
                                verificacionPendiente = false;

                            } else {
                                // Si no hay duplicados, salimos del bucle 3 y guardamos
                                verificacionPendiente = false;
                            }
                        } // Fin bucle verificacionPendiente

                        //Si no existen duplicados (o se aceptaron), INSERT/UPDATE
                        gestorHuesped.upsertHuesped(datosIngresados);
                        System.out.println("\n" + Colores.VERDE + "✅ ¡El huésped ha sido guardado exitosamente!" + Colores.RESET);

                        // AQUÍ VA LA LOGICA DE CARGAR OTRO (Dentro del éxito del alta)
                        System.out.print(Colores.CYAN + "\n🔄 ¿Desea cargar otro huésped? (SI/NO): " + Colores.RESET);

                        //validacion de ingreso correcto
                        String ingresoOtroHuesped = scanner.nextLine();
                        while (!ingresoOtroHuesped.equalsIgnoreCase("NO") && !ingresoOtroHuesped.equalsIgnoreCase("SI")) {
                            System.out.print(Colores.ROJO + "⚠️ Ingreso inválido. " + Colores.RESET + "¿Desea cargar otro huésped? (SI/NO): ");
                            ingresoOtroHuesped = scanner.nextLine();
                        }

                        //si ingreso NO termina el bucle principal, si ingreso SI se repite
                        if (ingresoOtroHuesped.equalsIgnoreCase("NO")) {
                            continuarCargando = false;
                        } else {
                            System.out.println(Colores.AZUL + "\n--- Nuevo Formulario ---\n" + Colores.RESET);
                        }
                        decisionPendiente = false; // Salimos del bucle de decisión ya que terminamos

                    } catch (PersistenciaException e) {
                        System.out.println(Colores.ROJO + "❌ ERROR DE BASE DE DATOS: " + e.getMessage() + Colores.RESET);
                        e.printStackTrace();
                        decisionPendiente = false; // Volver a empezar
                    }

                } else if (opcionBoton == 2) { // presiono CANCELAR
                    System.out.print(Colores.ROJO + "¿Realmente desea cancelar el alta del huésped? (SI/NO): " + Colores.RESET);

                    //validación de ingreso correcto
                    String ingresoCancelarAlta = scanner.nextLine();
                    while (!ingresoCancelarAlta.equalsIgnoreCase("NO") && !ingresoCancelarAlta.equalsIgnoreCase("SI")) {
                        System.out.print("Ingreso invalido. ¿Desea cancelar? (SI/NO): ");
                        ingresoCancelarAlta = scanner.nextLine();
                    }

                    if (ingresoCancelarAlta.equalsIgnoreCase("SI")) {
                        System.out.println(Colores.ROJO + "❌ Alta cancelada." + Colores.RESET);
                        continuarCargando = false;//termina el bucle principal
                        decisionPendiente = false; // Sale del bucle de decisión
                    } else {
                        // El bucle 'decisionPendiente' se repite y vuelve a mostrar "Acciones: 1=SIGUIENTE..."
                        // Los datos NO se pierden.
                        System.out.println(Colores.AZUL + "Regresando al menú de acciones..." + Colores.RESET);
                    }
                } else {
                    System.out.println(Colores.ROJO + "Opción inválida." + Colores.RESET);
                }
            } // Fin while decisionPendiente
        } // Fin while continuarCargando

        System.out.println(Colores.CYAN + "--- Fin CU9 'Dar de alta huésped' ---" + Colores.RESET + "\n");
    }


    //metodo privado para pedir los datos del huesped a dar de alta, CU9 (formulario)
    private DtoHuesped mostrarYPedirDatosFormulario() throws CancelacionException {

        // Encabezado del Formulario
        System.out.println(Colores.CYAN + "\n   ┌──────────────────────────────────────────────────┐");
        System.out.println("   │         📝 FORMULARIO DE REGISTRO                │");
        System.out.println("   └──────────────────────────────────────────────────┘" + Colores.RESET);

        //Cada uno de estos métodos solicita por teclado el ingreso de cada campo del formulario
        //Además, se hace una VALIDACIÓN DE FORMATO (que el email tenga @, que el DNI sean números, que la fecha sea válida)
        //en el momento, evitando datos sin sentido

        //Las validaciones de negocio las realizará el Gestor
        // Todos los métodos 'pedir...' pueden lanzar la excepción si el usuario escribe "CANCELAR"

        // --- SECCIÓN 1: DATOS PERSONALES ---
        System.out.println(Colores.AMARILLO + "\n   === 👤 DATOS PERSONALES ===" + Colores.RESET);

        // Agregamos colores y sangría (espacios) a los mensajes
        String apellido = pedirStringTexto(Colores.VERDE + "   > Apellido: " + Colores.RESET);

        String nombres = pedirStringTexto(Colores.VERDE + "   > Nombres: " + Colores.RESET);

        // Asumo que este metodo imprime su propio menú, así que solo lo llamamos

        TipoDocumento tipoDocumento = pedirTipoDocumento();

        String numeroDocumento = pedirDocumento(tipoDocumento);

        // Posición IVA
        String posIva = pedirPosIva();

        // CUIT (Opcional)
        String cuit = pedirCUIT(posIva);

        Date fechaNacimiento = pedirFecha();

        String nacionalidad = pedirStringTexto(Colores.VERDE + "   > Nacionalidad: " + Colores.RESET);

        String ocupacion = pedirStringTexto(Colores.VERDE + "   > Ocupación: " + Colores.RESET);


        // --- SECCIÓN 2: DOMICILIO ---
        System.out.println(Colores.AMARILLO + "\n   === 🏠 DOMICILIO ===" + Colores.RESET);

        String calleDireccion = pedirStringComplejo(Colores.VERDE + "   > Calle: " + Colores.RESET);

        Integer numeroDireccion = pedirEntero(Colores.VERDE + "   > Número: " + Colores.RESET);

        String pisoDireccion = pedirStringOpcional(Colores.VERDE + "   > Piso " + Colores.CYAN + "(Opcional)" + Colores.VERDE + ": " + Colores.RESET);

        String departamentoDireccion = pedirStringOpcional(Colores.VERDE + "   > Departamento " + Colores.CYAN + "(Opcional)" + Colores.VERDE + ": " + Colores.RESET);

        Integer codPostalDireccion = pedirEntero(Colores.VERDE + "   > Código Postal: " + Colores.RESET);

        String localidadDireccion = pedirStringComplejo(Colores.VERDE + "   > Localidad: " + Colores.RESET);

        String provinciaDireccion = pedirStringComplejo(Colores.VERDE + "   > Provincia: " + Colores.RESET);

        String paisDireccion = pedirStringTexto(Colores.VERDE + "   > País: " + Colores.RESET);


        // --- SECCIÓN 3: CONTACTO ---
        System.out.println(Colores.AMARILLO + "\n   === 📞 CONTACTO ===" + Colores.RESET);

        Long telefono = pedirTelefono(); // Asumo que dentro pide el dato con su propio mensaje, o podemos pasarle uno si el método lo permite

        String email = pedirEmail();


        //casteo los wrappers (necesarios para las validaciones) a primitivos para su posterior uso en la app
        int numeroDireccionPrimitivo = numeroDireccion;
        int codPostalDireccionPrimitivo = codPostalDireccion;

        // Crear los DTO (aún no tenemos el ID de dirección, no fuimos a la DB todavia, se inicia en NULL por defecto en la clase)
        // Crear DtoDireccion usando Builder
        DtoDireccion direccionDto = new DtoDireccion.Builder(calleDireccion, numeroDireccionPrimitivo, localidadDireccion, provinciaDireccion, paisDireccion)
                .departamento(departamentoDireccion)
                .piso(pisoDireccion)
                .codPostal(codPostalDireccionPrimitivo)
                .build();
        //Creamos el DtoHuesped usando el Builder
        DtoHuesped huespedDto = new DtoHuesped.Builder()
                .nombres(nombres)
                .apellido(apellido)
                .telefono(Collections.singletonList(telefono))
                .tipoDocumento(tipoDocumento)
                .documento(numeroDocumento)
                .cuit(cuit)
                .posicionIva(posIva != null ? PosIva.fromString(posIva) : null)
                .fechaNacimiento(fechaNacimiento)
                .email(Collections.singletonList(email))
                .ocupacion(Collections.singletonList(ocupacion))
                .nacionalidad(nacionalidad)
                .direccion(direccionDto)
                .build();

        //asociamos la direccion con el huesped
        huespedDto.setDtoDireccion(direccionDto);

        System.out.println(Colores.CYAN + "\n   ──────────────────────────────────────────────────");
        System.out.println("   ✅ Datos recolectados correctamente");
        System.out.println("   ──────────────────────────────────────────────────" + Colores.RESET);

        return huespedDto; // Devolver el DTO con los datos cargados (incluyendo la direccion correspondiente)
    }


    //Metodo auxiliar clave para verificar cancelación
    private void chequearCancelacion(String input) throws CancelacionException {
        // Si el input no es nulo y es "CANCELAR" (ignorando mayúsculas), lanzamos la excepción
        if (input != null && input.trim().equalsIgnoreCase("CANCELAR")) {
            throw new CancelacionException();
        }
    }

//=== Metodos para pedir Y VALIDAR cada tipo de dato, CU9 ===

    //Solicitar y Validar String complejo (calle, provincia, localidad)
    private String pedirStringComplejo(String mensaje) throws CancelacionException {
        String entrada;
        while (true) {
            System.out.print(mensaje);
            entrada = scanner.nextLine();

            chequearCancelacion(entrada);

            if (entrada.trim().isEmpty()) {
                System.out.println(Colores.ROJO + "     ❌ Error: Este campo es obligatorio." + Colores.RESET);
            } else if (!entrada.matches("^[\\p{L}0-9 ]+$")) { // Letras Unicode + Números + Espacios
                System.out.println(Colores.ROJO + "     ❌ Error: Solo se admiten letras, números y espacios." + Colores.RESET);
            } else {
                return entrada.trim();
            }
        }
    }

    //Solicitar y Validar String simple (nombres, apellidos, pais)
    private String pedirStringTexto(String mensaje) throws CancelacionException {
        String entrada;
        while (true) {
            System.out.print(mensaje);
            entrada = scanner.nextLine();

            chequearCancelacion(entrada);

            if (entrada.trim().isEmpty()) {//Validamos obligatoriedad del campo
                System.out.println(Colores.ROJO + "     ❌ Error: Este campo es obligatorio." + Colores.RESET);

                // Esta expresion ^[\p{L} ]+$ permite cualquier letra de cualquier idioma
                // y espacios, pero no números ni caracteres especiales.
            } else if (!entrada.matches("^[\\p{L} ]+$")) {//cualquier letra Unicode
                System.out.println(Colores.ROJO + "     ❌ Error: Solo se admiten letras y espacios." + Colores.RESET);

            } else {
                return entrada.trim();//Elimina los caracteres de espacio en blanco al principio y al final de la cadena
            }
        }
    }

    //Solicitar y Validar String opcional (dpto, piso)
    private String pedirStringOpcional(String mensaje) throws CancelacionException {
        String entrada;
        // La expresion permite letras (a-z, A-Z), números (0-9) y espacios.
        String str = "^[a-zA-Z0-9 ]+$";

        while (true) {
            System.out.print(mensaje);
            entrada = scanner.nextLine();

            chequearCancelacion(entrada);

            //Si está vacío, es válido (opcional)
            if (entrada.trim().isEmpty()) {
                return null;

                //Si no está vacío, valida el formato
            } else if (!entrada.matches(str)) {
                System.out.println(Colores.ROJO + "     ❌ Error: Solo letras, números y espacios." + Colores.RESET);

            } else {
                return entrada;
            }
        }
    }

    private Integer pedirEntero(String mensaje) throws CancelacionException {
        Integer valor = null; // Usamos la clase wrapper para permitir null
        boolean valido = false;

        while (!valido) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim(); // leemos siempre como String

            chequearCancelacion(entrada);

            if (entrada.isEmpty()) {
                System.out.println(Colores.ROJO + "     ❌ Error: Este campo es obligatorio." + Colores.RESET);
                continue;
            }
            try {
                int num = Integer.parseInt(entrada);
                if (num <= 0) {
                    System.out.println(Colores.ROJO + "     ❌ Error: Ingrese un número positivo." + Colores.RESET);
                } else {
                    valor = num;
                    valido = true;
                }
            } catch (NumberFormatException e) {
                System.out.println(Colores.ROJO + "     ❌ Error: Debe ingresar un número entero válido." + Colores.RESET);
            }
        }
        return valor;
    }

    private Long pedirTelefono() throws CancelacionException {
        Long valor = null;
        boolean valido = false;

        // Regex: Números, espacios, guiones, más y paréntesis
        String regexTelefono = "^[0-9+() -]+$";

        while (!valido) {
            // Prompt con color verde
            System.out.print(Colores.VERDE + "   > Teléfono: " + Colores.RESET);
            String entrada = scanner.nextLine().trim();

            chequearCancelacion(entrada);

            if (entrada.isEmpty()) {
                System.out.println(Colores.ROJO + "     ❌ Error: El teléfono es obligatorio." + Colores.RESET);
                continue;
            }

            if (!entrada.matches(regexTelefono)) {
                System.out.println(Colores.ROJO + "     ❌ Error: Caracteres inválidos. Use números, espacios, guiones, '+' o '()'." + Colores.RESET);
                continue;
            }

            // --- LIMPIEZA DE DATOS ---
            // Antes de convertir a Long, le sacamos el ruido que pueda haber ingresado el usuario, buscando estandarizar
            // Reemplazamos todo lo que NO sea número ("[^0-9]") por nada ("")
            String soloNumeros = entrada.replaceAll("[^0-9]", "");

            try {
                if (soloNumeros.isEmpty()) {
                    System.out.println(Colores.ROJO + "     ❌ Error: No ingresó ningún número." + Colores.RESET);
                    continue;
                }
                valor = Long.parseLong(soloNumeros);

                // Validación de longitud entre 6 y 15 números
                if (soloNumeros.length() < 6 || soloNumeros.length() > 15) {
                    System.out.println(Colores.ROJO + "     ❌ Error: El número parece demasiado corto o largo (6-15 dígitos)." + Colores.RESET);
                } else {
                    valido = true;
                }
            } catch (NumberFormatException e) {
                System.out.println(Colores.ROJO + "     ❌ Error: El número es demasiado largo para el sistema." + Colores.RESET);
            }
        }
        return valor;
    }

    private String pedirCUIT(String posIvaSeleccionada) throws CancelacionException {
        String cuit;
        String expresionCUIT = "^\\d{2}-\\d{8}-\\d$";

        // Verificamos si es Responsable Inscripto usando el Enum
        boolean esResponsableInscripto = posIvaSeleccionada != null &&
                posIvaSeleccionada.equals(PosIva.ResponsableInscripto.name());

        while (true) {
            // Cambiamos el mensaje según la obligatoriedad
            if (esResponsableInscripto) {
                System.out.print(Colores.VERDE + "   > CUIT " + Colores.ROJO + "(Obligatorio por ser Resp. Inscripto)" + Colores.VERDE + ": " + Colores.RESET);
            } else {
                System.out.print(Colores.VERDE + "   > CUIT " + Colores.CYAN + "(Opcional)" + Colores.VERDE + ": " + Colores.RESET);
            }

            cuit = scanner.nextLine().trim();
            chequearCancelacion(cuit);

            // CASO 1: Está vacío
            if (cuit.isEmpty()) {
                if (esResponsableInscripto) {
                    //No dejamos avanzar si es RI y no pone CUIT
                    System.out.println(Colores.ROJO + "     ❌ Error: El CUIT es obligatorio para Responsables Inscriptos." + Colores.RESET);
                } else {
                    return null; // Es válido que sea null (será Factura B)
                }

                // CASO 2: Escribió algo, validamos formato
            } else if (!cuit.matches(expresionCUIT)) {
                System.out.println(Colores.ROJO + "     ❌ Error: Formato incorrecto. Debe ser XX-XXXXXXXX-X" + Colores.RESET);
            } else {
                return cuit;
            }
        }
    }

    private String pedirEmail() throws CancelacionException {
        String email;
        // expresion simple para emails: algo@algo.algo
        String expresionEmail = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

        while (true) {
            // Prompt con "(Opcional)" destacado
            System.out.print(Colores.VERDE + "   > Email " + Colores.CYAN + "(Opcional)" + Colores.VERDE + ": " + Colores.RESET);
            email = scanner.nextLine();

            chequearCancelacion(email);

            if (email.trim().isEmpty()) {
                return null; // Válido (opcional)

            } else if (!email.matches(expresionEmail)) {
                System.out.println(Colores.ROJO + "     ❌ Error: Formato de email no válido." + Colores.RESET);

            } else {
                return email; // Válido
            }
        }
    }

    private Date pedirFecha() throws CancelacionException {
        Date fecha = null;
        boolean valida = false;
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        formatoFecha.setLenient(false);

        while (!valida) {
            System.out.print(Colores.VERDE + "   > Fecha de Nacimiento (dd/MM/yyyy): " + Colores.RESET);
            String fechaStr = scanner.nextLine();
            chequearCancelacion(fechaStr);
            if (fechaStr.trim().isEmpty()) {
                System.out.println(Colores.ROJO + "     ❌ Error: Este campo es obligatorio." + Colores.RESET);
            } else {
                try {
                    fecha = formatoFecha.parse(fechaStr);
                    // Convertir a LocalDate para comparar solo la fecha (sin hora)
                    LocalDate fechaLocal = Instant.ofEpochMilli(fecha.getTime())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    LocalDate hoy = LocalDate.now();
                    LocalDate fechaMinima = LocalDate.of(1900, 1, 1); // posterior a 31/12/1899

                    // Validar que sea anterior a hoy y posterior al 31/12/1899
                    if (!fechaLocal.isBefore(hoy) || fechaLocal.isBefore(fechaMinima)) {

                        System.out.println(Colores.ROJO + "     ❌ Error: La fecha debe ser anterior a hoy y posterior a 1900." + Colores.RESET);
                        continue;
                    }
                    valida = true; // Formato válido
                } catch (ParseException e) {
                    System.out.println(Colores.ROJO + "     ❌ Error: Formato de fecha inválido. Use dd/MM/yyyy." + Colores.RESET);
                }
            }
        }
        return fecha;
    }

    private TipoDocumento pedirTipoDocumento() throws CancelacionException {
        TipoDocumento tipoDoc = null;
        boolean valido = false;

        // Construimos las opciones con un formato más limpio: [DNI / PASAPORTE / ...]
        // Usamos Cyan para las opciones para que se diferencien del texto de la pregunta
        StringBuilder opciones = new StringBuilder(Colores.CYAN + "[");
        TipoDocumento[] valores = TipoDocumento.values();
        for (int i = 0; i < valores.length; i++) {
            opciones.append(valores[i].name());
            if (i < valores.length - 1) {
                opciones.append(" / ");
            }
        }
        opciones.append("]" + Colores.RESET);

        while (!valido) {
            // Prompt en Verde + Opciones en Cyan
            System.out.print(Colores.VERDE + "   > Tipo de Documento " + opciones + Colores.VERDE + ": " + Colores.RESET);

            String tipoDocStr = scanner.nextLine().toUpperCase().trim();
            chequearCancelacion(tipoDocStr);

            if (tipoDocStr.isEmpty()) {
                System.out.println(Colores.ROJO + "     ❌ Error: El tipo de documento es obligatorio." + Colores.RESET);
            } else {
                try {
                    tipoDoc = TipoDocumento.valueOf(tipoDocStr);
                    valido = true;
                } catch (IllegalArgumentException e) {
                    System.out.println(Colores.ROJO + "     ❌ Error: Tipo inválido. Copie una de las opciones mostradas." + Colores.RESET);
                }
            }
        }
        return tipoDoc;
    }

    private String pedirDocumento(TipoDocumento tipo) throws CancelacionException {
        String NroDocumento = null;
        boolean valido = false;

        // Definimos las reglas (Regex)
        String regexNumerico = "^\\d{7,8}$";
        String regexPasaporte = "^[A-Z0-9]{6,15}$";
        String regexOtro = "^.{4,20}$";

        while (!valido) {
            // Prompt en Verde
            System.out.print(Colores.VERDE + "   > Número de Documento: " + Colores.RESET);
            String entrada = scanner.nextLine().trim().toUpperCase();

            chequearCancelacion(entrada);

            if (entrada.isEmpty()) {
                System.out.println(Colores.ROJO + "     ❌ Error: El documento es obligatorio." + Colores.RESET);
                continue;
            }

            // Validamos según el tipo seleccionado
            switch (tipo) {
                case DNI:
                case LE:
                case LC:
                    if (entrada.matches(regexNumerico)) {
                        valido = true;
                    } else {
                        System.out.println(Colores.ROJO + "     ❌ Error: Para " + tipo + " debe ingresar entre 7 y 8 números." + Colores.RESET);
                    }
                    break;
                case PASAPORTE:
                    if (entrada.matches(regexPasaporte)) {
                        valido = true;
                    } else {
                        System.out.println(Colores.ROJO + "     ❌ Error: Formato de Pasaporte inválido (solo letras y números)." + Colores.RESET);
                    }
                    break;
                default: // OTRO
                    if (entrada.matches(regexOtro)) {
                        valido = true;
                    } else {
                        System.out.println(Colores.ROJO + "     ❌ Error: Formato inválido." + Colores.RESET);
                    }
                    break;
            }

            if (valido) {
                NroDocumento = entrada;
            }
        }
        return NroDocumento;
    }

    private String pedirPosIva() throws CancelacionException {
        String posIva = null;
        boolean valido = false;

        while (!valido) {
            // Transformamos el bloque de texto en un menú visualmente agradable
            System.out.println(Colores.VERDE + "   > Posición frente al IVA:" + Colores.RESET);
            System.out.println(Colores.AMARILLO + "      [1]" + Colores.RESET + " Consumidor Final (Por defecto)");
            System.out.println(Colores.AMARILLO + "      [2]" + Colores.RESET + " Monotributista");
            System.out.println(Colores.AMARILLO + "      [3]" + Colores.RESET + " Responsable Inscripto");
            System.out.println(Colores.AMARILLO + "      [4]" + Colores.RESET + " Exento");
            System.out.print(Colores.VERDE + "     >> Selección: " + Colores.RESET);

            try {
                int opcion = 0;
                String entrada = scanner.nextLine();

                chequearCancelacion(entrada);

                // Si da enter, es 0 (default)
                if (!entrada.isBlank()) {
                    opcion = Integer.parseInt(entrada);
                }

                switch (opcion) {
                    case 0: // Caso Enter vacío
                    case 1:
                        posIva = PosIva.ConsumidorFinal.name();
                        valido = true;
                        // Feedback visual de la selección por defecto
                        if(opcion == 0) System.out.println(Colores.CYAN + "        (Seleccionado: Consumidor Final)" + Colores.RESET);
                        break;
                    case 2:
                        posIva = PosIva.Monotributista.name();
                        valido = true;
                        break;
                    case 3:
                        posIva = PosIva.ResponsableInscripto.name();
                        valido = true;
                        break;
                    case 4:
                        posIva = PosIva.Exento.name();
                        valido = true;
                        break;
                    default:
                        System.out.println(Colores.ROJO + "     ❌ Error: Opción inválida." + Colores.RESET);
                }
            } catch (NumberFormatException e) {
                System.out.println(Colores.ROJO + "     ❌ Error: Debe ingresar un número." + Colores.RESET);
            }
        }
        return posIva;
    }

    //==== FIN METODOS CU9 ====


    //METODO AUXILIAR PARA PAUSAR
    public void pausa() {
        System.out.print("Presione ENTER para continuar...");
        scanner.nextLine();
        System.out.println();
    }

    //CU2
    public void buscarHuesped() {
        System.out.println("========================================");
        System.out.println("        BÚSQUEDA DE HUÉSPED 🔎");
        System.out.println("========================================");

        DtoHuesped dtoHuespedCriterios = solicitarCriteriosDeBusqueda();
        // CAMBIO: El gestor ahora devuelve ArrayList<Huesped>
        ArrayList<Huesped> huespedesEncontrados = gestorHuesped.buscarHuespedes(dtoHuespedCriterios);

        if (huespedesEncontrados.isEmpty()) {
            System.out.println("\nNo se encontraron huéspedes con los criterios especificados.");
            this.darDeAltaHuesped(); //CU 9
        } else {
            // CAMBIO: Llamamos a seleccionarHuespedDeLista con DtoHuesped
            mostrarListaDatosEspecificos(huespedesEncontrados);
            this.seleccionarHuespedDeLista(huespedesEncontrados);
        }
        pausa();
    }

    private DtoHuesped solicitarCriteriosDeBusqueda() {
        DtoHuesped criterios = new DtoHuesped();
        System.out.println("Ingrese uno o más criterios (presione ENTER para omitir).");

        // VALIDACIÓN DE APELLIDO
        while (true) {
            System.out.print("Apellido que comience con: ");
            String apellido = scanner.nextLine().trim();

            if (apellido.isEmpty()) {
                break; // Usuario omite este criterio
            }

            // Validar longitud
            if (apellido.length() >= 2) {
                System.out.println("⚠ Inserte solo la letra con la que comienza el apellido.");
                continue;
            }


            // Validar que solo contenga letras, espacios y caracteres válidos (á, é, í, ó, ú, ñ)
            if (!apellido.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
                System.out.println("⚠ El apellido solo puede contener letras y espacios. Intente nuevamente.");
                continue;
            }

            criterios.setApellido(apellido);
            break;
        }

        // VALIDACIÓN DE NOMBRES
        while (true) {
            System.out.print("Nombres que comiencen con: ");
            String nombres = scanner.nextLine().trim();

            if (nombres.isEmpty()) {
                break; // Usuario omite este criterio
            }

            // Validar longitud
            if (nombres.length() >= 2) {
                System.out.println("⚠ Inserte solo la letra con la que comienza el nombre.");
                continue;
            }


            // Validar que solo contenga letras, espacios y caracteres válidos
            if (!nombres.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
                System.out.println("⚠ Los nombres solo pueden contener letras y espacios. Intente nuevamente.");
                continue;
            }

            criterios.setNombres(nombres);
            break;
        }

        // VALIDACIÓN DE TIPO DE DOCUMENTO (sin cambios, ya está bien)
        criterios.setTipoDocumento(validarYLeerTipoDocumento());

        // VALIDACIÓN DE NÚMERO DE DOCUMENTO
        if (criterios.getTipoDocumento() != null) {
            criterios.setNroDocumento(pedirDocumentoSinExcepcion(criterios.getTipoDocumento()));
        }

        return criterios;
    }


    private TipoDocumento validarYLeerTipoDocumento() {
        while (true) {
            System.out.print("Tipo de Documento (DNI, Pasaporte, Libreta de Enrolamiento (LE), Libreta Civica(LC)): ");
            String tipoStr = scanner.nextLine().trim().toUpperCase();
            if (tipoStr.isEmpty()) {
                return null; // El usuario omitió este criterio.
            }
            try {
                return TipoDocumento.valueOf(tipoStr); // Intenta convertir el String al enum.
            } catch (IllegalArgumentException e) {
                System.out.println("Error: Tipo de documento no válido. Los valores posibles son DNI, PASAPORTE, LIBRETA DE ENROLAMIENTO Y LIBRETA CIVICA.");
            }
        }
    }//NO SE DE QUE SON ESTOS METODOS


    private void seleccionarHuespedDeLista(ArrayList<Huesped> listaDtoHuespedes) {

        // CAMBIO: Mensaje para CU10
        System.out.print("Ingrese el ID del huésped para **modificar/eliminar**, o 0 para dar de alta uno nuevo: ");
        int seleccion = leerOpcionNumerica();
        System.out.print("SIGUIENTE: presione cualquier botón...");
        scanner.nextLine();
        System.out.println();

        //Mapear lista entidades a dto
        ArrayList<DtoHuesped> listaHuespedes = new ArrayList<>();
        for (int i = 0; i < listaDtoHuespedes.size(); i++) {
            listaHuespedes.add(i, MapearHuesped.mapearEntidadADto(listaDtoHuespedes.get(i)));
        }

        //Sigue el flujo
        if (seleccion > 0 && seleccion <= listaDtoHuespedes.size()) {
            DtoHuesped huespedDtoSeleccionado = listaHuespedes.get(seleccion - 1);
            Huesped huespedSeleccionado = gestorHuesped.crearHuespedSinPersistir(huespedDtoSeleccionado);
            System.out.println("FUNCIONALIDAD CASO DE USO 10 EN PROGRESO...");
        } else if (seleccion == 0) {
            this.darDeAltaHuesped(); // CU 9
        } else {
            System.out.println("Opción inválida. Volviendo al menú principal.");
        }
    }

    private void mostrarListaDatosEspecificos(ArrayList<Huesped> listaHuespedes) {

        System.out.println("\n--- OPCIONES DE ORDENAMIENTO ---");
        System.out.println("Seleccione la columna:");
        System.out.println("1. Apellido");
        System.out.println("2. Nombre");
        System.out.println("3. Tipo de Documento");
        System.out.println("4. Número de Documento");
        System.out.print("Ingrese opción: ");

        int columna = leerOpcionNumerica();
        if (columna < 1 || columna > 4) {
            System.out.println("Opción inválida. No se ordenará la lista.");
        }

        System.out.println("Seleccione el orden:");
        System.out.println("1. Ascendente (A-Z / Menor a Mayor)");
        System.out.println("2. Descendente (Z-A / Mayor a Menor)");
        System.out.print("Ingrese opción: ");

        int orden = leerOpcionNumerica();
        boolean ascendente = (orden == 1);

        // Definimos el comparador para el DTO Huesped
        Comparator<Huesped> comparador = switch (columna) {
            case 1 -> // Apellido
                    Comparator.comparing(Huesped::getApellido, String.CASE_INSENSITIVE_ORDER);
            case 2 -> // Nombre
                    Comparator.comparing(Huesped::getNombres, String.CASE_INSENSITIVE_ORDER);
            case 3 -> // Tipo de Documento (Enum)
                    Comparator.comparing(h -> h.getTipoDocumento() != null ? h.getTipoDocumento().name() : "Z"); // Si es null, lo mandamos al final
            case 4 -> // Número de Documento (String en DTO)
                    Comparator.comparing(Huesped::getNroDocumento);
            default -> null; // Si es inválido, no se ordena
        };

        if (comparador != null) {
            if (!ascendente) {
                comparador = comparador.reversed();
            }
            // Sort en la lista de DtoHuesped
            listaHuespedes.sort(comparador);
        }

        System.out.println("\n-- Huéspedes Encontrados --");
        System.out.printf("%-5s %-20s %-20s %s%n", "ID", "APELLIDO", "NOMBRES", "DOCUMENTO");
        System.out.println("-----------------------------------------------------------------");
        //Por cada huesped obtenemos los 4 datos necesarios y mostramos esos.
        for (int i = 0; i < listaHuespedes.size(); i++) {
            Huesped h = listaHuespedes.get(i);
            String tipoDoc = (h.getTipoDocumento() != null ? h.getTipoDocumento().name() : "N/A");
            String docCompleto = tipoDoc + " " + (h.getNroDocumento() != null ? h.getNroDocumento() : "");
            System.out.printf("[%d]   %-20s %-20s %s%n", i + 1, h.getApellido(), h.getNombres(), docCompleto);
        }
        System.out.println("-----------------------------------------------------------------");
    }

    private int leerOpcionNumerica() {
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            return -1; // Devuelve un valor inválido si el usuario no ingresa un número
        } finally {
            scanner.nextLine(); // Limpia el buffer del scanner
        }
    }

    /**
     * METODO ORQUESTADOR
     * Coordina los 3 gestores para construir la matriz de estados.
     */
    private Map<Habitacion, Map<Date, String>> generarGrillaEstados(Date fechaInicio, Date fechaFin) {

        // 1. Pantalla pide habitaciones al GestorHabitacion
        ArrayList<Habitacion> habitaciones = gestorHabitacion.obtenerTodas();

        // Estructura para guardar los estados: Habitación -> (Fecha -> Estado)
        // Usamos LinkedHashMap
        Map<Habitacion, Map<Date, String>> grilla = new LinkedHashMap<>();

        // Ordenamos las habitaciones
        habitaciones.sort(Comparator.comparing(Habitacion::getTipoHabitacion)
                            .thenComparing(Habitacion::getNumero));

        // 2. Pantalla itera y consulta a los otros Gestores
        int contador = 1;
        for (Habitacion dtoHab : habitaciones) {
            Map<Date, String> estadosDia = new HashMap<>();

            // Iterar por día dentro del rango
            LocalDate inicio = fechaInicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate fin = fechaFin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            for (LocalDate date = inicio; !date.isAfter(fin); date = date.plusDays(1)) {
                Date fechaActual = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());

                String estado = "LIBRE"; // Estado por defecto

                // A. Verificar estado de la habitación
                if (dtoHab.getEstadoHabitacion() != null &&
                        "FUERA_DE_SERVICIO".equals(dtoHab.getEstadoHabitacion().name())) {
                    estado = "FUERA DE SERVICIO";
                }
                // B. Pantalla pregunta a GestorEstadia
                else if (gestorEstadia.estaOcupadaEnFecha(dtoHab.getNumero(), fechaActual, fechaActual)) {
                    estado = "OCUPADA";
                }
                // C. Pantalla pregunta a GestorReserva
                else if (gestorReserva.estaReservadaEnFecha(dtoHab.getNumero(), fechaActual, fechaActual)) {
                    estado = "RESERVADA";
                }

                estadosDia.put(fechaActual, estado);
            }
            grilla.put(dtoHab, estadosDia);
            System.out.println("Habitacion " + dtoHab.getNumero() + " procesada" );
        }
        return grilla;
    }

    // CU4: Reservar Habitación
    public void reservarHabitacion() throws Exception {
        System.out.println("\n--- INICIO CU4: RESERVA ---");

        // 1. LLAMADA AL CU5
        Map<Habitacion, Map<Date, String>> grilla = mostrarEstadoHabitaciones();

        if (grilla == null) return; // Si falló el CU5 o canceló

        // Recuperamos las fechas del mapa para validaciones posteriores
        Date fechaInicio = grilla.values().iterator().next().keySet().stream().min(Date::compareTo).orElse(new Date());
        Date fechaFin = grilla.values().iterator().next().keySet().stream().max(Date::compareTo).orElse(new Date());

        List<DtoReserva> seleccion = new ArrayList<>();

        // 2. Bucle de Selección
        while (true) {
            System.out.println("\n¿Desea seleccionar una habitación? (SI/NO): ");
            if (!scanner.nextLine().trim().equalsIgnoreCase("SI")) break;

            System.out.print("Ingrese Nro Habitación: ");
            String nro = scanner.nextLine().trim().toUpperCase();

            // Buscar habitación en el mapa (que actúa como caché de lo que vemos)
            Habitacion habSeleccionada = null;
            for (Habitacion h : grilla.keySet()) {
                if (h.getNumero().equals(nro)) {
                    habSeleccionada = h;
                    break;
                }
            }

            if (habSeleccionada == null) {
                System.out.println("Error: Habitación no encontrada en la lista actual.");
                continue;
            }

            // Validar disponibilidad consultando el MAPA
            boolean disponible = true;
            Map<Date, String> estados = grilla.get(habSeleccionada);

            for (String estado : estados.values()) {
                if (!"LIBRE".equals(estado)) {
                    disponible = false;
                    break;
                }
            }

            // Validar duplicado en selección actual
            boolean yaEnLista = seleccion.stream().anyMatch(r -> r.getIdHabitacion().equals(nro));

            if (!disponible || yaEnLista) {
                System.out.println("ERROR: La habitación no está 100% disponible en el rango o ya fue seleccionada.");
            } else {
                DtoReserva dto = new DtoReserva.Builder()
                        .idHabitacion(nro)
                        .fechaDesde(fechaInicio)
                        .fechaHasta(fechaFin)
                        .build();
                seleccion.add(dto);
                System.out.println(">> Habitación " + nro + " agregada.");
            }
        }

        if (seleccion.isEmpty()) {
            System.out.println("Finalizando sin reservas.");
            return;
        }

        // 3. ACTUALIZAR VISUALIZACIÓN (Pintar selección sobre la grilla base)
        imprimirGrilla(grilla, fechaInicio, fechaFin, seleccion);

        // ... Lógica de Confirmación y Persistencia (Igual que tenías) ...
        gestorReserva.crearReservas(seleccion);
    }

    private void imprimirGrilla(Map<Habitacion, Map<Date, String>> grilla, Date inicio, Date fin, List<DtoReserva> seleccion) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formatoCelda = "| %-9s ";

        System.out.println("\n--- GRILLA DE DISPONIBILIDAD ---");

        // Convertimos el KeySet a lista para poder recorrerla ordenadamente en el header
        List<Habitacion> habitacionesOrdenadas = new ArrayList<>(grilla.keySet());

        // 1. IMPRIMIR ENCABEZADO AGRUPADO POR TIPO (NUEVO)
        imprimirEncabezadoTipos(habitacionesOrdenadas);

        // 2. Imprimir fila de Números de Habitación
        System.out.print("   FECHA     ");
        for (Habitacion hab : habitacionesOrdenadas) {
            System.out.printf(formatoCelda, "Hab " + hab.getNumero());
        }
        System.out.println("|");

        // Línea separadora simple
        System.out.print("-------------");
        for (int k=0; k<habitacionesOrdenadas.size(); k++) System.out.print("+-----------");
        System.out.println("+");


        // 3. Filas (Días) - El cuerpo de la grilla sigue igual
        LocalDate inicioLocal = inicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate finLocal = fin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        LocalDate actual = inicioLocal;
        while (!actual.isAfter(finLocal)) {
            System.out.printf("%-12s ", actual.format(dtf)); // Fecha
            Date fechaFila = Date.from(actual.atStartOfDay(ZoneId.systemDefault()).toInstant());

            for (Habitacion hab : habitacionesOrdenadas) {
                String visual = "   ?   ";
                String color = Colores.RESET;

                // Lógica de visualización (Selección vs Estado BDD)
                boolean esSeleccion = false;
                if (seleccion != null) {
                    for (DtoReserva res : seleccion) {
                        if (res.getIdHabitacion().equals(hab.getNumero())) {
                            esSeleccion = true; break;
                        }
                    }
                }

                if (esSeleccion) {
                    visual = "   * ";
                    color = Colores.VERDE; // Verde para lo que está seleccionando el usuario
                } else {
                    Map<Date, String> mapaEstados = grilla.get(hab);
                    String estado = (mapaEstados != null) ? mapaEstados.get(fechaFila) : "LIBRE";
                    if (estado == null) estado = "LIBRE";

                    color = switch (estado) {
                        case "OCUPADA" -> {
                            visual = "   X   ";
                            yield Colores.ROJO;
                        }
                        case "RESERVADA" -> {
                            visual = "   R   ";
                            yield Colores.AMARILLO;
                        }
                        case "FUERA DE SERVICIO" -> {
                            visual = "   -   ";
                            yield Colores.CYAN;
                        }
                        case "LIBRE" -> {
                            visual = "   L   ";
                            yield Colores.RESET;
                        }
                        default -> color;
                    };
                }
                System.out.print("|" + color + String.format(" %-9s ", visual.trim()) + Colores.RESET);
            }
            System.out.println("|");
            actual = actual.plusDays(1);
        }
        System.out.println("REF: [L]ibre | " + Colores.AMARILLO + "[R]eservada" + Colores.RESET + " | "
                + Colores.ROJO + "[X]Ocupada" + Colores.RESET + " | " + Colores.VERDE + "[*] Tu Selección" + Colores.RESET
                + Colores.CYAN + "[-]Fuera de servicio" + Colores.RESET);
    }

    // CU5: Mostrar Estado de Habitaciones
    // Retorna el mapa con los datos para que el CU4 pueda reutilizarlos
    public Map<Habitacion, Map<Date, String>> mostrarEstadoHabitaciones() throws CancelacionException {
        System.out.println("========================================");
        System.out.println("   CU5: MOSTRAR ESTADO HABITACIONES");
        System.out.println("========================================\n");

        //chequeo de fechas
        boolean flagFechas = false;
        Date fechaInicio = null;
        Date fechaFin = null;

        while(!flagFechas) {
            // 1. Pedir y Validar Fechas (Bucle del diagrama)
            Date fechaReferencia = new Date(Long.MIN_VALUE);
            fechaInicio = pedirFechaFutura("Desde fecha dd/mm/aaaa ", fechaReferencia );
            fechaFin = pedirFechaFutura("Hasta Fecha dd/mm/aaaa ", fechaInicio);

            // Validar lógica de negocio (Rango coherente)
            flagFechas = gestorHabitacion.validarRangoFechas(fechaInicio, fechaFin);
        }
        System.out.println("\nProcesando estados...");

        // 2. ORQUESTACIÓN: Generar la grilla llamando a los gestores
        Map<Habitacion, Map<Date, String>> grilla = generarGrillaEstados(fechaInicio, fechaFin);

        if (grilla.isEmpty()) {
            System.out.println("No hay habitaciones registradas en el sistema.");
            return null;
        }

        // 3. Visualización (Pintar la grilla base sin selección)
        imprimirGrilla(grilla, fechaInicio, fechaFin, null);

        return grilla; // Retornamos los datos para que CU4 los use
    }


    private Date pedirFechaFutura(String mensaje, Date fechaInicio) throws CancelacionException {
            Date fecha = null;
            boolean valida = false;
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
            formatoFecha.setLenient(false);

            while (!valida) {
                System.out.print(Colores.VERDE + mensaje + Colores.RESET);
                String fechaStr = scanner.nextLine();
                chequearCancelacion(fechaStr);
                if (fechaStr.trim().isEmpty()) {
                    System.out.println(Colores.ROJO + "     ❌ Error: Este campo es obligatorio." + Colores.RESET);
                } else {
                    try {
                        fecha = formatoFecha.parse(fechaStr);
                        // Convertir a LocalDate para comparar solo la fecha (sin hora)
                        LocalDate fechaLocal = Instant.ofEpochMilli(fecha.getTime())
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();
                        LocalDate hoy = LocalDate.now();
                        LocalDate fechaMinima = LocalDate.of(1900, 1, 1); // posterior a 31/12/1899

                        if(fecha.after(fechaInicio)){
                            valida = true; //FechaFin después de FechaInicio
                        }else {
                            System.out.println(Colores.ROJO + "     ❌ Error: La Fecha de Fin de la selección debe ser futura a la de Inicio." + Colores.RESET);
                        }
                    } catch (ParseException e) {
                        System.out.println(Colores.ROJO + "     ❌ Error: Formato de fecha inválido. Use dd/MM/yyyy." + Colores.RESET);
                    }
                }

            }


            return fecha;
        }



    // --- CU15: OCUPAR HABITACIÓN ---
    public void ocuparHabitacion() throws Exception {
        System.out.println("========================================");
        System.out.println("   CU15: OCUPAR HABITACIÓN (CHECK-IN)");
        System.out.println("========================================\n");

        // 1. Mostrar Grilla (CU5)
        Map<Habitacion, Map<Date, String>> grilla = mostrarEstadoHabitaciones();
        if (grilla == null) return;

        Date fechaInicio = grilla.values().iterator().next().keySet().stream().min(Date::compareTo).orElse(new Date());
        Date fechaFin = grilla.values().iterator().next().keySet().stream().max(Date::compareTo).orElse(new Date());

        List<DtoEstadia> estadiasParaProcesar = new ArrayList<>();
        boolean deseaCargarOtra = true;

        // Loop Principal
        while (deseaCargarOtra) {
            Habitacion habSeleccionada = null;

            // Loop Selección Habitación
            while (habSeleccionada == null) {
                System.out.print("\nIngrese Nro Habitación a Ocupar: ");
                String nro = scanner.nextLine().trim().toUpperCase();

                // Obtener seleccion
                Habitacion candidata = null;
                for (Habitacion h : grilla.keySet()) {
                    if (h.getNumero().equals(nro)) {
                        candidata = h;
                        break;
                    }
                }

                if (candidata == null) {
                    System.out.println("Error: Habitación no encontrada.");
                    continue;
                }

                // Validar Estado
                Map<Date, String> estados = grilla.get(candidata);
                String estado = estados.get(fechaInicio);
                if (estado == null) estado = "LIBRE";

                if ("OCUPADA".equals(estado) || "FUERA DE SERVICIO".equals(estado)) {
                    System.out.println("Error: Habitación " + estado + ". Elija otra.");
                    habSeleccionada = null;
                } else if ("RESERVADA".equals(estado)) {
                    System.out.println("AVISO: Habitación RESERVADA. 1. OCUPAR IGUAL / 2. VOLVER");
                    if (leerOpcionNumerica() == 1){
                        habSeleccionada = candidata;
                        pintarHabitacionOcupada(grilla, fechaInicio, fechaFin, estadiasParaProcesar, habSeleccionada);
                    }
                } else {
                    habSeleccionada = candidata;
                    pintarHabitacionOcupada(grilla, fechaInicio, fechaFin, estadiasParaProcesar, habSeleccionada);
                }

            }


            // HUESPEDES
            System.out.println(">> Cargando huéspedes para Habitación " + habSeleccionada.getNumero() + "...");

            // Loop Selección Huéspedes
            ArrayList<DtoHuesped> grupoHuespedes = seleccionarGrupoHuespedes();

            if (!grupoHuespedes.isEmpty()) {
                DtoHabitacion dtoHab = Utils.Mapear.MapearHabitacion.mapearEntidadADto(habSeleccionada);
                DtoEstadia dtoEstadia = new DtoEstadia.Builder()
                        .dtoHabitacion(dtoHab)
                        .fechaCheckIn(fechaInicio)
                        .fechaCheckOut(fechaFin)
                        .valorEstadia(habSeleccionada.getCostoPorNoche())
                        .dtoHuespedes(grupoHuespedes)
                        .build();
                estadiasParaProcesar.add(dtoEstadia);
            } else {
                System.out.println("Carga de habitación cancelada (sin huéspedes).");
            }

            System.out.println("\n¿Desea cargar OTRA habitación? (SI/NO): ");
            if (!scanner.nextLine().trim().equalsIgnoreCase("SI")) {
                deseaCargarOtra = false;
            }
        }

        if (estadiasParaProcesar.isEmpty()) return;

        System.out.println("\nGuardando...");
        try {
            for (DtoEstadia dto : estadiasParaProcesar) {
                // Aquí el gestor validará si los acompañantes ya están ocupados
                gestorEstadia.crearEstadia(dto);
            }
            System.out.println("\n¡Check-in realizado con ÉXITO!");
            pausa();
        } catch (Exception e) {
            System.out.println("ERROR AL GUARDAR: " + e.getMessage());
            pausa();
        }
    }

    // --- SUB-METODO PARA SELECCIONAR HUÉSPEDES (Con distinción visual) ---
    private ArrayList<DtoHuesped> seleccionarGrupoHuespedes() {
        ArrayList<DtoHuesped> lista = new ArrayList<>();
        boolean seguir = true;

        while (seguir) {
            // Feedback visual del rol
            if (lista.isEmpty()) {
                System.out.println("\n--- SELECCIÓN DEL RESPONSABLE (Titular) ---");
                System.out.println("(Nota: El responsable puede figurar en múltiples habitaciones)");
            } else {
                System.out.println("\n--- SELECCIÓN DE ACOMPAÑANTE #" + lista.size() + " ---");
                System.out.println("(Nota: Los acompañantes NO pueden estar en otra habitación)");
            }

            System.out.println("1. Buscar Huésped existente");
            if (!lista.isEmpty()) System.out.println("2. Finalizar carga para esta habitación");

            System.out.print("Opción: ");
            int op = leerOpcionNumerica();

            if (op == 2 && !lista.isEmpty()) break;

            DtoHuesped seleccionado = null;

            if (op == 1) { // Buscar
                DtoHuesped criterios = solicitarCriteriosDeBusqueda();
                ArrayList<Huesped> res = gestorHuesped.buscarHuespedes(criterios);
                if (res.isEmpty()) {
                    System.out.println("No se encontraron huéspedes.");
                } else {
                    mostrarListaDatosEspecificos(res);
                    System.out.print("ID a seleccionar (0 cancelar): ");
                    int id = leerOpcionNumerica();
                    if (id > 0 && id <= res.size()) {
                        seleccionado = Utils.Mapear.MapearHuesped.mapearEntidadADto(res.get(id - 1));
                    }
                }
            }

            if (seleccionado != null) {
                // Verificar duplicado local (en la misma habitación)
                DtoHuesped finalSeleccionado = seleccionado;
                boolean yaEsta = lista.stream().anyMatch(h -> h.getNroDocumento().equals(finalSeleccionado.getNroDocumento()));

                if (yaEsta) {
                    System.out.println("¡Este huésped ya está en la lista de esta habitación!");
                } else {
                    lista.add(seleccionado);
                    System.out.println(">> Agregado: " + seleccionado.getApellido());
                }
            }

            if (!lista.isEmpty()) {
                System.out.println("\n¿Agregar otro acompañante? (SI/NO)");
                if (!scanner.nextLine().trim().equalsIgnoreCase("SI")) seguir = false;
            }
        }
        return lista;
    }

    // Metodo visual específico para CU15
    private void pintarHabitacionOcupada(Map<Habitacion, Map<Date, String>> grilla,
                                         Date inicio, Date fin,
                                         List<DtoEstadia> estadiasConfirmadas,
                                         Habitacion seleccionActual) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Lista ordenada
        List<Habitacion> habitacionesOrdenadas = new ArrayList<>(grilla.keySet());

        System.out.println("\n--- GRILLA DE OCUPACIÓN (CHECK-IN) ---");

        // 1. ENCABEZADO DE TIPOS (NUEVO)
        imprimirEncabezadoTipos(habitacionesOrdenadas);

        // 2. Encabezado de Números
        System.out.print("   FECHA     ");
        for (Habitacion hab : habitacionesOrdenadas) {
            // Marca visual si es la habitación actual del bucle
            if (seleccionActual != null && hab.getNumero().equals(seleccionActual.getNumero())) {
                System.out.print("|" + Colores.VERDE + String.format(" %-9s ", "Hab " + hab.getNumero()) + Colores.RESET);
            } else {
                System.out.print("|" + String.format(" %-9s ", "Hab " + hab.getNumero()));
            }
        }
        System.out.println("|");

        // Línea separadora
        System.out.print("-------------");
        for (int k=0; k<habitacionesOrdenadas.size(); k++) System.out.print("+-----------");
        System.out.println("+");

        // 3. Cuerpo de la grilla (Misma lógica que antes, pero ajustada al nuevo ancho)
        LocalDate inicioLocal = inicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate finLocal = fin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        LocalDate actual = inicioLocal;
        while (!actual.isAfter(finLocal)) {
            System.out.printf("%-12s ", actual.format(dtf));
            Date fechaFila = Date.from(actual.atStartOfDay(ZoneId.systemDefault()).toInstant());

            for (Habitacion hab : habitacionesOrdenadas) {
                String visual = "   ?   ";
                String color = Colores.RESET;
                boolean esSeleccion = false;

                // Lógica de selección CU15
                if (seleccionActual != null && hab.getNumero().equals(seleccionActual.getNumero())) {
                    esSeleccion = true;
                }
                if (!esSeleccion && estadiasConfirmadas != null) {
                    for (DtoEstadia dto : estadiasConfirmadas) {
                        if (dto.getDtoHabitacion().getNumero().equals(hab.getNumero())) {
                            esSeleccion = true; break;
                        }
                    }
                }

                if (esSeleccion) {
                    visual = "   * ";
                    color = Colores.VERDE;
                } else {
                    // Estado base
                    Map<Date, String> mapa = grilla.get(hab);
                    String estado = (mapa != null) ? mapa.get(fechaFila) : "LIBRE";
                    if (estado == null) estado = "LIBRE";

                    switch (estado) {
                        case "OCUPADA" -> { visual = "   X   "; color = Colores.ROJO; }
                        case "RESERVADA" -> { visual = "   R   "; color = Colores.AMARILLO; }
                        case "FUERA DE SERVICIO" -> { visual = "   -   "; color = Colores.ROJO; }
                        case "LIBRE" -> visual = "   L   ";
                    }
                }
                System.out.print("|" + color + String.format(" %-9s ", visual.trim()) + Colores.RESET);
            }
            System.out.println("|");
            actual = actual.plusDays(1);
        }
    }

    private String pedirDocumentoSinExcepcion(TipoDocumento tipo){
        String NroDocumento = null;
        boolean valido = false;

        // Definimos las reglas (Regex)
        // DNI, LE, LC: Solo números, entre 7 y 8 dígitos (ej: 12345678)
        String regexNumerico = "^\\d{7,8}$";
        // Pasaporte: Letras y números, entre 6 y 15 caracteres
        String regexPasaporte = "^[A-Z0-9]{6,15}$";
        // Otro: Cualquier cosa entre 4 y 20 caracteres
        String regexOtro = "^.{4,20}$";

        while (!valido) {
            System.out.print("Número de Documento: ");
            String entrada = scanner.nextLine().trim().toUpperCase(); // Normalizamos a mayúsculas


            if (entrada.isEmpty()) {
                // Si es obligatorio (que lo es), no dejamos pasar vacío
                System.out.println("Error: El documento es obligatorio.");
                continue;
            }

            // Validamos según el tipo seleccionado
            switch (tipo) {
                case DNI:
                case LE:
                case LC:
                    if (entrada.matches(regexNumerico)) {
                        valido = true;
                    } else {
                        System.out.println("Error: Para " + tipo + " debe ingresar entre 7 y 8 números.");
                    }
                    break;
                case PASAPORTE:
                    if (entrada.matches(regexPasaporte)) {
                        valido = true;
                    } else {
                        System.out.println("Error: Formato de Pasaporte inválido (solo letras y números).");
                    }
                    break;
                default: // OTRO
                    if (entrada.matches(regexOtro)) {
                        valido = true;
                    } else {
                        System.out.println("Error: Formato inválido.");
                    }
                    break;
            }

            if (valido) {
                NroDocumento = entrada;
            }
        }
        return NroDocumento;
    }

    // Método que imprime la fila superior con los TIPOS agrupados
    public void imprimirEncabezadoTipos(List<Habitacion> habitacionesOrdenadas) {
        // Espacio vacío sobre la columna de fechas (13 espacios)
        System.out.print("             ");

        int i = 0;
        while (i < habitacionesOrdenadas.size()) {
            Habitacion actual = habitacionesOrdenadas.get(i);
            String tipoActual = actual.getTipoHabitacion().getDescripcion(); // O .name() si prefieres

            // Contar cuántas habitaciones consecutivas son de este mismo tipo
            int contador = 0;
            for (int j = i; j < habitacionesOrdenadas.size(); j++) {
                if (habitacionesOrdenadas.get(j).getTipoHabitacion() == actual.getTipoHabitacion()) {
                    contador++;
                } else {
                    break;
                }
            }

            // Calcular el ancho total de este grupo
            // Cada celda de habitación ocupa 12 caracteres: "| " (2) + 9 (texto) + " " (1)
            int anchoGrupo = contador * 12;

            // Imprimir el nombre del tipo centrado en ese ancho, con bordes
            // Usamos CYAN para destacar el tipo
            System.out.print(Colores.CYAN + "|" + PantallaHelper.centrarTexto(tipoActual, anchoGrupo - 1) + Colores.RESET);

            // Saltar el índice
            i += contador;
        }
        System.out.println("|"); // Cerrar la línea

        // Imprimir una línea separadora decorativa debajo de los tipos
        System.out.print("             ");
        i = 0;
        while (i < habitacionesOrdenadas.size()) {
            Habitacion actual = habitacionesOrdenadas.get(i);
            int contador = 0;
            for (int j = i; j < habitacionesOrdenadas.size(); j++) {
                if (habitacionesOrdenadas.get(j).getTipoHabitacion() == actual.getTipoHabitacion()) contador++;
                else break;
            }
            // Dibuja "-----------"
            System.out.print("+" + "-".repeat((contador * 12) - 1));
            i += contador;
        }
        System.out.println("+");
    }


}