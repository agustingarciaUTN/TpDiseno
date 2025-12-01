package Utils;

import Dominio.NotaDeCredito;
import Dominio.Factura;
import Factura.DtoNotaDeCredito;
import java.util.ArrayList;

public class MapearNotaDeCredito implements MapeoInterfaz<DtoNotaDeCredito, NotaDeCredito> {

    @Override
    public NotaDeCredito mapearDtoAEntidad(DtoNotaDeCredito dtoNotaDeCredito) {
        if (dtoNotaDeCredito == null) return null;

        NotaDeCredito.Builder builder = new NotaDeCredito.Builder(
                dtoNotaDeCredito.getNumeroNotaCredito(),
                dtoNotaDeCredito.getMontoDevolucion()
        );

        // Reconstruir referencias de facturas (Objetos vacíos con ID, si DtoFactura tuviera ID numérico)
        // Como en tu DtoNotaDeCredito usaste ArrayList<Integer> idsFacturas, asumimos que Factura tiene ID.
        if (dtoNotaDeCredito.getIdsFacturas() != null) {
            for (Integer idFactura : dtoNotaDeCredito.getIdsFacturas()) {
                Factura facRef = new Factura.Builder(null, null, 0, null, null).build();
                facRef.setIdFactura(idFactura);
            }
        }

        return builder.build();
    }

    @Override
    public DtoNotaDeCredito mapearEntidadADto(NotaDeCredito notaDeCredito) {
        if (notaDeCredito == null) return null;

        DtoNotaDeCredito.Builder builder = new DtoNotaDeCredito.Builder()
                .numero(notaDeCredito.getNumeroNotaCredito())
                .monto(notaDeCredito.getMontoDevolucion());


        return builder.build();
    }
}