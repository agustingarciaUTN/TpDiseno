package Facultad.TrabajoPracticoDesarrollo.Dominio;

import Facultad.TrabajoPracticoDesarrollo.enums.EstadoReserva;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "reserva")
@Getter @Setter
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Integer idReserva;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_reserva")
    private EstadoReserva estadoReserva;

    @Column(name = "fecha_reserva")
    @Temporal(TemporalType.DATE)
    private Date fechaReserva;

    @Column(name = "fecha_desde")
    @Temporal(TemporalType.DATE)
    private Date fechaDesde;

    @Column(name = "fecha_hasta")
    @Temporal(TemporalType.DATE)
    private Date fechaHasta;

    // Datos del responsable (se guardan directo en la tabla reserva según tu JSON)
    @Column(name = "\"NombreHuespedResponsable\"")//EN ESTOS TRES ES POSIBLE QUE HAYA QUE SACAR LAS BARRAS INVERTIDAS Y LAS COMILLAS
    private String nombreHuespedResponsable;

    @Column(name = "\"ApellidoHuespedResponsable\"")//EN ESTOS TRES ES POSIBLE QUE HAYA QUE SACAR LAS BARRAS INVERTIDAS Y LAS COMILLAS
    private String apellidoHuespedResponsable;

    // CORRECCIÓN SOLICITADA: Mapeo exacto de la columna de teléfono
    @Column(name = "\"TelefonoHuespedResponsable\"")//EN ESTOS TRES ES POSIBLE QUE HAYA QUE SACAR LAS BARRAS INVERTIDAS Y LAS COMILLAS
    private String telefonoHuespedResponsable;

    // Relación con Habitación
    @ManyToOne
    @JoinColumn(name = "id_habitacion") // FK en tabla reserva
    private Habitacion habitacion;

    public Reserva() {}

    private Reserva(Builder builder) {
        this.idReserva = builder.idReserva;
        this.estadoReserva = builder.estadoReserva;
        this.fechaReserva = builder.fechaReserva;
        this.fechaDesde = builder.fechaDesde;
        this.fechaHasta = builder.fechaHasta;
        this.nombreHuespedResponsable = builder.nombreHuespedResponsable;
        this.apellidoHuespedResponsable = builder.apellidoHuespedResponsable;
        this.telefonoHuespedResponsable = builder.telefonoHuespedResponsable;
        this.habitacion = builder.habitacion;
    }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private Integer idReserva;
        private EstadoReserva estadoReserva;
        private Date fechaReserva;
        private Date fechaDesde;
        private Date fechaHasta;
        private String nombreHuespedResponsable;
        private String apellidoHuespedResponsable;
        private String telefonoHuespedResponsable;
        private Habitacion habitacion;

        // Constructor con lo mínimo indispensable
        public Builder() {}

        public Builder fechaDesde(Date val) { fechaDesde = val; return this; }
        public Builder fechaHasta(Date val) { fechaHasta = val; return this; }
        public Builder id(Integer val) { idReserva = val; return this; }
        public Builder estado(EstadoReserva val) { estadoReserva = val; return this; }
        public Builder fechaReserva(Date val) { fechaReserva = val; return this; }
        public Builder nombreResponsable(String val) { nombreHuespedResponsable = val; return this; }
        public Builder apellidoResponsable(String val) { apellidoHuespedResponsable = val; return this; }
        public Builder telefonoResponsable(String val) { telefonoHuespedResponsable = val; return this; }
        public Builder habitacion(Habitacion val) { habitacion = val; return this; }

        public Reserva build() { return new Reserva(this); }
    }
}