package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Habitacion;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Reserva;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoReserva;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilidad para convertir entre el DTO {@link DtoReserva} y la entidad {@link Reserva}.
 *
 * <p>Provee métodos estáticos para:
 * - Construir una entidad {@link Reserva} a partir de un {@link DtoReserva}.
 * - Construir un {@link DtoReserva} a partir de una entidad {@link Reserva}.
 * - Mapear listas de reservas a listas de DTOs.</p>
 *
 * <p>Comportamiento general:
 * - Si el parámetro de entrada es {@code null}, los métodos retornan {@code null} o una lista vacía según corresponda.
 * - El mapeo de la habitación desde el DTO crea una instancia "referencia" {@link Habitacion} solo con el identificador
 *   (número) para ser asociada a la entidad. Esto es útil cuando la habitación se resuelve en el servicio o repositorio.</p>
 */
public class MapearReserva {

    /**
     * Crea una entidad {@link Reserva} a partir de un {@link DtoReserva}.
     *
     * @param dto DTO de entrada; si es {@code null} se retorna {@code null}
     * @return nueva instancia de {@link Reserva} construida desde el DTO, o {@code null} si {@code dto} es {@code null}
     *
     * <p>Detalles:
     * - Si {@link DtoReserva#getIdHabitacion()} no es {@code null}, se crea una {@link Habitacion} de referencia
     *   (solo con el número/id) y se la asocia a la reserva. Se asume que la resolución completa de la habitación
     *   (carga desde BD u otro servicio) se realiza fuera de este mapeador.</p>
     */
    public static Reserva mapearDtoAEntidad(DtoReserva dto) {
        if (dto == null) return null;

        // Creamos una Habitación "referencia" solo con el ID para la Entidad
        Habitacion habRef = null;
        if (dto.getIdHabitacion() != null) {
            habRef = new Habitacion.Builder()
                .numero(dto.getIdHabitacion())
                .build();
        }

        Reserva reserva = new Reserva.Builder()
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
        // Mapear tipo y nro documento
        reserva.setTipoDocumentoResponsable(dto.getTipoDocumentoResponsable());
        reserva.setNroDocumentoResponsable(dto.getNroDocumentoResponsable());
        return reserva;
    }


    /**
     * Convierte una entidad {@link Reserva} a su DTO {@link DtoReserva}.
     *
     * @param entidad entidad de entrada; si es {@code null} se retorna {@code null}
     * @return {@link DtoReserva} construido desde la entidad, o {@code null} si {@code entidad} es {@code null}
     *
     * <p>Detalles:
     * - Extrae el identificador de la habitación llamando a {@code entidad.getHabitacion().getNumero()}.
     *   Si la habitación asociada es {@code null}, la implementación actual provocará un {@link NullPointerException};
     *   se recomienda validar la presencia de {@code getHabitacion()} antes de llamar a este método o ampliar
     *   el mapeador para manejar ese caso.</p>
     */
    public static DtoReserva mapearEntidadADto(Reserva entidad) {
        if (entidad == null) return null;

        // Extraemos el ID de la habitación si existe el objeto
        String idHab = entidad.getHabitacion() != null ? entidad.getHabitacion().getNumero() : null;

        DtoReserva dto = new DtoReserva.Builder()
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
        // Mapear tipo y nro documento
        dto.setTipoDocumentoResponsable(entidad.getTipoDocumentoResponsable());
        dto.setNroDocumentoResponsable(entidad.getNroDocumentoResponsable());
        return dto;
    }

    /**
     * Mapea una lista de {@link Reserva} a una lista de {@link DtoReserva}.
     *
     * @param lista lista de reservas; si es {@code null} se retorna una lista vacía
     * @return {@link ArrayList} de {@link DtoReserva} con cada reserva mapeada mediante {@link #mapearEntidadADto(Reserva)}
     *
     * <p>Notas:
     * - Mantiene el orden de la lista original.
     * - Si algún elemento de la lista es {@code null}, el método {@link #mapearEntidadADto(Reserva)} devolverá {@code null}
     *   para ese elemento y será añadido tal cual a la lista resultante; se puede filtrar según sea necesario.</p>
     */
    public static ArrayList<DtoReserva> mapearListaReservas (List<Reserva> lista) {

        ArrayList<DtoReserva> listaDto = new ArrayList<>();

        for (Reserva r : lista) {
            listaDto.add(MapearReserva.mapearEntidadADto(r));
        }

        return listaDto;

    }
}