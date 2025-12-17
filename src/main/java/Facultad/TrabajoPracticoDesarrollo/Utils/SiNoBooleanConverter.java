package Facultad.TrabajoPracticoDesarrollo.Utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Convertidor JPA que transforma valores {@link Boolean} a cadenas "SI"/"NO" para
 * la columna de la base de datos y viceversa.
 *
 * <p>Comportamiento:
 * - Al persistir, {@code true} se convierte en {@code "SI"}, {@code false} en {@code "NO"}.
 * - Al leer, cualquier cadena que iguale (ignorando mayúsculas/minúsculas y espacios)
 *   a {@code "SI"} se convierte en {@code Boolean.TRUE}; otras cadenas distintas de null
 *   se consideran {@code Boolean.FALSE} implícitamente.</p>
 *
 * <p>Notas:
 * - Si el valor de la entidad es {@code null}, se persiste como {@code null}.
 * - Si el valor de la columna es {@code null}, el atributo de la entidad queda {@code null}.
 * - {@code @Converter(autoApply = true)} hace que este convertidor se aplique automáticamente
 *   a todos los atributos {@code Boolean} gestionados por JPA en el contexto de la aplicación.</p>
 */
@Converter(autoApply = true)
public class SiNoBooleanConverter implements AttributeConverter<Boolean, String> {

    /**
     * Convierte el valor de atributo {@link Boolean} a su representación en la base de datos.
     *
     * @param attribute valor booleano del atributo (puede ser {@code null})
     * @return {@code "SI"} si {@code attribute} es {@code true}, {@code "NO"} si es {@code false},
     *         o {@code null} si {@code attribute} es {@code null}
     */
    @Override
    public String convertToDatabaseColumn(Boolean attribute) {
        if (attribute == null) return null;
        return attribute ? "SI" : "NO";
    }

    /**
     * Convierte el valor de la base de datos ({@link String}) al tipo de atributo {@link Boolean}.
     *
     * @param dbData cadena leída de la base de datos (puede ser {@code null})
     * @return {@code Boolean.TRUE} si {@code dbData} equivale a {@code "SI"} (ignorando mayúsculas
     *         y espacios), {@code Boolean.FALSE} si es otra cadena distinta de {@code null},
     *         o {@code null} si {@code dbData} es {@code null}
     */
    @Override
    public Boolean convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return "SI".equalsIgnoreCase(dbData.trim());
    }
}
