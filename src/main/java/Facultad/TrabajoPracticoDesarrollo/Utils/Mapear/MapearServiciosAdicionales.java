package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Estadia;
import Facultad.TrabajoPracticoDesarrollo.Dominio.ServiciosAdicionales;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoServiciosAdicionales;

public class MapearServiciosAdicionales  {


    public static ServiciosAdicionales mapearDtoAEntidad(DtoServiciosAdicionales dtoServiciosAdicionales) {
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


    public static DtoServiciosAdicionales mapearEntidadADto(ServiciosAdicionales serviciosAdicionales) {
        if (serviciosAdicionales == null) return null;

        return new DtoServiciosAdicionales.Builder()
                .id(serviciosAdicionales.getId())
                .tipo(serviciosAdicionales.getTipoServicio())
                .descripcion(serviciosAdicionales.getDescripcion())
                .valor(serviciosAdicionales.getValor())
                .fecha(serviciosAdicionales.getFechaConsumo())
                .idEstadia(serviciosAdicionales.getEstadia().getIdEstadia())
                .build();
    }
}