package Facultad.TrabajoPracticoDesarrollo.Dominio;

import Facultad.TrabajoPracticoDesarrollo.enums.Moneda;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "cheque")
@Getter @Setter
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
    private Date fechaDePago;

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
        private Date fechaDePago;

        public Builder() {}

        public Builder numeroCheque(String val) { numeroCheque = val; return this; }
        public Builder banco(String val) { banco = val; return this; }
        public Builder monto(Double val) { monto = val; return this; }
        public Builder fechaCobro(Date val) { fechaCobro = val; return this; }
        public Builder moneda(Moneda val) { moneda = val; return this; }
        public Builder plaza(String val) { plaza = val; return this; }
        public Builder fechaDePago(Date val) { fechaDePago = val; return this; }

        public Cheque build() {
            Cheque c = new Cheque();
            c.setNumeroCheque(numeroCheque);
            c.setBanco(banco);
            c.setMonto(monto);
            c.setFechaCobro(fechaCobro);
            c.setMoneda(moneda);
            c.setPlaza(plaza);
            c.setFechaDePago(fechaDePago);
            return c;
        }
    }
}