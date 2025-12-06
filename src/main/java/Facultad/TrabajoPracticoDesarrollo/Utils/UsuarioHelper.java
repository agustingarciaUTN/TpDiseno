package Facultad.TrabajoPracticoDesarrollo.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UsuarioHelper {

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
