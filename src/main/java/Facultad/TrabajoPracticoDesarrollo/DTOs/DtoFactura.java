package Facultad.TrabajoPracticoDesarrollo.DTOs;

import Facultad.TrabajoPracticoDesarrollo.enums.EstadoFactura;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoFactura;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoFactura {
  //  private int idFactura;

    public static final String REGEX_NUMERO = "^\\d{4}-\\d{8}$";

    @NotBlank
    //@Pattern(regexp = REGEX_NUMERO, message = "El formato debe ser XXXX-XXXXXXXX")
    @Size(min = 2, max = 20)
    private String numeroFactura;

    @NotNull
    //@PastOrPresent
    private Date fechaEmision;

    @NotNull
    //@FutureOrPresent
    private Date fechaVencimiento;

    private EstadoFactura estadoFactura;

    @NotNull
    private TipoFactura tipoFactura;

    @NotNull
    @PositiveOrZero
    private double importeTotal;

    @PositiveOrZero
    private double importeNeto;

    @PositiveOrZero
    private double iva;

    /*
    @NotNull
    @Valid
    private DtoEstadia dtoEstadia;
    */

    @NotNull
    private DtoEstadiaSimple idEstadia;

    @NotNull
    @Valid
    private DtoResponsableSimple idResponsable;

    @Valid
    private DtoNotaDeCredito dtoNotaDeCredito;

    // --- CONSTRUCTOR PRIVADO ---
    private DtoFactura(Builder builder) {
    //    this.idFactura = builder.idFactura;
        this.numeroFactura = builder.numeroFactura;
        this.fechaEmision = builder.fechaEmision;
        this.fechaVencimiento = builder.fechaVencimiento;
        this.estadoFactura = builder.estadoFactura;
        this.tipoFactura = builder.tipoFactura;
        this.importeTotal = builder.importeTotal;
        this.importeNeto = builder.importeNeto;
        this.iva = builder.iva;
        this.idEstadia = builder.idEstadia;
        this.idResponsable = builder.idResponsable;
        this.dtoNotaDeCredito = builder.dtoNotaDeCredito;
    }

    public DtoEstadiaSimple getIdEstadia() { return idEstadia; }
    public void setIdEstadia(DtoEstadiaSimple idEstadia) { this.idEstadia = idEstadia; }

    public DtoResponsableSimple getIdResponsable() { return idResponsable; }
    public void setIdResponsable(DtoResponsableSimple idResponsable) { this.idResponsable = idResponsable; }

    public DtoNotaDeCredito getIdNotaDeCredito() { return dtoNotaDeCredito; }
    public void setIdNotaDeCredito(DtoNotaDeCredito dtoNotaDeCredito) { this.dtoNotaDeCredito = dtoNotaDeCredito; }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
   //     private int idFactura;
        private String numeroFactura;
        private Date fechaEmision;
        private Date fechaVencimiento;
        private EstadoFactura estadoFactura;
        private TipoFactura tipoFactura;
        private double importeTotal;
        private double importeNeto;
        private double iva;
        private DtoEstadiaSimple idEstadia;
        private DtoResponsableSimple idResponsable;
        private DtoNotaDeCredito dtoNotaDeCredito;

        public Builder() {}

    //    public Builder idFactura(int val) { idFactura = val; return this; }
        public Builder numeroFactura(String val) { numeroFactura = val; return this; }
        public Builder fechaEmision(Date val) { fechaEmision = val; return this; }
        public Builder fechaVencimiento(Date val) { fechaVencimiento = val; return this; }
        public Builder estado(EstadoFactura val) { estadoFactura = val; return this; }
        public Builder tipo(TipoFactura val) { tipoFactura = val; return this; }
        public Builder importeTotal(double val) { importeTotal = val; return this; }
        public Builder importeNeto(double val) { importeNeto = val; return this; }
        public Builder iva(double val) { iva = val; return this; }

        public Builder idEstadia(DtoEstadiaSimple val) { idEstadia = val; return this; }
        public Builder idResponsable(DtoResponsableSimple val) { idResponsable = val; return this; }
        public Builder dtoNotaDeCredito(DtoNotaDeCredito val) { dtoNotaDeCredito = val; return this; }

        public DtoFactura build() {
            return new DtoFactura(this);
        }
    }
}