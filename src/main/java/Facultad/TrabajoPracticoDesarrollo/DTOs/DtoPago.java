package Facultad.TrabajoPracticoDesarrollo.DTOs;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Factura;
import Facultad.TrabajoPracticoDesarrollo.enums.Moneda;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;

@Data
public class DtoPago {

    @NotNull
    @Positive
    private int idPago;

    @NotNull
    private Moneda moneda;

    @NotNull
    @PositiveOrZero
    private double montoTotal;

    @NotNull
    private double cotizacion;

    @NotNull
    @PastOrPresent(message = "La fehca de pago no puede ser futura")
    private Date fechaPago;

    @NotNull
    @Valid
    private Factura factura;

    // Lista de IDs de los medios de pago asociados (para no arrastrar objetos complejos aquí)
    private ArrayList<Integer> idsMediosPago;

    // --- CONSTRUCTOR PRIVADO ---
    private DtoPago(Builder builder) {
        this.idPago = builder.idPago;
        this.moneda = builder.moneda;
        this.montoTotal = builder.montoTotal;
        this.cotizacion = builder.cotizacion;
        this.fechaPago = builder.fechaPago;
        this.factura = builder.factura;
        this.idsMediosPago = builder.idsMediosPago;
    }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private int idPago;
        private Moneda moneda;
        private double montoTotal;
        private double cotizacion;
        private Date fechaPago;
        private Factura factura;
        private ArrayList<Integer> idsMediosPago = new ArrayList<>();

        public Builder() {}

        public Builder idPago(int val) { idPago = val; return this; }
        public Builder moneda(Moneda val) { moneda = val; return this; }
        public Builder montoTotal(double val) { montoTotal = val; return this; }
        public Builder cotizacion(double val) { cotizacion = val; return this; }
        public Builder fechaPago(Date val) { fechaPago = val; return this; }
        public Builder Factura(Factura val) { factura = val; return this; }
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