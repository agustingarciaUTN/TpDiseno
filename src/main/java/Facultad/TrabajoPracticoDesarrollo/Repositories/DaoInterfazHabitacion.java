package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Habitacion;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoHabitacion;

import java.util.ArrayList;

public interface DaoInterfazHabitacion {
    // CRUD
    boolean persistirHabitacion(Habitacion h);
    boolean modificarHabitacion(Habitacion h);
    boolean eliminarHabitacion(String numero);
    DtoHabitacion obtenerPorNumero(String numero);
    ArrayList<DtoHabitacion> obtenerTodas();

}