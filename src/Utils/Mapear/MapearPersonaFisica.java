package Utils.Mapear;

import Dominio.PersonaFisica;
import ResponsablePago.DtoPersonaFisica;

public class MapearPersonaFisica implements MapeoInterfaz<DtoPersonaFisica, PersonaFisica> {

    @Override
    public PersonaFisica mapearDtoAEntidad(DtoPersonaFisica dtoPersonaFisica) {
        if (dtoPersonaFisica == null) return null;

        PersonaFisica.Builder builder = new PersonaFisica.Builder(
                dtoPersonaFisica.getHuesped()
        )
                .idResponsablePago(dtoPersonaFisica.getIdResponsablePago());


        return builder.build();
    }

    @Override
    public DtoPersonaFisica mapearEntidadADto(PersonaFisica personaFisica) {
        if (personaFisica == null) return null;

        DtoPersonaFisica.Builder builder = new DtoPersonaFisica.Builder()
                .id(personaFisica.getIdResponsablePago())
                .huesped(personaFisica.getHuesped());

        return builder.build();
    }
}