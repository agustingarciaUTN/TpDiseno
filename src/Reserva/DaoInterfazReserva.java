package Reserva;
import Dominio.Reserva;
import Excepciones.PersistenciaException;
import java.util.ArrayList;

public interface DaoInterfazReserva {
    boolean persistirReserva(Reserva reserva) throws PersistenciaException;
    boolean modificarReserva(Reserva reserva) throws PersistenciaException;
    boolean eliminarReserva(int id);
    DtoReserva obtenerPorId(int id);
    ArrayList<DtoReserva> obtenerTodas();
}