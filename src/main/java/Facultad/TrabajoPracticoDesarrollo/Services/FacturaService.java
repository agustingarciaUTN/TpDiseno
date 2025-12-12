package Facultad.TrabajoPracticoDesarrollo.Services;

import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoDetalleFacturacion;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoFactura;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoOcupantesHabitacion;
import Facultad.TrabajoPracticoDesarrollo.Dominio.*;
import Facultad.TrabajoPracticoDesarrollo.Repositories.*;
import Facultad.TrabajoPracticoDesarrollo.enums.EstadoFactura;
import Facultad.TrabajoPracticoDesarrollo.enums.PosIva;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoFactura;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FacturaService {

    private final EstadiaRepository estadiaRepository;
    private final FacturaRepository facturaRepository;
    private final ResponsablePagoRepository responsablePagoRepository;
    private final HuespedRepository huespedRepository;
    private final NotaDeCreditoRepository notaDeCreditoRepository;
    private final PersonaFisicaRepository personaFisicaRepository;

    @Autowired
    public FacturaService(PersonaFisicaRepository personaFisicaRepository, ResponsablePagoRepository responsablePagoRepository, HuespedRepository huespedRepository, EstadiaRepository estadiaRepository, FacturaRepository facturaRepository, NotaDeCreditoRepository notaDeCreditoRepository) {
        this.estadiaRepository = estadiaRepository;
        this.facturaRepository = facturaRepository;
        this.responsablePagoRepository = responsablePagoRepository;
        this.huespedRepository = huespedRepository;
        this.notaDeCreditoRepository = notaDeCreditoRepository;
        this.personaFisicaRepository = personaFisicaRepository;
    }

    // --- MÉTODOS DE BÚSQUEDA ---

    @Transactional(readOnly = true)
    public Factura buscarPorNumero(String numeroFactura) {
        return facturaRepository.findById(numeroFactura).orElse(null);
    }

    @Transactional(readOnly = true)
    public boolean existeFactura(String numeroFactura) {
        return facturaRepository.existsById(numeroFactura);
    }

    // --- MÉTODOS TRANSACCIONALES ---

    @Transactional
    public void guardarFactura(Factura factura) throws Exception {
        if (factura == null) throw new IllegalArgumentException("La factura no puede ser nula.");

        // Validación simple: Si ya existe, no la sobreescribimos (salvo que sea lógica de update)
        if (existeFactura(factura.getNumeroFactura())) {
            throw new Exception("Ya existe una factura con el número: " + factura.getNumeroFactura());
        }

        // Si la factura tiene una Nota de Crédito asociada, JPA la guardará por cascada
        // si está configurado CascadeType.ALL en la entidad Factura.
        // Si no, deberíamos guardarla aquí:
        if (factura.getNotaDeCredito() != null) {
            notaDeCreditoRepository.save(factura.getNotaDeCredito());
        }

        facturaRepository.save(factura);
    }

    @Transactional
    public void actualizarEstado(String numeroFactura, EstadoFactura nuevoEstado) throws Exception {
        Factura factura = facturaRepository.findById(numeroFactura)
                .orElseThrow(() -> new Exception("No se encontró la factura " + numeroFactura));

        factura.setEstadoFactura(nuevoEstado);
        facturaRepository.save(factura);
    }

    @Transactional
    public void generarNotaDeCredito(NotaDeCredito nota) {
        // Lógica específica para notas de crédito
        notaDeCreditoRepository.save(nota);
    }

    @Transactional(readOnly = true)
    public DtoOcupantesHabitacion buscarOcupantes(String nroHabitacion) {
        Estadia estadia = estadiaRepository.findEstadiaActivaPorHabitacion(nroHabitacion)
                .orElseThrow(() -> new IllegalArgumentException("No hay estadía activa en la habitación " + nroHabitacion));

        DtoOcupantesHabitacion respuesta = new DtoOcupantesHabitacion();
        respuesta.setNumeroHabitacion(nroHabitacion);
        respuesta.setIdEstadia(estadia.getIdEstadia());

        List<DtoOcupantesHabitacion.DtoHuespedResumen> lista = estadia.getHuespedes().stream()
                .map(h -> new DtoOcupantesHabitacion.DtoHuespedResumen(
                        h.getTipoDocumento(),
                        h.getNroDocumento(),
                        h.getNombres(),
                        h.getApellido()
                )).collect(Collectors.toList());

        respuesta.setOcupantes(lista);
        return respuesta;
    }

    public void validarResponsable(TipoDocumento tipo, String nro) {

        // Creamos el ID compuesto para buscar
        HuespedId id = new HuespedId(tipo, nro);

        Huesped h = huespedRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Huésped no encontrado"));

        LocalDate nacimiento = h.getFechaNacimiento().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (Period.between(nacimiento, LocalDate.now()).getYears() < 18) {
            throw new IllegalArgumentException("La persona seleccionada es menor de edad.");
        }
    }

    @Transactional(readOnly = true)
    public DtoDetalleFacturacion calcularDetalle(int idEstadia, int idResponsable, String horaSalida) {
        Estadia estadia = estadiaRepository.findById(idEstadia).orElseThrow();
        ResponsablePago responsable = responsablePagoRepository.findById(idResponsable).orElseThrow();

        // 1. Cálculo de Recargos
        LocalTime hora = LocalTime.parse(horaSalida);
        double costoNoche = estadia.getHabitacion().getCostoPorNoche();
        double recargo = 0.0;
        String detalleRecargo = "Sin recargo";

        if (hora.isAfter(LocalTime.of(11, 0))) {
            if (hora.isBefore(LocalTime.of(18, 1))) {
                recargo = costoNoche * 0.5;
                detalleRecargo = "Late Check-out (50%)";
            } else {
                recargo = costoNoche;
                detalleRecargo = "Late Check-out (Día completo)";
            }
        }

        // 2. Determinar Tipo de Factura
        TipoFactura tipo = TipoFactura.B;
        if (responsable instanceof PersonaJuridica) {
            tipo = TipoFactura.A;
        } else if (responsable instanceof PersonaFisica pf) {
            if (pf.getHuesped().getPosicionIva() == PosIva.RESPONSABLE_INSCRIPTO) {
                tipo = TipoFactura.A;
            }
        }

        // 3. Totales
        double subtotal = estadia.getValorEstadia() + recargo;
        double totalBruto = subtotal; // + sumar otros consumos si hubiera
        double iva = totalBruto * 0.21;
        double totalFinal = totalBruto + iva;

        // 4. Armar DTO
        DtoDetalleFacturacion dto = new DtoDetalleFacturacion();
        dto.setMontoEstadiaBase(estadia.getValorEstadia());
        dto.setRecargoHorario(recargo);
        dto.setDetalleRecargo(detalleRecargo);
        dto.setSubtotal(totalBruto);
        dto.setMontoIva(iva);
        dto.setMontoTotal(totalFinal);
        dto.setTipoFactura(tipo);

        if(responsable instanceof PersonaJuridica pj) dto.setNombreResponsable(pj.getRazonSocial());
        else if (responsable instanceof PersonaFisica pf) dto.setNombreResponsable(pf.getHuesped().getApellido() + " " + pf.getHuesped().getNombres());

        List<DtoDetalleFacturacion.DtoItemFactura> items = new ArrayList<>();
        items.add(new DtoDetalleFacturacion.DtoItemFactura("Alojamiento", estadia.getValorEstadia()));
        if(recargo > 0) items.add(new DtoDetalleFacturacion.DtoItemFactura(detalleRecargo, recargo));
        dto.setItems(items);

        return dto;
    }

    @Transactional
    public DtoFactura generarFactura(DtoFactura dto) throws Exception {
        if (existeFactura(dto.getNumeroFactura())) {
            throw new IllegalArgumentException("El número de factura ya existe: " + dto.getNumeroFactura());
        }

        Estadia estadia = estadiaRepository.findById(dto.getIdEstadia().getIdEstadia()).orElseThrow();
        ResponsablePago responsable = responsablePagoRepository.findById(dto.getIdResponsable().getIdResponsable()).orElseThrow();

        // Mapeo manual a Entidad
        Factura factura = new Factura();
        factura.setNumeroFactura(dto.getNumeroFactura());
        factura.setFechaEmision(dto.getFechaEmision());
        factura.setFechaVencimiento(dto.getFechaVencimiento());
        factura.setImporteTotal(dto.getImporteTotal());
        factura.setImporteNeto(dto.getImporteNeto());
        factura.setIva(dto.getIva());
        factura.setTipoFactura(dto.getTipoFactura());
        factura.setEstadoFactura(EstadoFactura.PENDIENTE); // Nace pendiente

        // Relaciones
        factura.setEstadia(estadia);
        factura.setResponsablePago(responsable);

        facturaRepository.save(factura);
        return dto;
    }

    @Transactional(readOnly = true)
    public int buscarIdResponsablePorHuesped(TipoDocumento tipo, String nro) {

        // 1. Buscamos si este huésped ya figura como Persona Física (Responsable)
        return personaFisicaRepository.findByHuesped_TipoDocumentoAndHuesped_NroDocumento(tipo, nro)
                .map(PersonaFisica::getIdResponsable) // Si existe, devolvemos su ID
                .orElseThrow(() -> new IllegalArgumentException(
                        "El huésped no está registrado como Responsable de Pago. " +
                                "Debe darlo de alta primero (CU12)." // O podrías crearlo aquí si el negocio lo permite
                ));
    }

}