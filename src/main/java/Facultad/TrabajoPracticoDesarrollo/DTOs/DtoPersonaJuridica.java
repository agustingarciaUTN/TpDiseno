package Facultad.TrabajoPracticoDesarrollo.DTOs;


import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.AllArgsConstructor;

import java.util.List;


@Data
@SuperBuilder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DtoPersonaJuridica extends DtoResponsableDePago {

    public static final String REGEX_CUIT = "^\\d{2}-?\\d{8}-?\\d{1}$";

    @NotNull
    @Positive
    private int idResponsablePago;

    @NotBlank
    @Size(min = 2, max = 200)
    private String razonSocial;

    @Pattern(regexp = REGEX_CUIT, message = "El CUIT debe tener 11 dígitos (con o sin guiones)")
    private String cuit;

    @NotEmpty
    private List<@NotNull Long> telefono;

    @Valid
    private DtoDireccion dtoDireccion;


    // --- CONSTRUCTOR PRIVADO ---
    private DtoPersonaJuridica(Builder builder) {
        this.idResponsablePago = builder.idResponsablePago;
        this.razonSocial = builder.razonSocial;
        this.cuit = builder.cuit;
        this.telefono = builder.telefono;
        this.dtoDireccion = builder.dtoDireccion;
    }


    public DtoDireccion getDireccion() { return dtoDireccion; }
    public void setDireccion(DtoDireccion dtoDireccion) { this.dtoDireccion = dtoDireccion; }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private int idResponsablePago;
        private String razonSocial;
        private String cuit;
        private List<Long> telefono;
        private DtoDireccion dtoDireccion;

        public Builder() {}

        public Builder id(int val) { idResponsablePago = val; return this; }
        public Builder razonSocial(String val) { razonSocial = val; return this; }
        public Builder cuit(String val) { cuit = val; return this; }
        public Builder telefono(List<Long> val) { telefono = val; return this; }
        public Builder dtoDireccion(DtoDireccion val) { dtoDireccion = val; return this; }

        public DtoPersonaJuridica build() {
            return new DtoPersonaJuridica(this);
        }
    }
}