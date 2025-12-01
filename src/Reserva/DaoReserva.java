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
        String sql = "INSERT INTO reserva (fecha_reserva, fecha_desde, fecha_hasta, estado_reserva, nombre_huesped, apellido_huesped, telefono_huesped, id_habitacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, new java.sql.Date(reserva.getFechaReserva().getTime()));
            ps.setDate(2, new java.sql.Date(reserva.getFechaDesde().getTime()));
            ps.setDate(3, new java.sql.Date(reserva.getFechaHasta().getTime()));
            ps.setString(4, reserva.getEstadoReserva().name());
            ps.setString(5, reserva.getNombreHuespedResponsable());
            ps.setString(6, reserva.getApellidoHuespedResponsable());
            ps.setString(7, reserva.getTelefonoHuespedResponsable());
            ps.setString(8, reserva.getIdHabitacion());

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

    // Implementar CRUD restante...
    @Override
    public boolean modificarReserva(Reserva reserva) throws PersistenciaException { return false; }
    @Override
    public boolean eliminarReserva(int id) { return false; }
    @Override
    public Reserva obtenerPorId(int id) { return null; }
    @Override
    public ArrayList<Reserva> obtenerTodas() { return new ArrayList<>(); }
}