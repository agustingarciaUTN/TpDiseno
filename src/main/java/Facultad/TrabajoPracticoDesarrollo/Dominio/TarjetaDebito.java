package Facultad.TrabajoPracticoDesarrollo.Dominio;

import Facultad.TrabajoPracticoDesarrollo.enums.Moneda;
import Facultad.TrabajoPracticoDesarrollo.enums.RedDePago;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "tarjeta_debito")
@Getter @Setter
@PrimaryKeyJoinColumn(name = "numero_tarjeta")
@DiscriminatorValue("D")
public class TarjetaDebito extends Tarjeta {

    public TarjetaDebito() { super(); }

    // Builder específico para Débito
    public static class Builder {
        private String nro;
        private String banco;
        private RedDePago red;
        private Date fechaVencimiento;
        private Integer codigoSeguridad;

        public Builder() {}

        public Builder nro(String val) { nro = val; return this; }
        public Builder red(RedDePago val) { red = val; return this; }
        public Builder banco(String val) { banco = val; return this; }
        public Builder fechaVencimiento(Date val) { fechaVencimiento = val; return this; }
        public Builder codigoSeguridad(Integer val) { codigoSeguridad = val; return this; }

        public TarjetaDebito build() {
            TarjetaDebito t = new TarjetaDebito();
            t.setNumeroTarjeta(nro);
            t.setBanco(banco);
            t.setRedDePago(red);
            t.setFechaVencimiento(fechaVencimiento);
            t.setCodigoSeguridad(codigoSeguridad);
            return t;
        }
    }
}