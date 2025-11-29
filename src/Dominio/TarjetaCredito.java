package Dominio;

import enums.Moneda;
import enums.RedDePago;
import java.util.Date;
import java.util.ArrayList;

public class TarjetaCredito extends Tarjeta {

    private int cuotasCantidad;

    // --- CONSTRUCTOR PRIVADO ---
    private TarjetaCredito(Builder builder) {
        super(builder.idPago, builder.pagos,
                builder.redDePago, builder.banco, builder.numeroDeTarjeta,
                builder.fechaVencimiento, builder.codigoSeguridad, builder.monto,
                builder.moneda, builder.fechaDePago);
        this.cuotasCantidad = builder.cuotasCantidad;
    }

    public int getCuotasCantidad() { return cuotasCantidad; }
    public void setCuotasCantidad(int cuotasCantidad) { this.cuotasCantidad = cuotasCantidad; }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        // Propio de Crédito
        private int cuotasCantidad;

        // De Tarjeta
        private RedDePago redDePago;
        private String banco;
        private String numeroDeTarjeta;
        private Date fechaVencimiento;
        private int codigoSeguridad;
        private float monto;
        private Moneda moneda;
        private Date fechaDePago;

        // De MedioPago
        private int idPago = 0;
        private ArrayList<Pago> pagos = new ArrayList<>();

        public Builder(RedDePago red, String numero, float monto, int cuotas) {
            this.redDePago = red;
            this.numeroDeTarjeta = numero;
            this.monto = monto;
            this.cuotasCantidad = cuotas;
        }

        public Builder banco(String val) { banco = val; return this; }
        public Builder fechaVencimiento(Date val) { fechaVencimiento = val; return this; }
        public Builder codigoSeguridad(int val) { codigoSeguridad = val; return this; }
        public Builder moneda(Moneda val) { moneda = val; return this; }
        public Builder fechaDePago(Date val) { fechaDePago = val; return this; }
        public Builder idPago(int val) { idPago = val; return this; }

        public TarjetaCredito build() {
            if (cuotasCantidad <= 0) throw new IllegalArgumentException("Cuotas debe ser mayor a 0");
            return new TarjetaCredito(this);
        }
    }
}