package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Direccion;
import Facultad.TrabajoPracticoDesarrollo.Huesped.DtoDireccion;

public class MapearDireccion  {


    public static Direccion mapearDtoAEntidad(DtoDireccion dto) {
        if (dto == null) return null;
        return new Direccion.Builder()
                .id(dto.getId())
                .calle(dto.getCalle())
                .numero((dto.getNumero()))
                .departamento(dto.getDepartamento())
                .piso(dto.getPiso())
                .codigoPostal(dto.getCodPostal())
                .localidad(dto.getLocalidad())
                .provincia(dto.getProvincia())
                .pais(dto.getPais())
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
                .codPostal(entity.getCodPostal())
                .build();
    }
}