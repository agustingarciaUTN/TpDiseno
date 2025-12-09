package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;
import Facultad.TrabajoPracticoDesarrollo.Dominio.TarjetaDebito;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoTarjetaDebito;

public class MapearTarjetaDebito {

    public static TarjetaDebito mapearDtoAEntidad(DtoTarjetaDebito dtoTarjetaDebito) {
        if (dtoTarjetaDebito == null) return null;
        return new TarjetaDebito.Builder(dtoTarjetaDebito.getRedDePago(), dtoTarjetaDebito.getNumeroDeTarjeta(), dtoTarjetaDebito.getMonto())
          //      .idPago(dtoTarjetaDebito.getIdPago())
                .banco(dtoTarjetaDebito.getBanco())
                .fechaVencimiento(dtoTarjetaDebito.getFechaVencimiento())
                .codigoSeguridad(dtoTarjetaDebito.getCodigoSeguridad())
                .moneda(dtoTarjetaDebito.getMoneda())
                .fecha(dtoTarjetaDebito.getFechaDePago())
                .build();
    }

    public static DtoTarjetaDebito mapearEntidadADto(TarjetaDebito tarjetaDebito) {
        if (tarjetaDebito == null) return null;
        return new DtoTarjetaDebito.Builder()
           //     .idPago(tarjetaDebito.getIdPago())
                .red(tarjetaDebito.getRedDePago())
                .banco(tarjetaDebito.getBanco())
                .numero(tarjetaDebito.getNumeroTarjeta())
                .vencimiento(tarjetaDebito.getFechaVencimiento())
                .seguridad(tarjetaDebito.getCodigoSeguridad())
                .monto(tarjetaDebito.getMonto())
                .moneda(tarjetaDebito.getMoneda())
                .fechaPago(tarjetaDebito.getFechaPago())
                .build();
    }
}