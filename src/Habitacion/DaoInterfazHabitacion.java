package Habitacion;
import Dominio.Habitacion;
import java.util.ArrayList;

public interface DaoInterfazHabitacion {
    // CRUD
    boolean persistirHabitacion(Habitacion h);
    boolean modificarHabitacion(Habitacion h);
    boolean eliminarHabitacion(String numero);
    DtoHabitacion obtenerPorNumero(String numero);

    // Extras
    // ArrayList<Habitacion> obtenerDisponibles(Date fechaDesde, Date fechaHasta);
}