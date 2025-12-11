package Facultad.TrabajoPracticoDesarrollo.Dominio;

import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

//Clase de Clave Compuesta

//Necesaria porque la tabla Huesped usa tipo_documento + numero_documento como PK.

@Data
public class HuespedId implements Serializable {
    private TipoDocumento tipoDocumento;
    private String nroDocumento;

    public HuespedId() {}

    public HuespedId(TipoDocumento tipoDocumento, String nroDocumento) {
        this.tipoDocumento = tipoDocumento;
        this.nroDocumento = nroDocumento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HuespedId that = (HuespedId) o;
        return tipoDocumento == that.tipoDocumento && Objects.equals(nroDocumento, that.nroDocumento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tipoDocumento, nroDocumento);
    }
}