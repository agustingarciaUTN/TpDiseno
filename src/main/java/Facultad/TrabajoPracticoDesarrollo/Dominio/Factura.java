package Facultad.TrabajoPracticoDesarrollo.Dominio;

import Facultad.TrabajoPracticoDesarrollo.enums.EstadoFactura;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoFactura;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "factura")
public class Factura {

    @Id
    @Column(name = "numero_factura") // PK String (ej: "B-00000001")
    private String numeroFactura;

    @Column(name = "fecha_emision")
    @Temporal(TemporalType.DATE)
    private Date fechaEmision;

    @Column(name = "fecha_vencimiento")
    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoFactura estadoFactura;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_factura")
    private TipoFactura tipoFactura;

    // Importes
    @Column(name = "importe_total")
    private Double importeTotal;

    @Column(name = "importe_neto") // Hibernate creará esta columna si falta
    private Double importeNeto;

    @Column(name = "iva") // Hibernate creará esta columna si falta
    private Double iva;

    // --- RELACIONES ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estadia")
    private Estadia estadia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_responsable")
    private ResponsablePago responsablePago;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_nota_credito") // FK Opcional
    private NotaDeCredito notaDeCredito;

    // --- CONSTRUCTORES ---
    public Factura() {}

    private Factura(Builder builder) {
        this.numeroFactura = builder.numeroFactura;
        this.fechaEmision = builder.fechaEmision;
        this.fechaVencimiento = builder.fechaVencimiento;
        this.estadoFactura = builder.estadoFactura;
        this.tipoFactura = builder.tipoFactura;
        this.importeTotal = builder.importeTotal;
        this.importeNeto = builder.importeNeto;
        this.iva = builder.iva;
        this.estadia = builder.estadia;
        this.responsablePago = builder.responsablePago;
        this.notaDeCredito = builder.notaDeCredito;
    }

    // --- GETTERS Y SETTERS ---
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

    public Double getImporteTotal() { return importeTotal; }
    public void setImporteTotal(Double importeTotal) { this.importeTotal = importeTotal; }

    public Double getImporteNeto() { return importeNeto; }
    public void setImporteNeto(Double importeNeto) { this.importeNeto = importeNeto; }

    public Double getIva() { return iva; }
    public void setIva(Double iva) { this.iva = iva; }

    public Estadia getEstadia() { return estadia; }
    public void setEstadia(Estadia estadia) { this.estadia = estadia; }

    public ResponsablePago getResponsablePago() { return responsablePago; }
    public void setResponsablePago(ResponsablePago responsablePago) { this.responsablePago = responsablePago; }

    public NotaDeCredito getNotaDeCredito() { return notaDeCredito; }
    public void setNotaDeCredito(NotaDeCredito notaDeCredito) { this.notaDeCredito = notaDeCredito; }

    // --- BUILDER ---
    public static class Builder {
        private String numeroFactura;
        private Date fechaEmision;
        private Double importeTotal;

        // Opcionales
        private Date fechaVencimiento;
        private EstadoFactura estadoFactura;
        private TipoFactura tipoFactura;
        private Double importeNeto;
        private Double iva;
        private Estadia estadia;
        private ResponsablePago responsablePago;
        private NotaDeCredito notaDeCredito;

        public Builder(String numeroFactura, Date fechaEmision, Double importeTotal) {
            this.numeroFactura = numeroFactura;
            this.fechaEmision = fechaEmision;
            this.importeTotal = importeTotal;
        }

        public Builder vencimiento(Date val) { fechaVencimiento = val; return this; }
        public Builder estado(EstadoFactura val) { estadoFactura = val; return this; }
        public Builder tipo(TipoFactura val) { tipoFactura = val; return this; }
        public Builder neto(Double val) { importeNeto = val; return this; }
        public Builder iva(Double val) { iva = val; return this; }
        public Builder estadia(Estadia val) { estadia = val; return this; }
        public Builder responsable(ResponsablePago val) { responsablePago = val; return this; }
        public Builder notaCredito(NotaDeCredito val) { notaDeCredito = val; return this; }

        public Factura build() { return new Factura(this); }
    }
}