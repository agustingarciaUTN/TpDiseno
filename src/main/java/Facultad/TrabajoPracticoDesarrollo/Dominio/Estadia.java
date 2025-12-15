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

    // Relación con EstadiaHuesped (incluye campo responsable)
    @OneToMany(mappedBy = "estadia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EstadiaHuesped> estadiaHuespedes = new ArrayList<>();

    // La relación con ServiciosAdicionales es unidireccional: el Servicio conoce a la Estadía, pero la Estadía no guarda la lista.

    // Relación con Facturas (1 a N)
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
        this.estadiaHuespedes = builder.estadiaHuespedes;
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
        private List<EstadiaHuesped> estadiaHuespedes = new ArrayList<>();

        public Builder() {}

        public Builder idEstadia(Integer val) { idEstadia = val; return this; }
        public Builder fechaCheckIn(Date val) { fechaCheckIn = val; return this; }
        public Builder fechaCheckOut(Date val) { fechaCheckOut = val; return this; }
        public Builder valorEstadia(Double val) { valorEstadia = val; return this; }
        public Builder reserva(Reserva val) { reserva = val; return this; }
        public Builder habitacion(Habitacion val) { habitacion = val; return this; }
        public Builder agregarEstadiaHuesped(EstadiaHuesped val) {
            if (estadiaHuespedes == null) estadiaHuespedes = new ArrayList<>();
            estadiaHuespedes.add(val);
            return this;
        }

        public Estadia build() { return new Estadia(this); }
    }
}