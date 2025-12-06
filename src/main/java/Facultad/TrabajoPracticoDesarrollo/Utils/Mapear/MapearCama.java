package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Cama;
import Facultad.TrabajoPracticoDesarrollo.Habitacion.DtoCama;

public class MapearCama{

    public static Cama mapearDtoAEntidad(DtoCama dto) {
        if (dto == null) return null;
        return new Cama.Builder()
                .idCama(dto.getIdCama())
                .tipoCama(dto.getTipoCama())
                .build();
    }

    public static DtoCama mapearEntidadADto(Cama entidad) {
        if (entidad == null) return null;
        return new DtoCama.Builder()
                .idCama(entidad.getIdCama())
                .tipoCama(entidad.getTipoCama())
                .build();
    }
}