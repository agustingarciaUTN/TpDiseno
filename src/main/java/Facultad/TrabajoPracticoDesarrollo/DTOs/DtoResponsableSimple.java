package Facultad.TrabajoPracticoDesarrollo.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DtoResponsableSimple {
    private Integer idResponsable;
    private String nombreCompleto;

}
