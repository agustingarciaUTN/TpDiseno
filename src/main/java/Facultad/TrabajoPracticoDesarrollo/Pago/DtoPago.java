package Facultad.TrabajoPracticoDesarrollo.Pago;

import Facultad.TrabajoPracticoDesarrollo.enums.Moneda;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;

@Data
public class DtoPago {
    // --- GETTERS Y SETTERS ---
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