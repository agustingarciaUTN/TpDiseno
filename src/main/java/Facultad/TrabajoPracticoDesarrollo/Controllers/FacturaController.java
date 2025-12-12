package Facultad.TrabajoPracticoDesarrollo.Controllers;


import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoFactura;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoInicioFactura;
import Facultad.TrabajoPracticoDesarrollo.Services.FacturaService;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam boolean esTercero
    ) {
        try {
            int idResponsableFinal;

            if (!esTercero) {
                // 1. Es Huesped: Validamos edad usando clave compuesta
                if(tipoDoc == null || nroDoc == null) throw new IllegalArgumentException("Faltan datos del huésped");

                facturaService.validarResponsable(tipoDoc, nroDoc);

                // 2. Buscamos el ID de ResponsablePago asociado a ese huésped (PersonaFisica)
                idResponsableFinal = facturaService.buscarIdResponsablePorHuesped(tipoDoc, nroDoc);

            } else {
                // Es tercero (Empresa), usamos el ID directo
                if(idResponsableJuridico == null) throw new IllegalArgumentException("Falta ID de la empresa");
                idResponsableFinal = idResponsableJuridico;
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

}
