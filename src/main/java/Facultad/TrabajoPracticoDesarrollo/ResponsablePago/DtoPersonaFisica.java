package Facultad.TrabajoPracticoDesarrollo.ResponsablePago;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Huesped;
import Facultad.TrabajoPracticoDesarrollo.Huesped.DtoHuesped;
import lombok.Getter;

public class DtoPersonaFisica {
    // --- GETTERS Y SETTERS ---
    @Getter
    private int idResponsablePago;
    private DtoHuesped dtoHuesped;

    // --- CONSTRUCTOR PRIVADO ---
    private DtoPersonaFisica(Builder builder) {
        this.idResponsablePago = builder.idResponsablePago;
        this.dtoHuesped = builder.dtoHuesped;
    }

    public DtoPersonaFisica() {}

    public DtoHuesped getHuesped() { return dtoHuesped; }
    public void setHuesped(DtoHuesped huesped) { this.dtoHuesped = huesped; }


    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private int idResponsablePago;
        private DtoHuesped dtoHuesped;

        public Builder() {}

        public Builder id(int val) { idResponsablePago = val; return this; }
        public Builder huesped(DtoHuesped val) { dtoHuesped = val; return this; }

        public DtoPersonaFisica build() {
            return new DtoPersonaFisica(this);
        }
    }
}