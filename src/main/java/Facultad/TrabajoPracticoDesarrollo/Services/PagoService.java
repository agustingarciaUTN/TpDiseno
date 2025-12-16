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
    private final NotaDeCreditoRepository notaDeCreditoRepository;
    private final ResponsablePagoRepository responsablePagoRepository;

    @Autowired
    public PagoService(FacturaRepository facturaRepository,
                       PagoRepository pagoRepository,
                       MedioDePagoRepository medioDePagoRepository,
                       EstadiaRepository estadiaRepository,
                       HabitacionRepository habitacionRepository,
                       FacturaService facturaService,
                       TarjetaRepository tarjetaRepository, NotaDeCreditoRepository notaDeCreditoRepository, ResponsablePagoRepository responsablePagoRepository) {
        this.facturaRepository = facturaRepository;
        this.pagoRepository = pagoRepository;
        this.medioDePagoRepository = medioDePagoRepository;
        this.estadiaRepository = estadiaRepository;
        this.habitacionRepository = habitacionRepository;
        this.facturaService = facturaService;
        this.tarjetaRepository = tarjetaRepository;
        this.notaDeCreditoRepository = notaDeCreditoRepository;
        this.responsablePagoRepository = responsablePagoRepository;
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
    @Transactional
    public DtoResultadoRegistroPago registrarPago(DtoPago dtoPago) throws Exception {
        // 1. Validar que la factura existe y está pendiente
        Factura factura = facturaRepository.findById(dtoPago.getNumeroFactura())
                .orElseThrow(() -> new IllegalArgumentException("La factura no existe"));

        if (factura.getEstadoFactura() == EstadoFactura.PAGADA) {
            throw new IllegalArgumentException("La factura ya está pagada");
        }

        // 2. Calcular el monto pagado en esta transacción
        Double montoAdeudado = factura.getImporteTotal();
        Double montoPagado = dtoPago.getMontoTotal();
        
        // Aplicar cotización si la moneda es diferente a Pesos Argentinos
        if (dtoPago.getMoneda() != Moneda.PESOS_ARGENTINOS) {
            if (dtoPago.getCotizacion() == null || dtoPago.getCotizacion() <= 0) {
                throw new IllegalArgumentException("Debe proporcionar una cotización válida");
            }
            montoPagado = montoPagado * dtoPago.getCotizacion();
        }

        // 3. Validar que el monto sea SUFICIENTE para pagar la factura completa
        if (montoPagado < montoAdeudado) {
            throw new IllegalArgumentException(String.format(
                    "El monto ingresado ($%.2f) es menor a la deuda ($%.2f). Falta pagar: $%.2f", 
                    montoPagado, montoAdeudado, (montoAdeudado - montoPagado)));
        }

        // 4. Crear la entidad Pago
        Pago pago = new Pago.Builder()
                .monto(dtoPago.getMontoTotal())
                .cotizacion(dtoPago.getCotizacion())
                .fecha(dtoPago.getFechaPago())
                .moneda(dtoPago.getMoneda())
                .factura(factura)
                .build();

        // 5. Procesar los medios de pago
        List<MedioPago> mediosPago = new ArrayList<>();
        for (DtoMedioPago dtoMedio : dtoPago.getMediosPago()) {
            MedioPago medioPago = new MedioPago();
            medioPago.setPago(pago);
            // Asignar monto, moneda y fecha de pago a cada medio de pago
            medioPago.setMonto(dtoMedio.getMonto());
            medioPago.setMoneda(dtoMedio.getMoneda());
            medioPago.setFechaPago(dtoPago.getFechaPago());

            // Determinar el tipo de medio de pago y crear la entidad correspondiente
            if (dtoMedio instanceof DtoEfectivo) {
                DtoEfectivo dtoEfectivo = (DtoEfectivo) dtoMedio;
                Efectivo efectivo = new Efectivo.Builder()
                        .build();
                medioPago.setEfectivo(efectivo);
            } else if (dtoMedio instanceof DtoCheque) {
                Cheque cheque = MapearCheque.mapearDtoAEntidad((DtoCheque) dtoMedio);
                medioPago.setCheque(cheque);
            } else if (dtoMedio instanceof DtoTarjetaCredito) {
                DtoTarjetaCredito dtoTarjeta = (DtoTarjetaCredito) dtoMedio;
                // Buscar si la tarjeta ya existe, si no crearla
                TarjetaCredito tarjeta = tarjetaRepository.findById(dtoTarjeta.getNumeroDeTarjeta())
                        .map(t -> (TarjetaCredito) t)
                        .orElseGet(() -> MapearTarjetaCredito.mapearDtoAEntidad(dtoTarjeta));
                medioPago.setTarjeta(tarjeta);
            } else if (dtoMedio instanceof DtoTarjetaDebito) {
                DtoTarjetaDebito dtoTarjeta = (DtoTarjetaDebito) dtoMedio;
                // Buscar si la tarjeta ya existe, si no crearla
                TarjetaDebito tarjeta = tarjetaRepository.findById(dtoTarjeta.getNumeroDeTarjeta())
                        .map(t -> (TarjetaDebito) t)
                        .orElseGet(() -> MapearTarjetaDebito.mapearDtoAEntidad(dtoTarjeta));
                medioPago.setTarjeta(tarjeta);
            }

            mediosPago.add(medioPago);
        }

        // Agregar todos los medios de pago al pago
        for (MedioPago mp : mediosPago) {
            pago.agregarMedioPago(mp);
        }

        // 6. Guardar el pago (en cascada guardará los medios de pago)
        pagoRepository.save(pago);

        // 7. Marcar la factura como PAGADA (sin cambiar su importeTotal)
        factura.setEstadoFactura(EstadoFactura.PAGADA);
        facturaRepository.save(factura);

        // 8. Calcular el vuelto
        Double vuelto = montoPagado - montoAdeudado;

        // 9. Verificar si todas las facturas de la habitación están pagadas
        String numeroHabitacion = factura.getEstadia().getHabitacion().getNumero();
        boolean todasPagadas = verificarTodasFacturasPagadas(factura.getEstadia().getIdEstadia());

        String estadoHabitacion = null;
        if (todasPagadas) {
            estadoHabitacion = "La deuda de la habitación ha sido cancelada";
        }

        // 10. Crear y retornar la respuesta
        DtoResultadoRegistroPago response = new DtoResultadoRegistroPago();
        response.setMensaje("Factura saldada. TOQUE UNA TECLA PARA CONTINUAR…");
        response.setVuelto(vuelto);
        response.setSaldoPendiente(0.0);
        response.setNumeroFactura(factura.getNumeroFactura());
        response.setEstadoFactura(factura.getEstadoFactura().name());
        response.setEstadoHabitacion(estadoHabitacion);
        response.setFacturaSaldada(true);

        return response;
    }

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


    // --- PASO 3: Buscar Facturas para la Grilla  ---
    public List<DtoFactura> buscarFacturasCandidatasANotaCredito(Long idResponsable) {
        // Buscamos facturas PENDIENTES (que no estén ya pagadas/anuladas)
        List<Factura> facturas = facturaRepository.findFacturasPendientesPorResponsable(idResponsable, EstadoFactura.PAGADA);

        return facturas.stream().map(f -> {

            // 1. Armar DtoResponsableResumido
            DtoResponsableSimple dtoResp = new DtoResponsableSimple();

            dtoResp.setIdResponsable(f.getResponsablePago().getId());


            // 2. Armar DtoEstadiaSimple
            DtoEstadiaSimple dtoEstadia = new DtoEstadiaSimple();
            if(f.getEstadia() != null) {
                dtoEstadia.setIdEstadia(f.getEstadia().getIdEstadia());
            }

            // 3. Construir DtoFactura
            return new DtoFactura.Builder()
                    .numeroFactura(f.getNumeroFactura())
                    .fechaEmision(f.getFechaEmision())
                    .fechaVencimiento(f.getFechaVencimiento())
                    .importeNeto(f.getImporteNeto())
                    .iva(f.getIva())
                    .importeTotal(f.getImporteTotal())
                    .estado(f.getEstadoFactura())
                    .tipo(f.getTipoFactura())
                    .idResponsable(dtoResp)
                    .idEstadia(dtoEstadia)
                    .build();

        }).collect(Collectors.toList());
    }

    // --- PASO 7: Generar Nota de Crédito ---
    @Transactional
    public DtoNotaCreditoGenerada generarNotaCredito(DtoSolicitudNotaCredito solicitud) {

        // 1. Obtener facturas
        List<Factura> facturas = facturaRepository.findAllById(solicitud.getIdsFacturasACancelar());

        if (facturas.isEmpty()) {
            throw new RuntimeException("No se seleccionaron facturas.");
        }

        // 2. Calcular totales
        double totalNeto = 0.0;
        double totalIva = 0.0;
        double totalDevolucion = 0.0;

        // Tomamos el responsable de la primera (todos deben ser el mismo)
        ResponsablePago responsable = facturas.get(0).getResponsablePago();

        for (Factura f : facturas) {
            if (f.getEstadoFactura() == EstadoFactura.PAGADA) {
                throw new RuntimeException("La factura " + f.getNumeroFactura() + " ya está pagada/anulada.");
            }
            totalNeto += (f.getImporteNeto() != null ? f.getImporteNeto() : 0.0);
            totalIva += (f.getIva() != null ? f.getIva() : 0.0);
            totalDevolucion += f.getImporteTotal();
        }

        // 3. Crear Nota de Crédito (Usando tu entidad que solo guarda monto)
        NotaDeCredito nc = new NotaDeCredito.Builder()
                .monto(totalDevolucion)
                // .fecha(LocalDate.now()) // Si agregas fecha a la entidad NotaDeCredito
                .build();

        nc = notaRepo.save(nc);

        // 4. Actualizar Facturas
        for (Factura f : facturas) {
            f.setNotaDeCredito(nc);
            f.setEstadoFactura(EstadoFactura.PAGADA); // <--- AQUÍ USAMOS PAGADA
        }
        facturaRepo.saveAll(facturas);

        // 5. Retornar respuesta
        DtoNotaCreditoGenerada respuesta = new DtoNotaCreditoGenerada();
        // Generamos un número visual (Ej: NC-0001)
        respuesta.setNroNotaCredito("NC-" + String.format("%08d", nc.getNumeroNotaCredito()));

        String nombreCompleto = responsable.getRazonSocial() != null
                ? responsable.getRazonSocial()
                : responsable.getNombre() + " " + responsable.getApellido();

        respuesta.setResponsableNombre(nombreCompleto);
        respuesta.setImporteNeto(totalNeto);
        respuesta.setImporteIva(totalIva);
        respuesta.setImporteTotal(totalDevolucion);

        return respuesta;
    }
}

