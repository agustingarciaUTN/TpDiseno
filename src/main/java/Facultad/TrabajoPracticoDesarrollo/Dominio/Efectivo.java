package Facultad.TrabajoPracticoDesarrollo.Dominio;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "efectivo")
@Getter @Setter
public class Efectivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_efectivo")
    private Integer idEfectivo;

    public Efectivo() {}

    // Builder
    public static class Builder {
        private Integer idEfectivo;

        public Builder() {}

        public Builder idEfectivo(Integer val) { idEfectivo = val; return this; }

        public Efectivo build() {
            Efectivo e = new Efectivo();
            e.setIdEfectivo(idEfectivo);
            return e;
        }
    }
}