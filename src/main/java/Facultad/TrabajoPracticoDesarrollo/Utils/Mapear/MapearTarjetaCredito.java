package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;
import Facultad.TrabajoPracticoDesarrollo.Dominio.TarjetaCredito;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoTarjetaCredito;
/**
 * Utilidad para convertir entre la entidad {@link TarjetaCredito} y el DTO {@link DtoTarjetaCredito}.
 *
 * <p>Provee métodos estáticos para mapear en ambas direcciones:
 * - {@link #mapearDtoAEntidad(DtoTarjetaCredito)}: construye una instancia de {@link TarjetaCredito}
 *   a partir de los datos presentes en el DTO.
 * - {@link #mapearEntidadADto(TarjetaCredito)}: construye un {@link DtoTarjetaCredito} a partir de la entidad.</p>
 *
 * <p>Comportamiento ante entradas {@code null}:
 * - Si el parámetro de entrada de cualquiera de los métodos es {@code null}, se retorna {@code null}.</p>
 *
 * <p>Notas:
 * - La línea relativa a {@code idPago} permanece comentada en el código original; si se requiere mapear
 *   ese campo activar/descomentar y asegurarse de la correspondencia en las clases dominio/DTO.</p>
 */
public class MapearTarjetaCredito  {
    /**
     * Mapea un {@link DtoTarjetaCredito} a la entidad {@link TarjetaCredito}.
     *
     * @param dtoTarjetaCredito DTO de entrada; si es {@code null} se retorna {@code null}
     * @return instancia de {@link TarjetaCredito} construida desde el DTO, o {@code null} si el DTO es {@code null}
     *
     * <p>Descripción:
     * - Copia los campos principales: número, red de pago, cantidad de cuotas, banco, fecha de vencimiento
     *   y código de seguridad al builder de {@link TarjetaCredito}.</p>
     */
    public static TarjetaCredito mapearDtoAEntidad(DtoTarjetaCredito dtoTarjetaCredito) {
        if (dtoTarjetaCredito == null) return null;
        return new TarjetaCredito.Builder()
             //   .idPago(dtoTarjetaCredito.getIdPago())
                .nro(dtoTarjetaCredito.getNumeroDeTarjeta())
                .red(dtoTarjetaCredito.getRedDePago())
                .cuotas(dtoTarjetaCredito.getCuotasCantidad())
                .banco(dtoTarjetaCredito.getBanco())
                .fechaVencimiento(dtoTarjetaCredito.getFechaVencimiento())
                .codigoSeguridad(dtoTarjetaCredito.getCodigoSeguridad())
                .build();
    }
    /**
     * Mapea una entidad {@link TarjetaCredito} a su {@link DtoTarjetaCredito}.
     *
     * @param tarjetaCredito entidad de entrada; si es {@code null} se retorna {@code null}
     * @return {@link DtoTarjetaCredito} construido desde la entidad, o {@code null} si la entidad es {@code null}
     *
     * <p>Descripción:
     * - Copia los campos principales desde la entidad al builder del DTO.
     * - Preserva la correspondencia de nombres entre entidad y DTO (p. ej. {@code numeroTarjeta} -> {@code numeroDeTarjeta}).</p>
     */
    public static DtoTarjetaCredito mapearEntidadADto(TarjetaCredito tarjetaCredito) {
        if (tarjetaCredito == null) return null;
        return DtoTarjetaCredito.builder()
             //   .idPago(tarjetaCredito.getIdPago())
                .redDePago(tarjetaCredito.getRedDePago())
                .banco(tarjetaCredito.getBanco())
                .numeroDeTarjeta(tarjetaCredito.getNumeroTarjeta())
                .fechaVencimiento(tarjetaCredito.getFechaVencimiento())
                .codigoSeguridad(tarjetaCredito.getCodigoSeguridad())
                .cuotasCantidad(tarjetaCredito.getCuotas())
                .build();
    }
}