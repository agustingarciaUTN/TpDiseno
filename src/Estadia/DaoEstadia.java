package Estadia;

import BaseDedatos.Coneccion;
import Dominio.Huesped;
import Excepciones.PersistenciaException;

import Dominio.Estadia;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DaoEstadia implements DaoInterfazEstadia {

    @Override
    public boolean crearEstadia(Estadia estadia, List<Huesped> huespedes) {

        if (huespedes == null || huespedes.isEmpty()) {
            throw new IllegalArgumentException("La estadia debe tener al menos un huesped.");
        }
        if (estadia.getFechaCheckIn() == null) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser nula.");
        }

        String sqlInsertEstadia = "INSERT INTO estadia (fecha_inicio, fecha_fin, valor_estadia, id_reserva) VALUES (?, ?, ?, ?)";
        String sqlInsertEstadiaHuesped = "INSERT INTO estadia_huesped (id_estadia, tipo_documento, nro_documento) VALUES (?,?,?)";

        Connection conexion = null;
        try {
            conexion = Coneccion.getConnection();
            conexion.setAutoCommit(false);

            int idEstadiaGenerado = -1;
            try (PreparedStatement sentencia = conexion.prepareStatement(sqlInsertEstadia, Statement.RETURN_GENERATED_KEYS)) {
                if (estadia.getFechaCheckIn() != null) {
                    sentencia.setTimestamp(1, new Timestamp(estadia.getFechaCheckIn().getTime()));
                } else {
                    sentencia.setNull(1, Types.TIMESTAMP);
                }

                if (estadia.getFechaCheckOut() != null) {
                    sentencia.setTimestamp(2, new Timestamp(estadia.getFechaCheckOut().getTime()));
                } else {
                    sentencia.setNull(2, Types.TIMESTAMP);
                }

                sentencia.setDouble(3, estadia.getValorEstadia());
                // Si tu entidad Estadia tiene idReserva, se inserta aquí
                sentencia.setInt(4, estadia.getIdReserva());

                int filasAfectadas = sentencia.executeUpdate();
                if (filasAfectadas == 0) {
                    conexion.rollback();
                    return false;
                }

                try (ResultSet rs = sentencia.getGeneratedKeys()) {
                    if (rs.next()) {
                        idEstadiaGenerado = rs.getInt(1);
                    } else {
                        conexion.rollback();
                        return false;
                    }
                }
            }

            if (idEstadiaGenerado <= 0) {
                conexion.rollback();
                return false;
            }

            try (PreparedStatement sentenciaJoin = conexion.prepareStatement(sqlInsertEstadiaHuesped)) {
                for (Huesped huesped : huespedes) {
                    sentenciaJoin.setInt(1, idEstadiaGenerado);

                    if (huesped.getTipoDocumento() != null) {
                        sentenciaJoin.setString(2, huesped.getTipoDocumento().name());
                    } else {
                        sentenciaJoin.setNull(2, Types.VARCHAR);
                    }

                    if (huesped.getNroDocumento() != 0) {
                        sentenciaJoin.setString(3, String.valueOf(huesped.getNroDocumento()));
                    } else { // revisar porque estariamos seteando en null
                        sentenciaJoin.setNull(3, Types.VARCHAR);
                    }

                    sentenciaJoin.addBatch();
                }
                int[] resultados = sentenciaJoin.executeBatch();
                boolean algunaInsertada = false;
                for (int r : resultados) {
                    if (r == Statement.SUCCESS_NO_INFO || r > 0) {
                        algunaInsertada = true;
                        break;
                    }
                }
                if (!algunaInsertada) {
                    conexion.rollback();
                    return false;
                }
            }

            conexion.commit();
            return true;
        } catch (SQLException e) {
            try { if (conexion != null) conexion.rollback(); } catch (SQLException ex) { /* log si aplica */ }
            System.err.println("Error al persistir estadía: " + e.getMessage());
            return false;
        } finally {
            try { if (conexion != null) { conexion.setAutoCommit(true); conexion.close(); } } catch (SQLException ignored) {}
        }

    }

    @Override
    public boolean modificarEstadia(DtoEstadia dto) {
        String sql = "UPDATE estadia SET fecha_inicio = ?, fecha_fin = ?, valor_estadia = ? WHERE id_estadia = ?";

        try (Connection conn = Coneccion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, new java.sql.Date(dto.getFechaInicio().getTime()));

            if (dto.getFechaFin() != null) {
                ps.setDate(2, new java.sql.Date(dto.getFechaFin().getTime()));
            } else {
                ps.setNull(2, Types.DATE);
            }

            ps.setDouble(3, dto.getValorEstadia());
            ps.setInt(4, dto.getIdEstadia());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al modificar estadía: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminarEstadia(int idEstadia) {
        String sql = "DELETE FROM estadia WHERE id_estadia = ?";

        try (Connection conn = Coneccion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEstadia);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar estadía: " + e.getMessage());
            return false;
        }
    }

    @Override
    public DtoEstadia obtenerEstadiaPorId(int idEstadia) {
        String sql = "SELECT id_estadia, fecha_inicio, fecha_fin, valor_estadia FROM estadia WHERE id_estadia = ?";

        try (Connection conn = Coneccion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEstadia);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DtoEstadia dto = new DtoEstadia();
                    dto.setIdEstadia(rs.getInt("id_estadia"));
                    dto.setFechaInicio(rs.getDate("fecha_inicio"));
                    dto.setFechaFin(rs.getDate("fecha_fin"));
                    dto.setValorEstadia(rs.getDouble("valor_estadia"));
                    return dto;
                }
            }

            return null;

        } catch (SQLException e) {
            System.err.println("Error al obtener estadía por ID: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<DtoEstadia> obtenerTodasLasEstadias() {
        ArrayList<DtoEstadia> estadias = new ArrayList<>();
        String sql = "SELECT id_estadia, fecha_inicio, fecha_fin, valor_estadia FROM estadia";

        try (Connection conn = Coneccion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                DtoEstadia dto = new DtoEstadia();
                dto.setIdEstadia(rs.getInt("id_estadia"));
                dto.setFechaInicio(rs.getDate("fecha_inicio"));
                dto.setFechaFin(rs.getDate("fecha_fin"));
                dto.setValorEstadia(rs.getDouble("valor_estadia"));
                estadias.add(dto);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener todas las estadías: " + e.getMessage());
        }

        return estadias;
    }

    @Override
    public boolean huespedTieneEstadias(String tipoDocumento, String nroDocumento) {
        String sql = "SELECT COUNT(*) as total FROM estadia_huesped WHERE tipo_documento = ? AND nro_documento = ?";

        try (Connection conn = Coneccion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tipoDocumento);
            ps.setString(2, nroDocumento);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total");
                    return total > 0;
                }
            }

            return false;

        } catch (SQLException e) {
            System.err.println("Error al verificar estadías del huésped: " + e.getMessage());
            return false;
        }
    }
}
