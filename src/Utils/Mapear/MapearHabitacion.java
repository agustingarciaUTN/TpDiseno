// java
package Utils.Mapear;

import Dominio.Habitacion;
import Habitacion.DtoHabitacion;

public class MapearHabitacion implements MapeoInterfaz<DtoHabitacion, Habitacion> {

    @Override
    public Habitacion mapearDtoAEntidad(DtoHabitacion dto) {
        if (dto == null) return null;
        Habitacion h = new Habitacion();
        try { h.setNumero(dto.getNumero()); } catch (Throwable ignored) {}
        try { h.setCapacidad(dto.getCapacidad()); } catch (Throwable ignored) {}
        try { h.setTipoHabitacion(dto.getTipoHabitacion()); } catch (Throwable ignored) {}
        try { h.setEstadoHabitacion(dto.getEstadoHabitacion()); } catch (Throwable ignored) {}
        try { h.setCostoPorNoche(dto.getCostoPorNoche()); } catch (Throwable ignored) {}
        // mapear camas, servicios u otros campos si existen en el DTO
        return h;
    }

    @Override
    public DtoHabitacion mapearEntidadADto(Habitacion entidad) {
        if (entidad == null) return null;
        DtoHabitacion dto = new DtoHabitacion();
        try { dto.setNumero(entidad.getNumero()); } catch (Throwable ignored) {}
        try { dto.setCapacidad(entidad.getCapacidad()); } catch (Throwable ignored) {}
        try { dto.setTipoHabitacion(entidad.getTipoHabitacion()); } catch (Throwable ignored) {}
        try { dto.setEstadoHabitacion(entidad.getEstadoHabitacion()); } catch (Throwable ignored) {}
        try { dto.setCostoPorNoche(entidad.getCostoPorNoche()); } catch (Throwable ignored) {}


        // mapear camas, servicios u otros campos si existen en la entidad
        return dto;
    }
}
