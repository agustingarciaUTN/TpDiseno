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
    @Column(name = "id_usuario")
    private Integer id;

    @Column(name = "nombre")
    private String nombre;

    // CORRECCIÓN: Atributo para guardar el Hash
    @Column(name = "hashContrasenia")
    private String hashContrasenia;

    public Usuario() {}

    public Usuario(String nombre, String hashContrasenia) {
        this.nombre = nombre;
        this.hashContrasenia = hashContrasenia;
    }

}