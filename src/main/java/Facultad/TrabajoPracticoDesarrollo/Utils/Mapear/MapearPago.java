package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;

import Facultad.TrabajoPracticoDesarrollo.Dominio.*;
import Facultad.TrabajoPracticoDesarrollo.DTOs.*;

/**
 * Utilidad para convertir entre la entidad {@link Pago} y el DTO {@link DtoPago}.
 *
 * <p>Provee métodos estáticos para mapear en ambas direcciones:
 * - {@link #mapearDtoAEntidad(DtoPago, Factura)}: construye una entidad {@link Pago}
 *   a partir de un {@link DtoPago} y una {@link Factura} ya resuelta y asociada.
 * - {@link #mapearEntidadADto(Pago)}: construye un {@link DtoPago} a partir de una entidad {@link Pago},
 *   incluyendo el mapeo de los medios de pago asociados.</p>
 *
 * <p>Comportamiento ante entradas {@code null}:
 * - Si el DTO de entrada es {@code null} en {@link #mapearDtoAEntidad}, se retorna {@code null}.
 * - Si la entidad de entrada es {@code null} en {@link #mapearEntidadADto}, se retorna {@code null}.</p>
 */
public class MapearPago {

    /**
     * Mapea un {@link DtoPago} a la entidad {@link Pago}.
     *
     * <p>Descripción:
     * - Copia campos básicos (id, cotización, moneda, monto y fecha) desde el DTO.
     * - Asocia la {@link Factura} recibida como parámetro a la nueva {@link Pago} (se asume que la factura
     *   ya fue recuperada/resuelta por el servicio que llama a este método).</p>
     *
     * @param dtoPago DTO de entrada; si es {@code null} se retorna {@code null}
     * @param factura instancia de {@link Factura} que se debe asociar al pago (puede ser {@code null})
     * @return nueva instancia de {@link Pago} construida desde el DTO y la factura, o {@code null} si {@code dtoPago} es {@code null}
     */
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

    /**
     * Mapea una entidad {@link Pago} a su {@link DtoPago}.
     *
     * <p>Descripción:
     * - Copia campos básicos (id, moneda, monto, cotización, fecha).
     * - Si la entidad tiene una {@link Factura} asociada, extrae su número y lo pone en el DTO.
     * - Si existen medios de pago asociados al {@link Pago}, itera sobre ellos y delega
     *   el mapeo a los mapeadores específicos: {@link MapearEfectivo}, {@link MapearCheque},
     *   {@link MapearTarjetaCredito} y {@link MapearTarjetaDebito}. Se detecta el tipo comprobando
     *   cuál de las propiedades del {@link MedioPago} no es {@code null} o mediante instanceof para tarjetas.</p>
     *
     * <p>Notas:
     * - Se preserva el orden de los medios de pago tal como aparecen en {@link Pago#getMediosPago()}.
     * - Si algún medio de pago no coincide con los tipos esperados (efectivo/cheque/tarjeta),
     *   no se agrega nada al DTO para ese elemento.</p>
     *
     * @param pago entidad de entrada; si es {@code null} se retorna {@code null}
     * @return {@link DtoPago} construido desde la entidad, o {@code null} si {@code pago} es {@code null}
     */
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