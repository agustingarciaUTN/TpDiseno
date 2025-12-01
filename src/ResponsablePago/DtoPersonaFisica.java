package ResponsablePago;

import Dominio.Huesped;
import enums.TipoDocumento;

public class DtoPersonaFisica {
    private int idResponsablePago;
    private Huesped huesped;

    // --- CONSTRUCTOR PRIVADO ---
    private DtoPersonaFisica(Builder builder) {
        this.idResponsablePago = builder.idResponsablePago;
        this.huesped = builder.huesped;
    }

    public DtoPersonaFisica() {}

    // --- GETTERS Y SETTERS ---
    public int getIdResponsablePago() { return idResponsablePago; }
    public void setIdResponsablePago(int idResponsablePago) { this.idResponsablePago = idResponsablePago; }

    public Huesped getHuesped() { return huesped; }
    public void setHuesped(Huesped huesped) { this.huesped = huesped; }


    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private int idResponsablePago;
        private Huesped huesped;

        public Builder() {}

        public Builder id(int val) { idResponsablePago = val; return this; }
        public Builder huesped(Huesped val) { huesped = val; return this; }

        public DtoPersonaFisica build() {
            return new DtoPersonaFisica(this);
        }
    }
}