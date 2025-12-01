// java
package Utils.Mapear;

import Dominio.*;
import Estadia.DtoEstadia;
import Reserva.DtoReserva;
import Habitacion.DtoHabitacion;
import Huesped.DtoHuesped;

import java.util.ArrayList;

/**
 * MapearEstadia puede recibir opcionalmente un MapearHuesped para evitar duplicar lógica.
 * Aquí se admite pasar null y mapear huespedes mínimamente.
 */
public class MapearEstadia implements MapeoInterfaz<DtoEstadia, Estadia> {

    private final MapearHuesped huespedMapper;

    public MapearEstadia(MapearHuesped huespedMapper) {
        this.huespedMapper = huespedMapper;
    }

    @Override
    public Estadia mapearDtoAEntidad(DtoEstadia dto) {
        if (dto == null) return null;
        if (dto.getFechaCheckIn() == null) throw new IllegalArgumentException("fechaCheckIn no puede ser null");

        Estadia.Builder builder = new Estadia.Builder(dto.getFechaCheckIn());
        try { builder.idEstadia(dto.getIdEstadia()); } catch (Throwable ignored) {}
        try { builder.fechaCheckOut(dto.getFechaCheckOut()); } catch (Throwable ignored) {}
        try { builder.valorEstadia(dto.getValorEstadia()); } catch (Throwable ignored) {}

        try {
            if (dto.getDtoReserva() != null) {
                DtoReserva dr = dto.getDtoReserva();
                Reserva r = new Reserva();
                try { r.setIdReserva(dr.getIdReserva()); } catch (Throwable ignored) {}
                builder.reserva(r);
            }
        } catch (Throwable ignored) {}

        try {
            if (dto.getDtoHabitacion() != null) {
                DtoHabitacion dh = dto.getDtoHabitacion();
                Habitacion h = new Habitacion();
                try { h.setNumero(dh.getNumero()); } catch (Throwable ignored) {}
                try { h.setNumero(dh.getNumero()); } catch (Throwable ignored) {}
                builder.habitacion(h);
            }
        } catch (Throwable ignored) {}

        try {
            if (dto.getDtoHuespedes() != null && !dto.getDtoHuespedes().isEmpty()) {
                for (DtoHuesped dh : dto.getDtoHuespedes()) {
                    Huesped hu;
                    if (huespedMapper != null) {
                        hu = huespedMapper.mapearDtoAEntidad(dh); // mapear completo o mínimo según implementación
                    } else {
                        hu = new Huesped();
                        try { hu.setNombres(dh.getNombres()); } catch (Throwable ignored) {}
                        try { hu.setNroDocumento(dh.getNroDocumento()); } catch (Throwable ignored) {}
                    }
                    if (hu != null) builder.agregarHuesped(hu);
                }
            }
        } catch (Throwable ignored) {}

        return builder.build();
    }

    @Override
    public DtoEstadia mapearEntidadADto(Estadia entidad) {
        if (entidad == null) return null;
        DtoEstadia dto = new DtoEstadia();
        try { dto.setIdEstadia(entidad.getIdEstadia()); } catch (Throwable ignored) {}
        try { dto.setFechaCheckIn(entidad.getFechaCheckIn()); } catch (Throwable ignored) {}
        try { dto.setFechaCheckOut(entidad.getFechaCheckOut()); } catch (Throwable ignored) {}
        try { dto.setValorEstadia(entidad.getValorEstadia()); } catch (Throwable ignored) {}

        try {
            if (entidad.getReserva() != null) {
                DtoReserva dr = new DtoReserva();
                try { dr.setIdReserva(entidad.getReserva().getIdReserva()); } catch (Throwable ignored) {}
                dto.setDtoReserva(dr);
            }
        } catch (Throwable ignored) {}

        try {
            if (entidad.getHabitacion() != null) {
                DtoHabitacion dh = new DtoHabitacion();
                try { dh.setNumero(entidad.getHabitacion().getNumero()); } catch (Throwable ignored) {}
                try { dh.setNumero(entidad.getHabitacion().getNumero()); } catch (Throwable ignored) {}
                dto.setDtoHabitacion(dh);
            }
        } catch (Throwable ignored) {}

        try {
            if (entidad.getHuespedes() != null && !entidad.getHuespedes().isEmpty()) {
                ArrayList<DtoHuesped> lista = new ArrayList<>();
                for (Huesped h : entidad.getHuespedes()) {
                    DtoHuesped dh;
                    if (huespedMapper != null) {
                        dh = huespedMapper.mapearEntidadADto(h);
                    } else {
                        dh = new DtoHuesped();
                        try { dh.setNombres(h.getNombres()); } catch (Throwable ignored) {}
                        try { dh.setNroDocumento(h.getNroDocumento()); } catch (Throwable ignored) {}
                    }
                    lista.add(dh);
                }
                dto.setDtoHuespedes(lista);
            }
        } catch (Throwable ignored) {}

        return dto;
    }
}
