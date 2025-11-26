package Dominio;

import java.util.Date;
import enums.Moneda;
import enums.RedDePago;

public class TarjetaDebito extends Tarjeta {

    public TarjetaDebito(RedDePago redDePago, String banco, int numeroDeTarjeta, Date fechaVencimiento, int codigoSeguridad, float monto, Moneda moneda, Date fechaDePago, int idPago,  java.util.ArrayList<Pago> pagos) {
        super(redDePago, banco, numeroDeTarjeta, fechaVencimiento, codigoSeguridad, monto, moneda, fechaDePago, idPago, pagos);
    }

    
}
