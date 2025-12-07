package Facultad.TrabajoPracticoDesarrollo.Estadia;


import Facultad.TrabajoPracticoDesarrollo.Habitacion.DtoHabitacion;
import Facultad.TrabajoPracticoDesarrollo.Huesped.DtoHuesped;
import Facultad.TrabajoPracticoDesarrollo.Reserva.DtoReserva;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.ArrayList;
import java.util.Date;

public class DtoEstadia {


    private int idEstadia;

    @NotNull(message = "La fecha de Check-In es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "America/Argentina/Buenos_Aires")
    private Date fechaCheckIn;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "America/Argentina/Buenos_Aires")
    private Date fechaCheckOut;

    @Positive(message = "El valor de la estadía debe ser mayor a cero")
    private double valorEstadia;

    // Puede ser Null si hace una estadia sin reservar
    private DtoReserva dtoReserva;

    @Valid // Valida cada huésped de la lista
    @NotEmpty(message = "Debe haber al menos un huésped asociado a la estadía")
    private ArrayList<DtoHuesped> dtoHuespedes;

    @NotNull(message = "La habitación es obligatoria")
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

        public void dtoReserva(DtoReserva val) {
            this.dtoReserva = val;
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

        public DtoEstadia build() {
            return new DtoEstadia(this);
        }
    }


}