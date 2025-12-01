// java
package Utils.Mapear;

import Dominio.Huesped;
import Huesped.DtoHuesped;

import java.util.ArrayList;

public class MapearHuesped implements MapeoInterfaz<DtoHuesped, Huesped> {

    private final MapearDireccion mapearDireccion = new MapearDireccion();
    private final MapearEstadia mapearEstadia = new MapearEstadia(this); // pasar mapper para relaciones si hace falta

    @Override
    public Huesped mapearDtoAEntidad(DtoHuesped dto) {
        if (dto == null) return null;
        Huesped h = new Huesped();
        try { h.setNombres(dto.getNombres()); } catch (Throwable ignored) {}
        try { h.setApellido(dto.getApellido()); } catch (Throwable ignored) {}
        try { h.setTelefono(dto.getTelefono()); } catch (Throwable ignored) {}
        try { h.setTipoDocumento(dto.getTipoDocumento()); } catch (Throwable ignored) {}
        try { h.setNroDocumento(dto.getNroDocumento()); } catch (Throwable ignored) {}
        try { h.setCuit(dto.getCuit()); } catch (Throwable ignored) {}
        try { h.setPosicionIva(dto.getPosicionIva()); } catch (Throwable ignored) {}
        try { h.setFechaNacimiento(dto.getFechaNacimiento()); } catch (Throwable ignored) {}
        try { h.setEmail(dto.getEmail()); } catch (Throwable ignored) {}
        try { h.setOcupacion(dto.getOcupacion()); } catch (Throwable ignored) {}
        try { h.setNacionalidad(dto.getNacionalidad()); } catch (Throwable ignored) {}

        try {
            if (dto.getDtoDireccion() != null) {
                h.setDireccion(mapearDireccion.mapearDtoAEntidad(dto.getDtoDireccion()));
            }
        } catch (Throwable ignored) {}

        // Mapear estadías de forma mínima para evitar ciclos completos
        try {
            if (dto.getDtoEstadias() != null && !dto.getDtoEstadias().isEmpty()) {
                ArrayList< Dominio.Estadia > lista = new ArrayList<>();
                for (Estadia.DtoEstadia de : dto.getDtoEstadias()) {
                    Dominio.Estadia e = mapearEstadia.mapearDtoAEntidad(de);
                    if (e != null) lista.add(e);
                }
                try { h.setEstadias(lista); } catch (Throwable ignored) {}
            }
        } catch (Throwable ignored) {}

        return h;
    }

    @Override
    public DtoHuesped mapearEntidadADto(Huesped entidad) {
        if (entidad == null) return null;
        DtoHuesped dto = new DtoHuesped();
        try { dto.setNombres(entidad.getNombres()); } catch (Throwable ignored) {}
        try { dto.setApellido(entidad.getApellido()); } catch (Throwable ignored) {}
        try { dto.setTelefono(entidad.getTelefono()); } catch (Throwable ignored) {}
        try { dto.setTipoDocumento(entidad.getTipoDocumento()); } catch (Throwable ignored) {}
        try { dto.setNroDocumento(entidad.getNroDocumento()); } catch (Throwable ignored) {}
        try { dto.setCuit(entidad.getCuit()); } catch (Throwable ignored) {}
        try { dto.setPosicionIva(entidad.getPosicionIva()); } catch (Throwable ignored) {}
        try { dto.setFechaNacimiento(entidad.getFechaNacimiento()); } catch (Throwable ignored) {}
        try { dto.setEmail(entidad.getEmail()); } catch (Throwable ignored) {}
        try { dto.setOcupacion(entidad.getOcupacion()); } catch (Throwable ignored) {}
        try { dto.setNacionalidad(entidad.getNacionalidad()); } catch (Throwable ignored) {}

        try {
            if (entidad.getDireccion() != null) {
                dto.setDtoDireccion(mapearDireccion.mapearEntidadADto(entidad.getDireccion()));
            }
        } catch (Throwable ignored) {}

        // Mapear estadías a DTOs (mínimo)
        try {
            if (entidad.getEstadias() != null && !entidad.getEstadias().isEmpty()) {
                ArrayList<Estadia.DtoEstadia> lista = new ArrayList<>();
                for (Dominio.Estadia e : entidad.getEstadias()) {
                    Estadia.DtoEstadia de = mapearEstadia.mapearEntidadADto(e);
                    if (de != null) lista.add(de);
                }
                try { dto.setDtoEstadias(lista); } catch (Throwable ignored) {}
            }
        } catch (Throwable ignored) {}

        return dto;
    }
}
