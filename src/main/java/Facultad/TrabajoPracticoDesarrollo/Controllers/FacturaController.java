package Facultad.TrabajoPracticoDesarrollo.Controllers;


import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoFactura;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoInicioFactura;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoPersonaJuridica;
import Facultad.TrabajoPracticoDesarrollo.Services.FacturaService;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;

/**
 * Controlador REST para operaciones relacionadas con facturación.
 *
 * <p>Expone endpoints bajo la ruta {@code /api/factura} para:
 * - buscar ocupantes de una habitación para iniciar una factura,
 * - calcular el detalle de la factura según la estadía y el responsable,
 * - generar la factura final,
 * - crear un responsable jurídico si se factura a una empresa.</p>
 *
 * <p>Permite CORS desde cualquier origen.</p>
 */
@RestController
@RequestMapping("/api/factura")
@CrossOrigin(origins = "*")
public class FacturaController {

    private final FacturaService facturaService;

    /**
     * Construye el controlador inyectando el servicio de facturación.
     *
     * @param service servicio que contiene la lógica de negocio para facturas
     */
    public FacturaController(FacturaService service) {
        this.facturaService = service;
    }


    /**
     * PASO 1: Buscar ocupantes de una habitación para iniciar la facturación.
     *
     * <p>POST /api/factura/buscar-ocupantes</p>
     *
     * @param datos DTO con la información inicial necesaria (por ejemplo {@link DtoInicioFactura#numeroHabitacion})
     * @return {@code 200 OK} con la lista de ocupantes encontrados,
     *         {@code 400 Bad Request} cuando faltan datos o los parámetros son inválidos.
     */
    @PostMapping("/buscar-ocupantes")
    public ResponseEntity<?> buscarOcupantes(@Valid @RequestBody DtoInicioFactura datos) {
        try {
            return ResponseEntity.ok(facturaService.buscarOcupantes(datos.getNumeroHabitacion()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Calcula el detalle de la factura para una estadía y responsable dado.
     *
     * <p>GET /api/factura/calcular-detalle</p>
     *
     * <p>Soporta dos flujos:
     * - Responsable tercero (empresa): se requiere {@code idResponsableJuridico} o {@code cuit} para buscar/crear responsable.
     * - Responsable huésped (persona física): requiere {@code tipoDoc} y {@code nroDoc} y se obtendrá/creará el id del responsable.</p>
     *
     * @param idEstadia identificador de la estadía
     * @param tipoDoc tipo de documento del huésped (opcional, requerido si {@code esTercero} es false)
     * @param nroDoc número de documento del huésped (opcional, requerido si {@code esTercero} es false)
     * @param idResponsableJuridico id numérico del responsable jurídico cuando ya existe (opcional)
     * @param horaSalida hora de salida en formato esperado por el servicio
     * @param esTercero indica si el responsable es tercero (empresa) o un huésped
     * @param cuit CUIT para buscar responsable jurídico por CUIT (opcional)
     * @return {@code 200 OK} con el detalle calculado por el servicio,
     *         {@code 400 Bad Request} en caso de parámetros inválidos,
     *         {@code 409 Conflict} con acción a realizar cuando se debe crear un responsable jurídico.
     */
    @GetMapping("/calcular-detalle")
    public ResponseEntity<?> calcularDetalle(
            @RequestParam int idEstadia,
            @RequestParam(required = false) TipoDocumento tipoDoc,
            @RequestParam(required = false) String nroDoc,
            @RequestParam(required = false) Integer idResponsableJuridico, // ID numérico si es empresa
            @RequestParam String horaSalida,
            @RequestParam boolean esTercero,
            @RequestParam(required = false) String cuit
    ) {
        try {
            int idResponsableFinal;

            if (esTercero) {
                // Si no viene ID, interrumpimos el flujo (tu solución anterior)
                if (idResponsableJuridico == null) {

                    // Intentamos buscarlo en la BD por CUIT
                    Integer idEncontrado = facturaService.buscarIdPorCuit(cuit);

                    if (idEncontrado != null) {
                        idResponsableFinal = idEncontrado;
                    } else {
                        // Si es null, significa que NO existe en la BD -> Mandamos a crear
                        return ResponseEntity.status(409)
                                .body(java.util.Collections.singletonMap("accion", "REDIRECCIONAR_A_ALTA_RESPONSABLE"));
                    }
                } else {
                    idResponsableFinal = idResponsableJuridico;
                }

            } else {
                // 1. Es Huesped: Validamos edad usando clave compuesta
                if(tipoDoc == null || nroDoc == null) throw new IllegalArgumentException("Faltan datos del huésped");

                facturaService.validarResponsable(tipoDoc, nroDoc);

                // 2. Buscamos el ID de ResponsablePago asociado a ese huésped (PersonaFisica)
                idResponsableFinal = facturaService.obtenerOAltaPersonaFisica(tipoDoc, nroDoc);
            }

            return ResponseEntity.ok(facturaService.calcularDetalle(idEstadia, idResponsableFinal, horaSalida));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Genera la factura final a partir del DTO de factura completo.
     *
     * <p>POST /api/factura/generar</p>
     *
     * @param facturaFinal DTO validado con los datos finales de la factura
     * @return {@code 200 OK} con el resultado de la generación,
     *         {@code 500 Internal Server Error} en caso de error del servidor.
     */
    @PostMapping("/generar")
    public ResponseEntity<?> generarFactura(@Valid @RequestBody DtoFactura facturaFinal) {
        try {
            return ResponseEntity.ok(facturaService.generarFactura(facturaFinal));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    /**
     * Crea un responsable jurídico y devuelve su id generado.
     *
     * <p>POST /api/factura/responsable</p>
     *
     * @param nuevoResponsable DTO con los datos de la persona jurídica a crear
     * @return {@code 200 OK} con un mapa que contiene {@code idResponsableGenerado},
     *         {@code 400 Bad Request} si ocurre un error al crear el responsable.
     */
    @PostMapping("/responsable")
    public ResponseEntity<?> crearResponsable(@RequestBody @Valid DtoPersonaJuridica nuevoResponsable) {
        try {
            // Llamamos al servicio para guardar
            int nuevoId = facturaService.guardarResponsableJuridico(nuevoResponsable);

            // Devolvemos el ID para que el frontend pueda volver a llamar a calcular-detalle
            return ResponseEntity.ok(java.util.Collections.singletonMap("idResponsableGenerado", nuevoId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear responsable: " + e.getMessage());
        }
    }

}
