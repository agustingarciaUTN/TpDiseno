package Dominio;

import java.util.Date;

import enums.EstadoFactura;
import enums.TipoFactura;

public class Factura {

    private int idFactura;
    private String numeroFactura;
    private Date fechaEmision;
    private Date fechaVencimiento;
    private EstadoFactura estadoFactura;
    private TipoFactura tipoFactura;

    // Importes
    private double importeTotal;
    private double importeNeto;
    private double iva;

    // Relaciones (ID para BD + Objeto para Lógica)
    private Estadia estadia;
    private ResponsablePago responsablePago;

    // Nota de crédito es opcional (puede ser nulo en BD), usamos Integer
    private NotaDeCredito notaDeCredito;



    // --- CONSTRUCTOR PRIVADO ---
    private Factura(Builder builder) {
        this.numeroFactura = builder.nroFactura;
        this.fechaEmision = builder.fechaEmision;
        this.fechaVencimiento = builder.fechaVencimiento;
        this.estadoFactura = builder.estadoFactura;
        this.tipoFactura = builder.tipoFactura;
        this.importeTotal = builder.importeTotal;
        this.importeNeto = builder.importeNeto;
        this.iva = builder.iva;
        this.estadia = builder.estadia;
        this.responsablePago = builder.responsable;
        this.notaDeCredito = builder.notaDeCredito;
    }

    public Factura() {}


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

    // Relaciones
    public Estadia getEstadia() { return estadia; }
    public void setEstadia(Estadia estadia) {
        this.estadia = estadia;
    }

    public ResponsablePago getResponsable() { return responsablePago; }
    public void setResponsable(ResponsablePago responsable) {
        this.responsablePago = responsable;
    }

    public NotaDeCredito getNotaDeCredito() { return notaDeCredito; }
    public void setNotaDeCredito(NotaDeCredito notaDeCredito) {
        this.notaDeCredito = notaDeCredito;
    }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private String nroFactura;
        private Date fechaEmision;
        private double importeTotal;


        // Opcionales
        private Date fechaVencimiento;
        private EstadoFactura estadoFactura;
        private TipoFactura tipoFactura;
        private double importeNeto;
        private double iva;

        // Objetos Relacionados
        private Estadia estadia;
        private ResponsablePago responsable;
        private NotaDeCredito notaDeCredito;

        // Constructor con lo obligatorio por base de datos (NOT NULL)
        public Builder(String numeroFactura, Date fechaEmision, double importeTotal, Estadia estadia, ResponsablePago responsable) {
            this.nroFactura = numeroFactura;
            this.fechaEmision = fechaEmision;
            this.importeTotal = importeTotal;
            this.estadia = estadia;
            this.responsable = responsable;
        }

        public Builder nroFactura(String val) { nroFactura = val; return this; }
        public Builder fechaVencimiento(Date val) { fechaVencimiento = val; return this; }
        public Builder estado(EstadoFactura val) { estadoFactura = val; return this; }
        public Builder tipo(TipoFactura val) { tipoFactura = val; return this; }
        public Builder importeNeto(double val) { importeNeto = val; return this; }
        public Builder iva(double val) { iva = val; return this; }

        public Builder estadia(Estadia val) { estadia = val; return this; }
        public Builder responsable(ResponsablePago val) { responsable = val; return this; }
        public Builder notaDeCredito(NotaDeCredito val) { notaDeCredito = val; return this; }

        public Factura build() {
            // Validaciones de Dominio
            if (importeTotal < 0) {
                throw new IllegalArgumentException("El importe total no puede ser negativo.");
            }
            return new Factura(this);
        }
    }

}