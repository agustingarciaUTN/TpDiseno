package Dominio;

import java.util.ArrayList;
public class PersonaJuridica  extends ResponsablePago {
    
    private String razonSocial;
    private String cuit;
    private long telefono;
    private int idDireccion; // CUAL DE LOS DOS
   // private DtoDireccion direccion; // CUAL DE LOS DOS

    public PersonaJuridica(String razonSocial, String cuit, long telefono, int idDireccion, int idResponsablePago, ArrayList<Factura> facturas) {
        super(idResponsablePago, facturas);
        if(razonSocial == null || razonSocial.isEmpty()) {
            throw new IllegalArgumentException("La razón social no puede estar vacía.");
        }
        if(idDireccion <= 0) {
            throw new IllegalArgumentException("La persona jurídica debe tener una dirección válida.");
        }
        this.razonSocial = razonSocial;
        this.cuit = cuit;
        this.telefono = telefono;
        this.idDireccion = idDireccion;
    }

    // Getters y Setters
    public String getRazonSocial() {
        return razonSocial;
    }
    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }
    public String getCuit() {
        return cuit;
    }
    public void setCuit(String cuit) {
        this.cuit = cuit;
    }
    public long getTelefono() {
        return telefono;
    }
    public void setTelefono(long telefono) {
        this.telefono = telefono;
    }
    public int getIdDireccion() {
        return idDireccion;
    }
    public void setIdDireccion(int idDireccion) {
        this.idDireccion = idDireccion;
    }
}