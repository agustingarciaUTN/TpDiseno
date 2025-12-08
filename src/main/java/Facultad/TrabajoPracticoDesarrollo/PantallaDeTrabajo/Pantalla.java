package Facultad.TrabajoPracticoDesarrollo.PantallaDeTrabajo;

import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoDireccion;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoHuesped;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Habitacion;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Huesped;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoEstadia;
import Facultad.TrabajoPracticoDesarrollo.Excepciones.PersistenciaException;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoHabitacion;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoReserva;
import Facultad.TrabajoPracticoDesarrollo.Services.Gestores.*;
import Facultad.TrabajoPracticoDesarrollo.Services.HabitacionService;
import Facultad.TrabajoPracticoDesarrollo.Utils.Colores;
import Facultad.TrabajoPracticoDesarrollo.Utils.Mapear.MapearHabitacion;
import Facultad.TrabajoPracticoDesarrollo.Utils.Mapear.MapearHuesped;
import Facultad.TrabajoPracticoDesarrollo.Utils.PantallaHelper;
import Facultad.TrabajoPracticoDesarrollo.enums.EstadoHabitacion;
import Facultad.TrabajoPracticoDesarrollo.enums.PosIva;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
import Facultad.TrabajoPracticoDesarrollo.Excepciones.CancelacionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class Pantalla implements CommandLineRunner {

    // Todos inyectados por Spring
    private final GestorHuesped gestorHuesped;
    private final GestorUsuario gestorUsuario;
    private final GestorReserva gestorReserva;
    private final GestorEstadia gestorEstadia; // <--- Ahora este también

    private final Scanner scanner;
    private boolean usuarioAutenticado;
    private String nombreUsuarioActual;

    @Autowired // Constructor Único
    public Pantalla(GestorHuesped gestorHuesped,
                    GestorUsuario gestorUsuario,
                    HabitacionService habitacionService,
                    GestorReserva gestorReserva,
                    GestorEstadia gestorEstadia) {

        this.gestorHuesped = gestorHuesped;
        this.gestorUsuario = gestorUsuario;
        this.gestorReserva = gestorReserva;
        this.gestorEstadia = gestorEstadia; // Inyectado

        this.scanner = new Scanner(System.in);
        this.usuarioAutenticado = false;
        this.nombreUsuarioActual = "";
    }

    // 4. Método run de CommandLineRunner (Spring lo llama al iniciar)
    @Override
    public void run(String... args) throws Exception {
        // Llamamos a tu lógica original
        iniciarSistema();
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
        } else {//Si no, mensaje de error
            System.out.println(Colores.ROJO + "❌ No se pudo acceder al sistema." + Colores.RESET);
        }

        //Mensaje de fin de sistema
        System.out.println("\n" + Colores.CYAN + "========================================");
        System.out.println("        👋 FIN DEL SISTEMA");
        System.out.println("========================================" + Colores.RESET);
    }

    //METODO PARA CU AUTENTICAR USUARIO
    private boolean autenticarUsuario() {
        System.out.println(Colores.NEGRILLA + "🔐 AUTENTICACION DE USUARIO" + Colores.RESET);
        System.out.println(Colores.CYAN + "   -------------------------" + Colores.RESET + "\n");

        boolean autenticacionExitosa = false;//Bandera para while de autenticacion

        while (!autenticacionExitosa) {
            System.out.println("Por favor, ingrese sus credenciales:");

            //El actor ingresa su nombre y su contraseña
            System.out.print(Colores.VERDE + "   👤 Usuario: " + Colores.RESET);
            String nombre = scanner.nextLine().trim();//Ingreso de nombre de usuario

            System.out.print(Colores.VERDE + "   🔑 Contraseña: " + Colores.RESET);
            String contrasenia = scanner.nextLine(); //Ingreso de contraseña

            //Validar con el gestor las credenciales ingresadas
            boolean credencialesValidas = gestorUsuario.autenticarUsuario(nombre, contrasenia);

            if (credencialesValidas) {
                //Autenticacion exitosa
                this.usuarioAutenticado = true;
                this.nombreUsuarioActual = nombre;
                System.out.println("\n" + Colores.VERDE + "✅ ¡Autenticación exitosa! Bienvenido, " + nombre + Colores.RESET + "\n");
                autenticacionExitosa = true;//Para salir del while
            } else {
                //El usuario o la contraseña son inválidos
                //El sistema muestra un mensaje de error
                System.out.println("\n" + Colores.ROJO + "╔═════════════════════════════════════════════╗");
                System.out.println("║ ❌ ERROR: Usuario o contraseña inválidos    ║");
                System.out.println("╚═════════════════════════════════════════════╝" + Colores.RESET + "\n");


                int opcion = -1;
                boolean opcionValida = false;

                // Seguimos preguntando hasta que ingrese 1 o 2
                while (!opcionValida) {
                    System.out.println("\n¿Qué desea hacer?");
                    System.out.println(Colores.AMARILLO + " [1]" + Colores.RESET + " 🔄 Volver a ingresar credenciales");
                    System.out.println(Colores.AMARILLO + " [2]" + Colores.RESET + " 🚪 Cerrar el sistema");
                    System.out.print(">> Ingrese una opción: ");

                    try {
                        String entrada = scanner.nextLine().trim();

                        if (entrada.isEmpty()) {
                            // Si da Enter vacío, avisamos y repetimos
                            System.out.println(Colores.ROJO + "⚠️  Debe ingresar una opción." + Colores.RESET);
                            continue;
                        }

                        opcion = Integer.parseInt(entrada);

                        if (opcion == 1 || opcion == 2) {
                            opcionValida = true; //Salimos del bucle
                        } else {
                            System.out.println(Colores.ROJO + "⚠️  Opción inválida. Ingrese 1 o 2." + Colores.RESET);
                        }

                    } catch (NumberFormatException e) {
                        System.out.println(Colores.ROJO + "⚠️  Error: Debe ingresar un número." + Colores.RESET);
                    }
                }

                //Accion final
                if (opcion == 2) {
                    System.out.println(Colores.AZUL + "\nCerrando el sistema..." + Colores.RESET);
                    return false; // Sale del metodo y cierra
                } else {
                    // Opción 1
                    System.out.println(Colores.AZUL + "\n-- Intente nuevamente --\n" + Colores.RESET);
                    //Vuelve a ingresar credenciales
                }
            }
        }

        return true;
    }

    //METODO PARA MOSTRAR MENU PRINCIPAL
    private void mostrarMenuPrincipal() throws Exception {

        boolean salir = false;//Bandera para ejecucion del while. Ademas, para entrar, debe estar autorizado

        while (!salir && usuarioAutenticado) {
            System.out.println("\n" + Colores.CYAN + "╔════════════════════════════════════════════════════╗");
            System.out.println("║                MENU PRINCIPAL                      ║");
            System.out.println("╚════════════════════════════════════════════════════╝" + Colores.RESET);

            // Datos del usuario
            System.out.println(Colores.VERDE + "   👤 Usuario activo: " + Colores.NEGRILLA + nombreUsuarioActual + Colores.RESET);
            System.out.println(Colores.CYAN + "   ──────────────────────────────────────────────────" + Colores.RESET);

            // Opciones de CU
            System.out.println(Colores.AMARILLO + "   [1]" + Colores.RESET + " 🔍 Buscar huésped (CU2)");
            System.out.println(Colores.AMARILLO + "   [2]" + Colores.RESET + " 🛏️  Reservar Habitación (CU4)");
            System.out.println(Colores.AMARILLO + "   [3]" + Colores.RESET + " 📝 Dar de alta huésped (CU9)");
            System.out.println(Colores.AMARILLO + "   [4]" + Colores.RESET + " 🗑️  Ocupar una Habitacion (CU15)");
            System.out.println(Colores.AMARILLO + "   [5]" + Colores.RESET + " 🚪 Cerrar sesión");

            System.out.println(Colores.CYAN + "======================================================" + Colores.RESET);
            System.out.print(">> Ingrese una opción: ");

            int opcion = -1;
            try {
                //Leemos toda la línea como String
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

            switch (opcion) {//Switch para derivar a la ejecucion de cada caso de uso
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
                    ocuparHabitacion();
                    break;
                case 5://Caso de cerrar sesion
                    boolean respuestaValida = false;

                    while (!respuestaValida) {
                        System.out.print(Colores.AMARILLO + "⚠️  ¿Está seguro que desea cerrar sesión? (SI/NO): " + Colores.RESET);
                        String confirmar = scanner.nextLine().trim();

                        if (confirmar.equalsIgnoreCase("SI")) {
                            System.out.println(Colores.AZUL + "\n👋 Cerrando sesión...\n" + Colores.RESET);
                            salir = true;
                            usuarioAutenticado = false;//Reestablecemos la variable de autenticacion
                            respuestaValida = true;
                        } else if (confirmar.equalsIgnoreCase("NO")) {
                            System.out.println(Colores.AZUL + "Volviendo al menú principal..." + Colores.RESET);
                            respuestaValida = true; //Sale del bucle interno y vuelve al menú
                        } else {
                            System.out.println(Colores.ROJO + "❌ Entrada inválida. Por favor ingrese 'SI' o 'NO'." + Colores.RESET);
                        }
                    }
                    break;
                default:
                    System.out.println(Colores.ROJO + "❌ Opción inválida. Intente nuevamente.\n" + Colores.RESET);//vuelve al while
            }
        }

    }

    // =================================== CU9 ===========================================
    public void darDeAltaHuesped() {
        //Mensaje de principio de ejecucion del CU9
        System.out.println("\n" + Colores.CYAN + "╔════════════════════════════════════════════════════╗");
        System.out.println("║           📝 DAR DE ALTA HUÉSPED (CU9)             ║");
        System.out.println("╚════════════════════════════════════════════════════╝" + Colores.RESET);
        System.out.println(Colores.AMARILLO + " ℹ️  Nota: Escriba 'CANCELAR' en cualquier campo para salir." + Colores.RESET + "\n");

        boolean continuarCargando = true; //bandera que representa la condicion del loop principal [1]

        // [BUCLE 1]: Controla el ciclo completo de carga
        // Se repite cada vez que el usuario termina de cargar un huésped y responde "SI" a "¿Desea cargar otro?".
        while (continuarCargando) {

            DtoHuesped datosIngresados = null;

            //INTENTO DE CARGA DE DATOS (creamos una excepción para manejar la opcion de CANCELAR en cualquier momento del formulario)
            //Envolvemos la carga en un try-catch para capturar la cancelación
            try {
                //metodo Pantalla -> Conserje para mostrar formulario y pedir datos
                datosIngresados = mostrarYPedirDatosFormulario();

            } catch (CancelacionException e) {
                // Si el usuario escribió "CANCELAR" durante el formulario:
                boolean confirmacionValida = false;

                while (!confirmacionValida) {
                    System.out.print(Colores.ROJO + "\n🛑 ¿Está seguro que desea cancelar la carga actual? (SI/NO): " + Colores.RESET);
                    String confir = scanner.nextLine().trim();

                    if (confir.equalsIgnoreCase("SI")) {
                        System.out.println(Colores.ROJO + "❌ Carga cancelada. Volviendo al menú principal..." + Colores.RESET);
                        return; // Sale del método completamente

                    } else if (confir.equalsIgnoreCase("NO")) {
                        System.out.println(Colores.AZUL + "🔄 Reiniciando formulario..." + Colores.RESET);
                        confirmacionValida = true; // Rompe el bucle de validación para permitir el continue de abajo

                    } else {
                        System.out.println(Colores.ROJO + "❌ Entrada inválida. Por favor ingrese 'SI' o 'NO'." + Colores.RESET);
                    }
                }

                // Si eligió NO, salimos del while de validación y ejecutamos esto para reiniciar el form
                continue;
            }

            //MENU DE DECISIÓN (Siguiente / Cancelar)
            //Agregamos este bucle 'decisionPendiente' para no perder datos al cancelar al final del formulario. La idea es que, hasta que no se diga que esta seguro de cancelar la carga, no se pierda lo ingresado
            boolean decisionPendiente = true;

            // [BUCLE 2]: Menú de Acciones Post-Formulario.
            // Mantiene al usuario en la pantalla de decisión ("Siguiente" o "Cancelar") hasta que elija una opción válida
            // Evita que el programa se cierre si el usuario se equivoca al elegir una opción.
            while (decisionPendiente) {
                System.out.println(Colores.CYAN + "\n────────── Fin del Formulario ──────────" + Colores.RESET);
                System.out.println("Acciones disponibles:");
                System.out.println(Colores.VERDE + "   [1]" + Colores.RESET + " 💾 GUARDAR / SIGUIENTE");
                System.out.println(Colores.ROJO  + "   [2]" + Colores.RESET + " ❌ CANCELAR OPERACIÓN");
                System.out.print(">> Ingrese una opción: ");

                int opcionBoton = -1;
                try {//validacion de ingreso
                    String entrada = scanner.nextLine();
                    opcionBoton = Integer.parseInt(entrada);
                } catch (NumberFormatException e) {
                    System.out.println(Colores.ROJO + "⚠️  Error: Debe ingresar un número." + Colores.RESET);
                    continue;
                }

                if (opcionBoton == 1) { // presiono SIGUIENTE
                    System.out.println(Colores.AZUL + "⏳ Procesando datos..." + Colores.RESET);

                    //llamar al gestor para que haga las validaciones de negocio
                    List<String> errores;
                    //Metodo que retorna una lista de todos los errores en la validacion de negocio
                    errores = gestorHuesped.validarDatosHuesped(datosIngresados);

                    //Actuamos en consecuencia, dependiendo si hubo errores o no

                    if (!errores.isEmpty()) {//Si hubo errores

                        System.out.println(Colores.ROJO + "\n╔══════════════════════════════════════════╗");
                        System.out.println("║ ❌ ERROR DE VALIDACIÓN DE DATOS          ║");
                        System.out.println("╚══════════════════════════════════════════╝" + Colores.RESET);
                        for (String error : errores) {
                            System.out.println(Colores.ROJO + "  • " + error + Colores.RESET);
                        }
                        System.out.println("\nPor favor, ingrese los datos nuevamente.");
                        decisionPendiente = false;//Salimos del bucle de decisión para recargar datos en formulario
                        continue; //fuerza al inicio del while principal
                    }

                    //Si no hubo errores de validacion de negocio, seguimos
                    try {
                        boolean verificacionPendiente = true;

                        // [BUCLE 3]: Verificación y Corrección de Duplicados
                        // Este bucle permite que, si el usuario elige "CORREGIR", se pidan de nuevo SOLO los datos conflictivos (tipo y nro documento)
                        // y se vuelva a verificar la duplicidad sin perder el resto de la información cargada.
                        while (verificacionPendiente) {

                            //Debemos fijarnos en la DB si existe un Huesped con el mismo TipoDoc y NroDoc que el ingresado
                            Huesped duplicado = gestorHuesped.chequearDuplicado(datosIngresados);
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
                                    System.out.println(Colores.AMARILLO + "   [2]" + Colores.RESET + " CORREGIR DATOS (Solo Tipo y Número de Documento)");
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
                                        String nuevoDoc = pedirDocumento(nuevoTipo, false);

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
                                // Si elige 1 (ACEPTAR IGUALMENTE), salimos del bucle 3 y guardamos (se sobrescribira el huesped)
                                verificacionPendiente = false;

                            } else {
                                // Si no hay duplicados, salimos del bucle 3 y guardamos
                                verificacionPendiente = false;
                            }
                        } // Fin bucle verificacionPendiente

                        //Si no existen duplicado (o se desea sobreescribirlo), INSERT/UPDATE
                        gestorHuesped.upsertHuesped(datosIngresados);

                        System.out.println("\n" + Colores.VERDE + "✅ ¡El huésped ha sido guardado exitosamente!" + Colores.RESET);

                        // Luego del exito del alta, se pregunta si se desea cargar otro huesped
                        System.out.print(Colores.CYAN + "\n🔄 ¿Desea cargar otro huésped? (SI/NO): " + Colores.RESET);

                        //validacion de ingreso correcto
                        String ingresoOtroHuesped = scanner.nextLine();
                        while (!ingresoOtroHuesped.equalsIgnoreCase("NO") && !ingresoOtroHuesped.equalsIgnoreCase("SI")) {
                            System.out.print(Colores.ROJO + "⚠️ Ingreso inválido. " + Colores.RESET + "¿Desea cargar otro huésped? (SI/NO): ");
                            ingresoOtroHuesped = scanner.nextLine();
                        }

                        //si ingresó NO, termina el bucle principal. Si ingresó SI, se repite
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
    }//Fin de CU 9


    //metodo privado para pedir los datos del huesped a dar de alta, CU9 (formulario)
    private DtoHuesped mostrarYPedirDatosFormulario() throws CancelacionException {

        // Encabezado del Formulario
        System.out.println(Colores.CYAN + "\n   ┌──────────────────────────────────────────────────┐");
        System.out.println("   │         📝 FORMULARIO DE REGISTRO                │");
        System.out.println("   └──────────────────────────────────────────────────┘" + Colores.RESET);

        //Cada uno de estos métodos solicita por teclado el ingreso de cada campo del formulario
        //Además, se hace una VALIDACIÓN DE FORMATO (que el email tenga @, que el DNI sean números, que la fecha sea válida, etc)
        //en el momento, evitando datos sin sentido y tener que reingresar todo a posteriori

        //Las validaciones de negocio las realizará el Gestor
        // Todos los métodos 'pedir...' pueden lanzar la excepción si el usuario escribe "CANCELAR"

        // --- SECCIÓN 1: DATOS PERSONALES ---
        System.out.println(Colores.AMARILLO + "\n   === 👤 DATOS PERSONALES ===" + Colores.RESET);

        String apellido = pedirStringTexto(Colores.VERDE + "   > Apellido: " + Colores.RESET);

        String nombres = pedirStringTexto(Colores.VERDE + "   > Nombres: " + Colores.RESET);

        TipoDocumento tipoDocumento = pedirTipoDocumento();

        String numeroDocumento = pedirDocumento(tipoDocumento, false);

        String posIva = pedirPosIva();

        String cuit = pedirCUIT(posIva, tipoDocumento, numeroDocumento);

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

        Long telefono = pedirTelefono();

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


    //Metodo auxiliar para verificar cancelación
    private void chequearCancelacion(String input) throws CancelacionException {
        // Si el input no es nulo y es "CANCELAR" (ignorando mayúsculas), lanzamos la excepción
        if (input != null && input.trim().equalsIgnoreCase("CANCELAR")) {
            throw new CancelacionException();
        }
    }


    //===================== Metodos para pedir Y VALIDAR cada tipo de dato, CU9 ========================

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
            } else if (!entrada.matches("^[\\p{L} ]+$")) {
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

    //Solicitar y Validar Enteros
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

    //Solicitar y Validar Telefono
    private Long pedirTelefono() throws CancelacionException {
        Long valor = null;
        boolean valido = false;

        // Regex: Números, espacios, guiones, más y paréntesis
        String regexTelefono = "^[0-9+() -]+$";

        while (!valido) {
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

    //Solicitar y Validar CUIT
    private String pedirCUIT(String posIvaSeleccionada, TipoDocumento tipoDoc, String nroDoc) throws CancelacionException {
        String cuit;

        // Prefijos válidos en Argentina (Personas físicas y jurídicas)
        List<String> prefijosValidos = Arrays.asList("20", "23", "24", "27", "30", "33", "34");

        while (true) {
            System.out.print(Colores.VERDE + "   > CUIT " + Colores.CYAN + "(Opcional)" + Colores.VERDE + ": " + Colores.RESET);

            cuit = scanner.nextLine().trim();
            chequearCancelacion(cuit);

            // --- CASO VACÍO ---
            if (cuit.isEmpty()) {
                return null; // Dejamos pasar (el Gestor validará si es RI)
            }

            boolean formatoValido = true;
            String mensajeError = "";

            // 1. Validar estructura básica (XX-XXXXXX-X)
            // Regex: 2 números, guión, números, guión, 1 número
            if (!cuit.matches("^\\d{2}-\\d+-\\d$")) {
                formatoValido = false;
                mensajeError = "Formato incorrecto. Debe ser XX-NUMERO-X (ej: 20-12345678-9).";
            } else {
                // Desglosamos el CUIT para validaciones finas
                String[] partes = cuit.split("-");
                String prefijo = partes[0];
                String numeroCentral = partes[1];
                // String digito = partes[2]; // Ya validado por regex que es 1 dígito

                // 2. Validar Prefijo (El "XX" del principio)
                if (!prefijosValidos.contains(prefijo)) {
                    formatoValido = false;
                    mensajeError = "Prefijo inválido. Use uno habitual (20, 23, 24, 27, 30, etc.).";
                }
                // 3. Validar coincidencia con el DNI (El número del medio)
                else if (tipoDoc == TipoDocumento.DNI || tipoDoc == TipoDocumento.LE || tipoDoc == TipoDocumento.LC) {
                    if (!numeroCentral.equals(nroDoc)) {
                        formatoValido = false;
                        // MENSAJE GENÉRICO (Lo que pediste)
                        mensajeError = "El CUIT debe contener el número de documento ingresado (XX-Documento-X).";
                    }
                }
                // 4. Validar longitud para otros documentos (Pasaporte/Otro)
                else {
                    if (numeroCentral.length() < 6 || numeroCentral.length() > 9) {
                        formatoValido = false;
                        mensajeError = "La longitud del número central no es válida.";
                    }
                }
            }

            if (formatoValido) {
                return cuit;
            } else {
                System.out.println(Colores.ROJO + "     ❌ Error: " + mensajeError + Colores.RESET);
            }
        }
    }

    //Solicitar y Validar Email
    private String pedirEmail() throws CancelacionException {
        String email;
        // expresion simple para emails: algo@algo.algo
        String expresionEmail = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

        while (true) {

            System.out.print(Colores.VERDE + "   > Email " + Colores.CYAN + "(Opcional)" + Colores.VERDE + ": " + Colores.RESET);
            email = scanner.nextLine();

            chequearCancelacion(email);

            if (email.trim().isEmpty()) {
                return null; // Válido (opcional)

            } else if (!email.matches(expresionEmail)) {
                System.out.println(Colores.ROJO + "     ❌ Error: Formato de email no válido (xxxx@xxxx.com)." + Colores.RESET);

            } else {
                return email; // Válido
            }
        }
    }

    //Solicitar y Validar Fecha de nacimiento
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

    //Solicitar y Validar Tipo Documento
    private TipoDocumento pedirTipoDocumento() throws CancelacionException {
        TipoDocumento tipoDoc = null;
        boolean valido = false;


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

    //Solicitar y Validar Numero de Documento. esOpcional Si es true, permite salir con Enter vacío
    private String pedirDocumento(TipoDocumento tipo, boolean esOpcional) throws CancelacionException {
        String nroDocumento = null;
        boolean valido = false;

        // --- REGLAS DE VALIDACIÓN (REGEX) ---
        // DNI, LE, LC: Solo números, 7 u 8 dígitos.
        String regexNumerico = "^\\d{7,8}$";

        // Pasaporte: Letras y números, 6 a 15 caracteres.
        String regexPasaporte = "^[A-Z0-9]{6,15}$";

        // Otro: Alfanumérico, 4 a 20 caracteres
        String regexOtro = "^.{4,20}$";

        while (!valido) {
            System.out.print(Colores.VERDE + "   > Número de Documento: " + Colores.RESET);

            String entrada = scanner.nextLine().trim().toUpperCase();

            // Manejo de cancelación dentro del bucle
            chequearCancelacion(entrada);


            // --- CASO 1: ENTRADA VACÍA ---
            if (entrada.isEmpty()) {
                if (esOpcional) {
                    return "0"; // Retorno especial para "sin filtro"
                } else {
                    System.out.println(Colores.ROJO + "     ❌ Error: El documento es obligatorio." + Colores.RESET);
                    continue;
                }
            }

            // --- CASO 2: VALIDACIÓN DE FORMATO ---
            if (tipo != null) {
                // VALIDACIÓN ESPECÍFICA (Cuando eligió un tipo)
                switch (tipo) {
                    case DNI:
                    case LE:
                    case LC:
                        if (entrada.matches(regexNumerico)) valido = true;
                        else System.out.println(Colores.ROJO + "     ❌ Error: Para " + tipo + " debe ingresar 7 u 8 números." + Colores.RESET);
                        break;
                    case PASAPORTE:
                        if (entrada.matches(regexPasaporte)) valido = true;
                        else System.out.println(Colores.ROJO + "     ❌ Error: Formato de Pasaporte inválido." + Colores.RESET);
                        break;
                    default: // OTRO
                        if (entrada.matches(regexOtro)) valido = true;
                        else System.out.println(Colores.ROJO + "     ❌ Error: Formato inválido." + Colores.RESET);
                        break;
                }
            } else {
                // VALIDACIÓN GENÉRICA (Cuando NO eligió tipo - Búsqueda)
                // Que matchee con al menos una validación

                boolean pareceDNI = entrada.matches(regexNumerico);
                boolean parecePasaporte = entrada.matches(regexPasaporte);

                if (pareceDNI || parecePasaporte) {
                    valido = true;
                } else {
                    System.out.println(Colores.ROJO + "     ❌ Error: El número ingresado no corresponde a un formato de documento válido (DNI o Pasaporte)." + Colores.RESET);
                }
            }

            if (valido) {
                nroDocumento = entrada;
            }
        }
        return nroDocumento;
    }

    //Solicitar y Validar Posicion frente al IVA
    private String pedirPosIva() throws CancelacionException {
        String posIva = null;
        boolean valido = false;

        while (!valido) {
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
                        posIva = PosIva.CONSUMIDOR_FINAL.name();
                        valido = true;
                        if(opcion == 0) System.out.println(Colores.CYAN + "        (Seleccionado: Consumidor Final)" + Colores.RESET);
                        break;
                    case 2:
                        posIva = PosIva.MONOTRIBUTISTA.name();
                        valido = true;
                        break;
                    case 3:
                        posIva = PosIva.RESPONSABLE_INSCRIPTO.name();
                        valido = true;
                        break;
                    case 4:
                        posIva = PosIva.EXENTO.name();
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

    //============================== FIN METODOS CU9 ======================================


    //METODO AUXILIAR PARA PAUSAR
    public void pausa() {
        System.out.print("\n" + Colores.AMARILLO + "⏹️  Presione ENTER para continuar..." + Colores.RESET);
        scanner.nextLine();
        System.out.println();
    }

    //=================================== CU2 =========================================
    public void buscarHuesped() {
        System.out.println("\n" + Colores.CYAN + "╔════════════════════════════════════════════════════╗");
        System.out.println("║           🔎 BÚSQUEDA DE HUÉSPED (CU2)             ║");
        System.out.println("╚════════════════════════════════════════════════════╝" + Colores.RESET);

        DtoHuesped dtoHuespedCriterios = solicitarCriteriosDeBusqueda();//Solicitamos los criterios por los que el usuario quiere realizar la busqueda de/los huespedes.
        // Se recuerda que solo se admitiran las iniciales para la busqueda por apellido y nombre. Respondiendo a la especificacion del caso de uso "comienza con"

        System.out.println(Colores.AZUL + "\n🔄 Buscando en la base de datos..." + Colores.RESET);


        ArrayList<Huesped> huespedesEncontrados = gestorHuesped.buscarHuespedes(dtoHuespedCriterios);//Llamamos al metodo del gestor que se encarga de dirigir la busqueda de los huespedes

        if (huespedesEncontrados.isEmpty()) {//No se encontraron huespedes con los criterios especificados
            System.out.println(Colores.AMARILLO + "\n⚠️  No se encontraron huéspedes con los criterios especificados." + Colores.RESET);
            System.out.print("¿Desea dar de alta un nuevo huésped? (SI/NO): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("SI")) {
                this.darDeAltaHuesped(); // Deriva al CU 9, dar de alta huesped
            }
        } else {
            // Mostramos la tabla y luego el menú de selección
            mostrarListaDatosEspecificos(huespedesEncontrados);
            this.seleccionarHuespedDeLista(huespedesEncontrados);

        }
        pausa();
    }

    private DtoHuesped solicitarCriteriosDeBusqueda() {
        DtoHuesped criterios = new DtoHuesped();

        System.out.println("\nIngrese uno o más criterios " + Colores.CYAN + "(Presione ENTER para omitir)" + Colores.RESET + ":");

        // --- 1. APELLIDO ---
        while (true) {
            System.out.print(Colores.VERDE + "   > Apellido (comienza con): " + Colores.RESET);
            String apellido = scanner.nextLine().trim();

            if (apellido.isEmpty()) break; // Omitir

            // Validación: Solo letras
            if (!apellido.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ]+$") || apellido.length() > 1) {
                System.out.println(Colores.ROJO + "     ❌ Error: Solo se admite una letra." + Colores.RESET);
                continue;
            }
            criterios.setApellido(apellido);
            break;
        }

        // --- 2. NOMBRES ---
        while (true) {
            System.out.print(Colores.VERDE + "   > Nombres (comienza con): " + Colores.RESET);
            String nombres = scanner.nextLine().trim();

            if (nombres.isEmpty()) break; // Omitir

            if (!nombres.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ]+$") || nombres.length() > 1) {
                System.out.println(Colores.ROJO + "     ❌ Error: Solo se admite una letra." + Colores.RESET);
                continue;
            }
            criterios.setNombres(nombres);
            break;
        }

        // --- 3. TIPO DE DOCUMENTO ---
        criterios.setTipoDocumento(validarYLeerTipoDocumento());

        // --- 4. NÚMERO DE DOCUMENTO ---
        // Usamos un metodo especial que permite validación flexible si no hay tipo seleccionado
        try{String nroDoc = pedirDocumento(criterios.getTipoDocumento(), true);
            criterios.setNroDocumento(nroDoc);}
        catch (CancelacionException ignored){}


        return criterios;
    }


    private TipoDocumento validarYLeerTipoDocumento() {
        while (true) {
            System.out.print(Colores.VERDE + "   > Tipo Doc " + Colores.CYAN + "[DNI/LE/LC/PASAPORTE/OTRO]" + Colores.VERDE + ": " + Colores.RESET);
            String tipoStr = scanner.nextLine().trim().toUpperCase();

            if (tipoStr.isEmpty()) {
                return null; // Omitir
            }
            try {
                return TipoDocumento.valueOf(tipoStr);
            } catch (IllegalArgumentException e) {
                System.out.println(Colores.ROJO + "     ❌ Error: Tipo inválido. Ingrese uno de los valores mostrados." + Colores.RESET);
            }
        }
    }


    private void seleccionarHuespedDeLista(ArrayList<Huesped> listaEntidadesHuespedes) {

        boolean banderaSeleccion = true;

        while(banderaSeleccion){
            System.out.println("\nAcciones disponibles:");
            System.out.println(Colores.AMARILLO + "   [ID]" + Colores.RESET + " Ingrese el número de ID para " + Colores.NEGRILLA + "MODIFICAR/ELIMINAR" + Colores.RESET);
            System.out.println(Colores.AMARILLO + "   [0]" + Colores.RESET + "  Dar de alta uno " + Colores.VERDE + "NUEVO" + Colores.RESET);

            System.out.print("\n>> Su selección: ");
            int seleccion = leerOpcionNumerica();

            // Mapear lista entidades a dto
            ArrayList<DtoHuesped> listaHuespedesDto = new ArrayList<>();
            for (Huesped listaEHuespedes : listaEntidadesHuespedes) {

                listaHuespedesDto.add(MapearHuesped.mapearEntidadADto(listaEHuespedes));
            }

            // Sigue el flujo. Mapeamos de entidad a DTO para pasarle al Gestor
            if (seleccion > 0 && seleccion <= listaEntidadesHuespedes.size()) {
                DtoHuesped huespedDtoSeleccionado = listaHuespedesDto.get(seleccion - 1);

                System.out.println(Colores.AZUL + "\n⏳ Cargando datos del huésped seleccionado..." + Colores.RESET);

                // lógica de negocio
                Huesped huespedSeleccionado = gestorHuesped.crearHuespedSinPersistir(huespedDtoSeleccionado);//Creamos el huesped para pasarlo al CU10

                // Mensaje de ejecucion de CU10
                System.out.println(Colores.CYAN + "╔════════════════════════════════════════════════════╗");
                System.out.println("║   🚧 FUNCIONALIDAD CASO DE USO 10 EN PROGRESO 🚧   ║");
                System.out.println("╚════════════════════════════════════════════════════╝" + Colores.RESET);
                banderaSeleccion = false;

            } else if (seleccion == 0) {
                System.out.println(Colores.AZUL + "--> Redirigiendo al Alta de Huésped..." + Colores.RESET);
                this.darDeAltaHuesped(); // CU 9
                banderaSeleccion = false;
            } else {
                System.out.println(Colores.ROJO + "❌ Opción inválida, vuelva a ingresar." + Colores.RESET);

            }
        }



    }

    //Metodo para la construccion de la tabla de huespedes en CU2
    private void mostrarListaDatosEspecificos(ArrayList<Huesped> listaHuespedes) {
        // --- MENÚ DE ORDENAMIENTO ---

        boolean banderaOrdenamiento = true;

        int columna = 0;

        while(banderaOrdenamiento){
            System.out.println(Colores.CYAN + "\n   --- 📊 OPCIONES DE ORDENAMIENTO ---" + Colores.RESET);
            System.out.println("   1. Apellido            3. Tipo Documento");
            System.out.println("   2. Nombre              4. Número Documento");
            System.out.print(Colores.VERDE + "   >> Ordenar por: " + Colores.RESET);

            columna = leerOpcionNumerica();//Solicitamos ingreso de parametro de ordenamiento

            if (columna < 1 || columna > 4) {
                System.out.println(Colores.ROJO + "     ❌ Opción inválida, vuelva a ingresar." + Colores.RESET);
            }
            else {
                banderaOrdenamiento = false;
            }
        }

        boolean banderaAscendente = true;
        boolean ascendente = false;

        while(banderaAscendente){
            System.out.println("\n   1. Ascendente (A-Z)    2. Descendente (Z-A)");
            System.out.print(Colores.VERDE + "   >> Criterio: " + Colores.RESET);

            int orden = leerOpcionNumerica();//Solicitamos ingreso de parametro de tipo de ordenamiento

            if(orden < 1 || orden > 2){
                System.out.println(Colores.ROJO + "     ❌ Opción inválida, vuelva a ingresar." + Colores.RESET);
                continue;
            }
            else{
                banderaAscendente = false;
            }
            ascendente = (orden == 1);
        }


        // Definimos el comparador para la ENTIDAD Huesped
        //Metodo de ordenamiento
        Comparator<Huesped> comparador = getHuespedComparator(columna, ascendente);
        listaHuespedes.sort(comparador);

        // --- TABLA DE RESULTADOS ---
        System.out.println("\n" + Colores.VERDE + "✅ Se encontraron " + listaHuespedes.size() + " resultados:" + Colores.RESET);

        // Encabezado de tabla con caracteres de caja
        System.out.println("┌──────┬──────────────────────┬──────────────────────┬────────────────────┐");
        System.out.printf("│ %-4s │ %-20s │ %-20s │ %-18s │%n", "ID", "APELLIDO", "NOMBRES", "DOCUMENTO");
        System.out.println("├──────┼──────────────────────┼──────────────────────┼────────────────────┤");

        for (int i = 0; i < listaHuespedes.size(); i++) {
            Huesped h = listaHuespedes.get(i);
            String tipoDoc = (h.getTipoDocumento() != null ? h.getTipoDocumento().name() : "-");
            // Convertimos el long a String para mostrarlo
            String nroDoc = String.valueOf(h.getNroDocumento());
            String docCompleto = tipoDoc + " " + nroDoc;

            // Imprimimos la fila formateada
            // Nota: Usamos una función auxiliar 'cortar' para que no rompa la tabla si el nombre es larguísimo
            System.out.printf("│ %-4d │ %-20s │ %-20s │ %-18s │%n",
                    (i + 1),
                    cortar(h.getApellido()),
                    cortar(h.getNombres()),
                    docCompleto);
        }
        System.out.println("└──────┴──────────────────────┴──────────────────────┴────────────────────┘");
    }

    //Metodo de ordenamiento de huespedes
    private static Comparator<Huesped> getHuespedComparator(int columna, boolean ascendente) {
        Comparator<Huesped> comparador = switch (columna) {
            case 1 -> // Apellido
                    Comparator.comparing(Huesped::getApellido, String.CASE_INSENSITIVE_ORDER);
            case 2 -> // Nombre
                    Comparator.comparing(Huesped::getNombres, String.CASE_INSENSITIVE_ORDER);
            case 3 -> // Tipo de Documento (Enum)
                    Comparator.comparing(h -> h.getTipoDocumento() != null ? h.getTipoDocumento().name() : "Z");
            case 4 -> // Número de Documento (long en Entidad)
                    Comparator.comparing(Huesped::getNroDocumento);
            default -> null;
        };

        if (!ascendente) {
            comparador = comparador.reversed();
        }
        return comparador;
    }

    // ================================ FIN METODOS CU2 =====================================

    // Metodo auxiliar para evitar que textos largos rompan la tabla
    private String cortar(String texto) {
        if (texto == null) return "";
        if (texto.length() <= 20) return texto;
        return texto.substring(0, 20 - 3) + "...";
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


    // METODO ORQUESTADOR OPTIMIZADO (Carga masiva)
    private Map<Habitacion, Map<Date, String>> generarGrillaEstados(Date fechaInicio, Date fechaFin) {

        System.out.println(Colores.AZUL + "⏳ Recuperando datos del servidor..." + Colores.RESET); // Feedback de carga con color

        // 1. Traer TODO de una vez (3 Consultas en total)
        ArrayList<Habitacion> habitaciones = gestorHabitacion.obtenerTodas();
        List<DtoReserva> todasLasReservas = gestorReserva.buscarReservasEnFecha(fechaInicio, fechaFin);
        List<DtoEstadia> todasLasEstadias = gestorEstadia.buscarEstadiasEnFecha(fechaInicio, fechaFin);

        Map<Habitacion, Map<Date, String>> grilla = new LinkedHashMap<>();

        // Ordenar (En memoria, rápido)
        habitaciones.sort(Comparator.comparing(Habitacion::getTipoHabitacion)
                .thenComparing(Habitacion::getNumero));

        // 2. Procesar en Memoria (Sin ir a la BD)
        for  (Habitacion hab : habitaciones) {
            Map<Date, String> estadosDia = new HashMap<>();

            // Filtramos las listas globales para quedarnos solo con lo de ESTA habitación
            // (Esto es muchísimo más rápido que preguntar a SQL)
            List<DtoReserva> reservasHab = todasLasReservas.stream()
                    .filter(r -> r.getIdHabitacion().equals(hab.getNumero())).toList();

            List<DtoEstadia> estadiasHab = todasLasEstadias.stream()
                    .filter(e -> e.getDtoHabitacion().getNumero().equals(hab.getNumero())).toList();

            LocalDate inicio = fechaInicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate fin = fechaFin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            for (LocalDate date = inicio; !date.isAfter(fin); date = date.plusDays(1)) {
                Date fechaActual = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
                String estado = "LIBRE";

                // A. Estado propio
                if (hab.getEstadoHabitacion() != null && "FUERA_DE_SERVICIO".equals(hab.getEstadoHabitacion().name())) {
                    estado = "FUERA DE SERVICIO";
                } else {
                    // B. Buscar en lista de Estadías (Memoria)
                    boolean ocupada = estadiasHab.stream().anyMatch(e ->
                            !fechaActual.before(e.getFechaCheckIn()) &&
                                    (e.getFechaCheckOut() == null || fechaActual.before(e.getFechaCheckOut()))
                    );

                    if (ocupada) {
                        estado = "OCUPADA";
                    } else {
                        // C. Buscar en lista de Reservas (Memoria)
                        boolean reservada = reservasHab.stream().anyMatch(r ->
                                !fechaActual.before(r.getFechaDesde()) && fechaActual.before(r.getFechaHasta())
                        );

                        if (reservada) estado = "RESERVADA";
                    }
                }
                estadosDia.put(fechaActual, estado);
            }
            grilla.put(hab, estadosDia);
        }
        return grilla;
    }

    // CU4: Reservar Habitación
    public void reservarHabitacion() throws Exception {
        System.out.println("\n" + Colores.CYAN + "╔════════════════════════════════════════════════════╗");
        System.out.println("║           🛏️  RESERVAR HABITACIÓN (CU4)            ║");
        System.out.println("╚════════════════════════════════════════════════════╝" + Colores.RESET);

        // 1. LLAMADA AL CU5
        System.out.println(Colores.AZUL + "ℹ️  Visualice el rango general para buscar disponibilidad:" + Colores.RESET);
        Map<Habitacion, Map<Date, String>> grillaVista = mostrarEstadoHabitaciones();

        if (grillaVista == null) return; // Cancelado o sin datos

        List<DtoReserva> listaParaReservar = new ArrayList<>();
        boolean seguirAgregando = true;

        // 2. Bucle de Selección
        while (seguirAgregando) {
            System.out.println("\n" + Colores.AMARILLO + "┌──────────────────────────────────────────────────┐");
            System.out.println("│               ➕ NUEVA SELECCIÓN                 │");
            System.out.println("└──────────────────────────────────────────────────┘" + Colores.RESET);

            // A. Selección de Habitación
            String entrada;
            int nro = -1;

            while (true) {
                System.out.print(Colores.VERDE + "   > Ingrese Nro Habitación a reservar: " + Colores.RESET);
                entrada = scanner.nextLine().trim();

                // Campo obligatorio
                if (entrada.isEmpty()) {
                    System.out.println(Colores.ROJO + "     ❌ Error: Campo Obligatorio." + Colores.RESET);
                    continue;
                }

                // Sólo dígitos
                if (!entrada.matches("^\\d+$")) {
                    System.out.println(Colores.ROJO + "     ❌ Error: Debe ingresar sólo números." + Colores.RESET);
                    continue;
                }

                try {
                    nro = Integer.parseInt(entrada);
                    if (nro <= 0) {
                        System.out.println(Colores.ROJO + "     ❌ Error: Ingrese un número positivo." + Colores.RESET);
                        continue;
                    }
                    break; // válido
                } catch (NumberFormatException e) {
                    System.out.println(Colores.ROJO + "     ❌ Error: Número demasiado grande." + Colores.RESET);
                }
            }


            // Validar existencia
            Habitacion habSeleccionada = null;
            for (Habitacion h : grillaVista.keySet()) {
                if (h.getNumero().equals(String.valueOf(nro))) {
                    habSeleccionada = h;
                    break;
                }
            }

            // --- CORRECCIÓN CRÍTICA: Validar null ANTES de usar el objeto ---
            if (habSeleccionada == null) {
                System.out.println(Colores.ROJO + "     ❌ Error: La habitación no existe o no está en la vista actual." + Colores.RESET);
                continue;
            }

            // Ahora es seguro preguntar el estado
            if(habSeleccionada.getEstadoHabitacion() == EstadoHabitacion.FUERA_DE_SERVICIO){
                System.out.println(Colores.ROJO + "     ❌ Error: La habitación está FUERA DE SERVICIO." + Colores.RESET);
                continue;
            }
            // ----------------------------------------------------------------


            // B. Selección de Fechas
            System.out.println(Colores.CYAN + "\n   Define el rango de fechas específico para la Habitación " + nro + ":" + Colores.RESET);

            Date fechaInicioReserva = null;
            Date fechaFinReserva;

            try {
                // Calculamos límites de la grilla para validar
                Date inicioGrilla;
                Optional<Date> minFechaOpt = grillaVista.values().stream()
                        .flatMap(m -> m.keySet().stream())
                        .min(Date::compareTo);
                inicioGrilla = minFechaOpt.orElse(new Date());

                Date finGrilla;
                Optional<Date> maxFechaOpt = grillaVista.values().stream().flatMap(m->m.keySet().stream()).max(Date::compareTo);
                finGrilla = maxFechaOpt.orElse(inicioGrilla);

                LocalDate inicioLocal = inicioGrilla.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate limiteAnterior = inicioLocal.minusDays(1);
                Date fechaLimiteParaPedir = Date.from(limiteAnterior.atStartOfDay(ZoneId.systemDefault()).toInstant());

                // 1. Pedir Inicio
                fechaInicioReserva = pedirFechaEntre(
                        "   > Fecha Inicio (dd/MM/yyyy): ",
                        fechaLimiteParaPedir,  finGrilla ,
                        "La fecha debe estar dentro del rango visualizado.");


                // 2. Pedir Fin
                fechaFinReserva = pedirFechaEntre(
                        "   > Fecha Fin (dd/MM/yyyy): ",
                        fechaInicioReserva, finGrilla,
                        "La fecha debe ser posterior al inicio y dentro del rango visualizado."
                );

            } catch (CancelacionException e) {
                System.out.println(Colores.ROJO + "Operación cancelada." + Colores.RESET);
                return;
            }

            // C. Validaciones de Negocio

            // 1. Validar coherencia de fechas (GestorHabitacion)
            if (!gestorHabitacion.validarRangoFechas(fechaInicioReserva, fechaFinReserva)) {
                continue;
            }

            // 2. Validar disponibilidad REAL en BD (GestorReserva y GestorEstadia)
            boolean ocupadaParcialmente = false;
            ZoneId zone = ZoneId.systemDefault();
            LocalDate inicio = fechaInicioReserva.toInstant().atZone(zone).toLocalDate();
            LocalDate fin = fechaFinReserva.toInstant().atZone(zone).toLocalDate();

            for(LocalDate d = inicio; !d.isAfter(fin) ; d = d.plusDays(1)){
                Date diaChequeado = Date.from(d.atStartOfDay(zone).toInstant());
                if(gestorEstadia.estaOcupadaEnFecha(String.valueOf(nro), diaChequeado, diaChequeado)){
                    ocupadaParcialmente = true;
                    break;
                }
            }

            boolean ocupada = gestorEstadia.estaOcupadaEnFecha(String.valueOf(nro), fechaInicioReserva, fechaFinReserva);
            boolean reservada = gestorReserva.estaReservadaEnFecha(String.valueOf(nro), fechaInicioReserva, fechaFinReserva);

            if (ocupada || ocupadaParcialmente) {
                System.out.println(Colores.ROJO + "     ❌ Error: La habitación está OCUPADA físicamente en esas fechas." + Colores.RESET);
                continue;
            } else if (reservada) {
                System.out.println(Colores.ROJO + "     ❌ Error: La habitación ya tiene una RESERVA confirmada." + Colores.RESET);
                continue;
            }

            // 3. Validar que no la haya seleccionado ya en este mismo proceso (Lista temporal)
            boolean yaEnLista = false;
            for(DtoReserva dto : listaParaReservar) {
                if(dto.getIdHabitacion().equals(String.valueOf(nro))) { // Corrección de tipo: nro es int
                    yaEnLista = true; break;
                }
            }
            if (yaEnLista) {
                System.out.println(Colores.ROJO + "     ❌ Error: Ya has seleccionado esta habitación en esta sesión." + Colores.RESET);
                continue;
            }

            System.out.println(Colores.VERDE + "     ✅ ¡Habitación disponible!" + Colores.RESET);

            // D. Solicitar Datos del Responsable
            System.out.println(Colores.AMARILLO + "\n   👤 Datos del Responsable de la Reserva:" + Colores.RESET);
            String nombreResp, apellidoResp, telefonoResp;
            try {
                // Agregué colores a los prompts aquí también
                apellidoResp = pedirStringTexto(Colores.VERDE + "   > Apellido: " + Colores.RESET);
                nombreResp = pedirStringTexto(Colores.VERDE + "   > Nombre: " + Colores.RESET);
                telefonoResp = String.valueOf(pedirTelefono()); // Asumiendo que pedirTelefono tiene su propio prompt
            } catch (CancelacionException e) {
                System.out.println(Colores.ROJO + "Reserva cancelada." + Colores.RESET);
                return;
            }

            // E. Crear DTO y agregar a la lista
            DtoReserva nuevaReserva = new DtoReserva.Builder()
                    .idHabitacion(String.valueOf(nro))
                    .fechaDesde(fechaInicioReserva)
                    .fechaHasta(fechaFinReserva)
                    .nombreResponsable(nombreResp)
                    .apellidoResponsable(apellidoResp)
                    .telefonoResponsable(telefonoResp)
                    .build();

            listaParaReservar.add(nuevaReserva);

            // F. Actualizar Visualización (Pintamos lo que seleccionó el usuario)
            // Necesitamos pasar las fechas de la vista original para mantener el marco de referencia
            Date inicioVista = grillaVista.values().iterator().next().keySet().stream().min(Date::compareTo).orElse(new Date());
            Date finVista = grillaVista.values().iterator().next().keySet().stream().max(Date::compareTo).orElse(new Date());

            imprimirGrilla(grillaVista, inicioVista, finVista, listaParaReservar);

            // G. Preguntar si sigue
            boolean flagIngreso = true; //flag por si toca otro boton o ingresa algo distinto a SI o NO
            while(flagIngreso) {
                System.out.print(Colores.AMARILLO + "\n¿Desea reservar otra habitación? (SI/NO): " + Colores.RESET);
                String resp = scanner.nextLine().trim();
                if (resp.equalsIgnoreCase("SI")) {
                    seguirAgregando = true;
                    flagIngreso = false; //no repetimos while
                } else if (resp.equalsIgnoreCase("NO")) {
                    seguirAgregando = false;
                    flagIngreso = false; //no repetimos while
                } else {
                    System.out.println(Colores.ROJO + "     ❌ Por favor ingrese SI o NO." + Colores.RESET);
                    flagIngreso = true;
                }
            }
        }

        if (listaParaReservar.isEmpty()) {
            System.out.println(Colores.AMARILLO + "Finalizando sin generar reservas." + Colores.RESET);
            return;
        }

        // 3. Confirmación y Persistencia
        System.out.println(Colores.AZUL + "\n💾 Guardando reservas..." + Colores.RESET);
        try {
            gestorReserva.crearReservas(listaParaReservar);
            System.out.println(Colores.VERDE + "✅ ¡Reservas registradas con ÉXITO!" + Colores.RESET);
        } catch (Exception e) {
            System.out.println(Colores.ROJO + "❌ Error al guardar: " + e.getMessage() + Colores.RESET);
            if (e.getCause() != null) {
                System.out.println(Colores.ROJO + "   Causa interna: " + e.getCause().getMessage() + Colores.RESET);
            }
        }
        System.out.println(Colores.CYAN + "\n--- Fin CU4 'Reservar Habitación' ---" + Colores.RESET);
        pausa();
    }

    private void imprimirGrilla(Map<Habitacion, Map<Date, String>> grilla, Date inicio, Date fin, List<DtoReserva> seleccion) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formatoCelda = "| %-9s ";
        System.out.println("\n--- GRILLA DE DISPONIBILIDAD ---");

        List<Habitacion> habitacionesOrdenadas = new ArrayList<>(grilla.keySet());

        // 1. IMPRIMIR ENCABEZADO AGRUPADO POR TIPO
        imprimirEncabezadoTipos(habitacionesOrdenadas);


        // 2. Imprimir fila de Números de Habitación
        System.out.print("       FECHA ");
        for (Habitacion hab : habitacionesOrdenadas) {
            System.out.printf(formatoCelda, "Hab " + hab.getNumero());

        }
        System.out.println("|");
        System.out.print("-------------");
        for (int k=0; k<habitacionesOrdenadas.size(); k++) System.out.print("+-----------");
        System.out.println("+");


        // 3. Filas (Días)
        LocalDate inicioLocal = inicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate finLocal = fin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        LocalDate actual = inicioLocal;

        while (!actual.isAfter(finLocal)) {
            System.out.printf("%-12s ", actual.format(dtf)); // Fecha
            Date fechaFila = Date.from(actual.atStartOfDay(ZoneId.systemDefault()).toInstant());

            for (Habitacion hab : habitacionesOrdenadas) {
                String visual = " ? ";
                String color = Colores.RESET;
                boolean esSeleccion = false;

                if (seleccion != null) {
                    for (DtoReserva res : seleccion) {

                        // Coincide Habitación
                        if (res.getIdHabitacion().equals(hab.getNumero())) {
                            // Coincide Rango de Fechas ( fechaFila >= desde Y fechaFila <= hasta )
                            // Usamos compareTo: >= 0 es posterior/igual, <= 0 es anterior/igual
                            if (!fechaFila.before(res.getFechaDesde()) && !fechaFila.after(res.getFechaHasta())) {
                                esSeleccion = true;
                                break;
                            }
                        }
                    }
                }

                if (esSeleccion) {
                    visual = " * "; // Marca visual de "Tu Selección"
                    color = Colores.VERDE;
                } else {
                    // Si no es selección nuestra, miramos la base de datos (cacheada en grilla)
                    Map<Date, String> mapaEstados = grilla.get(hab);
                    String estado = (mapaEstados != null) ? mapaEstados.get(fechaFila) : "LIBRE";
                    if (estado == null) estado = "LIBRE";

                    color = switch (estado) {
                        case "OCUPADA" -> {
                            visual = " X ";
                            yield Colores.ROJO;
                        }
                        case "RESERVADA" -> {
                            visual = " R ";
                            yield Colores.AMARILLO;
                        }
                        case "FUERA DE SERVICIO" -> {
                            visual = " - ";
                            yield Colores.CYAN;
                        }
                        case "LIBRE" -> {
                            visual = " L ";
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
                + " | " + Colores.CYAN + "[-]Fuera de servicio" + Colores.RESET);

    }


    // CU5: Mostrar Estado de Habitaciones
    // Retorna el mapa con los datos para que el CU4 pueda reutilizarlos
    // CU5: Mostrar Estado de Habitaciones
    public Map<Habitacion, Map<Date, String>> mostrarEstadoHabitaciones() throws CancelacionException {
        System.out.println("\n" + Colores.CYAN + "╔════════════════════════════════════════════════════╗");
        System.out.println("║        📊 MOSTRAR ESTADO HABITACIONES (CU5)        ║");
        System.out.println("╚════════════════════════════════════════════════════╝" + Colores.RESET);

        boolean flagFechas = false;
        Date fechaInicio = null;
        Date fechaFin = null;

        while(!flagFechas) {
            // 1. Pedir y Validar Fechas
            LocalDate ayer = LocalDate.now().minusDays(1);
            Date fechaReferencia = Date.from(ayer.atStartOfDay(ZoneId.systemDefault()).toInstant());

            SimpleDateFormat sdfEsp = new SimpleDateFormat("EEEE dd 'de' MMMM yyyy", new Locale("es", "ES"));
            sdfEsp.setTimeZone(TimeZone.getDefault());
            String fechaReferenciaStr = sdfEsp.format(fechaReferencia);

            System.out.println(Colores.AMARILLO + "\n   📅 Defina el rango de fechas a visualizar:" + Colores.RESET);

            fechaInicio = pedirFechaPosteriorA(
                    "   > Desde Fecha (dd/MM/yyyy): ",
                    fechaReferencia,
                    "La fecha de Inicio debe ser mayor a " + fechaReferenciaStr + "." );

            String fechaInicioStr = sdfEsp.format(fechaInicio);

            fechaFin = pedirFechaPosteriorA(
                    "   > Hasta Fecha (dd/MM/yyyy): ",
                    fechaInicio,
                    "La fecha limite debe ser mayor a la fecha de inicio (" + fechaInicioStr + ").");

            // Validar lógica de negocio
            flagFechas = gestorHabitacion.validarRangoFechas(fechaInicio, fechaFin);
            if(!flagFechas) {
                System.out.println(Colores.ROJO + "     ❌ Error: El rango de fechas no es válido. Intente nuevamente." + Colores.RESET);
            }
        }

        System.out.println(Colores.AZUL + "\n⏳ Procesando estados y disponibilidad..." + Colores.RESET);

        // 2. Generar la grilla
        Map<Habitacion, Map<Date, String>> grilla = generarGrillaEstados(fechaInicio, fechaFin);

        if (grilla.isEmpty()) {
            System.out.println(Colores.AMARILLO + "⚠️  No hay habitaciones registradas en el sistema." + Colores.RESET);
            return null;
        }

        // 3. Visualización
        imprimirGrilla(grilla, fechaInicio, fechaFin, null);

        return grilla;
    }


    /**
     * Pide una fecha que sea posterior (o igual en términos de día) a una fecha base.
     * @param mensaje El texto para pedir el dato.
     * @param fechaBase La fecha contra la cual comparar.
     * @param mensajeError El mensaje a mostrar si la validación falla.
     */
    private Date pedirFechaPosteriorA(String mensaje, Date fechaBase, String mensajeError) throws CancelacionException {
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        formatoFecha.setLenient(false);

        LocalDate baseLocal = fechaBase.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        while (true) {
            System.out.print(Colores.VERDE + mensaje + Colores.RESET);
            String fechaStr = scanner.nextLine().trim();
            chequearCancelacion(fechaStr);

            if (fechaStr.isEmpty()) {
                System.out.println(Colores.ROJO + "     ❌ Error: Este campo es obligatorio." + Colores.RESET);
                continue;
            }

            try {
                Date fechaIngresada = formatoFecha.parse(fechaStr);
                LocalDate ingresadaLocal = fechaIngresada.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                if (ingresadaLocal.isAfter(baseLocal)) {
                    return fechaIngresada;
                } else {
                    System.out.println(Colores.ROJO + "     ❌ Error: " + mensajeError + Colores.RESET);
                }

            } catch (ParseException e) {
                System.out.println(Colores.ROJO + "     ❌ Error: Formato inválido. Use dd/MM/yyyy." + Colores.RESET);
            }
        }
    }

    private Date pedirFechaEntre(String mensaje, Date fechaInicioReserva, Date fechaLimiteGrilla , String mensajeError) throws CancelacionException {
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        formatoFecha.setLenient(false);

        // Convertimos la fecha base a LocalDate para ignorar horas/minutos/segundos
        LocalDate baseLocal = fechaInicioReserva.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        while (true) {
            System.out.print(Colores.VERDE + mensaje + Colores.RESET);
            String fechaStr = scanner.nextLine().trim();
            chequearCancelacion(fechaStr);

            if (fechaStr.isEmpty()) {
                System.out.println(Colores.ROJO + "     ❌ Error: Este campo es obligatorio." + Colores.RESET);
                continue;
            }

            try {
                Date fechaIngresada = formatoFecha.parse(fechaStr);
                LocalDate ingresadaLocal = fechaIngresada.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate fechaLimite = fechaLimiteGrilla.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                // Validamos: La fecha ingresada debe ser posterior o igual a la base
                if (ingresadaLocal.isAfter(fechaLimite)) {
                    System.out.println(Colores.ROJO + "     ❌ Error: La fecha debe estar contenida en el rango de fechas visualizado en la grilla."  + Colores.RESET);
                } else if (!ingresadaLocal.isBefore(baseLocal)){
                    return fechaIngresada;
                } else {
                    System.out.println(Colores.ROJO + "     ❌ Error: " + mensajeError + Colores.RESET);
                }

            } catch (ParseException e) {
                System.out.println(Colores.ROJO + "     ❌ Error: Formato inválido. Use dd/MM/yyyy." + Colores.RESET);
            }
        }
    }


    // --- CU15: OCUPAR HABITACIÓN (CHECK-IN) ---
    public void ocuparHabitacion() throws Exception {
        System.out.println("\n" + Colores.CYAN + "╔════════════════════════════════════════════════════╗");
        System.out.println("║           🔑 OCUPAR HABITACIÓN (CU15)              ║");
        System.out.println("╚════════════════════════════════════════════════════╝" + Colores.RESET);

        // 1. Mostrar Grilla Base
        System.out.println(Colores.AZUL + "ℹ️  Visualizando disponibilidad actual..." + Colores.RESET);
        Map<Habitacion, Map<Date, String>> grilla = mostrarEstadoHabitaciones();

        if (grilla == null) return;

        // Límites visuales
        Date fechaInicioGrilla = grilla.values().iterator().next().keySet().stream().min(Date::compareTo).orElse(new Date());
        Date fechaFinGrilla = grilla.values().iterator().next().keySet().stream().max(Date::compareTo).orElse(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        List<DtoEstadia> estadiasParaProcesar = new ArrayList<>();
        boolean deseaCargarOtra = true;

        // --- FASE 1: SELECCIÓN DE HABITACIONES ---
        while (deseaCargarOtra) {
            System.out.println("\n" + Colores.AMARILLO + "┌──────────────────────────────────────────────────┐");
            System.out.println("│               ➕ NUEVA SELECCIÓN                 │");
            System.out.println("└──────────────────────────────────────────────────┘" + Colores.RESET);

            Habitacion habSeleccionada = null;
            Date fechaInicioOcupacion = null;
            Date fechaFinOcupacion = null;

            // Sub-Bucle: Validar selección individual
            while (habSeleccionada == null) {
                System.out.print(Colores.VERDE + "\n   > Ingrese Nro Habitación a Ocupar: " + Colores.RESET);
                String nro = scanner.nextLine().trim().toUpperCase();

                while(nro.isEmpty()){
                    System.out.println(Colores.ROJO +"     ❌ Error: Campo Obligatorio." + Colores.RESET);
                    System.out.print(Colores.VERDE + "   > Ingrese Nro Habitación a Ocupar: " + Colores.RESET);
                    nro = scanner.nextLine().trim().toUpperCase();
                }

                // 1. Validar existencia en la lista
                Habitacion candidata = null;
                for (Habitacion h : grilla.keySet()) {
                    if (h.getNumero().equals(nro)) {
                        candidata = h;
                        break;
                    }
                }

                if (candidata == null) {
                    System.out.println(Colores.ROJO + "     ❌ Error: Habitación no encontrada en la grilla." + Colores.RESET);
                    continue;
                }

                if(candidata.getEstadoHabitacion() == EstadoHabitacion.FUERA_DE_SERVICIO){
                    System.out.println(Colores.ROJO + "     ❌ Error: La habitación está FUERA DE SERVICIO." + Colores.RESET);
                    continue;
                }

                // 2. PEDIR FECHAS
                System.out.println(Colores.CYAN + "\n   Define el rango para la habitación " + nro + ":" + Colores.RESET);

                // Truco: Usamos fechas muy antiguas/lejanas como límites para que 'pedirFechaFutura'
                // solo valide el formato, y nosotros validamos la lógica de negocio abajo.
                // 1. Pedir Fecha Inicio: Debe ser posterior a "ayer" (es decir, de hoy en adelante)
                // Usamos Calendar para restar un día de forma segura y permitir seleccionar "HOY"
                // Calculamos la menor fecha presente en la vista (inicioGrilla)
                Date inicioGrilla;
                Optional<Date> minFechaOpt = grilla.values().stream()
                        .flatMap(m -> m.keySet().stream())
                        .min(Date::compareTo);
                inicioGrilla = minFechaOpt.orElse(new Date()); // si no hay fechas, usamos hoy

                //Conseguimos el limite superior de la fecha de la grilla
                Date finGrilla;
                Optional<Date> maxFechaOpt = grilla.values().stream().flatMap(m->m.keySet().stream()).max(Date::compareTo);
                finGrilla = maxFechaOpt.orElse(inicioGrilla);

                // Como pedirFechaPosteriorA exige 'posterior a' la fecha pasada,
                // pasamos un día anterior para que la selección válida sea >= inicioGrilla.
                LocalDate inicioLocal = inicioGrilla.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate limiteAnterior = inicioLocal.minusDays(1);
                Date fechaLimiteParaPedir = Date.from(limiteAnterior.atStartOfDay(ZoneId.systemDefault()).toInstant());

                // Flag para chequear que no pide una fecha anterior a hoy
                try {
                    fechaInicioOcupacion = pedirFechaEntre(
                            "   > Fecha Inicio (dd/MM/yyyy): ",
                            fechaLimiteParaPedir,  finGrilla ,
                            "La fecha no puede ser anterior a la fecha mínima de la vista.");


                    // 2. Pedir Fecha Fin: Debe ser posterior a la Fecha de Inicio recién ingresada
                    fechaFinOcupacion = pedirFechaEntre(
                            "   > Fecha Fin (dd/MM/yyyy): ",
                            fechaInicioOcupacion, finGrilla,
                            "La fecha de fin debe ser posterior a la fecha de inicio."
                    );
                } catch (CancelacionException e) {
                    System.out.println(Colores.ROJO + "Operación cancelada." + Colores.RESET);
                    return; // Sale del método
                }

                // 3. Validar que esté dentro de lo que vemos en pantalla
                if (fechaInicioOcupacion.before(fechaInicioGrilla) || fechaFinOcupacion.after(fechaFinGrilla)) {
                    System.out.println(Colores.ROJO + "     ⚠️ Error: Las fechas deben estar dentro del rango visualizado (" +
                            sdf.format(fechaInicioGrilla) + " - " + sdf.format(fechaFinGrilla) + ")." + Colores.RESET);
                    continue; // Vuelve a pedir habitación
                }

                // 4. Validar Disponibilidad (BD y Memoria)
                boolean ocupadaBD = gestorEstadia.estaOcupadaEnFecha(candidata.getNumero(), fechaInicioOcupacion, fechaFinOcupacion);
                boolean reservadaBD = gestorReserva.estaReservadaEnFecha(candidata.getNumero(), fechaInicioOcupacion, fechaFinOcupacion);

                boolean ocupadaEnLote = false;
                for (DtoEstadia previa : estadiasParaProcesar) {
                    if (previa.getDtoHabitacion().getNumero().equals(candidata.getNumero())) {
                        if (fechaInicioOcupacion.before(previa.getFechaCheckOut()) && fechaFinOcupacion.after(previa.getFechaCheckIn())) {
                            ocupadaEnLote = true;
                            break;
                        }
                    }
                }

                if (ocupadaEnLote) {
                    System.out.println(Colores.ROJO + "     ❌ Error: Ya seleccionó esta habitación en este proceso." + Colores.RESET);
                } else if (ocupadaBD) {
                    System.out.println(Colores.ROJO + "     ❌ Error: La habitación figura OCUPADA en el sistema." + Colores.RESET);
                } else if (reservadaBD) {
                    System.out.println(Colores.AMARILLO + "     ⚠️ AVISO: Habitación RESERVADA. ¿Es el titular?" + Colores.RESET);
                    System.out.println("     [1] SI (OCUPAR)");
                    System.out.println("     [2] NO (CANCELAR)");
                    System.out.print("     >> Opción: ");

                    int opcionNumerica = leerOpcionNumerica();
                    while(true) {
                        if (opcionNumerica == 1) {
                            habSeleccionada = candidata;
                            break;
                        } else if (opcionNumerica == 2) {
                            System.out.println(Colores.AZUL + "     ↩️ Reserva cancelada, elija otra habitación." + Colores.RESET);
                            break;
                        }
                        System.out.println(Colores.ROJO + "     ❌ Por favor ingrese 1 o 2." + Colores.RESET);
                        System.out.print("     >> Opción: ");
                        opcionNumerica = leerOpcionNumerica();
                    }
                } else {
                    habSeleccionada = candidata; // Libre y fechas válidas -> ÉXITO
                }
            }

            // 5. Guardar en lista temporal
            DtoHabitacion dtoHab = MapearHabitacion.mapearEntidadADto(habSeleccionada);
            DtoEstadia dtoEstadia = new DtoEstadia.Builder()
                    .dtoHabitacion(dtoHab)
                    .fechaCheckIn(fechaInicioOcupacion)
                    .fechaCheckOut(fechaFinOcupacion)
                    .valorEstadia(habSeleccionada.getCostoPorNoche())
                    .build();

            estadiasParaProcesar.add(dtoEstadia);
            System.out.println(Colores.VERDE + "     ✅ Selección guardada." + Colores.RESET);

            // 6. REIMPRIMIR LA GRILLA
            // Mostramos todo lo acumulado hasta ahora + la nueva selección
            pintarHabitacionOcupada(grilla, null, null, estadiasParaProcesar, null);

            boolean flagIngreso = true; //flag por si toca otro boton o ingresa algo distinto a SI o NO
            while(flagIngreso) {
                System.out.print(Colores.AMARILLO + "\n¿Desea ocupar otra habitación? (SI/NO): " + Colores.RESET);
                String resp = scanner.nextLine().trim();
                if (resp.equalsIgnoreCase("SI")) {
                    deseaCargarOtra = true;
                    flagIngreso = false; //no repetimos while
                } else if (resp.equalsIgnoreCase("NO")) {
                    deseaCargarOtra = false;
                    flagIngreso = false; //no repetimos while
                } else {
                    System.out.println(Colores.ROJO + "     ❌ Por favor ingrese SI o NO." + Colores.RESET);
                    flagIngreso = true; //repetimos while
                }
            }
        }

        if (estadiasParaProcesar.isEmpty()) return;

        System.out.println(Colores.CYAN + "\n══════════════════════════════════════════════════════");
        System.out.println("   ⚠️  Procediendo a la carga de HUÉSPEDES para " + estadiasParaProcesar.size() + " habitaciones");
        System.out.println("══════════════════════════════════════════════════════" + Colores.RESET);
        pausa();

        // --- FASE 2: CARGA DE HUÉSPEDES ---
        List<DtoEstadia> estadiasFinales = new ArrayList<>();

        for (DtoEstadia dto : estadiasParaProcesar) {
            System.out.println("\n" + Colores.AZUL + "╔════════════════════════════════════════════════════╗");
            System.out.println("║ 🏠 CARGA PARA HABITACIÓN: " + String.format("%-24s", dto.getDtoHabitacion().getNumero()) + " ║");
            System.out.println("╚════════════════════════════════════════════════════╝" + Colores.RESET);

            ArrayList<DtoHuesped> grupo = seleccionarGrupoHuespedes();

            if (!grupo.isEmpty()) {
                dto.setDtoHuespedes(grupo);
                estadiasFinales.add(dto);
            } else {
                System.out.println(Colores.ROJO + "❌ Se omitirá esta habitación." + Colores.RESET);
            }
        }

        if (estadiasFinales.isEmpty()) return;

        // --- FASE 3: PERSISTENCIA ---
        System.out.println(Colores.AZUL + "\n💾 Guardando ocupaciones..." + Colores.RESET);
        try {
            for (DtoEstadia dto : estadiasFinales) {
                gestorEstadia.crearEstadia(dto);
            }
            System.out.println("\n" + Colores.VERDE + "✅ ¡Check-in masivo realizado con ÉXITO!" + Colores.RESET);
            pausa();
        } catch (Exception e) {
            System.out.println("\n" + Colores.ROJO + "╔═════════════════════════════════════════════╗");
            System.out.println("║ ❌ ERROR CRÍTICO AL GUARDAR                 ║");
            System.out.println("╚═════════════════════════════════════════════╝" + Colores.RESET);
            System.out.println(Colores.ROJO + "Detalle: " + e.getMessage() + Colores.RESET);
            pausa();
        }
    }
    // --- SUB-METODO PARA SELECCIONAR HUÉSPEDES (Con distinción visual) ---
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
            ArrayList<Huesped> res = gestorHuesped.buscarHuespedes(criterios);

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





}