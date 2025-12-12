package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Habitacion;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Reserva;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoReserva;

import java.util.ArrayList;
import java.util.List;

public class MapearReserva {


    public static Reserva mapearDtoAEntidad(DtoReserva dto) {
        if (dto == null) return null;

        // Creamos una Habitación "referencia" solo con el ID para la Entidad
        Habitacion habRef = null;
        if (dto.getIdHabitacion() != null) {
            habRef = new Habitacion.Builder()
                    .numero(dto.getIdHabitacion())
                    .build();
        }

        return new Reserva.Builder()
                .id(dto.getIdReserva())
                .estado(dto.getEstadoReserva())
                .fechaReserva(dto.getFechaReserva())
                .fechaDesde(dto.getFechaDesde())
                .fechaHasta(dto.getFechaHasta())
                .nombreResponsable(dto.getNombreHuespedResponsable())
                .apellidoResponsable(dto.getApellidoHuespedResponsable())
                .telefonoResponsable(dto.getTelefonoHuespedResponsable())
                .habitacion(habRef)
                .build();
    }


    public static DtoReserva mapearEntidadADto(Reserva entidad) {
        if (entidad == null) return null;

        // Extraemos el ID de la habitación si existe el objeto
        String idHab = entidad.getHabitacion().getNumero();

        return new DtoReserva.Builder()
                .id(entidad.getIdReserva())
                .estado(entidad.getEstadoReserva())
                .fechaReserva(entidad.getFechaReserva())
                .fechaDesde(entidad.getFechaDesde())
                .fechaHasta(entidad.getFechaHasta())
                .nombreResponsable(entidad.getNombreHuespedResponsable())
                .apellidoResponsable(entidad.getApellidoHuespedResponsable())
                .telefonoResponsable(entidad.getTelefonoHuespedResponsable())
                .idHabitacion(idHab)
                .build();
    }

    public static ArrayList<DtoReserva> mapearListaReservas (List<Reserva> lista) {

        ArrayList<DtoReserva> listaDto = new ArrayList<>();

        for (Reserva r : lista) {
            listaDto.add(MapearReserva.mapearEntidadADto(r));
        }

        return listaDto;

    }
}