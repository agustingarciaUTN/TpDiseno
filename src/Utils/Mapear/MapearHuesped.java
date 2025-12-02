package Utils.Mapear;

import Dominio.Direccion;
import Dominio.Huesped;
import Huesped.DtoHuesped;
import enums.PosIva;

public class MapearHuesped  {

    // Composición: Usamos el mapper de dirección
    private static MapearDireccion mapearDireccion = new MapearDireccion();


    public static Huesped mapearDtoAEntidad(DtoHuesped dto) {
        if (dto == null) return null;

        return new Huesped.Builder(
                dto.getNombres(),
                dto.getApellido(),
                dto.getTipoDocumento(),
                dto.getNroDocumento()
        )
                .telefono(dto.getTelefono()) // Si es lista en tu dominio, ajusta aquí
                .cuit(dto.getCuit())
                // Manejo seguro de Enum y conversión de String
                .posicionIva(dto.getPosicionIva() != null ? dto.getPosicionIva() : null)
                .fechaNacimiento(dto.getFechaNacimiento())
                .email(dto.getEmail())
                .ocupacion(dto.getOcupacion())
                .nacionalidad(dto.getNacionalidad())
                // AQUÍ USAMOS EL OTRO MAPPER
                .direccion(mapearDireccion.mapearDtoAEntidad(dto.getDtoDireccion()))
                .build();
    }

    public static Huesped mapearDtoAEntidadSinDireccion(DtoHuesped dto, Direccion direccion) {
        if (dto == null) return null;

        return new Huesped.Builder(
                dto.getNombres(),
                dto.getApellido(),
                dto.getTipoDocumento(),
                dto.getNroDocumento()
        )
                .telefono(dto.getTelefono()) // Si es lista en tu dominio, ajusta aquí
                .cuit(dto.getCuit())
                // Manejo seguro de Enum y conversión de String
                .posicionIva(dto.getPosicionIva() != null ? dto.getPosicionIva() : null)
                .fechaNacimiento(dto.getFechaNacimiento())
                .email(dto.getEmail())
                .ocupacion(dto.getOcupacion())
                .nacionalidad(dto.getNacionalidad())

                .direccion(direccion)
                .build();
    }


    public static DtoHuesped mapearEntidadADto(Huesped entidad) {
        if (entidad == null) return null;

        return new DtoHuesped.Builder()
                .nombres(entidad.getNombres())
                .apellido(entidad.getApellido())
                .tipoDocumento(entidad.getTipoDocumento())
                .documento(entidad.getNroDocumento())
                .telefono(entidad.getTelefono())
                .cuit(entidad.getCuit())
                .posicionIva(entidad.getPosicionIva() != null ? entidad.getPosicionIva() : null)
                .fechaNacimiento(entidad.getFechaNacimiento())
                .email(entidad.getEmail())
                .ocupacion(entidad.getOcupacion())
                .nacionalidad(entidad.getNacionalidad())
                // AQUÍ USAMOS EL OTRO MAPPER
                .direccion(mapearDireccion.mapearEntidadADto(entidad.getDireccion()))
                .build();
    }
}