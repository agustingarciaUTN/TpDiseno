package Dominio;

import java.util.Date;
import java.util.ArrayList;

import Huesped.DtoDireccion;
import enums.PosIva;
import enums.TipoDocumento;

public class PersonaFisica extends ResponsablePago {

    private TipoDocumento tipoDocumento;
    private String numeroDocumento;
    
    public PersonaFisica( TipoDocumento tipoDocumento, String numeroDocumento, int idResponsablePago, ArrayList<Factura> facturas) {
        super(idResponsablePago, facturas);
        if(numeroDocumento == null || numeroDocumento.isEmpty()) {
            throw new IllegalArgumentException("El número de documento no puede estar vacío.");
        }
        if(tipoDocumento == null) {
            throw new IllegalArgumentException("El tipo de documento no puede ser nulo.");
        }
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
    }

    // Getters y Setters
    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }
    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }
    public String getNumeroDocumento() {
        return numeroDocumento;
    }
    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }
}
