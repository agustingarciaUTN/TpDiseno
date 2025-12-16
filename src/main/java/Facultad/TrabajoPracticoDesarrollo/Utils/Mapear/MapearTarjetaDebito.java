package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;
import Facultad.TrabajoPracticoDesarrollo.Dominio.TarjetaDebito;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoTarjetaDebito;

public class MapearTarjetaDebito {

    public static TarjetaDebito mapearDtoAEntidad(DtoTarjetaDebito dtoTarjetaDebito) {
        if (dtoTarjetaDebito == null) return null;
        return new TarjetaDebito.Builder()
          //      .idPago(dtoTarjetaDebito.getIdPago())
                .nro(dtoTarjetaDebito.getNumeroDeTarjeta())
                .red(dtoTarjetaDebito.getRedDePago())
                .banco(dtoTarjetaDebito.getBanco())
                .fechaVencimiento(dtoTarjetaDebito.getFechaVencimiento())
                .codigoSeguridad(dtoTarjetaDebito.getCodigoSeguridad())
                .build();
    }

    public static DtoTarjetaDebito mapearEntidadADto(TarjetaDebito tarjetaDebito) {
        if (tarjetaDebito == null) return null;
        return DtoTarjetaDebito.builder()
           //     .idPago(tarjetaDebito.getIdPago())
                .redDePago(tarjetaDebito.getRedDePago())
                .banco(tarjetaDebito.getBanco())
                .numeroDeTarjeta(tarjetaDebito.getNumeroTarjeta())
                .fechaVencimiento(tarjetaDebito.getFechaVencimiento())
                .codigoSeguridad(tarjetaDebito.getCodigoSeguridad())
                .build();
    }
}