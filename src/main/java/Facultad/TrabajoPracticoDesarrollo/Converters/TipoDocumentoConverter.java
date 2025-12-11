// java
package Facultad.TrabajoPracticoDesarrollo.Converters;

import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TipoDocumentoConverter implements AttributeConverter<TipoDocumento, String> {

    @Override
    public String convertToDatabaseColumn(TipoDocumento attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public TipoDocumento convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return TipoDocumento.valueOf(dbData);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Valor de BD inválido para TipoDocumento: " + dbData, ex);
        }
    }
}
