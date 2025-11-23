package Dominio;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Usuario {
    private int idUsuario;
    private String nombre;
    private String contrasenia;
    private String hashContrasenia;

    public Usuario() {
    }
    public Usuario(int idUsuario, String nombre, String contrasenia) {
        this.nombre = nombre;
        this.contrasenia = contrasenia;
        this.idUsuario = idUsuario;
        this.hashContrasenia = generarHashMD5(contrasenia);
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getContrasenia() {
        return contrasenia;
    }
    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
        this.hashContrasenia = generarHashMD5(contrasenia);
    }
    public int getId_Usuario(){
        return idUsuario;
    }
    public void setId_Usuario(int idUsuario){
        this.idUsuario = idUsuario;
    }
    public String getHashContrasenia() {
        return hashContrasenia;
    }

    public String generarHashMD5(String input) {
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