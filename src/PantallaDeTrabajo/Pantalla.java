package PantallaDeTrabajo;
import Dominio.Habitacion;
import Dominio.Huesped;
import Estadia.DtoEstadia;
import Estadia.GestorEstadia;
import Habitacion.GestorHabitacion;
import Huesped.*;
import Reserva.DtoReserva;
import Reserva.GestorReserva;
import Utils.Mapear.MapearHabitacion;
import Utils.Mapear.MapearHuesped;
import enums.PosIva;
import enums.TipoDocumento;
import Usuario.*;
import Habitacion.DtoHabitacion;

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
    private void mostrarMenuPrincipal() throws Exception {
        //Paso 4: El sistema presenta la pantalla principal
        boolean salir = false;

        while (!salir && usuarioAutenticado) {
            System.out.println("========================================");
            System.out.println("        MENU PRINCIPAL");
            System.out.println("========================================");
            System.out.println("Usuario: " + nombreUsuarioActual);
            System.out.println("----------------------------------------");
            System.out.println("1. Buscar huesped (CU2)");
            System.out.println("2. Reservar Habitación (CU4)");
            System.out.println("3. Dar de alta huesped (CU9)");
            System.out.println("4. Dar de baja huesped (CU11) ");
            System.out.println("5. Cerrar sesión");
            System.out.println("========================================");
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
                List<String> errores;
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
                //si ingresa NO, el bucle se repite y vuelve a pedir los datos (no sé si está bien que tenga que ingresar desde 0)
            }
        }//fin while

        System.out.println("-- Fin CU9 'dar de alta huésped' ---");
    }//fin CU9 darDeAltaHuesped

    //metodo privado para pedir los datos del huesped a dar de alta, CU9 (formulario)
    private DtoHuesped mostrarYPedirDatosFormulario() {

        System.out.println('\n' + "INGRESE LOS DATOS DEL HUÉSPED A REGISTRAR");

        //Cada uno de estos métodos solicita por teclado el ingreso de cada campo del formulario
        //Además, se hace una VALIDACIÓN DE FORMATO (que el email tenga @, que el DNI sean números, que la fecha sea válida)
        //en el momento, evitando datos sin sentido

        //Las validaciones de negocio las realizará el Gestor

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

        Long telefono = pedirTelefono();

        String email = pedirEmail();

        String ocupacion = pedirStringTexto("Ocupación: ");

        String nacionalidad = pedirStringTexto("Nacionalidad: ");

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
            } else if (!entrada.matches("^[\\p{L}0-9 ]+$")) { // Letras Unicode + Números + Espacios
                System.out.println("Error: Solo se admiten letras, números y espacios. No se permiten caracteres especiales.");
            } else {
                return entrada.trim();
            }
        }
    }

    //Solicitar y Validar String simple (nombres, apellidos, pais)
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

    private Long pedirTelefono() {
        Long valor = null;
        boolean valido = false;

        // Regex: Números, espacios, guiones, más y paréntesis
        String regexTelefono = "^[0-9+() -]+$";

        while (!valido) {
            System.out.print("Teléfono: ");
            String entrada = scanner.nextLine().trim();

            if (entrada.isEmpty()) {
                System.out.println("Error: El teléfono es obligatorio.");
                continue;
            }

            if (!entrada.matches(regexTelefono)) {
                System.out.println("Error: Caracteres inválidos. Use números, espacios, guiones, '+' o '()'.");
                continue;
            }

            // --- LIMPIEZA DE DATOS ---
            // Antes de convertir a Long, le sacamos el ruido que pueda haber ingresado el usuario, buscando estandarizar
            // Reemplazamos todo lo que NO sea número ("[^0-9]") por nada ("")
            String soloNumeros = entrada.replaceAll("[^0-9]", "");

            try {
                if (soloNumeros.isEmpty()) {
                    System.out.println("Error: No ingresó ningún número.");
                    continue;
                }
                valor = Long.parseLong(soloNumeros);

                // Validación de longitud entre 6 y 15 números
                if (soloNumeros.length() < 6 || soloNumeros.length() > 15) {
                    System.out.println("Error: El número parece demasiado corto o largo.");
                } else {
                    valido = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: El número es demasiado largo para el sistema.");
            }
        }
        return valor;
    }

    private String pedirCUIT() {
        String cuit;
        // Expresion para CUIT: 2 dígitos, un guión o barrita, 8 dígitos, un guión o barrita, 1 dígito.
        String expresionCUIT = "^\\d{2}-\\d{8}-\\d$";

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
                    System.out.println("Error: Formato de fecha inválido. Use dd/MM/yyyy.");
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
            System.out.print(opciones);
            String tipoDocStr = scanner.nextLine().toUpperCase().trim(); // A mayúsculas y sin espacios al inicio y final
            if (tipoDocStr.isEmpty()) {
                System.out.println("Error: El tipo de documento es obligatorio.");
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
            try {
                int opcion = 0;
                String entrada = scanner.nextLine();

                // Si da enter, es 0 (default)
                if (!entrada.isBlank()) {
                    opcion = Integer.parseInt(entrada);
                }

                switch (opcion) {
                    case 0:
                    case 1:

                        posIva = PosIva.ConsumidorFinal.name();
                        valido = true;
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
                        System.out.println("Opción inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un número.");
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
    }//VER CUANDO SE UTILIZA ESTO, EN EL CU9 NO LO USE

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

        // Encabezado
        System.out.print("             ");


        for (Habitacion hab : grilla.keySet()) {
            System.out.printf(formatoCelda, "Hab " + hab.getNumero());
        }
        System.out.println("|");

        // Filas (Días)
        LocalDate inicioLocal = inicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate finLocal = fin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        LocalDate actual = inicioLocal;
        while (!actual.isAfter(finLocal)) {
            System.out.printf("%-12s", actual.format(dtf));
            Date fechaFila = Date.from(actual.atStartOfDay(ZoneId.systemDefault()).toInstant());

            for (Map.Entry<Habitacion, Map<Date, String>> entry : grilla.entrySet()) {
                Habitacion hab = entry.getKey();
                String visual = "[ ? ]";

                // 1. ¿Está seleccionada por el usuario AHORA? (Prioridad visual)
                boolean esSeleccion = false;
                if (seleccion != null) {
                    for (DtoReserva res : seleccion) {
                        if (res.getIdHabitacion().equals(hab.getNumero())) {
                            esSeleccion = true;
                            break;
                        }
                    }
                }

                if (esSeleccion) {
                    visual = "[ * ]"; // Selección actual
                } else {
                    // 2. Estado proveniente de la orquestación (BD)
                    String estado = entry.getValue().get(fechaFila);
                    if (estado == null) estado = "LIBRE";

                    switch (estado) {
                        case "OCUPADA" -> visual = "[ X ]";
                        case "RESERVADA" -> visual = "[ R ]";
                        case "FUERA DE SERVICIO" -> visual = "[ - ]";
                        case "LIBRE" -> visual = "[ L ]";
                    }
                }
                System.out.printf(formatoCelda, visual);
            }
            System.out.println("|");
            actual = actual.plusDays(1);
        }
        System.out.println("REFERENCIAS: [L]ibre | [R]eservada | [X]Ocupada | [-]Fuera Servicio | [*] Tu Selección");
    }

    // CU5: Mostrar Estado de Habitaciones
    // Retorna el mapa con los datos para que el CU4 pueda reutilizarlos
    public Map<Habitacion, Map<Date, String>> mostrarEstadoHabitaciones() {
        System.out.println("========================================");
        System.out.println("   CU5: MOSTRAR ESTADO HABITACIONES");
        System.out.println("========================================\n");

        // 1. Pedir y Validar Fechas (Bucle del diagrama)
        Date fechaInicio = pedirFechaFutura("Fecha de Inicio");
        Date fechaFin = pedirFechaFutura("Fecha de Fin");

        // Validar lógica de negocio (Rango coherente)
        try {
            gestorHabitacion.validarRangoFechas(fechaInicio, fechaFin);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
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

                if (habSeleccionada == null) {
                    System.out.println("Error: Habitación no encontrada.");
                    continue;
                }

                // Validar Estado
                Map<Date, String> estados = grilla.get(habSeleccionada);
                String estado = estados.get(fechaInicio);
                if (estado == null) estado = "LIBRE";

                if ("OCUPADA".equals(estado) || "FUERA DE SERVICIO".equals(estado)) {
                    System.out.println("Error: Habitación " + estado + ". Elija otra.");
                    habSeleccionada = null;
                } else if ("RESERVADA".equals(estado)) {
                    System.out.println("AVISO: Habitación RESERVADA. 1. OCUPAR IGUAL / 2. VOLVER");
                    if (leerOpcionNumerica() == 1){
                        pintarHabitacionOcupada(grilla, fechaInicio, fechaFin, estadiasParaProcesar, habSeleccionada);
                        habSeleccionada = candidata;
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
            System.out.println("2. Alta Rápida de Huésped");
            if (!lista.isEmpty()) System.out.println("3. Finalizar carga para esta habitación");

            System.out.print("Opción: ");
            int op = leerOpcionNumerica();

            if (op == 3 && !lista.isEmpty()) break;

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
                        seleccionado = Utils.Mapear.MapearHuesped.mapearEntidadADto(res.get(id-1));
                    }
                }
            } else if (op == 2) { // Alta
                System.out.println(">> Alta Rápida <<");
                seleccionado = mostrarYPedirDatosFormulario();
                try {
                    gestorHuesped.upsertHuesped(seleccionado);
                } catch (Exception e) {
                    System.out.println("Error al crear: " + e.getMessage());
                    seleccionado = null;
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
        String formatoCelda = "| %-9s ";

        System.out.println("\n--- GRILLA DE OCUPACIÓN (CHECK-IN) ---");

        // Encabezado
        System.out.print("             ");
        for (Habitacion hab : grilla.keySet()) {
            System.out.printf(formatoCelda, "Hab " + hab.getNumero());
        }
        System.out.println("|");

        // Barrido de días
        LocalDate inicioLocal = inicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate finLocal = fin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        LocalDate actual = inicioLocal;
        while (!actual.isAfter(finLocal)) {
            System.out.printf("%-12s", actual.format(dtf));
            Date fechaFila = Date.from(actual.atStartOfDay(ZoneId.systemDefault()).toInstant());

            for (Map.Entry<Habitacion, Map<Date, String>> entry : grilla.entrySet()) {
                Habitacion hab = entry.getKey();
                String visual = "[ ? ]";

                // --- LÓGICA VISUAL CU15 ---
                boolean esSeleccion = false;

                // 1. Chequear si es la que acabo de elegir (Actual)
                if (seleccionActual != null && hab.getNumero().equals(seleccionActual.getNumero())) {
                    esSeleccion = true;
                }

                // 2. Chequear si ya está en la lista de "Confirmadas" (del bucle anterior)
                if (!esSeleccion && estadiasConfirmadas != null) {
                    for (DtoEstadia dto : estadiasConfirmadas) {
                        if (dto.getDtoHabitacion().getNumero().equals(hab.getNumero())) {
                            esSeleccion = true;
                            break;
                        }
                    }
                }

                if (esSeleccion) {
                    visual = "[ * ]"; // Marca de selección visual
                } else {
                    // 3. Estado original de la BDD
                    String estado = entry.getValue().get(fechaFila);
                    if (estado == null) estado = "LIBRE";

                    switch (estado) {
                        case "OCUPADA" -> visual = "[ X ]";
                        case "RESERVADA" -> visual = "[ R ]";
                        case "FUERA DE SERVICIO" -> visual = "[ - ]";
                        case "LIBRE" -> visual = "[ L ]";
                    }
                }
                System.out.printf(formatoCelda, visual);
            }
            System.out.println("|");
            actual = actual.plusDays(1);
        }
        System.out.println("REFERENCIAS: [L]ibre | [R]eservada | [X]Ocupada | [*] SU SELECCIÓN ACTUAL");
    }


}