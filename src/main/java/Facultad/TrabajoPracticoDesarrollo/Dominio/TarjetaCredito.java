package Facultad.TrabajoPracticoDesarrollo.Dominio;

import Facultad.TrabajoPracticoDesarrollo.enums.Moneda;
import Facultad.TrabajoPracticoDesarrollo.enums.RedDePago;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "tarjeta_credito")
@Getter @Setter
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
        private Date fechaVencimiento;
        private Integer codigoSeguridad;

        public Builder() {}

        public Builder nro(String val) { nro = val; return this;}
        public Builder monto(Double val) { monto = val; return this; }
        public Builder cuotas(Integer val) { cuotas = val; return this; }
        public Builder red(RedDePago val) { red = val; return this; }
        public Builder moneda(Moneda val) { moneda = val; return this; }
        public Builder fecha(Date val) { fechaPago = val; return this; }
        public Builder banco(String val) { banco = val; return this; }
        public Builder fechaVencimiento(Date val) { fechaVencimiento = val; return this; }
        public Builder codigoSeguridad(Integer val) { codigoSeguridad = val; return this; }

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