package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;

import Facultad.TrabajoPracticoDesarrollo.Dominio.PersonaJuridica;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoPersonaJuridica;


public class MapearPersonaJuridica  {

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