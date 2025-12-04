package Reserva;

import BaseDedatos.Conexion;
import Dominio.Reserva;
import Excepciones.PersistenciaException;
import java.sql.*;
import java.util.ArrayList;

public class DaoReserva implements DaoInterfazReserva {
    private static DaoReserva instancia;
    private DaoReserva() {}
    public static synchronized DaoReserva getInstance() {
        if (instancia == null) instancia = new DaoReserva();
        return instancia;
    }

    @Override
    public boolean persistirReserva(Reserva reserva) throws PersistenciaException {
        String sql = "INSERT INTO reserva (fecha_reserva, fecha_desde, fecha_hasta, estado_reserva, NombreHuespedResponsable, ApellidoHuespedResponsable, TelefonoHuespedResponsable, id_habitacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, new java.sql.Date(reserva.getFechaReserva().getTime()));
            ps.setDate(2, new java.sql.Date(reserva.getFechaDesde().getTime()));
            ps.setDate(3, new java.sql.Date(reserva.getFechaHasta().getTime()));
            ps.setString(4, reserva.getEstadoReserva().name());
            ps.setString(5, reserva.getNombreHuespedResponsable());
            ps.setString(6, reserva.getApellidoHuespedResponsable());
            ps.setString(7, reserva.getTelefonoHuespedResponsable());
            ps.setString(8, reserva.getHabitacion().getNumero());

            int res = ps.executeUpdate();
            if (res > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) reserva.setIdReserva(rs.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new PersistenciaException("Error persistir reserva", e);
        }
    }

    public boolean hayReservaEnFecha(String numeroHabitacion, java.util.Date fechaInicial, java.util.Date fechaFinal) {
        // CORRECCIÓN: Detecta cualquier tipo de solapamiento
        String sql = "SELECT 1 FROM reserva WHERE id_habitacion = ? " +
                "AND fecha_desde < ? AND fecha_hasta > ? " +
                "AND estado_reserva = 'ACTIVA' LIMIT 1";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            java.sql.Date fechaInicioSql = new java.sql.Date(fechaInicial.getTime());
            java.sql.Date fechaFinSql = new java.sql.Date(fechaFinal.getTime());

            ps.setString(1, numeroHabitacion);
            // OJO: Cruzamos los parámetros para cumplir la lógica
            ps.setDate(2, fechaFinSql);    // fecha_desde < NUEVO_FIN
            ps.setDate(3, fechaInicioSql); // fecha_hasta > NUEVO_INICIO

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    // Implementar CRUD restante...
    @Override
    public boolean modificarReserva(Reserva reserva) throws PersistenciaException { return false; }
    @Override
    public boolean eliminarReserva(int id) { return false; }
    @Override
    public DtoReserva obtenerPorId(int id) { return null; }
    @Override
    public ArrayList<DtoReserva> obtenerTodas() { return new ArrayList<>(); }
}