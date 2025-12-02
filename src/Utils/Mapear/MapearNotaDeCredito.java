package Utils.Mapear;

import Dominio.NotaDeCredito;
import Dominio.Factura;
import Factura.DtoNotaDeCredito;

public class MapearNotaDeCredito  {


    public static NotaDeCredito mapearDtoAEntidad(DtoNotaDeCredito dtoNotaDeCredito) {
        if (dtoNotaDeCredito == null) return null;

        NotaDeCredito.Builder builder = new NotaDeCredito.Builder(
                dtoNotaDeCredito.getNumeroNotaCredito(),
                dtoNotaDeCredito.getMontoDevolucion()
        );

        if (dtoNotaDeCredito.getIdsFacturas() != null) {
            for (Integer idFactura : dtoNotaDeCredito.getIdsFacturas()) {
                Factura facRef = new Factura.Builder(null, null, 0, null, null).build();
                facRef.setIdFactura(idFactura);
            }
        }

        return builder.build();
    }


    public static DtoNotaDeCredito mapearEntidadADto(NotaDeCredito notaDeCredito) {
        if (notaDeCredito == null) return null;

        DtoNotaDeCredito.Builder builder = new DtoNotaDeCredito.Builder()
                .numero(notaDeCredito.getNumeroNotaCredito())
                .monto(notaDeCredito.getMontoDevolucion());


        return builder.build();
    }
}