package Dominio;

public class Usuario {

    private int idUsuario;
    private String nombre;
    private String hashContrasenia; // SOLO el hash

    // --- CONSTRUCTOR PRIVADO ---
    private Usuario(Builder builder) {
        this.idUsuario = builder.idUsuario;
        this.nombre = builder.nombre;
        this.hashContrasenia = builder.hashContrasenia;
    }

    public Usuario() {}

    // --- GETTERS Y SETTERS ---
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getHashContrasenia() { return hashContrasenia; }
    public void setHashContrasenia(String hashContrasenia) { this.hashContrasenia = hashContrasenia; }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private int idUsuario = 0;
        private String nombre;
        private String hashContrasenia;

        // Constructor con obligatorios
        public Builder(String nombre, String hashContrasenia) {
            this.nombre = nombre;
            this.hashContrasenia = hashContrasenia;
        }

        public Builder id(int val) { idUsuario = val; return this; }

        public Usuario build() {
            if (nombre == null || nombre.isEmpty()) {
                throw new IllegalArgumentException("El nombre de usuario es obligatorio.");
            }
            if (hashContrasenia == null || hashContrasenia.isEmpty()) {
                throw new IllegalArgumentException("El hash de la contraseña es obligatorio.");
            }
            return new Usuario(this);
        }
    }
}