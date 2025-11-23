package Dominio;

import enums.TipoDocumento;
import enums.PosIva;
import java.util.Date;

public class Huesped {
    private String nombres;
    private String apellido;
    private int telefono;
    private TipoDocumento tipoDocumento;
    private long documento;
    private String cuit;
    private PosIva posicionIva;
    private Date fechaNacimiento;
    private String email;
    private String ocupacion;
    private String nacionalidad;


    // Constructor default
    public Huesped() {
    }

    // Constructor con todos los datos
    public Huesped(String nombres, String apellido, int telefono, TipoDocumento tipoDocumento, long documento, String cuit, PosIva posicionIva, Date fechaNacimiento, String email, String ocupacion, String nacionalidad) {
        this.nombres = nombres;
        this.apellido = apellido;
        this.telefono = telefono;
        this.tipoDocumento = tipoDocumento;
        this.documento = documento;
        this.cuit = cuit;
        this.posicionIva = posicionIva;
        this.fechaNacimiento = fechaNacimiento;
        this.email = email;
        this.ocupacion = ocupacion;
        this.nacionalidad = nacionalidad;
    }

    // Constructor con los datos más importantes
    public Huesped(String nombres, String apellido, TipoDocumento tipoDocumento, long documento, PosIva posicionIva) {
        this.nombres = nombres;
        this.apellido = apellido;
        this.tipoDocumento = tipoDocumento;
        this.documento = documento;
        this.posicionIva = posicionIva;
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
    public int getTelefono() {
        return telefono;
    }
    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }
    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }
    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }
    public long getDocumento() {
        return documento;
    }
    public void setDocumento(long documento) {
        this.documento = documento;
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
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getOcupacion() {
        return ocupacion;
    }
    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }
    public String getNacionalidad() {
        return nacionalidad;
    }
    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }
}
