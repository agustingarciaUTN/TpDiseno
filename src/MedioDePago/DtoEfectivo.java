package MedioDePago;


import enums.Moneda;
import java.util.Date;

public class DtoEfectivo {

    private int idPago;      // ID del MedioPago general
    private int idEfectivo;  // ID específico de la tabla efectivo
    private Moneda moneda;
    private float monto;
    private Date fechaDePago;

    // --- CONSTRUCTOR PRIVADO ---
    private DtoEfectivo(Builder builder) {
        this.idPago = builder.idPago;
        this.idEfectivo = builder.idEfectivo;
        this.moneda = builder.moneda;
        this.monto = builder.monto;
        this.fechaDePago = builder.fechaDePago;
    }

    // Constructor por defecto
    public DtoEfectivo() {}

    // --- GETTERS Y SETTERS ---
    public int getIdPago() { return idPago; }
    public void setIdPago(int idPago) { this.idPago = idPago; }

    public int getIdEfectivo() { return idEfectivo; }
    public void setIdEfectivo(int idEfectivo) { this.idEfectivo = idEfectivo; }

    public Moneda getMoneda() { return moneda; }
    public void setMoneda(Moneda moneda) { this.moneda = moneda; }

    public float getMonto() { return monto; }
    public void setMonto(float monto) { this.monto = monto; }

    public Date getFechaDePago() { return fechaDePago; }
    public void setFechaDePago(Date fechaDePago) { this.fechaDePago = fechaDePago; }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private int idPago;
        private int idEfectivo;
        private Moneda moneda;
        private float monto;
        private Date fechaDePago;

        public Builder() {}

        public Builder idPago(int val) { idPago = val; return this; }
        public Builder idEfectivo(int val) { idEfectivo = val; return this; }
        public Builder moneda(Moneda val) { moneda = val; return this; }
        public Builder monto(float val) { monto = val; return this; }
        public Builder fechaDePago(Date val) { fechaDePago = val; return this; }

        public DtoEfectivo build() {
            return new DtoEfectivo(this);
        }
    }
}
