package Facultad.TrabajoPracticoDesarrollo.DTOs;


import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class DtoOcupantesHabitacion {

    @NotBlank
    private String numeroHabitacion;

    @NotNull
    @Positive
    private int idEstadia;

    @Valid
    private List<DtoDatosOcupantes> ocupantes;

    private DtoOcupantesHabitacion (Builder builder) {
        this.numeroHabitacion = builder.numeroHabitacion;
        this.idEstadia = builder.idEstadia;
        this.ocupantes = builder.ocupantes;
    }

    public static class Builder {
        private String numeroHabitacion;
        private int idEstadia;
        private List<DtoDatosOcupantes> ocupantes;

        public Builder () {}

        public Builder numeroHabitacion(String val) { numeroHabitacion = val; return this; }
        public Builder idEstadia(int val) { idEstadia = val; return this;}
        public Builder ocupantes(List<DtoDatosOcupantes> val) { ocupantes = val; return this; }

        public DtoOcupantesHabitacion build() {
            return new DtoOcupantesHabitacion(this);
        }
    }

}
