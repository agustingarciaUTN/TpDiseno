package Facultad.TrabajoPracticoDesarrollo.Dominio;

import Facultad.TrabajoPracticoDesarrollo.enums.TipoServicio;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "servicios_adicionales")
public class ServiciosAdicionales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio_adicional")
    private Integer id;

    @Column(name = "descripcion_servicio")
    private String descripcion;

    @Column(name = "valor_servicio")
    private Double valor;

    @Column(name = "fecha_consumo")
    @Temporal(TemporalType.DATE)
    private Date fechaConsumo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_servicio")
    private TipoServicio tipoServicio;

    // Relación ManyToOne (Muchos servicios, una estadía)
    @ManyToOne(fetch = FetchType.LAZY) // Lazy es mejor para rendimiento
    @JoinColumn(name = "id_estadia", nullable = false) // FK obligatoria (Composición)
    private Estadia estadia;

    public ServiciosAdicionales() {}

    private ServiciosAdicionales(Builder builder) {
        this.id = builder.id;
        this.descripcion = builder.descripcion;
        this.valor = builder.valor;
        this.fechaConsumo = builder.fechaConsumo;
        this.tipoServicio = builder.tipoServicio;
        this.estadia = builder.estadia;
    }

    // Getters y Setters...
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }
    public Date getFechaConsumo() { return fechaConsumo; }
    public void setFechaConsumo(Date fechaConsumo) { this.fechaConsumo = fechaConsumo; }
    public TipoServicio getTipoServicio() { return tipoServicio; }
    public void setTipoServicio(TipoServicio tipoServicio) { this.tipoServicio = tipoServicio; }
    public Estadia getEstadia() { return estadia; }
    public void setEstadia(Estadia estadia) { this.estadia = estadia; }

    // Builder...
    public static class Builder {
        private Integer id;
        private String descripcion;
        private Double valor;
        private Date fechaConsumo;
        private TipoServicio tipoServicio;
        private Estadia estadia;

        public Builder() {}
        public Builder id(Integer val) { id = val; return this; }
        public Builder descripcion(String val) { descripcion = val; return this; }
        public Builder valor(Double val) { valor = val; return this; }
        public Builder fecha(Date val) { fechaConsumo = val; return this; }
        public Builder tipo(TipoServicio val) { tipoServicio = val; return this; }
        public Builder estadia(Estadia val) { estadia = val; return this; }

        public ServiciosAdicionales build() { return new ServiciosAdicionales(this); }
    }
}