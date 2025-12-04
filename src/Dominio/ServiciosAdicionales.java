package Dominio;

import java.util.Date;
import enums.TipoServicio;

public class ServiciosAdicionales {

    private int idServicio; // PK autogenerada
    private TipoServicio tipoServicio;
    private String descripcionServicio;
    private double valorServicio; // double por consistencia con moneda
    private Date fechaConsumo;

    // Relación
    private Estadia estadia;


    // --- CONSTRUCTOR PRIVADO ---
    private ServiciosAdicionales(Builder builder) {
        this.idServicio = builder.idServicio;
        this.tipoServicio = builder.tipoServicio;
        this.descripcionServicio = builder.descripcionServicio;
        this.valorServicio = builder.valorServicio;
        this.fechaConsumo = builder.fechaConsumo;
        this.estadia = builder.estadia;
    }

    public ServiciosAdicionales() {}

    // --- GETTERS Y SETTERS ---
    public int getIdServicio() { return idServicio; }
    public void setIdServicio(int idServicio) { this.idServicio = idServicio; }

    public TipoServicio getTipoServicio() { return tipoServicio; }
    public void setTipoServicio(TipoServicio tipoServicio) { this.tipoServicio = tipoServicio; }

    public String getDescripcionServicio() { return descripcionServicio; }
    public void setDescripcionServicio(String descripcionServicio) { this.descripcionServicio = descripcionServicio; }

    public double getValorServicio() { return valorServicio; }
    public void setValorServicio(double valorServicio) { this.valorServicio = valorServicio; }

    public Date getFechaConsumo() { return fechaConsumo; }
    public void setFechaConsumo(Date fechaConsumo) { this.fechaConsumo = fechaConsumo; }

    public Estadia getEstadia() { return estadia; }
    public void setEstadia(Estadia estadia) {
        this.estadia = estadia;
    }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private int idServicio = 0;
        private TipoServicio tipoServicio;
        private String descripcionServicio;
        private double valorServicio;
        private Date fechaConsumo;
        private Estadia estadia;

        // Constructor con obligatorios
        public Builder(TipoServicio tipo, double valor, Date fecha) {
            this.tipoServicio = tipo;
            this.valorServicio = valor;
            this.fechaConsumo = fecha;
        }

        public Builder id(int val) { idServicio = val; return this; }
        public Builder descripcion(String val) { descripcionServicio = val; return this; }
        public Builder estadia(Estadia val) {
            estadia = val;
            return this;
        }

        public ServiciosAdicionales build() {
            if (valorServicio < 0) {
                throw new IllegalArgumentException("El valor del servicio no puede ser negativo.");
            }
            if (fechaConsumo == null) {
                throw new IllegalArgumentException("La fecha de consumo es obligatoria.");
            }
            return new ServiciosAdicionales(this);
        }
    }
}