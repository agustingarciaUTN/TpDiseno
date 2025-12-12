package Facultad.TrabajoPracticoDesarrollo.Services;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Habitacion;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Reserva;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Estadia;
import Facultad.TrabajoPracticoDesarrollo.Repositories.HabitacionRepository;
import Facultad.TrabajoPracticoDesarrollo.Repositories.ReservaRepository;
import Facultad.TrabajoPracticoDesarrollo.Repositories.EstadiaRepository;
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
    private final ReservaRepository reservaRepository;
    private final EstadiaRepository estadiaRepository;

    @Autowired
    public HabitacionService(
            HabitacionRepository habitacionRepository,
            ReservaRepository reservaRepository,
            EstadiaRepository estadiaRepository
    ) {
        this.habitacionRepository = habitacionRepository;
        this.reservaRepository = reservaRepository;
        this.estadiaRepository = estadiaRepository;
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

    /**
     * Obtiene el estado de todas las habitaciones para un rango de fechas específico
     * Considera: estado físico, reservas activas y estadías en curso
     */
    @Transactional(readOnly = true)
    public List<java.util.Map<String, Object>> obtenerEstadoPorFechas(String fechaDesdeStr, String fechaHastaStr) {
        try {
            System.out.println("Obteniendo estado para fechas: " + fechaDesdeStr + " a " + fechaHastaStr);
            
            java.time.LocalDate fechaDesde = java.time.LocalDate.parse(fechaDesdeStr);
            java.time.LocalDate fechaHasta = java.time.LocalDate.parse(fechaHastaStr);
            
            // Convertir a Date para las consultas
            Date inicio = convertToDate(fechaDesde);
            Date fin = convertToDate(fechaHasta);
            
            System.out.println("Buscando reservas activas en el rango...");
            // Obtener todas las reservas y estadías del rango
            List<Reserva> reservasActivas = reservaRepository.buscarReservasActivasEnRango(inicio, fin);
            System.out.println("Reservas encontradas: " + (reservasActivas != null ? reservasActivas.size() : 0));
            
            System.out.println("Buscando estadías activas en el rango...");
            List<Estadia> estadiasActivas = estadiaRepository.buscarEstadiasEnRango(inicio, fin);
            System.out.println("Estadías encontradas: " + (estadiasActivas != null ? estadiasActivas.size() : 0));
            
            List<Habitacion> todasHabitaciones = obtenerTodas();
            System.out.println("Total habitaciones: " + todasHabitaciones.size());
            
            List<java.util.Map<String, Object>> resultado = new java.util.ArrayList<>();
            
            for (Habitacion hab : todasHabitaciones) {
                java.util.Map<String, Object> habInfo = new java.util.HashMap<>();
                habInfo.put("numero", hab.getNumero());
                habInfo.put("tipoHabitacion", hab.getTipoHabitacion() != null ? hab.getTipoHabitacion().toString() : "DESCONOCIDO");
                habInfo.put("capacidad", hab.getCapacidad());
                habInfo.put("costoPorNoche", hab.getCostoPorNoche());
                
                // Calcular estado para cada día del rango
                java.util.Map<String, String> estadosPorDia = new java.util.HashMap<>();
                java.time.LocalDate diaActual = fechaDesde;
                
                while (!diaActual.isAfter(fechaHasta)) {
                    String estado = determinarEstadoHabitacion(hab, diaActual, reservasActivas, estadiasActivas);
                    estadosPorDia.put(diaActual.toString(), estado);
                    diaActual = diaActual.plusDays(1);
                }
                
                habInfo.put("estadosPorDia", estadosPorDia);
                // Estado general (del primer día para compatibilidad)
                habInfo.put("estadoHabitacion", estadosPorDia.get(fechaDesde.toString()));
                
                resultado.add(habInfo);
            }
            
            System.out.println("Resultado final: " + resultado.size() + " habitaciones procesadas con estados por día");
            return resultado;
        } catch (Exception e) {
            System.err.println("Error en obtenerEstadoPorFechas: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al calcular estados: " + e.getMessage(), e);
        }
    }
    
    private String determinarEstadoHabitacion(
            Habitacion hab, 
            java.time.LocalDate fecha,
            List<Reserva> reservasActivas,
            List<Estadia> estadiasActivas
    ) {
        // 1. Verificar estado físico primero
        if (hab.getEstadoHabitacion() != null && 
            hab.getEstadoHabitacion().getDescripcion().contains("FUERA")) {
            return "MANTENIMIENTO";
        }
        
        // 2. Verificar si hay estadía activa (OCUPADA)
        boolean tieneEstadia = estadiasActivas.stream()
            .anyMatch(e -> {
                if (e.getHabitacion() == null || !e.getHabitacion().getNumero().equals(hab.getNumero())) {
                    return false;
                }
                if (e.getFechaCheckIn() == null) return false;
                
                java.time.LocalDate checkIn = convertToLocalDate(e.getFechaCheckIn());
                java.time.LocalDate checkOut = e.getFechaCheckOut() != null ? 
                    convertToLocalDate(e.getFechaCheckOut()) : fecha.plusYears(1);
                    
                return !fecha.isBefore(checkIn) && fecha.isBefore(checkOut);
            });
        
        if (tieneEstadia) return "OCUPADA";
        
        // 3. Verificar si hay reserva activa (RESERVADA)
        boolean tieneReserva = reservasActivas.stream()
            .anyMatch(r -> {
                if (r.getHabitacion() == null || !r.getHabitacion().getNumero().equals(hab.getNumero())) {
                    return false;
                }
                if (r.getFechaDesde() == null || r.getFechaHasta() == null) return false;
                
                java.time.LocalDate desde = convertToLocalDate(r.getFechaDesde());
                java.time.LocalDate hasta = convertToLocalDate(r.getFechaHasta());
                
                return !fecha.isBefore(desde) && fecha.isBefore(hasta);
            });
        
        if (tieneReserva) return "RESERVADA";
        
        // 4. Si no tiene nada, está disponible
        return "DISPONIBLE";
    }
    
    private java.time.LocalDate convertToLocalDate(Date date) {
        // Manejar tanto java.util.Date como java.sql.Date
        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate();
        }
        return date.toInstant()
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate();
    }
    
    private Date convertToDate(java.time.LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
    }
}