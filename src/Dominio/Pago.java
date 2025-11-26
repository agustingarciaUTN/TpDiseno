package Dominio;

import enums.Moneda;

import java.util.ArrayList;
import java.util.Date;

public class Pago {
    
    private int idPago;
    private Moneda moneda;
    private float montoTotal;
    private float cotizacion;
    private Date fechaPago;
    private ArrayList<MedioPago> mediosPago;
    private int idFactura;//CUAL DE LOS DOS
    //private Factura factura;// CUAL DE LOS DOS

    public Pago(int idPago , Moneda moneda, float montoTotal, float cotizacion, Date fechaPago, ArrayList<MedioPago> mediosPago, int idFactura) {
        if(idPago <= 0) {
            throw new IllegalArgumentException("El ID del pago debe ser un número positivo.");
        }
        if(montoTotal < 0) {
            throw new IllegalArgumentException("El monto total no puede ser negativo.");
        }
        if(fechaPago == null) {
            throw new IllegalArgumentException("La fecha de pago no puede ser nula.");
        }
        if(idFactura <= 0) {
            throw new IllegalArgumentException("El ID de la factura debe ser un número positivo.");
        }
        if(mediosPago == null || mediosPago.isEmpty()) {
            throw new IllegalArgumentException("El pago debe tener al menos un medio de pago asociado.");
        }
        this.idPago = idPago;
        this.moneda = moneda;
        this.montoTotal = montoTotal;
        this.cotizacion = cotizacion;
        this.fechaPago = fechaPago;
        this.mediosPago = mediosPago;
        this.idFactura = idFactura;
    }

    // Getters y Setters
    public int getIdPago() {
        return idPago;
    }
    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }
    public Moneda getMoneda() {
        return moneda;
    }
    public void setMoneda(Moneda moneda) {
        this.moneda = moneda;
    }
    public float getMontoTotal() {
        return montoTotal;
    }
    public void setMontoTotal(float montoTotal) {
        this.montoTotal = montoTotal;
    }
    public float getCotizacion() {
        return cotizacion;
    }
    public void setCotizacion(float cotizacion) {
        this.cotizacion = cotizacion;
    }
    public Date getFechaPago() {
        return fechaPago;
    }
    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }
    public ArrayList<MedioPago> getMediosPago() {
        return mediosPago;
    }
    public void setMediosPago(ArrayList<MedioPago> mediosPago) {
        this.mediosPago = mediosPago;
    }
    public int getIdFactura() {
        return idFactura;
    }
    public void setIdFactura(int idFactura) {
        this.idFactura = idFactura;
    }
    /*public Factura getFactura() {
        return factura;
    }
    public void setFactura(Factura factura) {
        this.factura = factura;
    }*/
}