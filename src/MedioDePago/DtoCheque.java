package MedioDePago;

import Dominio.Cheque;
import enums.Moneda;

import java.util.Date;

public class DtoCheque {
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

    // Constructor por defecto
    public DtoCheque() {}

    // --- GETTERS Y SETTERS ---
    public int getIdPago() { return idPago; }
    public void setIdPago(int idPago) { this.idPago = idPago; }

    public String getNumeroCheque() { return numeroCheque; }
    public void setNumeroCheque(String numeroCheque) { this.numeroCheque = numeroCheque; }

    public String getBanco() { return banco; }
    public void setBanco(String banco) { this.banco = banco; }

    public String getPlaza() { return plaza; }
    public void setPlaza(String plaza) { this.plaza = plaza; }

    public float getMonto() { return monto; }
    public void setMonto(float monto) { this.monto = monto; }

    public Date getFechaCobro() { return fechaCobro; }
    public void setFechaCobro(Date fechaCobro) { this.fechaCobro = fechaCobro; }

    public Date getFechaDePago() { return fechaDePago; }
    public void setFechaDePago(Date fechaDePago) { this.fechaDePago = fechaDePago; }

    public void setMoneda(Moneda moneda){this.moneda = moneda;}
    public Moneda getMoneda(){return moneda;}

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
