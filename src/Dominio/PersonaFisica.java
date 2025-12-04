package Dominio;

public class PersonaFisica extends ResponsablePago {

    private Huesped huesped;

    // --- CONSTRUCTOR PRIVADO ---
    private PersonaFisica(Builder builder) {
        super(builder.idResponsablePago);
        this.huesped = builder.huesped;
    }

    // Constructor por defecto
    public PersonaFisica() {
        super(0);
    }

    // --- GETTERS Y SETTERS ---
    public Huesped getHuesped() { return huesped; }
    public void setHuesped(Huesped huesped) { this.huesped = huesped; }


    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private Huesped huesped;
        private int idResponsablePago = 0;

        public Builder(Huesped huesped) {
            this.huesped = huesped;
        }

        public Builder idResponsablePago(int val) { idResponsablePago = val; return this; }

        public PersonaFisica build() {
            if (huesped == null) {
                throw new IllegalArgumentException("La persona física debe ser un huésped.");
            }
            return new PersonaFisica(this);
        }
    }
}
