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
    public void setHuespedes(List<Huesped> huespedes){this.huespedes = huespedes;}
    public ArrayList<Factura> getFacturas(){return facturas;}
    public void setFacturas(ArrayList<Factura> facturas){this.facturas = facturas;}
    public void setHabitacion(Habitacion habitacion){this.habitacion = habitacion;}
    public Habitacion getHabitacion(){return habitacion;}


    // --- CLASE BUILDER ---
    public static class Builder {
        private String nombres;
        private String apellido;
        private TipoDocumento tipoDocumento;
        private long documento;
        // Valores por defecto o opcionales
        private long telefono;
        private String cuit;
        private PosIva posicionIva;
        private Date fechaNacimiento;
        private String email;
        private String ocupacion;
        private String nacionalidad;
        private Direccion direccion;
        private List<Estadia> estadias;

        // Constructor del Builder con los datos OBLIGATORIOS (mínimos para existir)
        public Builder(String nombres, String apellido, TipoDocumento tipoDocumento, long documento) {
            this.nombres = nombres;
            this.apellido = apellido;
            this.tipoDocumento = tipoDocumento;
            this.documento = documento;
        }

        // Métodos fluidos para el resto
        public Builder telefono(long val) { telefono = val; return this; }
        public Builder cuit(String val) { cuit = val; return this; }
        public Builder posicionIva(PosIva val) { posicionIva = val; return this; }
        public Builder fechaNacimiento(Date val) { fechaNacimiento = val; return this; }
        public Builder email(String val) { email = val; return this; }
        public Builder ocupacion(String val) { ocupacion = val; return this; }
        public Builder nacionalidad(String val) { nacionalidad = val; return this; }
        public Builder direccion(Direccion val) { direccion = val; return this; }
        public Builder estadias(List<Estadia> val) { estadias = val; return this; }

        public Huesped build() {
            return new Huesped(this);
        }
    }



}
