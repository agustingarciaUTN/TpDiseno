package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Estadia;
import Facultad.TrabajoPracticoDesarrollo.Dominio.ServiciosAdicionales;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoServiciosAdicionales;

/**
 * Conversor entre la entidad {@link ServiciosAdicionales} y el DTO {@link DtoServiciosAdicionales}.
 *
 * <p>Provee métodos estáticos para mapear en ambas direcciones:
 * - {@link #mapearDtoAEntidad(DtoServiciosAdicionales)}: crea una entidad {@link ServiciosAdicionales}
 *   a partir del DTO recibido, creando una referencia a {@link Estadia} solo con el identificador.
 * - {@link #mapearEntidadADto(ServiciosAdicionales)}: crea un {@link DtoServiciosAdicionales} a partir
 *   de la entidad recibida.</p>
 *
 * <p>Comportamiento ante entradas {@code null}:
 * - Si el DTO pasado a {@code mapearDtoAEntidad} es {@code null}, se retorna {@code null}.
 * - Si la entidad pasada a {@code mapearEntidadADto} es {@code null}, se retorna {@code null}.</p>
 *
 * <p>Notas:
 * - Al mapear desde DTO a entidad se crea una {@code Estadia} de referencia (solo con {@code idEstadia}) para asociarla
 *   al {@link ServiciosAdicionales}. Se asume que la resolución completa de la estadía (carga desde BD u otro servicio)
 *   ocurre en la capa que invoque a este mapeador si es necesario.</p>
 */
public class MapearServiciosAdicionales  {

    /**
     * Mapea un {@link DtoServiciosAdicionales} a la entidad {@link ServiciosAdicionales}.
     *
     * @param dtoServiciosAdicionales DTO de entrada; puede ser {@code null}
     * @return instancia de {@link ServiciosAdicionales} construida desde el DTO, o {@code null} si el DTO es {@code null}
     *
     * <p>Descripción:
     * - Copia campos básicos (id, descripción, tipo, valor y fecha).
     * - Construye una {@link Estadia} de referencia usando únicamente {@code idEstadia} del DTO
     *   para asociarla a la entidad (no carga la estadía completa).</p>
     */
    public static ServiciosAdicionales mapearDtoAEntidad(DtoServiciosAdicionales dtoServiciosAdicionales) {
        if (dtoServiciosAdicionales == null) return null;

        // Referencia a Estadia (Solo ID)
        Estadia estadiaRef = new Estadia.Builder().idEstadia(dtoServiciosAdicionales.getIdEstadia()).build();

        return new ServiciosAdicionales.Builder()
                .id(dtoServiciosAdicionales.getIdServicio())
                .descripcion(dtoServiciosAdicionales.getDescripcionServicio())
                .tipo(dtoServiciosAdicionales.getTipoServicio())
                .valor(dtoServiciosAdicionales.getValorServicio())
                .fecha(dtoServiciosAdicionales.getFechaConsumo())
                .estadia(estadiaRef)
                .build();
    }

    /**
     * Mapea una entidad {@link ServiciosAdicionales} a su {@link DtoServiciosAdicionales}.
     *
     * @param serviciosAdicionales entidad de entrada; puede ser {@code null}
     * @return instancia de {@link DtoServiciosAdicionales} construida desde la entidad, o {@code null} si la entidad es {@code null}
     *
     * <p>Descripción:
     * - Extrae campos básicos (id, tipo, descripción, valor y fecha).
     * - Extrae el identificador de la estadía asociada mediante {@code serviciosAdicionales.getEstadia().getIdEstadia()}.</p>
     *
     * <p>Precaución:
     * - Si la propiedad {@code getEstadia()} de la entidad es {@code null}, la llamada a {@code getIdEstadia()}
     *   provocará un {@link NullPointerException}. Se recomienda validar la presencia de {@code getEstadia()}
     *   antes de invocar este mapeador o ampliar el método para manejar ese caso (por ejemplo, usando un chequeo
     *   nulo y dejando {@code idEstadia} como {@code null}).</p>
     */
    public static DtoServiciosAdicionales mapearEntidadADto(ServiciosAdicionales serviciosAdicionales) {
        if (serviciosAdicionales == null) return null;

        return new DtoServiciosAdicionales.Builder()
                .id(serviciosAdicionales.getId())
                .tipo(serviciosAdicionales.getTipoServicio())
                .descripcion(serviciosAdicionales.getDescripcion())
                .valor(serviciosAdicionales.getValor())
                .fecha(serviciosAdicionales.getFechaConsumo())
                .idEstadia(serviciosAdicionales.getEstadia().getIdEstadia())
                .build();
    }
}