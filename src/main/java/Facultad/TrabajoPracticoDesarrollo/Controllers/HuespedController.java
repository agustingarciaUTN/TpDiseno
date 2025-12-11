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

@RestController//Declarado como API, le dice a Spring que atiende pedidos web
@RequestMapping("/api/huespedes")
@CrossOrigin(origins = "*") // Permite peticiones desde cualquier Frontend (React/Angular/Postman)
public class HuespedController {

    //Aca le decimos a Spring que necesitamos un HuespedService para trabajar
    private final HuespedService huespedService;
    // 2. Inyección por Constructor (Spring te pasa el Gestor listo)
    public HuespedController(HuespedService huespedService) {
        this.huespedService = huespedService;
    }


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

    // Endpoint para verificar existencia antes de guardar (Requerido por CU9)
    // En HuespedController.java

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

    // Ejemplo para probar que el controller vive
    @GetMapping("/test")
    public String test() {
        return "Controller de Huéspedes activo";
    }
}