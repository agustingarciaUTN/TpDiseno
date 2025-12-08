package Facultad.TrabajoPracticoDesarrollo.Converters;

import Facultad.TrabajoPracticoDesarrollo.enums.PosIva;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

//ESTOS METODOS SON LLAMADOS AUTOMATICAMENTE POR SPRING CUANDO GUARDA O LEE DE LA BDD
//TRADUCE BDD - JAVA

@Converter(autoApply = true) // Se aplica solo a todos los atributos PosIva
public class PosIvaConverter implements AttributeConverter<PosIva, String> {

    @Override
    public String convertToDatabaseColumn(PosIva attribute) {
        // De Java a Base de Datos
        if (attribute == null) {
            return null;
        }
        // Guardamos el valor exacto que espera la BD ("CONSUMIDOR FINAL")
        return attribute.getDescripcion();
    }

    @Override
    public PosIva convertToEntityAttribute(String dbData) {
        // De Base de Datos a Java
        if (dbData == null) {
            return null;
        }
        // Usamos tu metodo fromString para encontrar el Enum correcto
        return PosIva.fromString(dbData);
    }
}