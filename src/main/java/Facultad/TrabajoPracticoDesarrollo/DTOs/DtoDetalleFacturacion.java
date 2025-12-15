package Facultad.TrabajoPracticoDesarrollo.DTOs;


import Facultad.TrabajoPracticoDesarrollo.Dominio.Estadia;
import Facultad.TrabajoPracticoDesarrollo.Dominio.ServiciosAdicionales;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoFactura;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
public class DtoDetalleFacturacion {

    public static final String REGEX_CUIT = "^\\d{2}-?\\d{8}-?\\d{1}$";

    // Datos del Responsable
    @NotNull
    @Positive
    private int idResponsable;

    @NotBlank
    @Size(min = 2, max = 200)
    private String nombreResponsable;

    @Pattern(regexp = REGEX_CUIT, message = "El CUIT debe tener 11 dígitos (con o sin guiones)")
    private String cuitResponsable;

    // Desglose
    @NotNull
    @Positive
    private double montoEstadiaBase;

    @Positive
    private double recargoHorario;

    private String detalleRecargo; // "50% Late Checkout"

    @Valid
    private List<DtoServiciosAdicionales> serviciosAdicionales;

    // Totales
    @NotNull
    @Positive
    private double subtotal;

    @Positive
    private double montoIva;

    @NotNull
    @Positive
    private double montoTotal; // A PAGAR

    private TipoFactura tipoFactura; // A o B

    private DtoDetalleFacturacion(Builder builder){
        this.idResponsable = builder.idResponsable;
        this.nombreResponsable = builder.nombreResponsable;
        this.cuitResponsable = builder.cuitResponsable;
        this.montoEstadiaBase = builder.montoEstadiaBase;
        this.recargoHorario = builder.recargoHorario;
        this.detalleRecargo = builder.detalleRecargo;
        this.serviciosAdicionales = builder.serviciosAdicionales;
        this.subtotal = builder.subtotal;
        this.montoIva = builder.montoIva;
        this.montoTotal = builder.montoTotal;
        this.tipoFactura = builder.tipoFactura;
    }

    public static class Builder {
        private int idResponsable;
        private String nombreResponsable;
        private String cuitResponsable;
        private double montoEstadiaBase;
        private double recargoHorario;
        private String detalleRecargo;
        private List<DtoServiciosAdicionales> serviciosAdicionales;
        private double subtotal;
        private double montoIva;
        private double montoTotal;
        private TipoFactura tipoFactura;

        public Builder () {}

        public Builder idResponsable(int val) { idResponsable = val; return this; }
        public Builder nombreResponsable(String val) { nombreResponsable = val; return this;}
        public Builder cuitResponsable(String val) { cuitResponsable = val; return this; }
        public Builder montoEstadiaBase(double val) { montoEstadiaBase = val; return this; }
        public Builder recargoHorario(double val) { recargoHorario = val; return this; }
        public Builder detalleRecargo(String val) { detalleRecargo = val; return this; }
        public Builder serviciosAdicionales(List<DtoServiciosAdicionales> val) { serviciosAdicionales = val; return this; }
        public Builder subtotal(double val) { subtotal = val; return this; }
        public Builder montoIva(double val) { montoIva = val; return this; }
        public Builder montoTotal(double val) { montoTotal = val; return this; }
        public Builder tipoFactura(TipoFactura val) { tipoFactura = val; return this; }

        public DtoDetalleFacturacion build() {
            return new DtoDetalleFacturacion(this);
        }
    }

}
