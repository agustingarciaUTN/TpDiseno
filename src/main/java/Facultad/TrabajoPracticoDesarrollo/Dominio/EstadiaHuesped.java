package Facultad.TrabajoPracticoDesarrollo.Dominio;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "estadia_huesped")
@Getter
@Setter
public class EstadiaHuesped {

    @EmbeddedId
    private EstadiaHuespedId id;

    @ManyToOne
    @MapsId("idEstadia") // Conecta con la parte del ID
    @JoinColumn(name = "id_estadia")
    private Estadia estadia;

    @ManyToOne
    @MapsId("idHuesped")
    @JoinColumns({
            @JoinColumn(name = "tipo_documento", referencedColumnName = "tipo_documento", insertable=false, updatable=false),
            @JoinColumn(name = "nro_documento", referencedColumnName = "numero_documento", insertable=false, updatable=false)
    })
    private Huesped huesped;

    @Column(name = "responsable")
    private Boolean esResponsable;
}