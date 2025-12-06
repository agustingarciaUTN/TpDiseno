package Facultad.TrabajoPracticoDesarrollo.Controllers; // ⚠️ AJUSTA ESTE PACKAGE A TU PROYECTO

import Facultad.TrabajoPracticoDesarrollo.Reserva.DtoReserva;
import Facultad.TrabajoPracticoDesarrollo.Reserva.GestorReserva;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Indica que esta clase atiende peticiones web
@RequestMapping("/api/reservas") // La dirección base: localhost:8080/api/reservas
@CrossOrigin(origins = "*") // Permite que Next.js se conecte sin bloqueos
public class ReservaController {

    // Llamamos a tu Gestor de siempre (Singleton)
    private final GestorReserva gestorReserva = GestorReserva.getInstance();

    // Endpoint: POST /api/reservas/crear
    // Este método reemplaza a la opción "Reservar" de tu menú viejo
    @PostMapping("/crear")
    public String crearReserva(@RequestBody List<DtoReserva> reservas) {
        try {
            System.out.println("Recibida petición de reserva: " + reservas.size() + " habitaciones.");

            // Llamamos a TU lógica original
            gestorReserva.crearReservas(reservas);

            return "✅ ¡Éxito! Reservas guardadas en la base de datos.";
        } catch (Exception e) {
            e.printStackTrace();
            // Devolvemos el error para que el Frontend sepa qué pasó
            throw new RuntimeException("Error al reservar: " + e.getMessage());
        }
    }

    // Endpoint de prueba: GET /api/reservas/hola
    @GetMapping("/hola")
    public String saludar() {
        return "¡El Backend está vivo y conectado!";
    }
}