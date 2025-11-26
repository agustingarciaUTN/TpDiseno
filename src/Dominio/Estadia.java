// src/Dominio/Estadia.java
package Dominio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Estadia {
    private int idEstadia;
    private Date fechaCheckIn;
    private Date fechaCheckOut;
    private double valorEstadia;
    private int idReserva;// CUAL DE LOS DOS
    private Reserva reserva; // CUAL DE LOS DOS
    private List<Huesped> huespedes;
    private ArrayList<Factura> facturas;

    public Estadia() {
        // constructor por defecto
    }
    /**
     * Constructor completo con validaciones
     * @param idEstadia ID de la estadía (obligatorio)
     * @param fechaCheckIn Fecha de inicio (obligatoria)
     * @param idReserva ID de la reserva (obligatorio)
     * @param fechaCheckOut Fecha de fin (opcional)
     * @param valorEstadia Valor de la estadía (opcional)
     */
    public Estadia(int idEstadia, Date fechaCheckIn, int idReserva, Date fechaCheckOut, double valorEstadia, List<Huesped> huespedes) {
        if (fechaCheckIn == null) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser nula");
        }
        if (idEstadia <= 0) {
            throw new IllegalArgumentException("El ID de estadía debe ser mayor a 0");
        }
        if (idReserva <= 0) {
            throw new IllegalArgumentException("El ID de reserva debe ser mayor a 0");
        }
        if(huespedes == null){
            throw new IllegalArgumentException("La estadia debe tener asignada al menos un huesped.");
        }
        this.idEstadia = idEstadia;
        this.fechaCheckIn = new Date(fechaCheckIn.getTime());
        this.idReserva = idReserva;
        this.fechaCheckOut = (fechaCheckOut == null) ? null : new Date(fechaCheckOut.getTime());
        this.valorEstadia = valorEstadia;
        this.huespedes = huespedes;
    }

    public int getIdEstadia() {
        return idEstadia;
    }
    public void setIdEstadia(int idEstadia) {
        this.idEstadia = idEstadia;
    }
    public Date getFechaCheckIn() {
        return (fechaCheckIn == null) ? null : new Date(fechaCheckIn.getTime());
    }
    public void setFechaCheckIn(Date fechaCheckIn) {
        this.fechaCheckIn = (fechaCheckIn == null) ? null : new Date(fechaCheckIn.getTime());
    }
    public Date getFechaCheckOut() {
        return (fechaCheckOut == null) ? null : new Date(fechaCheckOut.getTime());
    }
    public void setFechaCheckOut(Date fechaCheckOut) {
        this.fechaCheckOut = (fechaCheckOut == null) ? null : new Date(fechaCheckOut.getTime());
    }
    public double getValorEstadia() {
        return valorEstadia;
    }
    public void setValorEstadia(double valorEstadia) {
        this.valorEstadia = valorEstadia;
    }
    public void setIdReserva(int idReserva){this.idReserva = idReserva;}
    public int getIdReserva(){return idReserva;}
    public List<Huesped> getHuespedes(){return huespedes;}
    public void setHuespedes(List<Huesped> huespedes){this.huespedes = huespedes;}
}
