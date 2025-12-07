package Facultad.TrabajoPracticoDesarrollo.Dominio;

import Facultad.TrabajoPracticoDesarrollo.enums.Moneda;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "efectivo")
public class Efectivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_efectivo")
    private Integer idEfectivo;

    @Column(name = "monto")
    private Double monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "moneda")
    private Moneda moneda;

    @Column(name = "fecha_de_pago")
    @Temporal(TemporalType.DATE)
    private Date fechaPago;

    public Efectivo() {}

    // Builder
    public static class Builder {
        private Double monto;
        private Moneda moneda;
        private Date fechaPago;

        public Builder(Double monto, Moneda moneda) {
            this.monto = monto;
            this.moneda = moneda;
        }
        public Builder fecha(Date val) { fechaPago = val; return this; }

        public Efectivo build() {
            Efectivo e = new Efectivo();
            e.setMonto(monto);
            e.setMoneda(moneda);
            e.setFechaPago(fechaPago);
            return e;
        }
    }

    // Getters y Setters
    public Integer getIdEfectivo() { return idEfectivo; }
    public void setIdEfectivo(Integer idEfectivo) { this.idEfectivo = idEfectivo; }
    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }
    public Moneda getMoneda() { return moneda; }
    public void setMoneda(Moneda moneda) { this.moneda = moneda; }
    public Date getFechaPago() { return fechaPago; }
    public void setFechaPago(Date fechaPago) { this.fechaPago = fechaPago; }
}