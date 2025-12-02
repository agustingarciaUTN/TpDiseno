package Huesped;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import Dominio.Direccion;
import Dominio.Huesped;
import Utils.Mapear.MapearDireccion;
import Utils.Mapear.MapearEstadia;
import enums.PosIva;
import enums.TipoDocumento;

import Dominio.Estadia;
import Estadia.DtoEstadia;
import Excepciones.PersistenciaException;

import Utils.Mapear.MapearHuesped;

import java.util.List;
import java.util.ArrayList;


public class GestorHuesped {

    // 1. La única instancia (static y private)
    private static GestorHuesped instancia;

    // Referencias a los DAOs que este gestor necesita
    private final DaoHuespedInterfaz daoHuesped;
    private final DaoDireccionInterfaz daoDireccion; // Ejemplo si necesita validar habitación

    private static MapearHuesped mapearHuesped;
    private static MapearDireccion mapearDireccion;

    // 2. Constructor PRIVADO
    // Nadie puede hacer "new GestorReserva()" desde fuera.
    private GestorHuesped() {
        // ¡IMPORTANTE! Aquí obtenemos las instancias de los DAOs
        this.daoHuesped = DaoHuesped.getInstance();
        this.daoDireccion = DaoDireccion.getInstance();
        /*mapearHuesped = new MapearHuesped();
        mapearDireccion = new MapearDireccion();*/

    }

    // 3. Método de Acceso Global (Synchronized para seguridad en hilos)
    public static synchronized GestorHuesped getInstance() {
        if (instancia == null) {
            instancia = new GestorHuesped();
        }
        return instancia;
    }



   public ArrayList<Huesped> buscarHuespedes(DtoHuesped criterios){
        MapearHuesped mapearHuesped = new MapearHuesped();

       ArrayList<DtoHuesped> listaDtoHuespedesEncontrados; //datos de huespedes

       if (criterios != null) {
           listaDtoHuespedesEncontrados = daoHuesped.obtenerHuespedesPorCriterio(criterios.getApellido(),
                   criterios.getNombres(), criterios.getTipoDocumento(), criterios.getNroDocumento());
       }
       else {
           listaDtoHuespedesEncontrados = daoHuesped.obtenerTodosLosHuespedes();
       }

       ArrayList<Huesped> listaHuespedesEncontrados = new ArrayList<>();

       // For que mapea cada DtoHuesped a Huesped y lo añade a la lista
       for(int i = 0 ; i < listaDtoHuespedesEncontrados.size() ; i++){
           listaHuespedesEncontrados.add(i, mapearHuesped.mapearDtoAEntidad(listaDtoHuespedesEncontrados.get(i)));
       }

        return listaHuespedesEncontrados;
    }

