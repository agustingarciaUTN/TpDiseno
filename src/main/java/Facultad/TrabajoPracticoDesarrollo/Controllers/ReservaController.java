package Facultad.TrabajoPracticoDesarrollo.Controllers;

import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoReserva;
import Facultad.TrabajoPracticoDesarrollo.Services.EstadiaService;
import Facultad.TrabajoPracticoDesarrollo.Services.ReservaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
@Validated
public class ReservaController {

    private final ReservaService reservaService;
    private final EstadiaService estadiaService;

    @Autowired
    public ReservaController(ReservaService reservaService, EstadiaService estadiaService) {
        this.reservaService = reservaService;
        this.estadiaService = estadiaService;
    }

    // Buscar reservas en un rango (opcionalmente por habitación)
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarReservas(
            @RequestParam String fechaDesde,
            @RequestParam String fechaHasta,
            @RequestParam(required = false) String idHabitacion
    ) {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            Date desde = sdf.parse(fechaDesde);
            Date hasta = sdf.parse(fechaHasta);

            List<DtoReserva> reservas = reservaService.buscarReservasEnFecha(desde, hasta);
        if (idHabitacion != null && !idHabitacion.isEmpty()) {
            reservas = reservas.stream()
                    .filter(r -> idHabitacion.equals(r.getIdHabitacion()))
                    .toList();
        }
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al parsear fechas: " + e.getMessage());
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearReservas(@RequestBody List<DtoReserva> listaReservas) {
        try {
            // Pasamos la lista completa al Service
            reservaService.crearReservas(listaReservas);
            return ResponseEntity.ok("Reservas creadas con éxito");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }


    // GET /api/reservas/disponibilidad?habitacion=1&desde=2023-10-01&hasta=2023-10-05
    @GetMapping("/disponibilidad")
    public ResponseEntity<?> verificarDisponibilidad(
            @RequestParam String idHabitacion,
            @RequestParam Date fechaDesde,
            @RequestParam Date fechaHasta) {

        // 1. Llamada a GR (ReservaService)
        boolean libreDeReservas = reservaService.validarDisponibilidad(idHabitacion, fechaDesde, fechaHasta);

        // 2. Llamada a GE (EstadiaService) - Necesitás inyectar EstadiaService en este Controller
        boolean libreDeEstadias = estadiaService.validarDisponibilidad(idHabitacion, fechaDesde, fechaHasta);

        if (libreDeReservas && libreDeEstadias) {
            return ResponseEntity.ok(true); // Disponible
        } else {
            return ResponseEntity.ok(false); // Ocupada (Conflict)
        }
    }



    @GetMapping("/buscar-huesped")
    public ResponseEntity<?> buscarPorHuesped(
            @RequestParam String apellido,
            @RequestParam(required = false) String nombre
    ) {
        try {
            List<DtoReserva> resultado = reservaService.buscarReservasPorHuesped(apellido, nombre);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error en la búsqueda: " + e.getMessage());
        }
    }


    @PostMapping("/cancelar")
    public ResponseEntity<?> cancelarReservas(@RequestBody List<Integer> idsReservas) {
        try {
            reservaService.cancelarReservas(idsReservas);
            return ResponseEntity.ok("Reservas canceladas correctamente.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al cancelar: " + e.getMessage());
        }
    }

}
