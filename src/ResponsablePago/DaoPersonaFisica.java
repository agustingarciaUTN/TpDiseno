package ResponsablePago;

import BaseDedatos.Coneccion;
import Dominio.PersonaFisica;
import Excepciones.PersistenciaException;
import java.sql.*;

public class DaoPersonaFisica implements DaoInterfazPersonaFisica {
    private static DaoPersonaFisica instancia;
    private DaoPersonaFisica() {}
    public static synchronized DaoPersonaFisica getInstance() {
        if (instancia == null) instancia = new DaoPersonaFisica();
        return instancia;
    }

    @Override
    public boolean persistirPersonaFisica(PersonaFisica persona) throws PersistenciaException {
        String sqlPadre = "INSERT INTO responsable_pago DEFAULT VALUES";
        String sqlHija = "INSERT INTO persona_fisica (id_persona_fisica, tipo_documento, nro_documento) VALUES (?, ?, ?)";
        // SQL telefono si tienes tabla satelite para esto:
        // String sqlTel = "INSERT INTO telefono_persona_fisica ...";

        Connection conn = null;
        try {
            conn = Coneccion.getConnection();
            conn.setAutoCommit(false);

            int idGenerado;
            try (PreparedStatement ps = conn.prepareStatement(sqlPadre, Statement.RETURN_GENERATED_KEYS)) {
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) idGenerado = rs.getInt(1);
                    else throw new SQLException("Sin ID");
                }
                persona.setIdResponsablePago(idGenerado);
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlHija)) {
                ps.setInt(1, idGenerado);
                ps.setString(2, persona.getTipoDocumento().name());
                ps.setString(3, persona.getNumeroDocumento());
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            throw new PersistenciaException("Error persistir Persona Fisica", e);
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }

    @Override
    public PersonaFisica obtenerPorId(int id) { return null; } // TODO
}