package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Estadia;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Huesped;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad {@link Estadia}.
 *
 * <p>Provee operaciones CRUD heredadas de {@link JpaRepository} y consultas
 * específicas usadas por la lógica de reservas/check-in/check-out y facturación.</p>
 *
 * <p>Notas:
 * - Las consultas definen lógica para detectar solapamientos de fechas y para
 *   consultar estadías activas o facturables.
 * - Los métodos tipo {@code @Modifying} (si se usan) deben ejecutarse dentro de una
 *   transacción (p. ej. desde servicios anotados con {@code @Transactional}).</p>
 */
@Repository
public interface EstadiaRepository extends JpaRepository<Estadia, Integer> {

    /**
     * Busca estadías que se solapen con el rango dado.
     *
     * Lógica de solapamiento:
     * - Una estadía se considera solapada si su fecha de check-in es anterior al fin solicitado
     *   y su fecha de check-out es posterior al inicio solicitado, o bien si la estadía aún no tiene
     *   fecha de check-out (estadía en curso).
     *
     * @param inicio fecha de inicio del rango a comprobar
     * @param fin    fecha de fin del rango a comprobar
     */
     @Query("SELECT e FROM Estadia e WHERE " +
            "e.fechaCheckIn < :fin AND " +
            "(e.fechaCheckOut IS NULL OR e.fechaCheckOut > :inicio)")
    List<Estadia> buscarEstadiasEnRango(@Param("inicio") Date inicio, @Param("fin") Date fin);

    /**
     * Busca la estadía facturable más reciente para una habitación dada.
     *
     * <p>Se intenta devolver la estadía relevante para facturación (última por fechaCheckIn)
     * que esté activa o cuyo check-out sea en el futuro o en la fecha indicada.</p>
     *
     * @param nroHabitacion número de la habitación
     * @param fechaHoy      fecha de referencia para considerar estadías facturables
     * @return {@link Optional}{@code <Estadia>} con la estadía facturable si existe
     */
    @Query("SELECT e FROM Estadia e " +
            "WHERE e.habitacion.numero = :nroHabitacion " +
            "AND (e.fechaCheckOut IS NULL OR e.fechaCheckOut >= :fechaHoy) " +
            "ORDER BY e.fechaCheckIn DESC LIMIT 1")
    Optional<Estadia> findEstadiaFacturable(
            @Param("nroHabitacion") String nroHabitacion,
            @Param("fechaHoy") LocalDate fechaHoy
    );

    /**
     * Verifica si una habitación está ocupada físicamente en un rango de fechas.
     *
     * <p>Se utiliza para validar que no se permita un Check-In cuando existe
     * una estadía que solapa las fechas indicadas.</p>
     *
     * @param nroHabitacion número de la habitación a verificar
     * @param fechaInicio   inicio del rango a comprobar
     * @param fechaFin      fin del rango a comprobar
     * @return {@code true} si existe al menos una estadía que ocupe la habitación en ese rango
     */
    @Query("SELECT COUNT(e) > 0 FROM Estadia e WHERE e.habitacion.numero = :nroHabitacion " +
            "AND e.fechaCheckIn <= :fechaFin " +
            "AND (e.fechaCheckOut IS NULL OR e.fechaCheckOut > :fechaInicio)")
    boolean existeEstadiaEnFechas(
            @Param("nroHabitacion") String nroHabitacion,
            @Param("fechaInicio") Date fechaInicio,
            @Param("fechaFin") Date fechaFin
    );


    /**
     * Valida si una persona (huésped) ya está activa en alguna estadía dentro de un rango.
     *
     * <p>Útil para evitar que un acompañante sea agregado si ya está alojado en otra habitación
     * en las mismas fechas. Hace JOIN con la colección de huespedes asociados a la estadía.</p>
     *
     * @param tipoDoc     tipo de documento del huésped
     * @param nroDoc      número de documento del huésped
     * @param fechaInicio inicio del rango a comprobar
     * @param fechaFin    fin del rango a comprobar
     * @return {@code true} si el huésped está activo en alguna estadía que solape el rango
     */
    @Query("SELECT COUNT(e) > 0 FROM Estadia e JOIN e.estadiaHuespedes eh JOIN eh.huesped h " +
            "WHERE h.tipoDocumento = :tipoDoc AND h.nroDocumento = :nroDoc " +
            "AND e.fechaCheckIn < :fechaFin " +
            "AND (e.fechaCheckOut IS NULL OR e.fechaCheckOut > :fechaInicio)")
    boolean esHuespedActivo(
            @Param("tipoDoc") TipoDocumento tipoDoc,
            @Param("nroDoc") String nroDoc,
            @Param("fechaInicio") Date fechaInicio,
            @Param("fechaFin") Date fechaFin
    );

    // Busca una estadía activa (sin fecha de salida o fecha salida futura/hoy) para una habitación
    /**
     * Busca una estadía activa para una habitación (sin fecha de salida o con salida hoy/futura).
     *
     * @param nroHabitacion número de la habitación
     * @return {@link Optional}{@code <Estadia>} con la estadía activa si existe
     */
    @Query("SELECT e FROM Estadia e WHERE e.habitacion.numero = :nroHabitacion AND (e.fechaCheckOut IS NULL OR e.fechaCheckOut >= CURRENT_DATE)")
    Optional<Estadia> findEstadiaActivaPorHabitacion(@Param("nroHabitacion") String nroHabitacion);

    // Busca todas las estadías de una habitación para buscar facturas pendientes
    /**
     * Busca todas las estadías asociadas a una habitación (útil para consultar facturas pendientes, historial, etc.).
     *
     * @param numero número de la habitación
     * @return lista de {@link Estadia} relacionadas con la habitación
     */
    List<Estadia> findByHabitacion_Numero(String numero);


    // Para saber si el huésped ya pisó el hotel (CU11)
    /**
     * Indica si un huésped ha tenido alguna estadía (para el CU11: saber si el huésped ya pisó el hotel).
     *
     * @param huesped entidad {@link Huesped} a comprobar
     * @return {@code true} si existe al menos una estadía que contenga al huésped
     */
    @Query("SELECT COUNT(e) > 0 FROM Estadia e JOIN e.estadiaHuespedes eh WHERE eh.huesped = :huesped")
    boolean existsByHuespedesContaining(@Param("huesped") Huesped huesped);

    // Buscar estadía por el ID de la reserva asociada
    @Query("SELECT e FROM Estadia e WHERE e.reserva.idReserva = :idReserva")
    Optional<Estadia> findByReservaId(@Param("idReserva") Integer idReserva);
}
