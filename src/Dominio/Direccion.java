package Dominio;

import Utils.Mapear.MapearDireccion;
import Huesped.DtoDireccion;

public class Direccion {
    private int idDireccion;
    private String calle;
    private int numero;
    private String departamento;
    private String piso;
    private int codPostal;
    private String localidad;
    private String provincia;
    private String pais;

    // Constructor default
    public Direccion() {
    }

    // --- CONSTRUCTOR PRIVADO (Usado por el Builder) ---
    private Direccion(Builder builder) {
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
    public void setId(int idDireccion){this.idDireccion = idDireccion;}
    public int getId(){return idDireccion;}
    public String getCalle() {
        return calle;
    }
    public void setCalle(String calle) {
        this.calle = calle;
    }
    public int getNumero() {
        return numero;
    }
    public void setNumero(int numero) {
        this.numero = numero;
    }
    public String getDepartamento() {
        return departamento;
    }
    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
    public String getPiso() {
        return piso;
    }
    public void setPiso(String piso) {
        this.piso = piso;
    }
    public int getCodigoPostal() {
        return codPostal;
    }
    public void setCodigoPostal(int codPostal) {
        this.codPostal = codPostal;
    }
    public String getLocalidad() {
        return localidad;
    }
    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }
    public String getProvincia() {
        return provincia;
    }
    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }
    public String getPais() {
        return pais;
    }
    public void setPais(String pais) {
        this.pais = pais;
    }

    public Direccion crearSinPersistirDireccion(DtoDireccion dtoDireccion){
        return MapearDireccion.mapearDtoAEntidad(dtoDireccion);
    }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        // Datos obligatorios
        private String calle;
        private int numero;
        private String localidad;
        private String provincia;
        private String pais;

        // Datos opcionales (con valores por defecto)
        private int idDireccion = 0;
        private String departamento = null;
        private String piso = null;
        private int codPostal = 0;

        // Constructor con lo MÍNIMO necesario para una dirección válida
        public Builder(String calle, int numero, String localidad, String provincia, String pais) {
            this.calle = calle;
            this.numero = numero;
            this.localidad = localidad;
            this.provincia = provincia;
            this.pais = pais;
        }

        // Métodos fluidos para opcionales
        public Builder id(int val) { idDireccion = val; return this; }
        public Builder departamento(String val) { departamento = val; return this; }
        public Builder piso(String val) { piso = val; return this; }
        public Builder codigoPostal(int val) { codPostal = val; return this; }

        public Direccion build() {
            return new Direccion(this);
        }
    }
}
