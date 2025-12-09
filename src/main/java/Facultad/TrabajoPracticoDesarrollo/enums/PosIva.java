package Facultad.TrabajoPracticoDesarrollo.enums;

import java.text.Normalizer;

public enum PosIva {
    // AHORA SÍ: Constantes con guion bajo (Estándar Java)
    CONSUMIDOR_FINAL("CONSUMIDOR FINAL"),
    MONOTRIBUTISTA("MONOTRIBUTISTA"),
    RESPONSABLE_INSCRIPTO("RESPONSABLE INSCRIPTO"),
    EXENTO("EXENTO");

    private final String descripcion;

    PosIva(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }

    public static PosIva fromString(String s) {
        if (s == null) return null;
        String norm = normalize(s);
        for (PosIva p : values()) {
            // Comparamos contra el nombre O contra la descripción para ser flexibles
            if (norm.equals(normalize(p.name())) || norm.equals(normalize(p.descripcion))) {
                return p;
            }
        }
        // Opción: Retornar null o default en lugar de explotar si la BD tiene basura
        // throw new IllegalArgumentException("PosIva inválido: " + s);
        return null;
    }

    private static String normalize(String input) {
        if (input == null) return "";
        String tmp = Normalizer.normalize(input, Normalizer.Form.NFD);
        tmp = tmp.replaceAll("\\p{M}", "");
        tmp = tmp.replaceAll("[\\s_\\-]+", ""); // Ignora espacios y guiones
        return tmp.toLowerCase();
    }
}