package Utils.Mapear;

import Dominio.Estadia;
import Dominio.Huesped;
import Estadia.DtoEstadia;
import Huesped.DtoHuesped;
import java.util.ArrayList;

public class MapearEstadia implements MapeoInterfaz<DtoEstadia, Estadia> {

    // Composición de mappers
    private final MapearReserva mapearReserva = new MapearReserva();
    private final MapearHuesped mapearHuesped = new MapearHuesped();

    @Override
    public Estadia mapearDtoAEntidad(DtoEstadia dto) {
        if (dto == null) return null;

        Estadia.Builder builder = new Estadia.Builder(dto.getFechaCheckIn())
                .idEstadia(dto.getIdEstadia())
                .fechaCheckOut(dto.getFechaCheckOut())
                .valorEstadia(dto.getValorEstadia());

        // 1. Mapear Reserva
        if (dto.getDtoReserva() != null) {
            builder.reserva(mapearReserva.mapearDtoAEntidad(dto.getDtoReserva()));
        }

        // 2. Mapear Lista de Huéspedes
        if (dto.getDtoHuespedes() != null) {
            for (DtoHuesped dtoH : dto.getDtoHuespedes()) {
                builder.agregarHuesped(mapearHuesped.mapearDtoAEntidad(dtoH));
            }
        }

        return builder.build();
    }

    @Override
    public DtoEstadia mapearEntidadADto(Estadia entidad) {
        if (entidad == null) return null;

        DtoEstadia.Builder builder = new DtoEstadia.Builder()
                .idEstadia(entidad.getIdEstadia())
                .fechaCheckIn(entidad.getFechaCheckIn())
                .fechaCheckOut(entidad.getFechaCheckOut())
                .valorEstadia(entidad.getValorEstadia());

        // 1. Mapear Reserva
        if (entidad.getReserva() != null) {
            builder.dtoReserva(mapearReserva.mapearEntidadADto(entidad.getReserva()));
        }

        // 2. Mapear Lista de Huéspedes
        if (entidad.getHuespedes() != null) {
            ArrayList<DtoHuesped> listaDtos = new ArrayList<>();
            for (Huesped h : entidad.getHuespedes()) {
                listaDtos.add(mapearHuesped.mapearEntidadADto(h));
            }
            builder.dtoHuespedes(listaDtos);
        }

        return builder.build();
    }
}