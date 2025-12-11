package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Estadia;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Factura;
import Facultad.TrabajoPracticoDesarrollo.Dominio.MedioPago;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Pago;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoPago;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoFactura;


public class MapearPago {

    public static Pago mapearDtoAEntidad(DtoPago dtoPago) {
        if (dtoPago == null) return null;

        // Referencia Factura
        Estadia estadia = new Estadia();
        Factura facturaRef = new Factura.Builder(null, null, null, null, null, TipoFactura.A ).build();
        facturaRef.setNumeroFactura(dtoPago.getFactura().getNumeroFactura());

        // Nota: No podemos reconstruir los objetos MedioPago solo desde una lista de IDs genéricos.
        // La lista de medios quedará vacía en la entidad y deberá cargarse por separado si hace falta.

        return new Pago.Builder(
                dtoPago.getMoneda(),
                dtoPago.getMontoTotal(),
                dtoPago.getFechaPago(),
                facturaRef
        )
                .id(dtoPago.getIdPago())
                .cotizacion(dtoPago.getCotizacion())
                .build();

    }

    public static DtoPago mapearEntidadADto(Pago pago) {
        if (pago == null) return null;

        DtoPago.Builder builder = new DtoPago.Builder()
                .idPago(pago.getIdPago())
                .moneda(pago.getMoneda())
                .montoTotal(pago.getMontoTotal())
                .cotizacion(pago.getCotizacion())
                .fechaPago(pago.getFechaPago())
                .Factura(pago.getFactura());

        if (pago.getMediosPago() != null) {
            for (MedioPago mp : pago.getMediosPago()) {
                builder.agregarIdMedioPago(mp.getPago().getIdPago()); // ID del padre MedioPago
            }
        }

        return builder.build();
    }
}