package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Direccion;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Huesped;
import Facultad.TrabajoPracticoDesarrollo.Huesped.DtoDireccion;
import Facultad.TrabajoPracticoDesarrollo.Huesped.DtoHuesped;
import Facultad.TrabajoPracticoDesarrollo.enums.PosIva;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;

import java.sql.ResultSet;
import java.sql.SQLException;


public class MapearHuesped  {

    // Composición: Usamos el mapper de dirección
    private static MapearDireccion mapearDireccion = new MapearDireccion();


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

    public static DtoHuesped mapearResultSetADto(ResultSet rs) throws SQLException {
        DtoHuesped dto = new DtoHuesped();

        dto.setApellido(rs.getString("apellido"));
        dto.setNombres(rs.getString("nombres"));
        dto.setNroDocumento(rs.getString("numero_documento"));
        dto.setCuit(rs.getString("cuit"));
        dto.setNacionalidad(rs.getString("nacionalidad"));
        dto.setFechaNacimiento(rs.getDate("fecha_nacimiento"));

        // 1. TELEFONO (Viene del MAX(t.telefono))
        long tel = rs.getLong("telefono");
        if (!rs.wasNull() && tel != 0) {
            dto.setTelefono(java.util.Collections.singletonList(tel));
        }

        // 2. OCUPACION (Viene del MAX(o.ocupacion))
        String ocupacion = rs.getString("ocupacion");
        if (ocupacion != null) {
            dto.setOcupacion(java.util.Collections.singletonList(ocupacion));
        }

        // 3. EMAIL (Viene del MAX(e.email))
        String email = rs.getString("email");
        if (email != null) {
            dto.setEmail(java.util.Collections.singletonList(email));
        }

        // 4. DIRECCION
        int idDir = rs.getInt("id_direccion");
        if (!rs.wasNull() && idDir > 0) {
            DtoDireccion dtoDirTemp = new DtoDireccion();
            dtoDirTemp.setId(idDir);
            dto.setDtoDireccion(dtoDirTemp);
        }

        // 5. ENUMS
        try {
            String posIvaStr = rs.getString("pos_iva");
            if (posIvaStr != null) {
                // Convertimos el String de la BD al Enum usando tu metodo helper
                dto.setPosicionIva(PosIva.fromString(posIvaStr));
            }

            String tipoDocStr = rs.getString("tipo_documento");
            if (tipoDocStr != null) {
                dto.setTipoDocumento(TipoDocumento.valueOf(tipoDocStr.toUpperCase()));
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error mapeando enums: " + e.getMessage());
        }

        return dto;
    }
}