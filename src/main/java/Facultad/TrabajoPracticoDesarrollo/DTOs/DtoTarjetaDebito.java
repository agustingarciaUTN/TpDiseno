package Facultad.TrabajoPracticoDesarrollo.DTOs;

import Facultad.TrabajoPracticoDesarrollo.enums.Moneda;
import Facultad.TrabajoPracticoDesarrollo.enums.RedDePago;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Date;

@Data
public class DtoTarjetaDebito extends DtoMedioPago{

    private int idPago; // Creeria que hay que sacarlo

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

    private DtoTarjetaDebito(Builder builder) {
        this.idPago = builder.idPago;
        this.redDePago = builder.redDePago;
        this.banco = builder.banco;
        this.numeroDeTarjeta = builder.numeroDeTarjeta;
        this.fechaVencimiento = builder.fechaVencimiento;
        this.codigoSeguridad = builder.codigoSeguridad;
        this.monto = builder.monto;
        this.moneda = builder.moneda;
        this.fechaDePago = builder.fechaDePago;
    }

    public DtoTarjetaDebito() {}

    public static class Builder {
        private int idPago;
        private RedDePago redDePago;
        private String banco;
        private String numeroDeTarjeta;
        private Date fechaVencimiento;
        private int codigoSeguridad;
        private Double monto;
        private Moneda moneda;
        private Date fechaDePago;

        public Builder() {}

        public Builder idPago(int val) { idPago = val; return this; }
        public Builder red(RedDePago val) { redDePago = val; return this; }
        public Builder banco(String val) { banco = val; return this; }
        public Builder numero(String val) { numeroDeTarjeta = val; return this; }
        public Builder vencimiento(Date val) { fechaVencimiento = val; return this; }
        public Builder seguridad(int val) { codigoSeguridad = val; return this; }
        public Builder monto(Double val) { monto = val; return this; }
        public Builder moneda(Moneda val) { moneda = val; return this; }
        public Builder fechaPago(Date val) { fechaDePago = val; return this; }

        public DtoTarjetaDebito build() {
            return new DtoTarjetaDebito(this);
        }
    }
}