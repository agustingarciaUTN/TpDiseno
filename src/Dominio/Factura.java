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
    private int idEstadia;
    private Estadia estadia;

    private int idResponsablePago;
    private ResponsablePago responsablePago;

    // Nota de crédito es opcional (puede ser nulo en BD), usamos Integer
    private Integer idNotaDeCredito;
    private NotaDeCredito notaDeCredito;



    // --- CONSTRUCTOR PRIVADO ---
    private Factura(Builder builder) {
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
        this.estadia = builder.estadia;
        this.idResponsablePago = builder.idResponsablePago;
        this.responsablePago = builder.responsable;
        this.idNotaDeCredito = builder.idNotaDeCredito;
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
    public int getIdEstadia() { return idEstadia; }
    public void setIdEstadia(int idEstadia) { this.idEstadia = idEstadia; }

    public Estadia getEstadia() { return estadia; }
    public void setEstadia(Estadia estadia) {
        this.estadia = estadia;
        if(estadia != null) this.idEstadia = estadia.getIdEstadia();
    }

    public int getIdResponsable() { return idResponsablePago; }
    public void setIdResponsable(int idResponsable) { this.idResponsablePago = idResponsable; }

    public ResponsablePago getResponsable() { return responsablePago; }
    public void setResponsable(ResponsablePago responsable) {
        this.responsablePago = responsable;
        if(responsable != null) this.idResponsablePago = responsable.getIdResponsablePago();
    }

    public Integer getIdNotaDeCredito() { return idNotaDeCredito; }
    public void setIdNotaDeCredito(Integer idNotaDeCredito) { this.idNotaDeCredito = idNotaDeCredito; }

    public NotaDeCredito getNotaDeCredito() { return notaDeCredito; }
    public void setNotaDeCredito(NotaDeCredito notaDeCredito) {
        this.notaDeCredito = notaDeCredito;
    }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private String numeroFactura;
        private Date fechaEmision;
        private double importeTotal;
        private int idEstadia;
        private int idResponsablePago;

        // Opcionales
        private int idFactura = 0;
        private Date fechaVencimiento;
        private EstadoFactura estadoFactura;
        private TipoFactura tipoFactura;
        private double importeNeto;
        private double iva;

        // Objetos Relacionados (Opcionales para el builder, útiles para lógica)
        private Estadia estadia;
        private ResponsablePago responsable;
        private Integer idNotaDeCredito = null; // Puede ser nulo
        private NotaDeCredito notaDeCredito;

        // Constructor con lo obligatorio por base de datos (NOT NULL)
        public Builder(String numeroFactura, Date fechaEmision, double importeTotal, int idEstadia, int idResponsable) {
            this.numeroFactura = numeroFactura;
            this.fechaEmision = fechaEmision;
            this.importeTotal = importeTotal;
            this.idEstadia = idEstadia;
            this.idResponsablePago = idResponsable;
        }

        public Builder idFactura(int val) { idFactura = val; return this; }
        public Builder fechaVencimiento(Date val) { fechaVencimiento = val; return this; }
        public Builder estado(EstadoFactura val) { estadoFactura = val; return this; }
        public Builder tipo(TipoFactura val) { tipoFactura = val; return this; }
        public Builder importeNeto(double val) { importeNeto = val; return this; }
        public Builder iva(double val) { iva = val; return this; }

        public Builder estadia(Estadia val) { estadia = val; return this; }
        public Builder responsable(ResponsablePago val) { responsable = val; return this; }

        public Builder idNotaDeCredito(Integer val) { idNotaDeCredito = val; return this; }
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