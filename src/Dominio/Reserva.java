package Dominio;

import enums.EstadoReserva;
import java.util.Date;
public class Reserva {

    
    private EstadoReserva estadoReserva;
    private Date fechaReserva;
    private Date fechaDesde;
    private Date fechaHasta;
    private String nombreHuespedResponsable;
    private String apellidoHuespedResponsable;
    private String telefonoHuespedResponsable;
    private Habitacion habitacion; // CUAL DE LOS DOS
    private String idHabitacion;
    private int idReserva;
}
