package Facultad.TrabajoPracticoDesarrollo.DTOs;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.AllArgsConstructor;


@Data
@SuperBuilder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DtoPersonaJuridica extends DtoResponsableDePago {
    // --- GETTERS Y SETTERS ---
    private int idResponsablePago;
    private String razonSocial;
    private String cuit;
    private long telefono;
    private DtoDireccion dtoDireccion;


    // --- CONSTRUCTOR PRIVADO ---
    private DtoPersonaJuridica(Builder builder) {
        this.idResponsablePago = builder.idResponsablePago;
        this.razonSocial = builder.razonSocial;
        this.cuit = builder.cuit;
        this.telefono = builder.telefono;
        this.dtoDireccion = builder.dtoDireccion;
    }


    public DtoDireccion getDireccion() { return dtoDireccion; }
    public void setDireccion(DtoDireccion dtoDireccion) { this.dtoDireccion = dtoDireccion; }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private int idResponsablePago;
        private String razonSocial;
        private String cuit;
        private long telefono;
        private DtoDireccion dtoDireccion;

        public Builder() {}

        public Builder id(int val) { idResponsablePago = val; return this; }
        public Builder razonSocial(String val) { razonSocial = val; return this; }
        public Builder cuit(String val) { cuit = val; return this; }
        public Builder telefono(long val) { telefono = val; return this; }
        public Builder dtoDireccion(DtoDireccion val) { dtoDireccion = val; return this; }

        public DtoPersonaJuridica build() {
            return new DtoPersonaJuridica(this);
        }
    }
}