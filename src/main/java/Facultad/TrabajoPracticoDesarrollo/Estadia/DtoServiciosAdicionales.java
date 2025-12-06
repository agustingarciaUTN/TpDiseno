package Facultad.TrabajoPracticoDesarrollo.Estadia;

import Facultad.TrabajoPracticoDesarrollo.enums.TipoServicio;

import java.util.Date;

public class DtoServiciosAdicionales {
    private int idServicio;
    private TipoServicio tipoServicio;
    private String descripcionServicio;
    private double valorServicio;
    private Date fechaConsumo;
    private int idEstadia; // Solo ID para el DTO

    // --- CONSTRUCTOR PRIVADO ---
    private DtoServiciosAdicionales(Builder builder) {
        this.idServicio = builder.idServicio;
        this.tipoServicio = builder.tipoServicio;
        this.descripcionServicio = builder.descripcionServicio;
        this.valorServicio = builder.valorServicio;
        this.fechaConsumo = builder.fechaConsumo;
        this.idEstadia = builder.idEstadia;
    }

    public DtoServiciosAdicionales() {}

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

    public int getIdEstadia() { return idEstadia; }
    public void setIdEstadia(int idEstadia) { this.idEstadia = idEstadia; }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private int idServicio;
        private TipoServicio tipoServicio;
        private String descripcionServicio;
        private double valorServicio;
        private Date fechaConsumo;
        private int idEstadia;

        public Builder() {}

        public Builder id(int val) { idServicio = val; return this; }
        public Builder tipo(TipoServicio val) { tipoServicio = val; return this; }
        public Builder descripcion(String val) { descripcionServicio = val; return this; }
        public Builder valor(double val) { valorServicio = val; return this; }
        public Builder fecha(Date val) { fechaConsumo = val; return this; }
        public Builder idEstadia(int val) { idEstadia = val; return this; }

        public DtoServiciosAdicionales build() {
            return new DtoServiciosAdicionales(this);
        }
    }
}