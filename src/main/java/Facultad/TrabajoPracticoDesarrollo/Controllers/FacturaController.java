package Facultad.TrabajoPracticoDesarrollo.Controllers;


import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoFactura;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoInicioFactura;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoPersonaJuridica;
import Facultad.TrabajoPracticoDesarrollo.Services.FacturaService;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;

@RestController
@RequestMapping("/api/factura")
@CrossOrigin(origins = "*")
public class FacturaController {

    private final FacturaService facturaService;

    public FacturaController(FacturaService service) {
        this.facturaService = service;
    }

    // PASO 1: Buscar Ocupantes
    @PostMapping("/buscar-ocupantes")
    public ResponseEntity<?> buscarOcupantes(@Valid @RequestBody DtoInicioFactura datos) {
        try {
            return ResponseEntity.ok(facturaService.buscarOcupantes(datos.getNumeroHabitacion()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/calcular-detalle")
    public ResponseEntity<?> calcularDetalle(
            @RequestParam int idEstadia,
            @RequestParam(required = false) TipoDocumento tipoDoc,
            @RequestParam(required = false) String nroDoc,
            @RequestParam(required = false) Integer idResponsableJuridico, // ID numérico si es empresa
            @RequestParam String horaSalida,
            @RequestParam boolean esTercero,
            @RequestParam(required = false) String cuit
    ) {
        try {
            int idResponsableFinal;

            if (esTercero) {
                // Si no viene ID, interrumpimos el flujo (tu solución anterior)
                if (idResponsableJuridico == null) {

                    // Intentamos buscarlo en la BD por CUIT
                    Integer idEncontrado = facturaService.buscarIdPorCuit(cuit);

                    if (idEncontrado != null) {
                        idResponsableFinal = idEncontrado;
                    } else {
                        // Si es null, significa que NO existe en la BD -> Mandamos a crear
                        return ResponseEntity.status(409)
                                .body(java.util.Collections.singletonMap("accion", "REDIRECCIONAR_A_ALTA_RESPONSABLE"));
                    }
                } else {
                    idResponsableFinal = idResponsableJuridico;
                }

            } else {
                // 1. Es Huesped: Validamos edad usando clave compuesta
                if(tipoDoc == null || nroDoc == null) throw new IllegalArgumentException("Faltan datos del huésped");

                facturaService.validarResponsable(tipoDoc, nroDoc);

                // 2. Buscamos el ID de ResponsablePago asociado a ese huésped (PersonaFisica)
                idResponsableFinal = facturaService.obtenerOAltaPersonaFisica(tipoDoc, nroDoc);
            }

            return ResponseEntity.ok(facturaService.calcularDetalle(idEstadia, idResponsableFinal, horaSalida));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/generar")
    public ResponseEntity<?> generarFactura(@Valid @RequestBody DtoFactura facturaFinal) {
        try {
            return ResponseEntity.ok(facturaService.generarFactura(facturaFinal));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/responsable")
    public ResponseEntity<?> crearResponsable(@RequestBody @Valid DtoPersonaJuridica nuevoResponsable) {
        try {
            // Llamamos al servicio para guardar
            int nuevoId = facturaService.guardarResponsableJuridico(nuevoResponsable);

            // Devolvemos el ID para que el frontend pueda volver a llamar a calcular-detalle
            return ResponseEntity.ok(java.util.Collections.singletonMap("idResponsableGenerado", nuevoId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear responsable: " + e.getMessage());
        }
    }

}
