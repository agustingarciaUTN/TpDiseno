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

    // --- BÚSQUEDAS ---

    @Transactional(readOnly = true)

    /**
     * Busca qué reservas están "vivas" (activas) en un rango de fechas.
     * Sirve para pintar la grilla en la pantalla y ver qué está ocupado.
     *
     * @param fechaInicio Desde cuándo queremos mirar.
     * @param fechaFin    Hasta cuándo queremos mirar.
     * @return Una lista limpia (DTOs) para mostrar en el frontend.
     */
    public List<DtoReserva> buscarReservasEnFecha(Date inicio, Date fin) {
        List<Reserva> entidades = reservaRepository.buscarReservasActivasEnRango(inicio, fin);
        List<DtoReserva> dtos = new ArrayList<>();

        for (Reserva r : entidades) {
            dtos.add(MapearReserva.mapearEntidadADto(r));
        }
        return dtos;
    }

    @Transactional(readOnly = true)
    /**
     * El "semáforo" del sistema. Revisa si una habitación está libre en esas fechas.
     * Verifica que no se superponga con ninguna otra reserva existente.
     *
     * @param idHabitacion La habitación que queremos chequear.
     * @param fechaInicio  Cuándo entra el huésped.
     * @param fechaFin     Cuándo sale.
     * @return true si está libre (Luz Verde), false si ya está reservada (Luz Roja).
     */
    public boolean validarDisponibilidad(String idHabitacion, Date fechaInicio, Date fechaFin) {
        // Buscamos si existe alguna reserva para esa habitación que se solape con las fechas
        // Lógica: (StartA <= EndB) and (EndA >= StartB)
        return !reservaRepository.existeReservaEnFecha(idHabitacion, fechaInicio, fechaFin);
    }


    // --- CREACIÓN (Lógica Principal) ---

    @Transactional(rollbackFor = Exception.class) // Hace rollback si algo falla
    /**
     * Guarda las reservas en la base de datos.
     * Antes de guardar, hace una última validación de seguridad para asegurarse
     * de que nadie haya ocupado la habitación en el último milisegundo.
     *
     * @param reservas Lista de reservas a crear.
     * @throws Exception Si las fechas están mal o la habitación ya no está disponible.
     */
    public void crearReservas(List<DtoReserva> listaDtos) throws Exception {

        // 1. Validaciones generales de la lista
        validarListaReservas(listaDtos);

        // 2. Procesar cada reserva
        for (DtoReserva dto : listaDtos) {

            // a. Validar fechas (Lógica de negocio: no puede ser anterior a hoy)
            validarFechaIngreso(dto.getFechaDesde());

            // b. Validar disponibilidad en BD (concurrencia)
            if (reservaRepository.existeReservaEnFecha(dto.getIdHabitacion(), dto.getFechaDesde(), dto.getFechaHasta())) {
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

    /**
     * Validador de seguridad interno.
     * Revisa que la lista no sea nula y que cada reserva tenga lo mínimo indispensable
     * (habitación y nombre del responsable) antes de intentar procesar nada.
     *
     * @param reservas La lista cruda que vino del front.
     * @throws Exception Si la lista está vacía o faltan datos obligatorios.
     */
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

    /**
     * Regla de negocio temporal.
     * Se asegura de que no estemos intentando reservar con fecha de inicio en el pasado.
     * Convierte fechas de Date a LocalDate para comparar solo día/mes/año sin horas.
     *
     * @param fechaDesde La fecha de ingreso propuesta.
     * @throws Exception Si la fecha es anterior a hoy.
     */
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
            // Verificamos que exista y esté activa antes de cancelar (Concurrencia)
            Reserva r = reservaRepository.findById(id)
                    .orElseThrow(() -> new Exception("La reserva " + id + " no existe."));

            // Chequear si ya estaba cancelada
            if (r.getEstadoReserva() == EstadoReserva.CANCELADA) continue;


            r.setEstadoReserva(EstadoReserva.CANCELADA);
            reservaRepository.save(r);
        }

    }
}