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

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;

    @Autowired
    public ReservaService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    // --- BÚSQUEDAS ---

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
    public boolean estaReservadaEnFecha(String nroHabitacion, Date fechaInicial, Date fechaFin) {
        return reservaRepository.existeReservaEnFecha(nroHabitacion, fechaInicial, fechaFin);
    }

    // --- CREACIÓN (Lógica Principal) ---

    @Transactional(rollbackFor = Exception.class) // Hace rollback si algo falla
    public void crearReservas(List<DtoReserva> listaDtos) throws Exception {

        // 1. Validaciones generales de la lista
        validarListaReservas(listaDtos);

        // 2. Procesar cada reserva
        for (DtoReserva dto : listaDtos) {

            // a. Validar fechas (Lógica de negocio: no puede ser anterior a hoy)
            validarFechaIngreso(dto.getFechaDesde());

            // b. Validar disponibilidad en BD (concurrencia)
            if (estaReservadaEnFecha(dto.getIdHabitacion(), dto.getFechaDesde(), dto.getFechaHasta())) {
                throw new Exception("La habitación " + dto.getIdHabitacion() + " ya está reservada en las fechas seleccionadas.");
            }

            // c. Mapeo a Entidad
            Reserva reservaEntidad = MapearReserva.mapearDtoAEntidad(dto);

            // d. Completar datos del sistema
            reservaEntidad.setEstadoReserva(EstadoReserva.ACTIVA);
            reservaEntidad.setFechaReserva(new Date()); // Fecha de registro = Hoy

            // e. Guardar (JPA maneja el INSERT)
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
        }
    }

    private void validarFechaIngreso(Date fechaDesde) throws Exception {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaIngreso = fechaDesde.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (fechaIngreso.isBefore(hoy)) {
            throw new Exception("La fecha de ingreso no puede ser anterior al día de hoy.");
        }
    }
}