package Utils.Mapear;
import Dominio.TarjetaCredito;
import MedioDePago.DtoTarjetaCredito;

public class MapearTarjetaCredito implements MapeoInterfaz<DtoTarjetaCredito, TarjetaCredito> {
    @Override
    public TarjetaCredito mapearDtoAEntidad(DtoTarjetaCredito dtoTarjetaCredito) {
        if (dtoTarjetaCredito == null) return null;
        return new TarjetaCredito.Builder(dtoTarjetaCredito.getRedDePago(), dtoTarjetaCredito.getNumeroDeTarjeta(), dtoTarjetaCredito.getMonto(), dtoTarjetaCredito.getCuotasCantidad())
                .idPago(dtoTarjetaCredito.getIdPago())
                .banco(dtoTarjetaCredito.getBanco())
                .fechaVencimiento(dtoTarjetaCredito.getFechaVencimiento())
                .codigoSeguridad(dtoTarjetaCredito.getCodigoSeguridad())
                .moneda(dtoTarjetaCredito.getMoneda())
                .fechaDePago(dtoTarjetaCredito.getFechaDePago())
                .build();
    }
    @Override
    public DtoTarjetaCredito mapearEntidadADto(TarjetaCredito tarjetaCredito) {
        if (tarjetaCredito == null) return null;
        return new DtoTarjetaCredito.Builder()
                .idPago(tarjetaCredito.getIdPago())
                .red(tarjetaCredito.getRedDePago())
                .banco(tarjetaCredito.getBanco())
                .numero(tarjetaCredito.getNumeroDeTarjeta())
                .vencimiento(tarjetaCredito.getFechaVencimiento())
                .seguridad(tarjetaCredito.getCodigoSeguridad())
                .monto(tarjetaCredito.getMonto())
                .moneda(tarjetaCredito.getMoneda())
                .fechaPago(tarjetaCredito.getFechaDePago())
                .cuotas(tarjetaCredito.getCuotasCantidad())
                .build();
    }
}