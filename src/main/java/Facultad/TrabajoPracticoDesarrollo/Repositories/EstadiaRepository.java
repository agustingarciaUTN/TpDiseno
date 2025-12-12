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

@Repository
public interface EstadiaRepository extends JpaRepository<Estadia, Integer> {

    /**
     * Busca estadías que se solapen con el rango dado.
     * Considera que fechaCheckOut puede ser NULL (estadía en curso).
     * Lógica: (CheckIn < FinSolicitado) Y (CheckOut > InicioSolicitado O CheckOut es NULL)
     */
    @Query("SELECT e FROM Estadia e WHERE " +
            "e.fechaCheckIn < :fin AND " +
            "(e.fechaCheckOut IS NULL OR e.fechaCheckOut > :inicio)")
    List<Estadia> buscarEstadiasEnRango(@Param("inicio") Date inicio, @Param("fin") Date fin);

    /**
     * Verifica si una habitación está ocupada físicamente en una fecha específica (o rango).
     * Se usa para validar Check-In.
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
     * Valida si una persona (Huésped) ya está activa en alguna estadía en ese rango.
     * Útil para evitar que un acompañante sea agregado si ya está en otra habitación.
     * Hace un JOIN implícito con la lista de huéspedes.
     */
    @Query("SELECT COUNT(e) > 0 FROM Estadia e JOIN e.huespedes h " +
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
    @Query("SELECT e FROM Estadia e WHERE e.habitacion.numero = :nroHabitacion AND e.fechaCheckOut IS NULL")
    Optional<Estadia> findEstadiaActivaPorHabitacion(@Param("nroHabitacion") String nroHabitacion);

    //Metodo utilizado para el CU10, cuando tenemos que reasignar una estadia de un huesped a otro
    // TODO: Revisar - Estadia tiene List<Huesped>, no un solo huesped
    /*@Modifying
    @Query("UPDATE Estadia e SET e.huesped = :huespedDestino WHERE e.huesped = :huespedOriginal")
    void migrarEstadias(
            @Param("huespedOriginal") Huesped huespedOriginal,
            @Param("huespedDestino") Huesped huespedDestino
    );*/

    // Para saber si el huésped ya pisó el hotel (CU11)
    boolean existsByHuespedesContaining(Huesped huesped);
}