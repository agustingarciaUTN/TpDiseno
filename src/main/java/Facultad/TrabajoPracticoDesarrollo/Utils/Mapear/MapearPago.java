package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;

import Facultad.TrabajoPracticoDesarrollo.Dominio.*;
import Facultad.TrabajoPracticoDesarrollo.DTOs.*;


public class MapearPago {

    public static Pago mapearDtoAEntidad(DtoPago dtoPago, Factura factura) {
        if (dtoPago == null) return null;

        // Mapeamos el Pago con la factura ya cargada
        return new Pago.Builder()
                .id(dtoPago.getIdPago())
                .cotizacion(dtoPago.getCotizacion())
                .moneda(dtoPago.getMoneda())
                .monto(dtoPago.getMontoTotal())
                .fecha(dtoPago.getFechaPago())
                .factura(factura)
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
                .numeroFactura(pago.getFactura() != null ? pago.getFactura().getNumeroFactura() : null);

        // Agregar los medios de pago mapeados (si existen)
        if (pago.getMediosPago() != null) {
            for (MedioPago mp : pago.getMediosPago()) {
                // Mapear cada medio de pago según su tipo
                if (mp.getEfectivo() != null) {
                    builder.agregarMedioPago(MapearEfectivo.mapearEntidadADto(mp.getEfectivo()));
                } else if (mp.getCheque() != null) {
                    builder.agregarMedioPago(MapearCheque.mapearEntidadADto(mp.getCheque()));
                } else if (mp.getTarjeta() instanceof TarjetaCredito) {
                    builder.agregarMedioPago(MapearTarjetaCredito.mapearEntidadADto((TarjetaCredito) mp.getTarjeta()));
                } else if (mp.getTarjeta() instanceof TarjetaDebito) {
                    builder.agregarMedioPago(MapearTarjetaDebito.mapearEntidadADto((TarjetaDebito) mp.getTarjeta()));
                }
            }
        }

        return builder.build();
    }
}