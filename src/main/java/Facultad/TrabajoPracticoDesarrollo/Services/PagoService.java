package Facultad.TrabajoPracticoDesarrollo.Services;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Factura;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Pago;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoPago;
import Facultad.TrabajoPracticoDesarrollo.Repositories.PagoRepository;
import Facultad.TrabajoPracticoDesarrollo.Utils.Mapear.MapearPago;
import Facultad.TrabajoPracticoDesarrollo.enums.EstadoFactura;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Servicio de Cobranzas.
 * Simplemente registra que se ha realizado un pago sobre una factura.
 */
@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final FacturaService facturaService; // Inyectamos el Service, no el Repo

    @Autowired
    public PagoService(PagoRepository pagoRepository, FacturaService facturaService) {
        this.pagoRepository = pagoRepository;
        this.facturaService = facturaService;
    }

    /**
     * Asienta un pago en el sistema.
     * Valida cosas básicas como que el monto sea positivo y que
     * la factura que quieren pagar realmente exista.
     *
     * @param dtoPago Datos del pago (monto, fecha, forma de pago).
     * @throws IllegalArgumentException Si intentan pagar $0 o negativo.
     * @throws Exception Si la factura indicada no existe.
     */
    @Transactional
    public void registrarPago(DtoPago dtoPago) throws Exception {
        // 1. Validaciones básicas
        if (dtoPago.getMontoTotal() <= 0) {
            throw new IllegalArgumentException("El monto del pago debe ser mayor a cero.");
        }

        // 2. Buscar Factura usando el SERVICE de Facturas
        String nroFactura = String.valueOf(dtoPago.getFactura().getNumeroFactura());
        Factura factura = facturaService.buscarPorNumero(nroFactura);

        if (factura == null) {
            throw new Exception("No existe la factura con número: " + nroFactura);
        }

        // 3. Mapeo a Entidad
        Pago nuevoPago = MapearPago.mapearDtoAEntidad(dtoPago);

        // Asignamos la factura real recuperada (Managed)
        nuevoPago.setFactura(factura);

        if (nuevoPago.getFechaPago() == null) {
            nuevoPago.setFechaPago(new Date());
        }

        // 4. Guardar Pago
        pagoRepository.save(nuevoPago);

        // Opcional: Podriamos actualizar estado de factura
        // facturaService.actualizarEstado(nroFactura, EstadoFactura.PAGADA);
    }
}