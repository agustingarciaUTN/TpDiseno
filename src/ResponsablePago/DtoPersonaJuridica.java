package ResponsablePago;

import Huesped.DtoDireccion;

public class DtoPersonaJuridica {
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

    public DtoPersonaJuridica() {}

    // --- GETTERS Y SETTERS ---
    public int getIdResponsablePago() { return idResponsablePago; }
    public void setIdResponsablePago(int idResponsablePago) { this.idResponsablePago = idResponsablePago; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getCuit() { return cuit; }
    public void setCuit(String cuit) { this.cuit = cuit; }

    public long getTelefono() { return telefono; }
    public void setTelefono(long telefono) { this.telefono = telefono; }

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