package Facultad.pruebas.Services;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Estadia;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Habitacion;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Reserva;
import Facultad.TrabajoPracticoDesarrollo.Repositories.HabitacionRepository;
import Facultad.TrabajoPracticoDesarrollo.Services.EstadiaService;
import Facultad.TrabajoPracticoDesarrollo.Services.HabitacionService;
import Facultad.TrabajoPracticoDesarrollo.Services.ReservaService;
import Facultad.TrabajoPracticoDesarrollo.enums.EstadoHabitacion;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoHabitacion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Clase de pruebas unitarias para {@link HabitacionService}.
 * <p>
 * Cubre la lógica de negocio del Caso de Uso:
 * <ul>
 * <li><b>CU05:</b> Mostrar Estado de Habitaciones (Grilla de disponibilidad).</li>
 * </ul>
 * Verifica la correcta integración lógica de Reservas y Estadías para determinar
 * el estado (Libre, Ocupada, Reservada) en un rango de fechas.
 */
@ExtendWith(MockitoExtension.class)
class HabitacionServiceTest {

    @Mock
    private HabitacionRepository habitacionRepository;

    @Mock
    private ReservaService reservaService;

    @Mock
    private EstadiaService estadiaService;

    @InjectMocks
    private HabitacionService habitacionService;

    // ============================================================================================
    // GENERACIÓN DE GRILLA (CU05)
    // ============================================================================================

    /**
     * <b>Caso de Prueba: Grilla Vacía (Todo Disponible)</b>
     * <p>
     * Verifica que si no hay reservas ni estadías, todas las habitaciones aparezcan como DISPONIBLES.
     */
    @Test
    @DisplayName("CU05 - Grilla: Debe mostrar DISPONIBLE si no hay ocupación")
    void obtenerEstadoPorFechas_TodoDisponible() {
        // ARRANGE
        String fechaInicioStr = LocalDate.now().toString();
        String fechaFinStr = LocalDate.now().plusDays(1).toString();

        // 1. Mock Habitaciones
        Habitacion hab101 = crearHabitacion("101", EstadoHabitacion.HABILITADA);
        when(habitacionRepository.findAll()).thenReturn(List.of(hab101));

        // 2. Mock Servicios vacíos (Nadie reservó ni ocupó)
        when(reservaService.buscarReservasEnRango(any(), any())).thenReturn(Collections.emptyList());
        when(estadiaService.buscarEstadiasEnRango(any(), any())).thenReturn(Collections.emptyList());

        // ACT
        List<Map<String, Object>> grilla = habitacionService.obtenerEstadoPorFechas(fechaInicioStr, fechaFinStr);

        // ASSERT
        assertFalse(grilla.isEmpty());
        Map<String, Object> filaHabitacion = grilla.get(0);
        assertEquals("101", filaHabitacion.get("numero"));

        // Verificamos el día de hoy
        Map<String, Object> dias = (Map<String, Object>) filaHabitacion.get("estadosPorDia");
        Map<String, Object> diaHoy = (Map<String, Object>) dias.get(fechaInicioStr);

        assertEquals("DISPONIBLE", diaHoy.get("estado"));
    }

    /**
     * <b>Caso de Prueba: Prioridad de Estados (Ocupada mata Reservada)</b>
     * <p>
     * Verifica la jerarquía visual:
     * 1. Mantenimiento (Fuera de Servicio)
     * 2. Ocupada (Estadía activa)
     * 3. Reservada
     * 4. Disponible
     */
    @Test
    @DisplayName("CU05 - Grilla: Debe detectar estado OCUPADA correctamente")
    void obtenerEstadoPorFechas_EstadoOcupada() {
        // ARRANGE
        LocalDate hoy = LocalDate.now();
        String fechaStr = hoy.toString();

        // 1. Habitación
        Habitacion hab = crearHabitacion("202", EstadoHabitacion.HABILITADA);
        when(habitacionRepository.findAll()).thenReturn(List.of(hab));

        // 2. Mock Estadía Activa hoy
        Estadia estadia = new Estadia();
        estadia.setIdEstadia(50);
        estadia.setHabitacion(hab);
        estadia.setFechaCheckIn(convertToDate(hoy.minusDays(1))); // Entró ayer
        // Sin fecha check-out (sigue ahí)

        when(estadiaService.buscarEstadiasEnRango(any(), any())).thenReturn(List.of(estadia));
        when(reservaService.buscarReservasEnRango(any(), any())).thenReturn(Collections.emptyList());

        // ACT
        List<Map<String, Object>> grilla = habitacionService.obtenerEstadoPorFechas(fechaStr, fechaStr);

        // ASSERT
        Map<String, Object> estadoDia = obtenerEstadoDelDia(grilla, 0, fechaStr);

        assertEquals("OCUPADA", estadoDia.get("estado"));
        assertEquals(50, estadoDia.get("idEstadia"));
    }

