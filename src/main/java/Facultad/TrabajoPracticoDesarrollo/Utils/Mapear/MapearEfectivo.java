package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Efectivo;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoEfectivo;

/**
 * Conversor entre {@link DtoEfectivo} y {@link Efectivo}.
 *
 * <p>Provee métodos estáticos para mapear en ambas direcciones.
 * Si la entrada es {@code null}, los métodos devuelven {@code null}.</p>
 *
 * <p>Nota: el método {@link #mapearDtoAEntidad(DtoEfectivo)} actualmente
 * construye la entidad usando el {@code Builder} sin asignar campos específicos.
 * Completar el mapeo según los atributos reales de {@code DtoEfectivo} y {@code Efectivo}.</p>
 */
public class MapearEfectivo  {

    /**
     * Mapea un {@link DtoEfectivo} a la entidad {@link Efectivo}.
     *
     * @param dtoEfectivo DTO de entrada; puede ser {@code null}
     * @return instancia de {@link Efectivo} construida desde el DTO, o {@code null} si {@code dtoEfectivo} es {@code null}
     *
     * <p>Comportamiento actual: devuelve un {@code Efectivo} creado por su {@code Builder}
     * sin asignar campos adicionales. Actualizar este método para copiar los campos del DTO.</p>
     */
    public static Efectivo mapearDtoAEntidad(DtoEfectivo dtoEfectivo) {
        if (dtoEfectivo == null) return null;
        return new Efectivo.Builder()
                .build();
    }

    /**
     * Mapea una entidad {@link Efectivo} a su {@link DtoEfectivo}.
     *
     * @param efectivo entidad de entrada; puede ser {@code null}
     * @return instancia de {@link DtoEfectivo} construida desde la entidad, o {@code null} si {@code efectivo} es {@code null}
     *
     * <p>Comportamiento: actualmente solo se mapea el identificador {@code idEfectivo}.
     * Añadir más mapeos si el DTO contiene más atributos.</p>
     */
    public static DtoEfectivo mapearEntidadADto(Efectivo efectivo) {
        if (efectivo == null) return null;
        return DtoEfectivo.builder()
                .idEfectivo(efectivo.getIdEfectivo())
                .build();
    }
}