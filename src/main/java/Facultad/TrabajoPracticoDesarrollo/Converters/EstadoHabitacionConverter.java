package Facultad.TrabajoPracticoDesarrollo.Converters;

import Facultad.TrabajoPracticoDesarrollo.enums.EstadoHabitacion;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EstadoHabitacionConverter implements AttributeConverter<EstadoHabitacion, String> {

    @Override
    public String convertToDatabaseColumn(EstadoHabitacion attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getDescripcion();
    }

    @Override
    public EstadoHabitacion convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        return EstadoHabitacion.fromString(dbData);
    }
}
