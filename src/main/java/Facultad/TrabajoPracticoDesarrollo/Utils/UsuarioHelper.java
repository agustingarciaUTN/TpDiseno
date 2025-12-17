package Facultad.TrabajoPracticoDesarrollo.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilidad para operaciones relacionadas con usuarios.
 *
 * <p>Provee métodos estáticos de ayuda; actualmente contiene una función para
 * generar el hash MD5 de una cadena y devolverlo como texto hexadecimal en
 * minúsculas.</p>
 *
 * <p>Nota: MD5 no es adecuado para almacenar contraseñas en aplicaciones
 * de producción por razones de seguridad. Usar algoritmos modernos y técnicas
 * como PBKDF2, bcrypt o Argon2 para contraseñas.</p>
 */
public class UsuarioHelper {

    /**
     * Genera el hash MD5 de la cadena de entrada y lo devuelve en formato
     * hexadecimal (minúsculas).
     *
     * @param input texto de entrada cuyo hash se desea calcular (no debe ser {@code null})
     * @return representación hexadecimal en minúsculas del hash MD5 de {@code input}
     * @throws RuntimeException si no está disponible el algoritmo MD5 en el entorno
     *                          (envuelto desde {@link NoSuchAlgorithmException})
     *
     * <p>Comportamiento:
     * - La salida tiene longitud fija de 32 caracteres hexadecimales.
     * - Si {@code input} es {@code null} se lanzará una {@link NullPointerException}
     *   por la llamada a {@code input.getBytes()}.</p>
     *
     * <p>Ejemplo de uso:
     * <pre>
     * String h = UsuarioHelper.generarHashMD5("texto");
     * </pre>
     * </p>
     */
    public static String generarHashMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar el hash MD5", e);
        }
    }
}
