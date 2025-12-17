package Facultad.TrabajoPracticoDesarrollo.Dominio;

import Facultad.TrabajoPracticoDesarrollo.enums.TipoCama;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cama")
@Getter @Setter
public class Cama {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cama")
    private Integer idCama;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cama")
    private TipoCama tipoCama;

    // Relación ManyToOne con Habitación
    @ManyToOne
    @JoinColumn(name = "id_habitacion", nullable = false) // Nombre de la columna FK en BD
    private Habitacion habitacion;

    public Cama() {}

    private Cama(Builder builder) {
        this.idCama = builder.idCama;
        this.tipoCama = builder.tipoCama;
        this.habitacion = builder.habitacion;
    }

    // Builder...
    public static class Builder {
        private Integer idCama;
        private TipoCama tipoCama;
        private Habitacion habitacion;

        public Builder() {}
        public Builder idCama(Integer val) { idCama = val; return this; }
        public Builder tipoCama(TipoCama val) { tipoCama = val; return this; }
        public Builder habitacion(Habitacion val) { habitacion = val; return this; }
        public Cama build() { return new Cama(this); }
    }
}