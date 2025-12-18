package Facultad.pruebas.Services;

import Facultad.TrabajoPracticoDesarrollo.DTOs.*;
import Facultad.TrabajoPracticoDesarrollo.Dominio.*;
import Facultad.TrabajoPracticoDesarrollo.Repositories.*;
import Facultad.TrabajoPracticoDesarrollo.Services.PagoService;
import Facultad.TrabajoPracticoDesarrollo.enums.EstadoFactura;
import Facultad.TrabajoPracticoDesarrollo.enums.Moneda;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.Mockito.*;

/**
 * Clase de pruebas unitarias para {@link PagoService}.
 * <p>
 * Cubre la lógica de negocio del Caso de Uso:
 * <ul>
 * <li><b>CU16:</b> Ingresar Pago.</li>
 * </ul>
 * Verifica:
 * 1. Búsqueda de facturas pendientes (filtrando las ya pagadas).
 * 2. Validación de montos (Pago insuficiente, Pago con vuelto).
 * 3. Conversión de divisas (Cotización).
 * 4. Actualización de estado de Factura.
 */
@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock private FacturaRepository facturaRepository;
    @Mock private PagoRepository pagoRepository;
    @Mock private MedioDePagoRepository medioDePagoRepository;
    @Mock private EstadiaRepository estadiaRepository;
    @Mock private HabitacionRepository habitacionRepository;
    @Mock private TarjetaRepository tarjetaRepository;
    @Mock private ChequeRepository chequeRepository;
    // FacturaService se inyecta pero no se usa en los métodos principales testados aquí

    @InjectMocks
    private PagoService pagoService;

    // ============================================================================================
    // BÚSQUEDA DE FACTURAS PENDIENTES
    // ============================================================================================

    @Test
    @DisplayName("CU16 - Búsqueda: Debe retornar solo las facturas con estado PENDIENTE")
    void buscarFacturasPendientes_Exito() {
        // ARRANGE
        String nroHabitacion = "101";

        // 1. Mock Habitación
        Habitacion habitacion = new Habitacion();
        habitacion.setNumero(nroHabitacion);
        when(habitacionRepository.findById(nroHabitacion)).thenReturn(Optional.of(habitacion));

        // 2. Mock Estadía
        Estadia estadia = new Estadia();
        estadia.setIdEstadia(1);
        when(estadiaRepository.findByHabitacion_Numero(nroHabitacion)).thenReturn(List.of(estadia));

        // 3. Mock Facturas: Una PAGADA (no debe volver) y una PENDIENTE (sí debe volver)
        Factura facturaPagada = new Factura();
        facturaPagada.setEstadoFactura(EstadoFactura.PAGADA);

        Factura facturaPendiente = new Factura();
        facturaPendiente.setEstadoFactura(EstadoFactura.PENDIENTE);
        facturaPendiente.setNumeroFactura("A-0001");
        facturaPendiente.setImporteTotal(5000.0);
        // Datos necesarios para el mapper
        facturaPendiente.setEstadia(estadia);
        facturaPendiente.setFechaEmision(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));

        when(facturaRepository.findByEstadia_IdEstadia(1)).thenReturn(List.of(facturaPagada, facturaPendiente));

        // ACT
        List<DtoFactura> resultado = pagoService.buscarFacturasPendientesPorHabitacion(nroHabitacion);

        // ASSERT
        assertEquals(1, resultado.size());
        assertEquals("A-0001", resultado.get(0).getNumeroFactura());
    }

    @Test
    @DisplayName("CU16 - Búsqueda: Debe lanzar error si la habitación no existe")
    void buscarFacturasPendientes_HabitacionInexistente() {
        when(habitacionRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                pagoService.buscarFacturasPendientesPorHabitacion("999")
        );
    }

    // ============================================================================================
    // REGISTRAR PAGO
    // ============================================================================================

    @Test
    @DisplayName("CU16 - Pago Exitoso: Debe saldar factura y calcular vuelto en efectivo")
    void registrarPago_Exito_EfectivoConVuelto() throws Exception {
        // ARRANGE
        String nroFactura = "F-100";
        Double deuda = 10000.0;
        Double pagoCliente = 12000.0; // Paga con 12k, espera 2k de vuelto

        // Factura Pendiente
        Factura factura = new Factura();
        factura.setNumeroFactura(nroFactura);
        factura.setImporteTotal(deuda);
        factura.setEstadoFactura(EstadoFactura.PENDIENTE);

        // Mock estructura de habitación para verificar deuda final
        Estadia estadia = new Estadia();
        estadia.setIdEstadia(50);
        Habitacion hab = new Habitacion();
        hab.setNumero("202");
        estadia.setHabitacion(hab);
        factura.setEstadia(estadia);

        when(facturaRepository.findById(nroFactura)).thenReturn(Optional.of(factura));
        when(facturaRepository.findByEstadia_IdEstadia(50)).thenReturn(List.of(factura)); // Para verificar si todo se pagó

        // DTO de Pago
        DtoPago dtoPago = new DtoPago();
        dtoPago.setNumeroFactura(nroFactura);
        dtoPago.setMontoTotal(pagoCliente);
        dtoPago.setMoneda(Moneda.PESOS_ARGENTINOS);
        dtoPago.setFechaPago(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));

        // Medio de pago: Efectivo
        DtoEfectivo dtoEfectivo = new DtoEfectivo();
        dtoEfectivo.setMonto(pagoCliente);
        dtoEfectivo.setMoneda(Moneda.PESOS_ARGENTINOS);
        dtoPago.setMediosPago(List.of(dtoEfectivo));

        // ACT
        DtoResultadoRegistroPago resultado = pagoService.registrarPago(dtoPago);

        // ASSERT
        // 1. Verificar vuelto
        assertEquals(2000.0, resultado.getVuelto());
        assertTrue(resultado.isFacturaSaldada());

        // 2. Verificar cambio de estado en la entidad Factura
        assertEquals(EstadoFactura.PAGADA, factura.getEstadoFactura());
        verify(facturaRepository).save(factura);

        // 3. Verificar que se guardó el Pago
        verify(pagoRepository).save(any(Pago.class));
    }

    @Test
    @DisplayName("CU16 - Pago Exitoso: Debe aplicar cotización si paga en moneda extranjera")
    void registrarPago_Exito_MonedaExtranjera() throws Exception {
        // ARRANGE
        Double deudaPesos = 1100.0;
        Double pagoDolares = 10.0;
        Double cotizacion = 110.0; // 10 * 110 = 1100 Pesos (Pago exacto)

        Factura factura = new Factura();
        factura.setImporteTotal(deudaPesos);
        factura.setEstadoFactura(EstadoFactura.PENDIENTE);

        // Setup minimo para evitar null pointers en la validacion de habitacion
        Estadia estadia = new Estadia();
        estadia.setHabitacion(new Habitacion());
        factura.setEstadia(estadia);

        when(facturaRepository.findById(any())).thenReturn(Optional.of(factura));

        DtoPago dtoPago = new DtoPago();
        dtoPago.setNumeroFactura("F-USD");
        dtoPago.setMontoTotal(pagoDolares);
        dtoPago.setMoneda(Moneda.DOLARES);
        dtoPago.setCotizacion(cotizacion); // Importante
        dtoPago.setMediosPago(new ArrayList<>()); // Lista vacía para simplificar, la lógica de validación de monto ocurre antes

        // ACT
        DtoResultadoRegistroPago resultado = pagoService.registrarPago(dtoPago);

        // ASSERT
        assertEquals(0.0, resultado.getVuelto(), "El pago debió ser exacto por la conversión");
        assertEquals(EstadoFactura.PAGADA, factura.getEstadoFactura());
    }

    @Test
    @DisplayName("CU16 - Fallo: Debe rechazar el pago si el monto es insuficiente")
    void registrarPago_Fallo_MontoInsuficiente() {
        // ARRANGE
        Factura factura = new Factura();
        factura.setImporteTotal(1000.0);
        factura.setEstadoFactura(EstadoFactura.PENDIENTE);

        when(facturaRepository.findById(any())).thenReturn(Optional.of(factura));

        DtoPago dtoPago = new DtoPago();
        dtoPago.setNumeroFactura("F-1");
        dtoPago.setMontoTotal(500.0); // Paga la mitad
        dtoPago.setMoneda(Moneda.PESOS_ARGENTINOS);

        // ACT & ASSERT
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            pagoService.registrarPago(dtoPago);
        });

        assertTrue(ex.getMessage().contains("menor a la deuda"));
        assertTrue(ex.getMessage().contains("Falta pagar: $500,00"));

        // Verificar que NO se cambió el estado
        verify(facturaRepository, never()).save(any());
    }

    @Test
    @DisplayName("CU16 - Fallo: Debe rechazar si la factura ya estaba pagada")
    void registrarPago_Fallo_YaPagada() {
        // ARRANGE
        Factura factura = new Factura();
        factura.setEstadoFactura(EstadoFactura.PAGADA); // Ya pagada
        when(facturaRepository.findById(any())).thenReturn(Optional.of(factura));

        DtoPago dtoPago = new DtoPago();
        dtoPago.setNumeroFactura("F-YA-PAGADA");

        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> pagoService.registrarPago(dtoPago));
    }
}