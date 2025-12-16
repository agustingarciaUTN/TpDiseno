package Facultad.TrabajoPracticoDesarrollo.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoResultadoRegistroPago {
    private String mensaje;
    private Double vuelto;
    private Double saldoPendiente; // Monto que aún falta pagar
    private String numeroFactura;
    private String estadoFactura;
    private String estadoHabitacion; // Si se actualizó el estado de la habitación
    private boolean facturaSaldada;
}
