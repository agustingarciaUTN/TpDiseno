package Facultad.TrabajoPracticoDesarrollo.Dominio;

import Facultad.TrabajoPracticoDesarrollo.enums.Moneda;
import Facultad.TrabajoPracticoDesarrollo.enums.RedDePago;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tarjeta_debito")
@PrimaryKeyJoinColumn(name = "numero_tarjeta")
public class TarjetaDebito extends Tarjeta {

    public TarjetaDebito() { super(); }

    // Builder específico para Débito
    public static class Builder {
        private String nro;
        private String banco;
        private RedDePago red;
        private Double monto;
        private Moneda moneda;
        private Date fechaPago;

        public Builder(String nro, String banco, Double monto) {
            this.nro = nro; this.banco = banco; this.monto = monto;
        }
        public Builder red(RedDePago val) { red = val; return this; }
        public Builder moneda(Moneda val) { moneda = val; return this; }
        public Builder fecha(Date val) { fechaPago = val; return this; }

        public TarjetaDebito build() {
            TarjetaDebito t = new TarjetaDebito();
            t.setNumeroTarjeta(nro);
            t.setBanco(banco);
            t.setMonto(monto);
            t.setRedDePago(red);
            t.setMoneda(moneda);
            t.setFechaPago(fechaPago);
            return t;
        }
    }
}