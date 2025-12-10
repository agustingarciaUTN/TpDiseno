package Facultad.TrabajoPracticoDesarrollo.DTOs;

import Facultad.TrabajoPracticoDesarrollo.enums.Moneda;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
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

    // --- CONSTRUCTOR PRIVADO ---
    private DtoCheque(Builder builder) {
        this.numeroCheque = builder.numeroCheque;
        this.banco = builder.banco;
        this.plaza = builder.plaza;
        this.monto = builder.monto;
        this.fechaCobro = builder.fechaCobro;
        this.fechaDePago = builder.fechaDePago;
        this.moneda = builder.moneda;
    }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private String numeroCheque;
        private String banco;
        private String plaza;
        private Double monto;
        private Date fechaCobro;
        private Date fechaDePago;
        private Moneda moneda;


        public Builder() {}

        public Builder moneda(Moneda val) {moneda = val; return this;}
        public Builder numeroCheque(String val) { numeroCheque = val; return this; }
        public Builder banco(String val) { banco = val; return this; }
        public Builder plaza(String val) { plaza = val; return this; }
        public Builder monto(Double val) { monto = val; return this; }
        public Builder fechaCobro(Date val) { fechaCobro = val; return this; }
        public Builder fechaDePago(Date val) { fechaDePago = val; return this; }

        public DtoCheque build() {
            return new DtoCheque(this);
        }
    }

}
