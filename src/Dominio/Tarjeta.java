package Dominio;

import enums.RedDePago;
import java.util.Date;
import enums.Moneda;

public abstract class Tarjeta {
    
    private RedDePago redDePago;
    private String banco;
    private int numeroDeTarjeta;
    private Date fechaVencimiento;
    private int codigoSeguridad;
    private float monto;
    private Moneda moneda;
    private Date fechaDePago;

    public Tarjeta(RedDePago redDePago, String banco, int numeroDeTarjeta, Date fechaVencimiento, int codigoSeguridad, float monto, Moneda moneda, Date fechaDePago) {
        if(numeroDeTarjeta <= 0) {
            throw new IllegalArgumentException("El número de tarjeta debe ser un número positivo.");
        }
        if(redDePago == null) {
            throw new IllegalArgumentException("La red de pago no puede ser nula.");
        }
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
    public RedDePago getRedDePago() {
        return redDePago;
    }
    public void setRedDePago(RedDePago redDePago) {
        this.redDePago = redDePago;
    }
    public String getBanco() {
        return banco;
    }
    public void setBanco(String banco) {
        this.banco = banco;
    }
    public int getNumeroDeTarjeta() {
        return numeroDeTarjeta;
    }
    public void setNumeroDeTarjeta(int numeroDeTarjeta) {
        this.numeroDeTarjeta = numeroDeTarjeta;
    }
    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }
    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }
    public int getCodigoSeguridad() {
        return codigoSeguridad;
    }
    public void setCodigoSeguridad(int codigoSeguridad) {
        this.codigoSeguridad = codigoSeguridad;
    }
    public float getMonto() {
        return monto;
    }
    public void setMonto(float monto) {
        this.monto = monto;
    }
    public Moneda getMoneda() {
        return moneda;
    }
    public void setMoneda(Moneda moneda) {
        this.moneda = moneda;
    }
    public Date getFechaDePago() {
        return fechaDePago;
    }
    public void setFechaDePago(Date fechaDePago) {
        this.fechaDePago = fechaDePago;
    }

}
