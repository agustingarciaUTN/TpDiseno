package Facultad.TrabajoPracticoDesarrollo.DTOs;

import Facultad.TrabajoPracticoDesarrollo.enums.Moneda;
import lombok.Data;

import java.util.Date;

@Data
public class DtoCheque {
    // --- GETTERS Y SETTERS ---
    private int idPago; // ID del MedioPago
    private String numeroCheque;
    private String banco;
    private String plaza;
    private float monto;
    private Date fechaCobro;
    private Date fechaDePago;
    private Moneda moneda;

    // --- CONSTRUCTOR PRIVADO ---
    private DtoCheque(Builder builder) {
        this.idPago = builder.idPago;
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
        private int idPago;
        private String numeroCheque;
        private String banco;
        private String plaza;
        private float monto;
        private Date fechaCobro;
        private Date fechaDePago;
        private Moneda moneda;


        public Builder() {}

        public Builder idPago(int val) { idPago = val; return this; }
        public Builder moneda(Moneda val) {moneda = val; return this;}
        public Builder numeroCheque(String val) { numeroCheque = val; return this; }
        public Builder banco(String val) { banco = val; return this; }
        public Builder plaza(String val) { plaza = val; return this; }
        public Builder monto(float val) { monto = val; return this; }
        public Builder fechaCobro(Date val) { fechaCobro = val; return this; }
        public Builder fechaDePago(Date val) { fechaDePago = val; return this; }

        public DtoCheque build() {
            return new DtoCheque(this);
        }
    }

}
