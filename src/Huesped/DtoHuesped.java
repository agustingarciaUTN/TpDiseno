package Huesped;

import Dominio.Direccion;
import Dominio.Estadia;
import Estadia.DtoEstadia;
import enums.TipoDocumento;
import enums.PosIva;
import java.util.Date;
import java.util.List;

public class DtoHuesped {
    private String nombres;
    private String apellido;
    private List<Long> telefono;
    private TipoDocumento tipoDocumento;
    private String nroDocumento;
    private String cuit;
    private PosIva posicionIva;
    private Date fechaNacimiento;
    private List<String> email;
    private List<String> ocupacion;
    private String nacionalidad;
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
    public void setDireccion(DtoDireccion direccion) {
        if (direccion == null) {
            throw new IllegalArgumentException("La dirección no puede ser nula");
        }
        this.dtoDireccion = direccion;
    }
    public DtoDireccion getDireccion() {
        return dtoDireccion;
    }
    public List<DtoEstadia> getEstadias(){
        return dtoEstadias;
    }
    public void setEstadias(List<DtoEstadia> estadias){
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
}