package Reserva;

import Habitacion.DaoHabitacion;
import Habitacion.DaoInterfazHabitacion;

public class GestorReserva {
    // 1. La única instancia (static y private)
    private static GestorReserva instancia;

    // Referencias a los DAOs que este gestor necesita
    private final DaoInterfazReserva daoReserva;
    private final DaoInterfazHabitacion daoHabitacion; // Ejemplo si necesita validar habitación

    // 2. Constructor PRIVADO
    // Nadie puede hacer new GestorReserva() desde fuera.
    private GestorReserva() {
        // Obtenemos las instancias de los DAOs
        this.daoReserva = DaoReserva.getInstance();
        this.daoHabitacion = DaoHabitacion.getInstance();
    }

    // 3. Método de Acceso Global (Synchronized para seguridad en hilos)
    public static synchronized GestorReserva getInstance() {
        if (instancia == null) {
            instancia = new GestorReserva();
        }
        return instancia;
    }
}
