package Facultad.TrabajoPracticoDesarrollo.DTOs;

import Facultad.TrabajoPracticoDesarrollo.enums.TipoServicio;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Date;

@Data
public class DtoServiciosAdicionales {

    @NotNull
    @Positive
    private int idServicio;


    private TipoServicio tipoServicio;


    private String descripcion;

    @NotNull
    @Positive
    private double valor;

    @PastOrPresent
    private Date fechaConsumo;

    @NotNull
    private int idEstadia; // Solo ID para el DTO

    // --- CONSTRUCTOR PRIVADO ---
    private DtoServiciosAdicionales(Builder builder) {
        this.idServicio = builder.idServicio;
        this.tipoServicio = builder.tipoServicio;
        this.descripcion = builder.descripcion;
        this.valor = builder.valor;
        this.fechaConsumo = builder.fechaConsumo;
        this.idEstadia = builder.idEstadia;
    }


    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private int idServicio;
        private TipoServicio tipoServicio;
        private String descripcion;
        private double valor;
        private Date fechaConsumo;
        private int idEstadia;

        public Builder() {}

        public Builder id(int val) { idServicio = val; return this; }
        public Builder tipo(TipoServicio val) { tipoServicio = val; return this; }
        public Builder descripcion(String val) { descripcion = val; return this; }
        public Builder valor(double val) { valor = val; return this; }
        public Builder fecha(Date val) { fechaConsumo = val; return this; }
        public Builder idEstadia(int val) { idEstadia = val; return this; }

        public DtoServiciosAdicionales build() {
            return new DtoServiciosAdicionales(this);
        }
    }
}