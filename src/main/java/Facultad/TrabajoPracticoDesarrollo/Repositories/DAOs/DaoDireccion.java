package Facultad.TrabajoPracticoDesarrollo.Repositories.DAOs;


import Facultad.TrabajoPracticoDesarrollo.Dominio.Direccion;
import Facultad.TrabajoPracticoDesarrollo.Excepciones.PersistenciaException;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoDireccion;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class DaoDireccion implements DaoDireccionInterfaz {
    private DaoDireccion() {}


    @Override
    public boolean persistirDireccion(Direccion direccion) throws PersistenciaException {
        String sql = "INSERT INTO direccion (calle, numero, departamento, piso, \"codPostal\", localidad, provincia, pais) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, direccion.getCalle());
            ps.setInt(2, direccion.getNumero());
            ps.setString(3, direccion.getDepartamento());
            ps.setString(4, direccion.getPiso());
            ps.setInt(5, direccion.getCodPostal());
            ps.setString(6, direccion.getLocalidad());
            ps.setString(7, direccion.getProvincia());
            ps.setString(8, direccion.getPais());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) direccion.setId(rs.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al persistir dirección", e);
        }
    }

    @Override
    public boolean modificarDireccion(Direccion direccion) throws PersistenciaException {
        String sql = "UPDATE direccion SET calle=?, numero=?, departamento=?, piso=?, \"codPostal\"=?, localidad=?, provincia=?, pais=? WHERE id_direccion=?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // ... setear parámetros ...
            ps.setString(1, direccion.getCalle());
            ps.setInt(2, direccion.getNumero());
            ps.setString(3, direccion.getDepartamento());
            ps.setString(4, direccion.getPiso());
            ps.setInt(5, direccion.getCodPostal());
            ps.setString(6, direccion.getLocalidad());
            ps.setString(7, direccion.getProvincia());
            ps.setString(8, direccion.getPais());
            ps.setInt(9, direccion.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al modificar dirección", e);
        }
    }

    @Override
    public boolean eliminarDireccion(int idDireccion) {
        String sql = "DELETE FROM direccion WHERE id_direccion=?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDireccion);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    @Override
    public DtoDireccion obtenerDireccion(int idDireccion) {
        String sql = "SELECT * FROM direccion WHERE id_direccion=?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDireccion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new DtoDireccion.Builder(
                            rs.getString("calle"), rs.getInt("numero"),
                            rs.getString("localidad"), rs.getString("provincia"), rs.getString("pais"))
                            .idDireccion(rs.getInt("id_direccion"))
                            .departamento(rs.getString("departamento"))
                            .piso(rs.getString("piso"))
                            .codPostal(rs.getInt("codPostal"))
                            .build();
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}