package Facultad.TrabajoPracticoDesarrollo.Dominio;

import Facultad.TrabajoPracticoDesarrollo.enums.EstadoReserva;

import java.util.Date;

public class Reserva {

    private int idReserva;
    private EstadoReserva estadoReserva;
    private Date fechaReserva;
    private Date fechaDesde;
    private Date fechaHasta;

    // Datos del responsable
    private String nombreHuespedResponsable;
    private String apellidoHuespedResponsable;
    private String telefonoHuespedResponsable;

    // Relación
    private Habitacion habitacion;

    // --- CONSTRUCTOR PRIVADO ---
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

    public Reserva() {}

    // --- GETTERS Y SETTERS ---
    public int getIdReserva() { return idReserva; }
    public void setIdReserva(int idReserva) { this.idReserva = idReserva; }

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
    public void setHabitacion(Habitacion habitacion) {
        this.habitacion = habitacion;
    }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private int idReserva = 0;
        private EstadoReserva estadoReserva;
        private Date fechaReserva;
        private Date fechaDesde;
        private Date fechaHasta;
        private String nombreHuespedResponsable;
        private String apellidoHuespedResponsable;
        private String telefonoHuespedResponsable;
        private Habitacion habitacion;

        // Constructor con lo mínimo indispensable para reservar
        public Builder(Date fechaDesde, Date fechaHasta, Habitacion habitacion) {
            this.fechaDesde = fechaDesde;
            this.fechaHasta = fechaHasta;
            this.habitacion = habitacion;
        }

        public Builder id(int val) { idReserva = val; return this; }
        public Builder estado(EstadoReserva val) { estadoReserva = val; return this; }
        public Builder fechaReserva(Date val) { fechaReserva = val; return this; }

        public Builder nombreResponsable(String val) { nombreHuespedResponsable = val; return this; }
        public Builder apellidoResponsable(String val) { apellidoHuespedResponsable = val; return this; }
        public Builder telefonoResponsable(String val) { telefonoHuespedResponsable = val; return this; }

        public Builder habitacion(Habitacion val) {
            this.habitacion = val;
            return this;
        }

        public Reserva build() {
            if (fechaReserva == null) fechaReserva = new Date(); // Default hoy
            if (estadoReserva == null) estadoReserva = EstadoReserva.ACTIVA; // Default activa

            return new Reserva(this);
        }
    }
}