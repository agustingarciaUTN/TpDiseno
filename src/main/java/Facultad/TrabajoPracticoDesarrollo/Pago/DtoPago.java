package Facultad.TrabajoPracticoDesarrollo.Pago;

import Facultad.TrabajoPracticoDesarrollo.enums.Moneda;

import java.util.ArrayList;
import java.util.Date;

public class DtoPago {
    private int idPago;
    private Moneda moneda;
    private double montoTotal;
    private double cotizacion;
    private Date fechaPago;
    private int idFactura;

    // Lista de IDs de los medios de pago asociados (para no arrastrar objetos complejos aquí)
    private ArrayList<Integer> idsMediosPago;

    // --- CONSTRUCTOR PRIVADO ---
    private DtoPago(Builder builder) {
        this.idPago = builder.idPago;
        this.moneda = builder.moneda;
        this.montoTotal = builder.montoTotal;
        this.cotizacion = builder.cotizacion;
        this.fechaPago = builder.fechaPago;
        this.idFactura = builder.idFactura;
        this.idsMediosPago = builder.idsMediosPago;
    }

    public DtoPago() {}

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

    public ArrayList<Integer> getIdsMediosPago() { return idsMediosPago; }
    public void setIdsMediosPago(ArrayList<Integer> idsMediosPago) { this.idsMediosPago = idsMediosPago; }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private int idPago;
        private Moneda moneda;
        private double montoTotal;
        private double cotizacion;
        private Date fechaPago;
        private int idFactura;
        private ArrayList<Integer> idsMediosPago = new ArrayList<>();

        public Builder() {}

        public Builder idPago(int val) { idPago = val; return this; }
        public Builder moneda(Moneda val) { moneda = val; return this; }
        public Builder montoTotal(double val) { montoTotal = val; return this; }
        public Builder cotizacion(double val) { cotizacion = val; return this; }
        public Builder fechaPago(Date val) { fechaPago = val; return this; }
        public Builder idFactura(int val) { idFactura = val; return this; }

        public Builder idsMediosPago(ArrayList<Integer> val) { idsMediosPago = val; return this; }
        public Builder agregarIdMedioPago(int val) {
            if (this.idsMediosPago == null) this.idsMediosPago = new ArrayList<>();
            this.idsMediosPago.add(val);
            return this;
        }

        public DtoPago build() {
            return new DtoPago(this);
        }
    }
}