package Facultad.TrabajoPracticoDesarrollo.Services;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Reserva;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoReserva;
import Facultad.TrabajoPracticoDesarrollo.Repositories.ReservaRepository;
import Facultad.TrabajoPracticoDesarrollo.Utils.Mapear.MapearReserva;
import Facultad.TrabajoPracticoDesarrollo.enums.EstadoReserva;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Servicio para manejar las Reservas.
 * Se encarga de validar que las habitaciones estén libres antes de confirmar nada
 * y gestiona el calendario de ocupación.
 */
@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;

    @Autowired
    public ReservaService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    // --- BÚSQUEDAS PARA OTROS SERVICIOS ---

    /**
     * Busca reservas activas en un rango de fechas devolviendo las Entidades.
     * Método utilizado por HabitacionService para calcular la grilla de disponibilidad.
     */
    @Transactional(readOnly = true)
    public List<Reserva> buscarReservasEnRango(Date fechaInicio, Date fechaFin) {
        return reservaRepository.buscarReservasActivasEnRango(fechaInicio, fechaFin);
    }

    // --- BÚSQUEDAS PARA CONTROLADOR ---

    @Transactional(readOnly = true)
    public List<DtoReserva> buscarReservasEnFecha(Date inicio, Date fin) {
        List<Reserva> entidades = reservaRepository.buscarReservasActivasEnRango(inicio, fin);
        List<DtoReserva> dtos = new ArrayList<>();

        for (Reserva r : entidades) {
            dtos.add(MapearReserva.mapearEntidadADto(r));
        }
        return dtos;
    }

    @Transactional(readOnly = true)
    public boolean validarDisponibilidad(String idHabitacion, Date fechaInicio, Date fechaFin) {
        return !reservaRepository.existeReservaEnFecha(idHabitacion, fechaInicio, fechaFin);
    }

    // --- CREACIÓN (Lógica Principal) ---

    @Transactional(rollbackFor = Exception.class)
    public void crearReservas(List<DtoReserva> listaDtos) throws Exception {
        validarListaReservas(listaDtos);

        for (DtoReserva dto : listaDtos) {
            validarFechaIngreso(dto.getFechaDesde());

            if (reservaRepository.existeReservaEnFecha(dto.getIdHabitacion(), dto.getFechaDesde(), dto.getFechaHasta())) {
                throw new Exception("La habitación " + dto.getIdHabitacion() + " ya está reservada en las fechas seleccionadas.");
            }

            Reserva reservaEntidad = MapearReserva.mapearDtoAEntidad(dto);
            reservaEntidad.setEstadoReserva(EstadoReserva.ACTIVA);
            reservaEntidad.setFechaReserva(new Date()); 

            reservaRepository.save(reservaEntidad);
        }
    }

    // --- VALIDACIONES PRIVADAS ---

    private void validarListaReservas(List<DtoReserva> reservas) throws Exception {
        if (reservas == null || reservas.isEmpty()) {
            throw new Exception("La lista de reservas está vacía.");
        }
        for (DtoReserva dto : reservas) {
            if (dto.getNombreHuespedResponsable() == null || dto.getNombreHuespedResponsable().isBlank())
                throw new Exception("Falta nombre responsable para habitación " + dto.getIdHabitacion());
            if (dto.getIdHabitacion() == null)
                throw new Exception("Falta el número de habitación.");
            if (dto.getTipoDocumentoResponsable() == null)
                throw new Exception("Falta el tipo de documento del responsable.");
            if (dto.getNroDocumentoResponsable() == null || dto.getNroDocumentoResponsable().isBlank())
                throw new Exception("Falta el número de documento del responsable.");
            
            String nro = dto.getNroDocumentoResponsable();
            switch (dto.getTipoDocumentoResponsable()) {
                case DNI:
                    if (!nro.matches("^\\d{7,8}$")) throw new Exception("El DNI debe tener 7 u 8 números");
                    break;
                case PASAPORTE:
                    if (!nro.matches("^[A-Z0-9]{6,9}$")) throw new Exception("El pasaporte debe tener 6 a 9 caracteres alfanuméricos");
                    break;
                case LC:
                case LE:
                    if (!nro.matches("^\\d{6,8}$")) throw new Exception("El documento debe tener 6 a 8 números");
                    break;
                case OTRO:
                    if (!nro.matches("^[a-zA-Z0-9]{5,20}$")) throw new Exception("Formato de documento OTRO inválido (5-20 caracteres)");
                    break;
                default:
                    throw new Exception("Tipo de documento no soportado");
            }
        }
    }

    private void validarFechaIngreso(Date fechaDesde) throws Exception {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaIngreso = fechaDesde.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (fechaIngreso.isBefore(hoy)) {
            throw new Exception("La fecha de ingreso no puede ser anterior al día de hoy.");
        }
    }

    // CU06 - Búsqueda
    @Transactional(readOnly = true)
    public List<DtoReserva> buscarReservasPorHuesped(String apellido, String nombre) {
        String apellidoParam = (apellido != null && !apellido.isBlank()) ? apellido + "%" : null;
        String nombreParam = (nombre != null && !nombre.isBlank()) ? nombre + "%" : null;

        List<Reserva> reservas = reservaRepository.buscarParaCancelar(apellidoParam, nombreParam);

        List<DtoReserva> dtos = new ArrayList<>();
        for (Reserva r : reservas) {
            dtos.add(MapearReserva.mapearEntidadADto(r));
        }
        return dtos;
    }

    // CU06 - Cancelación
    @Transactional(rollbackFor = Exception.class)
    public void cancelarReservas(List<Integer> idsReservas) throws Exception {
        if (idsReservas == null || idsReservas.isEmpty()) {
            throw new Exception("No se seleccionaron reservas para cancelar.");
        }

        for (Integer id : idsReservas) {
            Reserva r = reservaRepository.findById(id)
                    .orElseThrow(() -> new Exception("La reserva " + id + " no existe."));

            if (r.getEstadoReserva() == EstadoReserva.CANCELADA) continue;

            r.setEstadoReserva(EstadoReserva.CANCELADA);
            reservaRepository.save(r);
        }
    }
}