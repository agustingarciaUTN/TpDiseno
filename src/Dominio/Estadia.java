// src/Dominio/Estadia.java
package Dominio;

import java.util.Date;

public class Estadia {
    private int id_estadia;
    private Date fecha_inicio;
    private Date fecha_fin;
    private double valor_estadia;

    public Estadia() {
        // constructor por defecto
    }

    public Estadia(int id_estadia, Date fecha_inicio, Date fecha_fin, double valor_estadia) {
        this.id_estadia = id_estadia;
        this.fecha_inicio = (fecha_inicio == null) ? null : new Date(fecha_inicio.getTime());
        this.fecha_fin = (fecha_fin == null) ? null : new Date(fecha_fin.getTime());
        this.valor_estadia = valor_estadia;
    }

    public int getIdEstadia() {
        return id_estadia;
    }

    public void setIdEstadia(int id_estadia) {
        this.id_estadia = id_estadia;
    }

    public Date getFechaInicio() {
        return (fecha_inicio == null) ? null : new Date(fecha_inicio.getTime());
    }

    public void setFechaInicio(Date fecha_inicio) {
        this.fecha_inicio = (fecha_inicio == null) ? null : new Date(fecha_inicio.getTime());
    }

    public Date getFechaFin() {
        return (fecha_fin == null) ? null : new Date(fecha_fin.getTime());
    }

    public void setFechaFin(Date fecha_fin) {
        this.fecha_fin = (fecha_fin == null) ? null : new Date(fecha_fin.getTime());
    }

    public double getValorEstadia() {
        return valor_estadia;
    }

    public void setValorEstadia(double valor_estadia) {
        this.valor_estadia = valor_estadia;
    }
}
