package Utils;

import Dominio.*;
import Huesped.DtoDireccion;
import Huesped.DtoHuesped;
import Estadia.DtoEstadia;
import Reserva.DtoReserva;
import Habitacion.DtoHabitacion;

import java.util.ArrayList;

public class MapearHelper {


    public Direccion mapearDtoAEntidadDireccion(DtoDireccion dtoDireccion) {
        if (dtoDireccion == null) return null;

        Direccion direccion = new Direccion();
        try {
            direccion.setCalle(dtoDireccion.getCalle());
        } catch (Throwable ignored) {}
        try {
            direccion.setNumero(dtoDireccion.getNumero());
        } catch (Throwable ignored) {}
        try {
            direccion.setPiso(dtoDireccion.getPiso());
        } catch (Throwable ignored) {}
        try {
            direccion.setDepartamento(dtoDireccion.getDepartamento());
        } catch (Throwable ignored) {}
        try {
            direccion.setLocalidad(dtoDireccion.getLocalidad());
        } catch (Throwable ignored) {}
        try {
            direccion.setProvincia(dtoDireccion.getProvincia());
        } catch (Throwable ignored) {}
        try {
            direccion.setPais(dtoDireccion.getPais());
        } catch (Throwable ignored) {}
        try {
            direccion.setCodigoPostal(dtoDireccion.getCodPostal());
        } catch (Throwable ignored) {}

        return direccion;
    }

    // Helper que mapea Huesped sin mapear estadías (para evitar recursión)
    private Huesped mapearDtoAEntidadHuespedBase(DtoHuesped dtoHuesped){
        if (dtoHuesped == null) return null;

        Huesped huesped = new Huesped();

        try { huesped.setNombres(dtoHuesped.getNombres()); } catch (Throwable ignored) {}
        try { huesped.setApellido(dtoHuesped.getApellido()); } catch (Throwable ignored) {}
        try { huesped.setTelefono(dtoHuesped.getTelefono()); } catch (Throwable ignored) {}
        try { huesped.setTipoDocumento(dtoHuesped.getTipoDocumento()); } catch (Throwable ignored) {}
        try { huesped.setNroDocumento(dtoHuesped.getNroDocumento()); } catch (Throwable ignored) {}
        try { huesped.setCuit(dtoHuesped.getCuit()); } catch (Throwable ignored) {}
        try { huesped.setPosicionIva(dtoHuesped.getPosicionIva()); } catch (Throwable ignored) {}
        try { huesped.setFechaNacimiento(dtoHuesped.getFechaNacimiento()); } catch (Throwable ignored) {}
        try { huesped.setEmail(dtoHuesped.getEmail()); } catch (Throwable ignored) {}
        try { huesped.setOcupacion(dtoHuesped.getOcupacion()); } catch (Throwable ignored) {}
        try { huesped.setNacionalidad(dtoHuesped.getNacionalidad()); } catch (Throwable ignored) {}

        try {
            if (dtoHuesped.getDtoDireccion() != null) {
                Direccion direccion = mapearDtoAEntidadDireccion(dtoHuesped.getDtoDireccion());
                if (direccion != null) {
                    huesped.setDireccion(direccion);
                }
            }
        } catch (Throwable ignored) {}

        return huesped;
    }

    // Método que mapea Huesped completo
    public Huesped mapearDtoAEntidadHuesped(DtoHuesped dtoHuesped){
        if (dtoHuesped == null) return null;

        Huesped huesped = mapearDtoAEntidadHuespedBase(dtoHuesped);

        // Mapear estadías asociadas (si existen) usando el mapeador de Estadia.
        try {
            if (dtoHuesped.getDtoEstadias() != null && !dtoHuesped.getDtoEstadias().isEmpty()) {
                ArrayList<Estadia> estadias = new ArrayList<>();
                for (DtoEstadia dtoE : dtoHuesped.getDtoEstadias()) {
                    Estadia e = mapearDtoAEntidadEstadia(dtoE);
                    if (e != null) estadias.add(e);
                }
                if (!estadias.isEmpty()) {
                    try { huesped.setEstadias(estadias); } catch (Throwable ignored) {}
                }
            }
        } catch (Throwable ignored) {}

        return huesped;
    }

    // Mapea DtoEstadia -> Estadia usando el Builder de Estadia.
    public Estadia mapearDtoAEntidadEstadia(DtoEstadia dtoEstadia) {
        if (dtoEstadia == null) return null;

        if (dtoEstadia.getFechaCheckIn() == null) {
            throw new IllegalArgumentException("fechaCheckIn no puede ser null");
        }

        Estadia.Builder builder = new Estadia.Builder(dtoEstadia.getFechaCheckIn());

        try { builder.idEstadia(dtoEstadia.getIdEstadia()); } catch (Throwable ignored) {}
        try { builder.fechaCheckOut(dtoEstadia.getFechaCheckOut()); } catch (Throwable ignored) {}
        try { builder.valorEstadia(dtoEstadia.getValorEstadia()); } catch (Throwable ignored) {}

        // Mapear reserva mínimamente (si existe)
        try {
            if (dtoEstadia.getDtoReserva() != null) {
                DtoReserva dtoR = dtoEstadia.getDtoReserva();
                Reserva reserva = new Reserva();
                try { reserva.setIdReserva(dtoR.getIdReserva()); } catch (Throwable ignored) {}
                // otros campos pueden mapearse aquí si se conocen
                builder.reserva(reserva);
            }
        } catch (Throwable ignored) {}

        // Mapear habitación mínimamente (si existe)
        try {
            if (dtoEstadia.getDtoHabitacion() != null) {
                DtoHabitacion dtoH = dtoEstadia.getDtoHabitacion();
                Habitacion habitacion = new Habitacion();
                try { habitacion.setNumero(dtoH.getNumero()); } catch (Throwable ignored) {}
                // otros campos pueden mapearse aquí si se conocen
                builder.habitacion(habitacion);
            }
        } catch (Throwable ignored) {}

        // Mapear huespedes usando la versión base del mapeador de Huesped para evitar recursión
        try {
            if (dtoEstadia.getDtoHuespedes() != null && !dtoEstadia.getDtoHuespedes().isEmpty()) {
                for (DtoHuesped dtoH : dtoEstadia.getDtoHuespedes()) {
                    Huesped h = mapearDtoAEntidadHuesped(dtoH);
                    if (h != null) builder.agregarHuesped(h);
                }
            }
        } catch (Throwable ignored) {}

        return builder.build();
    }

}
