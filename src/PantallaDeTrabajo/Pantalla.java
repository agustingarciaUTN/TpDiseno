package PantallaDeTrabajo;
import Dominio.Habitacion;
import Dominio.Huesped;
import Estadia.GestorEstadia;
import Habitacion.GestorHabitacion;
import Huesped.*;
import Reserva.DtoReserva;
import Reserva.GestorReserva;
import Utils.Mapear.MapearHuesped;
import enums.EstadoReserva;
import enums.PosIva;
import enums.TipoDocumento;
import Usuario.*;

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


    //constructor (hay que ver como lo vamos a llamar)
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
    public void iniciarSistema() throws PersistenciaException {
        System.out.println("========================================");
        System.out.println("   SISTEMA DE GESTION HOTELERA");
        System.out.println("========================================\n");

        //Primero autenticar
        if (autenticarUsuario()) {
            //Si la autenticacion es exitosa, mostrar menu principal
            mostrarMenuPrincipal();
        } else {
            System.out.println("No se pudo acceder al sistema.");
        }

        System.out.println("\n========================================");
        System.out.println("   FIN DEL SISTEMA");
        System.out.println("========================================");
    }

    //METODO PARA CU AUTENTICAR USUARIO
    private boolean autenticarUsuario() {
        System.out.println("-- AUTENTICACION DE USUARIO --\n");

        boolean autenticacionExitosa = false;

        while (!autenticacionExitosa) {
            //Paso 2: El sistema presenta la pantalla para autenticar al usuario
            System.out.println("Por favor, ingrese sus credenciales:");

            //Paso 3: El actor ingresa su nombre (en forma visible) y su contraseña (oculta)
            System.out.print("Nombre de usuario: ");
            String nombre = scanner.nextLine().trim();

            System.out.print("Contraseña: ");
            String contrasenia = scanner.nextLine(); //en consola no se puede ocultar realmente

            //Validar con el gestor
            boolean credencialesValidas = gestorUsuario.autenticarUsuario(nombre, contrasenia);

            if (credencialesValidas) {
                //Autenticacion exitosa
                this.usuarioAutenticado = true;
                this.nombreUsuarioActual = nombre;
                System.out.println("\n¡Autenticación exitosa! Bienvenido, " + nombre + "\n");
                autenticacionExitosa = true;
            } else {
                //Paso 3.A: El usuario o la contraseña son inválidos
                //Paso 3.A.1: El sistema muestra el mensaje de error
                System.out.println("\n*** ERROR ***");
                System.out.println("El usuario o la contraseña no son válidos");
                System.out.println("*************\n");

                //Paso 3.A.2: El actor cierra la pantalla de error
                System.out.print("Presione ENTER para continuar...");
                System.out.print("\033[H\033" +
                        "[2J");
                System.out.flush();
                scanner.nextLine();

                //Paso 3.A.3: El sistema blanquea los campos (se hace automáticamente al repetir el ciclo)

                //Preguntar qué desea hacer
                System.out.println("\n¿Qué desea hacer?");
                System.out.println("1. Volver a ingresar credenciales");
                System.out.println("2. Cerrar el sistema");
                System.out.print("Ingrese una opción: ");

                int opcion;
                try {
                    opcion = scanner.nextInt();
                    scanner.nextLine(); //consumir salto de linea
                } catch (Exception e) {
                    scanner.nextLine(); //limpiar buffer
                    System.out.println("\nOpción inválida. Intente nuevamente.\n");
                    continue;
                }

                if (opcion == 2) {
                    System.out.println("\nCerrando el sistema...");
                    return false; //Sale sin autenticar
                } else if (opcion == 1) {
                    System.out.println("\n-- Intente nuevamente --\n");
                    //Paso 3.A.4: El CU continua en el paso 2 (se repite el while)
                } else {
                    System.out.println("\nOpción inválida. Intente nuevamente.\n");
                }
            }
        }

        return true;
    }

    //METODO PARA MOSTRAR MENU PRINCIPAL
    private void mostrarMenuPrincipal() throws PersistenciaException {
        //Paso 4: El sistema presenta la pantalla principal
        boolean salir = false;

        while (!salir && usuarioAutenticado) {
            System.out.println("========================================");
            System.out.println("        MENU PRINCIPAL");
            System.out.println("========================================");
            System.out.println("Usuario: " + nombreUsuarioActual);
            System.out.println("----------------------------------------");
            System.out.println("1. Buscar huesped (CU2)");
            System.out.println("2. ");
            System.out.println("2. Dar de alta huesped (CU9)");
            System.out.println("3. Dar de baja huesped (CU11) ");
            System.out.println("4. Cerrar sesión");
            System.out.println("========================================");
            System.out.print("Ingrese una opción: ");

            int opcion = -1;
            try {
                opcion = scanner.nextInt();
                scanner.nextLine(); //consumir salto de linea
            } catch (Exception e) {
                scanner.nextLine(); //limpiar buffer
                System.out.println("\nOpción inválida. Intente nuevamente.\n");
                continue;
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
                    System.out.print("¿Está seguro que desea cerrar sesión? (SI/NO): ");
                    String confirmar = scanner.nextLine().trim();
                    if (confirmar.equalsIgnoreCase("SI")) {
                        System.out.println("\nCerrando sesión...\n");
                        salir = true;
                        usuarioAutenticado = false;
                    }
                    break;
                default:
                    System.out.println("Opción inválida. Intente nuevamente.\n");
            }
        }
        //Paso 5: El CU termina
    }

    // CU9
    public void darDeAltaHuesped() {

        //Mensaje de principio de ejecucion del CU9
        System.out.println('\n' + "-- Iniciando CU9 'dar de alta huesped' --");

        boolean continuarCargando = true;//bandera que representa la condicion del loop principal

        while (continuarCargando) {//loop principal

            //metodo Pantalla -> Conserje para mostrar formulario y pedir datos
            DtoHuesped datosIngresados = mostrarYPedirDatosFormulario();

            //parametros para manejar la seleccion de SIGUIENTE o CANCELAR
            int opcionBoton = -1;
            boolean opcionValida = false;

            //Luego de la carga de datos, se selecciona la opcion SIGUIENTE o CANCELAR
            while (!opcionValida) {//Bucle interno para la validacion de la seleccion
                System.out.println("Acciones: 1 = SIGUIENTE, 2 = CANCELAR");
                System.out.print("Ingrese una opción: ");

                try {
                    String entrada = scanner.nextLine(); // Leemos como String para no renegar con el buffer
                    opcionBoton = Integer.parseInt(entrada);//Parseamos a integer

                    if (opcionBoton == 1 || opcionBoton == 2) {
                        opcionValida = true;
                    } else {
                        System.out.println("Opción inválida. Ingrese 1 (SIGUIENTE) o 2 (CANCELAR).");
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Debe ingresar un número.");
                }
            }

            if (opcionBoton == 1) {//presiono SIGUIENTE
                System.out.println("Procesando datos...");


                //aca hay que llamar al gestor para que valide los datos
                List<String> errores = new ArrayList<>();
                //Metodo que retorna una lista de todos los errores en la validacion de negocio
                errores = gestorHuesped.validarDatosHuesped(datosIngresados);

                //Actuamos en consecuencia, dependiendo si hubo errores o no
                if (!errores.isEmpty()) {
                    System.out.println("ERROR: Se encontraron los siguientes errores: ");
                    for (String error : errores) {
                        System.out.println("- " + error);
                    }
                    System.out.println("Por favor, ingrese los datos nuevamente");
                    continue; //fuerza al inicio del while
                }

                //Si no hubo errores de validacion de negocio, seguimos
                try {
                    //Debemos fijarnos en la DB si existe un Huesped con el mismo TipoDoc y NroDoc que el ingresado
                    //Le pasamos al gestorHuesped un DTO con el huesped ingresado
                    DtoHuesped duplicado = gestorHuesped.chequearDuplicado(datosIngresados);
                    //Si chequearDuplicado retorna NULL, no hay duplicado

                    if (duplicado != null) {//si encuentra duplicado
                        System.out.println("----------------------------------------------------------------");
                        System.out.println("   ¡CUIDADO! El tipo y número de documento ya existen en el sistema:");
                        System.out.println("   Huésped existente: " + duplicado.getNombres() + " " + duplicado.getApellido());
                        System.out.println("----------------------------------------------------------------");
                        System.out.println("Opciones: 1 = ACEPTAR IGUALMENTE, 2 = CORREGIR");
                        System.out.println("Ingrese una opción: ");

                        //Parámetros para bucle interno
                        int opcionDuplicado = -1;
                        boolean opcionValida2 = false;

                        //Bucle para validar la entrada ACEPTAR IGUALMENTE o CORREGIR
                        while (!opcionValida2) {
                            System.out.println("Opciones: 1 = ACEPTAR IGUALMENTE, 2 = CORREGIR");
                            System.out.print("Ingrese una opción: ");

                            try {
                                String entrada = scanner.nextLine();
                                opcionDuplicado = Integer.parseInt(entrada);

                                if (opcionDuplicado == 1 || opcionDuplicado == 2) {
                                    opcionValida2 = true; // Salimos del bucle
                                } else {
                                    System.out.println("Opción inválida. Ingrese 1 (ACEPTAR IGUALMENTE) o 2 (CORREGIR).");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Debe ingresar un número.");
                            }
                        }

                        if (opcionDuplicado == 2) { // Eligió CORREGIR
                            System.out.println("Seleccionó CORREGIR. Vuelva a ingresar los datos.");
                            continue; // Vuelve al inicio del while para pedir de nuevo
                        }
                        // Si elige 1 (ACEPTAR IGUALMENTE), no hacemos nada y el código sigue
                    }


                    //Si no existen duplicados, INSERT. Si existe (y se seleccionó "aceptar igualmente"), UPDATE
                    gestorHuesped.upsertHuesped(datosIngresados);


                } catch (PersistenciaException e) {
                    System.out.println("ERROR DE BASE DE DATOS: No se pudo verificar el duplicado.");
                    e.printStackTrace();
                    continue; // Volver a empezar
                }

                System.out.println("El huésped '" + datosIngresados.getNombres() + " " + datosIngresados.getApellido() + "' ha sido satisfactoriamente cargado al sistema. ¿Desea cargar otro? (SI/NO)");

                System.out.println("¿Desea cargar otro huésped? (SI/NO): ");

                //validacion de ingreso correcto
                String ingresoOtroHuesped = scanner.nextLine();
                while (!ingresoOtroHuesped.equalsIgnoreCase("NO") && !ingresoOtroHuesped.equalsIgnoreCase("SI")) {
                    System.out.println("Ingreso invalido. ¿Desea cargar otro huésped? (SI/NO): ");
                    ingresoOtroHuesped = scanner.nextLine();
                }

                //si ingreso NO termina el bucle, si ingreso SI se repite
                if (ingresoOtroHuesped.equalsIgnoreCase("NO")) {
                    continuarCargando = false;
                }

            } else {//presiono CANCELAR
                System.out.println("¿Desea cancelar el alta del huésped? (SI/NO): ");

                //validación de ingreso correcto
                String ingresoCancelarAlta = scanner.nextLine();
                while (!ingresoCancelarAlta.equalsIgnoreCase("NO") && !ingresoCancelarAlta.equalsIgnoreCase("SI")) {
                    System.out.println("Ingreso invalido. ¿Desea cancelar el alta de huésped? (SI/NO): ");
                    ingresoCancelarAlta = scanner.nextLine();
                }

                if (ingresoCancelarAlta.equalsIgnoreCase("SI")) {
                    System.out.println("Alta cancelada.");
                    continuarCargando = false;//termina el bucle
                }
                //si ingresa NO, el bucle se repite y vuelve a pedir los datos (no se si esta bien que tenga que ingresar desde 0)
            }
        }//fin while

        System.out.println("-- Fin CU9 'dar de alta huésped' ---");
    }//fin CU9 darDeAltaHuesped

    //metodo privado para pedir los datos del huesped a dar de alta, CU9 (formulario)
    private DtoHuesped mostrarYPedirDatosFormulario() {

        System.out.println('\n' + "INGRESE LOS DATOS DEL HUÉSPED A REGISTRAR");

        //Cada uno de estos metodos solicita por teclado el ingreso de cada campo del formulario
        //Ademas, se hace una VALIDACION DE FORMATO (que el email tenga @, que el DNI sean números, que la fecha sea válida)
        //en el momento, evitando datos sin sentido

        //Las validaciones de negocio las realizara el Gestor

        String apellido = pedirStringTexto("Apellido: ");

        String nombres = pedirStringTexto("Nombres: ");

        TipoDocumento tipoDocumento = pedirTipoDocumento();

        String numeroDocumento = pedirDocumento(tipoDocumento);

        String cuit = pedirCUIT();

        String posIva = pedirPosIva();

        Date fechaNacimiento = pedirFecha();

        String calleDireccion = pedirStringComplejo("Calle: ");

        Integer numeroDireccion = pedirEntero("Número de calle: ");

        String departamentoDireccion = pedirStringOpcional("Departamento (opcional, presione Enter para omitir): ");

        String pisoDireccion = pedirStringOpcional("Piso (opcional, presione Enter para omitir): ");

        Integer codPostalDireccion = pedirEntero("Código Postal: ");

        String localidadDireccion = pedirStringComplejo("Localidad: ");

        String provinciaDireccion = pedirStringComplejo("Provincia: ");

        String paisDireccion = pedirStringTexto("Pais: ");

        Long telefono = pedirLong();

        String email = pedirEmail();

        String ocupacion = pedirStringTexto("Ocupación: ");

        String nacionalidad = pedirStringTexto("Nacionalidad: ");

        //casteo los wrappers (necesarios para las validaciones) a primitivos para su posterior uso en la app
        int numeroDireccionPrimitivo = numeroDireccion;
        int codPostalDireccionPrimitivo = codPostalDireccion;

        // Crear los DTO  (aún no tenemos el ID de dirección, no fuimos a la DB todavia, se inicia en NULL por defecto en la clase)
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
                .posicionIva(PosIva.valueOf(posIva))
                .fechaNacimiento(fechaNacimiento) 
                .email(Collections.singletonList(email))
                .ocupacion(Collections.singletonList(ocupacion))
                .nacionalidad(nacionalidad)
                .direccion(direccionDto)
                .build();

        //asociamos la direccion con el huesped
        huespedDto.setDtoDireccion(direccionDto);

        System.out.println("--- Fin Formulario ---");
        return huespedDto; // Devolver el DTO con los datos cargados (incluyendo la direccion correspondiente)

    }

    //=== Metodos para pedir Y VALIDAR cada tipo de dato, CU9 ===

    //Solicitar y Validar String complejo (calle, provincia, localidad)
    private String pedirStringComplejo(String mensaje) {
        String entrada;
        while (true) {
            System.out.print(mensaje);
            entrada = scanner.nextLine();
            if (entrada.trim().isEmpty()) {
                System.out.println("Error: Este campo es obligatorio.");
            } else if (!entrada.matches("^[\\\\p{L}0-9 ]+$")) { // Letras Unicode + Números + Espacios
                System.out.println("Error: Solo se admiten letras, números y espacios. No se permiten caracteres especiales.");
            } else {
                return entrada.trim();
            }
        }
    }

    //Solicitar y Validar String simple (nombres, apellidos, etc)
    private String pedirStringTexto(String mensaje) {
        String entrada;
        while (true) {
            System.out.print(mensaje);
            entrada = scanner.nextLine();

            if (entrada.trim().isEmpty()) {//Validamos obligatoriedad del campo
                System.out.println("Error: Este campo es obligatorio.");

                // Esta expresion ^[\p{L} ]+$ permite cualquier letra de cualquier idioma
                // y espacios, pero no números ni caracteres especiales.
            } else if (!entrada.matches("^[\\p{L} ]+$")) {//cualquier letra Unicode
                System.out.println("Error: Solo se admiten letras y espacios.");

            } else {
                return entrada.trim();//Elimina los caracteres de espacio en blanco al principio y al final de la cadena
            }
        }
    }

    //Solicitar y Validar String opcional (dpto, piso)
    private String pedirStringOpcional(String mensaje) {
        String entrada;
        // La expresion permite letras (a-z, A-Z), números (0-9) y espacios.
        String str = "^[a-zA-Z0-9 ]+$";

        while (true) {
            System.out.print(mensaje);
            entrada = scanner.nextLine();

            //Si está vacío, es válido (opcional)
            if (entrada.trim().isEmpty()) {
                return null;

                //Si no está vacío, valida el formato
            } else if (!entrada.matches(str)) {
                System.out.println("Error: Solo se admiten letras, números y espacios. No se permiten caracteres especiales.");

            } else {
                return entrada;
            }
        }
    }

    private Integer pedirEntero(String mensaje) {
        Integer valor = null; // Usamos la clase wrapper para permitir null
        boolean valido = false;

        while (!valido) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim(); // leemos siempre como String

            if (entrada.isEmpty()) {
                System.out.println("Error: Este campo es obligatorio. No se puede omitir.");
                continue;
            }
            try {
                int num = Integer.parseInt(entrada);
                if (num <= 0) {
                    System.out.println("Error: Ingrese un número positivo.");
                } else {
                    valor = num;
                    valido = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un número entero válido.");
            }
        }
        return valor;
    }

    private Long pedirLong() { // Devuelve Long (wrapper)
        Long valor = null; // Usamos la clase wrapper Long
        boolean valido = false;
        while (!valido) {
            System.out.print("Teléfono: ");
            String entrada = scanner.nextLine(); // Leemos siempre como String

            if (entrada.trim().isEmpty()) {
                System.out.println("Error: Este campo es obligatorio. No se puede omitir.");
                continue;
            } else {
                try {
                    valor = Long.parseLong(entrada); // Intentamos convertir String a long
                    valido = true;      // Si funciona, es válido
                } catch (NumberFormatException e) {
                    System.out.println("Error: Ingrese un número entero válido o presione Enter para omitir.");
                }
            }
        }
        return valor;
    }

    private String pedirCUIT() {
        String cuit;
        // Expresion para CUIT: 2 dígitos, un guión o barrita, 8 dígitos, un guión o barrita, 1 dígito.
        String expresionCUIT = "^\\d{2}-\\d{8}-\\d{1}$";

        while (true) {
            System.out.print("CUIT (opcional, formato XX-XXXXXXXX-X, presione Enter para omitir): ");
            cuit = scanner.nextLine();
            //Si está vacío, es válido (opcional)
            if (cuit.trim().isEmpty()) {
                return null;
                //Si no está vacío, valida el formato
            } else if (!cuit.matches(expresionCUIT)) {
                System.out.println("Error: Formato de CUIT incorrecto. Debe ser XX-XXXXXXXX-X");
            } else {
                return cuit;
            }
        }
    }

    private String pedirEmail() {
        String email;
        // expresion simple para emails: algo@algo.algo
        String expresionEmail = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

        while (true) {
            System.out.print("Email (opcional, presione Enter para omitir): ");
            email = scanner.nextLine();

            if (email.trim().isEmpty()) {
                return null; // Válido (opcional)

            } else if (!email.matches(expresionEmail)) {
                System.out.println("Error: Formato de email no válido.");

            } else {
                return email; // Válido
            }
        }
    }

    private Date pedirFecha() {
        Date fecha = null;
        boolean valida = false;
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        formatoFecha.setLenient(false);

        while (!valida) {
            System.out.print("Fecha de Nacimiento " + " (formato dd/MM/yyyy): ");
            String fechaStr = scanner.nextLine();

            if (fechaStr.trim().isEmpty()) {
                System.out.println("Error: Este campo es obligatorio.");
                continue;
            } else {
                try {
                    fecha = formatoFecha.parse(fechaStr);
                    // Convertir a LocalDate para comparar solo la fecha (sin hora)
                    LocalDate fechaLocal = Instant.ofEpochMilli(fecha.getTime())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    LocalDate hoy = LocalDate.now();

                    if (!fechaLocal.isBefore(hoy)) {
                        System.out.println("Error: La fecha debe ser anterior a hoy. Ingrese una fecha pasada.");
                        continue;
                    }
                    valida = true; // Formato válido
                } catch (ParseException e) {
                    System.out.println("Error: Formato de fecha inválido. Use dd/MM/yyyy o presione Enter para omitir.");
                }
            }
        }
        return fecha;
    }

    private TipoDocumento pedirTipoDocumento() {
        TipoDocumento tipoDoc = null;
        boolean valido = false;

        // Mostrar opciones válidas construyendo un String
        StringBuilder opciones = new StringBuilder("Tipo de Documento (");
        TipoDocumento[] valores = TipoDocumento.values();
        for (int i = 0; i < valores.length; i++) {
            opciones.append(valores[i].name()); // .name() devuelve el nombre del enum (DNI, LE, etc.)
            if (i < valores.length - 1) {
                opciones.append("/");
            }
        }
        opciones.append("): ");

        while (!valido) {
            System.out.print(opciones.toString());
            String tipoDocStr = scanner.nextLine().toUpperCase().trim(); // A mayúsculas y sin espacios al inicio y final
            if (tipoDocStr.isEmpty()) {
                System.out.println("Error: El tipo de documento es obligatorio.");
                continue;
            } else {
                try {
                    tipoDoc = TipoDocumento.valueOf(tipoDocStr);
                    valido = true; // Opción válida
                } catch (IllegalArgumentException e) {
                    System.out.println("Error: Tipo de documento inválido. Ingrese una de las opciones.");
                }
            }
        }
        return tipoDoc;
    }

    private String pedirDocumento(TipoDocumento tipo) {
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

    private String pedirPosIva() {
        String posIva = null;
        boolean valido = false;


        while (!valido) {
            System.out.println("""
                    Posición frente al IVA (1.Consumidor Final (por defecto),
                     2.Monotributista,\s
                    3.Responsable Inscripto,\s
                    4.Excento)""");
            int opcion = Integer.parseInt(scanner.nextLine());

            // Permitir Enter para el valor por defecto
            if (opcion == 0) {
                posIva = PosIva.ConsumidorFinal.toString(); // Asignar el default
                valido = true;
            } else {
                try {
                    valido = switch (opcion) {
                        case 1 -> {
                            posIva = PosIva.ConsumidorFinal.toString();
                            yield true;
                        }
                        case 2 -> {
                            posIva = PosIva.Monotributista.toString();
                            yield true;
                        }
                        case 3 -> {
                            posIva = PosIva.ResponsableInscripto.toString();
                            yield true;
                        }
                        case 4 -> {
                            posIva = PosIva.Excento.toString();
                            yield true;
                        }
                        default -> false;
                    };

                } catch (IllegalArgumentException e) {
                    System.out.println("Error: Posición IVA inválida. Ingrese una opción válida o Enter para ConsumidorFinal.");
                }
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
    public void buscarHuesped() throws PersistenciaException {
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
            criterios.setNroDocumento(pedirDocumento(criterios.getTipoDocumento()));
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

    private String validarYLeerNumeroDocumento(TipoDocumento tipoDoc) {
        while (true) {
            System.out.print("Número de Documento: ");
            String numeroStr = scanner.nextLine().trim();

            if (numeroStr.isEmpty()) {
                return ""; // Se devuelve 0 si se omite
            }

            try {


                // VALIDACIÓN DE RANGO SEGÚN TIPO DE DOCUMENTO
                if (tipoDoc == TipoDocumento.DNI) {
                    long numero = Long.parseLong(numeroStr.trim());
                    if (numero < 0 || numero > 99999999) {
                        System.out.println("El DNI debe estar entre 0 y 99.999.999. Intente nuevamente.");
                        continue;
                    }
                } else if (tipoDoc == TipoDocumento.LE || tipoDoc == TipoDocumento.LC) {
                    long numero = Long.parseLong(numeroStr.trim());
                    if (numero < 0 || numero > 99999999) {
                        System.out.println("La " + tipoDoc.name() + " debe estar entre 0 y 99.999.999. Intente nuevamente.");
                        continue;
                    }
                } else if (tipoDoc == TipoDocumento.PASAPORTE) {
                    if (numeroStr.isBlank()) {
                        System.out.println("Debe ingresar un Pasaporte. Intente nuevamente.");
                        continue;
                    }
                }

                return numeroStr;

            } catch (NumberFormatException e) {
                System.out.println("⚠ El número de documento debe ser un valor numérico. Intente nuevamente.");
            }
        }
    }//NO SE DE QUE SON ESTOS METODOS

    private void seleccionarHuespedDeLista(ArrayList<Huesped> listaDtoHuespedes) throws PersistenciaException {

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

    private int leerOpcionNumerica() {/
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            return -1; // Devuelve un valor inválido si el usuario no ingresa un número
        } finally {
            scanner.nextLine(); // Limpia el buffer del scanner
        }
    }//VER CUANDO SE UTILIZA ESTO, EN EL CU9 NO LO USE



    // GRILLA, CU 5
    public void mostrarGrillaDisponibilidad() {
        System.out.println("\n========================================");
        System.out.println("   CONSULTA DE DISPONIBILIDAD (GRILLA)");
        System.out.println("========================================\n");

        // A. Pedir Fechas (Usamos un helper que permita fechas futuras)
        System.out.println("Ingrese el rango de fechas a consultar:");
        Date fechaInicio = pedirFechaFutura("Fecha de Inicio");
        Date fechaFin = pedirFechaFutura("Fecha de Fin");

        // B. Validar Lógica de Negocio (Rango coherente)
        try {
            // Delegamos la validación al Gestor
            gestorHabitacion.validarRangoFechas(fechaInicio, fechaFin);
        } catch (IllegalArgumentException e) {
            System.out.println("Error en el rango de fechas: " + e.getMessage());
            pausa();
            return;
        }

        // C. Obtener Columnas (Habitaciones)
        ArrayList<Habitacion> habitaciones = gestorHabitacion.obtenerTodasLasHabitaciones();

        if (habitaciones.isEmpty()) {
            System.out.println("No hay habitaciones registradas en el sistema.");
            pausa();
            return;
        }

        System.out.println("\nCargando disponibilidad...\n");

        // D. Dibujar la Grilla
        dibujarGrilla(habitaciones, fechaInicio, fechaFin);

        pausa();
    }

    /**
     * Solicita la fecha de inicio en bucle hasta que sea válida (formato y >= hoy).
     */
    private Date validarFechaInicio() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);

        while (true) {
            System.out.print("Ingrese Fecha de Inicio (dd/MM/yyyy): ");
            String input = scanner.nextLine().trim();

            try {
                Date fecha = sdf.parse(input);

                // Validar que no sea anterior a hoy (para disponibilidad futura)
                if (fecha.before(getStartOfDay(new Date()))) {
                    System.out.println("La fecha de inicio no puede ser anterior al día de hoy.");
                    continue;
                }
                return fecha;

            } catch (ParseException e) {
                System.out.println("Formato inválido. Use dd/MM/yyyy.");
            }
        }
    }

    /**
     * Solicita la fecha de fin en bucle y valida lógica de negocio con el Gestor.
     */
    private Date validarFechaFin(Date fechaInicio) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);

        while (true) {
            System.out.print("Ingrese Fecha de Fin (dd/MM/yyyy): ");
            String input = scanner.nextLine().trim();

            try {
                Date fechaFin = sdf.parse(input);

                // Validar lógica de negocio con el Gestor (Rango válido, no excesivo, etc)
                try {
                    gestorHabitacion.validarRangoFechas(fechaInicio, fechaFin);
                    // Si no lanza excepción, el rango es válido, retornamos la fecha.
                    return fechaFin;
                } catch (IllegalArgumentException e) {
                    System.out.println("Error de lógica: " + e.getMessage());
                }

            } catch (ParseException e) {
                System.out.println("Formato inválido. Use dd/MM/yyyy.");
            }
        }
    }


    public String obtenerEstadoParaFecha(Habitacion habitacion, Date fecha) {
        String estado;
        if (habitacion.getEstadoHabitacion().name().equals("FUERA DE SERVICIO")) {
            estado = "FUERA DE SERVICIO";
        } else if (gestorEstadia.estaOcupadaEnFecha(habitacion.getNumero(), fecha, fecha)) {
            estado = "OCUPADA";
        } else if (gestorReserva.estaReservadaEnFecha(habitacion.getNumero(), fecha, fecha)) {
            estado = "RESERVADA";
        } else {
            estado = "LIBRE";
        }
        return estado;
    }

    private void dibujarGrilla(List<Habitacion> habitaciones, Date inicio, Date fin) {
        String formatoFecha = "%-12s";
        String formatoCelda = "| %-9s ";

        // Conversión a LocalDate para iterar cómodamente
        LocalDate inicioLocal = inicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate finLocal = fin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        DateTimeFormatter formateador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // 1. Imprimir Encabezado (Habitaciones)
        System.out.print("             "); // Espacio para la columna de fecha
        for (Habitacion hab : habitaciones) {
            System.out.printf(formatoCelda, "Hab " + hab.getNumero());
        }
        System.out.println("|");

        imprimirSeparador(habitaciones.size());

        // 2. Bucle Principal (Días)
        LocalDate actual = inicioLocal;
        while (!actual.isAfter(finLocal)) {
            // Imprimir Fecha (Fila)
            System.out.printf(formatoFecha, actual.format(formateador));

            // Convertir fecha actual a Date para consultar al Gestor
            Date fechaConsulta = Date.from(actual.atStartOfDay(ZoneId.systemDefault()).toInstant());

            // 3. Bucle Interno (Celdas/Habitaciones)
            for (Habitacion hab : habitaciones) {
                // ORQUESTACIÓN: La pantalla pregunta el estado para esa celda específica
                String estado = obtenerEstadoParaFecha(hab, fechaConsulta);

                // Mapeo visual
                String visual = switch (estado) {
                    case "OCUPADA" -> "[ X ]";     // Ocupación Real
                    case "RESERVADA" -> "[ R ]";   // Reserva Futura
                    case "FUERA DE SERVICIO" -> "[ - ]"; // Rota
                    case "LIBRE" -> "[ L ]";       // Disponible
                    default -> "[ ? ]";
                };

                // visual = colorizarEstado(visual, estado);

                System.out.printf(formatoCelda, visual);
            }
            System.out.println("|"); // Fin de fila

            actual = actual.plusDays(1); // Siguiente día
        }

        imprimirSeparador(habitaciones.size());
        System.out.println("REFERENCIAS: [L]ibre | [R]eservada | [X]Ocupada | [-]Fuera de Servicio");
    }

    // --- Helpers Auxiliares ---

    private void imprimirSeparador(int columnas) {
        System.out.print("------------"); // Ancho columna fecha
        for (int i = 0; i < columnas; i++) {
            System.out.print("+-----------"); // Ancho columna celda
        }
        System.out.println("+");
    }

    // Versión de pedirFecha que permite fechas futuras (a diferencia de la de nacimiento)
    private Date pedirFechaFutura(String mensaje) {
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        formatoFecha.setLenient(false);

        while (true) {
            System.out.print(mensaje + " (dd/MM/yyyy): ");
            String entrada = scanner.nextLine().trim();

            if (entrada.isEmpty()) {
                System.out.println("Error: La fecha es obligatoria.");
                continue;
            }

            try {
                return formatoFecha.parse(entrada);
            } catch (ParseException e) {
                System.out.println("Error: Formato inválido. Use dd/MM/yyyy.");
            }
        }
    }

    private Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    // CU4: Reservar Habitación
    public void reservarHabitacion() throws PersistenciaException {
        System.out.println("========================================");
        System.out.println("   CU4: RESERVAR HABITACIÓN");
        System.out.println("========================================\n");

        // 1. Solicitar rango de fechas
        System.out.println("-- Consultando Disponibilidad --");
        Date fechaInicio = pedirFechaFutura("Fecha de Inicio");
        Date fechaFin = pedirFechaFutura("Fecha de Fin");

        // Validar lógica de fechas (regla de negocio)
        try {
            gestorHabitacion.validarRangoFechas(fechaInicio, fechaFin);
        } catch (IllegalArgumentException e) {
            System.out.println("Error en fechas: " + e.getMessage());
            return;
        }

        // 2. Mostrar la grilla inicial (Estado actual de la BDD)
        ArrayList<Habitacion> todasLasHabitaciones = gestorHabitacion.obtenerTodasLasHabitaciones();
        if (todasLasHabitaciones.isEmpty()) {
            System.out.println("No hay habitaciones registradas en el sistema.");
            return;
        }

        System.out.println("\n--- Disponibilidad Actual ---");
        dibujarGrilla(todasLasHabitaciones, fechaInicio, fechaFin);

        // Lista para acumular las reservas que el usuario va seleccionando en memoria
        List<DtoReserva> listaReservasSolicitadas = new ArrayList<>();
        boolean continuarSeleccionando = true;

        // 3. Bucle de Selección
        while (continuarSeleccionando) {
            System.out.println("\n¿Desea seleccionar una habitación para reservar? (SI/NO): ");
            String respuesta = scanner.nextLine().trim();

            if (!respuesta.equalsIgnoreCase("SI")) {
                continuarSeleccionando = false;
                break; // Sale del bucle para proceder a confirmar o pintar
            }

            // A -> C: seleccionarHabitacion(...)
            System.out.print("Ingrese Número de Habitación a reservar: ");
            String nroHabitacion = scanner.nextLine().trim();

            // Verificar que la habitación existe en la lista cargada
            Habitacion habSeleccionada = todasLasHabitaciones.stream()
                    .filter(h -> h.getNumero().equals(nroHabitacion))
                    .findFirst()
                    .orElse(null);

            if (habSeleccionada == null) {
                System.out.println("Error: La habitación ingresada no existe.");
                continue;
            }

            // C -> C: validarDisponibilidadDeSeleccion(...)
            // Verificamos si está ocupada en BDD (Estadía o Reserva) o si está rota
            boolean ocupadaPorEstadia = gestorEstadia.estaOcupadaEnFecha(nroHabitacion, fechaInicio, fechaFin);
            boolean ocupadaPorReserva = gestorReserva.estaReservadaEnFecha(nroHabitacion, fechaInicio, fechaFin);
            boolean fueraDeServicio = habSeleccionada.getEstadoHabitacion().name().equals("FUERA_DE_SERVICIO");

            // También verificamos si YA la seleccionamos en este mismo proceso (para no duplicar)
            boolean yaSeleccionadaAhora = listaReservasSolicitadas.stream()
                    .anyMatch(r -> r.getIdHabitacion().equals(nroHabitacion));

            if (ocupadaPorEstadia || ocupadaPorReserva || fueraDeServicio || yaSeleccionadaAhora) {
                // C -> A: mostrarError("Habitaciones seleccionadas no están Disponibles")
                System.out.println("ERROR: La habitación seleccionada NO está disponible en esas fechas (o ya fue seleccionada).");
            } else {
                // Si está libre, creamos el DTO temporal y lo guardamos en la lista
                DtoReserva dto = new DtoReserva.Builder()
                        .idHabitacion(nroHabitacion)
                        .fechaDesde(fechaInicio)
                        .fechaHasta(fechaFin)
                        .estado(EstadoReserva.ACTIVA)
                        .fechaReserva(new Date()) // Fecha de hoy
                        .build();

                listaReservasSolicitadas.add(dto);
                System.out.println(">> Habitación " + nroHabitacion + " agregada a su selección temporal.");
            }
        }

        // Si no seleccionó nada, terminamos
        if (listaReservasSolicitadas.isEmpty()) {
            System.out.println("No se seleccionaron habitaciones. Finalizando caso de uso.");
            return;
        }

        // 4. ACTUALIZACIÓN VISUAL (Lo que pediste específicamente)
        // C -> C: pintarHabitacionesReservada(...)
        // Se vuelve a dibujar la grilla, pero ahora mostrando las habitaciones seleccionadas con [ * ]
        pintarHabitacionesReservada(todasLasHabitaciones, fechaInicio, fechaFin, listaReservasSolicitadas);

        // Mostrar resumen textual
        System.out.println("\n--- Resumen de Reservas a Generar ---");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (DtoReserva r : listaReservasSolicitadas) {
            System.out.println("Habitación: " + r.getIdHabitacion());
            System.out.println(" - Ingreso: " + sdf.format(r.getFechaDesde()) + " 12:00hs");
            System.out.println(" - Egreso:  " + sdf.format(r.getFechaHasta()) + " 10:00hs");
            System.out.println("-------------------------------------");
        }

        // 5. Confirmación (Botones ACEPTAR / RECHAZAR)
        System.out.println("¿Confirma la operación?");
        System.out.println("1. ACEPTAR");
        System.out.println("2. RECHAZAR");
        System.out.print("Ingrese opción: ");
        int opcion = leerOpcionNumerica();

        if (opcion != 1) {
            // RECHAZAR: Limpiamos y salimos
            System.out.println("Operación Rechazada. Se cancelan las selecciones.");
            return;
        }

        // 6. Solicitar Datos del Responsable (si presionó ACEPTAR)
        System.out.println("\n--- Datos del Responsable de la Reserva ---");

        // Usamos métodos de entrada
        String apellidoResponsable = pedirStringTexto("Apellido: ");
        String nombreResponsable = pedirStringTexto("Nombre: ");
        System.out.print("Teléfono: ");
        Long tel = pedirLong();
        String telefonoResponsable = String.valueOf(tel);

        // Actualizamos todos los DTO de la lista con estos datos comunes
        for (DtoReserva dto : listaReservasSolicitadas) {
            dto.setApellidoHuespedResponsable(apellidoResponsable);
            dto.setNombreHuespedResponsable(nombreResponsable);
            dto.setTelefonoHuespedResponsable(telefonoResponsable);
        }

        // 7. Persistir (Llamada al Gestor)
        System.out.println("Guardando reservas en el sistema...");
        try {
            // C -> GR: crearReserva(lista)
            gestorReserva.crearReservas(listaReservasSolicitadas);

            System.out.println("\n¡Reservas creadas con ÉXITO!");
            pausa();

        } catch (Exception e) {
            System.out.println("\n*** ERROR AL GUARDAR ***");
            System.out.println("Detalle: " + e.getMessage());
            pausa();
        }
    }

    //Sobrecarga de pintarGrilla, para actualizarla al seleccionar
        private void pintarHabitacionesReservada (List < Habitacion > habitaciones, Date inicio, Date
        fin, List < DtoReserva > reservasPendientes){
            System.out.println("\n--- GRILLA ACTUALIZADA CON SU SELECCIÓN ---");

            // Formatos de impresión
            String formatoFecha = "%-12s";
            String formatoCelda = "| %-9s ";
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DateTimeFormatter formateador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            // Conversión de fechas
            LocalDate inicioLocal = inicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate finLocal = fin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            // 1. Encabezado
            System.out.print("             ");
            for (Habitacion hab : habitaciones) {
                System.out.printf(formatoCelda, "Hab " + hab.getNumero());
            }
            System.out.println("|");
            imprimirSeparador(habitaciones.size());

            // 2. Días
            LocalDate actual = inicioLocal;
            while (!actual.isAfter(finLocal)) {
                // Imprimir Fecha
                System.out.printf(formatoFecha, actual.format(formateador));

                Date fechaConsulta = Date.from(actual.atStartOfDay(ZoneId.systemDefault()).toInstant());

                // 3. Habitaciones
                for (Habitacion hab : habitaciones) {
                    String visual = "[ ? ]"; // Default

                    // A. VERIFICAR SI ESTÁ EN LA LISTA DE PENDIENTES (Lógica de pintar)
                    boolean esPendiente = false;
                    for (DtoReserva dto : reservasPendientes) {
                        if (dto.getIdHabitacion().equals(hab.getNumero())) {
                            // Chequear si la fecha actual cae dentro del rango de la reserva pendiente
                            if (!fechaConsulta.before(dto.getFechaDesde()) && fechaConsulta.before(dto.getFechaHasta())) {
                                esPendiente = true;
                                break;
                            }
                        }
                    }

                    if (esPendiente) {
                        // SE PINTA COMO "RESERVADA" (o Seleccionada)
                        visual = "[ * ]"; // Usamos * para destacar la selección actual
                    } else {
                        // B. SI NO ES PENDIENTE, CONSULTAR ESTADO REAL
                        String estado = obtenerEstadoParaFecha(hab, fechaConsulta);
                        switch (estado) {
                            case "OCUPADA":
                                visual = "[ X ]";
                                break;
                            case "RESERVADA":
                                visual = "[ R ]";
                                break;
                            case "FUERA DE SERVICIO":
                                visual = "[ - ]";
                                break;
                            case "LIBRE":
                                visual = "[ L ]";
                                break;
                        }
                    }
                    System.out.printf(formatoCelda, visual);
                }
                System.out.println("|");
                actual = actual.plusDays(1);
            }
            imprimirSeparador(habitaciones.size());
            System.out.println("REFERENCIAS: [L]ibre | [R]eservada | [X]Ocupada | [*] SU SELECCIÓN");
        }


}