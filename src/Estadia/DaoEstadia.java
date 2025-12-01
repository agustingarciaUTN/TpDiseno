package Estadia;

import BaseDedatos.Conexion;
import Dominio.Estadia;
import Dominio.Huesped;
import Excepciones.PersistenciaException;
import java.sql.*;
import java.util.ArrayList;

public class DaoEstadia implements DaoInterfazEstadia {
    private static DaoEstadia instancia;
    private DaoEstadia() {}

    public static synchronized DaoEstadia getInstance() {
        if (instancia == null) instancia = new DaoEstadia();
        return instancia;
    }

    @Override
    public boolean persistirEstadia(Estadia estadia) throws PersistenciaException {
        String sql = "INSERT INTO estadia (fecha_inicio, fecha_fin, valor_estadia, id_reserva, id_habitacion) VALUES (?, ?, ?, ?, ?)";
        String sqlRel = "INSERT INTO estadia_huesped (id_estadia, tipo_documento, numero_documento) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = Conexion.getConnection();
            conn.setAutoCommit(false);

            // Insertar Estadía
            int idGenerado;
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setDate(1, new java.sql.Date(estadia.getFechaCheckIn().getTime()));
                if (estadia.getFechaCheckOut() != null) ps.setDate(2, new java.sql.Date(estadia.getFechaCheckOut().getTime()));
                else ps.setNull(2, Types.DATE);
                ps.setDouble(3, estadia.getValorEstadia());

                if (estadia.getReserva() != null) ps.setInt(4, estadia.getReserva().getIdReserva());
                else ps.setNull(4, Types.INTEGER);

                if (estadia.getHabitacion() != null) ps.setString(5, estadia.getHabitacion().getNumero());
                else ps.setNull(5, Types.VARCHAR);

                ps.executeUpdate();
                try(ResultSet rs = ps.getGeneratedKeys()){
                    if(rs.next()) idGenerado = rs.getInt(1);
                                        else throw new SQLException("No se encontró un ID");
                }
                estadia.setIdEstadia(idGenerado);
            }

            // Relación Huespedes
            if (estadia.getHuespedes() != null) {
                try (PreparedStatement psRel = conn.prepareStatement(sqlRel)) {
                    for (Huesped h : estadia.getHuespedes()) {
                        psRel.setInt(1, idGenerado);
                        psRel.setString(2, h.getTipoDocumento().name());
                        psRel.setString(3, h.getNroDocumento());
                        psRel.addBatch();
                    }
                    psRel.executeBatch();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            throw new PersistenciaException("Error al persistir estadia", e);
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }

    // Implementar métodos obtener usando Builder
    @Override
    public boolean modificarEstadia(Estadia estadia) throws PersistenciaException { return false; } // TODO
    @Override
    public boolean eliminarEstadia(int idEstadia) { return false; } // TODO
    @Override
    public DtoEstadia obtenerEstadiaPorId(int idEstadia) { return null; } // TODO
    @Override
    public ArrayList<DtoEstadia> obtenerTodasLasEstadias() { return new ArrayList<>(); } // TODO
}