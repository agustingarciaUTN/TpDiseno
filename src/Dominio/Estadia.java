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
    private Reserva reserva;
    private Habitacion habitacion;
    private ArrayList<Huesped> huespedes;

    public Estadia() {
        // constructor por defecto
    }

    // Constructor Privado: Solo el Builder puede usarlo
    private Estadia(Builder builder) {
        this.idEstadia = builder.idEstadia;
        this.fechaCheckIn = builder.fechaCheckIn;
        this.fechaCheckOut = builder.fechaCheckOut;
        this.valorEstadia = builder.valorEstadia;
        this.reserva = builder.reserva;
        this.habitacion = builder.habitacion;
        this.huespedes = builder.huespedes;
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

    public void setReserva(Reserva reserva){this.reserva = reserva;}
    public Reserva getReserva(){return reserva;}

    public List<Huesped> getHuespedes(){return huespedes;}
    public void setHuespedes(ArrayList<Huesped> huespedes){this.huespedes = huespedes;}

    public void setHabitacion(Habitacion habitacion){this.habitacion = habitacion;}
    public Habitacion getHabitacion(){return habitacion;}


    // --- CLASE BUILDER ---
    public static class Builder {
        // Campos obligatorios o importantes
        private int idEstadia = 0; // Por defecto 0 (nuevo)
        private Date fechaCheckIn;
        private ArrayList<Huesped> huespedes = new ArrayList<>();

        // Campos opcionales
        private Date fechaCheckOut;
        private double valorEstadia;
        private Reserva reserva;
        private Habitacion habitacion;

        // Constructor con los datos MÍNIMOS para que una estadía tenga sentido
        public Builder(Date fechaCheckIn) {
            this.fechaCheckIn = fechaCheckIn;
        }

        // Métodos
        public Builder idEstadia(int val) { idEstadia = val; return this; }
        public Builder fechaCheckOut(Date val) { fechaCheckOut = val; return this; }
        public Builder valorEstadia(double val) { valorEstadia = val; return this; }
        public Builder reserva(Reserva val) { reserva = val; return this; }
        public Builder habitacion(Habitacion val) { habitacion = val; return this; }

        // Métodos para listas (puedes pasar la lista entera o agregar uno a uno)
        public Builder huespedes(ArrayList<Huesped> val) { huespedes = val; return this; }
        public void agregarHuesped(Huesped val) {
            if (this.huespedes == null) this.huespedes = new ArrayList<>();
            this.huespedes.add(val);
        }

        public Estadia build() {
            // Validaciones de Dominio antes de crear el objeto
            if (fechaCheckIn == null) {
                throw new IllegalArgumentException("La fecha de inicio no puede ser nula");
            }
            if (huespedes == null || huespedes.isEmpty()) {
                throw new IllegalArgumentException("La estadía debe tener al menos un huésped");
            }
            return new Estadia(this);
        }
    }

}
