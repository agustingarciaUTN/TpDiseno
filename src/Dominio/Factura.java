package Dominio;

import java.util.Date;

import enums.EstadoFactura;
import enums.TipoFactura;

public class Factura {
    private int idFactura;
    private ResponsablePago responsablePago;
    private int idResonsablePago; // CUAL DE LOS DOS
    private String numeroFactura;
    private Date fechaEmision;
    private Date fechaVencimiento;
    private EstadoFactura estadoFactura;
    private float iva;
    private float importeNeto;
    private float importeTotal;
    private TipoFactura tipoFactura;
    private int idNotaDeCredito; // CUAL DE LOS DOS
    //private NotaDeCredito notaDeCredito;
    //private Estadia estadia; // CUAL DE LOS DOS
    private int idEstadia;

    public Factura(int idFactura, String numeroFactura, Date fechaEmision, Date fechaVencimiento, EstadoFactura estadoFactura, float iva, float importeNeto, float importeTotal, TipoFactura tipoFactura, int idEstadia) {
        if(idFactura <= 0) {
            throw new IllegalArgumentException("El ID de la factura debe ser un número positivo.");
        }
        if(numeroFactura == null || numeroFactura.isEmpty()) {
            throw new IllegalArgumentException("El número de factura no puede ser nulo o vacío.");
        }
        if(fechaEmision == null) {
            throw new IllegalArgumentException("La fecha de emisión no puede ser nula.");
        }
        if(importeTotal < 0) {
            throw new IllegalArgumentException("El importe total no puede ser negativo.");
        }
        if(idEstadia <= 0) {
            throw new IllegalArgumentException("El ID de la estadía debe ser un número positivo.");
        }
        if(idResonsablePago <= 0) {
            throw new IllegalArgumentException("El ID del responsable de pago debe ser un número positivo.");
        }
        this.idFactura = idFactura;
        this.numeroFactura = numeroFactura;
        this.fechaEmision = fechaEmision;
        this.fechaVencimiento = fechaVencimiento;
        this.estadoFactura = estadoFactura;
        this.iva = iva;
        this.importeNeto = importeNeto;
        this.importeTotal = importeTotal;
        this.tipoFactura = tipoFactura;
        this.idEstadia = idEstadia;
    }

    // Getters y Setters
    public int getIdFactura() {
        return idFactura;
    }
    public void setIdFactura(int idFactura) {
        this.idFactura = idFactura;
    }
    public ResponsablePago getResponsablePago() {
        return responsablePago;
    }
    public void setResponsablePago(ResponsablePago responsablePago) {
        this.responsablePago = responsablePago;
    }
    public String getNumeroFactura() {
        return numeroFactura;
    }
    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }
    public Date getFechaEmision() {
        return fechaEmision;
    }
    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }
    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }
    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }
    public EstadoFactura getEstadoFactura() {
        return estadoFactura;
    }
    public void setEstadoFactura(EstadoFactura estadoFactura) {
        this.estadoFactura = estadoFactura;
    }
    public float getIva() {
        return iva;
    }
    public void setIva(float iva) {
        this.iva = iva;
    }
    public float getImporteNeto() {
        return importeNeto;
    }
    public void setImporteNeto(float importeNeto) {
        this.importeNeto = importeNeto;
    }
    public float getImporteTotal() {
        return importeTotal;
    }
    public void setImporteTotal(float importeTotal) {
        this.importeTotal = importeTotal;
    }
    public TipoFactura getTipoFactura() {
        return tipoFactura;
    }
    public void setTipoFactura(TipoFactura tipoFactura) {
        this.tipoFactura = tipoFactura;
    }
    public int getIdNotaDeCredito() {
        return idNotaDeCredito;
    }
    public void setIdNotaDeCredito(int idNotaDeCredito) {
        this.idNotaDeCredito = idNotaDeCredito;
    }
    /*public NotaDeCredito getNotaDeCredito() {
        return notaDeCredito;
    }
    public void setNotaDeCredito(NotaDeCredito notaDeCredito) {
        this.notaDeCredito = notaDeCredito;
    }
    public Estadia getEstadia() {
        return estadia;
    }
    public void setEstadia(Estadia estadia) {
        this.estadia = estadia;
    }*/
    public int getIdEstadia() {
        return idEstadia;
    }
    public void setIdEstadia(int idEstadia) {
        this.idEstadia = idEstadia;
    }

}