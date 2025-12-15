package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;

import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoEstadiaSimple;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoResponsableDePago;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoResponsableSimple;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Estadia;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Factura;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoFactura;
import Facultad.TrabajoPracticoDesarrollo.Dominio.ResponsablePago;
import Facultad.TrabajoPracticoDesarrollo.enums.EstadoFactura;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoFactura;

public class MapearFactura {

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
