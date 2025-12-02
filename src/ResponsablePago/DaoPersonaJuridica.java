package ResponsablePago;

import BaseDedatos.Conexion;
import Dominio.PersonaJuridica;
import Excepciones.PersistenciaException;
import java.sql.*;

public class DaoPersonaJuridica implements DaoInterfazPersonaJuridica {
    private static DaoPersonaJuridica instancia;
    private DaoPersonaJuridica() {}

    public static synchronized DaoPersonaJuridica getInstance() {
        if (instancia == null) instancia = new DaoPersonaJuridica();
        return instancia;
    }

    @Override
    public boolean persistirPersonaJuridica(PersonaJuridica pj) throws PersistenciaException {
        String sqlPadre = "INSERT INTO responsable_pago DEFAULT VALUES";
        String sqlHija = "INSERT INTO persona_juridica (id_persona_juridica, razon_social, cuit, id_direccion) VALUES (?, ?, ?, ?)";
        String sqlTel = "INSERT INTO telefono_persona_juridica (id_persona_juridica, telefono) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = Conexion.getConnection();
            conn.setAutoCommit(false);

            // 1. Padre
            int idGenerado;
            try (PreparedStatement ps = conn.prepareStatement(sqlPadre, Statement.RETURN_GENERATED_KEYS)) {
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) idGenerado = rs.getInt(1);
                    else throw new SQLException("Fallo ID responsable");
                }
                pj.setIdResponsablePago(idGenerado);
            }

            // 2. Hija (Jurídica)
            try (PreparedStatement ps = conn.prepareStatement(sqlHija)) {
                ps.setInt(1, idGenerado);
                ps.setString(2, pj.getRazonSocial());
                ps.setString(3, pj.getCuit());
                ps.setInt(4, pj.getDireccion().getId()); // Asumimos que el Gestor ya persistió la dirección
                ps.executeUpdate();
            }

            // 3. Teléfono
            if (pj.getTelefono() != 0) {
                try (PreparedStatement ps = conn.prepareStatement(sqlTel)) {
                    ps.setInt(1, idGenerado);
                    ps.setString(2, String.valueOf(pj.getTelefono()));
                    ps.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            throw new PersistenciaException("Error persistir PJ", e);
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }

    // Implementar obtener, modificar y eliminar similar a PersonaFisica
    @Override
    public DtoPersonaJuridica obtenerPorId(int id) { return null; } // TODO: Implementar con Builder
    @Override
    public boolean modificarPersonaJuridica(PersonaJuridica pj) throws PersistenciaException { return false; } // TODO
    @Override
    public boolean eliminarPersonaJuridica(int id) { return false; } // TODO
}