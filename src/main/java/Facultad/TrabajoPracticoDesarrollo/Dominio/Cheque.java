package Facultad.TrabajoPracticoDesarrollo.Dominio;

import Facultad.TrabajoPracticoDesarrollo.enums.Moneda;

import java.util.ArrayList;
import java.util.Date;

public class Cheque extends MedioPago {

    private String numeroCheque;
    private String banco;
    private String plaza;
    private float monto;
    private Date fechaCobro;
    private Date fechaDePago;
    private Moneda moneda;



    // --- CONSTRUCTOR PRIVADO (Usado por el Builder) ---
    private Cheque(Builder builder) {
        // Llamamos al constructor del padre (MedioPago)
        super(builder.idPago, builder.pagos);

        this.numeroCheque = builder.numeroCheque;
        this.banco = builder.banco;
        this.plaza = builder.plaza;
        this.monto = builder.monto;
        this.fechaCobro = builder.fechaCobro;
        this.fechaDePago = builder.fechaDePago;
        this.moneda = builder.moneda;
    }

    // Constructor por defecto (opcional)
    public Cheque() {
        super(0, new ArrayList<>());
    }

    // Getters y Setters
    public String getNumeroCheque() {
        return numeroCheque;
    }
    public void setNumeroCheque(String numeroCheque) {
        this.numeroCheque = numeroCheque;
    }
    public String getBanco() {
        return banco;
    }
    public void setBanco(String banco) {
        this.banco = banco;
    }
    public String getPlaza() {
        return plaza;
    }
    public void setPlaza(String plaza) {
        this.plaza = plaza;
    }
    public float getMonto() {
        return monto;
    }
    public void setMonto(float monto) {
        this.monto = monto;
    }
    public Date getFechaCobro() {
        return fechaCobro;
    }
    public void setFechaCobro(Date fechaCobro) {
        this.fechaCobro = fechaCobro;
    }
    public Date getFechaDePago() {
        return fechaDePago;
    }
    public void setFechaDePago(Date fechaDePago) {
        this.fechaDePago = fechaDePago;
    }
    public void setMoneda(Moneda moneda){this.moneda = moneda;}
    public Moneda getMoneda(){return moneda;}

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        // Atributos propios de Cheque
        private String numeroCheque;
        private String banco;
        private String plaza;
        private float monto;
        private Date fechaCobro;
        private Date fechaDePago;
        private Moneda moneda;

        // Atributos heredados de MedioPago
        private int idPago = 0;
        private ArrayList<Pago> pagos = new ArrayList<>();

        // Constructor con los datos OBLIGATORIOS
        public Builder(String numeroCheque, String banco, float monto) {
            this.numeroCheque = numeroCheque;
            this.banco = banco;
            this.monto = monto;
        }

        // Métodos
        public Builder moneda(Moneda val) {moneda = val; return this;}
        public Builder plaza(String val) { plaza = val; return this; }
        public Builder fechaCobro(Date val) { fechaCobro = val; return this; }
        public Builder fechaDePago(Date val) { fechaDePago = val; return this; }

        // Métodos para atributos del padre
        public Builder idPago(int val) { idPago = val; return this; }
        public Builder pagos(ArrayList<Pago> val) { pagos = val; return this; }
        public Builder agregarPago(Pago val) {
            if (this.pagos == null) this.pagos = new ArrayList<>();
            this.pagos.add(val);
            return this;
        }

        public Cheque build() {
            // Validaciones de dominio
            if (numeroCheque == null || numeroCheque.isEmpty()) {
                throw new IllegalArgumentException("El número de cheque no puede ser nulo o vacío.");
            }
            if (monto < 0) {
                throw new IllegalArgumentException("El monto no puede ser negativo.");
            }
            // Instanciar
            return new Cheque(this);
        }
    }

}
