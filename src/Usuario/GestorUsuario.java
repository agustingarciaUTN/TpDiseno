package Usuario;
import Dominio.Usuario;
import static Utils.UsuarioHelper.generarHashMD5;

public class GestorUsuario {
    private final DaoUsuarioInterfaz daoUsuario;

    public GestorUsuario(DaoUsuarioInterfaz dao) {
        this.daoUsuario = dao;
    }

    public boolean autenticarUsuario(String nombre, String contrasenia) {
        try {
            //Validaciones básicas antes de consultar la BDD
            if (nombre == null || nombre.trim().isEmpty()) {
                System.err.println("El nombre de usuario no puede estar vacío");
                return false;
            }

            if (contrasenia == null || contrasenia.isEmpty()) {
                System.err.println("La contraseña no puede estar vacía");
                return false;
            }

            //Obtener el usuario de la base de datos
            DtoUsuario usuarioBDD = daoUsuario.obtenerUsuarioPorNombre(nombre);

            //Verificar si el usuario existe
            if (usuarioBDD == null) {
                                System.err.println("Credenciales inválidas");
                return false;
            }

            //Generar hash MD5 de la contraseña ingresada
            String hashIngresado = generarHashMD5(contrasenia);

            //Comparar el hash ingresado con el hash almacenado en la BDD
            String hashAlmacenado = usuarioBDD.getHashContrasenia();

            //Verificar si las contraseñas coinciden
            if (hashAlmacenado == null) {
                System.err.println("Error: hash de contraseña nulo.");
                return false;
            }
            return hashAlmacenado.equals(hashIngresado);
        } catch (Exception e) {
            System.err.println("Error durante la autenticación: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}