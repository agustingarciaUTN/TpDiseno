package Utils.Mapear;

import Dominio.PersonaJuridica;
import ResponsablePago.DtoPersonaJuridica;


public class MapearPersonaJuridica  {

    public static PersonaJuridica mapearDtoAEntidad(DtoPersonaJuridica dtoPersonaJuridica) {
        if (dtoPersonaJuridica == null) return null;
        MapearDireccion mapaDireccion = new MapearDireccion();
        PersonaJuridica.Builder builder = new PersonaJuridica.Builder(
                dtoPersonaJuridica.getRazonSocial(),
                dtoPersonaJuridica.getCuit(),
                mapaDireccion.mapearDtoAEntidad(dtoPersonaJuridica.getDireccion()) // Mantenemos ID dirección
        )
                .idResponsablePago(dtoPersonaJuridica.getIdResponsablePago())
                .telefono(dtoPersonaJuridica.getTelefono());


        return builder.build();
    }

    public static DtoPersonaJuridica mapearEntidadADto(PersonaJuridica personaJuridica) {
        if (personaJuridica == null) return null;
        MapearDireccion mapaDireccion = new MapearDireccion();
        DtoPersonaJuridica.Builder builder = new DtoPersonaJuridica.Builder()
                .id(personaJuridica.getIdResponsablePago())
                .razonSocial(personaJuridica.getRazonSocial())
                .cuit(personaJuridica.getCuit())
                .telefono(personaJuridica.getTelefono());
                mapaDireccion.mapearEntidadADto((personaJuridica.getDireccion()));


        return builder.build();
    }
}