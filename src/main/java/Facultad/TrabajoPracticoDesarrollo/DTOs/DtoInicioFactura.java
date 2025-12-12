package Facultad.TrabajoPracticoDesarrollo.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DtoInicioFactura {
    @NotBlank(message = "El número de habitación es obligatorio")
    private String numeroHabitacion;

    @NotBlank(message = "La hora de salida es obligatoria")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "La hora debe tener formato HH:mm (ej: 10:30)")
    private String horaSalida;
}
