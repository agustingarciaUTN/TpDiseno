package Usuario;
import Dominio.Usuario;

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
            DtoUsuario usuarioBDD = daoUsuario.ObtenerUsuarioPorNombre(nombre);

            //Verificar si el usuario existe
            if (usuarioBDD == null) {
                System.err.println("Usuario no encontrado en la base de datos");
                return false;
            }

            //Generar hash MD5 de la contraseña ingresada
            String hashIngresado = new Usuario().generarHashMD5(contrasenia);

            //Comparar el hash ingresado con el hash almacenado en la BDD
            String hashAlmacenado = usuarioBDD.getHashContrasenia();

            //Verificar si las contraseñas coinciden
            boolean autenticacionExitosa = hashAlmacenado.equals(hashIngresado);


            return autenticacionExitosa;

        } catch (Exception e) {
            System.err.println("Error durante la autenticación: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}