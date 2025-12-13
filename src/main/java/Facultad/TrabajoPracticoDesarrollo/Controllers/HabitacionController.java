package Facultad.TrabajoPracticoDesarrollo.Controllers;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Habitacion;
import Facultad.TrabajoPracticoDesarrollo.Services.HabitacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/habitaciones")
@CrossOrigin(origins = "*")
public class HabitacionController {

    private final HabitacionService habitacionService;

    @Autowired
    public HabitacionController(HabitacionService habitacionService) {
        this.habitacionService = habitacionService;
    }

    // Endpoint para obtener todas las habitaciones ordenadas
    // GET /api/habitaciones
    @GetMapping
    public ResponseEntity<List<Habitacion>> obtenerTodas() {
        return ResponseEntity.ok(habitacionService.obtenerTodas());
    }

    // Endpoint para buscar una habitación por número
    // GET /api/habitaciones/{numero}
    @GetMapping("/{numero}")
    public ResponseEntity<?> obtenerPorNumero(@PathVariable String numero) {
        Habitacion hab = habitacionService.obtenerPorNumero(numero);
        if (hab != null) {
            return ResponseEntity.ok(hab);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint para obtener habitaciones con estado según fechas
    // GET /api/habitaciones/estado?fechaDesde=YYYY-MM-DD&fechaHasta=YYYY-MM-DD
    @GetMapping("/estados")
    public ResponseEntity<?> obtenerEstadoPorFechas(
            @RequestParam String fechaDesde,
            @RequestParam String fechaHasta) {
        try {
            return ResponseEntity.ok(habitacionService.obtenerEstadoPorFechas(fechaDesde, fechaHasta));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al obtener estado: " + e.getMessage());
        }
    }
}