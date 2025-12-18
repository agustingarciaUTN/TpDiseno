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

/**
 * Controlador REST para operaciones relacionadas con pagos y facturas.
 *
 * <p>Expone endpoints bajo la ruta {@code /api/pagos} para:
 * - buscar facturas pendientes por número de habitación,
 * - registrar pagos de facturas,
 * - obtener el historial de pagos de una factura.</p>
 *
 * <p>Se utiliza validación de entrada mediante {@code @Validated} y anotaciones
 * de validación en parámetros y cuerpos de petición.</p>
 */
@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "*")
@Validated
public class PagoController {

    /**
     * Endpoint para crear tarjeta de crédito
     * POST /api/pagos/crear-tarjeta-credito
     */
    @PostMapping("/crear-tarjeta-credito")
    public ResponseEntity<?> crearTarjetaCredito(@RequestBody DtoTarjetaCredito dto) {
        try {
            var tarjeta = pagoService.crearTarjetaCredito(dto);
            return ResponseEntity.ok(tarjeta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * Endpoint para crear tarjeta de débito
     * POST /api/pagos/crear-tarjeta-debito
     */
    @PostMapping("/crear-tarjeta-debito")
    public ResponseEntity<?> crearTarjetaDebito(@RequestBody DtoTarjetaDebito dto) {
        try {
            var tarjeta = pagoService.crearTarjetaDebito(dto);
            return ResponseEntity.ok(tarjeta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    private final PagoService pagoService;

    /**
     * Construye el controlador inyectando el servicio de pagos.
     *
     * @param pagoService servicio que contiene la lógica de negocio para pagos y facturas
     */
    @Autowired
    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    /**
     * CU16 - Endpoint: Verificar existencia de cheque por número
     * GET /api/pagos/cheque-existe/{numeroCheque}
     * @param numeroCheque número de cheque a buscar
     * @return 200 OK con el cheque si existe, 404 si no existe
     */
    @GetMapping("/cheque-existe/{numeroCheque}")
public ResponseEntity<?> verificarChequeExiste(@PathVariable String numeroCheque) {
    try {
        var cheque = pagoService.buscarChequePorNumero(numeroCheque);
        if (cheque == null) {
            // Esto es correcto:
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "No existe el cheque"));
        }
        return ResponseEntity.ok(cheque);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", e.getMessage()));
    }
}

    /**
     * CU16 - Endpoint 1: Buscar facturas pendientes por número de habitación.
     *
     * <p>GET /api/pagos/buscar-facturas-pendientes?numeroHabitacion=101</p>
     *
     * @param numeroHabitacion número de habitación con exactamente 3 dígitos (ej: {@code "101"})
     * @return {@code 200 OK} con la lista de {@link DtoFactura} pendientes,
     *         {@code 400 Bad Request} con mensaje de error cuando el parámetro es inválido o no hay facturas,
     *         {@code 500 Internal Server Error} en caso de error inesperado.
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
     * CU16 - Endpoint 2: Registrar el pago de una factura.
     *
     * <p>POST /api/pagos/registrar</p>
     * <p>Body: {@link DtoPago} con datos del pago, monedas, cotización, monto total y lista de medios de pago.</p>
     *
     * @param dtoPago DTO con la información necesaria para registrar el pago. Debe ser válido según las anotaciones {@code @Valid}.
     * @return {@code 200 OK} con {@link DtoResultadoRegistroPago} si el pago se registra correctamente,
     *         {@code 400 Bad Request} si faltan datos o el monto es insuficiente,
     *         {@code 500 Internal Server Error} en caso de error del servidor.
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

    @GetMapping("/tarjeta-existe/{numero}")
public ResponseEntity<?> verificarTarjetaExiste(@PathVariable String numero, @RequestParam String tipo) {
    try {
        var dto = pagoService.buscarTarjetaPorNumeroYTipo(numero, tipo);
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "No existe la tarjeta"));
        }
        return ResponseEntity.ok(dto);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", e.getMessage()));
    }
}

    /**
     * Endpoint adicional: Obtener historial de pagos de una factura.
     *
     * <p>GET /api/pagos/factura/{numeroFactura}</p>
     *
     * @param numeroFactura identificador de la factura (ej: {@code "F-001"})
     * @return {@code 200 OK} con la lista de pagos asociados a la factura,
     *         {@code 500 Internal Server Error} en caso de error al obtener los pagos.
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
}