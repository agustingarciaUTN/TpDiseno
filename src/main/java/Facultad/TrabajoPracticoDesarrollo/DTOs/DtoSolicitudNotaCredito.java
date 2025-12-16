package Facultad.TrabajoPracticoDesarrollo.DTOs;

import lombok.Data;
import java.util.List;

@Data
public class DtoSolicitudNotaCredito {
    private Long idResponsable;
    private List<Long> idsFacturasACancelar;
}