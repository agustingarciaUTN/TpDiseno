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

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final FacturaService facturaService; // Inyectamos el Service, no el Repo

    @Autowired
    public PagoService(PagoRepository pagoRepository, FacturaService facturaService) {
        this.pagoRepository = pagoRepository;
        this.facturaService = facturaService;
    }

    @Transactional
    public void registrarPago(DtoPago dtoPago) throws Exception {
        // 1. Validaciones básicas
        if (dtoPago.getMontoTotal() <= 0) {
            throw new IllegalArgumentException("El monto del pago debe ser mayor a cero.");
        }

        // 2. Buscar Factura usando el SERVICE de Facturas
        String nroFactura = String.valueOf(dtoPago.getIdFactura());
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
        // JPA guardará automáticamente los medios de pago asociados (Efectivo/Tarjeta/Cheque)
        // por la relación CascadeType.ALL definida en la entidad Pago.
        pagoRepository.save(nuevoPago);

        // 5. Actualizar estado de la factura (Delegamos la lógica al FacturaService)
        // Ejemplo: Si el pago cubre el total, la marcamos como pagada.
        // Aquí simplificamos asumiendo que un pago registrado implica saldar la factura o parte de ella.
        // Podrías sumar los pagos previos de esta factura si quisieras validar el total.

        // facturaService.actualizarEstado(nroFactura, EstadoFactura.PAGADA);
    }
}