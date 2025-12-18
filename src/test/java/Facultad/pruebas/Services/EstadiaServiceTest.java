package Facultad.pruebas.Services;

import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoEstadia;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoHabitacion;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoHuesped;
import Facultad.TrabajoPracticoDesarrollo.Dominio.*;
import Facultad.TrabajoPracticoDesarrollo.Repositories.EstadiaRepository;
import Facultad.TrabajoPracticoDesarrollo.Repositories.HabitacionRepository;
import Facultad.TrabajoPracticoDesarrollo.Repositories.HuespedRepository;
import Facultad.TrabajoPracticoDesarrollo.Services.EstadiaService;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Clase de pruebas unitarias para {@link EstadiaService}.
 * <p>
 * Cubre la lógica de negocio del <b>CU15: Ocupar Habitación</b>.
 * Verifica reglas críticas:
 * <ul>
 * <li>Validación de habitación ocupada en fechas solicitadas.</li>
 * <li><b>Regla de Acompañante:</b> Un huésped no puede estar activo en otra habitación simultáneamente.</li>
 * <li>Control de capacidad máxima de la habitación.</li>
 * <li>Asignación correcta del Responsable vs Acompañantes.</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class EstadiaServiceTest {

    @Mock
    private EstadiaRepository estadiaRepository;
    @Mock
    private HuespedRepository huespedRepository;
    @Mock
    private HabitacionRepository habitacionRepository;

    @InjectMocks
    private EstadiaService estadiaService;

    // ============================================================================================
    // CU15: OCUPAR HABITACIÓN (Check-In)
    // ============================================================================================

    @Test
    @DisplayName("CU15 - Éxito: Debe crear estadía y asignar responsable correctamente")
    void crearEstadia_Exito() throws Exception {
        // ARRANGE
        DtoEstadia dto = crearDtoValido(2); // 2 Personas
        Habitacion habitacionMock = new Habitacion();
        habitacionMock.setNumero("101");
        habitacionMock.setCapacidad(3); // Capacidad de sobra

        // Mock: Habitación existe
        when(habitacionRepository.findById("101")).thenReturn(Optional.of(habitacionMock));

        // Mock: Habitación libre
        when(estadiaRepository.existeEstadiaEnFechas(any(), any(), any())).thenReturn(false);

        // Mock: Huéspedes existen en BD
        when(huespedRepository.findById(any(HuespedId.class))).thenReturn(Optional.of(new Huesped()));

        // Mock: Save intermedio (primero guarda estadía sola, luego con lista)
        Estadia estadiaGuardada = new Estadia();
        estadiaGuardada.setIdEstadia(1);
        when(estadiaRepository.save(any(Estadia.class))).thenReturn(estadiaGuardada);

        // ACT
        estadiaService.crearEstadia(dto);

        // ASSERT
        ArgumentCaptor<Estadia> captor = ArgumentCaptor.forClass(Estadia.class);
        // Se llama 2 veces al save (una al crear, otra al setear la lista de huéspedes)
        verify(estadiaRepository, atLeast(1)).save(captor.capture());

        Estadia valorFinal = captor.getValue();
        assertNotNull(valorFinal.getEstadiaHuespedes());
        assertEquals(2, valorFinal.getEstadiaHuespedes().size());
    }

    @Test
    @DisplayName("CU15 - Fallo: Debe rechazar si la habitación ya está ocupada")
    void crearEstadia_Fallo_HabitacionOcupada() {
        // ARRANGE
        DtoEstadia dto = crearDtoValido(1);

        // Mock: El repositorio dice que YA existe conflicto de fechas
        when(estadiaRepository.existeEstadiaEnFechas(eq("101"), any(), any())).thenReturn(true);

        // ACT & ASSERT
        Exception ex = assertThrows(Exception.class, () -> estadiaService.crearEstadia(dto));
        assertTrue(ex.getMessage().contains("ya está ocupada"));

        verify(estadiaRepository, never()).save(any());
    }

    @Test
    @DisplayName("CU15 - Fallo: Debe rechazar si la capacidad de la habitación es insuficiente")
    void crearEstadia_Fallo_CapacidadExcedida() {
        // ARRANGE
        DtoEstadia dto = crearDtoValido(3); // 3 Personas

        Habitacion habitacionMock = new Habitacion();
        habitacionMock.setNumero("101");
        habitacionMock.setCapacidad(2); // Solo entran 2

        when(habitacionRepository.findById("101")).thenReturn(Optional.of(habitacionMock));
        when(estadiaRepository.existeEstadiaEnFechas(any(), any(), any())).thenReturn(false);

        // ACT & ASSERT
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> estadiaService.crearEstadia(dto));
        assertTrue(ex.getMessage().contains("excede la capacidad"));
    }

    /**
     * <b>Regla de Negocio Crítica:</b>
     * [cite_start]"El acompañante solo puede figurar en una habitación a la vez." [cite: 507]
     */
    @Test
    @DisplayName("CU15 - Fallo: Debe rechazar si un acompañante ya está alojado en otro lado")
    void crearEstadia_Fallo_AcompananteDuplicado() {
        // ARRANGE
        DtoEstadia dto = crearDtoValido(2); // Titular + 1 Acompañante
        // Simulamos que el acompañante (índice 1) ya está activo
        DtoHuesped acompanante = dto.getDtoHuespedes().get(1);

        // Mock: Habitación libre
        when(estadiaRepository.existeEstadiaEnFechas(any(), any(), any())).thenReturn(false);

        // Mock: El acompañante ESTÁ activo en otra habitación
        when(estadiaRepository.esHuespedActivo(
                eq(acompanante.getTipoDocumento()),
                eq(acompanante.getNroDocumento()),
                any(), any())
        ).thenReturn(true);

        // ACT & ASSERT
        Exception ex = assertThrows(Exception.class, () -> estadiaService.crearEstadia(dto));
        assertTrue(ex.getMessage().contains("ya figura alojado"));

        verify(estadiaRepository, never()).save(any());
    }

    @Test
    @DisplayName("CU15 - Fallo: Debe validar datos obligatorios (Habitación o Huéspedes)")
    void crearEstadia_ValidacionesBasicas() {
        DtoEstadia dtoSinHab = crearDtoValido(1);
        dtoSinHab.setDtoHabitacion(null);

        assertThrows(IllegalArgumentException.class, () -> estadiaService.crearEstadia(dtoSinHab));

        DtoEstadia dtoSinHuespedes = crearDtoValido(1);
        dtoSinHuespedes.setDtoHuespedes(null);

        assertThrows(IllegalArgumentException.class, () -> estadiaService.crearEstadia(dtoSinHuespedes));
    }

    // --- HELPER ---
    private DtoEstadia crearDtoValido(int cantidadHuespedes) {
        DtoEstadia dto = new DtoEstadia();

        // Habitación
        DtoHabitacion hab = new DtoHabitacion();
        hab.setNumero("101");
        dto.setDtoHabitacion(hab);

        // Fechas
        dto.setFechaCheckIn(new Date());
        dto.setFechaCheckOut(new Date());

        // Huéspedes
        ArrayList<DtoHuesped> huespedes = new ArrayList<>();
        for (int i = 0; i < cantidadHuespedes; i++) {
            DtoHuesped h = new DtoHuesped();
            h.setTipoDocumento(TipoDocumento.DNI);
            h.setNroDocumento("100" + i);
            h.setApellido("User" + i);
            huespedes.add(h);
        }
        dto.setDtoHuespedes(huespedes);

        return dto;
    }
}