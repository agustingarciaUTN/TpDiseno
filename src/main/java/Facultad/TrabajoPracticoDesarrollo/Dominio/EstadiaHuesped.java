package Facultad.TrabajoPracticoDesarrollo.Dominio;

import Facultad.TrabajoPracticoDesarrollo.enums.Responsable;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "estadia_huesped")
@Getter
@Setter
@IdClass(EstadiaHuespedId.class)
public class EstadiaHuesped {

    @Id
    @Column(name = "id_estadia")
    private Integer idEstadia;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento")
    private TipoDocumento tipoDocumento;

    @Id
    @Column(name = "nro_documento")
    private String nroDocumento;

    @Enumerated(EnumType.STRING)
    @Column(name = "responsable", columnDefinition = "varchar(2) default 'NO'")
    private Responsable responsable = Responsable.NO;

    @ManyToOne
    @JoinColumn(name = "id_estadia", insertable = false, updatable = false)
    private Estadia estadia;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "tipo_documento", referencedColumnName = "tipo_documento", insertable = false, updatable = false),
            @JoinColumn(name = "nro_documento", referencedColumnName = "numero_documento", insertable = false, updatable = false)
    })
    private Huesped huesped;

    public EstadiaHuesped() {}

    public EstadiaHuesped(Integer idEstadia, TipoDocumento tipoDocumento, String nroDocumento, Responsable responsable) {
        this.idEstadia = idEstadia;
        this.tipoDocumento = tipoDocumento;
        this.nroDocumento = nroDocumento;
        this.responsable = responsable;
    }
}
