package Huesped;


import java.util.Date;

import Dominio.Direccion;
import Dominio.Huesped;
import enums.PosIva;
import enums.TipoDocumento;

import Dominio.Estadia;
import Estadia.DtoEstadia;
import Excepciones.PersistenciaException;

import java.util.List;
import java.util.ArrayList;


public class GestorHuesped {

    // 1. La única instancia (static y private)
    private static GestorHuesped instancia;

    // Referencias a los DAOs que este gestor necesita
    private final DaoHuespedInterfaz daoHuesped;
    private final DaoDireccionInterfaz daoDireccion; // Ejemplo si necesita validar habitación

    // 2. Constructor PRIVADO
    // Nadie puede hacer "new GestorReserva()" desde fuera.
    private GestorHuesped() {
        // ¡IMPORTANTE! Aquí obtenemos las instancias de los DAOs
        this.daoHuesped = DaoHuesped.getInstance();
        this.daoDireccion = DaoDireccion.getInstance();
    }

    // 3. Método de Acceso Global (Synchronized para seguridad en hilos)
    public static synchronized GestorHuesped getInstance() {
        if (instancia == null) {
            instancia = new GestorHuesped();
        }
        return instancia;
    }



   public ArrayList<DtoHuesped> buscarHuespedes(DtoHuesped criterios){
        //Dado unos criterios, posiblemente vacios, retorna una lista completa de todos los
        //atributos de Huesped.

        ArrayList<DtoHuesped> listaDtoHuespedesEncontrados; //datos de huespedes

        if (!criterios.estanVacios()) {
            listaDtoHuespedesEncontrados = daoHuesped.obtenerHuespedesPorCriterio(criterios);
        }
        else {
            listaDtoHuespedesEncontrados = daoHuesped.obtenerTodosLosHuespedes();
        }

        for (DtoHuesped datosHuesped : listaDtoHuespedesEncontrados) {
            // Buscamos los datos completos de la dirección usando el ID que vino en el huésped
            // Esto es crucial para que la Pantalla (CU10) tenga la dirección completa.
            if (datosHuesped.getIdDireccion() > 0) {
                 DtoDireccion dtoDireccion = daoDireccion.obtenerDireccion(datosHuesped.getIdDireccion());
                 // Asignamos el DTO de Dirección al DTO del Huésped
                 datosHuesped.setDireccion(dtoDireccion);
            }
            // Eliminado: El código para crear las entidades Huesped y Direccion
        }

        return listaDtoHuespedesEncontrados;
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
        DtoDireccion direccion = datos.getDireccion();
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


        if (datos.getTelefono() <= 0) {
            errores.add("El Teléfono es obligatorio.");
        }
        if (datos.getOcupacion() == null || datos.getOcupacion().trim().isEmpty()) {
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
        return daoHuesped.buscarPorTipoYNumeroDocumento(datos.getTipoDocumento(), datos.getNroDocumento());
    }

    public Huesped crearHuespedSinPersistir(DtoHuesped dtoHuesped) {
        Huesped huespedEntidad = new Huesped();

        if (dtoHuesped == null) {
            return huespedEntidad;
        }

        // Mapear campos básicos
        huespedEntidad.setNombres(dtoHuesped.getNombres());
        huespedEntidad.setApellido(dtoHuesped.getApellido());
        try {
            huespedEntidad.setTelefono(dtoHuesped.getTelefono());
        } catch (Exception ignored) {}

        try {
            huespedEntidad.setTipoDocumento(dtoHuesped.getTipoDocumento());
        } catch (Exception ignored) {}

        // Parseo seguro del número de documento (si viene como String)
        try {
            if (dtoHuesped.getNroDocumento() != null && !dtoHuesped.getNroDocumento().isBlank()) {
                huespedEntidad.setNroDocumento(Long.parseLong(dtoHuesped.getNroDocumento()));
            }
        } catch (NumberFormatException ignored) {}

        huespedEntidad.setCuit(dtoHuesped.getCuit());
        // Posición IVA (si viene como String)
        try {
            if (dtoHuesped.getPosicionIva() != null) {
                huespedEntidad.setPosicionIva(PosIva.fromString(dtoHuesped.getPosicionIva()));
            }
        } catch (IllegalArgumentException ignored) {}

        huespedEntidad.setEmail(dtoHuesped.getEmail());
        huespedEntidad.setFechaNacimiento(dtoHuesped.getFechaNacimiento());

        // Direccion: crear objeto Dominio.Direccion a partir de DtoDireccion si existe
        DtoDireccion dtoDir = dtoHuesped.getDireccion();
        if (dtoDir != null) {
            Direccion direccion = new Direccion();
            try { direccion.setId(dtoDir.getId()); } catch (Exception ignored) {}
            try { direccion.setCalle(dtoDir.getCalle()); } catch (Exception ignored) {}
            try { direccion.setNumero(dtoDir.getNumero()); } catch (Exception ignored) {}
            try { direccion.setPiso(dtoDir.getPiso()); } catch (Exception ignored) {}
            try { direccion.setDepartamento(dtoDir.getDepartamento()); } catch (Exception ignored) {}
            try { direccion.setLocalidad(dtoDir.getLocalidad()); } catch (Exception ignored) {}
            try { direccion.setProvincia(dtoDir.getProvincia()); } catch (Exception ignored) {}
            try { direccion.setPais(dtoDir.getPais()); } catch (Exception ignored) {}
            try { direccion.setCodigoPostal(dtoDir.getCodPostal()); } catch (Exception ignored) {}

            huespedEntidad.setDireccion(direccion);
        }

        // Estadías: convertir lista de DtoEstadia a List<Estadia> usando gestorEstadia
        List<Estadia> estadias = new ArrayList<>();
        List<DtoEstadia> listaDtoEstadias = dtoHuesped.getIdEstadias();
        if (listaDtoEstadias != null) {
            for (DtoEstadia dtoE : listaDtoEstadias) {
                try {
                    Estadia e = gestorEstadia.crearYPersistirEstadia(dtoE);
                    if (e != null) {
                        estadias.add(e);
                    }
                } catch (Exception ignored) {
                    // Si falla la conversión de una estadía, se ignora esa entrada pero se siguen procesando las demás
                }
            }
        }
        huespedEntidad.setEstadias(estadias);

        return huespedEntidad;
    }

    public DtoHuesped crearHuespedYPersistir(DtoHuesped datosHuesped) throws PersistenciaException {

        try {
            //Crear la Dirección
            // Llamamos al DAO de Dirección. Este metodo actualiza el DTO de dirección
            // con el nuevo ID generado por la BD
            DtoDireccion direccionConId = daoDireccion.persistirDireccion(datosHuesped.getDireccion());


            //Crear el Huésped
            // Ahora llamamos al DAO de Huésped, pasándole el DTO completo.
            // El DAO de Huésped sabrá sacar el ID de la dirección desde datosHuesped.getDireccion().getId()
            daoHuesped.persistirHuesped(datosHuesped);
            daoHuesped.crearEmailHuesped(datosHuesped);


            return datosHuesped;

        } catch (PersistenciaException e) {
            // Si algo falló (crear dirección O crear huésped), capturamos la excepcion
            System.err.println("Error en la capa de Gestor al crear el huésped completo:");
            // y la relanzamos para que la Pantalla se entere.
            throw e;
        }
    }


    /* Valida si un huésped puede ser eliminado del sistema
     * Un huésped solo puede eliminarse si NUNCA se alojó en el hotel
     * @param tipoDocumento Tipo de documento del huésped
     * @param nroDocumento Número de documento del huésped
     * @return true si puede eliminarse (no tiene estadías), false si no puede
     */
    public boolean puedeEliminarHuesped(String tipoDocumento, String nroDocumento) {
        if (tipoDocumento == null || tipoDocumento.trim().isEmpty()) {
            System.err.println("El tipo de documento no puede estar vacío");
            return false;
        }

        TipoDocumento tipo = TipoDocumento.valueOf(tipoDocumento);
        String documentoValido = pedirDocumento(nroDocumento, tipo);
        if (documentoValido == null || documentoValido.trim().isEmpty()) {
            System.err.println("El número de documento no es válido");
            return false;
        }

        // Verificar si el huésped tiene estadías registradas
        boolean tieneEstadias = gestorEstadia.huespedSeAlojoAlgunaVez(tipoDocumento, nroDocumento);

        return !tieneEstadias; // Retorna true solo si NO tiene estadías
    }

    /**
     * Elimina un huésped del sistema (borrado físico)
     * También elimina su dirección asociada
     * @param tipoDocumento Tipo de documento del huésped
     * @param nroDocumento Número de documento del huésped
     * @return true si se eliminó exitosamente
     */
    public boolean eliminarHuesped(String tipoDocumento, String nroDocumento) {
        try {
            // NIVEL 2.1: Verificar que puede eliminarse (ya valida que no tenga estadías)
            if (!puedeEliminarHuesped(tipoDocumento, nroDocumento)) {
                System.err.println("El huésped no puede ser eliminado porque tiene estadías registradas");
                registrarAuditoriaFallida(tipoDocumento, nroDocumento, "Tiene estadías registradas");
                return false;
            }

            // NIVEL 2.2: Verificar que no tenga reservas pendientes/activas
            if (tieneReservasPendientes(tipoDocumento, nroDocumento)) {
                System.err.println("El huésped tiene reservas pendientes y no puede ser eliminado");
                registrarAuditoriaFallida(tipoDocumento, nroDocumento, "Tiene reservas pendientes");
                return false;
            }

        // 2. Obtener el ID de la dirección antes de eliminar el huésped
            int idDireccion = daoHuesped.obtenerIdDireccion(tipoDocumento, nroDocumento);
            
            // 2.5  Eliminar los emails
            boolean emailsEliminados = daoHuesped.eliminarEmailsHuesped(tipoDocumento, nroDocumento);
            
            if (!emailsEliminados) {
                 System.err.println("No se pudieron eliminar los emails asociados al huésped.");
                 registrarAuditoriaFallida(tipoDocumento, nroDocumento, "Error en eliminación de emails (BD)");
                 return false;
            }

            // 3. Eliminar el huésped
            boolean huespedEliminado = daoHuesped.eliminarHuesped(tipoDocumento, nroDocumento);

            if (!huespedEliminado) {
                // (Si esto falla ahora, es raro, pero la auditoría es correcta)
                System.err.println("No se pudo eliminar el huésped");
                registrarAuditoriaFallida(tipoDocumento, nroDocumento, "Error en eliminación de BD");
                return false;
            }

            // 4. Si tenía dirección, eliminarla también
            if (idDireccion > 0) {
                boolean direccionEliminada = daoHuesped.eliminarDireccion(idDireccion);
                if (!direccionEliminada) {
                    System.err.println("Advertencia: El huésped fue eliminado pero hubo un error al eliminar su dirección");
                }
            }

            // NIVEL 2.3: Registrar auditoría de eliminación exitosa
            registrarAuditoriaExitosa(tipoDocumento, nroDocumento);

            return true;

        } catch (Exception e) {
            System.err.println("Error al eliminar huésped: " + e.getMessage());
            registrarAuditoriaFallida(tipoDocumento, nroDocumento, "Excepción: " + e.getMessage());
            return false;
        }
    }

    /**
     * NIVEL 2.2: Verifica si un huésped tiene reservas pendientes o activas
     * Reserva pendiente = fecha_inicio >= HOY y no tiene check-in
     */
    private boolean tieneReservasPendientes(String tipoDocumento, String nroDocumento) {
        // Consultar si existe en reserva_huesped con reservas activas/pendientes
        // Por ahora retornamos false (implementar cuando tengas el módulo de reservas)

        // TODO: Implementar cuando tengas DaoReserva
        // return daoReserva.tieneReservasPendientes(tipoDocumento, nroDocumento);

        return false; // Por ahora permite eliminar sin verificar reservas
    }

    /**
     * NIVEL 2.3: Registra en log la eliminación exitosa de un huésped
     */
    private void registrarAuditoriaExitosa(String tipoDocumento, String nroDocumento) {
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
    private void registrarAuditoriaFallida(String tipoDocumento, String nroDocumento, String motivo) {
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
        DtoDireccion direccionModificada = dtoHuespedModificado.getDireccion();

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
                    dtoHuespedModificado.setIdDireccion(direccionNuevaCreada.getId());
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
}

       