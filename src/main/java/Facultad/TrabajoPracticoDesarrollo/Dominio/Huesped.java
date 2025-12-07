package Facultad.TrabajoPracticoDesarrollo.Dominio;

import Facultad.TrabajoPracticoDesarrollo.enums.PosIva;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "huesped")
@IdClass(HuespedId.class) // Usamos la clave compuesta (Tipo + Numero)
public class Huesped {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento")
    private TipoDocumento tipoDocumento;

    @Id
    @Column(name = "numero_documento")
    private String nroDocumento;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "nombres")
    private String nombres;

    @Column(name = "fecha_nacimiento")
    @Temporal(TemporalType.DATE)
    private Date fechaNacimiento;

    @Column(name = "nacionalidad")
    private String nacionalidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "pos_iva")
    private PosIva posicionIva;

    @Column(name = "cuit")
    private String cuit;

    // --- RELACIÓN CON DIRECCIÓN ---
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_direccion", referencedColumnName = "id_direccion")
    private Direccion direccion;

    // --- LISTAS SATÉLITE ---
    @ElementCollection
    @CollectionTable(
            name = "telefono_huesped",
            joinColumns = {
                    @JoinColumn(name = "tipo_documento", referencedColumnName = "tipo_documento"),
                    @JoinColumn(name = "nro_documento", referencedColumnName = "numero_documento")
            }
    )
    @Column(name = "telefono")
    private List<Long> telefono = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "email_huesped",
            joinColumns = {
                    @JoinColumn(name = "tipo_documento", referencedColumnName = "tipo_documento"),
                    @JoinColumn(name = "nro_documento", referencedColumnName = "numero_documento")
            }
    )
    @Column(name = "email")
    private List<String> email = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "ocupacion_huesped",
            joinColumns = {
                    @JoinColumn(name = "tipo_documento", referencedColumnName = "tipo_documento"),
                    @JoinColumn(name = "nro_documento", referencedColumnName = "numero_documento")
            }
    )
    @Column(name = "ocupacion")
    private List<String> ocupacion = new ArrayList<>();

    // --- 1. CONSTRUCTOR VACÍO (Obligatorio para JPA) ---
    public Huesped() {}

    // --- 2. CONSTRUCTOR PRIVADO (Para el Builder) ---
    private Huesped(Builder builder) {
        this.tipoDocumento = builder.tipoDocumento;
        this.nroDocumento = builder.nroDocumento;
        this.apellido = builder.apellido;
        this.nombres = builder.nombres;
        this.fechaNacimiento = builder.fechaNacimiento;
        this.nacionalidad = builder.nacionalidad;
        this.posicionIva = builder.posicionIva;
        this.cuit = builder.cuit;
        this.direccion = builder.direccion;
        this.telefono = builder.telefono;
        this.email = builder.email;
        this.ocupacion = builder.ocupacion;
    }

    // --- GETTERS Y SETTERS ---
    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumento tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNroDocumento() { return nroDocumento; }
    public void setNroDocumento(String nroDocumento) { this.nroDocumento = nroDocumento; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public Date getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }

    public PosIva getPosicionIva() { return posicionIva; }
    public void setPosicionIva(PosIva posicionIva) { this.posicionIva = posicionIva; }

    public String getCuit() { return cuit; }
    public void setCuit(String cuit) { this.cuit = cuit; }

    public Direccion getDireccion() { return direccion; }
    public void setDireccion(Direccion direccion) { this.direccion = direccion; }

    public List<Long> getTelefono() { return telefono; }
    public void setTelefono(List<Long> telefono) { this.telefono = telefono; }

    public List<String> getEmail() { return email; }
    public void setEmail(List<String> email) { this.email = email; }

    public List<String> getOcupacion() { return ocupacion; }
    public void setOcupacion(List<String> ocupacion) { this.ocupacion = ocupacion; }

    // --- 3. CLASE BUILDER ---
    public static class Builder {
        private TipoDocumento tipoDocumento;
        private String nroDocumento;
        private String apellido;
        private String nombres;

        // Opcionales
        private Date fechaNacimiento;
        private String nacionalidad;
        private PosIva posicionIva;
        private String cuit;
        private Direccion direccion;
        private List<Long> telefono = new ArrayList<>();
        private List<String> email = new ArrayList<>();
        private List<String> ocupacion = new ArrayList<>();

        // Constructor con datos obligatorios (sugerido)
        public Builder(TipoDocumento tipo, String nro, String apellido, String nombres) {
            this.tipoDocumento = tipo;
            this.nroDocumento = nro;
            this.apellido = apellido;
            this.nombres = nombres;
        }

        public Builder fechaNacimiento(Date val) { fechaNacimiento = val; return this; }
        public Builder nacionalidad(String val) { nacionalidad = val; return this; }
        public Builder posicionIva(PosIva val) { posicionIva = val; return this; }
        public Builder cuit(String val) { cuit = val; return this; }
        public Builder direccion(Direccion val) { direccion = val; return this; }

        public Builder telefonos(List<Long> val) { telefono = val; return this; }
        public Builder emails(List<String> val) { email = val; return this; }
        public Builder ocupaciones(List<String> val) { ocupacion = val; return this; }

        public Huesped build() {
            return new Huesped(this);
        }
    }
}