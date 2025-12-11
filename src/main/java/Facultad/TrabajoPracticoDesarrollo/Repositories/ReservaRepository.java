package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Huesped;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    // 1. REEMPLAZO DE: persistirReserva()
    // No hace falta escribir nada, JpaRepository ya trae el método .save(reserva)
    // que maneja INSERT, claves generadas y transacciones automáticamente.

    /**
     * 2. REEMPLAZO DE: obtenerReservasEnPeriodo(inicio, fin)
     * * Tu SQL original era:
     * SELECT * FROM reserva WHERE estado_reserva = 'ACTIVA' AND fecha_desde < ? AND fecha_hasta > ?
     * * Aquí lo traducimos a JPQL (Java Persistence Query Language):
     */
    @Query("SELECT r FROM Reserva r WHERE r.estadoReserva = Facultad.TrabajoPracticoDesarrollo.enums.EstadoReserva.ACTIVA " +
            "AND r.fechaDesde < :fin AND r.fechaHasta > :inicio")
    List<Reserva> buscarReservasActivasEnRango(@Param("inicio") Date inicio, @Param("fin") Date fin);

    /**
     * 3. REEMPLAZO DE: hayReservaEnFecha(nroHabitacion, inicio, fin)
     * * Tu SQL original era:
     * SELECT 1 FROM reserva WHERE id_habitacion = ? AND ... LIMIT 1
     * * En JPQL verificamos si el conteo es mayor a 0 para devolver un booleano (true/false).
     */
    @Query("SELECT COUNT(r) > 0 FROM Reserva r WHERE r.habitacion.numero = :nroHabitacion " +
            "AND r.estadoReserva = Facultad.TrabajoPracticoDesarrollo.enums.EstadoReserva.ACTIVA " +
            "AND r.fechaDesde < :fin AND r.fechaHasta > :inicio")
    boolean existeReservaEnFecha(
            @Param("nroHabitacion") String nroHabitacion,
            @Param("inicio") Date inicio,
            @Param("fin") Date fin
    );

    //Metodo utilizado para el CU10, cuando tenemos que reasignar las reservas de un huesped a otro
    @Modifying // Indica que vamos a cambiar datos, no solo leer
    @Query("UPDATE Reserva r SET r.huesped = :huespedDestino WHERE r.huesped = :huespedOriginal")
    void migrarReservas(
            @Param("huespedOriginal") Huesped huespedOriginal,
            @Param("huespedDestino") Huesped huespedDestino
    );
}