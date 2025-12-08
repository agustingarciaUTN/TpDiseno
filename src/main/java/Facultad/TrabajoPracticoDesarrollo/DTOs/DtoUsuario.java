package Facultad.TrabajoPracticoDesarrollo.DTOs;

import lombok.Data;

@Data
public class DtoUsuario {
    // --- GETTERS Y SETTERS ---
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