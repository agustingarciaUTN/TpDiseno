package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Huesped;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Reserva;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
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

    // PARA EL CU11 (BORRAR)
    // Borramos buscando por los campos de texto
    @Modifying
    @Query("DELETE FROM Reserva r WHERE r.tipoDocumentoResponsable = :tipo AND r.nroDocumentoResponsable = :nro")
    void deleteByHuesped(@Param("tipo") TipoDocumento tipo, @Param("nro") String nro);

    // PARA EL CU10 (FUSIÓN/MIGRACIÓN)
    // Actualizamos los datos de texto de la reserva para que apunten al nuevo DNI/Nombre
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
}