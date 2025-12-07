package Facultad.TrabajoPracticoDesarrollo.Factura;

import Facultad.TrabajoPracticoDesarrollo.enums.EstadoFactura;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoFactura;

import java.util.Date;

public class DtoFactura {
    private int idFactura;
    private String numeroFactura;
    private Date fechaEmision;
    private Date fechaVencimiento;
    private EstadoFactura estadoFactura;
    private TipoFactura tipoFactura;
    private double importeTotal;
    private double importeNeto;
    private double iva;

    // Solo IDs para el DTO
    private DtoEstadia dtoEstadia;
    private Dto dtoResponsable;
    private Integer dtoNotaDeCredito; // Integer porque permite null

    // --- CONSTRUCTOR PRIVADO ---
    private DtoFactura(Builder builder) {
        this.idFactura = builder.idFactura;
        this.numeroFactura = builder.numeroFactura;
        this.fechaEmision = builder.fechaEmision;
        this.fechaVencimiento = builder.fechaVencimiento;
        this.estadoFactura = builder.estadoFactura;
        this.tipoFactura = builder.tipoFactura;
        this.importeTotal = builder.importeTotal;
        this.importeNeto = builder.importeNeto;
        this.iva = builder.iva;
        this.idEstadia = builder.idEstadia;
        this.idResponsable = builder.idResponsable;
        this.idNotaDeCredito = builder.idNotaDeCredito;
    }

    public DtoFactura() {}

    // --- GETTERS Y SETTERS ---
    public int getIdFactura() { return idFactura; }
    public void setIdFactura(int idFactura) { this.idFactura = idFactura; }

    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }

    public Date getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(Date fechaEmision) { this.fechaEmision = fechaEmision; }

    public Date getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(Date fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public EstadoFactura getEstadoFactura() { return estadoFactura; }
    public void setEstadoFactura(EstadoFactura estadoFactura) { this.estadoFactura = estadoFactura; }

    public TipoFactura getTipoFactura() { return tipoFactura; }
    public void setTipoFactura(TipoFactura tipoFactura) { this.tipoFactura = tipoFactura; }

    public double getImporteTotal() { return importeTotal; }
    public void setImporteTotal(double importeTotal) { this.importeTotal = importeTotal; }

    public double getImporteNeto() { return importeNeto; }
    public void setImporteNeto(double importeNeto) { this.importeNeto = importeNeto; }

    public double getIva() { return iva; }
    public void setIva(double iva) { this.iva = iva; }

    public int getIdEstadia() { return idEstadia; }
    public void setIdEstadia(int idEstadia) { this.idEstadia = idEstadia; }

    public int getIdResponsable() { return idResponsable; }
    public void setIdResponsable(int idResponsable) { this.idResponsable = idResponsable; }

    public Integer getIdNotaDeCredito() { return idNotaDeCredito; }
    public void setIdNotaDeCredito(Integer idNotaDeCredito) { this.idNotaDeCredito = idNotaDeCredito; }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private int idFactura;
        private String numeroFactura;
        private Date fechaEmision;
        private Date fechaVencimiento;
        private EstadoFactura estadoFactura;
        private TipoFactura tipoFactura;
        private double importeTotal;
        private double importeNeto;
        private double iva;
        private int idEstadia;
        private int idResponsable;
        private Integer idNotaDeCredito;

        public Builder() {}

        public Builder idFactura(int val) { idFactura = val; return this; }
        public Builder numeroFactura(String val) { numeroFactura = val; return this; }
        public Builder fechaEmision(Date val) { fechaEmision = val; return this; }
        public Builder fechaVencimiento(Date val) { fechaVencimiento = val; return this; }
        public Builder estado(EstadoFactura val) { estadoFactura = val; return this; }
        public Builder tipo(TipoFactura val) { tipoFactura = val; return this; }
        public Builder importeTotal(double val) { importeTotal = val; return this; }
        public Builder importeNeto(double val) { importeNeto = val; return this; }
        public Builder iva(double val) { iva = val; return this; }

        public Builder idEstadia(int val) { idEstadia = val; return this; }
        public Builder idResponsable(int val) { idResponsable = val; return this; }
        public Builder idNotaDeCredito(Integer val) { idNotaDeCredito = val; return this; }

        public DtoFactura build() {
            return new DtoFactura(this);
        }
    }
}