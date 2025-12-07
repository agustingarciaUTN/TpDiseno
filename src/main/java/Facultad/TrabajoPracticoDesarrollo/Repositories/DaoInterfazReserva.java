package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Reserva;
import Facultad.TrabajoPracticoDesarrollo.Excepciones.PersistenciaException;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoReserva;

import java.util.ArrayList;

public interface DaoInterfazReserva {
    boolean persistirReserva(Reserva reserva) throws PersistenciaException;
    boolean modificarReserva(Reserva reserva) throws PersistenciaException;
    boolean eliminarReserva(int id);
    DtoReserva obtenerPorId(int id);
    ArrayList<DtoReserva> obtenerTodas();
    ArrayList<DtoReserva> obtenerReservasEnPeriodo(java.util.Date inicio, java.util.Date fin);
    boolean hayReservaEnFecha(String numeroHabitacion,java.util.Date fechaInicial, java.util.Date fechaFin);
}