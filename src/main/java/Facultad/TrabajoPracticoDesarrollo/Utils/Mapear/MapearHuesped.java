package Facultad.TrabajoPracticoDesarrollo.Utils.Mapear;

import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoHuespedBusqueda;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Direccion;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Huesped;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoDireccion;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoHuesped;

/**
 * Utilidad para convertir entre el DTO {@link DtoHuesped} (y variantes de búsqueda) y la entidad {@link Huesped}.
 *
 * <p>Provee métodos estáticos para:
 * - Crear una entidad {@link Huesped} desde un DTO (usado en alta/crear).
 * - Actualizar una entidad existente con valores del DTO (usado en modificación).
 * - Crear una entidad sin mapear la dirección directamente (útil cuando la dirección se crea/recupera aparte).
 * - Mapear criterios de búsqueda a una entidad parcial para filtrar.</p>
 *
 * <p>Comportamiento general:
 * - Si el DTO de entrada es {@code null}, los métodos devuelven {@code null} o no realizan acción (según corresponda).
 * - El mapeo de la dirección delega en {@link MapearDireccion} cuando corresponde.
 * - El método de actualización modifica la entidad existente (no crea un nuevo objeto).</p>
 */
public class MapearHuesped  {


    // (Sirve solo para ALTA / CREAR)
    /**
     * Crea una nueva instancia de {@link Huesped} a partir de un {@link DtoHuesped}.
     *
     * @param dto DTO de entrada; si es {@code null} se retorna {@code null}
     * @return nueva instancia de {@link Huesped} construida desde el DTO, o {@code null} si {@code dto} es {@code null}
     *
     * <p>Notas:
     * - Se copian campos personales y de contacto.
     * - Se mapea la dirección
     * - Manejo seguro de enums: si el DTO tiene {@code null} para enums, se preserva {@code null}.</p>
     */
    public static Huesped mapearDtoAEntidad(DtoHuesped dto) {
        if (dto == null) return null;

        return new Huesped.Builder()
                .tipoDocumento(dto.getTipoDocumento())
                .nroDocumento(dto.getNroDocumento())
                .nombres(dto.getNombres())
                .apellido(dto.getApellido())
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
    /**
     * Actualiza los campos de una entidad {@link Huesped} existente con los valores del {@link DtoHuesped}.
     *
     * @param entidadExistente entidad a actualizar; si es {@code null} no se realiza ninguna acción
     * @param dto DTO con los nuevos valores; si es {@code null} no se realiza ninguna acción
     *
     * <p>Comportamiento:
     * - No crea una nueva entidad; modifica la pasada por referencia.
     * - No se modifica la dirección aquí: se espera que el servicio maneje la creación/actualización de la dirección
     *   según el diagrama de dominio.</p>
     */
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

    /**
     * Crea una nueva instancia de {@link Huesped} a partir de un {@link DtoHuesped} pero utilizando
     * la instancia de {@link Direccion} provista (no mapea la dirección desde el DTO).
     *
     * @param dto DTO de entrada; si es {@code null} se retorna {@code null}
     * @param direccion instancia de {@link Direccion} ya resuelta (puede ser {@code null})
     * @return nueva instancia de {@link Huesped} con la dirección proporcionada, o {@code null} si {@code dto} es {@code null}
     *
     * <p>Uso: cuando la dirección se crea/recupera separadamente y debe asociarse al huésped.</p>
     */
    public static Huesped mapearDtoAEntidadSinDireccion(DtoHuesped dto, Direccion direccion) {
        if (dto == null) return null;

        return new Huesped.Builder()
                .tipoDocumento(dto.getTipoDocumento())
                .nroDocumento(dto.getNroDocumento())
                .nombres(dto.getNombres())
                .apellido(dto.getApellido())
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


    /**
     * Mapea una entidad {@link Huesped} a su {@link DtoHuesped}.
     *
     * @param entidad entidad de entrada; si es {@code null} se retorna {@code null}
     * @return {@link DtoHuesped} construido desde la entidad, o {@code null} si {@code entidad} es {@code null}
     *
     */
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

    /**
     * Mapea un DTO de búsqueda {@link DtoHuespedBusqueda} a una entidad parcial {@link Huesped}
     * para ser utilizada en filtros o criterios de búsqueda.
     *
     * @param dto DTO de búsqueda; si es {@code null} se retorna {@code null}
     * @return instancia de {@link Huesped} con los campos de búsqueda copiados, o {@code null} si {@code dto} es {@code null}
     *
     * <p>Comportamiento: como los campos de búsqueda son opcionales, sólo se copian los que pueden utilizarse
     * para filtrar (nombres, apellido, tipo/nro de documento). El resto se deja sin setear.</p>
     */
    public static Huesped mapearBusquedaAEntidad(DtoHuespedBusqueda dto) {
        if (dto == null) return null;

        // Como en la búsqueda los campos son opcionales, pasamos null si no existen.
        return new Huesped.Builder()
                .nombres(dto.getNombres())
                .apellido(dto.getApellido())
                .tipoDocumento(dto.getTipoDocumento())
                .nroDocumento(dto.getNroDocumento())
                // El resto de datos (dirección, email, etc.) no se usan para filtrar en este caso,
                // así que no hace falta setearlos o se dejan en null/vacío por el builder.
                .build();
    }

}