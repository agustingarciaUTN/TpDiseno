package Facultad.TrabajoPracticoDesarrollo.Services;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Direccion;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Huesped;
import Facultad.TrabajoPracticoDesarrollo.Dominio.HuespedId;
import Facultad.TrabajoPracticoDesarrollo.Repositories.DireccionRepository;
import Facultad.TrabajoPracticoDesarrollo.Repositories.HuespedRepository;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoHuesped;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoDireccion;
import Facultad.TrabajoPracticoDesarrollo.Utils.Mapear.MapearDireccion;
import Facultad.TrabajoPracticoDesarrollo.Utils.Mapear.MapearHuesped;
import Facultad.TrabajoPracticoDesarrollo.enums.PosIva;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service //Le avisa a Spring que esta clase contiene Lógica de Negocio y que debe estar disponible para ser inyectada en los Controllers
public class HuespedService {

    //Pedimos la interfaz del repositorio y Spring te da una implementación que funciona
    private final HuespedRepository huespedRepository;
    private final DireccionRepository direccionRepository;

    @Autowired
    public HuespedService(HuespedRepository huespedRepository, DireccionRepository direccionRepository) {
        this.huespedRepository = huespedRepository;
        this.direccionRepository = direccionRepository;
    }

    /**
     * Busca huéspedes según los criterios del DTO.
     * Si el DTO está vacío, devuelve todos.
     */
    @Transactional(readOnly = true)
    public List<Huesped> buscarHuespedes(DtoHuesped criterios) {

        // 1. Decidimos qué metodo del Repository llamar
        if (criterios != null && !criterios.estanVacios()) {
            System.out.println("Buscando coincidencias...");

            // Usamos la query personalizada del Repository
            return huespedRepository.buscarPorCriterios(
                    criterios.getApellido(),
                    criterios.getNombres(),
                    criterios.getTipoDocumento(),
                    criterios.getNroDocumento()
            );
        } else {
            System.out.println("Sin filtro: Trayendo todos los huéspedes...");
            return huespedRepository.findAll();
        }
    }

    /**
     * Validaciones de negocio (adicionales a las anotaciones @Valid del DTO).
     * Ejemplo: Reglas cruzadas como CUIT vs IVA.
     */
    public List<String> validarDatosHuesped(DtoHuesped datos) {
        List<String> errores = new ArrayList<>();

        // Regla especial CUIT/IVA
        if (datos.getPosicionIva() == PosIva.RESPONSABLE_INSCRIPTO) {
            if (datos.getCuit() == null || datos.getCuit().trim().isEmpty()) {
                errores.add("El CUIT es obligatorio para Responsables Inscriptos.");
            }
        }
        return errores;
    }

    /**
     * Verifica si existe un huésped con esa clave compuesta.
     * Retorna la Entidad si existe, o null si no.
     */
    @Transactional(readOnly = true)
    public Huesped chequearDuplicado(DtoHuesped datos) {
        HuespedId id = new HuespedId(datos.getTipoDocumento(), datos.getNroDocumento());
        return huespedRepository.findById(id).orElse(null);
    }

    public Huesped crearHuespedSinPersistir(DtoHuesped dtoHuesped) {
        return MapearHuesped.mapearDtoAEntidad(dtoHuesped);
    }


    // Una vez que cargamos un objeto con findById, estamos obligados a trabajar sobre ESE objeto.
    // No podemos traer otro nuevo a partir del mapearDtoAEntidad, ya que tendria la misma PK.
    //a forma correcta y profesional en JPA no es "crear un objeto para pisar el viejo", sino "modificar el objeto que ya tenés en la mano"

    //Traés el objeto (findById). -> Está "Limpio".
    //
    //Le hacés setNombre(...), setTelefono(...). -> Ahora está "Sucio" (Dirty).
    //
    //Termina la transacción (@Transactional).
    //
    //Hibernate ve que está sucio y automáticamente genera el UPDATE solo con los campos que cambiaron.

    /**
     * Lógica de Alta o Modificación (UPSERT).
     * Reemplaza la lógica manual de JDBC por lógica JPA.
     */
    @Transactional
    public void upsertHuesped(DtoHuesped dto) {
        // 1. Buscamos si ya existe en la BD (Respetando el "alt" del diagrama)
        HuespedId id = new HuespedId(dto.getTipoDocumento(), dto.getNroDocumento());
        Optional<Huesped> existenteOpt = huespedRepository.findById(id);

        if (existenteOpt.isPresent()) {
            // ==========================================
            // CAMINO: MODIFICACIÓN (El huésped YA existe)
            // ==========================================
            Huesped huespedExistente = existenteOpt.get();

            // A. Actualizamos datos simples del Huésped
            // (Es necesario pasar los datos nuevos del DTO a la Entidad vieja)
            MapearHuesped.actualizarEntidadDesdeDto(huespedExistente, dto);

            // B. Actualizamos la Dirección (MANUALMENTE)
            Direccion direccionExistente = huespedExistente.getDireccion();

            if (direccionExistente != null && dto.getDtoDireccion() != null) {
                // Pasamos los datos del DTO a la entidad Dirección existente
                Direccion dirNueva = MapearDireccion.mapearDtoAEntidad(dto.getDtoDireccion());

                direccionExistente.setCalle(dirNueva.getCalle());
                direccionExistente.setNumero(dirNueva.getNumero());
                direccionExistente.setDepartamento(dirNueva.getDepartamento());
                direccionExistente.setPiso(dirNueva.getPiso());
                direccionExistente.setCodPostal(dirNueva.getCodPostal());
                direccionExistente.setLocalidad(dirNueva.getLocalidad());
                direccionExistente.setProvincia(dirNueva.getProvincia());
                direccionExistente.setPais(dirNueva.getPais());

                // DIAGRAMA: "GHU -> DD: modificarYPersistirDireccion"
                direccionRepository.save(direccionExistente);
            }

            // C. Guardamos finalmente el Huésped
            // DIAGRAMA: "GHU -> DHU: modificarYPersistirHuesped"
            huespedRepository.save(huespedExistente);

        } else {
            // ==========================================
            // CAMINO: ALTA (El huésped NO existe)
            // ==========================================

            // 1. Instanciamos los objetos (sin ID todavía)
            // DIAGRAMA: "create Direccion as D" y "create Huesped as H"
            Huesped nuevoHuesped = MapearHuesped.mapearDtoAEntidad(dto);
            Direccion nuevaDireccion = MapearDireccion.mapearDtoAEntidad(dto.getDtoDireccion());

            // 2. Guardamos la Dirección PRIMERO (Independiente)
            // Esto genera el ID en la base de datos para la dirección.
            // DIAGRAMA: "GHU -> DD: persistirDireccion"
            direccionRepository.save(nuevaDireccion);

            // 3. Asociamos la dirección persistida al huésped
            nuevoHuesped.setDireccion(nuevaDireccion);

            // 4. Guardamos el Huésped
            // DIAGRAMA: "GHU -> DHU: persistirHuesped"
            huespedRepository.save(nuevoHuesped);
        }
    }


}