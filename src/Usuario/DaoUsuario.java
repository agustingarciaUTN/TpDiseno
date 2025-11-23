package Usuario;

import Dominio.Usuario;
import BaseDedatos.Coneccion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DaoUsuario implements DaoUsuarioInterfaz {

    @Override
    public DtoUsuario ObtenerUsuario(int idUsuario){
        String sql = "SELECT id_usuario, nombre, contrasena FROM usuario WHERE id_usuario = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = Coneccion.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            rs = ps.executeQuery();

            if(rs.next()){
                DtoUsuario dto = new DtoUsuario();
                dto.setIdUsuario(rs.getInt("id_usuario"));
                dto.setNombre(rs.getString("nombre"));
                dto.setHashContrasenia(rs.getString("contrasena"));
                return dto;
            }

            return null;

        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por ID: " + e.getMessage());
            return null;
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
    }

    @Override
    public DtoUsuario ObtenerUsuarioPorNombre(String nombre){
        String sql = "SELECT id_usuario, nombre, contrasena FROM usuario WHERE nombre = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = Coneccion.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, nombre);
            rs = ps.executeQuery();

            if(rs.next()){
                DtoUsuario dto = new DtoUsuario();
                dto.setIdUsuario(rs.getInt("id_usuario"));
                dto.setNombre(rs.getString("nombre"));
                dto.setHashContrasenia(rs.getString("contrasena"));
                return dto;
            }

            return null;

        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por nombre: " + e.getMessage());
            return null;
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
    }

    @Override
    public boolean CrearUsuario(String nombre, String contrasenia, int idUsuario){
        String sql = "INSERT INTO usuario (id_usuario, nombre, contrasena) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = Coneccion.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            ps.setString(2, nombre);
            ps.setString(3, contrasenia);

            int filasAfectadas = ps.executeUpdate();

            if(filasAfectadas > 0){
                System.out.println("Usuario creado exitosamente: " + nombre);
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
            return false;
        } finally {
            cerrarRecursos(conn, ps, null);
        }
    }

    @Override
    public boolean ModificarUsuario(int idUsuario){
        String sql = "UPDATE usuario SET nombre = ?, contrasena = ? WHERE id_usuario = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = Coneccion.getConnection();
            ps = conn.prepareStatement(sql);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al modificar usuario: " + e.getMessage());
            return false;
        } finally {
            cerrarRecursos(conn, ps, null);
        }
    }

    @Override
    public boolean EliminarUsuario(int idUsuario){
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = Coneccion.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idUsuario);

            int filasAfectadas = ps.executeUpdate();

            if(filasAfectadas > 0){
                System.out.println("Usuario eliminado exitosamente");
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        } finally {
            cerrarRecursos(conn, ps, null);
        }
    }

    private void cerrarRecursos(Connection conn, PreparedStatement ps, ResultSet rs){
        try {
            if(rs != null && !rs.isClosed()) {
                rs.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar ResultSet");
        }

        try {
            if(ps != null && !ps.isClosed()) {
                ps.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar PreparedStatement");
        }

        try {
            if(conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar Connection");
        }
    }
}