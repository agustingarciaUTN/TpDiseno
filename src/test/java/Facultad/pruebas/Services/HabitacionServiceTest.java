package Facultad.pruebas.Services;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Habitacion;
import Facultad.TrabajoPracticoDesarrollo.Repositories.EstadiaRepository;
import Facultad.TrabajoPracticoDesarrollo.Repositories.HabitacionRepository;
import Facultad.TrabajoPracticoDesarrollo.Repositories.ReservaRepository;
import Facultad.TrabajoPracticoDesarrollo.Services.HabitacionService;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoHabitacion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HabitacionServiceTest {

    @Mock private HabitacionRepository habitacionRepository;
    @Mock private ReservaRepository reservaRepository;
    @Mock private EstadiaRepository estadiaRepository;

    @InjectMocks
    private HabitacionService habitacionService;

    @Test
    void obtenerTodas_OrdenaCorrectamente() {
        // Arrange: Usamos Builder para crear las habitaciones
        Habitacion h1 = new Habitacion.Builder()
                .numero("102")
                .tipoHabitacion(TipoHabitacion.DOBLE_ESTANDAR)
                .build();

        Habitacion h2 = new Habitacion.Builder()
                .numero("101")
                .tipoHabitacion(TipoHabitacion.INDIVIDUAL_ESTANDAR)
                .build();

        when(habitacionRepository.findAll()).thenReturn(Arrays.asList(h1, h2));

        // Act
        List<Habitacion> res = habitacionService.obtenerTodas();

        // Assert
        assertEquals("101", res.get(0).getNumero()); // Individual va primero
    }

    @Test
    void validarRangoFechas_FechasMalas_RetornaFalse() {
        Date inicio = new Date();
        Date fin = new Date(inicio.getTime() - 10000);
        assertFalse(habitacionService.validarRangoFechas(inicio, fin));
    }
}