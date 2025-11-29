package Dominio;

import enums.Moneda;
import enums.RedDePago;
import java.util.Date;
import java.util.ArrayList;

public class TarjetaDebito extends Tarjeta {

    // --- CONSTRUCTOR PRIVADO ---
    private TarjetaDebito(Builder builder) {
        super(builder.idPago, builder.pagos,
                builder.redDePago, builder.banco, builder.numeroDeTarjeta,
                builder.fechaVencimiento, builder.codigoSeguridad, builder.monto,
                builder.moneda, builder.fechaDePago);
    }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        // Atributos de Tarjeta (Padre)
        private RedDePago redDePago;
        private String banco;
        private String numeroDeTarjeta;
        private Date fechaVencimiento;
        private int codigoSeguridad;
        private float monto;
        private Moneda moneda;
        private Date fechaDePago;

        // Atributos de MedioPago (Abuelo)
        private int idPago = 0;
        private ArrayList<Pago> pagos = new ArrayList<>();

        // Constructor con obligatorios
        public Builder(RedDePago red, String numero, float monto) {
            this.redDePago = red;
            this.numeroDeTarjeta = numero;
            this.monto = monto;
        }

        // Métodos fluidos
        public Builder banco(String val) { banco = val; return this; }
        public Builder fechaVencimiento(Date val) { fechaVencimiento = val; return this; }
        public Builder codigoSeguridad(int val) { codigoSeguridad = val; return this; }
        public Builder moneda(Moneda val) { moneda = val; return this; }
        public Builder fechaDePago(Date val) { fechaDePago = val; return this; }

        public Builder idPago(int val) { idPago = val; return this; }
        public Builder pagos(ArrayList<Pago> val) { pagos = val; return this; }

        public TarjetaDebito build() {
            if (monto < 0) throw new IllegalArgumentException("Monto inválido");
            if (numeroDeTarjeta == null) throw new IllegalArgumentException("Número obligatorio");
            return new TarjetaDebito(this);
        }
    }
}