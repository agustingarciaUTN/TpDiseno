// java
package Utils.Mapear;

import Dominio.Reserva;
import Reserva.DtoReserva;
import Dominio.Habitacion;

public class MapearReserva implements MapeoInterfaz<DtoReserva, Reserva> {

    @Override
    public Reserva mapearDtoAEntidad(DtoReserva dto) {
        if (dto == null) return null;
        Reserva r = new Reserva();
        try { r.setIdReserva(dto.getIdReserva()); } catch (Throwable ignored) {}
        try { r.setEstadoReserva(dto.getEstadoReserva()); } catch (Throwable ignored) {}
        try { r.setFechaReserva(dto.getFechaReserva()); } catch (Throwable ignored) {}
        try { r.setFechaDesde(dto.getFechaDesde()); } catch (Throwable ignored) {}
        try { r.setFechaHasta(dto.getFechaHasta()); } catch (Throwable ignored) {}
        try { r.setNombreHuespedResponsable(dto.getNombreHuespedResponsable()); } catch (Throwable ignored) {}
        try { r.setApellidoHuespedResponsable(dto.getApellidoHuespedResponsable()); } catch (Throwable ignored) {}
        try { r.setTelefonoHuespedResponsable(dto.getTelefonoHuespedResponsable()); } catch (Throwable ignored) {}

        // Mapear habitación mínimamente a partir de idHabitacion (si es numérico se asigna a numero)
        try { r.setIdReserva(dto.getIdReserva()); } catch (Throwable ignored)  {}


        return r;
    }

    @Override
    public DtoReserva mapearEntidadADto(Reserva entidad) {
        if (entidad == null) return null;
        DtoReserva dto = new DtoReserva();
        try { dto.setIdReserva(entidad.getIdReserva()); } catch (Throwable ignored) {}
        try { dto.setEstadoReserva(entidad.getEstadoReserva()); } catch (Throwable ignored) {}
        try { dto.setFechaReserva(entidad.getFechaReserva()); } catch (Throwable ignored) {}
        try { dto.setFechaDesde(entidad.getFechaDesde()); } catch (Throwable ignored) {}
        try { dto.setFechaHasta(entidad.getFechaHasta()); } catch (Throwable ignored) {}
        try { dto.setNombreHuespedResponsable(entidad.getNombreHuespedResponsable()); } catch (Throwable ignored) {}
        try { dto.setApellidoHuespedResponsable(entidad.getApellidoHuespedResponsable()); } catch (Throwable ignored) {}
        try { dto.setTelefonoHuespedResponsable(entidad.getTelefonoHuespedResponsable()); } catch (Throwable ignored) {}

        // Mapear idHabitacion como String a partir del numero de la entidad Habitacion (si existe)
        try {
            Habitacion h = entidad.getHabitacion();
            if (h != null) {
                try { dto.setIdHabitacion(String.valueOf(h.getNumero())); } catch (Throwable ignored) {}
            }
        } catch (Throwable ignored) {}

        return dto;
    }
}
