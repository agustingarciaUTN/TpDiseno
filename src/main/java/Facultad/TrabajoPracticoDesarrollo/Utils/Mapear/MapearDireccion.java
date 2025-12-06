package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Direccion;
import Facultad.TrabajoPracticoDesarrollo.Huesped.DtoDireccion;

public class MapearDireccion  {


    public static Direccion mapearDtoAEntidad(DtoDireccion dto) {
        if (dto == null) return null;
        return new Direccion.Builder(
                dto.getCalle(),
                dto.getNumero(),
                dto.getLocalidad(),
                dto.getProvincia(),
                dto.getPais()
        )
                .id(dto.getId())
                .departamento(dto.getDepartamento())
                .piso(dto.getPiso())
                .codigoPostal(dto.getCodPostal())
                .build();
    }

    public static DtoDireccion mapearEntidadADto(Direccion entity) {
        if (entity == null) return null;
        return new DtoDireccion.Builder(
                entity.getCalle(),
                entity.getNumero(),
                entity.getLocalidad(),
                entity.getProvincia(),
                entity.getPais()
        )
                .idDireccion(entity.getId())
                .departamento(entity.getDepartamento())
                .piso(entity.getPiso())
                .codPostal(entity.getCodigoPostal())
                .build();
    }
}