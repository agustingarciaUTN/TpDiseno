package Habitacion;

import Dominio.Habitacion;
import Utils.Mapear.MapearHabitacion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class GestorHabitacion {

    // 1. La única instancia (static y private)
    private static GestorHabitacion instancia;

    // Referencias a los DAO que necesita
    private final DaoInterfazHabitacion daoHabitacion;

    // 2. Constructor PRIVADO
    // Nadie puede hacer new GestorHabitacion() desde fuera.
    private GestorHabitacion() {
        //Obtenemos las instancias de los DAO
        this.daoHabitacion = DaoHabitacion.getInstance();
    }

    // 3. Metodo de acceso global, synchronized para guardar peligro por multihilos
    public static synchronized GestorHabitacion getInstance() {
        if (instancia == null) {
            instancia = new GestorHabitacion();
        }
        return instancia;
    }

    public ArrayList<Habitacion> obtenerTodasLasHabitacionesOrdenadas() {
        ArrayList<DtoHabitacion> entidades = daoHabitacion.obtenerTodas();
        ArrayList<Habitacion> dtos = new ArrayList<>();

        // Aca se está creando la entidad habitación y asignándole todos los valores del dto.
        for (DtoHabitacion h : entidades) {
            dtos.add(MapearHabitacion.mapearDtoAEntidad(h));
        }

        // ORDENAMIENTO:
        // 1. Por Tipo de Habitación (según el orden del Enum)
        // 2. Por Número de Habitación (para que dentro de la misma categoría queden ordenadas)
        dtos.sort(Comparator.comparing(Habitacion::getTipoHabitacion)
                .thenComparing(Habitacion::getNumero));

        return dtos;
    }

    public Habitacion obtenerHabitacionPorNumero(String numero) {
        //Obtenemos el dto. Y mapeamos a entidad para devolverla
        return MapearHabitacion.mapearDtoAEntidad(daoHabitacion.obtenerPorNumero(numero)); // Placeholder
    }
    public ArrayList<Habitacion> obtenerTodas(){
        ArrayList<DtoHabitacion> listaDtoHabitaciones = daoHabitacion.obtenerTodas();
        ArrayList<Habitacion> listaEntidadesHabitaciones = new ArrayList<>();
        for (DtoHabitacion dto : listaDtoHabitaciones){
            listaEntidadesHabitaciones.add(MapearHabitacion.mapearDtoAEntidad(dto));
        }
        return listaEntidadesHabitaciones;
    }

    public boolean validarRangoFechas(Date inicio, Date fin) throws IllegalArgumentException {
        // 1. Validar Coherencia
        if (inicio.after(fin)) {
            System.out.println("La fecha de fin debe ser anterior a la fecha de inicio.");
            System.out.println("Por favor reingrese ambas fechas.");
            return false;
        }
        // 2. Validar Duración Máxima
        long diferenciaMilisec = Math.abs(fin.getTime() - inicio.getTime());
        long dias = TimeUnit.DAYS.convert(diferenciaMilisec, TimeUnit.MILLISECONDS);

        if (dias > 60) {
            System.out.println("El rango de fechas no puede superar los 60 días (demasiado grande para mostrar).");
            System.out.println("Por favor reingrese ambas fechas.");
            return false;
        }
        return true;
    }



}
