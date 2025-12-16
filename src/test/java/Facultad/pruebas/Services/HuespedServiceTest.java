package Facultad.pruebas.Services;

import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoDireccion; //
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoHuesped; //
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoHuespedBusqueda; //
import Facultad.TrabajoPracticoDesarrollo.Dominio.Direccion;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Huesped;
import Facultad.TrabajoPracticoDesarrollo.Dominio.HuespedId;
import Facultad.TrabajoPracticoDesarrollo.Repositories.*;
import Facultad.TrabajoPracticoDesarrollo.Services.HuespedService;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HuespedServiceTest {

    @Mock private HuespedRepository huespedRepository;
    @Mock private DireccionRepository direccionRepository;
    // Mocks extra necesarios para que el constructor de HuespedService no falle
    @Mock private ReservaRepository reservaRepository;
    @Mock private EstadiaRepository estadiaRepository;
    @Mock private FacturaRepository facturaRepository;
    @Mock private PersonaFisicaRepository personaFisicaRepository;
    @Mock private EstadiaHuespedRepository estadiaHuespedRepository;
    @Mock private EntityManager entityManager;

    @InjectMocks
    private HuespedService huespedService;

    @Test
    void buscarHuespedes_ConCriterios_LlamaQuery() {
        // Arrange
        DtoHuespedBusqueda criterios = new DtoHuespedBusqueda();
        criterios.setApellido("Gomez");

        when(huespedRepository.buscarPorCriterios(eq("Gomez"), any(), any(), any()))
                .thenReturn(List.of(new Huesped()));

        // Act
        List<Huesped> res = huespedService.buscarHuespedes(criterios);

        // Assert
        assertFalse(res.isEmpty());
    }

    @Test
    void upsertHuesped_Nuevo_GuardaTodo() {
        // Arrange: Usamos Builder para el DTO principal y anidamos el Builder de Dirección
        DtoHuesped dto = new DtoHuesped.Builder()
                .tipoDocumento(TipoDocumento.DNI)
                .documento("111222")
                .nombres("Test")
                .apellido("User")
                // CORRECCIÓN AQUÍ: Usamos el Builder de DtoDireccion
                .direccion(new DtoDireccion.Builder()
                        .calle("Calle Falsa")
                        .numero(123)
                        .localidad("Springfield")
                        .provincia("Estado")
                        .pais("Pais")
                        .codPostal(1000)
                        .build())
                .build();

        when(huespedRepository.findById(any(HuespedId.class))).thenReturn(Optional.empty());

        // Act
        huespedService.upsertHuesped(dto);

        // Assert
        verify(direccionRepository).save(any(Direccion.class));
        verify(huespedRepository).save(any(Huesped.class));
    }
}