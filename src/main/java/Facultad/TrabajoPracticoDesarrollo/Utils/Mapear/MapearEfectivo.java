package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Efectivo;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoEfectivo;

public class MapearEfectivo  {
    public static Efectivo mapearDtoAEntidad(DtoEfectivo dtoEfectivo) {
        if (dtoEfectivo == null) return null;
        return new Efectivo.Builder(dtoEfectivo.getMonto(), dtoEfectivo.getMoneda())
                .fechaDePago(dtoEfectivo.getFechaDePago())
                .idEfectivo(dtoEfectivo.getIdEfectivo())
                .build();
    }
    public static DtoEfectivo mapearEntidadADto(Efectivo efectivo) {
        if (efectivo == null) return null;
        return new DtoEfectivo.Builder()
                .idEfectivo(efectivo.getIdEfectivo())
                .moneda(efectivo.getMoneda())
                .monto(efectivo.getMonto())
                .fechaDePago(efectivo.getFechaDePago())
                .build();
    }
}