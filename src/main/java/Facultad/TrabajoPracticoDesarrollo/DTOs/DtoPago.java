package Facultad.TrabajoPracticoDesarrollo.DTOs;

import Facultad.TrabajoPracticoDesarrollo.enums.Moneda;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoPago {

    // ID solo para respuestas, no se envía en el request
    private Integer idPago;

    @NotNull(message = "La moneda es obligatoria")
    private Moneda moneda;

    @NotNull(message = "El monto total es obligatorio")
    @Positive(message = "El monto total debe ser positivo")
    private Double montoTotal;

    @PositiveOrZero(message = "La cotización debe ser mayor o igual a cero")
    private Double cotizacion;

    @NotNull(message = "La fecha de pago es obligatoria")
    @PastOrPresent(message = "La fecha de pago no puede ser futura")
    private Date fechaPago;

    // Número de factura en lugar de la entidad completa (como en la BD)
    @NotBlank(message = "El número de factura es obligatorio")
    private String numeroFactura;

    // Lista completa de medios de pago con sus datos (no solo IDs)
    @NotNull(message = "Debe proporcionar al menos un medio de pago")
    @Valid
    private List<DtoMedioPago> mediosPago;

    // --- CONSTRUCTOR PRIVADO ---
    private DtoPago(Builder builder) {
        this.idPago = builder.idPago;
        this.moneda = builder.moneda;
        this.montoTotal = builder.montoTotal;
        this.cotizacion = builder.cotizacion;
        this.fechaPago = builder.fechaPago;
        this.numeroFactura = builder.numeroFactura;
        this.mediosPago = builder.mediosPago;
    }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private Integer idPago;
        private Moneda moneda;
        private Double montoTotal;
        private Double cotizacion;
        private Date fechaPago;
        private String numeroFactura;
        private List<DtoMedioPago> mediosPago = new ArrayList<>();

        public Builder() {}

        public Builder idPago(Integer val) { idPago = val; return this; }
        public Builder moneda(Moneda val) { moneda = val; return this; }
        public Builder montoTotal(Double val) { montoTotal = val; return this; }
        public Builder cotizacion(Double val) { cotizacion = val; return this; }
        public Builder fechaPago(Date val) { fechaPago = val; return this; }
        public Builder numeroFactura(String val) { numeroFactura = val; return this; }
        public Builder mediosPago(List<DtoMedioPago> val) { mediosPago = val; return this; }
        public Builder agregarMedioPago(DtoMedioPago val) {
            if (this.mediosPago == null) this.mediosPago = new ArrayList<>();
            this.mediosPago.add(val);
            return this;
        }

        public DtoPago build() {
            return new DtoPago(this);
        }
    }
}