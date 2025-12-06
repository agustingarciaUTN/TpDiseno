package Facultad.TrabajoPracticoDesarrollo.Usuario;

public class DtoUsuario {
    private int idUsuario;
    private String nombre;

    // Dato sensible PLANO (Solo vive en el DTO para el transporte)
    private String contrasenia;

    // --- CONSTRUCTOR PRIVADO ---
    private DtoUsuario(Builder builder) {
        this.idUsuario = builder.idUsuario;
        this.nombre = builder.nombre;
        this.contrasenia = builder.contrasenia;
    }

    public DtoUsuario() {}

    // --- GETTERS Y SETTERS ---
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getContrasenia() { return contrasenia; }
    public void setContrasenia(String contrasenia) { this.contrasenia = contrasenia; }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private int idUsuario;
        private String nombre;
        private String contrasenia;

        public Builder() {}

        public Builder id(int val) { idUsuario = val; return this; }
        public Builder nombre(String val) { nombre = val; return this; }
        public Builder contrasenia(String val) { contrasenia = val; return this; }

        public DtoUsuario build() {
            return new DtoUsuario(this);
        }
    }
}