package Dominio;

import enums.Moneda;
import java.util.Date;

public class Efectivo extends MedioPago{

    private int idEfectivo;
    private Moneda moneda;
    private float monto;
    private Date fechaDePago;
    

    public Efectivo(int idEfectivo, Moneda moneda, float monto, Date fechaDePago) {
        if(idEfectivo <= 0) {
            throw new IllegalArgumentException("El ID del efectivo debe ser un número positivo.");
        }
        this.idEfectivo = idEfectivo;
        this.moneda = moneda;
        this.monto = monto;
        this.fechaDePago = fechaDePago;
    }


    // Getters y Setters
    public int getIdEfectivo() {
        return idEfectivo;
    }
    public void setIdEfectivo(int idEfectivo) {
        this.idEfectivo = idEfectivo;
    }
    public Moneda getMoneda() {
        return moneda;
    }
    public void setMoneda(Moneda moneda) {
        this.moneda = moneda;
    }
    public float getMonto() {
        return monto;
    }
    public void setMonto(float monto) {
        this.monto = monto;
    }
    public Date getFechaDePago() {
        return fechaDePago;
    }
    public void setFechaDePago(Date fechaDePago) {
        this.fechaDePago = fechaDePago;
    }
}
