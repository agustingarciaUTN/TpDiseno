package Facultad.TrabajoPracticoDesarrollo.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DtoUsuario {

    @NotNull
    @Positive
    private int idUsuario;

    @NotBlank
    @Size(min = 2, max = 100)
    private String nombre;

    // Dato sensible PLANO (Solo vive en el DTO para el transporte)
    @JsonIgnore //Nose si va
    @NotBlank
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