package Utils.Mapear;

import Dominio.Cama;
import Habitacion.DtoCama;

public class MapearCama implements MapeoInterfaz<DtoCama, Cama> {

    @Override
    public Cama mapearDtoAEntidad(DtoCama dto) {
        if (dto == null) return null;
        return new Cama.Builder()
                .idCama(dto.getIdCama())
                .tipoCama(dto.getTipoCama())
                .build();
    }

    @Override
    public DtoCama mapearEntidadADto(Cama entidad) {
        if (entidad == null) return null;
        return new DtoCama.Builder()
                .idCama(entidad.getIdCama())
                .tipoCama(entidad.getTipoCama())
                .build();
    }
}