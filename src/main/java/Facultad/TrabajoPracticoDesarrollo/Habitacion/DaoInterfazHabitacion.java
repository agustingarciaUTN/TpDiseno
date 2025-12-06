package Facultad.TrabajoPracticoDesarrollo.Habitacion;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Habitacion;

import java.util.ArrayList;

public interface DaoInterfazHabitacion {
    // CRUD
    boolean persistirHabitacion(Habitacion h);
    boolean modificarHabitacion(Habitacion h);
    boolean eliminarHabitacion(String numero);
    DtoHabitacion obtenerPorNumero(String numero);
    ArrayList<DtoHabitacion> obtenerTodas();

}