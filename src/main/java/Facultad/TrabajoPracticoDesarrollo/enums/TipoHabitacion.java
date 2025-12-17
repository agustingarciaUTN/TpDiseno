package Facultad.TrabajoPracticoDesarrollo.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.text.Normalizer;

public enum TipoHabitacion {
    INDIVIDUAL_ESTANDAR("INDIVIDUAL ESTANDAR"),
    DOBLE_ESTANDAR("DOBLE ESTANDAR"),
    DOBLE_SUPERIOR("DOBLE SUPERIOR"),
    SUPERIOR_FAMILY_PLAN("SUPERIOR FAMILY PLAN"),
    SUITE_DOBLE("SUITE DOBLE");


    private final String descripicion;

    TipoHabitacion(String descripicion) {
        this.descripicion = descripicion;
    }

    public String getDescripicion() {
        return descripicion;
    }

    public String getDescripcion() {
        return descripicion;
    }
    
    @JsonValue
    @Override
    public String toString() {
        return name(); // Retorna el nombre del enum: INDIVIDUAL_ESTANDAR (con guión bajo)
    }

    @JsonCreator
    public static TipoHabitacion fromString(String s) {
        if (s == null) return null;
        String norm = normalize(s);
        for (TipoHabitacion p : values()) {
            if (norm.equals(normalize(p.name())) || norm.equals(normalize(p.descripicion))) {
                return p;
            }
        }
        throw new IllegalArgumentException("Tipo de Habitacion inválido: " + s);
    }

    private static String normalize(String input) {
        if (input == null) return "";
        String tmp = Normalizer.normalize(input, Normalizer.Form.NFD);
        tmp = tmp.replaceAll("\\p{M}", "");      // quitar acentos
        tmp = tmp.replaceAll("[\\s_\\-]+", "");  // quitar espacios, guiones y underscores
        return tmp.toLowerCase();
    }
}
