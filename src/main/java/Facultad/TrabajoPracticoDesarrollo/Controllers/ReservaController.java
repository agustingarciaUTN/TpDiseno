package Facultad.TrabajoPracticoDesarrollo.Controllers;

import Facultad.TrabajoPracticoDesarrollo.Huesped.GestorHuesped;
import Facultad.TrabajoPracticoDesarrollo.Reserva.DtoReserva;
import Facultad.TrabajoPracticoDesarrollo.Reserva.GestorReserva;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
@Validated // Necesario para validar Listas en el root del body
public class ReservaController {

    private final GestorReserva gestorReserva;
    // 2. Inyección por Constructor (Spring te pasa el Gestor listo)
    public ReservaController(GestorReserva gestorReserva) {
        this.gestorReserva = gestorReserva;
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearReserva(@RequestBody List<@Valid DtoReserva> reservas) {
        try {
            if (reservas == null || reservas.isEmpty()) {
                return ResponseEntity.badRequest().body("Debe enviar al menos una reserva.");
            }

            // --- NUEVA VALIDACIÓN MANUAL DE FECHAS ---
            // Usamos LocalDate para comparar solo DÍA, MES y AÑO (ignorando la hora)
            java.time.LocalDate hoy = java.time.LocalDate.now();
            for (DtoReserva res : reservas) {
                // Convertimos la fechaDesde (Date) a LocalDate
                java.time.LocalDate fechaIngreso = res.getFechaDesde().toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate();

                // Si la fecha de ingreso es ANTERIOR a hoy, error.
                if (fechaIngreso.isBefore(hoy)) {
                    return ResponseEntity.badRequest().body("Error: La fecha de ingreso (" + fechaIngreso + ") no puede ser anterior a hoy." + hoy);
                }
            }
            // -----------------------------------------

            System.out.println("Recibida petición de reserva para " + reservas.size() + " habitaciones.");
            gestorReserva.crearReservas(reservas);

            return ResponseEntity.ok("✅ Reservas registradas con éxito.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error al reservar: " + e.getMessage());
        }
    }


    @GetMapping("/hola")
    public String saludar() {
        return "¡El Backend de Reservas está activo!";
    }
}