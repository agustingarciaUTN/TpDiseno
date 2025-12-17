package Facultad.TrabajoPracticoDesarrollo.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // <--- ¡MAGIA! Ignora lo que no sea el ID (nombre, tipo, etc.)
public class DtoResponsableSimple {
    private Integer idResponsable;
}