package Estadia;

import Dominio.Estadia;
import Excepciones.PersistenciaException;
import Huesped.DtoHuesped;
import Utils.Mapear.MapearEstadia;

import java.util.Date;
import java.util.List;

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
    public List<DtoEstadia> buscarEstadiasEnFecha(Date inicio, Date fin) {
        return ((DaoEstadia) daoEstadia).obtenerEstadiasEnPeriodo(inicio, fin);
    }
    /**
     * CU15: Crear Estadía (con validación de roles)
     */
    public void crearEstadia(DtoEstadia dtoEstadia) throws Exception {

        List<DtoHuesped> huespedes = dtoEstadia.getDtoHuespedes();

        // Validación básica
        if (huespedes == null || huespedes.isEmpty()) {
            throw new IllegalArgumentException("La estadía debe tener al menos un huésped asignado (El Responsable).");
        }

        // --- VALIDACIÓN DE REGLA DE NEGOCIO ---
        // Iteramos desde 1 porque el 0 es el Responsable que SÍ puede repetir habitación.
        // Los acompañantes >= 1 NO pueden estar en otra habitación simultáneamente.

        for (int i = 1; i < huespedes.size(); i++) {
            DtoHuesped acompanantes = huespedes.get(i);

            boolean estaOcupado = daoEstadia.esHuespedActivo(
                    acompanantes.getTipoDocumento().name(),
                    acompanantes.getNroDocumento(),
                    dtoEstadia.getFechaCheckIn(),
                    dtoEstadia.getFechaCheckOut()
            );

            if (estaOcupado) {
                throw new Exception("El acompañante " + acompanantes.getApellido() + " " + acompanantes.getNombres() +
                        " (" + acompanantes.getNroDocumento() + ") ya figura alojado en otra habitación en estas fechas.");
            }
        }
        // Mapeo y Persistencia
        Estadia estadiaEntidad = MapearEstadia.mapearDtoAEntidad(dtoEstadia);
        boolean exito = daoEstadia.persistirEstadia(estadiaEntidad);

        if (!exito) {
            throw new PersistenciaException("Error crítico al guardar la estadía en base de datos.", null);
        }
    }
}