    @Test
    @DisplayName("CU05 - Grilla: Debe detectar estado RESERVADA correctamente")
    void obtenerEstadoPorFechas_EstadoReservada() {
        // ARRANGE
        LocalDate hoy = LocalDate.now();
        String fechaStr = hoy.toString();

        Habitacion hab = crearHabitacion("303", EstadoHabitacion.HABILITADA);
        when(habitacionRepository.findAll()).thenReturn(List.of(hab));

        // Mock Reserva Activa hoy
        Reserva reserva = new Reserva();
        reserva.setIdReserva(99);
        reserva.setHabitacion(hab);
        reserva.setFechaDesde(convertToDate(hoy));       // Desde hoy
        reserva.setFechaHasta(convertToDate(hoy.plusDays(2))); // Hasta pasado mañana

        when(reservaService.buscarReservasEnRango(any(), any())).thenReturn(List.of(reserva));
        when(estadiaService.buscarEstadiasEnRango(any(), any())).thenReturn(Collections.emptyList());

        // ACT
        List<Map<String, Object>> grilla = habitacionService.obtenerEstadoPorFechas(fechaStr, fechaStr);

        // ASSERT
        Map<String, Object> estadoDia = obtenerEstadoDelDia(grilla, 0, fechaStr);

        assertEquals("RESERVADA", estadoDia.get("estado"));
        assertEquals(99, estadoDia.get("idReserva"));
    }

    @Test
    @DisplayName("CU05 - Grilla: Debe priorizar MANTENIMIENTO sobre cualquier reserva")
    void obtenerEstadoPorFechas_EstadoMantenimiento() {
        // ARRANGE
        LocalDate hoy = LocalDate.now();
        String fechaStr = hoy.toString();

        // Habitación rota
        Habitacion hab = crearHabitacion("404", EstadoHabitacion.FUERA_DE_SERVICIO);
        when(habitacionRepository.findAll()).thenReturn(List.of(hab));

        // Aunque haya reserva (error de datos), el mantenimiento manda visualmente
        Reserva reserva = new Reserva();
        reserva.setHabitacion(hab);
        reserva.setFechaDesde(convertToDate(hoy));
        reserva.setFechaHasta(convertToDate(hoy.plusDays(1)));

        when(reservaService.buscarReservasEnRango(any(), any())).thenReturn(List.of(reserva));
        when(estadiaService.buscarEstadiasEnRango(any(), any())).thenReturn(Collections.emptyList());

        // ACT
        List<Map<String, Object>> grilla = habitacionService.obtenerEstadoPorFechas(fechaStr, fechaStr);

        // ASSERT
        Map<String, Object> estadoDia = obtenerEstadoDelDia(grilla, 0, fechaStr);
        assertEquals("MANTENIMIENTO", estadoDia.get("estado"));
    }

    // ============================================================================================
    // VALIDACIONES SIMPLES
    // ============================================================================================

    @Test
    @DisplayName("Validación: Rango de fechas coherente")
    void validarRangoFechas_Logica() {
        Date inicio = new Date();
        Date fin = new Date(inicio.getTime() + 86400000L); // Mañana

        // Caso Feliz
        assertTrue(habitacionService.validarRangoFechas(inicio, fin));

        // Caso Invertido (Inicio > Fin)
        assertFalse(habitacionService.validarRangoFechas(fin, inicio));

        // Caso Null
        assertFalse(habitacionService.validarRangoFechas(null, fin));

        // Caso Rango Gigante (> 60 días)
        Date muyLejos = new Date(inicio.getTime() + (65L * 24 * 60 * 60 * 1000));
        assertFalse(habitacionService.validarRangoFechas(inicio, muyLejos));
    }

    // ============================================================================================
    // HELPERS
    // ============================================================================================

    private Habitacion crearHabitacion(String numero, EstadoHabitacion estado) {
        Habitacion h = new Habitacion();
        h.setNumero(numero);
        h.setTipoHabitacion(TipoHabitacion.DOBLE_ESTANDAR);
        h.setEstadoHabitacion(estado);
        h.setCostoPorNoche(1000.00F);
        h.setCapacidad(2);
        return h;
    }

    private Date convertToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> obtenerEstadoDelDia(List<Map<String, Object>> grilla, int indexFila, String fechaKey) {
        Map<String, Object> fila = grilla.get(indexFila);
        Map<String, Object> dias = (Map<String, Object>) fila.get("estadosPorDia");
        return (Map<String, Object>) dias.get(fechaKey);
    }
}