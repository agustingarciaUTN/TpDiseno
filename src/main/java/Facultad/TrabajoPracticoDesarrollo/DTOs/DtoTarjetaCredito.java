package Facultad.TrabajoPracticoDesarrollo.DTOs;

import Facultad.TrabajoPracticoDesarrollo.enums.Moneda;
import Facultad.TrabajoPracticoDesarrollo.enums.RedDePago;
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
public class DtoTarjetaCredito extends DtoMedioPago {
    private int idPago; //Creeria que hay que sacarlo

    private RedDePago redDePago;

    @NotBlank
    @Size(min = 2, max = 100)
    private String banco;

    @NotBlank
    @Size(min = 2, max = 20)
    private String numeroDeTarjeta;

    @FutureOrPresent
    private Date fechaVencimiento;

    //Para despues de corregir
    private int codigoSeguridad;

    @NotNull
    @PositiveOrZero
    private Double monto;

    @NotNull
    private Moneda moneda;

    @NotNull
    @PastOrPresent(message = "La fehca de pago no puede ser futura")
    private Date fechaDePago;
    // Getters y Setters ... (omitidos, estándar)

    @Positive
    private int cuotasCantidad;
}