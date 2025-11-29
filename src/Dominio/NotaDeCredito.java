package Dominio;

import java.util.ArrayList;

public class NotaDeCredito {

    private String numeroNotaCredito;
    private double montoDevolucion;
    private ArrayList<Factura> facturas;

    // --- CONSTRUCTOR PRIVADO ---
    private NotaDeCredito(Builder builder) {
        this.numeroNotaCredito = builder.numeroNotaCredito;
        this.montoDevolucion = builder.montoDevolucion;
        this.facturas = builder.facturas;
    }

    // Constructor por defecto
    public NotaDeCredito() {}

    // --- GETTERS Y SETTERS ---
    public String getNumeroNotaCredito() { return numeroNotaCredito; }
    public void setNumeroNotaCredito(String numeroNotaCredito) { this.numeroNotaCredito = numeroNotaCredito; }

    public double getMontoDevolucion() { return montoDevolucion; }
    public void setMontoDevolucion(double montoDevolucion) { this.montoDevolucion = montoDevolucion; }

    public ArrayList<Factura> getFacturas() { return facturas; }
    public void setFacturas(ArrayList<Factura> facturas) { this.facturas = facturas; }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        // Obligatorios
        private String numeroNotaCredito;
        private double montoDevolucion;

        // Opcionales
        private ArrayList<Factura> facturas = new ArrayList<>();

        public Builder(String numeroNotaCredito, double montoDevolucion) {
            this.numeroNotaCredito = numeroNotaCredito;
            this.montoDevolucion = montoDevolucion;
        }

        public Builder facturas(ArrayList<Factura> val) { facturas = val; return this; }

        public Builder agregarFactura(Factura val) {
            if (this.facturas == null) this.facturas = new ArrayList<>();
            this.facturas.add(val);
            return this;
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