package Facultad.TrabajoPracticoDesarrollo.Dominio;

import Facultad.TrabajoPracticoDesarrollo.enums.Moneda;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Integer idPago;

    @Column(name = "monto_total")
    private Double montoTotal;

    @Column(name = "cotizacion")
    private Double cotizacion;

    @Column(name = "fecha_pago")
    @Temporal(TemporalType.DATE)
    private Date fechaPago;

    @Enumerated(EnumType.STRING)
    @Column(name = "moneda")
    private Moneda moneda;

    // --- RELACIONES ---

    // Relación Muchos a Uno con Factura (La tabla pago tiene la FK 'id_factura')
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_factura", referencedColumnName = "numero_factura")
    private Factura factura;

    // Relación Uno a Muchos con MedioPago (Un pago se compone de uno o más medios)
    @OneToMany(mappedBy = "pago", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedioPago> mediosPago = new ArrayList<>();

    // --- CONSTRUCTORES ---
    public Pago() {}

    private Pago(Builder builder) {
        this.idPago = builder.idPago;
        this.montoTotal = builder.montoTotal;
        this.cotizacion = builder.cotizacion;
        this.fechaPago = builder.fechaPago;
        this.moneda = builder.moneda;
        this.factura = builder.factura;

        // Asignamos la lista y vinculamos la relación bidireccional
        if (builder.mediosPago != null) {
            for (MedioPago mp : builder.mediosPago) {
                this.agregarMedioPago(mp);
            }
        }
    }

    // --- MÉTODOS HELPER (Relación Bidireccional) ---
    public void agregarMedioPago(MedioPago medio) {
        mediosPago.add(medio);
        medio.setPago(this);
    }

    public void removerMedioPago(MedioPago medio) {
        mediosPago.remove(medio);
        medio.setPago(null);
    }

    // --- GETTERS Y SETTERS ---
    public Integer getIdPago() { return idPago; }
    public void setIdPago(Integer idPago) { this.idPago = idPago; }

    public Double getMontoTotal() { return montoTotal; }
    public void setMontoTotal(Double montoTotal) { this.montoTotal = montoTotal; }

    public Double getCotizacion() { return cotizacion; }
    public void setCotizacion(Double cotizacion) { this.cotizacion = cotizacion; }

    public Date getFechaPago() { return fechaPago; }
    public void setFechaPago(Date fechaPago) { this.fechaPago = fechaPago; }

    public Moneda getMoneda() { return moneda; }
    public void setMoneda(Moneda moneda) { this.moneda = moneda; }

    public Factura getFactura() { return factura; }
    public void setFactura(Factura factura) { this.factura = factura; }

    public List<MedioPago> getMediosPago() { return mediosPago; }
    public void setMediosPago(List<MedioPago> mediosPago) { this.mediosPago = mediosPago; }

    // --- BUILDER ---
    public static class Builder {
        private Integer idPago;
        private Double montoTotal;
        private Double cotizacion;
        private Date fechaPago;
        private Moneda moneda;
        private Factura factura;
        private List<MedioPago> mediosPago = new ArrayList<>();

        public Builder() {}

        public Builder id(Integer val) { idPago = val; return this; }
        public Builder monto(Double val) { montoTotal = val; return this; }
        public Builder cotizacion(Double val) { cotizacion = val; return this; }
        public Builder fecha(Date val) { fechaPago = val; return this; }
        public Builder moneda(Moneda val) { moneda = val; return this; }
        public Builder factura(Factura val) { factura = val; return this; }

        public Builder agregarMedio(MedioPago val) {
            if (mediosPago == null) mediosPago = new ArrayList<>();
            mediosPago.add(val);
            return this;
        }

        public Pago build() {
            return new Pago(this);
        }
    }


    public Pago build() {
            if (montoTotal < 0) {
                throw new IllegalArgumentException("El monto total no puede ser negativo.");
            }
            if (moneda == null) {
                throw new IllegalArgumentException("La moneda es obligatoria.");
            }
            if (fechaPago == null) {
                throw new IllegalArgumentException("La fecha de pago es obligatoria.");
            }
            if (factura == null) {
                throw new IllegalArgumentException("Debe existir una factura asociada.");
            }
            return new Pago(this);
    }
}

