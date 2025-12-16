package Facultad.TrabajoPracticoDesarrollo.Dominio;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "responsable_pago")
@Getter @Setter
@Inheritance(strategy = InheritanceType.JOINED) // Estrategia: Una tabla por clase
@DiscriminatorColumn(name = "tipo_responsable", discriminatorType = DiscriminatorType.STRING)
public abstract class ResponsablePago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_responsable")
    private Integer idResponsable;


    // Relación con Dirección (1 a 1)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_direccion", referencedColumnName = "id_direccion")
    private Direccion direccion;

    public ResponsablePago() {}

    // Constructor para hijos
    protected ResponsablePago(Integer id, String tipo, Direccion direccion) {
        this.idResponsable = id;
    //    this.tipoResponsable = tipo;
        this.direccion = direccion;
    }

    public Integer getIdPersonaFisica() {
        return this.idResponsable;
    }


    public Integer getId() {
        return this.idResponsable;
    }
}