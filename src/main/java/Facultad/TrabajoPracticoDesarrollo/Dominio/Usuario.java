package Facultad.TrabajoPracticoDesarrollo.Dominio;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "usuario")
@Getter @Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "\"idUsuario\"")
    private Integer idUsuario;


    @Column(name = "nombre", unique = true)
    private String nombre;


    @Column(name = "\"hashContrasenia\"")
    private String contrasenia;


    @Column(name = "tipo_usuario")
    private String tipoUsuario;

    // --- CONSTRUCTORES ---
    public Usuario() {}

    private Usuario(Builder builder) {
        this.idUsuario = builder.idUsuario;
        this.nombre = builder.nombre;
        this.contrasenia = builder.contrasenia;
        this.tipoUsuario = builder.tipoUsuario;
    }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private Integer idUsuario;
        private String nombre;
        private String contrasenia;
        private String tipoUsuario;

        public Builder() {}

        public Builder id(Integer val) { idUsuario = val; return this; }
        public Builder nombre(String val) { nombre = val; return this; }
        public Builder password(String val) { contrasenia = val; return this; } // Mapea a 'contrasenia'
        public Builder tipo(String val) { tipoUsuario = val; return this; }

        public Usuario build() {
            return new Usuario(this);
        }
    }
}