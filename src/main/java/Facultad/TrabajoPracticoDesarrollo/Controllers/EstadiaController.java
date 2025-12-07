package Facultad.TrabajoPracticoDesarrollo.Controllers;

import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoEstadia;
import Facultad.TrabajoPracticoDesarrollo.Services.Gestores.GestorEstadia;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/estadias")
@CrossOrigin(origins = "*")
public class EstadiaController {

    // Usamos el GestorSingleton tal como está en tu arquitectura actual
    private final GestorEstadia gestorEstadia;
    // 2. Inyección por Constructor (Spring te pasa el Gestor listo)
    public EstadiaController(GestorEstadia gestorEstadia) {
        this.gestorEstadia = gestorEstadia;
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearEstadia(@Valid @RequestBody DtoEstadia dtoEstadia) {
        try {
            // Validaciones de negocio manuales (fechas)
            // Ejemplo: CheckOut no puede ser anterior a CheckIn
            if (dtoEstadia.getFechaCheckOut() != null && dtoEstadia.getFechaCheckIn() != null) {
                if (dtoEstadia.getFechaCheckOut().before(dtoEstadia.getFechaCheckIn())) {
                    return ResponseEntity.badRequest().body("Error: La fecha de salida no puede ser anterior a la de entrada.");
                }
            }

            System.out.println("Iniciando Check-In para habitación: " +
                    (dtoEstadia.getDtoHabitacion() != null ? dtoEstadia.getDtoHabitacion().getNumero() : "S/N"));

            gestorEstadia.crearEstadia(dtoEstadia);

            return ResponseEntity.ok("✅ Estadía (Check-In) registrada correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
            // Captura errores como "El huésped ya está alojado en otra habitación" que lanza el Gestor
            return ResponseEntity.badRequest().body("Error en el Check-In: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public String test() {
        return "Controller de Estadías activo";
    }
}