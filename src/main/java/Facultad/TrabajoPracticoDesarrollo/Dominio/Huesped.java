package Facultad.TrabajoPracticoDesarrollo.Dominio;

import Facultad.TrabajoPracticoDesarrollo.Huesped.DtoHuesped;
import Facultad.TrabajoPracticoDesarrollo.Utils.Mapear.MapearHuesped;
import Facultad.TrabajoPracticoDesarrollo.enums.PosIva;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;

import java.util.Date;
import java.util.List;

public class Huesped {
    private String nombres;
    private String apellido;
    private TipoDocumento tipoDocumento;
    private String nroDocumento;
    private String cuit;
    private PosIva posicionIva;
    private Date fechaNacimiento;
    private String nacionalidad;


    // traemos todos porque si necesitamos solo uno, después lo filtramos y el acceso a la tabla intermedia es solo 1
    private List<String> email;
    private List<String> ocupacion;
    private List<Long> telefono;

    // relaciones
    private Direccion direccion;
    private List<Estadia> estadias;


    // Constructor default
    public Huesped() {
    }

    // Constructor Privado: Solo el Builder puede instanciar
    private Huesped(Builder builder) {
        this.nombres = builder.nombres;
        this.apellido = builder.apellido;
        this.telefono = builder.telefono;
        this.tipoDocumento = builder.tipoDocumento;
        this.nroDocumento = builder.nroDocumento;
        this.cuit = builder.cuit;
        this.posicionIva = builder.posicionIva;
        this.fechaNacimiento = builder.fechaNacimiento;
        this.email = builder.email;
        this.ocupacion = builder.ocupacion;
        this.nacionalidad = builder.nacionalidad;
        this.direccion = builder.direccion;
        this.estadias = builder.estadias;
    }

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
    public void setDireccion(Direccion direccion) {
        if (direccion == null) {
            throw new IllegalArgumentException("La dirección no puede ser nula");
        }
        this.direccion = direccion;
    }
    public Direccion getDireccion() {
        return direccion;
    }
    public List<Estadia> getEstadias(){
        return estadias;
    }
    public void setEstadias(List<Estadia> estadias){
        this.estadias = estadias;
    }

    public Huesped crearSinPersistirHuesped(DtoHuesped dtoHuesped, Direccion direccion){
        return MapearHuesped.mapearDtoAEntidadSinDireccion(dtoHuesped, direccion);
    }

    // --- CLASE BUILDER ---
    public static class Builder {
        private String nombres;
        private String apellido;
        private TipoDocumento tipoDocumento;
        private String nroDocumento;
        // Valores por defecto u opcionales
        private List<Long> telefono;
        private String cuit;
        private PosIva posicionIva;
        private Date fechaNacimiento;
        private List<String> email;
        private List<String> ocupacion;
        private String nacionalidad;
        private Direccion direccion;
        private List<Estadia> estadias;

        // Constructor del Builder con los datos OBLIGATORIOS (mínimos para existir)
        public Builder(String nombres, String apellido, TipoDocumento tipoDocumento, String documento) {
            this.nombres = nombres;
            this.apellido = apellido;
            this.tipoDocumento = tipoDocumento;
            this.nroDocumento = documento;
        }

        // Métodos para el resto
        public Builder telefono(List<Long> val) { telefono = val; return this; }
        public Builder cuit(String val) { cuit = val; return this; }
        public Builder posicionIva(PosIva val) { posicionIva = val; return this; }
        public Builder fechaNacimiento(Date val) { fechaNacimiento = val; return this; }
        public Builder email(List<String> val) { email = val; return this; }
        public Builder ocupacion(List<String> val) { ocupacion = val; return this; }
        public Builder nacionalidad(String val) { nacionalidad = val; return this; }
        public Builder direccion(Direccion val) { direccion = val; return this; }
        public Builder estadias(List<Estadia> val) { estadias = val; return this; }

        public Huesped build() {
            return new Huesped(this);
        }
    }

}
