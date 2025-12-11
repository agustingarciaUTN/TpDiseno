package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;
import Facultad.TrabajoPracticoDesarrollo.Dominio.TarjetaCredito;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoTarjetaCredito;

public class MapearTarjetaCredito  {

    public static TarjetaCredito mapearDtoAEntidad(DtoTarjetaCredito dtoTarjetaCredito) {
        if (dtoTarjetaCredito == null) return null;
        return new TarjetaCredito.Builder()
             //   .idPago(dtoTarjetaCredito.getIdPago())
                .nro(dtoTarjetaCredito.getNumeroDeTarjeta())
                .red(dtoTarjetaCredito.getRedDePago())
                .monto(dtoTarjetaCredito.getMonto())
                .cuotas(dtoTarjetaCredito.getCuotasCantidad())
                .banco(dtoTarjetaCredito.getBanco())
                .fechaVencimiento(dtoTarjetaCredito.getFechaVencimiento())
                .codigoSeguridad(dtoTarjetaCredito.getCodigoSeguridad())
                .moneda(dtoTarjetaCredito.getMoneda())
                .fecha(dtoTarjetaCredito.getFechaDePago())
                .build();
    }

    public static DtoTarjetaCredito mapearEntidadADto(TarjetaCredito tarjetaCredito) {
        if (tarjetaCredito == null) return null;
        return new DtoTarjetaCredito.Builder()
             //   .idPago(tarjetaCredito.getIdPago())
                .red(tarjetaCredito.getRedDePago())
                .banco(tarjetaCredito.getBanco())
                .numero(tarjetaCredito.getNumeroTarjeta())
                .vencimiento(tarjetaCredito.getFechaVencimiento())
                .seguridad(tarjetaCredito.getCodigoSeguridad())
                .monto(tarjetaCredito.getMonto())
                .moneda(tarjetaCredito.getMoneda())
                .fechaPago(tarjetaCredito.getFechaPago())
                .cuotas(tarjetaCredito.getCuotas())
                .build();
    }
}