package Facultad.TrabajoPracticoDesarrollo.DTOs;

import Facultad.TrabajoPracticoDesarrollo.enums.Moneda;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@SuperBuilder
@NoArgsConstructor
// Configuración para que el JSON sepa qué hijo es
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "tipoMedio" // Campo discriminador en el JSON (ej: "tipoMedio": "EFECTIVO")
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DtoEfectivo.class, name = "EFECTIVO"),
        @JsonSubTypes.Type(value = DtoTarjetaDebito.class, name = "TARJETA_DEBITO"),
        @JsonSubTypes.Type(value = DtoTarjetaCredito.class, name = "TARJETA_CREDITO"),
        @JsonSubTypes.Type(value = DtoCheque.class, name = "CHEQUE")
})
public abstract class DtoMedioPago {
    // Atributos comunes a todos los medios

    protected int idPago;
    p// ID de referencia al pago padre
}