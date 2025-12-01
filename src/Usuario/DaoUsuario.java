package Usuario;

import BaseDedatos.Conexion;
import Dominio.Usuario;
import Excepciones.PersistenciaException;
import java.sql.*;

public class DaoUsuario implements DaoUsuarioInterfaz {

    // 1. SINGLETON
    private static DaoUsuario instancia;
    private DaoUsuario() {}

    public static synchronized DaoUsuario getInstance() {
        if (instancia == null) {
            instancia = new DaoUsuario();
        }
        return instancia;
    }

    @Override
    public boolean persistir(Usuario usuario) throws PersistenciaException {
        String sql = "INSERT INTO usuario (nombre, hash_contrasenia) VALUES (?, ?)";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getHashContrasenia());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new PersistenciaException("Error al guardar usuario", e);
        }
    }

    @Override
    public DtoUsuario buscarPorNombre(String nombre) throws PersistenciaException {
        String sql = "SELECT * FROM usuario WHERE nombre = ?";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // USO DEL BUILDER con los datos justos y necesarios
                    return new DtoUsuario.Builder()
                            .nombre(rs.getString("nombre"))
                            .contrasenia(rs.getString("hash_contrasenia"))
                            .id(rs.getInt("id_usuario"))
                            .build();
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar usuario", e);
        }
        return null;
    }

    @Override
    public boolean modificar(Usuario usuario) throws PersistenciaException {
        // Por si cambian la contraseña
        String sql = "UPDATE usuario SET hash_contrasenia = ? WHERE id_usuario = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getHashContrasenia());
            pstmt.setInt(2, usuario.getIdUsuario());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al modificar usuario", e);
        }
    }
}