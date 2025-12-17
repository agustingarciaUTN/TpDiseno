package Facultad.TrabajoPracticoDesarrollo.Services;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Habitacion;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Reserva;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Estadia;
import Facultad.TrabajoPracticoDesarrollo.Repositories.HabitacionRepository;
// Quitamos Repositorios de Reserva/Estadía e importamos Servicios
import Facultad.TrabajoPracticoDesarrollo.Services.ReservaService;
import Facultad.TrabajoPracticoDesarrollo.Services.EstadiaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
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
    
    // Inyectamos Servicios en lugar de Repositorios
    private final ReservaService reservaService;
    private final EstadiaService estadiaService;

    @Autowired
    public HabitacionService(
            HabitacionRepository habitacionRepository,
            ReservaService reservaService,
            EstadiaService estadiaService
    ) {
        this.habitacionRepository = habitacionRepository;
        this.reservaService = reservaService;
        this.estadiaService = estadiaService;
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
     * Ahora devuelve información detallada (Map) y delega la búsqueda a los servicios.
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> obtenerEstadoPorFechas(String fechaDesdeStr, String fechaHastaStr) {
        try {
            LocalDate fechaDesde = LocalDate.parse(fechaDesdeStr);
            LocalDate fechaHasta = LocalDate.parse(fechaHastaStr);
            
            Date inicio = convertToDate(fechaDesde);
            Date fin = convertToDate(fechaHasta);
            
            // CAMBIO: Delegamos la búsqueda a los servicios correspondientes
            List<Reserva> reservasActivas = reservaService.buscarReservasEnRango(inicio, fin);
            List<Estadia> estadiasActivas = estadiaService.buscarEstadiasEnRango(inicio, fin);
            
            List<Habitacion> todasHabitaciones = obtenerTodas();
            List<Map<String, Object>> resultado = new ArrayList<>();
            
            for (Habitacion hab : todasHabitaciones) {
                Map<String, Object> habInfo = new HashMap<>();
                habInfo.put("numero", hab.getNumero());
                habInfo.put("tipoHabitacion", hab.getTipoHabitacion() != null ? hab.getTipoHabitacion().toString() : "DESCONOCIDO");
                habInfo.put("capacidad", hab.getCapacidad());
                habInfo.put("costoPorNoche", hab.getCostoPorNoche());
                
                Map<String, Object> estadosPorDia = new HashMap<>();
                LocalDate diaActual = fechaDesde;
                
                while (!diaActual.isAfter(fechaHasta)) {
                    // Usamos la lógica detallada para el frontend nuevo
                    Map<String, Object> estadoDetalle = determinarEstadoDetallado(hab, diaActual, reservasActivas, estadiasActivas);
                    estadosPorDia.put(diaActual.toString(), estadoDetalle);
                    diaActual = diaActual.plusDays(1);
                }
                
                habInfo.put("estadosPorDia", estadosPorDia);
                // Estado general para compatibilidad
                Map<String, Object> infoHoy = (Map<String, Object>) estadosPorDia.get(fechaDesde.toString());
                habInfo.put("estadoHabitacion", infoHoy.get("estado"));
                
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
     * Determina el estado detallado (ID, Fechas, Estado) para el frontend.
     */
    private Map<String, Object> determinarEstadoDetallado(
            Habitacion hab, 
            LocalDate fecha,
            List<Reserva> reservasActivas,
            List<Estadia> estadiasActivas
    ) {
        Map<String, Object> detalle = new HashMap<>();
        
        // 1. Mantenimiento
        if (hab.getEstadoHabitacion() != null && 
            hab.getEstadoHabitacion().getDescripcion().contains("FUERA")) {
            detalle.put("estado", "MANTENIMIENTO");
            return detalle;
        }
        
        // 2. Estadía (OCUPADA)
        Estadia estadiaFound = estadiasActivas.stream()
            .filter(e -> {
                if (e.getHabitacion() == null || !e.getHabitacion().getNumero().equals(hab.getNumero())) return false;
                if (e.getFechaCheckIn() == null) return false;
                
                LocalDate checkIn = convertToLocalDate(e.getFechaCheckIn());
                LocalDate checkOut = e.getFechaCheckOut() != null ? 
                    convertToLocalDate(e.getFechaCheckOut()) : fecha.plusYears(1);
                    
                return !fecha.isBefore(checkIn) && fecha.isBefore(checkOut);
            })
            .findFirst()
            .orElse(null);
        
        if (estadiaFound != null) {
            detalle.put("estado", "OCUPADA");
            detalle.put("idEstadia", estadiaFound.getIdEstadia());
            return detalle;
        }
        
        // 3. Reserva (RESERVADA)
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
            detalle.put("fechaInicio", reservaFound.getFechaDesde().toString()); 
            detalle.put("fechaFin", reservaFound.getFechaHasta().toString());
            return detalle;
        }
        
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