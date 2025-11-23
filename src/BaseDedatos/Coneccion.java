package BaseDedatos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Coneccion{

    // URL JDBC corregida (esta es la que va en getConnection)
    private static final String URL = "jdbc:postgresql://ep-red-truth-aczpiy6y-pooler.sa-east-1.aws.neon.tech:5432/neondb?sslmode=require";
    private static final String USUARIO = "neondb_owner";
    private static final String CONTRASENIA = "npg_QufXyUrW7aL9";

    private Coneccion() {
        // Constructor privado para clase utilitaria
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CONTRASENIA);
    }

    // Main para probar la conexión
    public static void main(String[] args) {
        Connection conn = null;

        try {
            System.out.println("🔄 Intentando conectar a Neon...");
            conn = getConnection();

            System.out.println("✅ ¡Conexión exitosa!");
            System.out.println("📊 Base de datos: " + conn.getCatalog());
            System.out.println("🔗 URL: " + conn.getMetaData().getURL());
            System.out.println("👤 Usuario: " + conn.getMetaData().getUserName());

        } catch (SQLException e) {
            System.out.println("❌ Error al conectar:");
            System.out.println("Mensaje: " + e.getMessage());
            e.printStackTrace();

        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                    System.out.println("🔒 Conexión cerrada correctamente.");
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}