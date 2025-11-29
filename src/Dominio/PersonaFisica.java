package Dominio;

import enums.TipoDocumento;

public class PersonaFisica extends ResponsablePago {

    private TipoDocumento tipoDocumento;
    private String numeroDocumento;

    // --- CONSTRUCTOR PRIVADO ---
    private PersonaFisica(Builder builder) {
        super(builder.idResponsablePago);
        this.tipoDocumento = builder.tipoDocumento;
        this.numeroDocumento = builder.numeroDocumento;
    }

    // Constructor por defecto
    public PersonaFisica() {
        super(0);
    }

    // --- GETTERS Y SETTERS ---
    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumento tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private TipoDocumento tipoDocumento;
        private String numeroDocumento;
        private int idResponsablePago = 0;

        public Builder(TipoDocumento tipo, String numero) {
            this.tipoDocumento = tipo;
            this.numeroDocumento = numero;
        }

        public Builder idResponsablePago(int val) { idResponsablePago = val; return this; }

        public PersonaFisica build() {
            if (numeroDocumento == null || numeroDocumento.isEmpty()) {
                throw new IllegalArgumentException("El número de documento no puede estar vacío.");
            }
            if (tipoDocumento == null) {
                throw new IllegalArgumentException("El tipo de documento no puede ser nulo.");
            }
            return new PersonaFisica(this);
        }
    }
}
