package Dominio;

import static Utils.UsuarioHelper.generarHashMD5;

public class Usuario {
    private int idUsuario;
    private String nombre;
    private String hashContrasenia;

    //sin constructor sin args porque no puede tener atributos null
    public Usuario(int idUsuario, String nombre, String contrasenia) {
        this.nombre = nombre;

        this.idUsuario = idUsuario;
        this.hashContrasenia = generarHashMD5(contrasenia);
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setHashContrasenia(String hash) {
        this.hashContrasenia = hash;
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


}