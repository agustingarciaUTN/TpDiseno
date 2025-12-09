package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Direccion;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Huesped;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoDireccion;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoHuesped;
import Facultad.TrabajoPracticoDesarrollo.enums.PosIva;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;

import java.sql.ResultSet;
import java.sql.SQLException;


public class MapearHuesped  {


    // (Sirve solo para ALTA / CREAR)
    public static Huesped mapearDtoAEntidad(DtoHuesped dto) {
        if (dto == null) return null;

        return new Huesped.Builder(
                dto.getTipoDocumento(),
                dto.getApellido(),
                dto.getNombres(),
                dto.getNroDocumento()
        )
                .telefonos(dto.getTelefono()) // Si es lista en tu dominio, ajusta aquí
                .cuit(dto.getCuit())
                // Manejo seguro de Enum y conversión de String
                .posicionIva(dto.getPosicionIva() != null ? dto.getPosicionIva() : null)
                .fechaNacimiento(dto.getFechaNacimiento())
                .emails(dto.getEmail())
                .ocupaciones(dto.getOcupacion())
                .nacionalidad(dto.getNacionalidad())
                // AQUÍ USAMOS EL OTRO MAPPER
                .direccion(MapearDireccion.mapearDtoAEntidad(dto.getDtoDireccion()))
                .build();
    }

    // (Sirve para MODIFICAR / ACTUALIZAR)
    public static void actualizarEntidadDesdeDto(Huesped entidadExistente, DtoHuesped dto) {
        if (dto == null || entidadExistente == null) return;

        // Acá NO hacemos 'new'. Usamos los setters sobre el objeto que ya existe.
        entidadExistente.setApellido(dto.getApellido());
        entidadExistente.setNombres(dto.getNombres());
        entidadExistente.setTipoDocumento(dto.getTipoDocumento());
        entidadExistente.setNroDocumento(dto.getNroDocumento());
        entidadExistente.setTelefono(dto.getTelefono());
        entidadExistente.setEmail(dto.getEmail());
        entidadExistente.setCuit(dto.getCuit());
        entidadExistente.setPosicionIva(dto.getPosicionIva());
        entidadExistente.setFechaNacimiento(dto.getFechaNacimiento());
        entidadExistente.setNacionalidad(dto.getNacionalidad());
        entidadExistente.setOcupacion(dto.getOcupacion());

        // Nota: La dirección la manejamos afuera (en el Service)
        // para respetar tu diagrama, así que acá no la tocamos.
    }

    public static Huesped mapearDtoAEntidadSinDireccion(DtoHuesped dto, Direccion direccion) {
        if (dto == null) return null;

        return new Huesped.Builder(
                dto.getTipoDocumento(),
                dto.getApellido(),
                dto.getNombres(),
                dto.getNroDocumento()
        )
                .telefonos(dto.getTelefono()) // Si es lista en tu dominio, ajusta aquí
                .cuit(dto.getCuit())
                // Manejo seguro de Enum y conversión de String
                .posicionIva(dto.getPosicionIva() != null ? dto.getPosicionIva() : null)
                .fechaNacimiento(dto.getFechaNacimiento())
                .emails(dto.getEmail())
                .ocupaciones(dto.getOcupacion())
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
                .direccion(MapearDireccion.mapearEntidadADto(entidad.getDireccion()))
                .build();
    }

}