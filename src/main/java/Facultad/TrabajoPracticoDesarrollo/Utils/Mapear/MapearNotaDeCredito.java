package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;

import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoHuesped;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Factura;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Huesped;
import Facultad.TrabajoPracticoDesarrollo.Dominio.NotaDeCredito;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoNotaDeCredito;

public class MapearNotaDeCredito  {

    /**
     * Mapea un {@link DtoNotaDeCredito} a la entidad {@link NotaDeCredito}.
     *
     * @param dtoNotaDeCredito DTO de entrada; puede ser {@code null}
     * @return instancia de {@link NotaDeCredito} construida desde el DTO, o {@code null} si {@code dtoNotaDeCredito} es {@code null}
     *
     * <p>Comportamiento: copia número y monto de devolución. No resuelve referencias a facturas
     * en el estado actual (ver bloque comentado para una aproximación).</p>
     */
    public static NotaDeCredito mapearDtoAEntidad(DtoNotaDeCredito dtoNotaDeCredito) {
        if (dtoNotaDeCredito == null) return null;

        NotaDeCredito.Builder builder = new NotaDeCredito.Builder()
                .numero(dtoNotaDeCredito.getNumeroNotaCredito())
                .monto(dtoNotaDeCredito.getMontoDevolucion());

        return builder.build();
    }

    /**
     * Mapea una entidad {@link NotaDeCredito} a su {@link DtoNotaDeCredito}.
     *
     * @param notaDeCredito entidad de entrada; puede ser {@code null}
     * @return instancia de {@link DtoNotaDeCredito} construida desde la entidad, o {@code null} si {@code notaDeCredito} es {@code null}
     *
     * <p>Comportamiento: copia número y monto. Si se requieren datos adicionales
     * en el DTO (por ejemplo IDs de facturas asociadas), ampliar este método para extraerlos.</p>
     */
    public static DtoNotaDeCredito mapearEntidadADto(NotaDeCredito notaDeCredito) {
        if (notaDeCredito == null) return null;

        DtoNotaDeCredito.Builder builder = new DtoNotaDeCredito.Builder()
                .numero(notaDeCredito.getNumeroNotaCredito())
                .monto(notaDeCredito.getMontoDevolucion());


        return builder.build();
    }
}