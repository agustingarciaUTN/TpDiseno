package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Factura;
import Facultad.TrabajoPracticoDesarrollo.Dominio.NotaDeCredito;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoNotaDeCredito;

public class MapearNotaDeCredito  {


    public static NotaDeCredito mapearDtoAEntidad(DtoNotaDeCredito dtoNotaDeCredito) {
        if (dtoNotaDeCredito == null) return null;

        NotaDeCredito.Builder builder = new NotaDeCredito.Builder(
                dtoNotaDeCredito.getNumeroNotaCredito(),
                dtoNotaDeCredito.getMontoDevolucion()
        );

      /*  if (dtoNotaDeCredito.getIdsFacturas() != null) {
            for (Integer idFactura : dtoNotaDeCredito.getIdsFacturas()) {
                Factura facRef = new Factura.Builder(null, null, 0, null, null).build();
                facRef.setNumeroFactura(idFactura);
            }
        }   */

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