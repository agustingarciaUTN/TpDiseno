package Facultad.TrabajoPracticoDesarrollo.Services;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Direccion;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Huesped;
import Facultad.TrabajoPracticoDesarrollo.Dominio.HuespedId;
import Facultad.TrabajoPracticoDesarrollo.Repositories.HuespedRepository;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoHuesped;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoDireccion;
import Facultad.TrabajoPracticoDesarrollo.Utils.Mapear.MapearHuesped;
import Facultad.TrabajoPracticoDesarrollo.enums.PosIva;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class HuespedService {

    private final HuespedRepository huespedRepository;

    @Autowired
    public HuespedService(HuespedRepository huespedRepository) {
        this.huespedRepository = huespedRepository;
    }

    /**
     * Busca huéspedes según los criterios del DTO.
     * Si el DTO está vacío, devuelve todos.
     */
    @Transactional(readOnly = true)
    public List<Huesped> buscarHuespedes(DtoHuesped criterios) {

        // 1. Decidimos qué método del Repository llamar
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
        if (datos.getPosicionIva() == PosIva.RESPONSABLEINSCRIPTO) {
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

    /**
     * Lógica de Alta o Modificación (UPSERT).
     * Reemplaza la lógica manual de JDBC por lógica JPA.
     */
    @Transactional
    public void upsertHuesped(DtoHuesped dto) {
        // 1. Buscamos si ya existe en la BD
        HuespedId id = new HuespedId(dto.getTipoDocumento(), dto.getNroDocumento());
        Optional<Huesped> existenteOpt = huespedRepository.findById(id);

        if (existenteOpt.isPresent()) {
            // === CAMINO: MODIFICACIÓN ===
            // Traemos la entidad "viva" de la base de datos (Managed)
            Huesped huespedExistente = existenteOpt.get();

            // Actualizamos sus campos con los datos nuevos del DTO
            actualizarDatosHuesped(huespedExistente, dto);

            // Guardamos (JPA detecta que es un update porque la entidad ya existe)
            huespedRepository.save(huespedExistente);

        } else {
            // === CAMINO: ALTA ===
            // Convertimos DTO a Entidad nueva
            Huesped huespedNuevo = MapearHuesped.mapearDtoAEntidad(dto);

            // Guardamos (JPA inserta Huésped y Dirección en cascada)
            huespedRepository.save(huespedNuevo);
        }
    }

    // Método auxiliar para pasar datos del DTO a la Entidad existente
    // (Esto mantiene el ID de la dirección vieja y solo cambia los valores)
    private void actualizarDatosHuesped(Huesped entidad, DtoHuesped dto) {
        entidad.setApellido(dto.getApellido());
        entidad.setNombres(dto.getNombres());
        entidad.setFechaNacimiento(dto.getFechaNacimiento());
        entidad.setNacionalidad(dto.getNacionalidad());
        entidad.setPosicionIva(dto.getPosicionIva());
        entidad.setCuit(dto.getCuit());

        // Actualizar listas (reemplazamos completas)
        entidad.setTelefono(dto.getTelefono());
        entidad.setEmail(dto.getEmail());
        entidad.setOcupacion(dto.getOcupacion());

        // Actualizar Dirección (sin perder el ID de la dirección en BD)
        Direccion dirEntidad = entidad.getDireccion();
        DtoDireccion dirDto = dto.getDtoDireccion();

        if (dirEntidad != null && dirDto != null) {
            dirEntidad.setCalle(dirDto.getCalle());
            dirEntidad.setNumero(dirDto.getNumero());
            dirEntidad.setDepartamento(dirDto.getDepartamento());
            dirEntidad.setPiso(dirDto.getPiso());
            dirEntidad.setCodPostal(dirDto.getCodPostal());
            dirEntidad.setLocalidad(dirDto.getLocalidad());
            dirEntidad.setProvincia(dirDto.getProvincia());
            dirEntidad.setPais(dirDto.getPais());
        } else if (dirDto != null) {
            // Si antes no tenía dirección, le ponemos una nueva
            // (Aquí usarías tu mapper de dirección)
            // entidad.setDireccion(MapearDireccion.mapearDtoAEntidad(dirDto));
            // Nota: Agrega MapearDireccion si no lo tienes importado
        }
    }
}