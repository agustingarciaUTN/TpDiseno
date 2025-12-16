package Facultad.TrabajoPracticoDesarrollo.Controllers;

import Facultad.TrabajoPracticoDesarrollo.DTOs.*;
import Facultad.TrabajoPracticoDesarrollo.Services.PagoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "*")
@Validated
public class PagoController {

    private final PagoService pagoService;

    @Autowired
    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    /**
     * CU16 - Endpoint 1: Buscar facturas pendientes por número de habitación
     * Paso 3 y 4 del flujo principal
     * 
     * GET /api/pagos/buscar-facturas-pendientes?numeroHabitacion=101
     * 
     * Respuestas:
     * - 200 OK: Lista de facturas pendientes
     * - 400 Bad Request: Número de habitación incorrecto o sin facturas pendientes
     */
    @GetMapping("/buscar-facturas-pendientes")
    public ResponseEntity<?> buscarFacturasPendientes(
            @RequestParam("numeroHabitacion") @Pattern(regexp = "^\\d{3}$", message = "El número de habitación debe tener exactamente 3 dígitos") String numeroHabitacion) {
        try {
            List<DtoFactura> facturas = pagoService.buscarFacturasPendientesPorHabitacion(numeroHabitacion);
            return ResponseEntity.ok(facturas);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error al buscar facturas: " + e.getMessage()));
        }
    }

    /**
     * CU16 - Endpoint 2: Registrar el pago de una factura
     * Paso 9-13 del flujo principal
     * 
     * POST /api/pagos/registrar
     * Body: {
     *   "numeroFactura": "F-001",
     *   "fechaPago": "2025-12-15",
     *   "moneda": "PESOS_ARGENTINOS",
     *   "cotizacion": 1.0,
     *   "montoTotal": 5000.0,
     *   "mediosPago": [
     *     {
     *       "tipoMedio": "EFECTIVO",
     *       "monto": 5000.0,
     *       "moneda": "PESOS_ARGENTINOS",
     *       "fechaDePago": "2025-12-15"
     *     }
     *   ]
     * }
     * 
     * Respuestas:
     * - 200 OK: Pago registrado exitosamente con información del vuelto
     * - 400 Bad Request: Datos inválidos o monto insuficiente
     * - 404 Not Found: Factura no encontrada
     */
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarPago(@RequestBody @Valid DtoPago dtoPago) {
        try {
            DtoResultadoRegistroPago response = pagoService.registrarPago(dtoPago);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error al registrar el pago: " + e.getMessage()));
        }
    }

    /**
     * Endpoint adicional: Obtener historial de pagos de una factura
     * Útil para mostrar todos los medios utilizados en el pago (observación del CU)
     * 
     * GET /api/pagos/factura/{numeroFactura}
     */
    @GetMapping("/factura/{numeroFactura}")
    public ResponseEntity<?> obtenerPagosPorFactura(@PathVariable String numeroFactura) {
        try {
            var pagos = pagoService.obtenerPagosPorFactura(numeroFactura);
            return ResponseEntity.ok(pagos);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error al obtener pagos: " + e.getMessage()));
        }
    }

    @GetMapping("/buscar-responsable")
    public ResponseEntity<?> buscarResponsable(
            @RequestParam(required = false) String cuit,
            @RequestParam(required = false) String tipoDoc,
            @RequestParam(required = false) String nroDoc) {
        try {
            DtoResponsableDePago responsable = pagoService.buscarResponsableParaNotaCredito(cuit, tipoDoc, nroDoc);
            return ResponseEntity.ok(responsable);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    /**
     * Endpoint para buscar facturas de un responsable.
     * Corresponde al paso 4 del flujo principal (listar grilla)[cite: 8].
     */
    @GetMapping("/facturas-por-responsable/{idResponsable}")
    public ResponseEntity<?> buscarFacturasPorResponsable(@PathVariable Long idResponsable) {
        try {
            List<DtoFactura> facturas = pagoService.buscarFacturasParaNotaCredito(idResponsable);
            return ResponseEntity.ok(facturas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    /**
     * Endpoint para generar la Nota de Crédito.
     * Corresponde al paso 7 (generar) y 8 (mostrar detalle)[cite: 8, 13].
     */
    @PostMapping("/generar-nota-credito")
    public ResponseEntity<?> generarNotaCredito(@RequestBody DtoSolicitudNotaCredito solicitud) {
        try {
            DtoNotaCreditoGenerada nota = pagoService.generarNotaCredito(solicitud);
            return ResponseEntity.ok(nota);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", e.getMessage()));
        }
    }
}