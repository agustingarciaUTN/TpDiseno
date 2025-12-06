package Facultad.TrabajoPracticoDesarrollo.Huesped;

import Facultad.TrabajoPracticoDesarrollo.Estadia.DtoEstadia;
import Facultad.TrabajoPracticoDesarrollo.enums.PosIva;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;


import java.util.Date;
import java.util.List;

public class DtoHuesped {
    // --- CONSTANTES DE VALIDACIÓN (Útiles para usarlas en la UI también) ---
    // Nombre: Letras, espacios y acentos (incluida la ñ)
    public static final String REGEX_NOMBRE = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$";
    // Telefono: Acepta +, espacios, guiones y números
    public static final String REGEX_TELEFONO = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$";
    // DNI/Pasaporte: Alfanumérico (para pasaportes extranjeros) sin espacios
    public static final String REGEX_DOCUMENTO = "^[a-zA-Z0-9]+$";


    @NotBlank(message = "El nombre es obligatorio")
    @Pattern(regexp = REGEX_NOMBRE, message = "El nombre solo puede contener letras y espacios")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String nombres;

    @NotBlank(message = "El apellido es obligatorio")
    @Pattern(regexp = REGEX_NOMBRE, message = "El apellido solo puede contener letras y espacios")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    private String apellido;

    @NotBlank(message = "El tipo de documento es obligatorio")
    private TipoDocumento tipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    @Pattern(regexp = REGEX_DOCUMENTO, message = "El documento no debe contener espacios ni símbolos")
    @Size(min = 6, max = 15, message = "El documento debe tener entre 6 y 15 caracteres")
    private String nroDocumento;


    private String cuit;


    private PosIva posicionIva;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
    @JsonFormat(pattern = "yyyy-MM-dd") // Estandariza el formato si viaja por JSON
    private Date fechaNacimiento;

    @NotBlank(message = "La nacionalidad es obligatoria")
    private String nacionalidad;

    // traemos todos porque si necesitamos solo uno, después lo filtramos y el acceso a la tabla intermedia es solo 1
    private List<String> email;
    private List<String> ocupacion;

    
    private List<Long> telefono;

    // relaciones
    private DtoDireccion dtoDireccion;
    private List<DtoEstadia> dtoEstadias;

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

    // Getters y Setters... (Mantenlos todos igual)
// Getters y Setters
    public String getNombres() {
        return nombres;
    }
    public void setNombres(String nombres) {
        this.nombres = nombres;
    }
    public String getApellido() {
        return apellido;
    }
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    public List<Long> getTelefono() {
        return telefono;
    }
    public void setTelefono(List<Long> telefono) {
        this.telefono = telefono;
    }
    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }
    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }
    public String getNroDocumento() {
        return nroDocumento;
    }
    public void setNroDocumento(String documento) {
        this.nroDocumento = documento;
    }
    public String getCuit() {
        return cuit;
    }
    public void setCuit(String cuit) {
        this.cuit = cuit;
    }
    public PosIva getPosicionIva() {
        return posicionIva;
    }
    public void setPosicionIva(PosIva posicionIva) {
        this.posicionIva = posicionIva;
    }
    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }
    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
    public List<String> getEmail() {
        return email;
    }
    public void setEmail(List<String> email) {
        this.email = email;
    }
    public List<String> getOcupacion() {
        return ocupacion;
    }
    public void setOcupacion(List<String> ocupacion) {
        this.ocupacion = ocupacion;
    }
    public String getNacionalidad() {
        return nacionalidad;
    }
    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }
    public void setDtoDireccion(DtoDireccion dtoDireccion) {
        if (dtoDireccion == null) {
            throw new IllegalArgumentException("La dirección no puede ser nula");
        }
        this.dtoDireccion = dtoDireccion;
    }
    public DtoDireccion getDtoDireccion() {
        return dtoDireccion;
    }
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

        // onsideramos vacío si es null, blanco O si es "0"
        boolean docVacio = (nroDocumento == null || nroDocumento.trim().isEmpty() || nroDocumento.equals("0"));

        // Retorna TRUE solo si TODOS los campos son "vacíos"
        return apellidoVacio && nombresVacio && tipoDocVacio && docVacio;
    }
}