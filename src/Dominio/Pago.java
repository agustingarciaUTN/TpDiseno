package Dominio;

import enums.Moneda;

import java.util.ArrayList;
import java.util.Date;

public class Pago {
    
    private Moneda moneda;
    private float montoTotal;
    private float cotizacion;
    private Date fechaPago;
    private ArrayList<MedioPago> mediosPago;
    private int idFactura;//CUAL DE LOS DOS
    private Factura factura;// CUAL DE LOS DOS

}
