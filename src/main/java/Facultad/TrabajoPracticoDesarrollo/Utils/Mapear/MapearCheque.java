package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Cheque;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoCheque;
/**
 * Conversor entre {@link DtoCheque} y {@link Cheque}.
 *
 * <p>Provee métodos estáticos para mapear en ambas direcciones.
 * Si la entrada es {@code null}, los métodos devuelven {@code null}.</p>
 *
 * <p>Ejemplo:
 * <pre>
 * Cheque entidad = MapearCheque.mapearDtoAEntidad(dto);
 * DtoCheque dto = MapearCheque.mapearEntidadADto(entidad);
 * </pre>
 * </p>
 */
public class MapearCheque  {
    /**
     * Mapea un {@link DtoCheque} a la entidad {@link Cheque}.
     *
     * @param dtoCheque DTO de entrada; puede ser {@code null}
     * @return instancia de {@link Cheque} construida desde el DTO, o {@code null} si {@code dtoCheque} es {@code null}
     */
    public static Cheque mapearDtoAEntidad(DtoCheque dtoCheque) {
        if (dtoCheque == null) return null;
        return new Cheque.Builder()
                .numeroCheque(dtoCheque.getNumeroCheque())
                .banco(dtoCheque.getBanco())
                .plaza(dtoCheque.getPlaza())
                .fechaCobro(dtoCheque.getFechaCobro())
                .build();
    }
    /**
     * Mapea una entidad {@link Cheque} a su {@link DtoCheque}.
     *
     * @param cheque entidad de entrada; puede ser {@code null}
     * @return instancia de {@link DtoCheque} construida desde la entidad, o {@code null} si {@code cheque} es {@code null}
     */
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