package Facultad.TrabajoPracticoDesarrollo.Dominio;

import Facultad.TrabajoPracticoDesarrollo.enums.Moneda;
import Facultad.TrabajoPracticoDesarrollo.enums.RedDePago;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tarjeta_credito")
@PrimaryKeyJoinColumn(name = "numero_tarjeta")
public class TarjetaCredito extends Tarjeta {

    @Column(name = "cuotas")
    private Integer cuotas;

    public TarjetaCredito() { super(); }

    public Integer getCuotas() { return cuotas; }
    public void setCuotas(Integer cuotas) { this.cuotas = cuotas; }

    // Builder específico para Crédito
    public static class Builder {
        private String nro;
        private String banco;
        private RedDePago red;
        private Double monto;
        private Integer cuotas;
        private Moneda moneda;
        private Date fechaPago;

        public Builder(String nro, String banco, Double monto) {
            this.nro = nro; this.banco = banco; this.monto = monto;
        }
        public Builder cuotas(Integer val) { cuotas = val; return this; }
        public Builder red(RedDePago val) { red = val; return this; }
        public Builder moneda(Moneda val) { moneda = val; return this; }
        public Builder fecha(Date val) { fechaPago = val; return this; }

        public TarjetaCredito build() {
            TarjetaCredito t = new TarjetaCredito();
            t.setNumeroTarjeta(nro);
            t.setBanco(banco);
            t.setMonto(monto);
            t.setCuotas(cuotas);
            t.setRedDePago(red);
            t.setMoneda(moneda);
            t.setFechaPago(fechaPago);
            return t;
        }
    }
}