package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;

import Facultad.TrabajoPracticoDesarrollo.Dominio.PersonaFisica;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoPersonaFisica;

public class MapearPersonaFisica  {

    public static PersonaFisica mapearDtoAEntidad(DtoPersonaFisica dtoPersonaFisica) {
        if (dtoPersonaFisica == null) return null;

        PersonaFisica.Builder builder = new PersonaFisica.Builder(
                MapearHuesped.mapearDtoAEntidad(dtoPersonaFisica.getHuesped())
        )
                .dtoPersonaFisica.getIdResponsablePago();


        return builder.build();
    }

    public static DtoPersonaFisica mapearEntidadADto(PersonaFisica personaFisica) {
        if (personaFisica == null) return null;

        DtoPersonaFisica.Builder builder = new DtoPersonaFisica.Builder()
                .id(personaFisica.getIdResponsablePago())
                .huesped(MapearHuesped.mapearEntidadADto(personaFisica.getHuesped()));

        return builder.build();
    }
}