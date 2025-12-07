package Facultad.TrabajoPracticoDesarrollo.Estadia;

import Facultad.TrabajoPracticoDesarrollo.enums.TipoServicio;
import lombok.Data;

import java.util.Date;

@Data
public class DtoServiciosAdicionales {
    // --- GETTERS Y SETTERS ---
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