package Facultad.TrabajoPracticoDesarrollo.Controllers;

import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoHuesped;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoHuespedBusqueda;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Huesped;
import Facultad.TrabajoPracticoDesarrollo.Services.HuespedService;
import Facultad.TrabajoPracticoDesarrollo.Utils.Mapear.MapearHuesped;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
import jakarta.validation.Valid; // Importante
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Controlador REST para operaciones sobre huéspedes.
 *
 * <p>Expone endpoints bajo la ruta {@code /api/huespedes} para:
 * - buscar huéspedes por criterios,
 * - verificar existencia por tipo y número de documento,
 * - crear, modificar y borrar huéspedes,
 * - endpoint de prueba para verificar que el controller está activo.</p>
 *
 * <p>Se permite acceso desde cualquier origen mediante CORS.</p>
 */
@RestController//Declarado como API, le dice a Spring que atiende pedidos web
@RequestMapping("/api/huespedes")
@CrossOrigin(origins = "*") // Permite peticiones desde cualquier Frontend (React/Angular/Postman)
public class HuespedController {

    //Aca le decimos a Spring que necesitamos un HuespedService para trabajar
    private final HuespedService huespedService;

    /**
     * Construye el controlador inyectando el servicio de huéspedes.
     *
     * @param huespedService servicio que contiene la lógica de negocio para huéspedes
     */
    public HuespedController(HuespedService huespedService) {
        this.huespedService = huespedService;
    }


