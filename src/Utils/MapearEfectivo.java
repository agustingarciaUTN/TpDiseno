package Utils;
import Dominio.Efectivo;
import MedioDePago.DtoEfectivo;

public class MapearEfectivo implements MapeoInterfaz<DtoEfectivo, Efectivo> {
    @Override
    public Efectivo mapearDtoAEntidad(DtoEfectivo dtoEfectivo) {
        if (dtoEfectivo == null) return null;
        return new Efectivo.Builder(dtoEfectivo.getMoneda(), dtoEfectivo.getMonto(), dtoEfectivo.getFechaDePago())
                .idPago(dtoEfectivo.getIdPago())
                .idEfectivo(dtoEfectivo.getIdEfectivo())
                .build();
    }
    @Override
    public DtoEfectivo mapearEntidadADto(Efectivo efectivo) {
        if (efectivo == null) return null;
        return new DtoEfectivo.Builder()
                .idPago(efectivo.getIdPago())
                .idEfectivo(efectivo.getIdEfectivo())
                .moneda(efectivo.getMoneda())
                .monto(efectivo.getMonto())
                .fechaDePago(efectivo.getFechaDePago())
                .build();
    }
}