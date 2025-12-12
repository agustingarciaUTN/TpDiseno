package Facultad.TrabajoPracticoDesarrollo.Converters;

import Facultad.TrabajoPracticoDesarrollo.enums.TipoHabitacion;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TipoHabitacionConverter implements AttributeConverter<TipoHabitacion, String> {

    @Override
    public String convertToDatabaseColumn(TipoHabitacion attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getDescripcion();
    }

    @Override
    public TipoHabitacion convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        return TipoHabitacion.fromString(dbData);
    }
}
