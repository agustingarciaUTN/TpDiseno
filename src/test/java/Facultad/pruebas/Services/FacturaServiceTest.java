package Facultad.pruebas.Services;

import Facultad.TrabajoPracticoDesarrollo.DTOs.*;
import Facultad.TrabajoPracticoDesarrollo.Dominio.*;
import Facultad.TrabajoPracticoDesarrollo.Repositories.*;
import Facultad.TrabajoPracticoDesarrollo.Services.FacturaService;
import Facultad.TrabajoPracticoDesarrollo.enums.*;
import org.junit.jupiter.api.DisplayName;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Clase de pruebas unitarias para {@link FacturaService}.
 * <p>
 * Cubre la lógica de negocio del <b>CU07: Facturar</b>.
 * Verifica reglas críticas:
 * <ul>
 * <li>Cálculo de Recargos (Late Check-out 50% y 100%).</li>
 * <li>Determinación de Tipo de Factura (A o B) según condición fiscal.</li>
 * <li>Cálculo de IVA (21%, 7%, etc).</li>
 * <li>Validación de mayoría de edad del responsable.</li>
 * <li>Generación correlativa de números de factura.</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class FacturaServiceTest {

    @Mock private EstadiaRepository estadiaRepository;
    @Mock private FacturaRepository facturaRepository;
    @Mock private ResponsablePagoRepository responsablePagoRepository;
    @Mock private HuespedRepository huespedRepository;
    @Mock private ServiciosAdicionalesRepository serviciosAdicionalesRepository;
    // Mocks adicionales requeridos por el constructor aunque no se usen intensivamente en estos tests
    @Mock private NotaDeCreditoRepository notaDeCreditoRepository;
    @Mock private PersonaFisicaRepository personaFisicaRepository;
    @Mock private DireccionRepository direccionRepository;

    @InjectMocks
    private FacturaService facturaService;

    // ============================================================================================
    // VALIDACIÓN DE RESPONSABLE (Mayoría de Edad)
    // ============================================================================================

    /**
     * <b>Regla de Negocio:</b> Un menor de edad no puede ser titular de una factura.
     */
    @Test
    @DisplayName("Validar Responsable - Fallo: Debe lanzar excepción si es menor de 18 años")
    void validarResponsable_MenorDeEdad() {
        // ARRANGE
        Huesped menor = new Huesped();
        // Nació hace 15 años
        LocalDate fechaNac = LocalDate.now().minusYears(15);
        menor.setFechaNacimiento(java.sql.Date.valueOf(fechaNac));

        when(huespedRepository.findById(any())).thenReturn(Optional.of(menor));

        // ACT & ASSERT
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                facturaService.validarResponsable(TipoDocumento.DNI, "123")
        );
        assertEquals("La persona seleccionada es menor de edad.", ex.getMessage());
    }

    // ============================================================================================
    // CÁLCULO DE DETALLE (La "Calculadora")
    // ============================================================================================

    /**
     * <b>Regla de Negocio (Late Check-out):</b>
     * Si sale entre las 11:01 y las 18:00, se cobra el 50% del valor de la noche.
     */
    @Test
    @DisplayName("Calculadora - Late Check-out: Debe cobrar 50% recargo si sale a las 13:00hs")
    void calcularDetalle_LateCheckout_MediaEstadia() {
        // ARRANGE
        int idEstadia = 1;
        int idResponsable = 100;
        String horaSalida = "13:00"; // Pasado las 11:00, antes de las 18:00

        // Mock Estadía y Habitación
        Estadia estadia = new Estadia();
        estadia.setValorEstadia(10000.0); // Valor base
        Habitacion habitacion = new Habitacion();
        habitacion.setCostoPorNoche(10000.0F);
        estadia.setHabitacion(habitacion);

        // Mock Responsable (Consumidor Final para simplificar IVA)
        PersonaFisica pf = crearPersonaFisica(PosIva.CONSUMIDOR_FINAL);

        when(estadiaRepository.findById(idEstadia)).thenReturn(Optional.of(estadia));
        when(responsablePagoRepository.findById(idResponsable)).thenReturn(Optional.of(pf));
        when(serviciosAdicionalesRepository.findByEstadia_IdEstadia(idEstadia)).thenReturn(Collections.emptyList());

        // ACT
        DtoDetalleFacturacion detalle = facturaService.calcularDetalle(idEstadia, idResponsable, horaSalida);

        // ASSERT
        // Recargo esperado: 50% de 10.000 = 5.000
        assertEquals(5000.0, detalle.getRecargoHorario());
        assertEquals("Late Check-out (50%)", detalle.getDetalleRecargo());

        // Subtotal: 10.000 + 5.000 = 15.000
        assertEquals(15000.0, detalle.getSubtotal());
    }

    /**
     * <b>Regla de Negocio (Late Check-out):</b>
     * Si sale después de las 18:00, se cobra el 100% del valor de la noche.
     */
    @Test
    @DisplayName("Calculadora - Late Check-out: Debe cobrar 100% recargo si sale a las 19:00hs")
    void calcularDetalle_LateCheckout_DiaCompleto() {
        // ARRANGE
        int idEstadia = 1;
        int idResponsable = 100;
        String horaSalida = "19:00"; // Pasado las 18:00

        Estadia estadia = new Estadia();
        estadia.setValorEstadia(10000.0);
        Habitacion habitacion = new Habitacion();
        habitacion.setCostoPorNoche(10000.0F);
        estadia.setHabitacion(habitacion);

        PersonaFisica pf = crearPersonaFisica(PosIva.CONSUMIDOR_FINAL);

        when(estadiaRepository.findById(idEstadia)).thenReturn(Optional.of(estadia));
        when(responsablePagoRepository.findById(idResponsable)).thenReturn(Optional.of(pf));

        // ACT
        DtoDetalleFacturacion detalle = facturaService.calcularDetalle(idEstadia, idResponsable, horaSalida);

        // ASSERT
        // Recargo esperado: 100% de 10.000 = 10.000
        assertEquals(10000.0, detalle.getRecargoHorario());
        assertEquals("Late Check-out (Día completo)", detalle.getDetalleRecargo());
    }

    /**
     * <b>Regla de Negocio (Fiscal):</b>
     * Responsable Inscripto genera Factura A con 21% de IVA.
     */
    @Test
    @DisplayName("Calculadora - Fiscal: Debe generar Factura A y 21% IVA para Resp. Inscripto")
    void calcularDetalle_IVA_ResponsableInscripto() {
        // ARRANGE
        PersonaFisica pf = crearPersonaFisica(PosIva.RESPONSABLE_INSCRIPTO);
        Estadia estadia = new Estadia();
        estadia.setValorEstadia(100.0); // Base fácil para porcentaje
        estadia.setHabitacion(new Habitacion()); // Para evitar null pointer en getCostoNoche
        estadia.getHabitacion().setCostoPorNoche(100.0F);

        when(estadiaRepository.findById(anyInt())).thenReturn(Optional.of(estadia));
        when(responsablePagoRepository.findById(anyInt())).thenReturn(Optional.of(pf));

        // ACT
        DtoDetalleFacturacion detalle = facturaService.calcularDetalle(1, 1, "10:00"); // Hora ok

        // ASSERT
        assertEquals(TipoFactura.A, detalle.getTipoFactura());
        assertEquals(21.0, detalle.getMontoIva()); // 21% de 100
        assertEquals(121.0, detalle.getMontoTotal());
    }

    /**
     * <b>Regla de Negocio (Fiscal):</b>
     * Monotributista genera Factura B con 7% de IVA (según lógica de tu servicio).
     */
    @Test
    @DisplayName("Calculadora - Fiscal: Debe generar Factura B y 7% IVA para Monotributista")
    void calcularDetalle_IVA_Monotributista() {
        // ARRANGE
        PersonaFisica pf = crearPersonaFisica(PosIva.MONOTRIBUTISTA);
        Estadia estadia = new Estadia();
        estadia.setValorEstadia(100.0);
        estadia.setHabitacion(new Habitacion());
        estadia.getHabitacion().setCostoPorNoche(100.0F);

        when(estadiaRepository.findById(anyInt())).thenReturn(Optional.of(estadia));
        when(responsablePagoRepository.findById(anyInt())).thenReturn(Optional.of(pf));

        // ACT
        DtoDetalleFacturacion detalle = facturaService.calcularDetalle(1, 1, "10:00");

        // ASSERT
        assertEquals(TipoFactura.B, detalle.getTipoFactura());
        assertEquals(7.0, detalle.getMontoIva()); // 7% de 100
    }

    // ============================================================================================
    // GENERACIÓN DE FACTURA (Check-out)
    // ============================================================================================

    /**
     * <b>Caso Feliz:</b> Generar factura por primera vez.
     * Verifica que se genere el número correlativo correcto y se cierre la estadía.
     */
    @Test
    @DisplayName("Generar Factura - Éxito: Crea numero correlativo y cierra estadía")
    void generarFactura_HappyPath() throws Exception {
        // ARRANGE
        DtoFactura dto = new DtoFactura();
        dto.setIdEstadia(new DtoEstadiaSimple());
        dto.getIdEstadia().setIdEstadia(50);
        dto.getIdResponsable().setIdResponsable(99);
        dto.setImporteTotal(12100.0); // Total con IVA

        // Mock Estadía y Responsable
        Estadia estadiaSpy = spy(new Estadia());
        when(estadiaRepository.findById(50)).thenReturn(Optional.of(estadiaSpy));

        // Mock Responsable (Persona Fisica Consumidor Final)
        PersonaFisica pf = crearPersonaFisica(PosIva.CONSUMIDOR_FINAL);
        when(responsablePagoRepository.findById(99)).thenReturn(Optional.of(pf));

        // Mock Numeración: La última fue la 0005-00000020
        Factura ultimaFactura = new Factura();
        ultimaFactura.setNumeroFactura("0005-00000020");
        when(facturaRepository.findTopByOrderByNumeroFacturaDesc()).thenReturn(ultimaFactura);

        // ACT
        DtoFactura resultado = facturaService.generarFactura(dto);

        // ASSERT
        // 1. Verificamos que el número siga la secuencia (+1)
        assertEquals("0005-00000021", resultado.getNumeroFactura());

        // 2. Verificamos que se haya guardado con estado PENDIENTE
        verify(facturaRepository).save(argThat(f ->
                f.getEstadoFactura() == EstadoFactura.PENDIENTE &&
                        f.getNumeroFactura().equals("0005-00000021")
        ));

        // 3. Verificamos que se haya cerrado la estadía (Set fecha check-out)
        verify(estadiaSpy).setFechaCheckOut(any(Date.class));
        verify(estadiaRepository).save(estadiaSpy);
    }

    // --- HELPER ---
    private PersonaFisica crearPersonaFisica(PosIva posIva) {
        PersonaFisica pf = new PersonaFisica();
        Huesped h = new Huesped();
        h.setPosicionIva(posIva);
        h.setApellido("Test");
        h.setNombres("User");
        pf.setHuesped(h);
        return pf;
    }
}