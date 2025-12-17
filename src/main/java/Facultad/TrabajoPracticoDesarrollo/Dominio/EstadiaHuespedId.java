package Facultad.TrabajoPracticoDesarrollo.Dominio;


import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

// 1. La Clave Compuesta
@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class EstadiaHuespedId implements Serializable {
    @Column(name = "id_estadia")
    private Integer idEstadia;

    @Column(name = "tipo_documento")
    @Enumerated(EnumType.STRING)
    private TipoDocumento tipoDocumento;

    @Column(name = "nro_documento")
    private String nroDocumento;
}
