package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.EstadiaHuesped;
import Facultad.TrabajoPracticoDesarrollo.Dominio.EstadiaHuespedId;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Huesped;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repositorio Spring Data JPA para la entidad {@link EstadiaHuesped}.
 *
 * <p>Proporciona operaciones CRUD heredadas de {@link JpaRepository} y consultas
 * específicas relacionadas con la asociación entre una {@code Estadia} y sus {@code Huespedes}.</p>
 *
 * <p>Notas importantes:
 * - La clave primaria es {@link EstadiaHuespedId} (entidad de unión/associación).
 * - Los métodos anotados con {@code @Modifying} realizan cambios en la base de datos y deben
 *   ejecutarse dentro de una transacción (por ejemplo desde un servicio anotado con {@code @Transactional}).</p>
 */
@Repository
public interface EstadiaHuespedRepository extends JpaRepository<EstadiaHuesped, EstadiaHuespedId> {

    /**
     * Indica si existe al menos un registro de asociación entre estadía y el {@link Huesped} dado.
     *
     * @param huesped entidad {@link Huesped} a verificar
     * @return {@code true} si existe al menos una asociación; {@code false} en caso contrario
     */
    boolean existsByHuesped(Huesped huesped);

    /**
     * Migra el historial de identificadores de huésped dentro de la tabla de asociación {@code estadia_huesped}.
     *
     * <p>Esta consulta nativa actualiza las columnas {@code tipo_documento} y {@code nro_documento},
     * reemplazando el par ({@code viejoTipo}, {@code viejoNro}) por ({@code nuevoTipo}, {@code nuevoNro}).</p>
     *
     * <p>Precauciones y consideraciones:
     * - Es una consulta {@code nativeQuery} y está anotada con {@code @Modifying}, por lo que debe
     *   ejecutarse dentro de una transacción para garantizar atomicidad y consistencia.
     * - Asegurarse de que los valores provistos coincidan con el formato/longitud de las columnas en BD.
     * - Esta operación modifica datos históricos; conviene realizar backups o pruebas en entorno controlado
     *   antes de ejecutarla en producción.</p>
     *
     * @param viejoTipo representación textual del tipo de documento a reemplazar
     * @param viejoNro  número de documento a reemplazar
     * @param nuevoTipo nueva representación textual del tipo de documento a establecer
     * @param nuevoNro  nuevo número de documento a establecer
     */
    @Modifying
    @Query(value = "UPDATE estadia_huesped " +
            "SET tipo_documento = :nuevoTipo, nro_documento = :nuevoNro " +
            "WHERE tipo_documento = :viejoTipo AND nro_documento = :viejoNro",
            nativeQuery = true)
    void migrarHistorialEstadias(
            @Param("viejoTipo") String viejoTipo,  // <--- Verificar @Param
            @Param("viejoNro") String viejoNro,
            @Param("nuevoTipo") String nuevoTipo,
            @Param("nuevoNro") String nuevoNro
    );
}