package Facultad.TrabajoPracticoDesarrollo.DTOs;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;

@Data
@AllArgsConstructor
public class    DtoEstadia {


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


    // No validar @Valid porque solo enviamos tipoDocumento y nroDocumento (referencia al huésped existente)
    @NotEmpty(message = "Debe haber al menos un huésped asociado a la estadía")
    private ArrayList<DtoHuesped> dtoHuespedes;

    @NotNull(message = "La habitación es obligatoria")
    private DtoHabitacion dtoHabitacion;

    // Constructor público sin argumentos para Jackson
    public DtoEstadia() {
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

    public ArrayList<DtoHuesped> getDtoHuespedes() { return dtoHuespedes; }
    
    public void setDtoHuespedes(ArrayList<DtoHuesped> dtoHuespedes) { this.dtoHuespedes = dtoHuespedes; }

    public DtoHabitacion getDtoHabitacion(){return dtoHabitacion;}


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