    /**
     * Busca huéspedes según criterios opcionales.
     *
     * <p>Recibe un {@link DtoHuespedBusqueda} en el cuerpo de la petición. Si el cuerpo es {@code null},
     * se consideran criterios por defecto (todas las coincidencias).</p>
     *
     * @param criterios criterios de búsqueda (opcional)
     * @return {@code 200 OK} con la lista de {@link DtoHuesped} que cumplen los criterios,
     *         o {@code 500 Internal Server Error} en caso de error inesperado.
     */
    @PostMapping("/buscar")
    public ResponseEntity<List<DtoHuesped>> buscarHuespedes(@RequestBody(required = false) DtoHuespedBusqueda criterios) {
        try {

            if (criterios == null) {
                criterios = new DtoHuespedBusqueda();
            }

            List<Huesped> listaEntidades = huespedService.buscarHuespedes(criterios);

            // 2. Controller convierte a DTOs (Falta este paso)
            List<DtoHuesped> dtos = new ArrayList<>();
            for (Huesped h : listaEntidades) {
                dtos.add(MapearHuesped.mapearEntidadADto(h));
            }

            // 3. Controller devuelve DTOs al Front
            return ResponseEntity.ok(dtos);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Verifica la existencia de un huésped por tipo y número de documento.
     *
     * <p>Construye un DTO dummy con {@code tipo} y {@code nro}, convierte el tipo al enum correspondiente
     * y delega la verificación al servicio.</p>
     *
     * @param tipo código del tipo de documento (ej: {@code "DNI"}, insensible a mayúsculas)
     * @param nro número de documento
     * @return {@code 200 OK} con {@link DtoHuesped} si existe un duplicado,
     *         {@code 200 OK} con {@code null} si no existe,
     *         {@code 400 Bad Request} si el tipo de documento es inválido,
     *         {@code 500 Internal Server Error} en caso de error técnico.
     */
    @GetMapping("/existe/{tipo}/{nro}")
    public ResponseEntity<?> verificarExistencia(@PathVariable String tipo, @PathVariable String nro) {
        try {
            // --- PASO 1: Cumplir con el Diagrama (Armar DTO Dummy) ---
            DtoHuesped dtoDummy = new DtoHuesped();
            // Convertimos el String al Enum (Manejar excepción si el tipo no existe)
            dtoDummy.setTipoDocumento(TipoDocumento.valueOf(tipo.toUpperCase()));
            dtoDummy.setNroDocumento(nro);

            // --- PASO 2: Llamar al Service ---
            Huesped existente = huespedService.chequearDuplicado(dtoDummy);

            // --- PASO 3: Respuesta al Frontend ---
            if (existente != null) {
                // Devolver DTO
                return ResponseEntity.ok(MapearHuesped.mapearEntidadADto(existente));

            } else {
                // No existe duplicado. Devolvemos 200 OK pero con cuerpo vacío (o null)
                // El front chequeará: if (response.data) { mostrarAlerta }
                return ResponseEntity.ok(null);
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Tipo de documento inválido: " + tipo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Crea o actualiza un huésped a partir del {@link DtoHuesped} recibido.
     *
     * <p>El DTO es validado por las anotaciones de bean validation; en caso de violaciones,
     * un GlobalExceptionHandler debe interceptar y devolver errores apropiados.</p>
     *
     * @param dtoHuesped DTO validado con los datos del huésped
     * @return {@code 200 OK} con mensaje de éxito si se guarda correctamente,
     *         {@code 400 Bad Request} con lista de errores de negocio si la validación de negocio falla,
     *         {@code 400 Bad Request} o {@code 500 Internal Server Error} en caso de error.
     */
    @PostMapping("/crear")//Si alguien llama a la dirección base /api/huespedes PERO usando el verbo POST y agregando /crear, entra a este metodo.
    public ResponseEntity<?> darDeAltaHuesped(@Valid @RequestBody DtoHuesped dtoHuesped) {
        try {
            // Si llega a esta línea, es porque el DTO YA PASÓ todas las validaciones de formato (@NotNull, Regex, etc)
            // Si falló alguna, el GlobalExceptionHandler ya lo interceptó antes.

            // --- PASO 1 DEL DIAGRAMA: validarDatosHuesped ---
            // La "Pantalla" (Controller) le pide al "Gestor" (Service) que valide
            List<String> erroresNegocio = huespedService.validarDatosHuesped(dtoHuesped);

            if (!erroresNegocio.isEmpty()) {
                // Si hay errores, la Pantalla se los muestra al Actor (Devuelve 400 Bad Request)
                return ResponseEntity.badRequest().body("Errores de validación: " + String.join(", ", erroresNegocio));
            }

            // Solo si pasó la validación, llamamos al upsert
            huespedService.upsertHuesped(dtoHuesped);

            return ResponseEntity.ok("✅ Huésped guardado correctamente");//Respuesta HTTP 200 OK

        } catch (Exception e) {
            // Capturamos errores de lógica de negocio (ej: base de datos caída)
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error al procesar: " + e.getMessage());
        }
    }

    /**
     * Endpoint simple de verificación del controlador.
     *
     * @return cadena indicando que el controlador de huéspedes está activo.
     */
    // Ejemplo para probar que el controller vive
    @GetMapping("/test")
    public String test() {
        return "Controller de Huéspedes activo";
    }

    /**
     * Modifica los datos de un huésped identificado por tipo y número de documento.
     *
     * <p>Valida el DTO recibido; si hay errores de negocio devuelve {@code 400 Bad Request} con detalle.</p>
     *
     * @param tipo tipo de documento del huésped a modificar
     * @param nro número de documento del huésped a modificar
     * @param dtoNuevo DTO validado con los nuevos datos
     * @return {@code 200 OK} con mensaje de éxito si la modificación fue correcta,
     *         {@code 400 Bad Request} con errores si la validación de negocio falla,
     *         {@code 400 Bad Request} o {@code 500 Internal Server Error} en caso de error.
     */
    @PutMapping("/modificar/{tipo}/{nro}")
    public ResponseEntity<?> modificarHuesped(
            @PathVariable String tipo,
            @PathVariable String nro,
            @Valid @RequestBody DtoHuesped dtoNuevo) {
        try {
            // 1. Validaciones de Negocio (CUIT, etc)
            List<String> errores = huespedService.validarDatosHuesped(dtoNuevo);
            if (!errores.isEmpty()) {
                return ResponseEntity.badRequest().body("Errores: " + String.join(", ", errores));
            }

            // 2. Ejecutar modificación
            huespedService.modificarHuesped(tipo, nro, dtoNuevo);

            return ResponseEntity.ok("✅ Huésped modificado correctamente");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al modificar: " + e.getMessage());
        }
    }

    /**
     * Elimina (da de baja) un huésped identificado por tipo y número de documento.
     *
     * @param tipo tipo de documento
     * @param nro número de documento
     * @return {@code 200 OK} con mensaje de éxito si se eliminó correctamente,
     *         {@code 400 Bad Request} si existe una restricción de negocio que impide la baja,
     *         {@code 500 Internal Server Error} en caso de error técnico.
     */
    @DeleteMapping("/borrar/{tipo}/{nro}")
    public ResponseEntity<?> borrarHuesped(@PathVariable String tipo, @PathVariable String nro) {
        try {
            huespedService.darDeBajaHuesped(tipo, nro);
            // Mensaje de éxito
            return ResponseEntity.ok("✅ Los datos del huésped han sido eliminados del sistema.");
        } catch (RuntimeException e) {
            // Caso de error lógico (se alojó en algun momento o tiene facturas asociadas)
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Errores técnicos (ej: Base de datos caída)
            return ResponseEntity.internalServerError().body("No se pudo eliminar: " + e.getMessage());
        }
    }
}