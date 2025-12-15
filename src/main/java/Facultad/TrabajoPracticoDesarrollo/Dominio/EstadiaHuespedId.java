package Facultad.TrabajoPracticoDesarrollo.Dominio;

import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadiaHuespedId implements Serializable {
    private Integer idEstadia;
    private TipoDocumento tipoDocumento;
    private String nroDocumento;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EstadiaHuespedId that = (EstadiaHuespedId) o;
        return Objects.equals(idEstadia, that.idEstadia) &&
                tipoDocumento == that.tipoDocumento &&
                Objects.equals(nroDocumento, that.nroDocumento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEstadia, tipoDocumento, nroDocumento);
    }
}
