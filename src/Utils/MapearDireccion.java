package Utils;

import Dominio.Direccion;
import Huesped.DtoDireccion;

public class MapearDireccion implements MapeoInterfaz<DtoDireccion, Direccion> {

    @Override
    public Direccion mapearDtoAEntidad(DtoDireccion dto) {
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

    @Override
    public DtoDireccion mapearEntidadADto(Direccion entity) {
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