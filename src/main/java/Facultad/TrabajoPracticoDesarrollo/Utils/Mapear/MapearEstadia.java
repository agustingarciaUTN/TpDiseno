package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Estadia;
import Facultad.TrabajoPracticoDesarrollo.Dominio.EstadiaHuesped;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Huesped;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoEstadia;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoHuesped;

import java.util.ArrayList;

/**
 * Utilidad para convertir entre la entidad {@link Estadia} y el DTO {@link DtoEstadia}.
 *
 * <p>Provee métodos estáticos para mapear en ambas direcciones. Los mapeos
 * incluyen objetos relacionados (reserva, habitación) y la conversión de la
 * colección de huéspedes a partir de {@link EstadiaHuesped}.</p>
 *
 * <p>Comportamiento ante {@code null}:
 * - Si el DTO o la entidad de entrada es {@code null}, los métodos retornan {@code null}.</p>
 */
public class MapearEstadia {

    /**
     * Mapea un {@link DtoEstadia} a la entidad {@link Estadia}.
     *
     * <p>Se copian fechas, id y valor. Además:
     * - Si el DTO contiene una reserva, se delega a {@link MapearReserva}.
     * - Si el DTO contiene una habitación, se delega a {@link MapearHabitacion}.
     * - Los huéspedes no se agregan aquí: se manejan en el Service usando {@link EstadiaHuesped}.</p>
     *
     * @param dto DTO de entrada; puede ser {@code null}
     * @return instancia de {@link Estadia} construida desde el DTO, o {@code null} si {@code dto} es {@code null}
     */
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

    /**
     * Mapea una entidad {@link Estadia} a su {@link DtoEstadia}.
     *
     * <p>Se copian id, fechas y valor. También se mapean:
     * - Reserva mediante {@link MapearReserva}.
     * - Habitación mediante {@link MapearHabitacion}.
     * - Lista de huéspedes: se recorre {@link Estadia#getEstadiaHuespedes()} y por cada
     *   {@link EstadiaHuesped} se extrae el {@link Huesped} (si existe) y se mapea con {@link MapearHuesped}.</p>
     *
     * @param entidad entidad de entrada; puede ser {@code null}
     * @return instancia de {@link DtoEstadia} construida desde la entidad, o {@code null} si {@code entidad} es {@code null}
     */
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