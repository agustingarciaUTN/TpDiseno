package Dominio;

public class NotaDeCredito {

    private String numeroNotaCredito;
    private double montoDevolucion;


    // --- CONSTRUCTOR PRIVADO ---
    private NotaDeCredito(Builder builder) {
        this.numeroNotaCredito = builder.numeroNotaCredito;
        this.montoDevolucion = builder.montoDevolucion;
    }

    // Constructor por defecto
    public NotaDeCredito() {}

    // --- GETTERS Y SETTERS ---
    public String getNumeroNotaCredito() { return numeroNotaCredito; }
    public void setNumeroNotaCredito(String numeroNotaCredito) { this.numeroNotaCredito = numeroNotaCredito; }

    public double getMontoDevolucion() { return montoDevolucion; }
    public void setMontoDevolucion(double montoDevolucion) { this.montoDevolucion = montoDevolucion; }


    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        // Obligatorios
        private String numeroNotaCredito;
        private double montoDevolucion;


        public Builder(String numeroNotaCredito, double montoDevolucion) {
            this.numeroNotaCredito = numeroNotaCredito;
            this.montoDevolucion = montoDevolucion;
        }

        public NotaDeCredito build() {
            if (montoDevolucion < 0) {
                throw new IllegalArgumentException("El monto de devolución no puede ser negativo.");
            }
            if (numeroNotaCredito == null || numeroNotaCredito.isEmpty()) {
                throw new IllegalArgumentException("El número de la nota de crédito es obligatorio (PK).");
            }
            return new NotaDeCredito(this);
        }
    }
}