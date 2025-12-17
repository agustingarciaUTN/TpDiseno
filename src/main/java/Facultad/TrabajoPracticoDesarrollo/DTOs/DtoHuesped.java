package Facultad.TrabajoPracticoDesarrollo.DTOs;

import Facultad.TrabajoPracticoDesarrollo.enums.PosIva;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;


import java.util.Date;
import java.util.List;

@Data
public class DtoHuesped {

    // --- CONSTANTES DE VALIDACIÓN ---
    public static final String REGEX_NOMBRE = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$";
    public static final String REGEX_TELEFONO = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$";


    @NotBlank(message = "El nombre es obligatorio")
    @Pattern(regexp = REGEX_NOMBRE, message = "El nombre solo puede contener letras y espacios")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String nombres;

    @NotBlank(message = "El apellido es obligatorio")
    @Pattern(regexp = REGEX_NOMBRE, message = "El apellido solo puede contener letras y espacios")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    private String apellido;

    @NotNull(message = "El tipo de documento es obligatorio")
    private TipoDocumento tipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    private String nroDocumento;

    private String cuit;

    @NotNull(message = "La posición frente al IVA es obligatoria")
    private PosIva posicionIva;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
    @JsonFormat(pattern = "yyyy-MM-dd") // Estandariza el formato si viaja por JSON
    private Date fechaNacimiento;

    @NotBlank(message = "La nacionalidad es obligatoria")
    private String nacionalidad;

    private List<@Email(message = "Formato de email inválido") String> email;

    //No tiene formato
    private List<String> ocupacion;

    @NotEmpty(message = "Debe ingresar al menos un teléfono")
    private List<@NotNull Long> telefono;

    // relaciones
    @NotNull(message = "La dirección es obligatoria")
    @Valid // CLAVE: Esto le dice a Spring "entrá y validame los atributos de DtoDireccion también"
    private DtoDireccion dtoDireccion;

    @JsonIgnore
    @Valid // Para validar estadías si vienen en la petición (opcional)
    private List<DtoEstadia> dtoEstadias;

    @AssertTrue(message = "El formato del número de documento no coincide con el tipo seleccionado")
    public boolean isDocumentoValido() {
        if (tipoDocumento == null || nroDocumento == null) return true; // Dejar pasar (NotNull lo agarra)

        switch (tipoDocumento) {
            case DNI:
            case LE:
            case LC:
                // Solo números, 7 u 8 dígitos
                return nroDocumento.matches("^\\d{7,8}$");

            case PASAPORTE:
                // Letras y números, 6 a 15 caracteres
                return nroDocumento.matches("^[A-Z0-9]{6,15}$");

            default: // OTRO
                // Alfanumérico, 4 a 20 caracteres
                return nroDocumento.matches("^.{4,20}$");
        }
    }


    // 1. Constructor Privado (Recibe el Builder)
    private DtoHuesped(Builder builder) {
        this.nombres = builder.nombres;
        this.apellido = builder.apellido;
        this.telefono = builder.telefono;
        this.tipoDocumento = builder.tipoDocumento;
        this.nroDocumento = builder.documento;
        this.cuit = builder.cuit;
        this.posicionIva = builder.posicionIva;
        this.fechaNacimiento = builder.fechaNacimiento;
        this.email = builder.email;
        this.ocupacion = builder.ocupacion;
        this.nacionalidad = builder.nacionalidad;
        this.dtoDireccion = builder.dtoDireccion;
        this.dtoEstadias = builder.dtoEstadias;
    }

    // Constructor vacío (necesario a veces para frameworks o serialización)
    public DtoHuesped() {}


    public DtoDireccion getDtoDireccion() {
        return dtoDireccion;
    }

    @JsonIgnore
    public List<DtoEstadia> getDtoEstadias(){
        return dtoEstadias;
    }
    public void setDtoEstadias(List<DtoEstadia> estadias){
        this.dtoEstadias = estadias;
    }

    // 2. Clase Static Builder
    public static class Builder {
        // Mismos atributos que la clase externa
        private String nombres;
        private String apellido;
        private TipoDocumento tipoDocumento;
        private String documento;

        // Opcionales inicializados
        private List<Long> telefono;
        private String cuit;
        private PosIva posicionIva;
        private Date fechaNacimiento;
        private List<String> email;
        private List<String> ocupacion;
        private String nacionalidad;
        private DtoDireccion dtoDireccion;
        private List<DtoEstadia> dtoEstadias;


        // Constructor del Builder (puedes pedir datos mínimos obligatorios o dejarlo vacío)
        public Builder() {}

        public Builder nombres(String val) { nombres = val; return this; }
        public Builder apellido(String val) { apellido = val; return this; }
        public Builder telefono(List<Long> val) { telefono = val; return this; }
        public Builder tipoDocumento(TipoDocumento val) { tipoDocumento = val; return this; }
        public Builder documento(String val) { documento = val; return this; }
        public Builder cuit(String val) { cuit = val; return this; }
        public Builder posicionIva(PosIva val) { posicionIva = val; return this; }
        public Builder fechaNacimiento(Date val) { fechaNacimiento = val; return this; }
        public Builder email(List<String> val) { email = val; return this; }
        public Builder ocupacion(List<String> val) { ocupacion = val; return this; }
        public Builder nacionalidad(String val) { nacionalidad = val; return this; }
        public Builder direccion(DtoDireccion val) { dtoDireccion = val; return this; }
        public Builder estadias(List<DtoEstadia> val) { dtoEstadias = val; return this; }

        public DtoHuesped build() {
            return new DtoHuesped(this);
        }
    }

    public boolean estanVacios() {
        boolean apellidoVacio = (apellido == null || apellido.trim().isEmpty());
        boolean nombresVacio = (nombres == null || nombres.trim().isEmpty());
        boolean tipoDocVacio = (tipoDocumento == null);

        // consideramos vacío si es null, blanco O si es "0"
        boolean docVacio = (nroDocumento == null || nroDocumento.trim().isEmpty() || nroDocumento.equals("0"));

        // Retorna TRUE solo si TODOS los campos son "vacíos"
        return apellidoVacio && nombresVacio && tipoDocVacio && docVacio;
    }
}