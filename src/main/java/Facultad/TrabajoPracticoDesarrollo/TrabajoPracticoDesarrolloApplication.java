package Facultad.TrabajoPracticoDesarrollo;

import Facultad.TrabajoPracticoDesarrollo.BaseDeDatos.Conexion;
import Facultad.TrabajoPracticoDesarrollo.Dominio.PersonaFisica;
import Facultad.TrabajoPracticoDesarrollo.Dominio.PersonaJuridica;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Usuario;
import Facultad.TrabajoPracticoDesarrollo.Excepciones.PersistenciaException;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoPersonaFisica;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoPersonaJuridica;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoUsuario;
import Facultad.TrabajoPracticoDesarrollo.Repositories.DaoInterfazPersonaFisica;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Repository;

import java.sql.*;

@SpringBootApplication
public class TrabajoPracticoDesarrolloApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrabajoPracticoDesarrolloApplication.class, args);
	}

    public static interface DaoUsuarioInterfaz {
        // Para persistir, recibimos la entidad completa con el hash ya calculado por el Gestor
        boolean persistir(Usuario usuario) throws PersistenciaException;

        // Buscamos por nombre para el login
        DtoUsuario buscarPorNombre(String nombre) throws PersistenciaException;

        // Si necesitamos modificar contraseña
        boolean modificar(Usuario usuario) throws PersistenciaException;
    }

    public static interface DaoInterfazPersonaJuridica {
        boolean persistirPersonaJuridica(PersonaJuridica pj) throws PersistenciaException;
        boolean modificarPersonaJuridica(PersonaJuridica pj) throws PersistenciaException;
        boolean eliminarPersonaJuridica(int id);
        DtoPersonaJuridica obtenerPorId(int id);
        // PersonaJuridica obtenerPorCuit(String cuit);
    }

    @Repository
    public static class DaoPersonaFisica implements DaoInterfazPersonaFisica {

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

    @Repository
    public static class DaoPersonaJuridica implements DaoInterfazPersonaJuridica {

        public DaoPersonaJuridica() {}



        @Override
        public boolean persistirPersonaJuridica(PersonaJuridica pj) throws PersistenciaException {
            String sqlPadre = "INSERT INTO responsable_pago DEFAULT VALUES";
            String sqlHija = "INSERT INTO persona_juridica (id_responsable, razon_social, cuit, id_direccion) VALUES (?, ?, ?, ?)";
            String sqlTel = "INSERT INTO telefono_personaJuridica (id_responsable, telefonos) VALUES (?, ?)";

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
                if (conn != null) try { conn.rollback(); } catch (SQLException ignored) {}
                throw new PersistenciaException("Error persistir PJ", e);
            } finally {
                if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
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
}
