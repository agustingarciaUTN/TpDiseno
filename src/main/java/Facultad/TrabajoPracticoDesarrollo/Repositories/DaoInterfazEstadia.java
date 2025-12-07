package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Estadia;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoEstadia;
import Facultad.TrabajoPracticoDesarrollo.Excepciones.PersistenciaException;

import java.util.ArrayList;
import java.util.Date;

public interface DaoInterfazEstadia {
    boolean persistirEstadia(Estadia estadia) throws PersistenciaException;
    boolean modificarEstadia(Estadia estadia) throws PersistenciaException;
    boolean eliminarEstadia(int idEstadia);
    DtoEstadia obtenerEstadiaPorId(int idEstadia);
    ArrayList<DtoEstadia> obtenerTodasLasEstadias();
    ArrayList<DtoEstadia> obtenerEstadiasEnPeriodo(Date inicio, Date fin);
    boolean hayEstadiaEnFecha(String numeroHabitacion, Date fechaInicial, Date fechaFin);
    //Validar si una persona ya está alojada (para acompañantes)
    boolean esHuespedActivo(String tipoDoc, String nroDoc, Date fechaInicio, Date fechaFin);


    // Obtener estadías de una reserva específica
    //ArrayList<Estadia> obtenerPorReserva(int dtoReserva);
    // Obtener estadías activas (sin fecha fin)
   // ArrayList<Estadia> obtenerEstadiasActivas();

}