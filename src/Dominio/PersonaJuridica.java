package Dominio;



public class PersonaJuridica extends ResponsablePago {

    private String razonSocial;
    private String cuit;
    private long telefono;
    private Direccion direccion;


    // --- CONSTRUCTOR PRIVADO ---
    private PersonaJuridica(Builder builder) {
        // Llamada al constructor del padre
        super(builder.idResponsablePago);

        this.razonSocial = builder.razonSocial;
        this.cuit = builder.cuit;
        this.telefono = builder.telefono;
        this.direccion = builder.direccion;
    }

    // Constructor por defecto
    public PersonaJuridica() {
        super(0);
    }

    // --- GETTERS Y SETTERS ---
    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getCuit() { return cuit; }
    public void setCuit(String cuit) { this.cuit = cuit; }

    public long getTelefono() { return telefono; }
    public void setTelefono(long telefono) { this.telefono = telefono; }

    public Direccion getDireccion() { return direccion; }
    public void setDireccion(Direccion direccion) { this.direccion = direccion; }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        // Propios
        private String razonSocial;
        private String cuit;
        private long telefono;
        private Direccion direccion;

        // Heredados de ResponsablePago
        private int idResponsablePago = 0;

        // Constructor con obligatorios
        public Builder(String razonSocial, String cuit, Direccion direccion) {
            this.razonSocial = razonSocial;
            this.cuit = cuit;
            this.direccion = direccion;
        }

        // Métodos fluidos
        public Builder telefono(long val) { telefono = val; return this; }

        public Builder idResponsablePago(int val) { idResponsablePago = val; return this; }

        public PersonaJuridica build() {
            // Validaciones de Dominio
            if (razonSocial == null || razonSocial.isEmpty()) {
                throw new IllegalArgumentException("La razón social no puede estar vacía.");
            }
            if (cuit == null || cuit.isEmpty()) {
                throw new IllegalArgumentException("El CUIT es obligatorio.");
            }
            if (direccion == null) {
                throw new IllegalArgumentException("La persona jurídica debe tener una dirección asignada.");
            }
            return new PersonaJuridica(this);
        }
    }
}