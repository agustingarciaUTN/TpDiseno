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

public Habitacion(TipoHabitacion tipoHabitacion, int capacidad, String numero) {
    this.tipoHabitacion = tipoHabitacion;
    this.capacidad = capacidad;
    this.numero = numero;
}

//setters y getters
public String getNumero() {
    return numero;
}
public void setNumero(String numero) {
    this.numero = numero;
}
public TipoHabitacion getTipoHabitacion() {
    return tipoHabitacion;
}
public void setTipoHabitacion(TipoHabitacion tipoHabitacion) {
    this.tipoHabitacion = tipoHabitacion;
}
public int getCapacidad() {
    return capacidad;
}
public void setCapacidad(int capacidad) {
    this.capacidad = capacidad;
}
public EstadoHabitacion getEstadoHabitacion() {
    return estadoHabitacion;
}
public void setEstadoHabitacion(EstadoHabitacion estadoHabitacion) {
    this.estadoHabitacion = estadoHabitacion;
}
public float getCostoPorNoche() {
    return costoPorNoche;
}
public void setCostoPorNoche(float costoPorNoche) {
    this.costoPorNoche = costoPorNoche;
}
public ArrayList<Reserva> getReservas() {
    return reservas;
}
public void setReservas(ArrayList<Reserva> reservas) {
    this.reservas = reservas;
} 

}
