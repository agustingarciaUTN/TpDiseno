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


/**
 * Controlador REST para operaciones sobre reservas.
 *
 * <p>Expone endpoints bajo la ruta {@code /api/reservas} para:
 * - buscar reservas por rango de fechas (y opcionalmente por habitación),
 * - crear múltiples reservas,
 * - verificar disponibilidad combinando reservas y estadías,
 * - buscar reservas por huésped,
 * - cancelar reservas por lista de IDs.</p>
 *
 * <p>Se permite acceso desde cualquier origen mediante CORS.</p>
 */
@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
@Validated
public class ReservaController {

    private final ReservaService reservaService;
    private final EstadiaService estadiaService;

    /**
     * Construye el controlador inyectando los servicios necesarios.
     *
     * @param reservaService servicio que maneja la lógica de reservas
     * @param estadiaService servicio que maneja la lógica de estadías
     */
    @Autowired
    public ReservaController(ReservaService reservaService, EstadiaService estadiaService) {
        this.reservaService = reservaService;
        this.estadiaService = estadiaService;
    }


    /**
     * Busca reservas dentro de un rango de fechas.
     *
     * <p>Parámetros esperados como {@code String} en formato {@code yyyy-MM-dd}:
     * {@code fechaDesde} y {@code fechaHasta}. Opcionalmente se puede filtrar
     * por {@code idHabitacion}.</p>
     *
     * @param fechaDesde fecha de inicio en formato {@code yyyy-MM-dd}
     * @param fechaHasta fecha de fin en formato {@code yyyy-MM-dd}
     * @param idHabitacion (opcional) id de la habitación para filtrar resultados
     * @return {@code 200 OK} con la lista de {@link DtoReserva} encontrada,
     *         o {@code 400 Bad Request} si ocurre error en el parseo de fechas.
     */
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

    /**
     * Crea múltiples reservas recibidas en el cuerpo de la petición.
     *
     * <p>Recibe una lista JSON de {@link DtoReserva} en el {@code RequestBody}.</p>
     *
     * @param listaReservas lista de reservas a crear
     * @return {@code 200 OK} con mensaje de éxito, o {@code 400 Bad Request}
     *         con el detalle del error si falla la operación.
     */
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


    /**
     * Verifica la disponibilidad de una habitación en un rango de fechas.
     *
     * <p>Combina la validación de {@link ReservaService} y {@link EstadiaService}
     * para determinar si la habitación está libre.</p>
     *
     * @param idHabitacion id de la habitación a consultar
     * @param fechaDesde fecha de inicio (se obtiene como {@link Date} desde el request)
     * @param fechaHasta fecha de fin (se obtiene como {@link Date} desde el request)
     * @return {@code 200 OK} con {@code true} si está disponible, {@code false} si está ocupada.
     */
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


    /**
     * Busca reservas por apellido de huésped y opcionalmente por nombre.
     *
     * @param apellido apellido del huésped (obligatorio)
     * @param nombre nombre del huésped (opcional)
     * @return {@code 200 OK} con la lista de {@link DtoReserva} que coinciden,
     *         o {@code 400 Bad Request} si ocurre un error durante la búsqueda.
     */
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

    /**
     * Cancela reservas por su lista de IDs.
     *
     * <p>Recibe una lista JSON de enteros con los IDs de reservas a cancelar.</p>
     *
     * @param idsReservas lista de IDs de reservas a cancelar
     * @return {@code 200 OK} con mensaje de éxito, o {@code 400 Bad Request} con el error.
     */
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
