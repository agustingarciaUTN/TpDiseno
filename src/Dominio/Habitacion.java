package Dominio;
import enums.TipoHabitacion;

import java.util.ArrayList;

import enums.EstadoHabitacion;
public class Habitacion {
    
    private String numero;
    private TipoHabitacion tipoHabitacion;
    private int capacidad;
    private EstadoHabitacion estadoHabitacion;
    private float costoPorNoche;
    private ArrayList<Reserva> reservas;

}
