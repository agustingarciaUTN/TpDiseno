package Facultad.TrabajoPracticoDesarrollo.ResponsablePago;

import Facultad.TrabajoPracticoDesarrollo.BaseDeDatos.Conexion;
import Facultad.TrabajoPracticoDesarrollo.Dominio.PersonaFisica;
import Facultad.TrabajoPracticoDesarrollo.Excepciones.PersistenciaException;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class DaoPersonaFisica implements DaoInterfazPersonaFisica {

    public DaoPersonaFisica() {}


    @Override
    public boolean persistirPersonaFisica(PersonaFisica persona) throws PersistenciaException {
        String sqlPadre = "INSERT INTO responsable_pago DEFAULT VALUES";
        String sqlHija = "INSERT INTO persona_fisica (id_responsable, tipo_documento, nro_documento) VALUES (?, ?, ?)";


        Connection conn = null;
        try {
            conn = Conexion.getConnection();
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
                ps.setString(2, persona.getHuesped().getTipoDocumento().name());
                ps.setString(3, persona.getHuesped().getNroDocumento());
                ps.executeUpdate();
            }


            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ignored) {}
            throw new PersistenciaException("Error persistir Persona Fisica", e);
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
        }
    }

    @Override
    public DtoPersonaFisica obtenerPorId(int id) { return null; } // TODO
}