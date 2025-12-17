package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;

import Facultad.TrabajoPracticoDesarrollo.Dominio.PersonaFisica;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoPersonaFisica;

/**
 * Conversor entre la entidad {@link PersonaFisica} y el DTO {@link DtoPersonaFisica}.
 *
 * <p>Provee métodos estáticos para mapear en ambas direcciones:
 * - {@link #mapearDtoAEntidad(DtoPersonaFisica)}: crea una entidad {@link PersonaFisica}
 *   a partir del DTO recibido, delegando el mapeo del huésped a {@link MapearHuesped}.
 * - {@link #mapearEntidadADto(PersonaFisica)}: construye un {@link DtoPersonaFisica} a partir
 *   de la entidad, delegando igualmente el mapeo del huésped.</p>
 *
 * <p>Comportamiento ante entradas {@code null}:
 * - Si el DTO pasado a {@code mapearDtoAEntidad} es {@code null} se retorna {@code null}.
 * - Si la entidad pasada a {@code mapearEntidadADto} es {@code null} se retorna {@code null}.</p>
 *
 * <p>Notas:
 * - Los métodos usan los builders de las clases de dominio/DTO para construir las instancias.
 * - El campo {@code idResponsablePago} se copia directamente desde el DTO a la entidad y
 *   el identificador de la entidad se obtiene desde {@code getIdResponsable()} para el DTO.</p>
 */
public class MapearPersonaFisica  {

    /**
     * Mapea un {@link DtoPersonaFisica} a la entidad {@link PersonaFisica}.
     *
     * @param dtoPersonaFisica DTO de entrada; si es {@code null} se retorna {@code null}
     * @return instancia de {@link PersonaFisica} construida desde el DTO, o {@code null} si {@code dtoPersonaFisica} es {@code null}
     *
     * <p>Descripción:
     * - Se delega el mapeo del objeto {@code Huesped} a {@link MapearHuesped#mapearDtoAEntidad}.
     * - Copia el campo {@code idResponsablePago} tal cual desde el DTO al builder de la entidad.</p>
     */
    public static PersonaFisica mapearDtoAEntidad(DtoPersonaFisica dtoPersonaFisica) {
        if (dtoPersonaFisica == null) return null;

        PersonaFisica.Builder builder = new PersonaFisica.Builder()
                .huesped(MapearHuesped.mapearDtoAEntidad(dtoPersonaFisica.getHuesped()))
                .idResponsablePago(dtoPersonaFisica.getIdResponsablePago());

        return builder.build();
    }

    /**
     * Mapea una entidad {@link PersonaFisica} a su {@link DtoPersonaFisica}.
     *
     * @param personaFisica entidad de entrada; si es {@code null} se retorna {@code null}
     * @return instancia de {@link DtoPersonaFisica} construida desde la entidad,
     *         o {@code null} si {@code personaFisica} es {@code null}
     *
     * <p>Descripción:
     * - Se obtiene el identificador de responsable mediante {@code getIdResponsable()}.
     * - El mapeo del {@code Huesped} se realiza mediante {@link MapearHuesped#mapearEntidadADto}.</p>
     */
    public static DtoPersonaFisica mapearEntidadADto(PersonaFisica personaFisica) {
        if (personaFisica == null) return null;

        DtoPersonaFisica.Builder builder = new DtoPersonaFisica.Builder()
                .id(personaFisica.getIdResponsable())
                .huesped(MapearHuesped.mapearEntidadADto(personaFisica.getHuesped()));

        return builder.build();
    }
}