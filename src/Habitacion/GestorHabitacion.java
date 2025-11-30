package Habitacion;

public class GestorHabitacion {

    // 1. La única instancia (static y private)
    private static GestorHabitacion instancia;

    // Referencias a los DAOs que necesita
    private final DaoInterfazHabitacion daoHabitacion;

    // 2. Constructor PRIVADO
    // Nadie puede hacer new GestorHabitacion() desde fuera.
    private GestorHabitacion() {
        //Obtenemos las instancias de los DAOs
        this.daoHabitacion = DaoHabitacion.getInstance();
    }

    // 3. Método de Acceso Global (Synchronized para seguridad en hilos)
    public static synchronized GestorHabitacion getInstance() {
        if (instancia == null) {
            instancia = new GestorHabitacion();
        }
        return instancia;
    }
}
