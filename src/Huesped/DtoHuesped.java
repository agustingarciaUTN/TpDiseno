package Huesped;

import enums.TipoDocumento;
import enums.PosIva;
import java.util.Date;

public class DtoHuesped {
    private String nombres;
    private String apellido;
    private long telefono;
    private TipoDocumento tipoDocumento;
    private String documento;
    private String cuit;
    private String posicionIva;
    private Date fechaNacimiento;
    private String email;
    private String ocupacion;
    private String nacionalidad;
    private DtoDireccion dtoDireccion;
    private int idDireccion;   

    // Constructor con todos los datos
    public DtoHuesped(String nombres, String apellido, long telefono, TipoDocumento tipoDocumento, String documento, String cuit, String posicionIva, Date fechaNacimiento, String email, String ocupacion, String nacionalidad) {
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
    
    public DtoHuesped (){}

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
    public String getDocumento() {
        return documento;
    }
    public void setDocumento(String documento) {
        this.documento = documento;
    }
    public String getCuit() {
        return cuit;
    }
    public void setCuit(String cuit) {
        this.cuit = cuit;
    }
    public String getPosicionIva() {
        return posicionIva;
    }
    public void setPosicionIva(String posicionIva) {
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
    public void setDireccion(DtoDireccion dtoDireccion){ this.dtoDireccion = dtoDireccion; }
    public DtoDireccion getDireccion(){ return this.dtoDireccion; }
    public int getIdDireccion() {
        return idDireccion;
    }
    public void setIdDireccion(int idDireccion) {
        this.idDireccion = idDireccion;
    }
    
    public boolean estanVacios() {
        boolean apellidoVacio = (apellido == null || apellido.trim().isEmpty());
        boolean nombresVacio = (nombres == null || nombres.trim().isEmpty());
        boolean tipoDocVacio = (tipoDocumento == null);
        boolean docVacio = (documento.isEmpty());
        return apellidoVacio && nombresVacio && tipoDocVacio && docVacio;
    }
    
    // Método auxiliar para convertir string de la BD a PosIva
    public static PosIva convertirPosIvaString(String posicionIvaStr) {
        if (posicionIvaStr == null) {
            return PosIva.ConsumidorFinal;
        }
        try {
            // Eliminamos espacios y convertimos a mayúsculas
            String posIvaSinEspacios = posicionIvaStr.replace(" ", "").toUpperCase();
            return PosIva.valueOf(posIvaSinEspacios);
        } catch (IllegalArgumentException e) {
            System.err.println("Valor de posicion_iva no válido: " + posicionIvaStr);
            return PosIva.ConsumidorFinal;
        }
    }
    public DtoHuesped(DtoHuesped original) {
        this.nombres = original.nombres;
        this.apellido = original.apellido;
        this.telefono = original.telefono;
        this.tipoDocumento = original.tipoDocumento;
        this.documento = original.documento;
        this.cuit = original.cuit;
        this.posicionIva = original.posicionIva;
        this.fechaNacimiento = original.fechaNacimiento;
        this.email = original.email;
        this.ocupacion = original.ocupacion;
        this.nacionalidad = original.nacionalidad;
        this.idDireccion = original.idDireccion;
        
        // Copiamos la referencia de la dirección (se edita por separado)
        this.dtoDireccion = original.dtoDireccion;
    }
}