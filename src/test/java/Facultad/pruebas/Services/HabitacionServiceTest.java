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

    /**
     * Test: Obtener todas las habitaciones.
     * Verifica que se ordenen correctamente (lógica de negocio: Individual primero).
     */
    @Test
    void obtenerTodas_OrdenaCorrectamente() {
        // Arrange: Usamos Builder para crear las habitaciones
        // Creamos habitaciones desordenadas
        Habitacion h1 = new Habitacion.Builder()
                .numero("102")
                .tipoHabitacion(TipoHabitacion.DOBLE_ESTANDAR)
                .build();

        Habitacion h2 = new Habitacion.Builder()
                .numero("101")
                .tipoHabitacion(TipoHabitacion.INDIVIDUAL_ESTANDAR)
                .build();

        // Simulamos que el Repositorio las devuelve en orden "incorrecto" o arbitrario (h1 antes que h2).
        // Esto fuerza a que el Servicio tenga que aplicar su lógica de ordenamiento.
        when(habitacionRepository.findAll()).thenReturn(Arrays.asList(h1, h2));

        // Act
        List<Habitacion> res = habitacionService.obtenerTodas();

        // Assert
        // Verificamos que el primer elemento de la lista resultante sea la habitación INDIVIDUAL (h2).
        // Si el servicio no ordenara, el primero sería h1 (DOBLE).
        assertEquals("101", res.get(0).getNumero()); // Individual va primero
    }

    /**
     * Test: Validar la lógica de rangos de fechas.
     * Escenario Negativo: Se intenta validar un rango donde la fecha de Fin es ANTERIOR a la de Inicio.
     * Resultado esperado: El método debe detectar la inconsistencia y retornar false.
     */
    @Test
    void validarRangoFechas_FechasMalas_RetornaFalse() {
        Date inicio = new Date(); // Fecha actual
        Date fin = new Date(inicio.getTime() - 10000); // Fecha fin = Hace 10 segundos (Pasado)
        // --- ACT ---
        // Llamamos a la validación con este rango imposible (Fin < Inicio).
        boolean esValido = habitacionService.validarRangoFechas(inicio, fin);

        // --- ASSERT ---
        // El servicio debe rechazar este rango.
        assertFalse(esValido, "El rango no puede ser válido si la fecha de fin es anterior a la de inicio");
    }
}