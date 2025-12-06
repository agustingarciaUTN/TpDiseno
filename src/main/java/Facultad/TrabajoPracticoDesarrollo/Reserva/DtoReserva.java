package Facultad.TrabajoPracticoDesarrollo.Reserva;

import Facultad.TrabajoPracticoDesarrollo.enums.EstadoReserva;

import java.util.Date;

public class DtoReserva {
    private int idReserva;
    private EstadoReserva estadoReserva;
    private Date fechaReserva;
    private Date fechaDesde;
    private Date fechaHasta;
    private String nombreHuespedResponsable;
    private String apellidoHuespedResponsable;
    private String telefonoHuespedResponsable;

    // Solo ID para el DTO
    private String idHabitacion;

    // --- CONSTRUCTOR PRIVADO ---
    private DtoReserva(Builder builder) {
        this.idReserva = builder.idReserva;
        this.estadoReserva = builder.estadoReserva;
        this.fechaReserva = builder.fechaReserva;
        this.fechaDesde = builder.fechaDesde;
        this.fechaHasta = builder.fechaHasta;
        this.nombreHuespedResponsable = builder.nombreHuespedResponsable;
        this.apellidoHuespedResponsable = builder.apellidoHuespedResponsable;
        this.telefonoHuespedResponsable = builder.telefonoHuespedResponsable;
        this.idHabitacion = builder.idHabitacion;
    }

    public DtoReserva() {}

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
    public void setNombreHuespedResponsable(String val) { this.nombreHuespedResponsable = val; }

    public String getApellidoHuespedResponsable() { return apellidoHuespedResponsable; }
    public void setApellidoHuespedResponsable(String val) { this.apellidoHuespedResponsable = val; }

    public String getTelefonoHuespedResponsable() { return telefonoHuespedResponsable; }
    public void setTelefonoHuespedResponsable(String val) { this.telefonoHuespedResponsable = val; }

    public String getIdHabitacion() { return idHabitacion; }
    public void setIdHabitacion(String idHabitacion) { this.idHabitacion = idHabitacion; }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private int idReserva;
        private EstadoReserva estadoReserva;
        private Date fechaReserva;
        private Date fechaDesde;
        private Date fechaHasta;
        private String nombreHuespedResponsable;
        private String apellidoHuespedResponsable;
        private String telefonoHuespedResponsable;
        private String idHabitacion;

        public Builder() {}

        public Builder id(int val) { idReserva = val; return this; }
        public Builder estado(EstadoReserva val) { estadoReserva = val; return this; }
        public Builder fechaReserva(Date val) { fechaReserva = val; return this; }
        public Builder fechaDesde(Date val) { fechaDesde = val; return this; }
        public Builder fechaHasta(Date val) { fechaHasta = val; return this; }

        public Builder nombreResponsable(String val) { nombreHuespedResponsable = val; return this; }
        public Builder apellidoResponsable(String val) { apellidoHuespedResponsable = val; return this; }
        public Builder telefonoResponsable(String val) { telefonoHuespedResponsable = val; return this; }

        public Builder idHabitacion(String val) { idHabitacion = val; return this; }

        public DtoReserva build() {
            return new DtoReserva(this);
        }
    }
}