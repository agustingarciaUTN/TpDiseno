package Facultad.TrabajoPracticoDesarrollo.Dominio;

import Facultad.TrabajoPracticoDesarrollo.enums.Moneda;

import java.util.ArrayList;
import java.util.Date;

public class Efectivo extends MedioPago{

    private int idEfectivo;
    private Moneda moneda;
    private float monto;
    private Date fechaDePago;

    // --- CONSTRUCTOR PRIVADO (Usado por el Builder) ---
    private Efectivo(Builder builder) {
        // Llamamos al constructor del padre (MedioPago)
        super(builder.idPago, builder.pagos);

        this.idEfectivo = builder.idEfectivo;
        this.moneda = builder.moneda;
        this.monto = builder.monto;
        this.fechaDePago = builder.fechaDePago;
    }

    // Constructor por defecto (opcional)
    public Efectivo() {
        super(0, new ArrayList<>());
    }

    // --- GETTERS Y SETTERS ---
    public int getIdEfectivo() { return idEfectivo; }
    public void setIdEfectivo(int idEfectivo) { this.idEfectivo = idEfectivo; }

    public Moneda getMoneda() { return moneda; }
    public void setMoneda(Moneda moneda) { this.moneda = moneda; }

    public float getMonto() { return monto; }
    public void setMonto(float monto) { this.monto = monto; }

    public Date getFechaDePago() { return fechaDePago; }
    public void setFechaDePago(Date fechaDePago) { this.fechaDePago = fechaDePago; }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        // Atributos propios
        private int idEfectivo = 0;
        private Moneda moneda;
        private float monto;
        private Date fechaDePago;

        // Atributos heredados de MedioPago
        private int idPago = 0;
        private ArrayList<Pago> pagos = new ArrayList<>();

        // Constructor con los datos OBLIGATORIOS
        public Builder(Moneda moneda, float monto, Date fechaDePago) {
            this.moneda = moneda;
            this.monto = monto;
            this.fechaDePago = fechaDePago;
        }

        // Métodos fluidos
        public Builder idEfectivo(int val) { idEfectivo = val; return this; }

        // Métodos para atributos del padre
        public Builder idPago(int val) { idPago = val; return this; }
        public Builder pagos(ArrayList<Pago> val) { pagos = val; return this; }
        public Builder agregarPago(Pago val) {
            if (this.pagos == null) this.pagos = new ArrayList<>();
            this.pagos.add(val);
            return this;
        }

        public Efectivo build() {
            // Validaciones de Dominio
            if (monto < 0) {
                throw new IllegalArgumentException("El monto no puede ser negativo.");
            }
            if (moneda == null) {
                throw new IllegalArgumentException("La moneda es obligatoria.");
            }
            if (fechaDePago == null) {
                throw new IllegalArgumentException("La fecha de pago es obligatoria.");
            }
            return new Efectivo(this);
        }
    }}
