package Facultad.TrabajoPracticoDesarrollo.Controllers;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Huesped;
import Facultad.TrabajoPracticoDesarrollo.Huesped.DtoHuesped;
import Facultad.TrabajoPracticoDesarrollo.Huesped.GestorHuesped;
import jakarta.validation.Valid; // Importante
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/huespedes")
@CrossOrigin(origins = "*") // Permite peticiones desde cualquier Frontend (React/Angular/Postman)
public class HuespedController {

    private final GestorHuesped gestorHuesped;
    // 2. Inyección por Constructor (Spring te pasa el Gestor listo)
    public HuespedController(GestorHuesped gestorHuesped) {
        this.gestorHuesped = gestorHuesped;
    }

    @PostMapping("/buscar")
    public ResponseEntity<List<Huesped>> buscarHuesped(@Valid @RequestBody(required = false) DtoHuesped criterios) {
        try {
            // Manejo de criterios nulos (para traer todos si no se envía body)
            if (criterios == null) {
                criterios = new DtoHuesped();
            }
            ArrayList<Huesped> listaHuespedes = gestorHuesped.buscarHuespedes(criterios);

            return ResponseEntity.ok(listaHuespedes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }

    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearHuesped(@Valid @RequestBody DtoHuesped dtoHuesped) {
        try {
            // Si llega a esta línea, es porque el DTO YA PASÓ todas las validaciones de formato (@NotNull, Regex, etc)
            // Si falló alguna, el GlobalExceptionHandler ya lo interceptó antes.

            gestorHuesped.upsertHuesped(dtoHuesped);

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