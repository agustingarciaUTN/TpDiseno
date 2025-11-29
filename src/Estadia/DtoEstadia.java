package Estadia;

import Dominio.Huesped;
//import Reserva.DtoReserva;
import Huesped.DtoHuesped;

import java.util.Date;
import java.util.ArrayList;

public class DtoEstadia {
    private int idEstadia;
    private Date fechaCheckIn;
    private Date fechaCheckOut;
    private double valorEstadia;
    //private DtoReserva dtoRreserva; NO EXISTE TODAVIA
    private ArrayList<DtoHuesped> dtoHuespedes;
    //private ArrayList<DtoFactura> dtoFacturas; NO EXISTE TODAVIA
    //private DtoHabitacion dtoHabitacion; NO EXISTE TODAVIA


    public DtoEstadia() {
        // constructor por defecto
    }

    public DtoEstadia(int id_estadia, Date fecha_inicio, int id_reserva, Date fecha_fin, double valor_estadia, List<Huesped> huespedes) {
        if (fecha_inicio == null) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser nula");
        }
        if (id_estadia <= 0) {
            throw new IllegalArgumentException("El ID de estadía debe ser mayor a 0");
        }
        if (id_reserva <= 0) {
            throw new IllegalArgumentException("El ID de reserva debe ser mayor a 0");
        }
        if(huespedes == null){
            throw new IllegalArgumentException("La estadia debe tener asignada al menos un huesped.");
        }
        this.id_estadia = id_estadia;
        this.fecha_inicio = new Date(fecha_inicio.getTime());
        this.id_reserva = id_reserva;
        this.fechaCheckOut = (fecha_fin == null) ? null : new Date(fecha_fin.getTime());
        this.valorEstadia = valor_estadia;
        this.huespedes = huespedes;
    }


    public int getIdEstadia() {
        return id_estadia;
    }
    public void setIdEstadia(int id_estadia) {
        this.id_estadia = id_estadia;
    }
    public Date getFechaCheckIn() {
        return (fecha_inicio == null) ? null : new Date(fecha_inicio.getTime());
    }
    public void setFechaCheckIn(Date fecha_inicio) {
        this.fecha_inicio = (fecha_inicio == null) ? null : new Date(fecha_inicio.getTime());
    }
    public Date getFechaCheckOut() {
        return (fechaCheckOut == null) ? null : new Date(fechaCheckOut.getTime());
    }
    public void setFechaCheckOut(Date fecha_fin) {
        this.fechaCheckOut = (fecha_fin == null) ? null : new Date(fecha_fin.getTime());
    }
    public double getValorEstadia() {
        return valorEstadia;
    }
    public void setValorEstadia(double valor_estadia) {
        this.valorEstadia = valor_estadia;
    }
    public void setIdReserva(int id_reserva){this.id_reserva = id_reserva;}
    public int getIdReserva(){return id_reserva;}
    public List<Huesped> getHuespedes(){return huespedes;}
    public void setHuespedes(List<Huesped> huespedes){this.huespedes = huespedes;}

}