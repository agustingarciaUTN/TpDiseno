package Facultad.TrabajoPracticoDesarrollo.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // Ignora todo menos el ID
public class DtoEstadiaSimple {
    private int idEstadia;

    public DtoEstadiaSimple(int idEstadia) {
        this.idEstadia = idEstadia;
    }
}