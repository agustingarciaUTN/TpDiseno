package Facultad.TrabajoPracticoDesarrollo.Controllers;

import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoHuesped;
import Facultad.TrabajoPracticoDesarrollo.Services.Gestores.GestorHuesped;
import Facultad.TrabajoPracticoDesarrollo.Services.HuespedService;
import jakarta.validation.Valid; // Importante
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/huespedes")
@CrossOrigin(origins = "*") // Permite peticiones desde cualquier Frontend (React/Angular/Postman)
public class HuespedController {

    private final HuespedService huespedService;
    // 2. Inyección por Constructor (Spring te pasa el Gestor listo)
    public HuespedController(HuespedService huespedService) {
        this.huespedService = huespedService;
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearHuesped(@Valid @RequestBody DtoHuesped dtoHuesped) {
        try {
            // Si llega a esta línea, es porque el DTO YA PASÓ todas las validaciones de formato (@NotNull, Regex, etc)
            // Si falló alguna, el GlobalExceptionHandler ya lo interceptó antes.

            huespedService.upsertHuesped(dtoHuesped);

            return ResponseEntity.ok("✅ Huésped guardado correctamente");

        } catch (Exception e) {
            // Capturamos errores de lógica de negocio (ej: base de datos caída)
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error al procesar: " + e.getMessage());
        }
    }

    // Ejemplo para probar que el controller vive
    @GetMapping("/test")
    public String test() {
        return "Controller de Huéspedes activo";
    }
}