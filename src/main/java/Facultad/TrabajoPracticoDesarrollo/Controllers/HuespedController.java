package Facultad.TrabajoPracticoDesarrollo.Controllers;

import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoHuesped;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Huesped;
import Facultad.TrabajoPracticoDesarrollo.Services.Gestores.GestorHuesped;
import Facultad.TrabajoPracticoDesarrollo.Services.HuespedService;
import Facultad.TrabajoPracticoDesarrollo.Utils.Mapear.MapearHuesped;
import jakarta.validation.Valid; // Importante
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController//Declarado como API, le dice a Spring que atiende pedidos web
@RequestMapping("/api/huespedes")
@CrossOrigin(origins = "*") // Permite peticiones desde cualquier Frontend (React/Angular/Postman)
public class HuespedController {

    //Aca le decimos a Spring que necesitamos un HuespedService para trabajar
    private final HuespedService huespedService;
    // 2. Inyección por Constructor (Spring te pasa el Gestor listo)
    public HuespedController(HuespedService huespedService) {
        this.huespedService = huespedService;
    }


    @PostMapping("/buscar")
    public ResponseEntity<List<Huesped>> buscarHuespedes(@RequestBody(required = false) DtoHuesped criterios) {
        try {

            if (criterios == null) {
                criterios = new DtoHuesped();
            }

            List<Huesped> listaEntidades = huespedService.buscarHuespedes(criterios);

            return ResponseEntity.ok(listaEntidades);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    @PostMapping("/crear")//Si alguien llama a la dirección base /api/huespedes PERO usando el verbo POST y agregando /crear, entra a este metodo.
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