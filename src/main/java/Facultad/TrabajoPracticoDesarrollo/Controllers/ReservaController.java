package Facultad.TrabajoPracticoDesarrollo.Controllers;

import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoReserva;
import Facultad.TrabajoPracticoDesarrollo.Services.ReservaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
@Validated
public class ReservaController {

    private final ReservaService reservaService;

    @Autowired
    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearReserva(@RequestBody List<@Valid DtoReserva> reservas) {
        try {
            // El Controller solo delega. La validación de fechas y negocio está en el Service.
            System.out.println("Recibida petición de reserva para " + reservas.size() + " habitaciones.");

            reservaService.crearReservas(reservas);

            return ResponseEntity.ok("✅ Reservas registradas con éxito.");

        } catch (Exception e) {
            e.printStackTrace();
            // Retornamos un error 400 con el mensaje que lanza el Service (ej: "Habitación ocupada")
            return ResponseEntity.badRequest().body("Error al reservar: " + e.getMessage());
        }
    }

    @GetMapping("/hola")
    public String saludar() {
        return "¡El Backend de Reservas está activo y usando JPA!";
    }
}