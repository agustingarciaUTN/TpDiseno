package Usuario;

import Dominio.Usuario;

public class DtoUsuario {
    private int idUsuario;
    private String nombre;
    private String contrasenia;
    private String hashContrasenia;

    public DtoUsuario() {
    }

    public DtoUsuario(int idUsuario, String nombre, String contrasenia) {
        this.nombre = nombre;
        this.contrasenia = contrasenia;
        this.idUsuario = idUsuario;
        this.hashContrasenia = new Usuario().generarHashMD5(contrasenia);
    }


public String getContrasenia() {
    return contrasenia;
}
public void setContrasenia(String Contrasenia) {
    this.contrasenia = Contrasenia;
}
public int getIdUsuario() {
    return idUsuario;
}
public void setIdUsuario(int idUsuario) {
    this.idUsuario = idUsuario;
}
public String getNombre() {
    return nombre;
}
public void setNombre(String nombre) {
    this.nombre = nombre;
}
public String getHashContrasenia() {
    return hashContrasenia;
}
public void setHashContrasenia(String hashContrasenia) {
    this.hashContrasenia = hashContrasenia;
}
}
