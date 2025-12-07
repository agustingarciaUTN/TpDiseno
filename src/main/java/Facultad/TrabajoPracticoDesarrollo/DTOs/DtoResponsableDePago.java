package Facultad.TrabajoPracticoDesarrollo.DTOs;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder // Usamos SuperBuilder para que la herencia funcione
@AllArgsConstructor
// Configuración para que el JSON sepa qué hijo es
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type" // Campo discriminador en el JSON
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DtoPersonaFisica.class, name = "fisica"),
        @JsonSubTypes.Type(value = DtoPersonaJuridica.class, name = "juridica")
})
public abstract class DtoResponsableDePago {
    private Long idResponsable;
}