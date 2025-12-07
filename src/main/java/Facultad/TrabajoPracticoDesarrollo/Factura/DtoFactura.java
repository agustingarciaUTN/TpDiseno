package Facultad.TrabajoPracticoDesarrollo.Factura;

import Facultad.TrabajoPracticoDesarrollo.Estadia.DtoEstadia;
import Facultad.TrabajoPracticoDesarrollo.ResponsablePago.DtoResponsableDePago;
import Facultad.TrabajoPracticoDesarrollo.enums.EstadoFactura;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoFactura;
import lombok.Data;


import java.util.Date;

@Data
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
    private DtoResponsableDePago dtoResponsable;
    private DtoNotaDeCredito dtoNotaDeCredito; // Integer porque permite null

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
        this.dtoEstadia = builder.dtoEstadia;
        this.dtoResponsable = builder.dtoResponsable;
        this.dtoNotaDeCredito = builder.dtoNotaDeCredito;
    }

    public DtoEstadia getIdEstadia() { return dtoEstadia; }
    public void setIdEstadia(DtoEstadia dtoEstadia) { this.dtoEstadia = dtoEstadia; }

    public DtoResponsableDePago getIdResponsable() { return dtoResponsable; }
    public void setIdResponsable(DtoResponsableDePago dtoResponsable) { this.dtoResponsable = dtoResponsable; }

    public DtoNotaDeCredito getIdNotaDeCredito() { return dtoNotaDeCredito; }
    public void setIdNotaDeCredito(DtoNotaDeCredito dtoNotaDeCredito) { this.dtoNotaDeCredito = dtoNotaDeCredito; }

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
        private DtoEstadia dtoEstadia;
        private DtoResponsableDePago dtoResponsable;
        private DtoNotaDeCredito dtoNotaDeCredito;

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

        public Builder idEstadia(DtoEstadia val) { dtoEstadia = val; return this; }
        public Builder idResponsable(DtoResponsableDePago val) { dtoResponsable = val; return this; }
        public Builder idNotaDeCredito(DtoNotaDeCredito val) { dtoNotaDeCredito = val; return this; }

        public DtoFactura build() {
            return new DtoFactura(this);
        }
    }
}