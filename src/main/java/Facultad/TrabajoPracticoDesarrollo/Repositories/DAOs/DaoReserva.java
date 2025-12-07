package Facultad.TrabajoPracticoDesarrollo.Repositories.DAOs;

import Facultad.TrabajoPracticoDesarrollo.BaseDeDatos.Conexion;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Reserva;
import Facultad.TrabajoPracticoDesarrollo.Excepciones.PersistenciaException;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoReserva;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;

@Repository
public class DaoReserva implements DaoInterfazReserva {
    public DaoReserva() {}


    @Override
    public boolean persistirReserva(Reserva reserva) throws PersistenciaException {
        // SQL corregido con comillas para mayúsculas y casteo de Enum
        String sql = "INSERT INTO reserva (fecha_reserva, fecha_desde, fecha_hasta, estado_reserva, \"NombreHuespedResponsable\", \"ApellidoHuespedResponsable\", \"TelefonoHuespedResponsable\", id_habitacion) " +
                "VALUES (?, ?, ?, ?::\"Estado_Reserva\", ?, ?, ?, ?)";

        Connection conn = null;

        try {
            conn = Conexion.getConnection();
            conn.setAutoCommit(false); // 1. INICIO TRANSACCIÓN MANUAL

            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setDate(1, new Date(reserva.getFechaReserva().getTime()));
                ps.setDate(2, new Date(reserva.getFechaDesde().getTime()));
                ps.setDate(3, new Date(reserva.getFechaHasta().getTime()));

                // Enum a String
                ps.setString(4, reserva.getEstadoReserva().name());

                ps.setString(5, reserva.getNombreHuespedResponsable());
                ps.setString(6, reserva.getApellidoHuespedResponsable());
                ps.setString(7, reserva.getTelefonoHuespedResponsable());
                ps.setString(8, reserva.getHabitacion().getNumero());

                int res = ps.executeUpdate();

                if (res > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            reserva.setIdReserva(rs.getInt(1));
                        }
                    }
                    conn.commit(); // 2. CONFIRMAR CAMBIOS (GUARDAR REALMENTE)
                    return true;
                } else {
                    conn.rollback(); // Si no se insertó nada, deshacer
                    return false;
                }
            }

        } catch (SQLException e) {
            // Si hay error, deshacemos cualquier cambio pendiente
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw new PersistenciaException("Error SQL al insertar Reserva: " + e.getMessage(), e);
        } finally {
            // Cerramos la conexión ordenadamente
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Restaurar default
                    conn.close();
                } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    @Override
    public boolean hayReservaEnFecha(String numeroHabitacion, java.util.Date fechaInicial, java.util.Date fechaFinal) {
        String sql = "SELECT 1 FROM reserva WHERE id_habitacion = ? " +
                "AND fecha_desde < ? AND fecha_hasta > ? " +
                "AND estado_reserva = 'ACTIVA'::\"Estado_Reserva\" LIMIT 1";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            Date fechaInicioSql = new Date(fechaInicial.getTime());
            Date fechaFinSql = new Date(fechaFinal.getTime());

            ps.setString(1, numeroHabitacion);
            ps.setDate(2, fechaFinSql);
            ps.setDate(3, fechaInicioSql);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar reserva: " + e.getMessage());
            return false;
        }
    }

    @Override
    public ArrayList<DtoReserva> obtenerReservasEnPeriodo(java.util.Date inicio, java.util.Date fin) {
        ArrayList<DtoReserva> lista = new ArrayList<>();
        String sql = "SELECT * FROM reserva WHERE estado_reserva = 'ACTIVA'::\"Estado_Reserva\" " +
                "AND fecha_desde < ? AND fecha_hasta > ?";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, new Date(fin.getTime()));
            ps.setDate(2, new Date(inicio.getTime()));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DtoReserva dto = new DtoReserva.Builder()
                            .id(rs.getInt("id_reserva"))
                            .idHabitacion(rs.getString("id_habitacion"))
                            .fechaDesde(rs.getDate("fecha_desde"))
                            .fechaHasta(rs.getDate("fecha_hasta"))
                            // Nombres de columnas con mayúsculas escapadas no son necesarias en el ResultSet.getString
                            // a menos que Postgres sea muy estricto, pero generalmente getString es case-insensitive.
                            // Si falla, prueba poniendo rs.getString("\"NombreHuespedResponsable\"")
                            .nombreResponsable(rs.getString("NombreHuespedResponsable"))
                            .apellidoResponsable(rs.getString("ApellidoHuespedResponsable"))
                            .telefonoResponsable(rs.getString("TelefonoHuespedResponsable"))
                            .build();
                    lista.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Métodos restantes
    @Override public boolean modificarReserva(Reserva reserva) throws PersistenciaException { return false; }
    @Override public boolean eliminarReserva(int id) { return false; }
    @Override public DtoReserva obtenerPorId(int id) { return null; }
    @Override public ArrayList<DtoReserva> obtenerTodas() { return new ArrayList<>(); }
}