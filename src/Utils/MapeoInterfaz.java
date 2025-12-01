package Utils;
/**
 * Interfaz genérica para mapeos DTO <-> Entidad.
 */
public interface MapeoInterfaz <DTO, ENT>{
    ENT mapearDtoAEntidad(DTO dto);
    DTO mapearEntidadADto(ENT entidad);
}
