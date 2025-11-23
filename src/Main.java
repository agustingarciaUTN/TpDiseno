// java

import PantallaDeTrabajo.Pantalla;

public class Main {

    public static void main(String[] args) {

        /* PASO 1: Probar la conexión a la base de datos
        if(probarConexion()){
        } else {
            // System.out.println("✗ Error en la conexión a la base de datos");
            // System.out.println("Verifica que:");
            // System.out.println("  - La base de datos esté activa");
            // System.out.println("  - Las credenciales sean correctas");
            // System.out.println("  - Tengas el driver JDBC de PostgreSQL\n");
            return; // Salir si no hay conexión
        }

        // PASO 2: Probar el hash MD5
        probarHashMD5();

        // PASO 3: Probar el DAO (acceso a datos)
        probarDao();

        // PASO 4: Probar el Gestor (lógica de negocio)
        probarGestor();


        */


        Pantalla pantalla = new Pantalla();
        pantalla.iniciarSistema();
    }
}

    // Probar conexión a la base de datos
    /*private static boolean probarConexion(){
        Connection conn = null;
        try {
            conn = Coneccion.getConnection();
            //System.out.println("Base de datos: " + conn.getCatalog());
            //System.out.println("Usuario: " + conn.getMetaData().getUserName());
            return true;
        } catch (SQLException e) {
            // System.out.println("Error: " + e.getMessage());
            return false;
        } finally {
            try {
                if(conn != null && !conn.isClosed()){
                    conn.close();
                }
            } catch (SQLException e) {
                // System.out.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }

    // Probar generación de hash MD5
    private static void probarHashMD5(){
        Usuario usuario = new Usuario();
        String contrasenia = "1234";
        String hash = usuario.generarHashMD5(contrasenia);

        // System.out.println("Contraseña original: " + contrasenia);
        // System.out.println("Hash MD5 generado:   " + hash);
        // System.out.println("Hash esperado:       81dc9bdb52d04dc20036dbd8313ed055");

        if(hash.equals("81dc9bdb52d04dc20036dbd8313ed055")){
            // System.out.println("✓ Hash correcto");
        } else {
            // System.out.println("✗ Hash incorrecto");
        }
    }

    // Probar acceso a datos (DAO)
    private static void probarDao(){
        DaoUsuarioInterfaz dao = new DaoUsuario();

        // System.out.println("Buscando usuario 'admin' en la base de datos...");
        DtoUsuario usuario = dao.ObtenerUsuarioPorNombre("admin");

        if(usuario != null){
            // System.out.println("✓ Usuario encontrado:");
            // System.out.println("  - ID: " + usuario.getIdUsuario());
            // System.out.println("  - Nombre: " + usuario.getNombre());
            // System.out.println("  - Hash: " + usuario.getHashContrasenia());
        } else {
            // System.out.println("✗ Usuario no encontrado");
            // System.out.println("Asegúrate de haber ejecutado el script SQL:");
            // System.out.println("INSERT INTO usuarios (id_usuario, nombre, contrasenia)");
            // System.out.println("VALUES (1, 'admin', '81dc9bdb52d04dc20036dbd8313ed055');");
        }
    }

    // Probar lógica de autenticación (Gestor)
    private static void probarGestor(){
        DaoUsuarioInterfaz dao = new DaoUsuario();
        GestorUsuario gestor = new GestorUsuario(dao);

        // System.out.println("Probando autenticación con credenciales correctas:");
        // System.out.println("  Usuario: admin");
        // System.out.println("  Contraseña: 1234");

        boolean autenticado = gestor.autenticarUsuario("admin", "1234");



        // System.out.println("\nProbando autenticación con contraseña incorrecta:");
        // System.out.println("  Usuario: admin");
        // System.out.println("  Contraseña: password_incorrecta");

        boolean autenticadoIncorrecto = gestor.autenticarUsuario("admin", "password_incorrecta");

        if(!autenticadoIncorrecto){
            // System.out.println("✓ Autenticación rechazada correctamente");
        } else {
            // System.out.println("✗ ERROR: Se autenticó con contraseña incorrecta");
        }
    }
}*/
