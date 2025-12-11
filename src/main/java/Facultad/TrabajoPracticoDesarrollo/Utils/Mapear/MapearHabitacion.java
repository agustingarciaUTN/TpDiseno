package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Habitacion;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoHabitacion;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Reserva;

import java.util.ArrayList;

public class MapearHabitacion  {

    private final MapearCama mapearCama = new MapearCama();


    public static Habitacion mapearDtoAEntidad(DtoHabitacion dto, ArrayList<Reserva> reservas) {
        if (dto == null) return null;

        Habitacion.Builder builder = new Habitacion.Builder()
                .numero(dto.getNumero())
                .tipoHabitacion(dto.getTipoHabitacion())
                .capacidad(dto.getCapacidad())
                .estado(dto.getEstadoHabitacion())
                .costo(dto.getCostoPorNoche())
                .reservas(reservas);


        return builder.build();
    }


    public static DtoHabitacion mapearEntidadADto(Habitacion entidad) {
        if (entidad == null) return null;

        DtoHabitacion.Builder builder = new DtoHabitacion.Builder()
                .numero(entidad.getNumero())
                .tipoHabitacion(entidad.getTipoHabitacion())
                .capacidad(entidad.getCapacidad())
                .estado(entidad.getEstadoHabitacion())
                .costo(entidad.getCostoPorNoche())
                .reservas(MapearReserva.mapearListaReservas(entidad.getReservas()));


        return builder.build();
    }
}