package Utils.Mapear;

import Dominio.Estadia;
import Dominio.Huesped;
import Estadia.DtoEstadia;
import Huesped.DtoHuesped;
import java.util.ArrayList;

public class MapearEstadia {

    public static Estadia mapearDtoAEntidad(DtoEstadia dto) {
        if (dto == null) return null;

        Estadia.Builder builder = new Estadia.Builder(dto.getFechaCheckIn())
                .idEstadia(dto.getIdEstadia())
                .fechaCheckOut(dto.getFechaCheckOut())
                .valorEstadia(dto.getValorEstadia());

        // 1. Mapear Reserva (si existe)
        if (dto.getDtoReserva() != null) {
            builder.reserva(MapearReserva.mapearDtoAEntidad(dto.getDtoReserva()));
        }

        // 2. Mapear Habitación (CORRECCIÓN IMPORTANTE: ESTO FALTABA)
        if (dto.getDtoHabitacion() != null) {
            builder.habitacion(MapearHabitacion.mapearDtoAEntidad(dto.getDtoHabitacion()));
        }

        // 3. Mapear Lista de Huéspedes
        if (dto.getDtoHuespedes() != null) {
            for (DtoHuesped dtoH : dto.getDtoHuespedes()) {
                builder.agregarHuesped(MapearHuesped.mapearDtoAEntidad(dtoH));
            }
        }

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

        // 3. Mapear Lista de Huéspedes
        if (entidad.getHuespedes() != null) {
            ArrayList<DtoHuesped> listaDtos = new ArrayList<>();
            for (Huesped h : entidad.getHuespedes()) {
                listaDtos.add(MapearHuesped.mapearEntidadADto(h));
            }
            builder.dtoHuespedes(listaDtos);
        }

        return builder.build();
    }
}