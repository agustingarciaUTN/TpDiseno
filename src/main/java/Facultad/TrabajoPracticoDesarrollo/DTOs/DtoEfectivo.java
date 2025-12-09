package Facultad.TrabajoPracticoDesarrollo.DTOs;


import Facultad.TrabajoPracticoDesarrollo.enums.Moneda;
import lombok.Data;

import java.util.Date;

@Data
public class DtoEfectivo extends DtoMedioPago {

    // --- GETTERS Y SETTERS ---
    private int idPago;      // ID del MedioPago general
    private int idEfectivo;  // ID específico de la tabla efectivo
    private Moneda moneda;
    private float monto;
    private Date fechaDePago;

    // --- CONSTRUCTOR PRIVADO ---
    private DtoEfectivo(Builder builder) {
        this.idPago = builder.idPago;
        this.idEfectivo = builder.idEfectivo;
        this.moneda = builder.moneda;
        this.monto = builder.monto;
        this.fechaDePago = builder.fechaDePago;
    }


    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private int idPago;
        private int idEfectivo;
        private Moneda moneda;
        private float monto;
        private Date fechaDePago;

        public Builder() {}

        public Builder idPago(int val) { idPago = val; return this; }
        public Builder idEfectivo(int val) { idEfectivo = val; return this; }
        public Builder moneda(Moneda val) { moneda = val; return this; }
        public Builder monto(float val) { monto = val; return this; }
        public Builder fechaDePago(Date val) { fechaDePago = val; return this; }

        public DtoEfectivo build() {
            return new DtoEfectivo(this);
        }
    }
}
