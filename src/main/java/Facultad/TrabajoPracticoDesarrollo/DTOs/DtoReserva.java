package Facultad.TrabajoPracticoDesarrollo.DTOs;

import Facultad.TrabajoPracticoDesarrollo.enums.EstadoReserva;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Date;

@Data
public class DtoReserva {
    // --- CONSTANTES (Las mismas que en DtoHuesped para consistencia) ---
    public static final String REGEX_NOMBRE = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$";
    public static final String REGEX_TELEFONO = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$";
    public static final String REGEX_HABITACION = "^[0-9]{1,3}$";

    //ATRIBUTOS

    @NotNull
    @Positive
    private int idReserva;

    @NotNull
    private EstadoReserva estadoReserva;

    @PastOrPresent
    private Date fechaReserva;

    @NotNull(message = "La fecha de ingreso es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "America/Argentina/Buenos_Aires")
    private Date fechaDesde;

    @NotNull(message = "La fecha de egreso es obligatoria")
    @Future(message = "La fecha de egreso debe ser futura")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "America/Argentina/Buenos_Aires")
    private Date fechaHasta;

    @NotBlank(message = "El nombre del responsable es obligatorio")
    @Pattern(regexp = REGEX_NOMBRE, message = "El nombre solo puede contener letras")
    private String nombreHuespedResponsable;

    @NotBlank(message = "El apellido del responsable es obligatorio")
    @Pattern(regexp = REGEX_NOMBRE, message = "El apellido solo puede contener letras")
    private String apellidoHuespedResponsable;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = REGEX_TELEFONO, message = "El formato del teléfono no es válido")
    private String telefonoHuespedResponsable;


    // Documento responsable
    @NotNull(message = "El tipo de documento es obligatorio")
    private TipoDocumento tipoDocumentoResponsable;

    @NotBlank(message = "El número de documento es obligatorio")
    private String nroDocumentoResponsable;

    // Validación de la Habitación
    @NotBlank(message = "El número de habitación es obligatorio")
    @Pattern(regexp = REGEX_HABITACION, message = "El número de habitación debe ser numérico y estar entre 1 y 999.")
    private String idHabitacion;



    // --- CONSTRUCTOR VACÍO PARA JACKSON ---
    public DtoReserva() {}

    // --- CONSTRUCTOR PRIVADO PARA BUILDER ---
    private DtoReserva(Builder builder) {
        this.idReserva = builder.idReserva;
        this.estadoReserva = builder.estadoReserva;
        this.fechaReserva = builder.fechaReserva;
        this.fechaDesde = builder.fechaDesde;
        this.fechaHasta = builder.fechaHasta;
        this.nombreHuespedResponsable = builder.nombreHuespedResponsable;
        this.apellidoHuespedResponsable = builder.apellidoHuespedResponsable;
        this.telefonoHuespedResponsable = builder.telefonoHuespedResponsable;
        this.tipoDocumentoResponsable = builder.tipoDocumentoResponsable;
        this.nroDocumentoResponsable = builder.nroDocumentoResponsable;
        this.idHabitacion = builder.idHabitacion;
    }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private int idReserva;
        private EstadoReserva estadoReserva;
        private Date fechaReserva;
        private Date fechaDesde;
        private Date fechaHasta;
        private String nombreHuespedResponsable;
        private String apellidoHuespedResponsable;
        private String telefonoHuespedResponsable;
        private TipoDocumento tipoDocumentoResponsable;
        private String nroDocumentoResponsable;
        private String idHabitacion;

        public Builder() {}

        public Builder id(int val) { idReserva = val; return this; }
        public Builder estado(EstadoReserva val) { estadoReserva = val; return this; }
        public Builder fechaReserva(Date val) { fechaReserva = val; return this; }
        public Builder fechaDesde(Date val) { fechaDesde = val; return this; }
        public Builder fechaHasta(Date val) { fechaHasta = val; return this; }

        public Builder nombreResponsable(String val) { nombreHuespedResponsable = val; return this; }
        public Builder apellidoResponsable(String val) { apellidoHuespedResponsable = val; return this; }
        public Builder telefonoResponsable(String val) { telefonoHuespedResponsable = val; return this; }
        public Builder tipoDocumentoResponsable(TipoDocumento val) { tipoDocumentoResponsable = val; return this; }
        public Builder nroDocumentoResponsable(String val) { nroDocumentoResponsable = val; return this; }
        public Builder idHabitacion(String val) { idHabitacion = val; return this; }

        public DtoReserva build() {
            return new DtoReserva(this);
        }
    }
}