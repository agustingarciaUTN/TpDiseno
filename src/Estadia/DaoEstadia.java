package Estadia;

import BaseDedatos.Conexion;
import Dominio.Estadia;
import Dominio.Huesped;
import Excepciones.PersistenciaException;
import Habitacion.DtoHabitacion;

import java.sql.*;
import java.util.ArrayList;

public class DaoEstadia implements DaoInterfazEstadia {
    private static DaoEstadia instancia;

    private DaoEstadia() {
    }

    public static synchronized DaoEstadia getInstance() {
        if (instancia == null) instancia = new DaoEstadia();
        return instancia;
    }

    @Override
    public boolean persistirEstadia(Estadia estadia) throws PersistenciaException {
        // CORRECCIÓN: Agregamos comillas dobles (\") a las columnas con guiones
        String sql = "INSERT INTO estadia (\"fecha_check-in\", \"fecha_check-out\", valor_estadia, id_reserva, numero_habitacion) VALUES (?, ?, ?, ?, ?)";

        // La tabla intermedia suele tener nombres estándar, pero revisa si requiere comillas también en tu BD
        String sqlRel = "INSERT INTO estadia_huesped (id_estadia, tipo_documento, nro_documento) VALUES (?, ?::\"Tipo_Documento\", ?)";

        Connection conn = null;
        try {
            conn = Conexion.getConnection();
            conn.setAutoCommit(false); // Transacción manual

            // 1. Insertar Estadía
            int idGenerado;
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setDate(1, new java.sql.Date(estadia.getFechaCheckIn().getTime()));

                if (estadia.getFechaCheckOut() != null)
                    ps.setDate(2, new java.sql.Date(estadia.getFechaCheckOut().getTime()));
                else
                    ps.setNull(2, Types.DATE);

                ps.setDouble(3, estadia.getValorEstadia());

                if (estadia.getReserva() != null)
                    ps.setInt(4, estadia.getReserva().getIdReserva());
                else
                    ps.setNull(4, Types.INTEGER);

                if (estadia.getHabitacion() != null)
                    ps.setString(5, estadia.getHabitacion().getNumero());
                else
                    ps.setNull(5, Types.VARCHAR);

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) idGenerado = rs.getInt(1);
                    else throw new SQLException("No se encontró un ID generado para la estadía");
                }
                estadia.setIdEstadia(idGenerado);
            }

            // 2. Relación Huespedes (estadia_huesped)
            // Se asume que los huéspedes ya existen en la BD (fueron validados/creados antes)
            if (estadia.getHuespedes() != null && !estadia.getHuespedes().isEmpty()) {
                try (PreparedStatement psRel = conn.prepareStatement(sqlRel)) {
                    for (Huesped h : estadia.getHuespedes()) {
                        psRel.setInt(1, idGenerado);
                        // Aseguramos que el Enum se guarde como String (o Casteo si es ENUM en BD)
                        // Si tu BD usa ENUM para tipo_documento, quizás necesites ?::"Tipo_Documento"
                        psRel.setString(2, h.getTipoDocumento().name());
                        psRel.setString(3, h.getNroDocumento());
                        psRel.addBatch();
                    }
                    psRel.executeBatch();
                }
            }

            conn.commit(); // CONFIRMAR CAMBIOS
            return true;

        } catch (SQLException e) {
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException _) {
            }
            // Pasamos la excepción 'e' para poder ver la causa real con getCause()
            throw new PersistenciaException("Error al persistir estadía: " + e.getMessage(), e);
        } finally {
            if (conn != null) try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException _) {
            }
        }
    }

    // --- MÉTODOS DE BÚSQUEDA (Mantienen las comillas que ya funcionaban) ---

    @Override
    public boolean hayEstadiaEnFecha(String numeroHabitacion, java.util.Date fechaInicial, java.util.Date fechaFin) {
        String sql = "SELECT 1 FROM estadia WHERE numero_habitacion = ? " +
                "AND ? >= \"fecha_check-in\" " +
                "AND (\"fecha_check-out\" IS NULL OR ? <= \"fecha_check-out\") LIMIT 1";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            java.sql.Date fechaFinal = new java.sql.Date(fechaFin.getTime());
            java.sql.Date fechaInicio = new java.sql.Date(fechaInicial.getTime());

            ps.setString(1, numeroHabitacion);
            ps.setDate(2, fechaInicio);
            ps.setDate(3, fechaFinal);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar disponibilidad de estadía: " + e.getMessage());
            return false;
        }
    }

    @Override
    public ArrayList<DtoEstadia> obtenerEstadiasEnPeriodo(java.util.Date inicio, java.util.Date fin) {
        ArrayList<DtoEstadia> lista = new ArrayList<>();
        String sql = "SELECT * FROM estadia WHERE " +
                "\"fecha_check-in\" < ? AND (\"fecha_check-out\" IS NULL OR \"fecha_check-out\" > ?)";

        try (Connection conn = BaseDedatos.Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, new java.sql.Date(fin.getTime()));
            ps.setDate(2, new java.sql.Date(inicio.getTime()));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DtoHabitacion hab = new DtoHabitacion.Builder(rs.getString("numero_habitacion"), null, 1).build();

                    DtoEstadia dto = new DtoEstadia.Builder()
                            .idEstadia(rs.getInt("id_estadia"))
                            .dtoHabitacion(hab)
                            .fechaCheckIn(rs.getDate("fecha_check-in"))
                            .fechaCheckOut(rs.getDate("fecha_check-out"))
                            .build();
                    lista.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean esHuespedActivo(String tipoDoc, String nroDoc, java.util.Date fechaInicio, java.util.Date fechaFin) {
        // Validación de acompañantes
        // Asumiendo que 'fecha_inicio' y 'fecha_fin' en la consulta original eran erratas y
        // se referían a las columnas check-in/out
        String sql = "SELECT 1 FROM estadia e " +
                "JOIN estadia_huesped eh ON e.id_estadia = eh.id_estadia " +
                "WHERE eh.tipo_documento = ? AND eh.nro_documento = ? " +
                "AND e.\"fecha_check-in\" < ? AND (e.\"fecha_check-out\" IS NULL OR e.\"fecha_check-out\" > ?) " +
                "LIMIT 1";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            java.sql.Date sqlInicioNuevo = new java.sql.Date(fechaInicio.getTime());
            java.sql.Date sqlFinNuevo = new java.sql.Date(fechaFin.getTime());

            ps.setString(1, tipoDoc);
            ps.setString(2, nroDoc);
            ps.setDate(3, sqlFinNuevo);
            ps.setDate(4, sqlInicioNuevo);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error al validar huesped activo: " + e.getMessage());
            return false;
        }
    }

    // Placeholders
    @Override
    public boolean modificarEstadia(Estadia estadia) throws PersistenciaException {
        return false;
    }
    @Override
    public ArrayList<DtoEstadia> obtenerTodasLasEstadias(){return null;}
    @Override
    public boolean eliminarEstadia(int idEstadia) {
        return false;
    }
    @Override
    public DtoEstadia obtenerEstadiaPorId(int idEstadia){return null;}
}