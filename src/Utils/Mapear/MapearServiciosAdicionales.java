package Utils.Mapear;

import Dominio.ServiciosAdicionales;
import Dominio.Estadia;
import Estadia.DtoServiciosAdicionales;

public class MapearServiciosAdicionales implements MapeoInterfaz<DtoServiciosAdicionales, ServiciosAdicionales> {

    @Override
    public ServiciosAdicionales mapearDtoAEntidad(DtoServiciosAdicionales dtoServiciosAdicionales) {
        if (dtoServiciosAdicionales == null) return null;

        // Referencia a Estadia (Solo ID)
        Estadia estadiaRef = new Estadia.Builder(null).idEstadia(dtoServiciosAdicionales.getIdEstadia()).build();

        return new ServiciosAdicionales.Builder(
                dtoServiciosAdicionales.getTipoServicio(),
                dtoServiciosAdicionales.getValorServicio(),
                dtoServiciosAdicionales.getFechaConsumo()
        )
                .id(dtoServiciosAdicionales.getIdServicio())
                .descripcion(dtoServiciosAdicionales.getDescripcionServicio())
                .estadia(estadiaRef)
                .build();
    }

    @Override
    public DtoServiciosAdicionales mapearEntidadADto(ServiciosAdicionales serviciosAdicionales) {
        if (serviciosAdicionales == null) return null;

        return new DtoServiciosAdicionales.Builder()
                .id(serviciosAdicionales.getIdServicio())
                .tipo(serviciosAdicionales.getTipoServicio())
                .descripcion(serviciosAdicionales.getDescripcionServicio())
                .valor(serviciosAdicionales.getValorServicio())
                .fecha(serviciosAdicionales.getFechaConsumo())
                .idEstadia(serviciosAdicionales.getEstadia().getIdEstadia())
                .build();
    }
}