package Facultad.pruebas.Services;

import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoDireccion;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoHuesped;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoHuespedBusqueda;
import Facultad.TrabajoPracticoDesarrollo.Dominio.*;
import Facultad.TrabajoPracticoDesarrollo.Repositories.*;
import Facultad.TrabajoPracticoDesarrollo.Services.HuespedService;
import Facultad.TrabajoPracticoDesarrollo.enums.PosIva;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link HuespedService}.
 * <p>
 * Covers business logic for:
 * <ul>
 * <li><b>CU02:</b> Search Guest (Filter logic).</li>
 * <li><b>CU09:</b> Register Guest (Upsert logic).</li>
 * <li><b>CU10:</b> Modify Guest (Updates, Identity changes, Address handling).</li>
 * <li><b>CU11:</b> Delete Guest (Validation of history/invoices).</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class HuespedServiceTest {

    @Mock private HuespedRepository huespedRepository;
    @Mock private DireccionRepository direccionRepository;
    @Mock private ReservaRepository reservaRepository;
    @Mock private EstadiaRepository estadiaRepository;
    @Mock private FacturaRepository facturaRepository;
    @Mock private PersonaFisicaRepository personaFisicaRepository;
    @Mock private EstadiaHuespedRepository estadiaHuespedRepository;
    @Mock private EntityManager entityManager;

    @InjectMocks
    private HuespedService huespedService;

    // ============================================================================================
    // CU02: SEARCH GUEST
    // ============================================================================================

    @Test
    @DisplayName("CU02 - Search: Should call custom query when filters are present")
    void buscarHuespedes_WithFilters() {
        // ARRANGE
        DtoHuespedBusqueda criteria = new DtoHuespedBusqueda();
        criteria.setApellido("Doe");
        criteria.setNroDocumento("12345");

        when(huespedRepository.buscarPorCriterios("Doe", null, null, "12345"))
                .thenReturn(List.of(new Huesped()));

        // ACT
        List<Huesped> result = huespedService.buscarHuespedes(criteria);

        // ASSERT
        assertFalse(result.isEmpty());
        verify(huespedRepository).buscarPorCriterios("Doe", null, null, "12345");
        verify(huespedRepository, never()).findAll();
    }

    @Test
    @DisplayName("CU02 - Search: Should return all guests when criteria is null or empty")
    void buscarHuespedes_NoFilters() {
        // ARRANGE
        when(huespedRepository.findAll()).thenReturn(List.of(new Huesped(), new Huesped()));

        // ACT
        List<Huesped> result = huespedService.buscarHuespedes(new DtoHuespedBusqueda());

        // ASSERT
        assertEquals(2, result.size());
        verify(huespedRepository).findAll();
    }

    // ============================================================================================
    // VALIDATIONS
    // ============================================================================================

    @Test
    @DisplayName("Validation: Should fail if CUIT is missing for Responsable Inscripto")
    void validarDatosHuesped_MissingCuit() {
        // ARRANGE
        DtoHuesped dto = new DtoHuesped();
        dto.setPosicionIva(PosIva.RESPONSABLE_INSCRIPTO);
        dto.setCuit(""); // Empty CUIT

        // ACT
        List<String> errors = huespedService.validarDatosHuesped(dto);

        // ASSERT
        assertFalse(errors.isEmpty());
        assertTrue(errors.contains("El CUIT es obligatorio para Responsables Inscriptos."));
    }

    // ============================================================================================
    // CU09: REGISTER/UPSERT GUEST
    // ============================================================================================

    @Test
    @DisplayName("CU09 - Upsert: Should create NEW guest and address if not exists")
    void upsertHuesped_NewGuest() {
        // ARRANGE
        DtoHuesped dto = createValidDto("11111111");

        // Mock: Guest does not exist
        when(huespedRepository.findById(any())).thenReturn(Optional.empty());

        // ACT
        huespedService.upsertHuesped(dto);

        // ASSERT
        // Verify address is saved first
        verify(direccionRepository).save(any(Direccion.class));
        // Verify guest is saved
        verify(huespedRepository).save(any(Huesped.class));
    }

    @Test
    @DisplayName("CU09 - Upsert: Should UPDATE existing guest and address")
    void upsertHuesped_ExistingGuest() {
        // ARRANGE
        DtoHuesped dto = createValidDto("11111111");
        Huesped existingHuesped = new Huesped();
        existingHuesped.setDireccion(new Direccion());

        // Mock: Guest exists
        when(huespedRepository.findById(any())).thenReturn(Optional.of(existingHuesped));

        // ACT
        huespedService.upsertHuesped(dto);

        // ASSERT
        verify(huespedRepository).saveAndFlush(existingHuesped);
        // Should update address, so save on address repo is called
        verify(direccionRepository).save(any(Direccion.class));
    }

    // ============================================================================================
    // CU10: MODIFY GUEST (Complex Scenarios)
    // ============================================================================================

    @Test
    @DisplayName("CU10 - Modify: Simple update (Same ID, Exclusive Address)")
    void modificarHuesped_SimpleUpdate() {
        // ARRANGE
        String docNum = "22222222";
        DtoHuesped dto = createValidDto(docNum); // Same ID

        Huesped huesped = new Huesped();
        Direccion direccion = new Direccion();
        direccion.setId(1);
        huesped.setDireccion(direccion);

        when(huespedRepository.findById(any())).thenReturn(Optional.of(huesped));
        // Mock: Only 1 person uses this direccion
        when(huespedRepository.countByDireccion(direccion)).thenReturn(1L);

        // ACT
        huespedService.modificarHuesped(TipoDocumento.DNI.name(), docNum, dto);

        // ASSERT
        verify(huespedRepository).save(huesped);
        // Address is updated in place
        verify(direccionRepository).save(direccion);
    }

    @Test
    @DisplayName("CU10 - Modify: Shared Address (Copy-on-Write strategy)")
    void modificarHuesped_SharedAddress() {
        // ARRANGE
        String docNum = "33333333";
        DtoHuesped dto = createValidDto(docNum);

        Huesped huesped = new Huesped();
        Direccion direccion = new Direccion();
        direccion.setId(5); // Shared ID
        huesped.setDireccion(direccion);

        when(huespedRepository.findById(any())).thenReturn(Optional.of(huesped));
        // Mock: 3 people use this address
        when(huespedRepository.countByDireccion(direccion)).thenReturn(3L);

        // ACT
        huespedService.modificarHuesped(TipoDocumento.DNI.name(), docNum, dto);

        // ASSERT
        // Should save a NEW address instance, not the shared one
        verify(direccionRepository).save(argThat(dir -> dir.getId() == null || dir.getId() != 5));
        verify(huespedRepository).save(huesped);
    }

    @Test
    @DisplayName("CU10 - Modify: Identity Change (Migration/Fusion)")
    void modificarHuesped_Fusion_TargetExists() {
        // ARRANGE
        String oldDoc = "100";
        String newDoc = "200";
        DtoHuesped dtoNew = createValidDto(newDoc);

        Huesped oldHuesped = new Huesped();
        Huesped targetHuesped = new Huesped(); // The one that stays

        // 1. Find Old
        when(huespedRepository.findById(new HuespedId(TipoDocumento.DNI, oldDoc)))
                .thenReturn(Optional.of(oldHuesped));

        // 2. Find New (Target exists -> Fusion)
        when(huespedRepository.findById(new HuespedId(TipoDocumento.DNI, newDoc)))
                .thenReturn(Optional.of(targetHuesped));

        // Mock PersonaFisica check (simple case, no fiscal role)
        when(personaFisicaRepository.findByHuesped(any())).thenReturn(Optional.empty());

        // ACT
        huespedService.modificarHuesped(TipoDocumento.DNI.name(), oldDoc, dtoNew);

        // ASSERT
        // Verify migration logic
        verify(reservaRepository).migrarReservas(eq(TipoDocumento.DNI), eq(oldDoc), any(), any(), any(), any(), any());
        verify(estadiaHuespedRepository).migrarHistorialEstadias(eq(TipoDocumento.DNI.name()), eq(oldDoc), any(), any());

        // Verify deletion of old guest
        verify(huespedRepository).borrarObligatorio(TipoDocumento.DNI.name(), oldDoc);
    }

    // ============================================================================================
    // CU11: DELETE GUEST
    // ============================================================================================

    @Test
    @DisplayName("CU11 - Delete: Should delete if no history exists")
    void darDeBajaHuesped_Success() {
        // ARRANGE
        Huesped huesped = new Huesped();
        when(huespedRepository.findById(any())).thenReturn(Optional.of(huesped));

        // No history
        when(estadiaHuespedRepository.existsByHuesped(huesped)).thenReturn(false);
        // No fiscal role
        when(personaFisicaRepository.findByHuesped(huesped)).thenReturn(Optional.empty());

        // ACT
        huespedService.darDeBajaHuesped("DNI", "123");

        // ASSERT
        verify(reservaRepository).deleteByHuesped(any(), any());
        verify(huespedRepository).delete(huesped);
        verify(huespedRepository).flush();
    }

    @Test
    @DisplayName("CU11 - Delete: Should fail if guest has stay history")
    void darDeBajaHuesped_Fail_WithHistory() {
        // ARRANGE
        Huesped huesped = new Huesped();
        when(huespedRepository.findById(any())).thenReturn(Optional.of(huesped));

        // Has history
        when(estadiaHuespedRepository.existsByHuesped(huesped)).thenReturn(true);

        // ACT & ASSERT
        Exception ex = assertThrows(RuntimeException.class, () ->
                huespedService.darDeBajaHuesped("DNI", "123")
        );
        assertEquals("El huésped no puede ser eliminado pues se ha alojado en el Hotel.", ex.getMessage());

        verify(huespedRepository, never()).delete(any());
    }

    @Test
    @DisplayName("CU11 - Delete: Should fail if guest has invoices associated")
    void darDeBajaHuesped_Fail_WithInvoices() {
        // ARRANGE
        Huesped huesped = new Huesped();
        when(huespedRepository.findById(any())).thenReturn(Optional.of(huesped));
        when(estadiaHuespedRepository.existsByHuesped(huesped)).thenReturn(false);

        // Is a fiscal person
        PersonaFisica pf = new PersonaFisica();
        when(personaFisicaRepository.findByHuesped(huesped)).thenReturn(Optional.of(pf));

        // Has invoices
        when(facturaRepository.existsByResponsablePago(pf)).thenReturn(true);

        // ACT & ASSERT
        Exception ex = assertThrows(RuntimeException.class, () ->
                huespedService.darDeBajaHuesped("DNI", "123")
        );
        assertTrue(ex.getMessage().contains("tiene facturas asociadas"));

        verify(huespedRepository, never()).delete(any());
    }

    // --- HELPER ---
    private DtoHuesped createValidDto(String doc) {
        DtoHuesped dto = new DtoHuesped();
        dto.setTipoDocumento(TipoDocumento.DNI);
        dto.setNroDocumento(doc);
        dto.setApellido("Test");
        dto.setNombres("User");

        DtoDireccion dir = new DtoDireccion();
        dir.setCalle("Street");
        dir.setNumero(123);
        dto.setDtoDireccion(dir);

        return dto;
    }
}