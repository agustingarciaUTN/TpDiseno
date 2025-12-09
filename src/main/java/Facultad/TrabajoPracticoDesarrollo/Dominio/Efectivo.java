package Facultad.TrabajoPracticoDesarrollo.Dominio;

import Facultad.TrabajoPracticoDesarrollo.enums.Moneda;
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

    @Column(name = "monto")
    private float monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "moneda")
    private Moneda moneda;

    @Column(name = "fecha_de_pago")
    @Temporal(TemporalType.DATE)
    private Date fechaDePago;

    public Efectivo() {}

    // Builder
    public static class Builder {
        private float monto;
        private Moneda moneda;
        private Date fechaDePago;
        private Integer idEfectivo;

        public Builder(float monto, Moneda moneda) {
            this.monto = monto;
            this.moneda = moneda;
        }


        public Builder fechaDePago(Date val) { fechaDePago = val; return this; }

        public Builder idEfectivo(Integer val) { idEfectivo = val; return this; }

        public Efectivo build() {
            Efectivo e = new Efectivo();
            e.setMonto(monto);
            e.setMoneda(moneda);
            e.setFechaDePago(fechaDePago);
            e.setIdEfectivo(idEfectivo);
            return e;
        }
    }
}