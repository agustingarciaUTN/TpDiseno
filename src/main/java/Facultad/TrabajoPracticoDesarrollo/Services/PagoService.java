package Facultad.TrabajoPracticoDesarrollo.Services;

import Facultad.TrabajoPracticoDesarrollo.Dominio.*;
import Facultad.TrabajoPracticoDesarrollo.DTOs.*;
import Facultad.TrabajoPracticoDesarrollo.Repositories.*;
import Facultad.TrabajoPracticoDesarrollo.Utils.Mapear.*;
import Facultad.TrabajoPracticoDesarrollo.enums.EstadoFactura;
import Facultad.TrabajoPracticoDesarrollo.enums.Moneda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de Cobranzas.
 * Simplemente registra que se ha realizado un pago sobre una factura.
 */
@Service
public class PagoService {

    private final FacturaRepository facturaRepository;
    private final PagoRepository pagoRepository;
    private final MedioDePagoRepository medioDePagoRepository;
    private final EstadiaRepository estadiaRepository;
    private final HabitacionRepository habitacionRepository;
    private final FacturaService facturaService;
    private final TarjetaRepository tarjetaRepository;

    @Autowired
    public PagoService(FacturaRepository facturaRepository,
                       PagoRepository pagoRepository,
                       MedioDePagoRepository medioDePagoRepository,
                       EstadiaRepository estadiaRepository,
                       HabitacionRepository habitacionRepository,
                       FacturaService facturaService,
                       TarjetaRepository tarjetaRepository) {
        this.facturaRepository = facturaRepository;
        this.pagoRepository = pagoRepository;
        this.medioDePagoRepository = medioDePagoRepository;
        this.estadiaRepository = estadiaRepository;
        this.habitacionRepository = habitacionRepository;
        this.facturaService = facturaService;
        this.tarjetaRepository = tarjetaRepository;
    }

    /**
     * CU16 - Paso 3 y 4: Buscar facturas pendientes por número de habitación
     */
    @Transactional(readOnly = true)
    public List<DtoFactura> buscarFacturasPendientesPorHabitacion(String numeroHabitacion) {
        // Verificar que la habitación existe
        Habitacion habitacion = habitacionRepository.findById(numeroHabitacion)
                .orElseThrow(() -> new IllegalArgumentException("Número de habitación incorrecto"));

        // Buscar todas las estadías de la habitación (activas o pasadas)
        List<Estadia> estadias = estadiaRepository.findByHabitacion_Numero(numeroHabitacion);
        
        if (estadias.isEmpty()) {
            throw new IllegalArgumentException("No existen facturas pendientes de pago");
        }

        // Buscar todas las facturas pendientes de todas las estadías de esta habitación
        List<Factura> facturasPendientes = new ArrayList<>();
        for (Estadia estadia : estadias) {
            List<Factura> facturasEstadia = facturaRepository.findByEstadia_IdEstadia(estadia.getIdEstadia())
                    .stream()
                    .filter(f -> f.getEstadoFactura() == EstadoFactura.PENDIENTE)
                    .collect(Collectors.toList());
            facturasPendientes.addAll(facturasEstadia);
        }

        if (facturasPendientes.isEmpty()) {
            throw new IllegalArgumentException("No existen facturas pendientes de pago");
        }

        // Mapear a DTO usando el mapeador existente
        return facturasPendientes.stream()
                .map(MapearFactura::mapearEntidadADto)
                .collect(Collectors.toList());
    }

    /**
     * CU16 - Paso 9-13: Registrar el pago de una factura (COMPLETO)
     * El pago debe ser igual o mayor al importeTotal de la factura
     * Se crea un único Pago con múltiples MedioPago
     */

    /**
     * Verifica si todas las facturas de una estadía están pagadas
     */
    private boolean verificarTodasFacturasPagadas(Integer idEstadia) {
        List<Factura> facturasEstadia = facturaRepository.findByEstadia_IdEstadia(idEstadia);
        return facturasEstadia.stream()
                .allMatch(f -> f.getEstadoFactura() == EstadoFactura.PAGADA);
    }

    /**
     * Obtener todos los pagos de una factura (para mostrar los medios utilizados)
     */
    @Transactional(readOnly = true)
    public List<Pago> obtenerPagosPorFactura(String numeroFactura) {
        return pagoRepository.findByFactura_NumeroFactura(numeroFactura);
    }


}