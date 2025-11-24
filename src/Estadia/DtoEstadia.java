package Estadia;

import java.util.Date;

public class DtoEstadia {
    private int id_estadia;
    private Date fecha_inicio;
    private Date fecha_fin;
    private double valor_estadia;
    private int id_reserva;

    public DtoEstadia() {
        // constructor por defecto
    }

    public DtoEstadia(int id_estadia, Date fecha_inicio, int id_reserva, Date fecha_fin, double valor_estadia) {
        if (fecha_inicio == null) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser nula");
        }
        if (id_estadia <= 0) {
            throw new IllegalArgumentException("El ID de estadía debe ser mayor a 0");
        }
        if (id_reserva <= 0) {
            throw new IllegalArgumentException("El ID de reserva debe ser mayor a 0");
        }

        this.id_estadia = id_estadia;
        this.fecha_inicio = new Date(fecha_inicio.getTime());
        this.id_reserva = id_reserva;
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
    public void setIdReserva(int id_reserva){this.id_reserva = id_reserva;}
    public int getIdReserva(){return id_reserva;}
}