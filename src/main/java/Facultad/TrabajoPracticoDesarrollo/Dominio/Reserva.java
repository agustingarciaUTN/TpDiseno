package Facultad.TrabajoPracticoDesarrollo.Dominio;

import Facultad.TrabajoPracticoDesarrollo.enums.EstadoReserva;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "reserva")
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

    // --- GETTERS Y SETTERS ---
    public Integer getIdReserva() { return idReserva; }
    public void setIdReserva(Integer idReserva) { this.idReserva = idReserva; }

    public EstadoReserva getEstadoReserva() { return estadoReserva; }
    public void setEstadoReserva(EstadoReserva estadoReserva) { this.estadoReserva = estadoReserva; }

    public Date getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(Date fechaReserva) { this.fechaReserva = fechaReserva; }

    public Date getFechaDesde() { return fechaDesde; }
    public void setFechaDesde(Date fechaDesde) { this.fechaDesde = fechaDesde; }

    public Date getFechaHasta() { return fechaHasta; }
    public void setFechaHasta(Date fechaHasta) { this.fechaHasta = fechaHasta; }

    public String getNombreHuespedResponsable() { return nombreHuespedResponsable; }
    public void setNombreHuespedResponsable(String nombreHuespedResponsable) { this.nombreHuespedResponsable = nombreHuespedResponsable; }

    public String getApellidoHuespedResponsable() { return apellidoHuespedResponsable; }
    public void setApellidoHuespedResponsable(String apellidoHuespedResponsable) { this.apellidoHuespedResponsable = apellidoHuespedResponsable; }

    public String getTelefonoHuespedResponsable() { return telefonoHuespedResponsable; }
    public void setTelefonoHuespedResponsable(String telefonoHuespedResponsable) { this.telefonoHuespedResponsable = telefonoHuespedResponsable; }

    public Habitacion getHabitacion() { return habitacion; }
    public void setHabitacion(Habitacion habitacion) { this.habitacion = habitacion; }

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
        public Builder(Date fechaDesde, Date fechaHasta, Habitacion habitacion) {
            this.fechaDesde = fechaDesde;
            this.fechaHasta = fechaHasta;
            this.habitacion = habitacion;
        }

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