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
                .cuotas(dtoTarjetaCredito.getCuotasCantidad())
                .banco(dtoTarjetaCredito.getBanco())
                .fechaVencimiento(dtoTarjetaCredito.getFechaVencimiento())
                .codigoSeguridad(dtoTarjetaCredito.getCodigoSeguridad())
                .build();
    }

    public static DtoTarjetaCredito mapearEntidadADto(TarjetaCredito tarjetaCredito) {
        if (tarjetaCredito == null) return null;
        return DtoTarjetaCredito.builder()
             //   .idPago(tarjetaCredito.getIdPago())
                .redDePago(tarjetaCredito.getRedDePago())
                .banco(tarjetaCredito.getBanco())
                .numeroDeTarjeta(tarjetaCredito.getNumeroTarjeta())
                .fechaVencimiento(tarjetaCredito.getFechaVencimiento())
                .codigoSeguridad(tarjetaCredito.getCodigoSeguridad())
                .cuotasCantidad(tarjetaCredito.getCuotas())
                .build();
    }
}