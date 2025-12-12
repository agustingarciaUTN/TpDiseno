package Facultad.TrabajoPracticoDesarrollo.DTOs;


import Facultad.TrabajoPracticoDesarrollo.enums.TipoFactura;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
public class DtoDetalleFacturacion {
    // Datos del Responsable
    private String nombreResponsable;
    private String cuitResponsable;

    // Desglose
    private double montoEstadiaBase;
    private double recargoHorario;
    private String detalleRecargo; // "50% Late Checkout"

    private List<DtoItemFactura> items;

    // Totales
    private double subtotal;
    private double montoIva;
    private double montoTotal; // A PAGAR

    private TipoFactura tipoFactura; // A o B

    @Data @AllArgsConstructor
    public static class DtoItemFactura {
        private String descripcion;
        private double importe;
    }
}
