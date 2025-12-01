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
        this.dtoHabitacion = builder.dtoHabitacion;
    }


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

    public DtoHabitacion getDtoHabitacion(){return dtoHabitacion;}
    public void setDtoHabitacion(DtoHabitacion dtoHabitacion){this.dtoHabitacion = dtoHabitacion;}


    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private int idEstadia;
        private Date fechaCheckIn;
        private Date fechaCheckOut;
        private double valorEstadia;
        private DtoReserva dtoReserva;
        private ArrayList<DtoHuesped> dtoHuespedes = new ArrayList<>();
        private DtoHabitacion dtoHabitacion;

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

        public Builder dtoHabitacion(DtoHabitacion val){this.dtoHabitacion = val; return this;}

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