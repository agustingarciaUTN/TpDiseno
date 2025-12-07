package Facultad.TrabajoPracticoDesarrollo.Dominio;

import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
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

    // --- Getters y Setters ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getHashContrasenia() { return hashContrasenia; }
    public void setHashContrasenia(String hashContrasenia) { this.hashContrasenia = hashContrasenia; }
}