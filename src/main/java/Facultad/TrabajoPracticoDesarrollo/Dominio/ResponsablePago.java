package Facultad.TrabajoPracticoDesarrollo.Dominio;

import jakarta.persistence.*;

@Entity
@Table(name = "responsable_pago")
@Inheritance(strategy = InheritanceType.JOINED) // Estrategia: Una tabla por clase
public abstract class ResponsablePago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_responsable")
    private Integer idResponsable;

    // Columna discriminadora (F o J), aunque con JOINED es opcional, a veces ayuda.
    // En tu caso, parece ser meramente informativa en la BD.
    @Column(name = "tipo_responsable")
    private String tipoResponsable;

    // Relación con Dirección (1 a 1)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_direccion", referencedColumnName = "id_direccion")
    private Direccion direccion;

    public ResponsablePago() {}

    // Constructor para hijos
    protected ResponsablePago(Integer id, String tipo, Direccion direccion) {
        this.idResponsable = id;
        this.tipoResponsable = tipo;
        this.direccion = direccion;
    }

    // Getters y Setters
    public Integer getIdResponsablePago() { return idResponsable; }
    public void setIdResponsablePago(Integer idResponsable) { this.idResponsable = idResponsable; }

    public String getTipoResponsable() { return tipoResponsable; }
    public void setTipoResponsable(String tipoResponsable) { this.tipoResponsable = tipoResponsable; }

    public Direccion getDireccion() { return direccion; }
    public void setDireccion(Direccion direccion) { this.direccion = direccion; }
}