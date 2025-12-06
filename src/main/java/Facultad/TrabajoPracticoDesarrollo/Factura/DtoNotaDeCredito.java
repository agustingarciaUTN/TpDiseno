package Facultad.TrabajoPracticoDesarrollo.Factura;

import java.util.ArrayList;

public class DtoNotaDeCredito {
    private String numeroNotaCredito; // Este es el ID ahora
    private double montoDevolucion;

    // Lista de IDs de facturas asociadas
    private ArrayList<Integer> idsFacturas;

    // --- CONSTRUCTOR PRIVADO ---
    private DtoNotaDeCredito(Builder builder) {
        this.numeroNotaCredito = builder.numeroNotaCredito;
        this.montoDevolucion = builder.montoDevolucion;
        this.idsFacturas = builder.idsFacturas;
    }

    public DtoNotaDeCredito() {}

    // --- GETTERS Y SETTERS ---
    public String getNumeroNotaCredito() { return numeroNotaCredito; }
    public void setNumeroNotaCredito(String numeroNotaCredito) { this.numeroNotaCredito = numeroNotaCredito; }

    public double getMontoDevolucion() { return montoDevolucion; }
    public void setMontoDevolucion(double montoDevolucion) { this.montoDevolucion = montoDevolucion; }

    public ArrayList<Integer> getIdsFacturas() { return idsFacturas; }
    public void setIdsFacturas(ArrayList<Integer> idsFacturas) { this.idsFacturas = idsFacturas; }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private String numeroNotaCredito;
        private double montoDevolucion;
        private ArrayList<Integer> idsFacturas = new ArrayList<>();

        public Builder() {}

        public Builder numero(String val) { numeroNotaCredito = val; return this; }
        public Builder monto(double val) { montoDevolucion = val; return this; }

        public Builder idsFacturas(ArrayList<Integer> val) { idsFacturas = val; return this; }

        public Builder agregarIdFactura(int idFactura) {
            if (this.idsFacturas == null) this.idsFacturas = new ArrayList<>();
            this.idsFacturas.add(idFactura);
            return this;
        }

        public DtoNotaDeCredito build() {
            return new DtoNotaDeCredito(this);
        }
    }
}