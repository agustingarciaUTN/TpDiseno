package Dominio;

import java.util.ArrayList;

public abstract class ResponsablePago {
    
    private int idResponsablePago;
    ArrayList<Factura> facturas;

    public ResponsablePago(int idResponsablePago, ArrayList<Factura> facturas) {
        if(idResponsablePago <= 0) {
            throw new IllegalArgumentException("El ID del responsable de pago debe ser un número positivo.");
        }
        this.facturas = facturas;
        this.idResponsablePago = idResponsablePago;
        
    }

    // Getters y Setters
    public int getIdResponsablePago() {
        return idResponsablePago;
    }
    public void setIdResponsablePago(int idResponsablePago) {
        this.idResponsablePago = idResponsablePago;
    }
    public ArrayList<Factura> getFacturas() {
        return facturas;
    }
    public void setFacturas(ArrayList<Factura> facturas) {
        this.facturas = facturas;
    }
}
