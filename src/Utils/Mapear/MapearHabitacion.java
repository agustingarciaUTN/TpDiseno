package Utils.Mapear;

import Dominio.Habitacion;
import Habitacion.DtoHabitacion;

public class MapearHabitacion  {

    private final MapearCama mapearCama = new MapearCama();


    public static Habitacion mapearDtoAEntidad(DtoHabitacion dto) {
        if (dto == null) return null;

        Habitacion.Builder builder = new Habitacion.Builder(
                dto.getNumero(),
                dto.getTipoHabitacion(),
                dto.getCapacidad()
        )
                .estado(dto.getEstadoHabitacion())
                .costo(dto.getCostoPorNoche());


        return builder.build();
    }


    public static DtoHabitacion mapearEntidadADto(Habitacion entidad) {
        if (entidad == null) return null;

        DtoHabitacion.Builder builder = new DtoHabitacion.Builder(entidad.getNumero(), entidad.getTipoHabitacion(), entidad.getCapacidad())
                .estado(entidad.getEstadoHabitacion())
                .costo(entidad.getCostoPorNoche());


        return builder.build();
    }
}