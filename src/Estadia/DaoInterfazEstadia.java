package Estadia;
import Dominio.Estadia;
import Excepciones.PersistenciaException;
import java.util.ArrayList;
import java.util.Date;

public interface DaoInterfazEstadia {
    boolean persistirEstadia(Estadia estadia) throws PersistenciaException;
    boolean modificarEstadia(Estadia estadia) throws PersistenciaException;
    boolean eliminarEstadia(int idEstadia);
    DtoEstadia obtenerEstadiaPorId(int idEstadia);
    ArrayList<DtoEstadia> obtenerTodasLasEstadias();
    ArrayList<DtoEstadia> obtenerEstadiasEnPeriodo(java.util.Date inicio, java.util.Date fin);
    boolean hayEstadiaEnFecha(String numeroHabitacion, java.util.Date fechaInicial, java.util.Date fechaFin);
    //Validar si una persona ya está alojada (para acompañantes)
    boolean esHuespedActivo(String tipoDoc, String nroDoc, Date fechaInicio, Date fechaFin);


    // Obtener estadías de una reserva específica
    //ArrayList<Estadia> obtenerPorReserva(int dtoReserva);
    // Obtener estadías activas (sin fecha fin)
   // ArrayList<Estadia> obtenerEstadiasActivas();

}