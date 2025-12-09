package Facultad.TrabajoPracticoDesarrollo.Controllers;

import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoPago;
import Facultad.TrabajoPracticoDesarrollo.Services.PagoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "*")
public class PagoController {

    private final PagoService pagoService;

    @Autowired
    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarPago(@RequestBody @Valid DtoPago dtoPago) {
        try {
            pagoService.registrarPago(dtoPago);
            return ResponseEntity.ok("✅ Pago registrado exitosamente.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error al registrar pago: " + e.getMessage());
        }
    }
}