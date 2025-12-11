package Facultad.TrabajoPracticoDesarrollo.DTOs;

import Facultad.TrabajoPracticoDesarrollo.enums.TipoCama;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DtoCama {

    @NotNull
    @Positive
    private int idCama;


    private TipoCama tipoCama;

    @NotNull
    private String idHabitacion;

    @NotNull
    @Valid
    private DtoHabitacion habitacion;

    // --- CONSTRUCTOR PRIVADO (Builder) ---
    private DtoCama(Builder builder) {
        this.idCama = builder.idCama;
        this.tipoCama = builder.tipoCama;
        this.idHabitacion = builder.idHabitacion;
    }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private int idCama;
        private TipoCama tipoCama;
        private String idHabitacion;

        public Builder() {}

        public Builder idCama(int val) { idCama = val; return this; }
        public Builder tipoCama(TipoCama val) { tipoCama = val; return this; }
        public Builder idHabitacion(String val) { idHabitacion = val; return this; }

        public DtoCama build() {
            return new DtoCama(this);
        }
    }
}