    public List<String> validarDatosHuesped(DtoHuesped datos){
        List<String> errores = new ArrayList<>();

        // Campos Obligatorios según CU09 especificación
        if (datos.getApellido() == null || datos.getApellido().trim().isEmpty()) {
            errores.add("El Apellido es obligatorio.");
        }
        if (datos.getNombres() == null || datos.getNombres().trim().isEmpty()) {
            errores.add("Los Nombres son obligatorios.");
        }
        if (datos.getTipoDocumento() == null) {
            errores.add("El Tipo de Documento es obligatorio.");
        }
        if (datos.getNroDocumento().isEmpty()) {
            // Asumiendo que Documento ahora es Long y lo pediste con pedirLongOpcional
            errores.add("El Número de Documento es obligatorio.");
        }
        if (datos.getFechaNacimiento() == null) {
            errores.add("La Fecha de Nacimiento es obligatoria.");
        } else {
            //regla de que la fecha no puede ser futura
            if (datos.getFechaNacimiento().after(new Date())) { // new Date() es la fecha/hora actual
                errores.add("La Fecha de Nacimiento no puede ser futura.");
            }
            // Podrías añadir validación de mayoría de edad si es necesario aquí
        }

        // Validación de Dirección (asumiendo que DtoHuesped tiene getDireccion())
        DtoDireccion direccion = datos.getDtoDireccion();
        if (direccion == null) {
            errores.add("Los datos de la Dirección son obligatorios.");
        } else {
            if (direccion.getCalle() == null || direccion.getCalle().trim().isEmpty()) {
                errores.add("La Calle de la dirección es obligatoria.");
            }
            if (direccion.getNumero() <= 0) {
                errores.add("El Número de la dirección es obligatorio y debe ser positivo.");
            }
            if (direccion.getLocalidad() == null || direccion.getLocalidad().trim().isEmpty()) {
                errores.add("La Localidad de la dirección es obligatoria.");
            }
            if (direccion.getProvincia() == null || direccion.getProvincia().trim().isEmpty()) {
                errores.add("La Provincia de la dirección es obligatoria.");
            }
            if (direccion.getPais() == null || direccion.getPais().trim().isEmpty()) {
                errores.add("El País de la dirección es obligatorio.");
            }
            if (direccion.getCodPostal() <= 0) {
                errores.add("El Código Postal es obligatorio y debe ser positivo.");
            }
        }
        List<Long> telefonos = datos.getTelefono();
        if (telefonos.getLast() <= 0) {
            errores.add("El Teléfono es obligatorio.");
        }
        List<String> ocupaciones = datos.getOcupacion();
        if (ocupaciones.getLast() == null || ocupaciones.getLast().trim().isEmpty()) {
            errores.add("La Ocupación es obligatoria.");
        }
        if (datos.getNacionalidad() == null || datos.getNacionalidad().trim().isEmpty()) {
            errores.add("La Nacionalidad es obligatoria.");
        }

        // Regla especial CUIT/IVA
        if (datos.getPosicionIva().equals(PosIva.ResponsableInscripto.toString()) ) {
            if (datos.getCuit() == null || datos.getCuit().trim().isEmpty()) {
                errores.add("El CUIT es obligatorio para Responsables Inscriptos.");
            } else {
                // Podrías añadir una validación básica de formato CUIT aquí si quieres
                if (!validarFormatoCUIT(datos.getCuit())) {
                    errores.add("El formato del CUIT ingresado no es válido (formato CUIT: XX-XXXXXXXX-X) .");
                }
            }
        }

        return errores;
    }

    private boolean validarFormatoCUIT(String cuit) {
        if (cuit == null) return false;
        // Expresión regular básica: 2 dígitos, guion, 8 dígitos, guion, 1 dígito
        return cuit.matches("^\\d{2}-\\d{8}-\\d{1}$");
    }

    public DtoHuesped chequearDuplicado(DtoHuesped datos) throws PersistenciaException {
        // La validación de null ya se hizo en el paso anterior (validarDatosHuesped)
        if(daoHuesped.existeHuesped(datos.getTipoDocumento(), datos.getNroDocumento())){return datos;}
        else {return null;}
    }

    public Huesped crearHuespedSinPersistir(DtoHuesped dtoHuesped){
        DtoDireccion dtoDireccion = dtoHuesped.getDtoDireccion();
        Direccion direccion = new Direccion();
        direccion = direccion.crearSinPersistirDireccion(dtoDireccion);

        Huesped huesped = new Huesped();
        huesped = huesped.crearSinPersistirHuesped(dtoHuesped, direccion);

        return huesped;
    }

    public Huesped crearHuespedYPersistir(DtoHuesped datosHuesped) throws PersistenciaException {

        try {
            //Crear la Dirección
            // Llamamos al DAO de Dirección. Este metodo actualiza el DTO de dirección
            // con el nuevo ID generado por la BD
            Huesped huesped = MapearHuesped.mapearDtoAEntidad(datosHuesped);
            daoHuesped.persistirHuesped(huesped);
            return huesped;

        } catch (PersistenciaException e) {
            // Si algo falló (crear dirección O crear huésped), capturamos la excepcion
            System.err.println("Error en la capa de Gestor al crear el huésped completo:");
            // y la relanzamos para que la Pantalla se entere.
            throw e;
        }
    }






    /**
     * NIVEL 2.2: Verifica si un huésped tiene reservas pendientes o activas
     * Reserva pendiente = fecha_inicio >= HOY y no tiene check-in
     */

    public int obtenerDireccionId(TipoDocumento tipoDocumento, String nroDocumento){

        return daoHuesped.obtenerIdDireccion(tipoDocumento, nroDocumento);
    }

    public boolean eliminacionDeHuesped(TipoDocumento tipoDocumento, String nroDocumento){
        return daoHuesped.eliminarHuesped(tipoDocumento, nroDocumento);
    }

