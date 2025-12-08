package Facultad.TrabajoPracticoDesarrollo.Controllers;

import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoEstadia;
import Facultad.TrabajoPracticoDesarrollo.Services.EstadiaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/estadias")
@CrossOrigin(origins = "*")
public class EstadiaController {

    private final EstadiaService estadiaService;

    @Autowired
    public EstadiaController(EstadiaService estadiaService) {
        this.estadiaService = estadiaService;
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearEstadia(@Valid @RequestBody DtoEstadia dtoEstadia) {
        try {
            // Validaciones de formato fechas (Controller)
            if (dtoEstadia.getFechaCheckOut() != null && dtoEstadia.getFechaCheckIn() != null) {
                if (dtoEstadia.getFechaCheckOut().before(dtoEstadia.getFechaCheckIn())) {
                    return ResponseEntity.badRequest().body("Error: La fecha de salida no puede ser anterior a la de entrada.");
                }
            }

            System.out.println("Iniciando Check-In para habitación: " +
                    (dtoEstadia.getDtoHabitacion() != null ? dtoEstadia.getDtoHabitacion().getNumero() : "S/N"));

            // Delegamos al servicio la lógica pesada
            estadiaService.crearEstadia(dtoEstadia);

            return ResponseEntity.ok("✅ Estadía (Check-In) registrada correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error en el Check-In: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public String test() {
        return "Controller de Estadías activo y usando JPA";
    }
}