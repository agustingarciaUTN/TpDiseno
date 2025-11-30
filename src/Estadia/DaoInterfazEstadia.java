package Estadia;
import Dominio.Estadia;
import Excepciones.PersistenciaException;
import java.util.ArrayList;

public interface DaoInterfazEstadia {
    boolean persistirEstadia(Estadia estadia) throws PersistenciaException;
    boolean modificarEstadia(Estadia estadia) throws PersistenciaException;
    boolean eliminarEstadia(int idEstadia);
    Estadia obtenerEstadiaPorId(int idEstadia);
    ArrayList<Estadia> obtenerTodasLasEstadias();

    /*
    // Obtener estadías de una reserva específica
    ArrayList<Estadia> obtenerPorReserva(int idReserva);
    // Obtener estadías activas (sin fecha fin)
    ArrayList<Estadia> obtenerEstadiasActivas();
    */
}