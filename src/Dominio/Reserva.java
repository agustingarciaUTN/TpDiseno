package Dominio;

import enums.EstadoReserva;
import java.util.Date;
public class Reserva {

    private int idReserva;
    private EstadoReserva estadoReserva;
    private Date fechaReserva;
    private Date fechaDesde;
    private Date fechaHasta;
    private String nombreHuespedResponsable;
    private String apellidoHuespedResponsable;
    private String telefonoHuespedResponsable;
    private Habitacion habitacion; // CUAL DE LOS DOS
    private String idHabitacion;
    private int idEstadia; // CUAL DE LOS DOS
    private Estadia estadia;

    public Reserva(Date fechaDesde, Date fechaHasta, int idReserva, String idHabitacion, EstadoReserva estadoReserva) {
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
        this.idReserva = idReserva;
        this.idHabitacion = idHabitacion;
        this.estadoReserva = estadoReserva;
    }

    //setters y getters
    public int getIdReserva() {
        return idReserva;
    }
    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }
    public EstadoReserva getEstadoReserva() {
        return estadoReserva;
    }
    public void setEstadoReserva(EstadoReserva estadoReserva) {
        this.estadoReserva = estadoReserva;
    }
    public Date getFechaReserva() {
        return fechaReserva;
    }
    public void setFechaReserva(Date fechaReserva) {
        this.fechaReserva = fechaReserva;
    }
    public Date getFechaDesde() {
        return fechaDesde;
    }
    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }
    public Date getFechaHasta() {
        return fechaHasta;
    }
    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }
    public String getNombreHuespedResponsable() {
        return nombreHuespedResponsable;
    }
    public void setNombreHuespedResponsable(String nombreHuespedResponsable) {
        this.nombreHuespedResponsable = nombreHuespedResponsable;
    }
    public String getApellidoHuespedResponsable() {
        return apellidoHuespedResponsable;
    }
    public void setApellidoHuespedResponsable(String apellidoHuespedResponsable) {
        this.apellidoHuespedResponsable = apellidoHuespedResponsable;
    }
    public String getTelefonoHuespedResponsable() {
        return telefonoHuespedResponsable;
    }
    public void setTelefonoHuespedResponsable(String telefonoHuespedResponsable) {
        this.telefonoHuespedResponsable = telefonoHuespedResponsable;
    }
    public Habitacion getHabitacion() {
        return habitacion;
    }
    public void setHabitacion(Habitacion habitacion) {
        this.habitacion = habitacion;
    }
    public String getIdHabitacion() {
        return idHabitacion;
    }
    public void setIdHabitacion(String idHabitacion) {
        this.idHabitacion = idHabitacion;
    }
    public int getIdEstadia() {
        return idEstadia;
    }
    public void setIdEstadia(int idEstadia) {
        this.idEstadia = idEstadia;
    }
    public Estadia getEstadia() {
        return estadia;
    }
    public void setEstadia(Estadia estadia) {
        this.estadia = estadia;
    }
}

