package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;

import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoResponsableDePago;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Factura;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoFactura;
import Facultad.TrabajoPracticoDesarrollo.Dominio.ResponsablePago;
import Facultad.TrabajoPracticoDesarrollo.enums.EstadoFactura;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoFactura;

public class MapearFactura {

    public static Factura mapearDtoAEntidad (DtoFactura dto, ResponsablePago responsable) {
        if (dto == null) return  null;

        return new Factura.Builder()
                .numeroFactura(dto.getNumeroFactura())
                .fechaEmision(dto.getFechaEmision())
                .importeTotal(dto.getImporteTotal())
                .responsable(responsable)
                .estadia(MapearEstadia.mapearDtoAEntidad(dto.getDtoEstadia()))
                .tipo(dto.getTipoFactura())
                .fechaVencimiento(dto.getFechaVencimiento())
                .estadoFactura(dto.getEstadoFactura())
                .importeNeto(dto.getImporteNeto())
                .iva(dto.getIva())
                .notaDeCredito(MapearNotaDeCredito.mapearDtoAEntidad(dto.getDtoNotaDeCredito()))
                .build();

    }

    public static DtoFactura mapearEntidadADto (Factura entidad, DtoResponsableDePago responsable) {

        if (entidad == null) return null;

        return new DtoFactura.Builder()
                .numeroFactura(entidad.getNumeroFactura())
                .fechaEmision(entidad.getFechaEmision())
                .fechaVencimiento(entidad.getFechaVencimiento())
                .estado(entidad.getEstadoFactura())
                .tipo(entidad.getTipoFactura())
                .importeTotal(entidad.getImporteTotal())
                .importeNeto(entidad.getImporteNeto())
                .iva(entidad.getIva())
                .dtoEstadia(MapearEstadia.mapearEntidadADto(entidad.getEstadia()))
                .dtoResponsable(responsable)
                .dtoNotaDeCredito(MapearNotaDeCredito.mapearEntidadADto(entidad.getNotaDeCredito()))
                .build();

    }

}
