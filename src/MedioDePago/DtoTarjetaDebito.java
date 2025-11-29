package MedioDePago;

import enums.Moneda;
import enums.RedDePago;
import java.util.Date;

public class DtoTarjetaDebito {
    private int idPago;
    private RedDePago redDePago;
    private String banco;
    private String numeroDeTarjeta;
    private Date fechaVencimiento;
    private int codigoSeguridad;
    private float monto;
    private Moneda moneda;
    private Date fechaDePago;

    private DtoTarjetaDebito(Builder builder) {
        this.idPago = builder.idPago;
        this.redDePago = builder.redDePago;
        this.banco = builder.banco;
        this.numeroDeTarjeta = builder.numeroDeTarjeta;
        this.fechaVencimiento = builder.fechaVencimiento;
        this.codigoSeguridad = builder.codigoSeguridad;
        this.monto = builder.monto;
        this.moneda = builder.moneda;
        this.fechaDePago = builder.fechaDePago;
    }

    public DtoTarjetaDebito() {}

    // Getters y Setters ... (omitidos por brevedad, son estándar)
    public int getIdPago() { return idPago; }
    public void setIdPago(int idPago) { this.idPago = idPago; }
    // ... resto ...

    public static class Builder {
        private int idPago;
        private RedDePago redDePago;
        private String banco;
        private String numeroDeTarjeta;
        private Date fechaVencimiento;
        private int codigoSeguridad;
        private float monto;
        private Moneda moneda;
        private Date fechaDePago;

        public Builder() {}

        public Builder idPago(int val) { idPago = val; return this; }
        public Builder red(RedDePago val) { redDePago = val; return this; }
        public Builder banco(String val) { banco = val; return this; }
        public Builder numero(String val) { numeroDeTarjeta = val; return this; }
        public Builder vencimiento(Date val) { fechaVencimiento = val; return this; }
        public Builder seguridad(int val) { codigoSeguridad = val; return this; }
        public Builder monto(float val) { monto = val; return this; }
        public Builder moneda(Moneda val) { moneda = val; return this; }
        public Builder fechaPago(Date val) { fechaDePago = val; return this; }

        public DtoTarjetaDebito build() {
            return new DtoTarjetaDebito(this);
        }
    }
}