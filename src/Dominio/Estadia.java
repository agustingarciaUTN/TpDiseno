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
    private ArrayList<Factura> facturas;

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
        this.facturas = builder.facturas;
    }

    /*

    public Estadia(int idEstadia, Date fechaCheckIn, Reserva reserva, Date fechaCheckOut, double valorEstadia, List<Huesped> huespedes, ArrayList<Factura> facturas) {
        if (fechaCheckIn == null) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser nula");
        }
        if (idEstadia <= 0) {
            throw new IllegalArgumentException("El ID de estadía debe ser mayor a 0");
        }
        if(huespedes == null){
            throw new IllegalArgumentException("La estadia debe tener asignada al menos un huesped.");
        }
        if(facturas == null){
            throw new IllegalArgumentException("La estadia debe tener asignada al menos una factura.");
        }
        this.facturas = facturas;
        this.idEstadia = idEstadia;
        this.fechaCheckIn = new Date(fechaCheckIn.getTime());
        this.reserva = reserva;
        this.fechaCheckOut = (fechaCheckOut == null) ? null : new Date(fechaCheckOut.getTime());
        this.valorEstadia = valorEstadia;
        this.huespedes = huespedes;
    }*/

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

    public ArrayList<Factura> getFacturas(){return facturas;}
    public void setFacturas(ArrayList<Factura> facturas){this.facturas = facturas;}

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
        private ArrayList<Factura> facturas = new ArrayList<>();

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
        public Builder agregarHuesped(Huesped val) {
            if (this.huespedes == null) this.huespedes = new ArrayList<>();
            this.huespedes.add(val);
            return this;
        }

        public Builder facturas(ArrayList<Factura> val) { facturas = val; return this; }
        public Builder agregarFactura(Factura val) {
            if (this.facturas == null) this.facturas = new ArrayList<>();
            this.facturas.add(val);
            return this;
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
