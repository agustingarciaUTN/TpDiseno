package Facultad.TrabajoPracticoDesarrollo.Dominio;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "persona_juridica")
@PrimaryKeyJoinColumn(name = "id_responsable")
public class PersonaJuridica extends ResponsablePago {

    @Column(name = "razon_social")
    private String razonSocial;

    @Column(name = "cuit")
    private String cuit;

    // Lista de teléfonos (Tabla satélite "telefonos_personaJuridica")
    @ElementCollection
    @CollectionTable(
            name = "\"telefonos_personaJuridica\"", // Comillas porque tiene mayúsculas en BD
            joinColumns = @JoinColumn(name = "id_responsable")
    )
    @Column(name = "telefonos")
    private List<Long> telefonos = new ArrayList<>();

    public PersonaJuridica() {
        super();
        this.setTipoResponsable("J");
    }

    // Getters y Setters
    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getCuit() { return cuit; }
    public void setCuit(String cuit) { this.cuit = cuit; }

    public List<Long> getTelefonos() { return telefonos; }
    public void setTelefonos(List<Long> telefonos) { this.telefonos = telefonos; }

    // Builder
    public static class Builder {
        private Direccion direccion;
        private String razonSocial;
        private String cuit;
        private List<Long> telefonos = new ArrayList<>();

        public Builder() {}
        public Builder direccion(Direccion val) { direccion = val; return this; }
        public Builder razonSocial(String val) { razonSocial = val; return this; }
        public Builder cuit(String val) { cuit = val; return this; }
        public Builder telefonos(List<Long> val) { telefonos = val; return this; }

        public PersonaJuridica build() {
            PersonaJuridica pj = new PersonaJuridica();
            pj.setDireccion(direccion);
            pj.setRazonSocial(razonSocial);
            pj.setCuit(cuit);
            pj.setTelefonos(telefonos);
            return pj;
        }
    }
}