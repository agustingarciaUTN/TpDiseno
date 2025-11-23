package PantallaDeTrabajo;
import Huesped.*;
import enums.PosIva;
import enums.TipoDocumento;
import Usuario.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import Excepciones.PersistenciaException;

public class Pantalla {

    private final GestorHuesped gestorHuesped;
    private final Scanner scanner;//para la entrada por teclado
    private final GestorUsuario gestorUsuario;
    private boolean usuarioAutenticado;
    private String nombreUsuarioActual;


    //constructor (hay que ver como lo vamos a llamar)
    public Pantalla() {
        //inicializamos el gestor huesped
        DaoHuespedInterfaz daoHuesped = new DaoHuesped();
        DaoDireccionInterfaz daoDireccion = new DaoDireccion();
        this.gestorHuesped = new GestorHuesped(daoHuesped, daoDireccion);

        //inicializamos el gestor usuario
        DaoUsuarioInterfaz daoUsuario = new DaoUsuario();
        this.gestorUsuario = new GestorUsuario(daoUsuario);

        //inicializamos el scanner
        this.scanner = new Scanner(System.in);
        this.usuarioAutenticado = false;
        this.nombreUsuarioActual = "";
    }

    //METODO PRINCIPAL PARA INICIAR EL SISTEMA
    public void iniciarSistema() {
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

                //Paso 3.A.3: El sistema blanquea los campos (se hace automaticamente al repetir el ciclo)

                //Preguntar qué desea hacer
                System.out.println("\n¿Qué desea hacer?");
                System.out.println("1. Volver a ingresar credenciales");
                System.out.println("2. Cerrar el sistema");
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
    private void mostrarMenuPrincipal() {
        //Paso 4: El sistema presenta la pantalla principal
        boolean salir = false;

        while (!salir && usuarioAutenticado) {
            System.out.println("========================================");
            System.out.println("        MENU PRINCIPAL");
            System.out.println("========================================");
            System.out.println("Usuario: " + nombreUsuarioActual);
            System.out.println("----------------------------------------");
            System.out.println("1. Buscar huesped (CU2)");
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
                    iniciarBusquedaHuesped();
                    break;
                case 2:
                    iniciarAltaHuesped();
                    break;
                case 3:
                    iniciarBajaHuesped();
                    break;
                case 4:
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

    public void iniciarAltaHuesped() {//este metodo debe tener el mismo nombre que el CU?

        //no se si es necesario, despues habra que ver la parte estetica
        System.out.println('\n'+"-- Iniciando CU9 'dar de alta huesped' --");

        boolean continuarCargando = true;//bandera

        while (continuarCargando) {
            //metodo para pedir datos
            DtoHuesped datosIngresados = mostrarYPedirDatosFormulario();

            System.out.println("Acciones: 1 = SIGUIENTE, 2 = CANCELAR");
            System.out.println("Ingrese una opción: ");
            int opcionBoton = -1;
            try {
                opcionBoton = scanner.nextInt();
                scanner.nextLine();//para consumir el salto de linea
            } catch (InputMismatchException e) {
                scanner.nextLine(); //limpiar buffer
                System.out.println("\nOpción inválida. Intente nuevamente.\n");
                break;
            }

            if (opcionBoton == 1) {//presiono SIGUIENTE
                System.out.println("Procesando datos...");


                //aca hay que llamar al gestor para que valide los datos
                List<String> errores = new ArrayList<>();
                errores = gestorHuesped.validarDatosHuesped(datosIngresados);


                if (!errores.isEmpty()) {
                    System.out.println("ERROR: Se encontraron los siguientes errores: ");
                    for (String error : errores) {
                        System.out.println("- " + error);
                    }
                    System.out.println("Por favor, ingrese los datos nuevamente");
                    continue; //fuerzaa al inicio del while
                }

                try {
                    DtoHuesped duplicado = gestorHuesped.chequearDuplicado(datosIngresados);

                    if (duplicado != null) {
                        System.out.println("----------------------------------------------------------------");
                        System.out.println("⚠️ ¡CUIDADO! El tipo y número de documento ya existen en el sistema:");
                        System.out.println("   Huésped existente: " + duplicado.getNombres() + " " + duplicado.getApellido());
                        System.out.println("----------------------------------------------------------------");
                        System.out.println("Opciones: 1 = ACEPTAR IGUALMENTE, 2 = CORREGIR");
                        System.out.println("Ingrese una opción: ");

                        int opcionDuplicado = scanner.nextInt();
                        scanner.nextLine(); // Consumir salto de línea

                        if (opcionDuplicado == 2) { // Eligió CORREGIR
                            System.out.println("Seleccionó CORREGIR. Vuelva a ingresar los datos.");
                            continue; // Vuelve al inicio del while para pedir todo de nuevo
                        }
                        // Si elige 1 (ACEPTAR IGUALMENTE), no hacemos nada y el código sigue
                    }


                    //paso todas las validaciones, creamos el Huesped en la db
                    gestorHuesped.crearHuespedCompleto(datosIngresados);


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
                    //no se si aca hay que consumir salto de linea o no
                    System.out.println("Ingreso invalido. ¿Desea cargar otro huésped? (SI/NO): ");
                    ingresoOtroHuesped = scanner.nextLine();
                }

                //si ingreso NO termina el bucle, si ingreso SI se repite
                if (ingresoOtroHuesped.equalsIgnoreCase("NO")) {
                    continuarCargando = false;
                }

            } else if (opcionBoton == 2) {//presiono CANCELAR
                System.out.println("¿Desea cancelar el alta del huésped? (SI/NO): ");

                //validacion de ingreso correcto (capaz esto puede ser una funcion aparte, habria que ver como manejar los mensajes distintos)
                String ingresoCancelarAlta = scanner.nextLine();
                while (!ingresoCancelarAlta.equalsIgnoreCase("NO") && !ingresoCancelarAlta.equalsIgnoreCase("SI")) {
                    //no se si aca hay que consumir salto de linea o no
                    System.out.println("Ingreso invalido. ¿Desea cancelar el alta de huesped? (SI/NO): ");
                    ingresoCancelarAlta = scanner.nextLine();
                }

                if (ingresoCancelarAlta.equalsIgnoreCase("SI")) {
                    System.out.println("Alta cancelada.");
                    continuarCargando = false;//termina el bucle
                }
                //si ingresa NO, el bucle se repite y vuelve a pedir los datos (no se si esta bien que tenga que ingresar todo de 0)
            } else {
                System.out.println("Ingreso invalido. Intente nuevamente.");
            }
        }//fin while

        System.out.println("-- Fin CU9 'dar de alta huesped' ---");
    }//fin iniciarAltaHuesped

        //metodo privado para pedir los datos del huesped a ingresar
    private DtoHuesped mostrarYPedirDatosFormulario() {

        System.out.println('\n'+"INGRESE LOS DATOS DEL HUÉSPED A REGISTRAR");
      
        String apellido = pedirStringTexto("Apellido: ");
        
        String nombres = pedirStringTexto("Nombres: ");

        TipoDocumento tipoDocumento = pedirTipoDocumento();


        String numeroDocumento = pedirDocumento(tipoDocumento);

        String cuit = pedirCUIT();

        String posIva = pedirPosIva();

        Date fechaNacimiento = pedirFecha("Fecha de Nacimiento ");

        String calleDireccion = pedirStringValidado("Calle: ");

        Integer numeroDireccion = pedirEntero("Número de calle: ");

        String departamentoDireccion = pedirStringOpcional("Departamento (opcional, presione Enter para omitir): ");

        String pisoDireccion = pedirStringOpcional("Piso (opcional, presione Enter para omitir): ");

        Integer codPostalDireccion = pedirEntero("Código Postal: ");

        String localidadDireccion = pedirStringValidado("Localidad: ");
        
        String provinciaDireccion = pedirStringValidado("Provincia: ");
        
        String paisDireccion = pedirStringTexto("Pais: ");

        Long telefono = pedirLong("Teléfono: ");

        String email = pedirEmail();

        String ocupacion = pedirStringTexto("Ocupacion: ");
        
        String nacionalidad = pedirStringTexto("Nacionalidad: ");

        //casteo los wrappers (necesarios para las validaciones) a primitivos para su posterior uso en la app
        int numeroDireccionPrimitivo = numeroDireccion.intValue();
        int codPostalDireccionPrimitivo = codPostalDireccion.intValue();


        // Crear los DTO  (aún no tenemos el ID de dirección)
        DtoDireccion direccionDto = new DtoDireccion(calleDireccion, numeroDireccionPrimitivo, departamentoDireccion, pisoDireccion, codPostalDireccionPrimitivo, localidadDireccion, provinciaDireccion, paisDireccion);
        DtoHuesped huespedDto = new DtoHuesped(nombres, apellido, telefono, tipoDocumento, numeroDocumento, cuit, posIva, fechaNacimiento, email, ocupacion, nacionalidad);

        //asociamos el la direccion con el huesped
        huespedDto.setDireccion(direccionDto);


        System.out.println("--- Fin Formulario ---");
        return huespedDto; // Devolver el DTO con los datos cargados

    }
    
    private String pedirStringValidado(String mensaje) {
        String entrada;
        while (true) {
            System.out.print(mensaje);
            entrada = scanner.nextLine();
            if (entrada.trim().isEmpty()) {
                System.out.println("Error: Este campo es obligatorio.");
            } else if (!entrada.matches("^[a-zA-Z0-9 ]+$")) { // Solo letras, números y espacios
                System.out.println("Error: Solo se admiten letras, números y espacios. No se permiten caracteres especiales.");
            } else {
                return entrada.trim();
            }
        }
    }
    
    private String pedirStringTexto(String mensaje) {
        String entrada;
        while (true) {
            System.out.print(mensaje);
            entrada = scanner.nextLine();

            if (entrada.trim().isEmpty()) {
                System.out.println("Error: Este campo es obligatorio.");
        
             // Esta expresion ^[\p{L} ]+$ permite cualquier letra de cualquier idioma
            // y espacios, pero no números ni caracteres especiales.
            } else if (!entrada.matches("^[\\p{L} ]+$")) { 
                System.out.println("Error: Solo se admiten letras y espacios.");
        
            } else {
                return entrada.trim();
            }
        }
    }
    
    private String pedirStringOpcional(String mensaje) {
        String entrada;
        // La expresion permite letras (a-z, A-Z), números (0-9) y espacios.
        String str = "^[a-zA-Z0-9 ]+$";

        while (true) {
            System.out.print(mensaje);
            entrada = scanner.nextLine();

            //Si está vacío, es válido (opcional)
            if (entrada.trim().isEmpty()) {
                return null; // O puedes devolver "" si lo preferís
        
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
            String entrada = scanner.nextLine(); // leemos siempre como String
            int validez = Integer.parseInt(entrada);

            if (entrada.trim().isEmpty()) {
                System.out.println("Error: Este campo es obligatorio. No se puede omitir.");
                continue;
            } else if(validez <= 0){
                System.out.println("Error: ingreser un numero positivo por favor.");
            }
            else {
                try {
                    valor = Integer.parseInt(entrada); // intentamos convertir el String a int
                    valido = true;      // Si funciona, es válido
                } catch (NumberFormatException e) {
                    System.out.println("Error: Ingrese un número entero válido o presione Enter para omitir.");
                }
            }
        }
        return valor;
    }


    private Long pedirLong(String mensaje) { // Devuelve Long (wrapper)
        Long valor = null; // Usamos la clase wrapper Long
        boolean valido = false;
        while (!valido) {
            System.out.print(mensaje);
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
    
    private Date pedirFecha(String mensaje) {
        Date fecha = null;
        boolean valida = false;
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        formatoFecha.setLenient(false);

        while (!valida) {
            System.out.print(mensaje + " (formato dd/MM/yyyy): ");
            String fechaStr = scanner.nextLine();

            if (fechaStr.trim().isEmpty()) {
                System.out.println("Error: Este campo es obligatorio.");
                continue;
            } else {
                try {
                    fecha = formatoFecha.parse(fechaStr);
                    // Convertir a LocalDate para comparar solo la fecha (sin hora)
                    java.time.LocalDate fechaLocal = java.time.Instant.ofEpochMilli(fecha.getTime())
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate();
                    java.time.LocalDate hoy = java.time.LocalDate.now();

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
            String tipoDocStr = scanner.nextLine().toUpperCase().trim(); // A mayúsculas y sin espacios
            if (tipoDocStr.isEmpty()) {
                valido = true; // Omitir es válido
            } else {
                try {
                    tipoDoc = TipoDocumento.valueOf(tipoDocStr);
                    valido = true; // Opción válida
                } catch (IllegalArgumentException e) {
                    System.out.println("Error: Tipo de documento inválido. Ingrese una de las opciones o Enter para omitir.");
                }
            }
        }
        return tipoDoc;
    }


   private String pedirPosIva() {
        String posIva = null;
        boolean valido = false;


        while (!valido) {
            System.out.println("Posicion frente al IVA (1.Consumidor Final (por defecto),"+ '\n' + " 2.Monotributista, " +'\n'+ "3.Responsable Inscripto, "+'\n'+"4.Excento)");
            int opcion = Integer.parseInt(scanner.nextLine());

            // Permitir Enter para el valor por defecto
            if (opcion == 0) {
                posIva = PosIva.ConsumidorFinal.toString(); // Asignar el default
                valido = true;
            } else {
                try {
                    switch (opcion) {
                        case 1:
                            posIva = PosIva.ConsumidorFinal.toString();
                            valido = true;
                            break;
                        case 2:
                            posIva = PosIva.Monotributista.toString();
                            valido = true;
                            break;
                        case 3:
                            posIva = PosIva.ResponsableInscripto.toString();
                            valido = true;
                            break;
                        case 4:
                            posIva = PosIva.Excento.toString();
                            valido = true;
                            break;
                    }

                } catch (IllegalArgumentException e) {
                    System.out.println("Error: Posición IVA inválida. Ingrese una opción válida o Enter para ConsumidorFinal.");
                }
            }
        }
        return posIva;
    }


    //METODO AUXILIAR PARA PAUSAR
    public void pausa() {
        System.out.print("Presione ENTER para continuar...");
        scanner.nextLine();
        System.out.println();
    }

    public void iniciarBusquedaHuesped() {
        System.out.println("========================================");
        System.out.println("        BÚSQUEDA DE HUÉSPED 🔎");
        System.out.println("========================================");

        DtoHuesped datos = leerCriteriosDeBusqueda();
        ArrayList<DtoHuesped> huespedesEncontrados = gestorHuesped.buscarHuesped(datos);

        if (huespedesEncontrados.isEmpty()) {
            System.out.println("\nNo se encontraron huéspedes con los criterios especificados.");
            System.out.print("¿Desea dar de alta un nuevo huésped? (SI/NO): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("SI")) {
                this.iniciarAltaHuesped();
            }
        } else {
            this.seleccionarHuespedDeLista(huespedesEncontrados);
        }
        pausa();
    }

    private DtoHuesped leerCriteriosDeBusqueda() {
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
            criterios.setDocumento(pedirDocumento(criterios.getTipoDocumento()));
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
                System.out.println("❌ Error: Tipo de documento no válido. Los valores posibles son DNI, PASAPORTE, Libreta de Enrolamiento, Libreta Civica.");
            }
        }
    }

    /*private long validarYLeerNumeroDocumento(TipoDocumento tipoDoc) {
        while (true) {
            System.out.print("Número de Documento: ");
            String numeroStr = scanner.nextLine().trim();

            if (numeroStr.isEmpty()) {
                return 0; // Se devuelve 0 si se omite
            }

            try {


                // VALIDACIÓN DE RANGO SEGÚN TIPO DE DOCUMENTO
                if (tipoDoc == TipoDocumento.DNI) {
                    if (numero < 0 || numero > 99999999) {
                        System.out.println("⚠ El DNI debe estar entre 0 y 99.999.999. Intente nuevamente.");
                        continue;
                    }
                } else if (tipoDoc == TipoDocumento.LE || tipoDoc == TipoDocumento.LC) {
                    if (numero < 0 || numero > 99999999) {
                        System.out.println("⚠ La " + tipoDoc.name() + " debe estar entre 0 y 99.999.999. Intente nuevamente.");
                        continue;
                    }
                } else if (tipoDoc == TipoDocumento.PASAPORTE) {
                    if (numero <= 0) {
                        System.out.println("⚠ El número de pasaporte debe ser mayor a 0. Intente nuevamente.");
                        continue;
                    }
                }

                return numero;

            } catch (NumberFormatException e) {
                System.out.println("⚠ El número de documento debe ser un valor numérico. Intente nuevamente.");
            }
        }
    }*/

    private void seleccionarHuespedDeLista(List<DtoHuesped> huespedes) {
        mostrarListaHuespedes(huespedes);
        System.out.print("Ingrese el ID del huésped para modificar, o 0 para dar de alta uno nuevo: ");
        int seleccion = leerOpcionNumerica();

        if (seleccion > 0 && seleccion <= huespedes.size()) {
            DtoHuesped huespedSeleccionado = huespedes.get(seleccion - 1);
            this.iniciarModificacionHuesped(huespedSeleccionado);
        } else {
            System.out.println("llegamos a seguir dando de alta otro huesped");//this.iniciarAltaHuesped();
        }
    }

    private void mostrarListaHuespedes(List<DtoHuesped> huespedes) {
        System.out.println("\n-- Huéspedes Encontrados --");
        System.out.printf("%-5s %-20s %-20s %s%n", "ID", "APELLIDO", "NOMBRES", "DOCUMENTO");
        System.out.println("-----------------------------------------------------------------");
        for (int i = 0; i < huespedes.size(); i++) {
            DtoHuesped h = huespedes.get(i);
            String docCompleto = (h.getTipoDocumento() != null ? h.getTipoDocumento().name() : "N/A") + " " + h.getDocumento();
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


    public void iniciarBajaHuesped() {
        System.out.println("\n========================================");
        System.out.println("   CU11: DAR DE BAJA HUÉSPED");
        System.out.println("========================================\n");

        // Paso 1: Buscar el huésped a eliminar
        System.out.println("Primero debe buscar el huésped que desea eliminar.\n");

        DtoHuesped criterios = leerCriteriosDeBusqueda();
        ArrayList<DtoHuesped> huespedesEncontrados = gestorHuesped.buscarHuesped(criterios);

        if (huespedesEncontrados.isEmpty()) {
            System.out.println("\nNo se encontraron huéspedes con los criterios especificados.");
            System.out.println("No hay huéspedes para eliminar.\n");
            pausa();
            return; // Termina el CU
        }

        // Mostrar lista de huéspedes encontrados
        mostrarListaHuespedes(huespedesEncontrados);

        System.out.print("\nIngrese el número del huésped que desea eliminar (0 para cancelar): ");
        int seleccion = leerOpcionNumerica();

        if (seleccion == 0) {
            System.out.println("Eliminación cancelada.\n");
            return; // Termina el CU
        }

        if (seleccion < 1 || seleccion > huespedesEncontrados.size()) {
            System.out.println("Selección inválida. Eliminación cancelada.\n");
            pausa();
            return; // Termina el CU
        }

        // Huésped seleccionado
        DtoHuesped huespedSeleccionado = huespedesEncontrados.get(seleccion - 1);

        // Paso 2: Validar si el huésped puede ser eliminado
        String tipoDoc = huespedSeleccionado.getTipoDocumento().name();
        String nroDoc = huespedSeleccionado.getDocumento();

        boolean puedeEliminar = gestorHuesped.puedeEliminarHuesped(tipoDoc, nroDoc);

        if (!puedeEliminar) {
            // Flujo Alternativo 2.A: El huésped se alojó alguna vez
            System.out.println("\n*** NO SE PUEDE ELIMINAR ***");
            System.out.println("El huésped se ha alojado en el hotel en alguna oportunidad.");
            System.out.println("Por razones de auditoría, el huésped NO puede ser eliminado del sistema.");
            System.out.println("*****************************\n");

            pausa();
            return; // Termina el CU (Flujo Alternativo 2.A.1)
        }

        // Paso 2 (continuación): El huésped NUNCA se alojó, se puede eliminar
        System.out.println("\nLos datos del huésped que será eliminado son:");
        System.out.println("----------------------------------------");
        System.out.println("Nombre:    " + huespedSeleccionado.getNombres());
        System.out.println("Apellido:  " + huespedSeleccionado.getApellido());
        System.out.println("Documento: " + huespedSeleccionado.getTipoDocumento().name() + " " + huespedSeleccionado.getDocumento());
        System.out.println("----------------------------------------\n");

        System.out.println("¿Está seguro que desea ELIMINAR este huésped?");
        System.out.println("1. ELIMINAR");
        System.out.println("2. CANCELAR");
        System.out.print("Ingrese una opción: ");

        int opcion = leerOpcionNumerica();

        if (opcion == 1) {
            // Paso 3: El actor presiona "ELIMINAR"
            System.out.println("\nEliminando huésped...");

            boolean eliminado = gestorHuesped.eliminarHuesped(tipoDoc, nroDoc);

            if (eliminado) {
                // Éxito
                System.out.println("\n*** ELIMINACIÓN EXITOSA ***");
                System.out.println("Los datos del huésped " + huespedSeleccionado.getNombres() + " " + huespedSeleccionado.getApellido());
                System.out.println("(" + huespedSeleccionado.getTipoDocumento().name() + " " + huespedSeleccionado.getDocumento() + ")");
                System.out.println("han sido eliminados del sistema.");
                System.out.println("***************************\n");
            } else {
                // Error
                System.out.println("\n*** ERROR ***");
                System.out.println("No se pudo eliminar el huésped.");
                System.out.println("Intente nuevamente o contacte al administrador.");
                System.out.println("*************\n");
            }

        } else if (opcion == 2) {
            // Flujo Alternativo 3.A: El actor presiona "CANCELAR"
            System.out.println("\nEliminación cancelada.\n");
        } else {
            System.out.println("\nOpción inválida. Eliminación cancelada.\n");
        }

        // Paso 4: El actor presiona cualquier tecla
        pausa();

        System.out.println("========================================");
        System.out.println("   FIN CU11: DAR DE BAJA HUÉSPED");
        System.out.println("========================================\n");
    }
    // Paso 5: El CU termina


 private void iniciarModificacionHuesped(DtoHuesped dtoHuesped){ //Metodo para Modificar Huesped CU10
    boolean salir = false;
    DtoHuesped dtoHuespedModificado = new DtoHuesped(dtoHuesped);
    String tipoDocStr = "";
    String posIvaStr = "";
        while(!salir){
            
            System.out.println("========================================");

            mostrarDatosHuesped(dtoHuespedModificado);

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

            switch(opcion){
                case 1:
                    System.out.print("Nuevo Apellido: ");
                    dtoHuespedModificado.setApellido(scanner.nextLine().trim());
                    break;
                case 2:
                    System.out.print("Nuevo Nombre: ");
                   dtoHuespedModificado.setNombres(scanner.nextLine().trim());
                    break;
                case 3:
                    System.out.print("Nuevo Tipo de Documento (DNI, PASAPORTE, LE, LC): ");
                    tipoDocStr = scanner.nextLine().trim().toUpperCase();

                    // FALTABA ESTO: Asignar el valor al DTO modificado
                    try {
                        dtoHuespedModificado.setTipoDocumento(TipoDocumento.valueOf(tipoDocStr));
                    } catch (IllegalArgumentException e) {
                        System.out.println("❌ Error: Tipo de documento no válido. Se mantiene el valor anterior.");
                        // Invalidamos tipoDocStr para que la validación posterior lo note
                        tipoDocStr = "ERROR"; 
                    }
                    break;
                case 4:
                    System.out.print("Nuevo Número de Documento: ");
                   dtoHuespedModificado.setDocumento(scanner.nextLine());
                    break;
                case 5:
                    System.out.print("Nuevo CUIT: ");
                    dtoHuespedModificado.setCuit(scanner.nextLine().trim());
                    break;
                case 6:
                    System.out.println("Nueva Posición Frente al IVA:");
                    System.out.println("1. " + PosIva.ConsumidorFinal.getDescripicion());
                    System.out.println("2. " + PosIva.Monotributista.getDescripicion());
                    System.out.println("3. " + PosIva.ResponsableInscripto.getDescripicion());
                    System.out.println("4. " + PosIva.Excento.getDescripicion());
                    System.out.print("Ingrese una opción: ");
                    try {
                        int opcionIva = scanner.nextInt();
                        scanner.nextLine(); // Consumir salto de línea
                        switch (opcionIva) {
                            case 1: posIvaStr = PosIva.ConsumidorFinal.getDescripicion(); break;
                            case 2: posIvaStr = PosIva.Monotributista.getDescripicion(); break;
                            case 3: posIvaStr = PosIva.ResponsableInscripto.getDescripicion(); break;
                            case 4: posIvaStr = PosIva.Excento.getDescripicion(); break;
                            default:
                                System.out.println("Opción inválida. Se mantendrá el valor actual.");
                                break;
                        }
                        if (opcionIva >= 1 && opcionIva <= 4) {
                            dtoHuespedModificado.setPosicionIva(posIvaStr);
                        }
                    } catch (Exception e) {
                        scanner.nextLine(); // Limpiar el buffer
                        System.out.println("Entrada inválida. Se mantendrá el valor actual.");
                    }
                    break;
                 case 7:
                    System.out.print("Nueva Fecha de Nacimiento (dd/MM/yyyy): ");
                    dtoHuespedModificado.setFechaNacimiento(leerFecha("Fecha de nacimiento", dtoHuespedModificado.getFechaNacimiento()));
                     break;
                case 8:
                    cambiarDireccionHuesped(dtoHuespedModificado.getDireccion());
                    break;
                case 9:
                    System.out.print("Nuevo Teléfono: ");
                    dtoHuespedModificado.setTelefono(scanner.nextLong());
                    break;
                case 10:
                    System.out.print("Nuevo Email: ");
                    dtoHuespedModificado.setEmail(scanner.nextLine().trim());
                    break;
                case 11:
                    System.out.print("Nueva Ocupación: ");
                    dtoHuespedModificado.setOcupacion(scanner.nextLine().trim());
                    break;
                case 12:
                    System.out.print("Nueva Nacionalidad: ");
                    dtoHuespedModificado.setNacionalidad(scanner.nextLine().trim());
                    break;
                 case 13: 
                    // Al pulsar SIGUIENTE validamos omisiones. Si hay errores, no salimos. Paso2 CU10
                    if (gestorHuesped.validarDatos(dtoHuespedModificado, tipoDocStr, posIvaStr)) {
                        if(gestorHuesped.tipoynroDocExistente(dtoHuespedModificado)){
                            System.out.println("¡CUIDADO! El tipo y número de documento ya existen en el sistema");
                            System.out.println("1. ACEPTAR IGUALMENTE");
                            System.out.println("2. CORREGIR");
                            System.out.print("Ingrese una opción: ");
                            int opcionDoc = -1;
                            try {
                                opcionDoc = scanner.nextInt();
                                scanner.nextLine(); //consumir salto de linea
                            } catch (Exception e) {
                                scanner.nextLine(); //limpiar buffer
                                System.out.println("\nOpción inválida. Intente nuevamente.\n");
                                break;
                            }
                            if (opcionDoc == 2) {
                                // quedarse en la pantalla para que el actor corrija
                                break;
                            }else if (opcionDoc != 1) {
                                System.out.println("Opción inválida. Intente nuevamente.\n");
                                break;
                            }   else{
                                gestorHuesped.modificarHuesped(dtoHuesped, dtoHuespedModificado);
                                salir = true; 
                            } 
                        }else{
                            gestorHuesped.modificarHuesped(dtoHuesped, dtoHuespedModificado);
                            salir = true;
                        }
                    } else {
                        // quedarse en la pantalla para que el actor corrija. Mensajes enviados en validarDatos
                    }
                    break;
                case 14:
                    BooleanRef salirRef = new BooleanRef(salir);
                    pulsarCancelar(salirRef);
                    salir = salirRef.getValue();
                    break;
                case 15:
                    eliminarHuespedDesdeCU10(dtoHuesped);//CU11
                    salir = true;
                    break;
                default:
                    System.out.println("Opción inválida. Intente nuevamente.\n");
            }
        }
    }

    private void eliminarHuespedDesdeCU10(DtoHuesped dtoHuesped){
        String tipoDoc = dtoHuesped.getTipoDocumento().name();
        String nroDoc = dtoHuesped.getDocumento();

        boolean puedeEliminar = gestorHuesped.puedeEliminarHuesped(tipoDoc, nroDoc);

        if (!puedeEliminar) {
            // Flujo Alternativo 2.A: El huésped se alojó alguna vez
            System.out.println("\n*** NO SE PUEDE ELIMINAR ***");
            System.out.println("El huésped se ha alojado en el hotel en alguna oportunidad.");
            System.out.println("Por razones de auditoría, el huésped NO puede ser eliminado del sistema.");
            System.out.println("*****************************\n");

            pausa();
            return; // Termina el CU (Flujo Alternativo 2.A.1)
        }

        // Paso 2 (continuación): El huésped NUNCA se alojó, se puede eliminar
        System.out.println("\nLos datos del huésped que será eliminado son:");
        System.out.println("----------------------------------------");
        System.out.println("Nombre:    " + dtoHuesped.getNombres());
        System.out.println("Apellido:  " + dtoHuesped.getApellido());
        System.out.println("Documento: " + dtoHuesped.getTipoDocumento().name() + " " + dtoHuesped.getDocumento());
        System.out.println("----------------------------------------\n");

        System.out.println("¿Está seguro que desea ELIMINAR este huésped?");
        System.out.println("1. ELIMINAR");
        System.out.println("2. CANCELAR");
        System.out.print("Ingrese una opción: ");

        int opcion = leerOpcionNumerica();

        if (opcion == 1) {
            // Paso 3: El actor presiona "ELIMINAR"
            System.out.println("\nEliminando huésped...");

            boolean eliminado = gestorHuesped.eliminarHuesped(tipoDoc, nroDoc);

            if (eliminado) {
                // Éxito
                System.out.println("\n*** ELIMINACIÓN EXITOSA ***");
                System.out.println("Los datos del huésped " + dtoHuesped.getNombres() + " " + dtoHuesped.getApellido());
                System.out.println("(" + dtoHuesped.getTipoDocumento().name() + " " + dtoHuesped.getDocumento() + ")");
                System.out.println("han sido eliminados del sistema.");
                System.out.println("***************************\n");
            } else {
                // Error
                System.out.println("\n*** ERROR ***");
                System.out.println("No se pudo eliminar el huésped.");
                System.out.println("Intente nuevamente o contacte al administrador.");
                System.out.println("*************\n");
            }

        } else if (opcion == 2) {
            // Flujo Alternativo 3.A: El actor presiona "CANCELAR"
            System.out.println("\nEliminación cancelada.\n");
        } else {
            System.out.println("\nOpción inválida. Eliminación cancelada.\n");
        }
    }
    
public void cambiarDireccionHuesped(DtoDireccion direccion){
    if (direccion == null) {
        System.out.println("\nNo hay dirección asociada al huésped.");        
        return;
    }
    
    boolean salir = false;
    while(!salir){
        System.out.println("\nSelecciona el dato a cambiar:");
        System.out.println("1. Calle: " + direccion.getCalle());
        System.out.println("2. Número: " + direccion.getNumero());
        System.out.println("3. Departamento: " + direccion.getDepartamento());
        System.out.println("4. Piso: " + direccion.getPiso());
        System.out.println("5. Código Postal: " + direccion.getCodPostal());
        System.out.println("6. Localidad: " + direccion.getLocalidad());
        System.out.println("7. Provincia: " + direccion.getProvincia());
        System.out.println("8. País: " + direccion.getPais());
        System.out.println("9. VOLVER");
        System.out.print("Ingrese una opción: ");
        int opcion = -1;
        try {
            opcion = scanner.nextInt();
            scanner.nextLine(); //consumir salto de linea
        } catch (Exception e) {
            scanner.nextLine(); //limpiar buffer
            System.out.println("\nOpción inválida. Intente nuevamente.\n");
            return;
        }
        switch(opcion){
            case 1:
                System.out.print("Nueva Calle: ");
                direccion.setCalle(scanner.nextLine().trim());
                break;
            case 2:
                System.out.print("Nuevo Número: ");
                direccion.setNumero(scanner.nextInt());
                break;
            case 3:
                System.out.print("Nuevo Departamento: ");
                direccion.setDepartamento(scanner.nextLine().trim());
                break;
            case 4:
                System.out.print("Nuevo Piso: ");
                direccion.setPiso(scanner.nextLine());
                break;
            case 5:
                System.out.print("Nuevo Código Postal: ");
                direccion.setCodPostal(scanner.nextInt());
                break;
            case 6:
                System.out.print("Nueva Localidad: ");
                direccion.setLocalidad(scanner.nextLine().trim());
                break;
            case 7:
                System.out.print("Nueva Provincia: ");
                direccion.setProvincia(scanner.nextLine().trim());
                break;
            case 8:
                System.out.print("Nuevo País: ");  
                direccion.setPais(scanner.nextLine().trim());
            break;
            case 9:
                return;
            default:
                System.out.println("Opción inválida. Intente nuevamente.\n");
        }
    }
}
    private void pulsarCancelar(BooleanRef salir){ //Paso3 CU10
        System.out.print("\n¿Desea cancelar la modificación del huésped? ");
        System.out.print("1. SI ");
        System.out.print(" 2. NO \n");
        int opt = -1;
        while (true) {
            System.out.print("Ingrese una opción: ");
            try {
                opt = scanner.nextInt();
                scanner.nextLine(); //consumir salto de linea
            } catch (Exception e) {
                scanner.nextLine(); //limpiar buffer
                System.out.println("\nOpción inválida. Intente nuevamente.\n");
                
            }
            switch (opt){
                case 1:
                    System.out.println("\nModificación cancelada.\n");
                    salir.setValue(true);
                    return;
                case 2:
                    System.out.println("\nContinuando con la modificación.\n");
                    return;
                default:
                    System.out.println("Opción inválida. Intente nuevamente.\n");
            }
        }
    }
       
    private void mostrarDatosHuesped(DtoHuesped dtoHuesped){
        System.out.println("---- DATOS DEL HUESPED ----");
        System.out.println("1. Apellido: " + dtoHuesped.getApellido());
        System.out.println("2. Nombre: " + dtoHuesped.getNombres());
        System.out.println("3. Tipo de documento: " + dtoHuesped.getTipoDocumento());
        System.out.println("4. Número de documento: " + dtoHuesped.getDocumento());
        System.out.println("5. CUIT: " + dtoHuesped.getCuit());
        System.out.println("6. Posición IVA: " + dtoHuesped.getPosicionIva());
        System.out.println("7. Fecha de nacimiento: " + dtoHuesped.getFechaNacimiento());
        System.out.println("8. Dirección, pulsa para mas informacion"); 
        System.out.println("9. Teléfono: " + dtoHuesped.getTelefono());
        System.out.println("10. Agregar Email");
        System.out.println("11. Ocupación: " + dtoHuesped.getOcupacion());
        System.out.println("12. Nacionalidad: " + dtoHuesped.getNacionalidad());
        System.out.println("13. SIGUIENTE");
        System.out.println("14. CANCELAR");
        System.out.println("15. BORRAR HUESPED");
        System.out.println("---------------------------\n");
    }

   

    /*
     * Lee una fecha desde la entrada en formato dd/MM/yyyy.
     * Si el usuario ingresa línea vacía, devuelve current (mantiene la fecha actual).
     */
    private Date leerFecha(String etiqueta, Date current) {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        formato.setLenient(false);
        while (true) {
            String actual = (current == null) ? "vacío" : formato.format(current);
            System.out.print(etiqueta + " (dd/MM/yyyy) [ENTER para mantener: " + actual + "]: ");
            String linea = scanner.nextLine().trim();
            if (linea.isEmpty()) {
                return current;
            }
            try {
                return formato.parse(linea);
            } catch (ParseException e) {
                System.out.println("Formato inválido. Use dd/MM/yyyy. Intente nuevamente.");
            }
        }
    }
    private String pedirDocumento(TipoDocumento tipo) {
        String documento = null;
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
                documento = entrada;
            }
        }
        return documento;
    }

}
