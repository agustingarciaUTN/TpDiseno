package Estadia;
import Dominio.Estadia;
import Excepciones.PersistenciaException;
import java.util.ArrayList;

public interface DaoInterfazEstadia {
    boolean persistirEstadia(Estadia estadia) throws PersistenciaException;
    boolean modificarEstadia(Estadia estadia) throws PersistenciaException;
    boolean eliminarEstadia(int idEstadia);
    DtoEstadia obtenerEstadiaPorId(int idEstadia);
    ArrayList<DtoEstadia> obtenerTodasLasEstadias();

    boolean hayEstadiaEnFecha(String numeroHabitacion, java.util.Date fecha);

    /*
    // Obtener estadías de una reserva específica
    ArrayList<Estadia> obtenerPorReserva(int dtoReserva);
    // Obtener estadías activas (sin fecha fin)
    ArrayList<Estadia> obtenerEstadiasActivas();
    */
}