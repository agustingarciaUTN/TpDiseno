package Estadia;



import Habitacion.DtoHabitacion;
import Huesped.DtoHuesped;
import Reserva.DtoReserva;


import java.util.Date;
import java.util.ArrayList;

public class DtoEstadia {
    private int idEstadia;
    private Date fechaCheckIn;
    private Date fechaCheckOut;
    private double valorEstadia;
    private DtoReserva dtoReserva;
    private ArrayList<DtoHuesped> dtoHuespedes;
    private DtoHabitacion dtoHabitacion;


    public DtoEstadia() {
        // constructor por defecto
    }
    private DtoEstadia(Builder builder) {
        this.idEstadia = builder.idEstadia;
        this.fechaCheckIn = builder.fechaCheckIn;
        this.fechaCheckOut = builder.fechaCheckOut;
        this.valorEstadia = builder.valorEstadia;
        this.dtoReserva = builder.dtoReserva;
        this.dtoHuespedes = builder.dtoHuespedes;
    }
    /*public DtoEstadia(int id_estadia, Date fecha_inicio, int id_reserva, Date fecha_fin, double valor_estadia, List<Huesped> huespedes) {
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
    }*/


    public int getIdEstadia() { return idEstadia; }
    public void setIdEstadia(int idEstadia) { this.idEstadia = idEstadia; }

    public Date getFechaCheckIn() { return fechaCheckIn; }
    public void setFechaCheckIn(Date fechaCheckIn) { this.fechaCheckIn = fechaCheckIn; }

    public Date getFechaCheckOut() { return fechaCheckOut; }
    public void setFechaCheckOut(Date fechaCheckOut) { this.fechaCheckOut = fechaCheckOut; }

    public double getValorEstadia() { return valorEstadia; }
    public void setValorEstadia(double valorEstadia) { this.valorEstadia = valorEstadia; }

    public DtoReserva getDtoReserva() { return dtoReserva; }
    public void setDtoReserva(DtoReserva dtoReserva) { this.dtoReserva = dtoReserva; }

    public ArrayList<DtoHuesped> getDtoHuespedes() { return dtoHuespedes; }
    public void setDtoHuespedes(ArrayList<DtoHuesped> dtoHuespedes) { this.dtoHuespedes = dtoHuespedes; }


    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private int idEstadia;
        private Date fechaCheckIn;
        private Date fechaCheckOut;
        private double valorEstadia;
        private DtoReserva dtoReserva;
        private ArrayList<DtoHuesped> dtoHuespedes = new ArrayList<>();

        public Builder() {
            // Constructor vacío
        }

        public Builder idEstadia(int val) {
            this.idEstadia = val;
            return this;
        }

        public Builder fechaCheckIn(Date val) {
            this.fechaCheckIn = val;
            return this;
        }

        public Builder fechaCheckOut(Date val) {
            this.fechaCheckOut = val;
            return this;
        }

        public Builder valorEstadia(double val) {
            this.valorEstadia = val;
            return this;
        }

        public Builder dtoReserva(DtoReserva val) {
            this.dtoReserva = val;
            return this;
        }

        public Builder dtoHuespedes(ArrayList<DtoHuesped> val) {
            this.dtoHuespedes = val;
            return this;
        }

        // Helper para agregar huéspedes de a uno
        public Builder agregarHuesped(DtoHuesped val) {
            if (this.dtoHuespedes == null) {
                this.dtoHuespedes = new ArrayList<>();
            }
            this.dtoHuespedes.add(val);
            return this;
        }

        /*public Builder facturas(ArrayList<DtoFactura> val) {
            this.facturas = val;
            return this;
        }*/

        public DtoEstadia build() {
            return new DtoEstadia(this);
        }
    }


}