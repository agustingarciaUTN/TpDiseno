package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Direccion;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoDireccion;

public class MapearDireccion  {


    public static Direccion mapearDtoAEntidad(DtoDireccion dto) {
        if (dto == null) return null;
        return new Direccion.Builder()
                .id(dto.getIdDireccion())
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

    // (Sirve para MODIFICAR / ACTUALIZAR)
    public static void actualizarEntidadDesdeDto(Direccion direccion, DtoDireccion dto) {
        if (direccion == null || dto == null) return;

        // Solo actualizamos los campos, NO hacemos 'new'
        direccion.setCalle(dto.getCalle());
        direccion.setNumero(dto.getNumero());
        direccion.setDepartamento(dto.getDepartamento());
        direccion.setPiso(dto.getPiso());
        direccion.setCodPostal(dto.getCodPostal());
        direccion.setLocalidad(dto.getLocalidad());
        direccion.setProvincia(dto.getProvincia());
        direccion.setPais(dto.getPais());
    }
}