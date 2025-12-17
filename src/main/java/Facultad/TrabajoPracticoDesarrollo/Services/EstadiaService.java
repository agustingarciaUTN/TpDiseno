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

    // --- BÚSQUEDAS PARA OTROS SERVICIOS ---
    
    /**
     * Busca estadías activas devolviendo las Entidades.
     * Utilizado por HabitacionService.
     */
    @Transactional(readOnly = true)
    public List<Estadia> buscarEstadiasEnRango(Date fechaInicio, Date fechaFin) {
        return estadiaRepository.buscarEstadiasEnRango(fechaInicio, fechaFin);
    }

    // --- MÉTODOS DEL NEGOCIO ---

    @Transactional(readOnly = true)
    public boolean validarDisponibilidad(String idHabitacion, Date fechaInicio, Date fechaFin) {
        return !estadiaRepository.existeEstadiaEnFechas(idHabitacion, fechaInicio, fechaFin);
    }

    @Transactional(readOnly = true)
    public List<DtoEstadia> buscarEstadiasEnFecha(Date inicio, Date fin) {
        List<Estadia> entidades = estadiaRepository.buscarEstadiasEnRango(inicio, fin);
        List<DtoEstadia> dtos = new ArrayList<>();
        for (Estadia e : entidades) {
            dtos.add(MapearEstadia.mapearEntidadADto(e));
        }
        return dtos;
    }

    @Transactional(rollbackFor = Exception.class)
    public void crearEstadia(DtoEstadia dtoEstadia) throws Exception {

        if (dtoEstadia.getDtoHuespedes() == null || dtoEstadia.getDtoHuespedes().isEmpty()) {
            throw new IllegalArgumentException("La estadía debe tener al menos un huésped (El Responsable).");
        }
        if (dtoEstadia.getDtoHabitacion() == null) {
            throw new IllegalArgumentException("Falta indicar la habitación.");
        }

        if (estadiaRepository.existeEstadiaEnFechas(dtoEstadia.getDtoHabitacion().getNumero(),
                dtoEstadia.getFechaCheckIn(),
                dtoEstadia.getFechaCheckOut())) {
            throw new Exception("La habitación " + dtoEstadia.getDtoHabitacion().getNumero() + " ya está ocupada en esas fechas.");
        }

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

        Habitacion habReal = habitacionRepository.findById(dtoEstadia.getDtoHabitacion().getNumero())
                .orElseThrow(() -> new Exception("La habitación no existe."));

        int cantidadPersonas = dtoEstadia.getDtoHuespedes().size();

        if (cantidadPersonas > habReal.getCapacidad()) {
            throw new IllegalArgumentException("La cantidad de huéspedes (" + cantidadPersonas + ") excede la capacidad de la habitación (" + habReal.getCapacidad() + ").");
        }

        Estadia estadiaNueva = MapearEstadia.mapearDtoAEntidad(dtoEstadia);
        estadiaNueva.setHabitacion(habReal);
        estadiaNueva = estadiaRepository.save(estadiaNueva);

        List<EstadiaHuesped> estadiaHuespedList = new ArrayList<>();
        boolean esPrimero = true;

        for (DtoHuesped dtoH : listaHuespedes) {
            HuespedId hid = new HuespedId(dtoH.getTipoDocumento(), dtoH.getNroDocumento());
            Huesped hReal = huespedRepository.findById(hid)
                    .orElseThrow(() -> new Exception("El huésped " + dtoH.getNroDocumento() + " no está registrado."));

            EstadiaHuespedId idIntermedio = new EstadiaHuespedId();
            idIntermedio.setIdEstadia(estadiaNueva.getIdEstadia());
            idIntermedio.setTipoDocumento(hReal.getTipoDocumento());
            idIntermedio.setNroDocumento(hReal.getNroDocumento());

            EstadiaHuesped eh = new EstadiaHuesped();
            eh.setId(idIntermedio);
            eh.setEstadia(estadiaNueva);
            eh.setHuesped(hReal);
            eh.setEsResponsable(esPrimero ? Responsable.SI : Responsable.NO);

            estadiaHuespedList.add(eh);
            esPrimero = false;
        }

        estadiaNueva.setEstadiaHuespedes(estadiaHuespedList);
        estadiaRepository.save(estadiaNueva);
    }
}