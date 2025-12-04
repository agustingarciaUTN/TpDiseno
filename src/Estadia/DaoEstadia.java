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
        String sql = "INSERT INTO estadia (fecha_check-in, fecha_check-out, valor_estadia, id_reserva, numero_habitacion) VALUES (?, ?, ?, ?, ?)";
        String sqlRel = "INSERT INTO estadia_huesped (id_estadia, tipo_documento, nro_documento) VALUES (?, ?, ?)";

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
            if (conn != null) try { conn.rollback(); } catch (SQLException _) {}
            throw new PersistenciaException("Error al persistir estadía", e);
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException _) {}
        }
    }

    // Agrega este método en tu DAO y en la interfaz DaoInterfazEstadia
    @Override
    public boolean hayEstadiaEnFecha(String numeroHabitacion, java.util.Date fechaInicial, java.util.Date fechaFin) {
        // Buscamos si hay una estadía para esa habitación donde:
        // 1. La fecha consultada es mayor o igual al inicio (ya llegó)
        // 2. Y (La fecha fin es nula O la fecha consultada es menor a la fecha fin)
        //    (Es decir, o no se fue todavía, o se va después de hoy)

        String sql = "SELECT 1 FROM estadia WHERE numero_habitacion = ? " +
                "AND ? >= \"fecha_check-in\" " +
                "AND (\"fecha_check-out\" IS NULL OR ? <= \"fecha_check-out\") LIMIT 1";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Convertimos la fecha de Java Util a SQL
            java.sql.Date fechaFinal = new java.sql.Date(fechaFin.getTime());
            java.sql.Date fechaInicio = new java.sql.Date(fechaInicial.getTime());

            ps.setString(1, numeroHabitacion); // Ojo: Asegúrate que la columna en tu BD se llama id_habitación o numero_habitación
            ps.setDate(2, fechaInicio);
            ps.setDate(3, fechaFinal);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Si devuelve una fila, la habitación está ocupada físicamente
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar disponibilidad de estadía: " + e.getMessage());
            return false; // Ante la duda, asumimos libre o manejas la excepción
        }
    }

    @Override
    public boolean esHuespedActivo(String tipoDoc, String nroDoc, java.util.Date fechaInicio, java.util.Date fechaFin) {
        // Query: Busca si existe alguna estadía solapada temporalmente donde figure esta persona
        // Lógica de solapamiento: (InicioA < FinB) Y (FinA > InicioB)
        // Nota: Si fecha_fin es NULL, asumimos que sigue alojado hoy (o hasta fecha lejana)

        String sql = "SELECT 1 FROM estadia e " +
                "JOIN estadia_huesped eh ON e.id_estadia = eh.id_estadia " +
                "WHERE eh.tipo_documento = ? AND eh.nro_documento = ? " +
                "AND e.fecha_inicio < ? AND (e.fecha_fin IS NULL OR e.fecha_fin > ?) " +
                "LIMIT 1";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            java.sql.Date sqlInicioNuevo = new java.sql.Date(fechaInicio.getTime());
            java.sql.Date sqlFinNuevo = new java.sql.Date(fechaFin.getTime());

            ps.setString(1, tipoDoc);
            ps.setString(2, nroDoc);
            // Parámetros cruzados para la lógica de intervalo
            ps.setDate(3, sqlFinNuevo);    // e.inicio < NUEVO_FIN
            ps.setDate(4, sqlInicioNuevo); // e.fin > NUEVO_INICIO

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // True = Ya está en el hotel
            }
        } catch (SQLException e) {
            System.err.println("Error al validar huesped activo: " + e.getMessage());
            return false; // Ante error, permitimos pasar (o podrías lanzar excepción)
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