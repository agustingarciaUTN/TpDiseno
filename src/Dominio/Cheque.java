package Dominio;

import java.util.Date;

public class Cheque extends MedioPago {

    private String numeroCheque;
    private String banco;
    private String plaza;
    private float monto;
    private Date fechaCobro;
    private Date fechaDePago;


    public Cheque(String numeroCheque, String banco, String plaza, float monto, Date fechaCobro, Date fechaDePago) {
        if (numeroCheque == null || numeroCheque.isEmpty()) {
            throw new IllegalArgumentException("El número de cheque no puede ser nulo o vacío.");
        }
        this.numeroCheque = numeroCheque;
        this.banco = banco;
        this.plaza = plaza;
        this.monto = monto;
        this.fechaCobro = fechaCobro;
        this.fechaDePago = fechaDePago;
    }

    // Getters y Setters
    public String getNumeroCheque() {
        return numeroCheque;
    }
    public void setNumeroCheque(String numeroCheque) {
        this.numeroCheque = numeroCheque;
    }
    public String getBanco() {
        return banco;
    }
    public void setBanco(String banco) {
        this.banco = banco;
    }
    public String getPlaza() {
        return plaza;
    }
    public void setPlaza(String plaza) {
        this.plaza = plaza;
    }
    public float getMonto() {
        return monto;
    }
    public void setMonto(float monto) {
        this.monto = monto;
    }
    public Date getFechaCobro() {
        return fechaCobro;
    }
    public void setFechaCobro(Date fechaCobro) {
        this.fechaCobro = fechaCobro;
    }
    public Date getFechaDePago() {
        return fechaDePago;
    }
    public void setFechaDePago(Date fechaDePago) {
        this.fechaDePago = fechaDePago;
    }
}
