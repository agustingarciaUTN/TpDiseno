package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Direccion;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoDireccion;
/**
 * Conversor entre {@link DtoDireccion} y {@link Direccion}.
 *
 * <p>Provee métodos estáticos para mapear en ambas direcciones y para
 * actualizar una entidad existente desde un DTO.</p>
 *
 * <p>Comportamiento:
 * - Si la entrada es {@code null}, los métodos retornan {@code null} (o no hacen nada en el caso de actualización).
 * - El método de mapeo a entidad no asigna el identificador; la línea correspondiente está comentada
 *   ya que el manejo del id depende del flujo de creación/actualización.</p>
 */
public class MapearDireccion  {

    /**
     * Mapea un {@link DtoDireccion} a la entidad {@link Direccion}.
     *
     * @param dto DTO de entrada; puede ser {@code null}
     * @return instancia de {@link Direccion} construida desde el DTO, o {@code null} si {@code dto} es {@code null}
     *
     * <p>Nota: la asignación del identificador (id) está comentada porque en algunos flujos
     * puede no ser correcto setear el id al crear una nueva entidad.</p>
     */
    public static Direccion mapearDtoAEntidad(DtoDireccion dto) {
        if (dto == null) return null;
        return new Direccion.Builder()
                //.id(dto.getIdDireccion())  ESTO HAY QUE VERLO, NO SE SI ESTA BIEN SETEAR LA DIRECCION CUANDO CREAMOS EL DTO PORQUE LA SETEARIAMOS COMO 0 O NULL
                .calle(dto.getCalle())
                .numero((dto.getNumero()))
                .departamento(dto.getDepartamento())
                .piso(dto.getPiso())
                .codigoPostal(dto.getCodPostal())
                .localidad(dto.getLocalidad())
                .provincia(dto.getProvincia())
                .pais(dto.getPais())
                .build();
    }

    /**
     * Mapea una entidad {@link Direccion} a su {@link DtoDireccion}.
     *
     * @param entity entidad de entrada; puede ser {@code null}
     * @return instancia de {@link DtoDireccion} construida desde la entidad, o {@code null} si {@code entity} es {@code null}
     */
    public static DtoDireccion mapearEntidadADto(Direccion entity) {
        if (entity == null) return null;
        return new DtoDireccion.Builder()
                .calle(entity.getCalle())
                .numero(entity.getNumero())
                .localidad(entity.getLocalidad())
                .provincia(entity.getProvincia())
                .pais(entity.getPais())
                .idDireccion(entity.getId())
                .departamento(entity.getDepartamento())
                .piso(entity.getPiso())
                .codPostal(entity.getCodPostal())
                .build();
    }

    // (Sirve para MODIFICAR / ACTUALIZAR)
    /**
     * Actualiza los campos de la entidad {@link Direccion} usando los valores del {@link DtoDireccion}.
     *
     * <p>Uso típico: modificar una entidad recuperada de la base de datos antes de persistir.
     * No crea una nueva instancia; modifica la ya existente.</p>
     *
     * @param direccion entidad a actualizar; si es {@code null} no hace nada
     * @param dto DTO con los nuevos valores; si es {@code null} no hace nada
     */
    public static void actualizarEntidadDesdeDto(Direccion direccion, DtoDireccion dto) {
        if (direccion == null || dto == null) return;

        // Solo actualizamos los campos, NO hacemos 'new'
        direccion.setCalle(dto.getCalle());
        direccion.setNumero(dto.getNumero());
        direccion.setDepartamento(dto.getDepartamento());
        direccion.setPiso(dto.getPiso());
        direccion.setCodPostal(dto.getCodPostal());
        direccion.setLocalidad(dto.getLocalidad());
        direccion.setProvincia(dto.getProvincia());
        direccion.setPais(dto.getPais());
    }
}