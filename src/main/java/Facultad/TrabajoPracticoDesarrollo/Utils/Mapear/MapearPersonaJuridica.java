package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;

import Facultad.TrabajoPracticoDesarrollo.Dominio.PersonaJuridica;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoPersonaJuridica;


public class MapearPersonaJuridica  {

    public static PersonaJuridica mapearDtoAEntidad(DtoPersonaJuridica dtoPersonaJuridica) {
        if (dtoPersonaJuridica == null) return null;
        PersonaJuridica.Builder builder = new PersonaJuridica.Builder(
                dtoPersonaJuridica.getRazonSocial(),
                dtoPersonaJuridica.getCuit(),
                MapearDireccion.mapearDtoAEntidad(dtoPersonaJuridica.getDireccion()) // Mantenemos ID dirección
        )
                .idResponsablePago(dtoPersonaJuridica.getIdResponsablePago())
                .telefonos(dtoPersonaJuridica.getTelefono());


        return builder.build();
    }

    public static DtoPersonaJuridica mapearEntidadADto(PersonaJuridica personaJuridica) {
        if (personaJuridica == null) return null;
        DtoPersonaJuridica.Builder builder = new DtoPersonaJuridica.Builder()
            //    .id(personaJuridica.getIdResponsablePago())
                .razonSocial(personaJuridica.getRazonSocial())
                .cuit(personaJuridica.getCuit())
                .telefono(personaJuridica.getTelefonos());
                MapearDireccion.mapearEntidadADto((personaJuridica.getDireccion()));


        return builder.build();
    }
}