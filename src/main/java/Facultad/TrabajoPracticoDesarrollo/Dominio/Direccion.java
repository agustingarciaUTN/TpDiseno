package Facultad.TrabajoPracticoDesarrollo.Dominio;

import jakarta.persistence.*;

@Entity
@Table(name = "direccion")
public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_direccion")
    private Integer id; // Cambié 'idDireccion' a 'id' para ser más estándar, pero mapeado a la columna correcta

    @Column(name = "calle")
    private String calle;

    @Column(name = "numero")
    private Integer numero; // Integer permite nulls, int no. JPA prefiere objetos.

    @Column(name = "departamento")
    private String departamento;

    @Column(name = "piso")
    private String piso;

    @Column(name = "codpostal") // Revisa si en tu BD es "codPostal" o "codpostal"
    private Integer codPostal;

    @Column(name = "localidad")
    private String localidad;

    @Column(name = "provincia")
    private String provincia;

    @Column(name = "pais")
    private String pais;

    // --- 1. CONSTRUCTOR VACÍO (Obligatorio para JPA) ---
    public Direccion() {
    }

    // --- 2. CONSTRUCTOR PRIVADO (Para el Builder) ---
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

    // --- GETTERS Y SETTERS (Necesarios para JPA y para usarlos) ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCalle() { return calle; }
    public void setCalle(String calle) { this.calle = calle; }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public String getPiso() { return piso; }
    public void setPiso(String piso) { this.piso = piso; }

    public Integer getCodPostal() { return codPostal; }
    public void setCodPostal(Integer codPostal) { this.codPostal = codPostal; }

    public String getLocalidad() { return localidad; }
    public void setLocalidad(String localidad) { this.localidad = localidad; }

    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    // --- 3. CLASE BUILDER (Tu patrón de diseño) ---
    public static class Builder {
        // Mismos atributos para acumular datos
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

        public Builder id(Integer val) { id = val; return this; }
        public Builder calle(String val) { calle = val; return this; }
        public Builder numero(Integer val) { numero = val; return this; }
        public Builder departamento(String val) { departamento = val; return this; }
        public Builder piso(String val) { piso = val; return this; }
        public Builder codPostal(Integer val) { codPostal = val; return this; }
        public Builder localidad(String val) { localidad = val; return this; }
        public Builder provincia(String val) { provincia = val; return this; }
        public Builder pais(String val) { pais = val; return this; }

        public Direccion build() {
            return new Direccion(this);
        }
    }
}
}
