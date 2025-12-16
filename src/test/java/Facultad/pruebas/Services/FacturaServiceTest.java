package Facultad.pruebas.Services;

import Facultad.TrabajoPracticoDesarrollo.DTOs.*;
import Facultad.TrabajoPracticoDesarrollo.Dominio.*;
import Facultad.TrabajoPracticoDesarrollo.Repositories.*;
import Facultad.TrabajoPracticoDesarrollo.Services.FacturaService;
import Facultad.TrabajoPracticoDesarrollo.enums.EstadoFactura;
import Facultad.TrabajoPracticoDesarrollo.enums.PosIva;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoFactura;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FacturaServiceTest {

    @Mock private FacturaRepository facturaRepository;
    @Mock private EstadiaRepository estadiaRepository;
    @Mock private ResponsablePagoRepository responsablePagoRepository;
    @Mock private ServiciosAdicionalesRepository serviciosAdicionalesRepository;
    @Mock private NotaDeCreditoRepository notaDeCreditoRepository;

    @InjectMocks
    private FacturaService facturaService;

    /**
     * Test: Calcular detalle de facturación con recargo por salida tardía (Late Check-out).
     * Escenario: Salida a las 19:00 hs (pasadas las 18:00) -> Recargo de 1 día completo.
     */
    @Test
    void calcularDetalle_ConLateCheckout_AplicaRecargo() {
        // Arrange
        int idEstadia = 1;
        int idResponsable = 5;
        String horaSalidaTarde = "19:00"; // Pasadas las 18:00 -> Recargo día completo

        // Configuramos precios y valores base
        Habitacion hab = new Habitacion.Builder()
                .costo(10000.0f)
                .build();

        // Builder Estadia
        Estadia estadia = new Estadia.Builder()
                .idEstadia(idEstadia)
                .valorEstadia(20000.0) // Valor original (ej: 2 noches)
                .habitacion(hab)
                .build();

        // El responsable es Consumidor Final -> Genera Factura B
        Huesped huesped = new Huesped.Builder()
                .posicionIva(PosIva.CONSUMIDOR_FINAL)
                .apellido("Gomez")
                .nombres("Juan")
                .build();

        // Builder PersonaFisica (Responsable)
        PersonaFisica responsable = new PersonaFisica.Builder()
                .idResponsablePago(idResponsable)
                .huesped(huesped)
                .build();

        // Mocks de retorno
        when(estadiaRepository.findById(idEstadia)).thenReturn(Optional.of(estadia));
        when(responsablePagoRepository.findById(idResponsable)).thenReturn(Optional.of(responsable));
        when(serviciosAdicionalesRepository.findByEstadia_IdEstadia(idEstadia)).thenReturn(new ArrayList<>());

        // Act
        DtoDetalleFacturacion detalle = facturaService.calcularDetalle(idEstadia, idResponsable, horaSalidaTarde);

        // Assert
        assertEquals(TipoFactura.B, detalle.getTipoFactura());

        // Validación matemática:
        // Subtotal = Valor Estadia (20.000) + Recargo 1 día (10.000) = 30.000
        assertEquals(30000.0, detalle.getSubtotal());

        // Total con IVA (21%) -> 30.000 * 1.21 = 36.300
        assertEquals(36300.0, detalle.getMontoTotal());

        assertTrue(detalle.getDetalleRecargo().contains("Día completo"));
    }

    /**
     * Test: Generar una factura nueva y persistirla.
     */
    @Test
    void generarFactura_NuevaFactura_GuardaCorrectamente() throws Exception {
        // Arrange
        DtoEstadiaSimple dtoEstadia = new DtoEstadiaSimple();
        dtoEstadia.setIdEstadia(10);

        DtoResponsableSimple dtoResp = new DtoResponsableSimple();
        dtoResp.setIdResponsable(5);

        // DTO Principal con Builder
        DtoFactura dto = new DtoFactura.Builder()
                .numeroFactura("B-0001-00000001")
                .importeTotal(5000.0)
                .fechaEmision(new java.util.Date())
                .idEstadia(dtoEstadia)
                .idResponsable(dtoResp)
                .build();

        // Validamos que NO exista previamente
        when(facturaRepository.existsById("B-0001-00000001")).thenReturn(false);

        // Simulamos que estadía y responsable existen
        when(estadiaRepository.findById(10)).thenReturn(Optional.of(new Estadia.Builder().idEstadia(10).build()));
        when(responsablePagoRepository.findById(5)).thenReturn(Optional.of(new PersonaFisica.Builder().idResponsablePago(5).build()));

        // Act
        facturaService.generarFactura(dto);

        // Assert
        verify(facturaRepository).save(any(Factura.class));
    }

    /**
     * Test: Intentar guardar una factura que ya existe en el sistema.
     * Resultado esperado: Lanzar Excepción para evitar duplicados.
     */
    @Test
    void guardarFactura_SiYaExiste_LanzaExcepcion() {
        // Arrange: Builder para la Entidad
        // Preparamos una factura simulada usando el Builder
        Factura f = new Factura.Builder()
                .numeroFactura("A-0001")
                .estadoFactura(EstadoFactura.PENDIENTE)
                .build();

        // Simulamos que el repositorio encuentra una factura con ese NUMERO (return true)
        when(facturaRepository.existsById("A-0001")).thenReturn(true);

        // Act & Assert
        // Verificamos que el método lance Exception al intentar guardar
        assertThrows(Exception.class, () -> facturaService.guardarFactura(f));

        // Verificamos que NO se llame al método save del repositorio
        verify(facturaRepository, never()).save(any(Factura.class));
    }
}