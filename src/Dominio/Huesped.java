package Dominio;

import enums.TipoDocumento;
import enums.PosIva;
import java.util.Date;
import java.util.List;

import Huesped.DaoDireccion;
import Huesped.DtoDireccion;

public class Huesped {
    private String nombres;
    private String apellido;
    private long telefono;
    private TipoDocumento tipoDocumento;
    private long documento;
    private String cuit;
    private PosIva posicionIva;
    private Date fechaNacimiento;
    private String email;
    private String ocupacion;
    private String nacionalidad;
    private DtoDireccion direccion;
    private int idDireccion;
    private List<Estadia> estadias;


    // Constructor default
    public Huesped() {
    }

    // Constructor con todos los datos
    public Huesped(String nombres, String apellido, int telefono,
                   TipoDocumento tipoDocumento, long documento, String cuit,
                   PosIva posicionIva, Date fechaNacimiento, String email,
                   String ocupacion, String nacionalidad, int idDireccion) {
        if (idDireccion <= 0) {
            throw new IllegalArgumentException("El huésped debe tener una dirección válida");
        }
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
        this.idDireccion = idDireccion;
    }
    // Constructor con los datos más importantes
    public Huesped(String nombres, String apellido, TipoDocumento tipoDocumento,
                   long documento, PosIva posicionIva, int idDireccion) {
        if (idDireccion <= 0) {
            throw new IllegalArgumentException("El huésped debe tener una dirección válida");
        }

        this.nombres = nombres;
        this.apellido = apellido;
        this.tipoDocumento = tipoDocumento;
        this.documento = documento;
        this.posicionIva = posicionIva;
        this.idDireccion = idDireccion;
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
    public long getTelefono() {
        return telefono;
    }
    public void setTelefono(long telefono) {
        this.telefono = telefono;
    }
    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }
    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }
    public long getNroDocumento() {
        return documento;
    }
    public void setNroDocumento(long documento) {
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
    public void setDireccion(DtoDireccion direccion) {
        if (direccion == null) {
            throw new IllegalArgumentException("La dirección no puede ser nula");
        }
        this.direccion = direccion;
        this.idDireccion = getIdDireccion();
    }
    public DtoDireccion getDireccion() {
        if (direccion == null && idDireccion > 0) {
            DaoDireccion daoDireccion = new DaoDireccion();
            direccion = daoDireccion.obtenerDireccion(idDireccion);
        }
        return direccion;
    }

    public int getIdDireccion() {
        return idDireccion;
    }

    public void setIdDireccion(int idDireccion) {
        if (idDireccion <= 0) {
            throw new IllegalArgumentException("El ID de dirección debe ser mayor a 0");
        }
        this.idDireccion = idDireccion;
    }
}
