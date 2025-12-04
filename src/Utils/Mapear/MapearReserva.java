package Utils.Mapear;

import Dominio.Reserva;
import Dominio.Habitacion;
import Reserva.DtoReserva;

public class MapearReserva {


    public static Reserva mapearDtoAEntidad(DtoReserva dto) {
        if (dto == null) return null;

        // Creamos una Habitación "referencia" solo con el ID para la Entidad
        Habitacion habRef = null;
        if (dto.getIdHabitacion() != null) {
            habRef = new Habitacion.Builder(dto.getIdHabitacion(), null, 1).build();
        }

        return new Reserva.Builder(dto.getFechaDesde(), dto.getFechaHasta(), habRef)
                .id(dto.getIdReserva())
                .estado(dto.getEstadoReserva())
                .fechaReserva(dto.getFechaReserva())
                .nombreResponsable(dto.getNombreHuespedResponsable())
                .apellidoResponsable(dto.getApellidoHuespedResponsable())
                .telefonoResponsable(dto.getTelefonoHuespedResponsable())
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
}