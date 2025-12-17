package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;

import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoEstadiaSimple;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoResponsableSimple;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Estadia;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Factura;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoFactura;
import Facultad.TrabajoPracticoDesarrollo.Dominio.ResponsablePago;


/**
 * Conversor entre la entidad {@link Factura} y el DTO {@link DtoFactura}.
 *
 * <p>Provee métodos estáticos para mapear en ambas direcciones:
 * - {@link #mapearDtoAEntidad(DtoFactura, ResponsablePago, Estadia)}: crea una entidad
 *   {@link Factura} a partir de un DTO, recibiendo además el {@link ResponsablePago}
 *   y la {@link Estadia} que deben asociarse (se asumen resueltas por el servicio).
 * - {@link #mapearEntidadADto(Factura)}: construye un {@link DtoFactura} desde la entidad,
 *   creando DTOs simplificados para responsable y estadía.</p>
 *
 * <p>Comportamiento ante entradas {@code null}:
 * - Si el DTO o la entidad pasada es {@code null}, los métodos retornan {@code null}.</p>
 */
public class MapearFactura {

    /**
     * Mapea un {@link DtoFactura} a la entidad {@link Factura}.
     *
     * <p>Nota: los objetos {@code ResponsablePago} y {@code Estadia} se reciben como parámetros
     * ya resueltos (por ejemplo, recuperados desde la base de datos) y se asignan directamente
     * a la nueva {@link Factura}.</p>
     *
     * @param dto DTO de entrada; puede ser {@code null}
     * @param responsable instancia de {@link ResponsablePago} asociada a la factura (puede ser {@code null})
     * @param estadia instancia de {@link Estadia} asociada a la factura (puede ser {@code null})
     * @return nueva instancia de {@link Factura} construida desde el DTO y las dependencias,
     *         o {@code null} si {@code dto} es {@code null}
     */
    public static Factura mapearDtoAEntidad (DtoFactura dto, ResponsablePago responsable, Estadia estadia) {
        if (dto == null) return  null;

        return new Factura.Builder()
                .numeroFactura(dto.getNumeroFactura())
                .fechaEmision(dto.getFechaEmision())
                .importeTotal(dto.getImporteTotal())
                .responsable(responsable)
                .estadia(estadia)
                .tipo(dto.getTipoFactura())
                .fechaVencimiento(dto.getFechaVencimiento())
                .estadoFactura(dto.getEstadoFactura())
                .importeNeto(dto.getImporteNeto())
                .iva(dto.getIva())
                .notaDeCredito(MapearNotaDeCredito.mapearDtoAEntidad(dto.getDtoNotaDeCredito()))
                .build();

    }
    /**
     * Mapea una entidad {@link Factura} a su {@link DtoFactura}.
     *
     * <p>Construye DTOs simplificados para la referencia a responsable y estadía:
     * - {@link DtoResponsableSimple} con el id del responsable.
     * - {@link DtoEstadiaSimple} con el id de la estadía.</p>
     *
     * <p>También delega el mapeo de la nota de crédito a {@link MapearNotaDeCredito}.</p>
     *
     * @param entidad entidad de entrada; puede ser {@code null}
     * @return instancia de {@link DtoFactura} construida desde la entidad, o {@code null} si {@code entidad} es {@code null}
     */
    public static DtoFactura mapearEntidadADto (Factura entidad) {

        if (entidad == null) return null;

        DtoResponsableSimple respSimple = new DtoResponsableSimple();
        respSimple.setIdResponsable(entidad.getResponsablePago().getIdResponsable());

        DtoEstadiaSimple estSimple = new DtoEstadiaSimple();
        estSimple.setIdEstadia(entidad.getEstadia().getIdEstadia());

        return new DtoFactura.Builder()
                .numeroFactura(entidad.getNumeroFactura())
                .fechaEmision(entidad.getFechaEmision())
                .fechaVencimiento(entidad.getFechaVencimiento())
                .estado(entidad.getEstadoFactura())
                .tipo(entidad.getTipoFactura())
                .importeTotal(entidad.getImporteTotal())
                .importeNeto(entidad.getImporteNeto())
                .iva(entidad.getIva())
                .idEstadia(estSimple)
                .idResponsable(respSimple)
                .dtoNotaDeCredito(MapearNotaDeCredito.mapearEntidadADto(entidad.getNotaDeCredito()))
                .build();

    }

}
