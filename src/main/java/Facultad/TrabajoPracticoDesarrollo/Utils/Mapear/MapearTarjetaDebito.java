package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;
import Facultad.TrabajoPracticoDesarrollo.Dominio.TarjetaDebito;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoTarjetaDebito;

/**
 * Conversor entre la entidad {@link TarjetaDebito} y el DTO {@link DtoTarjetaDebito}.
 *
 * <p>Provee métodos estáticos para mapear en ambas direcciones:
 * - {@link #mapearDtoAEntidad(DtoTarjetaDebito)}: construye una entidad {@link TarjetaDebito}
 *   a partir de los datos del DTO.
 * - {@link #mapearEntidadADto(TarjetaDebito)}: construye un {@link DtoTarjetaDebito} a partir de la entidad.</p>
 *
 * <p>Comportamiento ante entradas {@code null}:
 * - Si el parámetro de entrada es {@code null}, los métodos retornan {@code null}.</p>
 *
 * <p>Notas:
 * - La línea relacionada a {@code idPago} permanece comentada en ambos métodos conforme al código original;
 *   si se requiere mapear ese campo, descomentar y asegurar la correspondencia en las clases de dominio/DTO.</p>
 */
public class MapearTarjetaDebito {

    /**
     * Mapea un {@link DtoTarjetaDebito} a la entidad {@link TarjetaDebito}.
     *
     * @param dtoTarjetaDebito DTO de entrada; si es {@code null} se retorna {@code null}.
     * @return instancia de {@link TarjetaDebito} construida desde el DTO, o {@code null} si el DTO es {@code null}.
     *
     * <p>Campos mapeados:
     * - número de tarjeta -> {@code nro}
     * - red de pago -> {@code red}
     * - banco -> {@code banco}
     * - fecha de vencimiento -> {@code fechaVencimiento}
     * - código de seguridad -> {@code codigoSeguridad}</p>
     */
    public static TarjetaDebito mapearDtoAEntidad(DtoTarjetaDebito dtoTarjetaDebito) {
        if (dtoTarjetaDebito == null) return null;
        return new TarjetaDebito.Builder()
          //      .idPago(dtoTarjetaDebito.getIdPago())
                .nro(dtoTarjetaDebito.getNumeroDeTarjeta())
                .red(dtoTarjetaDebito.getRedDePago())
                .banco(dtoTarjetaDebito.getBanco())
                .fechaVencimiento(dtoTarjetaDebito.getFechaVencimiento())
                .codigoSeguridad(dtoTarjetaDebito.getCodigoSeguridad())
                .build();
    }

    /**
     * Mapea una entidad {@link TarjetaDebito} a su {@link DtoTarjetaDebito}.
     *
     * @param tarjetaDebito entidad de entrada; si es {@code null} se retorna {@code null}.
     * @return {@link DtoTarjetaDebito} construido desde la entidad, o {@code null} si la entidad es {@code null}.
     *
     * <p>Campos mapeados:
     * - red de pago -> {@code redDePago}
     * - banco -> {@code banco}
     * - número de tarjeta -> {@code numeroDeTarjeta}
     * - fecha de vencimiento -> {@code fechaVencimiento}
     * - código de seguridad -> {@code codigoSeguridad}</p>
     */
    public static DtoTarjetaDebito mapearEntidadADto(TarjetaDebito tarjetaDebito) {
        if (tarjetaDebito == null) return null;
        return DtoTarjetaDebito.builder()
           //     .idPago(tarjetaDebito.getIdPago())
                .redDePago(tarjetaDebito.getRedDePago())
                .banco(tarjetaDebito.getBanco())
                .numeroDeTarjeta(tarjetaDebito.getNumeroTarjeta())
                .fechaVencimiento(tarjetaDebito.getFechaVencimiento())
                .codigoSeguridad(tarjetaDebito.getCodigoSeguridad())
                .build();
    }
}