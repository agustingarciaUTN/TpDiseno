package Facultad.TrabajoPracticoDesarrollo.Dominio;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "estadia")
@Getter @Setter
public class Estadia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estadia")
    private Integer idEstadia;

    @Column(name = "\"fecha_check-in\"")
    @Temporal(TemporalType.DATE)
    private Date fechaCheckIn;

    @Column(name = "\"fecha_check-out\"")
    @Temporal(TemporalType.DATE)
    private Date fechaCheckOut;

    @Column(name = "valor_estadia")
    private Double valorEstadia;

    // --- RELACIONES ---

    @OneToOne
    @JoinColumn(name = "id_reserva")
    private Reserva reserva;

    @ManyToOne
    @JoinColumn(name = "numero_habitacion")
    private Habitacion habitacion;

    // Relación Muchos a Muchos con Huésped
    // Esta SÍ se mantiene porque la estadía necesita saber quiénes se alojaron
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "estadia_huesped",
            joinColumns = @JoinColumn(name = "id_estadia"),
            inverseJoinColumns = {
                    @JoinColumn(name = "tipo_documento", referencedColumnName = "tipo_documento"),
                    @JoinColumn(name = "nro_documento", referencedColumnName = "numero_documento")
            }
    )
    private List<Huesped> huespedes = new ArrayList<>();

    // NOTA: Se eliminó la lista de ServiciosAdicionales.
    // Ahora la relación es unidireccional: el Servicio conoce a la Estadía, pero la Estadía no guarda la lista.

    // Relación con Facturas (1 a N)
    // Se mantiene si es necesario navegar desde la estadía a sus facturas.
    @OneToMany(mappedBy = "estadia")
    private List<Factura> facturas = new ArrayList<>();

    // --- CONSTRUCTORES ---
    public Estadia() {}

    private Estadia(Builder builder) {
        this.idEstadia = builder.idEstadia;
        this.fechaCheckIn = builder.fechaCheckIn;
        this.fechaCheckOut = builder.fechaCheckOut;
        this.valorEstadia = builder.valorEstadia;
        this.reserva = builder.reserva;
        this.habitacion = builder.habitacion;
        this.huespedes = builder.huespedes;
        // Servicios removidos del constructor
    }

    // --- BUILDER ---
    public static class Builder {
        private Integer idEstadia;
        private Date fechaCheckIn;
        private Date fechaCheckOut;
        private Double valorEstadia;
        private Reserva reserva;
        private Habitacion habitacion;
        private List<Huesped> huespedes = new ArrayList<>();

        public Builder() {}

        public Builder idEstadia(Integer val) { idEstadia = val; return this; }
        public Builder fechaCheckIn(Date val) { fechaCheckIn = val; return this; }
        public Builder fechaCheckOut(Date val) { fechaCheckOut = val; return this; }
        public Builder valorEstadia(Double val) { valorEstadia = val; return this; }
        public Builder reserva(Reserva val) { reserva = val; return this; }
        public Builder habitacion(Habitacion val) { habitacion = val; return this; }
        public Builder agregarHuesped(Huesped val) {
            if (huespedes == null) huespedes = new ArrayList<>();
            huespedes.add(val);
            return this;
        }

        public Estadia build() { return new Estadia(this); }
    }
}