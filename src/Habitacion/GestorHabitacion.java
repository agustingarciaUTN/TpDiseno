package Habitacion;

import Dominio.Habitacion;
import Utils.Mapear.MapearHabitacion;
import enums.EstadoHabitacion;

import java.util.ArrayList;
import java.util.Date;

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





}
