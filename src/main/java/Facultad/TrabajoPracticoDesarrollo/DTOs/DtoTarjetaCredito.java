package Facultad.TrabajoPracticoDesarrollo.DTOs;

import Facultad.TrabajoPracticoDesarrollo.enums.Moneda;
import Facultad.TrabajoPracticoDesarrollo.enums.RedDePago;
import lombok.Data;

import java.util.Date;

@Data
public class DtoTarjetaCredito {
    private int idPago;
    private RedDePago redDePago;
    private String banco;
    private String numeroDeTarjeta;
    private Date fechaVencimiento;
    private int codigoSeguridad;
    private float monto;
    private Moneda moneda;
    private Date fechaDePago;
    // Getters y Setters ... (omitidos, estándar)
    private int cuotasCantidad;

    private DtoTarjetaCredito(Builder builder) {
        this.idPago = builder.idPago;
        this.redDePago = builder.redDePago;
        this.banco = builder.banco;
        this.numeroDeTarjeta = builder.numeroDeTarjeta;
        this.fechaVencimiento = builder.fechaVencimiento;
        this.codigoSeguridad = builder.codigoSeguridad;
        this.monto = builder.monto;
        this.moneda = builder.moneda;
        this.fechaDePago = builder.fechaDePago;
        this.cuotasCantidad = builder.cuotasCantidad;
    }


    public static class Builder {
        private int idPago;
        private RedDePago redDePago;
        private String banco;
        private String numeroDeTarjeta;
        private Date fechaVencimiento;
        private int codigoSeguridad;
        private float monto;
        private Moneda moneda;
        private Date fechaDePago;
        private int cuotasCantidad;

        public Builder() {}

        public Builder idPago(int val) { idPago = val; return this; }
        public Builder red(RedDePago val) { redDePago = val; return this; }
        public Builder banco(String val) { banco = val; return this; }
        public Builder numero(String val) { numeroDeTarjeta = val; return this; }
        public Builder vencimiento(Date val) { fechaVencimiento = val; return this; }
        public Builder seguridad(int val) { codigoSeguridad = val; return this; }
        public Builder monto(float val) { monto = val; return this; }
        public Builder moneda(Moneda val) { moneda = val; return this; }
        public Builder fechaPago(Date val) { fechaDePago = val; return this; }
        public Builder cuotas(int val) { cuotasCantidad = val; return this; }

        public DtoTarjetaCredito build() {
            return new DtoTarjetaCredito(this);
        }
    }
}