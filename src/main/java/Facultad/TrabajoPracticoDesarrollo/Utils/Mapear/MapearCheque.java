package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Cheque;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoCheque;

public class MapearCheque  {

    public static Cheque mapearDtoAEntidad(DtoCheque dtoCheque) {
        if (dtoCheque == null) return null;
        return new Cheque.Builder()
                .numeroCheque(dtoCheque.getNumeroCheque())
                .banco(dtoCheque.getBanco())
                .plaza(dtoCheque.getPlaza())
                .fechaCobro(dtoCheque.getFechaCobro())
                .build();
    }

    public static DtoCheque mapearEntidadADto(Cheque cheque) {
        if (cheque == null) return null;
        return DtoCheque.builder()
                .numeroCheque(cheque.getNumeroCheque())
                .banco(cheque.getBanco())
                .plaza(cheque.getPlaza())
                .fechaCobro(cheque.getFechaCobro())
                .build();
    }
}