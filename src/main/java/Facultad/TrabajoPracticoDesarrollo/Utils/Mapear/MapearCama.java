package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Cama;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoCama;
/**
 * Conversor simple entre la entidad {@link Cama} y el DTO {@link DtoCama}.
 * Provee métodos estáticos para mapear en ambas direcciones.
 * Si la entrada es {@code null}, devuelve {@code null}.
 */
public class MapearCama{
    /**
     * Mapea un {@link DtoCama} a la entidad {@link Cama}.
     *
     * @param dto DTO de entrada; puede ser {@code null}
     * @return instancia de {@link Cama} mapeada desde el DTO, o {@code null} si {@code dto} es {@code null}
     */
    public static Cama mapearDtoAEntidad(DtoCama dto) {
        if (dto == null) return null;
        return new Cama.Builder()
                .idCama(dto.getIdCama())
                .tipoCama(dto.getTipoCama())
                .build();
    }
    /**
     * Mapea una entidad {@link Cama} a su {@link DtoCama}.
     *
     * @param entidad entidad de entrada; puede ser {@code null}
     * @return instancia de {@link DtoCama} mapeada desde la entidad, o {@code null} si {@code entidad} es {@code null}
     */
    public static DtoCama mapearEntidadADto(Cama entidad) {
        if (entidad == null) return null;
        return new DtoCama.Builder()
                .idCama(entidad.getIdCama())
                .tipoCama(entidad.getTipoCama())
                .build();
    }
}