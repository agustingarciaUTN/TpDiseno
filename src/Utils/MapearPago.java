package Utils;

import Dominio.Pago;
import Dominio.Factura;
import Dominio.MedioPago;
import Pago.DtoPago;


public class MapearPago implements MapeoInterfaz<DtoPago, Pago> {

    @Override
    public Pago mapearDtoAEntidad(DtoPago dtoPago) {
        if (dtoPago == null) return null;

        // Referencia Factura
        Factura facturaRef = new Factura.Builder(null, null, 0, null, null).build();
        facturaRef.setIdFactura(dtoPago.getIdFactura());

        // Nota: No podemos reconstruir los objetos MedioPago solo desde una lista de IDs genéricos.
        // La lista de medios quedará vacía en la entidad y deberá cargarse por separado si hace falta.

        return new Pago.Builder(
                dtoPago.getMoneda(),
                dtoPago.getMontoTotal(),
                dtoPago.getFechaPago(),
                facturaRef
        )
                .idPago(dtoPago.getIdPago())
                .cotizacion(dtoPago.getCotizacion())
                .build();
    }

    @Override
    public DtoPago mapearEntidadADto(Pago pago) {
        if (pago == null) return null;

        DtoPago.Builder builder = new DtoPago.Builder()
                .idPago(pago.getIdPago())
                .moneda(pago.getMoneda())
                .montoTotal(pago.getMontoTotal())
                .cotizacion(pago.getCotizacion())
                .fechaPago(pago.getFechaPago())
                .idFactura(pago.getFactura().getIdFactura());

        if (pago.getMediosPago() != null) {
            for (MedioPago mp : pago.getMediosPago()) {
                builder.agregarIdMedioPago(mp.getIdPago()); // ID del padre MedioPago
            }
        }

        return builder.build();
    }
}