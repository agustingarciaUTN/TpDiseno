package Facultad.TrabajoPracticoDesarrollo.Services;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Habitacion;
import Facultad.TrabajoPracticoDesarrollo.Repositories.HabitacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class HabitacionService {

    private final HabitacionRepository habitacionRepository;

    @Autowired
    public HabitacionService(HabitacionRepository habitacionRepository) {
        this.habitacionRepository = habitacionRepository;
    }

    /**
     * Retorna todas las habitaciones ordenadas por Tipo (según el Enum) y luego por Número.
     */
    @Transactional(readOnly = true)
    public List<Habitacion> obtenerTodas() {
        List<Habitacion> habitaciones = habitacionRepository.findAll();

        // Ordenamiento en memoria para respetar el orden del Enum (Ordinal)
        habitaciones.sort(Comparator.comparing(Habitacion::getTipoHabitacion)
                .thenComparing(Habitacion::getNumero));

        return habitaciones;
    }

    @Transactional(readOnly = true)
    public Habitacion obtenerPorNumero(String numero) {
        return habitacionRepository.findById(numero).orElse(null);
    }

    /**
     * Valida la coherencia de fechas para las búsquedas (Lógica de negocio pura).
     * No accede a la BD, solo verifica reglas.
     */
    public boolean validarRangoFechas(Date inicio, Date fin) {
        if (inicio == null || fin == null) return false;

        if (inicio.after(fin)) {
            System.out.println("Error: La fecha de fin debe ser posterior a la de inicio.");
            return false;
        }

        // Regla: No permitir rangos mayores a 60 días para la grilla visual
        long diferencia = Math.abs(fin.getTime() - inicio.getTime());
        long dias = TimeUnit.DAYS.convert(diferencia, TimeUnit.MILLISECONDS);

        if (dias > 60) {
            System.out.println("Error: El rango de visualización no puede superar los 60 días.");
            return false;
        }
        return true;
    }
}