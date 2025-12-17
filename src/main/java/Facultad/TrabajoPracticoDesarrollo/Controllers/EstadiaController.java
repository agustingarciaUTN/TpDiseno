package Facultad.TrabajoPracticoDesarrollo.Controllers;

import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoEstadia;
import Facultad.TrabajoPracticoDesarrollo.Services.EstadiaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para operaciones relacionadas con estadías (check-in / check-out).
 *
 * <p>Expone endpoints bajo la ruta {@code /api/estadias} para:
 * - registrar una nueva estadía (check-in),
 * - endpoint de prueba para verificar que el controller está activo.</p>
 *
 * <p>Realiza validaciones básicas de formato (por ejemplo validación de fechas)
 * y delega la lógica de negocio al {@link EstadiaService}.</p>
 */
@RestController
@RequestMapping("/api/estadias")
@CrossOrigin(origins = "*")
public class EstadiaController {

    private final EstadiaService estadiaService;

    /**
     * Construye el controlador inyectando el servicio de estadías.
     *
     * @param estadiaService servicio que contiene la lógica de negocio para estadías
     */
    @Autowired
    public EstadiaController(EstadiaService estadiaService) {
        this.estadiaService = estadiaService;
    }

    /**
     * Registra una nueva estadía (Check-In).
     *
     * <p>POST /api/estadias/crear</p>
     *
     * @param dtoEstadia DTO validado con los datos de la estadía (fechas, habitación, huésped, etc.)
     * @return {@code 200 OK} con mensaje de éxito si la estadía se registra correctamente,
     *         {@code 400 Bad Request} con mensaje de error si las fechas son inválidas o ocurre un error.
     */
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

    /**
     * Endpoint de prueba para verificar que el controlador está activo.
     *
     * <p>GET /api/estadias/test</p>
     *
     * @return texto indicando que el controller de estadías está activo.
     */
    @GetMapping("/test")
    public String test() {
        return "Controller de Estadías activo y usando JPA";
    }
}