package Facultad.TrabajoPracticoDesarrollo.Services;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Estadia;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Habitacion;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Huesped;
import Facultad.TrabajoPracticoDesarrollo.Dominio.HuespedId;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoEstadia;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoHuesped;
import Facultad.TrabajoPracticoDesarrollo.Repositories.EstadiaRepository;
import Facultad.TrabajoPracticoDesarrollo.Repositories.HabitacionRepository;
import Facultad.TrabajoPracticoDesarrollo.Repositories.HuespedRepository;
import Facultad.TrabajoPracticoDesarrollo.Utils.Mapear.MapearEstadia;
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

    @Transactional(readOnly = true)
    public boolean validarDisponibilidad(String idHabitacion, Date fechaInicio, Date fechaFin) {
        // Buscamos si hay una estadía ACTIVA (no finalizada) que ocupe la habitación en esas fechas
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

        // 1. Validaciones Básicas
        if (dtoEstadia.getDtoHuespedes() == null || dtoEstadia.getDtoHuespedes().isEmpty()) {
            throw new IllegalArgumentException("La estadía debe tener al menos un huésped (El Responsable).");
        }
        if (dtoEstadia.getDtoHabitacion() == null) {
            throw new IllegalArgumentException("Falta indicar la habitación.");
        }

        // 2. Validar Disponibilidad Habitación (Doble chequeo)
        if (estadiaRepository.existeEstadiaEnFechas(dtoEstadia.getDtoHabitacion().getNumero(),
                dtoEstadia.getFechaCheckIn(),
                dtoEstadia.getFechaCheckOut())) {
            throw new Exception("La habitación " + dtoEstadia.getDtoHabitacion().getNumero() + " ya está ocupada en esas fechas.");
        }

        // 3. Validar Acompañantes (Regla de Negocio: No pueden estar en dos lados a la vez)
        // El índice 0 es el responsable, él SÍ puede figurar en varias (pagador). Los acompañantes no.
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

        // 4. Mapeo y Preparación de Entidades MANAGED
        // Esto es crucial: no podemos usar los objetos "nuevos" del DTO,
        // tenemos que buscar las referencias vivas en la BD para que JPA haga los vínculos.

        Estadia estadiaNueva = MapearEstadia.mapearDtoAEntidad(dtoEstadia);

        // a. Vincular Habitación Real
        Habitacion habReal = habitacionRepository.findById(dtoEstadia.getDtoHabitacion().getNumero())
                .orElseThrow(() -> new Exception("La habitación no existe."));
        estadiaNueva.setHabitacion(habReal);

        // b. Vincular Huéspedes Reales
        List<Huesped> huespedesReales = new ArrayList<>();
        for (DtoHuesped dtoH : listaHuespedes) {
            HuespedId hid = new HuespedId(dtoH.getTipoDocumento(), dtoH.getNroDocumento());
            Huesped hReal = huespedRepository.findById(hid)
                    .orElseThrow(() -> new Exception("El huésped " + dtoH.getNroDocumento() + " no está registrado. Debe darlo de alta antes."));
            huespedesReales.add(hReal);
        }
        estadiaNueva.setHuespedes(huespedesReales); // Asigna la lista para la tabla intermedia

        // 5. Guardar (Cascade persistirá la relación en estadia_huesped)
        estadiaRepository.save(estadiaNueva);
    }
}