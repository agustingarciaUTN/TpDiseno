package Estadia;

import BaseDedatos.Coneccion;
import java.sql.*;
import java.util.ArrayList;

public class DaoEstadia implements DaoInterfazEstadia {

    @Override
    public boolean crearEstadia(DtoEstadia dto) {
        String sql = "INSERT INTO estadia (fecha_inicio, fecha_fin, valor_estadia) VALUES (?, ?, ?)";

        try (Connection conn = Coneccion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, new java.sql.Date(dto.getFechaInicio().getTime()));

            if (dto.getFechaFin() != null) {
                ps.setDate(2, new java.sql.Date(dto.getFechaFin().getTime()));
            } else {
                ps.setNull(2, Types.DATE);
            }

            ps.setDouble(3, dto.getValorEstadia());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al crear estadía: " + e.getMessage());
            return false;
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
