package Dominio;

import enums.EstadoReserva;
import java.util.Date;
public class Reserva {

    private EstadoReserva estadoReserva;
    private Date fechaReserva;
    private String nombreHuespedResponsable;
    private String apellidoHuespedResponsable;
    private String telefonoHuespedResponsable;
    private Habitacion habitacion; // CUAL DE LOS DOS
    private String idHabitacion;

}
