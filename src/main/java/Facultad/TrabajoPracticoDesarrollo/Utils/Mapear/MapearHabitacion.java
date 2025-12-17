package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Habitacion;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoHabitacion;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Reserva;

import java.util.ArrayList;

/**
 * Conversor entre {@link DtoHabitacion} y {@link Habitacion}.
 *
 * <p>Provee métodos estáticos para mapear desde un DTO hacia la entidad y
 * desde la entidad hacia su DTO correspondiente.</p>
 *
 * <p>Comportamiento ante entradas {@code null}:
 * - Si el DTO o la entidad de entrada es {@code null}, los métodos retornan {@code null}.</p>
 *
 * <p>Notas:
 * - El mapeo de la lista de reservas se realiza pasando la lista recibida al builder de {@link Habitacion}
 *   (se asume que las reservas ya están resueltas por el servicio que llama).
 * - Para convertir la lista de reservas a DTOs se delega en {@code MapearReserva.mapearListaReservas}.</p>
 */
public class MapearHabitacion  {
    /**
     * Instancia auxiliar para mapear camas si fuera necesario.
     *
     * <p>Se mantiene como campo de instancia por compatibilidad con implementaciones
     * futuras que requieran mapeos no estáticos. Actualmente no se usa directamente en los métodos estáticos.</p>
     */
    private final MapearCama mapearCama = new MapearCama();

    /**
     * Mapea un {@link DtoHabitacion} a la entidad {@link Habitacion}.
     *
     * @param dto DTO de entrada; puede ser {@code null}
     * @param reservas lista de {@link Reserva} asociadas a la habitación; puede ser {@code null}
     * @return instancia de {@link Habitacion} construida desde el DTO y las reservas,
     *         o {@code null} si {@code dto} es {@code null}
     *
     * <p>Descripción:
     * - Copia los campos básicos (número, tipo, capacidad, estado, costo).
     * - Asigna la lista de reservas recibida directamente al builder.
     * - No valida ni transforma las reservas; se espera que estén en el formato correcto.</p>
     */
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

    /**
     * Mapea una entidad {@link Habitacion} a su {@link DtoHabitacion}.
     *
     * @param entidad entidad de entrada; puede ser {@code null}
     * @return instancia de {@link DtoHabitacion} construida desde la entidad,
     *         o {@code null} si {@code entidad} es {@code null}
     *
     * <p>Descripción:
     * - Copia los campos básicos de la entidad al DTO.
     * - Convierte la lista de reservas de la entidad a DTOs delegando en {@code MapearReserva.mapearListaReservas}.</p>
     */
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