    public boolean eliminacionDeDireccion(int id){
        return daoDireccion.eliminarDireccion(id);
    }
    public boolean tieneReservasPendientes(String tipoDocumento, String nroDocumento) {
        // Consultar si existe en reserva_huesped con reservas activas/pendientes
        // Por ahora retornamos false (implementar cuando tengas el módulo de reservas)

        // TODO: Implementar cuando tengas DaoReserva
        // return daoReserva.tieneReservasPendientes(tipoDocumento, nroDocumento);

        return false; // Por ahora permite eliminar sin verificar reservas
    }

    /**
     * NIVEL 2.3: Registra en log la eliminación exitosa de un huésped
     */
    public void registrarAuditoriaExitosa(String tipoDocumento, String nroDocumento) {
        String timestamp = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date());
        String mensaje = String.format("[AUDITORÍA] %s - Huésped eliminado: %s %s - Usuario: Sistema",
                timestamp, tipoDocumento, nroDocumento);

        System.out.println(mensaje);

        // Opcional: Escribir a archivo de log
        escribirLog(mensaje);
    }

    /**
     * NIVEL 2.3: Registra en log un intento fallido de eliminación
     */
    public void registrarAuditoriaFallida(String tipoDocumento, String nroDocumento, String motivo) {
        String timestamp = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date());
        String mensaje = String.format("[AUDITORÍA] %s - Intento fallido de eliminar huésped: %s %s - Motivo: %s",
                timestamp, tipoDocumento, nroDocumento, motivo);

        System.err.println(mensaje);

        // Opcional: Escribir a archivo de log
        escribirLog(mensaje);
    }

    /**
     * NIVEL 2.3: Escribe un mensaje en el archivo de auditoría
     */
    private void escribirLog(String mensaje) {
        try (java.io.FileWriter fw = new java.io.FileWriter("auditoria_huespedes.log", true);
             java.io.BufferedWriter bw = new java.io.BufferedWriter(fw);
             java.io.PrintWriter out = new java.io.PrintWriter(bw)) {

            out.println(mensaje);

        } catch (java.io.IOException e) {
            System.err.println("No se pudo escribir en el archivo de auditoría: " + e.getMessage());
        }
    }

    public void modificarHuesped(DtoHuesped dtoHuespedOriginal, DtoHuesped dtoHuespedModificado){
        
        // 1. Aseguramos que el DTO original (para el WHERE) tenga su dirección
        asignarDireccionAHuesped(dtoHuespedOriginal);

        // 2. Verificamos si la dirección del DTO modificado existe.
        // Si el DtoDireccion existe, significa que lo estamos modificando.
        DtoDireccion direccionModificada = dtoHuespedModificado.getDtoDireccion();

        Direccion direccionEntidadModificada = MapearDireccion.mapearDtoAEntidad(direccionModificada);
        if (direccionModificada != null) {
            try {
                if (direccionModificada.getId() > 0) {
                    // Si la dirección ya tiene un ID, la modificamos
                    daoDireccion.modificarDireccion(direccionModificada);
                    // Nos aseguramos que el DTO Huesped tenga el ID correcto
                    dtoHuespedModificado.setIdDireccion(direccionModificada.getId());
                } else {
                    // Si la dirección NO tiene ID (ID=0), es una dirección NUEVA
                    // (Esto pasa si el huésped no tenía dirección y le agregaste una)
                    DtoDireccion direccionNuevaCreada = daoDireccion.persistirDireccion(direccionModificada);
                    
                    // Actualizamos el DTO del Huesped con el ID de la dirección recién creada
                    dtoHuespedModificado.setDtoDireccion(direccionNuevaCreada.getId());
                }
            } catch (Excepciones.PersistenciaException e) {
                System.err.println("Error al intentar persistir la dirección en la BD: " + e.getMessage());
                // IMPORTANTE: Si falla la dirección, no debemos continuar con el huésped.
                return; 
            }
        }
        
        // 3. Finalmente, actualizamos la tabla 'huesped'
        // Ahora sí, 'dtoHuespedModificado.getIdDireccion()' tendrá el ID correcto (nuevo o modificado)
        daoHuesped.modificarHuesped(dtoHuespedOriginal, dtoHuespedModificado);
        System.out.println("“La operación ha culminado con éxito");
    }
    

    public boolean validarDatos(DtoHuesped dtoHuesped, String TipoDoc, String PosicionIva) {
        List<String> errores = new ArrayList<>();

        if (dtoHuesped.getApellido() == null || dtoHuesped.getApellido().isBlank()) {
            errores.add("Apellido");
        }
        if (dtoHuesped.getNombres() == null || dtoHuesped.getNombres().isBlank()) {
            errores.add("Nombres");
        }
        if ("ERROR".equals(TipoDoc)) {
        errores.add("Tipo de documento (valor inválido)");
        } else if (dtoHuesped.getTipoDocumento() == null) {
        errores.add("Tipo de documento");
        }
        /*String documentoValido = pedirDocumento(nroDocumento, tipo);
        if (documentoValido == null || documentoValido.trim().isEmpty()) {
            System.err.println("El número de documento no es válido");
            return false;
        }*/
        if (dtoHuesped.getDocumento() == null || dtoHuesped.getDocumento().isBlank()) {
            errores.add("Número de documento");
        }
        if(dtoHuesped.getCuit() == null || dtoHuesped.getCuit().isBlank()) {
            errores.add("CUIT");
        }
        if (PosicionIva == null || PosicionIva.isBlank()) {
            dtoHuesped.setPosicionIva(PosIva.ConsumidorFinal.toString());
        } else {
             try {
                 dtoHuesped.setPosicionIva(PosIva.fromString(PosicionIva).toString());
            } catch (IllegalArgumentException e) {
                 errores.add("Posición IVA inválida.");
            }
        }
        
        if(dtoHuesped.getFechaNacimiento() == null) {
            errores.add("Fecha de nacimiento");
        }
        // validar dirección
        if (dtoHuesped.getTelefono() <= 0L) {
            errores.add("Teléfono");
        }
        if(dtoHuesped.getEmail() == null || dtoHuesped.getEmail().isBlank()) {
            errores.add("Email");
        }
        if(dtoHuesped.getOcupacion() == null || dtoHuesped.getOcupacion().isBlank()) {
            errores.add("Ocupación");
        }
        if(dtoHuesped.getNacionalidad() == null || dtoHuesped.getNacionalidad().isBlank()) {
            errores.add("Nacionalidad");
        }
        if(dtoHuesped.getDireccion().getCalle() == null || dtoHuesped.getDireccion().getCalle().isBlank()) {
            errores.add("Calle");
        }
        if(dtoHuesped.getDireccion().getNumero() <= 0) {
            errores.add("Número de dirección");
        }
        if(dtoHuesped.getDireccion().getLocalidad() == null || dtoHuesped.getDireccion().getLocalidad().isBlank()) {
            errores.add("Ciudad");
        }
        if(dtoHuesped.getDireccion().getCodPostal() <= 0L ) {
            errores.add("Código postal");
        }
        if(dtoHuesped.getDireccion().getProvincia() == null || dtoHuesped.getDireccion().getProvincia().isBlank()) {
            errores.add("Provincia");
        }
        if(dtoHuesped.getDireccion().getPais() == null || dtoHuesped.getDireccion().getPais().isBlank()) {
            errores.add("País");
        }
        // Evaluamos si hay errores ( y los comentamos) o si no los hay
        if (!errores.isEmpty()) {
            System.out.println("\n*** ERROR: Faltan o son inválidos los siguientes datos obligatorios: ***");
            for (String e : errores){
                System.out.println("- " + e);
            }
            System.out.println("Por favor complete/corrija los campos indicados. Los campos no se han blanqueado.\n");
            return false;
        }
        return true;
    }    

    public boolean tipoynroDocExistente(DtoHuesped dtoHuesped) {
        //consultar dao si existe un huesped con ese tipo y nro de doc
        return daoHuesped.docExistente(dtoHuesped); //retornar true si existe, false si no
    }

    /**
     * Obtiene y asigna los datos completos de la dirección al DtoHuesped
     * @param dtoHuesped El DTO del huésped al que se le asignará la dirección
     * @return true si se pudo asignar la dirección, false si hubo algún error
     */
    public boolean asignarDireccionAHuesped(DtoHuesped dtoHuesped) {
        if (dtoHuesped == null || dtoHuesped.getIdDireccion() <= 0) {
            return false;
        }

        DtoDireccion dtoDireccion = daoDireccion.obtenerDireccion(dtoHuesped.getIdDireccion());
        if (dtoDireccion != null) {
            dtoHuesped.setDireccion(dtoDireccion);
            return true;
        }
        return false;
    }


    private String pedirDocumento(String nroDoc, TipoDocumento tipo) {
        String documento = null;
        boolean valido = false;


        // Definimos las reglas (Regex)
        // DNI, LE, LC: Solo números, entre 7 y 8 dígitos (ej: 12345678)
        String regexNumerico = "^\\d{7,8}$";
        // Pasaporte: Letras y números, entre 6 y 15 caracteres
        String regexPasaporte = "^[A-Z0-9]{6,15}$";
        // Otro: Cualquier cosa entre 4 y 20 caracteres
        String regexOtro = "^.{4,20}$";


            // Validamos según el tipo seleccionado
            switch (tipo) {
                case DNI:
                case LE:
                case LC:
                    if (nroDoc.matches(regexNumerico)) {
                        valido = true;
                    } else {
                        System.out.println("Error: Para " + tipo + " debe ingresar entre 7 y 8 números.");
                    }
                    break;
                case PASAPORTE:
                    if (nroDoc.matches(regexPasaporte)) {
                        valido = true;
                    } else {
                        System.out.println("Error: Formato de Pasaporte inválido (solo letras y números).");
                    }
                    break;
                default: // OTRO
                    if (nroDoc.matches(regexOtro)) {
                        valido = true;
                    } else {
                        System.out.println("Error: Formato inválido.");
                    }
                    break;
            }

            if (valido) {
                documento = nroDoc;
            }

        return documento;
    }


    //Logica de UPSERT para CU9
    public void upsertHuesped(DtoHuesped dtoHuesped) throws PersistenciaException {

        // 1. Verificamos existencia (condicion del alt)
        boolean existe = daoHuesped.existeHuesped(dtoHuesped.getTipoDocumento(), dtoHuesped.getNroDocumento());

        if (!existe) {
            // === CAMINO: ALTA (No existe) ===

            // A. Convertir DTOs a Entidades (Mapeo)
            //Estos dos metodos se encargan de llamar a las entidades, pedirles que "se creen" y retornarlas al gestor
            Direccion direccionEntidad = MapearDireccion.mapearDtoAEntidad(dtoHuesped.getDtoDireccion());
            Huesped huespedEntidad = MapearHuesped.mapearDtoAEntidad(dtoHuesped);

            // B. Persistir Dirección primero (para obtener su ID). El gestor le diece al DAO correspondiente que persista la entidad previamente creada
            daoDireccion.persistirDireccion(direccionEntidad);

            // C. Asignar la dirección guardada al huésped. Para poder persistirlo con su direccion asociada
            huespedEntidad.setDireccion(direccionEntidad);

            // D. Persistir Huésped. El gestor le diece al DAO correspondiente que persista la entidad previamente creada
            daoHuesped.persistirHuesped(huespedEntidad);

        } else {
            // === CAMINO: MODIFICACIÓN (Existe - "Aceptar Igualmente") ===

            // A. Recuperar el ID de la dirección que ya tenía este huésped en la BD
            // Esto es necesario para hacer el UPDATE sobre la fila correcta en la tabla direcciones
            int idDireccionExistente = daoHuesped.obtenerIdDireccion(dtoHuesped.getTipoDocumento(), dtoHuesped.getNroDocumento());

            // B. Actualizar la Dirección (Si corresponde)
            if (idDireccionExistente > 0) {
                // Convertimos el DTO de dirección a Entidad
                Direccion direccionEntidad = MapearDireccion.mapearDtoAEntidad(dtoHuesped.getDtoDireccion());
                // Le clavamos el ID viejo para que sobreescriba esa fila
                direccionEntidad.setId(idDireccionExistente);
                // Llamamos al DAO de Dirección (recibe Entidad)
                daoDireccion.modificarDireccion(direccionEntidad);

                // Preparamos la entidad Huesped con esta dirección
                Huesped huespedEntidad = MapearHuesped.mapearDtoAEntidad(dtoHuesped);
                huespedEntidad.setDireccion(direccionEntidad);

                // C. Actualizar el Huésped
                // Llamamos al DAO de Huésped (recibe Entidad)
                daoHuesped.modificarHuesped(huespedEntidad);
            }
        }
    }
}

       