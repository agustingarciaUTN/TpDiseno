package Dominio;

import java.util.Date;

import enums.Moneda;
import enums.RedDePago;

public class TarjetaCredito extends Tarjeta {

    
    private int cuotasCantidad;


    public TarjetaCredito(RedDePago redDePago, String banco, int numeroDeTarjeta, Date fechaVencimiento, int codigoSeguridad, float monto, Moneda moneda, Date fechaDePago, int cuotasCantidad) {
        super(redDePago, banco, numeroDeTarjeta, fechaVencimiento, codigoSeguridad, monto, moneda, fechaDePago);
        this.cuotasCantidad = cuotasCantidad;
    }

    //setters y getters
    public int getCuotasCantidad() { 
        return cuotasCantidad;
    }
    public void setCuotasCantidad(int cuotasCantidad) {
        this.cuotasCantidad = cuotasCantidad;
    }
}
