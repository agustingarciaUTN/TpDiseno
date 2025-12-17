package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Reserva;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Repositorio Spring Data JPA para la entidad {@link Reserva}.
 *
 * <p>Provee operaciones CRUD y de paginación heredadas de {@link JpaRepository}
 * para la entidad {@code Reserva} cuyo identificador es de tipo {@code Integer}.</p>
 *
 * <p>Notas:
 * - El método {@code save} provisto por {@link JpaRepository} maneja inserciones y actualizaciones
 *   (reemplaza la antigua implementación de persistirReserva()).</p>
 * - Las consultas marcadas con {@link Modifying} realizan operaciones de modificación (DELETE/UPDATE)
 *   y deben ejecutarse dentro de una transacción (por ejemplo desde un servicio anotado con
 *   {@code @Transactional}).</p>
 */
@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    // 1. REEMPLAZO DE: persistirReserva()
    // No hace falta escribir nada, JpaRepository ya trae el método .save(reserva)
    // que maneja INSERT, claves generadas y transacciones automáticamente.

    /**
     * Busca reservas activas que se solapen con el rango de fechas indicado.
     *
     * <p>Equivalente a la consulta SQL original:
     * {@code SELECT * FROM reserva WHERE estado_reserva = 'ACTIVA' AND fecha_desde < ? AND fecha_hasta > ?}</p>
     *
     * @param inicio fecha de inicio del rango a comprobar (inclusive/exclusive según lógica de negocio)
     * @param fin    fecha de fin del rango a comprobar
     * @return lista de {@link Reserva} que están en estado ACTIVA y que se solapan con el rango proporcionado
     */
    @Query("SELECT r FROM Reserva r WHERE r.estadoReserva = Facultad.TrabajoPracticoDesarrollo.enums.EstadoReserva.ACTIVA " +
            "AND r.fechaDesde < :fin AND r.fechaHasta > :inicio")
    List<Reserva> buscarReservasActivasEnRango(@Param("inicio") Date inicio, @Param("fin") Date fin);

    /**
     * Comprueba si existe al menos una reserva activa para una habitación en el rango de fechas indicado.
     *
     * <p>Devuelve {@code true} si hay al menos una reserva ACTIVA que solapa el rango
     * para la habitación cuyo número es {@code nroHabitacion}.</p>
     *
     * @param nroHabitacion número de la habitación (campo texto en la entidad habitación)
     * @param inicio        fecha de inicio del rango a comprobar
     * @param fin           fecha de fin del rango a comprobar
     * @return {@code true} si existe alguna reserva activa que se solapa, {@code false} en caso contrario
     */
    @Query("SELECT COUNT(r) > 0 FROM Reserva r WHERE r.habitacion.numero = :nroHabitacion " +
            "AND r.estadoReserva = Facultad.TrabajoPracticoDesarrollo.enums.EstadoReserva.ACTIVA " +
            "AND r.fechaDesde < :fin AND r.fechaHasta > :inicio")
    boolean existeReservaEnFecha(
            @Param("nroHabitacion") String nroHabitacion,
            @Param("inicio") Date inicio,
            @Param("fin") Date fin
    );

    // PARA EL CU11 (BORRAR)
    // Borramos buscando por los campos de texto
    /**
     * Elimina reservas asociadas a un huésped identificadas por tipo y número de documento.
     *
     * <p>Ejecuta un {@code DELETE} sobre la tabla {@code reserva} filtrando por
     * {@code tipoDocumentoResponsable} y {@code nroDocumentoResponsable}.</p>
     *
     * <p>IMPORTANTE: al ser un método {@link Modifying}, debe invocarse dentro de una transacción
     * (por ejemplo desde un servicio anotado con {@code @Transactional}).</p>
     *
     * @param tipo tipo de documento del responsable (enum {@link TipoDocumento})
     * @param nro  número de documento del responsable
     */
    @Modifying
    @Query("DELETE FROM Reserva r WHERE r.tipoDocumentoResponsable = :tipo AND r.nroDocumentoResponsable = :nro")
    void deleteByHuesped(@Param("tipo") TipoDocumento tipo, @Param("nro") String nro);

    // PARA EL CU10 (FUSIÓN/MIGRACIÓN)
    // Actualizamos los datos de texto de la reserva para que apunten al nuevo DNI/Nombre
    /**
     * Migra/actualiza los datos de texto de las reservas de un responsable antiguo al nuevo.
     *
     * <p>Actualiza los campos de documento, nombre, apellido y teléfono para todas las reservas
     * que coincidan con el documento viejo.</p>
     *
     * <p>IMPORTANTE: al ser un método {@link Modifying}, debe ejecutarse dentro de una transacción.</p>
     *
     * @param viejoTipo  tipo de documento antiguo
     * @param viejoNro   número de documento antiguo
     * @param nuevoTipo  nuevo tipo de documento
     * @param nuevoNro   nuevo número de documento
     * @param nuevoNombre nuevo nombre a guardar en las reservas
     * @param nuevoApellido nuevo apellido a guardar en las reservas
     * @param nuevoTel   nuevo teléfono a guardar en las reservas
     */
    @Modifying
    @Query("UPDATE Reserva r SET " +
            "r.tipoDocumentoResponsable = :nuevoTipo, " +
            "r.nroDocumentoResponsable = :nuevoNro, " +
            "r.nombreHuespedResponsable = :nuevoNombre, " +
            "r.apellidoHuespedResponsable = :nuevoApellido, " +
            "r.telefonoHuespedResponsable = :nuevoTel " +
            "WHERE r.tipoDocumentoResponsable = :viejoTipo AND r.nroDocumentoResponsable = :viejoNro")
    void migrarReservas(
            @Param("viejoTipo") TipoDocumento viejoTipo, @Param("viejoNro") String viejoNro,
            @Param("nuevoTipo") TipoDocumento nuevoTipo, @Param("nuevoNro") String nuevoNro,
            @Param("nuevoNombre") String nuevoNombre, @Param("nuevoApellido") String nuevoApellido,
            @Param("nuevoTel") String nuevoTel
    );

    // CU06: Búsqueda para cancelación
    /**
     * Búsqueda de reservas para el flujo de cancelación.
     *
     * <p>Consulta nativa que filtra por estado {@code ACTIVA} y permite filtrar opcionalmente
     * por apellido y nombre del responsable mediante ILIKE (búsqueda case-insensitive con patrones).</p>
     *
     * <p>Parámetros {@code null} se interpretan como "sin filtro" para ese campo.</p>
     *
     * @param apellido filtro de apellido (puede ser {@code null} o contener comodines SQL, p.\u00e9j. \"%Gonz%\")
     * @param nombre   filtro de nombre (puede ser {@code null} o contener comodines SQL)
     * @return lista de {@link Reserva} que cumplen los criterios; devuelve lista vacía si no hay coincidencias
     */
    @Query(value = "SELECT * FROM reserva r " +
            "WHERE CAST(r.estado_reserva AS text) = 'ACTIVA' " +
            "AND (CAST(:apellido AS text) IS NULL OR CAST(r.\"ApellidoHuespedResponsable\" AS text) ILIKE :apellido) " +
            "AND (CAST(:nombre AS text) IS NULL OR CAST(r.\"NombreHuespedResponsable\" AS text) ILIKE :nombre)",
            nativeQuery = true)
    List<Reserva> buscarParaCancelar(
            @Param("apellido") String apellido,
            @Param("nombre") String nombre
    );

    // Mantenimiento: Actualizar nombre/apellido cuando el huésped los corrige
    @Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query("UPDATE Reserva r SET " +
            "r.nombreHuespedResponsable = :nombre, " +
            "r.apellidoHuespedResponsable = :apellido, " +
            "r.telefonoHuespedResponsable = :telefono " +
            "WHERE r.tipoDocumentoResponsable = :tipo AND r.nroDocumentoResponsable = :nro")
    void actualizarDatosPersonales(
            @Param("tipo") TipoDocumento tipo,
            @Param("nro") String nro,
            @Param("nombre") String nombre,
            @Param("apellido") String apellido,
            @Param("telefono") String telefono
    );}