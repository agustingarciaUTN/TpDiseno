package Facultad.TrabajoPracticoDesarrollo.Dominio;

import Facultad.TrabajoPracticoDesarrollo.enums.Moneda;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "cheque")
public class Cheque {

    @Id
    @Column(name = "numero_cheque")
    private String numeroCheque;

    @Column(name = "banco")
    private String banco;

    @Column(name = "plaza")
    private String plaza;

    @Column(name = "fecha_cobro")
    @Temporal(TemporalType.DATE)
    private Date fechaCobro;

    @Column(name = "fecha_de_pago")
    @Temporal(TemporalType.DATE)
    private Date fechaPago;

    @Column(name = "monto")
    private Double monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "moneda")
    private Moneda moneda;

    public Cheque() {}

    // Builder
    public static class Builder {
        private String numeroCheque;
        private String banco;
        private Date fechaCobro;
        private Double monto;
        private Moneda moneda;
        private String plaza;
        private Date fechaPago;

        public Builder(String nro, String banco, Double monto) {
            this.numeroCheque = nro;
            this.banco = banco;
            this.monto = monto;
        }
        public Builder cobro(Date val) { fechaCobro = val; return this; }
        public Builder moneda(Moneda val) { moneda = val; return this; }
        public Builder plaza(String val) { plaza = val; return this; }
        public Builder pago(Date val) { fechaPago = val; return this; }

        public Cheque build() {
            Cheque c = new Cheque();
            c.setNumeroCheque(numeroCheque);
            c.setBanco(banco);
            c.setMonto(monto);
            c.setFechaCobro(fechaCobro);
            c.setMoneda(moneda);
            c.setPlaza(plaza);
            c.setFechaPago(fechaPago);
            return c;
        }
    }

    // Getters y Setters
    public String getNumeroCheque() { return numeroCheque; }
    public void setNumeroCheque(String numeroCheque) { this.numeroCheque = numeroCheque; }
    public String getBanco() { return banco; }
    public void setBanco(String banco) { this.banco = banco; }
    public String getPlaza() { return plaza; }
    public void setPlaza(String plaza) { this.plaza = plaza; }
    public Date getFechaCobro() { return fechaCobro; }
    public void setFechaCobro(Date fechaCobro) { this.fechaCobro = fechaCobro; }
    public Date getFechaPago() { return fechaPago; }
    public void setFechaPago(Date fechaPago) { this.fechaPago = fechaPago; }
    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }
    public Moneda getMoneda() { return moneda; }
    public void setMoneda(Moneda moneda) { this.moneda = moneda; }
}