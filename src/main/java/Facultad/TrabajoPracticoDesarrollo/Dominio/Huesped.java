package Facultad.TrabajoPracticoDesarrollo.Dominio;

import Facultad.TrabajoPracticoDesarrollo.enums.PosIva;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "huesped")
@Getter @Setter
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


    @Column(name = "pos_iva")
    private PosIva posicionIva;

    @Column(name = "cuit")
    private String cuit;

    // --- RELACIÓN CON DIRECCIÓN ---
    // CascadeType.MERGE permite guardar el huesped y actualizar la FK,
    // pero NO borra la dirección si borrás el huesped (respetando independencia).
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "id_direccion")
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
        // BLINDAJE EXTRA
        this.telefono = (builder.telefono != null) ? new ArrayList<>(builder.telefono) : new ArrayList<>();
        this.email = (builder.email != null) ? new ArrayList<>(builder.email) : new ArrayList<>();
        this.ocupacion = (builder.ocupacion != null) ? new ArrayList<>(builder.ocupacion) : new ArrayList<>();
    }

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

        public Builder() {}

        public Builder tipoDocumento(TipoDocumento val) { tipoDocumento = val; return this; }
        public Builder nroDocumento(String val) { nroDocumento = val; return this;}
        public Builder apellido(String val) { apellido = val; return this;}
        public Builder nombres(String val) { nombres = val; return this; }
        public Builder fechaNacimiento(Date val) { fechaNacimiento = val; return this; }
        public Builder nacionalidad(String val) { nacionalidad = val; return this; }
        public Builder posicionIva(PosIva val) { posicionIva = val; return this; }
        public Builder cuit(String val) { cuit = val; return this; }
        public Builder direccion(Direccion val) { direccion = val; return this; }

        public Builder telefonos(List<Long> val) {
            // Si viene null, dejamos la lista vacía. Si trae datos, creamos una NUEVA ArrayList con ellos.
            this.telefono = (val != null) ? new ArrayList<>(val) : new ArrayList<>();
            return this;
        }

        public Builder emails(List<String> val) {
            this.email = (val != null) ? new ArrayList<>(val) : new ArrayList<>();
            return this;
        }

        public Builder ocupaciones(List<String> val) {
            this.ocupacion = (val != null) ? new ArrayList<>(val) : new ArrayList<>();
            return this;
        }

        public Huesped build() {
            return new Huesped(this);
        }
    }
}