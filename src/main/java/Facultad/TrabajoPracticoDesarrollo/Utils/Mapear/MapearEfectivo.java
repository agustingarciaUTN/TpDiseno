package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Efectivo;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoEfectivo;

public class MapearEfectivo  {
    public static Efectivo mapearDtoAEntidad(DtoEfectivo dtoEfectivo) {
        if (dtoEfectivo == null) return null;
        return new Efectivo.Builder()
                .build();
    }
    public static DtoEfectivo mapearEntidadADto(Efectivo efectivo) {
        if (efectivo == null) return null;
        return DtoEfectivo.builder()
                .idEfectivo(efectivo.getIdEfectivo())
                .build();
    }
}