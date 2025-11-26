package Dominio;

import java.util.ArrayList;

public class NotaDeCredito {
    
    private String numeroNotaCredito;
    private float montoDevolucion;
    private ArrayList<Factura> facturas;

    public NotaDeCredito( String numeroNotaCredito, float montoDevolucion, ArrayList<Factura> facturas) {
        if(montoDevolucion < 0) {
            throw new IllegalArgumentException("El monto de devolución no puede ser negativo.");
        }
        if(numeroNotaCredito == null || numeroNotaCredito.isEmpty()) {
            throw new IllegalArgumentException("El número de la nota de crédito no puede estar vacío.");
        }
        if(facturas == null || facturas.isEmpty()) {
            throw new IllegalArgumentException("La nota de crédito debe estar asociada a al menos una factura.");
        }
        this.numeroNotaCredito = numeroNotaCredito;
        this.montoDevolucion = montoDevolucion;
        this.facturas = facturas;
    }

    // Getters y Setters
    public String getNumeroNotaCredito() {
        return numeroNotaCredito;
    }
    public void setNumeroNotaCredito(String numeroNotaCredito) {
        if(numeroNotaCredito == null || numeroNotaCredito.isEmpty()) {
            throw new IllegalArgumentException("El número de la nota de crédito no puede estar vacío.");
        }
        this.numeroNotaCredito = numeroNotaCredito;
    }

    public float getMontoDevolucion() {
        return montoDevolucion;
    }

    public void setMontoDevolucion(float montoDevolucion) {
        if(montoDevolucion < 0) {
            throw new IllegalArgumentException("El monto de devolución no puede ser negativo.");
        }
        this.montoDevolucion = montoDevolucion;
    }

    public ArrayList<Factura> getFacturas() {
        return facturas;
    }

    public void setFacturas(ArrayList<Factura> facturas) {
        if(facturas == null || facturas.isEmpty()) {
            throw new IllegalArgumentException("La nota de crédito debe estar asociada a al menos una factura.");
        }
        this.facturas = facturas;
    }
}