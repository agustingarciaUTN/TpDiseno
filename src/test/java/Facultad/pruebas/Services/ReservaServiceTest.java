package Facultad.pruebas.Services;

import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoReserva;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Habitacion;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Reserva;
import Facultad.TrabajoPracticoDesarrollo.Repositories.ReservaRepository;
import Facultad.TrabajoPracticoDesarrollo.Services.ReservaService;
import Facultad.TrabajoPracticoDesarrollo.enums.EstadoReserva;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @InjectMocks
    private ReservaService reservaService;

    // Helper para fechas
    private Date fechaFutura(int dias) {
        return Date.from(LocalDate.now().plusDays(dias).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Date fechaPasada(int dias) {
        return Date.from(LocalDate.now().minusDays(dias).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Test
    void buscarReservasEnFecha_DeberiaRetornarLista() {
        // Arrange: Usamos Builder para Habitación y Reserva
        Habitacion h = new Habitacion.Builder()
                .numero("101")
                .build();

        Reserva r = new Reserva.Builder()
                .id(1)
                .estado(EstadoReserva.ACTIVA)
                .habitacion(h)
                .build();

        when(reservaRepository.buscarReservasActivasEnRango(any(), any())).thenReturn(List.of(r));

        // Act
        List<DtoReserva> res = reservaService.buscarReservasEnFecha(new Date(), new Date());

        // Assert
        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals("101", res.get(0).getIdHabitacion());
    }

    @Test
    void crearReservas_DatosValidos_Guarda() throws Exception {
        // Arrange: Usamos Builder para el DTO
        DtoReserva dto = new DtoReserva.Builder()
                .idHabitacion("101")
                .nombreResponsable("Test")
                .apellidoResponsable("User")
                .telefonoResponsable("123")
                .fechaDesde(fechaFutura(2))
                .fechaHasta(fechaFutura(5))
                .build();

        when(reservaRepository.existeReservaEnFecha(anyString(), any(), any())).thenReturn(false);

        // Act
        reservaService.crearReservas(Collections.singletonList(dto));

        // Assert
        verify(reservaRepository, times(1)).save(any(Reserva.class));
    }
}