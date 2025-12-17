package Facultad.TrabajoPracticoDesarrollo.DTOs;

import Facultad.TrabajoPracticoDesarrollo.enums.Moneda;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DtoCheque extends DtoMedioPago {

    @NotBlank
    @Size(min = 2, max = 50)
    private String numeroCheque;

    @NotBlank
    private String banco;

    @NotBlank
    private String plaza;

    @NotNull
    @PositiveOrZero
    private Double monto;

    @NotNull
    private Date fechaCobro;

    @NotNull
    @PastOrPresent(message = "La fehca de pago no puede ser futura")
    private Date fechaDePago;

    @NotNull
    private Moneda moneda;
}
