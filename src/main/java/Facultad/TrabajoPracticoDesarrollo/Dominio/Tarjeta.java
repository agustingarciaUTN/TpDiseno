package Facultad.TrabajoPracticoDesarrollo.Dominio;

import Facultad.TrabajoPracticoDesarrollo.enums.Moneda;
import Facultad.TrabajoPracticoDesarrollo.enums.RedDePago;

import java.util.ArrayList;
import java.util.Date;

public abstract class Tarjeta extends MedioPago {

    private RedDePago redDePago;
    private String banco;
    private String numeroDeTarjeta; // Cambiado a String (int se queda corto para 16 dígitos)
    private Date fechaVencimiento;
    private int codigoSeguridad;
    private float monto;
    private Moneda moneda;
    private Date fechaDePago;

    // Constructor protegido para que lo usen los hijos (Debito/Credito)
    protected Tarjeta(int idPago, ArrayList<Pago> pagos,
                      RedDePago redDePago, String banco, String numeroDeTarjeta,
                      Date fechaVencimiento, int codigoSeguridad, float monto,
                      Moneda moneda, Date fechaDePago) {
        super(idPago, pagos);
        this.redDePago = redDePago;
        this.banco = banco;
        this.numeroDeTarjeta = numeroDeTarjeta;
        this.fechaVencimiento = fechaVencimiento;
        this.codigoSeguridad = codigoSeguridad;
        this.monto = monto;
        this.moneda = moneda;
        this.fechaDePago = fechaDePago;
    }

    // Getters y Setters
    public RedDePago getRedDePago() { return redDePago; }
    public void setRedDePago(RedDePago redDePago) { this.redDePago = redDePago; }

    public String getBanco() { return banco; }
    public void setBanco(String banco) { this.banco = banco; }

    public String getNumeroDeTarjeta() { return numeroDeTarjeta; }
    public void setNumeroDeTarjeta(String numeroDeTarjeta) { this.numeroDeTarjeta = numeroDeTarjeta; }

    public Date getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(Date fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public int getCodigoSeguridad() { return codigoSeguridad; }
    public void setCodigoSeguridad(int codigoSeguridad) { this.codigoSeguridad = codigoSeguridad; }

    public float getMonto() { return monto; }
    public void setMonto(float monto) { this.monto = monto; }

    public Moneda getMoneda() { return moneda; }
    public void setMoneda(Moneda moneda) { this.moneda = moneda; }

    public Date getFechaDePago() { return fechaDePago; }
    public void setFechaDePago(Date fechaDePago) { this.fechaDePago = fechaDePago; }
}