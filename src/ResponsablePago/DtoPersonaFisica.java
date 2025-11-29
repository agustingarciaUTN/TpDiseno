package ResponsablePago;

import enums.TipoDocumento;

public class DtoPersonaFisica {
    private int idResponsablePago;
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;

    // --- CONSTRUCTOR PRIVADO ---
    private DtoPersonaFisica(Builder builder) {
        this.idResponsablePago = builder.idResponsablePago;
        this.tipoDocumento = builder.tipoDocumento;
        this.numeroDocumento = builder.numeroDocumento;
    }

    public DtoPersonaFisica() {}

    // --- GETTERS Y SETTERS ---
    public int getIdResponsablePago() { return idResponsablePago; }
    public void setIdResponsablePago(int idResponsablePago) { this.idResponsablePago = idResponsablePago; }

    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumento tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private int idResponsablePago;
        private TipoDocumento tipoDocumento;
        private String numeroDocumento;

        public Builder() {}

        public Builder id(int val) { idResponsablePago = val; return this; }
        public Builder tipoDocumento(TipoDocumento val) { tipoDocumento = val; return this; }
        public Builder numeroDocumento(String val) { numeroDocumento = val; return this; }

        public DtoPersonaFisica build() {
            return new DtoPersonaFisica(this);
        }
    }
}