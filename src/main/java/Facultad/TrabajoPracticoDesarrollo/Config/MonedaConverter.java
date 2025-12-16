package Facultad.TrabajoPracticoDesarrollo.Config;

import Facultad.TrabajoPracticoDesarrollo.enums.Moneda;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MonedaConverter implements AttributeConverter<Moneda, String> {

    @Override
    public String convertToDatabaseColumn(Moneda attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValor();
    }

    @Override
    public Moneda convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return Moneda.fromValor(dbData);
    }
}
