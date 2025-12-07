package Facultad.TrabajoPracticoDesarrollo.Services.Gestores;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Direccion;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Huesped;
import Facultad.TrabajoPracticoDesarrollo.Excepciones.PersistenciaException;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoHuesped;
import Facultad.TrabajoPracticoDesarrollo.Utils.Mapear.MapearDireccion;
import Facultad.TrabajoPracticoDesarrollo.Utils.Mapear.MapearHuesped;
import Facultad.TrabajoPracticoDesarrollo.enums.PosIva;
import Facultad.TrabajoPracticoDesarrollo.Repositories.DAOs.DaoDireccion;
import Facultad.TrabajoPracticoDesarrollo.Repositories.DAOs.DaoDireccionInterfaz;
import Facultad.TrabajoPracticoDesarrollo.Repositories.DAOs.DaoHuespedInterfaz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GestorHuesped {

    // 1. La única instancia (static y private)
    private static GestorHuesped instancia;

    // Referencias a los DAO que este gestor necesita
    private final DaoHuespedInterfaz daoHuesped;
    private final DaoDireccionInterfaz daoDireccion; // Ejemplo si necesita validar habitación

    // 2. Constructor PRIVADO
    // Nadie puede hacer "new GestorReserva()" desde afuera.
    @Autowired
    private GestorHuesped(DaoHuespedInterfaz daoHuesped, DaoDireccion daoDireccion){
        this.daoHuesped = daoHuesped;
        this.daoDireccion = daoDireccion;
    }

   public ArrayList<Huesped> buscarHuespedes(DtoHuesped criterios){


       ArrayList<DtoHuesped> listaDtoHuespedesEncontrados; //datos de huéspedes

       // 1. Decidimos qué metodo del DAO llamar
       // Si el criterio NO es nulo Y NO está vacío (tiene al menos un dato real)...
       if (criterios != null && !criterios.estanVacios()) {
           System.out.println("Buscando coincidencias...");

           listaDtoHuespedesEncontrados = daoHuesped.obtenerHuespedesPorCriterio(MapearHuesped.mapearDtoAEntidad(criterios));//Obtenemos los huespedes que cumplan los criterios de busqueda
       }
       else {
           // Si es null o está "vacío" (todo Enter), traemos todos los huespedes del sistema
           System.out.println("Sin filtro: Trayendo todos los huéspedes...");
           listaDtoHuespedesEncontrados = daoHuesped.obtenerTodosLosHuespedes();//Obtenemos todos los huespedes dela bdd
       }

       ArrayList<Huesped> listaHuespedesEncontrados = new ArrayList<>();

       // For que mapea cada DtoHuesped a Huesped y lo añade a la lista de retorno
       for(int i = 0 ; i < listaDtoHuespedesEncontrados.size() ; i++){
           listaHuespedesEncontrados.add(i, MapearHuesped.mapearDtoAEntidad(listaDtoHuespedesEncontrados.get(i)));
       }

        return listaHuespedesEncontrados;
    }


    //Validacion de negocios de los datos ingresados en el formulario del CU9
    public List<String> validarDatosHuesped(DtoHuesped datos){
        List<String> errores = new ArrayList<>();

        // Regla especial CUIT/IVA
        //Si la posición IVA es "Responsable Inscripto", el CUIT no puede ser vacio
        if (datos.getPosicionIva().equals(PosIva.RESPONSABLE_INSCRIPTO) ) {
            if (datos.getCuit() == null || datos.getCuit().trim().isEmpty()) {
                errores.add("El CUIT es obligatorio para Responsables Inscriptos.");
            }
        }

        return errores;//Lista con los errores que encontramos
    }


    //buscarPorTipoYNumeroDocumento  <- nombre en Diag de Secuencia
    //Esto tambien es una validacion de negocio. El Tipo y Numero de documento ingresado, no puede existir en el sistema
    public Huesped chequearDuplicado(DtoHuesped datos) throws PersistenciaException {
        // Llamamos al DAO para buscar por tipo y número.
        // Si existe, nos devolverá el DTO con los datos de la BD (Nombre viejo, apellido viejo, etc).

        // Si obtenerHuesped devuelve algo distinto de null, es el duplicado real.
        //Llamamos al daoHuesped para que busque en la bdd con los datos ingresados
        return MapearHuesped.mapearDtoAEntidad(daoHuesped.obtenerHuesped(datos.getTipoDocumento(), datos.getNroDocumento()));
    }

    public Huesped crearHuespedSinPersistir(DtoHuesped dtoHuesped){
        return MapearHuesped.mapearDtoAEntidad(dtoHuesped);
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

            // 1. Recuperar ID de la dirección vieja para poder sobreescribirla
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

       