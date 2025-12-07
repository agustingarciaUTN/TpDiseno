package Facultad.TrabajoPracticoDesarrollo.Huesped;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DtoDireccion {

    // --- CONSTANTES DE VALIDACIÓN ---

    // Calle: Acepta letras, números, espacios, puntos y comas (Ej: "Av. Libertador, 1234")
    public static final String REGEX_CALLE = "^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s\\.\\,]+$";

    // Texto Geográfico: Solo letras y espacios (Para Ciudad, Provincia, País)
    public static final String REGEX_TEXTO = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$";

    // Alfanumérico Corto: Para piso y depto (Ej: "4", "B", "PB") - Sin espacios ni símbolos
    public static final String REGEX_ALFANUMERICO = "^[a-zA-Z0-9]+$";


    private int idDireccion;

    @NotBlank(message = "La calle es obligatoria")
    @Pattern(regexp = REGEX_CALLE, message = "La calle contiene caracteres inválidos")
    @Size(max = 100, message = "La calle no puede superar los 100 caracteres")
    private String calle;

    @NotNull(message = "El número de calle es obligatorio")
    @Min(value = 1, message = "El número debe ser positivo")
    @Max(value = 99999, message = "El número ingresado es demasiado grande")
    private Integer numero;

    @Pattern(regexp = REGEX_ALFANUMERICO, message = "El departamento solo acepta letras y números (Ej: A, 2, PB)")
    @Size(max = 5, message = "El departamento es muy largo")
    private String departamento;

    @Pattern(regexp = REGEX_ALFANUMERICO, message = "El piso solo acepta letras y números")
    @Size(max = 5, message = "El piso es muy largo")
    private String piso;

    @NotNull(message = "El código postal es obligatorio")
    @Min(value = 1000, message = "El código postal debe ser válido (min 1000)")
    @Max(value = 9999, message = "El código postal debe ser válido (max 9999)")
    private Integer codPostal;

    @NotBlank(message = "La localidad es obligatoria")
    @Pattern(regexp = REGEX_TEXTO, message = "La localidad solo puede contener letras y espacios")
    private String localidad;

    @NotBlank(message = "La provincia es obligatoria")
    @Pattern(regexp = REGEX_TEXTO, message = "La provincia solo puede contener letras y espacios")
    private String provincia;

    @NotBlank(message = "El país es obligatorio")
    @Pattern(regexp = REGEX_TEXTO, message = "El país solo puede contener letras y espacios")
    private String pais;

    // Constructor por defecto
    public DtoDireccion() {
    }

    // --- CONSTRUCTOR PRIVADO ---
    private DtoDireccion(Builder builder) {
        this.idDireccion = builder.idDireccion;
        this.calle = builder.calle;
        this.numero = builder.numero;
        this.departamento = builder.departamento;
        this.piso = builder.piso;
        this.codPostal = builder.codPostal;
        this.localidad = builder.localidad;
        this.provincia = builder.provincia;
        this.pais = builder.pais;
    }


   // Getters y Setters
    public int getId() {
        return idDireccion;
    }
    public void setId(int id) {
        this.idDireccion = id;
    }
    public int getNumero() {
        return numero;
    }
    public void setNumero(int numero) {
        this.numero = numero;
    }
    public int getCodPostal() {
        return codPostal;
    }
    public void setCodPostal(int codPostal) {
        this.codPostal = codPostal;
    }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private int idDireccion = 0;
        private String calle;
        private int numero;
        private String localidad;
        private String provincia;
        private String pais;

        // Opcionales
        private String departamento;
        private String piso;
        private int codPostal;

        // Constructor vacío (típico en DTOs para ir llenando de a poco)
        public Builder() {}

        // Constructor con obligatorios (opcional, si prefieres forzar datos)
        public Builder(String calle, int numero, String localidad, String provincia, String pais) {
            this.calle = calle;
            this.numero = numero;
            this.localidad = localidad;
            this.provincia = provincia;
            this.pais = pais;
        }

        public Builder idDireccion(int val) { idDireccion = val; return this; }
        public Builder calle(String val) { calle = val; return this; }
        public Builder numero(int val) { numero = val; return this; }
        public Builder departamento(String val) { departamento = val; return this; }
        public Builder piso(String val) { piso = val; return this; }
        public Builder codPostal(int val) { codPostal = val; return this; }
        public Builder localidad(String val) { localidad = val; return this; }
        public Builder provincia(String val) { provincia = val; return this; }
        public Builder pais(String val) { pais = val; return this; }

        public DtoDireccion build() {
            return new DtoDireccion(this);
        }
    }
}