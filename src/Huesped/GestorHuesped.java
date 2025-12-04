package Huesped;



import java.util.Date;

import Dominio.Direccion;
import Dominio.Huesped;
import Utils.Mapear.MapearDireccion;
import enums.PosIva;

import Excepciones.PersistenciaException;

import Utils.Mapear.MapearHuesped;

import java.util.List;
import java.util.ArrayList;


public class GestorHuesped {

    // 1. La única instancia (static y private)
    private static GestorHuesped instancia;

    // Referencias a los DAO que este gestor necesita
    private final DaoHuespedInterfaz daoHuesped;
    private final DaoDireccionInterfaz daoDireccion; // Ejemplo si necesita validar habitación

    // 2. Constructor PRIVADO
    // Nadie puede hacer "new GestorReserva()" desde afuera.
    private GestorHuesped() {
        // ¡IMPORTANTE! Aquí obtenemos las instancias de los DAO
        this.daoHuesped = DaoHuesped.getInstance();
        this.daoDireccion = DaoDireccion.getInstance();

    }

    // 3. Metodo de Acceso Global (Synchronized para seguridad en hilos)
    public static synchronized GestorHuesped getInstance() {
        if (instancia == null) {
            instancia = new GestorHuesped();
        }
        return instancia;
    }



   public ArrayList<Huesped> buscarHuespedes(DtoHuesped criterios){


       ArrayList<DtoHuesped> listaDtoHuespedesEncontrados; //datos de huéspedes

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
           listaHuespedesEncontrados.add(i, MapearHuesped.mapearDtoAEntidad(listaDtoHuespedesEncontrados.get(i)));
       }

        return listaHuespedesEncontrados;
    }


    //Validacion de negocios de los datos ingresados en el formulario del CU9
    public List<String> validarDatosHuesped(DtoHuesped datos){
        List<String> errores = new ArrayList<>();

        // Regla especial CUIT/IVA
        //Si la posición IVA es "Responsable Inscripto", el CUIT no puede ser vacio, ademas validamos su formato con Regex
        if (datos.getPosicionIva().equals(PosIva.ResponsableInscripto) ) {
            if (datos.getCuit() == null || datos.getCuit().trim().isEmpty()) {
                errores.add("El CUIT es obligatorio para Responsables Inscriptos.");
            }
        }

        return errores;//Lista con los errores que encontramos
    }


    //buscarPorTipoYNumeroDocumento  <- nombre en Diag de Secuencia
    //Esto tambien es una validacion de negocio. El Tipo y Numero de documento ingresado, no puede existir en el sistema
    public DtoHuesped chequearDuplicado(DtoHuesped datos) throws PersistenciaException {
        // Llamamos al DAO para buscar por tipo y número.
        // Si existe, nos devolverá el DTO con los datos de la BD (Nombre viejo, apellido viejo, etc).

        // Si obtenerHuesped devuelve algo distinto de null, es el duplicado real.
        return daoHuesped.obtenerHuesped(datos.getTipoDocumento(), datos.getNroDocumento());
    }

    public Huesped crearHuespedSinPersistir(DtoHuesped dtoHuesped){
        DtoDireccion dtoDireccion = dtoHuesped.getDtoDireccion();
        Direccion direccion = new Direccion();
        direccion = direccion.crearSinPersistirDireccion(dtoDireccion);

        Huesped huesped = new Huesped();
        huesped = huesped.crearSinPersistirHuesped(dtoHuesped, direccion);

        return huesped;
    }

    //Lógica de UPSERT para CU9
    public void upsertHuesped(DtoHuesped dtoHuesped) throws PersistenciaException {

        // 1. Verificamos existencia (alt)
        boolean existe = daoHuesped.existeHuesped(dtoHuesped.getTipoDocumento(), dtoHuesped.getNroDocumento());

        // A. Convertir DTOs a Entidades (Usando los mappers)
        Direccion direccionEntidad = MapearDireccion.mapearDtoAEntidad(dtoHuesped.getDtoDireccion());
        Huesped huespedEntidad = MapearHuesped.mapearDtoAEntidad(dtoHuesped);

        if (!existe) {
            // === CAMINO: ALTA (No existe) ===

            // 1. Persistir Dirección primero para obtener ID
            daoDireccion.persistirDireccion(direccionEntidad);

            // 2. Asignar la dirección (con ID generado) al huésped
            huespedEntidad.setDireccion(direccionEntidad);

            // 3. Persistir Huésped (El DAO se encarga de guardar emails, teléfonos, etc.)
            daoHuesped.persistirHuesped(huespedEntidad);

        } else {
            // === CAMINO: MODIFICACIÓN (Existe - "Aceptar Igualmente") ===

            // 1. Recuperar ID de la dirección vieja
            int idDireccionExistente = daoHuesped.obtenerIdDireccion(dtoHuesped.getTipoDocumento(), dtoHuesped.getNroDocumento());

            // 2. Actualizar la Dirección si existe
            if (idDireccionExistente > 0) {
                direccionEntidad.setId(idDireccionExistente);
                daoDireccion.modificarDireccion(direccionEntidad);
                // Vinculamos la dirección actualizada a la entidad huésped
                huespedEntidad.setDireccion(direccionEntidad);
            }

            // 3. Actualizar el Huésped
            // (El DAO se encarga de borrar e insertar de nuevo emails, teléfonos, etc.)
            daoHuesped.modificarHuesped(huespedEntidad);
        }
    }
}

       