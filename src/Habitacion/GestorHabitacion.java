package Habitacion;

import Dominio.Habitacion;
import Utils.Mapear.MapearHabitacion;
import enums.EstadoHabitacion;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
        // Referencia al mapearHabitacion
        MapearHabitacion mapearHabitacion = new MapearHabitacion();
    }

    // 3. Método de Acceso Global (Synchronized para seguridad en hilos)
    public static synchronized GestorHabitacion getInstance() {
        if (instancia == null) {
            instancia = new GestorHabitacion();
        }
        return instancia;
    }

    public ArrayList<Habitacion> obtenerTodasLasHabitaciones() {

        ArrayList<DtoHabitacion> habitacionesDtoEncontradas =  daoHabitacion.obtenerTodas();
        ArrayList<Habitacion> listaHabitaciones = new ArrayList<>();
        for(int i = 0 ; i < habitacionesDtoEncontradas.size() ; i++){
            listaHabitaciones.add(i, MapearHabitacion.mapearDtoAEntidad(habitacionesDtoEncontradas.get(i)));
        }
        return listaHabitaciones;
    }

    public Habitacion obtenerHabitacionPorNumero(String numero) {
        //Obtenemos el dto y mapeamos a entidad para devolver
        return MapearHabitacion.mapearDtoAEntidad(daoHabitacion.obtenerPorNumero(numero)); // Placeholder
    }

    public void validarRangoFechas(Date inicio, Date fin) throws IllegalArgumentException {
        // 1. Validar Nulos
        if (inicio == null || fin == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son obligatorias.");
        }

        // 2. Validar Orden Cronológico
        if (inicio.after(fin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        // 3. Validar Duración Máxima (Regla de Negocio opcional pero recomendada para UI)
        // Calculamos la diferencia en días
        long diferenciaMillies = Math.abs(fin.getTime() - inicio.getTime());
        long dias = TimeUnit.DAYS.convert(diferenciaMillies, TimeUnit.MILLISECONDS);

        if (dias > 60) {
            throw new IllegalArgumentException("El rango de fechas no puede superar los 60 días (demasiado grande para mostrar).");
        }
    }



}
