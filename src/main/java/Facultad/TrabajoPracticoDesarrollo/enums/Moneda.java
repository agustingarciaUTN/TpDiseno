package Facultad.TrabajoPracticoDesarrollo.enums;

public enum Moneda {
    PESOS_ARGENTINOS("PESOS ARGENTINOS"),
    DOLARES("DOLARES"),
    REALES("REALES"),
    PESOS_URUGUAYOS("PESOS URUGUAYOS"),
    EUROS("EUROS");

    private final String valor;

    Moneda(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static Moneda fromValor(String valor) {
        for (Moneda m : Moneda.values()) {
            if (m.valor.equals(valor)) {
                return m;
            }
        }
        throw new IllegalArgumentException("Valor de moneda no válido: " + valor);
    }
}
