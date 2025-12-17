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

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Inventario de Habitaciones.
 * Provee la lista de qué habitaciones tenemos disponibles para vender.
 */
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

    @Transactional(readOnly = true)
    public List<Habitacion> obtenerTodas() {
        List<Habitacion> habitaciones = habitacionRepository.findAll();
        habitaciones.sort(Comparator.comparing(Habitacion::getTipoHabitacion)
                .thenComparing(Habitacion::getNumero));
        return habitaciones;
    }

    @Transactional(readOnly = true)
    public Habitacion obtenerPorNumero(String numero) {
        return habitacionRepository.findById(numero).orElse(null);
    }

    public boolean validarRangoFechas(Date inicio, Date fin) {
        if (inicio == null || fin == null) return false;
        if (inicio.after(fin)) return false;
        long diferencia = Math.abs(fin.getTime() - inicio.getTime());
        long dias = TimeUnit.DAYS.convert(diferencia, TimeUnit.MILLISECONDS);
        return dias <= 60;
    }

    /**
     * Genera el reporte completo para la pantalla de "Estado de Habitaciones".
     * Ahora devuelve un objeto detallado por día con metadata de reservas.
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> obtenerEstadoPorFechas(String fechaDesdeStr, String fechaHastaStr) {
        try {
            LocalDate fechaDesde = LocalDate.parse(fechaDesdeStr);
            LocalDate fechaHasta = LocalDate.parse(fechaHastaStr);
            
            // Convertir a Date para los repositorios
            Date inicio = convertToDate(fechaDesde);
            Date fin = convertToDate(fechaHasta);
            
            // Buscar reservas y estadías que toquen el rango
            List<Reserva> reservasActivas = reservaRepository.buscarReservasActivasEnRango(inicio, fin);
            List<Estadia> estadiasActivas = estadiaRepository.buscarEstadiasEnRango(inicio, fin);
            
            List<Habitacion> todasHabitaciones = obtenerTodas();
            
            List<Map<String, Object>> resultado = new ArrayList<>();
            
            for (Habitacion hab : todasHabitaciones) {
                Map<String, Object> habInfo = new HashMap<>();
                habInfo.put("numero", hab.getNumero());
                habInfo.put("tipoHabitacion", hab.getTipoHabitacion() != null ? hab.getTipoHabitacion().toString() : "DESCONOCIDO");
                habInfo.put("capacidad", hab.getCapacidad());
                habInfo.put("costoPorNoche", hab.getCostoPorNoche());
                
                // Calcular estado detallado para cada día
                Map<String, Object> estadosPorDia = new HashMap<>();
                LocalDate diaActual = fechaDesde;
                
                while (!diaActual.isAfter(fechaHasta)) {
                    Map<String, Object> estadoDetalle = determinarEstadoDetallado(hab, diaActual, reservasActivas, estadiasActivas);
                    estadosPorDia.put(diaActual.toString(), estadoDetalle);
                    diaActual = diaActual.plusDays(1);
                }
                
                habInfo.put("estadosPorDia", estadosPorDia);
                
                // Estado general (compatible con versiones simples)
                Map<String, Object> estadoHoy = (Map<String, Object>) estadosPorDia.get(fechaDesde.toString());
                habInfo.put("estadoHabitacion", estadoHoy.get("estado"));
                
                resultado.add(habInfo);
            }
            
            return resultado;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al calcular estados: " + e.getMessage(), e);
        }
    }

    // --- MÉTODOS PRIVADOS ---

    /**
     * Determina el estado y devuelve un mapa con metadata (ids, fechas reales, etc.)
     */
    private Map<String, Object> determinarEstadoDetallado(
            Habitacion hab, 
            LocalDate fecha,
            List<Reserva> reservasActivas,
            List<Estadia> estadiasActivas
    ) {
        Map<String, Object> detalle = new HashMap<>();
        
        // 1. Verificar estado físico primero (Mantenimiento)
        if (hab.getEstadoHabitacion() != null && 
            hab.getEstadoHabitacion().getDescripcion().contains("FUERA")) {
            detalle.put("estado", "MANTENIMIENTO");
            return detalle;
        }
        
        // 2. Verificar si hay estadía activa (OCUPADA)
        Estadia estadiaFound = estadiasActivas.stream()
            .filter(e -> {
                if (e.getHabitacion() == null || !e.getHabitacion().getNumero().equals(hab.getNumero())) return false;
                if (e.getFechaCheckIn() == null) return false;
                
                LocalDate checkIn = convertToLocalDate(e.getFechaCheckIn());
                LocalDate checkOut = e.getFechaCheckOut() != null ? 
                    convertToLocalDate(e.getFechaCheckOut()) : fecha.plusYears(1); // Si no tiene checkout, asumimos ocupada indefinidamente por ahora
                    
                return !fecha.isBefore(checkIn) && fecha.isBefore(checkOut);
            })
            .findFirst()
            .orElse(null);
        
        if (estadiaFound != null) {
            detalle.put("estado", "OCUPADA");
            detalle.put("idEstadia", estadiaFound.getIdEstadia());
            return detalle;
        }
        
        // 3. Verificar si hay reserva activa (RESERVADA)
        Reserva reservaFound = reservasActivas.stream()
            .filter(r -> {
                if (r.getHabitacion() == null || !r.getHabitacion().getNumero().equals(hab.getNumero())) return false;
                if (r.getFechaDesde() == null || r.getFechaHasta() == null) return false;
                
                LocalDate desde = convertToLocalDate(r.getFechaDesde());
                LocalDate hasta = convertToLocalDate(r.getFechaHasta());
                
                // [desde, hasta) -> incluye inicio, excluye fin (check-out)
                return !fecha.isBefore(desde) && fecha.isBefore(hasta);
            })
            .findFirst()
            .orElse(null);
        
        if (reservaFound != null) {
            detalle.put("estado", "RESERVADA");
            detalle.put("idReserva", reservaFound.getIdReserva());
            // Enviamos fechas reales para validación en frontend
            detalle.put("fechaInicio", reservaFound.getFechaDesde().toString()); 
            detalle.put("fechaFin", reservaFound.getFechaHasta().toString());
            return detalle;
        }
        
        // 4. Si no tiene nada, está disponible
        detalle.put("estado", "DISPONIBLE");
        return detalle;
    }

    private LocalDate convertToLocalDate(Date date) {
        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate();
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private Date convertToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}