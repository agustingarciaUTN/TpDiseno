package Huesped;

public class DtoDireccion {
    private int idDireccion;
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

    // --- CONSTRUCTOR PRIVADO ---
    private DtoDireccion(Builder builder) {
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
    public int getId() {
        return idDireccion;
    }
    public void setId(int id) {
        this.idDireccion = id;
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

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private int idDireccion = 0;
        private String calle;
        private int numero;
        private String localidad;
        private String provincia;
        private String pais;

        // Opcionales
        private String departamento;
        private String piso;
        private int codPostal;

        // Constructor vacío (típico en DTOs para ir llenando de a poco)
        public Builder() {}

        // Constructor con obligatorios (opcional, si prefieres forzar datos)
        public Builder(String calle, int numero, String localidad, String provincia, String pais) {
            this.calle = calle;
            this.numero = numero;
            this.localidad = localidad;
            this.provincia = provincia;
            this.pais = pais;
        }

        public Builder idDireccion(int val) { idDireccion = val; return this; }
        public Builder calle(String val) { calle = val; return this; }
        public Builder numero(int val) { numero = val; return this; }
        public Builder departamento(String val) { departamento = val; return this; }
        public Builder piso(String val) { piso = val; return this; }
        public Builder codPostal(int val) { codPostal = val; return this; }
        public Builder localidad(String val) { localidad = val; return this; }
        public Builder provincia(String val) { provincia = val; return this; }
        public Builder pais(String val) { pais = val; return this; }

        public DtoDireccion build() {
            return new DtoDireccion(this);
        }
    }
}