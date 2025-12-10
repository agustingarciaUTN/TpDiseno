package Facultad.TrabajoPracticoDesarrollo.DTOs;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.util.ArrayList;

@Data
public class DtoNotaDeCredito {

    @NotNull
    @Positive
    private Integer numeroNotaCredito; // Este es el ID ahora

    @NotNull
    @PositiveOrZero
    private double montoDevolucion;

    // Lista de IDs de facturas asociadas
    // private ArrayList<Integer> idsFacturas;

    // --- CONSTRUCTOR PRIVADO ---
    private DtoNotaDeCredito(Builder builder) {
        this.numeroNotaCredito = builder.numeroNotaCredito;
        this.montoDevolucion = builder.montoDevolucion;
       // this.idsFacturas = builder.idsFacturas;
    }


    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private Integer numeroNotaCredito;
        private double montoDevolucion;
       // private ArrayList<Integer> idsFacturas = new ArrayList<>();

        public Builder() {}

        public Builder numero(Integer val) { numeroNotaCredito = val; return this; }
        public Builder monto(double val) { montoDevolucion = val; return this; }

      //  public Builder idsFacturas(ArrayList<Integer> val) { idsFacturas = val; return this; }

       /* public Builder agregarIdFactura(Integer idFactura) {
            if (this.idsFacturas == null) this.idsFacturas = new ArrayList<>();
            this.idsFacturas.add(idFactura);
            return this;
        }  */

        public DtoNotaDeCredito build() {
            return new DtoNotaDeCredito(this);
        }
    }
}