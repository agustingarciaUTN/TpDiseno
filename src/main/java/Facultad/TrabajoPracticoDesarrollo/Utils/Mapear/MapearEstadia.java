package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Estadia;
import Facultad.TrabajoPracticoDesarrollo.Dominio.EstadiaHuesped;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Huesped;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoEstadia;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoHuesped;

import java.util.ArrayList;

public class MapearEstadia {

    public static Estadia mapearDtoAEntidad(DtoEstadia dto) {
        if (dto == null) return null;

        Estadia.Builder builder = new Estadia.Builder()
                .fechaCheckIn(dto.getFechaCheckIn())
                .idEstadia(dto.getIdEstadia())
                .fechaCheckOut(dto.getFechaCheckOut())
                .valorEstadia(dto.getValorEstadia());

        // 1. Mapear Reserva (si existe)
        if (dto.getDtoReserva() != null) {
            builder.reserva(MapearReserva.mapearDtoAEntidad(dto.getDtoReserva()));
        }

        // 2. Mapear Habitación
        if (dto.getDtoHabitacion() != null) {
            builder.habitacion(MapearHabitacion.mapearDtoAEntidad(dto.getDtoHabitacion(), null));
        }

        // 3. Los huéspedes se manejan directamente en el Service con EstadiaHuesped
        // No se usa el builder.agregarEstadiaHuesped aquí

        return builder.build();
    }

    public static DtoEstadia mapearEntidadADto(Estadia entidad) {
        if (entidad == null) return null;

        DtoEstadia.Builder builder = new DtoEstadia.Builder()
                .idEstadia(entidad.getIdEstadia())
                .fechaCheckIn(entidad.getFechaCheckIn())
                .fechaCheckOut(entidad.getFechaCheckOut())
                .valorEstadia(entidad.getValorEstadia());

        // 1. Mapear Reserva
        if (entidad.getReserva() != null) {
            builder.dtoReserva(MapearReserva.mapearEntidadADto(entidad.getReserva()));
        }

        // 2. Mapear Habitación (Agregamos esto también por completitud)
        if (entidad.getHabitacion() != null) {
            builder.dtoHabitacion(MapearHabitacion.mapearEntidadADto(entidad.getHabitacion()));
        }

        // 3. Mapear Lista de Huéspedes desde EstadiaHuesped
        if (entidad.getEstadiaHuespedes() != null) {
            ArrayList<DtoHuesped> listaDtos = new ArrayList<>();
            for (EstadiaHuesped eh : entidad.getEstadiaHuespedes()) {
                if (eh.getHuesped() != null) {
                    listaDtos.add(MapearHuesped.mapearEntidadADto(eh.getHuesped()));
                }
            }
            builder.dtoHuespedes(listaDtos);
        }

        return builder.build();
    }
}