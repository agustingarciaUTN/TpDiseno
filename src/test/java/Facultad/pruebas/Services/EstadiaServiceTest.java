package Facultad.pruebas.Services;

import Facultad.TrabajoPracticoDesarrollo.DTOs.*;
import Facultad.TrabajoPracticoDesarrollo.Dominio.*;
import Facultad.TrabajoPracticoDesarrollo.Repositories.EstadiaRepository;
import Facultad.TrabajoPracticoDesarrollo.Repositories.HabitacionRepository;
import Facultad.TrabajoPracticoDesarrollo.Repositories.HuespedRepository;
import Facultad.TrabajoPracticoDesarrollo.Services.EstadiaService;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoHabitacion; // Importante
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EstadiaServiceTest {

    @Mock private EstadiaRepository estadiaRepository;
    @Mock private HuespedRepository huespedRepository;
    @Mock private HabitacionRepository habitacionRepository;

    @InjectMocks
    private EstadiaService estadiaService;

    @Test
    void validarDisponibilidad_SiExisteEstadia_RetornaFalse() {
        when(estadiaRepository.existeEstadiaEnFechas(anyString(), any(), any())).thenReturn(true);

        boolean disponible = estadiaService.validarDisponibilidad("101", new Date(), new Date());
        assertFalse(disponible);
    }

    @Test
    void crearEstadia_HabitacionOcupada_LanzaExcepcion() {
        // Arrange
        DtoHabitacion habDto = new DtoHabitacion.Builder()
                .numero("101")
                .capacidad(2) // Requerido por el Builder
                .tipoHabitacion(TipoHabitacion.DOBLE_ESTANDAR) // Requerido por lógica
                .build();

        // Creamos huesped válido
        DtoHuesped huespedDto = new DtoHuesped.Builder()
                .tipoDocumento(TipoDocumento.DNI)
                .documento("123") // Método correcto del Builder es documento()
                .nombres("Test")
                .apellido("User")
                .build();

        DtoEstadia dto = new DtoEstadia.Builder()
                .dtoHabitacion(habDto)
                .agregarHuesped(huespedDto)
                .fechaCheckIn(new Date())
                .fechaCheckOut(new Date())
                .build();

        when(estadiaRepository.existeEstadiaEnFechas(eq("101"), any(), any())).thenReturn(true);

        // Act & Assert
        Exception ex = assertThrows(Exception.class, () -> estadiaService.crearEstadia(dto));
        assertTrue(ex.getMessage().contains("ya está ocupada"));
    }

    @Test
    void crearEstadia_DatosValidos_GuardaEstadiaYHuespedes() throws Exception {
        // Arrange
        DtoHabitacion habDto = new DtoHabitacion.Builder()
                .numero("202")
                .capacidad(2)
                .build();

        DtoHuesped huespedDto = new DtoHuesped.Builder()
                .tipoDocumento(TipoDocumento.DNI)
                .documento("12345678") // Método correcto del Builder
                .apellido("Test")
                .nombres("User")
                .build();

        DtoEstadia dto = new DtoEstadia.Builder()
                .fechaCheckIn(new Date())
                .fechaCheckOut(new Date())
                .dtoHabitacion(habDto)
                .agregarHuesped(huespedDto)
                .valorEstadia(10000.0)
                .build();

        // Mocks
        when(estadiaRepository.existeEstadiaEnFechas(anyString(), any(), any())).thenReturn(false);
        when(habitacionRepository.findById("202")).thenReturn(Optional.of(new Habitacion.Builder().numero("202").build()));

        Huesped huespedReal = new Huesped.Builder()
                .tipoDocumento(TipoDocumento.DNI)
                .nroDocumento("12345678") // Método correcto del Builder de Dominio
                .build();

        when(huespedRepository.findById(any(HuespedId.class))).thenReturn(Optional.of(huespedReal));

        Estadia estadiaGuardada = new Estadia.Builder().idEstadia(100).build();
        when(estadiaRepository.save(any(Estadia.class))).thenReturn(estadiaGuardada);

        // Act
        estadiaService.crearEstadia(dto);

        // Assert
        verify(estadiaRepository, times(2)).save(any(Estadia.class));
    }
}