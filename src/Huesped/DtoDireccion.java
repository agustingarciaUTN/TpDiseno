package Huesped;

public class DtoDireccion {
    private int ID;
    private String calle;
    private int numero;
    private String departamento;
    private String piso;
    private int codPostal;
    private String localidad;
    private String provincia;
    private String pais;

    // Constructor por defecto
    public DtoDireccion() {
    }

    // Constructor con todos los datos
    public DtoDireccion(String calle, int numero, String departamento, String piso, int codPostal, String localidad, String provincia, String pais) {
        this.calle = calle;
        this.numero = numero;
        this.departamento = departamento;
        this.piso = piso;
        this.codPostal = codPostal;
        this.localidad = localidad;
        this.provincia = provincia;
        this.pais = pais;
    }

    // Getters y Setters
    public int getId() {
        return ID;
    }
    public void setId(int id) {
        this.ID = id;
    }
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
    public int getCodPostal() {
        return codPostal;
    }
    public void setCodPostal(int codPostal) {
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
}