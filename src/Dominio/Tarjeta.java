package Dominio;

import enums.RedDePago;
import java.util.Date;
import enums.Moneda;

public class Tarjeta {
    
    private RedDePago redDePago;
    private String banco;
    private int numeroDeTarjeta;
    private Date fechaVencimiento;
    private int codigoSeguridad;
    private float monto;
    private Moneda moneda;
    private Date fechaDePago;

}
