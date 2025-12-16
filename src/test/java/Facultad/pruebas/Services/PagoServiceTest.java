package Facultad.pruebas.Services;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Factura;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Pago;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoFactura;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoPago;
import Facultad.TrabajoPracticoDesarrollo.Repositories.PagoRepository;
import Facultad.TrabajoPracticoDesarrollo.Services.FacturaService;
import Facultad.TrabajoPracticoDesarrollo.Services.PagoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private FacturaService facturaService;

    @InjectMocks
    private PagoService pagoService;

    /**
     * Test: Validar que no se pueda registrar un pago con monto cero o negativo.
     */
    @Test
    void registrarPago_MontoInvalido_LanzaExcepcion() {
        // Arrange
        DtoPago dto = new DtoPago.Builder()
                .montoTotal(0.0) // Monto inválido
                .build();

        // Act & Assert
        // Esperamos una excepción de argumento ilegal
        assertThrows(IllegalArgumentException.class, () -> pagoService.registrarPago(dto));

        // Nos aseguramos de que NUNCA se haya llamado al repositorio para guardar
        verifyNoInteractions(pagoRepository);
    }

    /**
     * Test: Intentar pagar una factura que no existe en el sistema.
     */
    @Test
    void registrarPago_FacturaNoExiste_LanzaExcepcion() {

        // ARRANGE
        Factura facturaDominio = new Factura.Builder()
                .numeroFactura("B-9999")
                .build();

        DtoPago dto = new DtoPago.Builder()
                .montoTotal(100.0)
                .Factura(facturaDominio)
                .build();

        // El servicio de facturas dice "no encontré nada" (null)
        when(facturaService.buscarPorNumero("B-9999")).thenReturn(null);

        // Act & Assert
        assertThrows(Exception.class, () -> pagoService.registrarPago(dto));
    }

    /**
     * Test: Registro de pago exitoso.
     */
    @Test
    void registrarPago_DatosValidos_GuardaPago() throws Exception {
        // Arrange
        Factura facturaDominio = new Factura.Builder()
                .numeroFactura("B-0001")
                .build();

        DtoPago dto = new DtoPago.Builder()
                .montoTotal(1500.0)
                .fechaPago(new Date())
                .Factura(facturaDominio)
                .build();

        // El servicio de facturas confirma que existe
        Factura facturaReal = new Factura.Builder()
                .numeroFactura("B-0001")
                .build();

        when(facturaService.buscarPorNumero("B-0001")).thenReturn(facturaReal);

        // Act
        pagoService.registrarPago(dto);

        // Assert
        verify(pagoRepository).save(any(Pago.class));
    }
}