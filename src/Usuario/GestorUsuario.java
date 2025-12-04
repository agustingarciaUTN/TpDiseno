package Usuario;

import static Utils.UsuarioHelper.generarHashMD5;

public class GestorUsuario {

    // 1. La única instancia (static y private)
    private static GestorUsuario instancia;

    // Referencias a los DAOs que este gestor necesita
    private final DaoUsuarioInterfaz daoUsuario;// Ejemplo si necesita validar habitación

    // 2. Constructor PRIVADO
    // Nadie puede hacer new GestorUsuario() desde fuera.
    private GestorUsuario() {
        // Obtenemos las instancias de los DAOs
        this.daoUsuario = DaoUsuario.getInstance();
    }

    // 3. Método de Acceso Global (Synchronized para seguridad en hilos)
    public static synchronized GestorUsuario getInstance() {
        if (instancia == null) {
            instancia = new GestorUsuario();
        }
        return instancia;
    }



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
            DtoUsuario usuarioBDD = daoUsuario.buscarPorNombre(nombre);

            //Verificar si el usuario existe
            if (usuarioBDD == null) {
                                System.err.println("Credenciales inválidas");
                return false;
            }

            //Generar hash MD5 de la contraseña ingresada
            String hashIngresado = generarHashMD5(contrasenia);

            //Guardar el hash de la base de datos
            String hashAlmacenado = usuarioBDD.getContrasenia();

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