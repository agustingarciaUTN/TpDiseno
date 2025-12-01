// java
package Utils;

import Dominio.Direccion;
import Huesped.DtoDireccion;

public class MapearDireccion implements MapeoInterfaz<DtoDireccion, Direccion> {

    @Override
    public Direccion mapearDtoAEntidad(DtoDireccion dto) {
        if (dto == null) return null;
        Direccion d = new Direccion();
        try { d.setCalle(dto.getCalle()); } catch (Throwable ignored) {}
        try { d.setNumero(dto.getNumero()); } catch (Throwable ignored) {}
        try { d.setPiso(dto.getPiso()); } catch (Throwable ignored) {}
        try { d.setDepartamento(dto.getDepartamento()); } catch (Throwable ignored) {}
        try { d.setLocalidad(dto.getLocalidad()); } catch (Throwable ignored) {}
        try { d.setProvincia(dto.getProvincia()); } catch (Throwable ignored) {}
        try { d.setPais(dto.getPais()); } catch (Throwable ignored) {}
        try { d.setCodigoPostal(dto.getCodPostal()); } catch (Throwable ignored) {}
        return d;
    }

    @Override
    public DtoDireccion mapearEntidadADto(Direccion entidad) {
        if (entidad == null) return null;
        DtoDireccion dto = new DtoDireccion();
        try { dto.setCalle(entidad.getCalle()); } catch (Throwable ignored) {}
        try { dto.setNumero(entidad.getNumero()); } catch (Throwable ignored) {}
        try { dto.setPiso(entidad.getPiso()); } catch (Throwable ignored) {}
        try { dto.setDepartamento(entidad.getDepartamento()); } catch (Throwable ignored) {}
        try { dto.setLocalidad(entidad.getLocalidad()); } catch (Throwable ignored) {}
        try { dto.setProvincia(entidad.getProvincia()); } catch (Throwable ignored) {}
        try { dto.setPais(entidad.getPais()); } catch (Throwable ignored) {}
        try { dto.setCodPostal(entidad.getCodigoPostal()); } catch (Throwable ignored) {}
        return dto;
    }
}
