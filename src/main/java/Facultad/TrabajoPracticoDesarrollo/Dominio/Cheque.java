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

    public Cheque() {}

    // Builder
    public static class Builder {
        private String numeroCheque;
        private String banco;
        private Date fechaCobro;
        private String plaza;

        public Builder() {}

        public Builder numeroCheque(String val) { numeroCheque = val; return this; }
        public Builder banco(String val) { banco = val; return this; }
        public Builder fechaCobro(Date val) { fechaCobro = val; return this; }
        public Builder plaza(String val) { plaza = val; return this; }

        public Cheque build() {
            Cheque c = new Cheque();
            c.setNumeroCheque(numeroCheque);
            c.setBanco(banco);
            c.setFechaCobro(fechaCobro);
            c.setPlaza(plaza);
            return c;
        }
    }
}