package Facultad.TrabajoPracticoDesarrollo.Services;

import Facultad.TrabajoPracticoDesarrollo.Dominio.*;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoEstadia;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoHuesped;
import Facultad.TrabajoPracticoDesarrollo.Repositories.EstadiaRepository;
import Facultad.TrabajoPracticoDesarrollo.Repositories.HabitacionRepository;
import Facultad.TrabajoPracticoDesarrollo.Repositories.HuespedRepository;
import Facultad.TrabajoPracticoDesarrollo.Utils.Mapear.MapearEstadia;
import Facultad.TrabajoPracticoDesarrollo.enums.Responsable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Servicio encargado del "Check-in" y la ocupación real.
 * Transforma la "promesa" de una Reserva en una Estadía efectiva con gente adentro.
 */
@Service
public class EstadiaService {

    private final EstadiaRepository estadiaRepository;
    private final HuespedRepository huespedRepository;
    private final HabitacionRepository habitacionRepository;

    @Autowired
    public EstadiaService(EstadiaRepository estadiaRepository,
                          HuespedRepository huespedRepository,
                          HabitacionRepository habitacionRepository) {
        this.estadiaRepository = estadiaRepository;
        this.huespedRepository = huespedRepository;
        this.habitacionRepository = habitacionRepository;
    }

    /**
     * Revisa la disponibilidad "física".
     * A diferencia de las reservas, esto chequea si hay una Estadía vigente.
     * Es decir, si hay maletas y gente en la habitación AHORA.
     *
     * @param idHabitacion Número de la habitación.
     * @return true si no hay nadie (está vacía), false si está ocupada.
     */
    @Transactional(readOnly = true)
    public boolean validarDisponibilidad(String idHabitacion, Date fechaInicio, Date fechaFin) {
        // Buscamos si hay una estadía ACTIVA (no finalizada) que ocupe la habitación en esas fechas
        return !estadiaRepository.existeEstadiaEnFechas(idHabitacion, fechaInicio, fechaFin);
    }

    /**
     * Busca todas las estadías activas en un rango de fechas.
     * Útil para reportes o para ver quién está en el hotel.
     */
    @Transactional(readOnly = true)
    public List<DtoEstadia> buscarEstadiasEnFecha(Date inicio, Date fin) {
        List<Estadia> entidades = estadiaRepository.buscarEstadiasEnRango(inicio, fin);
        List<DtoEstadia> dtos = new ArrayList<>();
        for (Estadia e : entidades) {
            dtos.add(MapearEstadia.mapearEntidadADto(e));
        }
        return dtos;
    }

    /**
     * Hace el ingreso oficial (Check-in).
     * Toma los datos de la habitación y vincula a todos los huéspedes que van a dormir ahí.
     * Valida que la habitación no esté ocupada y que los acompañantes no estén en otra pieza.
     *
     * @param dtoEstadia Datos del check-in (habitación, fechas, lista de personas).
     * @throws Exception Si intentas meter gente en una habitación que ya tiene ocupantes.
     */
    @Transactional(rollbackFor = Exception.class)
    public void crearEstadia(DtoEstadia dtoEstadia) throws Exception {

        // 1. Validaciones Básicas
        if (dtoEstadia.getDtoHuespedes() == null || dtoEstadia.getDtoHuespedes().isEmpty()) {
            throw new IllegalArgumentException("La estadía debe tener al menos un huésped (El Responsable).");
        }
        if (dtoEstadia.getDtoHabitacion() == null) {
            throw new IllegalArgumentException("Falta indicar la habitación.");
        }

        // 2. Validar Disponibilidad
        if (estadiaRepository.existeEstadiaEnFechas(dtoEstadia.getDtoHabitacion().getNumero(),
                dtoEstadia.getFechaCheckIn(),
                dtoEstadia.getFechaCheckOut())) {
            throw new Exception("La habitación " + dtoEstadia.getDtoHabitacion().getNumero() + " ya está ocupada en esas fechas.");
        }

        // 3. Validar Acompañantes
        List<DtoHuesped> listaHuespedes = dtoEstadia.getDtoHuespedes();

        for (int i = 1; i < listaHuespedes.size(); i++) {
            DtoHuesped acomp = listaHuespedes.get(i);
            boolean ocupado = estadiaRepository.esHuespedActivo(
                    acomp.getTipoDocumento(),
                    acomp.getNroDocumento(),
                    dtoEstadia.getFechaCheckIn(),
                    dtoEstadia.getFechaCheckOut()
            );
            if (ocupado) {
                throw new Exception("El acompañante " + acomp.getApellido() + " ya figura alojado en otra habitación.");
            }
        }

        // 4. Mapeo Inicial (Sin relaciones todavía)
        Estadia estadiaNueva = MapearEstadia.mapearDtoAEntidad(dtoEstadia);

        // a. Vincular Habitación
        Habitacion habReal = habitacionRepository.findById(dtoEstadia.getDtoHabitacion().getNumero())
                .orElseThrow(() -> new Exception("La habitación no existe."));
        estadiaNueva.setHabitacion(habReal);

        // 5. GUARDADO PARCIAL: Guardamos la estadía SOLO con la habitación para generar el ID (idEstadia)
        // Necesitamos el ID generado para poder armar la clave compuesta de EstadiaHuesped
        estadiaNueva = estadiaRepository.save(estadiaNueva);


        // 6. Crear y Vincular los EstadiaHuesped usando la clave compuesta
        List<EstadiaHuesped> estadiaHuespedList = new ArrayList<>();
        boolean esPrimero = true;

        for (DtoHuesped dtoH : listaHuespedes) {
            HuespedId hid = new HuespedId(dtoH.getTipoDocumento(), dtoH.getNroDocumento());
            Huesped hReal = huespedRepository.findById(hid)
                    .orElseThrow(() -> new Exception("El huésped " + dtoH.getNroDocumento() + " no está registrado."));

            // --- AQUÍ ESTÁ EL CAMBIO CLAVE ---

            // A. Instanciamos la Clave Compuesta
            EstadiaHuespedId idIntermedio = new EstadiaHuespedId();
            idIntermedio.setIdEstadia(estadiaNueva.getIdEstadia()); // Usamos el ID generado arriba
            idIntermedio.setTipoDocumento(hReal.getTipoDocumento());
            idIntermedio.setNroDocumento(hReal.getNroDocumento());

            // B. Instanciamos la Entidad Intermedia
            EstadiaHuesped eh = new EstadiaHuesped();
            eh.setId(idIntermedio); // Seteamos el ID compuesto

            // C. Seteamos las Relaciones y Atributos extra
            eh.setEstadia(estadiaNueva);
            eh.setHuesped(hReal);
            eh.setEsResponsable(esPrimero ? Responsable.SI : Responsable.NO);

            estadiaHuespedList.add(eh);
            esPrimero = false;
        }

        // 7. Asignar la lista completa y volver a guardar (Update)
        // Al tener CascadeType.ALL en Estadia -> EstadiaHuespedes, esto persistirá la tabla intermedia
        estadiaNueva.setEstadiaHuespedes(estadiaHuespedList);
        estadiaRepository.save(estadiaNueva);
    }
}