package Facultad.TrabajoPracticoDesarrollo.Controllers;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Habitacion;
import Facultad.TrabajoPracticoDesarrollo.Services.HabitacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para operaciones relacionadas con habitaciones.
 *
 * <p>Expone endpoints bajo la ruta {@code /api/habitaciones} para:
 * - obtener la lista completa de habitaciones ordenadas,
 * - buscar una habitación por su número,
 * - obtener el estado de habitaciones en un rango de fechas.</p>
 *
 * <p>Se permite acceso desde cualquier origen mediante CORS.</p>
 */
@RestController
@RequestMapping("/api/habitaciones")
@CrossOrigin(origins = "*")
public class HabitacionController {

    private final HabitacionService habitacionService;


    /**
     * Construye el controlador inyectando el servicio de habitaciones.
     *
     * @param habitacionService servicio que contiene la lógica de negocio para habitaciones
     */
    @Autowired
    public HabitacionController(HabitacionService habitacionService) {
        this.habitacionService = habitacionService;
    }

    /**
     * Obtiene todas las habitaciones ordenadas.
     *
     * <p>GET /api/habitaciones</p>
     *
     * @return {@code 200 OK} con la lista de {@link Habitacion} si la operación fue exitosa,
     *         {@code 500 Internal Server Error} en caso de error inesperado.
     */
    @GetMapping
    public ResponseEntity<List<Habitacion>> obtenerTodas() {
        return ResponseEntity.ok(habitacionService.obtenerTodas());
    }


    /**
     * Busca una habitación por su número.
     *
     * <p>GET /api/habitaciones/{numero}</p>
     *
     * @param numero número identificador de la habitación
     * @return {@code 200 OK} con la {@link Habitacion} encontrada,
     *         {@code 404 Not Found} si no existe una habitación con ese número,
     *         {@code 400 Bad Request} si el parámetro es inválido.
     */
    @GetMapping("/{numero}")
    public ResponseEntity<?> obtenerPorNumero(@PathVariable String numero) {
        Habitacion hab = habitacionService.obtenerPorNumero(numero);
        if (hab != null) {
            return ResponseEntity.ok(hab);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtiene el estado de las habitaciones según un rango de fechas.
     *
     * <p>GET /api/habitaciones/estados?fechaDesde=YYYY-MM-DD&fechaHasta=YYYY-MM-DD</p>
     *
     * @param fechaDesde fecha de inicio en formato {@code YYYY-MM-DD}
     * @param fechaHasta fecha de fin en formato {@code YYYY-MM-DD}
     * @return {@code 200 OK} con el estado de habitaciones para el rango solicitado,
     *         {@code 400 Bad Request} si las fechas son inválidas o ocurre un error de parseo.
     */
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