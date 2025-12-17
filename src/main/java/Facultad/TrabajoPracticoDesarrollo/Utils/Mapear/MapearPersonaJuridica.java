package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;

import Facultad.TrabajoPracticoDesarrollo.Dominio.PersonaJuridica;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoPersonaJuridica;

/**
 * Conversor entre la entidad {@link PersonaJuridica} y el DTO {@link DtoPersonaJuridica}.
 *
 * <p>Provee métodos estáticos para mapear en ambas direcciones:
 * - {@link #mapearDtoAEntidad(DtoPersonaJuridica)}: crea una entidad {@link PersonaJuridica}
 *   a partir del DTO recibido.
 * - {@link #mapearEntidadADto(PersonaJuridica)}: construye un {@link DtoPersonaJuridica} a partir
 *   de la entidad.</p>
 *
 * <p>Comportamiento ante entradas {@code null}:
 * - Si el DTO pasado a {@code mapearDtoAEntidad} es {@code null} se retorna {@code null}.
 * - Si la entidad pasada a {@code mapearEntidadADto} es {@code null} se retorna {@code null}.</p>
 *
 * <p>Notas:
 * - El mapeo de la dirección delega en {@link MapearDireccion}. En el mapeo desde DTO a entidad
 *   se asume que se mantiene el identificador de dirección si el DTO lo trae.
 * - Los campos de lista/colección (p. ej. teléfonos) se copian tal cual; ajustar si hay necesidades
 *   de conversión adicionales.</p>
 */
public class MapearPersonaJuridica  {

    /**
     * Mapea un {@link DtoPersonaJuridica} a la entidad {@link PersonaJuridica}.
     *
     * @param dtoPersonaJuridica DTO de entrada; si es {@code null} se retorna {@code null}
     * @return instancia de {@link PersonaJuridica} construida desde el DTO, o {@code null} si {@code dtoPersonaJuridica} es {@code null}
     *
     */
    public static PersonaJuridica mapearDtoAEntidad(DtoPersonaJuridica dtoPersonaJuridica) {
        if (dtoPersonaJuridica == null) return null;
        PersonaJuridica.Builder builder = new PersonaJuridica.Builder()
                .razonSocial(dtoPersonaJuridica.getRazonSocial())
                .cuit(dtoPersonaJuridica.getCuit())
                .direccion(MapearDireccion.mapearDtoAEntidad(dtoPersonaJuridica.getDireccion())) //Mantenemos ID direccion
                .idResponsablePago(dtoPersonaJuridica.getIdResponsablePago())
                .telefonos(dtoPersonaJuridica.getTelefono());


        return builder.build();
    }

    /**
     * Mapea una entidad {@link PersonaJuridica} a su {@link DtoPersonaJuridica}.
     *
     * @param personaJuridica entidad de entrada; si es {@code null} se retorna {@code null}
     * @return instancia de {@link DtoPersonaJuridica} construida desde la entidad,
     *         o {@code null} si {@code personaJuridica} es {@code null}
     *
     */
    public static DtoPersonaJuridica mapearEntidadADto(PersonaJuridica personaJuridica) {
        if (personaJuridica == null) return null;
        DtoPersonaJuridica.Builder builder = new DtoPersonaJuridica.Builder()
                .id(personaJuridica.getIdResponsable())
                .razonSocial(personaJuridica.getRazonSocial())
                .cuit(personaJuridica.getCuit())
                .telefono(personaJuridica.getTelefonos());
                MapearDireccion.mapearEntidadADto((personaJuridica.getDireccion()));


        return builder.build();
    }
}