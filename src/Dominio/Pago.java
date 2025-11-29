package Dominio;

import enums.Moneda;
import java.util.ArrayList;
import java.util.Date;


public class Pago {

    private int idPago;
    private Moneda moneda;
    private double montoTotal; // double por consistencia
    private double cotizacion;
    private Date fechaPago;

    // Relación con Factura (Doble referencia)
    private int idFactura;
    private Factura factura;

    // Relación con Medios de Pago
    private ArrayList<MedioPago> mediosPago;

    // --- CONSTRUCTOR PRIVADO ---
    private Pago(Builder builder) {
        this.idPago = builder.idPago;
        this.moneda = builder.moneda;
        this.montoTotal = builder.montoTotal;
        this.cotizacion = builder.cotizacion;
        this.fechaPago = builder.fechaPago;
        this.idFactura = builder.idFactura;
        this.factura = builder.factura;
        this.mediosPago = builder.mediosPago;
    }

    // Constructor por defecto
    public Pago() {}

    // --- GETTERS Y SETTERS ---
    public int getIdPago() { return idPago; }
    public void setIdPago(int idPago) { this.idPago = idPago; }

    public Moneda getMoneda() { return moneda; }
    public void setMoneda(Moneda moneda) { this.moneda = moneda; }

    public double getMontoTotal() { return montoTotal; }
    public void setMontoTotal(double montoTotal) { this.montoTotal = montoTotal; }

    public double getCotizacion() { return cotizacion; }
    public void setCotizacion(double cotizacion) { this.cotizacion = cotizacion; }

    public Date getFechaPago() { return fechaPago; }
    public void setFechaPago(Date fechaPago) { this.fechaPago = fechaPago; }

    public int getIdFactura() { return idFactura; }
    public void setIdFactura(int idFactura) { this.idFactura = idFactura; }

    public Factura getFactura() { return factura; }
    public void setFactura(Factura factura) {
        this.factura = factura;
        if (factura != null) this.idFactura = factura.getIdFactura();
    }

    public ArrayList<MedioPago> getMediosPago() { return mediosPago; }
    public void setMediosPago(ArrayList<MedioPago> mediosPago) { this.mediosPago = mediosPago; }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private int idPago = 0;
        private Moneda moneda;
        private double montoTotal;
        private double cotizacion;
        private Date fechaPago;
        private int idFactura;

        // Opcionales
        private Factura factura;
        private ArrayList<MedioPago> mediosPago = new ArrayList<>();

        // Constructor con obligatorios
        public Builder(Moneda moneda, double montoTotal, Date fechaPago, int idFactura) {
            this.moneda = moneda;
            this.montoTotal = montoTotal;
            this.fechaPago = fechaPago;
            this.idFactura = idFactura;
        }

        public Builder idPago(int val) { idPago = val; return this; }
        public Builder cotizacion(double val) { cotizacion = val; return this; }

        // Relaciones
        public Builder factura(Factura val) { factura = val; return this; }

        public Builder mediosPago(ArrayList<MedioPago> val) { mediosPago = val; return this; }
        public Builder agregarMedioPago(MedioPago val) {
            if (this.mediosPago == null) this.mediosPago = new ArrayList<>();
            this.mediosPago.add(val);
            return this;
        }

        public Pago build() {
            if (montoTotal < 0) {
                throw new IllegalArgumentException("El monto total no puede ser negativo.");
            }
            if (moneda == null) {
                throw new IllegalArgumentException("La moneda es obligatoria.");
            }
            if (fechaPago == null) {
                throw new IllegalArgumentException("La fecha de pago es obligatoria.");
            }
            if (idFactura <= 0) {
                throw new IllegalArgumentException("El ID de la factura asociada es obligatorio.");
            }
            return new Pago(this);
        }
    }
}