package Facultad.TrabajoPracticoDesarrollo.DTOs;


import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class DtoOcupantesHabitacion {
    private String numeroHabitacion;
    private int idEstadia;
    private List<DtoHuespedResumen> ocupantes;

    @Data
    @AllArgsConstructor
    public static class DtoHuespedResumen {
        private TipoDocumento tipoDocumento;
        private String nroDocumento;
        private String nombre;
        private String apellido;

        // Helper para mostrar "DNI: 12345" en el front (opcional)
        public String getDocumentoVisual() {
            return tipoDocumento + " " + nroDocumento;
        }
    }
}
