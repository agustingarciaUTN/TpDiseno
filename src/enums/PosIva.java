package enums;

import java.text.Normalizer;

public enum PosIva {
    ConsumidorFinal("Consumidor Final"),
    Monotributista("Monotributista"),
    ResponsableInscripto("Responsable inscripto"),
    Excento("Exento");

    private final String descripicion;

    PosIva(String descripicion) {
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

    public static PosIva fromString(String s) {
        if (s == null) return null;
        String norm = normalize(s);
        for (PosIva p : values()) {
            if (norm.equals(normalize(p.name())) || norm.equals(normalize(p.descripicion))) {
                return p;
            }
        }
        throw new IllegalArgumentException("PosIva inválido: " + s);
    }

    private static String normalize(String input) {
        if (input == null) return "";
        String tmp = Normalizer.normalize(input, Normalizer.Form.NFD);
        tmp = tmp.replaceAll("\\p{M}", "");      // quitar acentos
        tmp = tmp.replaceAll("[\\s_\\-]+", "");  // quitar espacios, guiones y underscores
        return tmp.toLowerCase();
    }
}