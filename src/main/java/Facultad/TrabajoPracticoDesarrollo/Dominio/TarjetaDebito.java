package Facultad.TrabajoPracticoDesarrollo.Dominio;

import Facultad.TrabajoPracticoDesarrollo.enums.Moneda;
import Facultad.TrabajoPracticoDesarrollo.enums.RedDePago;

import java.util.ArrayList;
import java.util.Date;

public class TarjetaDebito extends Tarjeta {

    // --- CONSTRUCTOR PRIVADO ---
    private TarjetaDebito(Builder builder) {
        super(builder.idPago, builder.pagos,
                builder.redDePago, builder.banco, builder.numeroDeTarjeta,
                builder.fechaVencimiento, builder.codigoSeguridad, builder.monto,
                builder.moneda, builder.fechaDePago);
    }
    // --- GETTERS / SETTERS delegados a Tarjeta / MedioPago ---
    public int getIdPago() { return super.getIdPago(); }
    public void setIdPago(int idPago) { super.setIdPago(idPago); }

    public RedDePago getRedDePago() { return super.getRedDePago(); }
    public void setRedDePago(RedDePago redDePago) { super.setRedDePago(redDePago); }

    public String getBanco() { return super.getBanco(); }
    public void setBanco(String banco) { super.setBanco(banco); }

    public String getNumeroDeTarjeta() { return super.getNumeroDeTarjeta(); }
    public void setNumeroDeTarjeta(String numeroDeTarjeta) { super.setNumeroDeTarjeta(numeroDeTarjeta); }

    public Date getFechaVencimiento() { return super.getFechaVencimiento(); }
    public void setFechaVencimiento(Date fechaVencimiento) { super.setFechaVencimiento(fechaVencimiento); }

    public int getCodigoSeguridad() { return super.getCodigoSeguridad(); }
    public void setCodigoSeguridad(int codigoSeguridad) { super.setCodigoSeguridad(codigoSeguridad); }

    public float getMonto() { return super.getMonto(); }
    public void setMonto(float monto) { super.setMonto(monto); }

    public Moneda getMoneda() { return super.getMoneda(); }
    public void setMoneda(Moneda moneda) { super.setMoneda(moneda); }

    public Date getFechaDePago() { return super.getFechaDePago(); }
    public void setFechaDePago(Date fechaDePago) { super.setFechaDePago(fechaDePago); }
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