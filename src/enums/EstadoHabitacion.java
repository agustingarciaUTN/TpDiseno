package enums;

import java.text.Normalizer;

public enum EstadoHabitacion {
    HABILITADA("HABILITADA"),
    FUERA_DE_SERVICIO("FUERA DE SERVICIO");


    private final String descripicion;

    EstadoHabitacion(String descripicion) {
        this.descripicion = descripicion;
    }

    public String getDescripicion() {
        return descripicion;
    }

    public String getDescripcion() {
        return descripicion;
    }
    @Override
    public String toString() {
        return descripicion;
    }

    public static EstadoHabitacion fromString(String s) {
        if (s == null) return null;
        String norm = normalize(s);
        for (EstadoHabitacion p : values()) {
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
