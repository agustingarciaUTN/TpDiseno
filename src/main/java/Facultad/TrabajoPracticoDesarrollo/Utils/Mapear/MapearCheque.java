package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Cheque;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoCheque;

public class MapearCheque  {

    public static Cheque mapearDtoAEntidad(DtoCheque dtoCheque) {
        if (dtoCheque == null) return null;
        return new Cheque.Builder(dtoCheque.getNumeroCheque(), dtoCheque.getBanco(), dtoCheque.getMonto())
                .idPago(dtoCheque.getIdPago())
                .plaza(dtoCheque.getPlaza())
                .fechaCobro(dtoCheque.getFechaCobro())
                .fechaDePago(dtoCheque.getFechaDePago())
                .build();
    }

    public static DtoCheque mapearEntidadADto(Cheque cheque) {
        if (cheque == null) return null;
        return new DtoCheque.Builder()
                .idPago(cheque.getIdPago())
                .numeroCheque(cheque.getNumeroCheque())
                .banco(cheque.getBanco())
                .plaza(cheque.getPlaza())
                .monto(cheque.getMonto())
                .fechaCobro(cheque.getFechaCobro())
                .fechaDePago(cheque.getFechaDePago())
                .build();
    }
}