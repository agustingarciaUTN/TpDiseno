package Facultad.pruebas.Services;

import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoReserva;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Estadia;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Habitacion;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Reserva;
import Facultad.TrabajoPracticoDesarrollo.Repositories.EstadiaRepository;
import Facultad.TrabajoPracticoDesarrollo.Repositories.ReservaRepository;
import Facultad.TrabajoPracticoDesarrollo.Services.ReservaService;
import Facultad.TrabajoPracticoDesarrollo.enums.EstadoReserva;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Clase de pruebas unitarias para {@link ReservaService}.
 * <p>
 * Cubre la lógica de negocio de los Casos de Uso:
 * <ul>
 * <li><b>CU04:</b> Reservar Habitación (Validaciones de solapamiento, fechas y documentos).</li>
 * <li><b>CU06:</b> Cancelar Reserva (Cambio de estados y búsquedas).</li>
 * </ul>
 * Utiliza Mockito para aislar la capa de servicio de la base de datos.
 */
@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private EstadiaRepository estadiaRepository;

    @InjectMocks
    private ReservaService reservaService;

    // ============================================================================================
    // SECCIÓN CU04: CREAR RESERVAS (Validaciones Críticas)
    // ============================================================================================

    /**
     * <b>Caso de Prueba: Camino Feliz (Happy Path)</b>
     * <p>
     * Verifica que si todos los datos son correctos y NO hay reservas previas en esas fechas,
     * el servicio llame al repositorio para guardar la nueva reserva.
     */
    @Test
    @DisplayName("CU04 - Éxito: Debería guardar la reserva si no hay conflictos de fechas")
    void crearReservas_Exito() throws Exception {
        // ARRANGE: Preparamos un DTO válido y simulamos que la habitación está libre
        DtoReserva dto = crearDtoValido();

        // Mock: Cuando el repo busque conflictos, devuelve 'false' (no hay nadie ocupando)
        when(reservaRepository.existeReservaEnFecha(anyString(), any(Date.class), any(Date.class)))
                .thenReturn(false);

        // ACT: Ejecutamos el método del servicio
        reservaService.crearReservas(List.of(dto));

        // ASSERT: Verificamos que se haya llamado a 'save' exactamente una vez
        verify(reservaRepository, times(1)).save(any(Reserva.class));
    }

    /**
     * <b>Caso de Prueba: Regla de Negocio de Solapamiento</b>
     * <p>
     * Verifica que el sistema BLOQUEE la creación si ya existe una reserva en el rango de fechas solicitado.
     * Esto es crítico para evitar el overbooking.
     */
    @Test
    @DisplayName("CU04 - Fallo: Debería lanzar excepción si la habitación ya está ocupada (Solapamiento)")
    void crearReservas_Fallo_Solapamiento() {
        // ARRANGE: DTO válido
        DtoReserva dto = crearDtoValido();

        // Mock: Simulamos que el repositorio encuentra un conflicto (devuelve 'true')
        when(reservaRepository.existeReservaEnFecha(anyString(), any(Date.class), any(Date.class)))
                .thenReturn(true);

        // ACT & ASSERT: Esperamos que lance una excepción genérica (según tu código actual)
        Exception exception = assertThrows(Exception.class, () -> {
            reservaService.crearReservas(List.of(dto));
        });

        // Validamos el mensaje de error para asegurar que falló por la razón correcta
        assertTrue(exception.getMessage().contains("ya está reservada"));

        // Verificación clave: Aseguramos que NUNCA se intentó guardar en la BD
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    /**
     * <b>Caso de Prueba: Validación de Fechas</b>
     * <p>
     * Verifica que no se puedan crear reservas con fecha de inicio en el pasado (ayer o antes).
     */
    @Test
    @DisplayName("CU04 - Fallo: Debería rechazar reservas con fecha de inicio anterior a hoy")
    void crearReservas_Fallo_FechaPasada() {
        // ARRANGE: Creamos un DTO y modificamos la fecha para que sea "ayer"
        DtoReserva dto = crearDtoValido();
        LocalDate ayer = LocalDate.now().minusDays(1);
        dto.setFechaDesde(Date.from(ayer.atStartOfDay(ZoneId.systemDefault()).toInstant()));

        // ACT & ASSERT
        Exception exception = assertThrows(Exception.class, () -> {
            reservaService.crearReservas(List.of(dto));
        });

        assertEquals("La fecha de ingreso no puede ser anterior al día de hoy.", exception.getMessage());

        // Optimizacion: Ni siquiera debería llamar a la BD para validar disponibilidad
        verify(reservaRepository, never()).existeReservaEnFecha(any(), any(), any());
    }

    /**
     * <b>Caso de Prueba: Validación de Formato de Documento</b>
     * <p>
     * Prueba específicamente la validación de Regex para DNI implementada en el servicio.
     */
    @Test
    @DisplayName("CU04 - Fallo: Debería rechazar un DNI con formato inválido (muy corto)")
    void crearReservas_Fallo_DniInvalido() {
        // ARRANGE: DNI con solo 3 números (el regex pide 7 u 8)
        DtoReserva dto = crearDtoValido();
        dto.setTipoDocumentoResponsable(TipoDocumento.DNI);
        dto.setNroDocumentoResponsable("123");

        // ACT & ASSERT
        Exception exception = assertThrows(Exception.class, () -> {
            reservaService.crearReservas(List.of(dto));
        });

        assertTrue(exception.getMessage().contains("El DNI debe tener 7 u 8 números"));
    }

    /**
     * <b>Caso de Prueba: Integridad de Datos</b>
     * <p>
     * Verifica que el servicio maneje elegantemente listas nulas o vacías.
     */
    @Test
    @DisplayName("CU04 - Fallo: Debería lanzar excepción si la lista de reservas es nula o vacía")
    void crearReservas_Fallo_ListaVacia() {
        assertThrows(Exception.class, () -> reservaService.crearReservas(new ArrayList<>()));
        assertThrows(Exception.class, () -> reservaService.crearReservas(null));
    }

    // ============================================================================================
    // SECCIÓN CU06: BÚSQUEDA Y CANCELACIÓN
    // ============================================================================================

    /**
     * <b>Caso de Prueba: Búsqueda y Mapeo de Datos</b>
     * <p>
     * Este test es complejo porque verifica la integración entre Reserva y Estadía.
     * El servicio debe devolver un Mapa con datos planos para el Frontend, incluyendo
     * si la reserva ya se convirtió en Estadía (Check-in realizado).
     */
    @Test
    @DisplayName("CU06 - Búsqueda: Debería retornar datos mapeados y detectar si hay estadía asociada")
    void buscarReservasPorHuesped_Exito() {
        // ARRANGE: Preparamos datos simulados de la BD
        Reserva reservaMock = new Reserva();
        reservaMock.setIdReserva(1);
        reservaMock.setApellidoHuespedResponsable("GARCIA");
        reservaMock.setNombreHuespedResponsable("AGUSTIN");
        reservaMock.setFechaDesde(new Date());
        reservaMock.setFechaHasta(new Date());

        Habitacion habMock = new Habitacion();
        habMock.setNumero("101");
        habMock.setTipoHabitacion(TipoHabitacion.DOBLE_SUPERIOR);
        reservaMock.setHabitacion(habMock);

        // Mock 1: El repositorio de reservas encuentra coincidencias
        when(reservaRepository.buscarParaCancelar(any(), anyString()))
                .thenReturn(List.of(reservaMock));

        // Mock 2: El repositorio de estadías confirma que esta reserva YA TIENE check-in (ID 500)
        Estadia estadiaMock = new Estadia();
        estadiaMock.setIdEstadia(500);
        when(estadiaRepository.findByReservaId(1)).thenReturn(Optional.of(estadiaMock));

        // ACT
        List<Map<String, Object>> resultado = reservaService.buscarReservasPorHuesped(null, "AGUSTIN");

        // ASSERT: Validamos el contenido del mapa resultante
        assertFalse(resultado.isEmpty());
        Map<String, Object> item = resultado.get(0);

        assertEquals(1, item.get("idReserva"));
        assertEquals("GARCIA", item.get("apellidoHuespedResponsable"));
        assertEquals("101", item.get("idHabitacion"));
        // Validación crítica: ¿Encontró el ID de la estadía?
        assertEquals(500, item.get("idEstadia"));
    }

    /**
     * <b>Caso de Prueba: Cancelación Exitosa</b>
     * <p>
     * Verifica que el servicio cambie el estado de la reserva a CANCELADA y actualice la BD.
     */
    @Test
    @DisplayName("CU06 - Cancelar: Debería cambiar el estado de la reserva a CANCELADA")
    void cancelarReservas_Exito() throws Exception {
        // ARRANGE
        Integer idReserva = 10;
        Reserva reservaMock = new Reserva();
        reservaMock.setIdReserva(idReserva);
        reservaMock.setEstadoReserva(EstadoReserva.ACTIVA); // Estado inicial

        when(reservaRepository.findById(idReserva)).thenReturn(Optional.of(reservaMock));

        // ACT
        reservaService.cancelarReservas(List.of(idReserva));

        // ASSERT
        assertEquals(EstadoReserva.CANCELADA, reservaMock.getEstadoReserva());
        verify(reservaRepository, times(1)).save(reservaMock);
    }

    /**
     * <b>Caso de Prueba: Cancelación Idempotente</b>
     * <p>
     * Si una reserva ya estaba cancelada, el sistema no debería hacer nada (ni lanzar error ni intentar guardar de nuevo).
     */
    @Test
    @DisplayName("CU06 - Cancelar: No debería realizar acciones si la reserva ya estaba cancelada")
    void cancelarReservas_IgnorarYaCancelada() throws Exception {
        // ARRANGE
        Integer idReserva = 10;
        Reserva reservaMock = new Reserva();
        reservaMock.setIdReserva(idReserva);
        reservaMock.setEstadoReserva(EstadoReserva.CANCELADA); // YA está cancelada

        when(reservaRepository.findById(idReserva)).thenReturn(Optional.of(reservaMock));

        // ACT
        reservaService.cancelarReservas(List.of(idReserva));

        // ASSERT
        // Verify times(0) asegura que no se gastaron recursos en un update innecesario
        verify(reservaRepository, times(0)).save(reservaMock);
    }

    // ============================================================================================
    // MÉTODOS AUXILIARES
    // ============================================================================================

    /**
     * Helper para crear un DTO con datos válidos y fechas futuras.
     */
    private DtoReserva crearDtoValido() {
        DtoReserva dto = new DtoReserva();
        dto.setIdHabitacion("101");
        dto.setNombreHuespedResponsable("JUAN");
        dto.setApellidoHuespedResponsable("PEREZ");
        dto.setTipoDocumentoResponsable(TipoDocumento.DNI);
        dto.setNroDocumentoResponsable("12345678");

        // Configuramos fechas para "dentro de 5 días"
        LocalDate futuro = LocalDate.now().plusDays(5);
        dto.setFechaDesde(Date.from(futuro.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        dto.setFechaHasta(Date.from(futuro.plusDays(2).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        return dto;
    }
}