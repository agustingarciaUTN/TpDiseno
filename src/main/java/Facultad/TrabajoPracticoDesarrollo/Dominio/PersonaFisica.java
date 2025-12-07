package Facultad.TrabajoPracticoDesarrollo.Dominio;

import jakarta.persistence.*;

@Entity
@Table(name = "persona_fisica")
@PrimaryKeyJoinColumn(name = "id_responsable") // PK y FK al padre
public class PersonaFisica extends ResponsablePago {

    // Relación con Huesped (Usando su clave compuesta)
    @OneToOne
    @JoinColumns({
            @JoinColumn(name = "tipo_documento", referencedColumnName = "tipo_documento"),
            @JoinColumn(name = "nro_documento", referencedColumnName = "numero_documento")
    })
    private Huesped huesped;

    public PersonaFisica() {
        super();
        this.setTipoResponsable("F"); // Valor fijo para Física
    }

    public Huesped getHuesped() { return huesped; }
    public void setHuesped(Huesped huesped) { this.huesped = huesped; }

    // Builder estático
    public static class Builder {
        private Direccion direccion;
        private Huesped huesped;

        public Builder(Huesped huesped) {}
        public Builder direccion(Direccion val) { direccion = val; return this; }
        public Builder huesped(Huesped val) { huesped = val; return this; }

        public PersonaFisica build() {
            PersonaFisica pf = new PersonaFisica();
            pf.setDireccion(direccion);
            pf.setHuesped(huesped);
            return pf;
        }
    }
}