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

    @Test
    void calcularDetalle_ConLateCheckout_AplicaRecargo() {
        // Arrange
        int idEstadia = 1;
        int idResponsable = 5;
        String horaSalidaTarde = "19:00"; // Pasadas las 18:00 -> Recargo día completo

        // Builder Habitacion: costo es float según tu entidad
        Habitacion hab = new Habitacion.Builder()
                .costo(10000.0f)
                .build();

        // Builder Estadia
        Estadia estadia = new Estadia.Builder()
                .idEstadia(idEstadia)
                .valorEstadia(20000.0) // Double
                .habitacion(hab)
                .build();

        // Builder Huesped
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

        when(estadiaRepository.findById(idEstadia)).thenReturn(Optional.of(estadia));
        when(responsablePagoRepository.findById(idResponsable)).thenReturn(Optional.of(responsable));
        when(serviciosAdicionalesRepository.findByEstadia_IdEstadia(idEstadia)).thenReturn(new ArrayList<>());

        // Act
        DtoDetalleFacturacion detalle = facturaService.calcularDetalle(idEstadia, idResponsable, horaSalidaTarde);

        // Assert
        assertEquals(TipoFactura.B, detalle.getTipoFactura());
        // Monto base (20000.0) + Recargo día completo (10000.0) = 30000.0 subtotal
        assertEquals(30000.0, detalle.getSubtotal());
        // 30000.0 * 1.21 = 36300.0 Total
        assertEquals(36300.0, detalle.getMontoTotal());
        assertTrue(detalle.getDetalleRecargo().contains("Día completo"));
    }

    @Test
    void generarFactura_NuevaFactura_GuardaCorrectamente() throws Exception {
        // Arrange
        // Nota: Asumimos que DtoEstadiaSimple y DtoResponsableSimple tienen constructores o setters accesibles
        // ya que no tengo sus archivos de Builder específicos, usaré instanciación directa si es posible o mocks.
        // Si tienen Builders, reemplaza esto con sus builders correspondientes.
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

        when(facturaRepository.existsById("B-0001-00000001")).thenReturn(false);
        // Mocks de respuesta de repositorios
        when(estadiaRepository.findById(10)).thenReturn(Optional.of(new Estadia.Builder().idEstadia(10).build()));
        when(responsablePagoRepository.findById(5)).thenReturn(Optional.of(new PersonaFisica.Builder().idResponsablePago(5).build()));

        // Act
        facturaService.generarFactura(dto);

        // Assert
        verify(facturaRepository).save(any(Factura.class));
    }

    @Test
    void guardarFactura_SiYaExiste_LanzaExcepcion() {
        // Arrange: Builder para la Entidad
        Factura f = new Factura.Builder()
                .numeroFactura("A-0001")
                .estadoFactura(EstadoFactura.PENDIENTE)
                .build();

        when(facturaRepository.existsById("A-0001")).thenReturn(true);

        // Act & Assert
        assertThrows(Exception.class, () -> facturaService.guardarFactura(f));
    }
}