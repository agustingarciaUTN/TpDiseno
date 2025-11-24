package Usuario;


public interface DaoUsuarioInterfaz {
    boolean crearUsuario(String nombre, String contrasenia, int idUsuario);
    boolean modificarUsuario(int idUsuario);
    boolean eliminarUsuario(int idUsuario);
    DtoUsuario obtenerUsuario(int idUsuario);
    DtoUsuario obtenerUsuarioPorNombre(String nombre);
}

