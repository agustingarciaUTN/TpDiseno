package Facultad.TrabajoPracticoDesarrollo.Dominio;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "direccion")
@Getter @Setter
public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_direccion")
    private Integer id;

    @Column(name = "calle")
    private String calle;

    @Column(name = "numero")
    private Integer numero;

    @Column(name = "departamento")
    private String departamento;

    @Column(name = "piso")
    private String piso;

    @Column(name = "localidad")
    private String localidad;

    @Column(name = "provincia")
    private String provincia;

    @Column(name = "pais")
    private String pais;

    @Column(name = "\"codPostal\"")
    private Integer codPostal;

    // --- Constructor Vacío (Obligatorio JPA) ---
    public Direccion() {}

    // --- Constructor para Builder ---
    private Direccion(Builder builder) {
        this.id = builder.id;
        this.calle = builder.calle;
        this.numero = builder.numero;
        this.departamento = builder.departamento;
        this.piso = builder.piso;
        this.codPostal = builder.codPostal;
        this.localidad = builder.localidad;
        this.provincia = builder.provincia;
        this.pais = builder.pais;
    }

    // --- BUILDER ---
    public static class Builder {
        private Integer id;
        private String calle;
        private Integer numero;
        private String departamento;
        private String piso;
        private Integer codPostal;
        private String localidad;
        private String provincia;
        private String pais;

        public Builder() {}

        public Builder calle(String val) { calle = val; return this; }
        public Builder numero(Integer val) { numero = val; return this; }
        public Builder departamento(String val) { departamento = val; return this; }
        public Builder piso(String val) { piso = val; return this; }
        public Builder codigoPostal(Integer val) { codPostal = val; return this; }
        public Builder localidad(String val) { localidad = val; return this; }
        public Builder provincia(String val) { provincia = val; return this; }
        public Builder pais(String val) { pais = val; return this; }
        public Builder id(Integer val) { id = val; return this; }

        public Direccion build() { return new Direccion(this); }
    }
}