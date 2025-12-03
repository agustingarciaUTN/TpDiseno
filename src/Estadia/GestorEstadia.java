package Estadia;

public class GestorEstadia {


    // 1. La única instancia (static y private)
    private static GestorEstadia instancia;

    // Referencias a los DAO que necesita
    private final DaoInterfazEstadia daoEstadia;

    // 2. Constructor PRIVADO
    // Nadie puede hacer new GestorEstadia() desde fuera.
    private GestorEstadia() {
        //Obtenemos las instancias de los DAO
        this.daoEstadia = DaoEstadia.getInstance();
    }

    // 3. Método de Acceso Global (Synchronized para seguridad en hilos)
    public static synchronized GestorEstadia getInstance() {
        if (instancia == null) {
            instancia = new GestorEstadia();
        }
        return instancia;
    }


    public boolean estaOcupadaEnFecha(String nroHabitacion, java.util.Date fechaInicio, java.util.Date fechaFin) {
        return daoEstadia.hayEstadiaEnFecha(nroHabitacion, fechaInicio, fechaFin);
    }
}