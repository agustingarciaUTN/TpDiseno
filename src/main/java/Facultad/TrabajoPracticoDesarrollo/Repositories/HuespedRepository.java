package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Direccion;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Huesped;
import Facultad.TrabajoPracticoDesarrollo.Dominio.HuespedId;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para la entidad {@link Huesped}.
 *
 * <p>Proporciona operaciones CRUD y consultas específicas para la gestión de huéspedes.
 * La clave primaria de la entidad es de tipo compuesto {@link HuespedId}.</p>
 *
 * <p>Notas generales:
 * - Algunas consultas usan SQL nativo para poder filtrar/concatenar sobre columnas concretas
 *   y para limpiar tablas satélite (colecciones/element_collections) antes de borrados nativos.
 * - Los métodos anotados con {@code @Modifying} realizan cambios en la BD y deben ejecutarse
 *   dentro de una transacción (ej.: desde un servicio anotado con {@code @Transactional}).</p>
 */
@Repository
public interface HuespedRepository extends JpaRepository<Huesped, HuespedId> {

    /**
     * Busca huéspedes por criterios combinables.
     *
     * <p>Reemplaza a una antigua función 'obtenerHuespedesPorCriterio'.</p>
     *
     * Comportamiento:
     * - Coincidencia parcial para {@code apellido} y {@code nombres} (prefijo, case-insensitive).
     * - {@code tipo} se compara por su representación textual (por eso el parámetro se recibe como {@code String}).
     * - {@code nroDoc} se compara por prefijo.
     * - Si un parámetro es {@code NULL} o cadena vacía, se ignora ese filtro.</p>
     *
     * <p>Se utiliza una consulta nativa para permitir operaciones sobre columnas y joins específicos.</p>
     *
     * @param apellido filtro por apellido (prefijo, case-insensitive). Puede ser {@code null} o vacío.
     * @param nombres  filtro por nombres (prefijo, case-insensitive). Puede ser {@code null} o vacío.
     * @param tipo     tipo de documento como texto (ej. la representación de {@link TipoDocumento}). Puede ser {@code null}.
     * @param nroDoc   número de documento (prefijo). Puede ser {@code null} o vacío.
     * @return lista de {@link Huesped} que cumplen los criterios; lista vacía si no hay coincidencias.
     */
    @Query(value = "SELECT DISTINCT h.* FROM huesped h " +
            "LEFT JOIN direccion d ON d.id_direccion = h.id_direccion " +
            "WHERE " +
            "(CAST(:apellido AS TEXT) IS NULL OR :apellido = '' OR LOWER(h.apellido) LIKE LOWER(CONCAT(CAST(:apellido AS TEXT), '%'))) AND " +
            "(CAST(:nombres AS TEXT) IS NULL OR :nombres = '' OR LOWER(h.nombres) LIKE LOWER(CONCAT(CAST(:nombres AS TEXT), '%'))) AND " +
            "(CAST(:tipo AS TEXT) IS NULL OR CAST(h.tipo_documento AS TEXT) = CAST(:tipo AS TEXT)) AND " +
            "(CAST(:nroDoc AS TEXT) IS NULL OR :nroDoc = '' OR h.numero_documento LIKE CONCAT(CAST(:nroDoc AS TEXT), '%'))",
            nativeQuery = true)
    List<Huesped> buscarPorCriterios(
            @Param("apellido") String apellido,
            @Param("nombres") String nombres,
            @Param("tipo") String tipo,
            @Param("nroDoc") String nroDoc
    );



    // Para el CU10: Permitir cambio de DNI manteniendo historial
    /**
     * Actualiza la identidad de un huésped (tipo y número de documento).
     *
     * <p>Usado en el CU10 para permitir el cambio de DNI manteniendo historial.
     * Es una consulta JPQL de actualización; debe invocarse dentro de una transacción
     * y normalmente junto con manejo de versiones/historial en el servicio.</p>
     *
     * @param viejoTipo tipo de documento actual a buscar
     * @param viejoNro  número de documento actual a buscar
     * @param nuevoTipo nuevo tipo de documento a establecer
     * @param nuevoNro  nuevo número de documento a establecer
     */
    @Modifying
    @Query("UPDATE Huesped h SET h.tipoDocumento = :nuevoTipo, h.nroDocumento = :nuevoNro " +
            "WHERE h.tipoDocumento = :viejoTipo AND h.nroDocumento = :viejoNro")
    void actualizarIdentidad(
            @Param("viejoTipo") TipoDocumento viejoTipo,
            @Param("viejoNro") String viejoNro,
            @Param("nuevoTipo") TipoDocumento nuevoTipo,
            @Param("nuevoNro") String nuevoNro
    );

    // Cuenta cuántos huéspedes usan esa dirección, para evitar dejar direcciones zombies
    /**
     * Cuenta cuántos huéspedes están asociados a una {@link Direccion}.
     *
     * <p>Útil para evitar eliminar direcciones que todavía están en uso (direcciones "zombies").</p>
     *
     * @param direccion entidad {@link Direccion} a consultar
     * @return cantidad de {@link Huesped} que referencian la dirección
     */
    long countByDireccion(Direccion direccion);


    // --- LIMPIEZA DE TABLAS SATÉLITE (NECESARIO PARA BORRADO NATIVO) ---
    /**
     * Elimina los teléfonos asociados a un huésped identificado por tipo y número de documento.
     *
     * <p>Consulta nativa para borrar en la tabla de colección {@code telefono_huesped}.</p>
     *
     * @param tipo representación textual del tipo de documento
     * @param nro  número de documento
     */
    @Modifying
    @Query(value = "DELETE FROM telefono_huesped WHERE CAST(tipo_documento AS TEXT) = :tipo AND nro_documento = :nro", nativeQuery = true)
    void borrarTelefonos(@Param("tipo") String tipo, @Param("nro") String nro);

    /**
     * Elimina los emails asociados a un huésped identificado por tipo y número de documento.
     *
     * @param tipo representación textual del tipo de documento
     * @param nro  número de documento
     */
    @Modifying
    @Query(value = "DELETE FROM email_huesped WHERE CAST(tipo_documento AS TEXT) = :tipo AND nro_documento = :nro", nativeQuery = true)
    void borrarEmails(@Param("tipo") String tipo, @Param("nro") String nro);

    /**
     * Elimina las ocupaciones asociadas a un huésped identificado por tipo y número de documento.
     *
     * @param tipo representación textual del tipo de documento
     * @param nro  número de documento
     */
    @Modifying
    @Query(value = "DELETE FROM ocupacion_huesped WHERE CAST(tipo_documento AS TEXT) = :tipo AND nro_documento = :nro", nativeQuery = true)
    void borrarOcupaciones(@Param("tipo") String tipo, @Param("nro") String nro);

    /**
     * Elimina el registro obligatorio de {@code huesped} (borrado nativo).
     *
     * <p>Se recomienda ejecutar las llamadas a {@code borrarTelefonos}, {@code borrarEmails}
     * y {@code borrarOcupaciones} antes de invocar este método para mantener la integridad
     * de tablas satélite cuando se usan borrados nativos.</p>
     *
     * @param tipo representación textual del tipo de documento
     * @param nro  número de documento
     */
    @Modifying
    @Query(value = "DELETE FROM huesped WHERE CAST(tipo_documento AS TEXT) = :tipo AND numero_documento = :nro", nativeQuery = true)
    void borrarObligatorio(
            @Param("tipo") String tipo,
            @Param("nro") String nro
    );
}